package rogne.ntnu.no.cardroid;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.*;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CamActivity extends AppCompatActivity {
    ServerSocket welcome = null;
    Socket socket = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);
        camStart();
    }
    private void camStart(){
        class CameraSocket extends AsyncTask<Integer, Void, Void> {
            @Override
            protected Void doInBackground(Integer... ints) {
                try

                {
                    welcome = new ServerSocket(ints[0]);
                    Camera mCamera = getCameraInstance();
                    MediaRecorder mMediaRecorder = new MediaRecorder();
                    mCamera.unlock();
                    mMediaRecorder.setCamera(mCamera);
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
// this is the unofficially supported MPEG2TS format, suitable for streaming (Android 3.0+)
                    mMediaRecorder.setOutputFormat(8);
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                    socket = welcome.accept();
                    ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
                    mMediaRecorder.setOutputFile(pfd.getFileDescriptor());
                    try {
                        mMediaRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMediaRecorder.start();
                } catch(
                        IOException e)

                {
                    e.printStackTrace();
                }
                return null;
            }
        }
        new CameraSocket().execute(6672);
    }
    protected Camera getCameraInstance(){
        Camera c = null;
        System.out.println(Camera.CameraInfo.CAMERA_FACING_BACK);
         c = Camera.open(1);
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }
}

