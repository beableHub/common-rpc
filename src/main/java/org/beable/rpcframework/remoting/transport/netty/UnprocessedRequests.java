package org.beable.rpcframework.remoting.transport.netty;

import org.beable.rpcframework.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qing.wu
 */
public class UnprocessedRequests {

    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    private static volatile UnprocessedRequests instance;

    public static UnprocessedRequests getInstance(){
        if (instance == null){
            synchronized (UnprocessedRequests.class){
                if (instance == null){
                    instance = new UnprocessedRequests();
                }
            }
        }
        return instance;
    }

    private UnprocessedRequests(){}

    public void put(String requestId,CompletableFuture<RpcResponse<Object>> future){
        UNPROCESSED_RESPONSE_FUTURES.put(requestId,future);
    }

    public void complete(RpcResponse<Object> response){
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(response.getRequestId());
        if(future != null){
            future.complete(response);
        }else {
            throw new IllegalStateException();
        }
    }
}
