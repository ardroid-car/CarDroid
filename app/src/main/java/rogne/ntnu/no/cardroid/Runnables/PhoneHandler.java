package rogne.ntnu.no.cardroid.Runnables;

import java.io.PrintStream;

import rogne.ntnu.no.cardroid.Data.Command;

/**
 * Created by Mikael on 03.11.2017.
 */

public class PhoneHandler implements Handler{
    private PrintStream out;
    private Server.OnSendListener callback;
    private CommandBox box;

    public PhoneHandler(CommandBox box)
    {
        this.box = box;
    }


    @Override
    public void setOutputStream(PrintStream out) {
        this.out = out;
    }

    @Override
    public void handle(String line) {
        box.putCmd(new Command(line));
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