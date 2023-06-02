package Model.Goals;

import Enums.GoalType;
import Model.StatusAnswer;
import ZombieSkills.CustomPathSearch;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        if (!CustomPathSearch.isLocationReachable(entity, locationGoal)) {
            //Bukkit.getLogger().warning("Location is not reachable.");
            status = StatusAnswer.FAILED;
            return;
        }

        Bukkit.getLogger().info("MOVING goal to " + locationGoal + " is in action");
        //Bukkit.getLogger().info("The distance is: " + (entity.getLocation().distance(locationGoal)));
        if (entity.getLocation().distance(locationGoal) < 1) status = StatusAnswer.SUCCESS;
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

    @Override
    public boolean isMandatory() {
        return isMandatory;
    }

    @Override
    public Object getAnswer() {
        return answer;
    }
}