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
import java.util.Set;

import static ZombieSkills.CustomPathSearch.getNearestLedge;

public class PlaceBlockAtLedge implements TabExecutor {

    public PlaceBlockAtLedge() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Set<Block> ledges = getNearestLedge((Player) sender, 10);

        if (ledges != null) {
            for (Block block : ledges) {
                block.setType(Material.GLASS);
            }
            sender.sendMessage(ChatColor.GREEN + "" + ledges.size() + " ledges found.");
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
