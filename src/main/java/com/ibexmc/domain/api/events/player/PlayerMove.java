package com.ibexmc.domain.api.events.player;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.ibexmc.domain.domainblock.open.Teleport;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.api.events.domain.DomainEnterEvent;
import com.ibexmc.domain.api.events.domain.DomainEnterOwnEvent;
import com.ibexmc.domain.api.events.domain.DomainLeaveEvent;
import com.ibexmc.domain.api.events.domain.DomainLeaveOwnEvent;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.Permission;


public final class PlayerMove extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private Location locationFrom;
    private Location locationTo;

    /**
     * PlayerMove
     * This event is called when a command is entered by a player moves at least 1 block
     *
     * @param player The player that ran the command
     * @param locationFrom The location the player has moved from
     * @param locationTo The location the player has moved to
     *
     */
    public PlayerMove(Player player, Location locationFrom, Location locationTo) {

        super(player, locationFrom, locationTo);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        this.locationFrom = locationFrom;
        this.locationTo = locationTo;

        boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
        boolean isAllowedFrom = domainBlockManager.isAllowed(player, locationFrom);
        boolean isAllowedTo = domainBlockManager.isAllowed(player, locationTo);

        // Get the flags at this location - that way we can use the domainBlockManager.hasFlag that has the flags
        // already defined, and we can avoid looking them up again and again.
        Map<Flag.Type, Boolean> hmFlagsFrom = domainBlockManager.getFlagsAtLocation(locationFrom);
        Map<Flag.Type, Boolean> hmFlagsTo = domainBlockManager.getFlagsAtLocation(locationTo);

        if (domainBlockManager.hasFlag(Flag.Type.MEMBERS_ONLY, hmFlagsTo))
        {
            DomainBlock domainBlock = domainBlockManager.getCurrentField(locationTo);
            if (!StringFunctions.isNullOrEmpty(domainBlock.getMembersOnlyPermission())) {
                if (!hasBypass && !isAllowedTo) {
                    if (!Permission.hasPermission(player, domainBlock.getMembersOnlyPermission())) {

                        Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
                        Domain.getInstance().getData().putNotAllowed(timestampNow, player.getUniqueId());
                        int cnt = 0;
                        HashMap<Timestamp, UUID> hmNA = Domain.getInstance().getData().getNotAlloweds();
                        if (hmNA != null)
                        {
                            for (Entry<Timestamp, UUID> na : hmNA.entrySet()) {
                                Timestamp ts = na.getKey();
                                if (ts != null)
                                {
                                    long milliseconds = timestampNow.getTime() - ts.getTime();
                                    int seconds = (int) milliseconds / 1000;
                                    if (seconds < 20)
                                    {
                                        if (na.getValue().equals(player.getUniqueId()))
                                        {
                                            cnt++;
                                        }
                                    }
                                }
                            }
                        }
                        //instance.getDebug().log("PlayerEvents", "onPlayerMove", "Total: " + Integer.toString(cnt));
                        // If the player has tried 15 or more times, teleport them home
                        if (cnt >= 15)
                        {
                            instance.getMessage().send(player, "teleported_to_spawn", "&4You have been teleported back to spawn", null, true);
                            player.teleport(player.getLocation().getWorld().getSpawnLocation());
                        } else {
                            instance.getMessage().actionBar(player, instance.getLocale().localeText("members_only", "&cSorry, this area is for members only", null));
                            player.teleport(locationFrom);
                            // If the player has tried 10 or more times (but less than 15) then warn them
                            if (cnt >= 10 && cnt < 15)
                            {
                                instance.getMessage().actionBar(player, instance.getLocale().localeText("no_entry_allowed_tp_warning", "&4CAUTION: Continued attempts to enter this area will return you to spawn", null));
                            }
                        }



                        player.getWorld().playEffect(player.getLocation(), Effect.EXTINGUISH, 10);
                        return;
                    }
                }
            }
        }

        // Prevent Access by Non-Allowed players
        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ACCESS_BY_NONALLOWED, hmFlagsTo))
        {
            //instance.getDebug().log("PlayerEvents", "onPlayerMove", "Is Allowed = " + Boolean.toString(isAllowed));
            Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
            if (!hasBypass && !isAllowedTo) {
                Domain.getInstance().getData().putNotAllowed(timestampNow, player.getUniqueId());
                int cnt = 0;
                HashMap<Timestamp, UUID> hmNA = Domain.getInstance().getData().getNotAlloweds();
                if (hmNA != null)
                {
                    for (Entry<Timestamp, UUID> na : hmNA.entrySet()) {
                        Timestamp ts = na.getKey();
                        if (ts != null)
                        {
                            long milliseconds = timestampNow.getTime() - ts.getTime();
                            int seconds = (int) milliseconds / 1000;
                            //instance.getDebug().log("PlayerEvents", "onPlayerMove", na.getValue() + " - " + Integer.toString(cnt));
                            // If it has been less than 20 seconds since the last attempt, add it to the list
                            if (seconds < 20)
                            {
                                if (na.getValue().equals(player.getUniqueId()))
                                {
                                    cnt++;
                                }
                            }
                        }
                    }
                }
                //instance.getDebug().log("PlayerEvents", "onPlayerMove", "Total: " + Integer.toString(cnt));
                // If the player has tried 15 or more times, teleport them home
                if (cnt >= 15)
                {
                    instance.getMessage().send(player, "teleported_to_spawn", "&4You have been teleported back to spawn", null, true);
                    player.teleport(player.getLocation().getWorld().getSpawnLocation());
                } else {
                    instance.getMessage().actionBar(player, instance.getLocale().localeText("no_entry_allowed", "&4You are not allowed to enter here", null));
                    player.teleport(locationFrom);
                    // If the player has tried 10 or more times (but less than 15) then warn them
                    if (cnt >= 10 && cnt < 15)
                    {
                        instance.getMessage().actionBar(player, instance.getLocale().localeText("no_entry_allowed_tp_warning", "&4CAUTION: Continued attempts to enter this area will return you to spawn", null));
                    }
                }
                player.getWorld().playEffect(player.getLocation(), Effect.EXTINGUISH, 10);
                // Cleanup
                hmFlagsFrom = null;
                hmFlagsTo = null;
                return;
            }
        }

        DomainBlock blockFrom = domainBlockManager.getCurrentField(locationFrom);
        DomainBlock blockTo = domainBlockManager.getCurrentField(locationTo);
        int blockIDFrom = -1;
        int blockIDTo = -1;
        if (blockFrom != null) {
            blockIDFrom = blockFrom.getPlacedId();
        }
        if (blockTo != null) {
            blockIDTo = blockTo.getPlacedId();
        }

        if (hasBypass) {
            if (blockIDTo >= 0) {
                if (blockTo != null) {

                    String name = "Unknown";
                    if (blockTo.isAdminBlock()) {
                        name = "&4Admin&r";
                    } else {
                        if (blockTo.getOwner() != null) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(blockTo.getOwner());
                            if (offlinePlayer != null) {
                                name = offlinePlayer.getName();
                            }
                        }
                    }


                    instance.getMessage().actionBar(
                            player,
                            "&0Current Field Owner: &1" + name +
                                    " &0Type: &1" + blockTo.getConfig().getDisplayName() +
                                    " &0ID: &1" + Integer.toString(blockTo.getPlacedId())
                    );
                } else {

                    instance.getMessage().actionBar(
                            player, instance.getLocale().localeText("move_not_in_field", "&4Not currently in a Domain field", null));
                }
            } else {
                instance.getMessage().actionBar(
                        player, instance.getLocale().localeText("move_not_in_field", "&4Not currently in a Domain field", null));
            }
        }

        if (blockIDFrom != blockIDTo) {

            // If the player is in bypass mode, do not teleport
                if (domainBlockManager.hasFlag(Flag.Type.TELEPORT_TO, hmFlagsTo) && blockTo.getTeleportToLocation() != null && !hasBypass) {
                    if (blockTo.getTeleportToLocation() != null) {
                        boolean allowTeleport = true;
                        if (player.isSneaking() && domainBlockManager.isOwner(player, domainBlockManager.getDomainBlockById(blockIDTo))) {
                            allowTeleport = false;
                        }
                        if (Domain.getInstance().getData().openBlocks.containsKey(player.getUniqueId())) {
                            if (Domain.getInstance().getData().openBlocks.get(player.getUniqueId()) instanceof Teleport) {
                                allowTeleport = false;
                            }
                        }
                        if (allowTeleport) {
                            Location tpLoc = blockTo.getTeleportToLocation().clone().add(0.5, 0, 0.5);
                            if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                                if (WorldFunctions.safeTeleport(tpLoc)) {
                                    player.teleport(tpLoc);
                                } else {
                                    instance.getMessage().send(
                                            player,
                                            "teleport_unsafe",
                                            "&4Unable to teleport.  Location is unsafe",
                                            null,
                                            true
                                    );
                                }
                            } else {
                                player.teleport(tpLoc);
                            }

                        }
                    }
                }

            if (blockIDTo >= 0) {
                // Only check if we're moving INTO a block

                // Trigger the Domain Enter event
                Bukkit.getServer().getPluginManager().callEvent(new DomainEnterEvent(player, blockTo));
                // If the player is the owner of the field they're entering, trigger the Domain Enter Own event
                if (blockTo.getOwner() != null) {
                    if (blockTo.getOwner().equals(player.getUniqueId())) {
                        Bukkit.getServer().getPluginManager().callEvent(new DomainEnterOwnEvent(player, blockTo));
                    }
                }

                // Check if commands should be run when entering the field
                if (!hasBypass) {
                    // If the player is in bypass mode, do not run commands
                    if (domainBlockManager.hasFlag(Flag.Type.RUN_COMMAND, hmFlagsTo))
                    {
                        Set<String> cmds = blockTo.getEnterCommands();
                        if (cmds != null) {
                            for (String cmd : cmds) {
                                instance.getDebug().log("PlayerEvents", "PlayerMove", "Run Enter Command (original): " + cmd);
                                String runcmd = cmd.replaceAll("@p", player.getName()).replaceAll("@u", player.getUniqueId().toString());
                                instance.getDebug().log("PlayerEvents", "PlayerMove", "Run Enter Command (replaced @p and @u): " + runcmd);
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), runcmd);
                            }
                        }
                    }

                    // If the player is in bypass mode, do not snitch on them
                    if (!isAllowedTo) {
                        if (domainBlockManager.hasFlag(Flag.Type.SNITCH_ON_ENTER, hmFlagsTo))
                        {
                            Player owner = Bukkit.getPlayer(blockTo.getOwner());
                            if (owner != null) {
                                Map<String, String> placeHolder = new HashMap<>();
                                placeHolder.put("<%player%>", player.getDisplayName());
                                placeHolder.put("<%blocktype%>", blockTo.getConfig().getDisplayName());
                                placeHolder.put("<%location%>", WorldFunctions.simpleReadable(locationTo));
                                instance.getMessage().send(owner, "snitch_message_enter", "&3[Snitch]&r <%player%> has entered your <%blocktype%> field at <%location%>" ,placeHolder, true);
                            }
                        }
                    }

                    // If the player is in bypass mode while gliding, don't cancel it
                    if (player.isGliding())
                    {
                        if (domainBlockManager.hasFlag(Flag.Type.PREVENT_ELYTRA_GLIDING, hmFlagsTo))
                        {
                            if (!isAllowedTo) {
                                player.setGliding(false);
                            }
                        }
                    }
                }

                // Display the welcome message if set
                if (domainBlockManager.hasFlag(Flag.Type.DISPLAY_WELCOME, hmFlagsTo))
                {
                    String welcomeMsg = blockTo.getWelcomeMessage();
                    if (!welcomeMsg.equals(""))
                    {
                        welcomeMsg = welcomeMsg.replaceAll("@p", player.getName());
                        instance.getMessage().actionBar(player, welcomeMsg);
                    }
                }

            }

            if (blockIDFrom >= 0) {
                // Only check if we're moving FROM a block

                // Trigger the Domain Enter event
                Bukkit.getServer().getPluginManager().callEvent(new DomainLeaveEvent(player, blockFrom));
                // If the player is the owner of the field they're leaving, trigger the Domain Leave Own event
                if (blockFrom.getOwner() != null) {
                    if (blockFrom.getOwner().equals(player.getUniqueId())) {
                        Bukkit.getServer().getPluginManager().callEvent(new DomainLeaveOwnEvent(player, blockFrom));
                    }
                }

                // Check if commands should be run when leaving the field
                if (!hasBypass) {
                    // If the player is in bypass mode, do not run commands
                    if (domainBlockManager.hasFlag(Flag.Type.RUN_COMMAND, hmFlagsFrom))
                    {
                        Set<String> cmds = blockFrom.getLeaveCommands();
                        if (cmds != null) {
                            for (String cmd : cmds) {
                                instance.getDebug().log("PlayerEvents", "PlayerMove", "Run Leave Command (original): " + cmd);
                                String runcmd = cmd.replaceAll("@p", player.getName()).replaceAll("@u", player.getUniqueId().toString());
                                instance.getDebug().log("PlayerEvents", "PlayerMove", "Run Enter Command (replaced @p and @u): " + runcmd);
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), runcmd);
                            }
                        }
                    }
                }

                // Display farewell message
                if (domainBlockManager.hasFlag(Flag.Type.DISPLAY_FAREWELL, hmFlagsFrom))
                {
                    String farewellMsg = blockFrom.getFarewellMessage();
                    if (!farewellMsg.equals(""))
                    {
                        farewellMsg = farewellMsg.replaceAll("@p", player.getName());
                        instance.getMessage().actionBar(player, farewellMsg);
                    }
                }

                // Prevent non-allowed players (unless in bypass) from leaving a field
                // TODO: Add same to teleporting out
                if (domainBlockManager.hasFlag(Flag.Type.PREVENT_EXIT_BY_NONALLOWED, hmFlagsFrom))
                {
                    if (!isAllowedFrom) {
                        //TODO: Add custom message to the block?
                        instance.getMessage().actionBar(player, instance.getLocale().localeText("prevent_exit", "&cYou are not allowed to leave", null));
                        this.cancelled = true;
                        this.cancelReason = "Player is not allowed to leave this field";
                        return;
                    }
                }

                // If the player is in bypass mode, do not snitch on them
                if (!isAllowedFrom) {
                    if (domainBlockManager.hasFlag(Flag.Type.SNITCH_ON_LEAVE, hmFlagsFrom))
                    {
                        Player owner = Bukkit.getPlayer(blockFrom.getOwner());
                        if (owner != null) {
                            Map<String, String> placeHolder = new HashMap<>();
                            placeHolder.put("<%player%>", player.getDisplayName());
                            placeHolder.put("<%blocktype%>", blockFrom.getConfig().getDisplayName());
                            placeHolder.put("<%location%>", WorldFunctions.simpleReadable(locationFrom));
                            instance.getMessage().send(owner, "snitch_message_leave", "&3[Snitch]&r <%player%> has left your <%blocktype%> field at <%location%>" , placeHolder, true);
                        }
                    }
                }

            }
        } else {
            // same field

            // Instantly heal the player
            if (domainBlockManager.hasFlag(Flag.Type.INSTANT_HEAL_ALLOWED_PLAYER, hmFlagsTo))
            {
                AttributeInstance a = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                double max = a.getValue();
                if (max != player.getHealth()) {
                    if (isAllowedTo)
                    {
                        player.setHealth(a.getValue());
                        instance.getDebug().log("PlayerEvents", "onPlayerMove", "Instantly healing player.  New health:" + Double.toString(player.getHealth()) + " of " + Double.toString(max));
                    }
                }
            }
        }



    }

    public Location getLocationFrom() {
        return this.locationFrom;
    }

    public Location getLocationTo() {
        return this.locationTo;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

}
