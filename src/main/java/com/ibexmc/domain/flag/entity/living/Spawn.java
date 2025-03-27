package com.ibexmc.domain.flag.entity.living;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;

public class Spawn {

    public static class SpawnEgg {
        public static boolean prevent(Player player, Block block) {

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
            DomainBlock pblock = domainBlockManager.getCurrentField(block.getLocation());
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (!isAllowed) {
                        if (hmFlags.get(Flag.Type.PREVENT_SPAWNER_EGG) != null)
                        {
                            if (hmFlags.get(Flag.Type.PREVENT_SPAWNER_EGG))
                            {
                                if (!hasBypass) {
                                    instance.getMessage().send(player, "prevent_spawn_eggs", "&4You cannot use spawn eggs here", null, true);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            return ret;
        }
    }
    public static class Bucket {
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
            DomainBlock pblock = domainBlockManager.getCurrentField(block.getLocation());
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (!isAllowed) {
                        if (hmFlags.get(Flag.Type.PREVENT_SPAWN_FISH_FROM_BUCKET_BY_NONALLOWED) != null)
                        {
                            if (hmFlags.get(Flag.Type.PREVENT_SPAWN_FISH_FROM_BUCKET_BY_NONALLOWED))
                            {
                                if (hasBypass) {
                                    instance.getMessage().send(player, "fishbucket_place_deny_bypass", "&4You cannot place fish here, but have bypass mode enabled", null, true);
                                    return false;
                                } else {
                                    instance.getMessage().send(player, "fishbucket_place_deny", "&4You cannot place fish here", null, true);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            return ret;
        }
    }
}
