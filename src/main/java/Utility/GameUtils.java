package Utility;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.Random;

public class GameUtils {
    public static Block getFocusedBlock(LivingEntity entity){
        RayTraceResult result = entity.rayTraceBlocks(5);
        if(result != null){
            return result.getHitBlock();
        }
        else{
            return null;
        }
    }

    public static void playBreakBlockParticles(World world, Block block, int count){
        Location loc = block.getLocation();
        Location tempLoc;
        Random rand = new Random();
        for(int i = 0; i < count; i++){
            tempLoc = loc;
            tempLoc.add(rand.nextFloat(1), rand.nextFloat(1), rand.nextFloat(1));
            world.spawnParticle(Particle.BLOCK_CRACK, tempLoc, 1, block.getBlockData());
        }
    }

    public static void playBreakBlockSounds(World world, Block block){
        world.playSound(block.getLocation(), block.getBlockSoundGroup().getHitSound(), 1, 1);
    }

    public static float getBlockDestroySpeed(Block block, ItemStack tool){
        final int wood = 2, stone = 4, iron = 6, diamond = 8, netherite = 9, gold = 12;
        float multiplier, hardness = block.getType().getHardness();
        if(block.isValidTool(tool)){
            Bukkit.getLogger().info(tool.getType() + " is valid for breaking: " + block.getType());
            hardness *= 1.5f;
        }
        else{
            Bukkit.getLogger().info(tool.getType() + " is not valid for breaking: " + block.getType());
            hardness *= 5f;
            return hardness;
        }

        if(tool.getType() == Material.WOODEN_PICKAXE){
            hardness /= wood;
        }
        else if(tool.getType() == Material.STONE_PICKAXE){
            hardness /= stone;
        }
        else if(tool.getType() == Material.IRON_PICKAXE){
            hardness /= iron;
        }
        else if(tool.getType() == Material.DIAMOND_PICKAXE){
            hardness /= diamond;
        }
        else if(tool.getType() == Material.NETHERITE_PICKAXE){
            hardness /= netherite;
        }
        else if(tool.getType() == Material.GOLDEN_PICKAXE){
            hardness /= gold;
        }
        return hardness;
    }
}
