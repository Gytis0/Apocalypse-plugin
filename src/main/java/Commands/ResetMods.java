package Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import Utility.ResetModifications;

import java.util.ArrayList;
import java.util.List;

public class ResetMods implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        ResetModifications resetModifications = new ResetModifications();
        List<String> playerNamesList = new ArrayList<>();

        if(args.length == 0){
            playerNamesList.add(sender.getName());
        }
        else if(args.length > 0){
            if(args[0].equalsIgnoreCase("@a")){
                List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
                for(Player player : playerList){
                    playerNamesList.add(player.getName());
                }
            }
            else if(args.length > 0){
                for(String arg : args){
                    playerNamesList.add(arg);
                }
            }
        }

        for(String playerName : playerNamesList){
            resetModifications.ResetAll(playerName);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
            List<String> playerNames = new ArrayList<>();
            for(Player player : playerList){
                playerNames.add(player.getName());
            }
            playerNames.add("@a");
            return playerNames;
        }
        return null;
    }
}
