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

public class SetZombieType implements TabExecutor {
    Hordes hordes;

    public SetZombieType(Hordes hordes) {
        this.hordes = hordes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){

        if(args.length < 2){
            return false;
        }

        String userType = args[0];
        ZombieTypes type = ZombieTypes.REGULAR;
        boolean found = false;
        for(ZombieTypes zt : ZombieTypes.values()){
            if(zt.toString().equals(userType)){
                found = true;
                type = zt;
                break;
            }
        }

        if(!found){
            sender.sendMessage(ChatColor.RED + "A zombie type " + ChatColor.BOLD + "[" + userType + "]" + ChatColor.RESET + ChatColor.RED + " doesn't exist.");
            return true;
        }

        int weight;
        try{
            weight = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException ex){
            sender.sendMessage(ChatColor.DARK_RED + "Weight must be a number.");
            ex.printStackTrace();
            return true;
        }

        if(weight < 1){
            sender.sendMessage(ChatColor.DARK_RED + "Weight must be positive.");
        }

        hordes.changeWeight(type, weight);
        sender.sendMessage(ChatColor.GREEN + "Zombie " + ChatColor.BOLD + ChatColor.DARK_GREEN + "[" + userType + "]" + ChatColor.RESET + ChatColor.GREEN + " weight has been changed to " +  ChatColor.BOLD + ChatColor.DARK_GREEN + "(" + weight + ")" + ChatColor.RESET + ChatColor.GREEN + ".");

        int requirement;
        if(args.length == 3){
            try{
                requirement = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException ex){
                sender.sendMessage(ChatColor.DARK_RED + "Level requirement must be a number.");
                ex.printStackTrace();
                return true;
            }

            if(requirement < 0){
                sender.sendMessage(ChatColor.DARK_RED + "Weight must be non-negative.");
                return true;
            }

            hordes.changeLevel(type, requirement);
            sender.sendMessage(ChatColor.GREEN + "Zombie " + ChatColor.BOLD + ChatColor.DARK_GREEN + "[" + userType + "]" + ChatColor.RESET + ChatColor.GREEN + " level requirement has been changed to " +  ChatColor.BOLD + ChatColor.DARK_GREEN + "(" + requirement + " LV.)" + ChatColor.RESET + ChatColor.GREEN + ".");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Stream.of(ZombieTypes.values()).map(ZombieTypes::name).collect(Collectors.toList());
        }

        return null;
    }
}
