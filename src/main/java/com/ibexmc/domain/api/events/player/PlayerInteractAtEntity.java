package com.ibexmc.domain.api.events.player;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.helper.MaterialGroups;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.entity.living.Animal;
import com.ibexmc.domain.flag.entity.living.HostileMob;

public final class PlayerInteractAtEntity extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private boolean cancelled; // A flag to indicate if the event was cancelled
    private EquipmentSlot equipmentSlot;
    private Vector vector;
    private Entity entity;

    public PlayerInteractAtEntity(Player player,  EquipmentSlot equipmentSlot, Vector vector, Entity entity) {

        super(player, equipmentSlot, vector, entity);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        this.equipmentSlot = equipmentSlot;
        this.vector = vector;
        this.entity = entity;

        instance.getDebug().log("DomainPlayerInteractAtEntity", "PlayerInteractAtEntity", "PlayerInteractAtEntity called");
        if (entity != null) {
            instance.getDebug().log("DomainPlayerInteractAtEntity", "PlayerInteractAtEntity", "Entity: " + entity.getType());
        }

        boolean hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
        boolean isAllowed = domainBlockManager.isAllowed(player, player.getLocation());

        // Prevent non-allowed players from interacting with armor stands in the field
        if (entity.getType() == EntityType.ARMOR_STAND) {
            if (domainBlockManager.hasFlag(entity.getLocation(), Flag.Type.PREVENT_INTERACT_ARMOR_STAND_BY_NONALLOWED)) {
                if (!isAllowed) {
                    if (!hasBypass) {
                        this.cancelled = true;
                        this.cancelReason = "Player is not allowed to interact with armor stands here here.  Event has been cancelled";
                        instance.getMessage().send(player, "armorstand_deny", "&4You cannot access armor stands here", null, true);
                        return;
                    } else {
                        instance.getMessage().send(player, "armorstand_deny_bypass", "&4You cannot interact with armor stands here, but have bypass mode enabled", null, true);
                        instance.getDebug().log(
                                "PlayerInteractAtEntity",
                                "PlayerInteractAtEntity(Player, EquipmentSlot, Vector, Entity)",
                                player.getName() +
                                        " accessed an armor stand at " +
                                        WorldFunctions.simpleReadable(entity.getLocation())
                        );
                    }
                }
            }
        }

        // Get the ItemStack being used in the interaction
        ItemStack itemStack = new ItemStack(Material.AIR, 1);
        if (equipmentSlot == EquipmentSlot.HAND) {
            itemStack = player.getInventory().getItemInMainHand();
        } else if (equipmentSlot == EquipmentSlot.OFF_HAND) {
            itemStack = player.getInventory().getItemInOffHand();
        }
        if (itemStack == null) {
            return;
        }

        // Prevent non-allowed players from healing zombie villagers in the field
        if (entity instanceof ZombieVillager) {
            ZombieVillager zombieVillager = (ZombieVillager) entity;
            if (itemStack.getType() == Material.GOLDEN_APPLE) {
                if (HostileMob.ZombieVillager.Heal.preventNonAllowed(player, zombieVillager)) {
                    Domain.getInstance().getData().addConvertingZombieVillager(
                            zombieVillager.getEntityId()
                    );
                    zombieVillager.setConversionTime(20);
                    this.cancelled = true;
                    this.cancelReason = "Player is not allowed to heal zombie villagers here.  Event has been cancelled";
                    return;
                }
            }
        }

        // Prevent animals being saddled
        if (itemStack.getType() == Material.SADDLE) {
            for (EntityType etype : MaterialGroups.rideable()) {
                if (etype == entity.getType()) {
                    if (Animal.Saddle.preventNonAllowed(player, entity.getLocation())) {
                        final ItemStack saddle = itemStack.clone();
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Domain.getInstance(), new Runnable(){
                            public void run(){
                                if (entity instanceof Pig) {
                                    instance.getDebug().log("PlayerInteractAtEntity", "setSaddle", "Setting saddle for entity: " + entity.getName());
                                    ((Pig) entity).setSaddle(false);
                                    if (equipmentSlot == EquipmentSlot.HAND) {
                                        player.getInventory().setItemInMainHand(saddle);
                                    } else if (equipmentSlot == EquipmentSlot.OFF_HAND) {
                                        player.getInventory().setItemInOffHand(saddle);
                                    }
                                }
                                if (entity instanceof Horse) {
                                    Horse horse = (Horse) entity;
                                    instance.getDebug().log("PlayerInteractAtEntity", "setSaddle", horse.toString());
                                    horse.getInventory().setSaddle(new ItemStack(Material.AIR));
                                    if (equipmentSlot == EquipmentSlot.HAND) {
                                        player.getInventory().setItemInMainHand(saddle);
                                    } else if (equipmentSlot == EquipmentSlot.OFF_HAND) {
                                        player.getInventory().setItemInOffHand(saddle);
                                    }
                                }
                                if (entity instanceof Donkey) {
                                    Donkey donkey = (Donkey) entity;
                                    donkey.getInventory().setSaddle(new ItemStack(Material.AIR));
                                    if (equipmentSlot == EquipmentSlot.HAND) {
                                        player.getInventory().setItemInMainHand(saddle);
                                    } else if (equipmentSlot == EquipmentSlot.OFF_HAND) {
                                        player.getInventory().setItemInOffHand(saddle);
                                    }
                                }
                                if (entity instanceof Mule) {
                                    Mule mule = (Mule) entity;
                                    mule.getInventory().setSaddle(new ItemStack(Material.AIR));
                                    if (equipmentSlot == EquipmentSlot.HAND) {
                                        player.getInventory().setItemInMainHand(saddle);
                                    } else if (equipmentSlot == EquipmentSlot.OFF_HAND) {
                                        player.getInventory().setItemInOffHand(saddle);
                                    }
                                }
                            }
                        }, 5);
                        this.cancelled = true;
                        this.cancelReason = "Player is not allowed to use a saddle here.  Event has been cancelled";
                        return;
                    }
                }
            }
        }

    }



    public EquipmentSlot getHand() {
        return this.equipmentSlot;
    }

    public Vector getVector() {
        return this.vector;
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
