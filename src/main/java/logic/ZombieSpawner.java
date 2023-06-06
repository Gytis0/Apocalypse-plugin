package logic;

import Enums.HordeSpawningSettings;
import Enums.ZombieTypes;
import Model.Squad;
import Utility.RepeatableTask;
import ZombieTypes.Builder;
import ZombieTypes.Miner;
import ZombieTypes.Regular;
import apocalypse.apocalypse.Apocalypse;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ZombieSpawner {
    boolean isActive = false;
    boolean isNight = false;
    List<Player> playerList;

    Apocalypse apocalypse;
    RepeatableTask zombieHordes;

    Difficulty difficulty;
    Hordes hordes;

    long nightCount = 0;
    World thisWorld;
    Random rand;

    public ZombieSpawner(World world, Settings settings, Apocalypse apocalypse) {
        this.apocalypse = apocalypse;
        this.difficulty = settings.getDifficulty();
        this.hordes = settings.getHordes();
        this.thisWorld = world;

        rand = new Random();

        new RepeatableTask(this::update, 0, 3);
    }

    public void update() {
        isNight = isNightTime(thisWorld);

        if (isNight && isActive && zombieHordes == null) {
            nightCount = currentDay(thisWorld.getFullTime());
            sendTitlesToAllPlayers("" + ChatColor.WHITE + "The moon rises...", "" + ChatColor.WHITE + "Night: " +
                    ChatColor.RED + nightCount, 0, 5, 1);
            Sound sound = Sound.AMBIENT_CAVE;

            for (Player player : playerList) {
                player.playSound(player, sound, 100, 1);
            }

            difficulty.scaleDifficulty(nightCount);

            zombieHordes = new RepeatableTask(ZombieSpawner.this::spawnRandomSquad, 0, (long) difficulty.getCurrentSetting("rate"));

        } else if (zombieHordes != null) {
            if (!isNight || !isActive) {

                nightCount = currentDay(thisWorld.getFullTime());
                difficulty.scaleDifficulty(nightCount);

                Bukkit.getScheduler().cancelTask(zombieHordes.getId());
                zombieHordes = null;
            }
            if (!isNight) {
                sendTitlesToAllPlayers("", "" + ChatColor.BLACK + "The moon falls...", 2, 3, 3);
                for (Player player : playerList) {
                    player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 100, 1);
                }
            }
        }
    }

    void spawnRandomSquad() {
        Location squadLocation, zombieLocation;
        playerList = new ArrayList<>(Bukkit.getOnlinePlayers());

        float range = difficulty.getCurrentSetting(HordeSpawningSettings.range), size = difficulty.getCurrentSetting(HordeSpawningSettings.size), spacing = difficulty.getCurrentSetting(HordeSpawningSettings.spacing);

        int randomLevel, randomSquadIndex, amountOfZombies, weightToDeduce;
        float tempSize;
        List<Squad> availableSquads;
        Squad squadToSpawn;

        for (Player player : playerList) {
            if (player.isDead()) return;

            tempSize = size;
            while (true) {
                randomLevel = difficulty.getRandomLevel();
                availableSquads = getAvailableSquads(tempSize, randomLevel);
                if (availableSquads.size() == 0) break;

                squadLocation = randomLocation(player.getLocation(), range);

                randomSquadIndex = rand.nextInt(0, availableSquads.size());
                squadToSpawn = availableSquads.get(randomSquadIndex);
                Bukkit.getLogger().info(" ### Spawning \"" + squadToSpawn.getSquadName() + "\" squad. ### ");
                Set<ZombieTypes> zombies = squadToSpawn.getSquadContent().keySet();

                for (ZombieTypes zombieType : zombies) {
                    amountOfZombies = squadToSpawn.getSquadContent().get(zombieType);
                    weightToDeduce = hordes.findZombieClass(zombieType).getWeight() * amountOfZombies;
                    tempSize -= weightToDeduce;

                    for (int i = 0; i < amountOfZombies; i++) {
                        zombieLocation = spaceOutLocation(squadLocation, spacing);
                        spawnZombie(zombieType, player, zombieLocation, randomLevel);
                    }
                }
            }

            for (int i = 0; i < tempSize; i++) {
                spawnZombie(ZombieTypes.REGULAR, player, randomLocation(player.getLocation(), range), randomLevel);
            }
        }
    }

    public void spawnSquad(String squadName, LivingEntity target, int level) {
        Location squadLocation = target.getLocation();
        Location zombieLocation;

        float spacing = difficulty.getCurrentSetting(HordeSpawningSettings.spacing);

        int amountOfZombies;
        Squad squadToSpawn = hordes.findSquad(squadName);

        Set<ZombieTypes> zombies = squadToSpawn.getSquadContent().keySet();

        for (ZombieTypes zombieType : zombies) {
            amountOfZombies = squadToSpawn.getSquadContent().get(zombieType);

            for (int i = 0; i < amountOfZombies; i++) {
                zombieLocation = spaceOutLocation(squadLocation, spacing);
                spawnZombie(zombieType, target, zombieLocation, level);
            }
        }
    }

    public void toggleApocalypse(boolean enable) {
        isActive = enable;
        if (isActive) {
            playerList = new ArrayList<>(Bukkit.getOnlinePlayers());

            thisWorld.setTime(0);
            for (Player player : playerList) {
                player.setFoodLevel(10);
                player.setSaturation(10);
            }
            sendTitlesToAllPlayers("" + ChatColor.DARK_RED + ChatColor.BOLD + ChatColor.UNDERLINE + "THE APOCALYPSE",
                    "" + ChatColor.DARK_RED + ChatColor.BOLD + ChatColor.UNDERLINE + "HAS STARTED", 2, 4, 5);
            Sound sound = Sound.AMBIENT_NETHER_WASTES_ADDITIONS;
            for (Player player : playerList) {
                player.playSound(player, sound, 100, 0.8f);
            }
        } else {
            playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
            sendTitlesToAllPlayers("" + ChatColor.GRAY + ChatColor.BOLD + ChatColor.UNDERLINE + "THE APOCALYPSE",
                    "" + ChatColor.GRAY + ChatColor.BOLD + ChatColor.UNDERLINE + "HAS ENDED...", 3, 3, 2);
        }
    }

    public boolean isNightTime(World world) {
        long time = world.getTime();
        if ((time < 13000 || time > 23400) && isNight) {
            isNight = false;
        } else if (time >= 13000 && time <= 23400 && !isNight) {
            isNight = true;
        }
        return isNight;
    }

    Location randomLocation(Location origin, float range) {
        double d = Math.toRadians(rand.nextInt(360));
        int x = (int) (Math.sin(d) * range);
        int z = (int) (Math.cos(d) * range);
        int y = thisWorld.getHighestBlockYAt((int) (x + origin.getX()), (int) (z + origin.getZ()));
        return new Location(thisWorld, origin.getX() + x, y, origin.getZ() + z);
    }

    Location spaceOutLocation(Location loc, float spacing) {
        return new Location(thisWorld, loc.getX() + rand.nextFloat(spacing) - spacing / 2, loc.getY(), loc.getZ() + rand.nextFloat(spacing) - spacing / 2);
    }

    private List<Squad> getAvailableSquads(float weightRequirement, float levelRequirement) {
        List<Squad> allSquads = hordes.getSquads();
        List<Squad> result = new ArrayList<>();
        for (Squad squad : allSquads) {
            if (squad.getTotalWeight() <= weightRequirement && squad.getLevelRequirement() <= levelRequirement) {
                result.add(squad);
            }
        }

        return result;
    }

    private void spawnZombie(ZombieTypes zombieType, LivingEntity target, Location location, int level) {
        if (zombieType == ZombieTypes.REGULAR) {
            new Regular(apocalypse, location, target, level);
        } else if (zombieType == ZombieTypes.MINER) {
            new Miner(apocalypse, location, target, level);
        } else if (zombieType == ZombieTypes.BUILDER) {
            new Builder(apocalypse, location, target, level);
        }
    }

    long currentDay(long ticks) {
        return (ticks / 24000) + 1;
    }

    void sendTitlesToAllPlayers(String title, String subtitle, float in, float stay, float out) {
        for (Player player : playerList) {
            player.sendTitle(title, subtitle, (int) (in * 20), (int) (stay * 20), (int) (out * 20));
        }
    }
}