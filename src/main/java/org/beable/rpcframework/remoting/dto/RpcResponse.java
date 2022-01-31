package org.beable.rpcframework.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author qing.wu
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = -621079785656507401L;

    private String requestId;

    private Integer code;

    private String message;

    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setRequestId(requestId);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    public static <T> RpcResponse<T> fail(Integer statusCode) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(statusCode);
        response.setMessage("fail");
        return response;
    }
}
