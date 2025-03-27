package com.ibexmc.domain.util;

import com.ibexmc.domain.util.functions.GameFunctions;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class MaterialFunctions {




    // Material Sets
    public static Set<Material> buttons() {
        Set<Material> returnSet = new HashSet<>();
        returnSet.add(Material.ACACIA_BUTTON);
        returnSet.add(Material.BIRCH_BUTTON);
        returnSet.add(Material.DARK_OAK_BUTTON);
        returnSet.add(Material.JUNGLE_BUTTON);
        returnSet.add(Material.OAK_BUTTON);
        returnSet.add(Material.SPRUCE_BUTTON);
        returnSet.add(Material.STONE_BUTTON);
        if (GameFunctions.isVersionSafe(1.16)) {
            returnSet.add(Material.WARPED_BUTTON);
            returnSet.add(Material.CRIMSON_BUTTON);
            returnSet.add(Material.POLISHED_BLACKSTONE_BUTTON);
        }
        if (GameFunctions.isVersionSafe(1.19)) {
            returnSet.add(Material.MANGROVE_BUTTON);
        }
        return returnSet;
    }
    public static Set<Material> doors() {
        Set<Material> returnSet = new HashSet<>();
        returnSet.add(Material.ACACIA_DOOR);
        returnSet.add(Material.BIRCH_DOOR);
        returnSet.add(Material.DARK_OAK_DOOR);
        returnSet.add(Material.JUNGLE_DOOR);
        returnSet.add(Material.OAK_DOOR);
        returnSet.add(Material.SPRUCE_DOOR);
        returnSet.add(Material.IRON_DOOR);
        if (GameFunctions.isVersionSafe(1.16)) {
            returnSet.add(Material.WARPED_DOOR);
            returnSet.add(Material.CRIMSON_DOOR);
        }
        if (GameFunctions.isVersionSafe(1.19)) {
            returnSet.add(Material.MANGROVE_DOOR);
        }
        return returnSet;
    }
    public static Set<Material> gates() {
        Set<Material> returnSet = new HashSet<>();
        returnSet.add(Material.ACACIA_FENCE_GATE);
        returnSet.add(Material.BIRCH_FENCE_GATE);
        returnSet.add(Material.JUNGLE_FENCE_GATE);
        returnSet.add(Material.OAK_FENCE_GATE);
        returnSet.add(Material.SPRUCE_FENCE_GATE);
        returnSet.add(Material.DARK_OAK_FENCE_GATE);
        if (GameFunctions.isVersionSafe(1.16)) {
            returnSet.add(Material.CRIMSON_FENCE_GATE);
            returnSet.add(Material.WARPED_FENCE_GATE);
        }
        if (GameFunctions.isVersionSafe(1.19)) {
            returnSet.add(Material.MANGROVE_FENCE_GATE);
        }
        return returnSet;
    }
    public static Set<Material> pressurePlates() {
        Set<Material> returnSet = new HashSet<>();
        returnSet.add(Material.ACACIA_PRESSURE_PLATE);
        returnSet.add(Material.BIRCH_PRESSURE_PLATE);
        returnSet.add(Material.DARK_OAK_PRESSURE_PLATE);
        returnSet.add(Material.JUNGLE_PRESSURE_PLATE);
        returnSet.add(Material.OAK_PRESSURE_PLATE);
        returnSet.add(Material.SPRUCE_PRESSURE_PLATE);
        returnSet.add(Material.STONE_PRESSURE_PLATE);
        returnSet.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        returnSet.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        if (GameFunctions.is116()) {
            returnSet.add(Material.CRIMSON_PRESSURE_PLATE);
            returnSet.add(Material.WARPED_PRESSURE_PLATE);
            returnSet.add(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        }
        if (GameFunctions.isVersionSafe(1.19)) {
            returnSet.add(Material.MANGROVE_PRESSURE_PLATE);
        }
        return returnSet;
    }
    public static Set<Material> trapdoors() {
        Set<Material> returnSet = new HashSet<>();
        returnSet.add(Material.ACACIA_TRAPDOOR);
        returnSet.add(Material.BIRCH_TRAPDOOR);
        returnSet.add(Material.DARK_OAK_TRAPDOOR);
        returnSet.add(Material.JUNGLE_TRAPDOOR);
        returnSet.add(Material.OAK_TRAPDOOR);
        returnSet.add(Material.SPRUCE_TRAPDOOR);
        returnSet.add(Material.IRON_TRAPDOOR);
        if (GameFunctions.isVersionSafe(1.16)) {
            returnSet.add(Material.WARPED_TRAPDOOR);
            returnSet.add(Material.CRIMSON_TRAPDOOR);
        }
        if (GameFunctions.isVersionSafe(1.19)) {
            returnSet.add(Material.MANGROVE_TRAPDOOR);
        }
        return returnSet;
    }

    public static Set<Material> waterBuckets() {
        Set<Material> buckets = new HashSet<>();
        buckets.add(Material.WATER_BUCKET);
        if (GameFunctions.isVersionSafe(1.14)) {
            buckets.add(Material.COD_BUCKET);
            buckets.add(Material.SALMON_BUCKET);
            buckets.add(Material.TROPICAL_FISH_BUCKET);
            buckets.add(Material.PUFFERFISH_BUCKET);
        }
        if (GameFunctions.isVersionSafe(1.17)) {
            buckets.add(Material.AXOLOTL_BUCKET);
            buckets.add(Material.POWDER_SNOW_BUCKET);
        }
        if (GameFunctions.isVersionSafe(1.19)) {
            buckets.add(Material.TADPOLE_BUCKET);
        }
        return buckets;
    }
    // Safe Checks
    public static boolean isSafeMatch(Set<Material> materials, String materialName) {
        // This is a 'safe' material lookup which will work on name
        // rather than the material itself for an exact (case-insensitive) match
        for (Material material : materials) {
            if (materialName.equalsIgnoreCase(material.name())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isSafeContains(Set<Material> materials, String materialName) {
        // This is a 'safe' material lookup which will work on name
        // rather than the material itself if the material name is
        // contained in the material list (case-insensitive)
        for (Material material : materials) {
            if (material.name().toLowerCase().contains(materialName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    // Material Checks
    public static boolean isButton(Material material) {
        return buttons().contains(material);
    }
    public static boolean isDoor(Material material) {
        return doors().contains(material);
    }
    public static boolean isGate(Material material) {
        return gates().contains(material);
    }
    public static boolean isPressurePlate(Material material) { return pressurePlates().contains(material); }
    public static boolean isTrapdoor(Material material) {
        return trapdoors().contains(material);
    }
    // Material Set Groupings
    public static boolean isOpenable(Material material) {
        return isDoor(material) || isGate(material) || isTrapdoor(material);
    }
    public static boolean isRedstoneReactive(Material material) {
        return isButton(material) || isPressurePlate(material);
    }
}
