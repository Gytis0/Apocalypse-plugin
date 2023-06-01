package ZombieTypes;

import Enums.PathType;
import Enums.ZombieTypes;
import Model.Goals.Goal;
import Model.Goals.GoalReachTarget;
import Utility.RepeatableTask;
import ZombieSkills.BlockMining;
import ZombieSkills.TargetReachabilityDetection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.Queue;

public class Miner extends Regular {

    //Skills
    TargetReachabilityDetection targetReachabilityDetection;
    BlockMining blockMining;

    // AI
    int pathIndex = 0;
    int pathLevel = 1;
    int pathCycle = 0;

    public Miner(Location tempLoc, LivingEntity target, int level) {
        super(tempLoc, target, level);

        Bukkit.getScheduler().cancelTask(updateTask.getId());
        updateTask = new RepeatableTask(this::update, 0, 1f);

        zombieType = ZombieTypes.MINER;

        targetReachabilityDetection = new TargetReachabilityDetection(zombie, target);
        blockMining = new BlockMining(zombie, world, level, inventory, activeInventorySlot);
    }

    @Override
    protected void update() {
        cycle++;

        clearIfInvalid();

        // If there are goals to do, do them first
        if (goalManager.doGoals()) return;

        // If there are blocks to mine, mine them
        if (blockMining.trigger()) {
            blockMining.action();
            return;
        }

        // If the target is reachable, do not bother with the mining logic
        if (!blockMining.isBreaking() && targetReachabilityDetection.trigger() && cycle > 5) {
            targetReachabilityDetection.action();

            if (targetReachabilityDetection.getIsTargetReachable()) {
                Bukkit.getLogger().info("Player IS reachable.");
                return;
            }
        } else return;

        Bukkit.getLogger().info("Player IS NOT reachable");

        Queue<Goal> fails = goalManager.getMostRecentFails();
        GoalReachTarget goal;

        for (Goal g : fails) {
            try {
                goal = (GoalReachTarget) g;
            } catch (ClassCastException e) {
                continue;
            }

            if (goal.getPathType() == PathType.NEAREST_LEDGE) {
                if (pathLevel < 3) pathLevel++;
                else {
                    pathLevel = 1;
                    pathIndex++;
                }
            } else if (goal.getPathType() == PathType.FIRST_OBSTACLE) {
                pathLevel = 1;
                pathIndex = 0;
                cycle++;
            }
        }

        if (pathIndex == 0) {

        } else if (pathIndex == 1) {
            // to first obstacle
        }
    }
}