package co.gongzh.procbridge.server;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLServerSocketFactory;
import java.net.ServerSocket;

public class SSLServer extends Server{

    private SSLServerSocketFactory factory;

    public SSLServer(int port, @NotNull IDelegate delegate, SSLServerSocketFactory factory) {
        super(port, delegate);
        this.factory = factory;
    }

    @SneakyThrows
    @Override
    public synchronized ServerSocket getServerSocket() {
        return factory.createServerSocket(this.getPort());
    }
}
