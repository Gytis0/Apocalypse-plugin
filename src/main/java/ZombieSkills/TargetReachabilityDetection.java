package ZombieSkills;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

public class TargetReachabilityDetection implements ISkill {
    Zombie zombie;
    Pathfinder pathfinder;
    LivingEntity target;

    boolean isTargetReachable = true;

    public TargetReachabilityDetection(Zombie zombie, LivingEntity target) {
        this.zombie = zombie;
        this.pathfinder = zombie.getPathfinder();
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
        // We'd rather say the target is reachable than not, if we cannot figure it out
        Pathfinder.PathResult path = pathfinder.findPath(target);
        Location closestLocation;

        if (path == null) {
            isTargetReachable = true;
            return;
        }
        closestLocation = path.getFinalPoint();

        if (closestLocation == null) {
            isTargetReachable = true;
            return;
        }
        double distance = closestLocation.distance(target.getLocation());
        Bukkit.getLogger().info("Distance to target: " + distance);
        isTargetReachable = distance <= 1.0;
    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }
}
