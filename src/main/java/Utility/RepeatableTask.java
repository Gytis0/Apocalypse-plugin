package Utility;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class RepeatableTask implements Listener {
    private static Plugin plugin = null;
    private int id = -1;

    public RepeatableTask(Plugin instance){
        plugin = instance;
    }

    public RepeatableTask(Runnable runnable){
        this(runnable,0,0);
    }

    public RepeatableTask(Runnable runnable, float start, float delay){
        if (plugin.isEnabled()){
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, (long)(20 * start), (long)(20 * delay));
        }
        else{
            runnable.run();
        }
    }

    public int getId(){
        return id;
    }
}
