package Commands.Settings;

import logic.Hordes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoveSquad implements TabExecutor {
    Hordes hordes;

    public RemoveSquad(Hordes hordes) {
        this.hordes = hordes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){

        if(args.length < 1){
            return false;
        }

        String squadName = args[0];
        if(!hordes.getSquadNames().contains(squadName)){
            sender.sendMessage(ChatColor.RED + "A squad with a name of " + ChatColor.BOLD + "[" + squadName + "]" + ChatColor.RESET + ChatColor.RED + " doesn't exist. Consider creating it first.");
            return true;
        }

        hordes.removeSquad(squadName);
        sender.sendMessage(ChatColor.GREEN + "Squad " + ChatColor.BOLD + ChatColor.DARK_GREEN + "[" + args[0] + "]" + ChatColor.RESET + ChatColor.GREEN + " has been removed.");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return hordes.getSquadNames();
        }
        return null;
    }
}
