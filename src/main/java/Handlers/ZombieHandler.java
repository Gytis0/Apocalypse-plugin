package Handlers;

import apocalypse.apocalypse.Apocalypse;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ZombieHandler implements Listener {
    World world;

    public ZombieHandler(Apocalypse plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        world = Bukkit.getWorld("world");
    }

    @EventHandler
    public void onZombieMove(EntityMoveEvent event) {
        if (world.getGameTime() % 5 == 0) {
            if (event.getEntity().getType() == EntityType.ZOMBIE) {
                Zombie zombie = (Zombie) event.getEntity();
                if (zombie.getTarget() != null && zombie.isInWater()) {
                    zombie.stopDrowning();

                    Vector direction = zombie.getTarget().getLocation().toVector().subtract(zombie.getLocation().toVector()).normalize();
                    direction.setX(direction.getX() * 0.1);
                    direction.setY(direction.getY() * 0.3);
                    direction.setZ(direction.getZ() * 0.1);


                    zombie.setVelocity(zombie.getVelocity().add(direction));
                }
            }
        }
    }
}