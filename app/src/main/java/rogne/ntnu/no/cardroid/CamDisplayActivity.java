package rogne.ntnu.no.cardroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class CamDisplayActivity extends AppCompatActivity {
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_display);
        intialiseView();
        ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
            System.out.println("New picutre");
            displayStart(pfd);
    }

    private void intialiseView() {

        class NetworkCam extends AsyncTask<Integer, Void, Socket>{
            @Override
            protected Socket doInBackground(Integer... integers) {
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("192.168.0.125", 6672));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return socket;
            }
        }
        NetworkCam nc = new NetworkCam();
        nc.execute(1);
        while(socket == null){
            try {
                socket = nc.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayStart(ParcelFileDescriptor pfd) {
        ImageView iv = (ImageView) findViewById(R.id.display_image);
        InputStream input = new FileInputStream(pfd.getFileDescriptor());
        try {
            File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
            if(!file.exists()){
                file.createNewFile();
            }
            System.out.println(file.getAbsolutePath());
            BufferedInputStream buf;
            try {
                buf = new BufferedInputStream(input);
                System.out.println(System.currentTimeMillis());
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                System.out.println(System.currentTimeMillis());
                iv.setImageBitmap(bMap);
                if (input != null) {
                    input.close();
                }
                if (buf != null) {
                    buf.close();
                }/*
                OutputStream output = new FileOutputStream(file);
                try {
                    byte[] buffer = new byte[2^22]; // or other buffer size
                    int read;

                    while ((read = input.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                } finally {
                    output.close();
                }*/
            } finally {
                    input.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
