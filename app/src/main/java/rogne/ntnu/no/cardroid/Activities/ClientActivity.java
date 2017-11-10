package rogne.ntnu.no.cardroid.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;
import java.net.Socket;

import rogne.ntnu.no.cardroid.Data.Command;
import rogne.ntnu.no.cardroid.R;
import rogne.ntnu.no.cardroid.Runnables.Client;
import rogne.ntnu.no.cardroid.Threads.ConnectToServer;

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
    private int speed=0;
    private Client client;
    private ConnectToServer commandConnector;
    private ConnectToServer videoConnector;
    private Socket command_socket;
    private Socket video_socket;
    private Command lastCommand;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        speedBar = findViewById(R.id.activity_client_speed_Bar);


        rightButton = findViewById(R.id.activity_client_right_button);
        forwardButton = findViewById(R.id.activity_client_forward_button);
        backwardButton =  findViewById(R.id.activity_client_backwards_button);
        leftButton = findViewById(R.id.activity_client_left_button);

        setOnClickListeners();
        commandConnector = new ConnectToServer(IP, COMMAND_PORT, socket -> command_socket = socket);
        commandConnector.start();

        videoConnector = new ConnectToServer(IP, VIDEO_PORT, socket -> start(socket));
        videoConnector.start();


    }

    private void start(Socket socket) {
        video_socket = socket;
        try {
            client = new Client(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setOnClickListeners() {
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String buttonText = ((Button) v).getText().toString();
                Command cmd = null;
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    cmd = getCommand(buttonText);
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    cmd = getCommand("stop");
                }
                send(cmd);
                return true;
            }
        };
    }
    private Command getCommand(String text) {
        Command cmd = null;
        switch(text){
            case "forward":
                cmd = new Command(Command.FORWARD, speed, Command.START);
                break;
            case "backward":
                cmd = new Command(Command.BACKWARD, speed, Command.START);
                break;
            case "left":
                cmd = new Command(Command.TURN_LEFT, speed, Command.START);
                break;
            case "right":
                cmd = new Command(Command.TURN_RIGHT, speed, Command.START);
                break;
            default:
                cmd = new Command(Command.STOP_MOVING, 0, Command.STOP);
        }
        return cmd;
    }

    private void send(Command cmd) {
        if(client != null){
            if(cmd.isMoveDirection()){
                lastCommand = cmd;
            }
            if(cmd.isStop()){
                client.sendCommand(lastCommand);
                lastCommand = cmd;
            }
            client.sendCommand(cmd);
        }
    }
}
