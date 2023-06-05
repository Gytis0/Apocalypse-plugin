package Model.Goals;

import Enums.GoalType;
import Enums.PathType;
import Model.StatusAnswer;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class GoalReachTarget extends Goal {
    ReachTarget reachTarget;
    int level, index;
    LivingEntity origin, target;
    PathType pathType;

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity origin, LivingEntity target, int level, int index, PathType pathType) {
        this.reachTarget = reachTarget;
        this.goalType = GoalType.PATH_SETTING;
        this.pathType = pathType;
        this.origin = origin;
        this.target = target;
        this.level = level;
        this.index = index;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity origin, LivingEntity target, int level, int index, int timeoutTime, PathType pathType) {
        super(GoalType.PATH_SETTING, false, timeoutTime);
        this.reachTarget = reachTarget;
        this.pathType = pathType;
        this.origin = origin;
        this.target = target;
        this.level = level;
        this.index = index;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity origin, LivingEntity target, int level, int index, boolean mandatory, PathType pathType) {
        super(GoalType.PATH_SETTING, mandatory, -1);
        this.reachTarget = reachTarget;
        this.pathType = pathType;
        this.origin = origin;
        this.target = target;
        this.level = level;
        this.index = index;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity origin, LivingEntity target, int level, int index, int timeoutTime, boolean mandatory, PathType pathType) {
        super(GoalType.PATH_SETTING, mandatory, timeoutTime);
        this.reachTarget = reachTarget;
        this.pathType = pathType;
        this.origin = origin;
        this.target = target;
        this.level = level;
        this.index = index;
    }

    public PathType getPathType() {
        return pathType;
    }

    @Override
    public void run() {
        Object result = reachTarget.run(origin, target, level, index);

        if (result == null) status = StatusAnswer.FAILED;
        else {
            answer = result;
            status = StatusAnswer.SUCCESS;
        }

        if (lifetime > timeoutTime) {
            Bukkit.getLogger().info("This goal is timed out");
            status = StatusAnswer.TIMED_OUT;
            return;
        }
        lifetime++;
    }
}