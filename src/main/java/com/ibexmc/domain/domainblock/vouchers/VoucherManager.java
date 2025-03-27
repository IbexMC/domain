package com.ibexmc.domain.domainblock.vouchers;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.messaging.Error;
import com.ibexmc.internal.utility.IbexPersistentData;
import com.ibexmc.internal.utility.YmlParser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VoucherManager {

    private final Domain instance;

    public VoucherManager(Domain instance) {
        this.instance = instance;
    }

    /**
     * Loops through all voucher files in the Vouchers folder and attempts to load them
     */
    public void loadAll() {
        Domain.getInstance().getData().vouchers.clear();
        String filePathString = Domain.getInstance().getDataFolder().getAbsolutePath() + File.separator + "Vouchers";
        File filePath = new File(filePathString);
        if (filePath.exists()) {
            for (File file : new File(filePathString).listFiles()) {
                if (file.getName().contains(".yml")) {
                    load(file);
                }
            }
        }
    }

    /**
     * Loads a voucher file and inserts it into Domain.instance().data().vouchers
     * @param file File to load
     */
    public void load(File file) {
        try {
            YmlParser ymlParser = new YmlParser(
                    Domain.getInstance(),
                    file,
                    "Domain"
            );

            String name = "";
            List<String> lore = new ArrayList<>();
            int volume = 0;
            List<Flag.Type> flags = new ArrayList<>();
            boolean singleUse = false;

            YmlParser.Output crName = ymlParser.getString(
                    "name",
                    ""
            );
            if (crName.getSuccess()) {
                name = StringFunctions.color(crName.getString());

                YmlParser.Output crLore = ymlParser.getString(
                        "lore",
                        ""
                );
                if (crLore.getSuccess()) {
                    lore = StringFunctions.stringToList(crLore.getString());
                }

                YmlParser.Output crVolume = ymlParser.getInt(
                        "volume",
                        0
                );
                if (crVolume.getSuccess()) {
                    volume = crVolume.getInt();
                }

                YmlParser.Output crFlags = ymlParser.getStringList(
                        "flags"
                );
                if (crFlags.getSuccess()) {
                    for (String flagName : crFlags.getStringList()) {
                        Flag.Type flag = Flag.flagFromString(flagName);
                        if (flag != null) {
                            flags.add(flag);
                        }
                    }
                }

                YmlParser.Output crSingleUse = ymlParser.getBoolean(
                        "single_use",
                        false
                );
                if (crSingleUse.getSuccess()) {
                    singleUse = crSingleUse.getBoolean();
                }

                Voucher voucher = new Voucher(
                        file.getName().replace(".yml", ""),
                        name,
                        lore,
                        volume,
                        flags,
                        singleUse
                );

                instance.getDebug().log("VoucherManager", "load", "Adding voucher: " + voucher.key());
                Domain.getInstance().getData().vouchers.put(voucher.key(), voucher);

            } else {
                // invalid name - error
                instance.getError().save(
                        "VoucherManager.load.001",
                        "VoucherManager",
                        "load",
                        "Unable to add voucher " + file.getName(),
                        "Name field missing or invalid",
                        Error.Severity.WARN,
                        null
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "VoucherManager.load.002",
                    "VoucherManager",
                    "load",
                    "Unexpected error loading voucher " + file.getName(),
                    ex.getMessage(),
                    Error.Severity.WARN,
                    ex.getStackTrace()
            );
        }


    }

    /**
     * Gets a voucher from the ItemStack provided
     * @param itemStack ItemStack
     * @return Returns the voucher for the ItemStack, returns null if not found or not a voucher
     */
    public Voucher getVoucher(ItemStack itemStack) {

        IbexPersistentData persistentData = new IbexPersistentData(instance);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (persistentData.hasString(itemMeta, "domain_voucher")) {
            String key = persistentData.getString(itemStack, "domain_voucher");
            if (Domain.getInstance().getData().vouchers.containsKey(key)) {
                instance.getDebug().log("VoucherManager", "getVoucher", "Voucher found: " + key);
                return Domain.getInstance().getData().vouchers.get(key);
            }
        } else {
            instance.getDebug().log("VoucherManager", "getVoucher", "Item meta does not have domain_voucher");
        }
        return null;
    }
}
