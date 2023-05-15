package Model.Goals;

import Model.ReachTarget;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class GoalReachTarget extends Goal {
    boolean isCompleted = false;
    boolean isFailed = false;
    boolean isMandatory = false;
    int lifetime = 0;
    int timeoutTime;

    ReachTarget reachTarget;
    LivingEntity target;

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity target) {
        this.reachTarget = reachTarget;
        this.target = target;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity target, int timeoutTime) {
        super(false, timeoutTime);
        this.reachTarget = reachTarget;
        this.target = target;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity target, boolean mandatory) {
        super(mandatory, -1);
        this.reachTarget = reachTarget;
        this.target = target;
    }

    public GoalReachTarget(ReachTarget reachTarget, LivingEntity target, int timeoutTime, boolean mandatory) {
        super(mandatory, timeoutTime);
        this.reachTarget = reachTarget;
        this.target = target;
    }


    @Override
    public void run() {
        Bukkit.getLogger().info("REACHTARGET goal is in action");
        reachTarget.run(target);
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