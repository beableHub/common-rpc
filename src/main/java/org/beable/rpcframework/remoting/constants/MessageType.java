package org.beable.rpcframework.remoting.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author qing.wu
 */
@Getter
@AllArgsConstructor
public enum MessageType {

    REQUEST((byte)1),
    RESPONSE((byte)2),
    HEARTBEAT((byte)3),
    ;


    private final byte value;
}
