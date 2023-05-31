package Model.Goals;

import Enums.GoalType;
import Enums.PathType;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class GoalReachTarget extends Goal {
    ReachTarget reachTarget;
    Location start, end;

    PathType pathType;

    public GoalReachTarget(ReachTarget reachTarget, Location start, Location end, PathType pathType) {
        this.reachTarget = reachTarget;
        this.goalType = GoalType.PATH_SETTING;
        this.pathType = pathType;
        this.start = start;
        this.end = end;
    }

    public GoalReachTarget(ReachTarget reachTarget, Location start, Location end, int timeoutTime, PathType pathType) {
        super(GoalType.PATH_SETTING, false, timeoutTime);
        this.reachTarget = reachTarget;
        this.pathType = pathType;
        this.start = start;
        this.end = end;
    }

    public GoalReachTarget(ReachTarget reachTarget, Location start, Location end, boolean mandatory, PathType pathType) {
        super(GoalType.PATH_SETTING, mandatory, -1);
        this.reachTarget = reachTarget;
        this.pathType = pathType;
        this.start = start;
        this.end = end;
    }

    public GoalReachTarget(ReachTarget reachTarget, Location start, Location end, int timeoutTime, boolean mandatory, PathType pathType) {
        super(GoalType.PATH_SETTING, mandatory, timeoutTime);
        this.reachTarget = reachTarget;
        this.pathType = pathType;
        this.start = start;
        this.end = end;
    }

    public PathType getPathType() {
        return pathType;
    }

    @Override
    public void run() {
        Bukkit.getLogger().info("REACHTARGET goal is in action");
        isFailed = !reachTarget.run(start, end);
        isCompleted = true;

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