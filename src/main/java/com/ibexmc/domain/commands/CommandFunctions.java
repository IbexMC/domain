package com.ibexmc.domain.commands;

import java.util.*;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.api.events.domain.DomainBypassChangeEvent;
import com.ibexmc.domain.dependencies.myriad.Myriad;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.domainblock.open.IOpenDomainBlock;
import com.ibexmc.domain.domainblock.open.Resize;
import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.domain.domainblock.storage.IDomainStorage;
import com.ibexmc.domain.domainblock.storage.MySQL;
import com.ibexmc.domain.domainblock.storage.StorageManager;
import com.ibexmc.domain.domainblock.storage.YamlFile;
import com.ibexmc.domain.gui.BlockStoreGUI;
import com.ibexmc.domain.gui.DomainBlockGiveGUI;
import com.ibexmc.domain.gui.MainGUI;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.Permission;
import com.ibexmc.domain.domainblock.*;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.*;
import com.ibexmc.internal.messaging.Error;
import com.ibexmc.internal.messaging.Message;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import com.ibexmc.domain.domainblock.vouchers.Voucher;
import org.bukkit.scheduler.BukkitRunnable;


public class CommandFunctions {

    Domain instance;
    DomainBlockManager domainBlockManager;
    Message message;

    public CommandFunctions(Domain instance) {
        this.instance = instance;
        domainBlockManager = new DomainBlockManager(instance);
        message = instance.getMessage();
    }

    /**
     * Expands the current Domain field the provided number of blocks in the direction specified
     * @param sender CommandSender sending the command
     * @param arguments Arguments provided to the command
     */
    public void expand(CommandSender sender, Map<Integer, String> arguments) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String direction = "";
                int blocks = 0;
                if (arguments.containsKey(1) && arguments.containsKey(2)) {
                    direction = arguments.get(1);
                    if (NumberFunctions.isInt(arguments.get(2))) {
                        blocks = NumberFunctions.stringToInt(arguments.get(2));
                    }
                    if (blocks > 0) {
                        DomainBlock domainBlock = domainBlockManager.getCurrentField(player);
                        if (domainBlock != null) {
                            if (domainBlockManager.isOwner(player, domainBlock)) {
                                // Player is the owner of the field they are standing in
                                Resize resize = new Resize(
                                        domainBlock.getPlacedId()
                                );
                                // Add this resize to the openBlocks
                                instance.getData().openBlocks.put(
                                        player.getUniqueId(),
                                        resize
                                );
                                OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.NA);
                                openDomainBlock.openEffects(player, domainBlock);
                                Location corner;
                                switch (direction) {
                                    case "n":
                                    case "north":
                                        if (domainBlock.getCorner1().getBlockZ() < domainBlock.getCorner2().getBlockZ()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ() - blocks));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ() - blocks));
                                        }
                                        break;
                                    case "s":
                                    case "south":
                                        if (domainBlock.getCorner1().getBlockZ() > domainBlock.getCorner2().getBlockZ()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ() + blocks));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ() + blocks));
                                        }
                                        break;
                                    case "w":
                                    case "west":
                                        if (domainBlock.getCorner1().getBlockX() < domainBlock.getCorner2().getBlockX()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX() - blocks, corner.getY(), corner.getZ()));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX() - blocks, corner.getY(), corner.getZ()));
                                        }
                                        break;
                                    case "e":
                                    case "east":
                                        if (domainBlock.getCorner1().getBlockX() > domainBlock.getCorner2().getBlockX()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX() + blocks, corner.getY(), corner.getZ()));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX() + blocks, corner.getY(), corner.getZ()));
                                        }
                                        break;
                                    case "u":
                                    case "up":
                                        if (domainBlock.getCorner1().getBlockY() > domainBlock.getCorner2().getBlockY()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX(), corner.getY() + blocks, corner.getZ()));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX(), corner.getY() + blocks, corner.getZ()));
                                        }
                                        break;
                                    case "d":
                                    case "down":
                                        if (domainBlock.getCorner1().getBlockY() < domainBlock.getCorner2().getBlockY()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX(), corner.getY() - blocks, corner.getZ()));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX(), corner.getY() - blocks, corner.getZ()));
                                        }
                                        break;
                                    default:
                                        // error - invalid direction
                                        message.send(
                                                player,
                                                "usage_expand",
                                                "Usage: /domain expand &b<direction> <blocks>&r\nDirection is: north/east/south/west/up/down",
                                                null,
                                                true
                                        );
                                        break;
                                }
                                if (resize.valid(player)) {
                                    resize.finish(player);
                                    resize.display(player);
                                } else {
                                    // error - unable to resize
                                    instance.getDebug().log(
                                            "CommandFunctions",
                                            "expand",
                                            "Expand failed due to invalid resize rules"
                                    );
                                }
                            } else {
                                // not the owner
                                message.send(
                                        player,
                                        "command_no_perm_do",
                                        "&4You do not have permission to do that",
                                        null,
                                        true
                                );
                            }
                        } else {
                            // not in a field
                            message.send(
                                    player,
                                    "command_not_in_field",
                                    "&4You are not currently in a Domain field",
                                    null,
                                    true
                            );
                        }
                    } else {
                        // error - invalid block count
                        message.send(
                                player,
                                "usage_expand",
                                "Usage: /domain expand &b<direction> <blocks>&r\nDirection is: north/east/south/west/up/down",
                                null,
                                true
                        );
                    }
                } else {
                    // usage
                    message.send(
                            player,
                            "usage_expand",
                            "Usage: /domain expand &b<direction> <blocks>&r\nDirection is: north/east/south/west/up/down",
                            null,
                            true
                    );
                }
            } else {
                // no console
                message.send(
                        sender,
                        "command_no_console",
                        "&4That command cannot be run from console",
                        null,
                        true
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "001",
                    "CommandFunctions",
                    "expand",
                    "Unexpected error",
                    ex.getMessage(),
                    Error.Severity.WARN,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Shrinks the current Domain field the provided number of blocks in the direction specified
     * @param sender CommandSender sending the command
     * @param arguments Arguments provided to the command
     */
    public void shrink(CommandSender sender, Map<Integer, String> arguments) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String direction = "";
                int blocks = 0;
                if (arguments.containsKey(1) && arguments.containsKey(2)) {
                    direction = arguments.get(1);
                    if (NumberFunctions.isInt(arguments.get(2))) {
                        blocks = NumberFunctions.stringToInt(arguments.get(2));
                    }
                    if (blocks > 0) {
                        DomainBlock domainBlock = domainBlockManager.getCurrentField(player);
                        if (domainBlock != null) {
                            if (domainBlockManager.isOwner(player, domainBlock)) {
                                // Player is the owner of the field they are standing in
                                Resize resize = new Resize(
                                        domainBlock.getPlacedId()
                                );
                                // Add this resize to the openBlocks
                                instance.getData().openBlocks.put(
                                        player.getUniqueId(),
                                        resize
                                );
                                OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.NA);
                                openDomainBlock.openEffects(player, domainBlock);
                                Location corner;
                                switch (direction) {
                                    case "n":
                                    case "north":
                                        if (domainBlock.getCorner1().getBlockZ() < domainBlock.getCorner2().getBlockZ()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ() + blocks));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ() + blocks));
                                        }
                                        break;
                                    case "s":
                                    case "south":
                                        if (domainBlock.getCorner1().getBlockZ() > domainBlock.getCorner2().getBlockZ()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ() - blocks));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX(), corner.getY(), corner.getZ() - blocks));
                                        }
                                        break;
                                    case "w":
                                    case "west":
                                        if (domainBlock.getCorner1().getBlockX() < domainBlock.getCorner2().getBlockX()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX() + blocks, corner.getY(), corner.getZ()));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX() + blocks, corner.getY(), corner.getZ()));
                                        }
                                        break;
                                    case "e":
                                    case "east":
                                        if (domainBlock.getCorner1().getBlockX() > domainBlock.getCorner2().getBlockX()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX() - blocks, corner.getY(), corner.getZ()));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX() - blocks, corner.getY(), corner.getZ()));
                                        }
                                        break;
                                    case "u":
                                    case "up":
                                        if (domainBlock.getCorner1().getBlockY() > domainBlock.getCorner2().getBlockY()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX(), corner.getY() - blocks, corner.getZ()));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX(), corner.getY() - blocks, corner.getZ()));
                                        }
                                        break;
                                    case "d":
                                    case "down":
                                        if (domainBlock.getCorner1().getBlockY() < domainBlock.getCorner2().getBlockY()) {
                                            corner = domainBlock.getCorner1().clone();
                                            resize.corner1(new Location(corner.getWorld(), corner.getX(), corner.getY() + blocks, corner.getZ()));
                                            resize.corner2(domainBlock.getCorner2());
                                        } else {
                                            resize.corner1(domainBlock.getCorner1());
                                            corner = domainBlock.getCorner2().clone();
                                            resize.corner2(new Location(corner.getWorld(), corner.getX(), corner.getY() + blocks, corner.getZ()));
                                        }
                                        break;
                                    default:
                                        // error - invalid direction
                                        message.send(
                                                player,
                                                "usage_shrink",
                                                "Usage: /domain shrink &b<direction> <blocks>&r\nDirection is: north/east/south/west/up/down",
                                                null,
                                                true
                                        );
                                        break;
                                }
                                if (resize.valid(player)) {
                                    resize.finish(player);
                                    resize.display(player);
                                } else {
                                    // error - unable to resize
                                    instance.getDebug().log(
                                            "CommandFunctions",
                                            "expand",
                                            "Expand failed due to invalid resize rules"
                                    );
                                }
                            } else {
                                // not the owner
                                message.send(
                                        player,
                                        "command_no_perm_do",
                                        "&4You do not have permission to do that",
                                        null,
                                        true
                                );
                            }
                        } else {
                            // not in a field
                            message.send(
                                    player,
                                    "command_not_in_field",
                                    "&4You are not currently in a Domain field",
                                    null,
                                    true
                            );
                        }
                    } else {
                        // error - invalid block count
                        message.send(
                                player,
                                "usage_shrink",
                                "Usage: /domain shrink &b<direction> <blocks>&r\nDirection is: north/east/south/west/up/down",
                                null,
                                true
                        );
                    }
                } else {
                    // usage
                    message.send(
                            player,
                            "usage_shrink",
                            "Usage: /domain shrink &b<direction> <blocks>&r\nDirection is: north/east/south/west/up/down",
                            null,
                            true
                    );
                }
            } else {
                // no console
                message.send(
                        sender,
                        "command_no_console",
                        "&4That command cannot be run from console",
                        null,
                        true
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "001",
                    "CommandFunctions",
                    "shrink",
                    "Unexpected error - ",
                    ex.getMessage(),
                    Error.Severity.WARN,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Displays the Domain support URL
     * @param sender CommandSender calling the command
     */
    public void displayURL(CommandSender sender) {
        Map<String, String> urlparameters = new HashMap<>();
        urlparameters.put("<%url%>", "https://torpkev.github.io/domain_docs/");
        message.send(
                sender,
                "command_url",
                "Domain URL: <%url%>",
                urlparameters,
                true
        );
    }

    /**
     * Reloads the config/data
     * @param sender CommandSender calling the command
     */
    public void reload(CommandSender sender) {
        boolean hasAdminPerm = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player != null) {
                hasAdminPerm = Permission.hasPermission(player, "domain.admin");
            }
        } else {
            hasAdminPerm = true;
        }
        if (hasAdminPerm) {
            message.send(
                    sender,
                    "command_reload",
                    "&4Reloading Domain",
                    null,
                    true
            );
            instance.clearData();
            instance.loadData();
            instance.reloadLocale();
        }
    }

    /**
     * Toggles debug mode
     * @param sender CommandSender sending the command
     */
    public void debug(CommandSender sender) {
        boolean hasAdminPerm = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player != null) {
                hasAdminPerm = Permission.hasPermission(player, "domain.admin");
            }
        } else {
            hasAdminPerm = true;
        }
        if (hasAdminPerm) {
            instance.getInternalData().setDebugMode(!instance.getInternalData().getDebugMode());
            if (instance.getInternalData().getDebugMode()) {
                // Debug is now on
                message.send(
                        sender,
                        "command_debug_enable",
                        "&2Enabling debug mode - &4Use caution - this could generate large log files",
                        null,
                        true
                );
                instance.getDebug().log("CommandFunctions", "Debug", "Enable debug command run by " + sender.getName());
            } else {
                // Debug is now off
                message.send(
                        sender,
                        "command_debug_disable",
                        "&4Disabling debug mode",
                        null,
                        true
                );
                instance.getDebug().log("CommandFunctions", "Debug", "Disable debug command run by " + sender.getName());
            }

        } else {
            message.send(
                    sender,
                    "command_no_perm",
                    "&4You do not have permission to use this command",
                    null,
                    true
            );
        }
    }

    /**
     * Toggles bypass mode for the player calling the command
     * @param sender CommandSender sending the command
     */
    public void bypass(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Bukkit.getServer().getPluginManager().callEvent(new DomainBypassChangeEvent(player)); // Call the DomainBypassChangeEvent event
        } else {
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    public void cancel(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (instance.getData().openBlocks.containsKey(player.getUniqueId())) {
                IOpenDomainBlock iOpenDomainBlock = instance.getData().openBlocks.get(player.getUniqueId());
                if (iOpenDomainBlock != null) {
                    if (iOpenDomainBlock instanceof Resize) {
                        Resize.removeWand(player);
                        instance.getData().openBlocks.remove(player.getUniqueId());
                        Visualize visualize = new Visualize(instance);
                        visualize.clearVisualization(player);
                        message.send(
                                sender,
                                "command_resize_cancel",
                                "&2Resize has been cancelled",
                                null,
                                true
                        );
                    } else {
                        message.send(
                                sender,
                                "command_resize_cancel_na",
                                "&4You are not currently resizing a field",
                                null,
                                true
                        );
                    }
                } else {
                    message.send(
                            sender,
                            "command_resize_cancel_na",
                            "&4You are not currently resizing a field",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        sender,
                        "command_resize_cancel_na",
                        "&4You are not currently resizing a field",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Displays the current Domain Block with default visualization type
     * @param sender CommandSender sending the command
     */
    public void display(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<DomainBlock> domainBlocks = domainBlockManager.playerDomainBlocks(player.getLocation());
            int fields = 0;
            if (domainBlocks != null) {
                for (DomainBlock domainBlock : domainBlocks) {
                    fields++;
                    if (domainBlockManager.isOwner(player, domainBlock)) {
                        Visualize visualize = new Visualize(instance);
                        visualize.displayField(
                                player,
                                domainBlock,
                                0,
                                true,
                                false,
                                false
                        );
                    }
                }
            }
            if (fields == 0) {
                message.send(
                        player,
                        "command_not_in_field",
                        "&4You are not currently in a Domain field",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Displays the current Domain Block with Ring visualization type
     * @param sender CommandSender sending the command
     */
    public void ring(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<DomainBlock> domainBlocks = domainBlockManager.playerDomainBlocks(player.getLocation());
            int fields = 0;
            if (domainBlocks != null) {
                for (DomainBlock domainBlock : domainBlocks) {
                    fields++;
                    if (domainBlockManager.isOwner(player, domainBlock)) {
                        Visualize visualize = new Visualize(instance);
                        visualize.displayField(
                                player,
                                domainBlock,
                                0,
                                true,
                                false,
                                false,
                                Visualize.VisualizationType.RING,
                                domainBlock.getCorner1().clone(),
                                domainBlock.getCorner2().clone()
                        );
                    }
                }
            }
            if (fields == 0) {
                message.send(
                        player,
                        "command_not_in_field",
                        "&4You are not currently in a Domain field",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Clears the current visualization
     * @param sender CommandSender sending the command
     */
    public void clearVisualization(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Visualize visualize = new Visualize(instance);
            visualize.clearVisualization(player);
        } else {
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Displays the current Domain Block with Corner visualization type
     * @param sender CommandSender sending the command
     */
    public void corners(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<DomainBlock> domainBlocks = domainBlockManager.playerDomainBlocks(player.getLocation());
            int fields = 0;
            if (domainBlocks != null) {
                for (DomainBlock domainBlock : domainBlocks) {
                    fields++;
                    if (domainBlockManager.isOwner(player, domainBlock)) {
                        Visualize visualize = new Visualize(instance);
                        visualize.displayField(
                                player,
                                domainBlock,
                                0,
                                true,
                                false,
                                false,
                                Visualize.VisualizationType.CORNERS,
                                domainBlock.getCorner1().clone(),
                                domainBlock.getCorner2().clone()
                        );
                    }
                }
            }
            if (fields == 0) {
                message.send(
                        player,
                        "command_not_in_field",
                        "&4You are not currently in a Domain field",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Displays the current Domain Block with Cube visualization type
     * @param sender CommandSender sending the command
     */
    public void cube(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<DomainBlock> domainBlocks = domainBlockManager.playerDomainBlocks(player.getLocation());
            int fields = 0;
            if (domainBlocks != null) {
                for (DomainBlock domainBlock : domainBlocks) {
                    fields++;
                    if (domainBlockManager.isOwner(player, domainBlock)) {
                        Visualize visualize = new Visualize(instance);
                        visualize.displayField(
                                player,
                                domainBlock,
                                0,
                                true,
                                false,
                                false,
                                Visualize.VisualizationType.CUBE,
                                domainBlock.getCorner1().clone(),
                                domainBlock.getCorner2().clone()
                        );
                    }
                }
            }
            if (fields == 0) {
                message.send(
                        player,
                        "command_not_in_field",
                        "&4You are not currently in a Domain field",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Lists all the Domain blocks the player is in
     * @param sender CommandSender sending the command
     */
    public void list(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<DomainBlock> lstPB = domainBlockManager.playerDomainBlocks(player.getLocation());
            int fields = 0;
            if (lstPB != null) {
                for (DomainBlock pb : lstPB)  {
                    fields++;
                    if (fields == 1) {
                        message.send(
                                sender,
                                "command_list_player_owned_fields",
                                "Domain Fields at this location:",
                                null,
                                true
                        );
                    }
                    if (domainBlockManager.isOwner(player, pb))  {
                        HashMap<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%blockname%>", pb.getFieldName() + "&r");
                        placeHolder.put("<%blocktype%>", pb.getConfig().getDisplayName() + "&r");

                        String blockInfo = "<%blockname%> - <%blocktype%> &r";
                        if (placeHolder != null) {
                            for (Map.Entry<String, String> params : placeHolder.entrySet()) {
                                blockInfo = ColorFunctions.colorToString(blockInfo.replace(params.getKey(), params.getValue()));
                                instance.getDebug().log("CommandFunctions",  "list(CommandSender)", blockInfo);
                            }
                        }

                        TextComponent msg = new TextComponent(
                                "* "
                        );

                        TextComponent info_msg = new TextComponent(blockInfo);
                        msg.addExtra(info_msg);

                        TextComponent extra_msg = new TextComponent(instance.getLocale().localeText("command_list_fields_open", " &r[&bOpen&r]", null));
                        extra_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(instance.getLocale().localeText("command_list_fields_open_info", "&fThis will open the Domain block menu", null)).create()));
                        extra_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/domain open_gui " + pb.getPlacedId()));
                        msg.addExtra(extra_msg);

                        sender.spigot().sendMessage(msg);
                    }
                }
            }
            if (fields == 0) {
                message.send(
                        player,
                        "command_not_in_field",
                        "&4You are not currently in a Domain field",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Toggles the glow status of a player
     * @param sender CommandSender sending the command
     * @param arguments Arguments provided to the command
     */
    public void glow(CommandSender sender, Map<Integer, String> arguments) {
        boolean allowGlow = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.admin")) {
                allowGlow = true;
            } else {
                message.send(
                        player,
                        "command_no_perm_do",
                        "&4You do not have permission to do that",
                        null,
                        true
                );
                return;
            }
        } else {
            allowGlow = true;
        }
        if (allowGlow) {
            if (arguments.containsKey(1)) {
                Player glowPlayer = PlayerFunctions.playerFromName(arguments.get(1));
                if (glowPlayer != null) {
                    Map<String, String> glowParam = new HashMap<>();
                    glowParam.put("<%player%>", glowPlayer.getDisplayName());
                    if (glowPlayer.isGlowing()) {
                        message.send(
                                sender,
                                "glow_set_off",
                                "&aGlow disabled for <%player%>",
                                glowParam,
                                true
                        );
                        glowPlayer.setGlowing(false);
                    } else {
                        message.send(
                                sender,
                                "glow_set_on",
                                "&aGlow enabled for <%player%>",
                                glowParam,
                                true
                        );
                        glowPlayer.setGlowing(true);
                    }
                } else {
                    message.send(
                            sender,
                            "glow_no_player",
                            "&cInvalid Player",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        sender,
                        "glow_no_player",
                        "&cInvalid Player",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_perm_do",
                    "&4You do not have permission to do that",
                    null,
                    true
            );
        }
    }

    /**
     * Opens the Domain Buy GUI
     * @param sender CommandSender sending the command
     */
    public void buy(CommandSender sender) {
        if (instance.config().isDomainShop()) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Permission.hasPermission(player, "domain.field")) {
                    BlockStoreGUI buyGUI = new BlockStoreGUI(instance);
                    buyGUI.setPlayer(player);
                    player.openInventory(buyGUI.getInventory());
                } else {
                    message.send(
                            player,
                            "command_no_perm_do",
                            "&4You do not have permission to do that",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        sender,
                        "command_no_console",
                        "&4Command can only be called by an online player",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_buy_disabled",
                    "&4Domain shop is not enabled on this server",
                    null,
                    true
            );
        }
    }

    /**
     * Allows a player to the field you're currently in
     * @param sender CommandSender sending the command
     * @param arguments Command arguments
     */
    public void allowPlayer(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID addPlayerUUID = null;
            Map<String, String> placeHolder = new HashMap<>();
            if (arguments.containsKey(1)) {
                OfflinePlayer addOfflinePlayer = PlayerFunctions.offlinePlayerFromName(arguments.get(1));
                if (addOfflinePlayer != null) {
                    if (addOfflinePlayer.hasPlayedBefore()) {
                        addPlayerUUID = addOfflinePlayer.getUniqueId();
                        if (addOfflinePlayer.getName() != null) {
                            placeHolder.put("<%player%>", addOfflinePlayer.getName());
                        }
                    }
                }
            } else {
                // usage
                message.send(
                        player,
                        "usage_allow",
                        "Usage: /domain allow &b<player name>&r",
                        null,
                        true
                );
                return;
            }
            if (addPlayerUUID == null) {
                // invalid player
                message.send(
                        player,
                        "allow_player_not_found",
                        "&4Unable to allow player.  Player not found",
                        null,
                        true
                );
                message.send(
                        player,
                        "allow_player_not_found1",
                        "&4Player add cancelled.  You are now returned to chat.",
                        null,
                        true
                );
                return;
            }
            DomainBlock domainBlock = domainBlockManager.getCurrentField(player);
            if (domainBlock != null) {
                if (domainBlockManager.isOwner(player, domainBlock)) {
                    if (domainBlock.getAllowedPlayers().contains(addPlayerUUID)) {
                        message.send(
                                player,
                                "allow_player_already_in",
                                "&4Player is already allowed into this field",
                                placeHolder,
                                true
                        );
                        message.send(
                                player,
                                "allow_player_not_found1",
                                "&4Player add cancelled.  You are now returned to chat.",
                                placeHolder,
                                true
                        );
                    } else {
                        domainBlock.addAllowedPlayer(addPlayerUUID);
                        domainBlock.setDirty(true);
                        domainBlockManager.domainBlockSave(domainBlock);
                        message.send(
                                player,
                                "allow_player_found",
                                "&2SUCCESS - Player has been added to the field",
                                placeHolder,
                                true
                        );
                        Player allowPlayer = Bukkit.getPlayer(addPlayerUUID);
                        if (allowPlayer != null) {
                            placeHolder.put("<%name%>", ChatColor.stripColor(ColorFunctions.colorToString(domainBlock.getFieldName())));
                            placeHolder.put("<%player%>", player.getName());
                            message.send(
                                    allowPlayer,
                                    "allow_player_alert_added",
                                    "&cYou have been given access to the <%name%> field by <%player%>",
                                    placeHolder,
                                    true
                            );
                        }
                    }
                } else {
                    // not the owner
                    message.send(
                            player,
                            "command_no_perm_do",
                            "&4You do not have permission to do that",
                            null,
                            true
                    );
                }
            } else {
                // not in a field
                message.send(
                        player,
                        "command_not_in_field",
                        "&4You are not currently in a Domain field",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Disallows a player from the field you're currently in
     * @param sender CommandSender sending the command
     * @param arguments Command arguments
     */
    public void disallowPlayer(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID removePlayerUUID = null;
            Map<String, String> placeHolder = new HashMap<>();
            placeHolder.put("<%player%>", arguments.get(1));
            if (arguments.containsKey(1)) {
                OfflinePlayer removeOfflinePlayer = PlayerFunctions.offlinePlayerFromName(arguments.get(1));
                if (removeOfflinePlayer != null) {
                    if (removeOfflinePlayer.hasPlayedBefore()) {
                        removePlayerUUID = removeOfflinePlayer.getUniqueId();
                        if (removeOfflinePlayer.getName() != null) {
                            placeHolder.put("<%player%>", removeOfflinePlayer.getName());
                        }
                    }
                }
            } else {
                // usage
                message.send(
                        player,
                        "usage_disallow",
                        "Usage: /domain disallow &b<player name>&r",
                        placeHolder,
                        true
                );
                return;
            }
            if (removePlayerUUID == null) {
                // invalid player
                message.send(
                        player,
                        "disallow_player_not_found",
                        "&4Unable to disallow player.  Player not found",
                        placeHolder,
                        true
                );
                message.send(
                        player,
                        "disallow_player_not_found1",
                        "&4Player disallow cancelled.  You are now returned to chat.",
                        placeHolder,
                        true
                );
                return;
            }
            DomainBlock domainBlock = domainBlockManager.getCurrentField(player);
            if (domainBlock != null) {
                if (domainBlockManager.isOwner(player, domainBlock)) {
                    if (domainBlock.getAllowedPlayers().contains(removePlayerUUID)) {
                        domainBlock.removeAllowedPlayer(removePlayerUUID);
                        domainBlock.setDirty(true);
                        domainBlockManager.domainBlockSave(domainBlock);
                        message.send(
                                player,
                                "remove_player_success_owner",
                                "&2SUCCESS - <%player%> has been removed from access to this field",
                                null,
                                true
                        );
                        Player disallowPlayer = Bukkit.getPlayer(removePlayerUUID);
                        if (disallowPlayer != null) {
                            placeHolder.put("<%name%>", ChatColor.stripColor(ColorFunctions.colorToString(domainBlock.getFieldName())));
                            placeHolder.put("<%player%>", player.getName());
                            message.send(
                                    player,
                                    "disallow_player_alert_removed",
                                    "&cYou have been removed from access to the <%name%> field by <%player%>",
                                    placeHolder,
                                    true
                            );
                        }
                    } else {
                        message.send(
                                player,
                                "remove_player_fail_not_allowed",
                                "Unable to remove player, they are not allowed to the field",
                                null,
                                true
                        );
                        message.send(
                                player,
                                "disallow_player_not_found1",
                                "&4Player disallow cancelled.  You are now returned to chat.",
                                null,
                                true
                        );
                    }
                } else {
                    // not the owner
                    message.send(
                            player,
                            "command_no_perm_do",
                            "&4You do not have permission to do that",
                            null,
                            true
                    );
                }
            } else {
                // not in a field
                message.send(
                        player,
                        "command_not_in_field",
                        "&4You are not currently in a Domain field",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Gets a Domain Block if type is provided in arguments, if not, opens the Get GUI
     * @param sender CommandSender that sends the command
     * @param arguments Arguments provided with the command
     */
    public void get(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.admin")) {
                if (arguments.containsKey(1)) {
                    if ( instance.getData().getDomainFieldConfigs() != null) {
                        for (IFieldConfig config : instance.getData().getDomainFieldConfigs()) {
                            if (config != null) {
                                if (config.getBlockKey().equalsIgnoreCase(arguments.get(1))) {
                                    InventoryFunctions.addItemsToPlayerInventory(
                                            player,
                                            config.getItem(),
                                            true
                                    );
                                    message.send(
                                            player,
                                            "command_give_success",
                                            "&2SUCCESS &f- Domain block has been placed in your inventory",
                                            null,
                                            true
                                    );
                                    return;
                                }
                            }
                        }
                        // error - config not found
                        Map<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%blocklist%>", instance.getData().getBlockList());
                        message.send(
                                player,
                                "command_give_invalid_type",
                                "&4That block does not exist.  Valid block names are: <%blocklist%>",
                                placeHolder,
                                true
                        );
                    } else {
                        // no configs
                        Map<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%blocklist%>", instance.getData().getBlockList());
                        message.send(
                                player,
                                "command_give_invalid_type",
                                "&4That block does not exist.  Valid block names are: <%blocklist%>",
                                placeHolder,
                                true
                        );
                    }
                } else {
                    DomainBlockGiveGUI gui = new DomainBlockGiveGUI(instance);
                    gui.setPlayer(player);
                    player.openInventory(gui.getInventory());
                }
            } else {
                message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
            }
        } else {
            message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
        }
    }

    /**
     * Gives the player calling the command a resize wand if they are the owner of the field they are in
     * as well as putting the block into resize mode
     * @param sender CommandSender that sends the command
     */
    public void getResizeWand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DomainBlock domainBlock = domainBlockManager.getCurrentField(player);
            if (domainBlock != null) {
                if (domainBlockManager.isOwner(player, domainBlock)) {
                    Map<String, String> placeHolder = new HashMap<>();
                    placeHolder.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
                    ItemStack wand = Resize.wand();
                    if (InventoryFunctions.addItemsToPlayerInventory(player, wand, false)) {
                        message.send(
                                player,
                                "block_opened_to_resize",
                                "This <%blocktype%> field has been opened for resize",
                                placeHolder,
                                true
                        );
                        message.send(
                                player,
                                "block_opened_to_resize1",
                                "Use the wand to set each corner of the desired size",
                                placeHolder,
                                true
                        );
                        message.send(
                                player,
                                "block_opened_to_resize2",
                                "Left click corner 1 and Right click corner 2",
                                placeHolder,
                                true
                        );
                        message.send(
                                player,
                                "block_opened_to_resize3",
                                "When you have set both corners, right click the Domain block with the wand to set the field size",
                                placeHolder,
                                true
                        );
                        instance.getData().openBlocks.put(
                                player.getUniqueId(),
                                new Resize(
                                        domainBlock.getPlacedId()
                                )
                        );
                        OpenDomainBlock openDomainBlock = new OpenDomainBlock(domainBlock.getPlacedId(), OpenDomainBlockType.NA);
                        openDomainBlock.openEffects(player, domainBlock);
                    } else {
                        // Unable to give the player the resize wand
                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                        message.send(
                                player,
                                "unable_give_resize_wand",
                                "&4Sorry, you must have an open inventory slot to get the resize wand",
                                null,
                                true
                        );
                    }
                } else {
                    message.send(
                            player,
                            "",
                            "",
                            null,
                            true
                    );
                    message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                }
            } else {
                message.send(
                        player,
                        "no_field",
                        "&4No Domain field found",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
        }
    }

    /**
     * Gives the specified Domain Block to the player
     * /domain give [player] [blocktype]
     * @param sender CommandSender sending the message
     * @param arguments Arguments provided with the command
     */
    public void give(CommandSender sender, Map<Integer, String> arguments) {
        boolean canGive = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerFunctions.hasBypass(player)) {
                canGive = true;
            } else {
                message.send(
                        player,
                        "",
                        "",
                        null,
                        true
                );
                message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
            }
        } else {
            canGive = true;
        }

        if (canGive) {
            if (arguments.containsKey(1) && arguments.containsKey(2)) {
                Player givePlayer = PlayerFunctions.playerFromName(arguments.get(1));
                if (givePlayer == null) {
                    // invalid player
                    message.send(
                            sender,
                            "command_give_to_invalid_player",
                            "&4Invalid player",
                            null,
                            true
                    );
                    return;
                }
                if (instance.getData().getDomainFieldConfigs() != null) {
                    for (IFieldConfig config : instance.getData().getDomainFieldConfigs()) {
                        if (config != null) {
                            if (config.getBlockKey().equalsIgnoreCase(arguments.get(2))) {
                                InventoryFunctions.addItemsToPlayerInventory(
                                        givePlayer,
                                        config.getItem(),
                                        true
                                );
                                message.send(
                                        givePlayer,
                                        "command_give_success",
                                        "&2SUCCESS &f- Domain block has been placed in your inventory",
                                        null,
                                        true
                                );
                                return;
                            }
                        }
                    }
                    // error - config not found
                    Map<String, String> placeHolder = new HashMap<>();
                    placeHolder.put("<%blocklist%>", instance.getData().getBlockList());
                    message.send(
                            sender,
                            "command_give_invalid_type",
                            "&4That block does not exist.  Valid block names are: <%blocklist%>",
                            placeHolder,
                            true
                    );
                } else {
                    // no configs
                    HashMap<String, String> placeHolder = new HashMap<String, String>();
                    placeHolder.put("<%blocklist%>", instance.getData().getBlockList());
                    message.send(
                            sender,
                            "command_give_invalid_type",
                            "&4That block does not exist.  Valid block names are: <%blocklist%>",
                            placeHolder,
                            true
                    );
                }
            } else {
                // error usage
                message.send(
                        sender,
                        "usage_give",
                        "/domain give &b<player name> &b<block name>",
                        null,
                        true
                );
            }
        }
    }

    /**
     * Adds or removes clans from the current Domain field
     * @param sender CommandSender sending the command
     * @param arguments Arguments sent with the command
     */
    public void clan(CommandSender sender, Map<Integer, String> arguments) {
        if (instance.getDependencies().hasMyriadClans()) {
            if (arguments.containsKey(1) && arguments.containsKey(2)) {
                String clanTag = ChatColor.stripColor(ColorFunctions.colorToString(arguments.get(2).toUpperCase()));
                Myriad myriad = new Myriad(instance);
                if (myriad.validClan(clanTag)) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        DomainBlock domainBlock = domainBlockManager.getCurrentField(player);
                        if (domainBlock != null) {
                            if (domainBlockManager.isOwner(player, domainBlock)) {
                                Map<String, String> placeHolder = new HashMap<>();
                                placeHolder.put("<%clan%>", clanTag);
                                switch (arguments.get(1).toLowerCase()) {
                                    case "allow":
                                        if (domainBlock.getAllowedClans().contains(clanTag)) {
                                            // already allowed
                                            message.send(
                                                    player,
                                                    "clan_add_already_in",
                                                    "&cUnable to add <%clan%> to this field, as it is already allowed",
                                                    placeHolder,
                                                    true
                                            );
                                        } else {
                                            domainBlock.addAllowedClan(clanTag);
                                            domainBlock.setDirty(true);
                                            domainBlockManager.domainBlockSave(domainBlock);
                                            message.send(
                                                    player,
                                                    "clan_add_allow_success",
                                                    "Clan <%clan%> has been allowed to this field",
                                                    placeHolder,
                                                    true
                                            );
                                        }
                                        break;
                                    case "disallow":
                                        if (domainBlock.getAllowedClans().contains(clanTag)) {
                                            domainBlock.removeAllowedClan(clanTag);
                                            domainBlock.setDirty(true);
                                            domainBlockManager.domainBlockSave(domainBlock);
                                            message.send(
                                                    player,
                                                    "clan_remove_success",
                                                    "Clan <%clan%> has been removed from access to this field",
                                                    placeHolder,
                                                    true
                                            );
                                        } else {
                                            // not allowed
                                            message.send(
                                                    player,
                                                    "clan_remove_not_in",
                                                    "&cUnable to remove <%clan%> from this field, it is not currently allowed",
                                                    placeHolder,
                                                    true
                                            );
                                        }
                                        break;
                                    default:
                                        // usage
                                        message.send(
                                                player,
                                                "command_clan_usage",
                                                "Usage: /domain clan &2allow&r|&4disallow&r|&b <clan>",
                                                null,
                                                true
                                        );
                                        break;
                                }
                            } else {
                                // not the owner
                                message.send(
                                        player,
                                        "",
                                        "",
                                        null,
                                        true
                                );
                                message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                            }
                        } else {
                            // not in a field
                            message.send(
                                    player,
                                    "",
                                    "",
                                    null,
                                    true
                            );
                            message.send(player,"command_not_in_field","&4You are not currently in a Domain field",null,true);
                        }
                    } else {
                        // no console
                        message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
                    }
                } else {
                    // error - invalid clan tag
                    message.send(
                            sender,
                            "clan_add_allow_fail",
                            "&4Unable to add clan to field - Invalid clan tag",
                            null,
                            true
                    );
                }
            } else {
                // usage
                message.send(
                        sender,
                        "command_clan_usage",
                        "Usage: /domain clan &2allow&r|&4disallow&r|&b <clan>",
                        null,
                        true
                );
            }
        } else {
            // error - no clan support
            message.send(
                    sender,
                    "command_clan_req_simpleclans",
                    "&4The /domain clan command requires Myriad Clans",
                    null,
                    true
            );
        }
    }

    /**
     * Searches for Domain Blocks by Owner,Type, or World
     * @param sender CommandSender sending the command
     * @param arguments Arguments sent with the command
     */
    public void search(CommandSender sender, Map<Integer, String> arguments) {
        boolean canSearch = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.admin")) {
                canSearch = true;
            }
            if (PlayerFunctions.hasBypass(player)) {
                canSearch = true;
            }
        } else {
            canSearch = true;
        }
        if (canSearch) {
            if (arguments.containsKey(1) && arguments.containsKey(2)) {
                String searchOwner = "";
                String searchType = "";
                String searchWorld = "";
                switch (arguments.get(1).toLowerCase()) {
                    case "owner":
                        searchOwner = arguments.get(2);
                        break;
                    case "type":
                        searchType = arguments.get(2);
                        break;
                    case "world":
                        searchWorld = arguments.get(2);
                        break;
                    default:
                        // error - usage
                        message.send(
                                sender,
                                "usage_search",
                                "Usage: /domain search &bowner&f|&btype&f|&bworld &6<search term>&r",
                                null,
                                true
                        );
                        return;
                }
                List<DomainBlock> domainBlocks = new ArrayList<>();
                for (DomainBlock domainBlock : domainBlockManager.getAllDomainBlocks()) {
                    if (!StringFunctions.isNullOrEmpty(searchOwner)) {
                        if (domainBlock.getOwner() != null) {
                            OfflinePlayer offlinePlayer = PlayerFunctions.offlinePlayerFromName(searchOwner);
                            if (offlinePlayer != null) {
                                if (offlinePlayer.hasPlayedBefore()) {
                                    if (domainBlock.getOwner().equals(offlinePlayer.getUniqueId())) {
                                        domainBlocks.add(domainBlock);
                                    }
                                }
                            }
                        }
                    }
                    if (!StringFunctions.isNullOrEmpty(searchType)) {
                        if (domainBlock.getConfig().getBlockKey().equalsIgnoreCase(searchType)) {
                            domainBlocks.add(domainBlock);
                        }
                    }
                    if (!StringFunctions.isNullOrEmpty(searchWorld)) {
                        World world = Bukkit.getWorld(searchWorld);
                        if (world != null) {
                            if (domainBlock.getBlockLocation().getWorld().getUID().equals(world.getUID())) {
                                domainBlocks.add(domainBlock);
                            }
                        }
                    }

                }
                if (domainBlocks.size() == 0) {
                    message.send(
                            sender,
                            "command_search_no_results",
                            "&4No Domain fields found that match your search criteria",
                            null,
                            true
                    );
                    return;
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    final int pageItems = 10; // 10 items per page
                    int pageNum = 1; // Default to page 1
                    if (arguments.containsKey(3)) {
                        if (NumberFunctions.isInt(arguments.get(3))) {
                            pageNum = NumberFunctions.stringToInt(arguments.get(3));
                        }
                    }
                    pageNum = pageNum - 1; // -1 from pageNum as we're working with a 0 based array
                    List<List<DomainBlock>> pages = new ArrayList<>();
                    // Break the list into sub-lists
                    for (int i = 0; i < domainBlocks.size(); i += pageItems) {
                        pages.add(domainBlocks.subList(i, Math.min(i + pageItems, domainBlocks.size())));
                    }
                    if (pages.size() > 0) {
                        if (pageNum <= pages.size()) {
                            List<DomainBlock> lstDomainBlock = pages.get(pageNum);
                            if (lstDomainBlock.size() > 0) {
                                message.send(
                                        sender,
                                        "command_search_header",
                                        "&5Search Results",
                                        null,
                                        true
                                );
                            }
                            for (DomainBlock pagedDomainBlock : lstDomainBlock) {
                                String ownerName = "Unknown";
                                if (pagedDomainBlock.isAdminBlock()) {
                                    ownerName = "Admin Block";
                                } else {
                                    OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(pagedDomainBlock.getOwner());
                                    if (offlineOwner.hasPlayedBefore()) {
                                        ownerName = offlineOwner.getName();
                                    }
                                }
                                HashMap<String, String> placeHolder = new HashMap<>();
                                placeHolder.put("<%owner%>", ownerName);
                                placeHolder.put("<%blocktype%>", ChatColor.stripColor(pagedDomainBlock.getConfig().getDisplayName()));
                                placeHolder.put("<%name%>", ChatColor.stripColor(pagedDomainBlock.getFieldName()));
                                placeHolder.put("<%location%>", WorldFunctions.simpleReadable(pagedDomainBlock.getBlockLocation()));
                                String blockInfo = " <%name%>";
                                for (Map.Entry<String, String> params : placeHolder.entrySet()) {
                                    blockInfo = ColorFunctions.colorToString(blockInfo.replace(params.getKey(), params.getValue()));
                                }

                                TextComponent msg = new TextComponent(
                                        ""
                                );

                                // Open
                                TextComponent open_msg = new TextComponent(instance.getLocale().localeText("command_list_fields_open", " &r[&bOpen&r]", null));
                                open_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(instance.getLocale().localeText("command_list_fields_open_info", "&fThis will open the Domain block menu", null)).create()));
                                open_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/domain open_gui " + pagedDomainBlock.getPlacedId()));
                                msg.addExtra(open_msg);
                                // Teleport
                                Player tpPlayer = (Player) sender;
                                TextComponent tp_msg = new TextComponent(instance.getLocale().localeText("command_search_player_fields_tp", " &r[&bTP&r]", null));
                                tp_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(instance.getLocale().localeText("command_search_player_fields_tp_info", "&fThis will teleport you to the closest available spot to the domain block", null)).create()));
                                tp_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/domain teleport_to " + pagedDomainBlock.getPlacedId()));
                                msg.addExtra(tp_msg);
                                if (Permission.hasPermission(player, "domain.admin")) {
                                    // Disable
                                    TextComponent disable_msg = new TextComponent(instance.getLocale().localeText("command_search_player_fields_disable", " &r[&9Disable&r]", null));
                                    disable_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(instance.getLocale().localeText("command_search_player_fields_disable_info", "&fThis will disable the block", null)).create()));
                                    disable_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/domain disable_block " + pagedDomainBlock.getPlacedId()));
                                    msg.addExtra(disable_msg);
                                    // Delete
                                    TextComponent delete_msg = new TextComponent(instance.getLocale().localeText("command_search_player_fields_delete", " &r[&4Delete&r]", null));
                                    delete_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(instance.getLocale().localeText("command_search_player_fields_delete_info", "&fThis will delete the block", null)).create()));
                                    delete_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/domain delete_block " + pagedDomainBlock.getPlacedId()));
                                    msg.addExtra(delete_msg);
                                }

                                TextComponent info_msg = new TextComponent(blockInfo);
                                info_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder
                                        (
                                                instance.getLocale().localeText(
                                                        "search_hover_text",
                                                        "Owner: <%owner%>\n&rType: <%blocktype%>\n&rLocation: <%location%>",
                                                        placeHolder
                                                )
                                        ).create()
                                        )
                                );
                                msg.addExtra(info_msg);
                                sender.spigot().sendMessage(msg);

                            }
                            if ((pageNum + 1) < pages.size()) {
                                int nextPage = pageNum + 2;
                                // add a next
                                TextComponent paging = new TextComponent(
                                        instance.getLocale().localeText("next", "&f[&bNext&f]", null)
                                );
                                String args = "";
                                for (Map.Entry<Integer, String> arg : arguments.entrySet()) {
                                    if (arg.getKey() < 3) {
                                        args = args + " " + arg.getValue();
                                    } else {
                                        args = args + " " + pageNum + 1;
                                    }
                                }
                                paging.setClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                "/domain " + arguments.get(0) + " " + arguments.get(1) + " " + arguments.get(2) + " " + nextPage
                                        )
                                );

                                sender.spigot().sendMessage(paging);
                            }
                        }
                    } else {
                        message.send(
                                sender,
                                "command_search_no_results",
                                "&4No Domain fields found that match your search criteria",
                                null,
                                true
                        );
                    }
                } else {
                    // no paging
                    for (DomainBlock notPagedDomainBlock : domainBlocks) {
                        HashMap<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%placed_id%>", "" + notPagedDomainBlock.getPlacedId());
                        placeHolder.put("<%name%>", ChatColor.stripColor(notPagedDomainBlock.getFieldName()));
                        placeHolder.put("<%blocktype%>", ChatColor.stripColor(notPagedDomainBlock.getConfig().getDisplayName()));
                        placeHolder.put("<%location%>", WorldFunctions.simpleReadable(notPagedDomainBlock.getBlockLocation()));
                        message.send(
                                sender,
                                "search_nonpaged",
                                "Id: <%placed_id%> Name: <%name%> Type: <%blocktype%> Location <%location%>",
                                placeHolder,
                                true
                        );
                    }
                }

            } else {
                message.send(
                        sender,
                        "usage_search",
                        "Usage: /domain search &bowner&f|&btype&f|&bworld &6<search term>&r",
                        null,
                        true
                );
            }
        } else {
            // no perm
            message.send(
                    sender,
                    "command_no_perm_do",
                    "&4You do not have permission to do that",
                    null,
                    true
            );
        }
    }

    /**
     * Gets all Domain fields belonging the player sending the command
     * @param sender CommandSender sending the command
     * @param arguments Arguments sent with the command
     */
    public void me(CommandSender sender, Map<Integer, String> arguments) {
        boolean canSearch = false;
        Player player = (Player) sender;
        if (sender instanceof Player) {
            if (Permission.hasPermission(player, "domain.field")) {
                canSearch = true;
            }
        } else {
            // error
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
            return;
        }
        if (canSearch) {
            // Get all fields that belong to the player calling the command
            List<DomainBlock> domainBlocks = new ArrayList<>();
            for (DomainBlock domainBlock : domainBlockManager.getAllDomainBlocks()) {
                if (domainBlock.getOwner() != null) {
                    if (domainBlock.getOwner().equals(player.getUniqueId())) {
                        domainBlocks.add(domainBlock);
                    }
                }
            }
            // Return message if no results found
            if (domainBlocks.size() == 0) {
                message.send(
                        sender,
                        "command_me_no_results",
                        "&4No Domain fields found",
                        null,
                        true
                );
                return;
            }

            final int pageItems = 10; // 10 items per page
            int pageNum = 1; // Default to page 1
            if (arguments.containsKey(1)) {
                if (NumberFunctions.isInt(arguments.get(1))) {
                    pageNum = NumberFunctions.stringToInt(arguments.get(1));
                }
            }
            pageNum = pageNum - 1; // -1 from pageNum as we're working with a 0 based array
            List<List<DomainBlock>> pages = new ArrayList<>();
            // Break the list into sub-lists
            for (int i = 0; i < domainBlocks.size(); i += pageItems) {
                pages.add(domainBlocks.subList(i, Math.min(i + pageItems, domainBlocks.size())));
            }
            if (pages.size() > 0) {
                if (pageNum <= pages.size()) {
                    List<DomainBlock> lstDomainBlock = pages.get(pageNum);
                    if (lstDomainBlock.size() > 0) {
                        message.send(
                                sender,
                                "command_me_header",
                                "&5Your Domain Fields",
                                null,
                                true
                        );
                    }
                    for (DomainBlock pagedDomainBlock : lstDomainBlock) {
                        String ownerName = "Unknown";
                        if (pagedDomainBlock.isAdminBlock()) {
                            ownerName = "Admin Block";
                        } else {
                            OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(pagedDomainBlock.getOwner());
                            if (offlineOwner.hasPlayedBefore()) {
                                ownerName = offlineOwner.getName();
                            }
                        }
                        HashMap<String, String> placeHolder = new HashMap<>();
                        placeHolder.put("<%owner%>", ownerName);
                        placeHolder.put("<%blocktype%>", ChatColor.stripColor(pagedDomainBlock.getConfig().getDisplayName()));
                        placeHolder.put("<%name%>", ChatColor.stripColor(pagedDomainBlock.getFieldName()));
                        placeHolder.put("<%location%>", WorldFunctions.simpleReadable(pagedDomainBlock.getBlockLocation()));
                        String blockInfo = " <%name%>";
                        for (Map.Entry<String, String> params : placeHolder.entrySet()) {
                            blockInfo = ColorFunctions.colorToString(blockInfo.replace(params.getKey(), params.getValue()));
                        }

                        TextComponent msg = new TextComponent(
                                ""
                        );

                        // Open
                        TextComponent open_msg = new TextComponent(instance.getLocale().localeText("command_list_fields_open", " &r[&bOpen&r]", null));
                        open_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(instance.getLocale().localeText("command_list_fields_open_info", "&fThis will open the Domain block menu", null)).create()));
                        open_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/domain open_gui " + pagedDomainBlock.getPlacedId()));
                        msg.addExtra(open_msg);

                        // Teleport
                        if (instance.config().isAllowSelfFieldTeleport()) {
                            Player tpPlayer = (Player) sender;
                            TextComponent tp_msg = new TextComponent(instance.getLocale().localeText("command_search_player_fields_tp", " &r[&bTP&r]", null));
                            tp_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(instance.getLocale().localeText("command_search_player_fields_tp_info", "&fThis will teleport you to the closest available spot to the domain block", null)).create()));
                            tp_msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/domain teleport_to " + pagedDomainBlock.getPlacedId()));
                            msg.addExtra(tp_msg);
                        }

                        TextComponent info_msg = new TextComponent(blockInfo);
                        info_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder
                                        (
                                                instance.getLocale().localeText(
                                                        "search_hover_text",
                                                        "Owner: <%owner%>\n&rType: <%blocktype%>\n&rLocation: <%location%>",
                                                        placeHolder
                                                )
                                        ).create()
                                )
                        );
                        msg.addExtra(info_msg);
                        sender.spigot().sendMessage(msg);

                    }
                    if ((pageNum + 1) < pages.size()) {
                        int nextPage = pageNum + 2;
                        // add a next
                        TextComponent paging = new TextComponent(
                                instance.getLocale().localeText("next", "&f[&bNext&f]", null)
                        );
                        String args = "";
                        for (Map.Entry<Integer, String> arg : arguments.entrySet()) {
                            if (arg.getKey() < 3) {
                                args = args + " " + arg.getValue();
                            } else {
                                args = args + " " + pageNum + 1;
                            }
                        }
                        paging.setClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/domain " + arguments.get(0) + " " + nextPage
                                )
                        );

                        sender.spigot().sendMessage(paging);
                    }
                }
            } else {
                message.send(
                        sender,
                        "command_me_no_results",
                        "&4No Domain fields found",
                        null,
                        true
                );
                return;
            }

        } else {
            // no perm
            message.send(
                    sender,
                    "command_no_perm_do",
                    "&4You do not have permission to do that",
                    null,
                    true
            );
        }
    }

    /**
     * Opens the main Domain Block GUI for the Domain Block Identifier provided
     * @param sender CommandSender sending the command
     * @param arguments Arguments provided with the command
     */
    public void openMainGUI(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (arguments.containsKey(1)) {
                if (NumberFunctions.isInt(arguments.get(1))) {
                    DomainBlock domainBlock = domainBlockManager.getDomainBlockById(NumberFunctions.stringToInt(arguments.get(1)));
                    if (domainBlock != null) {
                        if (domainBlockManager.isOwner(player, domainBlock)) {
                            MainGUI gui = new MainGUI(
                                    instance,
                                    domainBlock,
                                    player
                            );
                            player.openInventory(gui.getInventory());
                        } else {
                            message.send(
                                    player,
                                    "",
                                    "",
                                    null,
                                    true
                            );
                            message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                        }
                    } else {
                        message.send(
                                player,
                                "",
                                "",
                                null,
                                true
                        );
                        message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                    }
                } else {
                    message.send(
                            player,
                            "command_opengui_invalid_blockid",
                            "&4Invalid Domain block ID",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        player,
                        "command_opengui_invalid_blockid",
                        "&4Invalid Domain block ID",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
        }
    }

    /**
     * Opens the Domain block for the current field the player is standing in
     * @param sender CommandSender sending the command
     * @param arguments Arguments provided with the command
     */
    public void openGUI(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DomainBlock domainBlock = domainBlockManager.getCurrentField(player);
            if (domainBlock != null) {
                if (domainBlockManager.isOwner(player, domainBlock)) {
                    MainGUI gui = new MainGUI(
                            instance,
                            domainBlock,
                            player
                    );
                    player.openInventory(gui.getInventory());
                } else {
                    message.send(
                            player,
                            "",
                            "",
                            null,
                            true
                    );
                    message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                }
            } else {
                message.send(
                        player,
                        "no_field",
                        "&4No Domain field found",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
        }
    }

    /**
     * Teleports the player to the nearest empty space next to the Domain Block
     * Player must have domain.admin permissions
     * @param sender CommandSender sending the command
     * @param arguments Arguments provided with the command
     */
    public void tp_to_block(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (arguments.containsKey(1)) {
                if (NumberFunctions.isInt(arguments.get(1))) {
                    DomainBlock domainBlock = domainBlockManager.getDomainBlockById(NumberFunctions.stringToInt(arguments.get(1)));
                    if (domainBlock != null) {
                        if (Permission.hasPermission(player, "domain.admin") || Permission.hasPermission(player, "domain.teleport")) {
                            Block nearestEmptySpace = DomainFunctions.getNearestEmptySpace(domainBlock.getBlockLocation().getBlock(), 10);
                            if (nearestEmptySpace != null){
                                player.teleport(nearestEmptySpace.getLocation());
                            } else {
                                message.send(
                                        sender,
                                        "command_tpblock_tp_error",
                                        "&4Unable to teleport, there may not be a safe place nearby",
                                        null,
                                        true
                                );
                            }
                        } else {
                            message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                        }
                    } else {
                        message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                    }
                } else {
                    message.send(
                            player,
                            "command_tpblock_invalid_blockid",
                            "&4Invalid Domain block ID",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        player,
                        "command_tpblock_invalid_blockid",
                        "&4Invalid Domain block ID",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
        }
    }

    /**
     * Disables the specified Domain Block
     * Player must have domain.admin permissions
     * @param sender CommandSender sending the command
     * @param arguments Arguments provided with the command
     */
    public void disable_block(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (arguments.containsKey(1)) {
                if (NumberFunctions.isInt(arguments.get(1))) {
                    DomainBlock domainBlock = domainBlockManager.getDomainBlockById(NumberFunctions.stringToInt(arguments.get(1)));
                    if (domainBlock != null) {
                        if (Permission.hasPermission(player, "domain.admin")) {
                            domainBlock.setActive(false);
                            domainBlock.setDirty(true);
                            domainBlockManager.domainBlockSave(domainBlock);
                            message.send(
                                    sender,
                                    "admin_disable_block_success",
                                    "&aSUCCESS &f- Field has been &4disabled",
                                    null,
                                    true
                            );
                        } else {
                            message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                        }
                    } else {
                        message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                    }
                } else {
                    message.send(
                            player,
                            "command_disable_invalid_blockid",
                            "&4Invalid Domain block ID",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        player,
                        "command_tpblock_invalid_blockid",
                        "&4Invalid Domain block ID",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
        }
    }

    /**
     * Deletes the specified Domain field
     * Player must have domain.admin permissions
     * @param sender CommandSender sending the command
     * @param arguments Arguments provided with the command
     */
    public void delete_block(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (arguments.containsKey(1)) {
                if (NumberFunctions.isInt(arguments.get(1))) {
                    DomainBlock domainBlock = domainBlockManager.getDomainBlockById(NumberFunctions.stringToInt(arguments.get(1)));
                    if (domainBlock != null) {
                        if (Permission.hasPermission(player, "domain.admin")) {
                            domainBlockManager.removeField(domainBlock.getPlacedId());
                            message.send(
                                    sender,
                                    "admin_delete_block_success",
                                    "&aSUCCESS &f- Field has been &4removed",
                                    null,
                                    true
                            );
                        } else {
                            message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                        }
                    } else {
                        message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                    }
                } else {
                    message.send(
                            player,
                            "command_delete_block_invalid_blockid",
                            "&4Invalid Domain block ID",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        player,
                        "command_delete_block_invalid_blockid",
                        "&4Invalid Domain block ID",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
        }
    }

    /**
     * Outputs a list of blocks that can be purged to Console
     * @param sender CommandSender sending the command
     */
    public void purge_list(CommandSender sender) {
        boolean allowPurgeList = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.admin")) {
                allowPurgeList = true;
            } else {
                message.send(player,"command_no_perm_do","&4You do not have permission to do that",null,true);
                return;
            }
        } else {
            allowPurgeList = true;
        }
        if (allowPurgeList) {
            Domain instance = Domain.getInstance();
            DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
            if (instance.getData().inactiveFields != null) {
                if (instance.getData().inactiveFields.size() > 0) {
                    for (DomainBlock inactiveField : instance.getData().inactiveFields) {
                        instance.getLog().log("CommandFunctions", "Purge", "Inactive Domain Field: " + inactiveField.getPlacedId() + " - " +
                                domainBlockManager.getFile(inactiveField.getPlacedId()).getAbsoluteFile());
                    }
                    message.send(
                            sender,
                            "command_purge_list",
                            "&4List of fields that can be purged has been sent to console",
                            null,
                            true
                    );
                } else {
                    // none to remove
                    message.send(
                            sender,
                            "command_purge_none",
                            "&4There are no fields to purge",
                            null,
                            true
                    );
                }
            } else {
                // none to remove
                message.send(
                        sender,
                        "command_purge_none",
                        "&4There are no fields to purge",
                        null,
                        true
                );
            }
        }
    }

    /**
     * Purges expired fields from the Data folder
     * @param sender CommandSender sending the command
     */
    public void purge(CommandSender sender) {
        if (sender instanceof Player) {
            message.send(sender,"command_no_perm_do","&4You do not have permission to do that",null,true);
            return;
        }
        if (instance.getData().inactiveFields != null) {
            if (instance.getData().inactiveFields.size() > 0) {
                for (DomainBlock inactiveField : instance.getData().inactiveFields) {
                    instance.getLog().log("CommandFunctions", "Purge", "Purging " + inactiveField.getPlacedId());
                    domainBlockManager.removeField(inactiveField.getPlacedId());
                }
                instance.getData().inactiveFields = new HashSet<>();
            } else {
                // none to remove
                message.send(
                        sender,
                        "command_purge_none",
                        "&4There are no fields to purge",
                        null,
                        true
                );
            }
        } else {
            // none to remove
            message.send(
                    sender,
                    "command_purge_none",
                    "&4There are no fields to purge",
                    null,
                    true
            );
        }
    }

    /**
     * Gets a rent confirmation and attempts to rent the field
     * @param sender CommandSender sending the command
     * @param arguments Arguments sent with the command
     */
    public void rentConfirm(CommandSender sender, Map<Integer, String> arguments) {
        if (arguments.containsKey(1) && arguments.containsKey(2)) {
            UUID uuid = StringFunctions.uuidFromString(arguments.get(1));
            int placedId = -1;
            if (NumberFunctions.isInt(arguments.get(2))) {
                placedId = NumberFunctions.stringToInt(arguments.get(2));
            }
            if (uuid != null && placedId > 0) {
                RentManager rentManager = new RentManager(instance);
                if (!rentManager.rent(placedId, uuid)) {
                    instance.getDebug().log("CommandFunctions", "rentConfirm", "Renting failed");
                }
            } else {
                instance.getDebug().log("CommandFunctions", "rentConfirm", "Renting failed");
            }
        } else {
            instance.getDebug().log("CommandFunctions", "rentConfirm", "Renting failed");
        }
    }

    /**
     * Displays the current version
     * @param sender CommandSender sending the command
     */
    public void version(CommandSender sender) {
        HashMap<String, String> versionParam = new HashMap<>();
        versionParam.put("<%name%>", instance.getName());
        versionParam.put("<%ver%>", instance.getDescription().getVersion());
        message.send(
                sender,
                "version",
                "<%name%> - Version <%ver%>",
                versionParam,
                true
        );
    }

    /**
     * Toggles the Create Spawn Event
     * Purposefully undocumented command
     * @param sender CommandSender sending the command
     */
    public void toggleSpawnEvent(CommandSender sender) {
        boolean canToggle = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.admin")) {
                canToggle = true;
            }
        } else {
            canToggle = true;
        }
        if (canToggle) {
            instance.getData().spawnEventStatus = !instance.getData().spawnEventStatus;
            if (instance.getData().spawnEventStatus) {
                instance.getLog().log("CommandFunctions", "Creature Spawn Event", "Enabled");
                message.send(
                        sender,
                        "spawn_event_enabled",
                        "Spawn event is enabled",
                        null,
                        true
                );
            } else {
                instance.getLog().log("CommandFunctions","Creature Spawn Event", "Disabled");
                message.send(
                        sender,
                        "spawn_event_disabled",
                        "Spawn event is disabled",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_perm_do",
                    "&4You do not have permission to do that",
                    null,
                    true
            );
        }
    }

    /**
     * Toggles the Player Move Event
     * Purposefully undocumented command
     * @param sender CommandSender sending the command
     */
    public void togglePlayerMove(CommandSender sender) {
        boolean canToggle = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.admin")) {
                canToggle = true;
            }
        } else {
            canToggle = true;
        }
        if (canToggle) {
            instance.getData().moveEventStatus = !instance.getData().moveEventStatus;
            if (instance.getData().moveEventStatus) {
                instance.getLog().log("CommandFunctions","Player Move Event", "Enabled");
                message.send(
                        sender,
                        "move_event_enabled",
                        "Player Move event is enabled",
                        null,
                        true
                );
            } else {
                instance.getLog().log("CommandFunctions","Player Move Event", "Disabled");
                message.send(
                        sender,
                        "move_event_disabled",
                        "Player Move event is disabled",
                        null,
                        true
                );
            }
        } else {
            message.send(
                    sender,
                    "command_no_perm_do",
                    "&4You do not have permission to do that",
                    null,
                    true
            );
        }
    }

    public void help(CommandSender sender) {
        boolean isAdmin = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.admin")) {
                isAdmin = true;
            }
        } else {
            isAdmin = true;
        }
        message.send(
                sender,
                "help_user",
                "&lDomain Help\n&5/domain allow &b<player name>&r - Allows a player into the current field\n&5/domain buy&r - Allows you to buy a Domain block\n&5/domain disallow &b<player name>&r - Disallows a player from the current field\n&5/domain display &bclear&f|&bcorners&f|&bcube&f|&bring&r - Displays the current field\n&5/domain get &6<block type>&r - Allows you to get a Domain Block\n&5/domain list&r - Lists all the fields at your current location\n&5/domain search &bowner&f|&btype&f|&bworld &6<search term>&r - Searches for Domain Blocks\n&5/domain url&r - Displays the URL for the help pages for Domain",
                null,
                true
        );
        if (isAdmin) {
            message.send(
                    sender,
                    "help_admin",
                    "&lAdmin Commands\n&d/domain bypass&r - Toggles bypass mode\n&d/domain debug&r - Toggles debug mode &4[use with caution]\n&d/domain give &6<player name> &6<block name>&r - Gives a Domain block to the player\n&d/domain purge &4- Purges old Domain Blocks [USE WITH EXTREME CAUTION!]&r\n&d/domain purge_list&r - Lists the Domain Blocks that can be purged\n&d/domain reload&r - Reloads the config\n&d/domain version&r - Displays the current version",
                    null,
                    true
            );
        }
    }

    /**
     * Teleports the player back to their prior location if idle kicked
     * @param sender CommandSender sending the command
     */
    public void idleBack(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.idle.back")) {
                if (instance.config().getIdleCyclesBeforeKick() > 0) {
                    if (instance.getData().hasIdle(player.getUniqueId())) {
                        Idle idle = instance.getData().getIdle(player.getUniqueId());
                        Idle.BackResult backResult = idle.back();
                        switch (backResult) {
                            case SUCCESS:
                                message.send(
                                        sender,
                                        "command_back_success",
                                        "&2You have been returned to your prior location",
                                        null,
                                        true
                                );
                                break;
                            case NO_PRIOR_LOCATION:
                                message.send(
                                        sender,
                                        "command_back_no_prior",
                                        "&4No prior idle location found",
                                        null,
                                        true
                                );
                                break;
                            case LOCATION_NOT_SAFE:
                                message.send(
                                        sender,
                                        "command_back_not_safe",
                                        "&4Unable to return to prior idle location as it is not a safe place to teleport to",
                                        null,
                                        true
                                );
                                break;
                            case TOO_FAR_FROM_KICKED_TO:
                                message.send(
                                        sender,
                                        "command_back_too_far",
                                        "&4Unable to return to prior idle location, you have moved too far from where you were kicked to",
                                        null,
                                        true
                                );
                                break;
                        }

                    } else {
                        // No location to go back to
                        message.send(
                                sender,
                                "command_back_no_prior",
                                "&4No prior idle location found",
                                null,
                                true
                        );
                    }
                } else {
                    // Idle kick is not active, so back cannot be used
                    message.send(
                            sender,
                            "command_back_disabled",
                            "&4Idle kick is disabled, so the Back command cannot be used",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        sender,
                        "command_no_perm_do",
                        "&4You do not have permission to do that",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    /**
     * Kicks a player from the field
     * @param sender CommandSender sending the command
     * @param arguments Arguments sent with the command
     */
    public void whoInKick(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            if (arguments.containsKey(1) && arguments.containsKey(2)) {
                if (NumberFunctions.isInt(arguments.get(1))) {
                    DomainBlock domainBlock = domainBlockManager.getDomainBlockById(NumberFunctions.stringToInt(arguments.get(1)));
                    if (domainBlock != null) {
                        Player player = null;
                        UUID uuid = StringFunctions.uuidFromString(arguments.get(2));
                        if (uuid != null) {
                            player = Bukkit.getPlayer(uuid);
                        }
                        if (player == null) {
                            // Invalid player
                            message.send(
                                    senderPlayer,
                                    "command_who_kick_invalid_player",
                                    "&4Invalid player",
                                    null,
                                    true
                            );
                            return;
                        }
                        if (domainBlockManager.isOwner(senderPlayer, domainBlock) || PlayerFunctions.hasBypass(senderPlayer)) {
                            domainBlockManager.kickFromField(player, domainBlock);
                            // success
                            message.send(
                                    player,
                                    "command_who_kick_success",
                                    "&4You have been kicked from the Domain field",
                                    null,
                                    true
                            );
                        } else {
                            message.send(
                                    senderPlayer,
                                    "command_no_perm_do",
                                    "&4You do not have permission to do that",
                                    null,
                                    true
                            );
                        }
                    } else {
                        // invalid domain block
                        message.send(
                                senderPlayer,
                                "command_who_kick_invalid_domain",
                                "&4Invalid domain block",
                                null,
                                true
                        );
                    }
                } else {
                    // invalid domain block
                    message.send(
                            senderPlayer,
                            "command_who_kick_invalid_domain",
                            "&4Invalid domain block",
                            null,
                            true
                    );
                }
            } else {
                // invalid usage
                message.send(
                        senderPlayer,
                        "command_who_kick_invalid_usage",
                        "&4Invalid usage",
                        null,
                        true
                );
            }
        } else {
            message.send(sender,"command_no_console","&4That command cannot be run from console",null,true);
        }
    }

    public void getVoucher(CommandSender sender, Map<Integer, String> arguments) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permission.hasPermission(player, "domain.admin")) {
                if (arguments.containsKey(1)) {
                    Voucher voucher = instance.getData().vouchers.get(arguments.get(1));
                    if (voucher != null) {
                        InventoryFunctions.addItemsToPlayerInventory(player, voucher.voucher());
                    } else {
                        message.send(
                                sender,
                                "voucher_invalid",
                                "&4ERROR &f- Invalid voucher key",
                                null,
                                true
                        );
                    }
                } else {
                    // invalid usage
                    message.send(
                            sender,
                            "voucher_usage",
                            "/domain voucher &b<key> &4- Gets the voucher with key specified",
                            null,
                            true
                    );
                }
            } else {
                message.send(
                        sender,
                        "command_no_perm_do",
                        "&4You do not have permission to do that",
                        null,
                        true
                );
            }
        } else {
            // no console
            message.send(
                    sender,
                    "command_no_console",
                    "&4That command cannot be run from console",
                    null,
                    true
            );
        }
    }

    public void transferToFile(CommandSender sender) {
        if (sender instanceof Player) {
            // console only
            message.send(
                    sender,
                    "command_console_only",
                    "&4Command can only be called from the console",
                    null,
                    true
            );
            return;
        }
        IDomainStorage fileStorage = new YamlFile(instance);
        StorageManager storageManager = new StorageManager(instance, fileStorage);
        storageManager.removeAll();
        storageManager.saveAll(false);
        message.send(
                sender,
                "storage_transfer_complete",
                "&2Storage transfer complete",
                null,
                true
        );
    }
    public void transferToMySql(CommandSender sender) {
        if (sender instanceof Player) {
            // console only
            message.send(
                    sender,
                    "command_console_only",
                    "&4Command can only be called from the console",
                    null,
                    true
            );
            return;
        }
        instance.setMySQL(instance.getIbexInternal().getMySQL(
                instance.config().getMySQLHost(),
                "" + instance.config().getMySQLPort(),
                instance.config().getMySQLDatabase(),
                instance.config().getMySQLUsername(),
                instance.config().getMySQLPassword(),
                instance.config().getMySQLUseSSL())
        );

        IDomainStorage dbStorage = new MySQL(instance);
        StorageManager storageManager = new StorageManager(instance, dbStorage);
        storageManager.check();
        instance.getLog().log("Clearing all existing Domain entries from database");
        storageManager.removeAll();

        // Create a copy of the placedFields map.
        Map<Integer, DomainBlock> placedFieldsCopy = new HashMap<>(instance.getData().placedFields);

        new BukkitRunnable() {
            private Iterator<Map.Entry<Integer, DomainBlock>> iterator = placedFieldsCopy.entrySet().iterator();

            @Override
            public void run() {
                if (iterator.hasNext()) {
                    Map.Entry<Integer, DomainBlock> placedEntry = iterator.next();
                    instance.getLog().log("Saving Domain field. Id: " + placedEntry.getValue().getPlacedId() + " Name: " + placedEntry.getValue().getFieldName());

                    IDomainStorage dbStorage = new MySQL(instance);
                    StorageManager storageManager = new StorageManager(instance, dbStorage);
                    storageManager.save(placedEntry.getValue());
                } else {
                    // The loop is done, cancel the task.
                    message.send(
                            sender,
                            "storage_transfer_complete",
                            "&2Storage transfer complete",
                            null,
                            true
                    );
                    this.cancel();
                }
            }
        }.runTaskTimer(instance, 20, 20); // Delay and period are both set to 20, which is equivalent to 1 second (20 ticks = 1 second).

    }
    //endregion
}
