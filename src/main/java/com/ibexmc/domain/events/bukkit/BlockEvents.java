package com.ibexmc.domain.events.bukkit;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.api.events.block.BlockPlaceDomainEvent;
import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.domain.events.eventcode.block.BlockBreak;
import com.ibexmc.domain.events.eventcode.block.BlockBurn;
import com.ibexmc.internal.functions.InventoryFunctions;
import com.ibexmc.internal.functions.StringFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import com.ibexmc.internal.utility.IbexPersistentData;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.Bukkit;

import com.ibexmc.domain.api.events.block.BlockBreakDomainEvent;
import com.ibexmc.domain.api.events.block.BlockBurnDomainEvent;
import com.ibexmc.domain.api.events.block.BlockFromDomainEvent;
import com.ibexmc.domain.api.events.block.BlockGrowDomainEvent;
import com.ibexmc.domain.api.events.block.BlockIgniteDomainEvent;
import com.ibexmc.domain.api.events.block.BlockSpreadDomainEvent;
import com.ibexmc.domain.api.events.block.BlockToDomainEvent;
import com.ibexmc.domain.api.events.block.PistonExtendDomainEvent;
import com.ibexmc.domain.api.events.block.SignChangeDomainEvent;
import com.ibexmc.domain.api.events.block.SpongeAbsorbDomainEvent;
import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.flag.redstone.TNT;

public class BlockEvents implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        if (domainBlockManager.isDomainBlock(event.getBlock())) {
            instance.getDebug().log(
                    "BlockEvents",
                    "onBlockBreak",
                    "Event Cancelled: Domain blocks cannot be broken"
            );
            instance.getMessage().send(event.getPlayer(), "break_domain_block", "&cSorry, you cannot break a Domain block", null, true);
            event.setCancelled(true);
            return;
        }

        instance.getDebug().log(
                "BlockEvents",
                "onBlockBreak",
                "Block Break: " + event.getBlock().getType().name()
        );

        if(!event.isCancelled()) {

            //region Rent Sign Breaking
            BlockData blockData = event.getBlock().getBlockData();
            if (blockData instanceof Sign || blockData instanceof WallSign) {
                RentManager rentManager = new RentManager(instance);
                DomainBlock domainBlock = rentManager.domainBlockFromSign(event.getBlock());
                if (domainBlock != null) {
                    instance.getDebug().log("BlockEvents", "onBlockBreak", "Rent Sign broken");
                    rentManager.removeRentSign(domainBlock, event.getBlock());
                }
            }
            //endregion

            if (domainBlockManager.inField(event.getBlock().getLocation())) {
                DomainBlock domainBlock = domainBlockManager.getCurrentField(event.getBlock().getLocation());
                if (domainBlock != null) {
                    BlockBreakDomainEvent blockBreakEvent = new BlockBreakDomainEvent(domainBlock, event.getBlock(), event.getPlayer());
                    Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
                    if (!blockBreakEvent.isCancelled()) {
                        instance.getDebug().log("BlockEvents", "onBlockBreak", "Running onBlockBreak");
                        BlockBreak blockBreak = new BlockBreak();
                        blockBreak.onBlockBreak(domainBlock, event.getBlock(), event.getPlayer());
                        if (blockBreak.isCancelled()) {
                            event.setCancelled(true);
                            instance.getDebug().log(
                                    "BlockEvents",
                                    "onBlockBreak",
                                    "Event Cancelled: " +
                                            StringFunctions.replaceNullOrBlank(
                                                    blockBreakEvent.getCancelReason(),
                                                    "Event was cancelled by Domain"
                                            )
                            );
                            return;
                        }
                    } else {
                        instance.getDebug().log(
                                "BlockEvents",
                                "onBlockBreak",
                                "Event Cancelled: " +
                                        StringFunctions.replaceNullOrBlank(
                                                blockBreakEvent.getCancelReason(),
                                                "Event was cancelled externally"
                                        )
                        );
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        if(!event.isCancelled()) { // If the Bukkit BlockBurnEvent has not already been cancelled

            //region Rent Sign Breaking
            BlockData blockData = event.getBlock().getBlockData();
            if (blockData instanceof Sign || blockData instanceof WallSign) {
                RentManager rentManager = new RentManager(instance);
                DomainBlock domainBlock = rentManager.domainBlockFromSign(event.getBlock());
                if (domainBlock != null) {
                    instance.getDebug().log("BlockEvents", "onBlockBurn", "Rent Sign burned");
                    rentManager.removeRentSign(domainBlock, event.getBlock());
                }
            }
            //endregion

            if (domainBlockManager.inField(event.getBlock().getLocation())) { // Check if the burn is inside a Domain field
                BlockBurnDomainEvent blockBurnEvent = new BlockBurnDomainEvent(event.getBlock()); // Create the Domain Event
                Bukkit.getServer().getPluginManager().callEvent(blockBurnEvent);
                if (!blockBurnEvent.isCancelled()) { // If the Domain Event was not cancelled run the BlockBurn code
                    instance.getDebug().log("BlockEvents", "onBlockBreak", "Running onBlockBreak");
                    BlockBurn blockBurn = new BlockBurn();
                    blockBurn.onBlockBurn(event.getBlock());
                    if (blockBurn.isCancelled()) { // Check if the BlockBurn code wants to cancel the Bukkit BlockBurnEvent
                        event.setCancelled(true); // Cancel the Bukkit BlockBurnEvent
                        instance.getDebug().log(
                                "BlockEvents",
                                "onBlockBurn",
                                "Event Cancelled: " +
                                        StringFunctions.replaceNullOrBlank(
                                                blockBurnEvent.getCancelReason(),
                                                "Event was cancelled by Domain"
                                        )
                        );
                        return;
                    }
                } else { // The Domain Event was cancelled
                    instance.getDebug().log(
                            "BlockEvents",
                            "onBlockBurn",
                            "Event Cancelled: " +
                                    StringFunctions.replaceNullOrBlank(
                                            blockBurnEvent.getCancelReason(),
                                            "Event was cancelled externally"
                                    )
                    );
                    event.setCancelled(true); // Cancel the Bukkit BlockBurnEvent
                    return;
                }

            }
        }
    }
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        if(!event.isCancelled()) {
            // Check if it's a liquid first
            if (event.getBlock().isLiquid()) {

                boolean sourceIsDomainField = domainBlockManager.inField(event.getBlock().getLocation());
                boolean destinationIsDomainField = domainBlockManager.inField(event.getToBlock().getLocation());

                if (sourceIsDomainField ) {
                    BlockFromDomainEvent blockFromEvent = new BlockFromDomainEvent(event.getBlock(), event.getToBlock());
                    Bukkit.getServer().getPluginManager().callEvent(blockFromEvent);
                    if (blockFromEvent.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    if (destinationIsDomainField) {
                        instance.getDebug().log("BlockEvents", "onBlockFromTo (To)", "Destination is a domain field");
                        BlockToDomainEvent blockToEvent = new BlockToDomainEvent(event.getBlock(), event.getToBlock());
                        Bukkit.getServer().getPluginManager().callEvent(blockToEvent);
                        if (blockToEvent.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }




            }
        }
    }
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        if(!event.isCancelled()) {
            if (domainBlockManager.inField(event.getBlock().getLocation())) {
                BlockGrowDomainEvent blockGrowEvent = new BlockGrowDomainEvent(event.getBlock());
                Bukkit.getServer().getPluginManager().callEvent(blockGrowEvent);
                if (blockGrowEvent.isCancelled()) {
                    instance.getDebug().log("BlockEvents", "onBlockGrow", blockGrowEvent.getCancelReason());
                    event.setCancelled(true);
                    return;
                }

            }
        }
    }
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        if(!event.isCancelled()) {
            if (domainBlockManager.inField(event.getBlock().getLocation())) {
                DomainBlock domainBlock = domainBlockManager.getCurrentField(event.getBlock().getLocation());
                if (domainBlock != null) {
                    BlockIgniteDomainEvent blockIgniteEvent = new BlockIgniteDomainEvent(domainBlock, event.getBlock(), event.getCause(), event.getPlayer());
                    Bukkit.getServer().getPluginManager().callEvent(blockIgniteEvent);
                    if (blockIgniteEvent.isCancelled()) {
                        instance.getDebug().log("BlockEvents", "onBlockIgnite", blockIgniteEvent.getCancelReason());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {

        Domain instance = Domain.getInstance();

        if(!event.isCancelled()) {
            PistonExtendDomainEvent pistonExtendEvent = new PistonExtendDomainEvent(event.getDirection(), event.getBlocks());
            Bukkit.getServer().getPluginManager().callEvent(pistonExtendEvent);
            if (pistonExtendEvent.isCancelled()) {
                instance.getDebug().log("BlockEvents", "onBlockPistonExtend", pistonExtendEvent.getCancelReason());
                event.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        RentManager rentManager = new RentManager(instance);
        IbexPersistentData persistentData = new IbexPersistentData(instance);

        if(!event.isCancelled()) {

            instance.getDebug().log("BlockEvents", "onBlockPlace", "Placing " + event.getBlock().getType().name());


            //region Rent Sign
            BlockData blockData = event.getBlock().getBlockData();
            if (blockData instanceof Sign || blockData instanceof WallSign) {
                instance.getDebug().log("BlockEvents", "onBlockPlace", "Sign or WallSign found");
                if (persistentData.hasInt(event.getItemInHand(), "domain_rent_sign")) {
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "Rent sign found");
                    int placedId = persistentData.getInt(event.getItemInHand(), "domain_rent_sign");
                    if (placedId >= 0) {
                        if (rentManager.newSign(event.getBlock(), blockData, placedId)) {
                            instance.getDebug().log("BlockEvents", "onBlockPlace", "New rent sign created, cancelling event");
                            event.getPlayer().getInventory().setItemInMainHand(
                                    InventoryFunctions.addItemStackAmount(
                                            event.getPlayer().getInventory().getItemInMainHand(),
                                            -1
                                    )
                            );
                            event.setCancelled(true);
                        } else {
                            instance.getDebug().log("BlockEvents", "onBlockPlace", "New rent sign failed");
                        }
                    } else {
                        instance.getDebug().log("BlockEvents", "onBlockPlace", "Invalid placed id in rent data");
                    }
                } else {
                    instance.getDebug().log("BlockEvents", "onBlockPlace", "No rent sign found, regular sign");
                }
            }
            //endregion



            // No check if they are in a field as this event handles placing new Domain blocks
            BlockPlaceDomainEvent blockPlaceEvent = new BlockPlaceDomainEvent(event.getBlock(), event.getPlayer());
            Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
            if (blockPlaceEvent.isCancelled()) {
                instance.getDebug().log("BlockEvents", "onBlockPlace", blockPlaceEvent.getCancelReason());
                event.setCancelled(true);
                return;
            }
        } else {
            instance.getDebug().log("BlockEvents", "onBlockPlace", "Event is already cancelled");
        }
    }
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if(!event.isCancelled()) {
            if (domainBlockManager.inField(event.getBlock().getLocation())) {
                BlockSpreadDomainEvent blockSpreadEvent = new BlockSpreadDomainEvent(event.getSource(), event.getBlock());
                Bukkit.getServer().getPluginManager().callEvent(blockSpreadEvent);
                if (blockSpreadEvent.isCancelled()) {
                    instance.getDebug().log("BlockEvents", "onBlockSpread", blockSpreadEvent.getCancelReason());
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if(!event.isCancelled()) {

            if (event.getLine(0) != null) {
                if(event.getLine(0).equalsIgnoreCase("[Domain]")) {
                    // No check if they are in a field as this event handles placing rent signs
                    SignChangeDomainEvent signChangeEvent = new SignChangeDomainEvent(event.getBlock(), event.getPlayer());
                    Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
                    if (signChangeEvent.isCancelled()) {
                        instance.getDebug().log("BlockEvents", "onSignChange", signChangeEvent.getCancelReason());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        if(!event.isCancelled()) {
            if (domainBlockManager.inField(event.getBlock().getLocation())) {
                SpongeAbsorbDomainEvent spongeAbsorbEvent = new SpongeAbsorbDomainEvent(event.getBlock());
                Bukkit.getServer().getPluginManager().callEvent(spongeAbsorbEvent);
                if (spongeAbsorbEvent.isCancelled()) {
                    instance.getDebug().log("BlockEvents", "onSpongeAbsorb", spongeAbsorbEvent.getCancelReason());
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onTNTPrime(TNTPrimeEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        instance.getDebug().log("BlocKEvents", "onTNTPrimed", "TNT Prime Event Started");
        instance.getDebug().log(
                "BlocKEvents",
                "onTNTPrimed",
                "Block Location: " + WorldFunctions.simpleFancyReadable(event.getBlock().getLocation())
        );
        if (TNT.Priming.prevent(event.getBlock())) {
            instance.getDebug().log("PaperEvent", "onTNTPrimed", "TNT Prime Event Cancelled");
            event.setCancelled(true);
        }
    }
}
