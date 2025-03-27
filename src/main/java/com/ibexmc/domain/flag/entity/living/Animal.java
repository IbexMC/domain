package com.ibexmc.domain.flag.entity.living;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class Animal {
    public static class Saddle {
        public static boolean preventNonAllowed(Player player, Location location) {

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
            Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(location);
            DomainBlock pblock = domainBlockManager.getCurrentField(location);
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (!isAllowed) {
                        if (hmFlags.get(Flag.Type.PREVENT_SADDLE_BY_NONALLOWED) != null)
                        {
                            if (hmFlags.get(Flag.Type.PREVENT_SADDLE_BY_NONALLOWED))
                            {
                                if (!hasBypass) {
                                    instance.getMessage().send(player, "saddle_deny", "&4You cannot use a saddle here", null, true);
                                    return true;
                                } else {
                                    instance.getMessage().send(player, "saddle_deny_bypass", "&4You cannot use a saddle here, but have Domain bypass enabled", null, true);
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

            return ret;
        }
    }
    public static class Lead {
        public static boolean preventNonAllowed(Player player, Location location) {

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
            Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(location);
            DomainBlock pblock = domainBlockManager.getCurrentField(location);
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (!isAllowed) {
                        if (hmFlags.get(Flag.Type.PREVENT_LEAD_BY_NONALLOWED) != null)
                        {
                            if (hmFlags.get(Flag.Type.PREVENT_LEAD_BY_NONALLOWED))
                            {
                                if (!hasBypass) {
                                    instance.getMessage().send(player, "lead_deny", "&4You cannot use a lead here", null, true);
                                    return true;
                                } else {
                                    instance.getMessage().send(player, "lead_deny_bypass", "&4You cannot use a lead here, but have Domain bypass enabled", null, true);
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

            return ret;
        }
    }
    public static class Mount {
        public static boolean preventNonAllowed(Player player, Location location) {

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
            Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(location);
            DomainBlock pblock = domainBlockManager.getCurrentField(location);
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (!isAllowed) {
                        if (hmFlags.get(Flag.Type.PREVENT_MOUNT_ANIMAL_BY_NONALLOWED) != null)
                        {
                            if (hmFlags.get(Flag.Type.PREVENT_MOUNT_ANIMAL_BY_NONALLOWED))
                            {
                                if (!hasBypass) {
                                    instance.getMessage().send(player, "mount_animal_deny", "&4You cannot mount animals here", null, true);
                                    return true;
                                } else {
                                    instance.getMessage().send(player, "mount_animal_deny_bypass", "&4You cannot mount animals here, but have Domain bypass enabled", null, true);
                                    return false;
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
