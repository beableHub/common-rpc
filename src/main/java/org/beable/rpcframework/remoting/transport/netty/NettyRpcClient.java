package org.beable.rpcframework.remoting.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.beable.rpcframework.remoting.constants.MessageType;
import org.beable.rpcframework.remoting.dto.RpcMessage;
import org.beable.rpcframework.remoting.dto.RpcRequest;
import org.beable.rpcframework.remoting.dto.RpcResponse;
import org.beable.rpcframework.remoting.transport.Client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author qing.wu
 */
@Slf4j
public class NettyRpcClient implements Client {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    private final UnprocessedRequests unprocessedRequests = UnprocessedRequests.getInstance();

    public NettyRpcClient(){
        // initialize resources
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // The timeout period of the connection
                //  If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline p = socketChannel.pipeline();
                        // If no data is sent to the server within 5 seconds, a heartbeat request is sent
                        p.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                        p.addLast(new NettyMessageEncoder());
                        p.addLast(new NettyMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
    }

    @Override
    public Object send(RpcRequest rpcRequest){
        // TODO
        SocketAddress socketAddress = new InetSocketAddress("10.118.32.165", 9988);
        // build return value
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // get  server address related channel
        Channel channel = doConnect(socketAddress);
        if (channel.isActive()){
            unprocessedRequests.put(rpcRequest.getRequestId(),resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .messageType(MessageType.REQUEST.getValue())
                    .data(rpcRequest)
                    .build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);

                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        }
        RpcResponse<Object> response = new RpcResponse<>();
        try {
            response = resultFuture.get();
        } catch (Exception e) {
            log.error("send error",e);
        }
        return Objects.nonNull(response)?response.getData():null;
    }

    public Channel doConnect(SocketAddress address){
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(address).addListener((ChannelFutureListener)future -> {
            if (future.isSuccess()){
                log.info("The client has connected [{}] successful!", address.toString());
                completableFuture.complete(future.channel());
            }else {
                throw new IllegalStateException("connect fail. address:" + address.toString());
            }
        });
        try {
            return completableFuture.get(10,TimeUnit.SECONDS);
        } catch (Exception e){
            throw new RuntimeException(address + " connect fail.", e);
        }
    }
}
