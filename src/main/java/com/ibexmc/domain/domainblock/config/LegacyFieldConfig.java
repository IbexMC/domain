package com.ibexmc.domain.domainblock.config;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.Visualize;
import com.ibexmc.domain.domainblock.spawner.Spawner;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.ColorFunctions;
import com.ibexmc.internal.functions.ItemFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LegacyFieldConfig implements IFieldConfig {

    private Material baseMaterial = Material.AIR;
    private Material fieldDisplayMaterial = Material.RED_STAINED_GLASS;
    private Material fieldResizeMaterial = Material.LIME_STAINED_GLASS;
    private int fieldDisplaySecs = 30;
    private String displayName = "";
    private String defaultNewName = "";
    private String blockKey = "";
    private List<String> itemLore = new ArrayList<>();
    private String welcomeMsg = "";
    private String farewellMsg = "";
    private int volume = 0;
    private String position1 = "";
    private String position2 = "";
    private String position3 = "";
    private String position4 = "";
    private String position5 = "";
    private String position6 = "";
    private String position7 = "";
    private String position8 = "";
    private String position9 = "";
    private int fieldOrder = 0;
    private String lockedTo = "";
    private boolean blockInField = true;
    private Set<Flag> flags = new HashSet<>();
    private int costToPlace = 0;
    private int costToRefund = 0;
    private int rentCost = 0;
    private int rentDays = 0;
    private int defaultRadius = 5;
    private int defaultHeightUp = 2;
    private int defaultHeightDown = 5;
    private int maxMerge = -1;
    private int maxBlocks = -1;
    private boolean inheritPermsFromParent = false;
    private Set<String> invalidWorlds = new HashSet<>();
    private int offlineDays = -1;
    private boolean ignoreYAxis = false;
    private boolean preventResize = false;
    private boolean oneTimeUse = false;
    private Visualize.VisualizationType visualizationType = Visualize.VisualizationType.CUBE;
    private int costToBuy = 0;
    private int costToSell = 0;
    private Set<Spawner> spawners = new HashSet<>();
    private boolean allowWhoInKick = false;
    private boolean snapToChunk = false;
    private String dynmapLineHexColor = "8B589C";
    private String dynmapFillHexColor = "F7E6FC";
    private double dynmapFillOpacity = 0.5;
    private boolean dynmapShowMarker = false;
    private String dynmapMarkerIcon = "default";

    @Override
    public Material getBaseMaterial() {
        return baseMaterial;
    }

    @Override
    public void setBaseMaterial(Material baseMaterial) {
        this.baseMaterial = baseMaterial;
    }

    @Override
    public Material getFieldDisplayMaterial() {
        return fieldDisplayMaterial;
    }

    @Override
    public void setFieldDisplayMaterial(Material fieldDisplayMaterial) {
        this.fieldDisplayMaterial = fieldDisplayMaterial;
    }

    @Override
    public Material getFieldResizeMaterial() {
        return fieldResizeMaterial;
    }

    @Override
    public void setFieldResizeMaterial(Material fieldResizeMaterial) {
        this.fieldResizeMaterial = fieldResizeMaterial;
    }

    @Override
    public int getFieldDisplaySecs() {
        return fieldDisplaySecs;
    }

    @Override
    public void setFieldDisplaySecs(int fieldDisplaySecs) {
        this.fieldDisplaySecs = fieldDisplaySecs;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDefaultNewName() {
        return defaultNewName;
    }

    @Override
    public void setDefaultNewName(String defaultNewName) {
        this.defaultNewName = defaultNewName;
    }

    @Override
    public String getBlockKey() {
        return blockKey;
    }

    @Override
    public void setBlockKey(String blockKey) {
        this.blockKey = blockKey;
    }

    @Override
    public List<String> getItemLore() {
        return itemLore;
    }

    @Override
    public void setItemLore(List<String> itemLore) {
        this.itemLore = itemLore;
    }

    @Override
    public String getWelcomeMsg() {
        return welcomeMsg;
    }

    @Override
    public void setWelcomeMsg(String welcomeMsg) {
        this.welcomeMsg = welcomeMsg;
    }

    @Override
    public String getFarewellMsg() {
        return farewellMsg;
    }

    @Override
    public void setFarewellMsg(String farewellMsg) {
        this.farewellMsg = farewellMsg;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getPosition1() {
        return position1;
    }

    public void setPosition1(String position1) {
        this.position1 = position1;
    }

    public String getPosition2() {
        return position2;
    }

    public void setPosition2(String position2) {
        this.position2 = position2;
    }

    public String getPosition3() {
        return position3;
    }

    public void setPosition3(String position3) {
        this.position3 = position3;
    }

    public String getPosition4() {
        return position4;
    }

    public void setPosition4(String position4) {
        this.position4 = position4;
    }

    public String getPosition5() {
        return position5;
    }

    public void setPosition5(String position5) {
        this.position5 = position5;
    }

    public String getPosition6() {
        return position6;
    }

    public void setPosition6(String position6) {
        this.position6 = position6;
    }

    public String getPosition7() {
        return position7;
    }

    public void setPosition7(String position7) {
        this.position7 = position7;
    }

    public String getPosition8() {
        return position8;
    }

    public void setPosition8(String position8) {
        this.position8 = position8;
    }

    public String getPosition9() {
        return position9;
    }

    public void setPosition9(String position9) {
        this.position9 = position9;
    }

    @Override
    public int getFieldOrder() {
        return fieldOrder;
    }

    @Override
    public void setFieldOrder(int fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    @Override
    public String getLockedTo() {
        return lockedTo;
    }

    @Override
    public void setLockedTo(String lockedTo) {
        this.lockedTo = lockedTo;
    }

    @Override
    public boolean isBlockInField() {
        return blockInField;
    }

    @Override
    public void setBlockInField(boolean blockInField) {
        this.blockInField = blockInField;
    }

    @Override
    public Set<Flag> getFlags() {
        return flags;
    }

    @Override
    public void setFlags(Set<Flag> flags) {
        this.flags = flags;
    }

    @Override
    public int getCostToPlace() {
        return costToPlace;
    }

    @Override
    public void setCostToPlace(int costToPlace) {
        this.costToPlace = costToPlace;
    }

    @Override
    public int getCostToRefund() {
        return costToRefund;
    }

    @Override
    public void setCostToRefund(int costToRefund) {
        this.costToRefund = costToRefund;
    }

    @Override
    public int getRentCost() {
        return rentCost;
    }

    @Override
    public void setRentCost(int rentCost) {
        this.rentCost = rentCost;
    }

    @Override
    public int getRentDays() {
        return rentDays;
    }

    @Override
    public void setRentDays(int rentDays) {
        this.rentDays = rentDays;
    }

    @Override
    public int getDefaultRadius() {
        return defaultRadius;
    }

    @Override
    public void setDefaultRadius(int defaultRadius) {
        this.defaultRadius = defaultRadius;
    }

    @Override
    public int getDefaultHeightUp() {
        return defaultHeightUp;
    }

    @Override
    public void setDefaultHeightUp(int defaultHeightUp) {
        this.defaultHeightUp = defaultHeightUp;
    }

    @Override
    public int getDefaultHeightDown() {
        return defaultHeightDown;
    }

    @Override
    public void setDefaultHeightDown(int defaultHeightDown) {
        this.defaultHeightDown = defaultHeightDown;
    }

    @Override
    public int getMaxMerge() {
        return maxMerge;
    }

    @Override
    public void setMaxMerge(int maxMerge) {
        this.maxMerge = maxMerge;
    }

    @Override
    public int getMaxBlocks() {
        return maxBlocks;
    }

    @Override
    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

    @Override
    public boolean isInheritPermsFromParent() {
        return inheritPermsFromParent;
    }

    @Override
    public void setInheritPermsFromParent(boolean inheritPermsFromParent) {
        this.inheritPermsFromParent = inheritPermsFromParent;
    }

    @Override
    public Set<String> getInvalidWorlds() {
        return invalidWorlds;
    }

    @Override
    public void setInvalidWorlds(Set<String> invalidWorlds) {
        this.invalidWorlds = invalidWorlds;
    }

    @Override
    public int getOfflineDays() {
        return offlineDays;
    }

    @Override
    public void setOfflineDays(int offlineDays) {
        this.offlineDays = offlineDays;
    }

    @Override
    public boolean isIgnoreYAxis() {
        return ignoreYAxis;
    }

    @Override
    public void setIgnoreYAxis(boolean ignoreYAxis) {
        this.ignoreYAxis = ignoreYAxis;
    }

    @Override
    public boolean isPreventResize() {
        return preventResize;
    }

    @Override
    public void setPreventResize(boolean preventResize) {
        this.preventResize = preventResize;
    }

    @Override
    public boolean isOneTimeUse() {
        return oneTimeUse;
    }

    @Override
    public void setOneTimeUse(boolean oneTimeUse) {
        this.oneTimeUse = oneTimeUse;
    }

    @Override
    public Visualize.VisualizationType getVisualizationType() {
        return visualizationType;
    }

    @Override
    public void setVisualizationType(Visualize.VisualizationType visualizationType) {
        this.visualizationType = visualizationType;
    }

    @Override
    public int getCostToBuy() {
        return costToBuy;
    }

    @Override
    public void setCostToBuy(int costToBuy) {
        this.costToBuy = costToBuy;
    }

    @Override
    public int getCostToSell() {
        return costToSell;
    }

    @Override
    public void setCostToSell(int costToSell) {
        this.costToSell = costToSell;
    }

    @Override
    public Set<Spawner> getSpawners() {
        return spawners;
    }

    @Override
    public void setSpawners(Set<Spawner> spawners) {
        this.spawners = spawners;
    }

    @Override
    public boolean isAllowWhoInKick() {
        return allowWhoInKick;
    }

    @Override
    public void setAllowWhoInKick(boolean allowWhoInKick) {
        this.allowWhoInKick = allowWhoInKick;
    }

    @Override
    public boolean isSnapToChunk() {
        return snapToChunk;
    }

    @Override
    public void setSnapToChunk(boolean snapToChunk) {
        this.snapToChunk = snapToChunk;
    }

    @Override
    public String getDynmapLineHexColor() {
        return dynmapLineHexColor;
    }

    @Override
    public void setDynmapLineHexColor(String dynmapLineHexColor) {
        this.dynmapLineHexColor = dynmapLineHexColor;
    }

    @Override
    public String getDynmapFillHexColor() {
        return dynmapFillHexColor;
    }

    @Override
    public void setDynmapFillHexColor(String dynmapFillHexColor) {
        this.dynmapFillHexColor = dynmapFillHexColor;
    }

    @Override
    public double getDynmapFillOpacity() {
        return dynmapFillOpacity;
    }

    @Override
    public void setDynmapFillOpacity(double dynmapFillOpacity) {
        this.dynmapFillOpacity = dynmapFillOpacity;
    }

    @Override
    public boolean isDynmapShowMarker() {
        return dynmapShowMarker;
    }

    @Override
    public void setDynmapShowMarker(boolean dynmapShowMarker) {
        this.dynmapShowMarker = dynmapShowMarker;
    }

    @Override
    public String getDynmapMarkerIcon() {
        return dynmapMarkerIcon;
    }

    @Override
    public void setDynmapMarkerIcon(String dynmapMarkerIcon) {
        this.dynmapMarkerIcon = dynmapMarkerIcon;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(this.baseMaterial); // Base item we will end with
        //item = NBTEditor.set(item, this.Key, "domain", "block");
        ItemMeta meta = item.getItemMeta(); // Get the item meta data
        IbexPersistentData persistentData = new IbexPersistentData(Domain.getInstance());
        persistentData.setString(meta, "domain_block", this.blockKey);
        meta.setDisplayName(ColorFunctions.colorToString(this.displayName)); // Set the name
        List<String> coloredLore = new ArrayList<>();
        for (String l : this.itemLore) {
            coloredLore.add(ColorFunctions.colorToString(l));
        }
        meta.setLore(coloredLore); // Set the List containing our Lore to the ItemMeta object
        item.setItemMeta(meta); // Set the meta data to the item
        return item;
    }

    public Shape remedyShape() {
        Shape shape = new Shape();

        String shapePos1 = " ";
        String shapePos2 = " ";
        String shapePos3 = " ";
        String shapePos4 = " ";
        String shapePos5 = " ";
        String shapePos6 = " ";
        String shapePos7 = " ";
        String shapePos8 = " ";
        String shapePos9 = " ";

        for (Ingredient i : recipeItems())
        {
            switch (i.getLetter())
            {
                case "A":
                    shapePos1 = "A";
                    break;
                case "B":
                    shapePos2 = "B";
                    break;
                case "C":
                    shapePos3 = "C";
                    break;
                case "D":
                    shapePos4 = "D";
                    break;
                case "E":
                    shapePos5 = "E";
                    break;
                case "F":
                    shapePos6 = "F";
                    break;
                case "G":
                    shapePos7 = "G";
                    break;
                case "H":
                    shapePos8 = "H";
                    break;
                case "I":
                    shapePos9 = "I";
                    break;
                default:
                    break;
            }
        }

        shape.setTop(shapePos1 + shapePos2 + shapePos3);
        shape.setMiddle(shapePos4 + shapePos5 + shapePos6);
        shape.setBottom(shapePos7 + shapePos8 + shapePos9);
        return shape;
    }
    public List<Ingredient> recipeItems() {
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(new Ingredient("A", position1));
        ingredientList.add(new Ingredient("B", position2));
        ingredientList.add(new Ingredient("C", position3));
        ingredientList.add(new Ingredient("D", position4));
        ingredientList.add(new Ingredient("E", position5));
        ingredientList.add(new Ingredient("F", position6));
        ingredientList.add(new Ingredient("G", position7));
        ingredientList.add(new Ingredient("H", position8));
        ingredientList.add(new Ingredient("I", position9));
        return ingredientList;
    }
    public static class Ingredient {
        private Material material = Material.AIR;
        private String letter = "";
        public Ingredient(String letter, String materialName) {
            this.letter = letter;
            if (!StringFunctions.isNullOrEmpty(materialName)) {
                Material material = ItemFunctions.materialFromName(materialName);
                if (material != null) {
                    if (material != Material.AIR) {
                        this.material = material;
                    }
                }
            }
        }
        public Material getMaterial() {
            return material;
        }
        public String getLetter() {
            return letter;
        }
    }
    public static class Shape {
        private String top = "";
        private String middle = "";
        private String bottom = "";

        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }

        public String getMiddle() {
            return middle;
        }

        public void setMiddle(String middle) {
            this.middle = middle;
        }

        public String getBottom() {
            return bottom;
        }

        public void setBottom(String bottom) {
            this.bottom = bottom;
        }
    }

}
