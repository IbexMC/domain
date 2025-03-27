package com.ibexmc.domain.util.functions;

import com.ibexmc.domain.Domain;
import com.ibexmc.internal.functions.NumberFunctions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class GameFunctions {
    private static String url = "https://www.spigotmc.org/resources/domain-land-claiming-grief-prevention-protection-fields-1-13-1-14.64393/";
    public static void checkVersion() {
        Domain instance = Domain.getInstance();
        String currentVersion = instance.getDescription().getVersion();
        String latestVersion = getLatestVersion();
        if (!currentVersion.equalsIgnoreCase(latestVersion)) {
            instance.getLog().log(
                    "GameFunctions",
                    "Version Check",
                    ChatColor.RED + "WARNING: " + ChatColor.RESET + "You are not using the most recent supported version of Domain.  Please visit " + url + " to download"
            );
        }
    }
    private static String getLatestVersion()
    {
        Domain instance = Domain.getInstance();
        try
        {
            HttpsURLConnection con = (HttpsURLConnection)new URL("https://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=64393".getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (version.length() <= 10) {
                return version.trim();
            }
        }
        catch (Exception ex)
        {
            instance.getLog().log("GameFunctions","Version Check", "Unexpected error checking for updates - Please check manually at : " + url);
            instance.getDebug().log("VersionCheck", "getLatestVersion()", "Unexpected Error: " + ex.getMessage());
        }
        return null;
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("org.spigotmc.SpigotConfig");
                return false;
            } catch (ClassNotFoundException e1) {
                return false;
            }
        }
    }

    public static boolean is114() {
        return getVersion().contains("v1_14");
    }
    public static boolean is115() {
        return getVersion().contains("v1_15");
    }
    public static boolean is116() {
        return getVersion().contains("v1_16");
    }
    public static boolean is117() {
        return getVersion().contains("v1_17");
    }
    public static boolean is118() {
        return getVersion().contains("v1_18");
    }
    public static boolean is119() {
        return getVersion().contains("v1_19");
    }

    public static double version() {
        double returnVal = 0.0;
        String spigotVersion = getBukkitVersion();
        String[] bits = spigotVersion.split("-");
        if (bits[0] != null) {
            String[] vers = bits[0].split("\\.");
            if (NumberFunctions.isInt(vers[0])) {
                if (vers[1] != null) {
                    if (NumberFunctions.isInt(vers[1])) {
                        return NumberFunctions.stringToDouble(vers[0] + "." + vers[1]);
                    }
                }
            }
        }
        return returnVal;
    }
    /**
     * Checks if the version provided is version safe for current Spigot version
     * Examples: Checking if safe for 1.14 while on 1.16 would return true, but
     * checking if 1.16 safe while on 1.15 would return false.
     * @param version Version to check
     * @return If true, current version is newer or equal to version being checked
     */
    public static boolean isVersionSafe(double version) {
        return (version <= version());
    }

    /**
     * Returns the current server version
     * @return Version as a string
     */
    public static String getVersion() {
        return getVersionType() + " - " + getBukkitVersion();
    }
    /**
     * Gets the version type (Spigot, Paper etc.)
     * @return Version type as a string
     */
    private static String getVersionType() {
        return Bukkit.getServer().getName();
    }

    /**
     * Gets the bukkit version
     * @return Bukkit version as a string
     */
    public static String getBukkitVersion() {
        return Bukkit.getServer().getBukkitVersion();
    }


}
