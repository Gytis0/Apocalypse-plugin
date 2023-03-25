package Commands.Settings;

import logic.Hordes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateSquad implements TabExecutor {
    Hordes hordes;

    public CreateSquad(Hordes hordes) {
        this.hordes = hordes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){

        if(args.length < 1){
            return false;
        }

        hordes.addSquad(args[0]);
        sender.sendMessage(ChatColor.GREEN + "Squad " + ChatColor.BOLD + ChatColor.DARK_GREEN + "[" + args[0] + "]" + ChatColor.RESET + ChatColor.GREEN + " has been created.");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
