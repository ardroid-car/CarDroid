package rogne.ntnu.no.cardroid;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CamDisplayActivity extends AppCompatActivity {
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_display);
        displayStart();
    }

    private void displayStart() {
        class NetworkCam extends AsyncTask<Integer, Void, Void>{
            @Override
            protected Void doInBackground(Integer... integers) {
                socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("192.168.0.125", 6672));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
                InputStream is = new FileInputStream(pfd.getFileDescriptor());

                return null;
            }
        }
        new NetworkCam().execute(1);
    }
}
