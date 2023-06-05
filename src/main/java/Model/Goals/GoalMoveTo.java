package Model.Goals;

import Enums.GoalType;
import Model.StatusAnswer;
import Utility.Pathing;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;

public class GoalMoveTo extends Goal {
    Mob entity;
    Location locationGoal;

    public GoalMoveTo(Mob entity, Location locationGoal) {
        this.entity = entity;
        this.locationGoal = locationGoal;
        this.goalType = GoalType.MOVEMENT;
    }

    public GoalMoveTo(Mob entity, Location locationGoal, int timeoutTime) {
        super(GoalType.MOVEMENT, false, timeoutTime);
        this.entity = entity;
        this.locationGoal = locationGoal;
    }

    public GoalMoveTo(Mob entity, Location locationGoal, boolean mandatory) {
        super(GoalType.MOVEMENT, mandatory, -1);
        this.entity = entity;
        this.locationGoal = locationGoal;
    }

    public GoalMoveTo(Mob entity, Location locationGoal, int timeoutTime, boolean mandatory) {
        super(GoalType.MOVEMENT, mandatory, timeoutTime);
        this.entity = entity;
        this.locationGoal = locationGoal;
    }

    @Override
    public void run() {
        if (!Pathing.isLocationReachable(entity, locationGoal)) {
            //Bukkit.getLogger().warning("Location is not reachable.");
            status = StatusAnswer.FAILED;
            return;
        }
        Block locationBlock = locationGoal.getBlock();
        Block entityBlock = Pathing.findEntityFloorBlock(entity);

        Bukkit.getLogger().info("MOVING to " + locationGoal);
        Bukkit.getLogger().info("entity distance " + (entity.getLocation().distance(locationGoal)));
        Bukkit.getLogger().info("block distance " + (entityBlock.getLocation().distance(locationBlock.getLocation())));

        Bukkit.getLogger().info("entity block " + entityBlock.getLocation());
        Bukkit.getLogger().info("location block " + locationBlock.getLocation());

        if (entityBlock.equals(locationBlock)) status = StatusAnswer.SUCCESS;
        else {
            entity.getPathfinder().moveTo(locationGoal);
            status = StatusAnswer.RUNNING;
        }

        if (lifetime > timeoutTime) {
            Bukkit.getLogger().info("This goal is timed out");
            status = StatusAnswer.TIMED_OUT;
            return;
        }
        lifetime++;
    }
}