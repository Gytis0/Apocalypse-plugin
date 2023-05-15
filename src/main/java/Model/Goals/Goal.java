package Model.Goals;

public abstract class Goal implements IGoal {
    boolean isCompleted = false;
    boolean isFailed = false;
    boolean isMandatory = false;
    int lifetime = 0;
    int timeoutTime = 30;

    public Goal() {
    }

    public Goal(boolean isMandatory, int timeoutTime) {
        this.isMandatory = isMandatory;
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
}
