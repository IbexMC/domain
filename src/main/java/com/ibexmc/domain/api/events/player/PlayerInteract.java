package com.ibexmc.domain.api.events.player;

import java.util.List;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.open.*;
import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.domain.flag.entity.nonliving.ArmorStand;
import com.ibexmc.domain.helper.FieldCommands;
import com.ibexmc.domain.helper.MaterialGroups;
import com.ibexmc.domain.util.DomainParticles;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.flag.block.Flora;
import com.ibexmc.domain.flag.entity.living.Spawn;
import com.ibexmc.domain.flag.entity.nonliving.Vehicle;
import com.ibexmc.domain.flag.player.Interact;

public final class PlayerInteract extends BasePlayer implements Cancellable {

    private String cancelReason; // The cancellation reason
    private Event.Result useBlockInteract;
    private Event.Result useHandInteract;

    /**
     * PlayerInteracts
     * This event is called when a player interacts with something in a Domain field
     *
     * @param player - The player that ran the command
     *
     */
    public PlayerInteract(Player player, EquipmentSlot equipmentSlot, Action action, Block block) {

        super(player, equipmentSlot, action, block);

        Domain instance = super.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        RentManager rentManager = new RentManager(instance);
        IbexPersistentData persistentData = new IbexPersistentData(instance);
        
        instance.getDebug().log("Domain_PlayerInteractEvent", "PlayerInteract", "PlayerInteract called.  Action = " + action.toString());

        if (block != null){

            instance.getDebug().log("&eDomain_PlayerInteractEvent", "PlayerInteract", "Block: " + block.getType().name());


            DomainBlock clickedDomainBlock = domainBlockManager.getFromBlock(block);
            if (clickedDomainBlock != null) {
                // Block has been clicked

                if (equipmentSlot.equals(EquipmentSlot.HAND)) {
                    ItemStack inHand = player.getInventory().getItemInMainHand();
                    if (inHand != null) {
                        if (inHand.getType().isBlock()) {
                            BlockData blockData = inHand.getType().createBlockData();
                            if (blockData instanceof org.bukkit.block.data.type.Sign || blockData instanceof WallSign) {
                                if (clickedDomainBlock.getConfig().getRentCost() > 0 && clickedDomainBlock.getConfig().getRentDays() > 0) {
                                    if (domainBlockManager.isOwner(player, clickedDomainBlock)) {
                                        ItemStack rentSign = rentManager.rentSign(inHand.clone(), clickedDomainBlock.getPlacedId());
                                        if (rentSign != null) {
                                            player.getInventory().setItemInMainHand(rentSign);
                                            this.setCancelled(true);
                                            this.cancelReason = "Item is a rent sign";
                                            return;
                                        }
                                    } else {
                                        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                        this.setCancelled(true);
                                        this.cancelReason = "Player is not the owner";
                                        return;
                                    }
                                } else {
                                    instance.getMessage().send(
                                            player,
                                            "rent_not_available",
                                            "&4This field is not available for rent",
                                            null,
                                            true
                                    );
                                    SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
                                    this.setCancelled(true);
                                    this.cancelReason = "Item is a rent sign";
                                    return;
                                }

                            }
                        }
                    }
                }
            }
            //region Domain Block/Rent Sign
            //region Open Domain Block (Resize, teleport etc.)
            if (equipmentSlot != null) {
                if (equipmentSlot.equals(EquipmentSlot.HAND)) {
                    ItemStack inHand = player.getInventory().getItemInMainHand();
                    if (Domain.getInstance().getData().openBlocks.containsKey(player.getUniqueId())) {
                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Player has an open block");
                        IOpenDomainBlock openDomainBlock = Domain.getInstance().getData().openBlocks.get(
                                player.getUniqueId()
                        );
                        if (openDomainBlock != null) {
                            //region Merge
                            if (openDomainBlock instanceof Merge) {
                                Merge merge = (Merge) openDomainBlock;
                                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                                    merge.merge(player);
                                    this.useHandInteract = Event.Result.DENY;
                                    this.useBlockInteract = Event.Result.DENY;
                                    this.cancelReason = "Merge event";
                                    return;
                                }
                            }
                            //endregion
                            //region Teleport
                            if (openDomainBlock instanceof Teleport) {
                                Teleport teleport = (Teleport) openDomainBlock;
                                boolean teleportResult = false;
                                if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                                    teleportResult = teleport.set(player, block);
                                }
                                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                                    teleportResult = teleport.cancel(player, block);
                                }
                                if (teleportResult) {
                                    this.useHandInteract = Event.Result.DENY;
                                    this.useBlockInteract = Event.Result.DENY;
                                    this.cancelReason = "Teleport set event";
                                    return;
                                }
                            }
                            //endregion
                            //region Magnet
                            if (openDomainBlock instanceof Magnet) {
                                Magnet magnet = (Magnet) openDomainBlock;
                                boolean magnetResult = false;
                                if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                                    magnetResult = magnet.set(player, block);
                                }
                                if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                                    magnetResult = magnet.cancel(player, block);
                                }
                                if (magnetResult) {
                                    this.useHandInteract = Result.DENY;
                                    this.useBlockInteract = Result.DENY;
                                    this.cancelReason = "Magnet set event";
                                    return;
                                }
                            }
                            //endregion
                            if (inHand != null) {
                                //region Resize
                                if (persistentData.hasString(inHand, "domain_wand")) {
                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "It was a wand");
                                    // Item in hand is a wand
                                    if (persistentData.getString(inHand, "domain_wand").equalsIgnoreCase("resize")) {
                                        if (openDomainBlock instanceof Resize) {
                                            Resize resize = (Resize) openDomainBlock;
                                            switch (action) {
                                                case LEFT_CLICK_BLOCK:
                                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Left click");
                                                    resize.corner1(block.getLocation());
                                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Corner 1 set");
                                                    resize.display(player);
                                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Displaying after corner 1 set");
                                                    break;
                                                case RIGHT_CLICK_BLOCK:
                                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Right click");
                                                    DomainBlock domainBlock = domainBlockManager.getFromBlock(block);
                                                    if (domainBlock == null) {
                                                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Not a domain block");
                                                        resize.corner2(block.getLocation());
                                                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Corner 2 set");
                                                        resize.display(player);
                                                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Displaying after corner 2 set");
                                                    } else {
                                                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Is a domain block");
                                                        if (domainBlock.getPlacedId() == resize.placedId()) {
                                                            instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Finishing");
                                                            if (resize.valid(player)) {
                                                                instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Valid");
                                                                resize.finish(player);
                                                            } else {
                                                                instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Not valid");
                                                            }
                                                        }
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    this.useHandInteract = Event.Result.DENY;
                                    this.useBlockInteract = Event.Result.DENY;
                                    this.cancelReason = "Resize event";
                                    return;
                                } else {
                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Not a wand");
                                }
                                //endregion
                            }
                        }
                    } else {
                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cChecking if we should open the block");
                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cPlayer = " + player.getName());
                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cPlayer UID = " + player.getUniqueId().toString());
                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cInHand = " + inHand.getType().name());
                        //region Open Block
                        if (inHand.getType().equals(Material.STICK)) {
                            if (persistentData.hasString(inHand, "domain_wand")) {
                                inHand = new ItemStack(Material.AIR, 1);
                                player.getInventory().setItemInMainHand(inHand);
                                instance.getMessage().send(
                                        player,
                                        "remove_wand",
                                        "&cWand not in use, removing it from inventory",
                                        null,
                                        true
                                );
                                this.useHandInteract = Event.Result.DENY;
                                this.useBlockInteract = Event.Result.DENY;
                                this.cancelReason = "Removing Wand";
                                return;
                            }
                        }
                        if (inHand.getType() == Material.AIR) {
                            instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cEmpty hand (AIR)");
                            if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                                instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cAction is right click");
                                DomainBlock domainBlock = domainBlockManager.getFromBlock(block);
                                if (domainBlock != null) {
                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cBlock is a domain block - opening");
                                    domainBlockManager.openBlock(player, domainBlock);
                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cExiting out of player interact");
                                    this.useHandInteract = Event.Result.DENY;
                                    this.useBlockInteract = Event.Result.DENY;
                                    this.cancelReason = "Open block event";
                                    return;
                                } else {
                                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "&cDomainBlock from block returned null");
                                }
                            }
                        }
                        //endregion
                    }
                }
            }
            //endregion
            //endregion

            if (domainBlockManager.inField(block.getLocation())) {

                instance.getDebug().log("&eDomain_PlayerInteractEvent", "PlayerInteract", "In field");
                // Stop interactions
                if (equipmentSlot != null) {
                    if (equipmentSlot.equals(EquipmentSlot.HAND) || action.equals(Action.PHYSICAL)) {
                        FieldCommands fieldCommands = new FieldCommands(instance);
                        if (fieldCommands.stopInteractions(player, block, action)) {
                            this.useHandInteract = Event.Result.DENY;
                            this.useBlockInteract = Event.Result.DENY;
                            this.cancelReason = "Hand interaction has been cancelled";
                            instance.getDebug().log("&eDomain_PlayerInteractEvent", "PlayerInteract", "Hand interaction has been cancelled");
                            return;
                        }
                    }
                }

                switch (action) {
                    case PHYSICAL:
                        // Prevent Crop Damage <start>
                        if (Flora.FarmLand.Damage.preventNonAllowed(player, block)) {
                            this.useBlockInteract = Event.Result.DENY;
                            this.cancelReason = "Block interaction has been cancelled";
                            return;
                        }
                        // Prevent Crop Damage <end>
                        // Stop interactions <start>
                        FieldCommands fieldCommands = new FieldCommands(instance);
                        if (fieldCommands.stopInteractions(player, block, action)) {
                            this.useBlockInteract = Event.Result.DENY;
                            this.cancelReason = "Block interaction has been cancelled";
                            return;
                        }
                        // Stop interactions <end>
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (equipmentSlot != null) {
                            if (equipmentSlot.equals(EquipmentSlot.HAND)) { //If the event is fired by HAND (main hand)
                                if (block != null) {

                                    if (!domainBlockManager.isDomainBlock(block)) // Check that we're not right-clicking a domain block
                                    {
                                        // Not a domain block
                                        if (PlayerFunctions.materialInMainHand(player) != Material.AIR) // If we're not right clicking with an empty hand, we can test for a wand
                                        {
                                            boolean exitEvent = false;
                                            ItemStack istack = PlayerFunctions.itemStackInMainHand(player); // Get the main hand item


                                            if (istack != null) {
                                                ItemMeta imeta = istack.getItemMeta();
                                                if (imeta != null) {
                                                    switch (imeta.getDisplayName().toLowerCase()) {
                                                        case "domain owner wand":
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                            }
                                            if (exitEvent) {
                                                return;
                                            }
                                        }
                                    } else {
                                        // It is a domain block
                                        if (PlayerFunctions.materialInMainHand(player) != Material.AIR) // If we're not right clicking with an empty hand, we can test for a wand
                                        {
                                            boolean exitEvent = false;
                                            ItemStack istack = PlayerFunctions.itemStackInMainHand(player); // Get the main hand item
                                            if (istack != null) {
                                                ItemMeta imeta = istack.getItemMeta();
                                                if (imeta != null) {
                                                    switch (imeta.getDisplayName().toLowerCase()) {
                                                        case "domain owner wand":
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                            }
                                            if (exitEvent) {
                                                return;
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        break;
                    case RIGHT_CLICK_BLOCK:
                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Right click captured");

                        if (player != null) {
                            ItemStack inHand = new ItemStack(Material.AIR, 1);
                            if (equipmentSlot == EquipmentSlot.HAND) {
                                inHand = player.getInventory().getItemInMainHand();
                            }
                            if (equipmentSlot == EquipmentSlot.OFF_HAND) {
                                inHand = player.getInventory().getItemInOffHand();
                            }
                            boolean isVehicle = false;
                            boolean isArmorStand = false;

                            switch (inHand.getType()) {
                                case MINECART:
                                case CHEST_MINECART:
                                case FURNACE_MINECART:
                                case HOPPER_MINECART:
                                case COMMAND_BLOCK_MINECART:
                                case TNT_MINECART:
                                case ACACIA_BOAT:
                                case BIRCH_BOAT:
                                case DARK_OAK_BOAT:
                                case JUNGLE_BOAT:
                                case OAK_BOAT:
                                case SPRUCE_BOAT:
                                    isVehicle = true;
                                    break;
                                case ARMOR_STAND:
                                    isArmorStand = true;
                                    break;
                                default:
                                    break;
                            }

                            for (Material material : MaterialGroups.spawnEggs()) {
                                if (inHand.getType() == material) {
                                    if (Spawn.SpawnEgg.prevent(player, block)) {
                                        this.useBlockInteract = Event.Result.DENY;
                                        this.cancelReason = "Spawn eggs are not allowed here.  Event has been cancelled";
                                        DomainParticles.denyAccessSmall(block.getLocation(), player);
                                        return;
                                    }
                                }
                            }
                            for (Material material : MaterialGroups.fishBuckets()) {
                                if (inHand.getType() == material) {
                                    if (Spawn.Bucket.preventNonAllowed(player, block)) {
                                        this.useBlockInteract = Event.Result.DENY;
                                        this.cancelReason = "Fish spawning has been cancelled";
                                        DomainParticles.denyAccessSmall(block.getLocation(), player);
                                        return;
                                    }
                                }
                            }

                            if (isVehicle) {
                                if (Vehicle.Place.preventNonAllowed(player, block)) {
                                    this.useBlockInteract = Event.Result.DENY;
                                    this.cancelReason = "Block interaction has been cancelled";
                                    instance.getMessage().send(player, "vehicle_place_deny", "&cYou cannot place vehicles here", null, true);
                                    return;
                                }
                            }
                            if (isArmorStand) {
                                if (ArmorStand.Place.preventNonAllowed(player, block)) {
                                    this.useBlockInteract = Event.Result.DENY;
                                    this.cancelReason = "Block interaction has been cancelled";
                                    DomainParticles.denyAccessSmall(block.getLocation(), player);
                                    return;
                                }
                            }
                        }

                        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_FIREWORK_LAUNCH_BY_NONALLOWED)) {
                            Material m = PlayerFunctions.materialInMainHand(player);
                            if (m.equals(Material.FIREWORK_ROCKET)) {
                                if (!PlayerFunctions.hasBypass(player)) {
                                    instance.getMessage().send(player, "firework_launch_deny", "&4You cannot launch fireworks here", null, true);
                                    this.useHandInteract = Event.Result.DENY;
                                    this.cancelReason = "Hand interaction has been cancelled";
                                    return;
                                } else {
                                    instance.getMessage().send(player, "firework_launch_deny_bypass", "&4You cannot launch fireworks here, but have bypass mode enabled", null, true);
                                }
                            }
                        }

                        // Checking for spawn eggs <start>
                        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_SPAWNER_EGG)) {
                            Material m = PlayerFunctions.materialInMainHand(player);

                            List<Material> eggs = MaterialGroups.spawnEggs();

                            if (eggs != null) {
                                for (Material megg : eggs) {
                                    if (megg == m) {
                                        if (!PlayerFunctions.hasBypass(player)) {
                                            instance.getMessage().send(player, "prevent_spawn_eggs", "&4You cannot use spawn eggs here", null, true);
                                            this.useHandInteract = Event.Result.DENY;
                                            this.cancelReason = "Hand interaction has been cancelled - Spawn eggs cannot be used here";
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        // Checking for spawn eggs <end>

                        // Prevent Lava Place <start>
                        if (PlayerFunctions.materialInMainHand(player) == Material.LAVA_BUCKET) {
                            if (Interact.Lava.Place.preventNonAllowed(player, block)) {
                                this.useHandInteract = Event.Result.DENY;
                                this.cancelReason = "Hand interaction has been cancelled - Lava cannot be placed here";
                                return;
                            }
                        }
                        // Prevent Lava Place <end>

                        break;
                    default:
                        break;
                }
            }
        }

    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Event.Result useBlockInteraction() {
        return useBlockInteract;
    }

    public void setBlockInteraction(boolean cancel) {
        useBlockInteract = Event.Result.DENY;
    }

    public Event.Result useHandInteraction() {
        return useHandInteract;
    }

    public void setHandInteraction(boolean cancel) {
        useHandInteract = Event.Result.DENY;
    }

    @Override
    public boolean isCancelled() {
        if (useBlockInteract == Event.Result.DENY || useHandInteract == Event.Result.DENY) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setCancelled(boolean cancel) {
        if (cancel) {
            this.useBlockInteract = Event.Result.DENY;
            this.useHandInteract = Event.Result.DENY;
        } else {
            this.useBlockInteract = Event.Result.ALLOW;
            this.useHandInteract = Event.Result.ALLOW;
        }
    }

}
