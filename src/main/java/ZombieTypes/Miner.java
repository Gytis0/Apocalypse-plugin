package ZombieTypes;

import Enums.ZombieTypes;
import ZombieSkills.BlockMining;
import ZombieSkills.TargetReachabilityDetection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import Utility.RepeatableTask;

public class Miner extends Regular{

    //Skills
    TargetReachabilityDetection targetReachabilityDetection;
    BlockMining blockMining;


    public Miner(Location tempLoc, LivingEntity target, int level) {
        super(tempLoc, target, level);

        Bukkit.getScheduler().cancelTask(updateTask.getId());
        updateTask = new RepeatableTask(this::update, 0, 1f);

        zombieType = ZombieTypes.MINER;

        targetReachabilityDetection = new TargetReachabilityDetection(zombie, pathfinder, target);
        blockMining = new BlockMining(zombie, pathfinder, world, inventory, activeInventorySlot);
    }

    @Override
    protected void update(){
        cycle++;

        clearIfInvalid();

        // If there are blocks to mine, mine them
        if(blockMining.trigger()){
            blockMining.action();
            return;
        }

        // If the target is reachable, do not bother with the mining logic
        if(!blockMining.isBreaking() && targetReachabilityDetection.trigger() && cycle > 5){
            targetReachabilityDetection.action();

            if(targetReachabilityDetection.getIsTargetReachable()) {
                blockMining.removeNextPathBlock();
                return;
            }
        }
        else return;

        if(blockMining.getNextPathBlock() != null){
            zombie.getPathfinder().moveTo(blockMining.getNextPathBlock().getLocation());
        }
        else{
            zombie.setTarget(target);
        }

        // If the player at the same Y level, mine straight to it
        double heightDifference = target.getLocation().getY() - zombie.getLocation().getY();
        if(Math.abs(heightDifference) <= 0.25){
            zombie.setCustomName(ChatColor.AQUA + "Setting mining STRAIGHT");
            blockMining.mineStraightTo(target);
            return;
        }

        // If the player is above, try to make a path to it
        if (heightDifference > 0.25){
            zombie.setCustomName(ChatColor.AQUA + "Setting mining UP");
            blockMining.mineUpTo(target);
            return;
        }

        // If the player is below, try to make a path to it
        if(heightDifference < -0.25){
            zombie.setCustomName(ChatColor.AQUA + "Setting mining DOWN");
            blockMining.mineDownTo(target);
            return;
        }
    }
}