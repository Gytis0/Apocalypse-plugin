package Model;

import Enums.ZombieTypes;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Squad implements ConfigurationSerializable {
    private String squadName;
    private HashMap<ZombieTypes, Integer> squadContent;
    private int totalWeight, levelRequirement;

    public Squad(String name){
        squadName = name;
        squadContent = new HashMap<>();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        Map<String, Integer> tempMap = new HashMap<>();
        squadContent.forEach((key, value) -> {
            tempMap.put(key.toString(), value);
        });

        serialized.put("squadName", squadName);
        serialized.put("squad", tempMap);
        serialized.put("totalWeight", totalWeight);
        serialized.put("levelRequirement", levelRequirement);
        return serialized;
    }

    public void deserialize(Object squadName, Object squadContent, Object totalWeight, Object levelRequirement){
        this.squadName = (String) squadName;
        this.squadContent = (HashMap<ZombieTypes, Integer>) squadContent;
        this.totalWeight = (int) totalWeight;
        this.levelRequirement = (int) levelRequirement;
    }

    public void removeClass(ZombieTypes type){
        squadContent.remove(type);
    }

    public HashMap<ZombieTypes, Integer> getSquadContent() {
        return squadContent;
    }

    public void setClassAmount(ZombieTypes type, int amount){
        if(amount == 0){
            removeClass(type);
        }
        else{
            squadContent.put(type, amount);
        }
    }

    public String getSquadName() {
        return squadName;
    }

    public void setTotalWeight(int totalWeight){
        this.totalWeight = totalWeight;
    }

    public int getTotalWeight(){
        return totalWeight;
    }

    public void setLevelRequirement(int levelRequirement){
        this.levelRequirement = levelRequirement;
    }

    public int getLevelRequirement(){
        return levelRequirement;
    }
}
