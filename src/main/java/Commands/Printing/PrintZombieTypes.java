package Commands.Printing;

import logic.Hordes;
import Model.ZombieClass;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrintZombieTypes implements TabExecutor {
    Hordes hordes;

    public PrintZombieTypes(Hordes hordes) {
        this.hordes = hordes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        List<ZombieClass> zombieClasses = hordes.getZombieClasses();
        for(ZombieClass zc : zombieClasses){
            sender.sendMessage(ChatColor.GREEN + zc.getType().toString() + ": " +
                    ChatColor.DARK_GREEN + "[" + zc.getLevelRequirement() + "LV.] " +
                                ChatColor.AQUA + "(weight: " + zc.getWeight() + ")" +
                                ChatColor.RESET + ".");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
