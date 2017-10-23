package rogne.ntnu.no.cardroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.*;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class CamOldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_old);
        camStart();
    }

    private void camStart() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 201);
        }
        final MediaRecorder mMediaRecorder = new MediaRecorder();
        Camera mCamera = getCameraInstance();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        Socket socket = getConnection();
        ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
        mMediaRecorder.setOutputFile(pfd.getFileDescriptor());
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
// this is the unofficially supported MPEG2TS format, suitable for streaming (Android 3.0+)
        mMediaRecorder.setOutputFormat(8);
         mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        SurfaceView sv = (SurfaceView) findViewById(R.id.thing);
        SurfaceHolder sh = sv.getHolder();
        mMediaRecorder.setMaxDuration(-1);
        mMediaRecorder.setVideoFrameRate(15);
        Surface s = sh.getSurface();
        mMediaRecorder.setPreviewDisplay(s);
        mMediaRecorder.start();
    }


    protected Camera getCameraInstance() {
        Camera c = null;
        System.out.println(Camera.CameraInfo.CAMERA_FACING_BACK);
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    private Socket getConnection() {
        class CameraSocket extends AsyncTask<Integer, Integer, Socket> {
            @Override
            protected Socket doInBackground(Integer... ints) {
                Socket socket = null;
                try

                {
                    ServerSocket serverSocket = new ServerSocket(ints[0]);
                    socket = serverSocket.accept();
                } catch (
                        IOException e)

                {
                    e.printStackTrace();
                }
                return socket;
            }
        }
        CameraSocket cs = new CameraSocket();
        cs.execute(6672);
        Socket s = null;
        try {
            while (s == null) {
                s = cs.get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return s;
    }
}
