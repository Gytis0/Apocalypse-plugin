package ZombieTypes;

import Enums.ZombieTypes;
import Utility.RepeatableTask;
import com.destroystokyo.paper.entity.Pathfinder;
import logic.GoalManager;
import logic.Stats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Regular implements Listener {
    protected RepeatableTask updateTask;
    protected LivingEntity target;
    protected final World world;

    protected int level;

    // AI
    protected Pathfinder pathfinder;
    protected GoalManager goalManager;

    List<ItemStack> inventory;
    int activeInventorySlot = 0;

    public Zombie zombie;
    public ZombieTypes zombieType;

    int cycle = 0;

    public Regular(Location tempLoc, LivingEntity target, int level) {
        this.level = level;
        zombieType = ZombieTypes.REGULAR;
        updateTask = new RepeatableTask(this::update, 0, 2f);

        spawnZombie(tempLoc, target);

        world = Bukkit.getWorld("world");
        inventory = new ArrayList<>();
        goalManager = new GoalManager();
    }

    protected void update() {
        clearIfInvalid();
        cycle++;
    }

    protected void equip(int hotkeySlot) {
        if (hotkeySlot <= inventory.size() - 1) {
            zombie.getEquipment().setItemInMainHand(inventory.get(hotkeySlot));
            Bukkit.getLogger().info("Equipped: " + inventory.get(hotkeySlot));
        } else {
            zombie.clearActiveItem();
            Bukkit.getLogger().info("Equipping empty hand.");
        }
    }

    public LivingEntity getCustomTarget() {
        return target;
    }

    // Utils
    protected void spawnZombie(Location loc, LivingEntity target) {
        Stats.addZombieCount();
        loc.add(0, 1, 0);

        zombie = loc.getWorld().spawn(loc, Zombie.class);
        this.target = target;
        zombie.setTarget(target);

        setName(target.getName());
        zombie.setCanPickupItems(false);
        pathfinder = zombie.getPathfinder();
    }

    protected void clearIfInvalid() {
        if (zombie.isDead() || !zombie.isValid()) {
            Bukkit.getLogger().warning("Cancelling task {" + updateTask.getId() + "}, because the zombie is dead or invalid");
            Bukkit.getScheduler().cancelTask(updateTask.getId());

            Stats.reduceZombieCount();
        }
    }

    protected void setName(String name) {
        zombie.setCustomNameVisible(true);
        zombie.setCustomName(ChatColor.GRAY + zombieType.toString().toUpperCase() + ChatColor.WHITE + " | " + name + ChatColor.GOLD + " [Lv. " + level + "]");
    }
}