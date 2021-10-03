package com.github.capture.sender;

import lombok.Data;

/**
 * @author yusheng
 * @version 1.0.0
 * @datetime 2021-09-07 08:39
 * @description message request packet.
 */
@Data
public class MessageRequestPacket extends Packet {
    // 客户端id
    private String clientId;
    // 消息时间戳(消息产生时间)
    private long messageTs;
    // 消息id
    private String messageId;
    // 消息数据
    private byte[] data;

    public MessageRequestPacket(String clientId,long messageTs,String messageId,byte[] data){
        this.clientId = clientId;
        this.messageTs = messageTs;
        this.messageId = messageId;
        this.data = data;
    }

    @Override
    public byte getCmd() {
        return 4;
    }
}
