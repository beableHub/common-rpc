package org.beable.common.rpc.core.remoting.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author qing.wu
 */
@Slf4j
public class NettyMessageDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

    }
}
