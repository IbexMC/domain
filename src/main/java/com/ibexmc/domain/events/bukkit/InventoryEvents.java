package com.ibexmc.domain.events.bukkit;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.helper.MaterialGroups;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.gui.IInventoryGUI;
import com.ibexmc.domain.util.functions.PlayerFunctions;

import java.util.HashMap;
import java.util.Map;

public class InventoryEvents implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof IInventoryGUI) {
            IInventoryGUI clickedGUI = (IInventoryGUI) event.getInventory().getHolder();
            clickedGUI.onClick(event);
        }
        if (event.getInventory().getHolder() instanceof Domain.IGUI) // check if the inventory opened is our storage GUI
        {
            Domain.IGUI clickedGUI = (Domain.IGUI) event.getInventory().getHolder();
            clickedGUI.onGUIClick(event);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if (event.getInventory().getLocation() != null) {
            if (domainBlockManager.inField(event.getInventory().getLocation())) {
                Block block = event.getInventory().getLocation().getBlock();
                Player player = (Player) event.getPlayer();
                boolean hasBypass = PlayerFunctions.hasBypass(player);
                boolean isAllowed = domainBlockManager.isAllowed(player, block.getLocation());
                boolean preventInteract = domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_INTERACT);

                if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_INTERACT_SHULKERBOX_BY_NONALLOWED) || preventInteract) {
                    if (block.getType().name().contains("SHULKER_BOX")) {
                        if (!hasBypass && !isAllowed) {
                            float volume = (float) 0.5;
                            player.getWorld().playSound(block.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, volume, 1);
                            player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, block.getLocation(), 15, -1, 1.5, -1, 5);
                            instance.getMessage().send(player, "shulkerbox_interact_nonallowed", "You cannot access shulker boxes here" , null, true);
                            event.setCancelled(true);
                        } else {
                            if (hasBypass && !isAllowed) {
                                Map<String, String> parameters = new HashMap<>();
                                parameters.put("<%blocktype%>", block.getType().name().toLowerCase().replace("_",  " "));
                                parameters.put("<%location%>", WorldFunctions.simpleReadable(block.getLocation()));
                                instance.getMessage().send(player, "shulkerbox_interact_nonallowed_bypass", "Access to <%blocktype%> at <%location%> allowed due to bypass mode" , parameters, true);
                                DomainFunctions.bypassLog(instance, player, "Accessed " + block.getType().name().toLowerCase().replace("_", " ") + " at " + WorldFunctions.simpleReadable(block.getLocation()));
                            }
                        }
                        return;
                    }
                }



                if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_INTERACT_BARREL_BY_NONALLOWED) || preventInteract) {
                    if (block.getType() == Material.BARREL) {
                        if (!hasBypass && !isAllowed) {
                            float volume = (float) 0.5;
                            player.getWorld().playSound(block.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, volume, 1);
                            player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, block.getLocation(), 15, -1, 1.5, -1, 5);
                            instance.getMessage().send(player, "barrel_interact_nonallowed", "You cannot access barrels here" , null, true);
                            event.setCancelled(true);
                        } else {
                            if (hasBypass && !isAllowed) {
                                Map<String, String> parameters = new HashMap<>();
                                parameters.put("<%blocktype%>", block.getType().name().toLowerCase().replace("_",  " "));
                                parameters.put("<%location%>", WorldFunctions.simpleReadable(block.getLocation()));
                                instance.getMessage().send(player, "barrel_interact_nonallowed_bypass", "Access to <%blocktype%> at <%location%> allowed due to bypass mode" , parameters, true);
                                DomainFunctions.bypassLog(instance, player, "Accessed " + block.getType().name().toLowerCase().replace("_",  " ") + " at " + WorldFunctions.simpleReadable(block.getLocation()));
                            }
                        }
                        return;
                    }
                }
                if (domainBlockManager.hasFlag(block.getLocation(), Flag.Type.PREVENT_INTERACT_CHEST_BY_NONALLOWED) || preventInteract) {
                    for (Material m : MaterialGroups.chests()) {
                        if (m == block.getType()) {
                            if (!hasBypass && !isAllowed) {
                                float volume = (float) 0.5;
                                player.getWorld().playSound(block.getLocation(), Sound.BLOCK_CHEST_LOCKED, volume, 1);
                                player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, block.getLocation(), 15, -1, 1.5, -1, 5);
                                instance.getMessage().send(player, "chest_interact_nonallowed", "You cannot open chests here" , null, true);
                                event.setCancelled(true);
                            } else {
                                if (hasBypass && !isAllowed) {
                                    Map<String, String> parameters = new HashMap<>();
                                    parameters.put("<%blocktype%>", block.getType().name().toLowerCase().replace("_",  " "));
                                    parameters.put("<%location%>", WorldFunctions.simpleReadable(block.getLocation()));
                                    instance.getMessage().send(player, "chest_interact_nonallowed_bypass", "Access to <%blocktype%> at <%location%> allowed due to bypass mode" , parameters, true);
                                    DomainFunctions.bypassLog(instance, player, "Accessed " + block.getType().name().toLowerCase().replace("_",  " ") + " at " + WorldFunctions.simpleReadable(block.getLocation()));
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof IInventoryGUI) {
            IInventoryGUI clickedGUI = (IInventoryGUI) event.getInventory().getHolder();
            clickedGUI.onClose(event);
        }
    }
    @EventHandler
    public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        ItemStack air = new ItemStack(Material.AIR);

        for (HumanEntity entity : event.getViewers()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                int i = 0;
                for (ItemStack itemStack : event.getInventory()) {
                    if (i > 0) {
                        if (itemStack != null) {
                            if (domainBlockManager.isItemIsDomainBlock(itemStack)) {
                                event.getInventory().setResult(air);
                                instance.getMessage().send(player, "craft_domain_block", "&4ERROR &r- You cannot craft with a Domain block", null, true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
