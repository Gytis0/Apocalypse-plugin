package Model.Goals;

import Enums.GoalType;

public interface IGoal {
    public void run();

    public boolean isMandatory();

    public GoalType getGoalType();

    public Object getAnswer();
}
