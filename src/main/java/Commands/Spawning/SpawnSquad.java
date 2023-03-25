package Commands.Spawning;

import logic.Hordes;
import logic.ZombieSpawner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpawnSquad implements TabExecutor {
    ZombieSpawner zombieSpawner;
    Hordes hordes;

    public SpawnSquad(ZombieSpawner zombieSpawner, Hordes hordes) {
        this.zombieSpawner = zombieSpawner;
        this.hordes = hordes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){

        if(args.length < 1){
            return false;
        }



        if(!hordes.getSquadNames().contains(args[0])){
            sender.sendMessage(ChatColor.RED + "No such squad \"" + args[0] + "\".");
            return true;
        }

        int level;

        if(args.length == 1){
            level = 1;
        }
        else {
            try{
                level = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e){
                sender.sendMessage(ChatColor.RED + "Level has to be a number.");
                return true;
            }
        }

        zombieSpawner.spawnSquad(args[0], (Player) sender, level);

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