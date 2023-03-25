package Commands.Printing;

import logic.Stats;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrintActiveZombies implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        sender.sendMessage(ChatColor.GOLD + "There are " + ChatColor.RED + Stats.getZombieCount() + ChatColor.GOLD + " active zombies.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
