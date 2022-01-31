package org.beable.rpcframework.remoting.transport.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcServer {

    private static final int PORT = 9988;

    @SneakyThrows
    public void start(){
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                2,
                Executors.defaultThreadFactory()
        );
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new NettyMessageEncoder());
                            p.addLast(new NettyMessageDecoder());
                            p.addLast(serviceHandlerGroup, new NettyRpcServerHandler());
                        }
                    });
            // 绑定端口
            ChannelFuture f = bootstrap.bind(host, PORT).sync();
            // 等待服务器端口关闭
            f.channel().closeFuture().sync();
        }catch (InterruptedException e){
            log.error("occur exception when start server:", e);
        }finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }
}
