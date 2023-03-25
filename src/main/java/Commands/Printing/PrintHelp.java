package Commands.Printing;

import apocalypse.apocalypse.Apocalypse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrintHelp implements TabExecutor {
    Apocalypse plugin;
    public PrintHelp(Apocalypse plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args){
        sender.sendMessage("\n-----------START OF HELP-----------");
        for (Map map : plugin.getDescription().getCommands().values()) {
            sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + map.get("usage").toString() +
                    ChatColor.WHITE + " || "
                    + ChatColor.BLUE + ChatColor.BOLD + map.get("aliases") + "\n" +
                    ChatColor.UNDERLINE + ChatColor.GRAY + map.get("description") + "\n");
        }
        sender.sendMessage("-----------END OF HELP-----------\n");
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
