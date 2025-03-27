package com.ibexmc.domain.scheduled;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.Magnet;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

public class MagnetSchedule {

    private Domain instance;

    public MagnetSchedule(Domain instance) {
        this.instance = instance;
    }

    public void Run() {

        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        int magnetRange = instance.config().getMagnetRange();
        for (DomainBlock domainBlock : domainBlockManager.getAllDomainBlocks()) {
            if (domainBlock.getMagnetLocation() != null) {
                if (domainBlock.getMagnetLocation().getChunk().isLoaded()) {
                    Location magnetLocation = domainBlock.getMagnetLocation().clone();
                    boolean animalMagnet = domainBlockManager.hasActiveFlag(domainBlock, Flag.Type.ANIMAL_MAGNET);
                    boolean monsterMagnet = domainBlockManager.hasActiveFlag(domainBlock, Flag.Type.MONSTER_MAGNET);
                    for (Entity entity : WorldFunctions.getNearbyEntities(magnetLocation.clone(), magnetRange, magnetRange, magnetRange)) {
                        Magnet magnet = new Magnet(instance, entity, magnetLocation.clone().add(0.5, 0, 0.5), magnetRange);
                        if (animalMagnet && entity instanceof Animals) {
                            magnet.pull();
                        }
                        if (monsterMagnet && entity instanceof Monster) {
                            magnet.pull();
                        }
                    }
                }
            }
        }
    }
}
