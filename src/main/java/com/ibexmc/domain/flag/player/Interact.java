package com.ibexmc.domain.flag.player;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.GameFunctions;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.ibexmc.domain.domainblock.DomainBlock;

import java.util.Map;

public class Interact {
    public static class Lava {
        public static class Place {
            public static boolean preventNonAllowed(Player player, Block block) {

                Domain instance = Domain.getInstance();
                DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
                
                boolean ret = false;

                boolean isPlayer = PlayerFunctions.isEntityPlayer(player);
                boolean isAllowed = domainBlockManager.isAllowed(player);
                boolean hasBypass = false;
                if (isPlayer)
                {
                    hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
                }
                Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(block.getLocation());
                DomainBlock pblock = domainBlockManager.getCurrentField(player.getLocation());
                if (pblock != null) {
                    if (pblock.getPlacedId() > 0) {
                        if (hmFlags.get(Flag.Type.PREVENT_LAVA_PLACE) != null && !isAllowed)
                        {
                            if (hmFlags.get(Flag.Type.PREVENT_LAVA_PLACE) && !hasBypass)
                            {
                                return true;
                            }
                        }
                    }
                }

                return ret;
            }
        }
    }
    public static class Tool {
        public static class CraftingTable {

        }
        public static class Furnace {

        }
        public static class SmithingTable {
            public static boolean preventNonAllowed(Player player, SmithingTable smithingTable) {
                if (GameFunctions.isVersionSafe(1.16)) {
                    return true; // TODO: finish this (current code is for sample only)
                } else {
                    return false;
                }
            }
        }
    }
}
