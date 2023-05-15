package ZombieSkills;

import Model.ReachTarget;
import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockBuilding extends CustomPathSearch implements Skill {
    Zombie zombie;
    Pathfinder pathfinder;
    World world;
    List<ItemStack> inventory;
    int activeInventorySlot;

    double buildRange = 4;

    Block lastPlacedBlock;

    List<Block> path;

    public ReachTarget setPathToLedge = (target -> setPathToLedge(target));

    public ReachTarget setPathToFirstObstacle = (target) -> {
        Bukkit.getLogger().info("Setting path to FIRST OBSTACLE...");

        path = getCustomPathTopBlocks(zombie, target);

        Block obstacle = getFirstObstacleOf(path);

        path = getStraightLinePath(getTopBlock(zombie).getLocation(), obstacle.getLocation());
        if (path != null) {
            Bukkit.getLogger().info("PATH SET TO:");
            for (Block block : path) {
                Bukkit.getLogger().info(block.getLocation().toString());
            }
        } else {
            Bukkit.getLogger().info("Could not set a path");
        }
    };

    public BlockBuilding(Zombie zombie, Pathfinder pathfinder, World world, List<ItemStack> inventory, int activeInventorySlot) {
        super(world);
        this.zombie = zombie;
        this.pathfinder = pathfinder;
        this.world = world;
        this.inventory = inventory;
        this.activeInventorySlot = activeInventorySlot;

        path = new ArrayList<>();
        inventory.add(new ItemStack(Material.MOSS_BLOCK));
    }

    public void setPathToLedge(LivingEntity target) {
        Bukkit.getLogger().info("Setting path to LEDGE...");
        Block ledge = getNearestLedge(target);
        path = getStraightLinePath(getTopBlock(zombie).getLocation(), ledge.getLocation());
        if (path != null) {
            Bukkit.getLogger().info("PATH SET TO:");
            for (Block block : path) {
                Bukkit.getLogger().info(block.getLocation().toString());
            }
        } else {
            Bukkit.getLogger().info("Could not set a path");
        }
    }

    public void setPathToFirstObstacle(LivingEntity target) {
        Bukkit.getLogger().info("Setting path to FIRST OBSTACLE...");

        path = getCustomPathTopBlocks(zombie, target);

        Block obstacle = getFirstObstacleOf(path);

        path = getStraightLinePath(getTopBlock(zombie).getLocation(), obstacle.getLocation());
        if (path != null) {
            Bukkit.getLogger().info("PATH SET TO:");
            for (Block block : path) {
                Bukkit.getLogger().info(block.getLocation().toString());
            }
        } else {
            Bukkit.getLogger().info("Could not set a path");
        }
    }

    protected boolean placeBlock(Location blockLocation, Material block) {
        if (zombie.getLocation().distance(blockLocation) > buildRange) return false;

        if (blockLocation.getBlock().getType() != Material.AIR) return true;

        blockLocation.getBlock().setType(block);
        Bukkit.getLogger().info("Block placed at : " + blockLocation);
        return true;
    }

    @Override
    public boolean trigger() {
        if (path.size() > 0) {
            return true;
        } else return false;

    }

    @Override
    public void action() {
        if (placeBlock(path.get(0).getLocation(), inventory.get(activeInventorySlot).getType())) {
            lastPlacedBlock = path.get(0);
            path.remove(0);
        } else {
            Bukkit.getLogger().info("Moving to block:" + path.get(0).getLocation());
            if (lastPlacedBlock != null) {
                //pathfinder.moveTo(path.get(0).getLocation());
                pathfinder.moveTo(lastPlacedBlock.getLocation());
            }
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }
}
