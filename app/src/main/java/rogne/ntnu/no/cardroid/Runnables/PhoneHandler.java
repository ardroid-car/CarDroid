package rogne.ntnu.no.cardroid.Runnables;

import java.io.PrintStream;

/**
 * Created by Mikael on 03.11.2017.
 */

public class PhoneHandler implements Handler {
    private PrintStream out;
    private Server.OnSendListener callback;

    @Override
    public void setOutputStream(PrintStream out) {
        this.out = out;
    }

    @Override
    public void handle(String line) {
        out.println(line);
        onSend(line);
    }

    @Override
    public void setCallback(Server.OnSendListener callback) {
        this.callback = callback;
    }

    public void onSend(String lineSent) {
        if (callback != null) {
            callback.onSend(lineSent);
        }
    }
}