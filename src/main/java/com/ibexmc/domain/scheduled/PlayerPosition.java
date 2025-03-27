package com.ibexmc.domain.scheduled;

import java.util.Map;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;


public class PlayerPosition {

    private final Domain instance;
    private DomainBlockManager domainBlockManager;

    public PlayerPosition(Domain instance) {
        this.instance = instance;
        this.domainBlockManager = new DomainBlockManager(instance);
    }

    public void Run() {
        if (Bukkit.getOnlinePlayers() != null)  {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (domainBlockManager.inField(p.getLocation())) {
                    Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(p.getLocation());
                    if (hmFlags != null)
                    {
                        AttributeInstance a = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        double max = a.getValue();
                        double cur = p.getHealth();
                        if (hmFlags.containsKey(Flag.Type.HEAL_ALL_PLAYERS))
                        {
                            if (hmFlags.get(Flag.Type.HEAL_ALL_PLAYERS))
                            {
                                if (cur < max)
                                {
                                    if (max - cur >= 2.0)
                                    {
                                        p.setHealth(cur + 2.0);
                                        //instance.getDebug().log("TimesPlayerPos", "HEAL_ALL_PLAYERS", "Healing player.  New health:" + Double.toString(p.getHealth()) + " of " + Double.toString(max));
                                    } else {
                                        p.setHealth(max);
                                        //instance.getDebug().log("TimesPlayerPos", "HEAL_ALL_PLAYERS", "Healing player.  New health:" + Double.toString(p.getHealth()) + " of " + Double.toString(max));
                                    }

                                }
                            } else {
                                // No need to run HEAL_ALLOWED_PLAYER if HEAL_ALL_PLAYERS is active
                                if (hmFlags.containsKey(Flag.Type.HEAL_ALLOWED_PLAYER))
                                {
                                    if (hmFlags.get(Flag.Type.HEAL_ALLOWED_PLAYER))
                                    {
                                        if (domainBlockManager.isAllowed(p))
                                        {
                                            if (cur < max)
                                            {
                                                if (max - cur >= 2.0)
                                                {
                                                    p.setHealth(cur + 2.0);
                                                    //instance.getDebug().log("TimesPlayerPos", "HEAL_ALLOWED_PLAYER", "Healing player.  New health:" + Double.toString(p.getHealth()) + " of " + Double.toString(max));
                                                } else {
                                                    p.setHealth(max);
                                                    //instance.getDebug().log("TimesPlayerPos", "HEAL_ALLOWED_PLAYER", "Healing player.  New health:" + Double.toString(p.getHealth()) + " of " + Double.toString(max));
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (hmFlags.get(Flag.Type.HARM_NONALLOWED_PLAYER) != null)
                        {
                            if (hmFlags.get(Flag.Type.HARM_NONALLOWED_PLAYER))
                            {
                                if (!domainBlockManager.isAllowed(p))
                                {
                                    if (!PlayerFunctions.hasBypass(p))
                                    {
                                        if (cur >= 2.1)
                                        {
                                            p.getWorld().playEffect(p.getLocation(), Effect.CHORUS_FLOWER_DEATH, 10);
                                            p.setHealth(cur - 2.0);
                                            //instance.getDebug().log("TimesPlayerPos", "HARM_NONALLOWED_PLAYER", "Harming player.  New health:" + Double.toString(p.getHealth()) + " of " + Double.toString(max));
                                        }
                                    }
                                }
                            }
                        }
                        if (hmFlags.get(Flag.Type.REPAIR_MAIN_HAND_ITEM) != null)
                        {
                            if (hmFlags.get(Flag.Type.REPAIR_MAIN_HAND_ITEM))
                            {
                                ItemStack istack = p.getInventory().getItemInMainHand();
                                ItemMeta meta = istack.getItemMeta(); // Get the item meta
                                if (meta != null) // Check if it's null again
                                {
                                    if (meta instanceof Damageable)
                                    {
                                        Damageable d = (Damageable) meta; // Create the damageable (make sure its org.bukkit.inventory.meta.Damageable and not Entity.Damageable)
                                        if (d.hasDamage())
                                        {
                                            //instance.getDebug().log("TimesPlayerPos", "REPAIR_MAIN_HAND_ITEM", "Repairing item in main hand");
                                            if (d.getDamage() > 0)
                                            {
                                                if (d.getDamage() < 10) {
                                                    d.setDamage(0);
                                                } else {
                                                    d.setDamage(d.getDamage() - 10); // Set the damage
                                                }
                                                istack.setItemMeta(meta); // Set the meta back to the itemstack
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
