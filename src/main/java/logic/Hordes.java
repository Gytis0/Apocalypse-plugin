package logic;


import Enums.ZombieTypes;
import Model.Squad;
import Model.ZombieClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

// Stores information about available zombie classes and custom made squads
public class Hordes {
    private List<ZombieClass> zombieClasses;
    private List<Squad> squads;
    public Hordes(){
        zombieClasses = new ArrayList<>();
        squads = new ArrayList<>();

        for(ZombieTypes zc : ZombieTypes.values()){
            zombieClasses.add(new ZombieClass(zc));
        }
    }

    // Zombie classes
    public void changeLevel(ZombieTypes type, int levelRequirement){
        findZombieClass(type).setLevelRequirement(levelRequirement);

        for(Squad squad : squads){
            calculateSquadWeight(squad);
            calculateSquadLevelRequirement(squad);
        }
    }
    public void changeWeight(ZombieTypes type, int weight){
        findZombieClass(type).setWeight(weight);

        for(Squad squad : squads){
            calculateSquadWeight(squad);
            calculateSquadLevelRequirement(squad);
        }
    }
    public List<ZombieClass> getZombieClasses(){
        return zombieClasses;
    }
    public void setZombieClasses(List<ZombieClass> zombieClasses) {
        if(zombieClasses == null || zombieClasses.isEmpty()){
            zombieClasses.clear();
            for(ZombieTypes zc : ZombieTypes.values()){
                zombieClasses.add(new ZombieClass(zc));
            }
        }
        else{
            this.zombieClasses = zombieClasses;}
    }
    public ZombieClass findZombieClass(ZombieTypes type){
        for(ZombieClass zc : zombieClasses){
            if(zc.getType().equals(type)){
                return zc;
            }
        }
        return null;
    }

    // Squads
    public void addSquad(String squadName){
        squads.add(new Squad(squadName));
    }
    public void setSquadZombie(String squadName, ZombieTypes type, int amount){
        Squad squad = findSquad(squadName);
        squad.setClassAmount(type, amount);

        calculateSquadWeight(squad);
        calculateSquadLevelRequirement(squad);
    }
    public void removeSquadZombie(String squadName, ZombieTypes type){
        setSquadZombie(squadName, type, 0);
    }
    public void removeSquad(String squadName){
        squads.remove(findSquad(squadName));
    }

    public List<Squad> getSquads(){
        return squads;
    }
    public void setSquads(List<Squad> squads) {
        if(squads == null){
            this.squads = new ArrayList<>();
        }
        else{
            this.squads = squads;
        }
    }
    public Squad findSquad(String squadName){
        for(Squad s : squads){
            if(s.getSquadName().equalsIgnoreCase(squadName)){
                return s;
            }
        }
        return null;
    }
    public List<String> getSquadNames(){
        List<String> names = new ArrayList<String>();
        for(Squad s : squads){
            names.add(s.getSquadName());
        }
        return names;
    }

    private void calculateSquadWeight(Squad squad){
        HashMap<ZombieTypes, Integer> content = squad.getSquadContent();
        Set<ZombieTypes> set = content.keySet();

        int sum = 0;

        for(ZombieTypes zt: set){
            sum += findZombieClass(zt).getWeight() * content.get(zt);
        }

        squad.setTotalWeight(sum);
    }
    private void calculateSquadLevelRequirement(Squad squad){
        HashMap<ZombieTypes, Integer> content = squad.getSquadContent();
        Set<ZombieTypes> set = content.keySet();

        int maxLevel = 0;

        for(ZombieTypes zt: set){
            if(findZombieClass(zt).getLevelRequirement() > maxLevel){
                maxLevel = findZombieClass(zt).getLevelRequirement();
            }
        }

        squad.setLevelRequirement(maxLevel);
    }
}