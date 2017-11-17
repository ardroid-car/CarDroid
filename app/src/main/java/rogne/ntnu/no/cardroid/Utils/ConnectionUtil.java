package rogne.ntnu.no.cardroid.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidParameterException;

/**
 * Created by Kristoffer on 2017-11-17.
 */

public class ConnectionUtil extends Thread {

    private OnDisconnectListener listener;

    interface OnDisconnectListener {
        void onDisconnect(boolean isDisconnected);
    }

    private Socket conn;
    public final static int OUT = 0;
    public final static int IN = 1;
    private int type;

    public ConnectionUtil(Socket conn, int type) {
        this.conn = conn;
        if (type != OUT && type != IN) {
            throw new InvalidParameterException("Type needs to be either 0 (OUT) or 1 (IN)");
        }
    }

    public void setOnDisconnectListener(OnDisconnectListener listener){
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            switch (type) {
                case OUT:
                    checkOutStream();
                    break;
                case IN:
                    checkInStream();
            }
            sleep(2000);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void checkInStream() throws IOException {
        if (!conn.isClosed()) {
            int heartbeat = conn.getInputStream().read();
            if(heartbeat == -1){
                onDisconnect(true);
            }
        }
    }

    private void onDisconnect(boolean b) {
        if(listener != null){
            listener.onDisconnect(b);
        }
    }

    private void checkOutStream(){
        if (!conn.isClosed()) {
            try {
                conn.getOutputStream().write(1);
            } catch (IOException e) {
                onDisconnect(true);
            }
        }
    }
}
