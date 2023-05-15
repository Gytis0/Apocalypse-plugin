package logic;

import Model.Goals.Goal;
import org.bukkit.Bukkit;

import java.util.Queue;

public class GoalManager {
    protected Queue<Goal> goals;

    public boolean doGoals() {
        if (!goals.isEmpty()) {
            Goal goal = goals.peek();
            if (goal.isCompleted()) {
                goals.poll();
                Bukkit.getLogger().info("Removed a goal, because it was done.");
            } else if (goal.isMandatory() && goal.isFailed()) {
                goals.clear();
                Bukkit.getLogger().info("Clearing the queue, because one of the mandatory goals failed.");
            } else if (goal.isFailed()) {
                goals.poll();
                Bukkit.getLogger().info("Removing a goal, because it has failed.");
            } else {
                Bukkit.getLogger().info("Running a goal...");
                goal.run();
            }
            return true;
        } else return false;
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }
}
