package Model.Goals;

import Enums.GoalType;

public interface IGoal {
    public void run();

    public boolean isCompleted();

    public boolean isFailed();

    public boolean isMandatory();

    public GoalType getGoalType();
}
