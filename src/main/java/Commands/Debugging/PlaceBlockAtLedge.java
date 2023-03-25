package Commands.Debugging;

import Model.Point;
import Utility.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PlaceBlockAtLedge implements TabExecutor {
    List<BlockFace> directions;

    public PlaceBlockAtLedge(){
        directions = new ArrayList<>();
        directions.add(BlockFace.NORTH);
        directions.add(BlockFace.EAST);
        directions.add(BlockFace.SOUTH);
        directions.add(BlockFace.WEST);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        Block ledge = getNearestLedge((Player)sender);
        if(ledge != null){
            ledge.setType(Material.GLASS);
            sender.sendMessage(ChatColor.GREEN + "Block placed at: " + ledge.getLocation());
        }
        else{
            sender.sendMessage(ChatColor.RED + "Block could not be placed.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    protected Block getNearestLedge(LivingEntity entity) {
        List<Block> checkedBlocks = new ArrayList<>();
        Queue<Point> blocksToCheck = new LinkedList<>();

        Block topBlock = getTopBlock(entity);
        Block tempBlock;
        Point tempPoint;

        blocksToCheck.add(new Point(topBlock));
        checkedBlocks.add(topBlock);

        while(!blocksToCheck.isEmpty()){
            tempPoint = blocksToCheck.poll();
            Bukkit.getLogger().info("Queue check for: " + tempPoint.getBlock().toString());

            if(tempPoint.getLength() > 5) {
                Bukkit.getLogger().info("Out of range.");
                continue;
            }

            if(tempPoint.getBlock().getType() == Material.AIR && isBlockClear(tempPoint.getBlock())){
                return tempPoint.getBlock();
            }

            Bukkit.getLogger().info("Block is not a ledge");

            for(BlockFace direction : directions){
                tempBlock = tempPoint.getBlock().getRelative(direction);

                if(isBlockClear(tempBlock) && !checkedBlocks.contains(tempBlock.getRelative(direction))){
                    blocksToCheck.add(new Point(tempBlock, tempPoint.getLength() + 1));
                    checkedBlocks.add(tempBlock);
                }
            }
        }

        // No ledge could be found
        Bukkit.getLogger().info("No ledge could be found.");
        return null;
    }

    public Block getTopBlock(LivingEntity entity){
        Block block = entity.getLocation().getBlock().getRelative(0, -1,0);
        int modX = 0, modZ = 0;
        if(block.getType() == Material.AIR){
            double x = entity.getLocation().getX();
            double z = entity.getLocation().getZ();
            double xd = Math.abs(x - Math.round(x)), zd = Math.abs(z - Math.round(z));

            // Check for closest blocks, see if they're AIR. If yes, search around, if no, return
            if(xd < zd) {
                modX = Utils.getRoundedMod(x);
                if(block.getRelative(modX, 0, modZ).getType() == Material.AIR){
                    modZ = Utils.getRoundedMod(z);
                    modX = 0;
                    if(block.getRelative(modX, 0, modZ).getType() == Material.AIR){
                        modX = Utils.getRoundedMod(x);
                    }
                }
            }
            else{
                modZ = Utils.getRoundedMod(z);
                if(block.getRelative(modX, 0, modZ).getType() == Material.AIR){
                    modX = Utils.getRoundedMod(x);
                    modZ = 0;
                    if(block.getRelative(modX, 0, modZ).getType() == Material.AIR) {
                        modZ = Utils.getRoundedMod(z);
                    }
                }
            }
        }
        return block.getRelative(modX, 0, modZ);
    }

    protected boolean isBlockClear(Block block){
        Block botBlock = block.getRelative(BlockFace.UP);
        if(botBlock.getType() != Material.AIR) return false;

        Block topBlock = botBlock.getRelative(BlockFace.UP);
        if(topBlock.getType() != Material.AIR) return false;

        return true;
    }
}
