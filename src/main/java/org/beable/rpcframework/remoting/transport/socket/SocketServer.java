package org.beable.rpcframework.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qing.wu
 */
@Slf4j
public class SocketServer {

    private static final int PORT = 9988;

    // TODO
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public void start(){
        try(ServerSocket server = new ServerSocket()){
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host,PORT));
            Socket socket;
            while ((socket = server.accept()) != null){
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new SocketHandlerRunnable(socket));
            }
        }catch (IOException e){
            log.error("occur IOException:", e);
        }
    }
}
