package com.github.capture.sender;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author yusheng
 * @version 1.0.0
 * @datetime 2021-09-07 08:35
 * @description packet.
 */
@Data
public abstract class Packet {
    @JSONField(deserialize = false,serialize = false)
    private byte version = 0x01;

    @JSONField(serialize = false)
    public abstract byte getCmd();
}
