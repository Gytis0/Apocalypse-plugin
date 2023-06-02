package Commands.Debugging;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static ZombieSkills.CustomPathSearch.findNearestLedge;

public class PlaceBlockAtLedge implements TabExecutor {

    public PlaceBlockAtLedge() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int range = 10, obstacles = 0;
        if (args.length > 0) {
            range = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            obstacles = Integer.parseInt(args[1]);
        }

        Block ledge = findNearestLedge((Player) sender, range, obstacles);

        if (ledge != null) {
            ledge.setType(Material.GLASS);
        } else {
            sender.sendMessage(ChatColor.RED + "No ledges could be found.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
