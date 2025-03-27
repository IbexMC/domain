package com.ibexmc.domain.flag.block;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;

public class Flora {
    public static class FarmLand {
        public static class Damage {
            public static boolean preventNonAllowed(Player player, Block block) {

                Domain instance = Domain.getInstance();
                DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
                boolean ret = false;
                if (block == null)
                {
                    return false;
                }
                if (block.getType() == Material.FARMLAND) {
                    Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(player.getLocation());
                    if (hmFlags.get(Flag.Type.PREVENT_CROP_DAMAGE_BY_NONALLOWED) != null)
                    {
                        if (hmFlags.get(Flag.Type.PREVENT_CROP_DAMAGE_BY_NONALLOWED))
                        {
                            boolean isAllowed = domainBlockManager.isAllowed(player);
                            boolean hasBypass = PlayerFunctions.hasBypass(player);

                            if (isAllowed) {
                                instance.getDebug().log("FieldCommands", "stopCropDamage", "Crop damage allowed - " + player.getName() + " is allowed access to field");
                            } else {
                                if (hasBypass) {
                                    instance.getDebug().log("FieldCommands", "stopCropDamage", "Crop damage allowed " + player.getName() + " has bypass access to field");
                                }
                            }
                            if (!isAllowed && !hasBypass)
                            {
                                instance.getDebug().log("FieldCommands", "stopCropDamage", "Cancelling crop damage");
                                ret = true;
                            }
                        }
                    }
                }
                return ret;
            }
        }
        public static class Crop {
            public static class Damage {
                // Prevent damage to any crop
            }
            public static class Wheat {
                public static class Grow {
                    // prevent wheat growing
                }
            }
            public static class Potato {
                public static class Grow {
                    // prevent potatoes growing
                }
            }
        }
    }

}
