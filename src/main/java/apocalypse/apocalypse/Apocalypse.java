package apocalypse.apocalypse;

import Commands.Debugging.PlaceBlockAtLedge;
import Commands.Printing.*;
import Commands.ResetMods;
import Commands.Settings.*;
import Commands.Spawning.DespawnZombies;
import Commands.Spawning.SpawnSquad;
import Commands.ToggleApocalypse;
import Model.DifficultySetting;
import Handlers.BedHandler;
import Handlers.PlayerHandler;
import Handlers.WorldHandler;
import Handlers.ZombieHandler;
import logic.Settings;
import logic.ZombieSpawner;
import Utility.DelayedTask;
import Utility.RepeatableTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public final class Apocalypse extends JavaPlugin {
    // plugin
    World overworld;
    ZombieSpawner zombieSpawner;

    // configs
    Settings settings;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(DifficultySetting.class);
        overworld = Bukkit.getWorld("world");

        new BedHandler(this);
        new PlayerHandler(this);
        new ZombieHandler(this);

        new DelayedTask(this);
        new RepeatableTask(this);

        settings = new Settings(this, overworld);

        new WorldHandler(this, settings.getDifficulty());

        zombieSpawner = new ZombieSpawner(overworld, settings, this);

        setupCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown main.logic
        Bukkit.getLogger().info("Goodbye world :(");
    }

    void setupCommands(){
        // Printing
        getCommand("printHelp").setExecutor(new PrintHelp(this));
        getCommand("printDifficultySettings").setExecutor(new PrintDifficultySettings(settings.getDifficulty()));
        getCommand("printCurrentSettings").setExecutor(new PrintCurrentDifficultySettings(settings.getDifficulty()));
        getCommand("printZombieTypes").setExecutor(new PrintZombieTypes(settings.getHordes()));
        getCommand("printSquads").setExecutor(new PrintSquads(settings.getHordes()));
        getCommand("printActiveZombies").setExecutor(new PrintActiveZombies());
        getCommand("printLevelSpawnPercentages").setExecutor(new PrintLevelSpawnPercentages(settings.getDifficulty()));
        getCommand("printMyVector").setExecutor(new PrintMyVector(overworld));

        // Settings / configurations
        getCommand("setDifficultySetting").setExecutor(new SetDifficultySetting(settings.getDifficulty()));
        getCommand("setZombieType").setExecutor(new SetZombieType(settings.getHordes()));
        getCommand("createSquad").setExecutor(new CreateSquad(settings.getHordes()));
        getCommand("setSquad").setExecutor(new SetSquad(settings.getHordes()));
        getCommand("removeSquad").setExecutor(new RemoveSquad(settings.getHordes()));

        getCommand("saveApocalypseSettings").setExecutor(new SaveApocalypseSettings(settings));

        getCommand("resetMods").setExecutor(new ResetMods());
        getCommand("toggleApocalypse").setExecutor(new ToggleApocalypse(zombieSpawner));

        //Spawning
        getCommand("spawnSquad").setExecutor(new SpawnSquad(zombieSpawner, settings.getHordes()));
        getCommand("despawnZombies").setExecutor(new DespawnZombies());

        // Debugging
        getCommand("placeBlockAtLedge").setExecutor(new PlaceBlockAtLedge());
    }
}