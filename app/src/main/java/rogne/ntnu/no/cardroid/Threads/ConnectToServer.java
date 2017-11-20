package rogne.ntnu.no.cardroid.Threads;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by krist on 2017-11-05.
 * A thread that tries to connect to the given ip and port. Retries every 1000ms if it could not connect. When a connection is made, it sends the now connected socket back through a callback.
 */

public class ConnectToServer extends Thread {
    private final String TAG = "STREAMER";
    private long threadTimeout = 1000;
    private boolean running = false;

    public interface OnConnected {
        void onConnected(Socket socket);
    }

    private OnConnected callback;
    private InetSocketAddress address;

    public ConnectToServer(String ip, int port, OnConnected callback) {
        this.address = new InetSocketAddress(ip, port);
        this.callback = callback;
        this.running = true;
        setName("ClientOLD-Connector");
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket socket = new Socket();
                socket.connect(address);
                onConnect(socket);
                running = false;
            } catch(ConnectException e){
                try {
                    Log.e(TAG,"Connection failed, retrying in " + threadTimeout + " ms.");
                    sleep(threadTimeout);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
        Log.v(TAG, getName() + " thread finished");
    }
    public void stopConnecting(){
        running = false;
        Log.e(TAG, "stopConnecting called");
    }

    private void onConnect(Socket socket) {
        if (callback != null) {
            callback.onConnected(socket);
        }
    }
}
