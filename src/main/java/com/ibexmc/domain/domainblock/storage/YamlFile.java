package com.ibexmc.domain.domainblock.storage;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.domain.domainblock.vouchers.Voucher;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.internal.functions.NumberFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.messaging.Error;
import com.ibexmc.internal.utility.YmlParser;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlFile implements IDomainStorage {

    private final Domain instance;

    public YamlFile(Domain instance) {
        this.instance = instance;
    }

    /**
     * Checks if the Data folder exists and is accessible
     * Writes a temporary file and removes it again in order to make sure we can write here
     */
    @Override
    public void checkStorage() {
        File testFile = new File(instance.getDataFolder().getAbsolutePath() + File.separator + "Data" + File.separator + "test.txt");

        try {
            if (testFile.createNewFile()) {
                if (testFile.canRead() && testFile.canWrite()) {
                    if (!testFile.delete()) {
                        instance.getError().save(
                                "001",
                                "YamlFile",
                                "checkStorage",
                                "Failed to delete test file",
                                "Unable to delete test file created at " + instance.getDataFolder().getAbsolutePath() + File.separator + "Data" + File.separator + "test.txt",
                                Error.Severity.URGENT,
                                null
                        );
                    }
                } else {
                    instance.getError().save(
                            "002",
                            "YamlFile",
                            "checkStorage",
                            "Cannot read or write to the test file",
                            "Unable to read or write to test file created at " + instance.getDataFolder().getAbsolutePath() + File.separator + "Data" + File.separator + "test.txt",
                            Error.Severity.URGENT,
                            null
                    );
                }
            } else {
                instance.getError().save(
                        "003",
                        "YamlFile",
                        "checkStorage",
                        "Failed to create the test file",
                        "Unable to create test file at " + instance.getDataFolder().getAbsolutePath() + File.separator + "Data" + File.separator + "test.txt",
                        Error.Severity.URGENT,
                        null
                );
            }
        } catch (IOException e) {
            instance.getError().save(
                    "004",
                    "YamlFile",
                    "checkStorage",
                    "Unexpected I/O exception thrown while checking storage",
                    e.getMessage(),
                    Error.Severity.URGENT,
                    e.getStackTrace()
            );
        } catch (Exception ex) {
            instance.getError().save(
                    "005",
                    "YamlFile",
                    "checkStorage",
                    "Unexpected exception thrown while checking storage",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Loops through all .yml files in the Data folder and calls loadDomainField for each
     */
    @Override
    public void loadDomainFields() {
        String currentFile = "Unknown";
        try {
            String filePathString = instance.getDataFolder().getAbsolutePath() + File.separator + "Data";
            File filePath = new File(filePathString);
            if (filePath.exists()) {
                for (File file : new File(filePathString).listFiles()) {
                    currentFile = file.getName();
                    if (file.getName().contains(".yml")) {
                        String fieldIdPart = file.getName().toLowerCase().replace(".yml", "");
                        if (NumberFunctions.isInt(fieldIdPart)) {
                            loadDomainField(NumberFunctions.stringToInt(fieldIdPart));
                        }
                    }
                }
            }
        } catch (NullPointerException npe) {
            instance.getError().save(
                    "001",
                    "YamlFile",
                    "loadDomainFields",
                    "Unable to load all files at file " + currentFile,
                    npe.getMessage(),
                    Error.Severity.WARN,
                    npe.getStackTrace()
            );
        } catch (Exception ex) {
            instance.getError().save(
                    "002",
                    "YamlFile",
                    "loadDomainFields",
                    "Unexpected error getting all files at file " + currentFile,
                    ex.getMessage(),
                    Error.Severity.WARN,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Loads the Domain field from file, if it loads successfully, it will push it to data
     * @param fieldId Domain field identifier
     */
    @Override
    public void loadDomainField(int fieldId) {

        File file = new File(
                instance.getDataFolder() +
                        File.separator + "Data" +  File.separator + fieldId + ".yml"
        );
        if (file.exists()) {
            YmlParser ymlParser = new YmlParser(
                    instance,
                    file,
                    "Domain"
            );

            YmlParser.Output crPlacedID = ymlParser.getInt(
                    "placed_id",
                    -1
            );
            if (crPlacedID.getSuccess()) {
                if (crPlacedID.getInt() < 1) {
                    // error getting placed id
                    return;
                }
            } else {
                // error getting placed id
                return;
            }

            YmlParser.Output crBlockType = ymlParser.getString(
                    "block_type",
                    ""
            );
            if (crBlockType.getSuccess()) {
                IFieldConfig fieldConfig = instance.getData().getDomainFieldConfig(crBlockType.getString());
                if (fieldConfig == null) {
                    // error getting config
                    return;
                }
                DomainBlock domainBlock = new DomainBlock(crPlacedID.getInt(), fieldConfig.getBlockKey());
                YmlParser.Output crFieldName = ymlParser.getString(
                        "field_name",
                        ""
                );
                if (crFieldName.getSuccess()) {
                    domainBlock.setFieldName(crFieldName.getString());
                } else {
                    // Deprecated block_name
                    crFieldName = ymlParser.getString(
                            "block_name",
                            ""
                    );
                    if (crFieldName.getSuccess()) {
                        domainBlock.setFieldName(crFieldName.getString());
                    } else {
                        // Error getting field name
                    }
                }
                YmlParser.Output crWelcome = ymlParser.getString(
                        "welcome_msg",
                        ""
                );
                if (crWelcome.getSuccess()) {
                    domainBlock.setWelcomeMessage(crWelcome.getString());
                } else {
                    domainBlock.setWelcomeMessage("");
                }
                YmlParser.Output crFarewell = ymlParser.getString(
                        "farewell_msg",
                        ""
                );
                if (crFarewell.getSuccess()) {
                    domainBlock.setFarewellMessage(crFarewell.getString());
                } else {
                    domainBlock.setFarewellMessage("");
                }
                YmlParser.Output crLocation = ymlParser.getString(
                        "location",
                        ""
                );
                if (crLocation.getSuccess()) {
                    Location blockLocation = WorldFunctions.deserialize(crLocation.getString());
                    if (blockLocation != null) {
                        domainBlock.setBlockLocation(blockLocation);
                    } else {
                        // error
                        return;
                    }
                    if (blockLocation.getWorld() == null) {
                        instance.getError().save(
                                "DBM_InvalidWorld_blockLocation_001",
                                "DomainBlockManager",
                                "load",
                                "World in Domain Block file is invalid",
                                "File: " + file.getName() + "\n" +
                                        "Provided Location: " + crLocation.getString(),
                                Error.Severity.WARN,
                                null
                        );
                        return;
                    }
                } else {
                    // error
                    return;
                }
                YmlParser.Output crCorner1 = ymlParser.getString(
                        "corner1",
                        ""
                );
                if (crCorner1.getSuccess()) {
                    Location corner1 = WorldFunctions.deserialize(crCorner1.getString());
                    if (corner1 != null) {
                        domainBlock.setCorner1(corner1);
                    } else {
                        // error
                        return;
                    }
                    if (corner1.getWorld() == null) {
                        instance.getError().save(
                                "DBM_InvalidWorld_corner1_001",
                                "DomainBlockManager",
                                "load",
                                "World in Corner 1 in Domain Block file is invalid",
                                "File: " + file.getName() + "\n" +
                                        "Provided Location: " + crLocation.getString(),
                                Error.Severity.WARN,
                                null
                        );
                        return;
                    }
                } else {
                    // error
                    return;
                }
                YmlParser.Output crCorner2 = ymlParser.getString(
                        "corner2",
                        ""
                );
                if (crCorner2.getSuccess()) {
                    Location corner2 = WorldFunctions.deserialize(crCorner2.getString());
                    if (corner2 != null) {
                        domainBlock.setCorner2(corner2);
                    } else {
                        // error
                        return;
                    }
                    if (corner2.getWorld() == null) {
                        instance.getError().save(
                                "DBM_InvalidWorld_corner2_001",
                                "DomainBlockManager",
                                "load",
                                "World in Corner 2 in Domain Block file is invalid",
                                "File: " + file.getName() + "\n" +
                                        "Provided Location: " + crLocation.getString(),
                                Error.Severity.WARN,
                                null
                        );
                        return;
                    }
                } else {
                    // error
                    return;
                }
                domainBlock.setUsedVolume(DomainFunctions.blockCount(domainBlock.getCorner1(), domainBlock.getCorner2()));
                YmlParser.Output crNumMerged = ymlParser.getInt(
                        "num_merged",
                        0
                );
                if (crNumMerged.getSuccess()) {
                    domainBlock.setMergedCount(crNumMerged.getInt());
                } else {
                    domainBlock.setMergedCount(0);
                }
                YmlParser.Output crParent = ymlParser.getInt(
                        "parent",
                        -1
                );
                if (crParent.getSuccess()) {
                    domainBlock.setParentPlacedId(crParent.getInt());
                } else {
                    // Deprecated locked_to
                    crParent = ymlParser.getString(
                            "locked_to",
                            ""
                    );
                    if (crParent.getSuccess()) {
                        if (NumberFunctions.isInt(crParent.getString())) {
                            domainBlock.setParentPlacedId(NumberFunctions.stringToInt(crParent.getString()));
                        } else {
                            domainBlock.setParentPlacedId(-1);
                        }
                    } else {
                        domainBlock.setParentPlacedId(-1);
                    }
                }
                YmlParser.Output crPlaced = ymlParser.getLong(
                        "placed_dtime",
                        0
                );
                if (crPlaced.getSuccess()) {
                    domainBlock.setPlacedLongTimestamp(crPlaced.getLong());
                } else {
                    // error
                }
                YmlParser.Output crPlacedBy = ymlParser.getString(
                        "placed_by",
                        ""
                );
                if (crPlacedBy.getSuccess()) {
                    String placedByUIDString = crPlaced.getString();
                    if (placedByUIDString != null) {
                        UUID placedByUUID = StringFunctions.uuidFromString(placedByUIDString);
                        if (placedByUUID != null) {
                            domainBlock.setPlacedBy(placedByUUID);
                        } else {
                            // invalid placed by
                        }
                    }
                } else {
                    // error
                }
                YmlParser.Output crAdminBlock = ymlParser.getBoolean(
                        "admin_block",
                        false
                );
                if (crAdminBlock.getSuccess()) {
                    domainBlock.setAdminBlock(crAdminBlock.getBoolean());
                } else {
                    domainBlock.setAdminBlock(false);
                }
                if (domainBlock.isAdminBlock()) {
                    domainBlock.setOwner(null);
                } else {
                    // Owner
                    YmlParser.Output crOwner = ymlParser.getString(
                            "owner",
                            ""
                    );
                    if (crOwner.getSuccess()) {
                        UUID ownerUUID = StringFunctions.uuidFromString(crOwner.getString());
                        if (ownerUUID != null) {
                            domainBlock.setOwner(ownerUUID);
                        } else {
                            // error
                        }
                    } else {
                        // error
                    }
                }
                YmlParser.Output crAllowed = ymlParser.getStringList(
                        "allowed"
                );
                if (crAllowed.getSuccess()) {
                    Set<UUID> allowed = new HashSet<>();
                    if (crAllowed.getStringList() != null) {
                        for (String allowedEntry : crAllowed.getStringList()) {
                            UUID allowedUUID = StringFunctions.uuidFromString(allowedEntry);
                            if (allowedUUID != null) {
                                allowed.add(allowedUUID);
                            }
                        }
                    }
                    domainBlock.setAllowedPlayers(allowed);
                }
                YmlParser.Output crAllowedClan = ymlParser.getStringList(
                        "clan_allowed"
                );
                Set<String> allowedClans = new HashSet<>();
                if (crAllowedClan.getSuccess()) {
                    allowedClans = new HashSet<>(crAllowedClan.getStringList());
                }
                domainBlock.setAllowedClans(allowedClans);
                YmlParser.Output crFlagSection = ymlParser.getConfigSection(
                        "flags"
                );
                Set<Flag> flags = new HashSet<>();
                if (crFlagSection.getSuccess()) {
                    Set<String> flagSections;
                    if (crFlagSection.getConfigSection() != null) {
                        flagSections = crFlagSection.getConfigSection().getKeys(false);
                        if (flagSections.size() > 0) {
                            for (String flagString : flagSections) {
                                YmlParser.Output crFlag = ymlParser.getBoolean(
                                        "flags." + flagString,
                                        false
                                );
                                if (crFlag.getSuccess()) {
                                    Flag.Type flagType = Flag.flagFromString(flagString.toUpperCase());
                                    if (flagType != null) {
                                        Flag flag = new Flag(
                                                domainBlock.getPlacedId(),
                                                flagType,
                                                crFlag.getBoolean()
                                        );
                                        flags.add(flag);
                                    } else {
                                        // error
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // error
                }
                domainBlock.setFlags(flags);
                YmlParser.Output crTeleportTo = ymlParser.getString(
                        "teleport_to",
                        ""
                );
                if (crTeleportTo.getSuccess()) {
                    Location teleportTo = WorldFunctions.deserialize(crTeleportTo.getString());
                    if (teleportTo != null) {
                        domainBlock.setTeleportToLocation(teleportTo);
                    } else {
                        // error
                    }
                } else {
                    // error
                }
                YmlParser.Output crMagnet = ymlParser.getString(
                        "magnet",
                        ""
                );
                if (crMagnet.getSuccess()) {
                    Location magnet = WorldFunctions.deserialize(crMagnet.getString());
                    if (magnet != null) {
                        domainBlock.setMagnetLocation(magnet);
                    } else {
                        // error
                    }
                } else {
                    // error
                }
                YmlParser.Output crMembersOnly = ymlParser.getString(
                        "members_only_perm",
                        ""
                );
                if (crMembersOnly.getSuccess()) {
                    domainBlock.setMembersOnlyPermission(crMembersOnly.getString());
                } else {
                    domainBlock.setMembersOnlyPermission("");
                }
                YmlParser.Output crEnterCommands = ymlParser.getStringList(
                        "run_commands.on_enter"
                );
                Set<String> enterCommands = new HashSet<>();
                if (crEnterCommands.getSuccess()) {
                    enterCommands = new HashSet<>(crEnterCommands.getStringList());
                }
                domainBlock.setEnterCommands(enterCommands);
                YmlParser.Output crLeaveCommands = ymlParser.getStringList(
                        "run_commands.on_leave"
                );
                Set<String> leaveCommands = new HashSet<>();
                if (crLeaveCommands.getSuccess()) {
                    leaveCommands = new HashSet<>(crLeaveCommands.getStringList());
                }
                domainBlock.setLeaveCommands(leaveCommands);
                Set<Location> rentSigns = new HashSet<>();
                YmlParser.Output crRentSigns = ymlParser.getStringList(
                        "rent_signs"
                );
                if (crRentSigns.getSuccess()) {
                    for (String rentSignString : crRentSigns.getStringList()) {
                        Location rentSignLocation = WorldFunctions.deserialize(rentSignString);
                        if (rentSignLocation != null) {
                            rentSigns.add(rentSignLocation);
                        }
                    }
                }
                domainBlock.setRentSigns(rentSigns);
                YmlParser.Output crRentDays = ymlParser.getInt(
                        "rent_days",
                        0
                );
                if (crRentDays.getSuccess()) {
                    domainBlock.setRentDays(crRentDays.getInt());
                } else {
                    domainBlock.setRentDays(0);
                }
                YmlParser.Output crRentCost = ymlParser.getDouble(
                        "rent_cost",
                        0
                );
                if (crRentCost.getSuccess()) {
                    domainBlock.setRentCost(crRentCost.getDouble());
                } else {
                    domainBlock.setRentCost(0);
                }
                YmlParser.Output crRenter = ymlParser.getString(
                        "renter",
                        ""
                );
                if (crRenter.getSuccess()) {
                    UUID renterUUID = StringFunctions.uuidFromString(crRenter.getString());
                    if (renterUUID != null) {
                        domainBlock.setRenter(renterUUID);
                        // If renter is not null, then setting field name, welcome/farewell messages, allowed
                        // list or clan allowed list will add to the rent versions, anything above // Renter will
                        // add to initial list as renter is null.
                        //region Rent Until
                        YmlParser.Output crRentUntil = ymlParser.getString(
                                "rent_until",
                                "0"
                        );
                        if (crRentUntil.getSuccess()) {
                            domainBlock.setRentUntilLongTimestamp(NumberFunctions.stringToLong(crRentUntil.getString()));
                        } else {
                            // error
                        }
                        //endregion
                        //region Rent Field Name
                        YmlParser.Output crRentFieldName = ymlParser.getString(
                                "rent_field_name",
                                ""
                        );
                        if (crRentFieldName.getSuccess()) {
                            domainBlock.setFieldName(crRentFieldName.getString());
                        } else {
                            domainBlock.setFieldName(domainBlock.getFieldName());
                        }
                        //endregion
                        //region Rent Welcome Message
                        YmlParser.Output crRentWelcomeMsg = ymlParser.getString(
                                "rent_welcome_msg",
                                ""
                        );
                        if (crRentWelcomeMsg.getSuccess()) {
                            domainBlock.setWelcomeMessage(crRentWelcomeMsg.getString());
                        } else {
                            domainBlock.setWelcomeMessage(domainBlock.getWelcomeMessage());
                        }
                        //endregion
                        //region Rent Farewell Message
                        YmlParser.Output crRentFarewellMsg = ymlParser.getString(
                                "rent_farewell_msg",
                                ""
                        );
                        if (crRentFarewellMsg.getSuccess()) {
                            domainBlock.setFarewellMessage(crRentFarewellMsg.getString());
                        } else {
                            domainBlock.setFarewellMessage(domainBlock.getFarewellMessage());
                        }
                        //endregion
                        //region Rent Allowed
                        YmlParser.Output crRentAllowed = ymlParser.getStringList(
                                "rent_allowed"
                        );
                        if (crRentAllowed.getSuccess()) {
                            Set<UUID> rentAllowed = new HashSet<>();
                            if (crRentAllowed.getStringList() != null) {
                                for (String allowedEntry : crRentAllowed.getStringList()) {
                                    UUID allowedUUID = StringFunctions.uuidFromString(allowedEntry);
                                    if (allowedUUID != null) {
                                        rentAllowed.add(allowedUUID);
                                    }
                                }
                            }
                            domainBlock.setAllowedPlayers(rentAllowed);
                        }
                        //endregion
                        //region Rent Allowed Clans
                        YmlParser.Output crRentAllowedClan = ymlParser.getStringList(
                                "rent_clan_allowed"
                        );
                        Set<String> rentAllowedClans = new HashSet<>();
                        if (crRentAllowedClan.getSuccess()) {
                            rentAllowedClans = new HashSet<>(crRentAllowedClan.getStringList());
                        }
                        domainBlock.setAllowedClans(rentAllowedClans);
                        //endregion
                    } else {
                        // invalid renter
                    }
                } else {
                    // error
                }
                YmlParser.Output crVouchers = ymlParser.getStringList(
                        "vouchers"
                );
                if (crVouchers.getSuccess()) {
                    if (crVouchers.getStringList() != null) {
                        for (String voucherKey : crVouchers.getStringList()) {
                            // No check to see if it is a valid key in case the vouchers
                            // map is not populated, or there was a problem loading a
                            // voucher.
                            domainBlock.addVoucher(voucherKey);
                        }
                    }
                }
                YmlParser.Output crActive = ymlParser.getBoolean(
                        "active",
                        false
                );
                if (crActive.getSuccess()) {
                    domainBlock.setActive(crActive.getBoolean());
                } else {
                    // error
                    domainBlock.setActive(false);
                }
                YmlParser.Output crExpired = ymlParser.getBoolean(
                        "expired",
                        false
                );
                if (crExpired.getSuccess()) {
                    domainBlock.setExpired(crExpired.getBoolean());
                } else {
                    // error
                    domainBlock.setExpired(false);
                }

                domainBlock.setMaxVolume(domainBlock.getConfig().getVolume() * (domainBlock.getMergedCount() + 1));

                for (String voucherKey : domainBlock.getVouchers()) {
                    if (instance.getData().vouchers.containsKey(voucherKey)) {
                        Voucher voucher = instance.getData().vouchers.get(voucherKey);
                        if (voucher != null) {
                            // Valid voucher
                            if (voucher.volume() > 0) {
                                instance.getDebug().log(
                                        "DomainBlockManager",
                                        "load",
                                        "Adding " + voucher.volume() + " to existing volume of " + domainBlock.getMaxVolume()
                                );
                                domainBlock.setMaxVolume(domainBlock.getMaxVolume() + voucher.volume());
                            }
                            if (voucher.flags().size() > 0) {
                                for (Flag.Type flagType : voucher.flags()) {
                                    instance.getDebug().log(
                                            "DomainBlockManager",
                                            "load",
                                            "Adding flag " + flagType + " to flags"
                                    );
                                    domainBlock.addFlag(new Flag(domainBlock.getPlacedId(), flagType, false));
                                }
                            }
                        }
                    }
                }

                // Save the field to data
                instance.getData().placedFields.put(domainBlock.getPlacedId(), domainBlock);

                // Update rent signs
                RentManager rentManager = new RentManager(instance);
                rentManager.updateSigns(domainBlock.getPlacedId());

                if (instance.getDependencies().hasDynmap()) {
                    instance.getDependencies().updateFieldDynmap(domainBlock);
                }
            } else {
                // error getting config
            }
        }

    }

    /**
     * Replaces the existing Domain field with the new one being passed
     * Deletes the existing file and saves it again
     * @param domainBlock Domain field to replace
     */
    @Override
    public void alterField(DomainBlock domainBlock) {
        deleteField(domainBlock.getPlacedId());
        saveField(domainBlock);
    }

    /**
     * Deletes the file in the Data folder for the field provided
     * @param fieldId
     */
    @Override
    public void deleteField(int fieldId) {
        File file = new File(
                instance.getDataFolder() +
                        File.separator + "Data" +  File.separator + fieldId + ".yml"
        );
        if (file.exists()) {
            try {
                file.delete();
            } catch (NullPointerException npe) {
                instance.getError().save(
                        "001",
                        "YamlFile",
                        "deleteField",
                        "Unable to delete " + file.getName(),
                        npe.getMessage(),
                        Error.Severity.WARN,
                        npe.getStackTrace()
                );
            } catch (Exception ex) {
                instance.getError().save(
                        "002",
                        "YamlFile",
                        "deleteField",
                        "Unexpected error trying to delete " + file.getName(),
                        ex.getMessage(),
                        Error.Severity.WARN,
                        ex.getStackTrace()
                );
            }
        }
    }

    /**
     * Saves the file in the Data folder fo rhte field provided
     * @param domainBlock
     */
    @Override
    public void saveField(DomainBlock domainBlock) {
        File dir = new File(instance.getDataFolder() + File.separator + "Data");
        if (!dir.exists()) {
            boolean mk = dir.mkdir();
            if (!mk) {
                instance.getError().save(
                        "001",
                        "DomainBlock",
                        "save",
                        "Unable to save",
                        "Error creating data directory",
                        Error.Severity.CRITICAL,
                        null
                );
            }
        }

        File file = new File(
                instance.getDataFolder() +
                        File.separator + "Data" +
                        File.separator + domainBlock.getPlacedId() + ".yml"
        );
        if (!file.exists()) {
            try {
                boolean c = file.createNewFile();
                if (!c) {
                    instance.getError().save(
                            "002",
                            "YamlFile",
                            "saveField",
                            "Unable to save",
                            "Error creating new file",
                            Error.Severity.CRITICAL,
                            null
                    );
                }
            } catch (IOException e) {
                instance.getError().save(
                        "001",
                        "YamlFile",
                        "saveField",
                        "Unable to save",
                        e.getMessage(),
                        Error.Severity.CRITICAL,
                        e.getStackTrace()
                );
            }
        }

        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

        fileConfig.set("placed_id", domainBlock.getPlacedId());
        fileConfig.set("block_type", domainBlock.getConfig().getBlockKey());
        fileConfig.set("field_name", domainBlock.getFieldName());
        fileConfig.set("block_name", null); // deprecated
        fileConfig.set("welcome_msg", domainBlock.getWelcomeMessage());
        fileConfig.set("farewell_msg", domainBlock.getFarewellMessage());
        fileConfig.set("location", WorldFunctions.serializeSimple(domainBlock.getBlockLocation()));
        fileConfig.set("corner1", WorldFunctions.serializeSimple(domainBlock.getCorner1()));
        fileConfig.set("corner2", WorldFunctions.serializeSimple(domainBlock.getCorner2()));
        fileConfig.set("num_merged", domainBlock.getMergedCount());
        fileConfig.set("parent", domainBlock.getParentPlacedId());
        fileConfig.set("locked_to", null); // deprecated
        fileConfig.set("placed_dtime", domainBlock.getPlacedLongTimestamp());
        if (domainBlock.getPlacedBy() != null) {
            fileConfig.set("placed_by", domainBlock.getPlacedBy().toString());
        } else {
            fileConfig.set("placed_by", null);
        }

        fileConfig.set("admin_block", domainBlock.isAdminBlock());
        if (!domainBlock.isAdminBlock()) {
            fileConfig.set("owner", domainBlock.getOwner().toString());
        } else {
            fileConfig.set("owner", null);
        }
        if (domainBlock.getAllowedPlayers() != null) {
            List<String> allowedList = new ArrayList<>();
            for (UUID allowedUUID : domainBlock.getAllowedPlayers()) {
                allowedList.add(allowedUUID.toString());
            }
            fileConfig.set("allowed", allowedList);
        } else {
            fileConfig.set("allowed", new ArrayList<String>());
        }
        fileConfig.set("allowed_clans", new ArrayList<>(domainBlock.getAllowedClans()));
        fileConfig.set("flags", null);
        for (Flag flag : domainBlock.getFlags()) {
            fileConfig.set("flags." + flag.getFlagType().name(), flag.isActive());
        }
        fileConfig.set("teleport_to", WorldFunctions.serializeSimple(domainBlock.getTeleportToLocation()));
        fileConfig.set("magnet", WorldFunctions.serializeSimple(domainBlock.getMagnetLocation()));
        fileConfig.set("members_only_perm", domainBlock.getMembersOnlyPermission());
        fileConfig.set("run_commands.on_enter", new ArrayList<>(domainBlock.getEnterCommands()));
        fileConfig.set("run_commands.on_leave", new ArrayList<>(domainBlock.getLeaveCommands()));
        List<String> rentSigns = new ArrayList<>();
        for (Location rentSignLocation : domainBlock.getRentSigns()) {
            rentSigns.add(WorldFunctions.serialize(rentSignLocation));
        }
        fileConfig.set("rent_signs", rentSigns);
        fileConfig.set("rent_days", domainBlock.getRentDays());
        fileConfig.set("rent_cost", domainBlock.getRentCost());
        if (domainBlock.getRenter() != null) {
            fileConfig.set("renter", domainBlock.getRenter().toString());
            fileConfig.set("rent_until", domainBlock.getRentUntilLongTimestamp());
            fileConfig.set("rent_field_name", domainBlock.getRentFieldName());
            fileConfig.set("rent_welcome_msg", domainBlock.getRentWelcomeMsg());
            fileConfig.set("rent_farewell_msg", domainBlock.getRentFarewellMsg());
            if (domainBlock.getRentAllowedPlayers() != null) {
                List<String> rentAllowedList = new ArrayList<>();
                for (UUID rentAllowedUUID : domainBlock.getRentAllowedPlayers()) {
                    rentAllowedList.add(rentAllowedUUID.toString());
                }
                fileConfig.set("rent_allowed", rentAllowedList);
            } else {
                fileConfig.set("rent_allowed", new ArrayList<String>());
            }
            fileConfig.set("rent_allowed_clans", new ArrayList<>(domainBlock.getRentAllowedClans()));
        } else {
            fileConfig.set("renter", null);
            fileConfig.set("rent_until", null);
        }
        if (domainBlock.getVouchers() != null) {
            List<String> vouchersList = new ArrayList<>();
            for (String voucherKey : domainBlock.getVouchers()) {
                vouchersList.add(voucherKey.toString());
            }
            fileConfig.set("vouchers", vouchersList);
        } else {
            fileConfig.set("vouchers", new ArrayList<String>());
        }
        fileConfig.set("active", domainBlock.isActive());
        fileConfig.set("expired", domainBlock.isExpired());

        try {
            fileConfig.save(file);
            if (instance.getDependencies().hasDynmap()) {
                instance.getDependencies().updateFieldDynmap(domainBlock);
            }
        } catch (IOException e) {
            instance.getError().save(
                    "001",
                    "YamlFile",
                    "saveField",
                    "File not saved.  Unexpected I/O Error saving field " + domainBlock.getPlacedId(),
                    e.getMessage(),
                    Error.Severity.URGENT,
                    e.getStackTrace()
            );
        } catch (Exception ex) {
            instance.getError().save(
                    "002",
                    "YamlFile",
                    "saveField",
                    "File not saved. Unexpected I/O Error saving field " + domainBlock.getPlacedId(),
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Loops through all data files and deletes them
     */
    @Override
    public void clearDomainData() {
        String currentFile = "Unknown";
        try {
            String filePathString = instance.getDataFolder().getAbsolutePath() + File.separator + "Data";
            File filePath = new File(filePathString);
            if (filePath.exists()) {
                for (File file : new File(filePathString).listFiles()) {
                    currentFile = file.getName();
                    if (file.getName().contains(".yml")) {
                        String fieldIdPart = file.getName().toLowerCase().replace(".yml", "");
                        if (NumberFunctions.isInt(fieldIdPart)) {
                            deleteField(NumberFunctions.stringToInt(fieldIdPart));
                        }
                    }
                }
            }
        } catch (NullPointerException npe) {
            instance.getError().save(
                    "001",
                    "YamlFile",
                    "clearDomainData",
                    "Unable to delete all files at file " + currentFile,
                    npe.getMessage(),
                    Error.Severity.WARN,
                    npe.getStackTrace()
            );
        } catch (Exception ex) {
            instance.getError().save(
                    "002",
                    "YamlFile",
                    "clearDomainData",
                    "Unexpected error deleting all files at file " + currentFile,
                    ex.getMessage(),
                    Error.Severity.WARN,
                    ex.getStackTrace()
            );
        }
    }

}
