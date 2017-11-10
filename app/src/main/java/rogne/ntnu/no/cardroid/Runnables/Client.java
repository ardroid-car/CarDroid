package rogne.ntnu.no.cardroid.Runnables;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import rogne.ntnu.no.cardroid.Data.Command;

/**
 * Created by Kristoffer on 2017-11-10.
 */

public class Client {

    private PrintWriter out;

    public Client(OutputStream out) {
        this.out = new PrintWriter(out);
    }
    public void sendCommand(Command command){
        out.println(command);
    }
}
