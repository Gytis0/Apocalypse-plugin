package Model.Goals;

import org.bukkit.entity.LivingEntity;

@FunctionalInterface
public interface ReachTarget {
    boolean run(LivingEntity origin, LivingEntity target, int level, int index);
}
