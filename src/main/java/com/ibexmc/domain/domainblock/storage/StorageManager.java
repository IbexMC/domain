package com.ibexmc.domain.domainblock.storage;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;

import java.util.Map;

public class StorageManager {

    private final Domain instance;
    private IDomainStorage storage;

    public StorageManager(Domain instance, IDomainStorage storage) {
        this.instance = instance;
        this.storage = storage;
    }
    public StorageManager(Domain instance) {
        this.instance = instance;
        if (instance.config().useMySQL()) {
            storage = new MySQL(instance);
        } else {
            storage = new YamlFile(instance);
        }
        storage.checkStorage();
    }

    public void check() {
        storage.checkStorage();
    }

    public void load(int fieldId) {
        storage.loadDomainField(fieldId);
    }

    public void loadAll() {
        storage.loadDomainFields();
    }

    public void remove(DomainBlock domainBlock) {
        storage.deleteField(domainBlock.getPlacedId());
    }

    public void removeAll() {
        storage.clearDomainData();
    }

    public void save(DomainBlock domainBlock) {
        storage.alterField(domainBlock);
    }

    public void saveAll(boolean onlyIfDirty) {
        for (Map.Entry<Integer, DomainBlock> domainEntry : instance.getData().placedFields.entrySet()) {
            if (!onlyIfDirty || (onlyIfDirty && domainEntry.getValue().isDirty())) {
                save(domainEntry.getValue());
            }
        }
    }

}
