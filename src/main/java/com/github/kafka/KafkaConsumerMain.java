package com.github.kafka;

import com.github.capture.model.TcpPacketOuterClass;
import com.github.capture.model.TcpPacketRecord;
import com.github.ck.ClickHouseJClient;
import com.github.conf.AppConfiguration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.namednumber.EtherType;

/**
 * @author yusheng
 * @version 1.0.0
 * @datetime 2022-01-04 09:55
 * @description
 */
public class KafkaConsumerMain extends Thread{
    private final KafkaConsumer<String, byte[]> consumer;
    private final String topic;
    private final ClickHouseJClient clickHouseJClient;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() < 8 ? 8 : Runtime.getRuntime().availableProcessors());
    private static final AppConfiguration appConfig = AppConfiguration.loadFromPropertiesResource("client.properties");

    public static void main(String[] args){
        String kafkaServerHost = appConfig.getString("client.kafka.server.host","127.0.0.1");
        String kafkaTopic = appConfig.getString("client.kafka.topic.name","");

        KafkaConsumerMain kafkaConsumerMain = new KafkaConsumerMain(kafkaTopic,kafkaServerHost);
        kafkaConsumerMain.start();

        try{
            kafkaConsumerMain.join();
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }

    public KafkaConsumerMain(String topic,String kafkaServer){
        String kafkaConsumerGroupID = appConfig.getString("client.kafka.consumer.group.id","default-consumer-group");
        String kafkaConsumerOffset = appConfig.getString("client.kafka.consumer.offset","latest"); // earliest

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer + ":" + 9092);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupID);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,kafkaConsumerOffset);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        consumer = new KafkaConsumer<>(props);

        this.topic = topic;

        String chHost = appConfig.getString("client.ch.server.host","127.0.0.1");
        String chDb = appConfig.getString("client.ch.db.name","default");
        String chUser = appConfig.getString("client.ch.user","default");
        String chPassword = appConfig.getString("client.ch.passwd","");

        this.clickHouseJClient = new ClickHouseJClient(chHost,8123,chDb,chUser,chPassword);
    }

    @Override
    public void run() {

        while(true){
            consumer.subscribe(Collections.singletonList(this.topic));
            ConsumerRecords<String, byte[]> records = consumer.poll(5000);

            for (ConsumerRecord<String, byte[]> record : records) {
                // System.out.println("Received message: (" + record.key() + ") at offset " + record.offset() + ", value byte size:" + record.value().length);
                parseAndWrite(record);
            }
        }

    }

    private void parseAndWrite(ConsumerRecord<String, byte[]> record){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                long startTs = System.currentTimeMillis();
                try{
                    TcpPacketOuterClass.TcpPacket rawTcpPacket = TcpPacketOuterClass.TcpPacket.parseFrom(record.value());

                    byte[] rawData = rawTcpPacket.getRawData().toByteArray();

                    EthernetPacket ethernetPacket = EthernetPacket.newPacket(rawData,0,rawData.length);
                    EthernetPacket.EthernetHeader ethernetHeader = ethernetPacket.getHeader();

                    EtherType etherType = ethernetHeader.getType();

                    TcpPacketRecord packetRecord = new TcpPacketRecord();
                    packetRecord.setMessageID(record.key());
                    packetRecord.setMessageTs(System.currentTimeMillis());
                    packetRecord.setMessageCaptureTs(rawTcpPacket.getTs());

                    packetRecord.setEtherType(etherType.value());

                    String ethSrcAddress = ethernetHeader.getSrcAddr().toString();
                    packetRecord.setEthSrcAddress(ethSrcAddress);

                    String ethDstAddress = ethernetHeader.getDstAddr().toString();
                    packetRecord.setEthDstAddress(ethDstAddress);

                    switch (etherType.value()){
                        case (short) 0x0800:
                            // EtherType.IPV4;
                            IpV4Packet ipV4Packet = ethernetPacket.getPayload().get(IpV4Packet.class);
                            ipV4PacketParse(ipV4Packet,packetRecord);

                            TcpPacket tcpPacket = ethernetPacket.getPayload().get(TcpPacket.class);
                            tcpPacketParse(tcpPacket,packetRecord);

                            break;
                        case (short) 0x86dd:
                            // EtherType.IPV6

                            break;
                        case (short) 0x0806:
                            // EtherType.ARP


                            break;
                        case (short) 0x8035:
                            // EtherType.RARP

                            break;
                        case (short) 0x8100:
                            // EtherType.DOT1Q_VLAN_TAGGED_FRAMES

                            break;
                        case (short) 0x880b:
                            // EtherType.PPP


                            break;
                        case (short) 0x8847:
                            // EtherType.MPLS

                            break;
                        case (short) 0x809b:
                            // EtherType.APPLETALK


                            break;
                        case (short) 0x8863:
                            // EtherType.PPPOE_DISCOVERY_STAGE

                            break;
                        case (short) 0x8864:
                            // EtherType.PPPOE_SESSION_STAGE

                            break;
                        default:
                            break;
                    }

                    // System.out.println(packetRecord.toString());

                    clickHouseJClient.gatherAndWrite(packetRecord);

                    // System.out.println("- - - - - - - - - - - - - - - - - - - - - millis: " + (System.currentTimeMillis() - startTs));
                }catch (Exception ex){
                    System.err.println("parse error!");
                }
            }
        });
    }

    private void ipV4PacketParse(IpV4Packet ipV4Packet,TcpPacketRecord packetRecord){
        IpV4Packet.IpV4Header ipV4Header = ipV4Packet.getHeader();

        byte ipVersion = ipV4Header.getVersion().value();
        packetRecord.setIpVersion(ipVersion);

        byte ipIhl = ipV4Header.getIhl();
        packetRecord.setIpIhl(ipIhl);

        byte tos = ipV4Header.getTos().value();
        packetRecord.setIpTos(tos);

        short ipTotalLen = ipV4Header.getTotalLength();
        packetRecord.setIpTotalLen(ipTotalLen);

        int ipIdentification = ipV4Header.getIdentificationAsInt();
        packetRecord.setIpIdentification(ipIdentification);

        boolean ipReservedFlag = ipV4Header.getReservedFlag();
        packetRecord.setIpReservedFlag(ipReservedFlag);

        boolean ipDontFragmentFlag = ipV4Header.getDontFragmentFlag();
        packetRecord.setIpDontFragmentFlag(ipDontFragmentFlag);

        boolean ipMoreFragmentFlag = ipV4Header.getMoreFragmentFlag();
        packetRecord.setIpMoreFragmentFlag(ipMoreFragmentFlag);

        short ipFragmentOffset = ipV4Header.getFragmentOffset();
        packetRecord.setIpFragmentOffset(ipFragmentOffset);

        byte ipTtl = ipV4Header.getTtl();
        packetRecord.setIpTtl(ipTtl);

        byte ipProtocol = ipV4Header.getProtocol().value();
        packetRecord.setIpProtocol(ipProtocol);

        short ipHeaderChecksum = ipV4Header.getHeaderChecksum();
        packetRecord.setIpHeaderChecksum(ipHeaderChecksum);

        String ipSrcAddress = ipV4Header.getSrcAddr().getHostAddress();
        packetRecord.setIpSrcAddress(ipSrcAddress);

        String ipDstAddress = ipV4Header.getDstAddr().getCanonicalHostName();
        packetRecord.setIpDstAddress(ipDstAddress);
    }

    private void tcpPacketParse(TcpPacket tcpPacket,TcpPacketRecord packetRecord){
        TcpPacket.TcpHeader tcpHeader = tcpPacket.getHeader();

        int tcpSrcPort = tcpHeader.getSrcPort().valueAsInt();
        packetRecord.setTcpSrcPort(tcpSrcPort);

        int tcpDstPort = tcpHeader.getDstPort().valueAsInt();
        packetRecord.setTcpDstPort(tcpDstPort);

        long tcpSeqNumber = tcpHeader.getSequenceNumberAsLong();
        packetRecord.setTcpSeqNumber(tcpSeqNumber);

        long tcpAcknowledgmentNumber = tcpHeader.getAcknowledgmentNumberAsLong();
        packetRecord.setTcpAcknowledgmentNumber(tcpAcknowledgmentNumber);

        packetRecord.setTcpDataOffset(tcpHeader.getDataOffset());

        packetRecord.setTcpReserved(tcpHeader.getReserved());

        packetRecord.setTcpSyn(tcpHeader.getSyn());

        packetRecord.setTcpAck(tcpHeader.getAck());

        packetRecord.setTcpFin(tcpHeader.getFin());

        packetRecord.setTcpPsh(tcpHeader.getPsh());

        packetRecord.setTcpRst(tcpHeader.getRst());

        packetRecord.setTcpUrg(tcpHeader.getUrg());

        packetRecord.setTcpChecksum(tcpHeader.getChecksum());

        packetRecord.setTcpWindow(tcpHeader.getWindowAsInt());

        packetRecord.setTcpUrgentPointer(tcpHeader.getUrgentPointerAsInt());
    }
}

