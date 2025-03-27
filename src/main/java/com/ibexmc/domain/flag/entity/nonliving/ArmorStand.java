package com.ibexmc.domain.flag.entity.nonliving;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;

public class ArmorStand {
    public static class Damage {
        public static boolean preventNonAllowed(Player player, org.bukkit.entity.ArmorStand armorStand) {

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
            Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(armorStand.getLocation());
            DomainBlock pblock = domainBlockManager.getCurrentField(armorStand.getLocation());
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (hmFlags.get(Flag.Type.PREVENT_BREAK) != null && !isAllowed)
                    {
                        if (hmFlags.get(Flag.Type.PREVENT_BREAK))
                        {
                            if (hasBypass) {
                                instance.getMessage().send(player, "armorstand_break_deny_bypass", "&4You cannot break armor stands here, but have bypass mode enabled", null, true);
                                return false;
                            } else {
                                instance.getMessage().send(player, "armorstand_break_deny", "&4You cannot break armor stands here", null, true);
                                return true;
                            }
                        }
                    }
                }
            }

            return ret;
        }
        public static boolean preventExplosion(org.bukkit.entity.ArmorStand armorStand) {

            Domain instance = Domain.getInstance();
            DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
            boolean ret = false;
            Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(armorStand.getLocation());
            DomainBlock pblock = domainBlockManager.getCurrentField(armorStand.getLocation());
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (hmFlags.get(Flag.Type.PREVENT_EXPLOSION_DAMAGE) != null)
                    {
                        if (hmFlags.get(Flag.Type.PREVENT_EXPLOSION_DAMAGE))
                        {
                            return true;
                        }
                    }
                }
            }

            return ret;
        }
    }
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
            DomainBlock pblock = domainBlockManager.getCurrentField(block.getLocation());
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (!isAllowed) {
                        if (hmFlags.get(Flag.Type.PREVENT_BUILD) != null || hmFlags.get(Flag.Type.PREVENT_BUILD_EXCEPT_DOMAINBLOCK))
                        {
                            if (hmFlags.get(Flag.Type.PREVENT_BUILD) || hmFlags.get(Flag.Type.PREVENT_BUILD_EXCEPT_DOMAINBLOCK))
                            {
                                if (hasBypass) {
                                    instance.getMessage().send(player, "armorstand_place_deny_bypass", "&4You cannot place with armor stands here, but have bypass mode enabled", null, true);
                                    return false;
                                } else {
                                    instance.getMessage().send(player, "armorstand_place_deny", "&4You cannot place armor stands here", null, true);
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
