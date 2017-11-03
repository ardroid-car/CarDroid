package rogne.ntnu.no.cardroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import rogne.ntnu.no.cardroid.Client.SimpleClientActivity;
import rogne.ntnu.no.cardroid.Server.Server;

public class MainActivity extends AppCompatActivity {
    Server  carServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Intent client = new Intent(this, SimpleClientActivity.class);
        Intent server = new Intent(this, CarServerActivity.class);
       Button phoneButton =(Button) findViewById(R.id.phoneButton);
       Button carButton= (Button) findViewById(R.id.carButton);



        carButton.setOnClickListener((View.OnClickListener) v ->     startActivity(server));


        phoneButton.setOnClickListener((View.OnClickListener) v ->      startActivity(client));


    }

}
