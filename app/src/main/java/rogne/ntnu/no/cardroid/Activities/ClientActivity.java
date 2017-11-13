package rogne.ntnu.no.cardroid.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;
import java.net.Socket;

import rogne.ntnu.no.cardroid.R;
import rogne.ntnu.no.cardroid.Runnables.Client;
import rogne.ntnu.no.cardroid.Threads.ConnectToServer;
import rogne.ntnu.no.cardroid.Threads.ImageReceiverThread;

/**
 * Created by Kristoffer on 2017-11-10.
 */

public class ClientActivity extends Activity {
    private final String IP = "192.168.0.125";
    private final int COMMAND_PORT = 6671;
    private final int VIDEO_PORT = 6672;
    private Button rightButton;
    private Button forwardButton;
    private Button leftButton;
    private Button backwardButton;
    private SeekBar speedBar;
    private int speed = 0;
    private Client client;
    private ConnectToServer commandConnector;
    private ConnectToServer videoConnector;
    private Socket command_socket;
    private Socket video_socket;
    private ImageView display;
    private ImageReceiverThread fetcher;
    private Button clawButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        speedBar = findViewById(R.id.activity_client_speed_Bar);
        speedBar.setProgress(255);
        speed = speedBar.getProgress();
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        display = findViewById(R.id.activity_client_display);

        rightButton = findViewById(R.id.activity_client_right_button);
        forwardButton = findViewById(R.id.activity_client_forward_button);
        backwardButton = findViewById(R.id.activity_client_backwards_button);
        leftButton = findViewById(R.id.activity_client_left_button);
        clawButton = findViewById(R.id.activity_client_claw_button);

        setOnClickListeners();
        commandConnector = new ConnectToServer(IP, COMMAND_PORT, socket -> command_socket = socket);
        commandConnector.start();
        videoConnector = new ConnectToServer(IP, VIDEO_PORT, socket -> video_socket = socket);
        videoConnector.start();
        new Thread(() -> {
            while (video_socket == null || command_socket == null) {
            }
            runOnUiThread(this::start);
        }).start();

    }

    private void start() {
        try {
            client = new Client(command_socket);
            fetcher = new ImageReceiverThread(video_socket);
            setFetcherListener();
            fetcher.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFetcherListener() {
        if (fetcher != null) {
            fetcher.setOnImageAvailableListener(bitmap -> runOnUiThread(() -> {
                display.setImageBitmap(bitmap);
            }));
        }
    }

    private void setOnClickListeners() {
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String buttonText = ((Button) v).getText().toString();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    client.handle(buttonText+":down", speed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    client.handle(buttonText+":up", speed);
                }
                return true;
            }
        };
        rightButton.setOnTouchListener(listener);
        leftButton.setOnTouchListener(listener);
        forwardButton.setOnTouchListener(listener);
        backwardButton.setOnTouchListener(listener);
        clawButton.setOnTouchListener(listener);
    }
}
