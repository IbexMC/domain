package com.ibexmc.domain.domainblock;


import java.util.*;
import java.util.Map.Entry;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.DomainParticles;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Visualize {

    Domain instance;
    DomainBlockManager domainBlockManager;

    public Visualize(Domain instance) {
        this.instance = instance;
        domainBlockManager = new DomainBlockManager(instance);
    }

    public enum VisualizationType {
        CUBE,
        CORNERS,
        PARTICLE_CUBE,
        PARTICLE_RING,
        RING
    }

    /**
     * Clears all visualizations for the specified player
     * @param player Player to clear visualizations for
     */
    public void clearVisualization(Player player) {
        if (instance.getData().hasMaskedBlock(player.getUniqueId())) {
            DomainBlock masked = domainBlockManager.getDomainBlockById(instance.getData().getMaskedBlock(player.getUniqueId()));
            if (masked != null) {
                BlockData blockData = masked.getConfig().getBaseMaterial().createBlockData();
                player.sendBlockChange(masked.getBlockLocation(), blockData);
            }
            instance.getData().removeMaskedBlock(player.getUniqueId());
        }
        if (instance.getData().visualizations.containsKey(player.getUniqueId())) {
            Map<Integer, List<Block>> visualizations = instance.getData().visualizations.get(player.getUniqueId());
            for (Map.Entry<Integer, List<Block>> entry : visualizations.entrySet()) {
                if (entry.getValue() != null) {
                    for (Block block : entry.getValue())
                    {
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                    }
                }
            }
            instance.getData().visualizations.remove(player.getUniqueId());
        }
    }

    /**
     * Clear visualizations for a specific Domain Block for the player
     * @param player Player to clear visualizations for
     * @param placedId Domain Block Identifier to clear visualizations for
     */
    public void clearVisualization(Player player, int placedId) {
        if (instance.getData().hasMaskedBlock(player.getUniqueId())) {
            DomainBlock masked = domainBlockManager.getDomainBlockById(instance.getData().getMaskedBlock(player.getUniqueId()));
            if (masked != null) {
                BlockData blockData = masked.getConfig().getBaseMaterial().createBlockData();
                player.sendBlockChange(masked.getBlockLocation(), blockData);
            }
            instance.getData().removeMaskedBlock(player.getUniqueId());
        }
        if (instance.getData().visualizations.containsKey(player.getUniqueId())) {
            Map<Integer, List<Block>> visualizations = instance.getData().visualizations.get(player.getUniqueId());
            for (Map.Entry<Integer, List<Block>> entry : visualizations.entrySet()) {
                if (entry.getKey() == placedId) {
                    if (entry.getValue() != null) {
                        for (Block block : entry.getValue()) {
                            player.sendBlockChange(block.getLocation(), block.getBlockData());
                        }
                    }
                }
            }
            visualizations.remove(placedId);
            if (visualizations.size() > 0) {
                instance.getData().visualizations.put(player.getUniqueId(), visualizations);
            } else {
                instance.getData().visualizations.remove(player.getUniqueId());
            }
        }
        if (instance.getData().particleDisplay.containsKey(player.getUniqueId())) {
            instance.getData().particleDisplay.remove(player.getUniqueId());
        }
    }

    /**
     * Displays a visualization with default visualization type
     * @param player Player to show visualization for
     * @param domainBlock Domain Block to visualize
     * @param timeOverride Overrides the display time
     * @param hideOtherFields If true, other visualizations are cleared
     * @param resize If true, visualization is part of a resize
     * @param valid If true, field is valid
     * @param corner1 Corner 1
     * @param corner2 Corner 2
     */
    public void displayField(
            Player player,
            DomainBlock domainBlock,
            int timeOverride,
            boolean hideOtherFields,
            boolean resize,
            boolean valid,
            Location corner1,
            Location corner2
    ) {
        VisualizationType visualizationType = domainBlockManager.getVisualizationTypeFromConfig(domainBlock);
        displayField(
                player,
                domainBlock,
                timeOverride,
                hideOtherFields,
                resize,
                valid,
                visualizationType,
                corner1,
                corner2
        );
    }

    /**
     * Displays a visualization with default visualization type
     * @param player Player to show visualization for
     * @param domainBlock Domain Block to visualize
     * @param timeOverride Overrides the display time
     * @param hideOtherFields If true, other visualizations are cleared
     * @param resize If true, visualization is part of a resize
     * @param valid If true, field is valid
     */
    public void displayField(
            Player player,
            DomainBlock domainBlock,
            int timeOverride,
            boolean hideOtherFields,
            boolean resize,
            boolean valid
    ) {
        VisualizationType visualizationType = domainBlockManager.getVisualizationTypeFromConfig(domainBlock);
        displayField(
                player,
                domainBlock,
                timeOverride,
                hideOtherFields,
                resize,
                valid,
                visualizationType,
                domainBlock.getCorner1().clone(),
                domainBlock.getCorner2().clone()
        );
    }
    /**
     * Displays a visualization with a specified type
     * @param player Player to show visualization for
     * @param domainBlock Domain Block to visualize
     * @param visualizationType Visualization Type
     * @param timeOverride Overrides the display time
     * @param hideOtherFields If true, other visualizations are cleared
     * @param resize If true, visualization is part of a resize
     * @param valid If true, field is valid
     * @param corner1 Corner 1
     * @param corner2 Corner 2
     */
    public void displayField(
            Player player,
            DomainBlock domainBlock,
            int timeOverride,
            boolean hideOtherFields,
            boolean resize,
            boolean valid,
            VisualizationType visualizationType,
            Location corner1,
            Location corner2
    ) {
        if (domainBlock == null) {
            return;
        }
        if (hideOtherFields) {
            clearVisualization(player);
        } else {
            clearVisualization(player, domainBlock.getPlacedId());
        }
        int displaySecs = domainBlock.getConfig().getFieldDisplaySecs();
        if (displaySecs < 0) {
            timeOverride = 20;
        }
        if (timeOverride > 0) {
            displaySecs = timeOverride;
        }
        Material m;
        BlockData b;
        List<Block> blocks = new ArrayList<>();


        switch (visualizationType) {
            case PARTICLE_CUBE:
                instance.getData().particleDisplay.put(player.getUniqueId(), domainBlock.getPlacedId());
                Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
                    new BukkitRunnable() {
                        public void run() {
                            if (!instance.getData().particleDisplay.containsKey(player.getUniqueId())) {
                                this.cancel();
                            } else {

                                for (Location location : DomainFunctions.getHollowCube(domainBlock.getCorner1(), domainBlock.getCorner2())) {
                                    if (location.distance(player.getLocation()) < 20) {
                                        int red = 117;
                                        int green = 17;
                                        int blue = 8;
                                        if (valid) {
                                            red = 2;
                                            green = 227;
                                            blue = 6;
                                        }
                                        DomainParticles.displayParticleDustPlayer(player, location, 10, red, green, blue, 2);
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(instance, 0, 3);
                }, 3);
                break;
            case PARTICLE_RING:
                instance.getData().particleDisplay.put(player.getUniqueId(), domainBlock.getPlacedId());
                Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
                    new BukkitRunnable() {
                        public void run() {
                            if (!instance.getData().particleDisplay.containsKey(player.getUniqueId())) {
                                this.cancel();
                            } else {

                                for (Location location : DomainFunctions.getRing(corner1, corner2, player.getLocation().getBlockY())) {
                                    if (location.distance(player.getLocation()) < 20) {
                                        int red = 117;
                                        int green = 17;
                                        int blue = 8;
                                        if (valid) {
                                            red = 2;
                                            green = 227;
                                            blue = 6;
                                        }
                                        DomainParticles.displayParticleDustPlayer(player, location, 10, red, green, blue, 2);
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(instance, 0, 3);
                }, 3);
                break;
            case CUBE:
                m = domainBlock.getConfig().getFieldDisplayMaterial();
                if (m == null) {
                    m = Material.RED_STAINED_GLASS;
                }
                if (resize) {
                    if (valid) {
                        m = Material.GREEN_STAINED_GLASS;
                    } else {
                        m = Material.RED_STAINED_GLASS;
                    }
                }
                b = m.createBlockData();
                List<Location> cube = DomainFunctions.getHollowCube(corner1, corner2);
                for (Location location : cube) {
                    blocks.add(location.getBlock());
                    player.sendBlockChange(location, b);
                }
                break;
            case CORNERS:
                m = Material.SEA_LANTERN;
                Material mok = Material.LIME_CONCRETE;
                if (resize) {
                    if (!valid) {
                        mok = Material.RED_CONCRETE;
                    }
                }
                b = m.createBlockData();
                BlockData bok = mok.createBlockData();
                Map<Integer, Location> map = DomainFunctions.getExtendedCornerMap(corner1, corner2);
                for (Entry<Integer, Location> corners : map.entrySet()) {
                    if (corners.getValue().getBlock() != null) {
                        blocks.add(corners.getValue().getBlock());
                        if (corners.getKey() < 1000 || corners.getKey() >= 100000) {
                            player.sendBlockChange(corners.getValue(), b);
                        } else {
                            player.sendBlockChange(corners.getValue(), bok);
                        }
                    } else {
                        instance.getDebug().log("Visualize", "displayCorners", "Block at location is null - Location: " + WorldFunctions.simpleReadable(corners.getValue()));
                    }
                }
                break;
            case RING:
                m = domainBlock.getConfig().getFieldDisplayMaterial();
                if (m == null) {
                    m = Material.RED_STAINED_GLASS;
                }
                if (resize) {
                    if (valid) {
                        m = Material.GREEN_STAINED_GLASS;
                    } else {
                        m = Material.RED_STAINED_GLASS;
                    }
                }
                b = m.createBlockData();
                List<Location> ring = DomainFunctions.getRing(corner1, corner2, player.getLocation().getBlockY());
                for (Location location : ring) {
                    blocks.add(location.getBlock());
                    player.sendBlockChange(location, b);
                }
                break;
            default:
                break;
        }

        Map<Integer, List<Block>> blockMap = new HashMap<>();
        blockMap.put(domainBlock.getPlacedId(), blocks);
        instance.getData().visualizations.put(player.getUniqueId(), blockMap);
        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
            clearVisualization(player, domainBlock.getPlacedId());
        }, displaySecs * 20);
    }

    /**
     * Masks a Domain block
     * @param placedId Domain Block identifier
     * @param player Player to mask the block for
     * @param maskWith Material to mask with
     */
    public void maskBlock(int placedId, Player player, Material maskWith) {
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(placedId);
        if (domainBlock == null) {
            return;
        }
        // Only one block can be masked at a time
        if (instance.getData().hasMaskedBlock(player.getUniqueId())) {
            DomainBlock masked = domainBlockManager.getDomainBlockById(instance.getData().getMaskedBlock(player.getUniqueId()));
            if (masked != null) {
                BlockData blockData = masked.getConfig().getBaseMaterial().createBlockData();
                player.sendBlockChange(masked.getBlockLocation(), blockData);
            }
            instance.getData().removeMaskedBlock(player.getUniqueId());
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
            BlockData blockData = maskWith.createBlockData();
            instance.getData().putMaskedBlock(player.getUniqueId(), placedId);
            player.sendBlockChange(domainBlock.getBlockLocation(), blockData);
        },4);
    }

}
