package Model.Goals;

import Model.StatusAnswer;
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

    public Object doGoals() {
        if (!goals.isEmpty()) {
            if (recentFailedGoals.size() > 5) recentFailedGoals.poll();
            Goal goal = goals.peek();

            Bukkit.getLogger().info("Running a goal...");
            goal.run();

            if (goal.status == StatusAnswer.SUCCESS) {
                Bukkit.getLogger().info("Removed a goal, because it was done.");
                goals.poll();
                return goal.getAnswer();
            } else if (goal.status == StatusAnswer.RUNNING) {
                Bukkit.getLogger().info("Goal is still running...");
            } else if (goal.isMandatory() && goal.status == StatusAnswer.FAILED) {
                Bukkit.getLogger().info("Clearing the queue, because one of the mandatory goals failed.");
                goals.clear();
                recentFailedGoals.add(goal);
                return goal.getAnswer();
            } else if (goal.status == StatusAnswer.FAILED) {
                Bukkit.getLogger().warning("Removing a goal, because it has failed.");
                goals.poll();
                recentFailedGoals.add(goal);
                return goal.getAnswer();
            }
        }
        return null;
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public Queue<Goal> getMostRecentFails() {
        Bukkit.getLogger().info("Returning all fails: " + recentFailedGoals);
        Queue<Goal> result = new ArrayDeque<>(recentFailedGoals);
        recentFailedGoals.clear();
        return result;
    }

    public boolean areGoalsEmpty() {
        return goals.size() == 0;
    }
}
