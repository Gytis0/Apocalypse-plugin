package Model.Goals;

import Enums.GoalType;
import Model.StatusAnswer;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GoalStandStill extends Goal {
    Mob entity;
    int timeToStand;

    PotionEffect standStill;

    public GoalStandStill(Mob entity, int timeToStand) {
        this.goalType = GoalType.WAITING;
        this.entity = entity;
        this.timeToStand = timeToStand;
        this.standStill = new PotionEffect(PotionEffectType.SLOW, timeToStand, 255);
    }

    public GoalStandStill(Mob entity, int timeToStand, int timeoutTime) {
        super(GoalType.WAITING, false, timeoutTime);
        this.entity = entity;
        this.timeToStand = timeToStand;
        this.standStill = new PotionEffect(PotionEffectType.SLOW, timeToStand, 255);
    }

    public GoalStandStill(Mob entity, int timeToStand, boolean isMandatory) {
        super(GoalType.WAITING, isMandatory, -1);
        this.entity = entity;
        this.timeToStand = timeToStand;
        this.standStill = new PotionEffect(PotionEffectType.SLOW, timeToStand, 255);
    }

    public GoalStandStill(Mob entity, int timeToStand, int timeoutTime, boolean isMandatory) {
        super(GoalType.WAITING, isMandatory, timeoutTime);
        this.entity = entity;
        this.timeToStand = timeToStand;
        this.standStill = new PotionEffect(PotionEffectType.SLOW, timeToStand, 255);
    }

    @Override
    public void run() {
        entity.addPotionEffect(standStill);

        if (entity.hasPotionEffect(PotionEffectType.SLOW)) {
            status = StatusAnswer.SUCCESS;
        } else {
            status = StatusAnswer.FAILED;
        }
    }

    @Override
    public boolean isMandatory() {
        return isMandatory;
    }

    @Override
    public Object getAnswer() {
        return null;
    }
}
