package rogne.ntnu.no.cardroid.Runnables;

import java.io.PrintStream;

/**
 * Created by Mikael on 03.11.2017.
 */

public class CarHandler implements Handler {
    private PrintStream out;
    private Server.OnSendListener callback;
    private CommandBox box;
    private boolean running = false;

    public CarHandler(CommandBox box)
    {
        this.box = box;
        running = true;
        Thread fetcher = new Thread(() -> {
            while(running){
                handle(box.getCmd().toString());
            }
        });
        fetcher.start();
    }

    @Override
    public void setOutputStream(PrintStream out) {
        this.out = out;
    }

    public void stopFetcher(){
        running = false;
    }

    @Override
    public void handle(String line) {
        if(out != null) {
            out.println(line);
        }
        onSend(line);
    }

    @Override
    public void setCallback(Server.OnSendListener callback) {
        this.callback = callback;
    }

    public void onSend(String lineSent) {
        System.out.println(lineSent);
        if (callback != null) {
            callback.onSend(lineSent);
        }
    }
}