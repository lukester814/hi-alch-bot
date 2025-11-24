package Hi_alch.managers;

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

    // Helpers
    private ProfileSerializer serializer;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public ProfileManager() {
        this.profilesDirectory = System.getProperty("user.home") + "/HiAlchBot_Profiles/";
        this.loadedProfiles = new HashMap<>();

        // Create profiles directory
        createProfilesDirectory();

        // Initialize serializer
        this.serializer = new ProfileSerializer(profilesDirectory);

        // Load default profiles
        this.loadedProfiles = DefaultProfiles.loadDefaultProfiles();

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
    // PROFILE SAVING
    // ===========================================

    /**
     * Save profile to file
     */
    public boolean saveProfile(BotProfile profile) {
        boolean result = serializer.saveProfile(profile);
        if (result) {
            loadedProfiles.put(profile.profileName, profile);
        }
        return result;
    }

    // ===========================================
    // PROFILE LOADING
    // ===========================================

    /**
     * Load profile from file
     */
    public BotProfile loadProfile(String profileName) {
        BotProfile profile = serializer.loadProfile(profileName);
        if (profile != null) {
            loadedProfiles.put(profileName, profile);
        }
        return profile;
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
