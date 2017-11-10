package rogne.ntnu.no.cardroid.Runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import rogne.ntnu.no.cardroid.Data.Command;

/**
 * Created by Mikael on 13.10.2017.
 */

public class ClientOLD implements Runnable {

    static String message = "test";
    static String ip;
    static int port;
    Socket client;
    PrintStream stream;
    BufferedReader buffer;
    Boolean send = false;
    String sendMessage;
    String input;
    Command cmd = null;
    static Boolean recieved = true;
    String resend = "Test";
    String lastInput;


    public ClientOLD(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        ClientOLD.message = message;
    }


    public void run() {


        try {
            System.out.println("Trying to connect");

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

                        while (true) {


                            System.out.println(buffer.toString());


                            while ((in = buffer.readLine()) != null) {
                                System.out.print("input is: ");
                                System.out.println(in);
                                lastInput = in;

                            }

                        }

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
            });
            th.start();
            while (client.isConnected()) {


                if (client.isClosed()) {
                    message = "closed";
                }
            }
            // System.out.println("Connection lost");

        }
    }

    public void sendMessage(String i) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean re = true;
                try {
                    while (re) {
                        stream.println(i);
                        Thread.sleep(500);

                        re = reSend(i);
                    }
                } catch (Exception e) {
                    System.out.println("Connection not established yet");
                }


            }

        });
        t.start();
    }


    public void send(String i) {
        setMessage(i);
        System.out.println("Message sent: " + i);
        sendMessage(i);

    }

    public boolean reSend(String i) {

        if (i.equals(lastInput)) {
            System.out.println("Lastinput is: " + lastInput);
            return false;

        } else {
            System.out.println("Resent" + i);
            return true;
        }


    }


    public void setOutput(String i) {
        input = i;

    }


    public void setCommand(Command cm) {
        cmd = cm;
        sendMessage = cmd.toString();
        send = true;

    }


    public boolean isRecieved() {
        return recieved;
    }
}








