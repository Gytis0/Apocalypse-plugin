package Model;

import org.bukkit.entity.LivingEntity;

@FunctionalInterface
public interface ReachTarget {
    void run(LivingEntity target, int range, int obstaclesToIgnore);
}
