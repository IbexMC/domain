package com.ibexmc.domain.domainblock.storage;

import com.ibexmc.domain.domainblock.DomainBlock;


public interface IDomainStorage {

    void checkStorage();
    void loadDomainFields();
    void loadDomainField(int fieldId);
    void alterField(DomainBlock domainBlock);
    void deleteField(int fieldId);
    void saveField(DomainBlock domainBlock);
    void clearDomainData();

}
