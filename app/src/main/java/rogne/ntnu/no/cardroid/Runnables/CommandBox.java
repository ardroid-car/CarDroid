package rogne.ntnu.no.cardroid.Runnables;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import rogne.ntnu.no.cardroid.Data.Command;

/**
 * Created by Vinh on 06/11/2017.
 */

public class CommandBox {

    ArrayList<Command> commands = new ArrayList<>();
    private boolean available = false;

    public CommandBox()
    {
    }

    public synchronized Command getCmd()
    {
        while(!available) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(CommandBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        available = !commands.isEmpty();
        notifyAll();
        return commands.get(0);
    }

    public synchronized void putCmd(Command command)
    {
        while(available)
        {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(CommandBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        available = true;
        notifyAll();
        this.commands.add(command);
    }
}
