package com.ibexmc.domain.helper;

import com.ibexmc.domain.Domain;
import com.ibexmc.internal.messaging.Error;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomainFunctions {

    private final Domain instance;

    public DomainFunctions(Domain instance) {
        this.instance = instance;
    }


    private boolean fileExists(String filePath) {
        File myfile = new File(filePath);
        return myfile.exists();
    }
    private void saveResourceIfNotExists(String filePath) {
        if (!fileExists(instance.getDataFolder() +
                File.separator + filePath)) {
            instance.getLog().log("Creating File &f- &b" + filePath);
            Domain.getInstance().saveResource(filePath, false);
        }
    }
    public void createResourceFiles() {
        saveResourceIfNotExists("config.yml");
        saveResourceIfNotExists("flags.yml");
        saveResourceIfNotExists("gui.yml");
        saveResourceIfNotExists("lang.yml");
        saveResourceIfNotExists("material_groups.yml");
    }
    public void createDefaultBlocks() {
        saveResourceIfNotExists("Blocks/city.yml");
        saveResourceIfNotExists("Blocks/cow.yml");
        saveResourceIfNotExists("Blocks/custom.yml");
        saveResourceIfNotExists("Blocks/fly.yml");
        saveResourceIfNotExists("Blocks/griefprevent.yml");
        saveResourceIfNotExists("Blocks/miningrights.yml");
        saveResourceIfNotExists("Blocks/moballow.yml");
        saveResourceIfNotExists("Blocks/nofly.yml");
        saveResourceIfNotExists("Blocks/nopvp.yml");
        saveResourceIfNotExists("Blocks/teleport.yml");
        saveResourceIfNotExists("Blocks/zombie.yml");
    }
    public void createDefaultVouchers() {
        saveResourceIfNotExists("Vouchers/added5000.yml");
        saveResourceIfNotExists("Vouchers/minershaste.yml");
        saveResourceIfNotExists("Vouchers/nopvp.yml");
    }
    public void createDataFolder() {

        String folderPath = instance.getDataFolder() +
                File.separator + "Data";
        Path folder = Paths.get(folderPath);
        if (!Files.exists(folder)) {
            // If it doesn't exist, create the folder
            try {
                Files.createDirectories(folder);
            } catch (IOException e) {
                instance.getError().save(
                        "001",
                        "DomainFunctions",
                        "createDataFolder",
                        "Failed to create Data folder at " + folderPath,
                        e.getMessage(),
                        Error.Severity.CRITICAL,
                        e.getStackTrace()
                );
            }
        }
    }

    /**
     * Gets the opposite corners of a chunk that would form a cuboid
     * @param location Location to derive chunk from
     * @return Array of 2 opposing corners that would form a cuboid
     */
    public static Location[] chunkCorners(Location location) {
        Location[] corners = new Location[2];
        if (location != null) {
            int block1X = location.getChunk().getX() << 4;
            int block1Z = location.getChunk().getZ() << 4;
            int block1Y = location.getWorld().getMaxHeight();
            int block2X = (location.getChunk().getX() << 4) + 16;
            int block2Z = (location.getChunk().getZ() << 4) + 16;
            int block2Y = location.getWorld().getMinHeight();
            corners[0] = new Location(location.getWorld(), block1X, block1Y, block1Z);
            corners[1] = new Location(location.getWorld(), block2X, block2Y, block2Z);
        }
        return corners;
    }

    public static List<Location> getHollowCube(Location corner1, Location corner2) {
        List<Location> result = new ArrayList<Location>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX()) - 1;
        double minY = Math.min(corner1.getY(), corner2.getY()) - 1;
        double minZ = Math.min(corner1.getZ(), corner2.getZ()) - 1;
        double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + 1;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;

        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }

        return result;
    }

    public static List<Location> getCorners(Location corner1, Location corner2) {
        List<Location> result = new ArrayList<Location>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX()) - 1;
        double minY = Math.min(corner1.getY(), corner2.getY()) - 1;
        double minZ = Math.min(corner1.getZ(), corner2.getZ()) - 1;
        double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + 1;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;

        result.add(new Location(world, maxX, maxY, maxZ));
        result.add(new Location(world, maxX, maxY, minZ));
        result.add(new Location(world, maxX, minY, maxZ));
        result.add(new Location(world, maxX, minY, minZ));
        result.add(new Location(world, minX, maxY, maxZ));
        result.add(new Location(world, minX, maxY, minZ));
        result.add(new Location(world, minX, minY, maxZ));
        result.add(new Location(world, minX, minY, minZ));

        return result;
    }

    public static Map<Integer, Location> getExtendedCornerMap(Location corner1, Location corner2) {
        Map<Integer, Location> map = new HashMap<Integer, Location>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX()) - 1;
        double minY = Math.min(corner1.getY(), corner2.getY()) - 1;
        double minZ = Math.min(corner1.getZ(), corner2.getZ()) - 1;
        double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + 1;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;

        map.put(1, new Location(world, maxX, maxY, minZ));
        map.put(101, new Location(world, maxX - 1, maxY, minZ));
        map.put(1001, new Location(world, maxX - 2, maxY, minZ));
        map.put(10001, new Location(world, maxX - 3, maxY, minZ));
        map.put(102, new Location(world, maxX, maxY - 1, minZ));
        map.put(1002, new Location(world, maxX, maxY - 2, minZ));
        map.put(10002, new Location(world, maxX, maxY - 3, minZ));
        map.put(103, new Location(world, maxX, maxY, minZ + 1));
        map.put(1003, new Location(world, maxX, maxY, minZ + 2));
        map.put(10003, new Location(world, maxX, maxY, minZ + 3));


        map.put(2, new Location(world, minX, maxY, minZ));
        map.put(201, new Location(world, minX + 1, maxY, minZ));
        map.put(2001, new Location(world, minX + 2, maxY, minZ));
        map.put(20001, new Location(world, minX + 3, maxY, minZ));
        map.put(202, new Location(world, minX, maxY - 1, minZ));
        map.put(2002, new Location(world, minX, maxY - 2, minZ));
        map.put(20002, new Location(world, minX, maxY - 3, minZ));
        map.put(203, new Location(world, minX, maxY, minZ + 1));
        map.put(2003, new Location(world, minX, maxY, minZ + 2));
        map.put(20003, new Location(world, minX, maxY, minZ + 3));

        map.put(3, new Location(world, maxX, minY, minZ));
        map.put(301, new Location(world, maxX - 1, minY, minZ));
        map.put(3001, new Location(world, maxX - 2, minY, minZ));
        map.put(30001, new Location(world, maxX - 3, minY, minZ));
        map.put(302, new Location(world, maxX, minY + 1, minZ));
        map.put(3002, new Location(world, maxX, minY + 2, minZ));
        map.put(30002, new Location(world, maxX, minY + 3, minZ));
        map.put(303, new Location(world, maxX, minY, minZ + 1));
        map.put(3003, new Location(world, maxX, minY, minZ + 2));
        map.put(30003, new Location(world, maxX, minY, minZ + 3));

        map.put(4, new Location(world, minX, minY, minZ));
        map.put(401, new Location(world, minX + 1, minY, minZ));
        map.put(4001, new Location(world, minX + 2, minY, minZ));
        map.put(40001, new Location(world, minX + 3, minY, minZ));
        map.put(402, new Location(world, minX, minY + 1, minZ));
        map.put(4002, new Location(world, minX, minY + 2, minZ));
        map.put(40002, new Location(world, minX, minY + 3, minZ));
        map.put(403, new Location(world, minX, minY, minZ + 1));
        map.put(4003, new Location(world, minX, minY, minZ + 2));
        map.put(40003, new Location(world, minX, minY, minZ + 3));

        map.put(5, new Location(world, maxX, maxY, maxZ));
        map.put(501, new Location(world, maxX - 1, maxY, maxZ));
        map.put(5001, new Location(world, maxX - 2, maxY, maxZ));
        map.put(50001, new Location(world, maxX - 3, maxY, maxZ));
        map.put(502, new Location(world, maxX, maxY - 1, maxZ));
        map.put(5002, new Location(world, maxX, maxY - 2, maxZ));
        map.put(50002, new Location(world, maxX, maxY - 3, maxZ));
        map.put(503, new Location(world, maxX, maxY, maxZ - 1));
        map.put(5003, new Location(world, maxX, maxY, maxZ - 2));
        map.put(50003, new Location(world, maxX, maxY, maxZ - 3));

        map.put(6, new Location(world, minX, maxY, maxZ));
        map.put(601, new Location(world, minX + 1, maxY, maxZ));
        map.put(6001, new Location(world, minX + 2, maxY, maxZ));
        map.put(60001, new Location(world, minX + 3, maxY, maxZ));
        map.put(602, new Location(world, minX, maxY - 1, maxZ));
        map.put(6002, new Location(world, minX, maxY - 2, maxZ));
        map.put(60002, new Location(world, minX, maxY - 3, maxZ));
        map.put(603, new Location(world, minX, maxY, maxZ - 1));
        map.put(6003, new Location(world, minX, maxY, maxZ - 2));
        map.put(60003, new Location(world, minX, maxY, maxZ - 3));

        map.put(7, new Location(world, maxX, minY, maxZ));
        map.put(701, new Location(world, maxX - 1, minY, maxZ));
        map.put(7001, new Location(world, maxX - 2, minY, maxZ));
        map.put(70001, new Location(world, maxX - 3, minY, maxZ));
        map.put(702, new Location(world, maxX, minY + 1, maxZ));
        map.put(7002, new Location(world, maxX, minY + 2, maxZ));
        map.put(70002, new Location(world, maxX, minY + 3, maxZ));
        map.put(703, new Location(world, maxX, minY, maxZ - 1));
        map.put(7003, new Location(world, maxX, minY, maxZ - 2));
        map.put(70003, new Location(world, maxX, minY, maxZ - 3));

        map.put(8, new Location(world, minX, minY, maxZ));
        map.put(801, new Location(world, minX + 1, minY, maxZ));
        map.put(8001, new Location(world, minX + 2, minY, maxZ));
        map.put(80001, new Location(world, minX + 3, minY, maxZ));
        map.put(802, new Location(world, minX, minY + 1, maxZ));
        map.put(8002, new Location(world, minX, minY + 2, maxZ));
        map.put(80002, new Location(world, minX, minY + 3, maxZ));
        map.put(803, new Location(world, minX, minY, maxZ - 1));
        map.put(8003, new Location(world, minX, minY, maxZ - 2));
        map.put(80003, new Location(world, minX, minY, maxZ - 3));

        return map;
    }
    public static List<Location> getRing(Location corner1, Location corner2, int eyeLevel) {
        List<Location> result = new ArrayList<Location>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX()) - 1;
        double minZ = Math.min(corner1.getZ(), corner2.getZ()) - 1;
        double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;

        for (double x = minX; x <= maxX; x++) {
            for (double z = minZ; z <= maxZ; z++) {
                int components = 0;
                if (x == minX || x == maxX) components++;
                if (z == minZ || z == maxZ) components++;
                if (components >= 1) {
                    result.add(new Location(world, x, eyeLevel, z));
                }
            }
        }

        return result;
    }
    public static boolean testOverlap(int x1, int x2, int y1, int y2) {
        return (x1 >= y1 && x1 <= y2) ||
                (x2 >= y1 && x2 <= y2) ||
                (y1 >= x1 && y1 <= x2) ||
                (y2 >= x1 && y2 <= x2);
    }
    public static List<Block> getBlocksInRegion(Location start, Location end) {
        List<Block> blocks = new ArrayList<>();
        if (start.getWorld().getName().equalsIgnoreCase(end.getWorld().getName())) {
            int topBlockX = (Math.max(start.getBlockX(), end.getBlockX()));
            int bottomBlockX = (Math.min(start.getBlockX(), end.getBlockX()));

            int topBlockY = (Math.max(start.getBlockY(), end.getBlockY()));
            int bottomBlockY = (Math.min(start.getBlockY(), end.getBlockY()));

            int topBlockZ = (Math.max(start.getBlockZ(), end.getBlockZ()));
            int bottomBlockZ = (Math.min(start.getBlockZ(), end.getBlockZ()));
            for(int x = bottomBlockX; x <= topBlockX; x++)
            {
                for(int z = bottomBlockZ; z <= topBlockZ; z++)
                {
                    for(int y = bottomBlockY; y <= topBlockY; y++)
                    {
                        Block block = start.getWorld().getBlockAt(x, y, z);
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }
    public static int blockCount(Location l1, Location l2 ) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

        int x = x2-x1;
        int y = y2-y1;
        int z = z2-z1;

        int r = x*y*z;

        return r;
    }

    public static Block getNearestEmptySpace(Block b, int maxradius) {
        BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
        BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
        for (int r = 0; r <= maxradius; r++) {
            for (int s = 0; s < 6; s++) {
                BlockFace f = faces[s%3];
                BlockFace[] o = orth[s%3];
                if (s >= 3)
                    f = f.getOppositeFace();
                Block c = b.getRelative(f, r);
                for (int x = -r; x <= r; x++) {
                    for (int y = -r; y <= r; y++) {
                        Block a = c.getRelative(o[0], x).getRelative(o[1], y);
                        if (a.getType() == Material.AIR && a.getRelative(BlockFace.UP).getType() == Material.AIR)
                            return a;
                    }
                }
            }
        }
        return null;// no empty space within a cube of (2*(maxradius+1))^3
    }

    public static void bypassLog(Domain instance, Player player, String message) {
        instance.getLog().log("[BYPASS] Player: " + player.getName() + " - " + message);
    }

}
