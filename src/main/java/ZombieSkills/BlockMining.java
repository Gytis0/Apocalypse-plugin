package ZombieSkills;

import Model.BlockFound;
import Model.Goals.ReachTarget;
import Utility.*;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Utility.Pathing.findEntityFloorBlock;

public class BlockMining extends Skill {
    Zombie zombie;
    Pathfinder pathfinder;
    World world;
    List<ItemStack> inventory;
    int activeInventorySlot;

    List<Block> blocksToBreak = new ArrayList<>();
    boolean breaking = false;
    double mineRange = 5;

    List<Block> currentPath = new ArrayList<>();

    // 4 Rays
    List<BlockFace> directions = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    BlockFace recentDirection;

    int breakingTaskId, breakTaskId;

    public ReachTarget searchForFirstObstacle = this::mineFirstObstacle;
    public ReachTarget searchForStraightPath = this::mineStraightLine;
    public ReachTarget carveUp = this::carveUp;
    public ReachTarget carveDown = this::carveDown;

    public BlockMining(Zombie zombie, World world, int level, List<ItemStack> inventory, int activeInventorySlot, List<Block> path) {
        this.zombie = zombie;
        this.pathfinder = zombie.getPathfinder();
        this.world = world;
        this.inventory = inventory;
        this.activeInventorySlot = activeInventorySlot;
        this.currentPath = path;

        inventory.add(new ItemStack(Material.NETHERITE_PICKAXE));
    }

    public Object mineFirstObstacle(LivingEntity origin, LivingEntity target, int level, int index) {
        Block obstacle = Pathing.findFirstObstacleTo(origin, target);
        if (obstacle != null && !currentPath.contains(obstacle) && Pathing.isBlockReachable(origin, obstacle, mineRange)) {
            blocksToBreak.add(obstacle);
            return obstacle;
        }

        //Bukkit.getLogger().warning("First obstacle failed");
        return null;
    }

    public Object mineStraightLine(LivingEntity origin, LivingEntity target, int level, int index) {
        Block topBlock = findBlockBetween(origin, target, true);
        Block botBlock = findBlockBetween(origin, target, false);
        boolean success = false;

        if (topBlock == null && botBlock == null) {
            //Bukkit.getLogger().warning("Straight path failed");
            return null;
        }

        if (topBlock != null && !currentPath.contains(topBlock) && Pathing.isBlockReachable(origin, topBlock, mineRange)) {
            blocksToBreak.add(topBlock);
            success = true;
        }
        if (botBlock != null && !currentPath.contains(botBlock) && Pathing.isBlockReachable(origin, botBlock, mineRange)) {
            blocksToBreak.add(botBlock);
            success = true;
        }

        if (!success) return null;

        if (botBlock != null) return botBlock;
        else return topBlock;
    }

    @Nullable
    public Object carveUp(LivingEntity origin, LivingEntity target, int level, int index) {
        Block tempBlock;

        // If the path doesn't exist, find a start for it
        if (currentPath.isEmpty()) {
            //Bukkit.getLogger().info("Finding a new starting block...");
            BlockFound startBlock = findStartBlockEyeLevel(origin, target);
            if (startBlock == null) return null;
            return mineUp(startBlock);
        }

        Block pivotBlock = currentPath.get(currentPath.size() - 1).getRelative(BlockFace.UP).getRelative(BlockFace.UP);
        List<BlockFound> possibleBlocks = new ArrayList<>();
        for (BlockFace d : directions) {
            tempBlock = pivotBlock.getRelative(d);
            if (!tempBlock.isPassable() && !tempBlock.getRelative(BlockFace.DOWN).isPassable())
                possibleBlocks.add(new BlockFound(tempBlock, d.getOppositeFace()));
        }

        BlockFound closestBlock = Utils.findClosestBlockFoundToTarget(possibleBlocks, target);

        return mineUp(closestBlock);
    }

    public Object carveDown(LivingEntity origin, LivingEntity target, int level, int index) {
        Block tempBlock;

        // If the path doesn't exist, find a start for it
        if (currentPath.isEmpty()) {
            //Bukkit.getLogger().info("Finding a new starting block...");
            BlockFound startBlock = findStartBlockDown(origin, target);
            if (startBlock == null) return null;
            return mineDown(startBlock);
        }


        Block pivotBlock = currentPath.get(currentPath.size() - 1);
        List<BlockFound> possibleBlocks = new ArrayList<>();
        for (BlockFace d : directions) {
            tempBlock = pivotBlock.getRelative(d);
            if (!tempBlock.isPassable()) possibleBlocks.add(new BlockFound(tempBlock, d.getOppositeFace()));
        }

        BlockFound closestBlock = Utils.findClosestBlockFoundToTarget(possibleBlocks, target);

        return mineDown(closestBlock);
    }

    private Block mineUp(BlockFound startBlock) {
        Block result;
        Block tempBlock;
        tempBlock = result = startBlock.getBlock();
        recentDirection = startBlock.getFoundFrom();

        blocksToBreak.add(tempBlock);

        tempBlock = tempBlock.getRelative(BlockFace.UP);
        if (!tempBlock.isPassable()) blocksToBreak.add(tempBlock);

        tempBlock = tempBlock.getRelative(recentDirection);
        if (!tempBlock.isPassable()) blocksToBreak.add(tempBlock);

        return result;
    }

    private Block mineDown(BlockFound startBlock) {
        Block result;
        Block tempBlock;
        tempBlock = result = startBlock.getBlock();
        recentDirection = startBlock.getFoundFrom();

        blocksToBreak.add(tempBlock);

        tempBlock = tempBlock.getRelative(BlockFace.UP);
        if (!tempBlock.isPassable()) blocksToBreak.add(tempBlock);

        tempBlock = tempBlock.getRelative(BlockFace.UP);
        if (!tempBlock.isPassable()) blocksToBreak.add(tempBlock);

        return result;
    }


    public boolean isBreaking() {
        return breaking;
    }

    @Nullable
    public BlockFound findStartBlockEyeLevel(LivingEntity origin, LivingEntity target) {
        List<BlockFound> possibleBlocks = new ArrayList<>();

        for (BlockFace d : directions) {
            RayTraceResult result = world.rayTraceBlocks(origin.getLocation().add(0, 1, 0), d.getDirection(), mineRange + 2);
            if (result != null && result.getHitBlock() != null)
                possibleBlocks.add(new BlockFound(result.getHitBlock(), d.getOppositeFace()));
        }

        if (possibleBlocks.isEmpty()) return null;

        return Utils.findClosestBlockFoundToTarget(possibleBlocks, target);
    }

    @Nullable
    public BlockFound findStartBlockDown(LivingEntity origin, LivingEntity target) {
        List<BlockFound> possibleBlocks = new ArrayList<>();

        Block entityBlock = Pathing.findEntityFloorBlock(origin);
        Block tempBlock;

        for (BlockFace d : directions) {
            tempBlock = entityBlock.getRelative(d);
            if (!tempBlock.isPassable()) possibleBlocks.add(new BlockFound(tempBlock, d.getOppositeFace()));
        }

        if (possibleBlocks.isEmpty()) return null;

        return Utils.findClosestBlockFoundToTarget(possibleBlocks, target);
    }

    // find a block that's between two entities' with a range
    protected Block findBlockBetween(LivingEntity origin, LivingEntity target, boolean eyeLevel) {
        double yOffset = 1.5;
        if (eyeLevel) {
            yOffset += 1.1;
        }

        Location originLoc = findEntityFloorBlock(origin).getLocation().add(0.5, yOffset, 0.5);
        Location targetLoc = findEntityFloorBlock(target).getLocation().add(0.5, yOffset, 0.5);

        Vector direction = targetLoc.toVector().subtract(originLoc.toVector());
        double distance = originLoc.distance(targetLoc);
        int range = 4;
        RayTraceResult result = world.rayTraceBlocks(originLoc, direction, Utils.clamp((int) distance, 0, range));


        if (result != null) {
            if (result.getHitEntity() != null) {
                //Bukkit.getLogger().info("I hit " + result.getHitEntity());
            }
            return result.getHitBlock();
        } else {
            return null;
        }
    }

    protected boolean startBreakingBlock(ItemStack tool, Block block) {
        if (zombie.getLocation().distance(block.getLocation()) > mineRange) return false;

        breaking = true;
        float timeToBreak = GameUtils.getBlockDestroySpeed(block, tool);

        breakingTaskId = new RepeatableTask(() -> {
            GameUtils.playBreakBlockParticles(world, block, 3);
            zombie.swingMainHand();
            world.playSound(block.getLocation(), block.getBlockSoundGroup().getHitSound(), 1, 1);
        }, 0, 0.5f).getId();

        breakTaskId = new DelayedTask(() -> {
            destroyBlock(block, tool);
        }, timeToBreak).getId();

        return true;
    }

    protected void stopBreakingBlocks() {
        breaking = false;
        blocksToBreak.clear();
        Bukkit.getScheduler().cancelTask(breakingTaskId);
        Bukkit.getScheduler().cancelTask(breakTaskId);
    }

    protected void destroyBlock(Block block, ItemStack tool) {
        breaking = false;

        if (tool != null && block.isValidTool(tool)) {
            block.breakNaturally(tool);
        } else {
            block.setType(Material.AIR);
        }

        Bukkit.getScheduler().cancelTask(breakingTaskId);
        world.playSound(block.getLocation(), block.getBlockSoundGroup().getBreakSound(), 1, 1);
        blocksToBreak.remove(0);
        zombie.removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    public boolean trigger() {
        if (!enabled) return false;

        zombie.setAI(!breaking);

        return blocksToBreak.size() > 0;
    }

    @Override
    public void action() {
        if (!isBreaking()) {
            if (!startBreakingBlock(inventory.get(activeInventorySlot), blocksToBreak.get(0))) {
                //Bukkit.getLogger().info("Can't reach the block");
                // Bukkit.getLogger().info(blocksToBreak.get(0).getLocation().toVector().toString());
                // Bukkit.getLogger().info("distance to block " + zombie.getLocation().distance(blocksToBreak.get(0).getLocation()));

                pathfinder.moveTo(blocksToBreak.get(0).getLocation());
            }
        } else {
            //Bukkit.getLogger().info("Breaking " + blocksToBreak.get(0).getLocation().toVector());
        }
    }

    @Override
    public void disable() {
        enabled = false;
        stopBreakingBlocks();
        currentPath.clear();
        zombie.setAI(true);
    }

    @Override
    public void enable() {
        enabled = true;
    }
}
