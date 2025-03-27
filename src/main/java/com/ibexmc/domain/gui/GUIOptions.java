package com.ibexmc.domain.gui;

import java.io.File;

import com.ibexmc.internal.functions.ItemFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.messaging.Error;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ibexmc.domain.Domain;

public class GUIOptions {

    private Domain instance;

    public GUIOptions(Domain instance) {
        this.instance = instance;
    }

    public void loadOptions() {
        instance.getDebug().log("GUIOptions", "loadOptions()", "&aLoading GUI Options from gui.yml");
        try {
            File customYml = new File(Domain.getInstance().getDataFolder()+"/gui.yml");

            if (!customYml.exists()) {
                Domain.getInstance().saveResource("gui.yml", false);
            }

            if (customYml.exists())
            {
                FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
                if (customConfig.contains("gui"))
                    for(String key : customConfig.getConfigurationSection("gui").getKeys(false)){

                        boolean validMaterial = false;
                        boolean validName = false;

                        Material material = Material.BARRIER; // This default should never be used, if no valid material is found, it won't add the option
                        String name = ""; // This default should never be used, if no valid name is found, it won't add the option
                        String lore = ""; // Default to a blank lore
                        boolean enabled = true; // default to true

                        // Material
                        if (customConfig.contains("gui." + key + ".material")) {
                            if (customConfig.isString("gui." + key + ".material")) {
                                Material fileMaterial = ItemFunctions.materialFromName(customConfig.getString("gui." + key + ".material"));
                                if (fileMaterial != null) {
                                    if (!fileMaterial.equals(Material.AIR)) {
                                        material = fileMaterial;
                                        validMaterial = true;
                                    } else {
                                        instance.getError().save(
                                                "001",
                                                "GUIOptions",
                                                "loadOptions",
                                                "Invalid material",
                                                "gui." + key + ".material is not a valid material",
                                                Error.Severity.WARN,
                                                null
                                        );
                                    }
                                } else {
                                    instance.getError().save(
                                            "002",
                                            "GUIOptions",
                                            "loadOptions",
                                            "Invalid material",
                                            "gui." + key + ".material is not a valid material",
                                            Error.Severity.WARN,
                                            null
                                    );
                                }
                            } else {
                                instance.getError().save(
                                        "003",
                                        "GUIOptions",
                                        "loadOptions",
                                        "Invalid material",
                                        "gui." + key + ".material is not a String value",
                                        Error.Severity.WARN,
                                        null
                                );
                            }
                        }
                        // Name
                        if (customConfig.contains("gui." + key + ".name"))
                        {
                            if (customConfig.isString("gui." + key + ".name")) {
                                String fileName = customConfig.getString("gui." + key + ".name");
                                if (fileName != null) {
                                    if (!fileName.equals("")) {
                                        name = fileName;
                                        validName = true;
                                    }
                                }
                            } else {
                                instance.getError().save(
                                        "004",
                                        "GUIOptions",
                                        "loadOptions",
                                        "Invalid material",
                                        "gui." + key + ".name is not a String value",
                                        Error.Severity.WARN,
                                        null
                                );
                            }
                        }
                        // Lore
                        if (customConfig.contains("gui." + key + ".lore"))
                        {
                            if (customConfig.isString("gui." + key + ".lore")) {
                                String fileLore = customConfig.getString("gui." + key + ".lore");
                                if (!fileLore.equals("")) {
                                    lore = fileLore;
                                }
                            } else {
                                instance.getError().save(
                                        "005",
                                        "GUIOptions",
                                        "loadOptions",
                                        "Invalid lore",
                                        "gui." + key + ".lore is not a String value",
                                        Error.Severity.WARN,
                                        null
                                );
                            }
                        }
                        // Enabled
                        if (customConfig.contains("gui." + key + ".enabled"))
                        {
                            if (customConfig.isBoolean("gui." + key + ".enabled")) {
                                enabled = customConfig.getBoolean("gui." + key + ".enabled");
                                if (!enabled) {
                                    if (customConfig.contains("gui.disabled.material"))
                                    {
                                        if (customConfig.isString("gui.disabled.material")) {
                                            Material disabledMaterial = ItemFunctions.materialFromName(customConfig.getString("gui.disabled.material"));
                                            if (disabledMaterial != null) {
                                                if (!disabledMaterial.equals(Material.AIR)) {
                                                    material = disabledMaterial;
                                                } else {
                                                    material = Material.BARRIER;
                                                }
                                            } else {
                                                material = Material.BARRIER;
                                            }
                                        } else {
                                            material = Material.BARRIER;
                                        }
                                    } else {
                                        material = Material.BARRIER;
                                    }

                                    if (customConfig.contains("gui.disabled.lore"))
                                    {
                                        if (customConfig.isString("gui.disabled.lore")) {
                                            lore = customConfig.getString("gui.disabled.lore");
                                            if (lore.equals("")) {
                                                lore = "&fThis option is not available";
                                            }
                                        } else {
                                            lore = "&fThis option is not available";
                                        }
                                    } else {
                                        lore = "&fThis option is not available";
                                    }
                                }
                            } else {
                                instance.getError().save(
                                        "006",
                                        "GUIOptions",
                                        "loadOptions",
                                        "Invalid name",
                                        "gui." + key + ".name is invalid",
                                        Error.Severity.WARN,
                                        null
                                );
                            }
                        }
                        // If valid option, add it to the list
                        if (validMaterial && validName) {
                            GUIOption guiOption = new GUIOption(material, name, StringFunctions.stringToList(lore), enabled);

                            Domain.getInstance().getData().putGUIOption(key, guiOption);
                        }
                    }
            } else {
                instance.getLog().log("GUIOptions", "&cERROR", "gui.yml does not exist.  This will cause issues with your GUI Options.  Download the gui.yml and place it in your Domain folder");
                instance.getError().save(
                        "007",
                        "GUIOptions",
                        "loadOptions",
                        "Unable to load file",
                        "flags.yml does not exist",
                        Error.Severity.WARN,
                        null
                );
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "008",
                    "GUIOptions",
                    "loadOptions",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.WARN,
                    ex.getStackTrace()
            );
        }
    }

}
