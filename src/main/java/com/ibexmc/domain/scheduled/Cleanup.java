package com.ibexmc.domain.scheduled;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Cleanup {

    private final Domain instance;
    private DomainBlockManager domainBlockManager;

    public Cleanup(Domain instance) {
        this.instance = instance;
        this.domainBlockManager = new DomainBlockManager(instance);
    }

    public void Run() {

        if (!Domain.getInstance().isCleanup()) {
            return;
        }

        Domain.getInstance().getData().clearOldConvertingZombieVillagers();

        List<Player> remlist = new ArrayList<Player>();
        HashMap<Player, Timestamp> hmPvP = Domain.getInstance().getData().getPVPLists();
        if (hmPvP != null)
        {
            for (Entry<Player, Timestamp> pvp : hmPvP.entrySet()) {
                long milliseconds = new Timestamp(System.currentTimeMillis()).getTime() - pvp.getValue().getTime();
                int seconds = (int) milliseconds / 1000;
                if (seconds >= 30) // TODO: Split this out to glow & tp
                {
                    remlist.add(pvp.getKey());
                }
            }
            if (remlist != null)
            {
                for (Player p : remlist)
                {
                    Domain.getInstance().getData().removePVPListByKey(p);
                    p.setGlowing(false);
                }
            }
        }

        List<Block> arrowremlist = new ArrayList<Block>();
        HashMap<Block, Timestamp> hmArrow = Domain.getInstance().getData().getArrowBlock();

        if (hmArrow != null)
        {
            for (Entry<Block, Timestamp> arrow : hmArrow.entrySet()) {
                long milliseconds = new Timestamp(System.currentTimeMillis()).getTime() - arrow.getValue().getTime();
                int seconds = (int) milliseconds / 1000;
                if (seconds >= 2) // After 2 seconds, remove the block entry
                {
                    arrowremlist.add(arrow.getKey());
                }
            }
            if (arrowremlist != null)
            {
                for (Block b : arrowremlist)
                {
                    Domain.getInstance().getData().removeArrowBlocksByBlock(b);
                }
            }
        }

        List<Timestamp> naremlist = new ArrayList<Timestamp>();
        HashMap<Timestamp, UUID> hmNA = Domain.getInstance().getData().getNotAlloweds();
        if (hmNA != null) {
            for (Entry<Timestamp, UUID> na : hmNA.entrySet()) {
                long milliseconds = new Timestamp(System.currentTimeMillis()).getTime() - na.getKey().getTime();
                int seconds = (int) milliseconds / 1000;
                if (seconds >= 2) // After 2 seconds, remove the block entry
                {
                    naremlist.add(na.getKey());
                }
            }
            if (naremlist != null)
            {
                for (Timestamp t : naremlist)
                {
                    Domain.getInstance().getData().removeNotAllowedByKey(t);
                }
            }
        }

    }
}
