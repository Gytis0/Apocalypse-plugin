package Commands.Spawning;

import logic.Stats;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import Utility.Utils;

import java.util.List;

public class DespawnZombies implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        Utils.despawnAllZombies();
        sender.sendMessage(ChatColor.GOLD + String.valueOf(Stats.getZombieCount()) + ChatColor.WHITE + " zombies were despawned.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
