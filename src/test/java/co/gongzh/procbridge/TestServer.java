package co.gongzh.procbridge;

import co.gongzh.procbridge.server.Server;
import co.gongzh.procbridge.utils.Delegate;
import co.gongzh.procbridge.utils.Handler;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

public class TestServer extends Server {

    public static final int PORT = 8000;

    public TestServer() {
        super(PORT, new Delegate() {
            @Handler
            Object echo(Object payload) {
                return payload;
            }

            @Handler
            int sum(JSONArray numbers) {
                return numbers.toList().stream().mapToInt(el -> (int) el).sum();
            }

            @Handler
            void err() {
                throw new RuntimeException("generated error");
            }

            @Override
            protected @Nullable Object handleUnknownRequest(@Nullable String method, @Nullable Object payload) {
                return null;
            }
        });
    }

    public static void main(String[] args) {
        Server server = new TestServer();
        server.start();
        System.out.println("Test Server is on " + TestServer.PORT + "...");
    }
}
