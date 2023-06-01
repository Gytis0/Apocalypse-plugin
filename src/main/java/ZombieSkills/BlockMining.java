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

import static ZombieSkills.CustomPathSearch.getEntityFloorBlock;

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

    // 4 Rays
    double wallSearchRange = 10;
    List<BlockFace> directions;
    Block nextPathBlock;
    boolean firstGrade = true;

    double lastDistanceToFocusBlock = -1;

    PotionEffect standStill;

    int breakingTaskId, breakTaskId;

    public BlockMining(Zombie zombie, World world, int level, List<ItemStack> inventory, int activeInventorySlot) {
        this.zombie = zombie;
        this.pathfinder = zombie.getPathfinder();
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

    public boolean isBreaking() {
        return breaking;
    }

    // find a block that's between two entities' with a range
    protected Block findBlockBetween(LivingEntity origin, LivingEntity target, World world, boolean eyeLevel) {
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

    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }
}
