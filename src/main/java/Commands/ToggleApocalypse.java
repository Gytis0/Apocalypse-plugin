package Commands;

import Utility.Utils;
import logic.ZombieSpawner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToggleApocalypse implements TabExecutor {
    ZombieSpawner plugin;

    public ToggleApocalypse(ZombieSpawner plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        if (args[0].equalsIgnoreCase("true")) {
            plugin.toggleApocalypse(true);
            Bukkit.getLogger().info(sender.getName() + " has enabled hordeSpawning.");
            sender.sendMessage("" + ChatColor.BOLD + ChatColor.AQUA + "Apocalypse was enabled.");
        } else if (args[0].equalsIgnoreCase("false")) {
            plugin.toggleApocalypse(false);
            Utils.despawnAllZombies();

            Bukkit.getLogger().info(sender.getName() + " has disabled hordeSpawning.");
            sender.sendMessage("" + ChatColor.BOLD + ChatColor.AQUA + "Apocalypse was disabled.");
        } else {
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("true");
            arguments.add("false");
            return arguments;
        } else if (args.length > 1) {
            return new ArrayList<>();
        }
        return null;
    }
}
