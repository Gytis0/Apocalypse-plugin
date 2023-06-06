package Commands.Settings;

import Enums.LevelSettings;
import logic.Difficulty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetDifficultySetting implements TabExecutor {
    Difficulty difficulty;

    public SetDifficultySetting(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Float base, scale, linear;
        String setting;
        String sign = "+";
        scale = null;
        linear = null;
        if (args.length < 2) {
            return false;
        }

        setting = args[0];
        if (!difficulty.getAvailableSettings().contains(setting)) {
            sender.sendMessage(ChatColor.DARK_RED + "Setting name must be valid");
            return true;
        }

        if (args[1].equalsIgnoreCase("null")) {
            base = difficulty.getSetting(setting).getBase();
        } else {
            try {
                base = Float.parseFloat(args[1]);
                if (setting.equalsIgnoreCase(LevelSettings.levelFocus.toString()) || setting.equalsIgnoreCase(LevelSettings.falloff.toString()) || setting.equalsIgnoreCase(LevelSettings.width.toString()) || setting.equalsIgnoreCase(LevelSettings.maxLevel.toString())) {
                    if (base < 0) {
                        sender.sendMessage(ChatColor.DARK_RED + setting + " must be non-negative");
                        return true;
                    }
                } else if (base <= 0) {
                    sender.sendMessage(ChatColor.DARK_RED + setting + " must be positive");
                    return true;
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.DARK_RED + setting + " must be a number.");
                ex.printStackTrace();
                return true;
            }
        }


        if (args.length > 2) {
            if (args[2].equals("null")) {
                linear = difficulty.getSetting(setting).getLinear();
            } else {
                try {
                    linear = Float.parseFloat(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.DARK_RED + "Linear must be a number.");
                    ex.printStackTrace();
                    return true;
                }
            }
        }

        if (args.length > 3) {
            if (args[3].equals("null")) {
                scale = difficulty.getSetting(setting).getScale();
            } else {
                try {
                    scale = Float.parseFloat(args[3]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.DARK_RED + "Scale must be a number.");
                    ex.printStackTrace();
                    return true;
                }
            }

        }

        String temp = (ChatColor.GOLD + setting + ChatColor.WHITE + " was set from: " +
                ChatColor.RESET + ChatColor.GRAY + difficulty.getSetting(setting).getBase() +
                ChatColor.GREEN + " (" + sign + difficulty.getSetting(setting).getLinear() + ")" +
                ChatColor.DARK_GREEN + " (x" + difficulty.getSetting(setting).getScale() + ")");
        sender.sendMessage(temp);
        Bukkit.getLogger().info(temp);

        difficulty.setSetting(setting, base, scale, linear);
        if (linear == null) {
            linear = difficulty.getSetting(setting).getLinear();
        }

        if (linear < 0) {
            sign = "";
        }

        temp = (ChatColor.GOLD + setting + ChatColor.WHITE + " was set to:    " +
                ChatColor.RESET + ChatColor.GRAY + difficulty.getSetting(setting).getBase() +
                ChatColor.GREEN + " (" + sign + difficulty.getSetting(setting).getLinear() + ")" +
                ChatColor.DARK_GREEN + " (x" + difficulty.getSetting(setting).getScale() + ")");

        sender.sendMessage(temp);
        Bukkit.getLogger().info(temp);


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return difficulty.getAvailableSettings();
        } else if (args.length > 1) {
            return new ArrayList<>();
        }
        return null;
    }
}
