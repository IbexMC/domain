package com.ibexmc.domain.domainblock.open;

import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.internal.functions.NumberFunctions;
import org.bukkit.entity.Player;
import com.ibexmc.domain.Domain;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;

import java.util.HashMap;
import java.util.Map;

public class TypedValue implements IOpenDomainBlock {

    //region Objects
    private int placedId;
    private Type type;
    private Player player;
    private final Domain instance;
    private DomainBlockManager domainBlockManager;
    public enum Type {
        RENT_COST,
        RENT_DAYS
    }
    //endregion
    //region Constructor
    public TypedValue(int placedId, Type type, Player player) {
        this.placedId = placedId;
        this.type = type;
        this.player = player;
        this.instance = Domain.getInstance();
        domainBlockManager = new DomainBlockManager(instance);
    }
    //endregion
    //region Methods
    /**
     * Gets the Domain Block Identifier
     * @return Domain Block Identifier
     */
    public int placedId() {
        return this.placedId;
    }
    public boolean set(String value) {

        RentManager rentManager = new RentManager(instance);
        DomainBlock domainBlock = domainBlockManager.getDomainBlockById(this.placedId);
        if (domainBlock == null) {
            return false;
        }
        Map<String, String> placeHolder = new HashMap<>();
        placeHolder.put("<%blocktype%>", domainBlock.getConfig().getDisplayName());
        switch (type) {
            case RENT_COST:
                placeHolder.put("<%cost%>", value);
                if (NumberFunctions.isDouble(value)) {
                    domainBlock.setRentCost(NumberFunctions.stringToDouble(value));
                    rentManager.updateSigns(domainBlock.getPlacedId());
                    if (NumberFunctions.stringToDouble(value) > 0) {
                        instance.getMessage().send(
                                player,
                                "rent_set_cost",
                                "Rent for this <%blocktype%> field has been set to $<%cost%>",
                                placeHolder, true
                        );
                    } else {
                        instance.getMessage().send(
                                player,
                                "rent_set_cost_zero",
                                "This <%blocktype%> field is no longer available to rent.  Set the cost to at least $1 to make available",
                                placeHolder, true
                        );
                    }
                    domainBlock.setDirty(true);
                    domainBlockManager.domainBlockSave(domainBlock);
                    Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
                    return true;
                } else {
                    instance.getMessage().send(
                            player,
                            "rent_set_cost_error",
                            "&cERROR - &fUnable to set rent cost to <%value%>",
                            placeHolder, true
                    );
                    return false;
                }
            case RENT_DAYS:
                placeHolder.put("<%days%>", value);
                if (NumberFunctions.isInt(value)) {
                    domainBlock.setRentDays(NumberFunctions.stringToInt(value));
                    rentManager.updateSigns(domainBlock.getPlacedId());
                    if (NumberFunctions.stringToInt(value) > 0) {
                        instance.getMessage().send(
                                player,
                                "rent_set_days",
                                "Number of days in the rent period for this <%blocktype%> field has been set to <%days%>",
                                placeHolder, true
                        );
                    } else {
                        instance.getMessage().send(
                                player,
                                "rent_set_days_zero",
                                "This <%blocktype%> field is no longer available to rent.  Set the number of days in the rent period to at least 1 to make available",
                                placeHolder, true
                        );
                    }
                    domainBlock.setDirty(true);
                    domainBlockManager.domainBlockSave(domainBlock);
                    Domain.getInstance().getData().openBlocks.remove(player.getUniqueId());
                    return true;
                } else {
                    instance.getMessage().send(
                            player,
                            "rent_set_days_error",
                            "&cERROR - &fUnable to set rent days to <%value%>",
                            placeHolder, true
                    );
                    return false;
                }
            default:
                break;
        }
        return false;
    }
    //endregion
}
