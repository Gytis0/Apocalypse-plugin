package ZombieTypes;

import Enums.GoalTypes;
import Enums.ZombieTypes;
import Model.Goal;
import Utility.RepeatableTask;
import ZombieSkills.BlockBuilding;
import ZombieSkills.TargetReachabilityDetection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;


public class Builder extends Regular{

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
    protected void update(){
        cycle++;

        clearIfInvalid();

        // If there are goals to do, do them first
        if(!goals.isEmpty()){
            Goal goal = goals.peek();
            if(goal.isCompleted() || goal.isTimedOut()){
                goals.poll();
                Bukkit.getLogger().info("Removed a goal, because it was done.");
            }
            else{
                Bukkit.getLogger().info("Running a goal...");
                goal.run();
            }
            return;
        }

        // If there is a path to build, build it
        if(blockBuilding.trigger()){
            blockBuilding.action();
            return;
        }

        // If the target is reachable, do not bother with the building logic
        if(targetReachabilityDetection.trigger() && cycle > 5){
            Bukkit.getLogger().info("Checking if the player is reachable...");
            targetReachabilityDetection.action();

            if(targetReachabilityDetection.getIsTargetReachable()) {
                Bukkit.getLogger().info("Player is reachable.");
                return;
            }
        }
        else return;

        Bukkit.getLogger().info("Player is not reachable");
        goals.add(new Goal(GoalTypes.MOVE_TO, zombie, blockBuilding.getFirstObstacleTo(zombie, target).get().getLocation()));
        goals.add(new Goal(GoalTypes.RUN_FUNCTION, blockBuilding.setPathToFirstObstacle, target));
    }
}
