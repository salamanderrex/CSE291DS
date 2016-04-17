package pingPongTest;


import java.net.*;

import rmi.*;

public class PingPongTestClient  {
    public static void main(String [] args) {
        int id = 0;
        if (args.length >= 1)
            id = Integer.parseInt(args[0]);
        else
            id = -1;
        InetSocketAddress address = new InetSocketAddress("localhost",8888);
       // InetSocketAddress address = new InetSocketAddress("myserver",8888);
        PingPongServer  server = Stub.create(PingPongServer.class, address);
        try {
            System.out.println(server.ping(id));

        } catch (Exception e){
            System.out.print("no RMI exception");

        }

    }

}

