package rogne.ntnu.no.cardroid.useless;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import rogne.ntnu.no.cardroid.R;

public class CameraRecordActivity extends AppCompatActivity {
    private String cameraId;
    private Size videoDimensions;
    private ParcelFileDescriptor pfd;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession session;
    private boolean recording;
    private MediaRecorder recorder;
    private TextureView textureView;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private Surface surface;

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    protected CameraDevice cameraDevice;
    protected CaptureRequest.Builder captureRequestBuilder;

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_record);
        textureView = (TextureView) findViewById(R.id.recorder_preview);
        textureView.setSurfaceTextureListener(textureListener);
        Button btn = (Button) findViewById(R.id.recorder_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpCamera();
            }
        });
    }
    private void setUpCamera(){
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] recordSizes = null;
            if(cameraCharacteristics != null){
                recordSizes = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(MediaCodec.class);
            }
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            final File outputFile = new File(dir.getAbsolutePath(), "testHigh.mp3");
            //ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(getConnection());
            videoDimensions = recordSizes[recordSizes.length-1];
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            recorder.setVideoEncodingBitRate(10000000);
            recorder.setVideoFrameRate(30);
            recorder.setMaxFileSize(0);
            recorder.setPreviewDisplay(new Surface(textureView.getSurfaceTexture()));
            recorder.setVideoSize(videoDimensions.getWidth(), videoDimensions.getHeight());
            recorder.setOutputFile(outputFile.getAbsolutePath());
           // recorder.setOutputFile(pfd.getFileDescriptor());
            try{
                recorder.prepare();
                final CaptureRequest.Builder captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                captureRequest.addTarget(surface);
                captureRequest.addTarget(recorder.getSurface());
                mCaptureRequest = captureRequest.build();
                cameraDevice.createCaptureSession(Arrays.asList(surface,recorder.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        session = cameraCaptureSession;
                        startRecording(session);
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        session = cameraCaptureSession;
                    }
                }, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void startRecording(CameraCaptureSession session){
        try{
            if(!recording){
                ((Button) findViewById(R.id.recorder_button)).setText("Stop");
                session.setRepeatingRequest(mCaptureRequest, new CameraCaptureSession.CaptureCallback(){},null);
                recorder.start();
                ((Button) findViewById(R.id.recorder_button)).setText("Stop");
                recording = true;
            } else {
                ((Button) findViewById(R.id.recorder_button)).setText("Start");
                recording = false;
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder= null;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(){
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[0];  //Rear cam
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if(map != null){
                videoDimensions = map.getOutputSizes(SurfaceTexture.class)[0];
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraRecordActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                    return;
                }
                manager.openCamera(cameraId, stateCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        if(texture != null){
            texture.setDefaultBufferSize(videoDimensions.getWidth(),videoDimensions.getHeight());
            surface = new Surface(texture);
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureRequestBuilder.addTarget(surface);
                cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        if (null == cameraDevice) {
                            return;
                        }
                        // When the session is ready, we start displaying the preview.
                        session = cameraCaptureSession;
                        updatePreview();
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        Toast.makeText(CameraRecordActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();

                    }
                },null);
        }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if(cameraDevice == null){
           //TODO: something
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try{
            session.setRepeatingRequest(captureRequestBuilder.build(),null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    protected void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        System.out.println("getting socket");
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
