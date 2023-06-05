package Model.Goals;

import Enums.GoalType;
import Model.StatusAnswer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;

public class GoalMoveFree extends Goal {

    Mob entity;

    public GoalMoveFree(Mob entity) {
        super(GoalType.FREE, false, -1);
        this.entity = entity;
    }

    public GoalMoveFree(int timeoutTime, Mob entity) {
        super(GoalType.FREE, false, timeoutTime);
        this.entity = entity;
    }

    public GoalMoveFree(boolean isMandatory, Mob entity) {
        super(GoalType.FREE, isMandatory, -1);
        this.entity = entity;
    }

    public GoalMoveFree(boolean isMandatory, int timeoutTime, Mob entity) {
        super(GoalType.FREE, isMandatory, timeoutTime);
        this.entity = entity;
    }

    @Override
    public void run() {
        entity.setAI(true);
        status = StatusAnswer.SUCCESS;
        Bukkit.getLogger().info("MOVING FREELY...");
    }
}
