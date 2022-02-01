package com.github.ck;

import com.github.capture.model.TcpPacketRecord;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.ClickHouseStatement;
import ru.yandex.clickhouse.domain.ClickHouseFormat;
import ru.yandex.clickhouse.settings.ClickHouseProperties;
import ru.yandex.clickhouse.settings.ClickHouseQueryParam;
import ru.yandex.clickhouse.util.ClickHouseRowBinaryStream;
import ru.yandex.clickhouse.util.ClickHouseStreamCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yusheng
 * @version 1.0.0
 * @datetime 2021-12-29 11:03
 * @description
 */
public class ClickHouseJClient implements AutoCloseable{
    private static final Map<ClickHouseQueryParam,String> additionDBParams = new HashMap<>();
    private ClickHouseDataSource dataSource;
    private ClickHouseConnection conn;
    private final static List<TcpPacketRecord> records = new ArrayList<>(600);
    private ClickHouseStatement stmt;
    private static final String insertSql = "INSERT INTO bdp.raw_packet";

    public ClickHouseJClient(String ckHost,int ckHttpPort,String ckDB,String ckUser,String ckPassword){
        additionDBParams.put(ClickHouseQueryParam.SESSION_ID,"ck-session-id");

        String url = "jdbc:clickhouse://" + ckHost + ":" + ckHttpPort;
        ClickHouseProperties properties = new ClickHouseProperties();
        properties.setClientName("ClickHouse Java Client!");
        properties.setUser(ckUser);
        properties.setPassword(ckPassword);
        properties.setCompress(true);
        properties.setHost(ckHost);
        properties.setDatabase(ckDB);
        properties.setConnectionTimeout(60 * 1000);
        properties.setSessionTimeout(60 * 1000L);

        dataSource = new ClickHouseDataSource(url,properties);
        try{
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
        }catch (SQLException se){
            se.printStackTrace();
        }
    }

    public synchronized void gatherAndWrite(TcpPacketRecord record){
        if(record != null){
            records.add(record);
        }

        if(records.size() >= 500){
            try{
                stmt.write().send(insertSql, new ClickHouseStreamCallback() {
                    @Override
                    public void writeTo(ClickHouseRowBinaryStream stream) throws IOException {
                        for (TcpPacketRecord packetRecord : records) {
                            stream.writeString(packetRecord.getMessageID());
                            stream.writeInt64(packetRecord.getMessageTs());
                            stream.writeInt64(packetRecord.getMessageCaptureTs());
                            stream.writeInt16(packetRecord.getEtherType());
                            stream.writeString(packetRecord.getEthSrcAddress());
                            stream.writeString(packetRecord.getEthDstAddress());

                            stream.writeInt8(packetRecord.getIpVersion());
                            stream.writeInt8(packetRecord.getIpIhl());
                            stream.writeInt8(packetRecord.getIpTos());

                            stream.writeUInt16(packetRecord.getIpTotalLen());
                            stream.writeInt32(packetRecord.getIpIdentification());


                            stream.writeUInt8(packetRecord.isIpReservedFlag());
                            stream.writeUInt8(packetRecord.isIpDontFragmentFlag());
                            stream.writeUInt8(packetRecord.isIpMoreFragmentFlag());

                            stream.writeInt16(packetRecord.getIpFragmentOffset());

                            stream.writeInt8(packetRecord.getIpTtl());
                            stream.writeInt8(packetRecord.getIpProtocol());

                            stream.writeInt16(packetRecord.getIpHeaderChecksum());
                            stream.writeString(packetRecord.getIpSrcAddress());
                            stream.writeString(packetRecord.getIpDstAddress());

                            stream.writeUInt32(packetRecord.getTcpSrcPort());
                            stream.writeUInt32(packetRecord.getTcpDstPort());

                            stream.writeInt64(packetRecord.getTcpSeqNumber());
                            stream.writeInt64(packetRecord.getTcpAcknowledgmentNumber());

                            stream.writeInt8(packetRecord.getTcpDataOffset());
                            stream.writeInt8(packetRecord.getTcpReserved());

                            stream.writeUInt8(packetRecord.isTcpSyn());
                            stream.writeUInt8(packetRecord.isTcpAck());
                            stream.writeUInt8(packetRecord.isTcpFin());
                            stream.writeUInt8(packetRecord.isTcpPsh());
                            stream.writeUInt8(packetRecord.isTcpRst());
                            stream.writeUInt8(packetRecord.isTcpUrg());

                            stream.writeInt16(packetRecord.getTcpChecksum());
                            stream.writeInt32(packetRecord.getTcpWindow());
                            stream.writeInt32(packetRecord.getTcpUrgentPointer());

                        }
                    }
                },ClickHouseFormat.RowBinary);
            }catch (SQLException se){
                se.printStackTrace();
            }

            records.clear();
        }
    }

    @Override
    public void close() throws Exception {
        if(conn != null){
            conn.close();
            conn = null;
        }
    }
}