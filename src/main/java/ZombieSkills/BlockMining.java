package ZombieSkills;

import Utility.DelayedTask;
import Utility.GameUtils;
import Utility.RepeatableTask;
import Utility.Utils;
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
import java.util.List;

import static ZombieSkills.CustomPathSearch.*;

public class BlockMining implements Skill {
    Zombie zombie;
    Pathfinder pathfinder;
    World world;
    List<ItemStack> inventory;
    int activeInventorySlot;

    List<Block> blocksToBreak;
    Block focusBlock;
    boolean breaking = false;
    double mineRange = 4;

    // New algo
    double wallSearchRange = 10;
    List<BlockFace> directions;
    Block nextPathBlock;
    boolean firstGrade = true;

    double lastDistanceToFocusBlock = -1;

    PotionEffect standStill;

    int breakingTaskId, breakTaskId;

    public BlockMining(Zombie zombie, Pathfinder pathfinder, World world, List<ItemStack> inventory, int activeInventorySlot) {
        this.zombie = zombie;
        this.pathfinder = pathfinder;
        this.world = world;
        this.inventory = inventory;
        this.activeInventorySlot = activeInventorySlot;

        blocksToBreak = new ArrayList<>();
        directions = new ArrayList<>();

        directions.add(BlockFace.NORTH);
        directions.add(BlockFace.EAST);
        directions.add(BlockFace.SOUTH);
        directions.add(BlockFace.WEST);

        standStill = new PotionEffect(PotionEffectType.SLOW, 60, 255);

        inventory.add(new ItemStack(Material.WOODEN_PICKAXE));
    }

    // Public
    public void mineStraightTo(LivingEntity target) {
        Block topBlock, botBlock;

        topBlock = findBlockBetween(zombie, target, world, true);
        botBlock = findBlockBetween(zombie, target, world, false);

        if (topBlock != null) {
            blocksToBreak.add(topBlock);
        }
        if (botBlock != null) {
            blocksToBreak.add(botBlock);
        }

        if (topBlock == null && botBlock == null) {
            mineUpTo(target);
        }
    }

    public void mineUpTo(LivingEntity target) {
        if (firstGrade) {
            mineUpToFirstGrade(target);
        } else if (!firstGrade) {
            mineUpToSecondGrade(target);
        }
    }

    protected void mineUpToFirstGrade(LivingEntity target) {
        double xDifference = Math.abs(zombie.getLocation().getX() - target.getLocation().getX());
        double yDifference = Math.abs(zombie.getLocation().getZ() - target.getLocation().getZ());
        double difference = xDifference + yDifference;

        // if difference is less than 1.41, we consider the target to be right above the zombie
        if (difference < 1.41) {
            // Broadcast, that this skill can't reach the zombie
            blocksToBreak.add(getTopBlock(target));
        } else {
            List<Block> optionalCustomPath = getPathTopBlocks(zombie, target);
            Block obstacle = getFirstObstacleOf(optionalCustomPath);

            if (obstacle != null && isBlockReachable(zombie, obstacle, mineRange)) {
                blocksToBreak.add(obstacle);

                Block blockInFrontOfObstacle = findBlockBefore(obstacle, zombie);

                if (blockInFrontOfObstacle.getType() != Material.AIR) {
                    blocksToBreak.add(blockInFrontOfObstacle);
                }
            } else {
                // Broadcast that miners can't find a way to mine straight up
                // Enable second grade
                firstGrade = false;
                mineUpToSecondGrade(target);
            }
        }
    }

    protected void mineUpToSecondGrade(LivingEntity target) {
        Bukkit.getLogger().info("Using new algo");

        if (nextPathBlock != null) {
            if (!getTopBlock(zombie).equals(nextPathBlock)) {
                Bukkit.getLogger().info("I'm still not on my next path block. " + nextPathBlock.getLocation());
                Bukkit.getLogger().info("I'm on " + getTopBlock(zombie).getLocation());
                return;
            } else {
                removeNextPathBlock();
                Bukkit.getLogger().info("I reached my next path block, continuing");
            }
        }

        double xDifference = Math.abs(zombie.getLocation().getX() - target.getLocation().getX());
        double yDifference = Math.abs(zombie.getLocation().getZ() - target.getLocation().getZ());
        double difference = xDifference + yDifference;

        // if difference is less than 1.41, we consider the target to be right above the zombie
        if (difference < 1.41 && getTopBlock(target).getLocation().distance(zombie.getLocation()) <= mineRange) {
            blocksToBreak.add(getTopBlock(target));
            return;
        }

        List<Block> blocksAround = new ArrayList<>();
        RayTraceResult result;
        Bukkit.getLogger().info("Ray casting...");
        for (BlockFace direction : directions) {
            result = world.rayTraceBlocks(zombie.getEyeLocation(), direction.getDirection(), wallSearchRange);
            if (result != null) {
                if (result.getHitBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR &&
                        result.getHitBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(direction.getOppositeFace()).getType() != Material.AIR) {
                    blocksAround.add(result.getHitBlock());
                }
            }
        }

        if (blocksAround.size() == 0) {
            //Make the zombie walk around a bit to find the closest wall
            Bukkit.getLogger().warning("Zombie cannot find any walls to mine up to the player");
            return;
        }

        double closestDistance = -1, tempDistance;
        Block closestBlock = blocksAround.get(0);
        for (Block block : blocksAround) {
            tempDistance = block.getLocation().distance(target.getLocation());
            if (closestDistance == -1) {
                closestDistance = tempDistance;
                closestBlock = block;
            } else if (closestDistance > tempDistance) {
                closestDistance = tempDistance;
                closestBlock = block;
            }
        }

        blocksToBreak.add(closestBlock);
        if (closestBlock.getRelative(BlockFace.UP).getType() != Material.AIR) {
            blocksToBreak.add(closestBlock.getRelative(BlockFace.UP));
        }
        if (closestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() != Material.AIR) {
            blocksToBreak.add(closestBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP));
        }

        nextPathBlock = closestBlock.getRelative(BlockFace.DOWN);
    }

    public void mineDownTo(LivingEntity target) {
        double xDifference = Math.abs(zombie.getLocation().getX() - target.getLocation().getX());
        double yDifference = Math.abs(zombie.getLocation().getZ() - target.getLocation().getZ());
        double difference = xDifference + yDifference;

        // if difference is less than 1.41, we consider the target to be right below the zombie
        if (difference < 1.41) {
            blocksToBreak.add(getTopBlock(zombie));
            return;
        }

        Location tempTargetLocation = target.getLocation();
        tempTargetLocation.setY(zombie.getLocation().getY());

        Block topBlock = findBlockBetween(zombie, tempTargetLocation, world, true);
        Block midBlock = findBlockBetween(zombie, tempTargetLocation, world, false);
        Block botBlock = null;

        if (midBlock != null) {
            blocksToBreak.add(midBlock);

            botBlock = midBlock.getRelative(BlockFace.DOWN);
            blocksToBreak.add(botBlock);
        }
        if (topBlock != null) {
            blocksToBreak.add(topBlock);

            if (botBlock == null) {
                botBlock = topBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                blocksToBreak.add(botBlock);
            }
        }
    }

    public boolean isBreaking() {
        return breaking;
    }

    // find a block that's between two entities' with a range
    protected Block findBlockBetween(LivingEntity origin, LivingEntity target, World world, boolean eyeLevel) {
        double yOffset = 1.5;
        if (eyeLevel) {
            yOffset += 1.1;
        }

        Location originLoc = getTopBlock(origin).getLocation().add(0.5, yOffset, 0.5);
        Location targetLoc = getTopBlock(target).getLocation().add(0.5, yOffset, 0.5);

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

    protected Block findBlockBetween(LivingEntity origin, Location targetLoc, World world, boolean eyeLevel) {
        double yOffset = 1.5;
        if (eyeLevel) {
            yOffset += 1.1;
        }

        Location originLoc = getTopBlock(origin).getLocation().add(0.5, yOffset, 0.5);

        Vector direction = targetLoc.toVector().subtract(originLoc.toVector());
        double distance = originLoc.distance(targetLoc);

        int range = 4;
        RayTraceResult result = world.rayTraceBlocks(originLoc, direction, Utils.clamp((int) distance, 0, range));

        if (result != null) {
            return result.getHitBlock();
        } else return null;
    }

    protected void startBreakingBlock(ItemStack tool) {
        breaking = true;
        float speed = GameUtils.getBlockDestroySpeed(focusBlock, tool);
        zombie.addPotionEffect(standStill);

        breakingTaskId = new RepeatableTask(() -> {
            GameUtils.playBreakBlockParticles(world, focusBlock, 3);
            zombie.swingMainHand();
            world.playSound(focusBlock.getLocation(), focusBlock.getBlockSoundGroup().getHitSound(), 1, 1);
        }, 0, 1).getId();

        breakTaskId = new DelayedTask(() -> {
            destroyBlock(focusBlock, tool);
        }, speed).getId();
    }

    protected void stopBreakingBlocks() {
        breaking = false;
        firstGrade = true;
        zombie.removePotionEffect(PotionEffectType.SLOW);
        blocksToBreak.clear();
        Bukkit.getScheduler().cancelTask(breakingTaskId);
        Bukkit.getScheduler().cancelTask(breakTaskId);
        lastDistanceToFocusBlock = -1;
    }

    protected void destroyBlock(Block block, ItemStack tool) {
        focusBlock = null;
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
        lastDistanceToFocusBlock = -1;

        firstGrade = true;
    }

    protected Block findBlockBefore(Block block, LivingEntity entity) {
        Location blockFacing = block.getLocation();
        Vector direction = zombie.getLocation().clone().subtract(blockFacing).toVector();

        blockFacing.setDirection(direction);
        entity.getLocation().setDirection(direction);
        return block.getRelative(Utils.getFacingByYaw(blockFacing.getYaw()));
    }

    protected boolean isBlockReachable(Zombie zombie, Block block, double range) {
        Location closestLocation = zombie.getPathfinder().findPath(block.getLocation()).getFinalPoint();

        if (closestLocation != null) {
            if (closestLocation.distance(block.getLocation()) <= range) {
                return true;
            } else return false;
        }
        Bukkit.getLogger().warning("Could not predict if the block is reachable");
        return false;
    }

    public Block getNextPathBlock() {
        return nextPathBlock;
    }

    public void removeNextPathBlock() {
        nextPathBlock = null;
    }

    @Override
    public boolean trigger() {
        if (blocksToBreak.size() > 0) {
            focusBlock = blocksToBreak.get(0);

            return true;
        } else return false;
    }

    @Override
    public void action() {
        Location focusBlockLocation = focusBlock.getLocation();
        if (zombie.getLocation().distance(focusBlockLocation) > mineRange) {
            if (lastDistanceToFocusBlock != -1) {
                if (lastDistanceToFocusBlock - focusBlockLocation.distance(zombie.getLocation()) < 0.5f) {
                    stopBreakingBlocks();
                    if (firstGrade) {
                        // Can't reach the block with first algo, change to second one
                        firstGrade = false;
                    } else if (!firstGrade) {
                        // Broadcast that this skill cannot reach the blocks to mine
                        Bukkit.getLogger().warning("Zombie could not find any way to reach the player.");
                    }

                    Bukkit.getLogger().info("Can't reach the block, cancelling my task");
                }
            }

            lastDistanceToFocusBlock = focusBlockLocation.distance(zombie.getLocation());


            pathfinder.moveTo(focusBlock.getLocation());
            return;
        }

        if (!breaking) {
            startBreakingBlock(inventory.get(activeInventorySlot));
        }

        zombie.lookAt(focusBlock.getLocation());
    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }
}
