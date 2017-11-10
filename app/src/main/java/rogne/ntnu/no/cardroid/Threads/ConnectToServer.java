package rogne.ntnu.no.cardroid.Threads;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by krist on 2017-11-05.
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
        setName("Client-Connector");
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
