package com.ibexmc.domain.domainblock.storage;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.config.IFieldConfig;
import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.domain.domainblock.vouchers.Voucher;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.NumberFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.messaging.Error;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MySQL implements IDomainStorage {

    private final Domain instance;

    public MySQL(Domain instance) {
        this.instance = instance;
    }

    /**
     * Checks if the ibex_plugin table exists, if not, creates it
     */
    @Override
    public void checkStorage() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS `ibex_plugin` (`ibex_id` int NOT NULL AUTO_INCREMENT,`plugin_name` varchar(45) NOT NULL,`key_type` varchar(50) NOT NULL,`ibex_key` varchar(255) NOT NULL,`ibex_value` varchar(255) DEFAULT NULL, PRIMARY KEY (`ibex_id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;";
        instance.getDebug().log(
                "MySQL",
                "checkStorage",
                "Creating ibex_plugin if it does not exist.  SQL: " + createTableSQL
        );
        try {
            CompletableFuture<Integer> createResult = instance.getMySQL().updateAsync(createTableSQL);
            createResult.thenAcceptAsync(intResult -> {
                int result = intResult;
                instance.getDebug().log(
                        "MySQL",
                        "checkStorage",
                        "Creating ibex_plugin if it does not exist.  Result: " + result
                );
            });
        } catch (Exception ex) {
            instance.getError().save(
                    "002",
                    "MySQL",
                    "checkStorage",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Loads a distinct list of domain field id values
     */
    @Override
    public void loadDomainFields() {

        String fieldsSQL = "SELECT DISTINCT '' AS plugin_name, key_type, '' AS ibex_key, '' AS ibex_value FROM ibex_plugin WHERE plugin_name = 'domain';";
        instance.getDebug().log(
                "MySQL",
                "loadDomainFields",
                "Getting list of domain fields.  SQL: " + fieldsSQL
        );
        try {
            CompletableFuture<com.ibexmc.internal.sql.MySQL.ListIbexTable> listResultFuture = instance.getMySQL().getListAsync(fieldsSQL);

            listResultFuture.thenAcceptAsync(listResult -> {
                List<com.ibexmc.internal.sql.MySQL.IbexTable> results = listResult.getResults();
                if (!results.isEmpty()) {
                    for (com.ibexmc.internal.sql.MySQL.IbexTable result : results) {
                        String stringFieldId = result.getKeyType();
                        instance.getDebug().log(
                                "MySQL",
                                "loadDomainFields",
                                "Field found with id: " + stringFieldId
                        );
                        if (!StringFunctions.isNullOrEmpty(stringFieldId)) {
                            if (NumberFunctions.isInt(stringFieldId)) {
                                instance.getDebug().log(
                                        "MySQL",
                                        "loadDomainFields",
                                        "Loading field with id: " + stringFieldId
                                );
                                loadDomainField(NumberFunctions.stringToInt(stringFieldId));
                            }
                        }
                    }
                } else {
                    instance.getDebug().log(
                            "MySQL",
                            "loadDomainFields",
                            "No fields found"
                    );
                }
            });

            try {
                listResultFuture.get();
            } catch (InterruptedException | ExecutionException ex) {
                instance.getError().save(
                        "001",
                        "MySQL",
                        "loadDomainFields",
                        "Unexpected Error",
                        ex.getMessage(),
                        Error.Severity.CRITICAL,
                        ex.getStackTrace()
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "002",
                    "MySQL",
                    "loadDomainFields",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }

    }

    /**
     * Loads the domain field details, if it loads successfully, it will push out to data
     * @param id DomainBlock field identifier
     */
    @Override
    public void loadDomainField(int id) {
        try {
            String fieldDetailSQL = "SELECT ibex_key, ibex_value FROM ibex_plugin WHERE plugin_name = 'domain' AND key_type = '%field_id%'";
            String sqlField = fieldDetailSQL.replace("%field_id%", "" + id);
            instance.getDebug().log(
                    "MySQL",
                    "loadDomainField",
                    "Getting field detail.  SQL: " + sqlField
            );
            CompletableFuture<com.ibexmc.internal.sql.MySQL.ListIbexTableKeyValue> fieldFuture = instance.getMySQL().getListAsyncKeyValue(sqlField);
            fieldFuture.thenAcceptAsync(listResultDetail -> {
                List<com.ibexmc.internal.sql.MySQL.IbexTableKeyValue> shopDetails = listResultDetail.getResults();
                if (!shopDetails.isEmpty()) {

                    DomainBlock domainBlock;

                    String fieldName = "";
                    IFieldConfig config = null;
                    String welcomeMsg = "";
                    String farewellMsg = "";
                    Location blockLocation = null;
                    Location blockCorner1 = null;
                    Location blockCorner2 = null;
                    int numMerged = 0;
                    UUID placedBy = null;
                    long placedDateTime = 0;
                    int parentId = -1;
                    UUID owner = null;
                    Set<Flag> flags = new HashSet<>();
                    Set<UUID> allowed = new HashSet<>();
                    Set<String> clanAllowed = new HashSet<>();
                    Set<Location> rentSigns = new HashSet<>();
                    int rentDays = 0;
                    double rentCost = 0;
                    UUID renter = null;
                    long rentUntil = 0;
                    String rentFieldName = "";
                    String rentWelcomeMsg = "";
                    String rentFarewellMsg = "";
                    Set<UUID> rentAllowed = new HashSet<>();
                    Set<String> rentClanAllowed = new HashSet<>();
                    Set<String> enterCommands = new HashSet<>();
                    Set<String> leaveCommands = new HashSet<>();
                    Location tpTo = null;
                    Location magnet = null;
                    boolean active = true;
                    boolean adminBlock = false;
                    boolean expired = false;
                    String membersOnlyPerm = "";
                    Set<String> vouchers = new HashSet<>();

                    instance.getDebug().log(
                            "MySQL",
                            "loadDomainField",
                            "Looping through field details for id: " + id
                    );
                    for (com.ibexmc.internal.sql.MySQL.IbexTableKeyValue result : shopDetails) {
                        instance.getDebug().log(
                                "MySQL",
                                "loadDomainField",
                                "Key: " + result.getKey() + " Value: " + result.getValue()
                        );
                        switch (result.getKey().toLowerCase()) {
                            case "block_type":
                                IFieldConfig fieldConfig = instance.getData().getDomainFieldConfig(result.getValue());
                                if (fieldConfig == null) {
                                    return;
                                } else {
                                    config = fieldConfig;
                                }
                                break;
                            case "field_name":
                                fieldName = result.getValue();
                                break;
                            case "welcome_message":
                                welcomeMsg = result.getValue();
                                break;
                            case "farewell_message":
                                farewellMsg = result.getValue();
                                break;
                            case "location":
                                blockLocation = WorldFunctions.deserialize(result.getValue());
                                break;
                            case "corner1":
                                blockCorner1 = WorldFunctions.deserialize(result.getValue());
                                break;
                            case "corner2":
                                blockCorner2 = WorldFunctions.deserialize(result.getValue());
                                break;
                            case "num_merged":
                                numMerged = NumberFunctions.stringToInt(result.getValue(), 0);
                                break;
                            case "parent_id":
                                parentId = NumberFunctions.stringToInt(result.getValue(), -1);
                                break;
                            case "placed_datetime":
                                if (NumberFunctions.isLong(result.getValue())) {
                                    placedDateTime = NumberFunctions.stringToLong(result.getValue());
                                } else {
                                    placedDateTime = 0;
                                }
                                break;
                            case "placed_by":
                                placedBy = StringFunctions.uuidFromString(result.getValue());
                                break;
                            case "admin_block":
                                if (result.getValue().equalsIgnoreCase("true")) {
                                    adminBlock = true;
                                } else {
                                    adminBlock = false;
                                }
                                break;
                            case "owner":
                                owner = StringFunctions.uuidFromString(result.getValue());
                                break;
                            case "allowed":
                                UUID allowedPlayer = StringFunctions.uuidFromString(result.getValue());
                                if (allowedPlayer != null) {
                                    allowed.add(allowedPlayer);
                                }
                                break;
                            case "allowed_clans":
                                clanAllowed.add(result.getValue().toUpperCase());
                                break;
                            case "teleport_to":
                                tpTo = WorldFunctions.deserialize(result.getValue());
                                break;
                            case "magnet":
                                magnet = WorldFunctions.deserialize(result.getValue());
                                break;
                            case "members_only_perm":
                                membersOnlyPerm = result.getValue();
                                break;
                            case "run_command_enter":
                                enterCommands.add(result.getValue());
                                break;
                            case "run_command_leave":
                                leaveCommands.add(result.getValue());
                                break;
                            case "renter":
                                renter = StringFunctions.uuidFromString(result.getValue());
                                break;
                            case "rent_until":
                                if (NumberFunctions.isLong(result.getValue())) {
                                    rentUntil = NumberFunctions.stringToLong(result.getValue());
                                } else {
                                 rentUntil = 0;
                                }
                                break;
                            case "rent_field_name":
                                rentFieldName = result.getValue();
                                break;
                            case "rent_welcome_message":
                                rentWelcomeMsg = result.getValue();
                                break;
                            case "rent farewell_message":
                                rentFarewellMsg = result.getValue();
                                break;
                            case "rent_allowed":
                                UUID rentAllowedPlayer = StringFunctions.uuidFromString(result.getValue());
                                if (rentAllowedPlayer != null) {
                                    rentAllowed.add(rentAllowedPlayer);
                                }
                                break;
                            case "rent_allowed_clans":
                                rentClanAllowed.add(result.getValue().toUpperCase());
                                break;
                            case "rent_sign":
                                Location rentSign = WorldFunctions.deserialize(result.getValue());
                                if (rentSign != null) {
                                    rentSigns.add(rentSign);
                                }
                                break;
                            case "rent_days":
                                rentDays = NumberFunctions.stringToInt(result.getValue(), 0);
                                break;
                            case "rent_cost":
                                rentCost = NumberFunctions.stringToDouble(result.getValue(), 0d);
                                break;
                            case "voucher":
                                vouchers.add(result.getValue());
                                break;
                            case "active":
                                if (result.getValue().equalsIgnoreCase("false")) {
                                    active = false;
                                } else {
                                    active = true;
                                }
                                break;
                            case "expired":
                                if (result.getValue().equalsIgnoreCase("true")) {
                                    expired = true;
                                } else {
                                    expired = false;
                                }
                                break;
                            case "flag_on":
                                Flag.Type flagOnType = Flag.flagFromString(result.getValue());
                                if (flagOnType != null) {
                                    Flag flag = new Flag(
                                            id,
                                            flagOnType,
                                            true
                                    );
                                    flags.add(flag);
                                }
                                break;
                            case "flag_off":
                                Flag.Type flagOffType = Flag.flagFromString(result.getValue());
                                if (flagOffType != null) {
                                    Flag flag = new Flag(
                                            id,
                                            flagOffType,
                                            false
                                    );
                                    flags.add(flag);
                                }
                                break;

                        }

                    }
                    instance.getDebug().log(
                            "MySQL",
                            "loadDomainField",
                            "Validating Field details"
                    );
                    if (
                            config == null ||
                                    (!adminBlock && owner == null) ||
                                    blockLocation == null ||
                                    blockCorner1 == null ||
                                    blockCorner2 == null
                    ) {
                        // error
                        instance.getLog().log("MySQL","loadDomainField", "Some field didn't load");
                        instance.getError().save(
                                "001",
                                "MySQL",
                                "loadDomainField",
                                "Validation Error",
                                "Field details invalid",
                                Error.Severity.CRITICAL,
                                null
                        );
                    } else {
                        instance.getDebug().log(
                                "MySQL",
                                "loadDomainField",
                                "Field validated successfully - Creating field"
                        );

                        domainBlock = new DomainBlock(id, config.getBlockKey());

                        domainBlock.setFieldName(fieldName);
                        domainBlock.setWelcomeMessage(welcomeMsg);
                        domainBlock.setFarewellMessage(farewellMsg);
                        domainBlock.setBlockLocation(blockLocation);
                        domainBlock.setCorner1(blockCorner1);
                        domainBlock.setCorner2(blockCorner2);
                        domainBlock.setMergedCount(numMerged);
                        domainBlock.setPlacedBy(placedBy);
                        domainBlock.setPlacedLongTimestamp(placedDateTime);
                        domainBlock.setParentPlacedId(parentId);
                        domainBlock.setOwner(owner);
                        domainBlock.setFlags(flags);
                        domainBlock.setAllowedPlayers(allowed);
                        domainBlock.setAllowedClans(clanAllowed);
                        domainBlock.setRentSigns(rentSigns);
                        domainBlock.setRentDays(rentDays);
                        domainBlock.setRentCost(rentCost);
                        if (renter != null) {
                            domainBlock.setRenter(renter);
                            domainBlock.setRentUntilLongTimestamp(rentUntil);
                            domainBlock.setRentFieldName(rentFieldName);
                            domainBlock.setRentWelcomeMsg(rentWelcomeMsg);
                            domainBlock.setRentFarewellMsg(rentFarewellMsg);
                            domainBlock.setRentAllowedPlayers(rentAllowed);
                            domainBlock.setRentAllowedClans(rentClanAllowed);
                        }
                        domainBlock.setEnterCommands(enterCommands);
                        domainBlock.setLeaveCommands(leaveCommands);
                        if (tpTo != null) {
                            domainBlock.setTeleportToLocation(tpTo);
                        }
                        if (magnet != null) {
                            domainBlock.setMagnetLocation(magnet);
                        }
                        domainBlock.setMembersOnlyPermission(membersOnlyPerm);
                        domainBlock.setVouchers(vouchers);
                        domainBlock.setAdminBlock(adminBlock);
                        domainBlock.setExpired(expired);
                        domainBlock.setActive(active);

                        domainBlock.setMaxVolume(domainBlock.getConfig().getVolume() * (domainBlock.getMergedCount() + 1));

                        for (String voucherKey : domainBlock.getVouchers()) {
                            if (Domain.getInstance().getData().vouchers.containsKey(voucherKey)) {
                                Voucher voucher = Domain.getInstance().getData().vouchers.get(voucherKey);
                                if (voucher != null) {
                                    // Valid voucher
                                    if (voucher.volume() > 0) {
                                        instance.getDebug().log(
                                                "MySQL",
                                                "loadDomainField",
                                                "Adding " + voucher.volume() + " to existing volume of " + domainBlock.getMaxVolume()
                                        );
                                        domainBlock.setMaxVolume(domainBlock.getMaxVolume() + voucher.volume());
                                    }
                                    if (voucher.flags().size() > 0) {
                                        for (Flag.Type flagType : voucher.flags()) {
                                            instance.getDebug().log(
                                                    "MySQL",
                                                    "loadDomainField",
                                                    "Adding flag " + flagType + " to flags"
                                            );
                                            domainBlock.addFlag(new Flag(domainBlock.getPlacedId(), flagType, false));
                                        }
                                    }
                                }
                            }
                        }

                        // Save the field to data
                        Domain.getInstance().getData().placedFields.put(domainBlock.getPlacedId(), domainBlock);

                        // Update rent signs
                        RentManager rentManager = new RentManager(instance);
                        rentManager.updateSigns(domainBlock.getPlacedId());

                        if (Domain.getInstance().getDependencies().hasDynmap()) {
                            Domain.getInstance().getDependencies().updateFieldDynmap(domainBlock);
                        }

                        instance.getDebug().log(
                                "MySQL",
                                "loadDomainField",
                                "Field created"
                        );

                    }
                }
            });

            try {
                fieldFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                instance.getError().save(
                        "002",
                        "MySQL",
                        "loadDomainField",
                        "Unexpected Error",
                        e.getMessage(),
                        Error.Severity.CRITICAL,
                        e.getStackTrace()
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "003",
                    "MySQL",
                    "loadDomainField",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Deletes the specified field from the ibex_plugin table and then resaves it
     * @param domainBlock Domain field to save
     */
    @Override
    public void alterField(DomainBlock domainBlock) {
        String deleteFieldSQL = "DELETE FROM ibex_plugin WHERE plugin_name = 'domain' AND key_type = '%field_id%';";
        try {
            String sqlField = deleteFieldSQL.replace("%field_id%", "" + domainBlock.getPlacedId());
            instance.getDebug().log(
                    "MySQL",
                    "alterField",
                    "Deleting field.  SQL: " + sqlField
            );
            CompletableFuture<Integer> deleteFuture = instance.getMySQL().updateAsync(sqlField);
            deleteFuture.thenAcceptAsync(deleteResultInt -> {
                int deleteResult = deleteResultInt.intValue();
                instance.getDebug().log(
                        "MySQL",
                        "alterField",
                        "Result from deleting field: " + deleteResult
                );
                if (deleteResult < 1) {
                    instance.getDebug().log(
                            "MySQL",
                            "alterField",
                            "No field to delete"
                    );
                } else {
                    instance.getDebug().log(
                            "MySQL",
                            "alterField",
                            "Field deleted, attempting to save it again"
                    );

                }
                saveField(domainBlock);
            });
            try {
                deleteFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                instance.getError().save(
                        "002",
                        "MySQL",
                        "alterField",
                        "Unexpected Error",
                        e.getMessage(),
                        Error.Severity.CRITICAL,
                        e.getStackTrace()
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "003",
                    "MySQL",
                    "alterField",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Deletes the specified field from the ibex_plugin table
     * @param fieldId Domain field id to delete
     */
    @Override
    public void deleteField(int fieldId) {
        String deleteFieldSQL = "DELETE FROM ibex_plugin WHERE plugin_name = 'domain' AND key_type = '%field_id%';";
        try {
            String sqlField = deleteFieldSQL.replace("%field_id%", "" + fieldId);
            instance.getDebug().log(
                    "MySQL",
                    "deleteField",
                    "Deleting field.  SQL: " + sqlField
            );
            CompletableFuture<Integer> deleteFuture = instance.getMySQL().updateAsync(sqlField);
            deleteFuture.thenAcceptAsync(deleteResultInt -> {
                int deleteResult = deleteResultInt.intValue();
                instance.getDebug().log(
                        "MySQL",
                        "deleteField",
                        "Result from deleting field: " + deleteResult
                );
                if (deleteResult < 1) {
                    instance.getDebug().log(
                            "MySQL",
                            "deleteField",
                            "No field to delete"
                    );
                } else {
                    instance.getDebug().log(
                            "MySQL",
                            "deleteField",
                            "Field deleted"
                    );
                }
            });
            try {
                deleteFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                instance.getError().save(
                        "002",
                        "MySQL",
                        "deleteField",
                        "Unexpected Error",
                        e.getMessage(),
                        Error.Severity.CRITICAL,
                        e.getStackTrace()
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "003",
                    "MySQL",
                    "deleteField",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Calls saveFieldDetail for each field key/value
     * @param domainBlock DomainBlock to save
     */
    @Override
    public void saveField(DomainBlock domainBlock) {
        saveFieldDetail(domainBlock, "block_type", domainBlock.getConfig().getBlockKey());
        saveFieldDetail(domainBlock, "field_name", domainBlock.getFieldName());
        saveFieldDetail(domainBlock, "welcome_message", domainBlock.getWelcomeMessage());
        saveFieldDetail(domainBlock, "farewell_message", domainBlock.getFarewellMessage());
        saveFieldDetail(domainBlock, "location", WorldFunctions.serializeSimple(domainBlock.getBlockLocation()));
        saveFieldDetail(domainBlock, "corner1", WorldFunctions.serializeSimple(domainBlock.getCorner1()));
        saveFieldDetail(domainBlock, "corner2", WorldFunctions.serializeSimple(domainBlock.getCorner2()));
        saveFieldDetail(domainBlock, "num_merged", "" + domainBlock.getMergedCount());
        saveFieldDetail(domainBlock, "parent_id", "" + domainBlock.getParentPlacedId());
        saveFieldDetail(domainBlock, "placed_datetime", "" + domainBlock.getPlacedLongTimestamp());
        saveFieldDetail(domainBlock, "placed_by", "" + domainBlock.getPlacedBy());
        saveFieldDetail(domainBlock, "admin_block", "" + domainBlock.isAdminBlock());
        if (!domainBlock.isAdminBlock()) {
            saveFieldDetail(domainBlock, "owner", "" + domainBlock.getOwner());
        }
        for (UUID allowedUUID : domainBlock.getAllowedPlayers()) {
            saveFieldDetail(domainBlock, "allowed", "" + allowedUUID);
        }
        for (String clanTag : domainBlock.getAllowedClans()) {
            saveFieldDetail(domainBlock, "allowed_clans", clanTag);
        }
        saveFieldDetail(domainBlock, "teleport_to", WorldFunctions.serializeSimple(domainBlock.getTeleportToLocation()));
        saveFieldDetail(domainBlock, "magnet", WorldFunctions.serializeSimple(domainBlock.getMagnetLocation()));
        saveFieldDetail(domainBlock, "members_only_perm", domainBlock.getMembersOnlyPermission());
        for (String enterCommand : domainBlock.getEnterCommands()) {
            saveFieldDetail(domainBlock, "run_command_enter", enterCommand);
        }
        for (String leaveCommand : domainBlock.getLeaveCommands()) {
            saveFieldDetail(domainBlock, "run_command_leave", leaveCommand);
        }
        saveFieldDetail(domainBlock, "renter", "" + domainBlock.getRenter());
        if (domainBlock.getRenter() != null) {
            saveFieldDetail(domainBlock, "rent_until", "" + domainBlock.getRentUntilLongTimestamp());
            saveFieldDetail(domainBlock, "rent_field_name", domainBlock.getRentFieldName());
            saveFieldDetail(domainBlock, "rent_welcome_message", domainBlock.getRentWelcomeMsg());
            saveFieldDetail(domainBlock, "rent farewell_message", domainBlock.getRentFarewellMsg());
            for (UUID allowedUUID : domainBlock.getRentAllowedPlayers()) {
                saveFieldDetail(domainBlock, "rent_allowed", "" + allowedUUID);
            }
            for (String clanTag : domainBlock.getRentAllowedClans()) {
                saveFieldDetail(domainBlock, "rent_allowed_clans", clanTag);
            }
            for (Location signLocation : domainBlock.getRentSigns()) {
                saveFieldDetail(domainBlock, "rent_sign", WorldFunctions.serializeSimple(signLocation));
            }
        }
        saveFieldDetail(domainBlock, "rent_days", "" + domainBlock.getRentDays());
        saveFieldDetail(domainBlock, "rent_cost", "" + domainBlock.getRentCost());
        for (String voucher : domainBlock.getVouchers()) {
            saveFieldDetail(domainBlock, "voucher", voucher);
        }
        saveFieldDetail(domainBlock, "expired", "" + domainBlock.isExpired());
        for (Flag flag : domainBlock.getFlags()) {
            if (flag.isActive()) {
                saveFieldDetail(domainBlock, "flag_on", flag.getFlagType().name());
            } else {
                saveFieldDetail(domainBlock, "flag_off", flag.getFlagType().name());
            }
        }
        saveFieldDetail(domainBlock, "active", "" + domainBlock.isActive());
    }

    /**
     * Creates the field in the ibex_plugin table
     * @param domainBlock Domain field to save
     * @param key Key value in ibex_plugin table
     * @param value Value in ibex_plugin table
     */
    private void saveFieldDetail(DomainBlock domainBlock, String key, String value) {
        try {
            String insertFieldSQL = "INSERT INTO ibex_plugin (plugin_name, key_type, ibex_key, ibex_value) VALUES ('domain', '%field_id%', '%key%', '%value%');";
            String sqlField = insertFieldSQL.replace("%field_id%", "" + domainBlock.getPlacedId());
            sqlField = sqlField.replace("%key%", key.replace("'", "''"));
            sqlField = sqlField.replace("%value%", value.replace("'", "''"));

            instance.getDebug().log(
                    "MySQL",
                    "saveFieldDetail",
                    "Saving field detail.  SQL: " + sqlField
            );

            CompletableFuture<Integer> saveFuture = instance.getMySQL().updateAsync(sqlField);
            saveFuture.thenAcceptAsync(createResultInt -> {
                int createResult = createResultInt.intValue();
                if (createResult < 1) {
                    instance.getError().save(
                            "001",
                            "MySQL",
                            "saveFieldDetail",
                            "No rows created",
                            "No rows created",
                            Error.Severity.CRITICAL,
                            null
                    );
                } else {
                    instance.getDebug().log(
                            "MySQL",
                            "saveFieldDetail",
                            "Field detail created"
                    );
                    if (key.equalsIgnoreCase("active")) {
                        instance.getData().placedFields.remove(domainBlock.getPlacedId());
                        instance.getData().placedFields.put(domainBlock.getPlacedId(), domainBlock);
                    }
                }
            });

            try {
                saveFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                instance.getError().save(
                        "002",
                        "MySQL",
                        "saveFieldDetail",
                        "Unexpected Error",
                        e.getMessage(),
                        Error.Severity.CRITICAL,
                        e.getStackTrace()
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "003",
                    "MySQL",
                    "saveFieldDetail",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Clears all Domain data from the ibex_plugin table
     */
    @Override
    public void clearDomainData() {
        try {
            String clearTableSQL = "DELETE FROM ibex_plugin WHERE plugin_name = 'domain';";
            instance.getDebug().log(
                    "MySQL",
                    "clearDomainData",
                    "Clearing Domain data.  SQL: " + clearTableSQL
            );
            CompletableFuture<Integer> clearFuture = instance.getMySQL().updateAsync(clearTableSQL);
            clearFuture.thenAcceptAsync(deleteResultInt -> {
                int deleteResult = deleteResultInt.intValue();
                instance.getDebug().log(
                        "MySQL",
                        "clearDomainData",
                        "Result from clearing Domain data: " + deleteResult
                );
                if (deleteResult < 1) {
                    instance.getDebug().log(
                            "MySQL",
                            "clearDomainData",
                            "No data to clear"
                    );
                } else {
                    instance.getDebug().log(
                            "MySQL",
                            "clearDomainData",
                            "Domain data cleared"
                    );
                }
            });
            try {
                clearFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                instance.getError().save(
                        "002",
                        "MySQL",
                        "clearDomainData",
                        "Unexpected Error",
                        e.getMessage(),
                        Error.Severity.CRITICAL,
                        e.getStackTrace()
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "003",
                    "MySQL",
                    "clearDomainData",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

}
