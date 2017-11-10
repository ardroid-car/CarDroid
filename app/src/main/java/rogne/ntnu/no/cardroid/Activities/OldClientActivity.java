package rogne.ntnu.no.cardroid.Activities;


import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.PrintWriter;

import rogne.ntnu.no.cardroid.Runnables.ClientOLD;
import rogne.ntnu.no.cardroid.Data.Command;
import rogne.ntnu.no.cardroid.R;

public class OldClientActivity extends Activity {


    private PrintWriter printwriter;
    private TextView textField;
    private Button rightButton;
    private Button forwardButton;
    private Button leftButton;
    private Button backwardButton;
    private Button button;
    private Boolean connected = true;
    private Boolean sent = false;
    private ClientOLD clientOLD;
    public String message = "TestMessage";
    private TextView textView;
    Thread t;
    private SeekBar speedBar = null;
    private int speed=0;

    // private PrintWriter stream;
    private String ip = "192.168.0.125";
    private int port = 6671;


    @Override
    public void onCreate(Bundle savedInstanceState) {
     /*  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
       speedBar =(SeekBar) findViewById(R.id.activity_client_speed_Bar);


        rightButton = (Button) findViewById(R.id.activity_client_right_button);   //reference to the send button
        forwardButton = (Button) findViewById(R.id.activity_client_forward_button);
        backwardButton = (Button) findViewById(R.id.activity_client_backwards_button);
        button = (Button) findViewById(R.id.button);
        leftButton = (Button) findViewById(R.id.activity_client_left_button);

        clientOLD = new ClientOLD(ip, port);


        textView = (TextView) findViewById(R.id.textView);
       textView.setMovementMethod(new ScrollingMovementMethod());



        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                speed = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        //Button press event listener
        button.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {

                printView("Trying to connect");
                createConnection();

                connected = false;

                printView(clientOLD.getMessage());


            }
        });

        forwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Command forward;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    forward=new Command(Command.FORWARD,speed,Command.START);
                    message =forward.toString();
                    clientOLD.send(message);
                    printView(clientOLD.getMessage());







                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    forward=new Command(Command.FORWARD,speed,Command.STOP);
                    message = forward.toString();
                    clientOLD.send(message);
                    printView(clientOLD.getMessage());


                    printView(clientOLD.getMessage());

                }

                //   printView(message);

                return false;

            }

        });
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Command right;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   right=new Command(Command.TURN_RIGHT,speed,Command.START);
                    message = right.toString();
                    clientOLD.send(message);
                    printView(clientOLD.getMessage());



                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    right=new Command(Command.TURN_RIGHT,speed,Command.STOP);
                    message = right.toString();
                    clientOLD.send(message);
                    printView(clientOLD.getMessage());

                }

                //   printView(message);

                return false;

            }

        });
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Command left;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    left=new Command(Command.TURN_LEFT,speed,Command.START);
                    message =left.toString();
                    clientOLD.send(message);
                    printView(clientOLD.getMessage());



                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    left=new Command(Command.TURN_LEFT,speed,Command.STOP);
                    message =left.toString();
                    clientOLD.send(message);
                    printView(clientOLD.getMessage());

                }

                //   printView(message);

                return false;

            }

        });
        backwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Command back;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   back=new Command(Command.BACKWARD,speed,Command.START);
                    message =back.toString();
                    clientOLD.send(message);
                    printView(clientOLD.getMessage());;



                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    back=new Command(Command.BACKWARD,speed,Command.STOP);
                    message =back.toString();
                    clientOLD.send(message);
                    printView(clientOLD.getMessage());
                }

                //   printView(message);

                return false;

            }

        });
    }


    public void printView(final String i) {
        textView.post(new Runnable() {

            @Override
            public void run() {
                textView.append("\n"+ i);

            }
        });
    }


    public void createConnection() {

        //  (new Thread(clientOLD)).start();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                clientOLD.run();

            }
        });
        t.start();


    }

    public void updateClient(final String me) {

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                clientOLD.send(me);

            }
        });
        th.run();
        th.destroy();


    }

}






