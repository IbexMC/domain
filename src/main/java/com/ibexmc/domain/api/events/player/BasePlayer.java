package com.ibexmc.domain.api.events.player;

import com.ibexmc.domain.Domain;
import com.ibexmc.internal.functions.ItemFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

public abstract class BasePlayer extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Block block;
    private Material material;
    private String message;
    private Item item;
    private Entity entity;
    private EquipmentSlot equipmentSlot;
    private Location locationFrom;
    private Location locationTo;
    private Action action;
    private Vector vector;
    private Domain instance;

    // Player Join, Player Quit
    public BasePlayer(Player player) {
        this.player = player;
        this.instance = Domain.getInstance();
    }

    // Player Interact
    public BasePlayer(Player player, EquipmentSlot equipmentSlot, Action action, Block block) {
        this.player = player;
        this.equipmentSlot = equipmentSlot;
        this.action = action;
        this.block = block;
        this.instance = Domain.getInstance();
    }

    // Player Interact At Entity
    public BasePlayer(Player player, EquipmentSlot equipmentSlot, Vector vector, Entity entity) {
        this.player = player;
        this.equipmentSlot = equipmentSlot;
        this.vector = vector;
        this.entity = entity;
        this.instance = Domain.getInstance();
    }

    public BasePlayer(Player player, EquipmentSlot equipmentSlot, Entity entity) {
        this.player = player;
        this.equipmentSlot = equipmentSlot;
        this.entity = entity;
        this.instance = Domain.getInstance();
    }

    // Player Shear
    public BasePlayer(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
        this.instance = Domain.getInstance();
    }

    // Player Move
    public BasePlayer(Player player, Location locationFrom, Location locationTo ) {
        this.player = player;
        this.locationFrom = locationFrom;
        this.locationTo = locationTo;
        this.instance = Domain.getInstance();
    }

    // Bucket Empty, Bucket Fill
    public BasePlayer(Player player, Block block, Material material) {
        this.player = player;
        this.block = block;
        this.material = material;
        this.instance = Domain.getInstance();
    }

    // Command PreProcess
    public BasePlayer(Player player, String message) {
        this.player = player;
        this.message = message;
        this.instance = Domain.getInstance();
    }

    // Player Drop
    public BasePlayer(Player player, Item item) {
        this.player = player;
        this.item = item;
        this.instance = Domain.getInstance();
    }

    public Player getPlayer() {
        return this.player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Domain getInstance() {
        return instance;
    }

    public void logBaseValues() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.player != null) {
            stringBuilder.append("Player: ");
            stringBuilder.append(this.player.getName());
            stringBuilder.append(" | ");
        }
        if (this.block != null) {
            stringBuilder.append("Block Type: ");
            stringBuilder.append(this.block.getType().name());
            stringBuilder.append(" | ");
            stringBuilder.append("Block Location: ");
            stringBuilder.append(WorldFunctions.simpleReadable(this.block.getLocation()));
            stringBuilder.append(" | ");
        }
        if (this.material != null) {
            stringBuilder.append("Material: ");
            stringBuilder.append(this.material.name());
            stringBuilder.append(" | ");
        }
        if (this.message != null) {
            stringBuilder.append("Message: ");
            stringBuilder.append(this.message);
            stringBuilder.append(" | ");
        }
        if (this.item != null) {
            if (this.item.getItemStack() != null) {
                stringBuilder.append("ItemStack: ");
                stringBuilder.append(ItemFunctions.getFriendlyStringFromItemStack(this.item.getItemStack()));
                stringBuilder.append(" | ");
            }
        }
        if (this.entity != null) {
            stringBuilder.append("Entity Type: ");
            stringBuilder.append(this.entity.getType().name());
            stringBuilder.append(" | ");
            stringBuilder.append("Entity Location: ");
            stringBuilder.append(WorldFunctions.simpleReadable(this.entity.getLocation()));
            stringBuilder.append(" | ");
        }
        if (this.equipmentSlot != null) {
            stringBuilder.append("Equipment Slot: ");
            stringBuilder.append(this.equipmentSlot.name());
            stringBuilder.append(" | ");
        }
        if (this.action != null) {
            stringBuilder.append("Action: ");
            stringBuilder.append(this.action.name());
            stringBuilder.append(" | ");
        }
        if (this.locationFrom != null) {
            stringBuilder.append("Location From: ");
            stringBuilder.append(WorldFunctions.simpleReadable(this.locationFrom));
            stringBuilder.append(" | ");
        }
        if (this.locationTo != null) {
            stringBuilder.append("Location To: ");
            stringBuilder.append(WorldFunctions.simpleReadable(this.locationTo));
            stringBuilder.append(" | ");
        }
        if (stringBuilder.toString().length() > 0) {
            stringBuilder.append("#");
        }
        if (this.vector != null) {
            stringBuilder.append("Vector: " + vector.toString());
        }
        String values = stringBuilder.toString().replace(" | #", "");
        instance.getLog().log("BasePlayer", "Player Event Base Values:", values);
    }
}

