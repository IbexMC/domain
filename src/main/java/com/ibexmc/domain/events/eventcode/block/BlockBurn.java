package com.ibexmc.domain.events.eventcode.block;

import com.ibexmc.domain.Domain;
import org.bukkit.block.Block;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;

public class BlockBurn {
    private boolean cancelled = false;
    public void onBlockBurn(Block block) {

        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_BLOCK_BURN)) {
            this.cancelled = true;
            return;
        }
    }
    public boolean isCancelled() {
        return cancelled;
    }
}
