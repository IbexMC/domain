package com.ibexmc.domain.domainblock.config;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.Visualize;
import com.ibexmc.domain.domainblock.spawner.Spawner;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.ColorFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.messaging.Error;
import com.ibexmc.internal.utility.YmlParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ShapedRecipe;

import java.io.File;
import java.util.*;

public class ConfigManager {

    private final Domain instance;

    public ConfigManager(Domain instance) {
        this.instance = instance;
    }

    public void loadAllConfigs() {
        if (instance.config().isUseLegacyConfig()) {
            for (File file : Objects.requireNonNull(new File(instance.getDataFolder().getAbsolutePath() + File.separator + "Blocks").listFiles())) {
                instance.getDebug().log("ConfigManager", "loadAllConfigs", "Processing " + file.getName());
                if (file.exists()) {
                    IFieldConfig fieldConfig = loadConfig(new LegacyFieldConfig(), file);
                    if (!StringFunctions.isNullOrEmpty(fieldConfig.getBlockKey())) {
                        NamespacedKey key = new NamespacedKey(Domain.getInstance(), "domain_key_" + fieldConfig.getBlockKey());
                        ShapedRecipe recipe = new ShapedRecipe(key, fieldConfig.getItem());
                        LegacyFieldConfig legacyFieldConfig = (LegacyFieldConfig) fieldConfig;
                        LegacyFieldConfig.Shape legacyShape = legacyFieldConfig.remedyShape();
                        recipe.shape(legacyShape.getTop(), legacyShape.getMiddle(), legacyShape.getBottom());
                        for (LegacyFieldConfig.Ingredient i : legacyFieldConfig.recipeItems()) {
                            recipe.setIngredient(i.getLetter().charAt(0), i.getMaterial());
                        }
                        if (instance.getConfig().getBoolean(("allow_block_build"))) {
                            try {
                                Bukkit.removeRecipe(recipe.getKey());
                                Bukkit.addRecipe(recipe); // Add the recipe to the server
                            } catch (Exception ex) {
                                instance.getError().save(
                                        "001",
                                        "ConfigManager",
                                        "loadAllConfigs.Legacy",
                                        "Unable to add recipe for " + fieldConfig.getDisplayName(),
                                        ex.getMessage(),
                                        Error.Severity.WARN,
                                        ex.getStackTrace()
                                );
                            }
                        }
                        instance.getData().addDomainFieldConfig(fieldConfig);
                        instance.getData().putAvailableBlocks(fieldConfig.getBlockKey(), fieldConfig.getItem());
                    }
                }
            }
        } else {
            // TODO: Load new config file format
        }
    }

    private IFieldConfig loadConfig(IFieldConfig fieldConfig, File file) {

        YmlParser ymlParser = new YmlParser(
                Domain.getInstance(),
                file,
                "Domain"
        );

        YmlParser.Output crBlockKey = ymlParser.getString("block_key","");
        if (crBlockKey.getSuccess() && !StringFunctions.isNullOrEmpty(crBlockKey.getString())) {
            fieldConfig.setBlockKey(crBlockKey.getString());
            YmlParser.Output crBaseMaterial = ymlParser.getMaterial("base_material",Material.AIR);
            if (crBaseMaterial.getSuccess()) {
                fieldConfig.setBaseMaterial(crBaseMaterial.getMaterial());
            } else {
                fieldConfig.setBaseMaterial(Material.AIR);
            }
            YmlParser.Output crFieldDisplayMaterial = ymlParser.getMaterial("field_display_material",Material.RED_STAINED_GLASS);
            if (crFieldDisplayMaterial.getSuccess()) {
                fieldConfig.setFieldDisplayMaterial(crFieldDisplayMaterial.getMaterial());
            } else {
                fieldConfig.setFieldDisplayMaterial(Material.RED_STAINED_GLASS);
            }
            YmlParser.Output crFieldResizeMaterial = ymlParser.getMaterial("field_resize_material",Material.LIME_STAINED_GLASS);
            if (crFieldResizeMaterial.getSuccess()) {
                fieldConfig.setFieldResizeMaterial(crFieldResizeMaterial.getMaterial());
            } else {
                fieldConfig.setFieldDisplayMaterial(Material.LIME_STAINED_GLASS);
            }
            YmlParser.Output crFieldDisplaySecs = ymlParser.getInt("field_display_secs",30);
            if (crFieldDisplaySecs.getSuccess()) {
                fieldConfig.setFieldDisplaySecs(crFieldDisplaySecs.getInt());
            } else {
                fieldConfig.setFieldDisplaySecs(30);
            }
            YmlParser.Output crDisplayName = ymlParser.getString("display_name","");
            if (crDisplayName.getSuccess()) {
                fieldConfig.setDisplayName(ColorFunctions.colorToString(crDisplayName.getString()));
            } else {
                fieldConfig.setDisplayName("");
            }
            YmlParser.Output crDefaultName = ymlParser.getString("default_new_name","<%player%>'s <%type%> field");
            if (crDefaultName.getSuccess()) {
                if (!StringFunctions.isNullOrEmpty(crDefaultName.getString())) {
                    fieldConfig.setDefaultNewName(ColorFunctions.colorToString(crDefaultName.getString()));
                } else {
                    fieldConfig.setDefaultNewName("<%player%>'s <%type%> field");
                }
            } else {
                fieldConfig.setDefaultNewName("<%player%>'s <%type%> field");
            }
            YmlParser.Output crLore = ymlParser.getString("lore",  "");
            if (crLore.getSuccess()) {
                fieldConfig.setItemLore(StringFunctions.stringToList(crLore.getString(), "\\|"));
            } else {
                fieldConfig.setItemLore(new ArrayList<>());
            }
            YmlParser.Output crVolume = ymlParser.getInt("volume",0);
            if (crVolume.getSuccess()) {
                fieldConfig.setVolume(crVolume.getInt());
            } else {
                fieldConfig.setVolume(0);
            }
            if (fieldConfig instanceof LegacyFieldConfig) {
                YmlParser.Output crPosition1 = ymlParser.getString("position_1","");
                if (crPosition1.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition1(crPosition1.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition1("");
                }
                YmlParser.Output crPosition2 = ymlParser.getString("position_2","");
                if (crPosition2.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition2(crPosition2.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition2("");
                }
                YmlParser.Output crPosition3 = ymlParser.getString("position_3","");
                if (crPosition3.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition3(crPosition3.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition3("");
                }
                YmlParser.Output crPosition4 = ymlParser.getString("position_4","");
                if (crPosition4.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition4(crPosition4.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition4("");
                }
                YmlParser.Output crPosition5 = ymlParser.getString("position_5","");
                if (crPosition5.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition5(crPosition5.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition5("");
                }
                YmlParser.Output crPosition6 = ymlParser.getString("position_6","");
                if (crPosition6.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition6(crPosition6.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition6("");
                }
                YmlParser.Output crPosition7 = ymlParser.getString("position_7","");
                if (crPosition7.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition7(crPosition7.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition7("");
                }
                YmlParser.Output crPosition8 = ymlParser.getString("position_8","");
                if (crPosition8.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition8(crPosition8.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition8("");
                }
                YmlParser.Output crPosition9 = ymlParser.getString("position_9","");
                if (crPosition9.getSuccess()) {
                    ((LegacyFieldConfig) fieldConfig).setPosition9(crPosition9.getString());
                } else {
                    ((LegacyFieldConfig) fieldConfig).setPosition9("");
                }
            }
            YmlParser.Output crFieldOrder = ymlParser.getInt("field_order",0);
            if (crFieldOrder.getSuccess()) {
                fieldConfig.setFieldOrder(crFieldOrder.getInt());
            } else {
                fieldConfig.setFieldOrder(0);
            }
            YmlParser.Output crLockedTo = ymlParser.getString("locked_to","");
            if (crLockedTo.getSuccess()) {
                fieldConfig.setLockedTo(crLockedTo.getString());
            } else {
                fieldConfig.setLockedTo("");
            }
            YmlParser.Output crBlockInField = ymlParser.getBoolean("block_in_field",true);
            if (crBlockInField.getSuccess()) {
                fieldConfig.setBlockInField(crBlockInField.getBoolean());
            } else {
                fieldConfig.setBlockInField(true);
            }
            YmlParser.Output crCostToBuy = ymlParser.getInt("cost_to_buy",0);
            if (crCostToBuy.getSuccess()) {
                fieldConfig.setCostToBuy(crCostToBuy.getInt());
            } else {
                fieldConfig.setCostToBuy(0);
            }
            YmlParser.Output crCostToSell = ymlParser.getInt("cost_to_sell",0);
            if (crCostToSell.getSuccess()) {
                fieldConfig.setCostToSell(crCostToSell.getInt());
            } else {
                fieldConfig.setCostToSell(0);
            }
            YmlParser.Output crCostToPlace = ymlParser.getInt("cost_to_place",0);
            if (crCostToPlace.getSuccess()) {
                fieldConfig.setCostToPlace(crCostToPlace.getInt());
            } else {
                fieldConfig.setCostToPlace(0);
            }
            YmlParser.Output crCostToRefund = ymlParser.getInt("cost_to_refund",0);
            if (crCostToRefund.getSuccess()) {
                fieldConfig.setCostToRefund(crCostToRefund.getInt());
            } else {
                fieldConfig.setCostToRefund(0);
            }
            YmlParser.Output crCostToRent = ymlParser.getInt("cost_to_rent",0);
            if (crCostToRent.getSuccess()) {
                fieldConfig.setRentCost(crCostToRent.getInt());
            } else {
                fieldConfig.setRentCost(0);
            }
            YmlParser.Output crRentDays = ymlParser.getInt("rent_days",0);
            if (crRentDays.getSuccess()) {
                fieldConfig.setRentDays(crRentDays.getInt());
            } else {
                fieldConfig.setRentDays(0);
            }
            YmlParser.Output crDefaultRadius = ymlParser.getInt("default_radius",0);
            if (crDefaultRadius.getSuccess()) {
                fieldConfig.setDefaultRadius(crDefaultRadius.getInt());
            } else {
                fieldConfig.setDefaultRadius(0);
            }
            YmlParser.Output crDefaultHeightUp = ymlParser.getInt("default_height_up",0);
            if (crDefaultHeightUp.getSuccess()) {
                fieldConfig.setDefaultHeightUp(crDefaultHeightUp.getInt());
            } else {
                fieldConfig.setDefaultHeightUp(0);
            }
            YmlParser.Output crDefaultHeightDown = ymlParser.getInt("default_height_down",0);
            if (crDefaultHeightDown.getSuccess()) {
                fieldConfig.setDefaultHeightDown(crDefaultHeightDown.getInt());
            } else {
                fieldConfig.setDefaultHeightDown(0);
            }
            YmlParser.Output crMaxMerge = ymlParser.getInt("max_merge",0);
            if (crMaxMerge.getSuccess()) {
                fieldConfig.setMaxMerge(crMaxMerge.getInt());
            } else {
                fieldConfig.setMaxMerge(0);
            }
            YmlParser.Output crMaxBlocks = ymlParser.getInt("max_blocks",0);
            if (crMaxBlocks.getSuccess()) {
                fieldConfig.setMaxBlocks(crMaxBlocks.getInt());
            } else {
                fieldConfig.setMaxBlocks(0);
            }
            YmlParser.Output crInheritPerms = ymlParser.getBoolean("inherit_perms",true);
            if (crInheritPerms.getSuccess()) {
                fieldConfig.setInheritPermsFromParent(crInheritPerms.getBoolean());
            } else {
                fieldConfig.setInheritPermsFromParent(true);
            }
            YmlParser.Output crInvalidWorlds = ymlParser.getStringList("invalid_worlds");
            Set<String> invalidWorlds = new HashSet<>();
            if (crInvalidWorlds.getSuccess()) {
                if (crInvalidWorlds.getStringList() != null) {
                    for (String worldName : crInvalidWorlds.getStringList()) {
                        World world = Bukkit.getWorld(worldName);
                        if (world != null) {
                            invalidWorlds.add(world.getName());
                        }
                    }
                }
            }
            fieldConfig.setInvalidWorlds(invalidWorlds);
            YmlParser.Output crOfflineDays = ymlParser.getInt("offline_days",9999);
            if (crOfflineDays.getSuccess()) {
                fieldConfig.setOfflineDays(crOfflineDays.getInt());
            } else {
                fieldConfig.setOfflineDays(9999);
            }
            YmlParser.Output crIgnoreYAxis = ymlParser.getBoolean("ignore_y_axis",false);
            if (crIgnoreYAxis.getSuccess()) {
                fieldConfig.setIgnoreYAxis(crIgnoreYAxis.getBoolean());
            } else {
                fieldConfig.setIgnoreYAxis(false);
            }
            YmlParser.Output crPreventResize = ymlParser.getBoolean("prevent_resize",false);
            if (crPreventResize.getSuccess()) {
                fieldConfig.setPreventResize(crPreventResize.getBoolean());
            } else {
                fieldConfig.setPreventResize(false);
            }
            YmlParser.Output crOneTimeUse = ymlParser.getBoolean("one_time_use",false);
            if (crOneTimeUse.getSuccess()) {
                fieldConfig.setOneTimeUse(crOneTimeUse.getBoolean());
            } else {
                fieldConfig.setOneTimeUse(false);
            }
            YmlParser.Output crVisualization = ymlParser.getString("visualization","CUBE");
            if (crVisualization.getSuccess()) {
                Visualize.VisualizationType visualizationType = Visualize.VisualizationType.valueOf(crVisualization.getString());
                if (visualizationType == null) {
                    fieldConfig.setVisualizationType(Visualize.VisualizationType.CUBE);
                } else {
                    fieldConfig.setVisualizationType(visualizationType);
                }
            } else {
                fieldConfig.setVisualizationType(Visualize.VisualizationType.CUBE);
            }
            YmlParser.Output crWhoInKick = ymlParser.getBoolean("allow_who_in_kick",false);
            if (crWhoInKick.getSuccess()) {
                fieldConfig.setAllowWhoInKick(crWhoInKick.getBoolean());
            } else {
                fieldConfig.setAllowWhoInKick(false);
            }
            YmlParser.Output crSnapToChunk = ymlParser.getBoolean("snap_to_chunk",false);
            if (crSnapToChunk.getSuccess()) {
                fieldConfig.setSnapToChunk(crSnapToChunk.getBoolean());
            } else {
                fieldConfig.setSnapToChunk(false);
            }
            Set<Spawner> spawners = new HashSet<>();
            YmlParser.Output crSpawnerSection = ymlParser.getConfigSection("spawn");
            if (crSpawnerSection.getSuccess()) {
                for (Object key : crSpawnerSection.getConfigSection().getKeys(false).toArray()) {
                    EntityType entityType = EntityType.valueOf(key.toString());
                    int spawnAmount = 0;
                    int multiAmountMax = 0;
                    int spawnChance = 0;
                    int multiChance = 0;
                    Set<UUID> worlds = new HashSet<>();
                    int minLight = 0;
                    int maxLight = 0;
                    boolean requireWater = false;
                    boolean requireSlimeChunk = false;
                    boolean requireGrass = false;
                    int maxDistance = 16;
                    int maxEntities = 20;
                    int baby = 0;
                    if (entityType != null) {
                        YmlParser.Output crSpawnerAmount = ymlParser.getInt("spawn." + entityType.name() + ".amount", 0);
                        if (crSpawnerAmount.getSuccess()) {
                            spawnAmount = crSpawnerAmount.getInt();
                        }
                        YmlParser.Output crSpawnerMultiAmountMax = ymlParser.getInt("spawn." + entityType.name() + ".multi_amount_max", 0);
                        if (crSpawnerMultiAmountMax.getSuccess()) {
                            multiAmountMax = crSpawnerMultiAmountMax.getInt();
                        }    //endregion    //region Spawn.Chance    
                        YmlParser.Output crSpawnerChance = ymlParser.getInt("spawn." + entityType.name() + ".chance", 0);
                        if (crSpawnerChance.getSuccess()) {
                            spawnChance = crSpawnerChance.getInt();
                        }
                        YmlParser.Output crSpawnerMultiChance = ymlParser.getInt("spawn." + entityType.name() + ".multi_chance", 0);
                        if (crSpawnerMultiChance.getSuccess()) {
                            multiChance = crSpawnerMultiChance.getInt();
                        }
                        YmlParser.Output crWorlds = ymlParser.getStringList("spawn." + entityType.name() + ".worlds");
                        if (crWorlds.getSuccess()) {
                            if (crWorlds.getStringList() != null) {
                                for (String worldName : crWorlds.getStringList()) {
                                    World spawnWorld = Bukkit.getWorld(worldName);
                                    if (spawnWorld != null) {
                                        worlds.add(crWorlds.getUUID());
                                    }
                                }
                            }
                            YmlParser.Output crSpawnerMinLight = ymlParser.getInt("spawn." + entityType.name() + ".min_light", 0);
                            if (crSpawnerMinLight.getSuccess()) {
                                minLight = crSpawnerMinLight.getInt();
                            }
                            YmlParser.Output crSpawnerMaxLight = ymlParser.getInt("spawn." + entityType.name() + ".max_light", 0);
                            if (crSpawnerMaxLight.getSuccess()) {
                                maxLight = crSpawnerMaxLight.getInt();
                            }
                            YmlParser.Output crSpawnerReqWater = ymlParser.getBoolean("spawn." + entityType.name() + ".req_water", false);
                            if (crSpawnerReqWater.getSuccess()) {
                                requireWater = crSpawnerReqWater.getBoolean();
                            }
                            YmlParser.Output crSpawnerReqSlimeChunk = ymlParser.getBoolean("spawn." + entityType.name() + ".req_slime_chunk", false);
                            if (crSpawnerReqSlimeChunk.getSuccess()) {
                                requireSlimeChunk = crSpawnerReqSlimeChunk.getBoolean();
                            }
                            YmlParser.Output crSpawnerReqGrass = ymlParser.getBoolean("spawn." + entityType.name() + ".req_grass", false);
                            if (crSpawnerReqGrass.getSuccess()) {
                                requireGrass = crSpawnerReqGrass.getBoolean();
                            }
                            YmlParser.Output crSpawnerBaby = ymlParser.getInt("spawn." + entityType.name() + ".baby", 0);
                            if (crSpawnerBaby.getSuccess()) {
                                baby = crSpawnerBaby.getInt();
                            }
                            YmlParser.Output crSpawnerMaxDistance = ymlParser.getInt("spawn." + entityType.name() + ".max_distance", 0);
                            if (crSpawnerMaxDistance.getSuccess()) {
                                maxDistance = crSpawnerMaxDistance.getInt();
                            }
                            YmlParser.Output crSpawnerMaxEntities = ymlParser.getInt("spawn." + entityType.name() + ".max_entities", 0);
                            if (crSpawnerMaxEntities.getSuccess()) {
                                maxEntities = crSpawnerMaxEntities.getInt();
                            }
                            Spawner spawner = new Spawner(entityType, spawnAmount, multiAmountMax, spawnChance, multiChance, worlds, minLight, maxLight, requireWater, requireSlimeChunk, requireGrass, maxDistance, maxEntities, baby);
                            spawners.add(spawner);

                        }
                        fieldConfig.setSpawners(spawners);
                    }
                }
            }
            YmlParser.Output crFlagSection = ymlParser.getConfigSection("flags");
            Set<Flag> flags = new HashSet<>();
            if (crFlagSection.getSuccess()) {
                for (Object key : crFlagSection.getConfigSection().getKeys(false).toArray()) {
                    Flag.Type flagType = Flag.flagFromString(key.toString());
                    if (flagType != null) {
                        YmlParser.Output crFlagStatus = ymlParser.getBoolean("flags." + flagType.name(),false);
                        if (crFlagStatus.getSuccess()) {
                            Flag flag = new Flag(0,flagType,crFlagStatus.getBoolean());
                            flags.add(flag);
                        }
                    }
                }
            }
            fieldConfig.setFlags(flags);
            fieldConfig.setDynmapLineHexColor(Domain.getInstance().config().getDynmapLineHexColor());
            fieldConfig.setDynmapFillHexColor(Domain.getInstance().config().getDynmapFillHexColor());
            fieldConfig.setDynmapFillOpacity(Domain.getInstance().config().getDynmapFillOpacity());
            fieldConfig.setDynmapShowMarker(Domain.getInstance().config().getDynmapShowMarker());
            fieldConfig.setDynmapMarkerIcon(Domain.getInstance().config().getDynmapIcon());
            YmlParser.Output crDynmapLineColor = ymlParser.getString("dynmap.line_hex_color","8B589C");
            if (crDynmapLineColor.getSuccess()) {
                fieldConfig.setDynmapLineHexColor(crDynmapLineColor.getString());
            }
            YmlParser.Output crDynmapFillColor = ymlParser.getString("dynmap.fill_hex_color","F7E6FC");
            if (crDynmapFillColor.getSuccess()) {
                fieldConfig.setDynmapFillHexColor(crDynmapFillColor.getString());
            }
            YmlParser.Output crDynmapFillOpacity = ymlParser.getDouble("dynmap.fill_opacity",0.5);
            if (crDynmapFillOpacity.getSuccess()) {
                fieldConfig.setDynmapFillOpacity(crDynmapFillOpacity.getDouble());
            }
            YmlParser.Output crDynmapShowMarker = ymlParser.getBoolean("dynmap.show_marker",false);
            if (crDynmapShowMarker.getSuccess()) {
                fieldConfig.setDynmapShowMarker(crDynmapShowMarker.getBoolean());
                if (crDynmapShowMarker.getBoolean()) {
                    YmlParser.Output crDynmapMarkerIcon = ymlParser.getString("dynmap.marker_icon","default");
                    if (crDynmapMarkerIcon.getSuccess()) {
                        fieldConfig.setDynmapMarkerIcon(crDynmapMarkerIcon.getString());
                    }
                }
            }
        } else {
            // invalid block key
            // todo: error
        }


        return fieldConfig;
    }
}
