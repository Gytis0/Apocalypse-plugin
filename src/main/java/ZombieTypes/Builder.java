package ZombieTypes;

import Enums.PathType;
import Enums.ZombieTypes;
import Model.Goals.Goal;
import Model.Goals.GoalMoveTo;
import Model.Goals.GoalReachTarget;
import Utility.RepeatableTask;
import ZombieSkills.BlockBuilding;
import ZombieSkills.CustomPathSearch;
import ZombieSkills.TargetReachabilityDetection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;


public class Builder extends Regular {

    // Skills
    BlockBuilding blockBuilding;
    TargetReachabilityDetection targetReachabilityDetection;

    // AI
    int pathIndex = 0;
    int pathLevel = 1;
    int pathCycle = 0;

    public Builder(Location tempLoc, LivingEntity target, int level) {
        super(tempLoc, target, level);

        Bukkit.getScheduler().cancelTask(updateTask.getId());
        updateTask = new RepeatableTask(this::update, 0, 1f);

        zombieType = ZombieTypes.BUILDER;

        blockBuilding = new BlockBuilding(zombie, pathfinder, world, inventory, activeInventorySlot);
        targetReachabilityDetection = new TargetReachabilityDetection(zombie, pathfinder, target);
    }

    @Override
    protected void update() {
        cycle++;

        clearIfInvalid();

        // If there are goals to do, do them first
        if (goalManager.doGoals()) return;

        // If there is a path to build, build it
        if (blockBuilding.trigger()) {
            blockBuilding.action();
            return;
        }

        // If the target is reachable, do not bother with the building logic
        if (targetReachabilityDetection.trigger() && cycle > 5) {
            Bukkit.getLogger().info("Checking if the player is reachable...");
            targetReachabilityDetection.action();

            if (targetReachabilityDetection.getIsTargetReachable()) {
                Bukkit.getLogger().info("Player is reachable.");
                return;
            }
        } else return;

        Bukkit.getLogger().info("Player is not reachable");

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
            List<Block> ledges = CustomPathSearch.getNearestLedges(target, 10 * pathLevel, pathCycle);
            Block startPos = null, endPos = null;
            Collections.shuffle(ledges, new Random(System.currentTimeMillis()));

            for (Block b : ledges) {
                startPos = CustomPathSearch.findLocationForPathBuilding(b.getLocation(), zombie);
                if (startPos != null) {
                    endPos = b;
                    break;
                }
            }

            goalManager.addGoal(new GoalMoveTo(zombie, startPos.getLocation(), true));
            goalManager.addGoal(new GoalReachTarget(blockBuilding.setStraightPathToBlock, startPos.getLocation(), endPos.getLocation(), PathType.NEAREST_LEDGE));
        } else if (pathIndex == 1) {
            // to first obstacle
        }
    }
}