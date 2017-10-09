package rogne.ntnu.no.cardroid.Server;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Kristoffer on 2017-09-28.
 */

public class Server implements Runnable {
    private ServerSocket serverSocket = null;
    private int port;
    private boolean running = false;
    private boolean stop = false;
    private TextView log;
    PrintStream out;
    public Server(TextView log, int port){
        this.port = port;
        this.log = log;
    }
    @Override
    public void run() {

        int tries = 3;
        while (tries > 0 && !startServer(tries)) {
            tries--;
        }
        boolean running = true;
        while (running &! this.stop) {
            listenForConnection();

        }
    }

    private  boolean startServer(int tries) {
        try {
            serverSocket = new ServerSocket(port, 10);
            printToView("Server started on port: " + port);
            return true;
        } catch (IOException ex) {
            int attempts = 4 - tries;
            System.out.println("Attempt " + attempts);
            System.out.println("Could not start server on port: " + port + ". Reason: " + ex.getMessage());
            return false;
        }
    }
    public void stopServer(){
        running = false;
        stop = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForConnection() {
        Socket conn = null;
        try {
            conn = serverSocket.accept();
            printToView("Connection recived from " + conn.getInetAddress().getHostName() + " : " + conn.getPort());
            handle(conn);
        } catch (IOException ex) {
            printToView("Could not accept connection. Reason: " + ex.getMessage());
        }
    }

    private void handle(Socket conn) {
        String line , input = "";

        try
        {
            //get socket writing and reading streams
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            out = new PrintStream(conn.getOutputStream());
            //Now start reading input from client
            running = true;
            while(running)
            {
                line = in.readLine();
                if(line != null){
                printToView(line);
                } else if(!conn.isConnected()){
                    printToView("Connection to device lost");
                    listenForConnection();
                } else if(line == null){
                    System.out.println(line);
                }
            }

            out.println("Server shutting down");
            printToView("Server shutting down");
            //client disconnected, so close socket
            conn.close();
        }

        catch (IOException e)
        {
            System.out.println("IOException on socket : " + e);
            e.printStackTrace();
        }
    }

    private void printToView(final String line) {
        System.out.println(line);
        log.post(new Runnable() {
            @Override
            public void run() {
                log.append("\n" + line);
            }
        });
    }
    public boolean send(String line){
        if(out != null){
            //printToView(line);
            out.println("[" + line +"]");
        }
        return out != null;
    }
}
