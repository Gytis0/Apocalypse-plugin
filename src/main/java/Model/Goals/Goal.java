package Model.Goals;

import Enums.GoalType;
import Model.StatusAnswer;

public abstract class Goal implements IGoal {
    boolean isMandatory = false;
    int lifetime = 0;
    int timeoutTime = 15;
    GoalType goalType;
    Object answer;
    StatusAnswer status = null;

    public Goal() {
    }

    public Goal(GoalType goalType, boolean isMandatory, int timeoutTime) {
        this.isMandatory = isMandatory;
        this.goalType = goalType;
        if (timeoutTime != -1) this.timeoutTime = timeoutTime;
    }

    @Override
    public void run() {

    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public GoalType getGoalType() {
        return goalType;
    }


}
