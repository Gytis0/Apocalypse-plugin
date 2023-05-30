package Commands.Debugging;

import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlaceBlockAt45 implements TabExecutor {

    World world;

    public PlaceBlockAt45(World world) {
        this.world = world;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Vector vector = new Vector(0, -0.7, -1);
        LivingEntity player = (LivingEntity) sender;
        RayTraceResult ray = world.rayTrace(player.getLocation(), vector, 16, FluidCollisionMode.ALWAYS, true, 16, null);

        if (ray != null) {
            ray.getHitBlock().setType(Material.GLASS);
        } else {
            sender.sendMessage(ChatColor.RED + "Could not place a block.");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
