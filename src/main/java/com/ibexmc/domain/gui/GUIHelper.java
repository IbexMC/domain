package com.ibexmc.domain.gui;


import com.ibexmc.internal.functions.SoundFunctions;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.util.functions.GameFunctions;

public class GUIHelper {
    public static ItemStack get(String key) {
        switch (key) {
            case "na":
                return Domain.getInstance().getData().getGUIOption(
                        "na",
                        Material.BLACK_STAINED_GLASS_PANE,
                        "&0.",
                        "",
                        true
                ).getItemStack();
            case "disabled":
                return Domain.getInstance().getData().getGUIOption(
                        "disabled",
                        Material.BLACK_STAINED_GLASS_PANE,
                        "",
                        "&fThis option is not available",
                        true
                ).getItemStack();
            case "back":
                return Domain.getInstance().getData().getGUIOption(
                        "back",
                        Material.NETHER_STAR,
                        "&bBack",
                        "&fReturn to previous page",
                        true
                ).getItemStack();
            case "error":
                return Domain.getInstance().getData().getGUIOption(
                        "error",
                        Material.RED_STAINED_GLASS_PANE,
                        "",
                        "",
                        true
                ).getItemStack();

            case "display_field":
                return Domain.getInstance().getData().getGUIOption(
                        "display_field", Material.BEACON,
                        "&bDisplay Field",
                        "&fVisualize the field",
                        true
                ).getItemStack();
            case "edit_field":
                return Domain.getInstance().getData().getGUIOption(
                        "edit_field", Material.ENCHANTING_TABLE,
                        "&bEdit Field",
                        "&fAccess the Domain Edit Menu",
                        true
                ).getItemStack();
            case "info":
                return Domain.getInstance().getData().getGUIOption(
                        "info", Material.KNOWLEDGE_BOOK,
                        "&bField Information",
                        "&fOpens the Domain Information Menu",
                        true
                ).getItemStack();
            case "permission":
                return Domain.getInstance().getData().getGUIOption(
                        "permission", Material.PLAYER_HEAD,
                        "&bPermissions",
                        "&fOpens the Domain Permissions menu|&eMay take a few moments to open",
                        true
                ).getItemStack();

            case "info_flags_previous":
                return Domain.getInstance().getData().getGUIOption(
                        "info_flags_previous", Material.LIGHT_BLUE_CONCRETE,
                        "&bPrevious",
                        "&fGoes to the previous page",
                        true
                ).getItemStack();
            case "info_flags_previous_disabled":
                return Domain.getInstance().getData().getGUIOption(
                        "info_flags_previous_disabled", Material.GRAY_CONCRETE,
                        "&4Previous",
                        "No previous pages found",
                        true
                ).getItemStack();
            case "info_flags_next":
                return Domain.getInstance().getData().getGUIOption(
                        "info_flags_next", Material.ORANGE_CONCRETE,
                        "&bNext",
                        "&fGoes to the next page",
                        true
                ).getItemStack();
            case "info_flags_next_disabled":
                return Domain.getInstance().getData().getGUIOption(
                        "info_flags_next_disabled", Material.GRAY_CONCRETE,
                        "&4Next",
                        "No more pages found",
                        true
                ).getItemStack();
            case "flag_add":
                return Domain.getInstance().getData().getGUIOption(
                        "flag_add", Material.LIME_CONCRETE,
                        "&aAdd Flag",
                        "&fAdds a new flag to the field",
                        true
                ).getItemStack();
            case "info_info":
                return Domain.getInstance().getData().getGUIOption(
                        "info_info", Material.PAPER,
                        "&bField Information",
                        "&fDisplays information about your field",
                        true
                ).getItemStack();
            case "info_who":
                return Domain.getInstance().getData().getGUIOption(
                        "info_who", Material.ENDER_EYE,
                        "&bWho else is in field?",
                        "&fDisplays a list of other players in your field",
                        true
                ).getItemStack();
            case "info_flags":
                return Domain.getInstance().getData().getGUIOption(
                        "info_flags", Material.YELLOW_BANNER,
                        "&bFlags",
                        "&fDisplays the flags currently assigned to this field",
                        true
                ).getItemStack();
            case "info_rent":
                if (GameFunctions.is114()) {
                    return Domain.getInstance().getData().getGUIOption(
                            "info_rent", Material.OAK_SIGN,
                            "&bRent Info",
                            "&fDisplays any rent information for this field",
                            true
                    ).getItemStack();
                } else {
                    return Domain.getInstance().getData().getGUIOption(
                            "info_rent", Material.GOLD_INGOT,
                            "&bRent Info",
                            "&fDisplays any rent information for this field",
                            true
                    ).getItemStack();
                }
            case "rent_info":
                return Domain.getInstance().getData().getGUIOption(
                        "rent_info", Material.BOOK,
                        "&bRent Info",
                        "&fDisplays rent information for this field",
                        true
                ).getItemStack();
            case "rent_cost":
                return Domain.getInstance().getData().getGUIOption(
                        "rent_cost", Material.GOLD_INGOT,
                        "&bRent Cost",
                        "&fSets the rent cost",
                        true
                ).getItemStack();
            case "rent_days":
                return Domain.getInstance().getData().getGUIOption(
                        "rent_days", Material.CLOCK,
                        "&bRent Days",
                        "&fSets the rent days",
                        true
                ).getItemStack();
            default:
                return Domain.getInstance().getData().getGUIOption(
                        "error",
                        Material.RED_STAINED_GLASS_PANE,
                        "",
                        "",
                        false
                ).getItemStack();
        }
    }
    public static void soundError(Player player) {
        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.ENTITY_VILLAGER_NO);
    }
    public static void soundClick(Player player) {
        SoundFunctions.playSoundWorld(player.getLocation(), 0.5f, 1.0f, Sound.BLOCK_IRON_TRAPDOOR_CLOSE);
    }
}
