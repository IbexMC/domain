package com.ibexmc.domain.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.ibexmc.domain.util.functions.GameFunctions;


public class MaterialGroups {


    public static boolean isCauldron(String materialName) {
        boolean returnVal = false;
        switch (materialName.toUpperCase()) {
            case "CAULDRON":
                returnVal = true;
                break;
            case "LAVA_CAULDRON":
            case "POWDER_SNOW_CAULDRON":
            case "WATER_CAULDRON":
                if (GameFunctions.isVersionSafe(1.17)) {
                    returnVal = true;
                }
                break;
        }
        return returnVal;
    }

    public static boolean isSign(String materialName) {
        boolean returnVal = false;
        switch (materialName.toUpperCase()) {
            case "SIGN":
            case "WALL_SIGN":
            case "LEGACY_SIGN":
            case "LEGACY_WALL_SIGN":
            case "ACACIA_SIGN":
            case "ACACIA_HANGING_SIGN":
            case "ACACIA_WALL_SIGN":
            case "ACACIA_WALL_HANGING_SIGN":
            case "BAMBOO_SIGN":
            case "BAMBOO_HANGING_SIGN":
            case "BAMBOO_WALL_SIGN":
            case "BAMBOO_WALL_HANGING_SIGN":
            case "BIRCH_SIGN":
            case "BIRCH_HANGING_SIGN":
            case "BIRCH_WALL_SIGN":
            case "BIRCH_WALL_HANGING_SIGN":
            case "CHERRY_SIGN":
            case "CHERRY_HANGING_SIGN":
            case "CHERRY_WALL_SIGN":
            case "CHERRY_WALL_HANGING_SIGN":
            case "CRIMSON_SIGN":
            case "CRIMSON_HANGING_SIGN":
            case "CRIMSON_WALL_SIGN":
            case "CRIMSON_WALL_HANGING_SIGN":
            case "DARK_OAK_SIGN":
            case "DARK_OAK_HANGING_SIGN":
            case "DARK_OAK_WALL_SIGN":
            case "DARK_OAK_WALL_HANGING_SIGN":
            case "JUNGLE_SIGN":
            case "JUNGLE_HANGING_SIGN":
            case "JUNGLE_WALL_SIGN":
            case "JUNGLE_WALL_HANGING_SIGN":
            case "MANGROVE_SIGN":
            case "MANGROVE_HANGING_SIGN":
            case "MANGROVE_WALL_SIGN":
            case "MANGROVE_WALL_HANGING_SIGN":
            case "OAK_SIGN":
            case "OAK_HANGING_SIGN":
            case "OAK_WALL_SIGN":
            case "OAK_WALL_HANGING_SIGN":
            case "SPRUCE_SIGN":
            case "SPRUCE_HANGING_SIGN":
            case "SPRUCE_WALL_SIGN":
            case "SPRUCE_WALL_HANGING_SIGN":
            case "WARPED_SIGN":
            case "WARPED_HANGING_SIGN":
            case "WARPED_WALL_SIGN":
            case "WARPED_WALL_HANGING_SIGN":
                returnVal = true;
                break;
        }
        return returnVal;
    }
    public static List<Material> redstoneInteractions() {
        List<Material> m = new ArrayList<Material>();
        Material.ENDER_PEARL.name().contains("_DOOR");
        m.addAll(pressurePlates());
        m.addAll(buttons());
        return m;
    }
    public static List<Material> interactions() {
        List<Material> m = new ArrayList<Material>();
        m.addAll(beds());
        m.addAll(doors());
        m.addAll(buttons());
        m.addAll(trapdoors());
        m.addAll(fenceGates());
        m.addAll(pressurePlates());
        m.addAll(shulkerBoxes());
        m.addAll(chests());
        m.addAll(enderChests());
        m.addAll(tnt());
        m.addAll(anvils());
        m.addAll(flowerPots());
        m.addAll(otherInteractions());
        return m;
    }


    /* Test code */
    // =========================================================================
    private static final ImmutableSet<Material> BEDS = Sets.immutableEnumSet(
            Material.BLACK_BED,
            Material.BLUE_BED,
            Material.BROWN_BED,
            Material.CYAN_BED,
            Material.GRAY_BED,
            Material.GREEN_BED,
            Material.LIGHT_BLUE_BED,
            Material.LIGHT_GRAY_BED,
            Material.LIME_BED,
            Material.MAGENTA_BED,
            Material.ORANGE_BED,
            Material.PINK_BED,
            Material.PURPLE_BED,
            Material.RED_BED,
            Material.WHITE_BED,
            Material.YELLOW_BED
    );

    public static boolean isBed(final Material material) {
        return BEDS.contains(material);
    }
    // =========================================================================







    public static List<Material> beds()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.BLACK_BED);
        m.add(Material.BLUE_BED);
        m.add(Material.BROWN_BED);
        m.add(Material.CYAN_BED);
        m.add(Material.GRAY_BED);
        m.add(Material.GREEN_BED);
        m.add(Material.LIGHT_BLUE_BED);
        m.add(Material.LIGHT_GRAY_BED);
        m.add(Material.LIME_BED);
        m.add(Material.MAGENTA_BED);
        m.add(Material.ORANGE_BED);
        m.add(Material.PINK_BED);
        m.add(Material.PURPLE_BED);
        m.add(Material.RED_BED);
        m.add(Material.WHITE_BED);
        m.add(Material.YELLOW_BED);
        return m;
    }
    public static List<Material> doors()
    {
        List<Material> m = new ArrayList<>();
        m.add(Material.ACACIA_DOOR);
        m.add(Material.BIRCH_DOOR);
        m.add(Material.DARK_OAK_DOOR);
        m.add(Material.JUNGLE_DOOR);
        m.add(Material.OAK_DOOR);
        m.add(Material.SPRUCE_DOOR);
        m.add(Material.IRON_DOOR);
        if (GameFunctions.isVersionSafe(1.16)) {
            m.add(Material.WARPED_DOOR);
        }
        if (GameFunctions.isVersionSafe(1.16)) {
            m.add(Material.CRIMSON_DOOR);
            m.add(Material.WARPED_DOOR);
        }
        if (GameFunctions.isVersionSafe(1.20)) {
            m.add(Material.CHERRY_DOOR);
            m.add(Material.MANGROVE_DOOR);
            m.add(Material.BAMBOO_DOOR);
        }
        return m;
    }
    public static List<Material> buttons()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.ACACIA_BUTTON);
        m.add(Material.BIRCH_BUTTON);
        m.add(Material.DARK_OAK_BUTTON);
        m.add(Material.JUNGLE_BUTTON);
        m.add(Material.OAK_BUTTON);
        m.add(Material.SPRUCE_BUTTON);
        m.add(Material.STONE_BUTTON);
        if (GameFunctions.isVersionSafe(1.16)) {
            m.add(Material.WARPED_BUTTON);
        }
        if (GameFunctions.isVersionSafe(1.16)) {
            m.add(Material.CRIMSON_BUTTON);
            m.add(Material.WARPED_BUTTON);
        }
        if (GameFunctions.isVersionSafe(1.20)) {
            m.add(Material.CHERRY_BUTTON);
            m.add(Material.MANGROVE_BUTTON);
            m.add(Material.BAMBOO_BUTTON);
        }
        return m;
    }
    public static List<Material> trapdoors()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.ACACIA_TRAPDOOR);
        m.add(Material.BIRCH_TRAPDOOR);
        m.add(Material.DARK_OAK_TRAPDOOR);
        m.add(Material.JUNGLE_TRAPDOOR);
        m.add(Material.OAK_TRAPDOOR);
        m.add(Material.SPRUCE_TRAPDOOR);
        m.add(Material.IRON_TRAPDOOR);
        if (GameFunctions.isVersionSafe(1.16)) {
            m.add(Material.WARPED_TRAPDOOR);
            m.add(Material.CRIMSON_TRAPDOOR);
        }
        if (GameFunctions.isVersionSafe(1.20)) {
            m.add(Material.CHERRY_TRAPDOOR);
            m.add(Material.MANGROVE_TRAPDOOR);
            m.add(Material.BAMBOO_TRAPDOOR);
        }
        return m;
    }
    public static List<Material> fenceGates()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.ACACIA_FENCE_GATE);
        m.add(Material.BIRCH_FENCE_GATE);
        m.add(Material.DARK_OAK_FENCE_GATE);
        m.add(Material.JUNGLE_FENCE_GATE);
        m.add(Material.OAK_FENCE_GATE);
        m.add(Material.SPRUCE_FENCE_GATE);
        if (GameFunctions.isVersionSafe(1.16)) {
            m.add(Material.WARPED_FENCE_GATE);
            m.add(Material.CRIMSON_FENCE_GATE);
        }
        if (GameFunctions.isVersionSafe(1.20)) {
            m.add(Material.CHERRY_FENCE_GATE);
            m.add(Material.MANGROVE_FENCE_GATE);
            m.add(Material.BAMBOO_FENCE_GATE);
        }
        return m;
    }
    public static List<Material> pressurePlates()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.ACACIA_PRESSURE_PLATE);
        m.add(Material.BIRCH_PRESSURE_PLATE);
        m.add(Material.DARK_OAK_PRESSURE_PLATE);
        m.add(Material.JUNGLE_PRESSURE_PLATE);
        m.add(Material.OAK_PRESSURE_PLATE);
        m.add(Material.SPRUCE_PRESSURE_PLATE);
        m.add(Material.STONE_PRESSURE_PLATE);
        m.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        m.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        if (GameFunctions.isVersionSafe(1.16)) {
            m.add(Material.WARPED_PRESSURE_PLATE);
            m.add(Material.CRIMSON_PRESSURE_PLATE);
        }
        if (GameFunctions.isVersionSafe(1.20)) {
            m.add(Material.CHERRY_PRESSURE_PLATE);
            m.add(Material.MANGROVE_PRESSURE_PLATE);
            m.add(Material.BAMBOO_PRESSURE_PLATE);
        }
        return m;
    }
    public static List<Material> shulkerBoxes()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.BLACK_SHULKER_BOX);
        m.add(Material.BLUE_SHULKER_BOX);
        m.add(Material.BROWN_SHULKER_BOX);
        m.add(Material.CYAN_SHULKER_BOX);
        m.add(Material.GRAY_SHULKER_BOX);
        m.add(Material.GREEN_SHULKER_BOX);
        m.add(Material.LIGHT_BLUE_SHULKER_BOX);
        m.add(Material.LIGHT_GRAY_SHULKER_BOX);
        m.add(Material.LIME_SHULKER_BOX);
        m.add(Material.MAGENTA_SHULKER_BOX);
        m.add(Material.ORANGE_SHULKER_BOX);
        m.add(Material.PINK_SHULKER_BOX);
        m.add(Material.PURPLE_SHULKER_BOX);
        m.add(Material.RED_SHULKER_BOX);
        m.add(Material.WHITE_SHULKER_BOX);
        m.add(Material.YELLOW_SHULKER_BOX);
        m.add(Material.SHULKER_BOX);
        return m;
    }
    public static List<Material> chests()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.CHEST);
        m.add(Material.CHEST_MINECART);
        m.add(Material.TRAPPED_CHEST);
        return m;
    }
    public static List<Material> enderChests()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.ENDER_CHEST);
        return m;
    }
    public static List<Material> tnt()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.TNT);
        m.add(Material.TNT_MINECART);
        return m;
    }
    public static List<Material> anvils()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.ANVIL);
        m.add(Material.CHIPPED_ANVIL);
        m.add(Material.DAMAGED_ANVIL);
        return m;
    }
    public static List<EntityType> rideable()
    {
        List<EntityType> m = new ArrayList<EntityType>();
        m.add(EntityType.HORSE);
        m.add(EntityType.SKELETON_HORSE);
        m.add(EntityType.DONKEY);
        m.add(EntityType.MULE);
        m.add(EntityType.PIG);
        if (GameFunctions.isVersionSafe(1.16)) {
            m.add(EntityType.STRIDER);
        }
        if (GameFunctions.isVersionSafe(1.20)) {
            m.add(EntityType.CAMEL);
        }
        return m;
    }
    public static HashSet<EntityType> hostileMobs() {
        HashSet<EntityType> hsMonsters = new HashSet<EntityType>();

        hsMonsters.add(EntityType.BAT);
        hsMonsters.add(EntityType.BLAZE);
        hsMonsters.add(EntityType.CAVE_SPIDER);
        hsMonsters.add(EntityType.CREEPER);
        hsMonsters.add(EntityType.DROWNED);
        hsMonsters.add(EntityType.ELDER_GUARDIAN);
        hsMonsters.add(EntityType.ENDERMAN);
        hsMonsters.add(EntityType.ENDERMITE);
        hsMonsters.add(EntityType.EVOKER);
        hsMonsters.add(EntityType.EVOKER_FANGS);
        hsMonsters.add(EntityType.GHAST);
        hsMonsters.add(EntityType.GUARDIAN);
        hsMonsters.add(EntityType.HUSK);
        hsMonsters.add(EntityType.ILLUSIONER);
        hsMonsters.add(EntityType.MAGMA_CUBE);
        hsMonsters.add(EntityType.PHANTOM);
        hsMonsters.add(EntityType.SHULKER);
        hsMonsters.add(EntityType.SILVERFISH);
        hsMonsters.add(EntityType.SKELETON_HORSE);
        hsMonsters.add(EntityType.SKELETON);
        hsMonsters.add(EntityType.SLIME);
        hsMonsters.add(EntityType.SPIDER);
        hsMonsters.add(EntityType.STRAY);
        hsMonsters.add(EntityType.VEX);
        hsMonsters.add(EntityType.VINDICATOR);
        hsMonsters.add(EntityType.WITCH);
        hsMonsters.add(EntityType.WITHER);
        hsMonsters.add(EntityType.WITHER_SKELETON);
        hsMonsters.add(EntityType.ZOMBIE_HORSE);
        hsMonsters.add(EntityType.ZOMBIE);
        hsMonsters.add(EntityType.ZOMBIE_VILLAGER);
        if (GameFunctions.is115()) {
            hsMonsters.add(EntityType.PILLAGER);
            hsMonsters.add(EntityType.BEE);
        }
        if (GameFunctions.isVersionSafe(1.16)) {
            // Add any new entities here
            hsMonsters.add(EntityType.PIGLIN);
            hsMonsters.add(EntityType.HOGLIN);
            hsMonsters.add(EntityType.ZOGLIN);
        }
        if (GameFunctions.isVersionSafe(1.19)) {
            hsMonsters.add(EntityType.WARDEN);
        }
        return hsMonsters;
    }

    public static List<Material> fishBuckets() {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.COD_BUCKET);
        m.add(Material.PUFFERFISH_BUCKET);
        m.add(Material.SALMON_BUCKET);
        m.add(Material.TROPICAL_FISH_BUCKET);
        return m;
    }
    public static List<Material> spawnEggs()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.BAT_SPAWN_EGG);
        m.add(Material.BLAZE_SPAWN_EGG);
        m.add(Material.CAVE_SPIDER_SPAWN_EGG);
        m.add(Material.CHICKEN_SPAWN_EGG);
        m.add(Material.COD_SPAWN_EGG);
        m.add(Material.COW_SPAWN_EGG);
        m.add(Material.CREEPER_SPAWN_EGG);
        m.add(Material.DOLPHIN_SPAWN_EGG);
        m.add(Material.DONKEY_SPAWN_EGG);
        m.add(Material.DROWNED_SPAWN_EGG);
        m.add(Material.ELDER_GUARDIAN_SPAWN_EGG);
        m.add(Material.ENDERMAN_SPAWN_EGG);
        m.add(Material.ENDERMITE_SPAWN_EGG);
        m.add(Material.EVOKER_SPAWN_EGG);
        m.add(Material.GHAST_SPAWN_EGG);
        m.add(Material.GUARDIAN_SPAWN_EGG);
        m.add(Material.HORSE_SPAWN_EGG);
        m.add(Material.HUSK_SPAWN_EGG);
        m.add(Material.LLAMA_SPAWN_EGG);
        m.add(Material.MAGMA_CUBE_SPAWN_EGG);
        m.add(Material.MOOSHROOM_SPAWN_EGG);
        m.add(Material.MULE_SPAWN_EGG);
        m.add(Material.OCELOT_SPAWN_EGG);
        m.add(Material.PARROT_SPAWN_EGG);
        m.add(Material.PHANTOM_SPAWN_EGG);
        m.add(Material.PIG_SPAWN_EGG);
        m.add(Material.POLAR_BEAR_SPAWN_EGG);
        m.add(Material.PUFFERFISH_SPAWN_EGG);
        m.add(Material.RABBIT_SPAWN_EGG);
        m.add(Material.SALMON_SPAWN_EGG);
        m.add(Material.SHEEP_SPAWN_EGG);
        m.add(Material.SHULKER_SPAWN_EGG);
        m.add(Material.SILVERFISH_SPAWN_EGG);
        m.add(Material.SKELETON_HORSE_SPAWN_EGG);
        m.add(Material.SKELETON_SPAWN_EGG);
        m.add(Material.SLIME_SPAWN_EGG);
        m.add(Material.SPIDER_SPAWN_EGG);
        m.add(Material.SQUID_SPAWN_EGG);
        m.add(Material.STRAY_SPAWN_EGG);
        m.add(Material.TROPICAL_FISH_SPAWN_EGG);
        m.add(Material.TURTLE_SPAWN_EGG);
        m.add(Material.VEX_SPAWN_EGG);
        m.add(Material.VILLAGER_SPAWN_EGG);
        m.add(Material.VINDICATOR_SPAWN_EGG);
        m.add(Material.WITCH_SPAWN_EGG);
        m.add(Material.WITHER_SKELETON_SPAWN_EGG);
        m.add(Material.WOLF_SPAWN_EGG);
        m.add(Material.ZOMBIE_HORSE_SPAWN_EGG);
        m.add(Material.ZOMBIE_SPAWN_EGG);
        m.add(Material.ZOMBIE_VILLAGER_SPAWN_EGG);
        if (GameFunctions.isVersionSafe(1.14)) {
            m.add(Material.PILLAGER_SPAWN_EGG);
        }
        if (GameFunctions.isVersionSafe(1.15)) {
            m.add(Material.BEE_SPAWN_EGG);
        }
        if (GameFunctions.isVersionSafe(1.16)) {
            // Add any new spawn eggs here
            m.add(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
            m.add(Material.HOGLIN_SPAWN_EGG);
            m.add(Material.STRIDER_SPAWN_EGG);
            m.add(Material.ZOGLIN_SPAWN_EGG);
        }
        if (GameFunctions.isVersionSafe(1.19)) {
            m.add(Material.WARDEN_SPAWN_EGG);
        }
        if (GameFunctions.isVersionSafe(1.20)) {
            m.add(Material.CAMEL_SPAWN_EGG);
            m.add(Material.SNIFFER_SPAWN_EGG);
        }
        return m;
    }
    public static List<Material> flowerPots()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.POTTED_ACACIA_SAPLING);
        m.add(Material.POTTED_ALLIUM);
        m.add(Material.POTTED_AZURE_BLUET);
        m.add(Material.POTTED_BIRCH_SAPLING);
        m.add(Material.POTTED_BLUE_ORCHID);
        m.add(Material.POTTED_BROWN_MUSHROOM);
        m.add(Material.POTTED_CACTUS);
        m.add(Material.POTTED_DANDELION);
        m.add(Material.POTTED_DARK_OAK_SAPLING);
        m.add(Material.POTTED_DEAD_BUSH);
        m.add(Material.POTTED_FERN);
        m.add(Material.POTTED_JUNGLE_SAPLING);
        m.add(Material.POTTED_OAK_SAPLING);
        m.add(Material.POTTED_ORANGE_TULIP);
        m.add(Material.POTTED_OXEYE_DAISY);
        m.add(Material.POTTED_PINK_TULIP);
        m.add(Material.POTTED_POPPY);
        m.add(Material.POTTED_RED_MUSHROOM);
        m.add(Material.POTTED_RED_TULIP);
        m.add(Material.POTTED_SPRUCE_SAPLING);
        m.add(Material.POTTED_WHITE_TULIP);
        m.add(Material.FLOWER_POT);
        return m;
    }
    public static List<Material> otherInteractions()
    {
        List<Material> m = new ArrayList<Material>();
        m.add(Material.LEVER);
        m.add(Material.ARMOR_STAND);
        m.add(Material.BEACON);
        m.add(Material.BREWING_STAND);
        m.add(Material.CAULDRON);
        m.add(Material.COMPARATOR);
        m.add(Material.CRAFTING_TABLE);
        m.add(Material.DAYLIGHT_DETECTOR);
        m.add(Material.DISPENSER);
        m.add(Material.DROPPER);
        m.add(Material.ENCHANTING_TABLE);
        m.add(Material.FURNACE);
        m.add(Material.FURNACE_MINECART);
        m.add(Material.HOPPER);
        m.add(Material.HOPPER_MINECART);
        m.add(Material.ITEM_FRAME);
        m.add(Material.JUKEBOX);
        m.add(Material.NOTE_BLOCK);
        if (GameFunctions.isVersionSafe(1.14)) {
            m.add(Material.CAMPFIRE);
        }
        if (GameFunctions.isVersionSafe(1.15)) {
            m.add(Material.BEE_NEST);
            m.add(Material.BEEHIVE);
        }
        if (GameFunctions.isVersionSafe(1.16)) {
            // Add any new materials to stop interactions on here
            m.add(Material.LODESTONE);
            m.add(Material.RESPAWN_ANCHOR);
            m.add(Material.SMITHING_TABLE);
            m.add(Material.SOUL_CAMPFIRE);
            m.add(Material.TARGET);
        }
        if (GameFunctions.isVersionSafe(1.20)) {
            m.add(Material.CHISELED_BOOKSHELF);
        }
        return m;
    }

}
