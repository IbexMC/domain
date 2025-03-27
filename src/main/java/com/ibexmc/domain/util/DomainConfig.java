package com.ibexmc.domain.util;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.CommandsToBlock;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.FileFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.messaging.Error;
import com.ibexmc.internal.utility.YmlParser;
import org.bukkit.Location;

import java.io.File;

public class DomainConfig {

    Domain instance;
    YmlParser ymlParser;

    private boolean useLegacyConfig = true; // TODO: Make this config driven - default to false

    private boolean debug = false;
    private String displayName = "&f[&6Domain&f]&r";
    private boolean permLimitByBlock = false;
    private boolean removeBypassJoinQuit = false;
    private boolean displayBypassSnitch = false;
    private int pvpGlowTime = 30;
    private int pvpNoTeleportTime = 30;
    private int flyWarnSeconds = 3;
    private boolean expireBlocks = true;
    private boolean useMySQL = false;
    private String mysqlHost = "";
    private int mysqlPort = 0;
    private String mysqlDatabase = "";
    private String mysqlUsername = "";
    private String mysqlPassword = "";
    private boolean mysqlUseSSL = false;
    private String defaultName = "";
    private String defaultWelcome = "";
    private String defaultFarewell = "";
    private int idleCycleSeconds = 20;
    private int idleCyclesBeforeKick = 6;
    private boolean domainShop = true;
    private int maxDays = 30;
    private boolean allowSelfFieldTeleport = false;
    private boolean idleFieldDirectToSpawn = false;
    private int newFieldKickCooldown = 20;
    private int papiUpdateTime = 10;
    private boolean voidCheck = false;
    private String voidLocation = "";
    private boolean dynmapEnabled = false;
    private String dynmapLineHexColor = "8B589C";
    private String dynmapFillHexColor = "F7E6FC";
    private double dynmapFillOpacity = 0.5;
    private boolean dynmapShowMarker = false;
    private String dynmapIcon = "default";
    private int magnetRange = 5;
    private boolean allowBStats = true;

    public DomainConfig(Domain instance) {
        this.instance = instance;
        File configFile = FileFunctions.getConfigYml(instance);
        ymlParser = new YmlParser(instance, configFile, Domain.internalName);
        load();
    }

    public boolean isUseLegacyConfig() {
        return useLegacyConfig;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPermLimitByBlock() {
        return permLimitByBlock;
    }

    public boolean isRemoveBypassJoinQuit() {
        return removeBypassJoinQuit;
    }

    public boolean isDisplayBypassSnitch() {
        return displayBypassSnitch;
    }

    public int getPvpGlowTime() {
        return pvpGlowTime;
    }

    public int getPvpNoTeleportTime() {
        return pvpNoTeleportTime;
    }

    public int getFlyWarnSeconds() {
        return flyWarnSeconds;
    }

    public boolean isExpireBlocks() {
        return expireBlocks;
    }

    public boolean useMySQL() {
        return useMySQL;
    }

    public String getMySQLHost() {
        return mysqlHost;
    }

    public int getMySQLPort() {
        return mysqlPort;
    }

    public String getMySQLDatabase() {
        return mysqlDatabase;
    }

    public String getMySQLUsername() {
        return mysqlUsername;
    }

    public String getMySQLPassword() {
        return mysqlPassword;
    }

    public boolean getMySQLUseSSL() {
        return mysqlUseSSL;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getDefaultWelcome() {
        return defaultWelcome;
    }

    public String getDefaultFarewell() {
        return defaultFarewell;
    }

    public int getIdleCycleSeconds() {
        return idleCycleSeconds;
    }

    public int getIdleCyclesBeforeKick() {
        return idleCyclesBeforeKick;
    }

    public boolean isDomainShop() {
        return domainShop;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public boolean isAllowSelfFieldTeleport() {
        return allowSelfFieldTeleport;
    }

    public boolean isIdleFieldDirectToSpawn() {
        return idleFieldDirectToSpawn;
    }

    public int getNewFieldKickCooldown() {
        return newFieldKickCooldown;
    }

    public int getPapiUpdateTime() {
        return papiUpdateTime;
    }

    public boolean isVoidCheck() {
        return voidCheck;
    }

    public String getVoidLocation() {
        return voidLocation;
    }

    public boolean isDynmapEnabled() {
        return dynmapEnabled;
    }

    public String getDynmapLineHexColor() {
        return dynmapLineHexColor;
    }

    public String getDynmapFillHexColor() {
        return dynmapFillHexColor;
    }

    public double getDynmapFillOpacity() {
        return dynmapFillOpacity;
    }

    public boolean getDynmapShowMarker() {
        return dynmapShowMarker;
    }

    public String getDynmapIcon() {
        return dynmapIcon;
    }

    public int getMagnetRange() {
        return magnetRange;
    }

    public boolean isAllowBStats() {
        return allowBStats;
    }

    public void load() {

        YmlParser.Output crDebug = ymlParser.getBoolean(
                "debug",
                false
        );
        if (crDebug.getSuccess()) {
            this.debug = crDebug.getBoolean();
        }

        YmlParser.Output crDisplayName = ymlParser.getString(
                "display_name",
                ""
        );
        if (crDisplayName.getSuccess()) {
            this.displayName = crDisplayName.getString();
        }

        YmlParser.Output crPermLimitByBlock = ymlParser.getBoolean(
                "perm_limit_by_block",
                false
        );
        if (crPermLimitByBlock.getSuccess()) {
            this.permLimitByBlock = crPermLimitByBlock.getBoolean();
        }

        YmlParser.Output crRemoveBypassJoinQuit = ymlParser.getBoolean(
                "remove_bypass_join_quit",
                false
        );
        if (crRemoveBypassJoinQuit.getSuccess()) {
            this.removeBypassJoinQuit = crRemoveBypassJoinQuit.getBoolean();
        }

        YmlParser.Output crDisplayBypassSnitch = ymlParser.getBoolean(
                "display_bypass_snitch",
                false
        );
        if (crDisplayBypassSnitch.getSuccess()) {
            this.displayBypassSnitch = crDisplayBypassSnitch.getBoolean();
        }

        YmlParser.Output crPvPGlowTime = ymlParser.getInt(
                "pvp_glow_time",
                30
        );
        if (crPvPGlowTime.getSuccess()) {
            this.pvpGlowTime = crPvPGlowTime.getInt();
        }

        YmlParser.Output crPvPNoTeleportTime = ymlParser.getInt(
                "pvp_no_teleport_time",
                30
        );
        if (crPvPNoTeleportTime.getSuccess()) {
            this.pvpNoTeleportTime = crPvPNoTeleportTime.getInt();
        }

        YmlParser.Output crFlyWarnTime = ymlParser.getInt(
                "fly_warn_seconds",
                3
        );
        if (crFlyWarnTime.getSuccess()) {
            this.flyWarnSeconds = crFlyWarnTime.getInt();
        }

        YmlParser.Output crExpireBlocks = ymlParser.getBoolean(
                "expire_blocks",
                false
        );
        if (crExpireBlocks.getSuccess()) {
            this.expireBlocks = crExpireBlocks.getBoolean();
        }

        YmlParser.Output crBlockedHomeCommands = ymlParser.getStringList(
                "blocked_commands.home"
        );
        if (crBlockedHomeCommands.getSuccess()) {
            instance.getData().addCommandToBlock(new CommandsToBlock(Flag.Type.PREVENT_COMMAND_HOME_BY_NONALLOWED, crBlockedHomeCommands.getStringList()));
        }

        YmlParser.Output crBlockedTeleportCommands = ymlParser.getStringList(
                "blocked_commands.teleport"
        );
        if (crBlockedTeleportCommands.getSuccess()) {
            instance.getData().addCommandToBlock(new CommandsToBlock(Flag.Type.PREVENT_COMMAND_TELEPORT_BY_NONALLOWED, crBlockedTeleportCommands.getStringList()));
        }

        YmlParser.Output crBlockedCustom1Commands = ymlParser.getStringList(
                "blocked_commands.custom1"
        );
        if (crBlockedCustom1Commands.getSuccess()) {
            instance.getData().addCommandToBlock(new CommandsToBlock(Flag.Type.PREVENT_COMMAND_CUSTOM1_BY_NONALLOWED, crBlockedCustom1Commands.getStringList()));
        }

        YmlParser.Output crBlockedCustom2Commands = ymlParser.getStringList(
                "blocked_commands.custom2"
        );
        if (crBlockedCustom2Commands.getSuccess()) {
            instance.getData().addCommandToBlock(new CommandsToBlock(Flag.Type.PREVENT_COMMAND_CUSTOM2_BY_NONALLOWED, crBlockedCustom2Commands.getStringList()));
        }

        YmlParser.Output crBlockedCustom3Commands = ymlParser.getStringList(
                "blocked_commands.custom3"
        );
        if (crBlockedCustom3Commands.getSuccess()) {
            instance.getData().addCommandToBlock(new CommandsToBlock(Flag.Type.PREVENT_COMMAND_CUSTOM3_BY_NONALLOWED, crBlockedCustom3Commands.getStringList()));
        }

        YmlParser.Output crBlockedCustom4Commands = ymlParser.getStringList(
                "blocked_commands.custom4"
        );
        if (crBlockedCustom4Commands.getSuccess()) {
            instance.getData().addCommandToBlock(new CommandsToBlock(Flag.Type.PREVENT_COMMAND_CUSTOM4_BY_NONALLOWED, crBlockedCustom4Commands.getStringList()));
        }

        YmlParser.Output crMySQLUse = ymlParser.getBoolean(
                "mysql.use",
                false
        );
        if (crMySQLUse.getSuccess()) {
            this.useMySQL = crMySQLUse.getBoolean();
            YmlParser.Output crMySQLHost = ymlParser.getString(
                    "mysql.host",
                    ""
            );
            if (crMySQLHost.getSuccess()) {
                this.mysqlHost = crMySQLHost.getString();
            }
            YmlParser.Output crMySQLPort = ymlParser.getInt(
                    "mysql.port",
                    3306
            );
            if (crMySQLPort.getSuccess()) {
                this.mysqlPort = crMySQLPort.getInt();
            }
            YmlParser.Output crMySQLDatabase = ymlParser.getString(
                    "mysql.database",
                    ""
            );
            if (crMySQLDatabase.getSuccess()) {
                this.mysqlDatabase = crMySQLDatabase.getString();
            }
            YmlParser.Output crMySQLUsername = ymlParser.getString(
                    "mysql.username",
                    ""
            );
            if (crMySQLUsername.getSuccess()) {
                this.mysqlUsername = crMySQLUsername.getString();
            }
            YmlParser.Output crMySQLPassword = ymlParser.getString(
                    "mysql.password",
                    ""
            );
            if (crMySQLPassword.getSuccess()) {
                this.mysqlPassword = crMySQLPassword.getString();
            }
            YmlParser.Output crMySQLUseSSL = ymlParser.getBoolean(
                    "mysql.use_ssl",
                    false
            );
            if (crMySQLUseSSL.getSuccess()) {
                this.mysqlUseSSL = crMySQLUseSSL.getBoolean();
            }
            if (
                    StringFunctions.isNullOrEmpty(mysqlHost) ||
                            StringFunctions.isNullOrEmpty(mysqlDatabase) ||
                            StringFunctions.isNullOrEmpty(mysqlUsername) ||
                            StringFunctions.isNullOrEmpty(mysqlPassword)
            ) {
                // Unable to use MySQL - invalid config
                this.useMySQL = false;
            }
        }

        YmlParser.Output crDefaultName = ymlParser.getString(
                "default_name",
                ""
        );
        if (crDefaultName.getSuccess()) {
            this.defaultName = crDefaultName.getString();
        }

        YmlParser.Output crDefaultWelcome = ymlParser.getString(
                "default_welcome",
                ""
        );
        if (crDefaultWelcome.getSuccess()) {
            this.defaultWelcome = crDefaultWelcome.getString();
        }

        YmlParser.Output crDefaultFarewell = ymlParser.getString(
                "default_farewell",
                ""
        );
        if (crDefaultFarewell.getSuccess()) {
            this.defaultFarewell = crDefaultFarewell.getString();
        }

        YmlParser.Output crSafeTpToField = ymlParser.getBoolean(
                "allow_self_teleport_to_field",
                false
        );
        if (crSafeTpToField.getSuccess()) {
            this.allowSelfFieldTeleport = crSafeTpToField.getBoolean();
        }

        YmlParser.Output crIdleFieldDirectToSpawn = ymlParser.getBoolean(
                "field_kick_direct_to_spawn",
                false
        );
        if (crIdleFieldDirectToSpawn.getSuccess()) {
            this.idleFieldDirectToSpawn = crIdleFieldDirectToSpawn.getBoolean();
        }

        YmlParser.Output crIdleCycleSeconds = ymlParser.getInt(
                "idle_cycle_seconds",
                20
        );
        if (crIdleCycleSeconds.getSuccess()) {
            this.idleCycleSeconds = crIdleCycleSeconds.getInt();
        }

        YmlParser.Output crIdleCycles = ymlParser.getInt(
                "idle_cycles_before_kick",
                6
        );
        if (crIdleCycles.getSuccess()) {
            this.idleCyclesBeforeKick = crIdleCycles.getInt();
        }

        YmlParser.Output crNewFieldKickCooldown = ymlParser.getInt(
                "new_field_kick_cooldown",
                10
        );
        if (crNewFieldKickCooldown.getSuccess()) {
            this.newFieldKickCooldown = crNewFieldKickCooldown.getInt();
        }

        YmlParser.Output crDomainShop = ymlParser.getBoolean(
                "domain_shop",
                false
        );
        if (crDomainShop.getSuccess()) {
            this.domainShop = crDomainShop.getBoolean();
        }

        YmlParser.Output crPAPIUpdateTime = ymlParser.getInt(
                "papi_update_time",
                10
        );
        if (crPAPIUpdateTime.getSuccess()) {
            this.papiUpdateTime = crPAPIUpdateTime.getInt();
        }


        YmlParser.Output crVoidCheck = ymlParser.getBoolean(
                "void_check",
                false
        );
        if (crVoidCheck.getSuccess()) {
            this.voidCheck = crVoidCheck.getBoolean();
            if (this.voidCheck) {
                YmlParser.Output crVoidLocations = ymlParser.getString(
                        "void_location",
                        ""
                );
                if (crVoidLocations.getSuccess()) {
                    Location voidLocationCheck = WorldFunctions.deserialize(crVoidLocations.getString());
                    if (voidLocationCheck != null) {
                        this.voidLocation = crVoidLocations.getString();
                    }
                }
            }
        }

        try {
            if (instance.getConfig().contains("dynmap.enabled"))
            {
                if (instance.getConfig().isBoolean("dynmap.enabled")) {
                    this.dynmapEnabled = instance.getConfig().getBoolean("dynmap.enabled");
                    if (dynmapEnabled) {
                        if (instance.getConfig().contains("dynmap.line_hex_color")) {
                            if (instance.getConfig().isString("dynmap.line_hex_color")) {
                                this.dynmapLineHexColor = instance.getConfig().getString("dynmap.line_hex_color");
                            }
                        }
                        if (instance.getConfig().contains("dynmap.fill_hex_color")) {
                            if (instance.getConfig().isString("dynmap.fill_hex_color")) {
                                this.dynmapFillHexColor = instance.getConfig().getString("dynmap.fill_hex_color");
                            }
                        }
                        if (instance.getConfig().contains("dynmap.fill_opacity")) {
                            if (instance.getConfig().isDouble("dynmap.fill_opacity")) {
                                this.dynmapFillOpacity = instance.getConfig().getDouble("dynmap.fill_opacity");
                            }
                        }
                        if (instance.getConfig().isBoolean("dynmap.show_marker")) {
                            this.dynmapShowMarker = instance.getConfig().getBoolean("dynmap.show_marker");
                            if (dynmapShowMarker) {
                                if (instance.getConfig().contains("dynmap.marker_icon")) {
                                    if (instance.getConfig().isString("dynmap.marker_icon")) {
                                        this.dynmapIcon = instance.getConfig().getString("dynmap.marker_icon");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    dynmapEnabled = false;
                    instance.getError().save(
                            "DomainConfig.init.dynmapEnabled.001",
                            "DomainConfig",
                            "load().dynmapEnabled",
                            "Unable to enable Dynmap",
                            "dynmap.enabled is not a boolean value",
                            Error.Severity.WARN,
                            null
                    );
                }
            }
        } catch (Exception ex) {
            dynmapEnabled = false;
            instance.getError().save(
                    "DomainConfig.init.dynmapEnabled.001",
                    "DomainConfig",
                    "load().dynmapEnabled",
                    "Unexpected error getting dynmap.enabled",
                    ex.getMessage(),
                    Error.Severity.WARN,
                    ex.getStackTrace()
            );
        }

        YmlParser.Output crMagnetRange = ymlParser.getInt(
                "magnet_range",
                5
        );
        if (crMagnetRange.getSuccess()) {
            this.magnetRange = crMagnetRange.getInt();
        }

        YmlParser.Output crAllowBStats = ymlParser.getBoolean(
                "allow_bstats",
                true
        );
        if (crAllowBStats.getSuccess()) {
            this.allowBStats = crAllowBStats.getBoolean();
        }
    }
}
