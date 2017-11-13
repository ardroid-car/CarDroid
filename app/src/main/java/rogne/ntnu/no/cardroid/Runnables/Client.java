package rogne.ntnu.no.cardroid.Runnables;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

import rogne.ntnu.no.cardroid.Data.Command;

/**
 * Created by Kristoffer on 2017-11-10.
 */

public class Client extends Thread {

    private Command lastCommand = new Command(Command.STOP_MOVING, 0, Command.STOP);
    private PrintStream out;
    private Socket socket;
    private boolean running = false;
    private Command command;

    public Client(Socket socket) throws IOException {
        OutputStream out = socket.getOutputStream();
        this.socket = socket;
        this.out = new PrintStream(out);
        this.running = true;
        start();
    }

    public void sendCommand(Command command) {
        this.command = command;
    }

    @Override
    public void run() {
        while (running) {
            if (command != null) {
                out.println(command.toString());
                command = null;
            }
        }
    }

    public void stopClient() {
        running = false;
    }

    public void handle(String text, int speed) {
         if (!isMoveDirection(text)) {
            sendCommand(getCommand(text, speed));
        } else if (isMoveDirection(text)) {
            lastCommand = getCommand(text, speed);
            sendCommand(lastCommand);
        }
    }

    private boolean isMoveDirection(String text) {
        return text.equals("forward") && text.equals("backward");
    }

    private Command getCommand(String text, int speed) {
        Command cmd = null;
        System.out.println(text.toLowerCase());
        String[] str = text.toLowerCase().split(":");
        String cases = str[0];
        String action = str[1];
        switch (action) {
            case "down":
                switch (cases) {
                    case "forward":
                        cmd = new Command(Command.FORWARD, speed, Command.START);
                        break;
                    case "back":
                        cmd = new Command(Command.BACKWARD, speed, Command.START);
                        break;
                    case "left":
                        cmd = new Command(Command.TURN_LEFT, speed, Command.START);
                        break;
                    case "right":
                        cmd = new Command(Command.TURN_RIGHT, speed, Command.START);
                        break;
                    case "claw":
                        cmd = new Command(Command.CLAW, Command.CLAW_OPEN, Command.START);
                        break;
                }
                break;
            case "up":
                switch (cases) {
                    case "claw":
                        cmd = new Command(Command.CLAW, Command.CLAW_CLOSE, Command.START);
                        break;
                    default:
                        cmd = new Command(Command.STOP_MOVING, 0, Command.STOP);
                }
                break;
        }
        return cmd;
    }
}
