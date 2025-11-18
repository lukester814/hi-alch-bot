package Hi_alch.gui;

/**
 * Configuration data structure for the High Alchemy Bot GUI
 *
 * This class holds all user-configurable settings from the GUI.
 * It's used for saving/loading settings and passing configuration
 * between components.
 */
public class GUIConfiguration {

    // ===========================================
    // ITEM SETTINGS
    // ===========================================

    public String selectedItemName = "";
    public int selectedItemId = -1;
    public int buyLimit = 100;
    public double priceMarkup = 5.0;
    public int natureRuneAmount = 1000;

    // ===========================================
    // BOT OPTIONS
    // ===========================================

    public boolean smartProfitEnabled = false;
    public boolean worldHopEnabled = false;
    public boolean autoConfigureAlchWarning = true;
    public boolean itemsAlreadyInInventory = false;
    public boolean restockWhenEmpty = true;

    // ===========================================
    // DISCORD SETTINGS
    // ===========================================

    public String discordWebhookUrl = "";
    public boolean discordNotificationsEnabled = false;

    // ===========================================
    // ANTI-BAN SETTINGS
    // ===========================================

    public boolean antibanEnabled = true;
    public int antibanAggression = 5; // 1-10 scale
    public boolean enableBreakSystem = true;
    public int minBreakMinutes = 5;
    public int maxBreakMinutes = 15;
    public boolean enableFatigueSystem = true;
    public boolean useProfileSeeding = true;
    public String userProfileSeed = "";

    // ===========================================
    // METHODS
    // ===========================================

    /**
     * Validate the configuration
     * @return true if configuration is valid, false otherwise
     */
    public boolean isValid() {
        // Item must be selected
        if (selectedItemName == null || selectedItemName.trim().isEmpty()) {
            return false;
        }

        // Buy limit must be positive
        if (buyLimit <= 0) {
            return false;
        }

        // Price markup must be reasonable
        if (priceMarkup < 0 || priceMarkup > 100) {
            return false;
        }

        // Nature runes must be positive
        if (natureRuneAmount <= 0) {
            return false;
        }

        // Anti-ban aggression must be 1-10
        if (antibanAggression < 1 || antibanAggression > 10) {
            return false;
        }

        // Break times must be valid
        if (enableBreakSystem && (minBreakMinutes < 0 || maxBreakMinutes < minBreakMinutes)) {
            return false;
        }

        return true;
    }

    /**
     * Create a copy of this configuration
     */
    public GUIConfiguration copy() {
        GUIConfiguration copy = new GUIConfiguration();

        // Item settings
        copy.selectedItemName = this.selectedItemName;
        copy.selectedItemId = this.selectedItemId;
        copy.buyLimit = this.buyLimit;
        copy.priceMarkup = this.priceMarkup;
        copy.natureRuneAmount = this.natureRuneAmount;

        // Bot options
        copy.smartProfitEnabled = this.smartProfitEnabled;
        copy.worldHopEnabled = this.worldHopEnabled;
        copy.autoConfigureAlchWarning = this.autoConfigureAlchWarning;
        copy.itemsAlreadyInInventory = this.itemsAlreadyInInventory;
        copy.restockWhenEmpty = this.restockWhenEmpty;

        // Discord settings
        copy.discordWebhookUrl = this.discordWebhookUrl;
        copy.discordNotificationsEnabled = this.discordNotificationsEnabled;

        // Anti-ban settings
        copy.antibanEnabled = this.antibanEnabled;
        copy.antibanAggression = this.antibanAggression;
        copy.enableBreakSystem = this.enableBreakSystem;
        copy.minBreakMinutes = this.minBreakMinutes;
        copy.maxBreakMinutes = this.maxBreakMinutes;
        copy.enableFatigueSystem = this.enableFatigueSystem;
        copy.useProfileSeeding = this.useProfileSeeding;
        copy.userProfileSeed = this.userProfileSeed;

        return copy;
    }

    /**
     * Reset to default values
     */
    public void resetToDefaults() {
        // Item settings
        selectedItemName = "";
        selectedItemId = -1;
        buyLimit = 100;
        priceMarkup = 5.0;
        natureRuneAmount = 1000;

        // Bot options
        smartProfitEnabled = false;
        worldHopEnabled = false;
        autoConfigureAlchWarning = true;
        itemsAlreadyInInventory = false;
        restockWhenEmpty = true;

        // Discord settings
        discordWebhookUrl = "";
        discordNotificationsEnabled = false;

        // Anti-ban settings
        antibanEnabled = true;
        antibanAggression = 5;
        enableBreakSystem = true;
        minBreakMinutes = 5;
        maxBreakMinutes = 15;
        enableFatigueSystem = true;
        useProfileSeeding = true;
        userProfileSeed = "";
    }

    @Override
    public String toString() {
        return String.format("Config[item=%s, buyLimit=%d, markup=%.1f%%, runes=%d]",
                selectedItemName, buyLimit, priceMarkup, natureRuneAmount);
    }
}
