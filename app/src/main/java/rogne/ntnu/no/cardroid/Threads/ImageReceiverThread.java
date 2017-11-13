package rogne.ntnu.no.cardroid.Threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import rogne.ntnu.no.cardroid.Utils.ByteUtils;

/**
 * Created by krist on 2017-11-06.
 */

public class ImageReceiverThread extends Thread {
    private OnImageAvailable callback;

    public interface OnImageAvailable {
        void onImageAvailable(Bitmap map);
    }

    private Socket socket;

    public ImageReceiverThread(Socket socket) {
        this.socket = socket;
    }

    public void setOnImageAvailableListener(OnImageAvailable listener) {
        this.callback = listener;
    }

    @Override
    public void run() {
        InputStream in = null;
        try {
            in = socket.getInputStream();
            while (true) {
                int length = getImageLength(in);
                byte[] bytes = new byte[length];
                while(in.available() < length){
                }
                in.read(bytes);
                Bitmap map = getBitmap(bytes, length);
                onImageAvailable(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getBitmap(byte[] imageBytes, int length) {
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes,0,length);
        float cx = (float) imageBitmap.getWidth() / 2;
        float cy = (float) imageBitmap.getHeight() / 2;
        Matrix matrix = new Matrix();
        matrix.setRotate(90, cx, cy);
        matrix.postScale(1, -1, cx, cy);
        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        return imageBitmap;
    }

    private int getImageLength(InputStream in) throws IOException {
        byte[] startByte = ByteUtils.getSeperatorBytes();
        byte[] byteArray = new byte[4];
        boolean image = false;
        byte[] readByte = new byte[1];
        int matchingBytes = 0;
        while (!image) {
            in.read(readByte);
            if (readByte[0] == startByte[matchingBytes]) {
                byteArray[matchingBytes] = readByte[0];
                matchingBytes++;
            } else {
                matchingBytes = 0;
            }
            image = Arrays.equals(byteArray, startByte);
        }
        in.read(byteArray);
        int length = ByteUtils.byteToInt(byteArray);
        return length;
    }

    private void onImageAvailable(Bitmap map) {
        if (map != null && callback != null) {
            this.callback.onImageAvailable(map);
        }
    }
}
