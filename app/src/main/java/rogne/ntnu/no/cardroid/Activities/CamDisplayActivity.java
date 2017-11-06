package rogne.ntnu.no.cardroid.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
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

import rogne.ntnu.no.cardroid.R;

public class CamDisplayActivity extends AppCompatActivity {
    Socket socket;
    ParcelFileDescriptor pfd;
    MediaPlayer mp;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            displayStart(surfaceTexture);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mp = new MediaPlayer();
        setContentView(R.layout.activity_cam_display);
        intialiseView();
        pfd = ParcelFileDescriptor.fromSocket(socket);
        ((TextureView) findViewById(R.id.surface)).setSurfaceTextureListener(mSurfaceTextureListener);
    }

    private void intialiseView() {

        class NetworkCam extends AsyncTask<Integer, Void, Socket> {
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
        while (socket == null) {
            try {
                socket = nc.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private String getVideoFilePath() {
        final File dir = getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + "test" + ".jpg";
    }
    private void displayStart(SurfaceTexture surfaceTexture) {
        Log.d("Bleh", "bleh");
        ImageView iv = (ImageView) findViewById(R.id.display_image);
        InputStream input = new FileInputStream(pfd.getFileDescriptor());
        File file = new File(getVideoFilePath());
        System.out.println(file.getAbsolutePath());
        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[input.available()];
            int len = input.read(buffer);
            while(len == 0){
                len = input.read(buffer);
            }
            out.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mp.setDataSource(file.getAbsolutePath());
            Surface s = new Surface(surfaceTexture);
            mp.setSurface(s);
            mp.prepare();
            mp.start();
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
        ImageStreamListener isl = new ImageStreamListener(new InetSocketAddress("192.168.0.125", 6672));
        isl.setOnNewImageListener(c-> updateImage(c));
        new Thread(isl).start();
        */
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
