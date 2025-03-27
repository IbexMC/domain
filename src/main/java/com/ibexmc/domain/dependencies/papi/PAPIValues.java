package com.ibexmc.domain.dependencies.papi;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PAPIValues {

    private final Domain instance;
    private Map<String, String> papiData;


    public PAPIValues(Domain instance, Map<String, String> papiData) {
        this.instance = instance;
        this.papiData = papiData;
    }

    /**
     * Gets the total number of blocks owned
     * @return
     */
    public Map<String, String> papiData() { return papiData; }

    /**
     * Updates the PAPI values for the specified player
     * @param player Player to update values for
     */
    public void update(Player player) {
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (player != null) {
            Map<String, String> papiData = new HashMap<>();
            papiData.put("total_blocks", "" + domainBlockManager.getOwnedDomainBlocks(player.getUniqueId(), true).size());
            DomainBlock domainBlock = domainBlockManager.getCurrentField(player);
            if (domainBlock != null) {
                papiData.put("current_field",domainBlock.getFieldName());
            } else {
                papiData.put("current_field","None");
            }
            PAPIValues papiValues = new PAPIValues(
                    instance,
                    papiData
            );
            instance.getGameData().papi().put(player.getUniqueId(), papiValues);
        }
    }
}
