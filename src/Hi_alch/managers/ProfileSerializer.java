package Hi_alch.managers;

import java.io.*;
import java.util.*;

/**
 * Profile Serializer - Save/Load Profile Data
 *
 * Handles:
 * - Saving profiles to files
 * - Loading profiles from files
 * - Parsing key=value format
 * - Profile value setting
 */
public class ProfileSerializer {

    private String profilesDirectory;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public ProfileSerializer(String profilesDirectory) {
        this.profilesDirectory = profilesDirectory;
    }

    // ===========================================
    // PROFILE SAVING
    // ===========================================

    /**
     * Save profile to file
     */
    public boolean saveProfile(ProfileManager.BotProfile profile) {
        try {
            profile.updateModifiedTime();
            String filename = profilesDirectory + sanitizeFilename(profile.profileName) + ".profile";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                // Write profile data in key=value format
                writer.write("# Hi-Alch Bot Profile\n");
                writer.write("# Profile: " + profile.profileName + "\n");
                writer.write("# Created: " + new Date(profile.createdTimestamp) + "\n");
                writer.write("# Modified: " + new Date(profile.lastModifiedTimestamp) + "\n");
                writer.write("\n");

                // Metadata
                writeLine(writer, "profileName", profile.profileName);
                writeLine(writer, "profileVersion", profile.profileVersion);
                writeLine(writer, "createdTimestamp", profile.createdTimestamp);
                writeLine(writer, "lastModifiedTimestamp", profile.lastModifiedTimestamp);
                writer.write("\n");

                // Bot settings
                writer.write("# Bot Settings\n");
                writeLine(writer, "itemName", profile.itemName);
                writeLine(writer, "itemId", profile.itemId);
                writeLine(writer, "buyLimit", profile.buyLimit);
                writeLine(writer, "priceMarkup", profile.priceMarkup);
                writeLine(writer, "natureRuneAmount", profile.natureRuneAmount);
                writeLine(writer, "smartProfit", profile.smartProfit);
                writeLine(writer, "worldHopping", profile.worldHopping);
                writeLine(writer, "skipBuying", profile.skipBuying);
                writeLine(writer, "restockWhenEmpty", profile.restockWhenEmpty);
                writer.write("\n");

                // Location settings
                writer.write("# Location Settings\n");
                writeLine(writer, "alchLocation", profile.alchLocation);
                writeLine(writer, "autoNavigate", profile.autoNavigate);
                writer.write("\n");

                // Session goals
                writer.write("# Session Goals\n");
                writeLine(writer, "alchGoalEnabled", profile.alchGoalEnabled);
                writeLine(writer, "targetAlchs", profile.targetAlchs);
                writeLine(writer, "profitGoalEnabled", profile.profitGoalEnabled);
                writeLine(writer, "targetProfit", profile.targetProfit);
                writeLine(writer, "xpGoalEnabled", profile.xpGoalEnabled);
                writeLine(writer, "targetXP", profile.targetXP);
                writeLine(writer, "timeGoalEnabled", profile.timeGoalEnabled);
                writeLine(writer, "targetMinutes", profile.targetMinutes);
                writeLine(writer, "levelGoalEnabled", profile.levelGoalEnabled);
                writeLine(writer, "targetLevel", profile.targetLevel);
                writeLine(writer, "useAndLogic", profile.useAndLogic);
                writer.write("\n");

                // Break settings
                writer.write("# Break Settings\n");
                writeLine(writer, "breaksEnabled", profile.breaksEnabled);
                writeLine(writer, "minBreakIntervalMinutes", profile.minBreakIntervalMinutes);
                writeLine(writer, "maxBreakIntervalMinutes", profile.maxBreakIntervalMinutes);
                writeLine(writer, "minBreakDurationMinutes", profile.minBreakDurationMinutes);
                writeLine(writer, "maxBreakDurationMinutes", profile.maxBreakDurationMinutes);
                writeLine(writer, "breakActivity", profile.breakActivity);
                writeLine(writer, "randomizeBreak", profile.randomizeBreak);
                writer.write("\n");

                // Antiban settings
                writer.write("# Antiban Settings\n");
                writeLine(writer, "aggressionLevel", profile.aggressionLevel);
                writeLine(writer, "cameraMovement", profile.cameraMovement);
                writeLine(writer, "tabChecks", profile.tabChecks);
                writeLine(writer, "mouseLeaves", profile.mouseLeaves);
                writeLine(writer, "skillChecks", profile.skillChecks);
                writer.write("\n");

                // Discord settings
                writer.write("# Discord Settings\n");
                writeLine(writer, "discordEnabled", profile.discordEnabled);
                writeLine(writer, "webhookURL", profile.webhookURL);
                writer.write("\n");

                // Mule settings
                writer.write("# Mule Settings\n");
                writeLine(writer, "muleEnabled", profile.muleEnabled);
                writeLine(writer, "muleUsername", profile.muleUsername);
                writeLine(writer, "muleLocation", profile.muleLocation);
                writer.write("\n");

                // Export settings
                writer.write("# Export Settings\n");
                writeLine(writer, "autoExportCSV", profile.autoExportCSV);
                writeLine(writer, "exportDirectory", profile.exportDirectory);

                BotUtils.log("✅ Saved profile: " + profile.profileName + " to " + filename);
                return true;
            }

        } catch (Exception e) {
            BotUtils.logError("Error saving profile", e);
            return false;
        }
    }

    // ===========================================
    // PROFILE LOADING
    // ===========================================

    /**
     * Load profile from file
     */
    public ProfileManager.BotProfile loadProfile(String profileName) {
        try {
            String filename = profilesDirectory + sanitizeFilename(profileName) + ".profile";
            File file = new File(filename);

            if (!file.exists()) {
                BotUtils.log("❌ Profile file not found: " + filename);
                return null;
            }

            ProfileManager.BotProfile profile = new ProfileManager.BotProfile(profileName);

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    // Skip comments and empty lines
                    if (line.startsWith("#") || line.isEmpty()) {
                        continue;
                    }

                    // Parse key=value
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        setProfileValue(profile, key, value);
                    }
                }

                BotUtils.log("✅ Loaded profile: " + profileName);
                return profile;
            }

        } catch (Exception e) {
            BotUtils.logError("Error loading profile", e);
            return null;
        }
    }

    /**
     * Set profile value from string
     */
    private void setProfileValue(ProfileManager.BotProfile profile, String key, String value) {
        try {
            switch (key) {
                // Metadata
                case "profileName": profile.profileName = value; break;
                case "profileVersion": profile.profileVersion = Integer.parseInt(value); break;
                case "createdTimestamp": profile.createdTimestamp = Long.parseLong(value); break;
                case "lastModifiedTimestamp": profile.lastModifiedTimestamp = Long.parseLong(value); break;

                // Bot settings
                case "itemName": profile.itemName = value; break;
                case "itemId": profile.itemId = Integer.parseInt(value); break;
                case "buyLimit": profile.buyLimit = Integer.parseInt(value); break;
                case "priceMarkup": profile.priceMarkup = Double.parseDouble(value); break;
                case "natureRuneAmount": profile.natureRuneAmount = Integer.parseInt(value); break;
                case "smartProfit": profile.smartProfit = Boolean.parseBoolean(value); break;
                case "worldHopping": profile.worldHopping = Boolean.parseBoolean(value); break;
                case "skipBuying": profile.skipBuying = Boolean.parseBoolean(value); break;
                case "restockWhenEmpty": profile.restockWhenEmpty = Boolean.parseBoolean(value); break;

                // Location
                case "alchLocation": profile.alchLocation = value; break;
                case "autoNavigate": profile.autoNavigate = Boolean.parseBoolean(value); break;

                // Goals
                case "alchGoalEnabled": profile.alchGoalEnabled = Boolean.parseBoolean(value); break;
                case "targetAlchs": profile.targetAlchs = Integer.parseInt(value); break;
                case "profitGoalEnabled": profile.profitGoalEnabled = Boolean.parseBoolean(value); break;
                case "targetProfit": profile.targetProfit = Integer.parseInt(value); break;
                case "xpGoalEnabled": profile.xpGoalEnabled = Boolean.parseBoolean(value); break;
                case "targetXP": profile.targetXP = Integer.parseInt(value); break;
                case "timeGoalEnabled": profile.timeGoalEnabled = Boolean.parseBoolean(value); break;
                case "targetMinutes": profile.targetMinutes = Integer.parseInt(value); break;
                case "levelGoalEnabled": profile.levelGoalEnabled = Boolean.parseBoolean(value); break;
                case "targetLevel": profile.targetLevel = Integer.parseInt(value); break;
                case "useAndLogic": profile.useAndLogic = Boolean.parseBoolean(value); break;

                // Breaks
                case "breaksEnabled": profile.breaksEnabled = Boolean.parseBoolean(value); break;
                case "minBreakIntervalMinutes": profile.minBreakIntervalMinutes = Integer.parseInt(value); break;
                case "maxBreakIntervalMinutes": profile.maxBreakIntervalMinutes = Integer.parseInt(value); break;
                case "minBreakDurationMinutes": profile.minBreakDurationMinutes = Integer.parseInt(value); break;
                case "maxBreakDurationMinutes": profile.maxBreakDurationMinutes = Integer.parseInt(value); break;
                case "breakActivity": profile.breakActivity = value; break;
                case "randomizeBreak": profile.randomizeBreak = Boolean.parseBoolean(value); break;

                // Antiban
                case "aggressionLevel": profile.aggressionLevel = Integer.parseInt(value); break;
                case "cameraMovement": profile.cameraMovement = Boolean.parseBoolean(value); break;
                case "tabChecks": profile.tabChecks = Boolean.parseBoolean(value); break;
                case "mouseLeaves": profile.mouseLeaves = Boolean.parseBoolean(value); break;
                case "skillChecks": profile.skillChecks = Boolean.parseBoolean(value); break;

                // Discord
                case "discordEnabled": profile.discordEnabled = Boolean.parseBoolean(value); break;
                case "webhookURL": profile.webhookURL = value; break;

                // Mule
                case "muleEnabled": profile.muleEnabled = Boolean.parseBoolean(value); break;
                case "muleUsername": profile.muleUsername = value; break;
                case "muleLocation": profile.muleLocation = value; break;

                // Export
                case "autoExportCSV": profile.autoExportCSV = Boolean.parseBoolean(value); break;
                case "exportDirectory": profile.exportDirectory = value; break;
            }

        } catch (Exception e) {
            BotUtils.log("⚠️ Failed to parse profile value: " + key + "=" + value);
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    private void writeLine(BufferedWriter writer, String key, Object value) throws IOException {
        writer.write(key + "=" + (value != null ? value.toString() : "") + "\n");
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
