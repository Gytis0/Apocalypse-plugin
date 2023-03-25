package Commands.Printing;

import Enums.LevelSettings;
import logic.Difficulty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrintLevelSpawnPercentages implements TabExecutor {
    Difficulty difficulty;
    public PrintLevelSpawnPercentages(Difficulty difficulty){
        this.difficulty = difficulty;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        Player player = Bukkit.getPlayer(sender.getName());
        List<Float> percentages = difficulty.getLevelSpawnPercentages();
        int maxLevel = (int)difficulty.getCurrentSetting(LevelSettings.maxLevel);
        for(int i = 0; i <= maxLevel; i++){
            if(percentages.get(i) != 0){
                player.sendMessage("Level " + ChatColor.GOLD + "[" + i + "]" + ChatColor.RESET + " has a chance of " + ChatColor.RED + percentages.get(i) * 100 + "%" + ChatColor.RESET + " to spawn.");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
