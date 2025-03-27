package com.ibexmc.domain.api.events.block;

import java.util.List;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public abstract class BaseBlock extends Event {
    private static final HandlerList handlers = new HandlerList();
    private DomainBlock domainBlock;
    private Block block;
    private Block sourceBlock;
    private BlockFace blockFace;
    private List<Block> blocksMoved;
    private Player player;
    private IgniteCause igniteCause;
    private Domain instance;

    public BaseBlock(DomainBlock domainBlock) {
        this.domainBlock = domainBlock;
        this.instance = Domain.getInstance();
    }

    // Block Break
    public BaseBlock(DomainBlock domainBlock, Block block, Player player) {
        this.domainBlock = domainBlock;
        this.block = block;
        this.player = player;
        this.instance = Domain.getInstance();
    }

    // Block Place
    public BaseBlock(Block block, Player player) {
        this.block = block;
        this.player = player;
        this.instance = Domain.getInstance();
    }


    // Piston Extend
    public BaseBlock(BlockFace blockFace, List<Block> blocksMoved) {
        this.blockFace = blockFace;
        this.blocksMoved = blocksMoved;
        this.instance = Domain.getInstance();
    }

    // Block Ignite
    public BaseBlock(DomainBlock domainBlock, Block block, IgniteCause igniteCause, Player player) {
        this.domainBlock = domainBlock;
        this.block = block;
        this.igniteCause = igniteCause;
        this.player = player;
        this.instance = Domain.getInstance();
    }

    // Block Spread, Block From, Block To
    public BaseBlock(Block sourceBlock, Block block) {
        this.sourceBlock = sourceBlock;
        this.block = block;
        this.instance = Domain.getInstance();
    }

    // Block Burn, Block Grow
    public BaseBlock(Block block) {
        this.block = block;
        this.instance = Domain.getInstance();
    }

    public Block getBlock() {
        return this.block;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
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
        if (this.domainBlock != null) {
            stringBuilder.append("DomainBlock: ");
            stringBuilder.append(this.domainBlock.getFieldName());
            stringBuilder.append(" | ");
        }
        if (this.sourceBlock != null) {
            stringBuilder.append("Source Block: ");
            stringBuilder.append(this.sourceBlock.getType().name());
            stringBuilder.append(" | ");
        }
        if (this.blockFace != null) {
            stringBuilder.append("Block Face: ");
            stringBuilder.append(this.blockFace.name());
            stringBuilder.append(" | ");
        }
        if (this.blocksMoved != null) {
            stringBuilder.append("Blocks Moved Count: ");
            stringBuilder.append(this.blocksMoved.size());
            stringBuilder.append(" | ");
        }
        if (this.blockFace != null) {
            stringBuilder.append("Ignite Cause: ");
            stringBuilder.append(this.igniteCause.name());
            stringBuilder.append(" | ");
        }
        if (stringBuilder.toString().length() > 0) {
            stringBuilder.append("#");
        }
        String values = stringBuilder.toString().replace(" | #", "");
        instance.getLog().log("BaseBlock", "Player Event Base Values:", values);
    }

    public Domain getInstance() {
        return instance;
    }
}

