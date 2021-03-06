package test;

import test.PingPongServer;
import test.PingPongServerImplementation;
import rmi.RMIException;
import rmi.Skeleton;
import rmi.Stub;

import java.net.InetSocketAddress;

/**
 * Created by qingyu on 4/17/16.
 */
public class LocalPingPongTest extends Test {
    /**
     * Test notice.
     */
    public static final String notice =
            "checking a local pingpong";


    /**
     * Performs the test.
     * <p>
     * <p>
     * This method enters an infinite loop, which may only be terminated by
     * <code>clean</code> upon termination of the test by timeout.
     */
    @Override
    protected synchronized void perform() throws TestFailed {

        try {


            PingPongServerImplementation server = new PingPongServerImplementation();
            InetSocketAddress address = new InetSocketAddress("localhost", 1234);
            // InetSocketAddress address = new InetSocketAddress("myserver",8888);
            Skeleton<PingPongServer> skeleton = new Skeleton(PingPongServer.class, server, address);

            try {
                skeleton.start();
            } catch (RMIException e) {
                System.out.println("error");
                throw new TestFailed("skeleton cannot start");
            }
            System.out.println("start server Daze~");

            // InetSocketAddress address = new InetSocketAddress("myserver",8888);
            PingPongServer remoteserver = Stub.create(PingPongServer.class, address);
            try {
                String result = remoteserver.ping(-1);
                if (!result.equals("pong-1")) {
                    throw new TestFailed("wrong return");
                }

            } catch (Exception e) {
                System.out.print("no RMI exception");
                throw new TestFailed("no RMI exception");

            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new TestFailed(" pingpong test fail");
        }
    }


    /**
     * Terminates the infinite loop.
     */
    @Override
    protected synchronized void clean() {
        notifyAll();
    }
}
