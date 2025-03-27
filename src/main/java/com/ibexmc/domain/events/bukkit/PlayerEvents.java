package com.ibexmc.domain.events.bukkit;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.api.events.domain.DomainEnterEvent;
import com.ibexmc.domain.api.events.domain.DomainEnterOwnEvent;
import com.ibexmc.domain.api.events.domain.DomainLeaveEvent;
import com.ibexmc.domain.api.events.domain.DomainLeaveOwnEvent;

import com.ibexmc.domain.domainblock.DomainBlock;
import com.ibexmc.domain.domainblock.DomainBlockManager;
import com.ibexmc.domain.domainblock.open.IOpenDomainBlock;
import com.ibexmc.domain.domainblock.open.TypedValue;
import com.ibexmc.domain.domainblock.rent.RentManager;
import com.ibexmc.domain.flag.Flag;
import com.ibexmc.domain.helper.BlockCommands;
import com.ibexmc.domain.helper.DomainFunctions;
import com.ibexmc.domain.util.DomainParticles;
import com.ibexmc.domain.util.functions.PlayerFunctions;
import com.ibexmc.domain.api.events.player.*;
import com.ibexmc.internal.functions.InventoryFunctions;
import com.ibexmc.internal.functions.SoundFunctions;
import com.ibexmc.internal.functions.WorldFunctions;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.ibexmc.domain.domainblock.vouchers.Voucher;
import com.ibexmc.domain.domainblock.vouchers.VoucherManager;
import com.ibexmc.domain.domainblock.OpenDomainBlock;


public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if(!event.isCancelled()) {
            if (event.getBlock() != null) {
                if (PlayerFunctions.isEntityPlayer(event.getPlayer())) {
                    if (domainBlockManager.inField(event.getBlock().getLocation())) {
                        BucketEmpty bucketEmptyEvent = new BucketEmpty(event.getPlayer(), event.getBlock(), event.getBucket());
                        Bukkit.getServer().getPluginManager().callEvent(bucketEmptyEvent);
                        if (bucketEmptyEvent.isCancelled()) {
                            instance.getDebug().log("PlayerEvents", "onBucketEmpty", bucketEmptyEvent.getCancelReason());
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        
        instance.getDebug().log("PlayerEvents", "onBucketFill", event.getBlock().getType().name());
        if(!event.isCancelled()) {
            if (event.getBlock() != null) {
                if (
                        event.getBlock().getType() == Material.LAVA ||
                                event.getBlock().getType() == Material.WATER ||
                                event.getBlock().getType() == Material.BUBBLE_COLUMN
                ) {
                    if (PlayerFunctions.isEntityPlayer(event.getPlayer())) {

                        if (domainBlockManager.inField(event.getBlock().getLocation())) {
                            BucketFill bucketFillEvent = new BucketFill(event.getPlayer(), event.getBlock(), event.getBlock().getType());
                            Bukkit.getServer().getPluginManager().callEvent(bucketFillEvent);
                            if (bucketFillEvent.isCancelled()) {
                                instance.getDebug().log("PlayerEvents", "onBucketFill", bucketFillEvent.getCancelReason());
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if(!event.isCancelled()) {
            if (PlayerFunctions.isEntityPlayer(event.getPlayer())) {
                if (domainBlockManager.inField(event.getPlayer().getLocation())) {
                    CommandPreprocess commandPreprocessEvent = new CommandPreprocess(event.getPlayer(), event.getMessage());
                    Bukkit.getServer().getPluginManager().callEvent(commandPreprocessEvent);
                    if (commandPreprocessEvent.isCancelled()) {
                        instance.getDebug().log("PlayerEvents", "onCommandPreprocess", commandPreprocessEvent.getCancelReason());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if(!event.isCancelled()) {
            if (PlayerFunctions.isEntityPlayer(event.getPlayer())) {
                if (domainBlockManager.inField(event.getPlayer().getLocation())) {
                    PlayerDrop playerDropEvent = new PlayerDrop(event.getPlayer(), event.getItemDrop());
                    Bukkit.getServer().getPluginManager().callEvent(playerDropEvent);
                    if (playerDropEvent.isCancelled()) {
                        instance.getDebug().log("PlayerEvents", "onPlayerDrop", playerDropEvent.getCancelReason());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);

        instance.getDebug().log("&ePlayerEvents", "onPlayerInteract", "Use Interacted Block = " + event.useInteractedBlock() + " Use Item in Hand = " + event.useItemInHand());

        // Adds an entry for ender pearl thrown
        if ((event.getPlayer().getInventory().getItemInMainHand().getType() == Material.ENDER_EYE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.ENDER_PEARL) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Adding an Ender Pearl Thrown entry");
            Domain.getInstance().getGameData().enderPearlThrown().put(event.getPlayer().getUniqueId());
        }

        Location preInteractLocation = event.getPlayer().getLocation();
        if (!(event.useInteractedBlock() == Result.DENY) && !(event.useItemInHand() == Result.DENY))
        {

            //region Voucher Interaction
            try {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (event.getClickedBlock() != null) {
                        if (domainBlockManager.isDomainBlock(event.getClickedBlock())) {
                            DomainBlock domainBlock = domainBlockManager.getFromBlock(event.getClickedBlock());
                            if (domainBlock != null) {
                                if (domainBlockManager.isOwner(event.getPlayer(), domainBlock)) {
                                    VoucherManager voucherManager = new VoucherManager(instance);
                                    Voucher voucher = voucherManager.getVoucher(event.getPlayer().getInventory().getItemInMainHand());
                                    if (voucher != null) {
                                        boolean voucherExists = false;
                                        for (String voucherKey : domainBlock.getVouchers()) {
                                            if (voucherKey.equalsIgnoreCase(voucher.key())) {
                                                voucherExists = true;
                                            }
                                        }
                                        if (!voucherExists) {
                                            InventoryFunctions.addItemStackAmount(event.getPlayer().getInventory().getItemInMainHand(), -1);
                                            DomainParticles.voucherSuccess(domainBlock.getBlockLocation().clone());
                                            SoundFunctions.playSoundWorld(domainBlock.getBlockLocation().clone(), 0.5f, 1.0f, Sound.BLOCK_ANVIL_USE);
                                            domainBlock.addVoucher(voucher.key());
                                            domainBlockManager.domainBlockSave(domainBlock);
                                            if (voucher.volume() > 0) {
                                                domainBlock.setMaxVolume(domainBlock.getMaxVolume() + voucher.volume());
                                            }
                                            if (voucher.flags().size() > 0) {
                                                for (Flag.Type flagType : voucher.flags()) {
                                                    domainBlock.addFlag(new Flag(domainBlock.getPlacedId(), flagType, false));
                                                }
                                            }
                                            instance.getMessage().send(
                                                    event.getPlayer(),
                                                    "voucher_saved",
                                                    "&2SUCCESS &f- Your voucher has been applied",
                                                    null, true
                                            );

                                            instance.getDebug().log(
                                                    "PlayerEvents",
                                                    "onPlayerInteract",
                                                    "Voucher " + voucher.key() + " to be added to " + domainBlock.getPlacedId()
                                            );
                                        } else {
                                            instance.getMessage().send(event.getPlayer(),
                                                    "voucher_exists",
                                                    "&Sorry, this voucher is already applied",
                                                    null, true
                                            );
                                            instance.getDebug().log(
                                                    "PlayerEvents",
                                                    "onPlayerInteract",
                                                    "Voucher " + voucher.key() + " already exists on " + domainBlock.getPlacedId()
                                            );
                                        }
                                    } else {
                                        instance.getDebug().log(
                                                "PlayerEvents",
                                                "onPlayerInteract",
                                                "Not a voucher"
                                        );
                                    }
                                }
                                else {
                                    instance.getMessage().send(event.getPlayer(),
                                            "command_no_perm_do",
                                            "&4You do not have permission to do that",
                                            null, true
                                    );
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {
                instance.getMessage().send(event.getPlayer(),
                        "voucher_error",
                        "&Sorry, this voucher cannot be applied",
                        null, true
                );
                instance.getDebug().log(
                        "PlayerEvents",
                        "onPlayerInteract",
                        "Unexpected error applying voucher: " + ex.getMessage()
                );
            }
            //endregion
            //region Rent Sign Interaction
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (event.getClickedBlock() != null) {
                    BlockData blockData = event.getClickedBlock().getBlockData();
                    if (blockData instanceof Sign || blockData instanceof WallSign) {
                        RentManager rentManager = new RentManager(instance);
                        DomainBlock domainBlock = rentManager.domainBlockFromSign(event.getClickedBlock());
                        if (domainBlock != null) {
                            instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Valid rent sign - placedId: " + domainBlock.getPlacedId());
                            rentManager.rentConfirm(event.getPlayer(), domainBlock.getPlacedId());
                            event.setUseInteractedBlock(Result.DENY);
                            event.setUseItemInHand(Result.DENY);
                            event.setCancelled(true);
                            return;
                        } else {
                            instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Not a valid rent sign");
                        }
                    }
                }
            }
            //endregion

            if (event.getClickedBlock() != null) {
                    PlayerInteract playerInteractEvent = new PlayerInteract(event.getPlayer(), event.getHand(), event.getAction(), event.getClickedBlock());
                    Bukkit.getServer().getPluginManager().callEvent(playerInteractEvent);
                    if (playerInteractEvent.isCancelled()) {
                        instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Cancelled: " + playerInteractEvent.getCancelReason());
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.setUseItemInHand(Event.Result.DENY);

                        Domain.getInstance().getData().addPluginTPPlayer(event.getPlayer().getUniqueId());

                        if (!WorldFunctions.match(preInteractLocation, event.getPlayer().getLocation(), false)) {
                            instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Player has moved, teleporting back");
                            event.getPlayer().teleport(preInteractLocation, TeleportCause.PLUGIN);
                        }

                        return;
                    }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (event.getRightClicked() != null) {
            if (domainBlockManager.inField(event.getRightClicked().getLocation())) {
                PlayerInteractAtEntity playerInteractAtEntityEvent = new PlayerInteractAtEntity(event.getPlayer(), event.getHand(), event.getClickedPosition(), event.getRightClicked());
                Bukkit.getServer().getPluginManager().callEvent(playerInteractAtEntityEvent);
                if (playerInteractAtEntityEvent.isCancelled()) {
                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Cancelled: " + playerInteractAtEntityEvent.getCancelReason());
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if(!event.isCancelled()) {
            if (domainBlockManager.inField(event.getPlayer().getLocation())) {
                PlayerInteractEntity playerInteractEntityEvent = new PlayerInteractEntity(event.getPlayer(), event.getHand(), event.getRightClicked());
                Bukkit.getServer().getPluginManager().callEvent(playerInteractEntityEvent);
                if (playerInteractEntityEvent.isCancelled()) {
                    instance.getDebug().log("PlayerEvents", "onPlayerInteractEntity", playerInteractEntityEvent.getCancelReason());
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // PlayerJoin is not cancellable
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        PlayerJoin playerJoinEvent = new PlayerJoin(event.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(playerJoinEvent);
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (!Domain.getInstance().getData().moveEventStatus) {
            return;
        }

        if (Domain.getInstance().getData().voidPlayer.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().setVelocity(new Vector(0, 0, 0));
            event.setCancelled(true);
            return;
        }

        if(!event.isCancelled()) {
            if (
                    event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                            event.getFrom().getBlockZ() == event.getTo().getBlockZ()
            ) {
                // Do not run this code if there hasn't been a full grid movement
                return;
            }

            PlayerMove playerMoveEvent = new PlayerMove(event.getPlayer(), event.getFrom(), event.getTo());

            int worldMin =  event.getTo().getWorld().getMinHeight();
            int worldMax =  event.getTo().getWorld().getMaxHeight();
            if (worldMin < 0) {
                worldMin++;
            } else {
                worldMin--;
            }
            if (Domain.getInstance().config().isVoidCheck() && (event.getTo().getBlockY() <= worldMin ||  event.getTo().getBlockY() >= worldMax)) {
                event.getPlayer().setVelocity(new Vector(0, 0, 0));
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 99));
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 99));
                Domain.getInstance().getData().voidPlayer.add(event.getPlayer().getUniqueId());
                if (Domain.getInstance().getData().voidLocation != null && WorldFunctions.safeTeleport(Domain.getInstance().getData().voidLocation)) {
                    instance.getDebug().log(
                            "PlayerEvents",
                            "onPlayerMove",
                            "Teleporting the player back to preset location " + WorldFunctions.simpleReadable(Domain.getInstance().getData().voidLocation)
                    );
                    event.getPlayer().teleport(Domain.getInstance().getData().voidLocation);
                } else {
                    Location bed = event.getPlayer().getBedSpawnLocation();
                    if (bed != null) {
                        instance.getDebug().log(
                                "PlayerEvents",
                                "onPlayerMove",
                                "Teleporting the player back to bed " + WorldFunctions.simpleReadable(bed)
                        );
                        event.getPlayer().teleport(bed);
                    } else {
                        instance.getDebug().log(
                                "PlayerEvents",
                                "onPlayerMove",
                                "Teleporting the player back to world spawn " + WorldFunctions.simpleReadable(event.getPlayer().getLocation().getWorld().getSpawnLocation())
                        );
                        event.getPlayer().teleport(event.getPlayer().getLocation().getWorld().getSpawnLocation());
                    }
                }
            }

            Bukkit.getServer().getPluginManager().callEvent(playerMoveEvent);
            if (playerMoveEvent.isCancelled()) {
                instance.getDebug().log("PlayerEvents", "onPlayerMove", playerMoveEvent.getCancelReason());
                event.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if(!event.isCancelled()) {
            if (domainBlockManager.inField(event.getPlayer().getLocation())) {
                PlayerShear playerShearEvent = new PlayerShear(event.getPlayer(), event.getEntity());
                Bukkit.getServer().getPluginManager().callEvent(playerShearEvent);
                if (playerShearEvent.isCancelled()) {
                    instance.getDebug().log("PlayerEvents", "onPlayerShear", playerShearEvent.getCancelReason());
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // PlayerQuit is not cancellable
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        PlayerQuit playerQuitEvent = new PlayerQuit(event.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(playerQuitEvent);
    }
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent evt) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        instance.getDebug().log("PlayerEvents", "onPlayerPortal", "onPlayerPortal called");
        if(evt.isCancelled()) {
            instance.getDebug().log("PlayerEvents", "onPlayerPortal", "Event was cancelled");
            return;
        } else {
            if (evt.getPlayer() != null)
            {
                boolean hasBypass = PlayerFunctions.hasBypass(evt.getPlayer());
                boolean isAllowed = domainBlockManager.isAllowed(evt.getPlayer(), evt.getFrom());

                if (domainBlockManager.hasFlag(evt.getFrom(), Flag.Type.PREVENT_NETHER_PORTAL_BY_NONALLOWED) && evt.getCause() == TeleportCause.NETHER_PORTAL)
                {
                    if (!hasBypass && !isAllowed)
                    {
                        instance.getMessage().send(evt.getPlayer(),"deny_nether_portal", "&4You cannot use this Nether Portal",  null, true);

                        evt.setCancelled(true);
                    }
                }
                if (domainBlockManager.hasFlag(evt.getFrom(), Flag.Type.PREVENT_END_PORTAL_BY_NONALLOWED) && evt.getCause() == TeleportCause.END_PORTAL)
                {
                    if (!hasBypass && !isAllowed)
                    {
                        instance.getMessage().send(evt.getPlayer(),"deny_end_portal", "&4You cannot use this End Portal",null, true);
                        evt.setCancelled(true);
                    }
                }

                DomainBlock pbFrom = domainBlockManager.getCurrentField(evt.getFrom());
                DomainBlock pbTo = domainBlockManager.getCurrentField(evt.getTo());
                int iFrom = -1;
                int iTo = -1;
                if (pbFrom != null)
                {
                    instance.getDebug().log("PlayerEvents", "onPlayerTeleport", evt.getPlayer().getName() + " has teleported from " + pbFrom.getFieldName());
                    iFrom = pbFrom.getPlacedId();
                }
                if (pbTo != null)
                {
                    instance.getDebug().log("PlayerEvents", "onPlayerTeleport", evt.getPlayer().getName() + " has teleported to " + pbTo.getFieldName());
                    iTo= pbTo.getPlacedId();
                }

                if (iFrom != iTo) {
                    if (iTo >= 0) {
                        // Trigger the Domain Enter event
                        Bukkit.getServer().getPluginManager().callEvent(new DomainEnterEvent(evt.getPlayer(), pbTo));
                        // If the player is the owner of the field they're entering, trigger the Domain Enter Own event
                        if (pbTo.getOwner().equals(evt.getPlayer().getUniqueId())) {
                            Bukkit.getServer().getPluginManager().callEvent(new DomainEnterOwnEvent(evt.getPlayer(), pbTo));
                        }
                    }
                }
                if (iFrom != iTo) {
                    if (iFrom >= 0) {
                        // Trigger the Domain Enter event
                        Bukkit.getServer().getPluginManager().callEvent(new DomainLeaveEvent(evt.getPlayer(), pbFrom));
                        // If the player is the owner of the field they're entering, trigger the Domain Enter Own event
                        if (pbFrom.getOwner().equals(evt.getPlayer().getUniqueId())) {
                            Bukkit.getServer().getPluginManager().callEvent(new DomainLeaveOwnEvent(evt.getPlayer(), pbFrom));
                        }
                    }
                }

            }
            if (evt.isCancelled())
            {
                Block b = DomainFunctions.getNearestEmptySpace(evt.getPlayer().getLocation().getBlock(), 5);
                if (b != null)
                {
                    evt.getPlayer().teleport(b.getLocation());
                }
            }
        }
    }
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent evt) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (evt.getCause() != null) {
            instance.getDebug().log("PlayerEvents", "onTeleport", "Event: " + evt.getCause().name());
        }
        instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Teleport cause: " + evt.getCause().name());
        if (Domain.getInstance().getData().isAllowPluginTPPlayer(evt.getPlayer().getUniqueId())) {
            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Not blocking PLUGIN teleport as player is being blocked from interaction");
            return;
        }

        if (evt.getCause().equals(TeleportCause.PLUGIN) && Domain.getInstance().getData().voidPlayer.contains(evt.getPlayer().getUniqueId())) {
            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Not blocking teleport as the player is out of the world");
            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Teleporting to " + WorldFunctions.simpleReadable(evt.getTo()));
            Domain.getInstance().getData().voidPlayer.remove(evt.getPlayer().getUniqueId());
            evt.setCancelled(false);
            return;
        }

        if (evt.getPlayer() != null && evt.getFrom() != null && evt.getTo() != null)
        {
            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Teleport From: " + WorldFunctions.simpleReadable(evt.getFrom()));
            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Teleport To: " + WorldFunctions.simpleReadable(evt.getTo()));
            boolean hasBypass = PlayerFunctions.hasBypass(evt.getPlayer());
            boolean isAllowed = false;
            if (evt.getPlayer() != null && evt.getTo() != null) {
                isAllowed = domainBlockManager.isAllowed(evt.getPlayer(), evt.getTo());
            }
            DomainBlock pbFrom = domainBlockManager.getCurrentField(evt.getFrom());
            DomainBlock pbTo = domainBlockManager.getCurrentField(evt.getTo());
            int iFrom = -1;
            int iTo = -1;
            if (pbFrom != null)
            {
                instance.getDebug().log("PlayerEvents", "onPlayerTeleport", evt.getPlayer().getName() + " has teleported from " + pbFrom.getFieldName());
                iFrom = pbFrom.getPlacedId();
            }
            if (pbTo != null)
            {
                instance.getDebug().log("PlayerEvents", "onPlayerTeleport", evt.getPlayer().getName() + " has teleported to " + pbTo.getFieldName());
                iTo= pbTo.getPlacedId();
            }

            if (iFrom != iTo) {
                if (iTo >= 0) {
                    // Trigger the Domain Enter event
                    Bukkit.getServer().getPluginManager().callEvent(new DomainEnterEvent(evt.getPlayer(), pbTo));
                    // If the player is the owner of the field they're entering, trigger the Domain Enter Own event
                    if (pbTo.getOwner() != null) {
                        if (pbTo.getOwner().equals(evt.getPlayer().getUniqueId())) {
                            Bukkit.getServer().getPluginManager().callEvent(new DomainEnterOwnEvent(evt.getPlayer(), pbTo));
                        }
                    }
                }
            }
            if (iFrom != iTo) {
                if (iFrom >= 0) {
                    // Trigger the Domain Enter event
                    Bukkit.getServer().getPluginManager().callEvent(new DomainLeaveEvent(evt.getPlayer(), pbFrom));
                    // If the player is the owner of the field they're entering, trigger the Domain Enter Own event
                    if (pbFrom.getOwner() != null) {
                        if (pbFrom.getOwner().equals(evt.getPlayer().getUniqueId())) {
                            Bukkit.getServer().getPluginManager().callEvent(new DomainLeaveOwnEvent(evt.getPlayer(), pbFrom));
                        }
                    }
                }
            }

            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Checking TELEPORT_TO");
            if (domainBlockManager.hasFlag(evt.getTo(), Flag.Type.TELEPORT_TO)) {
                instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "TELEPORT_TO exists here");
                if (pbTo.getTeleportToLocation() != null) {
                    instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Teleport location exists");
                    if (!hasBypass)  {
                        Bukkit.getScheduler().runTaskLater(Domain.getInstance(), new Runnable(){
                            public void run(){
                                instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Teleporting");
                                evt.getPlayer().teleport(pbTo.getTeleportToLocation(), TeleportCause.COMMAND);
                            }
                        }, 20);
                        return;
                    }
                }
            }


            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Checking PREVENT_TELEPORT_TO");
            if (domainBlockManager.hasFlag(evt.getTo(), Flag.Type.PREVENT_TELEPORT_TO)) {
                instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "PREVENT_TELEPORT_TO exists here");
                if (!hasBypass && !isAllowed)
                {
                    instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Player is not allowed to teleport to here");
                    instance.getMessage().send(evt.getPlayer(), "deny_teleport_to", "&4You cannot teleport to this location", null, true);

                    evt.setCancelled(true);
                    return;
                } else {
                    instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Player has access or bypass");
                }
            }

            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Checking PVP TELEPORT CHECKS");
            if (!evt.isCancelled()) {
                Timestamp ts = Domain.getInstance().getData().getPVPListTimestamp(evt.getPlayer());
                if (ts != null) {
                    long milliseconds = new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime();
                    int seconds = (int) milliseconds / 1000;
                    if (seconds < Domain.getInstance().config().getPvpNoTeleportTime())
                    {
                        Map<String, String> parameters = new HashMap<>();
                        parameters.put("<%pvpseconds%>", Integer.toString(Domain.getInstance().config().getPvpNoTeleportTime() - seconds));


                        if (domainBlockManager.hasFlag(evt.getFrom(), Flag.Type.PREVENT_PVP_TELEPORT_FROM))
                        {
                            if (!hasBypass && !isAllowed)
                            {
                                instance.getMessage().send(evt.getPlayer(), "deny_pvp_teleport_from", "&4You cannot teleport from this area after engaging in PVP.  Time remaining: <%pvpseconds%>" , parameters, true);
                                evt.setCancelled(true);
                                return;
                            }
                        }
                        if (domainBlockManager.hasFlag(evt.getTo(), Flag.Type.PREVENT_PVP_TELEPORT_TO))
                        {
                            if (!hasBypass && !isAllowed)
                            {
                                instance.getMessage().send(evt.getPlayer(), "deny_pvp_teleport_to", "&4You cannot teleport to this area after engaging in PVP.  Time remaining: <%pvpseconds%>" , parameters, true);
                                evt.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
            if (!evt.isCancelled()) {
                instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Event is not cancelled - checking if ender pearl or chorus fruit");
                if (evt.getCause() == TeleportCause.ENDER_PEARL) {

                    instance.getDebug().log("PlayerEvents", "onPlayerInteract", "Adding an Ender Pearl Thrown entry");
                    Domain.getInstance().getGameData().enderPearlThrown().put(evt.getPlayer().getUniqueId());

                    if (domainBlockManager.hasFlag(evt.getTo(), Flag.Type.PREVENT_ENDERPEARL_TO)) {
                        instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "PREVENT_ENDERPEARL_TO exists here");
                        if (!hasBypass && !isAllowed) {
                            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Player is not allowed to enderpearl teleport to here");
                            instance.getMessage().send(evt.getPlayer(), "deny_enderpearl_to", "&4You cannot Enderpearl teleport to this location", null, true);
                            evt.setCancelled(true);
                        } else {
                            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Player has access or bypass");
                        }
                    }
                }
                if (evt.getCause() == TeleportCause.CHORUS_FRUIT)
                {
                    if (domainBlockManager.hasFlag(evt.getTo(), Flag.Type.PREVENT_CHORUSFRUIT_TO))
                    {
                        instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "PREVENT_CHORUSFRUIT_TO exists here");
                        if (!hasBypass && !isAllowed)
                        {
                            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Player is not allowed to Chorus Fruit teleport to here");
                            instance.getMessage().send(evt.getPlayer(), "deny_chorusfruit_to", "&4You cannot Chorus Fruit teleport to this location", null, true);
                            evt.setCancelled(true);
                        } else {
                            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Player has access or bypass");
                        }
                    }
                }
            }
            if (!evt.isCancelled()) // Check if we're entering or leaving a new field, if so, display farewell/welcome messages as needed
            {
                if (iFrom != iTo)
                {
                    if (pbFrom != null)
                    {
                        if (domainBlockManager.hasFlag(evt.getFrom(), Flag.Type.DISPLAY_FAREWELL))
                        {
                            String farewellMsg = pbFrom.getFarewellMessage();
                            if (!farewellMsg.equals(""))
                            {
                                farewellMsg = farewellMsg.replaceAll("@p", evt.getPlayer().getName());
                                //instance.getMessage().send("na", ColorFunctions.colorToString(farewellMsg), evt.getPlayer(), true, null); // Farewell message is player set, so no locale code needed
                                instance.getMessage().actionBar(evt.getPlayer(), farewellMsg);
                            }
                        }
                    }
                    if (pbTo != null)
                    {
                        if (domainBlockManager.hasFlag(evt.getTo(), Flag.Type.DISPLAY_WELCOME))
                        {
                            String welcomeMsg = pbTo.getWelcomeMessage();
                            if (!welcomeMsg.equals(""))
                            {
                                welcomeMsg = welcomeMsg.replaceAll("@p", evt.getPlayer().getName());
                                //instance.getMessage().send("na", ColorFunctions.colorToString(welcomeMsg), evt.getPlayer(), true, null); // Welcome message is player set, so no locale code needed
                                instance.getMessage().actionBar(evt.getPlayer(), welcomeMsg);

                            }
                        }
                    }
                }
            }
            instance.getDebug().log("PlayerEvents", "onPlayerTeleport", "Event cancelled = " + evt.isCancelled());
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Domain instance = Domain.getInstance();
        DomainBlockManager domainBlockManager = new DomainBlockManager(instance);
        if (Domain.getInstance().getData().openBlocks.containsKey(event.getPlayer().getUniqueId())) {
            IOpenDomainBlock openDomainBlock = Domain.getInstance().getData().openBlocks.get(event.getPlayer().getUniqueId());
            if (openDomainBlock instanceof TypedValue) {
                TypedValue typedValue = (TypedValue) openDomainBlock;
                Bukkit.getScheduler().runTaskLater(Domain.getInstance(), () -> {
                    typedValue.set(event.getMessage());
                }, 2);

                event.setCancelled(true);
                return;
            }
        }
        if (Domain.getInstance().getData().openDomainBlockExists(event.getPlayer().getUniqueId())) {
            OpenDomainBlock openDomainBlock = Domain.getInstance().getData().openDomainBlockGet(event.getPlayer().getUniqueId());
            if (openDomainBlock != null) {
                DomainBlock domainBlock = domainBlockManager.getDomainBlockByID(openDomainBlock.getPlacedID());
                boolean updateBlock = false;
                Map<String, String> placeHolders = new HashMap<>();
                if (domainBlock != null) {
                    switch (openDomainBlock.getOpenDomainBlockType())
                    {
                        case ALLOW_PLAYER:
                            event.setCancelled(true);
                            boolean foundPlayer = false;
                            UUID playerUUID = null;
                            OfflinePlayer allowOfflinePlayer = PlayerFunctions.offlinePlayerFromName(event.getMessage());
                            if (allowOfflinePlayer != null) {
                                if (allowOfflinePlayer.hasPlayedBefore()) {
                                    playerUUID = allowOfflinePlayer.getUniqueId();
                                    if (domainBlock.getAllowedPlayers().contains(playerUUID)) {
                                        // Already in
                                        instance.getMessage().send(event.getPlayer(), "allow_player_already_in", "&4Player is already allowed into this field", null, true);
                                        instance.getMessage().send(event.getPlayer(), "allow_player_not_found1", "&4Player add cancelled.  You are now returned to chat.", null, true);
                                        Domain.getInstance().getData().openDomainBlockRemove(event.getPlayer().getUniqueId());
                                        return;
                                    } else {
                                        // Add
                                        domainBlock.addAllowedPlayer(playerUUID);
                                        domainBlock.setDirty(true);
                                        instance.getMessage().send(event.getPlayer(),"allow_player_found", "&2SUCCESS - Player has been added to the field", null, true);
                                    }
                                } else {
                                    // invalid player
                                    instance.getMessage().send(event.getPlayer(),"allow_player_not_found", "&4Unable to allow player.  Player not found", null, true);
                                    instance.getMessage().send(event.getPlayer(),"allow_player_not_found1", "&4Player add cancelled.  You are now returned to chat.", null, true);
                                    Domain.getInstance().getData().openDomainBlockRemove(event.getPlayer().getUniqueId());
                                }
                            } else {
                                // invalid player
                                instance.getMessage().send(event.getPlayer(),"allow_player_not_found", "&4Unable to allow player.  Player not found", null, true);
                                instance.getMessage().send(event.getPlayer(),"allow_player_not_found1", "&4Player add cancelled.  You are now returned to chat.", null, true);
                                Domain.getInstance().getData().openDomainBlockRemove(event.getPlayer().getUniqueId());
                            }
                            break;
                        case NAME:
                            String oldname = domainBlock.getFieldName();
                            domainBlock.setFieldName(event.getMessage());
                            placeHolders.put("<%oldname%>", oldname);
                            placeHolders.put("<%newname%>", event.getMessage());
                            instance.getMessage().send(event.getPlayer(), "command_name_success", "Field renamed from '<%oldname%>' to '<%newname%>'" , placeHolders, true);
                            Domain.getInstance().getData().openDomainBlockRemove(event.getPlayer().getUniqueId());
                            updateBlock = true;
                            break;
                        case OWNER:
                            event.setCancelled(true);
                            if (openDomainBlock.getNewOwnerName() != null) {
                                if (!openDomainBlock.getNewOwnerName().equals("")) {
                                    if (ChatColor.stripColor(event.getMessage()).equalsIgnoreCase("confirm " + ChatColor.stripColor(openDomainBlock.getNewOwnerName()))) {
                                        BlockCommands blockCommands = new BlockCommands(instance);
                                        blockCommands.changeOwner(openDomainBlock.getNewOwnerUUID(), domainBlock);
                                        updateBlock = true;
                                    } else {
                                        instance.getMessage().send(event.getPlayer(),"change_owner_fail_confirm", "Your confirmation text was not correct.  Please try again", null, true);
                                    }
                                } else {
                                    instance.getMessage().send(event.getPlayer(), "change_owner_fail_confirm_blank", "Your confirmation text is missing.  Please try again",null, true);
                                }
                            } else {
                                instance.getMessage().send(event.getPlayer(), "change_owner_fail_confirm_blank", "Your confirmation text is missing.  Please try again", null, true);
                            }
                            break;
                        case WELCOME:
                            domainBlock.setWelcomeMessage(event.getMessage());
                            placeHolders.put("<%newmessage%>", event.getMessage());
                            instance.getMessage().send(event.getPlayer(),"command_welcome_success", "Welcome message changed to '<%newmessage%>'" , placeHolders, true);
                            updateBlock = true;
                            break;
                        case FAREWELL:
                            domainBlock.setFarewellMessage(event.getMessage());
                            placeHolders.put("<%newmessage%>", event.getMessage());
                            instance.getMessage().send(event.getPlayer(),"command_farewell_success", "Farewell message changed to '<%newmessage%>'" , placeHolders, true);
                            updateBlock = true;
                            break;
                        case COMMAND_ENTER:
                            domainBlock.clearEnterCommands();
                            domainBlock.addEnterCommand(event.getMessage());
                            placeHolders.put("<%command%>", event.getMessage());
                            instance.getMessage().send(event.getPlayer(), "command_onenter_success", "&aSUCCESS &f- Enter Command Set: '&d<%command%>&f'" , placeHolders, true);
                            updateBlock = true;
                            break;
                        case COMMAND_LEAVE:
                            domainBlock.clearLeaveCommands();
                            domainBlock.addLeaveCommand(event.getMessage());
                            placeHolders.put("<%command%>", event.getMessage());
                            instance.getMessage().send(event.getPlayer(), "command_onleave_success", "&aSUCCESS &f- Leave Command Set: '&d<%command%>&f'" , placeHolders, true);
                            updateBlock = true;
                            break;
                        case ADD_FLAG:
                            //TODO: GUI with all flags, except those already assigned to the field
                        default:
                            break;
                    }
                }
                if (updateBlock) {
                    domainBlockManager.domainBlockSave(domainBlock);
                    event.setCancelled(true);
                    Domain.getInstance().getData().openDomainBlockRemove(event.getPlayer().getUniqueId());
                }
            }
        }
    }


}
