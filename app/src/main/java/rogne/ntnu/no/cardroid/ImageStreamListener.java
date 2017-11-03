package rogne.ntnu.no.cardroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Kristoffer on 2017-11-03.
 */

public class ImageStreamListener implements Runnable {
    public interface OnNewImage {
        void onNewImage(Bitmap mp);
    }

    private boolean running = false;
    private OnNewImage callback;
    InetSocketAddress adr;

    public ImageStreamListener(InetSocketAddress adr) {

        this.adr = adr;
    }

    public void setOnNewImageListener(OnNewImage listener) {
        callback = listener;
        running = true;
    }

    @Override
    public void run() {
        try {
            System.out.println("run");
            BufferedInputStream buf;
            Socket socket = new Socket();
            socket.connect(adr);
            ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
            FileDescriptor fd = pfd.getFileDescriptor();
            while (running) {
                InputStream input = new FileInputStream(fd);
                buf = new BufferedInputStream(input);
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (callback != null && bMap != null) {
                    System.out.println("callBack");
                    callback.onNewImage(bMap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
