package com.ibexmc.domain.flag.redstone;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import org.bukkit.block.Block;

import java.util.Map;

public class TNT {
    public static class Priming {
        public static boolean prevent(Block block) {

            Domain instance = Domain.getInstance();
            DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

            /* PaperMC ONLY */
            if (!instance.getData().isPaper()) {
                return false;
            }
            Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(block.getLocation());
            DomainBlock pblock = domainBlockManager.getCurrentField(block.getLocation());
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (hmFlags.get(Flag.Type.PREVENT_TNT_PRIMING) != null) {
                        if (hmFlags.get(Flag.Type.PREVENT_TNT_PRIMING)) {
                            instance.getDebug().log(
                                    "TNTPriming",
                                    "prevent",
                                    "TNT Priming cancelled"
                            );
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
}
