package com.ibexmc.domain.domainblock.vouchers;


import com.ibexmc.domain.Domain;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Voucher {

    //region Objects
    private String key = "";                                            // Voucher Key (from filename)
    private String name = "";                                           // Voucher Name
    private List<String> lore = new ArrayList<>();                      // Voucher Lore
    private Material material = Material.PAPER;                         // Voucher Material
    private int volume = 0;                                             // Volume to add
    private List<Flag.Type> flags;                                      // Flags to add
    private boolean singleUse = false;                                  // Single Use
    //endregion
    //region Constructor
    /**
     * Constructor for voucher
     * @param key Voucher Key
     * @param name Voucher Name
     * @param lore Voucher Lore
     * @param volume Volume to add
     * @param flags Flags to add
     * @param singleUse If true, voucher is not returned when block is removed
     */
    public Voucher(String key, String name, List<String> lore, int volume, List<Flag.Type> flags, boolean singleUse) {
        this.key = key;
        this.name = name;
        this.lore = lore;
        this.volume = volume;
        this.flags = flags;
        this.singleUse = singleUse;
    }
    //endregion
    //region Methods
    //region Getters
    /**
     * Gets the Voucher key
     * @return Voucher Key
     */
    public String key() {
        return this.key;
    }

    /**
     * Gets the Voucher name
     * @return Voucher Name
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the voucher ItemStack lore
     * @return Voucher Lore
     */
    public List<String> lore() {
        return this.lore;
    }

    /**
     * Gets the Volume to add
     * @return Volume
     */
    public int volume() {
        return this.volume;
    }

    /**
     * Gets the list of flags to add
     * @return Flags
     */
    public List<Flag.Type> flags() {
        return this.flags;
    }

    /**
     * Gets if the voucher is single use, so not returned when block is destroyed
     * @return Single Use
     */
    public boolean singleUse() {
        return this.singleUse;
    }
    /**
     * Gets the voucher ItemStack
     * @return Voucher ItemStack
     */
    public ItemStack voucher() {
        ItemStack wand = new ItemStack(this.material, 1);
        wand.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta wandMeta = wand.getItemMeta();
        IbexPersistentData persistentData = new IbexPersistentData(Domain.getInstance());
        persistentData.setString(wandMeta, "domain_voucher", this.key);
        wandMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wandMeta.setDisplayName(this.name);
        List<String> wandLore = this.lore;
        wandMeta.setLore(wandLore);
        wand.setItemMeta(wandMeta);
        return wand;
    }
    //endregion

    //endregion

}
