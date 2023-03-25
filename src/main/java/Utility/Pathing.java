package Utility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Pathing {

    private static List<Block> previousPath = new ArrayList<>();

    public static void clearPathPoints(){
        if(previousPath.size() > 0){
            for(Block block : previousPath){
                block.setType(Material.AIR);
            }
            previousPath.clear();
        }
    }

    public static void drawPathPoints(List<Block> path) {
        if(path != null){
            for(Block block : path){
                previousPath.add(block.getRelative(0, 1, 0));
                block.getRelative(0, 1, 0).setType(Material.GLASS);
            }
        }
        else{
            Bukkit.getLogger().info("Can't draw a path for a null.");
        }

    }


}
