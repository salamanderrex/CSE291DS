package pingPongTest;


import java.net.*;

import rmi.*;

public class PingPongTestClient  {
    public static void main(String [] args) {
        int id = Integer.parseInt(args[0]);
        InetSocketAddress address = new InetSocketAddress("myserver",8888);
        PingPongServer  server = Stub.create(PingPongServer.class, address);
        try {
            System.out.println(server.ping(id));
        } catch (Exception e){
            System.out.print("no RMI exception");

        }

    }

}

