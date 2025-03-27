package com.ibexmc.domain.helper;

import com.ibexmc.domain.Domain;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.flag.FlagManager;
import com.ibexmc.domain.util.functions.GameFunctions;
import com.ibexmc.domain.util.functions.PlayerFunctions;


import java.util.HashMap;
import java.util.Map;

public class FieldCommands {

    private final Domain instance;
    private DomainBlockManager domainBlockManager;

    public FieldCommands(Domain instance) {
        this.instance = instance;
        this.domainBlockManager = new DomainBlockManager(instance);
    }

    private void logBypass(Block block, Player player, Map<String, String> placeholder) {
        instance.getMessage().send(player, "interact_nonallowed_bypass", "Access to <%blocktype%> at <%location%> allowed due to bypass mode", placeholder, true);
        DomainFunctions.bypassLog(instance, player, "Accessed " + block.getType().name().toLowerCase().replace("_", " ") + " at " + WorldFunctions.simpleReadable(block.getLocation()));
    }

    private void denyAccessUser(Block block, Player player) {
        float volume = 0.5f;
        player.getWorld().playSound(block.getLocation(), Sound.BLOCK_CHEST_LOCKED, volume, 1);
        player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, block.getLocation(), 15, -1, 1.5, -1, 5);
    }

    private boolean preventInteract(Block block, Player player, Map<String, String> placeholder, boolean hasBypass, boolean isAllowed, Flag.Type flagType, String messageKey, String denyMessage) {

            boolean validBlock = false;
            switch (flagType) {
                case PREVENT_INTERACT_CHEST_BY_NONALLOWED:
                    if (MaterialGroups.chests().contains(block.getType())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_DOOR_ACCESS_BY_NONALLOWED:
                    if (MaterialGroups.doors().contains(block.getType())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_GATE_ACCESS_BY_NONALLOWED:
                    if (MaterialGroups.fenceGates().contains(block.getType())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_TRAPDOOR_ACCESS_BY_NONALLOWED:
                    if (MaterialGroups.trapdoors().contains(block.getType())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_BUTTON_ACCESS_BY_NONALLOWED:
                    if (MaterialGroups.buttons().contains(block.getType())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_PRESSURE_PLATE_ACCESS_BY_NONALLOWED:
                    if (MaterialGroups.pressurePlates().contains(block.getType())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_LEVER_BY_NONALLOWED:
                    if (block.getType() == Material.LEVER) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_FURNACE_BY_NONALLOWED:
                    if (block.getType() == Material.FURNACE || block.getType() == Material.FURNACE_MINECART) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_BREWING_STAND_BY_NONALLOWED:
                    if (block.getType() == Material.BREWING_STAND) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_CAKE_BY_NONALLOWED:
                    if (block.getType() == Material.CAKE) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_CRAFTING_TABLE_BY_NONALLOWED:
                    if (block.getType() == Material.CRAFTING_TABLE) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_ENCHANTING_TABLE_BY_NONALLOWED:
                    if (block.getType() == Material.ENCHANTING_TABLE) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_ANVIL_BY_NONALLOWED:
                    if (block.getType() == Material.ANVIL || block.getType() == Material.CHIPPED_ANVIL || block.getType() == Material.DAMAGED_ANVIL) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_ENDERCHEST_BY_NONALLOWED:
                    if (block.getType() == Material.ENDER_CHEST) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_REDSTONE_REPEATER_BY_NONALLOWED:
                    if (block.getType() == Material.REPEATER) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_SHULKERBOX_BY_NONALLOWED:
                    if (MaterialGroups.shulkerBoxes().contains(block.getType())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_BED_BY_NONALLOWED:
                    if (MaterialGroups.isBed(block.getType())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_DROPPER_BY_NONALLOWED:
                    if (block.getType() == Material.DROPPER) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_DISPENSER_BY_NONALLOWED:
                    if (block.getType() == Material.DISPENSER) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_HOPPER_BY_NONALLOWED:
                    if (block.getType() == Material.HOPPER || block.getType() == Material.HOPPER_MINECART) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_CAULDRON_BY_NONALLOWED:
                    if (MaterialGroups.isCauldron(block.getType().name())) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_COMPOSTER_BY_NONALLOWED:
                    if (block.getType() == Material.COMPOSTER) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_LOOM_BY_NONALLOWED:
                    if (block.getType() == Material.LOOM) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_BARREL_BY_NONALLOWED:
                    if (block.getType() == Material.BARREL) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_SMOKER_BY_NONALLOWED:
                    if (block.getType() == Material.SMOKER) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_BLAST_FURNACE_BY_NONALLOWED:
                    if (block.getType() == Material.BLAST_FURNACE) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_CARTOGRAPHY_TABLE_BY_NONALLOWED:
                    if (block.getType() == Material.CARTOGRAPHY_TABLE) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_GRINDSTONE_BY_NONALLOWED:
                    if (block.getType() == Material.GRINDSTONE) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_STONECUTTER_BY_NONALLOWED:
                    if (block.getType() == Material.STONECUTTER) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_BELL_BY_NONALLOWED:
                    if (block.getType() == Material.BELL) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_LECTERN_BY_NONALLOWED:
                    if (block.getType() == Material.LECTERN) {
                        validBlock = true;
                    }
                    break;
                case PREVENT_INTERACT_CAMPFIRE_BY_NONALLOWED:
                    if (block.getType() == Material.CAMPFIRE) {
                        validBlock = true;
                    }
                    if (GameFunctions.isVersionSafe(1.16)) {
                        if (block.getType() == Material.SOUL_CAMPFIRE) {
                            validBlock = true;
                        }
                    }
                    break;
                case PREVENT_INTERACT_BEEHIVE_BY_NONALLOWED:
                    if (GameFunctions.isVersionSafe(1.15)) {
                        if (block.getType() == Material.BEEHIVE) {
                            validBlock = true;
                        }
                    }
                    break;
                case PREVENT_INTERACT_BEENEST_BY_NONALLOWED:
                    if (GameFunctions.isVersionSafe(1.15)) {
                        if (block.getType() == Material.BEE_NEST) {
                            validBlock = true;
                        }
                    }
                    break;
                case PREVENT_INTERACT_SWEET_BERRY_BUSH_BY_NONALLOWED:
                    if (GameFunctions.isVersionSafe(1.15)) {
                        if (block.getType() == Material.SWEET_BERRY_BUSH) {
                            validBlock = true;
                        }
                    }
                    break;
                case PREVENT_INTERACT_BOOKSHELF_BY_NONALLOWED:
                    if (GameFunctions.isVersionSafe(1.20)) {
                        if (block.getType() == Material.CHISELED_BOOKSHELF) {
                            validBlock = true;
                        }
                    }
                    break;
                case PREVENT_INTERACT_SMITHINGTABLE_BY_NONALLOWED:
                    if (GameFunctions.isVersionSafe(1.16)) {
                        if (block.getType() == Material.SMITHING_TABLE) {
                            validBlock = true;
                        }
                    }
                    break;
                case PREVENT_INTERACT_LODESTONE_BY_NONALLOWED:
                    if (GameFunctions.isVersionSafe(1.16)) {
                        if (block.getType() == Material.LODESTONE) {
                            validBlock = true;
                        }
                    }
                    break;
            }

            if (validBlock) {
                if (!hasBypass && !isAllowed) {
                    denyAccessUser(block, player);
                    instance.getMessage().send(player, messageKey, denyMessage, placeholder, true);
                    return true;
                } else if (hasBypass && !isAllowed) {
                    logBypass(block, player, placeholder);
                }
            }

        return false;
    }

    public boolean stopInteractions(Player player, Block block, Action eventAction) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("<%blocktype%>", block.getType().name().toLowerCase().replace("_",  " "));
        placeholder.put("<%location%>", WorldFunctions.simpleReadable(block.getLocation()));

        boolean hasBypass = PlayerFunctions.hasBypass(player);
        boolean isAllowed = domainBlockManager.isAllowed(player, block.getLocation());
        boolean preventInteract = domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_INTERACT);
        Map<Flag.Type, Boolean> flags = domainBlockManager.getFlagsAtLocation(block.getLocation());
        if (preventInteract) {
            FlagManager flagManager = new FlagManager(instance);
            for (Flag.Type flagType : flagManager.interactionFlags()) {
                // Only adding to the map if it doesn't already exist.  If we have PREVENT_INTERACT and PREVENT_INTERACT_CHEST_BY_NONALLOWED = false
                // then we should be using the false value
                if (!flags.containsKey(flagType)) {
                    flags.put(flagType, true);
                }
            }
        }
        // Looping only through the flags that matter here - if PREVENT_INTERACT then it is all interaction flags, otherwise, only the ones at the location
        for (Map.Entry<Flag.Type, Boolean> flagEntry : flags.entrySet()) {
            if (flagEntry.getValue()) {
                switch (flagEntry.getKey()) {
                    case PREVENT_INTERACT_CHEST_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "chest_interact_nonallowed", "You cannot open chests here")) {
                            return true;
                        }
                        break;
                    case PREVENT_DOOR_ACCESS_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "door_interact_nonallowed", "You cannot open doors here")) {
                            return true;
                        }
                        break;
                    case PREVENT_GATE_ACCESS_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "gate_interact_nonallowed", "You cannot open gates here")) {
                            return true;
                        }
                        break;
                    case PREVENT_TRAPDOOR_ACCESS_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "trapdoor_interact_nonallowed", "You cannot access trapdoors here")) {
                            return true;
                        }
                        break;
                    case PREVENT_BUTTON_ACCESS_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "button_interact_nonallowed", "You cannot access buttons here")) {
                            return true;
                        }
                        break;
                    case PREVENT_PRESSURE_PLATE_ACCESS_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "plate_interact_nonallowed", "You cannot access pressure plates here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_LEVER_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "lever_interact_nonallowed", "You cannot access levers here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_FURNACE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "furnace_interact_nonallowed", "You cannot access furnaces here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_BREWING_STAND_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "brewingstand_interact_nonallowed", "You cannot access brewing stands here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_CAKE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "cake_interact_nonallowed", "You cannot eat cake here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_CRAFTING_TABLE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "craftingtable_interact_nonallowed", "You cannot access crafting tables here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_ENCHANTING_TABLE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "enchantingtable_interact_nonallowed", "You cannot access enchanting tables here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_ANVIL_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "anvil_interact_nonallowed", "You cannot access anvils here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_ENDERCHEST_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "enderchest_interact_nonallowed", "You cannot access enderchests here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_REDSTONE_REPEATER_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "redstone_repeater_interact_nonallowed", "You cannot access redstone repeaters here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_SHULKERBOX_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "shulkerbox_interact_nonallowed", "You cannot access shulker boxes here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_BED_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "bed_interact_nonallowed", "You cannot access beds here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_DROPPER_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "dropper_interact_nonallowed", "You cannot access droppers here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_DISPENSER_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "dispenser_interact_nonallowed", "You cannot access dispensers here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_HOPPER_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "hopper_interact_nonallowed", "You cannot access hoppers here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_CAULDRON_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "cauldron_interact_nonallowed", "You cannot access cauldrons here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_COMPOSTER_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "composter_interact_nonallowed", "You cannot access composters here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_LOOM_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "loom_interact_nonallowed", "You cannot access looms here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_BARREL_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "barrel_interact_nonallowed", "You cannot access barrels here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_SMOKER_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "smoker_interact_nonallowed", "You cannot access smokers here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_BLAST_FURNACE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "blastfurnace_interact_nonallowed", "You cannot access blast furnaces here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_CARTOGRAPHY_TABLE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "cartographytable_interact_nonallowed", "You cannot access cartography tables here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_GRINDSTONE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "grindstone_interact_nonallowed", "You cannot access grindstones here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_STONECUTTER_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "stonecutter_interact_nonallowed", "You cannot access stone cutters here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_BELL_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "bell_interact_nonallowed", "You cannot access bells here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_CAMPFIRE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "campfire_interact_nonallowed", "You cannot access campfires here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_LECTERN_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "lectern_interact_nonallowed", "You cannot access lecterns here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_BEEHIVE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "beehive_interact_nonallowed", "You cannot access beehives here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_BEENEST_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "beenest_interact_nonallowed", "You cannot access bee nests here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_SWEET_BERRY_BUSH_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "berry_interact_nonallowed", "You cannot access sweet berry bushes here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_BOOKSHELF_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "bookshelf_interact_nonallowed", "You cannot access bookshelves here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_SMITHINGTABLE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "smithingtable_interact_nonallowed", "You cannot access smithing tables here")) {
                            return true;
                        }
                        break;
                    case PREVENT_INTERACT_LODESTONE_BY_NONALLOWED:
                        if (preventInteract(block, player, placeholder, hasBypass, isAllowed, flagEntry.getKey(), "lodestone_interact_nonallowed", "You cannot access lodestones here")) {
                            return true;
                        }
                        break;
                }
            }
        }

        return false;
    }
}
