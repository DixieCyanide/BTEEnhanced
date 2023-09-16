/*
 * BTEMoreEnhanced, a building tool
 * Copyright 2022 (C) DixieCyanide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.dixiecyanide.btemoreenhanced.update;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import org.bukkit.ChatColor;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class UpdateChecker implements Runnable {
    private final BTEMoreEnhanced bteMoreEnhanced;
    private final Logger logger;

    public UpdateChecker(BTEMoreEnhanced bteMoreEnhanced) {
        this.bteMoreEnhanced = bteMoreEnhanced;
        this.logger = bteMoreEnhanced.getLogger();
    }

    @Override
    public void run() {
        logger.info(ChatColor.GRAY + "-----CHECKING FOR UPDATES-----");
        String current = cleanVersion(bteMoreEnhanced.getDescription().getVersion());
        String latest = getLatestVersion();
        logger.info(ChatColor.AQUA + "Current version: " + current);
        logger.info(ChatColor.AQUA + "Latest version: " + latest);
        if (!current.equals(latest)) {
            logger.info(ChatColor.DARK_RED + "Plugin is not latest! Is it outdated? https://github.com/DixieCyanide/BTEMoreEnhanced/releases");
        } else {
            logger.info(ChatColor.GREEN + "Plugin is up to date.");
        }
        logger.info(ChatColor.GRAY + "------------------------------");
    }

    private String getLatestVersion() {
        String latestVersion = "NOT FOUND";
        try {
            URL url = new URL("https://api.github.com/repos/DixieCyanide/BTEMoreEnhanced/releases");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            int code = con.getResponseCode();
            if (code >= 200 && code <= 299) {
                JSONArray jsonObject = new JSONArray(response.toString());
                latestVersion = cleanVersion(jsonObject.getJSONObject(0).getString("tag_name"));
            } else {
                logger.severe("Request for latest release not successful. Response code: " + code);
            }
        } catch (Exception e) {
            logger.severe("Unexpected error occurred while getting latest release version number.");
            e.printStackTrace();
        }
        return latestVersion;
    }

    private static String cleanVersion(String version) {
        return version.replaceAll("[^0-9.]", "");
    }
}
