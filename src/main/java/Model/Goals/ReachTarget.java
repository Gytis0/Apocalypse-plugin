package Model.Goals;

import org.bukkit.Location;

@FunctionalInterface
public interface ReachTarget {
    boolean run(Location start, Location end);
}
