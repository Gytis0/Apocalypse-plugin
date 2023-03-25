package Commands.Settings;

import Enums.ZombieTypes;
import logic.Hordes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetSquad implements TabExecutor {
    Hordes hordes;

    public SetSquad(Hordes hordes) {
        this.hordes = hordes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){

        if(args.length < 3){
            return false;
        }

        String squadName = args[0];
        if(!hordes.getSquadNames().contains(squadName)){
            sender.sendMessage(ChatColor.RED + "A squad with a name of " + ChatColor.BOLD + "[" + squadName + "]" + ChatColor.RESET + ChatColor.RED + " doesn't exist. Consider creating it first.");
            return true;
        }

        String userType = args[1];
        boolean found = false;
        for(ZombieTypes type : ZombieTypes.values()){
            if(type.toString().equals(userType)){
                found = true;
                break;
            }
        }

        if(!found){
            sender.sendMessage(ChatColor.RED + "A zombie type " + ChatColor.BOLD + "[" + userType + "]" + ChatColor.RESET + ChatColor.RED + " doesn't exist.");
            return true;
        }

        hordes.setSquadZombie(squadName, ZombieTypes.valueOf(userType), Integer.parseInt(args[2]));
        sender.sendMessage(ChatColor.GREEN + "Zombie " + ChatColor.BOLD + ChatColor.DARK_GREEN + "[" + userType + "]" + ChatColor.RESET + ChatColor.GREEN + " amount has been changed to " +  ChatColor.BOLD + ChatColor.DARK_GREEN + "(" + args[2] + ")" + ChatColor.RESET + ChatColor.GREEN + ".");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return hordes.getSquadNames();
        }
        else if(args.length == 2){
            return Stream.of(ZombieTypes.values()).map(ZombieTypes::name).collect(Collectors.toList());
        }

        return null;
    }
}
