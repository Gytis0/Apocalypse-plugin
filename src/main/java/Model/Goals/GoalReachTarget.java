package Model.Goals;

import Enums.GoalType;
import Enums.PathType;
import Model.ReachTarget;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class GoalReachTarget extends Goal {
    ReachTarget reachTarget;
    LivingEntity target;
    int range, obstaclesToIgnore;

    PathType pathType;

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity target, int range, int obstaclesToIgnore, PathType pathType) {
        this.reachTarget = reachTarget;
        this.target = target;
        this.goalType = GoalType.PATH_SETTING;
        this.pathType = pathType;

        this.range = range;
        this.obstaclesToIgnore = obstaclesToIgnore;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity target, int range, int obstaclesToIgnore, int timeoutTime, PathType pathType) {
        super(GoalType.PATH_SETTING, false, timeoutTime);
        this.reachTarget = reachTarget;
        this.target = target;
        this.pathType = pathType;

        this.range = range;
        this.obstaclesToIgnore = obstaclesToIgnore;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity target, int range, int obstaclesToIgnore, boolean mandatory, PathType pathType) {
        super(GoalType.PATH_SETTING, mandatory, -1);
        this.reachTarget = reachTarget;
        this.target = target;
        this.pathType = pathType;

        this.range = range;
        this.obstaclesToIgnore = obstaclesToIgnore;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity target, int range, int obstaclesToIgnore, int timeoutTime, boolean mandatory, PathType pathType) {
        super(GoalType.PATH_SETTING, mandatory, timeoutTime);
        this.reachTarget = reachTarget;
        this.target = target;
        this.pathType = pathType;

        this.range = range;
        this.obstaclesToIgnore = obstaclesToIgnore;
    }

    public PathType getPathType() {
        return pathType;
    }

    @Override
    public void run() {
        Bukkit.getLogger().info("REACHTARGET goal is in action");
        reachTarget.run(target, range, obstaclesToIgnore);
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