package Model;

import Enums.ZombieTypes;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class ZombieClass implements ConfigurationSerializable {
    private ZombieTypes type;
    private int levelRequirement;
    private int weight;

    public ZombieClass(ZombieTypes type) {
        this.type = type;
        this.levelRequirement = 0;
        this.weight = 1;
    }

    public ZombieClass(ZombieTypes type, int levelRequirement, int weight) {
        this.type = type;
        this.levelRequirement = levelRequirement;
        this.weight = weight;
    }

    @Override
    public Map<String, Object> serialize(){
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("type", type);
        serialized.put("levelRequirement", levelRequirement);
        serialized.put("weight", weight);
        return serialized;
    }

    public void deserialize(Object type, Object levelRequirement, Object weight){
        this.type = (ZombieTypes) type;
        this.levelRequirement = (int) levelRequirement;
        this.weight = (int) weight;
    }

    public ZombieTypes getType() {
        return type;
    }

    public void setType(ZombieTypes type) {
        this.type = type;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public void setLevelRequirement(int levelRequirement) {
        this.levelRequirement = levelRequirement;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
