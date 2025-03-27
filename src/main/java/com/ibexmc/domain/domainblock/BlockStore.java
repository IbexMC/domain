package com.ibexmc.domain.domainblock;

import org.bukkit.inventory.ItemStack;

public class BlockStore {
    private ItemStack block;
    private String blockType;
    private int cost;

    public BlockStore(ItemStack block, String blockType, int cost) {
        this.block = block;
        this.blockType = blockType;
        this.cost = cost;
    }

    public void setBlock(ItemStack block) {
        this.block = block;
    }
    public ItemStack getBlock() {
        return block;
    }
    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }
    public String getBlockType() {
        return blockType;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public int getCost() {
        return cost;
    }

}
