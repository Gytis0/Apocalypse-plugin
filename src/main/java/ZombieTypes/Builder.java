package ZombieTypes;

import Enums.ZombieTypes;
import Model.Goals.GoalMoveTo;
import Model.Goals.GoalReachTarget;
import Utility.RepeatableTask;
import ZombieSkills.BlockBuilding;
import ZombieSkills.TargetReachabilityDetection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;


public class Builder extends Regular {

    // Skills
    BlockBuilding blockBuilding;
    TargetReachabilityDetection targetReachabilityDetection;

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
        goalManager.addGoal(new GoalMoveTo(zombie, blockBuilding.getFirstObstacleTo(zombie, target).getLocation()));
        goalManager.addGoal(new GoalReachTarget(blockBuilding.setPathToFirstObstacle, target, 60));
    }
}