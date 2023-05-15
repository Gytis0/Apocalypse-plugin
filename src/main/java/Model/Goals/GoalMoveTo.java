package Model.Goals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Mob;

public class GoalMoveTo extends Goal {
    boolean isCompleted = false;
    boolean isFailed = false;
    boolean isMandatory = false;
    int lifetime = 0;
    int timeoutTime;

    Mob entity;
    Location locationGoal;

    public GoalMoveTo(Mob entity, Location locationGoal) {
        this.entity = entity;
        this.locationGoal = entity.getPathfinder().findPath(locationGoal).getFinalPoint();
    }

    public GoalMoveTo(Mob entity, Location locationGoal, int timeoutTime) {
        super(false, timeoutTime);
        this.entity = entity;
        this.locationGoal = entity.getPathfinder().findPath(locationGoal).getFinalPoint();
    }

    public GoalMoveTo(Mob entity, Location locationGoal, boolean mandatory) {
        super(mandatory, -1);
        this.entity = entity;
        this.locationGoal = entity.getPathfinder().findPath(locationGoal).getFinalPoint();
    }

    public GoalMoveTo(Mob entity, Location locationGoal, int timeoutTime, boolean mandatory) {
        super(mandatory, timeoutTime);
        this.entity = entity;
        this.locationGoal = entity.getPathfinder().findPath(locationGoal).getFinalPoint();
    }

    @Override
    public void run() {
        Bukkit.getLogger().info("MOVING goal is in action");

        if (entity.getLocation().distance(locationGoal) < 1) isCompleted = true;
        else entity.getPathfinder().moveTo(locationGoal);

        if (lifetime > timeoutTime) {
            Bukkit.getLogger().info("This goal is timed out");
            isFailed = true;
            return;
        }
        lifetime++;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public boolean isFailed() {
        return isFailed;
    }

    @Override
    public boolean isMandatory() {
        return isMandatory;
    }
}