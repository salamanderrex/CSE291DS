package com.p0;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class catserver {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        // The name of the file to open.
        String fileName = args[0];

        // This will reference one line at a time
        String line = null;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            // FileReader reads text files in the default encoding.
            fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            bufferedReader =
                    new BufferedReader(fileReader);


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

        int portNumber = Integer.parseInt(args[1]);

        try {
                ServerSocket serverSocket =
                        new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader socketIn = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = socketIn.readLine()) != null) {

                String socketReturn = "";
                if (inputLine.equals("LINE")) {
                    System.out.println("receive LINE");
                    if ((line = bufferedReader.readLine()) == null) {
                        //reopen the file
                        // Always close files.
                        bufferedReader.close();

                        try {
                            // FileReader reads text files in the default encoding.
                            fileReader =
                                    new FileReader(fileName);

                            // Always wrap FileReader in BufferedReader.
                            bufferedReader =
                                    new BufferedReader(fileReader);


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

                        if ((line = bufferedReader.readLine()) == null) {
                            System.out.println("file open error");
                            System.exit(1);

                        }
                    }

                    out.println(line.toUpperCase());
                } else {
                    System.out.println("receive "+inputLine);
                }


            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            bufferedReader.close();

        }
    }
}
