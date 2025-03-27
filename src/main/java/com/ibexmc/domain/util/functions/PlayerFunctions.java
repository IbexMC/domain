package com.ibexmc.domain.util.functions;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.ibexmc.domain.Domain;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerFunctions {

    /**
     * Returns the player head for an offline player
     * @param uuid Player Unique Id
     * @return Player head
     */
    public static ItemStack head(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta headMeta = head.getItemMeta();
        if (headMeta instanceof SkullMeta) {
            SkullMeta headSkull = (SkullMeta) headMeta;
            headSkull.setDisplayName(offlinePlayer.getName());
            headSkull.setOwningPlayer(offlinePlayer);
            head.setItemMeta(headSkull);
        }
        return head;
    }

    /**
     * Gets an online player by name
     * @param name Name to lookup
     * @return Player, if not found, returns null
     */
    public static Player playerFromName(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ChatColor.stripColor(name).equalsIgnoreCase(ChatColor.stripColor(player.getName()))) {
                return player;
            }
            if (ChatColor.stripColor(name).equalsIgnoreCase(ChatColor.stripColor(player.getDisplayName()))) {
                return player;
            }
        }
        return null;
    }

    /**
     * Gets an offline player by name
     * @param name Name to lookup
     * @return Offline player
     */
    public static OfflinePlayer offlinePlayerFromName(String name) {
        try {
            name = ChatColor.stripColor(name);
            OfflinePlayer[] offlinePlayerArray = Bukkit.getOfflinePlayers();
            List<OfflinePlayer> offlinePlayers = Arrays.asList(offlinePlayerArray);
            for (OfflinePlayer offlinePlayer : offlinePlayers) {
                String playerName = "";
                if (offlinePlayer != null) {
                    if (offlinePlayer.getName() != null) {
                        playerName = ChatColor.stripColor(offlinePlayer.getName());
                    }
                }
                if (name.equalsIgnoreCase(playerName)) {
                    return offlinePlayer;
                }
            }
        } catch (Exception ex)
        {
            String a = ex.getMessage();
        }
        return null;
    }

    public static OfflinePlayer offlinePlayerFromUUID(UUID uuid) {
        OfflinePlayer offlinePlayer = null;
        offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return offlinePlayer;
    }

    public static boolean isEntityPlayer(Entity e) {
        //instance.getDebug().log("Check.IsEntityPlayer", "Check called");
        if (e instanceof Player) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasBypass(Player p) {
        //boolean retVal = false;
        //instance.getDebug().log("Check", "HasBypass", "Checking for bypass list");
        if (p == null) {
            return false;
        } else {
            return Domain.getInstance().getData().hasBypass(p.getUniqueId());
        }
    }

    public static Material materialInMainHand(Player player) {
        //Alert.ExtremeLog("Check.MainHand", player.getInventory().getItemInMainHand().toString());
        Material m = Material.AIR;
        ItemStack istack = player.getInventory().getItemInMainHand();
        if (istack != null)
        {
            m = istack.getType();
        }
        return m;
    }
    public static ItemStack itemStackInMainHand(Player player) {
        //Alert.ExtremeLog("Check.MainHand", player.getInventory().getItemInMainHand().toString());
        ItemStack istack = player.getInventory().getItemInMainHand();
        if (istack == null)
        {
            istack = new ItemStack(Material.AIR, 1);
        }
        return istack;
    }

    public static Player playerFromEntity(Entity e) {
        //instance.getDebug().log("Provide", "PlayerFromEntity", "Called");
        if (PlayerFunctions.isEntityPlayer(e))
        {
            Player player = (Player) e;
            //instance.getDebug().log("Provide", "PlayerFromEntity", "Returning player");
            return player;
        } else {
            //instance.getDebug().log("Provide", "PlayerFromEntity", "Returning null");
            return null;
        }
    }

    public static int firstEmptySlot(UUID uuid)  {
        int slot = -1;
        OfflinePlayer offlinePlayer = PlayerFunctions.offlinePlayerFromUUID(uuid);
        if (offlinePlayer != null) {
            if (offlinePlayer.getPlayer() != null) {
                if (offlinePlayer.getPlayer().getInventory().firstEmpty() >= 0) {
                    slot = offlinePlayer.getPlayer().getInventory().firstEmpty();
                }
            }
        }
        return slot;
    }

}
