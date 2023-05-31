package Model.Goals;

import org.bukkit.Location;

@FunctionalInterface
public interface BuildPath {
    boolean run(Location start, Location end);
}
