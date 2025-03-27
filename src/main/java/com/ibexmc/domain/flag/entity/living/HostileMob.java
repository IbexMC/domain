package com.ibexmc.domain.flag.entity.living;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.entity.Player;

import java.util.Map;

public class HostileMob {
    public static class ZombieVillager {
        public static class Heal {
            public static boolean preventNonAllowed(Player player, org.bukkit.entity.ZombieVillager zombieVillager) {

                Domain instance = Domain.getInstance();
                DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

                instance.getDebug().log(
                        "HealZombieVillager",
                        "preventNonAllowed()",
                        "Started"
                );

                if (player == null) {
                    instance.getDebug().log(
                            "HealZombieVillager",
                            "preventNonAllowed()",
                            "Player is null"
                    );
                    return false;
                }
                if (zombieVillager == null) {
                    instance.getDebug().log(
                            "HealZombieVillager",
                            "preventNonAllowed()",
                            "Zombie Villager is null"
                    );
                    return false;
                }

                instance.getDebug().log(
                        "HealZombieVillager",
                        "preventNonAllowed()",
                        "Zombie Villager - Is Converting: " + zombieVillager.isConverting()
                );


                boolean isPlayer = PlayerFunctions.isEntityPlayer(player);
                boolean isAllowed = domainBlockManager.isAllowed(player);
                boolean hasBypass = false;
                if (isPlayer)
                {
                    hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
                }
                Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(zombieVillager.getLocation());
                DomainBlock placedBlock = domainBlockManager.getCurrentField(zombieVillager.getLocation());
                if (placedBlock != null) {
                    if (placedBlock.getPlacedId() > 0) {
                        if (!isAllowed) {
                            if (hmFlags.get(Flag.Type.PREVENT_HEAL_ZOMBIE_VILLAGER_BY_NONALLOWED) != null)
                            {
                                if (hmFlags.get(Flag.Type.PREVENT_HEAL_ZOMBIE_VILLAGER_BY_NONALLOWED))
                                {
                                    if (hasBypass) {
                                        instance.getMessage().send(player,
                                                "heal_zombie_villager_deny_bypass",
                                                "&cYou should not be able to heal zombie villagers here, but can due to Domain Bypass mode",
                                                null, true
                                        );
                                        return false;
                                    } else {
                                        instance.getMessage().send(player,
                                                "heal_zombie_villager_deny",
                                                "&cYou cannot heal zombie villagers here",
                                                null, true
                                        );
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                return false;
            }
        }
    }
}
