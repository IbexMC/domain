package com.ibexmc.domain.domainblock.flags;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.util.MaterialFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

import java.util.HashSet;
import java.util.Set;

public class Break {

    private final Domain instance;
    private final DomainBlockManager domainBlockManager;

    public Break(Domain instance) {
        this.instance = instance;
        domainBlockManager = new DomainBlockManager(instance);
    }

    /**
     * Breaks our flag apart and checks if it is valid.  If so, checks if the applyToEntity
     * is valid to the flag, and then checks if the block is valid to the flag, if so, returns
     * true if the flag ends in allow, and false otherwise (deny).  Returns true if no match
     * is found, or if the flag is malformed.  (Always allow the action unless it is specifically
     * denied).
     * @param domainBlock Domain Block the flag is applied to
     * @param block Block being broken
     * @param applyToEntity Entity breaking the block
     * @param flag Flag to apply
     * @return If true, action is allowed, if false, action should be stopped
     */
    public boolean allow(DomainBlock domainBlock, Block block, Entity applyToEntity, String flag) {
        FlagParts flagParts = new FlagParts(flag);
        if (flagParts.isValid()) {
            if (applyTo(domainBlock, flagParts.applyTo, applyToEntity)) {
                if (applyToBlock(flagParts.breakType, block)) {
                    return flagParts.status.equalsIgnoreCase("allow");
                }
            }
        }
        return true;
    }

    /**
     * Breaks our flag apart and checks if it is valid.  If so, checks if the applyToEntity
     * is valid to the flag, and then checks if the broken entity is valid to the flag, if so, returns
     * true if the flag ends in allow, and false otherwise (deny).  Returns true if no match
     * is found, or if the flag is malformed.  (Always allow the action unless it is specifically
     * denied).
     * @param domainBlock Domain Block the flag is applied to
     * @param brokenEntity Entity being broken
     * @param applyToEntity Entity breaking the block
     * @param flag Flag to apply
     * @return If true, action is allowed, if false, action should be stopped
     */
    public boolean allow(DomainBlock domainBlock, Entity brokenEntity, Entity applyToEntity, String flag) {
        FlagParts flagParts = new FlagParts(flag);
        if (flagParts.isValid()) {
            if (applyTo(domainBlock, flagParts.applyTo, applyToEntity)) {
                if (applyToEntity(flagParts.breakType, brokenEntity)) {
                    return flagParts.status.equalsIgnoreCase("allow");
                }
            }
        }
        return true;
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

    /**
     * Checks if the block being broken can be applied to the flag
     * @param materialName Material name (or group) from the flag
     * @param block Block being broken
     * @return If true, this flag can be applied to this entity
     */
    private boolean applyToBlock(String materialName, Block block) {
        Material namedMaterial = StringFunctions.stringToMaterial(materialName.toUpperCase());
        if (namedMaterial != null) {
            return (block.getType().isBlock() && block.getType().name().equalsIgnoreCase(materialName));
        } else {
            switch (materialName.toUpperCase()) {
                case "*":
                    return block.getType().isBlock();
                case "BEDS":
                    return instance.getData().getMaterialGroup("beds").contains(block.getType());
                case "BUTTONS":
                    return instance.getData().getMaterialGroup("buttons").contains(block.getType());
                case "DOORS":
                    return instance.getData().getMaterialGroup("doors").contains(block.getType());
                case "ORES":
                    return instance.getData().getMaterialGroup("ores").contains(block.getType());
                case "TRAPDOORS":
                    return instance.getData().getMaterialGroup("trapdoors").contains(block.getType());
                case "SIGNS":
                    return instance.getData().getMaterialGroup("signs").contains(block.getType());
            }
        }
        return false;
    }

    /**
     * Checks if the entity being broken can be applied to the flag
     * @param entityName Entity name (or group) from the flag
     * @param entity Entity being broken
     * @return If true, this flag can be applied to this entity
     */
    private boolean applyToEntity(String entityName, Entity entity) {
        EntityType namedEntity = EntityType.valueOf(entityName);
        if (namedEntity != null) {
            return entityName.equalsIgnoreCase(entity.getType().name());
        } else {
            switch (entityName.toUpperCase()) {
                case "*":
                    return !(entity instanceof LivingEntity); // Break can't apply to living
                case "BOATS":
                    return ((entity.getType().equals(EntityType.BOAT)) ||
                            (entity.getType().equals(EntityType.CHEST_BOAT)));
                case "MINECARTS":
                    return (
                            (entity.getType().equals(EntityType.MINECART)) ||
                            (entity.getType().equals(EntityType.MINECART_CHEST)) ||
                            (entity.getType().equals(EntityType.MINECART_COMMAND)) ||
                            (entity.getType().equals(EntityType.MINECART_FURNACE)) ||
                            (entity.getType().equals(EntityType.MINECART_HOPPER)) ||
                            (entity.getType().equals(EntityType.MINECART_TNT)) ||
                            (entity.getType().equals(EntityType.MINECART_MOB_SPAWNER))
                            );
                case "VEHICLE":
                    return (
                            (entity.getType().equals(EntityType.BOAT)) ||
                            (entity.getType().equals(EntityType.CHEST_BOAT)) ||
                            (entity.getType().equals(EntityType.MINECART)) ||
                            (entity.getType().equals(EntityType.MINECART_CHEST)) ||
                            (entity.getType().equals(EntityType.MINECART_COMMAND)) ||
                            (entity.getType().equals(EntityType.MINECART_FURNACE)) ||
                            (entity.getType().equals(EntityType.MINECART_HOPPER)) ||
                            (entity.getType().equals(EntityType.MINECART_TNT)) ||
                            (entity.getType().equals(EntityType.MINECART_MOB_SPAWNER))
                    );
            }
        }
        return false;
    }

    private static class FlagParts {
        private String breakType;
        private String applyTo;
        private String status = "";
        private boolean valid = false;
        public FlagParts(String flag) {
            String[] parts = flag.split("\\.");
            if (parts.length >= 4) {
                this.breakType = parts[1].toLowerCase();
                this.applyTo = parts[2].toLowerCase();
                if (parts[3].equalsIgnoreCase("allow") || parts[3].equalsIgnoreCase("deny")) {
                    this.status = parts[3].toLowerCase();
                } else {
                    this.breakType = "";
                    this.applyTo = "";
                    this.status = "";
                }
            } else {
                this.breakType = "";
                this.applyTo = "";
                this.status = "";
            }
            if (!StringFunctions.isNullOrEmpty(breakType) && !StringFunctions.isNullOrEmpty(applyTo) && !StringFunctions.isNullOrEmpty(status)) {
                this.valid = true;
            }
        }

        public String getBreakType() {
            return breakType;
        }
        public String getApplyTo() {
            return applyTo;
        }
        public String getStatus() {
            return status;
        }
        public boolean isValid() {
            return valid;
        }
    }
}
