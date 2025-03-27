package com.ibexmc.domain.util;

import com.ibexmc.domain.Domain;
import com.ibexmc.internal.functions.ParticleFunctions;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DomainParticles extends ParticleFunctions {
    public DomainParticles() {
        super();
    }
    public static void flame(Location location) {
        location.getWorld().spawnParticle(Particle.FLAME, location.getBlockX(), location.getBlockY(), location.getBlockZ(), 10);

    }
    public static void denyAccess(Location location, Player player) {
        float volume = (float) 0.5;
        player.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, volume, 1);
        player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, location, 10, -1, 1.5, -1, 3);
    }
    public static void denyAccessSmall(Location location, Player player) {
        float volume = (float) 0.3;
        player.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, volume, 1);
        player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, location, 10, -0.5, -1, -0.5, 3);
    }
    public static Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }
    public static Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }
    public static Location getBelow(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(0, Math.cos(angle), Math.sin(angle)).normalize().multiply(distance));
    }
    public static Location getAbove(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(0, Math.cos(angle), Math.sin(angle)).normalize().multiply(distance));
    }
    public static Location getBackSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.sin(angle), 0, Math.cos(angle)).normalize().multiply(distance));
    }
    public static Location getFrontSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.sin(angle), 0, Math.cos(angle)).normalize().multiply(distance));
    }

    public static void denyBlock(Location blockLocation) {
        new BukkitRunnable() {
            private int step = 0;
            Location location = blockLocation.clone().add(0.5,1,0.5);
            public void run() {
                step++;
                int r = 0;
                int g = 0;
                int b = 0;

                if (step >= 0 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.02,
                            location.getZ());
                    displayParticleDust(
                            location,
                            5,  // count
                            r, // red
                            g,   // green
                            b,   // blue
                            2    // size
                    );
                }
                if (step >= 5 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.02,
                            location.getZ());
                    displayParticleDust(
                            location,
                            3,  // count
                            r + 5, // red
                            g,   // green
                            b,   // blue
                            1    // size
                    );
                }
                if (step >= 10 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.03,
                            location.getZ());
                    displayParticleDust(
                            location,
                            1,  // count
                            r + 10, // red
                            g,   // green
                            b,   // blue
                            1    // size
                    );
                }


                if (step >= 15) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Domain.getInstance(), 0, 3);
    }
    public static void takeEffect(Location blockLocation) {
        new BukkitRunnable() {
            private int step = 0;
            Location location = blockLocation.add(0.5,0,0.5);
            public void run() {
                step++;
                int r = 247;
                int g = 119;
                int b = 224;

                if (step >= 0 && step < 15) {
                    location = new Location (
                            blockLocation.getWorld(),
                            blockLocation.getX(),
                            blockLocation.getY() + 0,
                            blockLocation.getZ());
                    displayParticleDust(
                            location,
                            15,  // count
                            r, // red
                            g,   // green
                            b,   // blue
                            2    // size
                    );
                    location = new Location (
                            blockLocation.getWorld(),
                            blockLocation.getX(),
                            blockLocation.getY() + 0.3,
                            blockLocation.getZ());
                    displayParticleDust(
                            location,
                            10,  // count
                            r - 5, // red
                            g - 5,   // green
                            b - 5,   // blue
                            1    // size
                    );
                    location = new Location (
                            blockLocation.getWorld(),
                            blockLocation.getX(),
                            blockLocation.getY() + 0.6,
                            blockLocation.getZ());
                    displayParticleDust(
                            location,
                            5,  // count
                            r - 10, // red
                            g - 10,   // green
                            b - 10,   // blue
                            1    // size
                    );
                    if (step % 2 == 0) {


                        if (step < 15) {

                            if (step % 5 == 0) {
                                location = new Location (
                                        blockLocation.getWorld(),
                                        blockLocation.getX(),
                                        blockLocation.getY() + 0,
                                        blockLocation.getZ());
                                displayParticleDust(
                                        location,
                                        3,  // count
                                        255, // red
                                        255,   // green
                                        255,   // blue
                                        1    // size
                                );
                            }
                        }
                    }
                }


                if (step >= 15) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Domain.getInstance(), 0, 3);
    }

    public static void voucherSuccess(Location blockLocation) {
        new BukkitRunnable() {
            private int step = 0;
            Location location = blockLocation.clone().add(0.5,1,0.5);
            public void run() {
                step++;
                int r = 255;
                int g = 0;
                int b = 0;

                if (step >= 0 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.02,
                            location.getZ());
                    displayParticleDust(
                            location,
                            5,  // count
                            r, // red
                            g,   // green
                            b,   // blue
                            2    // size
                    );
                }
                if (step >= 5 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.02,
                            location.getZ());
                    displayParticleDust(
                            location,
                            3,  // count
                            r, // red
                            g + 255,   // green
                            b,   // blue
                            1    // size
                    );
                }
                if (step >= 10 && step < 15) {
                    location = new Location (
                            location.getWorld(),
                            location.getX(),
                            location.getY() + 0.03,
                            location.getZ());
                    displayParticleDust(
                            location,
                            1,  // count
                            r, // red
                            g + 255,   // green
                            b + 255,   // blue
                            1    // size
                    );
                }


                if (step >= 15) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Domain.getInstance(), 0, 3);
    }
}
