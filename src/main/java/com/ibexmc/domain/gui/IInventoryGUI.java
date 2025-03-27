package com.ibexmc.domain.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public interface IInventoryGUI extends InventoryHolder {

    /**
     * Used to handle inventory clicking
     * @param event InventoryClickEvent to process
     */
    void onClick(InventoryClickEvent event);

    /**
     * Used to handle inventory closing
     * @param event InventoryCloseEvent to process
     */
    void onClose(InventoryCloseEvent event);
}
