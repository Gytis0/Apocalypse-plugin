package Model.Goals;

import org.bukkit.entity.LivingEntity;

@FunctionalInterface
public interface ReachTarget {
    Object run(LivingEntity origin, LivingEntity target, int level, int index);
}
