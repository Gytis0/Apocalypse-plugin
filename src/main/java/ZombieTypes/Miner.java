package ZombieTypes;

import Enums.PathType;
import Enums.ZombieTypes;
import Model.Goals.Goal;
import Model.Goals.GoalMoveTo;
import Model.Goals.GoalReachTarget;
import Utility.RepeatableTask;
import ZombieSkills.BlockMining;
import ZombieSkills.TargetReachabilityDetection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.Queue;

public class Miner extends Regular {

    //Skills
    TargetReachabilityDetection targetReachabilityDetection;
    BlockMining blockMining;

    Block focusBlock;

    // AI
    int pathIndex = 1, maxIndex = 3;
    int pathLevel = 1, maxLevel = 3;
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
        if (!goalManager.areGoalsEmpty()) {
            Object obj = goalManager.doGoals();
            if (obj instanceof Block) focusBlock = (Block) obj;
        }

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
        if (fails.size() == 0) Bukkit.getLogger().warning("No fails occured");
        for (Goal g : fails) {
            Bukkit.getLogger().warning("Failed a goal: " + g.getGoalType());
        }

        for (Goal g : fails) {
            if (g instanceof GoalReachTarget) increaseIndex();
        }

        Bukkit.getLogger().info("Current level / index / cycle: " + pathLevel + " / " + pathIndex + " / " + pathCycle);
        if (focusBlock != null) {
            goalManager.addGoal(new GoalMoveTo(zombie, focusBlock.getLocation()));
            focusBlock = null;
            Bukkit.getLogger().info("Added move to focus block goal");
        } else if (pathIndex == 1 && goalManager.areGoalsEmpty()) {
            goalManager.addGoal(new GoalReachTarget(blockMining.searchForFirstObstacle, zombie, target, pathLevel, pathIndex, PathType.FIRST_OBSTACLE));
            Bukkit.getLogger().info("Added firstObstacle goal");
        } else if (pathIndex == 2 && goalManager.areGoalsEmpty()) {
            goalManager.addGoal(new GoalReachTarget(blockMining.searchForStraightPath, zombie, target, pathLevel, pathIndex, PathType.STRAIGHT_LINE));
            Bukkit.getLogger().info("Added straightLine goal");
        } else if (pathIndex == 3 && goalManager.areGoalsEmpty()) {
            goalManager.addGoal(new GoalReachTarget(blockMining.searchFor4raysUp, zombie, target, pathLevel, pathIndex, PathType.RAYS_UP));
            Bukkit.getLogger().info("Added raysUp goal");
        }


    }

    protected void increaseLevel() {
        if (pathLevel == maxLevel) {
            pathLevel = 1;
            increaseIndex();
        } else pathLevel++;
    }

    protected void increaseIndex() {
        if (pathIndex == maxIndex) {
            pathLevel = 1;
            pathIndex = 1;
            pathCycle++;
        } else pathIndex++;
    }
}