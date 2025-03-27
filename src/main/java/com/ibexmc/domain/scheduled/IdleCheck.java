package com.ibexmc.domain.scheduled;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.Idle;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IdleCheck {

    private final Domain instance;
    private DomainBlockManager domainBlockManager;

    public IdleCheck(Domain instance) {
        this.instance = instance;
        this.domainBlockManager = new DomainBlockManager(instance);
    }

    public void Run() {
        if (Domain.getInstance().config().getIdleCyclesBeforeKick() > 0) {
            instance.getDebug().log("TimedIdleCheck", "Run", "Idle Check");
            if (Bukkit.getOnlinePlayers() != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!PlayerFunctions.hasBypass(player)) {
                        Idle idle = new Idle(player);
                        if (Domain.getInstance().getData().hasIdle(player.getUniqueId())) {
                            idle = Domain.getInstance().getData().getIdle(player.getUniqueId());
                        }
                        idle.updateLocation();
                        if (idle.inIdleField()) {
                            instance.getDebug().log("TimedIdleCheck", "Run", player.getName() + " in idle field");
                            if (idle.fieldOldEnough()) {
                                if (idle.getUnmoved() == Domain.getInstance().config().getIdleCyclesBeforeKick()) {
                                    idle.warnPlayer();
                                }
                                if (idle.getUnmoved() > Domain.getInstance().config().getIdleCyclesBeforeKick()) {
                                    if (!domainBlockManager.isAllowed(player, player.getLocation())) {
                                        idle.kickPlayer(true);
                                    }
                                }
                            } else {
                                instance.getDebug().log("TimedIdleCheck", "Run", "Field is not old enough to do idle kicks");
                            }
                        }
                        Domain.getInstance().getData().putIdle(player.getUniqueId(), idle);
                    }
                }
            }
        }
    }
}
