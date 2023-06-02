package ZombieSkills;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Utility.Pathing.getEntityFloorBlock;

public class BlockMining implements Skill {
    Zombie zombie;
    Pathfinder pathfinder;
    World world;
    List<ItemStack> inventory;
    int activeInventorySlot;

    List<Block> blocksToBreak = new ArrayList<>();
    boolean breaking = false;
    double mineRange = 5;

    // 4 Rays
    double wallSearchRange = 10;
    List<BlockFace> directions = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    PotionEffect standStill = new PotionEffect(PotionEffectType.SLOW, 60, 255);

    int breakingTaskId, breakTaskId;

    public ReachTarget searchForFirstObstacle = this::searchForFirstObstacle;
    public ReachTarget searchForStraightPath = this::searchForStraightPath;
    public ReachTarget searchFor4raysUp = this::searchFor4raysUp;

    public BlockMining(Zombie zombie, World world, int level, List<ItemStack> inventory, int activeInventorySlot) {
        this.zombie = zombie;
        this.pathfinder = zombie.getPathfinder();
        this.world = world;
        this.inventory = inventory;
        this.activeInventorySlot = activeInventorySlot;

        inventory.add(new ItemStack(Material.WOODEN_PICKAXE));
    }

    public Object searchForFirstObstacle(LivingEntity origin, LivingEntity target, int level, int index) {
        Block obstacle = Pathing.findFirstObstacleTo(origin, target);
        if (obstacle != null && Pathing.isBlockReachable(origin, obstacle, mineRange)) {
            blocksToBreak.add(obstacle);
            return obstacle;
        }

        Bukkit.getLogger().warning("First obstacle failed");
        return null;
    }

    public Object searchForStraightPath(LivingEntity origin, LivingEntity target, int level, int index) {
        Block topBlock = findBlockBetween(origin, target, true);
        Block botBlock = findBlockBetween(origin, target, false);
        boolean success = false;

        if (topBlock == null && botBlock == null) {
            Bukkit.getLogger().warning("Straight path failed");
            return null;
        }

        if (topBlock != null && Pathing.isBlockReachable(origin, topBlock, mineRange)) {
            blocksToBreak.add(topBlock);
            success = true;
        }
        if (botBlock != null && Pathing.isBlockReachable(origin, botBlock, mineRange)) {
            blocksToBreak.add(botBlock);
            success = true;
        }

        if (!success) return null;

        if (botBlock != null) return botBlock;
        else return topBlock;
    }

    public Object searchFor4raysUp(LivingEntity origin, LivingEntity target, int level, int index) {
        List<Block> possibleBlocks = new ArrayList<>();
        for (BlockFace d : directions) {
            RayTraceResult result = world.rayTraceBlocks(origin.getLocation().add(0, 1, 0), d.getDirection(), 4 * level);
            if (result != null && result.getHitBlock() != null) possibleBlocks.add(result.getHitBlock());
        }


        if (possibleBlocks.size() == 0) {
            Bukkit.getLogger().warning("rays up failed");
            return null;
        }

        double smallestDistance = 9999;
        Block blockToMine = null;
        for (Block b : possibleBlocks) {
            if (b.getLocation().distance(target.getLocation()) < smallestDistance && Pathing.isBlockReachable(origin, b, mineRange)) {
                smallestDistance = b.getLocation().distance(target.getLocation());
                blockToMine = b;
            }
        }

        blocksToBreak.add(blockToMine);

        Block tempBlock = blockToMine.getRelative(BlockFace.UP);
        if (!tempBlock.isPassable()) blocksToBreak.add(tempBlock);

        tempBlock = tempBlock.getRelative(BlockFace.UP);
        if (!tempBlock.isPassable()) blocksToBreak.add(tempBlock);

        return blockToMine;
    }

    public boolean isBreaking() {
        return breaking;
    }

    // find a block that's between two entities' with a range
    protected Block findBlockBetween(LivingEntity origin, LivingEntity target, boolean eyeLevel) {
        double yOffset = 1.5;
        if (eyeLevel) {
            yOffset += 1.1;
        }

        Location originLoc = getEntityFloorBlock(origin).getLocation().add(0.5, yOffset, 0.5);
        Location targetLoc = getEntityFloorBlock(target).getLocation().add(0.5, yOffset, 0.5);

        Vector direction = targetLoc.toVector().subtract(originLoc.toVector());
        double distance = originLoc.distance(targetLoc);
        int range = 4;
        RayTraceResult result = world.rayTraceBlocks(originLoc, direction, Utils.clamp((int) distance, 0, range));


        if (result != null) {
            if (result.getHitEntity() != null) {
                Bukkit.getLogger().info("I hit " + result.getHitEntity());
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
        zombie.addPotionEffect(standStill);

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
        zombie.removePotionEffect(PotionEffectType.SLOW);
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
        return blocksToBreak.size() > 0;
    }

    @Override
    public void action() {
        if (!isBreaking()) {
            Bukkit.getLogger().info("Not breaking");
            if (!startBreakingBlock(inventory.get(activeInventorySlot), blocksToBreak.get(0))) {
                Bukkit.getLogger().info("Can't reach the block, moving to " + blocksToBreak.get(0).getLocation());
                Bukkit.getLogger().info("The distance to block is: " + zombie.getLocation().distance(blocksToBreak.get(0).getLocation()));

                pathfinder.moveTo(blocksToBreak.get(0).getLocation());
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
