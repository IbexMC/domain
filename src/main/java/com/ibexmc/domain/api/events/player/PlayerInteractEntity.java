package com.ibexmc.domain.api.events.player;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.util.DomainParticles;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;

import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.domainblock.OpenDomainBlock;
import com.ibexmc.domain.domainblock.OpenDomainBlockType;


public final class PlayerInteractEntity extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private EquipmentSlot equipmentSlot;
    private Entity entity;

    /**
     * PlayerInteractEntity
     * This event is called when a player right clicks entity in a Domain field
     *
     * @param player The player that ran the command
     * @param equipmentSlot The hand used to interact
     * @param entity The entity being interacted with
     *
     */
    public PlayerInteractEntity(Player player, EquipmentSlot equipmentSlot, Entity entity) {

        super(player, equipmentSlot, entity);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        this.equipmentSlot = equipmentSlot;
        this.entity = entity;

        instance.getDebug().log("Domain_PlayerInteractEntityEvent", "PlayerInteractEntity", "PlayerInteractEntity called");

        boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
        boolean isAllowed = domainBlockManager.isAllowed(player, player.getLocation());

        if (entity.getType() == EntityType.PLAYER) {
            if (equipmentSlot == EquipmentSlot.HAND) {
                if (Domain.getInstance().getData().openDomainBlockExists(player.getUniqueId())) {
                    OpenDomainBlock openDomainBlock = Domain.getInstance().getData().openDomainBlockGet(player.getUniqueId());
                    if (openDomainBlock != null) {
                        DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(openDomainBlock.getPlacedID());
                        if (domainBlock != null) {
                            if (openDomainBlock.getOpenDomainBlockType().equals(OpenDomainBlockType.ALLOW_PLAYER)) {
                                Entity e = entity;
                                if (e instanceof Player) {
                                    Player clickedPlayer = (Player) e;
                                    if (domainBlock.getAllowedPlayers().contains(e.getUniqueId())) {
                                        instance.getMessage().send(player, "allow_player_already_in", "&4Player is already allowed into this field", null, true);
                                        this.cancelled = true;
                                        this.cancelReason = "Player is already allowed into this field";
                                        return;
                                    } else {
                                        domainBlock.addAllowedPlayer(e.getUniqueId());
                                        domainBlock.setDirty(true);
                                        domainBlockManager.domainBlockSave(domainBlock);
                                        this.cancelled = true;
                                        this.cancelReason = "Player has been allowed into this field - Cancelling the event to prevent further code";
                                        Domain.getInstance().getData().openDomainBlockRemove(player.getUniqueId());
                                        instance.getMessage().send(player, "allow_player_found", "&2SUCCESS - Player has been added to the field", null, true);
                                        instance.getMessage().send(clickedPlayer, "allow_player_allowed", "&2You are now allowed into this field", null, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (entity.getType() == EntityType.ITEM_FRAME) {
            if (domainBlockManager.hasFlag(entity.getLocation(), Flag.Type.PREVENT_ITEM_FRAME_ACCESS_BY_NONALLOWED))
            {
                if (!hasBypass && !isAllowed)
                {
                    instance.getDebug().log("PlayerEvents", "onPlayerInteractEntity", "Item frame interaction not allowed");
                    instance.getMessage().send(player, "item_frame_place", "&4You cannot place items in Item Frames here", null, true);
                    this.cancelled = true;
                    this.cancelReason = "Player cannot place item frames here";
                    return;
                }
            }
        }
        if (entity.getType() == EntityType.VILLAGER)
        {
            if (domainBlockManager.hasFlag(entity.getLocation(), Flag.Type.PREVENT_VILLAGER_TRADE_BY_ALL))
            {
                if (!hasBypass)
                {
                    DomainParticles.denyAccess(entity.getLocation(), player);
                    instance.getDebug().log("PlayerEvents", "onPlayerInteractEntity", "Villager right click interaction cancelled");
                    instance.getMessage().send(player, "prevent_villager_trade", "&4You cannot trade with a villager here", null, true);
                    this.cancelled = true;
                    this.cancelReason = "Player cannot trade with a villager here";
                    return;
                }
            }
            if (domainBlockManager.hasFlag(entity.getLocation(), Flag.Type.PREVENT_VILLAGER_TRADE_BY_NONALLOWED))
            {
                if (!hasBypass && !isAllowed)
                {
                    DomainParticles.denyAccess(entity.getLocation(), player);
                    instance.getDebug().log("PlayerEvents", "onPlayerInteractEntity", "Villager right click interaction cancelled");
                    instance.getMessage().send(player, "prevent_villager_trade", "&4You cannot trade with a villager here", null, true);
                    this.cancelled = true;
                    this.cancelReason = "Player cannot trade with a villager here";
                    return;
                }
            }
        }
    }



    public EquipmentSlot getHand() {
        return this.equipmentSlot;
    }

    public Entity getRightClicked() {
        return this.entity;
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
