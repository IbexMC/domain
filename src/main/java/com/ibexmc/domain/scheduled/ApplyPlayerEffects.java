package com.ibexmc.domain.scheduled;

import java.util.HashMap;
import java.util.Map;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ApplyPlayerEffects {

    private final Domain instance;
    private DomainBlockManager domainBlockManager;

    public ApplyPlayerEffects(Domain instance) {
        this.instance = instance;
        this.domainBlockManager = new DomainBlockManager(instance);
    }

    public void Run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean isAllowed = domainBlockManager.isAllowed(p);
            boolean inFlyZone = false;
            Map<Flag.Type, Boolean> hmFlags = domainBlockManager.getFlagsAtLocation(p.getLocation());

            if (hmFlags.containsKey(Flag.Type.ENABLE_FLY_FOR_ALLOWED)) {
                if (hmFlags.get(Flag.Type.ENABLE_FLY_FOR_ALLOWED)) {
                    if (isAllowed) {
                        inFlyZone = true;
                    }
                }
            }

            if (Domain.getInstance().getData().containsCanFly(p.getUniqueId())) {
                Integer flySeconds = Domain.getInstance().getData().getCanFlySeconds(p.getUniqueId());
                if (!inFlyZone) {
                    // Player is currently listed as flying but not in fly zone
                    if (p.getAllowFlight()) {
                        // Player has fly enabled
                        if (flySeconds > 0) {
                            // Player has warning time left - warn player and decrease timer
                            Map<String, String> placeHolder = new HashMap<>();
                            placeHolder.put("<%seconds%>", Integer.toString(flySeconds));
                            instance.getMessage().actionBar(p, instance.getLocale().localeText("player_fly_end_warning", "&4CAUTION - You will lose your ability to fly in <%seconds%> seconds", placeHolder));
                            Domain.getInstance().getData().putCanFly(p.getUniqueId(), flySeconds - 1);
                        } else {
                            // Players timer has expired, disable flying
                            instance.getMessage().actionBar(p, instance.getLocale().localeText("player_fly_end", "&4You can no longer fly", null));
                            p.setAllowFlight(false);
                            Domain.getInstance().getData().removeCanFlyByUUID(p.getUniqueId());
                        }
                    } else {
                        // Player does not have fly enabled, disable immediately, no warning
                        instance.getMessage().actionBar(p, instance.getLocale().localeText("player_fly_end", "&4You can no longer fly", null));
                        p.setAllowFlight(false);
                        Domain.getInstance().getData().removeCanFlyByUUID(p.getUniqueId());
                    }
                } else {
                    // Currently listed as flying, in a fly zone
                    if (flySeconds < Domain.getInstance().config().getFlyWarnSeconds()) {
                        // Reset the count to 3
                        Domain.getInstance().getData().putCanFly(p.getUniqueId(), 3);
                        if (p.getAllowFlight()) {
                            instance.getMessage().actionBar(p, instance.getLocale().localeText("player_fly_warning", "&2You can now fly in this Domain field", null));
                        }
                    }
                    if (!p.getAllowFlight()) {
                        // Player is not set to fly
                        instance.getMessage().actionBar(p, instance.getLocale().localeText("player_fly_warning", "&2You can now fly in this Domain field", null));
                        p.setAllowFlight(true);
                    }
                }
            } else {
                if (inFlyZone) {
                    // Not currently listed as flying but is in fly zone - set as flying
                    if (!p.getAllowFlight()) {
                        Domain.getInstance().getData().putCanFly(p.getUniqueId(), Domain.getInstance().config().getFlyWarnSeconds());
                        instance.getMessage().actionBar(p, instance.getLocale().localeText("player_fly_warning", "&2You can now fly in this Domain field", null));
                        p.setAllowFlight(true);
                    }
                }

            }

            // Potion effect checks
            if (hmFlags.containsKey(Flag.Type.EFFECT_MINERS_HASTE_ALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_MINERS_HASTE_ALLOWED)) {
                    if (isAllowed) {
                        p.removePotionEffect(PotionEffectType.FAST_DIGGING);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 0));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_LEVITATION_ALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_LEVITATION_ALLOWED)) {
                    if (isAllowed) {
                        p.removePotionEffect(PotionEffectType.LEVITATION);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 2));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_NIGHT_VISION_ALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_NIGHT_VISION_ALLOWED)) {
                    if (isAllowed) {
                        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 2));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_WATER_BREATHING_ALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_WATER_BREATHING_ALLOWED)) {
                    if (isAllowed) {
                        p.removePotionEffect(PotionEffectType.WATER_BREATHING);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 200, 0));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_HUNGER_NONALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_HUNGER_NONALLOWED)) {
                    if (!isAllowed) {
                        p.removePotionEffect(PotionEffectType.HUNGER);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 2));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_WEAKNESS_NONALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_WEAKNESS_NONALLOWED)) {
                    if (!isAllowed) {
                        p.removePotionEffect(PotionEffectType.WEAKNESS);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_CONFUSION_NONALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_CONFUSION_NONALLOWED)) {
                    if (!isAllowed) {
                        p.removePotionEffect(PotionEffectType.CONFUSION);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 2));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_BLINDNESS_NONALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_BLINDNESS_NONALLOWED)) {
                    if (!isAllowed) {
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_SLOW_DIGGING_NONALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_SLOW_DIGGING_NONALLOWED)) {
                    if (!isAllowed) {
                        p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 0));
                    }
                }
            }
            if (hmFlags.containsKey(Flag.Type.EFFECT_WITHER_NONALLOWED)) {
                if (hmFlags.get(Flag.Type.EFFECT_WITHER_NONALLOWED)) {
                    if (!isAllowed) {
                        p.removePotionEffect(PotionEffectType.WITHER);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
                    }
                }
            }
        }

    }
}
