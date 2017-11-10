package rogne.ntnu.no.cardroid.Runnables;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import rogne.ntnu.no.cardroid.Data.Command;

/**
 * Created by Vinh on 06/11/2017.
 */

public class CommandBox {

    private ArrayList<Command> commands = new ArrayList<>();
    private int size = 1;
    public CommandBox(int size){
        this.size = size;
    }

    public CommandBox() {
    }

    public synchronized Command getCmd()
    {
        while(commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(CommandBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        notifyAll();
        return commands.remove(0);
    }
    public synchronized void putCmd(Command command)
    {
        while(commands.size()> size)
        {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(CommandBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.commands.add(command);
        notifyAll();
    }

}
