package org.beable.common.rpc.core.remoting.transport.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.remoting.constants.MessageType;
import org.beable.common.rpc.core.remoting.dto.RpcMessage;
import org.beable.common.rpc.core.remoting.dto.RpcRequest;
import org.beable.common.rpc.core.remoting.dto.RpcResponse;
import org.beable.common.rpc.core.remoting.handler.RequestHandler;

import java.util.UUID;

@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RequestHandler requestHandler;

    public NettyRpcServerHandler(){
        this.requestHandler = new RequestHandler();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                log.info("server receive msg: [{}] ", msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                Object result = requestHandler.handle(rpcRequest);
                log.info(String.format("server get result: %s", result.toString()));
                rpcMessage.setMessageType(MessageType.RESPONSE.getValue());
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
                    rpcMessage.setData(response);
                } else {
                    RpcResponse<Object> rpcResponse = RpcResponse.fail(500);
                    rpcMessage.setData(rpcResponse);
                    log.error("not writable now, message dropped");
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE);
            }
        } finally {
            // Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }
}
