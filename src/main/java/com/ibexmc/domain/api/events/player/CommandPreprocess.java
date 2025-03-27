package com.ibexmc.domain.api.events.player;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.ibexmc.domain.domainblock.CommandsToBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;

public final class CommandPreprocess extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private String message;

    /**
     * CommandPreprocess
     * This event is called when a command is entered by a player in a Domain field
     *
     * @param player The player that ran the command
     * @param message The command being entered
     *
     */
    public CommandPreprocess(Player player, String message) {

        super(player, message);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        this.message = message;

        instance.getDebug().log("Domain_CommandPreprocessEvent", "CommandPreprocess", "CommandPreprocess called");

        String[] result = message.split(" ", 2);
        String commandtext = result[0];

        boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
        boolean isAllowed = domainBlockManager.isAllowed(player, player.getLocation());

        instance.getDebug().log(
                "PlayerEvents.CommandPreprocess",
                "CommandPreProcess",
                player.getName() + " ran the command " + commandtext + " at " + WorldFunctions.simpleReadable(player.getLocation()) +
                        "\n" + "Has Bypass: " + hasBypass + "\n" + "Is Allowed: " + isAllowed
        );

        List<CommandsToBlock> commands = instance.getData().getCommands();
        if (commands != null) {
            if (commands.size() > 0) {
                for (CommandsToBlock cb : commands)
                {
                    if (cb.getCommands() != null)
                    {
                        for (String cmd : cb.getCommands())
                        {
                            if (cmd.equalsIgnoreCase(commandtext))
                            {
                                if (domainBlockManager.hasFlag(player.getLocation(), cb.getFlagType()))
                                {
                                    if (!hasBypass && !isAllowed) {

                                        instance.getDebug().log(
                                                "PlayerEvents.CommandPreprocess",
                                                "CommandPreProcess",
                                                player.getName() + " ran the command " + commandtext + " at " + WorldFunctions.simpleReadable(player.getLocation()) +
                                                        "\n" + "Flag Type: " + cb.getFlagType()
                                        );

                                        Map<String, String> parameters = new HashMap<>();
                                        parameters.put("<%command%>", commandtext);

                                        instance.getMessage().send(player, "prevent_command", "&4You cannot use the <%command%> command here", parameters, true);



                                        this.cancelled = true;
                                        this.cancelReason = "That command cannot be used here.  Command is cancelled.";

                                        instance.getDebug().log(
                                                "PlayerEvents.CommandPreprocess",
                                                "CommandPreProcess",
                                                player.getName() + " ran the command " + commandtext + " at " + WorldFunctions.simpleReadable(player.getLocation()) +
                                                        "\n" + "COMMAND WAS CANCELLED"
                                        );


                                        parameters = null;
                                        result = null;
                                        commandtext = null;
                                        commands = null;
                                        return;
                                    } else {
                                        if (hasBypass) {
                                            instance.getDebug().log(
                                                    "PlayerEvents.CommandPreprocess",
                                                    "CommandPreProcess",
                                                    player.getName() + " ran the command " + commandtext + " at " + WorldFunctions.simpleReadable(player.getLocation())
                                            );
                                        }
                                    }
                                    result = null;
                                    commandtext = null;
                                    commands = null;
                                    return;
                                } else {
                                    instance.getDebug().log(
                                            "PlayerEvents.CommandPreprocess",
                                            "CommandPreProcess",
                                            player.getName() + " ran the command " + commandtext + " at " + WorldFunctions.simpleReadable(player.getLocation()) +
                                                    "\n" + "Flag: " + cb.getFlagType() + " is not enabled"
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String getCommand() {
        return this.message;
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
