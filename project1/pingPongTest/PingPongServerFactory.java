package pingPongTest;

/**
 * Created by qingyu on 4/26/16.
 */
public class PingPongServerFactory {
    private static volatile PingPongServerImplementation server =null;

    public static PingPongServerImplementation makePingServer() {
        if (server == null) {
            synchronized (PingPongServerFactory.class) {
                // Double check
                if (server == null) {
                    server = new PingPongServerImplementation();
                }
            }
        }
        return server;
    }
}
