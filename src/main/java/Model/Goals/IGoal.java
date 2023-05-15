package Model.Goals;

public interface IGoal {
    public void run();

    public boolean isCompleted();

    public boolean isFailed();

    public boolean isMandatory();
}
