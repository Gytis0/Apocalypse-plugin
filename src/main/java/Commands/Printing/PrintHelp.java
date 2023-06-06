package Commands.Printing;

import apocalypse.apocalypse.Apocalypse;
import org.bukkit.Bukkit;
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

    public PrintHelp(Apocalypse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int printPerPage = 9, pageIndex = 1, maxPages;
        if (args.length > 0) {
            try {
                pageIndex = Integer.parseInt(args[0]);
            } catch (RuntimeException e) {
                Bukkit.getLogger().warning("Error while parsing page index to number");
                return false;
            }
        }

        List<Map<String, Object>> elements = plugin.getDescription().getCommands().values().stream().toList();
        maxPages = (int) Math.nextUp((double) elements.size() / (double) printPerPage);

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "-----------HELP PAGE (" + pageIndex + "/" + maxPages + ")-----------");
        int start = (pageIndex - 1) * printPerPage;
        String commandName, description;
        for (int i = start; i < start + printPerPage; i++) {
            commandName = elements.get(i).get("usage").toString();
            commandName = commandName.substring(1);
            if (commandName.contains(" ")) commandName = commandName.substring(0, commandName.indexOf(" "));
            description = elements.get(i).get("description").toString();
            sender.sendMessage("" + ChatColor.GREEN + ChatColor.UNDERLINE + commandName + ChatColor.RESET + ChatColor.GREEN + ": " + ChatColor.RESET + description);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            return new ArrayList<>();
        }
        return null;
    }
}
