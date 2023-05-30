package Commands.Printing;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrintMyVector implements TabExecutor {
    World world;

    public PrintMyVector(World world) {
        this.world = world;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Vector facing = player.getLocation().getDirection();

        sender.sendMessage("-------------------");
        sender.sendMessage("Pitch: " + player.getLocation().getPitch());
        sender.sendMessage("Yaw: " + player.getLocation().getYaw());
        sender.sendMessage("X: " + facing.getX());
        sender.sendMessage("Y: " + facing.getY());
        sender.sendMessage("Z: " + facing.getZ());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
