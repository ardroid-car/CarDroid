package rogne.ntnu.no.cardroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import rogne.ntnu.no.cardroid.Server.Server;

public class CarServerActivity extends AppCompatActivity {

    private Server carServer;
    private Server phoneServer;
    private Command lastCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_server);
        carServer = new Server(((TextView) findViewById(R.id.ServerLog)), 6670);
        phoneServer = new Server(((TextView) findViewById(R.id.ServerLog)), 6671);
        buttonListeners();
        SeekBar s = (SeekBar) findViewById(R.id.seekBar);
        s.setProgress(255);
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ((TextView) findViewById(R.id.SpeedValue)).setText("Speed: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(carServer).start();
        new Thread(phoneServer).start();
        stop();
    }

    public void altCarControl(View v){
        carServer.stopServer();
        phoneServer.stopServer();
        Intent intent = new Intent(this, AltControlActivity.class);
        startActivity(intent);
    }

    public void camTestSend(View view){
        Intent intent = new Intent(this, CamActivity.class);
        startActivity(intent);
    }
    public void camTestRecieve(View view){
        Intent intent = new Intent(this, CamDisplayActivity.class);
        startActivity(intent);
    }

    public void forward(){
        Command cmd = new Command(Command.FORWARD, ((SeekBar) findViewById(R.id.seekBar)).getProgress(), Command.START);
        sendCommand(cmd.toString());
        lastCommand = cmd;
    }
    public void backward(){
        Command cmd = new Command(Command.BACKWARD, ((SeekBar) findViewById(R.id.seekBar)).getProgress(), Command.START);
        sendCommand(cmd.toString());
        lastCommand = cmd;
    }
    public void left(){
        Command cmd = new Command(Command.TURN_LEFT, ((SeekBar) findViewById(R.id.seekBar)).getProgress(), Command.START);
        sendCommand(cmd.toString());
    }
    public void right(){
        Command cmd = new Command(Command.TURN_RIGHT, ((SeekBar) findViewById(R.id.seekBar)).getProgress(), Command.START);
        sendCommand(cmd.toString());
    }
    public void stop(){
        Command cmd = new Command(Command.STOP_MOVING, ((SeekBar) findViewById(R.id.seekBar)).getProgress(), Command.STOP);
        sendCommand(cmd.toString());
        lastCommand = cmd;
    }
    private void resume(){
        if(lastCommand!=null){
            sendCommand(lastCommand.toString());
        }
    }

    public void sendCommand(String cmd){
        carServer.send(cmd);
    }

    private void buttonListeners() {
        View.OnTouchListener directionalListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    startMove(v);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stop();
                }
                return true;
            }
        };
        View.OnTouchListener turnListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    startMove(v);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    resume();
                }
                return true;
            }
        };
        findViewById(R.id.FwdBtn).setOnTouchListener(directionalListener);
        findViewById(R.id.BackBtn).setOnTouchListener(directionalListener);
        findViewById(R.id.LeftBtn).setOnTouchListener(turnListener);
        findViewById(R.id.RightBtn).setOnTouchListener(turnListener);
    }

    private void startMove(View v) {
        switch (v.getId()){
            case R.id.FwdBtn:
                forward();
                break;
            case R.id.BackBtn:
                backward();
                break;
            case R.id.LeftBtn:
                left();
                break;
            case R.id.RightBtn:
                right();
                break;
            default:
                break;
        }
    }
}
