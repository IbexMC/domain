package com.ibexmc.domain.domainblock;

import java.util.*;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.dependencies.myriad.Myriad;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DomainBlock implements Comparable<Object> {

    private Domain instance;
    private final int placedId;
    private String fieldName = "";
    private IFieldConfig config;
    private String welcomeMsg = "";
    private String farewellMsg = "";
    private Location blockLocation;
    private Location blockCorner1;
    private Location blockCorner2;
    private int maxVolume = 0;
    private int usedVolume = 0;
    private int numMerged = 0;
    private UUID placedBy;
    private long placedDateTime = 0;
    private int lockedToPlacedID = -1;
    private UUID owner;
    private Set<Flag> flags = new HashSet<>();
    private Set<UUID> allowed = new HashSet<>();
    private Set<String> clanAllowed = new HashSet<>();
    private Set<Location> rentSigns = new HashSet<>();
    private int rentDays = 0;
    private double rentCost = 0;
    private UUID renter;
    private long rentUntil;
    private String rentFieldName = "";
    private String rentWelcomeMsg = "";
    private String rentFarewellMsg = "";
    private Set<UUID> rentAllowed = new HashSet<>();
    private Set<String> rentClanAllowed = new HashSet<>();
    private Set<String> enterCommands = new HashSet<>();
    private Set<String> leaveCommands = new HashSet<>();
    private Location tpTo;
    private Location magnet;
    private boolean active = true;
    private boolean dirty = false;
    private boolean adminBlock = false;
    private boolean expired = false;
    private String membersOnlyPerm = "";
    private Set<String> vouchers = new HashSet<>();

    /**
     * Constructor
     * @param configKey Block Configuration key
     */
    public DomainBlock(String configKey) {
        DomainBlockManager domainBlockManager = new DomainBlockManager(Domain.getInstance());
        this.placedId = domainBlockManager.getNextID();
        IFieldConfig blockConfig = Domain.getInstance().getData().getDomainFieldConfig(configKey);
        if (blockConfig != null) {
            this.config = blockConfig;
        }
        instance = Domain.getInstance();
    }

    /**
     * Constructor
     * @param placedId Placed Id
     * @param configKey Block Configuration key
     */
    public DomainBlock(int placedId, String configKey) {
        this.placedId = placedId;
        IFieldConfig blockConfig = Domain.getInstance().getData().getDomainFieldConfig(configKey);
        if (blockConfig != null) {
            this.config = blockConfig;
        }
    }

    /**
     * Gets the Placed Block Identifier
     * @return Placed Id
     */
    public int getPlacedId() {
        return this.placedId;
    }

    /**
     * Gets the field name
     * @return Field name
     */
    public String getFieldName() {
        if (this.renter == null) {
            return this.fieldName;
        } else {
            return this.rentFieldName;
        }
    }

    /**
     * Sets the field name
     * @param fieldName Field name
     */
    public void setFieldName(String fieldName) {
        if (this.renter == null) {
            this.fieldName = fieldName;
        } else {
            this.rentFieldName = fieldName;
        }
    }

    /**
     * Gets the block config
     * @return Block Config
     */
    public IFieldConfig getConfig() {
        return this.config;
    }

    /**
     * Gets the welcome message
     * @return Welcome message
     */
    public String getWelcomeMessage() {
        if (renter == null) {
            return this.welcomeMsg;
        } else {
            return this.rentWelcomeMsg;
        }
    }

    /**
     * Sets the welcome message
     * @param message Welcome message
     */
    public void setWelcomeMessage(String message) {
        if (this.renter == null) {
            this.welcomeMsg = message;
        } else {
            this.rentWelcomeMsg = message;
        }
    }

    /**
     * Gets the farewell message
     * @return Farewell message
     */
    public String getFarewellMessage() {
        if (this.renter == null) {
            return this.farewellMsg;
        } else {
            return this.rentFarewellMsg;
        }
    }

    /**
     * Sets the farewell message
     * @param message Farewell message
     */
    public void setFarewellMessage(String message) {
        if (this.renter == null) {
            this.farewellMsg = message;
        } else {
            this.rentFarewellMsg = message;
        }
    }

    /**
     * Gets the block location
     * @return Block Location
     */
    public Location getBlockLocation() {
        return this.blockLocation;
    }

    /**
     * Sets the block location from a serialized string
     * @param serializedLocation Serialized location string
     */
    public void setBlockLocation(String serializedLocation) {
        this.blockLocation = WorldFunctions.deserialize(serializedLocation);
    }

    /**
     * Sets the block location
     * @param location Block Location
     */
    public void setBlockLocation(Location location) {
        this.blockLocation = location.clone();
    }

    /**
     * Gets the first corner location
     * @return First Corner Location
     */
    public Location getCorner1() {
        return this.blockCorner1;
    }

    /**
     * Sets the first corner location from a serialized string
     * @param serializedLocation Serialized location string
     */
    public void setCorner1(String serializedLocation) {
        this.blockCorner1 = WorldFunctions.deserialize(serializedLocation);
    }

    /**
     * Sets the first corner location
     * @param location First corner
     */
    public void setCorner1(Location location) {
        this.blockCorner1 = location.clone();
    }

    /**
     * Gets the second corner location
     * @return Second Corner Location
     */
    public Location getCorner2() {
        return this.blockCorner2;
    }

    /**
     * Sets the second corner location from a serialized string
     * @param serializedLocation Serialized location string
     */
    public void setCorner2(String serializedLocation) {
        this.blockCorner2 = WorldFunctions.deserialize(serializedLocation);
    }

    /**
     * Sets the second corner location
     * @param location Second corner
     */
    public void setCorner2(Location location) {
        this.blockCorner2 = location.clone();
    }

    /**
     * Gets the used volume
     * @return Used Volume
     */
    public int getUsedVolume() {
        if (usedVolume == 0) {
            setUsedVolume(DomainFunctions.blockCount(getCorner1(), getCorner2()));
        }
        return this.usedVolume;
    }

    /**
     * Sets the used volume
     * @param volume Volume
     */
    public void setUsedVolume(int volume) {
        this.usedVolume = volume;
    }

    /**
     * Gets the maximum volume
     * @return Volume
     */
    public int getMaxVolume() {
        //return this.maxVolume;
        return this.getConfig().getVolume() * (this.getMergedCount() + 1);
    }

    /**
     * Sets the max volume
     * @param volume Volume to set
     */
    public void setMaxVolume(int volume) {
        this.maxVolume = volume;
    }

    /**
     * Gets the number of blocks currently merged
     * @return Number of merged blocks
     */
    public int getMergedCount() {
        return this.numMerged;
    }

    /**
     * Sets the number of blocks currently merged
     * @param count Number of merged blocks
     */
    public void setMergedCount(int count) {
        this.numMerged = count;
    }

    /**
     * Gets the Placed Id for the parent block
     * @return Parent Placed Id
     */
    public int getParentPlacedId() {
        return this.lockedToPlacedID;
    }

    /**
     * Sets the Placed Id for the parent block
     * @param placedId Parent Placed Id
     */
    public void setParentPlacedId(int placedId) {
        this.lockedToPlacedID = placedId;
    }

    /**
     * Gets the player identifier that placed the field
     * @return Player Unique Identifier
     */
    public UUID getPlacedBy() {
        return this.placedBy;
    }

    /**
     * Sets the player identifier that placed the field
     * @param uuid Player Unique Identifier
     */
    public void setPlacedBy(UUID uuid) {
        this.placedBy = uuid;
    }

    /**
     * Gets the timestamp when field was placed as a long
     * @return Long timestamp
     */
    public long getPlacedLongTimestamp() {
        return this.placedDateTime;
    }

    /**
     * Sets the timestamp when the field was placed, as a long
     * @param placed Long timestamp
     */
    public void setPlacedLongTimestamp(long placed) {
        this.placedDateTime = placed;
    }

    /**
     * Gets the player identifier that owns the field
     * @return Owner Player Unique Identifier
     */
    public UUID getOwner() {
        return this.owner;
    }

    /**
     * Sets the player identifier that owns the field
     * @param uuid Owner Player Unique Identifier
     */
    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    /**
     * Gets a hashset of Flags on this field
     * @return Flags
     */
    public Set<Flag> getFlags() {
        return this.flags;
    }

    /**
     * Sets the Flags on this field
     * @param flags Flags
     */
    public void setFlags(Set<Flag> flags) {
        this.flags = flags;
    }

    /**
     * Adds a Flag to the field
     * @param flag Flag to add
     */
    public void addFlag(Flag flag) {
        if (this.flags == null) {
            flags = new HashSet<>();
        }
        for (Flag flagEntry : flags) {
            if (flagEntry.getFlagType() == flag.getFlagType()) {
                return;
            }
        }
        flags.add(flag);
    }

    /**
     * Removes a Flag from the field
     * @param flag Flag to remove
     */
    public void removeFlag(Flag flag) {
        Set<Flag> newFlags = new HashSet<>();
        if (this.flags != null) {
            for (Flag flagEntry : flags) {
                if (flagEntry.getFlagType() != flag.getFlagType()) {
                    newFlags.add(flagEntry);
                }
            }
        }
        this.flags = newFlags;
    }

    /**
     * Removes a Flag from the field
     * @param flagType Flag Type to remove
     */
    public void removeFlag(Flag.Type flagType) {
        Set<Flag> newFlags = new HashSet<>();
        if (this.flags != null) {
            for (Flag flagEntry : flags) {
                if (flagEntry.getFlagType() != flagType) {
                    newFlags.add(flagEntry);
                }
            }
        }
        this.flags = newFlags;
    }

    /**
     * Gets a hashset of Player Unique Identifiers that are allowed in this field
     * @return Allowed Player Unique Identifiers
     */
    public Set<UUID> getAllowedPlayers() {
        if (renter == null) {
            return this.allowed;
        } else {
            return this.rentAllowed;
        }
    }

    /**
     * Sets the players that are allowed in this field
     * @param allowed Allowed Player Unique Identifiers
     */
    public void setAllowedPlayers(Set<UUID> allowed) {
        if (this.renter == null) {
            this.allowed = allowed;
        } else {
            this.rentAllowed = allowed;
        }
    }

    /**
     * Adds a player to the allowed list
     * @param uuid Player Unique Identifier to add
     */
    public void addAllowedPlayer(UUID uuid) {
        if (this.renter == null) {
            if (this.allowed == null) {
                allowed = new HashSet<>();
            }
            allowed.add(uuid);
        } else {
            if (this.rentAllowed == null) {
                rentAllowed = new HashSet<>();
            }
            rentAllowed.add(uuid);
        }
    }

    /**
     * Removes a player from the allowed list
     * @param uuid Player Unique Identifier to remove
     */
    public void removeAllowedPlayer(UUID uuid) {
        if (this.renter == null) {
            if (this.allowed == null) {
                allowed = new HashSet<>();
            }
            this.allowed.remove(uuid);
        } else {
            if (this.rentAllowed == null) {
                rentAllowed = new HashSet<>();
            }
            this.rentAllowed.remove(uuid);
        }
    }

    /**
     * Gets a HashSet of clan tags that are allowed in the field
     * @return Clan Tags
     */
    public Set<String> getAllowedClans() {
        if (this.renter == null) {
            return this.clanAllowed;
        } else {
            return this.rentClanAllowed;
        }
    }

    /**
     * Sets the clan tags that are allowed in the field
     * @param clanAllowed Clan Tags
     */
    public void setAllowedClans(Set<String> clanAllowed) {
        if (this.renter == null) {
            this.clanAllowed = clanAllowed;
        } else {
            this.rentClanAllowed = clanAllowed;
        }
    }

    /**
     * Adds a clan to the allowed list
     * @param clanTag Clan Tag to add
     */
    public void addAllowedClan(String clanTag) {
        if (this.renter == null) {
            if (this.clanAllowed == null) {
                clanAllowed = new HashSet<>();
            }
            clanAllowed.add(clanTag);
        } else {
            if (this.rentClanAllowed == null) {
                rentClanAllowed = new HashSet<>();
            }
            rentClanAllowed.add(clanTag);
        }
    }

    /**
     * Removes a clan from the allowed list
     * @param clanTag Clan Tag to remove
     */
    public void removeAllowedClan(String clanTag) {
        if (this.renter == null) {
            if (this.clanAllowed == null) {
                clanAllowed = new HashSet<>();
            }
            this.clanAllowed.remove(clanTag);
        } else {
            if (this.rentClanAllowed == null) {
                rentClanAllowed = new HashSet<>();
            }
            this.rentClanAllowed.remove(clanTag);
        }
    }

    /**
     * Gets a HashSet of locations where rent signs are
     * @return Rent Sign Locations
     */
    public Set<Location> getRentSigns() {
        return this.rentSigns;
    }

    /**
     * Sets the rent sign locations
     * @param locations Rent Sign Locations
     */
    public void setRentSigns(Set<Location> locations) {
        this.rentSigns = locations;
    }

    /**
     * Adds a rent sign Location
     * @param location Location to add
     */
    public void addRentSign(Location location) {
        if (this.rentSigns == null) {
            this.rentSigns = new HashSet<>();
        }
        this.rentSigns.add(location.clone());
    }

    /**
     * Removes a rent sign location
     * @param location Location to remove
     */
    public void removeRentSign(Location location) {
        Set<Location> newRentSigns = new HashSet<>();
        if (this.rentSigns != null) {
            for (Location signLocation : this.rentSigns) {
                if (!WorldFunctions.match(signLocation, location, false)) {
                    newRentSigns.add(signLocation);
                }
            }
        }
        this.rentSigns = newRentSigns;
    }

    /**
     * Gets the number of days the field is rented for at a time
     * @return Rent Days
     */
    public int getRentDays() {
        return this.rentDays;
    }

    /**
     * Sets the number of days the field is rented for
     * @param rentDays Rent Days
     */
    public void setRentDays(int rentDays) {
        this.rentDays = rentDays;
    }

    /**
     * Gets the cost to rent the field
     * @return Rent Cost
     */
    public double getRentCost() {
        return this.rentCost;
    }

    /**
     * Sets the cost to rent the field
     * @param rentCost Rent Cost
     */
    public void setRentCost(double rentCost) {
        this.rentCost = rentCost;
    }

    /**
     * Gets the current renter
     * @return Renter
     */
    public UUID getRenter() {
        return this.renter;
    }

    /**
     * Sets the current renter
     * @param renter Renter
     */
    public void setRenter(UUID renter) {
        this.renter = renter;
    }

    /**
     * Gets the date and time current rent ends
     * @return Rent end timestamp
     */
    public long getRentUntilLongTimestamp() {
        return this.rentUntil;
    }

    /**
     * Sets the date and time the current rent ends
     * @param rentUntil Rent end timestamp
     */
    public void setRentUntilLongTimestamp(long rentUntil) {
        this.rentUntil = rentUntil;
    }

    /**
     * Gets the field name when rented
     * @return Field Name
     */
    public String getRentFieldName() {
        return rentFieldName;
    }

    /**
     * Sets the rented field name
     * @param rentFieldName Field Name
     */
    public void setRentFieldName(String rentFieldName) {
        this.rentFieldName = rentFieldName;
    }

    /**
     * Gets the welcome message when rented
     * @return Welcome message
     */
    public String getRentWelcomeMsg() {
        return rentWelcomeMsg;
    }

    /**
     * Sets the rented welcome message
     * @param rentWelcomeMsg Welcome message
     */
    public void setRentWelcomeMsg(String rentWelcomeMsg) {
        this.rentWelcomeMsg = rentWelcomeMsg;
    }

    /**
     * Gets the farewell message when rented
     * @return Farewell message
     */
    public String getRentFarewellMsg() {
        return rentFarewellMsg;
    }

    /**
     * Sets the rented farewell message
     * @param rentFarewellMsg Farewell message
     */
    public void setRentFarewellMsg(String rentFarewellMsg) {
        this.rentFarewellMsg = rentFarewellMsg;
    }

    /**
     * Gets the allowed players when rented
     * @return Allowed player unique identifiers
     */
    public Set<UUID> getRentAllowedPlayers() {
        return rentAllowed;
    }

    /**
     * Sets the allowed players when rented
     * @param rentAllowed Allowed player unique identifiers
     */
    public void setRentAllowedPlayers(Set<UUID> rentAllowed) {
        this.rentAllowed = rentAllowed;
    }

    /**
     * Gets the allowed clans when rented
     * @return Allowed clans
     */
    public Set<String> getRentAllowedClans() {
        return rentClanAllowed;
    }

    /**
     * Sets the allowed clans when rented
     * @param rentClanAllowed Allowed clans
     */
    public void setRentAllowedClans(Set<String> rentClanAllowed) {
        this.rentClanAllowed = rentClanAllowed;
    }

    /**
     * Gets a HashSet of commands to run when a player enters the field
     * @return Commands
     */
    public Set<String> getEnterCommands() {
        return this.enterCommands;
    }

    /**
     * Sets the commands to run when a player enters the field
     * @param commands Commands
     */
    public void setEnterCommands(Set<String> commands) {
        this.enterCommands = commands;
    }

    /**
     * Adds a command to run when a player enters the field
     * @param command Command to add
     */
    public void addEnterCommand(String command) {
        if (this.enterCommands == null) {
            this.enterCommands = new HashSet<>();
        }
        this.enterCommands.add(command);
    }

    /**
     * Removes a command from the commands that run when a player enters the field
     * @param command Command to remove
     */
    public void removeEnterCommand(String command) {
        if (this.enterCommands == null) {
            this.enterCommands = new HashSet<>();
        }
        this.enterCommands.remove(command);
    }

    /**
     * Clears the list of commands that run when a player enters the field
     */
    public void clearEnterCommands() {
        this.enterCommands = new HashSet<>();
    }

    /**
     * Gets a HashSet of commands to run when a player leaves the field
     * @return Commands
     */
    public Set<String> getLeaveCommands() {
        return this.leaveCommands;
    }

    /**
     * Sets the commands to run when a player leaves the field
     * @param commands Commands
     */
    public void setLeaveCommands(Set<String> commands) {
        this.leaveCommands = commands;
    }

    /**
     * Adds a command to run when a player leaves the field
     * @param command Command to add
     */
    public void addLeaveCommand(String command) {
        if (this.leaveCommands == null) {
            this.leaveCommands = new HashSet<>();
        }
        this.leaveCommands.add(command);
    }

    /**
     * Removes a command from the commands that run when a player leaves the field
     * @param command Command to remove
     */
    public void removeLeaveCommand(String command) {
        if (this.leaveCommands == null) {
            this.leaveCommands = new HashSet<>();
        }
        this.leaveCommands.remove(command);
    }

    /**
     * Clears the list of commands that run when a player leaves the field
     */
    public void clearLeaveCommands() {
        this.leaveCommands = new HashSet<>();
    }

    /**
     * Gets the location to teleport to when the player enters the field
     * @return Teleport Location
     */
    public Location getTeleportToLocation() {
        return this.tpTo;
    }

    /**
     * Sets the location to teleport to when the player enters the field
     * @param location Teleport Location
     */
    public void setTeleportToLocation(Location location) {
        if (location != null) {
            this.tpTo = location.clone();
        }
    }

    /**
     * Gets the location to drag entities to when magnet features are enabled
     * @return Magnet location
     */
    public Location getMagnetLocation() {
        return magnet;
    }

    /**
     * Sets the location to drag entities to when magnet features are enabled
     * @param magnet Magnet location
     */
    public void setMagnetLocation(Location magnet) {
        if (magnet != null) {
            this.magnet = magnet.clone();
        }
    }

    /**
     * Returns true if the field is active
     * @return Active Status
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets the active status for the field
     * @param active Active Status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns true if the field is dirty (unsaved changed)
     * @return Dirty Status
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * Sets the dirty status of the field
     * @param dirty Dirty Status
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Returns true if this is an Admin block
     * @return Admin Block
     */
    public boolean isAdminBlock() {
        return this.adminBlock;
    }

    /**
     * Sets the Admin block status
     * @param adminBlock Admin Block
     */
    public void setAdminBlock(boolean adminBlock) {
        this.adminBlock = adminBlock;
        if (this.adminBlock) {
            this.owner = null;
        }
    }

    /**
     * Returns true if the field is expired
     * @return Expiration Status
     */
    public boolean isExpired() {
        return this.expired;
    }

    /**
     * Sets the field expiration status
     * @param expired Expiration Status
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * Gets the permission required to enter the field if members-only
     * @return Permission
     */
    public String getMembersOnlyPermission() {
        return this.membersOnlyPerm;
    }

    /**
     * Sets the permission required to enter the field if members-only
     * @param permission Permission
     */
    public void setMembersOnlyPermission(String permission) {
        this.membersOnlyPerm = permission;
    }

    /**
     * Gets the list of vouchers assigned to this field
     * @return List of voucher keys
     */
    public Set<String> getVouchers() {
        return this.vouchers;
    }

    public void setVouchers(Set<String> vouchers) {
        this.vouchers = vouchers;
    }
    /**
     * Adds a voucher to the field
     * @param key Voucher Key
     */
    public void addVoucher(String key) {
        this.vouchers.add(key);
    }

    //TODO: Move this to DomainBlockManager
    public List<InField> isInField() {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        List<InField> lstInField = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (domainBlockManager.inField(player.getLocation(), this)) {
                boolean allowed = false;
                if (domainBlockManager.isAllowedToField(player, this)) {
                    allowed = true;
                }
                lstInField.add(new InField(player, allowed));
            }
        }
        return lstInField;
    }


    /**
     * Checks if the specified player is allowed in this field
     * @param player Player to check
     * @return If true, player has access
     */
    public boolean hasAccess(Player player) {
        if (PlayerFunctions.hasBypass(player)) {
            return true;
        }
        if (this.owner != null) {
            if (this.owner.equals(player.getUniqueId())) {
                return true;
            }
        }
        if (this.renter != null) {
            if (this.renter.equals(player.getUniqueId())) {
                return true;
            }
        }
        if (this.allowed != null) {
            for (UUID uuid : this.allowed) {
                if (uuid.equals(player.getUniqueId())) {
                    return true;
                }
            }
        }
        if (Domain.getInstance().getDependencies().hasMyriadClans()) {
            Myriad myriad = new Myriad(instance);
            String playerClanTag = myriad.playerClanTag(player.getUniqueId());
            if (!StringFunctions.isNullOrEmpty(playerClanTag)) {
                if (this.clanAllowed != null) {
                    for (String tag : this.clanAllowed) {
                        if (tag.equalsIgnoreCase(playerClanTag)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Provides a comparator for sorting purposes
     * @param o Comparator Object
     * @return Comparator integer
     */
    @Override
    public int compareTo(@NotNull Object o) {
        return Integer.toString(this.config.getFieldOrder()).compareTo(Integer.toString(((DomainBlock) o).config.getFieldOrder()));
    }



    public class InField {
        private Player player;
        private boolean allowed = false;

        public InField(Player player, boolean allowed) {
            this.player = player;
            this.allowed = allowed;
        }

        public Player getPlayer() {
            return this.player;
        }
        public boolean isAllowed() {
            return this.allowed;
        }

    }
}