package com.ibexmc.domain;

import java.io.File;

import com.ibexmc.domain.dependencies.Dependencies;
import com.ibexmc.domain.domainblock.config.ConfigManager;
import com.ibexmc.domain.domainblock.storage.StorageManager;
import com.ibexmc.domain.events.bukkit.*;
import com.ibexmc.domain.events.paper.PaperEvent;
import com.ibexmc.domain.flag.FlagManager;
import com.ibexmc.domain.gui.GUIOptions;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.data.LiveData;
import com.ibexmc.domain.scheduled.*;
import com.ibexmc.domain.util.DomainConfig;
import com.ibexmc.internal.Internal;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.messaging.Debug;
import com.ibexmc.internal.messaging.Error;
import com.ibexmc.internal.messaging.Locale;
import com.ibexmc.internal.messaging.Log;
import com.ibexmc.internal.messaging.Message;
import com.ibexmc.internal.sql.MySQL;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.ibexmc.domain.api.API;
import com.ibexmc.domain.data.InstanceData;
import com.ibexmc.domain.domainblock.vouchers.VoucherManager;
import com.ibexmc.domain.commands.DomainCommand;
import com.ibexmc.domain.util.functions.GameFunctions;

public class Domain extends JavaPlugin {

    private static Domain instance;
    public static final String loggingName = "Domain";
    public static final String internalName = "domain";
    private boolean cleanup = true;
    private DomainConfig config;
    private API api;
    private IbexPersistentData persistentData = new IbexPersistentData(this);
    private final LiveData liveData = new LiveData(this);
    private InstanceData gameData = new InstanceData();
    private Dependencies dependencies;
    private BossBar bar = Bukkit.createBossBar("Domain Bypass Enabled", BarColor.GREEN, BarStyle.SOLID);
    private DomainFunctions domainFunctions;
    private com.ibexmc.internal.data.Data internalData;
    private MySQL mysql = null;
    private Internal ibexInternal = null;
    private Locale locale = null;
    private Log log;
    private Debug debug;
    private Error error;
    private Message message;

    public com.ibexmc.internal.data.Data getInternalData() {
        return this.internalData;
    }
    public DomainFunctions getDomainFunctions() {
        return domainFunctions;
    }
    public MySQL getMySQL() { return mysql; }
    public void setMySQL(MySQL mySQL) { this.mysql = mySQL; }
    public Internal getIbexInternal() { return this.ibexInternal; }
    public Locale getLocale() { return locale; }
    public Log getLog() { return this.log; }
    public Debug getDebug() { return this.debug; }
    public Error getError() { return this.error; }
    public Message getMessage() { return this.message; }

    public interface IGUI extends InventoryHolder{
        int getSlotCount();
        String getName();
        boolean isValidGUI();
        int getPlacedID();
        void setKey(String key);
        void setPlacedID(int placedID);
        void setPlayer(Player player);
        int getPageNum();
        void setPageNum(int pageNum);
        void onGUIClick(InventoryClickEvent event);
    }

    /**
     * Gets the current running instance
     * @return Domain instance
     */
    public static Domain getInstance() { return instance; }

    /**
     * Gets the API class in this instance
     * @return API class
     */
    public API api() { return api; }

    /**
     * Gets the API class in this instance
     * @return API class
     */
    public API getAPI() { return api; }

    /**
     * Gets the live data
     * @return Live data
     */
    public LiveData getData() {
        return liveData;
    }

    public InstanceData getGameData() { return gameData; }

    public IbexPersistentData getPersistentData() { return persistentData; }

    /**
     * Gets the dependencies
     * @return Dependencies
     */
    public Dependencies getDependencies() {
        return dependencies;
    }

    /**
     * Checks if cleanup is turned on
     * @return If true, cleanup is turned on
     */
    public boolean isCleanup() {
        return cleanup;
    }

    /**
     * Sets the cleanup status
     * @param cleanup If true, cleanup will be turned on
     */
    public void setCleanup(boolean cleanup) {
        this.cleanup = cleanup;
    }

    /**
     * Gets the Config data
     * @return Config Data
     */
    public DomainConfig config() {
        return this.config;
    }

    /**
     * Sets the Config data
     * @param config Config data to set
     */
    public void setConfig(DomainConfig config) {
        this.config = config;
    }

    /**
     * Gets the Bypass 'boss' bar
     * @return Bypass boss bar
     */
    public BossBar getBypassBar() {
        return this.bar;
    }

    /**
     * Sets the Bypass 'boss' bar
     * @param bar Bypass boss bar
     */
    public void setBypassBar(BossBar bar) {
        this.bar = bar;
    }


    /**
     * Starts the event listeners
     */
    public void loadEventListeners() {
        debug.log("Domain", "loadEventListeners()", "Starting Event Listeners");
        try {
            Bukkit.getPluginManager().registerEvents(new BlockEvents(), this);
            Bukkit.getPluginManager().registerEvents(new CreatureSpawnEvents(), this);
            Bukkit.getPluginManager().registerEvents(new EntityEvents(), this);
            Bukkit.getPluginManager().registerEvents(new HangingEvents(), this);
            Bukkit.getPluginManager().registerEvents(new InventoryEvents(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
            Bukkit.getPluginManager().registerEvents(new PotionSplashEvents(), this);
            Bukkit.getPluginManager().registerEvents(new ProjectileEvents(), this);
            debug.log("Domain", "loadEventListeners()", "Starting Vehicle Listener");
            Bukkit.getPluginManager().registerEvents(new VehicleEvents(), this);
            if (liveData.isPaper() && (GameFunctions.is114() || GameFunctions.is115() || GameFunctions.is116() || GameFunctions.is117() || GameFunctions.is118())) {
                Bukkit.getPluginManager().registerEvents(new PaperEvent(), this);
            }



        } catch (Exception ex) {
            error.save(
                    "001",
                    "Domain",
                    "loadEventListeners",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Starts the command listener
     */
    public void loadCommands() {
        debug.log("Domain", "loadCommands", "Activating /domain");
        try {
            getCommand("domain").setExecutor(new DomainCommand());
        } catch (Exception ex) {
            error.save(
                    "001",
                    "Domain",
                    "loadCommands",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Starts the schedule tasks
     */
    public void startScheduled() {
        try {
            PlayerPosition playerPosition = new PlayerPosition(this);
            BukkitTask playerPositionTask = new BukkitRunnable() {
                public void run() {
                    playerPosition.Run();
                }
            }.runTaskTimer(instance, 0, 5 * 20);
            debug.log("Domain", "startScheduled", "startPlayerPos running with id " + playerPositionTask.getTaskId());

            Cleanup cleanup = new Cleanup(this);
            BukkitTask cleanupTask = new BukkitRunnable() {
                public void run() {
                    cleanup.Run();
                }
            }.runTaskTimer(instance, 0, 2 * 20);
            debug.log("Domain", "startScheduled", "startCleanup running with id " + cleanupTask.getTaskId());

            ApplyPlayerEffects applyPlayerEffects = new ApplyPlayerEffects(this);
            BukkitTask applyPlayerEffectsTask = new BukkitRunnable() {
                public void run() {
                    applyPlayerEffects.Run();
                }
            }.runTaskTimer(instance, 0, 20);
            debug.log("Domain", "startScheduled", "startApplyPlayerEffects running with id " + applyPlayerEffectsTask.getTaskId());

            BukkitTask reminderTask = new BukkitRunnable() {
                public void run() {
                    PlayerReminder playerReminder = new PlayerReminder(instance);
                    playerReminder.Run();
                }
            }.runTaskTimer(instance, 0, 60 * 20);
            debug.log("Domain", "startScheduled", "startReminder running with id " + reminderTask.getTaskId());

            Hourly hourly = new Hourly(this);
            BukkitTask hourlyTask = new BukkitRunnable() {
                public void run() {
                    hourly.Run();
                }
            }.runTaskTimer(instance, 0, 3600 * 20);
            debug.log("Domain", "startScheduled", "startHourly running with id " + hourlyTask.getTaskId());

            SpawnMobs spawnMobs = new SpawnMobs(this);
            BukkitTask spawnTask = new BukkitRunnable() {
                public void run() {
                    spawnMobs.Run();
                }
            }.runTaskTimer(instance, 0, 20 * 20);
            debug.log("Domain", "startScheduled", "TimedSpawner running with id " + spawnTask.getTaskId());

            if (this.config.getIdleCycleSeconds() > 0) {
                IdleCheck idleCheck = new IdleCheck(this);
                BukkitTask idleTask = new BukkitRunnable() {
                    public void run() {
                        idleCheck.Run();
                    }
                }.runTaskTimer(instance, 0, 20L * this.config.getIdleCycleSeconds());
                debug.log("Domain", "startScheduled", "TimedIdleCheck running with id " + idleTask.getTaskId());
            }
            if (config.getPapiUpdateTime() > 0) {
                UpdatePAPIValues updatePAPIValues = new UpdatePAPIValues(this);
                BukkitTask papiTask = new BukkitRunnable() {
                    public void run() {
                        updatePAPIValues.Run();
                    }
                }.runTaskTimer(instance, 0, (config.getPapiUpdateTime() * 20L));
                debug.log("Domain", "startScheduled", "PAPI Update running with id " + papiTask.getTaskId());
            }

            MagnetSchedule magnetSchedule = new MagnetSchedule(this);
            BukkitTask magnetTask = new BukkitRunnable() {
                public void run() {
                    magnetSchedule.Run();
                }
            }.runTaskTimer(instance, 0, 20);
            debug.log("Domain", "startScheduled", "Magnet running with id " + magnetTask.getTaskId());
        } catch (Exception ex) {
            error.save(
                    "001",
                    "Domain",
                    "startScheduled",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }

    /**
     * Clears all data from the instance
     */
    public void clearData() {

        StorageManager storageManager = new StorageManager(this);
        storageManager.saveAll(false);
        debug.log("Domain", "clearData", "Removing current Bypass bars");
        BossBar bar = Domain.getInstance().getBypassBar();
        for (Player barPlayer : bar.getPlayers()) {
            bar.removePlayer(barPlayer);
        }
        liveData.clearBypassPlayer();
        debug.log("Domain", "clearData()", "Clearing existing data");
        liveData.clearDomainFieldConfig();
        liveData.clearFlags();
        liveData.clearAllowed();
        liveData.placedFields.clear();
    }

    /**
     * Loads data to the instance
     */
    public void loadData() {

        ConfigManager configManager = new ConfigManager(this);
        configManager.loadAllConfigs();
        VoucherManager voucherManager = new VoucherManager(this);
        voucherManager.loadAll();

        StorageManager storageManager = new StorageManager(instance);
        storageManager.loadAll();

        if (config.isVoidCheck()) {
            Location voidLocation = WorldFunctions.deserialize(config.getVoidLocation());
            if (voidLocation != null) {
                getData().voidLocation = voidLocation;
            }
        }

    }

    public void reloadLocale() {
        locale = ibexInternal.getLocale();
        locale.load("Domain", this.getDataFolder() + File.separator + "lang.yml");
        FlagManager flagManager = new FlagManager(this);
        flagManager.loadFlags();
        GUIOptions guiOptions = new GUIOptions(this);
        guiOptions.loadOptions();
    }
    @Override
    public void onEnable() {
        try {
            instance = this;

            error = new Error(instance, "Domain");

            config = new DomainConfig(this);

            this.ibexInternal = new Internal(this, "Domain");

            internalData = new com.ibexmc.internal.data.Data(this);
            internalData.setDebugMode(this.config().isDebug());
            locale = ibexInternal.getLocale();
            locale.load("Domain", this.getDataFolder() + File.separator + "lang.yml");
            log = new Log(instance, "Domain");
            debug = new Debug(instance, "Domain", internalData);

            message = new Message(instance, "Domain", internalData, locale);
            dependencies = new Dependencies(this);

            if (config.useMySQL()) {
                mysql = ibexInternal.getMySQL(
                        config.getMySQLHost(),
                        "" + config.getMySQLPort(),
                        config.getMySQLDatabase(),
                        config.getMySQLUsername(),
                        config.getMySQLPassword(),
                        config.getMySQLUseSSL()
                );
            }

            domainFunctions = new DomainFunctions(this);

            log.log("&e[>]&1 ==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1== &e[<]");
            log.log("                  &fDomain" + " - " + getDescription().getVersion());
            log.log("&e[>]&1 ==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1==&f==&1== &e[<]");

            File flagFile = new File(this.getDataFolder(), "flags.yml");
            boolean flagsFileExists = flagFile.exists();
            domainFunctions.createResourceFiles();
            if (!flagsFileExists) {
                debug.log("Domain", "onEnable", "Creating default blocks");
                domainFunctions.createDefaultBlocks();
                domainFunctions.createDefaultVouchers();
                domainFunctions.createDataFolder();
            }

            persistentData = new IbexPersistentData(this);
            gameData = new InstanceData();

            api = new API(this);

            liveData.setPaper(GameFunctions.isPaper());

            FlagManager flagManager = new FlagManager(this);
            flagManager.loadFlags();
            GUIOptions guiOptions = new GUIOptions(this);
            guiOptions.loadOptions();
            loadEventListeners();
            loadCommands();
            startScheduled();

            getServer().getScheduler().scheduleSyncDelayedTask(this, this::loadData);

            if (!dependencies.setupEconomy() ) {
                log.log("Domain", "OnEnable", "No Vault dependency found");
                dependencies.setVault(false);
            } else {
                log.log("Domain", "OnEnable","Vault dependency loaded");
                dependencies.setVault(true);
            }
            if (dependencies.hookMyriadClans()) {
                log.log("Domain", "OnEnable", "Myriad dependency found");
            }
            if (dependencies.hookWorldGuard()) {
                log.log("Domain", "OnEnable", "WorldGuard dependency found");
            }
            if (dependencies.hookDynmap()) {
                log.log("Domain", "OnEnable", "Dynmap dependency found");
            }

//            if (config.isAllowBStats()) {
//                Metrics metrics = new Metrics(this, 20096);
//            }

        } catch (Exception ex) {
            error.save(
                    "001",
                    "Domain",
                    "onEnable",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.CRITICAL,
                    ex.getStackTrace()
            );
        }
    }

    @Override
    public void onDisable() {
        StorageManager storageManager = new StorageManager(this);
        storageManager.saveAll(true);
    }

}
