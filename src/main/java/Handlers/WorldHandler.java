package Handlers;

import apocalypse.apocalypse.Apocalypse;
import logic.Difficulty;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;

public class WorldHandler implements Listener {
    Difficulty difficulty;

    public WorldHandler(Apocalypse plugin, Difficulty difficulty) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.difficulty = difficulty;
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event){
        difficulty.scaleDifficulty();
    }
}
