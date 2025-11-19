package Hi_alch;

import java.io.*;
import java.util.*;

/**
 * Profile Manager for Configuration Persistence
 *
 * Features:
 * - Save/load complete bot configurations
 * - Named profiles (F2P Safe, P2P Battlestaffs, Custom, etc.)
 * - Import/export profiles as JSON-like files
 * - Default profiles included
 * - Profile versioning
 */
public class ProfileManager {

    // ===========================================
    // PROFILE DATA STRUCTURE
    // ===========================================

    public static class BotProfile {
        public String profileName;
        public long createdTimestamp;
        public long lastModifiedTimestamp;
        public int profileVersion = 1;

        // Bot settings
        public String itemName;
        public int itemId;
        public int buyLimit;
        public double priceMarkup;
        public int natureRuneAmount;
        public boolean smartProfit;
        public boolean worldHopping;
        public boolean skipBuying;
        public boolean restockWhenEmpty;

        // Location settings
        public String alchLocation;
        public boolean autoNavigate;

        // Session goals
        public boolean alchGoalEnabled;
        public int targetAlchs;
        public boolean profitGoalEnabled;
        public int targetProfit;
        public boolean xpGoalEnabled;
        public int targetXP;
        public boolean timeGoalEnabled;
        public int targetMinutes;
        public boolean levelGoalEnabled;
        public int targetLevel;
        public boolean useAndLogic;

        // Break settings
        public boolean breaksEnabled;
        public int minBreakIntervalMinutes;
        public int maxBreakIntervalMinutes;
        public int minBreakDurationMinutes;
        public int maxBreakDurationMinutes;
        public String breakActivity;
        public boolean randomizeBreak;

        // Antiban settings
        public int aggressionLevel;
        public boolean cameraMovement;
        public boolean tabChecks;
        public boolean mouseLeaves;
        public boolean skillChecks;

        // Discord settings
        public boolean discordEnabled;
        public String webhookURL;

        // Mule settings
        public boolean muleEnabled;
        public String muleUsername;
        public String muleLocation;

        // Export settings
        public boolean autoExportCSV;
        public String exportDirectory;

        public BotProfile(String name) {
            this.profileName = name;
            this.createdTimestamp = System.currentTimeMillis();
            this.lastModifiedTimestamp = System.currentTimeMillis();
        }

        public void updateModifiedTime() {
            this.lastModifiedTimestamp = System.currentTimeMillis();
        }
    }

    // ===========================================
    // STATE
    // ===========================================

    private String profilesDirectory;
    private Map<String, BotProfile> loadedProfiles;
    private BotProfile currentProfile;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public ProfileManager() {
        this.profilesDirectory = System.getProperty("user.home") + "/HiAlchBot_Profiles/";
        this.loadedProfiles = new HashMap<>();

        // Create profiles directory
        createProfilesDirectory();

        // Load default profiles
        loadDefaultProfiles();

        BotUtils.log("📁 ProfileManager initialized");
        BotUtils.log("📂 Profiles directory: " + profilesDirectory);
    }

    // ===========================================
    // DIRECTORY MANAGEMENT
    // ===========================================

    private void createProfilesDirectory() {
        File dir = new File(profilesDirectory);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                BotUtils.log("✅ Created profiles directory: " + profilesDirectory);
            } else {
                BotUtils.log("⚠️ Failed to create profiles directory");
            }
        }
    }

    // ===========================================
    // DEFAULT PROFILES
    // ===========================================

    private void loadDefaultProfiles() {
        // F2P Safe Profile
        BotProfile f2pSafe = new BotProfile("F2P Safe");
        f2pSafe.itemName = "Rune platebody";
        f2pSafe.itemId = 1127;
        f2pSafe.buyLimit = 125;
        f2pSafe.priceMarkup = 5.0;
        f2pSafe.natureRuneAmount = 1000;
        f2pSafe.smartProfit = true;
        f2pSafe.worldHopping = false;
        f2pSafe.alchLocation = "GRAND_EXCHANGE";
        f2pSafe.breaksEnabled = true;
        f2pSafe.minBreakIntervalMinutes = 60;
        f2pSafe.maxBreakIntervalMinutes = 90;
        f2pSafe.minBreakDurationMinutes = 10;
        f2pSafe.maxBreakDurationMinutes = 20;
        f2pSafe.aggressionLevel = 3; // Very human-like
        loadedProfiles.put(f2pSafe.profileName, f2pSafe);

        // P2P Battlestaffs Profile
        BotProfile p2pBstaves = new BotProfile("P2P Battlestaffs");
        p2pBstaves.itemName = "Air battlestaff";
        p2pBstaves.itemId = 1397;
        p2pBstaves.buyLimit = 13000;
        p2pBstaves.priceMarkup = 3.0;
        p2pBstaves.natureRuneAmount = 5000;
        p2pBstaves.smartProfit = true;
        p2pBstaves.worldHopping = true;
        p2pBstaves.alchLocation = "GRAND_EXCHANGE";
        p2pBstaves.breaksEnabled = true;
        p2pBstaves.minBreakIntervalMinutes = 45;
        p2pBstaves.maxBreakIntervalMinutes = 75;
        p2pBstaves.minBreakDurationMinutes = 5;
        p2pBstaves.maxBreakDurationMinutes = 15;
        p2pBstaves.aggressionLevel = 5; // Moderate
        loadedProfiles.put(p2pBstaves.profileName, p2pBstaves);

        // Agility Training Profile
        BotProfile agilityProfile = new BotProfile("Agility Training");
        agilityProfile.itemName = "Rune 2h sword";
        agilityProfile.itemId = 1319;
        agilityProfile.buyLimit = 70;
        agilityProfile.priceMarkup = 5.0;
        agilityProfile.natureRuneAmount = 500;
        agilityProfile.skipBuying = true;
        agilityProfile.restockWhenEmpty = false;
        agilityProfile.alchLocation = "VARROCK_ROOFTOP";
        agilityProfile.autoNavigate = true;
        agilityProfile.breaksEnabled = true;
        agilityProfile.minBreakIntervalMinutes = 90;
        agilityProfile.maxBreakIntervalMinutes = 120;
        agilityProfile.aggressionLevel = 4;
        loadedProfiles.put(agilityProfile.profileName, agilityProfile);

        BotUtils.log("📚 Loaded " + loadedProfiles.size() + " default profiles");
    }

    // ===========================================
    // PROFILE SAVING
    // ===========================================

    /**
     * Save profile to file
     */
    public boolean saveProfile(BotProfile profile) {
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

                // Add to loaded profiles
                loadedProfiles.put(profile.profileName, profile);
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
    public BotProfile loadProfile(String profileName) {
        try {
            String filename = profilesDirectory + sanitizeFilename(profileName) + ".profile";
            File file = new File(filename);

            if (!file.exists()) {
                BotUtils.log("❌ Profile file not found: " + filename);
                return null;
            }

            BotProfile profile = new BotProfile(profileName);

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
                loadedProfiles.put(profileName, profile);
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
    private void setProfileValue(BotProfile profile, String key, String value) {
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
    // PROFILE MANAGEMENT
    // ===========================================

    /**
     * Get list of all available profiles
     */
    public List<String> getAvailableProfiles() {
        List<String> profiles = new ArrayList<>();

        // Add loaded profiles
        profiles.addAll(loadedProfiles.keySet());

        // Scan directory for saved profiles
        File dir = new File(profilesDirectory);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".profile"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName().replace(".profile", "");
                    if (!profiles.contains(name)) {
                        profiles.add(name);
                    }
                }
            }
        }

        Collections.sort(profiles);
        return profiles;
    }

    /**
     * Delete profile
     */
    public boolean deleteProfile(String profileName) {
        try {
            String filename = profilesDirectory + sanitizeFilename(profileName) + ".profile";
            File file = new File(filename);

            if (file.exists() && file.delete()) {
                loadedProfiles.remove(profileName);
                BotUtils.log("🗑️ Deleted profile: " + profileName);
                return true;
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error deleting profile", e);
            return false;
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

    // ===========================================
    // GETTERS/SETTERS
    // ===========================================

    public BotProfile getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(BotProfile profile) {
        this.currentProfile = profile;
        BotUtils.log("📋 Active profile: " + profile.profileName);
    }

    public Map<String, BotProfile> getLoadedProfiles() {
        return new HashMap<>(loadedProfiles);
    }
}
