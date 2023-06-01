package ZombieSkills;

import Model.Point;
import Utility.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class CustomPathSearch {
    static World world = Bukkit.getWorld("world");
    static List<BlockFace> directions = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public CustomPathSearch(World world) {
        this.world = world;
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

    public static Block findBotBlockFromY(int x, int y, int z) {
        Block block = world.getBlockAt(x, y, z);

        while (block.getType() == Material.AIR) {
            block = block.getRelative(BlockFace.DOWN);
        }

        return block;
    }

    public static Block getEntityFloorBlock(LivingEntity entity) {
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

    @Nullable
    public static Block getEntityRoofBlock(LivingEntity entity, int range) {
        Block startBlock = getEntityFloorBlock(entity);
        Block tempBlock = startBlock.getRelative(BlockFace.UP);

        for (int i = 0; i < range; i++) {
            if (tempBlock.getType() != Material.AIR) {
                return tempBlock;
            }

            tempBlock = tempBlock.getRelative(BlockFace.UP);
        }

        return null;
    }

    @Nullable
    public static List<Block> getNearestLedges(LivingEntity entity, int range, int obstaclesToIgnore) {
        List<Block> checkedBlocks = new ArrayList<>();
        Queue<Point> blocksToCheck = new LinkedList<>();
        Set<Block> ledges = new HashSet<>();

        Block topBlock = getEntityFloorBlock(entity);
        Block tempBlock;
        Point tempPoint;

        blocksToCheck.add(new Point(topBlock));
        checkedBlocks.add(topBlock);

        while (!blocksToCheck.isEmpty()) {
            tempPoint = blocksToCheck.poll();
            //Bukkit.getLogger().info("Queue check for: " + tempPoint.getBlock().toString());

            if (tempPoint.getLength() > range || tempPoint.getObstaclesReached() > obstaclesToIgnore) {
                //Bukkit.getLogger().info("Out of range or too many obstacles passed.");
                continue;
            }

            if (isBlockLedge(tempPoint.getBlock())) {
                if (tempPoint.getObstaclesReached() >= obstaclesToIgnore) {
                    ledges.add(tempPoint.getBlock());
                    continue;
                } else {
                    //Bukkit.getLogger().info("Passing an obstacle...");
                    tempBlock = tempPoint.getBlock();
                    tempBlock = findBotBlockFromY(tempBlock.getX(), tempBlock.getY(), tempBlock.getZ());
                    blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1, tempPoint.getObstaclesReached() + 1));
                    checkedBlocks.add(tempBlock);
                    continue;
                }
            }

            tempBlock = tempPoint.getBlock().getRelative(BlockFace.UP);
            if (tempBlock.getType() != Material.AIR && !checkedBlocks.contains(tempBlock)) {
                blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1, tempPoint.getObstaclesReached()));
                checkedBlocks.add(tempBlock);
                continue;
            }

            if (tempBlock.getType() != Material.AIR) continue;

            tempBlock = tempPoint.getBlock().getRelative(BlockFace.DOWN);
            if (tempPoint.getBlock().getType() == Material.AIR && isBlockClear(tempPoint.getBlock()) && tempBlock.getType() != Material.AIR && !checkedBlocks.contains(tempBlock)) {
                blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1, tempPoint.getObstaclesReached()));
                checkedBlocks.add(tempBlock);
                continue;
            }

            if (tempPoint.getBlock().getType() == Material.AIR) continue;


            //Bukkit.getLogger().info("Block is not a ledge");
            for (BlockFace direction : directions) {
                tempBlock = tempPoint.getBlock().getRelative(direction);

                if (!checkedBlocks.contains(tempBlock.getRelative(direction))) {
                    blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1, tempPoint.getObstaclesReached()));
                    checkedBlocks.add(tempBlock);
                }
            }
        }

        if (!ledges.isEmpty()) {
            return new ArrayList<>(ledges);
        } else {
            Bukkit.getLogger().info("No ledges could be found.");
            return null;
        }
    }

    @Nullable
    public static Block getNearestLedge(LivingEntity entity, int range, int obstaclesToIgnore) {
        List<Block> checkedBlocks = new ArrayList<>();
        Queue<Point> blocksToCheck = new LinkedList<>();

        Block topBlock = getEntityFloorBlock(entity);
        Block tempBlock;
        Point tempPoint;

        blocksToCheck.add(new Point(topBlock));
        checkedBlocks.add(topBlock);

        while (!blocksToCheck.isEmpty()) {
            tempPoint = blocksToCheck.poll();
            //Bukkit.getLogger().info("Queue check for: " + tempPoint.getBlock().toString());

            if (tempPoint.getLength() > range || tempPoint.getObstaclesReached() > obstaclesToIgnore) {
                //Bukkit.getLogger().info("Out of range or too many obstacles passed.");
                continue;
            }

            if (isBlockLedge(tempPoint.getBlock())) {
                if (tempPoint.getObstaclesReached() >= obstaclesToIgnore) {
                    return tempPoint.getBlock();
                } else {
                    //Bukkit.getLogger().info("Passing an obstacle...");
                    tempBlock = tempPoint.getBlock();
                    tempBlock = findBotBlockFromY(tempBlock.getX(), tempBlock.getY(), tempBlock.getZ());
                    blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1, tempPoint.getObstaclesReached() + 1));
                    checkedBlocks.add(tempBlock);
                    continue;
                }
            }

            tempBlock = tempPoint.getBlock().getRelative(BlockFace.UP);
            if (tempBlock.getType() != Material.AIR && !checkedBlocks.contains(tempBlock)) {
                blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1, tempPoint.getObstaclesReached()));
                checkedBlocks.add(tempBlock);
                continue;
            }

            if (tempBlock.getType() != Material.AIR) continue;

            tempBlock = tempPoint.getBlock().getRelative(BlockFace.DOWN);
            if (tempPoint.getBlock().getType() == Material.AIR && isBlockClear(tempPoint.getBlock()) && tempBlock.getType() != Material.AIR && !checkedBlocks.contains(tempBlock)) {
                blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1, tempPoint.getObstaclesReached()));
                checkedBlocks.add(tempBlock);
                continue;
            }

            if (tempPoint.getBlock().getType() == Material.AIR) continue;

            //Bukkit.getLogger().info("Block is not a ledge");
            for (BlockFace direction : directions) {
                tempBlock = tempPoint.getBlock().getRelative(direction);

                if (!checkedBlocks.contains(tempBlock.getRelative(direction))) {
                    blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1, tempPoint.getObstaclesReached()));
                    checkedBlocks.add(tempBlock);
                }
            }
        }

        Bukkit.getLogger().warning("No ledges could be found.");
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

    @Nullable
    public static Block findLocationForPathBuildingUp(Location targetLoc, LivingEntity builder) {
        double smallestDistance = 9999;
        double pathDegrees = -0.7;

        Block result = null;
        List<Block> possibleResults = new ArrayList<>();
        List<Vector> vectors = Arrays.asList(new Vector(0, pathDegrees, -1), new Vector(1, pathDegrees, 0), new Vector(0, pathDegrees, 1), new Vector(-1, pathDegrees, 0));

        for (Vector v : vectors) {
            RayTraceResult ray = world.rayTrace(targetLoc, v, 64, FluidCollisionMode.ALWAYS, true, 1, null);
            if (ray != null && ray.getHitBlock() != null) {
                possibleResults.add(ray.getHitBlock());
                Bukkit.getLogger().info("Found a ray: " + ray.getHitBlock());
            }
        }

        for (Block b : possibleResults) {
            if (b.getLocation().distance(builder.getLocation()) < smallestDistance && CustomPathSearch.isTargetReachable(builder, b.getLocation()) && CustomPathSearch.isBlockClear(b)) {
                smallestDistance = b.getLocation().distance(builder.getLocation());
                result = b;
                Bukkit.getLogger().info("new block assigned: " + result);
            }
        }

        if (result != null) {
            return result.getRelative(BlockFace.UP);
        } else {
            return null;
        }
    }

    // GET a path
    public static List<Block> getPathTopBlocks(LivingEntity origin, LivingEntity target) {
        Location originLoc = getEntityFloorBlock(origin).getLocation();
        Location targetLoc = getEntityFloorBlock(target).getLocation();

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
            block = findTopBlockFromY(cordX, getEntityFloorBlock(origin).getY(), cordZ);
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

    public static List<Block> getPathStraightLine(@NotNull Location originLoc, @NotNull Location targetLoc) {
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

        for (Block b : blocks) {
            Bukkit.getLogger().info(b.getLocation().toString());
        }

        if (!isPathWalkable(blocks)) return null;

        return blocks;
    }

    // MODIFY a path

    public static List<Block> widenStraightPath(List<Block> path, int range) {
        Block start = path.get(0), end = path.get(path.size() - 1);

        int xd = Math.abs(end.getX() - start.getX());
        int zd = Math.abs(end.getZ() - start.getZ());

        List<BlockFace> directions;
        if (xd > zd) directions = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH);
        else directions = Arrays.asList(BlockFace.WEST, BlockFace.EAST);

        int size = path.size() * 3;
        Block block;
        for (int i = 0; i < size; i += 3) {
            block = path.get(i);
            for (BlockFace d : directions) {
                path.add(i + 1, block.getRelative(d));
            }
        }

        return path;
    }

    public static List<Block> cleanUpPath(List<Block> path) {
        for (int i = 0; i < path.size(); i++) {
            if (!path.get(i).isReplaceable()) {
                path.remove(i);
                i--;
            }
        }

        return path;
    }

    // booleans
    public static boolean isPathWalkable(List<Block> path) {
        return !isPathTooSteep(path) && isPathClear(path);
    }

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

    public static boolean isPathTooSteep(List<Block> path) {
        double lastY = path.get(0).getY();
        for (Block b : path) {
            if (b.getLocation().getY() - lastY > 1) {
                Bukkit.getLogger().warning("Path would be too steep");
                return true;
            }
            lastY = b.getLocation().getY();
        }
        return false;
    }

    public static boolean isBlockClear(Block block) {
        Block botBlock = block.getRelative(BlockFace.UP);
        if (botBlock.getType() != Material.AIR) return false;

        Block topBlock = botBlock.getRelative(BlockFace.UP);
        if (topBlock.getType() != Material.AIR) return false;

        return true;
    }

    public static boolean isBlockLedge(Block block) {
        if (block.getType() == Material.AIR && block.getRelative(BlockFace.DOWN).getType() == Material.AIR && isBlockClear(block))
            return true;
        return false;
    }

    public static boolean isTargetReachable(LivingEntity origin, Location target) {
        Mob mob;
        // Sometimes this function is called for a player, in which case it should be skipped or returned as successful
        try {
            mob = (Mob) origin;
        } catch (ClassCastException e) {
            return true;
        }
        Location finalPoint = mob.getPathfinder().findPath(target).getFinalPoint();
        if (finalPoint != null) {
            return finalPoint.distance(target) <= 1;
        } else {
            return false;
        }
    }
}