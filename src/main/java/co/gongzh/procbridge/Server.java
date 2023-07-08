package co.gongzh.procbridge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Gong Zhang
 */
public class Server {

    private final int port;
    private final @NotNull IDelegate delegate;

    private ExecutorService executor;
    private ServerSocket serverSocket;
    private boolean started;

    private @Nullable PrintStream logger;

    public Server(int port, @NotNull IDelegate delegate) {
        this.port = port;
        this.delegate = delegate;

        this.started = false;
        this.executor = null;
        this.serverSocket = null;
        this.logger = System.err;
    }

    public final synchronized boolean isStarted() {
        return started;
    }

    public final int getPort() {
        return port;
    }

    @Nullable
    public PrintStream getLogger() {
        return logger;
    }

    public void setLogger(@Nullable PrintStream logger) {
        this.logger = logger;
    }

    public synchronized void start() {
        if (started) {
            throw new IllegalStateException("server already started");
        }

        final ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new ServerException(e);
        }
        this.serverSocket = serverSocket;

        final ExecutorService executor = Executors.newCachedThreadPool();
        this.executor = executor;
        executor.execute(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    Connection conn = new Connection(socket, delegate);
                    synchronized (Server.this) {
                        if (!started) {
                            return; // finish listener
                        }
                        executor.execute(conn);
                    }
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                    return; // finish listener
                }
            }
        });

        started = true;
    }

    public synchronized void stop() {
        if (!started) {
            throw new IllegalStateException("server does not started");
        }

        executor.shutdown();
        executor = null;

        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
        serverSocket = null;

        this.started = false;
    }

    final class Connection implements Runnable {

        private final Socket socket;
        private final IDelegate delegate;

        Connection(Socket socket, IDelegate delegate) {
            this.socket = socket;
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try (OutputStream os = socket.getOutputStream();
                 InputStream is = socket.getInputStream()) {

                Map.Entry<String, Object> req = Protocol.readRequest(is);
                String method = req.getKey();
                Object payload = req.getValue();

                Object result = null;
                Exception exception = null;
                try {
                    result = delegate.handleRequest(method, payload);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    exception = ex;
                }

                if (exception != null) {
                    Protocol.writeBadResponse(os, exception.getMessage());
                } else {
                    Protocol.writeGoodResponse(os, result);
                }
            } catch (Exception ex) {
                if (logger != null) {
                    ex.printStackTrace(logger);
                }
            }
        }

    }

}
