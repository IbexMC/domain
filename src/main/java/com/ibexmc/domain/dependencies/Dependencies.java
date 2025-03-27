package com.ibexmc.domain.dependencies;

import com.ibexmc.internal.functions.ColorFunctions;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.myriad.Myriad;

public class Dependencies {
    
    private Domain instance;

    public Dependencies(Domain instance) {
        this.instance = instance;
    }

    // Economy
    private static Economy econ = null;
    private boolean vaultExists = false;
    public boolean hasVault() {
        return vaultExists;
    }
    public void setVault(boolean vault) {
        vaultExists = true;
    }

    private boolean hasWorldGuard = false;
    private WorldGuardPlugin worldGuard;
    public boolean hookWorldGuard() {
        Plugin plug = instance.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plug != null) {
            worldGuard = ((WorldGuardPlugin) plug);
            hasWorldGuard = true;
            return true;
        }
        return false;
    }
    public WorldGuardPlugin getWorldGuardPlugin() {
        return worldGuard;
    }
    public WorldGuard getWorldGuard() {
        return WorldGuard.getInstance();
    }
    public boolean hasWorldGuard() {
        return hasWorldGuard;
    }

    // Myriad Clans
    private boolean hasMyriadClans = false;
    private Myriad mc;
    public boolean hookMyriadClans() {
        Plugin plug = instance.getServer().getPluginManager().getPlugin("Myriad");

        if (plug != null) {
            mc = ((Myriad) plug);
            hasMyriadClans = true;
            return true;
        } else {
            return false;
        }
    }
    public Myriad getMyriadClans()
    {
        return mc;
    }
    public boolean hasMyriadClans()
    {
        return hasMyriadClans;
    }


    // Dynmap
    private boolean hasDynmap = false;
    private DynmapAPI dynmapAPI = null;
    private MarkerAPI markerAPI = null;
    private MarkerSet markerSet = null;
    public boolean hookDynmap() {
        if (instance.config().isDynmapEnabled()) {
            Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
            if (plug != null) {
                dynmapAPI = ((DynmapAPI) plug);
                hasDynmap = true;
                markerAPI = dynmapAPI.getMarkerAPI();
                markerSet = markerAPI.createMarkerSet("domain.fields", "Domain Fields", dynmapAPI.getMarkerAPI().getMarkerIcons(), false);
                return true;
            }
        }
        return false;
    }
    public DynmapAPI getDynmapAPI() { return dynmapAPI; }
    public boolean hasDynmap() {
        if (instance.config().isDynmapEnabled()) {
            return hasDynmap;
        } else {
            return false;
        }
    }
    public void updateFieldDynmap(DomainBlock domainBlock) {
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (!hasDynmap()) {
            return;
        }
        instance.getDebug().log("Dependencies", "updateFieldDynmap", "Updating Dynmap for " + domainBlock.getFieldName());
        String id = "domain.id." + domainBlock.getPlacedId();
        String name = ChatColor.stripColor(domainBlock.getFieldName());
        String worldName = domainBlock.getBlockLocation().getWorld().getName();
        double[] x = new double[]{domainBlock.getCorner1().getBlockX(), domainBlock.getCorner2().getBlockX()};
        double[] z = new double[]{domainBlock.getCorner1().getBlockZ(), domainBlock.getCorner2().getBlockZ()};
        AreaMarker currentMarkerSet = markerSet.findAreaMarker(id);
        if (currentMarkerSet != null) {
            instance.getDebug().log("Dependencies", "updateFieldDynmap", "Dynmap already exists for " + domainBlock.getFieldName() + " - deleting AreaMarker");
            removeFieldDynmap(domainBlock);
        }
        if (domainBlockManager.hasActiveFlag(domainBlock, Flag.Type.SHOW_ON_DYNMAP)) {
            instance.getDebug().log("Dependencies", "updateFieldDynmap", "SHOW_ON_DYNMAP exists for " + domainBlock.getFieldName() + " - adding AreaMarker");
            AreaMarker areaMarker = markerSet.createAreaMarker(id, name, true, worldName, x, z, true);

            if (domainBlock.getConfig().isDynmapShowMarker()) {
                MarkerIcon markerIcon = markerAPI.getMarkerIcon(domainBlock.getConfig().getDynmapMarkerIcon());
                String htmlLabel = "<div>" + name + "</div>";
                Marker marker = markerSet.createMarker("domain.icon.id." + domainBlock.getPlacedId(), htmlLabel, true,
                        domainBlock.getBlockLocation().getWorld().getName(), domainBlock.getBlockLocation().getBlockX(), domainBlock.getBlockLocation().getBlockY(), domainBlock.getBlockLocation().getBlockZ(), markerIcon, false);
                marker.setLabel(name);
            }

            int lineColor = ColorFunctions.convertHexToInteger(domainBlock.getConfig().getDynmapLineHexColor());
            int fillColor = ColorFunctions.convertHexToInteger(domainBlock.getConfig().getDynmapFillHexColor());
            double opacity = domainBlock.getConfig().getDynmapFillOpacity();
            instance.getDebug().log("Dependencies", "updateFieldDynmap", "Line color = " + lineColor + " fillColor = " + fillColor + " opacity = " + opacity);
            areaMarker.setLineStyle(3, 1.0, lineColor);
            areaMarker.setFillStyle(opacity, fillColor);
            areaMarker.setCornerLocations(x, z);
        } else {
            instance.getDebug().log("Dependencies", "updateFieldDynmap", "SHOW_ON_DYNMAP does not exist for " + domainBlock.getFieldName() + " - NOT adding AreaMarker");
        }

    }
    public void removeFieldDynmap(DomainBlock domainBlock) {
        if (!hasDynmap()) {
            return;
        }
        String id = "domain.id." + domainBlock.getPlacedId();
        AreaMarker currentMarkerSet = markerSet.findAreaMarker(id);
        if (currentMarkerSet != null) {
            currentMarkerSet.deleteMarker();
        }
        Marker currentMarker = markerSet.findMarker("domain.icon.id." + domainBlock.getPlacedId());
        if (currentMarker != null) {
            currentMarker.deleteMarker();
        }
    }

    public boolean setupEconomy() {
        try {
            if (instance.getServer().getPluginManager().getPlugin("Vault") == null) {
                return false;
            }
            RegisteredServiceProvider<Economy> rsp = instance.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            econ = rsp.getProvider();
            return econ != null;
        } catch (Exception ex) {
            instance.getDebug().log("Domain", "setupEconomy", "Unexpected Error - " + ex.getMessage());
            return false;
        }
    }
    public Economy getEconomy() {
        return econ;
    }

}
