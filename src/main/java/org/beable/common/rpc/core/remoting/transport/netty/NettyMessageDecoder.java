package org.beable.common.rpc.core.remoting.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.beable.common.rpc.core.remoting.constants.MessageType;
import org.beable.common.rpc.core.remoting.dto.RpcMessage;
import org.beable.common.rpc.core.remoting.dto.RpcRequest;
import org.beable.common.rpc.core.remoting.dto.RpcResponse;
import org.beable.common.rpc.core.serialize.Serializer;
import org.beable.common.rpc.extension.ExtensionLoader;

/**
 * @author qing.wu
 */
@Slf4j
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * 公式: 发送数据包长度 = 长度域的值 + lengthFieldOffset + lengthFieldLength + lengthAdjustment
     * https://blog.csdn.net/thinking_fioa/article/details/80573483
     */
    public NettyMessageDecoder(){
        this(8 * 1024 * 1024,0,4,-4,0);
    }

    /**
     * <pre>
     * 1. maxFrameLength - 发送的数据帧最大长度
     * 2. lengthFieldOffset - 定义长度域位于发送的字节数组中的下标。换句话说：发送的字节数组中下标为${lengthFieldOffset}的地方是长度域的开始地方
     * 3. lengthFieldLength - 用于描述定义的长度域的长度。换句话说：发送字节数组bytes时, 字节数组bytes[lengthFieldOffset, lengthFieldOffset+lengthFieldLength]域对应于的定义长度域部分
     * 4. lengthAdjustment - 满足公式: 发送的字节数组bytes.length - lengthFieldLength = bytes[lengthFieldOffset, lengthFieldOffset+lengthFieldLength] + lengthFieldOffset + lengthAdjustment
     * 5. initialBytesToStrip - 接收到的发送数据包，去除前initialBytesToStrip位
     * 6. failFast - true: 读取到长度域超过maxFrameLength，就抛出一个 TooLongFrameException。false: 只有真正读取完长度域的值表示的字节之后，才会抛出 TooLongFrameException，默认情况下设置为true，建议不要修改，否则可能会造成内存溢出
     * 7. ByteOrder - 数据存储采用大端模式或小端模式
     * </pre>
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     * @param lengthAdjustment
     * @param initialBytesToStrip
     */
    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                               int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if(decoded instanceof ByteBuf){
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= 5){
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .build();
        // build message object
        int bodyLength = fullLength - 5;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // deserialize the object
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("JDK");
            if (messageType == MessageType.REQUEST.getValue()) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            }else if (messageType == MessageType.RESPONSE.getValue()){
                RpcResponse tmpValue = serializer.deserialize(bs,RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;
    }
}
