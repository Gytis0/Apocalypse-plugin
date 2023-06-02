package ZombieSkills;

import Model.Goals.ReachTarget;
import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

    public ReachTarget setPathToTargetLedge = this::setPathToTargetLedge;

    public BlockBuilding(Zombie zombie, World world, int level, List<ItemStack> inventory, int activeInventorySlot) {
        this.zombie = zombie;
        this.pathfinder = zombie.getPathfinder();
        this.world = world;
        this.inventory = inventory;
        this.activeInventorySlot = activeInventorySlot;

        path = new ArrayList<>();
        inventory.add(new ItemStack(Material.MOSS_BLOCK));
    }

    public boolean setPathToFirstObstacle(LivingEntity target, int range, int obstaclesToIgnore) {
        Bukkit.getLogger().info("Setting path to FIRST OBSTACLE...");
        this.target = target;

        path = findPathTopBlocks(zombie, target);

        Block obstacle = findFirstObstacleOf(path);

        path = findPathStraightLine(getEntityFloorBlock(zombie).getLocation(), obstacle.getLocation());
        if (path != null) {
            Bukkit.getLogger().info("Set path to first obstacle");
            return true;
        } else {
            Bukkit.getLogger().info("Could not set a path to first obstacle");
            path = new ArrayList<>();
            return false;
        }
    }

    public boolean setPathToTargetLedge(LivingEntity origin, LivingEntity target, int level, int index) {
        List<Block> playerLedges = CustomPathSearch.findNearestLedges(target, level * 10, index - 1);
        Block closestLedge = null;
        double closestDistance = 9999;
        double temp;
        for (Block b : playerLedges) {
            temp = origin.getLocation().distance(b.getLocation());
            if (temp < closestDistance) {
                closestDistance = temp;
                closestLedge = b;
            }
        }

        // Path from current position
        path = findPathStraightLine(CustomPathSearch.getEntityFloorBlock(origin).getLocation(), closestLedge.getLocation());
        if (path == null) {
            path = new ArrayList<>();
        } else {
            Bukkit.getLogger().info("Set path from straight line");
            path = CustomPathSearch.widenStraightPath(path, 1);
            path = CustomPathSearch.cleanUpPath(path);
            return true;
        }

        // Path with 45 degrees. To above
        Block startBlock = null, endBlock = null;
        if (target.getLocation().getY() >= origin.getLocation().getY()) {
            for (Block b : playerLedges) {
                startBlock = CustomPathSearch.findLocationForPathBuildingUp(b.getLocation(), zombie);
                if (startBlock != null) {
                    Bukkit.getLogger().info("Set path from BELOW to ABOVE angles");
                    endBlock = b;
                    break;
                }
            }
        } else {
            startBlock = CustomPathSearch.findNearestLedge(zombie, level * 5, 0);

            closestDistance = 9999;
            for (Block b : playerLedges) {
                if (b.getLocation().distance(startBlock.getLocation()) < closestDistance) {
                    closestDistance = b.getLocation().distance(startBlock.getLocation());
                    endBlock = b;
                }
            }
        }

        if (startBlock != null) {
            path = findPathStraightLine(startBlock.getLocation(), endBlock.getLocation());
            if (path == null) {
                path = new ArrayList<>();
            } else {
                path = CustomPathSearch.widenStraightPath(path, 1);
                path = CustomPathSearch.cleanUpPath(path);
                return true;
            }
        }

        Bukkit.getLogger().warning("Not a single algorithm found a way to the player");
        return false;
    }

    protected boolean placeBlock(Location blockLocation, Material block) {
        if (zombie.getLocation().distance(blockLocation) > buildRange) return false;

        if (!blockLocation.getBlock().isReplaceable()) return true;

        blockLocation.getBlock().setType(block);
        //Bukkit.getLogger().info("Block placed at : " + blockLocation);
        return true;
    }

    @Override
    public boolean trigger() {
        if (path.size() > 0) {
            return true;
        } else {
            lastPlacedBlock = null;
            return false;
        }
    }

    @Override
    public void action() {
        if (placeBlock(path.get(0).getLocation(), inventory.get(activeInventorySlot).getType())) {
            lastPlacedBlock = path.get(0);
            path.remove(0);
        }

        if (lastPlacedBlock != null) {
            //Bukkit.getLogger().info("Moving to last placed:" + lastPlacedBlock.getRelative(BlockFace.UP).getLocation());
            pathfinder.moveTo(lastPlacedBlock.getRelative(BlockFace.UP).getLocation());
        } else {
            //Bukkit.getLogger().info("Moving to first location");
            pathfinder.moveTo(path.get(0).getLocation());
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }
}
