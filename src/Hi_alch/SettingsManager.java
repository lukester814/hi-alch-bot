package Hi_alch;

import Hi_alch.gui.GUIConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Professional Settings Manager for Bot Configuration
 *
 * Features:
 * - Save/Load bot configurations to/from files
 * - JSON-like format without external dependencies
 * - Default settings management
 * - Auto-save functionality
 * - User-friendly file dialogs
 * - Configuration validation
 *
 * REUSABLE: Perfect settings system for any bot project!
 */
public class SettingsManager {

    // ===========================================
    // CONFIGURATION
    // ===========================================

    // Default settings directory
    private static final String SETTINGS_DIR = "settings";
    private static final String DEFAULT_FILENAME = "bot_settings.txt";

    // Current configuration cache
    private GUIConfiguration lastSavedConfig;
    private String lastFilename = "";

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public SettingsManager() {
        // Create settings directory if it doesn't exist
        File settingsDir = new File(SETTINGS_DIR);
        if (!settingsDir.exists()) {
            settingsDir.mkdirs();
        }

        BotUtils.log("💾 SettingsManager initialized");
    }

    // ===========================================
    // SAVE METHODS
    // ===========================================

    /**
     * Save settings with specific filename
     */
    public boolean saveSettings(String filename) {
        if (lastSavedConfig == null) {
            BotUtils.log("⚠️ No configuration to save");
            return false;
        }

        return saveConfiguration(lastSavedConfig, filename);
    }

    /**
     * Save configuration to file
     */
    public boolean saveConfiguration(GUIConfiguration config, String filename) {
        if (config == null || filename == null || filename.trim().isEmpty()) {
            BotUtils.log("❌ Invalid configuration or filename");
            return false;
        }

        try {
            File file = new File(SETTINGS_DIR, filename + ".txt");

            // Create configuration string
            String configData = serializeConfiguration(config);

            // Write to file
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(configData);
            }

            lastSavedConfig = config;
            lastFilename = filename;

            BotUtils.log("💾 Settings saved to: " + file.getAbsolutePath());
            return true;

        } catch (Exception e) {
            BotUtils.logError("Error saving settings", e);
            return false;
        }
    }

    /**
     * Quick save using last filename
     */
    public boolean quickSave() {
        if (lastFilename.isEmpty()) {
            return saveSettings(DEFAULT_FILENAME);
        }
        return saveSettings(lastFilename);
    }

    /**
     * Auto-save configuration
     */
    public boolean autoSave() {
        return saveSettings("autosave");
    }

    /**
     * Save with user file dialog
     */
    public boolean saveWithDialog(GUIConfiguration config) {
        JFileChooser fileChooser = new JFileChooser(SETTINGS_DIR);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Settings Files", "txt"));
        fileChooser.setSelectedFile(new File("my_settings.txt"));

        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filename = selectedFile.getName();
            if (!filename.endsWith(".txt")) {
                filename += ".txt";
            }

            return saveConfiguration(config, filename.replace(".txt", ""));
        }

        return false;
    }

    // ===========================================
    // LOAD METHODS
    // ===========================================

    /**
     * Load settings from specific filename
     */
    public GUIConfiguration loadSettings(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            BotUtils.log("❌ Invalid filename");
            return null;
        }

        try {
            File file = new File(SETTINGS_DIR, filename + ".txt");

            if (!file.exists()) {
                BotUtils.log("⚠️ Settings file not found: " + filename);
                return null;
            }

            // Read file content
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            // Parse configuration
            GUIConfiguration config = deserializeConfiguration(content.toString());

            if (config != null) {
                lastSavedConfig = config;
                lastFilename = filename;
                BotUtils.log("📁 Settings loaded from: " + file.getAbsolutePath());
            }

            return config;

        } catch (Exception e) {
            BotUtils.logError("Error loading settings", e);
            return null;
        }
    }

    /**
     * Load with user file dialog
     */
    public GUIConfiguration loadWithDialog() {
        JFileChooser fileChooser = new JFileChooser(SETTINGS_DIR);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Settings Files", "txt"));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filename = selectedFile.getName().replace(".txt", "");

            return loadSettings(filename);
        }

        return null;
    }

    /**
     * Load default settings
     */
    public GUIConfiguration loadDefaultSettings() {
        // Try to load from default file first
        GUIConfiguration config = loadSettings("default");

        if (config == null) {
            // Create built-in default configuration
            config = createDefaultConfiguration();
            BotUtils.log("📁 Using built-in default settings");
        }

        return config;
    }

    // ===========================================
    // SERIALIZATION METHODS
    // ===========================================

    /**
     * Serialize configuration to string format
     */
    private String serializeConfiguration(GUIConfiguration config) {
        StringBuilder sb = new StringBuilder();

        sb.append("# High Alchemy Bot Settings\n");
        sb.append("# Generated: ").append(BotUtils.getCurrentFullTimeString()).append("\n\n");

        // Item settings
        sb.append("[Item Settings]\n");
        sb.append("selectedItemName=").append(config.selectedItemName != null ? config.selectedItemName : "").append("\n");
        sb.append("selectedItemId=").append(config.selectedItemId).append("\n");
        sb.append("buyLimit=").append(config.buyLimit).append("\n");
        sb.append("priceMarkup=").append(config.priceMarkup).append("\n");
        sb.append("natureRuneAmount=").append(config.natureRuneAmount).append("\n");
        sb.append("\n");

        // Bot options
        sb.append("[Bot Options]\n");
        sb.append("smartProfitEnabled=").append(config.smartProfitEnabled).append("\n");
        sb.append("worldHopEnabled=").append(config.worldHopEnabled).append("\n");
        sb.append("\n");

        // Discord settings
        sb.append("[Discord Settings]\n");
        sb.append("discordWebhookUrl=").append(config.discordWebhookUrl != null ? config.discordWebhookUrl : "").append("\n");
        sb.append("discordNotificationsEnabled=").append(config.discordNotificationsEnabled).append("\n");
        sb.append("\n");

        // Anti-ban settings
        sb.append("[Anti-ban Settings]\n");
        sb.append("antibanEnabled=").append(config.antibanEnabled).append("\n");
        sb.append("antibanAggression=").append(config.antibanAggression).append("\n");

        return sb.toString();
    }

    /**
     * Deserialize configuration from string format
     */
    private GUIConfiguration deserializeConfiguration(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }

        try {
            GUIConfiguration config = new GUIConfiguration();
            Map<String, String> values = parseConfigurationData(data);

            // Item settings
            config.selectedItemName = values.getOrDefault("selectedItemName", "");
            config.selectedItemId = BotUtils.safeParseInt(values.getOrDefault("selectedItemId", "-1"), -1);
            config.buyLimit = BotUtils.safeParseInt(values.getOrDefault("buyLimit", "100"), 100);
            config.priceMarkup = BotUtils.safeParseDouble(values.getOrDefault("priceMarkup", "5.0"), 5.0);
            config.natureRuneAmount = BotUtils.safeParseInt(values.getOrDefault("natureRuneAmount", "1000"), 1000);

            // Bot options
            config.smartProfitEnabled = Boolean.parseBoolean(values.getOrDefault("smartProfitEnabled", "false"));
            config.worldHopEnabled = Boolean.parseBoolean(values.getOrDefault("worldHopEnabled", "false"));

            // Discord settings
            config.discordWebhookUrl = values.getOrDefault("discordWebhookUrl", "");
            config.discordNotificationsEnabled = Boolean.parseBoolean(values.getOrDefault("discordNotificationsEnabled", "false"));

            // Anti-ban settings
            config.antibanEnabled = Boolean.parseBoolean(values.getOrDefault("antibanEnabled", "true"));
            config.antibanAggression = BotUtils.safeParseInt(values.getOrDefault("antibanAggression", "5"), 5);

            return config;

        } catch (Exception e) {
            BotUtils.logError("Error parsing configuration", e);
            return null;
        }
    }

    /**
     * Parse configuration data into key-value map
     */
    private Map<String, String> parseConfigurationData(String data) {
        Map<String, String> values = new HashMap<>();

        String[] lines = data.split("\n");
        for (String line : lines) {
            line = line.trim();

            // Skip comments and empty lines
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("[")) {
                continue;
            }

            // Parse key=value pairs
            int equalsIndex = line.indexOf('=');
            if (equalsIndex > 0) {
                String key = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1).trim();
                values.put(key, value);
            }
        }

        return values;
    }

    /**
     * Create default configuration
     */
    private GUIConfiguration createDefaultConfiguration() {
        GUIConfiguration config = new GUIConfiguration();

        // Default item settings
        config.selectedItemName = "Dragon longsword";
        config.selectedItemId = 1305;
        config.buyLimit = 100;
        config.priceMarkup = 5.0;
        config.natureRuneAmount = 1000;

        // Default bot options
        config.smartProfitEnabled = true;
        config.worldHopEnabled = false;

        // Default Discord settings
        config.discordWebhookUrl = "";
        config.discordNotificationsEnabled = false;

        // Default anti-ban settings
        config.antibanEnabled = true;
        config.antibanAggression = 5;

        return config;
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Check if settings file exists
     */
    public boolean settingsExist(String filename) {
        File file = new File(SETTINGS_DIR, filename + ".txt");
        return file.exists();
    }

    /**
     * Delete settings file
     */
    public boolean deleteSettings(String filename) {
        try {
            File file = new File(SETTINGS_DIR, filename + ".txt");
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    BotUtils.log("🗑️ Settings deleted: " + filename);
                }
                return deleted;
            }
            return false;
        } catch (Exception e) {
            BotUtils.logError("Error deleting settings", e);
            return false;
        }
    }

    /**
     * List available settings files
     */
    public String[] listSettingsFiles() {
        File settingsDir = new File(SETTINGS_DIR);
        File[] files = settingsDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null) {
            return new String[0];
        }

        String[] filenames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filenames[i] = files[i].getName().replace(".txt", "");
        }

        return filenames;
    }

    /**
     * Get last saved configuration
     */
    public GUIConfiguration getLastSavedConfig() {
        return lastSavedConfig;
    }

    /**
     * Update current configuration cache
     */
    public void updateCurrentConfig(GUIConfiguration config) {
        this.lastSavedConfig = config;
    }
}