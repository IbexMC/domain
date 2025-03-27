package com.ibexmc.domain.gui;

import java.util.List;

import com.ibexmc.internal.functions.ColorFunctions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIOption {
    private Material icon;
    private String name;
    private List<String> lore;
    private boolean enabled;

    public GUIOption(Material icon, String name, List<String> lore, boolean enabled) {
        //instance.getDebug().log("GUIOption", "Constructor", "Creating new GUIOption: &fIcon = " + icon.name() + " &fName = " + name + " &fEnabled = " + Boolean.toString(enabled));
        this.icon = icon;
        if (name == null) {
            name = "";
        }
        if (!name.equals("")) {
            this.name = name;
        } else {
            this.name = "&0.";
        }
        this.lore = lore;
        this.enabled = enabled;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ItemStack getItemStack() {
        if (icon == null) {
            this.icon = Material.AIR;
        }

        ItemStack itemStack = new ItemStack(this.icon, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(ColorFunctions.colorToString(this.name));
            itemMeta.setLore(this.lore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }
}
