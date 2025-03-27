package com.ibexmc.domain.domainblock;

import com.ibexmc.domain.Domain;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Magnet {

    private Domain instance;
    private Entity entity;
    private Location location;
    private int range = 3;

    public Magnet(Domain instance, Entity entity, Location location, int range) {
        this.instance = instance;
        this.entity = entity;
        this.location = location;
        this.range = range;
    }

    public void pull() {
        if (this.entity != null) {
            if (this.entity.getLocation().distance(this.location) <= this.range) {
                if (
                    // If the entity isn't in same x/y block, teleport them to it
                        !(
                                entity.getLocation().getBlockX() == this.location.getBlockX() &&
                                        entity.getLocation().getBlockY() == this.location.getBlockY()
                        )
                ) {
                    this.entity.teleport(this.location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    this.entity.setVelocity(new Vector());
                    if (this.entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) this.entity;
                        livingEntity.setCollidable(false);
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10));
                    }
                }
            }
        }
    }
}
