package logic;

import Enums.HordeSpawningSettings;
import Enums.LevelSettings;
import Enums.ZombieTypes;
import Model.DifficultySetting;
import Utility.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Stream;

public class Difficulty {
    World thisWorld;

    // General
    List<String> availableHordeSpawningSettings;
    List<String> availableLevelSettings;
    List<String> availableSettings;
    Map<String, DifficultySetting> difficultySettings;
    Map<String, Float> currentSettings;

    // Levels
    List<Float> levelSpawnPercentages;
    // Classes
    Map<ZombieTypes, Integer> classesLevels;

    Random rand;

    public Difficulty() {
        thisWorld = Bukkit.getWorld("world");

        availableHordeSpawningSettings = new ArrayList<>();
        availableLevelSettings = new ArrayList<>();
        levelSpawnPercentages = new ArrayList<>();

        difficultySettings = new HashMap<>();
        currentSettings = new HashMap<>();
        classesLevels = new HashMap<>();

        availableHordeSpawningSettings = Stream.of(HordeSpawningSettings.values()).map(Enum::name).toList();
        availableLevelSettings = Stream.of(LevelSettings.values()).map(Enum::name).toList();

        availableSettings = Stream.concat(availableHordeSpawningSettings.stream(), availableLevelSettings.stream()).toList();
        for (String diff : availableSettings) {
            difficultySettings.put(diff, new DifficultySetting());
            currentSettings.put(diff, 0f);
        }

        rand = new Random();
    }

    // Base
    public void setSetting(String difficultyName, Float base, Float scale, Float linear) {
        difficultySettings.get(difficultyName).setSettings(base, scale, linear);
        if (availableLevelSettings.contains(difficultyName)) {
            updateLevelSpawnPercentages();
        }
        scaleDifficulty();
    }

    public void setDifficultySettings(Map<String, DifficultySetting> difficultySettings) {
        this.difficultySettings = difficultySettings;
    }

    public DifficultySetting getSetting(LevelSettings setting) {
        return difficultySettings.get(setting.toString());
    }

    public DifficultySetting getSetting(HordeSpawningSettings setting) {
        return difficultySettings.get(setting.toString());
    }

    public DifficultySetting getSetting(String setting) {
        return difficultySettings.get(setting);
    }

    public float getCurrentSetting(String setting) {
        return currentSettings.get(setting);
    }

    public float getCurrentSetting(HordeSpawningSettings setting) {
        return currentSettings.get(setting.toString());
    }

    public float getCurrentSetting(LevelSettings setting) {
        return currentSettings.get(setting.toString());
    }

    public List<String> getHordeSpawningSettings() {
        return availableHordeSpawningSettings;
    }

    public List<String> getLevelSettings() {
        return availableLevelSettings;
    }

    public List<String> getAvailableSettings() {
        return availableSettings;
    }

    public Map<String, DifficultySetting> getDifficultySettings() {
        return difficultySettings;
    }

    public void scaleDifficulty(long nightCount) {

        for (String setting : availableSettings) {
            currentSettings.put(setting, difficultySettings.get(setting).scaleUp(nightCount));
        }
        updateLevelSpawnPercentages();
    }

    public void scaleDifficulty() {
        long nightCount = thisWorld.getFullTime() / 24000;
        for (String setting : availableSettings) {
            currentSettings.put(setting, difficultySettings.get(setting).scaleUp(nightCount));
        }
        updateLevelSpawnPercentages();
    }

    // Levels
    public int getRandomLevel() {
        float random = rand.nextFloat(0f, 1f);
        int maxLevel = (int) getCurrentSetting(LevelSettings.maxLevel);
        float percentageCarry = 0f;

        for (int i = 0; i <= maxLevel; i++) {
            if (random < levelSpawnPercentages.get(i) + percentageCarry) {
                return i;
            }
            percentageCarry += levelSpawnPercentages.get(i);
        }
        return -1;
    }

    public void updateLevelSpawnPercentages() {
        List<Float> ans = new ArrayList<>();

        int maxLevel = (int) getCurrentSetting("maxLevel");
        float focus = getCurrentSetting("levelFocus");
        focus = Utils.clamp(focus, 0, 1);
        int width = (int) getCurrentSetting("width");
        float falloff = getCurrentSetting("falloff");

        int focusTab;
        if (focus != 1) {
            focusTab = (int) ((maxLevel + 1) * focus);
        } else {
            focusTab = maxLevel;
        }
        int distance, value, sum = 0;
        float blocks;

        for (int i = 0; i <= maxLevel; i++) {
            blocks = 0;
            distance = Math.abs(focusTab - i);
            if (distance <= width) {
                value = width - distance;
                blocks = (float) Math.pow(falloff, value);
                sum += blocks;
            }
            ans.add(blocks);
        }

        for (int i = 0; i <= maxLevel; i++) {
            ans.set(i, ans.get(i) / sum);
        }

        levelSpawnPercentages = ans;
    }

    public List<Float> getLevelSpawnPercentages() {
        return levelSpawnPercentages;
    }

    // Classes
    public ZombieTypes getRandomClass(int level) {
        return ZombieTypes.REGULAR;
    }
}
