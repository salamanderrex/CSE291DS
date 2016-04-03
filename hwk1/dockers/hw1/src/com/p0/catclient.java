package com.p0;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Pack200;

class RunTask extends TimerTask {
    PrintWriter out;
     Timer timer;
     int count = 0;
    public RunTask(PrintWriter out, Timer timer) {

        this.out = out;
        this.timer = timer;
    }
    @Override
    public void run() {

        if (count <=10) {
            out.print("LINE\n");
            out.flush();
            System.out.println("timer count "+count*3 +"s");
            count ++;

        } else {
            timer.cancel();
            System.out.println("STOP Timer");
            //System.exit(0);

        }


    }
}
public class catclient {



    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = "myserver";
        int portNumber = Integer.parseInt(args[1]);
        String fileName = args[0];

        try {
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in));



            Timer timer = new Timer();
            TimerTask tasknew = new RunTask(out,timer);
            timer.schedule(tasknew,0,3000);

            /*
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                String receive = in.readLine();
                System.out.println("echo: " + receive);
                boolean inFile = checkSentence(fileName,receive);
                System.out.println(inFile);
                if (inFile) {
                    out.write("OK\n");
                } else {
                    out.write("MISSING\n");
                }
                out.flush();
            }
            */

            int count = 0;
            String receive;
            while ((receive = in.readLine()) != null) {
                System.out.println("echo: " + receive);
                boolean inFile = checkSentence(fileName,receive);
                System.out.println(inFile);
                if (inFile) {
                    out.write("OK\n");
                } else {
                    out.write("MISSING\n");
                }
                out.flush();
                count++;
                if (count > 10) {
                    in.close();
                    out.close();
                    stdIn.close();
                    echoSocket.close();
                    System.out.println("job done, send line " + count+ " times");
                    System.exit(0);

                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }



    private static boolean checkSentence (String fileName, String s) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            // FileReader reads text files in the default encoding.
            fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            bufferedReader =
                    new BufferedReader(fileReader);


            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equalsIgnoreCase(s)) {
                    return true;
                }

            }
            bufferedReader.close();
            fileReader.close();


        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return false;
    }
}
