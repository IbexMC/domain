package com.ibexmc.domain.flag;

import java.util.ArrayList;
import java.util.List;

import com.ibexmc.domain.Domain;
import com.ibexmc.internal.functions.ColorFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagMenuItem {
    private Flag.Type flagType;
    private String name;
    private List<String> lore;
    private Material flagAllowed;
    private Material flagDenied;
    private boolean locked = false;

    public FlagMenuItem(Flag.Type flagType, String name, List<String> lore, Material flagAllowed, Material flagDenied, boolean locked) {
        this.flagType = flagType;
        this.name = name;
        this.lore = lore;
        this.flagAllowed = flagAllowed;
        this.flagDenied = flagDenied;
        this.locked = locked;
    }

    public FlagMenuItem(Flag.Type flagType) {
        this.flagType = flagType;
        this.name = this.flagType.name();
        this.lore = StringFunctions.stringToList("");
        this.flagAllowed = Material.BARRIER;
        this.flagDenied = Material.BARRIER;
        this.locked = false;
    }

    public Flag.Type getFlagType() {
        return flagType;
    }

    public void setFlagType(Flag.Type flagType) {
        this.flagType = flagType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Material getFlagAllowed() {
        return flagAllowed;
    }

    public void setFlagAllowed(Material flagAllowed) {
        this.flagAllowed = flagAllowed;
    }

    public Material getFlagDenied() {
        return flagDenied;
    }

    public void setFlagDenied(Material flagDenied) {
        this.flagDenied = flagDenied;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public ItemStack getItemStackAllowed() {
        if (flagAllowed == null) {
            this.flagAllowed = Material.LIME_STAINED_GLASS_PANE;
        }
        ItemStack itemStack = new ItemStack(this.flagAllowed, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (name != null) {
                if (!name.equals("")) {
                    itemMeta.setDisplayName(ColorFunctions.colorToString(this.name));
                } else {
                    itemMeta.setDisplayName(this.flagType.name());
                }
            } else {
                itemMeta.setDisplayName(this.flagType.name());
            }
            List<String> itemLore = new ArrayList<>();
            if (this.lore != null) {
                for (String loreLine : this.lore) {
                    itemLore.add(loreLine);
                }
                itemLore.add(Domain.getInstance().getLocale().localeText("flag_allow", "&2Flag enabled", null));
            }
            itemMeta.setLore(itemLore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    public ItemStack getItemStackDenied() {
        if (flagAllowed == null) {
            this.flagAllowed = Material.RED_STAINED_GLASS_PANE;
        }
        ItemStack itemStack = new ItemStack(this.flagDenied, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (name != null) {
                if (!name.equals("")) {
                    itemMeta.setDisplayName(ColorFunctions.colorToString(this.name));
                } else {
                    itemMeta.setDisplayName(this.flagType.name());
                }
            } else {
                itemMeta.setDisplayName(this.flagType.name());
            }
            List<String> itemLore = new ArrayList<>();
            if (this.lore != null) {
                for (String loreLine : this.lore) {
                    itemLore.add(loreLine);
                }
                itemLore.add(Domain.getInstance().getLocale().localeText("flag_deny", "&4Flag disabled", null));
            }
            itemMeta.setLore(itemLore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    public ItemStack getItemStackNA() {
        if (flagAllowed == null) {
            this.flagAllowed = Material.GRAY_STAINED_GLASS_PANE;
        }
        ItemStack itemStack = new ItemStack(this.flagDenied, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (name != null) {
                if (!name.equals("")) {
                    itemMeta.setDisplayName(ColorFunctions.colorToString(this.name));
                } else {
                    itemMeta.setDisplayName(this.flagType.name());
                }
            } else {
                itemMeta.setDisplayName(this.flagType.name());
            }
            if (lore != null) {
                if (lore.size() > 0) {
                    itemMeta.setLore(this.lore);
                }
            }
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
