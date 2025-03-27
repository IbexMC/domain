package com.ibexmc.domain.scheduled;

import java.sql.Timestamp;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.domain.domainblock.storage.StorageManager;
import com.ibexmc.internal.functions.TimeFunctions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Hourly {

    Domain instance;

    public Hourly(Domain instance) {
        this.instance = instance;
    }

    public void Run() {
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        StorageManager storageManager = new StorageManager(instance);
        storageManager.saveAll(true);
        instance.getDebug().log("Hourly", "Scheduled - Hourly", "&4Save Complete");

        if (Domain.getInstance().config().isExpireBlocks()) {
            instance.getDebug().log("Hourly", "Run", "Expire Blocks is true - Checking block expirations");
            for (DomainBlock domainBlock : domainBlockManager.getAllDomainBlocks()) {
                if (domainBlock.isActive() && !domainBlock.isAdminBlock()) {
                    int maxDays = domainBlock.getConfig().getOfflineDays();
                    if (maxDays > 0) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(domainBlock.getOwner());
                        if (offlinePlayer != null) {
                            if (offlinePlayer.hasPlayedBefore()) {
                                Timestamp lastSeen = new Timestamp(offlinePlayer.getLastPlayed());
                                Timestamp expires = TimeFunctions.addDays(lastSeen, maxDays);
                                if (offlinePlayer.getPlayer() == null) {
                                    if (!TimeFunctions.after(
                                            expires,
                                            TimeFunctions.now()
                                    )) {
                                        // expire
                                        instance.getDebug().log("Hourly", "Run", "Expiring field: " + domainBlock.getPlacedId());
                                        domainBlock.setExpired(true);
                                        domainBlock.setActive(false);
                                        domainBlock.setDirty(true);
                                        domainBlockManager.domainBlockSave(domainBlock);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            instance.getDebug().log("Hourly", "Run", "Expire Blocks is false - Not checking block expirations");
        }

        //region Rent Expiration
        RentManager rentManager = new RentManager(instance);
        rentManager.expireCheck();
        //endregion

    }
}
