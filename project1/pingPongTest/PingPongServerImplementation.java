package pingPongTest;

/**
 * Created by qingyu on 4/16/16.
 */
public class PingPongServerImplementation implements  PingPongServer {
    public String ping (int id) {
        return "pong" + id;
    }
}
