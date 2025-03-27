package com.ibexmc.domain.dependencies.papi;

import com.ibexmc.domain.Domain;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPISupport extends PlaceholderExpansion {
    private Domain instance;

    public PAPISupport(Domain instance){
        this.instance = instance;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "domain";
    }

    @Override
    public String getAuthor() {
        return "torpkev";
    }

    @Override
    public String getVersion() {
        return instance.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        PAPIValues papiValues = Domain.getInstance().getGameData().papi().get(player.getUniqueId());
        if (papiValues == null) {
            return "";
        }

        switch (identifier) {
            case "total_blocks":
                return papiValues.papiData().get("total_blocks");
            case "current_field":
                return papiValues.papiData().get("current_field");
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }
}

