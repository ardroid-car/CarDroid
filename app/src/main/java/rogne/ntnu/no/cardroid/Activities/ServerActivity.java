package rogne.ntnu.no.cardroid.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import rogne.ntnu.no.cardroid.Data.CameraImageStreamer;
import rogne.ntnu.no.cardroid.R;
import rogne.ntnu.no.cardroid.Runnables.CarHandler;
import rogne.ntnu.no.cardroid.Runnables.CommandBox;
import rogne.ntnu.no.cardroid.Runnables.PhoneHandler;
import rogne.ntnu.no.cardroid.Runnables.Server;
import rogne.ntnu.no.cardroid.Threads.ServerConnectionListener;
import rogne.ntnu.no.cardroid.Utils.ByteUtils;
import rogne.ntnu.no.cardroid.Utils.ImageUtils;

public class ServerActivity extends AppCompatActivity {
    private CameraImageStreamer streamer;
    private Socket videoSocket;
    private Socket carSocket;
    private Socket phoneSocket;
    private TextView phoneStatus;
    private ServerConnectionListener mainServer;
    private Server carServer;
    private Server phoneServer;
    private TextView carStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        streamer = new CameraImageStreamer(this);
        phoneStatus = (TextView) findViewById(R.id.activity_server_phone_status);
        carStatus = (TextView) findViewById(R.id.activity_server_car_status);
        CommandBox box = new CommandBox(10);
        carServer = new Server(6670, new CarHandler(box));
        phoneServer = new Server(6671, new PhoneHandler(box));
        Thread carThread = new Thread(carServer);
        carThread.start();
        Thread phoneThread = new Thread(phoneServer);
        phoneThread.start();
        carServer.setOnConnectedListener(socket -> updateCarStatus(socket));
        phoneServer.setOnConnectedListener(socket -> updatePhoneStatus(socket));
        listenForConnection();
    }

    private void updatePhoneStatus(Socket socket) {
        this.phoneSocket = socket;
        runOnUiThread(()-> phoneStatus.setText("Connected to: " + phoneSocket.getInetAddress()));
    }

    private void updateCarStatus(Socket socket) {
        this.carSocket = socket;
        runOnUiThread(()-> carStatus.setText("Connected to: " + carSocket.getInetAddress()));
    }

    private void listenForConnection() {
        mainServer = new ServerConnectionListener(6672, this::perpareToStream);
        mainServer.start();
    }

    private void perpareToStream(Socket socket) {
        this.videoSocket = socket;
        runOnUiThread(() -> phoneStatus.setText("Connected to: " + socket.getInetAddress()));
        mainServer.stopServer();
        mainServer = null;
        runOnUiThread(()->startStream());
    }

    private void startStream() {
        if (videoSocket != null) {
            try {
                final OutputStream output = videoSocket.getOutputStream();
                streamer.setCallback(i -> {
                    byte[] data = ImageUtils.imageToByteArray(i);
                    byte[] dataLength = ByteUtils.intToByte(data.length);
                    try {
                        output.write(ByteUtils.concat(ByteUtils.getSeperatorBytes(), dataLength, data));
                    } catch (IOException e) {
                        try {
                            if(videoSocket != null) {
                                videoSocket.close();
                                videoSocket = null;
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        runOnUiThread(()->listenForConnection());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        streamer.startImageStream();
    }

    @Override
    protected void onStop() {
        if (mainServer != null) {
            mainServer.stopServer();
            mainServer = null;
        }
        if (streamer != null) {
            streamer.stopImageStream();
        }
        super.onStop();
    }
}
