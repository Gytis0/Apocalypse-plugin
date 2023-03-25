package Commands.Settings;

import logic.Settings;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SaveApocalypseSettings implements TabExecutor {
    Settings settings;

    public SaveApocalypseSettings(Settings settings) {
        this.settings = settings;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){

        if(settings.saveSettings()){
            sender.sendMessage(ChatColor.GREEN + "Settings were saved.");
        }
        else {
            sender.sendMessage(ChatColor.DARK_RED + "Settings couldn't be saved. Check the server console for more details.");
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
