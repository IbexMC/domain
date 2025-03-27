package com.ibexmc.domain.data;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import com.ibexmc.domain.domainblock.*;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.domainblock.open.IOpenDomainBlock;
import com.ibexmc.domain.gui.GUIOption;
import com.ibexmc.internal.functions.CollectionFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.vouchers.Voucher;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.flag.FlagMenuItem;

public class LiveData {

    private final Domain instance;
    private Map<String, IFieldConfig> domainFieldConfigMap = new HashMap<>();
    private Set<UUID> allowPluginTPPlayers = new HashSet<>();
    private boolean paper = false;
    private HashMap<String, String> locale = new HashMap<>();
    private Set<UUID> bypassPlayers = new HashSet<>();
    private Map<UUID, Integer> particleCycleCount = new HashMap<>();
    private Map<UUID, OpenDomainBlock> openBlock = new HashMap<>();
    private Set<UUID> pressurePlateUser = new HashSet<>();
    private HashMap<String, List<Block>> blockChanges = new HashMap<>();
    private String blockList = "";
    private Map<Integer, Timestamp> convertingZombieVillagers = new HashMap<>();
    private HashMap<Block, Timestamp> arrowBlock = new HashMap<>();
    private HashMap<UUID, Integer> canFly = new HashMap<>();
    private HashMap<UUID, OpenCommand> openCommand = new HashMap<>();
    private HashMap<UUID, OpenField> openConfirmTake = new HashMap<>();
    private HashMap<Integer, DomainBlock> changeOwner = new HashMap<>();
    private HashMap<Timestamp, UUID> notAllowed = new HashMap<>();
    private HashMap<Player, Timestamp> pvpList = new HashMap<>();
    private HashMap<String, ItemStack> availableBlocks = new HashMap<>();
    private Set<Flag> flags = new HashSet<>();
    private Set<UUID> allowed = new HashSet<>();
    private List<CommandsToBlock> commands = new ArrayList<>();
    private HashMap<String, GUIOption> guiOptions = new HashMap<>();
    public Map<UUID, Integer> particleDisplay = new HashMap<>();
    public Set<DomainBlock> inactiveFields = new HashSet<>();
    public Location voidLocation;
    public Set<UUID> voidPlayer = new HashSet<>();
    public Map<String, Voucher> vouchers = new HashMap<>();
    public boolean spawnEventStatus = true;
    public boolean moveEventStatus = true;
    public Map<Integer, DomainBlock> placedFields = new HashMap<>();
    public Map<UUID, Map<Integer, List<Block>>> visualizations = new HashMap<>();
    private Map<UUID, Integer> maskedBlocks = new HashMap<>();
    public Map<UUID, IOpenDomainBlock> openBlocks = new HashMap<>();
    private Map<UUID, Idle> idleMap = new HashMap<>();

    public LiveData(Domain instance) {
        this.instance = instance;
    }



    public void addDomainFieldConfig(IFieldConfig fieldConfig) {
        if (domainFieldConfigMap == null ) {
            this.domainFieldConfigMap = new HashMap<>();
        }
        domainFieldConfigMap.put(fieldConfig.getBlockKey(), fieldConfig);
    }
    public void clearDomainFieldConfig() {
        this.domainFieldConfigMap = new HashMap<>();
    }
    public IFieldConfig getDomainFieldConfig(String blockKey) {
        if (domainFieldConfigMap == null ) {
            this.domainFieldConfigMap = new HashMap<>();
        }
        return this.domainFieldConfigMap.get(blockKey);
    }
    public Set<IFieldConfig> getDomainFieldConfigs() {
        if (domainFieldConfigMap == null ) {
            this.domainFieldConfigMap = new HashMap<>();
        }
        Set<IFieldConfig> fieldConfigs = new HashSet<>();
        for (Map.Entry<String, IFieldConfig> configEntry : this.domainFieldConfigMap.entrySet()) {
            fieldConfigs.add(configEntry.getValue());
        }
        return fieldConfigs;
    }
    public Map<String, IFieldConfig> getDomainFieldConfigMap() {
        return this.domainFieldConfigMap;
    }
    public void addPluginTPPlayer(UUID uuid) {
        if (!allowPluginTPPlayers.contains(uuid)) {
            allowPluginTPPlayers.add(uuid);
            instance.getDebug().log("LiveData", "allowPluginTPPlayers", "Added " + uuid);
            Bukkit.getScheduler().runTaskLaterAsynchronously(Domain.getInstance(), new Runnable() {
                public void run() {
                    if (allowPluginTPPlayers.contains(uuid)) {
                        allowPluginTPPlayers.remove(uuid);
                        instance.getDebug().log("LiveData", "allowPluginTPPlayers", "Removed " + uuid);
                    }
                }
            }, 1 * 20);
        }
    }
    public boolean isAllowPluginTPPlayer(UUID uuid) {
        return allowPluginTPPlayers.contains(uuid);
    }
    public boolean isConvertingZombieVillager(Integer entityID) {
        if (convertingZombieVillagers == null) {
            convertingZombieVillagers = new HashMap<>();
        }
        return convertingZombieVillagers.containsKey(entityID);
    }
    public void addConvertingZombieVillager(int entityID) {
        if (convertingZombieVillagers == null) {
            convertingZombieVillagers = new HashMap<>();
        }
        convertingZombieVillagers.put(entityID, new Timestamp(System.currentTimeMillis()));
    }
    public void clearOldConvertingZombieVillagers() {
        if (convertingZombieVillagers == null) {
            convertingZombieVillagers = new HashMap<>();
        }
        for (Map.Entry<Integer, Timestamp> entry : convertingZombieVillagers.entrySet()) {
            long milliseconds = new Timestamp(System.currentTimeMillis()).getTime() - entry.getValue().getTime();
            int seconds = (int) milliseconds / 1000;
            if (seconds >= 600) // Clear if over 10 minutes
            {
                convertingZombieVillagers.remove(entry.getKey());
            }
        }
    }
    public void removeConvertingZombieVillager(int entityID) {
        if (convertingZombieVillagers == null) {
            convertingZombieVillagers = new HashMap<>();
        }
        if (convertingZombieVillagers.containsKey(entityID)) {
            convertingZombieVillagers.remove(entityID);
        }
    }
    public List<CommandsToBlock> getCommands() {
        return commands;
    }
    public void setCommands(List<CommandsToBlock> commands) {
        this.commands = commands;
    }
    public void addCommandToBlock(CommandsToBlock commandsToBlock) {
        this.commands.add(commandsToBlock);
    }
    public boolean isPaper() {
        return paper;
    }
    public void setPaper(boolean paper) {
        this.paper = paper;
    }

    public GUIOption getGUIOption(String key, Material defaultIcon, String defaultName, String defaultLore, boolean enabled) {
        GUIOption guiOption = new GUIOption(defaultIcon, defaultName, StringFunctions.stringToList(defaultLore), enabled);
        if (this.guiOptions.containsKey(key)) {
            guiOption = this.guiOptions.get(key);
        }
        return guiOption;
    }
    public void putGUIOption(String key, GUIOption guiOption) {
        this.guiOptions.put(key, guiOption);
    }
    private HashMap<String, FlagMenuItem> flagMenuItems = new HashMap<String, FlagMenuItem>();
    public HashMap<String, FlagMenuItem> getFlagMenuItems() {
        return flagMenuItems;
    }
    public TreeMap<String, FlagMenuItem> flagTreeMap() {
        return (TreeMap<String, FlagMenuItem>) CollectionFunctions.convertHashMapToTreeMap(flagMenuItems);
    }

    public void putFlagMenuItem(String flagType, FlagMenuItem flagMenuItem) {
        this.flagMenuItems.put(flagType, flagMenuItem);
    }
    public Set<UUID> getBypassPlayers() {
        return bypassPlayers;
    }
    public void setBypassPlayers(Set<UUID> bypassPlayers) {
        this.bypassPlayers = bypassPlayers;
    }
    public void addBypassPlayer(UUID uuid) {
        if (!bypassPlayers.contains(uuid)) {
            bypassPlayers.add(uuid);
        }
    }
    public void removeBypassPlayer(UUID uuid) {
        if (bypassPlayers.contains(uuid)) {
            bypassPlayers.remove(uuid);
        }
    }
    public boolean hasBypass(UUID uuid) {
        return bypassPlayers.contains(uuid);
    }
    public void clearBypassPlayer() {
        this.bypassPlayers = new HashSet<UUID>();
    }
    public boolean openDomainBlockExists(UUID uuid) {
        if (openBlock.containsKey(uuid)) {
            return true;
        } else {
            return false;
        }
    }
    public OpenDomainBlock openDomainBlockGet(UUID uuid) {
        if (openBlock.containsKey(uuid)) {
            return this.openBlock.get(uuid);
        } else {
            return null;
        }
    }
    public void openDomainBlockPut(Integer placedID, UUID uuid, OpenDomainBlockType openDomainBlockType) {
        OpenDomainBlock openDomainBlock = new OpenDomainBlock(placedID, openDomainBlockType);
        this.openBlock.put(uuid, openDomainBlock);
    }
    public void openDomainBlockPut(Integer placedID, UUID uuid, OpenDomainBlockType openDomainBlockType, UUID newOwnerUUID, String newOwnerName) {
        OpenDomainBlock openDomainBlock = new OpenDomainBlock(placedID, openDomainBlockType);
        openDomainBlock.setNewOwnerUUID(newOwnerUUID);
        openDomainBlock.setNewOwnerName(newOwnerName);
        this.openBlock.put(uuid, openDomainBlock);
    }
    public void openDomainBlockPut(Integer placedID, UUID uuid, OpenDomainBlockType openDomainBlockType, Location corner1, Location corner2) {
        OpenDomainBlock openDomainBlock = new OpenDomainBlock(placedID, openDomainBlockType);
        openDomainBlock.setCorner1(corner1);
        openDomainBlock.setCorner2(corner2);
        this.openBlock.put(uuid, openDomainBlock);
    }
    public void openDomainBlockPut(Integer placedID, UUID uuid, OpenDomainBlockType openDomainBlockType, Location tpTo) {
        OpenDomainBlock openDomainBlock = new OpenDomainBlock(placedID, openDomainBlockType);
        openDomainBlock.setTPTo(tpTo);
        this.openBlock.put(uuid, openDomainBlock);
    }
    public void openDomainBlockRemove(UUID uuid) {
        this.openBlock.remove(uuid);
    }
    public HashMap<Block, Timestamp> getArrowBlock() {
        return this.arrowBlock;
    }
    public void putArrowBlock(Block block, Timestamp timestamp) {
        this.arrowBlock.put(block, timestamp);
    }
    public void removeArrowBlocksByBlock(Block block) {
        if (arrowBlock.containsKey(block)) {
            arrowBlock.remove(block);
        }
    }
    public void putCanFly(UUID uuid, Integer seconds) {
        this.canFly.put(uuid, seconds);
    }
    public boolean containsCanFly(UUID uuid) {
        return this.canFly.containsKey(uuid);
    }
    public Integer getCanFlySeconds(UUID uuid) {
        Integer seconds = 0;
        if (canFly.containsKey(uuid)) {
            seconds = canFly.get(uuid);
        }
        return seconds;
    }
    public void removeCanFlyByUUID(UUID uuid) {
        if (canFly.containsKey(uuid)) {
            canFly.remove(uuid);
        }
    }
    public HashMap<UUID, OpenField> getOpenConfirmTakes() {
        return this.openConfirmTake;
    }
    public void setOpenConfirmTake(HashMap<UUID, OpenField> openConfirmTake) {
        this.openConfirmTake = openConfirmTake;
    }
    public void putOpenConfirmTake(UUID key, OpenField openField) {
        this.openConfirmTake.put(key, openField);
    }
    public OpenField getOpenConfirmTakeValue(UUID key) {
        OpenField value = new OpenField();
        if (openConfirmTake.containsKey(key)) {
            value = openConfirmTake.get(key);
        }
        return value;
    }
    public void removeOpenConfirmTakeByKey(UUID key) {
        if (openConfirmTake.containsKey(key)) {
            openConfirmTake.remove(key);
        }
    }
    public boolean containsOpenConfirmTake(UUID uuid) {
        return this.openConfirmTake.containsKey(uuid);
    }
    public HashMap<Integer, DomainBlock> getChangeOwners() {
        return this.changeOwner;
    }
    public HashMap<Timestamp, UUID> getNotAlloweds() {
        return this.notAllowed;
    }
    public void putNotAllowed(Timestamp key, UUID uuid) {
        this.notAllowed.put(key, uuid);
    }
    public UUID getNotAllowedUUID(Timestamp key) {
        UUID value = UUID.randomUUID();
        if (notAllowed.containsKey(key)) {
            value = notAllowed.get(key);
        }
        return value;
    }
    public void removeNotAllowedByKey(Timestamp key) {
        if (notAllowed.containsKey(key)) {
            notAllowed.remove(key);
        }
    }
    public HashMap<Player, Timestamp> getPVPLists() {
        return this.pvpList;
    }
    public void putPVPList(Player key, Timestamp timestamp) {
        this.pvpList.put(key, timestamp);
    }
    public Timestamp getPVPListTimestamp(Player key) {
        Timestamp value = null;
        if (pvpList.containsKey(key)) {
            value = pvpList.get(key);
        }
        return value;
    }
    public void removePVPListByKey(Player key) {
        if (pvpList.containsKey(key)) {
            pvpList.remove(key);
        }
    }
    public boolean containsPVPList(Player uuid) {
        return this.pvpList.containsKey(uuid);
    }
    public HashMap<String, ItemStack> getAvailableBlocks() {
        return this.availableBlocks;
    }
    public void putAvailableBlocks(String key, ItemStack itemStack) {
        this.availableBlocks.put(key, itemStack);
    }
    public ItemStack getAvailableBlocksItemStack(String key) {
        ItemStack value = new ItemStack(Material.AIR, 1);
        if (availableBlocks.containsKey(key)) {
            value = availableBlocks.get(key);
        }
        return value;
    }
    public boolean containsAvailableBlocks(String key) {
        return this.availableBlocks.containsKey(key);
    }
    public String getBlockList() {
        List<String> names = new ArrayList<>();
        for (IFieldConfig fieldConfig : getDomainFieldConfigs()) {
            names.add(fieldConfig.getBlockKey());
        }
        Collections.sort(names);
        String result = String.join(", ", names);
        return result;
    }

    public void addFlag(Flag f){
        try {
            if (flags == null) {
                flags = new HashSet<>();
            }
            if (f != null)
            {
                flags.add(f);
            }
        } catch (Exception ex) {
            instance.getDebug().log("Domain", "addFlag", "Unexpected Error - " + ex.getMessage());
        }
    }
    public Set<Flag> getFlags() {
        return flags;
    }
    public void clearFlags() {
        flags = new HashSet<Flag>();
    }
    public void addAllowed(UUID a) {
        try {
            if (allowed == null) {
                allowed = new HashSet<>();
            }
            if (a != null)
            {
                allowed.add(a);
            }
        } catch (Exception ex) {
            instance.getDebug().log("Domain", "addAllowed", "Unexpected Error - " + ex.getMessage());
        }
    }
    public Set<UUID> getAllowed() {
        return allowed;
    }
    public void clearAllowed() {
        allowed = new HashSet<>();
    }
    public boolean hasIdle(UUID uuid) {
        return idleMap.containsKey(uuid);
    }
    public Idle getIdle(UUID uuid) {
        return idleMap.get(uuid);
    }
    public void putIdle(UUID uuid, Idle idle) {
        this.idleMap.put(uuid, idle);
    }

    public boolean hasMaskedBlock(UUID uuid) {
        return this.maskedBlocks.containsKey(uuid);
    }
    public int getMaskedBlock(UUID uuid) {
        return this.maskedBlocks.get(uuid);
    }
    public void putMaskedBlock(UUID uuid, int placedId) {
        this.maskedBlocks.put(uuid, placedId);
    }
    public void removeMaskedBlock(UUID uuid) {
        this.maskedBlocks.remove(uuid);
    }
    public void clearMaskedBlocks() {
        this.maskedBlocks = new HashMap<>();
    }

    private Map<String, Set<Material>> materialGroups = new HashMap<>();
    public Set<Material> getMaterialGroup(String key) {
        if (this.materialGroups.containsKey(key)) {
            return this.materialGroups.get(key);
        } else {
            return new HashSet<>();
        }
    }
    public void putMaterialGroup(String key, Set<Material> materials) {
        this.materialGroups.put(key, materials);
    }

    public void clearMaterialGroups() {
        this.materialGroups = new HashMap<>();
    }
}
