package com.ibexmc.domain.flag;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.gui.GUIOption;
import com.ibexmc.internal.functions.ItemFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.messaging.Error;
import com.ibexmc.internal.utility.IbexPersistentData;
import com.ibexmc.internal.utility.YmlParser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class FlagManager {

    private final Domain instance;

    public FlagManager(Domain instance) {
        this.instance = instance;
    }

    public void loadFlags() {
        instance.getDebug().log("DomainFlags", "loadFlags()", "&aLoading Flags from flags.yml");
        try {

            File file = new File(Domain.getInstance().getDataFolder()+"/flags.yml");

            if (!file.exists()) {
                Domain.getInstance().saveResource("flags.yml", false);
            }

            if (file.exists()) {

                YmlParser ymlParser = new YmlParser(
                        Domain.getInstance(),
                        file,
                        "Domain"
                );
                for (Flag.Type flagType : Flag.Type.values()) {
                    String flagName = "";
                    List<String> lore = new ArrayList<>();
                    Material iconOn = Material.LIME_STAINED_GLASS_PANE;
                    Material iconOff = Material.RED_STAINED_GLASS_PANE;
                    boolean locked = false;

                    YmlParser.Output crName = ymlParser.getString(
                            "flags." + flagType.name().toUpperCase() + ".name",
                            flagType.name()
                    );
                    if (crName.getSuccess()) {
                        flagName = crName.getString();
                        YmlParser.Output crLore = ymlParser.getString(
                                "flags." + flagType.name().toUpperCase() + ".lore",
                                ""
                        );
                        if (crLore.getSuccess()) {
                            lore = StringFunctions.stringToList(StringFunctions.color(crLore.getString()), "\\|");
                        }

                        YmlParser.Output crIconOn = ymlParser.getString(
                                "flags." + flagType.name().toUpperCase() + ".icon_on",
                                ""
                        );
                        if (crIconOn.getSuccess()) {
                            if (!StringFunctions.isNullOrEmpty(crIconOn.getString())) {
                                iconOn = ItemFunctions.materialFromName(crIconOn.getString());
                            }
                        }

                        YmlParser.Output crIconOff = ymlParser.getString(
                                "flags." + flagType.name().toUpperCase() + ".icon_off",
                                ""
                        );
                        if (crIconOff.getSuccess()) {
                            if (!StringFunctions.isNullOrEmpty(crIconOff.getString())) {
                                iconOff = ItemFunctions.materialFromName(crIconOff.getString());
                            }
                        }

                        YmlParser.Output crLocked = ymlParser.getBoolean(
                                "flags." + flagType.name().toUpperCase() + ".locked",
                                false
                        );
                        if (crLocked.getSuccess()) {
                            if (StringFunctions.isNullOrEmpty(crLocked.getString())) {
                                locked = crLocked.getBoolean();
                            }
                        }


                        FlagMenuItem flagMenuItem = new FlagMenuItem(
                                flagType,
                                flagName,
                                lore,
                                iconOn,
                                iconOff,
                                locked
                        );

                        Domain.getInstance().getData().putFlagMenuItem(flagType.name(), flagMenuItem);
                    }



                }


            } else {
                instance.getLog().log("DomainBlockManager", "&cERROR", "flags.yml does not exist.  This will cause issues with your flag selection.  Download the flags.yml and place it in your Domain folder");
                instance.getError().save(
                        "DomainConfig.init.flagMenuItems.002",
                        "DomainConfig",
                        "init().flagMenuItems",
                        "Unable to load flags.yml",
                        "flags.yml does not exist",
                        Error.Severity.URGENT,
                        null
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "DomainConfig.init.flagMenuItems.001",
                    "DomainConfig",
                    "init().flagMenuItems",
                    "Unexpected error getting flag menu items",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }

    public Map<Flag.Type, Boolean> fieldFlags(DomainBlock domainBlock) {
        Map<Flag.Type, Boolean> flags = new HashMap<>();
        // Load block flags
        if (domainBlock.getConfig().getFlags() != null) {
            for (Flag flag : domainBlock.getConfig().getFlags()) {
                instance.getDebug().log("FlagManager", "fieldFlags", "Block Flags - Flag: " + flag.getFlagType() + " Status: " + flag.isActive());
                flags.put(flag.getFlagType(), flag.isActive());
            }
        }
        // Overlay the field flags
        if (domainBlock.getFlags() != null) {
            for (Flag flag : domainBlock.getFlags()) {
                instance.getDebug().log("FlagManager", "fieldFlags", "Override Flags - Flag: " + flag.getFlagType() + " Status: " + flag.isActive());
                flags.put(flag.getFlagType(), flag.isActive());
            }
        }
        return flags;
    }
    public TreeMap<String, Flag.Type> fieldFlagsByName(DomainBlock domainBlock) {
        TreeMap<String, Flag.Type> flags = new TreeMap<>();
        // Load block flags
        if (domainBlock.getConfig().getFlags() != null) {
            for (Flag flag : domainBlock.getConfig().getFlags()) {
                flags.put(flagMenuItem(flag.getFlagType()).getName(), flag.getFlagType());
            }
        }
        // Overlay the field flags
        if (domainBlock.getFlags() != null) {
            for (Flag flag : domainBlock.getFlags()) {
                if (flag.getFlagType() != null) {
                    flags.put(flagMenuItem(flag.getFlagType()).getName(), flag.getFlagType());
                } else {
                    instance.getDebug().log("FlagManager", "fieldFlagsByName", "Flag is null");
                }
            }
        }
        return flags;
    }
    public ItemStack item(Flag.Type flagType, boolean flagStatus) {
        instance.getDebug().log(
                "FlagManager",
                "item",
                "Getting Item for " + flagType + " Status = " + flagStatus
        );
        GUIOption na = Domain.getInstance().getData().getGUIOption(
                "na", Material
                        .BLACK_STAINED_GLASS_PANE,
                "&0.",
                "",
                true
        );
        if (Domain.getInstance().getData().getFlagMenuItems().containsKey(flagType.toString())) {
            instance.getDebug().log(
                    "FlagManager",
                    "item",
                    "Flag type = " + flagType.name()
            );
            FlagMenuItem flagMenuItem = Domain.getInstance().getData().getFlagMenuItems().get(flagType.toString());
            if (flagMenuItem == null) {
                instance.getDebug().log(
                        "FlagManager",
                        "item",
                        "Flag menu item is null"
                );
                flagMenuItem = new FlagMenuItem(
                        flagType,
                        flagType.name(),
                        new ArrayList<>(),
                        Material.LIME_STAINED_GLASS_PANE,
                        Material.RED_STAINED_GLASS_PANE,
                        false
                );
            }
            instance.getDebug().log(
                    "FlagManager",
                    "item",
                    "Flag menu item is not null"
            );
            ItemStack item;
            IbexPersistentData persistentData = new IbexPersistentData(instance);
            if (flagStatus) {
                item = flagMenuItem.getItemStackAllowed();
                ItemMeta itemMeta = item.getItemMeta();
                persistentData.setString(itemMeta, "domain_flag", flagType.toString());
                persistentData.setInt(itemMeta, "domain_flag_status", 1);
                item.setItemMeta(itemMeta);
                //item = NBTEditor.set(item, flagType.toString(),"domain", "flag");
                //item = NBTEditor.set(item, "1","domain", "flag_status");
            } else {
                item = flagMenuItem.getItemStackDenied();
                ItemMeta itemMeta = item.getItemMeta();
                persistentData.setString(itemMeta, "domain_flag", flagType.toString());
                persistentData.setInt(itemMeta, "domain_flag_status", 0);
                item.setItemMeta(itemMeta);
            }
            return item;
        } else {
            instance.getDebug().log(
                    "FlagManager",
                    "item",
                    "Flag is not in map"
            );
        }
        return na.getItemStack();
    }
    public FlagMenuItem flagMenuItem(Flag.Type flagType) {
        if (Domain.getInstance().getData().getFlagMenuItems().containsKey(flagType.toString())) {
            return Domain.getInstance().getData().getFlagMenuItems().get(flagType.toString());
        }
        return new FlagMenuItem(
                flagType,
                flagType.name(),
                new ArrayList<String>(),
                Material.LIME_STAINED_GLASS_PANE,
                Material.RED_STAINED_GLASS_PANE,
                false
        );
    }

    public Set<Flag.Type> interactionFlags() {
        Set<Flag.Type> flags = new HashSet<>();
        flags.add(Flag.Type.PREVENT_BUTTON_ACCESS_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_DOOR_ACCESS_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_GATE_ACCESS_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_ANVIL_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_BARREL_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_BED_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_BEEHIVE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_BEENEST_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_BELL_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_BLAST_FURNACE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_BOOKSHELF_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_BREWING_STAND_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_CAKE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_CAMPFIRE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_CARTOGRAPHY_TABLE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_CAULDRON_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_CHEST_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_COMPOSTER_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_CRAFTING_TABLE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_DISPENSER_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_DROPPER_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_ENCHANTING_TABLE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_ENDERCHEST_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_FURNACE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_GRINDSTONE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_HOPPER_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_LECTERN_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_LEVER_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_LODESTONE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_LOOM_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_REDSTONE_REPEATER_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_SHULKERBOX_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_SMITHINGTABLE_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_SMOKER_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_STONECUTTER_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_INTERACT_SWEET_BERRY_BUSH_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_PRESSURE_PLATE_ACCESS_BY_NONALLOWED);
        flags.add(Flag.Type.PREVENT_TRAPDOOR_ACCESS_BY_NONALLOWED);
        return flags;
    }
}
