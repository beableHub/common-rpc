package org.beable.common.rpc.core.config;

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
public class RpcServiceConfig {

    private Object service;
    private String group = "default-group";
    private String version = "1.0";

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
