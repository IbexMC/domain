package com.ibexmc.domain.util.functions;

import com.ibexmc.domain.Domain;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.messaging.Error;
import com.ibexmc.internal.utility.YmlParser;
import org.bukkit.Material;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaterialGroups {

    private Domain instance;

    public MaterialGroups(Domain instance) {
        this.instance = instance;
        loadAll();
    }

    public void loadAll() {
        Domain.getInstance().getData().clearMaterialGroups();
        String filePathString = Domain.getInstance().getDataFolder().getAbsolutePath() + File.separator + "material_groups.yml";
        File filePath = new File(filePathString);
        if (filePath.exists()) {
            load(filePath);
        }
    }
    public void load(File file) {
        try {
            YmlParser ymlParser = new YmlParser(
                    Domain.getInstance(),
                    file,
                    "Domain"
            );

            List<String> keys = ymlParser.getKeys("");
            for (String key : keys) {
                YmlParser.Output outMaterials = ymlParser.getStringList(
                        key
                );
                if (outMaterials.getSuccess()) {
                    Set<Material> materials = new HashSet<>();
                    for (String materialName : outMaterials.getStringList()) {
                        Material material = StringFunctions.stringToMaterial(materialName);
                        if (material != null) {
                            materials.add(material);
                        }
                    }
                    instance.getData().putMaterialGroup(key, materials);
                }
            }
        } catch (Exception ex) {
            instance.getError().save(
                    "001",
                    "MaterialGroups",
                    "load",
                    "Unexpected Error",
                    ex.getMessage(),
                    Error.Severity.URGENT,
                    ex.getStackTrace()
            );
        }
    }
}
