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


    public interface OnSendListener {
        void onSend(String sentLine);
    }

    public  Handler PHONE_HANDLER = new PhoneHandler();
    public Handler CAR_HANDLER = new CarHandler();
    private OnSendListener callback;
    private Handler handler;
    private ServerSocket serverSocket = null;
    private int port;
    private boolean running = false;
    private boolean stop = false;

    public Server(int port, Handler handler) {
        this.port = port;
        this.handler = handler;
        this.handler.setCallback(this.callback);
    }

    @Override
    public void run() {

        int tries = 3;
        while (tries > 0 && !startServer(tries)) {
            tries--;
        }
        boolean running = true;
        while (running & !this.stop) {
            listenForConnection();

        }
    }

    public void setOnSendListener(OnSendListener listener) {
        this.callback = listener;
    }

    private boolean startServer(int tries) {
        try {
            serverSocket = new ServerSocket(port, 10);
            return true;
        } catch (IOException ex) {
            int attempts = 4 - tries;
            System.out.println("Attempt " + attempts);
            System.out.println("Could not start server on port: " + port + ". Reason: " + ex.getMessage());
            return false;
        }
    }

    private void listenForConnection() {
        Socket conn = null;
        try {
            conn = serverSocket.accept();
            handle(conn);
        } catch (IOException ex) {
        }
    }

    public void handle(Socket conn) {
        String line, input = "";
        PrintStream out;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            out = new PrintStream(conn.getOutputStream());
            handler.setOutputStream(out);

            running = true;
            while (running) {
                line = in.readLine();
                handler.handle(line);
            }
            out.print("Server shutting down");
            conn.close();
        } catch (IOException e) {
            System.out.println("IOExeption on socet : " + e);
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;
        stop = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String line){
        handler.handle(line);
    }


}
