package com.ibexmc.domain.domainblock.flags;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class FlagsManager {

    private final Domain instance;

    public FlagsManager(Domain instance) {
        this.instance = instance;
    }

    public boolean allow(DomainBlock domainBlock, Object type, Object to, String flag) {
        String[] parts = flag.split("\\.");
        if (parts.length >= 1) {
            TopLevelFlag topLevelFlag = TopLevelFlag.getEnumFromString(parts[0].toUpperCase());
            switch (topLevelFlag) {
                case ACCESS:
                    if (to instanceof Entity) {
                        Access access = new Access(instance);

                    }
                    /*

                    Exit
Farewell
IdleKick
In
Permission
RunCommand
SnitchEnter
SnitchLeave
Welcome

                     */
                    break;
                case BAZAAR:
                    break;
                case BREAK:
                    if (to instanceof Entity) {
                        Break breakFlag = new Break(instance);
                        if (type instanceof Block) {
                            return breakFlag.allow(domainBlock, (Block) type, (Entity) to, flag);
                        } else if (type instanceof Entity) {
                            return breakFlag.allow(domainBlock, (Entity) type, (Entity) to, flag);
                        }
                    }
                    break;
                case BUILD:
                    break;
                case BURN:
                    break;
                case COMMAND:
                    break;
                case EFFECT:
                    break;
                case EPIDEMIC:
                    break;
                case GROW:
                    break;
                case HURT:
                    break;
                case INTERACT:
                    break;
                case MAGNET:
                    break;
                case SPAWN:
                    break;
                case SPAWNER:
                    break;
                case TELEPORT:
                    break;
                case TRADE:
                    break;
                case TRANSPORT:
                    break;
                case WORLD:
                    break;
                case UNEXPECTED_ERROR:
                    break;
                default:
                    break;
            }
        }
        return true;
    }
}
