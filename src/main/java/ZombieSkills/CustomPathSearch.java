package ZombieSkills;

import Model.Point;
import Utility.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CustomPathSearch {
    static World world;
    static List<BlockFace> directions;

    static int ledgeSearchRange = 10;

    public CustomPathSearch(World world) {
        this.world = world;
        directions = new ArrayList<>();
        directions.add(BlockFace.NORTH);
        directions.add(BlockFace.EAST);
        directions.add(BlockFace.SOUTH);
        directions.add(BlockFace.WEST);
    }

    // FIND various blocks
    public static Block findTopBlockFromY(int x, int y, int z) {
        Block block = world.getBlockAt(x, y, z);

        if (block.getType() == Material.AIR) {
            if (world.getHighestBlockYAt(x, z) > y) {
                while (block.getType() == Material.AIR) {
                    block = block.getRelative(BlockFace.UP);
                }
            } else {
                while (block.getType() == Material.AIR) {
                    block = block.getRelative(BlockFace.DOWN);
                }
            }
        }

        while (block.getRelative(BlockFace.UP).getType() != Material.AIR) {
            block = block.getRelative(BlockFace.UP);
        }

        return block;
    }

    public static Block getTopBlock(LivingEntity entity) {
        Block block = entity.getLocation().getBlock().getRelative(0, -1, 0);
        int modX = 0, modZ = 0;
        if (block.getType() == Material.AIR) {
            double x = entity.getLocation().getX();
            double z = entity.getLocation().getZ();
            double xd = Math.abs(x - Math.round(x)), zd = Math.abs(z - Math.round(z));

            // Check for closest blocks, see if they're AIR. If yes, search around, if no, return
            if (xd < zd) {
                modX = Utils.getRoundedMod(x);
                if (block.getRelative(modX, 0, modZ).getType() == Material.AIR) {
                    modZ = Utils.getRoundedMod(z);
                    modX = 0;
                    if (block.getRelative(modX, 0, modZ).getType() == Material.AIR) {
                        modX = Utils.getRoundedMod(x);
                    }
                }
            } else {
                modZ = Utils.getRoundedMod(z);
                if (block.getRelative(modX, 0, modZ).getType() == Material.AIR) {
                    modX = Utils.getRoundedMod(x);
                    modZ = 0;
                    if (block.getRelative(modX, 0, modZ).getType() == Material.AIR) {
                        modZ = Utils.getRoundedMod(z);
                    }
                }
            }
        }
        return block.getRelative(modX, 0, modZ);
    }

    public static Block getNearestLedge(LivingEntity entity) {
        List<Block> checkedBlocks = new ArrayList<>();
        Queue<Point> blocksToCheck = new LinkedList<>();

        Block topBlock = getTopBlock(entity);
        Block tempBlock;
        Point tempPoint;

        blocksToCheck.add(new Point(topBlock));
        checkedBlocks.add(topBlock);

        while (!blocksToCheck.isEmpty()) {
            tempPoint = blocksToCheck.poll();
            Bukkit.getLogger().info("Queue check for: " + tempPoint.getBlock().toString());

            if (tempPoint.getLength() > ledgeSearchRange) {
                Bukkit.getLogger().info("Out of range.");
                continue;
            }

            if (tempPoint.getBlock().getType() == Material.AIR && isBlockClear(tempPoint.getBlock())) {
                return tempPoint.getBlock();
            }

            Bukkit.getLogger().info("Block is not a ledge");

            for (BlockFace direction : directions) {
                tempBlock = tempPoint.getBlock().getRelative(direction);

                if (isBlockClear(tempBlock) && !checkedBlocks.contains(tempBlock.getRelative(direction))) {
                    blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1));
                    checkedBlocks.add(tempBlock);
                }
            }
        }

        // No ledge could be found
        Bukkit.getLogger().info("No ledge could be found.");
        return null;
    }

    @Nullable
    public static Block getFirstObstacleTo(LivingEntity origin, LivingEntity entity) {
        List<Block> customPath = getPathTopBlocks(origin, entity);
        return getFirstObstacleOf(customPath);
    }

    @Nullable
    public static Block getFirstObstacleOf(List<Block> path) {
        int y = path.get(0).getY();
        for (Block block : path) {
            if (block.getY() - y > 1) {
                return block;
            } else {
                y = block.getY();
            }
        }
        return null;
    }

    // GET a path
    public static List<Block> getPathTopBlocks(LivingEntity origin, LivingEntity target) {
        Location originLoc = getTopBlock(origin).getLocation();
        Location targetLoc = getTopBlock(target).getLocation();

        double xLength = Math.abs(originLoc.getX() - targetLoc.getX());
        double zLength = Math.abs(originLoc.getZ() - targetLoc.getZ());
        double length = xLength + zLength;

        double xd = xLength / length;
        double zd = zLength / length;
        int xDirection = 1, zDirection = 1;
        if (originLoc.getX() > targetLoc.getX()) {
            xDirection = -1;
        }
        if (originLoc.getZ() > targetLoc.getZ()) {
            zDirection = -1;
        }

        List<Block> blocks = new ArrayList<>();

        int cordX, cordZ;
        Block block;
        double x = 0, y = 0, z = 0;
        while (true) {
            cordX = (int) (originLoc.getX() + (x * xDirection));
            cordZ = (int) (originLoc.getZ() + (z * zDirection));
            block = findTopBlockFromY(cordX, getTopBlock(origin).getY(), cordZ);
            if (!blocks.contains(block)) {
                blocks.add(block);
            }
            if (!(x < xLength || z < zLength)) {
                break;
            }
            x += xd;
            z += zd;
        }

        return blocks;
    }

    public static List<Block> getPathStraightLine(Location originLoc, Location targetLoc) {
        // Won't calculate a path if it would be too steep to use
        if (isPathTooSteep(originLoc, targetLoc)) {
            Bukkit.getLogger().info("Path would be too steep");
            return null;
        }

        double xLength = Math.abs(originLoc.getX() - targetLoc.getX());
        double yLength = Math.abs(originLoc.getY() - targetLoc.getY());
        double zLength = Math.abs(originLoc.getZ() - targetLoc.getZ());

        double length = xLength + zLength;

        double xDelta = xLength / length;
        double yDelta = yLength / length;
        double zDelta = zLength / length;

        int xDirection = 1, yDirection = 1, zDirection = 1;
        if (originLoc.getX() > targetLoc.getX()) {
            xDirection = -1;
        }
        if (originLoc.getY() > targetLoc.getY()) {
            yDirection = -1;
        }
        if (originLoc.getZ() > targetLoc.getZ()) {
            zDirection = -1;
        }

        List<Block> blocks = new ArrayList<>();

        int globalX, globalY, globalZ;
        Block block;
        double localX = 0, localY = 0, localZ = 0;

        while (true) {
            globalX = (int) (originLoc.getX() + (localX * xDirection));
            globalY = (int) (originLoc.getY() + (localY * yDirection));
            globalZ = (int) (originLoc.getZ() + (localZ * zDirection));
            block = world.getBlockAt(globalX, globalY, globalZ);

            if (!blocks.contains(block)) {
                blocks.add(block);
            }

            if (localX >= xLength && localY >= yLength && localZ >= zLength) {
                break;
            }
            if (localX < xLength) localX += xDelta;
            if (localY < yLength) localY += yDelta;
            if (localZ < zLength) localZ += zDelta;
        }

        return blocks;
    }

    // booleans
    public static boolean isPathClear(List<Block> path) {
        Block botBlock, midBlock, topBlock;
        for (Block block : path) {
            botBlock = block.getRelative(BlockFace.UP);
            if (botBlock.getType() != Material.AIR) return false;

            midBlock = botBlock.getRelative(BlockFace.UP);
            if (midBlock.getType() != Material.AIR) return false;

            topBlock = midBlock.getRelative(BlockFace.UP);
            if (topBlock.getType() != Material.AIR) return false;
        }
        return true;
    }

    public static boolean isPathTooSteep(Location origin, Location target) {
        int x = Math.abs(target.getBlockX() - origin.getBlockX());
        int y = Math.abs(target.getBlockY() - origin.getBlockY());
        int z = Math.abs(target.getBlockZ() - origin.getBlockZ());

        Bukkit.getLogger().info("X: " + x + ". Y: " + y + ". Z: " + z);
        if (x + z >= y) {
            return false;
        } else return true;
    }

    public static boolean isBlockClear(Block block) {
        Block botBlock = block.getRelative(BlockFace.UP);
        if (botBlock.getType() != Material.AIR) return false;

        Block topBlock = botBlock.getRelative(BlockFace.UP);
        if (topBlock.getType() != Material.AIR) return false;

        return true;
    }
}