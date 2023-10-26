package co.gongzh.procbridge.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.Executor;

public class SSLClient extends Client{

    private SSLSocketFactory factory;

    public SSLClient(@NotNull String host, int port, long timeout, @Nullable Executor executor, SSLSocketFactory factory) {
        super(host, port, timeout, executor);
        this.factory = factory;
    }
}
