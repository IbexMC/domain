package com.ibexmc.domain.api.events.player;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.meta.ItemMeta;

import com.ibexmc.domain.domainblock.DomainBlockManager;

public final class PlayerDrop extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private Item item;

    /**
     * PlayerDrop
     * This event is called when a player drops an item in a Domain field
     *
     * @param player The player that ran the command
     * @param item The Item object being dropped
     *
     */
    public PlayerDrop(Player player, Item item) {

        super(player, item);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        this.item = item;

        instance.getDebug().log("Domain_PlayerDropEvent", "PlayerDrop", "PlayerDrop called");

        boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
        boolean isAllowed = domainBlockManager.isAllowed(player, player.getLocation());
        String itemString = "an item";

        if (item.getItemStack() != null) {
            ItemMeta itemMeta = item.getItemStack().getItemMeta();
            if (itemMeta != null) {
                itemString = itemMeta.getDisplayName();
            }
            itemString = Integer.toString(item.getItemStack().getAmount()) + "x " + itemString;
        }

        if (domainBlockManager.hasFlag(item.getLocation(), Flag.Type.PREVENT_DROP_ITEM_BY_NONALLOWED)) {
            if (!isAllowed && !hasBypass) {
                this.cancelled = true;
                this.cancelReason = "Item drop at " +
                        WorldFunctions.simpleReadable(item.getLocation()) +
                        " is cancelled.";
                instance.getMessage().send(player, "prevent_drop_item", "&4You cannot drop items here", null, true);
                return;
            } else {
                if (hasBypass) {
                    instance.getDebug().log(
                            "PlayerEvents.PlayerDrop",
                            "PlayerDrop",
                            player.getName() + " dropped " + itemString + " at " + WorldFunctions.simpleReadable(player.getLocation())
                    );
                }
            }
        }
    }



    public Item getDroppedItem() {
        return this.item;
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
