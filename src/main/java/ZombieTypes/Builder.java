package ZombieTypes;

import Enums.PathType;
import Enums.ZombieTypes;
import Model.Goals.Goal;
import Model.Goals.GoalReachTarget;
import Utility.RepeatableTask;
import ZombieSkills.BlockBuilding;
import ZombieSkills.TargetReachabilityDetection;
import apocalypse.apocalypse.Apocalypse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Queue;


public class Builder extends Regular {
    // Skills
    TargetReachabilityDetection targetReachabilityDetection;
    BlockBuilding blockBuilding;

    // AI
    int pathIndex = 1, maxIndex = 1;
    int pathLevel = 1, maxLevel = 5;
    int pathCycle = 0;

    public Builder(Apocalypse apocalypse, Location tempLoc, LivingEntity target, int level) {
        super(apocalypse, tempLoc, target, level);

        Bukkit.getScheduler().cancelTask(updateTask.getId());
        updateTask = new RepeatableTask(this::update, 0, 1f);

        zombieType = ZombieTypes.BUILDER;
        setName();

        blockBuilding = new BlockBuilding(zombie, world, level, inventory, activeInventorySlot);
        targetReachabilityDetection = new TargetReachabilityDetection(zombie, target);
    }

    @Override
    protected void update() {
        cycle++;

        clearIfInvalid();

        // If the target is dead, find a new one
        if (!isThereAtargetToKill()) return;

        // If there are goals to do, do them first
        if (!goalManager.areGoalsEmpty()) {
            Object obj = goalManager.doGoals();
            return;
        }
        // If there is a path to build, build it
        if (blockBuilding.trigger()) {
            blockBuilding.action();
            return;
        }

        // If the target is reachable, do not bother with the building logic
        if (targetReachabilityDetection.trigger() && cycle > 5) {
            //Bukkit.getLogger().info("Checking if the player is reachable...");
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
        GoalReachTarget goal;

        for (Goal g : fails) {
            try {
                goal = (GoalReachTarget) g;
            } catch (ClassCastException e) {
                continue;
            }

            if (goal.getPathType() == PathType.NEAREST_LEDGE) {
                increaseLevel();
            }
        }

        Bukkit.getLogger().info("Current level / index / cycle: " + pathLevel + " / " + pathIndex + " / " + pathCycle);

        if (pathIndex == 1) {
            goalManager.addGoal(new GoalReachTarget(blockBuilding.setPathToTargetLedge, zombie, currentTarget, pathLevel, pathIndex, PathType.NEAREST_LEDGE));
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

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (!playerIsReachable) event.setCancelled(true);
    }
}