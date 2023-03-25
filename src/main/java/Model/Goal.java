package Model;

import Enums.GoalTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

public class Goal {
    Mob entity;

    GoalTypes goal;
    boolean isCompleted = false;
    boolean timedOut = false;
    int lifetime = 0;

    // Move to
    Location locationGoal;

    // Reach target
    ReachTarget reachTarget;
    LivingEntity target;

    public Goal(GoalTypes goal, ReachTarget reachTarget, LivingEntity target) {
        this.goal = goal;
        this.reachTarget = reachTarget;
        this.target = target;
    }

    public Goal(GoalTypes goal, Mob entity, Location locationGoal) {
        this.goal = goal;
        this.entity = entity;

        this.locationGoal = entity.getPathfinder().findPath(locationGoal).getFinalPoint();
    }

    public void run(){
        if(lifetime > 60) {
            Bukkit.getLogger().info("This goal is timed out");
            timedOut = true;
            return;
        }
        lifetime++;

        if(goal == GoalTypes.MOVE_TO){
            Bukkit.getLogger().info("MOVING goal is in action");
            if(entity.getLocation().distance(locationGoal) < 1){
                isCompleted = true;
            }
            else{
                entity.getPathfinder().moveTo(locationGoal);
            }
        }
        else if(goal == GoalTypes.RUN_FUNCTION) {
            Bukkit.getLogger().info("FUNCTION goal is in action");
            reachTarget.run(target);
            isCompleted = true;
        }
    }


    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Location getLocationGoal() {
        return locationGoal;
    }

    public boolean isTimedOut() {
        return timedOut;
    }
}
