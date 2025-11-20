package Hi_alch.core;

import Hi_alch.AlchBotGUI;
import Hi_alch.BotUtils;

/**
 * ConfigurationValidator - Validates Bot Configuration
 *
 * Validates all configuration settings before bot start:
 * - Item selection
 * - Buy limits
 * - Nature rune amounts
 * - Discord webhook settings
 * - Anti-ban settings
 *
 * Extracted from HighAlchBot to improve separation of concerns.
 */
public class ConfigurationValidator {

    private String lastValidationError = "";

    // ===========================================
    // MAIN VALIDATION
    // ===========================================

    /**
     * Validate complete bot configuration
     * @param config Configuration to validate
     * @return true if valid, false otherwise
     */
    public boolean validateConfiguration(AlchBotGUI.GUIConfiguration config) {
        try {
            lastValidationError = "";

            if (!validateItemSelection(config)) {
                return false;
            }

            if (!validateBuyLimit(config)) {
                return false;
            }

            if (!validateNatureRunes(config)) {
                return false;
            }

            if (!validateDiscordSettings(config)) {
                return false;
            }

            BotUtils.log("✅ Configuration validation successful");
            return true;

        } catch (Exception e) {
            lastValidationError = "Error validating configuration: " + e.getMessage();
            BotUtils.logError("Error validating configuration", e);
            return false;
        }
    }

    // ===========================================
    // SPECIFIC VALIDATIONS
    // ===========================================

    /**
     * Validate item selection
     */
    private boolean validateItemSelection(AlchBotGUI.GUIConfiguration config) {
        if (config.selectedItemName == null || config.selectedItemName.trim().isEmpty()) {
            lastValidationError = "No item selected";
            BotUtils.log("❌ No item selected");
            return false;
        }

        if (config.selectedItemName.toLowerCase().contains("select")) {
            lastValidationError = "Please select a valid item";
            BotUtils.log("❌ Please select a valid item");
            return false;
        }

        return true;
    }

    /**
     * Validate buy limit
     */
    private boolean validateBuyLimit(AlchBotGUI.GUIConfiguration config) {
        if (config.buyLimit <= 0) {
            lastValidationError = "Invalid buy limit: " + config.buyLimit;
            BotUtils.log("❌ Invalid buy limit: " + config.buyLimit);
            return false;
        }

        if (config.buyLimit > 10000) {
            lastValidationError = "Buy limit too high (max 10,000): " + config.buyLimit;
            BotUtils.log("❌ Buy limit too high (max 10,000): " + config.buyLimit);
            return false;
        }

        return true;
    }

    /**
     * Validate nature rune amount
     */
    private boolean validateNatureRunes(AlchBotGUI.GUIConfiguration config) {
        if (config.natureRuneAmount <= 0) {
            lastValidationError = "Invalid nature rune amount: " + config.natureRuneAmount;
            BotUtils.log("❌ Invalid nature rune amount: " + config.natureRuneAmount);
            return false;
        }

        if (config.natureRuneAmount > 10000) {
            lastValidationError = "Nature rune amount too high (max 10,000): " + config.natureRuneAmount;
            BotUtils.log("❌ Nature rune amount too high (max 10,000): " + config.natureRuneAmount);
            return false;
        }

        return true;
    }

    /**
     * Validate Discord settings
     */
    private boolean validateDiscordSettings(AlchBotGUI.GUIConfiguration config) {
        if (config.discordNotificationsEnabled) {
            if (config.discordWebhookUrl == null || config.discordWebhookUrl.trim().isEmpty()) {
                lastValidationError = "Discord notifications enabled but no webhook URL provided";
                BotUtils.log("❌ Discord notifications enabled but no webhook URL provided");
                return false;
            }

            if (!BotUtils.isValidDiscordWebhook(config.discordWebhookUrl)) {
                lastValidationError = "Invalid Discord webhook URL format";
                BotUtils.log("❌ Invalid Discord webhook URL format");
                return false;
            }
        }

        return true;
    }

    // ===========================================
    // ERROR RETRIEVAL
    // ===========================================

    /**
     * Get last validation error message
     */
    public String getLastValidationError() {
        return lastValidationError;
    }

    /**
     * Check if last validation had an error
     */
    public boolean hasValidationError() {
        return !lastValidationError.isEmpty();
    }
}
