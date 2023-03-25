package Commands.Printing;

import Enums.ZombieTypes;
import logic.Hordes;
import Model.Squad;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class PrintSquads implements TabExecutor {
    Hordes hordes;

    public PrintSquads(Hordes hordes) {
        this.hordes = hordes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        List<Squad> squads = hordes.getSquads();
        if(squads.isEmpty()){
            sender.sendMessage(ChatColor.RED + "There are no squads created.");
            return true;
        }

        String message;
        HashMap<ZombieTypes, Integer> content;

        for(Squad s : squads){
            sender.sendMessage(ChatColor.GRAY + "-----------------------------");
            sender.sendMessage(ChatColor.GOLD + s.getSquadName());
            sender.sendMessage(ChatColor.GRAY + "Weight: " + s.getTotalWeight() + ". Level requirement: " +s.getLevelRequirement());
            content = s.getSquadContent();
            message = "";
            for(ZombieTypes zt : ZombieTypes.values()){
                if(content.containsKey(zt)){
                    message = message.concat(ChatColor.YELLOW + zt.toString() + ChatColor.GREEN + " (" +  content.get(zt) + ") \n");
                }
            }
            sender.sendMessage(message);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
