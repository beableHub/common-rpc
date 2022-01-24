package org.beable.common.rpc.core.remoting.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.remoting.dto.RpcMessage;

/**
 * @author qing.wu
 */
@Slf4j
public class NettyMessageEncoder extends MessageToByteEncoder<RpcMessage> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {

    }
}
