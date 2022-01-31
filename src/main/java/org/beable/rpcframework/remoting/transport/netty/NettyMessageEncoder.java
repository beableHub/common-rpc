package org.beable.rpcframework.remoting.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.remoting.dto.RpcMessage;
import org.beable.rpcframework.serialize.Serializer;
import org.beable.rpcframework.common.extension.ExtensionLoader;


/**
 *  <pre>
 *  2B full length （消息总长度）
 *  1B message type （消息类型）
 *  body （object类型数据）
 *  </pre>
 * @author qing.wu
 */
@Slf4j
public class NettyMessageEncoder extends MessageToByteEncoder<RpcMessage> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf out) throws Exception {
        // 4B full length（消息长度）. 总长度先空着，后面填。
        out.writerIndex(out.writerIndex() + 4);
        // 1B message type
        out.writeByte(rpcMessage.getMessageType());
        int fullLength = 4 + 1;
        // 写 body，返回 body 长度
        int bodyLength = writeBody(rpcMessage, out);
        fullLength += bodyLength;
        // 当前写指针
        int writeIndex = out.writerIndex();
        out.writerIndex(writeIndex - fullLength);
        out.writeInt(fullLength);
        // 写指针复原
        out.writerIndex(writeIndex);
    }

    private int writeBody(RpcMessage rpcMessage, ByteBuf out) {
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("JDK");
        byte[] bodyBytes = serializer.serialize(rpcMessage.getData());
        out.writeBytes(bodyBytes);
        return bodyBytes.length;
    }
}
