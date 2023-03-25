package Utility;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class ResetModifications {
    public void ResetAll(String playerName){
        ResetMaxHealth(playerName);
    }
    public void ResetAll(Player player){
        ResetMaxHealth(player);
    }

    // After every death, players health gets lower by 2
    // Reset player health to 20
    public void ResetMaxHealth(String playerName){
        Player player = Bukkit.getPlayer(playerName);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }
    public void ResetMaxHealth(Player player){
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }
}
