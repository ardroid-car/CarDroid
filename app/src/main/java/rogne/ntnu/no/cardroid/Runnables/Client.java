package rogne.ntnu.no.cardroid.Runnables;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import rogne.ntnu.no.cardroid.Data.Command;

/**
 * Created by Kristoffer on 2017-11-10.
 */

public class Client {

    private Command lastCommand = new Command(Command.STOP_MOVING, 0, Command.STOP);
    private PrintWriter out;

    public Client(OutputStream out) {
        this.out = new PrintWriter(out);
    }

    public void sendCommand(Command command) {
        System.out.println(command.toString());
        out.println(command.toString());
    }

    public void handle(String text, int speed){
        System.out.println(text);
        System.out.println(speed);
        if(speed == -1 &! isMoveDirection(text)){
            sendCommand(lastCommand);
        } else if(speed == -1 && isMoveDirection(text)){
            sendCommand(getCommand("stop",0));
        } else if(speed != -1 &! isMoveDirection(text)){
            sendCommand(getCommand(text, speed));
        } else if(speed != -1 && isMoveDirection(text)){
            lastCommand = getCommand(text, speed);
            sendCommand(lastCommand);
        }
    }

    private boolean isMoveDirection(String text){
        return text.equals("forward") && text.equals("backward");
    }
    private Command getCommand(String text, int speed) {
        Command cmd = null;
        switch (text.toLowerCase()) {
            case "forward":
                cmd = new Command(Command.FORWARD, speed, Command.START);
                break;
            case "backward":
                cmd = new Command(Command.BACKWARD, speed, Command.START);
                break;
            case "left":
                cmd = new Command(Command.TURN_LEFT, speed, Command.START);
                break;
            case "right":
                cmd = new Command(Command.TURN_RIGHT, speed, Command.START);
                break;
            default:
                cmd = new Command(Command.STOP_MOVING, 0, Command.STOP);
        }
        return cmd;
    }
}
