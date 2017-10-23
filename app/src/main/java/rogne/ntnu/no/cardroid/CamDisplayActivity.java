package rogne.ntnu.no.cardroid;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                InputStream input = new FileInputStream(pfd.getFileDescriptor());
                try {
                    File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
                    if(!file.exists()){
                        file.createNewFile();

                    }
                    System.out.println(file.getAbsolutePath());
                    OutputStream output = new FileOutputStream(file);
                    try {
                        byte[] buffer = new byte[2^22]; // or other buffer size
                        int read;

                        while ((read = input.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                    } finally {
                        output.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        }
        new NetworkCam().execute(1);
    }
}
