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

public class PrintDifficultySettings implements TabExecutor {
    Difficulty difficulty;
    public PrintDifficultySettings(Difficulty difficulty){
        this.difficulty = difficulty;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        sender.sendMessage("" + ChatColor.GRAY + ChatColor.BOLD + "Settings:");

        String sign = "+";

        for(String setting : difficulty.getAvailableSettings()){
            if (difficulty.getSetting(setting).getLinear() <= 0){
                sign = "";
            }
            else sign = "+";
            sender.sendMessage("" + ChatColor.BOLD + ChatColor.WHITE + setting + ": " +
                    ChatColor.RESET + ChatColor.GRAY + difficulty.getSetting(setting).getBase() +
                    ChatColor.GREEN + " (" + sign + difficulty.getSetting(setting).getLinear() + ")" +
                    ChatColor.DARK_GREEN + " (x" + difficulty.getSetting(setting).getScale() + ")" +
                    ChatColor.WHITE + ".");
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
