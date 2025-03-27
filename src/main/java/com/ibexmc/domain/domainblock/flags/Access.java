package com.ibexmc.domain.domainblock.flags;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import org.bukkit.entity.*;

public class Access {
    private final Domain instance;
    private final DomainBlockManager domainBlockManager;

    public enum AccessType {
        EXIT("EXIT"),
        FAREWELL("FAREWELL"),
        IDLE_KICK("IDLEKICK"),
        IN("IN"),
        PERMISSION("PERMISSION"),
        RUN_COMMAND("RUNCOMMAND"),
        SNITCH_ENTER("SNITCHENTER"),
        SNITCH_LEAVE("SNITCHLEAVE"),
        WELCOME("WELCOME");

        private final String accessType;

        AccessType(String accessType) {
            this.accessType = accessType;
        }

        public String getAccessType() {
            return this.accessType;
        }
    }
    public Access(Domain instance) {
        this.instance = instance;
        domainBlockManager = new DomainBlockManager(instance);
    }

    public void allow(DomainBlock domainBlock, Entity applyToEntity, String flag) {
        FlagParts flagParts = new FlagParts(flag);
        if (flagParts.isValid()) {

        }
    }

    /**
     * Checks if the apply to entity and the apply to section of the flag match
     * If so, this flag can be used for this apply to entity
     * @param domainBlock Domain Block the flag is being applied to (needed to check for allowed/owner)
     * @param applyToName Apply to name from the flag
     * @param appliedEntity Applied entity
     * @return If true, this flag can be applied to this entity
     */
    private boolean applyTo(DomainBlock domainBlock, String applyToName, Entity appliedEntity) {
        switch (applyToName.toUpperCase()) {
            case "*":
                return true;
            case "PLAYER":
                return (appliedEntity instanceof Player);
            case "PVPPLAYER":
                if (appliedEntity instanceof Player) {
                    Player pvpPlayer = ((Player) appliedEntity);
                    return instance.getData().containsPVPList(pvpPlayer);
                } else {
                    return false;
                }
            case "NONPLAYER":
                return !(appliedEntity instanceof Player);
            case "HOSTILE":
                return (appliedEntity instanceof Enemy);
            case "ANIMAL":
                return (appliedEntity instanceof Animals);
            case "MOB":
                return (appliedEntity instanceof Mob);
            case "LIVING":
                return (appliedEntity instanceof LivingEntity);
            case "VILLAGER":
                return (appliedEntity instanceof Villager);
            case "ALLOWED":
                if (appliedEntity instanceof Player) {
                    return (domainBlockManager.isAllowedToField((Player) appliedEntity, domainBlock));
                } else {
                    return false;
                }
            case "NONALLOWED":
                if (appliedEntity instanceof Player) {
                    return !(domainBlockManager.isAllowedToField((Player) appliedEntity, domainBlock));
                } else {
                    return false;
                }
            case "OWNER":
                if (appliedEntity instanceof Player) {
                    return (domainBlockManager.isOwner((Player) appliedEntity, domainBlock));
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

}
