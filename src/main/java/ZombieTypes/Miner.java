package ZombieTypes;

import Enums.PathType;
import Enums.ZombieTypes;
import Model.Goals.*;
import Utility.RepeatableTask;
import ZombieSkills.BlockMining;
import ZombieSkills.TargetReachabilityDetection;
import apocalypse.apocalypse.Apocalypse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Miner extends Regular {

    //Skills
    TargetReachabilityDetection targetReachabilityDetection;
    BlockMining blockMining;

    // AI
    int pathIndex = 1, maxIndex = 3;
    int pathLevel = 1, maxLevel = 3;
    int pathCycle = 0;

    // Broadcast
    List<Block> path = new ArrayList<>();
    boolean movedToFront = true;

    public Miner(Apocalypse apocalypse, Location tempLoc, LivingEntity target, int level) {
        super(apocalypse, tempLoc, target, level);
        Bukkit.getScheduler().cancelTask(updateTask.getId());
        updateTask = new RepeatableTask(this::update, 0, 1f);

        zombieType = ZombieTypes.MINER;
        setName();

        targetReachabilityDetection = new TargetReachabilityDetection(zombie, target);
        blockMining = new BlockMining(zombie, world, level, inventory, activeInventorySlot, path);
    }

    @Override
    protected void update() {
        cycle++;

        clearIfInvalid();

        // If there are goals to do, do them first
        if (!goalManager.areGoalsEmpty()) {
            Object obj = goalManager.doGoals();
            if (obj instanceof Block) {
                path.add(((Block) obj).getRelative(BlockFace.DOWN));
                movedToFront = false;
            }
        }

        // If there are blocks to mine, mine them
        if (blockMining.trigger()) {
            //Bukkit.getLogger().info("There are blocks to mine");
            blockMining.action();
            return;
        }

        // If the target is reachable, do not bother with the mining logic
        if (!blockMining.isBreaking() && targetReachabilityDetection.trigger() && cycle > 5) {
            targetReachabilityDetection.action();

            if (targetReachabilityDetection.getIsTargetReachable()) {
                Bukkit.getLogger().info("Player IS reachable.");
                playerIsReachable = true;
                return;
            }
        } else return;

        Bukkit.getLogger().info("Player IS NOT reachable");
        playerIsReachable = false;

        Queue<Goal> fails = goalManager.getMostRecentFails();

        for (Goal g : fails) {
            if (g instanceof GoalReachTarget) increaseIndex();
        }

        //Bukkit.getLogger().info("Current level / index / cycle: " + pathLevel + " / " + pathIndex + " / " + pathCycle);
        if (!path.isEmpty() && !movedToFront) {
            goalManager.addGoal(new GoalMoveTo(zombie, path.get(path.size() - 1).getLocation()));
            goalManager.addGoal(new GoalStandStill(zombie));
            movedToFront = true;
        } else if (pathIndex == 1 && goalManager.areGoalsEmpty()) {
            goalManager.addGoal(new GoalReachTarget(blockMining.searchForFirstObstacle, zombie, target, pathLevel, pathIndex, PathType.FIRST_OBSTACLE));
            goalManager.addGoal(new GoalMoveFree(zombie));
            //Bukkit.getLogger().info("Added firstObstacle goal");
        } else if (pathIndex == 2 && goalManager.areGoalsEmpty()) {
            if (isTargetRelativelyTheSameY(zombie, target)) {
                //Bukkit.getLogger().info("Added straightLine goal");
                goalManager.addGoal(new GoalReachTarget(blockMining.searchForStraightPath, zombie, target, pathLevel, pathIndex, PathType.STRAIGHT_LINE));
                goalManager.addGoal(new GoalMoveFree(zombie));
            } else increaseIndex();
        } else if (pathIndex == 3 && goalManager.areGoalsEmpty()) {
            if (target.getLocation().getY() > zombie.getLocation().getY()) {
                goalManager.addGoal(new GoalReachTarget(blockMining.carveUp, zombie, target, pathLevel, pathIndex, PathType.RAYS_UP));
                goalManager.addGoal(new GoalMoveFree(zombie));
                //Bukkit.getLogger().info("Added raysUp goal");
            } else {
                goalManager.addGoal(new GoalReachTarget(blockMining.carveDown, zombie, target, pathLevel, pathIndex, PathType.RAYS_DOWN));
                goalManager.addGoal(new GoalMoveFree(zombie));
                //Bukkit.getLogger().info("Added raysDown goal");
            }
        }
    }

    protected void increaseLevel() {
        if (pathLevel == maxLevel) {
            increaseIndex();
        } else pathLevel++;
    }

    protected void increaseIndex() {
        if (pathIndex == maxIndex) {
            increaseCycle();
        } else {
            pathLevel = 1;
            pathIndex++;
        }
    }

    protected void increaseCycle() {
        pathLevel = 1;
        pathIndex = 1;
        pathCycle++;
    }

    // Later change this. Make it to see if the angle between them is steep or not.
    // If it's not steep, do straight line. If it is steep, do the advanced algos
    protected boolean isTargetRelativelyTheSameY(LivingEntity origin, LivingEntity target) {
        //Bukkit.getLogger().info("Difference is: " + Math.abs(target.getLocation().getY() - origin.getLocation().getY()));
        return Math.abs(target.getLocation().getY() - origin.getLocation().getY()) < 2;
    }
}