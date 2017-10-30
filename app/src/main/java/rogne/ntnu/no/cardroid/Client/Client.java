package rogne.ntnu.no.cardroid.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import rogne.ntnu.no.cardroid.Command;

/**
 * Created by Mikael on 13.10.2017.
 */

class Client implements Runnable {

    static String message = "test";
    static String ip;
    static int port;
    Socket client;
    PrintStream stream;
    BufferedReader buffer;
    Boolean send = false;
    String sendMessage;
    String input;
    Command cmd=null;


    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }


    public void run() {


        try {
            System.out.println ("Trying to connect");

            client = new Socket(ip, port);  //connect to server

            stream = new PrintStream(client.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("....");
        if (client.isConnected()) {
            System.out.println("Connected to:   " + ip + "     on Port: " + port);


            System.out.println(message);
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    String in;

                    try {
                        buffer = new BufferedReader(new InputStreamReader(client.getInputStream()));

                            while(true) {


                                System.out.println(buffer.toString());


                            while ((in = buffer.readLine()) != null) {
                                System.out.print("input is: ");
                                System.out.println(in);

                            }

                        }

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
            });
            th.start();
            while (client.isConnected()) {
                if (send) {
                    System.out.println("Meesage sent: " + sendMessage);
                    send(sendMessage);
                }


                if (client.isClosed()) {
                    message = "closed";
                }
            }
            // System.out.println("Connection lost");

        }
    }


    public String getMessage() {
        return sendMessage;
    }

    public void setMessage(String m) {
        sendMessage = m;
        send = true;
    }

    public void send(String i) {
        System.out.println("Message sent: " + i);
        stream.println(i);
        send = false;
    }

    public boolean isConnected() {
        if (client.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void setOutput(String i) {
        input = i;

    }

    public void setCommand(Command cm){
        cmd=cm;
        sendMessage=cmd.toString();
        send = true;

    }

}








