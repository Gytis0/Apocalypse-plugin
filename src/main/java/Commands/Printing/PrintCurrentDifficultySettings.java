package Commands.Printing;

import logic.Difficulty;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrintCurrentDifficultySettings implements TabExecutor {
    Difficulty difficulty;
    public PrintCurrentDifficultySettings(Difficulty difficulty){
        this.difficulty = difficulty;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        sender.sendMessage("" + ChatColor.GRAY + ChatColor.BOLD + "Current settings:");

        for(String setting : difficulty.getAvailableSettings()){
            sender.sendMessage("" + ChatColor.WHITE + setting + ": " + difficulty.getCurrentSetting(setting));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length > 0){
            return new ArrayList<>();
        }
        return null;
    }
}
