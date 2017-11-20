package rogne.ntnu.no.cardroid.Runnables;

import java.io.PrintStream;

/**
 * Created by Mikael on 03.11.2017.
 * Common interface for the phone and car handler classes.
 */

public interface Handler {
    void handle(String line);

    void setOutputStream(PrintStream out);

    void setCallback(Server.OnSendListener callback);

    void onSend(String line);
}


