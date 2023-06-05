package Model.Goals;

import Enums.GoalType;
import Model.StatusAnswer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;

public class GoalStandStill extends Goal {
    Mob entity;

    public GoalStandStill(Mob entity) {
        this.goalType = GoalType.WAITING;
        this.entity = entity;
    }

    public GoalStandStill(Mob entity, int timeToStand, int timeoutTime) {
        super(GoalType.WAITING, false, timeoutTime);
        this.entity = entity;
    }

    public GoalStandStill(Mob entity, int timeToStand, boolean isMandatory) {
        super(GoalType.WAITING, isMandatory, -1);
        this.entity = entity;
    }

    public GoalStandStill(Mob entity, int timeToStand, int timeoutTime, boolean isMandatory) {
        super(GoalType.WAITING, isMandatory, timeoutTime);
        this.entity = entity;
    }

    @Override
    public void run() {
        entity.setAI(false);
        status = StatusAnswer.SUCCESS;
        Bukkit.getLogger().info("STANDING STILL...");
    }
}
