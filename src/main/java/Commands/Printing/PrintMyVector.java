package Commands.Printing;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import Utility.Utils;

import java.util.List;

public class PrintMyVector implements TabExecutor {
    World world;
    public PrintMyVector(World world){
        this.world = world;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        Player player = (Player) sender;
        Vector facing = player.getLocation().getDirection();

        sender.sendMessage("-------------------");
        sender.sendMessage("Pitch: " + player.getLocation().getPitch());
        sender.sendMessage("Yaw: " + player.getLocation().getYaw());
        sender.sendMessage(String.valueOf(facing.getX()));
        sender.sendMessage(String.valueOf(facing.getZ()));

        facing = Utils.fixateVector(facing);

        Location newLoc = player.getLocation().setDirection(facing);
        player.teleport(newLoc);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
