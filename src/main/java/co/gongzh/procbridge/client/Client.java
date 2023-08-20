package co.gongzh.procbridge.client;

import co.gongzh.procbridge.exception.ClientException;
import co.gongzh.procbridge.exception.ServerException;
import co.gongzh.procbridge.exception.TimeoutException;
import co.gongzh.procbridge.model.StatusCode;
import co.gongzh.procbridge.utils.Protocol;
import co.gongzh.procbridge.utils.TimeoutExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author Gong Zhang
 */
public class Client {

    private final @NotNull String host;
    private final int port;
    private final long timeout;
    private final @Nullable Executor executor;

    public static final long FOREVER = 0;

    public Client(@NotNull String host, int port, long timeout, @Nullable Executor executor) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.executor = executor;
    }

    public Client(@NotNull String host, int port) {
        this(host, port, FOREVER, null);
    }

    @NotNull
    public final String getHost() {
        return host;
    }

    public final int getPort() {
        return port;
    }

    public long getTimeout() {
        return timeout;
    }

    @Nullable
    public Executor getExecutor() {
        return executor;
    }

    @Nullable
    public final Object request(@Nullable String method, @Nullable Object payload) throws ClientException, TimeoutException, ServerException {
        final StatusCode[] respStatusCode = { null };
        final Object[] respPayload = { null };
        final Throwable[] innerException = { null };

        try (final Socket socket = new Socket(host, port)) {
            Runnable task = () -> {
                try (OutputStream os = socket.getOutputStream();
                     InputStream is = socket.getInputStream()) {

                    Protocol.writeRequest(os, method, payload);
                    Map.Entry<StatusCode, Object> entry = Protocol.readResponse(is);
                    respStatusCode[0] = entry.getKey();
                    respPayload[0] = entry.getValue();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    innerException[0] = ex;
                }
            };

            if (timeout <= 0) {
                task.run();
            } else {
                TimeoutExecutor guard = new TimeoutExecutor(timeout, executor);
                guard.execute(task);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ClientException(ex);
        }

        if (innerException[0] != null) {
            throw new RuntimeException(innerException[0]);
        }

        if(respStatusCode[0] == StatusCode.BAD_RESPONSE){
            throw new ServerException("Bad Response: "+respPayload[0]+" ("+respPayload.length+")");
        }

        return respPayload[0];
    }

}
