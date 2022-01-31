package org.beable.rpcframework.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author qing.wu
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -6361732388289880612L;

    private String requestId;

    private String interfaceName;

    private String methodName;

    private Object[] parameters;

    private Class<?>[] paramTypes;

    private String version;

    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + "#" + this.getGroup() + "#" + this.getVersion();
    }
}
