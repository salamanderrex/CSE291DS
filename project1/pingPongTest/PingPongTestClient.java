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

        InetSocketAddress address = new InetSocketAddress("localhost",8889);
        //InetSocketAddress address = new InetSocketAddress("myserver",8889);

        PingPongServer  server = Stub.create(PingPongServer.class, address);
        int TEST_TIME = 4;
        int count = 0;
        for (int i = 0 ; i < TEST_TIME; i ++) {


            try {
                System.out.println(server.ping(id));
                count ++;

            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        System.out.println("4 Test Completed,"+(TEST_TIME - count)+ " times Failed" );

    }

}

