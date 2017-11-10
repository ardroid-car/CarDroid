package rogne.ntnu.no.cardroid.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import rogne.ntnu.no.cardroid.R;
import rogne.ntnu.no.cardroid.Runnables.Server;

public class MainActivity extends AppCompatActivity {
    Server  carServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent client = new Intent(this, ClientActivity.class);
        Intent server = new Intent(this, ServerActivity.class);
       Button phoneButton =(Button) findViewById(R.id.phoneButton);
       Button carButton= (Button) findViewById(R.id.carButton);



        carButton.setOnClickListener((View.OnClickListener) v ->     startActivity(server));


        phoneButton.setOnClickListener((View.OnClickListener) v ->      startActivity(client));


    }

}
