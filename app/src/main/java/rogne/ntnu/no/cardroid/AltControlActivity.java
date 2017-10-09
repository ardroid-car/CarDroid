package rogne.ntnu.no.cardroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import rogne.ntnu.no.cardroid.Server.Server;

public class AltControlActivity extends AppCompatActivity {

    private final long commandInterval = 200;
    private Server carServer;
    private Server phoneServer;
    private Command lastCommand;
    private Command quedCommand;
    private long timeSinceLastMove = System.currentTimeMillis();
    private long timeSinceLastTurn = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alt_control);
        carServer = new Server(((TextView) findViewById(R.id.ServerLog2)), 6670);
        phoneServer = new Server(((TextView) findViewById(R.id.ServerLog2)), 6671);
        new Thread(carServer).start();
        new Thread(phoneServer).start();
        stop();
        setSeekBarListeners();
    }

    private void setSeekBarListeners() {

        SeekBar s2 = (SeekBar) findViewById(R.id.seekBar2);
        s2.setProgress(255);
        s2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                turn(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(255);
                resume();

            }
        });
        SeekBar s3 = (SeekBar) findViewById(R.id.seekBar3);
        s3.setProgress(255);
        s3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                move(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                stop();

            }
        });
    }

    private void turn(int i) {
            if (i - 255 > 0) {
                ((TextView) findViewById(R.id.TurnText)).setText("Truning left: " + (i - 255));
                left(i - 255);
            } else if (i - 255 < 0) {
                ((TextView) findViewById(R.id.TurnText)).setText("Turning Right: " + i);
                right(i);
            } else {
                ((TextView) findViewById(R.id.TurnText)).setText("Not Turning");
                resume();
            }
    }

    private void move(int i) {
        if (timeSinceLastMove + commandInterval < System.currentTimeMillis()) {
            if (i - 255 > 0) {
                ((TextView) findViewById(R.id.SpeedText)).setText("Forward speed: " + (i - 255));
                forward(i - 255);
            } else if (i - 255 < 0) {
                ((TextView) findViewById(R.id.SpeedText)).setText("Backward speed: " + i);
                backward(i);
            } else {
                ((TextView) findViewById(R.id.SpeedText)).setText("No Speed");
                stop();
            }
            timeSinceLastMove = System.currentTimeMillis();
        }

    }

    public void forward(int i) {
        Command cmd = new Command(Command.FORWARD, i, Command.START);
        sendCommand(cmd.toString());
        lastCommand = cmd;
    }

    public void backward(int i) {
        Command cmd = new Command(Command.BACKWARD, i, Command.START);
        sendCommand(cmd.toString());
        lastCommand = cmd;
    }

    public void left(int i) {
        Command cmd = new Command(Command.TURN_LEFT, i, Command.START);
        if (timeSinceLastTurn + commandInterval < System.currentTimeMillis()) {
            sendCommand(cmd.toString());
            timeSinceLastMove = System.currentTimeMillis();
        }
    }

    public void right(int i) {
        Command cmd = new Command(Command.TURN_RIGHT, i, Command.START);
        if (timeSinceLastTurn + commandInterval < System.currentTimeMillis()) {
            sendCommand(cmd.toString());
            timeSinceLastTurn = System.currentTimeMillis();
        }
    }

    public void stop() {
        Command cmd = new Command(Command.STOP_MOVING, 0, Command.STOP);
        sendCommand(cmd.toString());
        lastCommand = cmd;
    }

    private void resume() {
        if (lastCommand != null) {
            sendCommand(lastCommand.toString());
        }
    }

    public void sendCommand(String cmd) {
        carServer.send(cmd);
    }

}
