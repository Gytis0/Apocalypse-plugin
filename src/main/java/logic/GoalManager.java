package logic;

import Model.Goals.Goal;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.Queue;

public class GoalManager {
    protected Queue<Goal> goals;
    protected Queue<Goal> recentFailedGoals;

    public GoalManager() {
        goals = new ArrayDeque<>();
        recentFailedGoals = new ArrayDeque<>();
    }

    public boolean doGoals() {
        if (!goals.isEmpty()) {
            Goal goal = goals.peek();
            if (goal.isCompleted()) {
                goals.poll();
                Bukkit.getLogger().info("Removed a goal, because it was done.");
            } else if (goal.isMandatory() && goal.isFailed()) {
                goals.clear();
                recentFailedGoals.add(goal);
                Bukkit.getLogger().info("Clearing the queue, because one of the mandatory goals failed.");
            } else if (goal.isFailed()) {
                goals.poll();
                recentFailedGoals.add(goal);
                Bukkit.getLogger().info("Removing a goal, because it has failed.");
            } else {
                Bukkit.getLogger().info("Running a goal...");
                goal.run();
            }

            if (recentFailedGoals.size() > 5) recentFailedGoals.poll();

            return true;
        } else return false;
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public Queue<Goal> getMostRecentFails() {
        Queue<Goal> result = recentFailedGoals;
        recentFailedGoals.clear();
        return recentFailedGoals;
    }
}
