package com.ibexmc.domain.flag.entity.nonliving;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;

public class Vehicle {
    public static class Boat {

    }
    public static class Minecart {

    }
    public static class Break {
        public static boolean preventNonAllowed(Player player, org.bukkit.entity.Vehicle vehicle) {

            Domain instance = Domain.getInstance();
            DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

            instance.getDebug().log("FieldCommands", "preventBreakVehicle()", "Vehicle Name: " + vehicle.getType().name());
            boolean ret = false;

            boolean isPlayer = PlayerFunctions.isEntityPlayer(player);
            boolean isAllowed = domainBlockManager.isAllowed(player);
            boolean hasBypass = false;
            if (isPlayer)
            {
                hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(player));
            }
            Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(vehicle.getLocation());
            DomainBlock pblock = domainBlockManager.getCurrentField(vehicle.getLocation());
            if (pblock != null) {
                if (pblock.getPlacedId() > 0) {
                    if (!isAllowed) {

                        switch (vehicle.getType()) {
                            case MINECART_CHEST:
                            case MINECART_FURNACE:
                            case MINECART_HOPPER:
                            case MINECART_TNT:
                            case MINECART_MOB_SPAWNER:
                            case MINECART:
                                if (hmFlags.get(Flag.Type.PREVENT_BREAK_MINECART_BY_NONALLOWED) != null)
                                {
                                    if (hmFlags.get(Flag.Type.PREVENT_BREAK_MINECART_BY_NONALLOWED))
                                    {
                                        if (hasBypass) {
                                            instance.getMessage().send(player, "vehicle_break_deny_bypass", "&cYou should not be able to break this vehicle here, but can due to Domain Bypass mode", null, true);
                                            return false;
                                        } else {
                                            instance.getMessage().send(player, "vehicle_break_deny", "&cYou cannot break this vehicle here", null, true);
                                            return true;
                                        }
                                    }
                                }
                                break;
                            case BOAT:
                                if (hmFlags.get(Flag.Type.PREVENT_BREAK_BOAT_BY_NONALLOWED) != null)
                                {
                                    if (hmFlags.get(Flag.Type.PREVENT_BREAK_BOAT_BY_NONALLOWED))
                                    {
                                        if (hasBypass) {
                                            instance.getMessage().send(player, "vehicle_break_deny_bypass", "&cYou should not be able to break this vehicle here, but can due to Domain Bypass mode", null, true);
                                            return false;
                                        } else {
                                            instance.getMessage().send(player, "vehicle_break_deny", "&cYou cannot break this vehicle here", null, true);
                                            return true;
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
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
                    if (hmFlags.get(Flag.Type.PREVENT_VEHICLE_PLACE_BY_NONALLOWED) != null && !isAllowed)
                    {
                        if (hmFlags.get(Flag.Type.PREVENT_VEHICLE_PLACE_BY_NONALLOWED) && !hasBypass)
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
