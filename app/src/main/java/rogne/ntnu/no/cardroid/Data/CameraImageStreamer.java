package rogne.ntnu.no.cardroid.Data;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by krist on 2017-11-05.
 */

public class CameraImageStreamer {
    private CameraCaptureSession session;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private final String TAG = "STREAMER";
    private final Activity activity;
    private final int BACK_CAM = 0;
    private final int FRONT_CAM = 1;
    private final int CAM_TO_USE = BACK_CAM;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private CameraDevice cameraDevice;
    private Size imageDimension = new Size(640, 480);
    protected CaptureRequest.Builder captureRequestBuilder;
    private String cameraId;
    private OnImageCaptured callback;

    public interface OnImageCaptured {
        void onImageCaptured(Image image);
    }

    public CameraImageStreamer(Activity activity) {
        this.activity = activity;
        openCamera();
    }

    public void setCallback(OnImageCaptured onImageCaptured) {
        callback = onImageCaptured;
    }

    public void startImageStream() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.YUV_420_888);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[jpegSizes.length - 2].getWidth();
                height = jpegSizes[jpegSizes.length - 2].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(0));
            final File file = new File(Environment.getExternalStorageDirectory() + "/pic.png");
            ImageReader.OnImageAvailableListener readerListener = reader1 -> onImageCaptured(reader1.acquireLatestImage());
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession currentSession) {
                    try {
                        session = currentSession;
                        session.capture(captureBuilder.build(), null, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    public void stopImageStream(){
        try {
            if(session != null) {
                session.stopRepeating();
            }
            if(cameraDevice != null) {
            cameraDevice.close();
            }
            cameraDevice = null;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
    private void openCamera() {
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[CAM_TO_USE];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            //createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
            openCamera();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void onImageCaptured(Image image) {

        if (image != null) {
            if (callback != null) {
                try {
                    callback.onImageCaptured(image);
                } finally {
                    image.close();
                }
            }
        }
    }
}
