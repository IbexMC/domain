package com.ibexmc.domain.events.bukkit;

import com.ibexmc.domain.Domain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.flag.entity.nonliving.Vehicle;
import com.ibexmc.domain.util.functions.PlayerFunctions;

public class VehicleEvents implements Listener {

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("VehicleEvents", "onVehicleDamage()", "Vehicle Damage Event Fired");
        instance.getDebug().log("VehicleEvents", "onVehicleDamage()", "Event: " + evt.getEventName());


        if (evt.getAttacker() instanceof Player) {
            Player player = (Player) evt.getAttacker();
            if (Vehicle.Break.preventNonAllowed(player, evt.getVehicle())) {
                evt.setCancelled(true);
            }
        }

    }
    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("VehicleEvents", "onVehicleDestroy()", "Vehicle Destroy Event Fired");
        instance.getDebug().log("VehicleEvents", "onVehicleDestroy()", "Event: " + evt.getEventName());
    }
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("VehicleEvents", "onVehicleEnter()", "Vehicle entered");
        String errMsg = "";
        switch (evt.getVehicle().getName())
        {
            case "Horse":
                errMsg = instance.getLocale().localeText("vehicle_enter_horse", "You cannot ride a horse here", null);
                break;
            case "Mule":
                errMsg = instance.getLocale().localeText("vehicle_enter_mule", "You cannot ride a mule here", null);
                break;
            case "Pig":
                errMsg = instance.getLocale().localeText("vehicle_enter_pig", "You cannot ride a pig here", null);
                break;
            case "Boat":
                errMsg = instance.getLocale().localeText("vehicle_enter_boat", "You cannot ride in a boat here", null);
                break;
            case "Minecart":
                errMsg = instance.getLocale().localeText("vehicle_enter_minecart", "You cannot ride in a minecart here", null);
                break;
            default:
                errMsg = instance.getLocale().localeText("vehicle_enter_default", "You cannot ride in a vehicle here", null);
                break;
        }
        boolean isPlayer = PlayerFunctions.isEntityPlayer(evt.getEntered());
        boolean isAllowed = domainBlockManager.isAllowed(evt.getEntered());
        boolean hasBypass = false;
        if (isPlayer)
        {
            hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(evt.getEntered()));
        }
        if (domainBlockManager.hasFlag(evt.getVehicle().getLocation(), Flag.Type.PREVENT_VEHICLE_ENTER_ALL))
        {
            if (!hasBypass)
            {
                evt.setCancelled(true);
            }
        }
        if (!evt.isCancelled())
        {
            if (domainBlockManager.hasFlag(evt.getVehicle().getLocation(), Flag.Type.PREVENT_VEHICLE_ENTER_BY_NONALLOWED))
            {
                if (isPlayer)
                {
                    if (!hasBypass)
                    {
                        if (!isAllowed)
                        {
                            evt.setCancelled(true);
                            instance.getMessage().send((Player) evt.getEntered(),"na", errMsg , null, true);

                            return;
                        }
                    }
                }
            }
        }
        if (!evt.isCancelled())
        {
            if (domainBlockManager.hasFlag(evt.getVehicle().getLocation(), Flag.Type.PREVENT_VEHICLE_ENTER_BY_NONPLAYER) && !isPlayer)
            {
                evt.setCancelled(true);
                instance.getMessage().send((Player) evt.getEntered(), "na", errMsg , null, true);
                return;
            }
        }
    }
    @EventHandler
    public void onVehicleExit(VehicleExitEvent evt) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        String errMsg = "";
        switch (evt.getVehicle().getName())
        {
            case "Horse":
                errMsg = "You cannot get off a horse here";
                errMsg = instance.getLocale().localeText("vehicle_exit_horse", "You cannot get off a horse here", null);
                break;
            case "Mule":
                errMsg = "You cannot get off a mule here";
                errMsg = instance.getLocale().localeText("vehicle_exit_mule", "You cannot get off a mule here", null);
                break;
            case "Pig":
                errMsg = "You cannot get off a pig here";
                errMsg = instance.getLocale().localeText("vehicle_exit_pig", "You cannot get off a pig here", null);
                break;
            case "Boat":
                errMsg = "You cannot get out of a boat here";
                errMsg = instance.getLocale().localeText("vehicle_exit_boat", "You cannot get out of a boat here", null);
                break;
            case "Minecart":
                errMsg = "You cannot get out of a minecart here";
                errMsg = instance.getLocale().localeText("vehicle_exit_minecart", "You cannot get out of a minecart here", null);
                break;
            default:
                errMsg = "You cannot get out of a vehicle here";
                errMsg = instance.getLocale().localeText("vehicle_exit_default", "You cannot get out of a vehicle here", null);
                break;
        }
        boolean isPlayer = PlayerFunctions.isEntityPlayer(evt.getExited());
        boolean isAllowed = domainBlockManager.isAllowed(evt.getExited());
        boolean hasBypass = false;
        if (isPlayer)
        {
            hasBypass = PlayerFunctions.hasBypass(PlayerFunctions.playerFromEntity(evt.getExited()));
        }
        if (domainBlockManager.hasFlag(evt.getVehicle().getLocation(), Flag.Type.PREVENT_VEHICLE_EXIT_ALL))
        {
            if (!hasBypass)
            {
                evt.setCancelled(true);
                instance.getMessage().send((Player) evt.getExited(), "na", errMsg , null, true);
                return;
            }
        }
        if (!evt.isCancelled())
        {
            if (domainBlockManager.hasFlag(evt.getVehicle().getLocation(), Flag.Type.PREVENT_VEHICLE_EXIT_BY_NONALLOWED))
            {
                if (isPlayer)
                {
                    if (!hasBypass)
                    {
                        if (!isAllowed)
                        {
                            evt.setCancelled(true);
                            instance.getMessage().send((Player) evt.getExited(),"na", errMsg , null, true);
                            return;
                        }
                    }
                }
            }
        }
        if (!evt.isCancelled())
        {
            if (domainBlockManager.hasFlag(evt.getVehicle().getLocation(), Flag.Type.PREVENT_VEHICLE_EXIT_BY_NONPLAYER) && !isPlayer)
            {
                evt.setCancelled(true);
            }
        }
    }

}
