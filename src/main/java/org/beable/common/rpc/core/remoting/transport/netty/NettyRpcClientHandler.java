package org.beable.common.rpc.core.remoting.transport.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.remoting.constants.MessageType;
import org.beable.common.rpc.core.remoting.dto.RpcMessage;
import org.beable.common.rpc.core.remoting.dto.RpcResponse;
import org.beable.common.rpc.core.remoting.handler.RequestHandler;

/**
 * @author qing.wu
 */
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private final RequestHandler requestHandler;
    private final UnprocessedRequests unprocessedRequests;

    public NettyRpcClientHandler() {
        this.requestHandler = new RequestHandler();
        unprocessedRequests = UnprocessedRequests.getInstance();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {
                log.info("client receive msg: [{}]", msg);
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                if (messageType == MessageType.RESPONSE.getValue()){
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            // Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }
}
