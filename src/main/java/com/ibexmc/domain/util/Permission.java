package com.ibexmc.domain.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Permission {
    public enum PermissionType {
        ADMIN,
        CREATE
    }
    private static String permissionTypeToString(PermissionType permissionType) {
        String permissionText = "";
        switch (permissionType) {
            case ADMIN:
                permissionText = "domain.admin";
                break;
            default:
                break;
        }
        return permissionText;
    }
    public static boolean hasPermission(Player player, PermissionType permission) {
        boolean hasOP = false;
        boolean hasAdmin = false;
        boolean hasPermission = false;
        if (player.isOp()) {
            hasOP = true;
        }
        if (player.hasPermission(permissionTypeToString(PermissionType.ADMIN))) {
            hasAdmin = true;
        }
        if (player.hasPermission(permissionTypeToString(permission))) {
            hasPermission = true;
        }
        /* TODO:
        Domain.instance().debug().log(
                "Permissions",
                "hasPermission",
                "Has Permission: " + hasPermission,
                "Permission: " + permissionTypeToString(permission) + " Op: " + hasOP + " Admin: " + hasAdmin + " Permission: " + hasPermission
        );
        */
        return (hasOP || hasAdmin || hasPermission);
    }
    public static boolean hasPermission(Player player, String permission) {
        boolean hasOP = false;
        boolean hasAdmin = false;
        boolean hasPermission = false;
        if (player.isOp()) {
            hasOP = true;
        }
        if (player.hasPermission(permissionTypeToString(PermissionType.ADMIN))) {
            hasAdmin = true;
        }
        if (player.hasPermission(permission)) {
            hasPermission = true;
        }
        return (hasOP || hasAdmin || hasPermission);
    }
    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.isOp() || player.hasPermission(permission))
            {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    public static boolean isAdmin(Player player) {
        if (Permission.hasPermission(player, "domain.admin")) {
            return true;
        } else {
            return false;
        }
    }
}
