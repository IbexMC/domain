package com.ibexmc.domain.scheduled;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.spawner.Spawner;
import com.ibexmc.domain.domainblock.spawner.SpawnerManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.NumberFunctions;

public class SpawnMobs {

    Domain instance;

    public SpawnMobs(Domain instance) {
        this.instance = instance;
    }

    public void Run() {
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        for (DomainBlock domainBlock : domainBlockManager.getAllDomainBlocks()) {
            if (domainBlock.isActive()) {
                if (domainBlock.getConfig().getSpawners() != null) {
                    if (domainBlock.getConfig().getSpawners().size() > 0) {
                        boolean hasSpawnFlag = false;
                        for (Flag flag : domainBlock.getFlags()) {
                            if (flag.getFlagType() == Flag.Type.SPAWN) {
                                hasSpawnFlag = flag.isActive();
                                break;
                            }
                        }
                        if (hasSpawnFlag) {
                            for (Spawner spawner : domainBlock.getConfig().getSpawners()) {
                                int mobAmount = spawner.amount();
                                // If we have a multi amount greater than amount, and a chance > 0, check if we should spawn multiple
                                if (spawner.amount() < spawner.multiAmountMax()) {
                                    if (spawner.multiChance() > 0) {
                                        if (spawner.multiChance() <= NumberFunctions.random(0, 100)) {
                                            // Mob amount is a number between amount and multiAmountMax, it could still be just amount
                                            mobAmount = NumberFunctions.random(spawner.amount(), spawner.multiAmountMax());
                                        }
                                    }
                                }
                                for (int i = 0; i < mobAmount; ++i) {
                                    SpawnerManager.spawn(domainBlock, spawner);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
