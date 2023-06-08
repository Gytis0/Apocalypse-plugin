package ZombieTypes;

import Enums.ZombieTypes;
import Model.Goals.GoalManager;
import Utility.RepeatableTask;
import apocalypse.apocalypse.Apocalypse;
import logic.Stats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Regular implements Listener {
    protected RepeatableTask updateTask;
    protected LivingEntity originalTarget;
    protected LivingEntity currentTarget;
    protected final World world;

    protected int level;

    // AI
    protected GoalManager goalManager;

    List<ItemStack> inventory;
    int activeInventorySlot = 0;

    public Zombie zombie;
    public ZombieTypes zombieType;

    boolean playerIsReachable = true;

    int cycle = 0;

    public Regular(Apocalypse apocalypse, Location tempLoc, LivingEntity target, int level) {
        Bukkit.getPluginManager().registerEvents(this, apocalypse);

        this.level = level;
        this.originalTarget = target;
        this.currentTarget = target;

        zombieType = ZombieTypes.REGULAR;

        updateTask = new RepeatableTask(this::update, 0, 5f);

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
            //Bukkit.getLogger().info("Equipped: " + inventory.get(hotkeySlot));
        } else {
            zombie.clearActiveItem();
            //Bukkit.getLogger().info("Equipping empty hand.");
        }
    }

    public LivingEntity getOriginalTarget() {
        return originalTarget;
    }

    // Utils
    protected void spawnZombie(Location loc, LivingEntity target) {
        Stats.addZombieCount();
        loc.add(0, 1, 0);

        zombie = loc.getWorld().spawn(loc, Zombie.class);
        this.originalTarget = target;
        zombie.setTarget(target);

        setName();
        zombie.setCanPickupItems(false);
    }

    protected void clearIfInvalid() {
        if (zombie.isDead() || !zombie.isValid()) {
            Bukkit.getLogger().warning("Cancelling task {" + updateTask.getId() + "}, because the zombie is dead or invalid");
            Bukkit.getScheduler().cancelTask(updateTask.getId());

            Stats.reduceZombieCount();
        }
    }

    protected void setName() {
        zombie.setCustomNameVisible(true);
        ChatColor classColor = ChatColor.WHITE;
        if (zombieType == ZombieTypes.REGULAR) classColor = ChatColor.GREEN;
        else if (zombieType == ZombieTypes.MINER) classColor = ChatColor.DARK_RED;
        else if (zombieType == ZombieTypes.BUILDER) classColor = ChatColor.YELLOW;

        zombie.setCustomName(classColor + zombieType.toString().toUpperCase() + ChatColor.WHITE + " || " + originalTarget.getName() + " [Lv. " + level + "]");
    }

    protected boolean isThereAtargetToKill() {
        if (!currentTarget.isDead()) return true;
        else if (currentTarget.isDead() && !originalTarget.isDead()) {
            currentTarget = originalTarget;
            return true;
        } else if (currentTarget.isDead() && originalTarget.isDead() && zombie.getTarget() != null) {
            currentTarget = zombie.getTarget();
            return true;
        } else {
            //Bukkit.getLogger().info("Idling, because there are not targets to KILL");
            return false;
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (!playerIsReachable) event.setCancelled(true);
    }
}