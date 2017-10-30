package rogne.ntnu.no.cardroid.Client;


import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.PrintWriter;

import rogne.ntnu.no.cardroid.Command;
import rogne.ntnu.no.cardroid.R;

public class SimpleClientActivity extends Activity {


    private PrintWriter printwriter;
    private TextView textField;
    private Button buttonr;
    private Button buttonf;
    private Button buttonl;
    private Button buttonb;
    private Button button;
    private Boolean connected = true;
    private Boolean sent = false;
    private Client client;
    public String message = "TestMessage";
    private TextView textView;
    Thread t;
    private SeekBar speedBar = null;
    private int speed=0;

    // private PrintWriter stream;
    private String ip = "192.168.0.125";
    private int port = 6671;
    String sentence = "Test";


    @Override
    public void onCreate(Bundle savedInstanceState) {
     /*  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       speedBar =(SeekBar) findViewById(R.id.seekBar);


        buttonr = (Button) findViewById(R.id.buttonr);   //reference to the send button
        buttonf = (Button) findViewById(R.id.buttonf);
        buttonb = (Button) findViewById(R.id.buttonb);
        button = (Button) findViewById(R.id.button);
        buttonl = (Button) findViewById(R.id.buttonl);

        client = new Client(ip, port);


        textView = (TextView) findViewById(R.id.textView);
       textView.setMovementMethod(new ScrollingMovementMethod());



        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                speed=progressChanged;
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

                printView(client.getMessage());


            }
        });

        buttonf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Command forward;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    forward=new Command(Command.FORWARD,speed,Command.START);
                    message = forward.toString();
                    client.setMessage(message);
                    printView(client.getMessage());



                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    forward=new Command(Command.FORWARD,speed,Command.STOP);
                    message = forward.toString();
                    client.setMessage(message);
                    printView(client.getMessage());

                }

                //   printView(message);

                return false;

            }

        });
        buttonr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Command right;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   right=new Command(Command.TURN_RIGHT,speed,Command.START);
                    message = right.toString();
                    client.setMessage(message);
                    printView(client.getMessage());



                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    right=new Command(Command.TURN_RIGHT,speed,Command.STOP);
                    message = right.toString();
                    client.setMessage(message);
                    printView(client.getMessage());

                }

                //   printView(message);

                return false;

            }

        });
        buttonl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Command left;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    left=new Command(Command.TURN_LEFT,speed,Command.START);
                    message = left.toString();
                    client.setMessage(message);
                    printView(client.getMessage());



                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    left=new Command(Command.TURN_LEFT,speed,Command.STOP);
                    message = left.toString();
                    client.setMessage(message);
                    printView(client.getMessage());

                }

                //   printView(message);

                return false;

            }

        });
        buttonb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Command back;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   back=new Command(Command.BACKWARD,speed,Command.START);
                    message = back.toString();
                    client.setMessage(message);
                    printView(client.getMessage());



                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    back=new Command(Command.BACKWARD,speed,Command.STOP);
                    message = back.toString();
                    client.setMessage(message);
                    printView(client.getMessage());

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

        //  (new Thread(client)).start();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                client.run();

            }
        });
        t.start();


    }

    public void updateClient(final String me) {

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                client.send(me);

            }
        });
        th.run();
        th.destroy();


    }

}






