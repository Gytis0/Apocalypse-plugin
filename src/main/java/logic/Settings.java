package logic;

import Model.DifficultySetting;
import Model.Squad;
import Model.ZombieClass;
import Utility.FileReading;
import apocalypse.apocalypse.Apocalypse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Settings {
    Apocalypse plugin;
    World world;
    // Settings for things that scale every night
    Difficulty difficulty;
    // Settings for creating zombie classes, squads...
    Hordes hordes;

    String difficultySettingsFileName = "difficultySettings.json";
    String zombieClassesFileName = "zombieClasses.json";
    String squadsFileName = "squads.json";

    public Settings(Apocalypse plugin, World thisWorld) {
        this.plugin = plugin;
        this.world = thisWorld;

        difficulty = new Difficulty();
        hordes = new Hordes();

        loadDifficultySettings(plugin, difficultySettingsFileName);
        loadHordeSettings(plugin, zombieClassesFileName, squadsFileName);

        difficulty.scaleDifficulty(this.world.getFullTime() / 24000);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Hordes getHordes() {
        return hordes;
    }


    public boolean saveSettings() {
        Gson gson = new Gson();
        String saveText;
        // Difficulty settings
        saveText = gson.toJson(difficulty.getDifficultySettings());
        boolean firstSave = writeToFile(difficultySettingsFileName, saveText);

        // ZombieClasses
        saveText = gson.toJson(hordes.getZombieClasses());
        boolean secondSave = writeToFile(zombieClassesFileName, saveText);

        // Squads
        saveText = gson.toJson(hordes.getSquads());
        boolean thirdSave = writeToFile(squadsFileName, saveText);

        return firstSave && secondSave && thirdSave;
    }

    public void loadDifficultySettings(Apocalypse plugin, String fileName) {
        writeDefaults(fileName);

        Gson gson = new Gson();
        String content = readFromFile(plugin.getDataFolder().getAbsolutePath() + "/" + fileName);

        Type mapType = new TypeToken<Map<String, DifficultySetting>>() {
        }.getType();
        difficulty.setDifficultySettings(gson.fromJson(content, mapType));
    }

    public void loadHordeSettings(Apocalypse plugin, String zombieClassesFileName, String squadsFileName) {
        Gson gson = new Gson();

        writeDefaults(zombieClassesFileName);

        String content = readFromFile(plugin.getDataFolder().getAbsolutePath() + "/" + zombieClassesFileName);
        Type listType = new TypeToken<ArrayList<ZombieClass>>() {
        }.getType();
        List<ZombieClass> classes = gson.fromJson(content, listType);
        if (!classes.isEmpty()) {
            Bukkit.getLogger().info("There are some classes");
        } else {
            Bukkit.getLogger().warning("There are no classes");
        }
        hordes.setZombieClasses(classes);

        writeDefaults(squadsFileName);

        content = readFromFile(plugin.getDataFolder().getAbsolutePath() + "/" + squadsFileName);
        listType = new TypeToken<ArrayList<Squad>>() {
        }.getType();
        hordes.setSquads(gson.fromJson(content, listType));
    }

    private boolean writeToFile(String fileName, String content) {
        String path = plugin.getDataFolder().getAbsolutePath() + "/" + fileName;

        try {
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readFromFile(String path) {
        File file = new File(path);
        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String content = "";
        while (reader.hasNextLine()) {
            content = content.concat(reader.nextLine());
        }

        reader.close();
        return content;
    }

    private void writeDefaults(String fileName) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
        // if file does not exist, create it
        if (file.length() == 0) {
            try {
                Bukkit.getLogger().info(fileName + " does not exist. Creating one now...");
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Write defaults
            InputStream stream = plugin.getResource(fileName);
            FileReading.copyInputStreamToFile(stream, file);
            try {
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void resetToDefaults() {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/" + difficultySettingsFileName);
        file.delete();
        writeDefaults(difficultySettingsFileName);

        file = new File(plugin.getDataFolder().getAbsolutePath() + "/" + zombieClassesFileName);
        file.delete();
        writeDefaults(zombieClassesFileName);

        file = new File(plugin.getDataFolder().getAbsolutePath() + "/" + squadsFileName);
        file.delete();
        writeDefaults(squadsFileName);

        loadDifficultySettings(plugin, difficultySettingsFileName);
        loadHordeSettings(plugin, zombieClassesFileName, squadsFileName);

        difficulty.scaleDifficulty(this.world.getFullTime() / 24000);
    }
}