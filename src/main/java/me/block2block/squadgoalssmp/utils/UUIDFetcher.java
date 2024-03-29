package me.block2block.squadgoalssmp.utils;

import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

public class UUIDFetcher {

    /**
     * @param player The player
     * @return The UUID of the given player
     */
    //Uncomment this if you want the helper method for BungeeCord:
	/*
	public static UUID getUUID(ProxiedPlayer player) {
		return getUUID(player.getName());
	}
	*/

    /**
     * @param player The player
     * @return The UUID of the given player
     */
    //Uncomment this if you want the helper method for Bukkit/Spigot:
    public static UUID getUUID(Player player) {
        return getUUID(player.getName());
    }


    /**
     * @param playername The name of the player
     * @return The UUID of the given player
     */
    public static UUID getUUID(String playername) {

        try {
            String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);

            StringBuilder result = new StringBuilder();

            readData(output, result, playername);
            if (result.toString().equals("")) {
                return null;
            }

            String u = result.toString();
            String uuid = "";

            for (int i = 0; i <= 31; i++) {
                uuid = uuid + u.charAt(i);
                if (i == 7 || i == 11 || i == 15 || i == 19) {
                    uuid = uuid + "-";
                }
            }

            return UUID.fromString(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    private static void readData(String toRead, StringBuilder result, String playerName) {
        try {
            toRead = toRead.replace(playerName, "");
            int i = 17;

            while (i < 200) {
                if (!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {

                    result.append(String.valueOf(toRead.charAt(i)));

                } else {
                    break;
                }

                i++;
            }
        } catch (Exception ignored) {
        }
    }

    public static String callURL(String URL) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            java.net.URL url = new URL(URL);
            urlConn = url.openConnection();

            if (urlConn != null) urlConn.setReadTimeout(60 * 1000);

            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);

                if (bufferedReader != null) {
                    int cp;

                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }

                    bufferedReader.close();
                }
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}