package org.beable.rpcframework.remoting.dto;

import lombok.*;

/**
 * @author qing.wu
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    private byte messageType;

    private byte codec;

    private byte compress;

    private int requestId;

    private Object data;
}
