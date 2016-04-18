package pingPongTest;


import rmi.Skeleton;
import rmi.*;
import java.net.InetSocketAddress;


/**
 * Created by qingyu on 4/16/16./
 */
public class PingPongTestServer {
    public static void main(String [] args) {

        PingPongServerImplementation server = new PingPongServerImplementation();

        InetSocketAddress address = new InetSocketAddress("localhost",8889);
        //InetSocketAddress address = new InetSocketAddress("myserver",8888);
        Skeleton <PingPongServer> skeleton = new Skeleton (PingPongServer.class,server,address);

        try {
            skeleton.start();
        } catch (RMIException e ){
            System.out.println("error");
        }
        System.out.println("start server Daze~");

    }
}
