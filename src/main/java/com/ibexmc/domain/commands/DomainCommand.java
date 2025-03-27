package com.ibexmc.domain.commands;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.util.Permission;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DomainCommand implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete (@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> tabCompletions = new ArrayList<>();
        Map<Integer, String> arguments = StringFunctions.getArguments(args);
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean permField =  Permission.hasPermission(player, "domain.field");
            boolean permBypass =  Permission.hasPermission(player, "domain.bypass");
            boolean permAdmin =  Permission.hasPermission(player, "domain.admin");
            boolean hasBypass = PlayerFunctions.hasBypass(player);
            if (!arguments.containsKey(1)) {
                tabCompletions.add("help");
                tabCompletions.add("url");
                if (permField) {
                    if (Domain.getInstance().getDependencies().hasMyriadClans()) {
                        tabCompletions.add("clan");
                    }
                    tabCompletions.add("allow");
                    tabCompletions.add("buy");
                    tabCompletions.add("disallow");
                    tabCompletions.add("display");
                    tabCompletions.add("list");
                }
                if (permBypass) {
                    tabCompletions.add("bypass");
                }
                if (hasBypass) {
                    tabCompletions.add("get");
                    tabCompletions.add("give");
                    tabCompletions.add("search");
                }
                if (permAdmin) {
                    tabCompletions.add("debug");
                    tabCompletions.add("reload");
                    tabCompletions.add("version");
                    tabCompletions.add("purge_list");
                    tabCompletions.add("voucher");
                    // No completion for purge as it is console only
                }
            }
            if (arguments.containsKey(1) && !arguments.containsKey(2)) {
                if (permField) {
                    if (arguments.get(0).equalsIgnoreCase("display")) {
                        tabCompletions.add("clear");
                        tabCompletions.add("corners");
                        tabCompletions.add("cube");
                        tabCompletions.add("ring");
                    }
                    if (
                            arguments.get(0).equalsIgnoreCase("allow") ||
                                    arguments.get(0).equalsIgnoreCase("disallow")
                    ) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            tabCompletions.add(ChatColor.stripColor(onlinePlayer.getName()));
                        }
                    }
                    if (Domain.getInstance().getDependencies().hasMyriadClans()) {
                        if (arguments.get(0).equalsIgnoreCase("clan")) {
                            tabCompletions.add("allow");
                            tabCompletions.add("disallow");
                        }
                    }
                }
                if (hasBypass) {
                    if (arguments.get(0).equalsIgnoreCase("get")) {
                        for (IFieldConfig config : Domain.getInstance().getData().getDomainFieldConfigs()) {
                            tabCompletions.add(config.getBlockKey());
                        }
                    }
                    if (
                            arguments.get(0).equalsIgnoreCase("give") ||
                            arguments.get(0).equalsIgnoreCase("glow")
                    ) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            tabCompletions.add(ChatColor.stripColor(onlinePlayer.getName()));
                        }
                    }
                    if (arguments.get(0).equalsIgnoreCase("search")) {
                        tabCompletions.add("owner");
                        tabCompletions.add("type");
                        tabCompletions.add("world");
                    }
                }
                if (permAdmin) {
                    if (arguments.get(0).equalsIgnoreCase("purge")) {
                        tabCompletions.add("list");
                    }
                }
            }
            if (arguments.containsKey(2) && !arguments.containsKey(3)) {
                if (hasBypass) {
                    if (arguments.get(0).equalsIgnoreCase("give")) {
                        for (IFieldConfig config : Domain.getInstance().getData().getDomainFieldConfigs()) {
                            tabCompletions.add(config.getBlockKey());
                        }
                    }
                    if (arguments.get(0).equalsIgnoreCase("search")) {
                        if (arguments.get(1).equalsIgnoreCase("type")) {
                            for (IFieldConfig config : Domain.getInstance().getData().getDomainFieldConfigs()) {
                                tabCompletions.add(config.getBlockKey());
                            }
                        }
                        if (arguments.get(1).equalsIgnoreCase("world")) {
                            for (World world : Bukkit.getWorlds()) {
                                tabCompletions.add(world.getName());
                            }
                        }
                    }
                }
            }
        }
        return tabCompletions;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        CommandFunctions commandFunctions = new CommandFunctions(Domain.getInstance());
        Map<Integer, String> arguments = StringFunctions.getArguments(args);
        if (arguments.containsKey(0)) {
            switch (arguments.get(0)) {
                //region Voucher
                case "voucher":
                    commandFunctions.getVoucher(sender, arguments);
                    break;
                //endregion
                //region Expand
                case "expand":
                    commandFunctions.expand(sender, arguments);
                    break;
                //endregion
                //region Shrink
                case "shrink":
                    commandFunctions.shrink(sender, arguments);
                    break;
                //endregion
                //region Allow
                case "allow":
                    commandFunctions.allowPlayer(sender, arguments);
                    break;
                //endregion
                // region Back (from idle kick)
                case "back":
                    commandFunctions.idleBack(sender);
                    break;
                //endregion
                //region Buy
                case "buy":
                    commandFunctions.buy(sender);
                    break;
                //endregion
                //region Bypass
                case "bypass":
                    commandFunctions.bypass(sender);
                    break;
                //endregion
                //region Cancel
                case "cancel":
                    commandFunctions.cancel(sender);
                    break;
                //endregion
                //region Clan
                case "clan":
                    commandFunctions.clan(sender, arguments);
                    break;
                //endregion
                //region Debug
                case "debug":
                    commandFunctions.debug(sender);
                    break;
                //endregion
                //region Delete Block
                case "delete_block":
                    commandFunctions.delete_block(sender, arguments);
                    break;
                //endregion
                //region Disable Block
                case "disable_block":
                    commandFunctions.disable_block(sender, arguments);
                    break;
                //endregion
                //region Disallow
                case "disallow":
                    commandFunctions.disallowPlayer(sender, arguments);
                    break;
                //endregion
                //region Display
                case "display":
                    if (arguments.containsKey(1)) {
                        switch (arguments.get(1)) {
                            case "clear":
                                commandFunctions.clearVisualization(sender);
                                break;
                            case "corners":
                                commandFunctions.corners(sender);
                                break;
                            case "cube":
                                commandFunctions.cube(sender);
                                break;
                            case "ring":
                                commandFunctions.ring(sender);
                                break;
                            default:
                                commandFunctions.display(sender);
                                break;
                        }
                    } else {
                        commandFunctions.display(sender);
                    }
                    break;
                //endregion
                //region Get
                case "get":
                    commandFunctions.get(sender, arguments);
                    break;
                //endregion
                //region Give
                case "give":
                    commandFunctions.give(sender, arguments);
                    break;
                //endregion
                //region Glow
                case "glow":
                    commandFunctions.glow(sender, arguments);
                    break;
                //endregion
                //region Help TODO
                case "help":
                case "?":
                    commandFunctions.help(sender);
                    break;
                //endregion
                //region List
                case "list":
                    commandFunctions.list(sender);
                    break;
                //endregion
                // region Me
                case "me":
                    commandFunctions.me(sender, arguments);
                    break;
                //endregion
                //region OpenGUI
                case "open_gui":
                    commandFunctions.openMainGUI(sender, arguments);
                    break;
                //endregion
                //region OpenGUI
                case "open":
                    commandFunctions.openGUI(sender, arguments);
                    break;
                //endregion
                //region Player Move Event Toggle
                case "playermove":
                    commandFunctions.togglePlayerMove(sender);
                    break;
                //endregion
                //region Purge
                case "purge":
                    commandFunctions.purge(sender);
                    break;
                //endregion
                //region Purge List
                case "purge_list":
                    commandFunctions.purge_list(sender);
                    break;
                //endregion
                //region Reload
                case "reload":
                    commandFunctions.reload(sender);
                    break;
                //endregion
                //region Rent Confirmation
                case "rentconfirm":
                    commandFunctions.rentConfirm(sender, arguments);
                    break;
                //endregion
                //region Resize
                case "resize":
                    commandFunctions.getResizeWand(sender);
                    break;
                //endregion
                //region Search
                case "search":
                    commandFunctions.search(sender, arguments);
                    break;
                //endregion
                //region Spawn Event Toggle
                case "spawnevent":
                    commandFunctions.toggleSpawnEvent(sender);
                    break;
                //endregion
                //region Teleport To
                case "teleport_to":
                    commandFunctions.tp_to_block(sender, arguments);
                    break;
                //endregion
                //region URL
                case "url":
                    commandFunctions.displayURL(sender);
                    break;
                //endregion
                //region Version
                case "version":
                    commandFunctions.version(sender);
                    break;
                //endregion
                //region Who In Kick
                case "who_in_kick":
                    commandFunctions.whoInKick(sender, arguments);
                    break;
                //endregion
                case "transfer_to_file":
                    commandFunctions.transferToFile(sender);
                    break;
                case "transfer_to_mysql":
                    commandFunctions.transferToMySql(sender);
                    break;
                default:
                    Domain.getInstance().getMessage().send(sender, "command_invalid_command", "&4Invalid command. Type in /domain for options", null, true);
                    break;
            }
        } else {
            // invalid
            commandFunctions.help(sender);
        }

        return true;
    }
}
