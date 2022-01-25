package org.beable.common.rpc.core.remoting.transport.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.remoting.dto.RpcMessage;
import org.beable.common.rpc.core.remoting.handler.RequestHandler;

/**
 * @author qing.wu
 */
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private final RequestHandler requestHandler;

    public NettyRpcClientHandler() {
        this.requestHandler = new RequestHandler();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                log.info("client receive msg: [{}]", msg);

            }
        } finally {
            // Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }
}
