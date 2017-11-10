package rogne.ntnu.no.cardroid.Threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by krist on 2017-11-06.
 */

public class ImageRecieverThread extends Thread {
    private OnImageAvailable callback;

    public interface OnImageAvailable {
        void onImageAvailable(Bitmap map);
    }

    private Socket socket;

    public ImageRecieverThread(Socket socket) {
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
                System.out.println("ClientOLD Recieved Length" + length);
                byte[] bytes = new byte[length];
                while(in.available() < length){
                    System.out.println("Waiting for image");
                }
                in.read(bytes);
                Bitmap map = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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

    private int getImageLength(InputStream in) throws IOException {
        byte[] startByte = "JPEG".getBytes();
        byte[] readBytes = new byte[4];
        boolean image = false;
        byte[] readByte = new byte[1];
        int matchingBytes = 0;
        while (!image) {
            in.read(readByte);
            if (readByte[0] == startByte[matchingBytes]) {
                readBytes[matchingBytes] = readByte[0];
                matchingBytes++;
            } else {
                matchingBytes = 0;
            }
            image = Arrays.equals(readBytes, startByte);
        }
        in.read(readBytes);
        int length = java.nio.ByteBuffer.wrap(readBytes).getInt();
        return length;
    }

    private void onImageAvailable(Bitmap map) {
        if (map != null && callback != null) {
            this.callback.onImageAvailable(map);
        }
    }

    private byte[] decodeString(String string) {
        String[] str = string.split(",");
        byte[] bytes = new byte[str.length];

        for (int i = 0, len = bytes.length; i < len; i++) {
            bytes[i] = Byte.parseByte(str[i].trim());
        }
        return bytes;
    }

}
