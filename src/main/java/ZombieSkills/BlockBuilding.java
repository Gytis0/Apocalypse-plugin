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

import static ZombieSkills.CustomPathSearch.*;

public class BlockBuilding implements Skill {
    Zombie zombie;
    LivingEntity target;
    Pathfinder pathfinder;
    World world;
    List<ItemStack> inventory;
    int activeInventorySlot;

    double buildRange = 4;

    Block lastPlacedBlock;

    List<Block> path;

    public ReachTarget setPathToLedge = this::setPathToLedge;

    public ReachTarget setPathToFirstObstacle = this::setPathToFirstObstacle;

    public BlockBuilding(Zombie zombie, Pathfinder pathfinder, World world, List<ItemStack> inventory, int activeInventorySlot) {
        this.zombie = zombie;
        this.pathfinder = pathfinder;
        this.world = world;
        this.inventory = inventory;
        this.activeInventorySlot = activeInventorySlot;

        path = new ArrayList<>();
        inventory.add(new ItemStack(Material.MOSS_BLOCK));
    }

    public void setPathToLedge(LivingEntity target, int range, int obstaclesToIgnore) {
        Bukkit.getLogger().info("Setting path to LEDGE...");
        this.target = target;
        Block ledge = getNearestLedge(target, range, obstaclesToIgnore);
        path = getPathStraightLine(getEntityFloorBlock(zombie).getLocation(), ledge.getLocation());
        if (path != null) {
            Bukkit.getLogger().info("PATH SET TO:");
            for (Block block : path) {
                Bukkit.getLogger().info(block.getLocation().toString());
            }
        } else {
            Bukkit.getLogger().info("Could not set a path");
            path = new ArrayList<>();
        }
    }

    public void setPathToFirstObstacle(LivingEntity target, int range, int obstaclesToIgnore) {
        Bukkit.getLogger().info("Setting path to FIRST OBSTACLE...");
        this.target = target;

        path = getPathTopBlocks(zombie, target);

        Block obstacle = getFirstObstacleOf(path);

        path = getPathStraightLine(getEntityFloorBlock(zombie).getLocation(), obstacle.getLocation());
        if (path != null) {
            Bukkit.getLogger().info("PATH SET TO:");
            for (Block block : path) {
                Bukkit.getLogger().info(block.getLocation().toString());
            }
        } else {
            Bukkit.getLogger().info("Could not set a path");
            path = new ArrayList<>();
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
                pathfinder.moveTo(lastPlacedBlock.getLocation());
            } else {
                Bukkit.getLogger().info("Moving to first location");
                Location startLocation = CustomPathSearch.findLocationForPathBuilding(target.getLocation(), zombie.getLocation()).getLocation();
                pathfinder.moveTo(startLocation);
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
