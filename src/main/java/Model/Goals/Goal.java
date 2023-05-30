package Model.Goals;

import Enums.GoalType;

public abstract class Goal implements IGoal {
    boolean isCompleted = false;
    boolean isFailed = false;
    boolean isMandatory = false;
    int lifetime = 0;
    int timeoutTime = 30;
    GoalType goalType;

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
    public boolean isCompleted() {
        return false;
    }

    @Override
    public boolean isFailed() {
        return false;
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
