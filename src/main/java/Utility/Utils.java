package Utility;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

public class Utils {
    public static int clamp(int value, int min, int max){
        if(value < min) return min;
        else if(value > max) return max;
        else return value;
    }

    public static int getRoundedMod(double value){
        if(value < Math.round(value)){
            return 1;
        }
        else{
            return -1;
        }
    }

    public static Vector fixateVector(Vector vector){
        if(vector.getX() >= 0.5){
            vector.setX(1);
            vector.setZ(0);
            return vector;
        }
        else if(vector.getX() <= -0.5){
            vector.setX(-1);
            vector.setZ(0);
            return vector;
        }

        if(vector.getZ() >= 0.5){
            vector.setZ(1);
            vector.setX(0);
            return vector;
        }
        else if(vector.getZ() <= -0.5){
            vector.setZ(-1);
            vector.setX(0);
            return vector;
        }

        Bukkit.getLogger().warning("fixateVector() couldn't fixate a vector: " + vector);
        Bukkit.getLogger().warning("this could cause some issues in finding the right blocks to mine");
        return vector;
    }

    public static void despawnAllZombies(){
        List<LivingEntity> allEntities = Bukkit.getServer().getWorld("world").getLivingEntities();

        for(LivingEntity le : allEntities){
            if(le.getType().equals(EntityType.ZOMBIE)){
                le.remove();
            }
        }
    }

    public static BlockFace getFacingByYaw(double yaw){
        Bukkit.getLogger().info("Yaw is: " + yaw);
        double index = yaw / 90;
        Bukkit.getLogger().info("Index is: " + index);
        index = Math.round(index);
        Bukkit.getLogger().info("Rounded index is: " + index);

        if(index == 0 || index == 4) return BlockFace.SOUTH;
        else if(index == 1) return BlockFace.WEST;
        else if(index == 2) return BlockFace.NORTH;
        else if(index == 3) return BlockFace.EAST;
        else return null;
    }
}
