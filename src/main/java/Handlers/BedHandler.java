package Handlers;

import apocalypse.apocalypse.Apocalypse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BedHandler implements Listener {
    public BedHandler(Apocalypse plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event){
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.RED + "NO SLEEP");
        event.setCancelled(true);
    }
}
