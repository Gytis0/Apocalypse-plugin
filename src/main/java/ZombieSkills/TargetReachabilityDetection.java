package ZombieSkills;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import java.util.Objects;

public class TargetReachabilityDetection implements Skill {
    Zombie zombie;
    Pathfinder pathfinder;
    LivingEntity target;

    boolean isTargetReachable = true;

    public TargetReachabilityDetection(Zombie zombie, Pathfinder pathfinder, LivingEntity target) {
        this.zombie = zombie;
        this.pathfinder = pathfinder;
        this.target = target;
    }

    public boolean getIsTargetReachable() {
        return isTargetReachable;
    }

    // zombie move somewhere around 0.06 - 0.088 worth of velocity
    // gravity for zombies is about -0.784
    @Override
    public boolean trigger() {
        double speed = 0;
        speed += Math.abs(zombie.getVelocity().getX());
        speed += Math.abs(zombie.getVelocity().getZ());

        if (speed < 0.055) {
            return true;
        } else return false;
    }

    @Override
    public void action() {
        Location closestLocation = pathfinder.findPath(target).getFinalPoint();
        Objects.requireNonNull(closestLocation, "Target reachability detection could not find path");

        if (closestLocation.distance(target.getLocation()) <= 1.5) {
            isTargetReachable = true;
        } else {
            isTargetReachable = false;
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }
}
