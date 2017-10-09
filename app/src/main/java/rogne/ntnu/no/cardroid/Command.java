package rogne.ntnu.no.cardroid;

/**
 * Created by Kristoffer on 2017-09-29.
 */

public class Command {
    public final static String FORWARD = "f";
    public final static String STOP_MOVING = "h";
    public final static String BACKWARD = "b";
    public final static String TURN_LEFT = "l";
    public final static String TURN_RIGHT = "r";
    public final static String TURN_DOWN_FOR_WHAT = "lul";
    public final static int START = 1;
    public final static int STOP = 0;
    private String command;
    private int value;
    private int startStop;

    public Command(String command, int value, int startStop) {
        this.command = command;
        this.value = value;
        this.startStop = startStop;
    }

    public Command(String string) {
        String str[] = string.split(":");
        this.command = str[0].substring(2);
        this.value = Integer.parseInt(str[1].substring(2));
        this.startStop = Integer.parseInt(str[2].substring(2));
    }

    @Override
    public String toString() {
        return "c=" + command + ":v=" + value + ":s=" + startStop;
    }
}