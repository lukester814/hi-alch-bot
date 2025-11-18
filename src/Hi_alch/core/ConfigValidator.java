package Hi_alch.core;

import Hi_alch.gui.GUIConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Configuration Validation Utility
 *
 * Provides centralized validation for all configuration values.
 * Separates validation logic from the configuration data class
 * and provides detailed validation messages.
 */
public class ConfigValidator {

    // ===========================================
    // VALIDATION RESULT CLASS
    // ===========================================

    /**
     * Represents the result of a validation check
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
            this.warnings = new ArrayList<>();
        }

        private ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = errors;
            this.warnings = warnings;
        }

        public boolean isValid() {
            return valid && errors.isEmpty();
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }

        public String getErrorMessage() {
            if (errors.isEmpty()) {
                return "";
            }
            return String.join("\n", errors);
        }

        public String getWarningMessage() {
            if (warnings.isEmpty()) {
                return "";
            }
            return String.join("\n", warnings);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Validation Result: ").append(isValid() ? "VALID" : "INVALID").append("\n");

            if (!errors.isEmpty()) {
                sb.append("Errors:\n");
                for (String error : errors) {
                    sb.append("  - ").append(error).append("\n");
                }
            }

            if (!warnings.isEmpty()) {
                sb.append("Warnings:\n");
                for (String warning : warnings) {
                    sb.append("  - ").append(warning).append("\n");
                }
            }

            return sb.toString();
        }
    }

    // ===========================================
    // DISCORD WEBHOOK VALIDATION
    // ===========================================

    private static final Pattern DISCORD_WEBHOOK_PATTERN = Pattern.compile(
            "https://discord(?:app)?\\.com/api/webhooks/\\d+/[A-Za-z0-9_-]+"
    );

    /**
     * Validate Discord webhook URL format
     */
    public static boolean isValidDiscordWebhook(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return DISCORD_WEBHOOK_PATTERN.matcher(url.trim()).matches();
    }

    /**
     * Validate Discord webhook with detailed result
     */
    public static ValidationResult validateDiscordWebhook(String url) {
        ValidationResult result = new ValidationResult();

        if (url == null || url.trim().isEmpty()) {
            result.errors.add("Discord webhook URL cannot be empty");
            return new ValidationResult(false, result.errors, result.warnings);
        }

        if (!url.startsWith("https://")) {
            result.errors.add("Webhook URL must start with https://");
        }

        if (!url.contains("discord.com") && !url.contains("discordapp.com")) {
            result.errors.add("Webhook URL must be from discord.com or discordapp.com");
        }

        if (!url.contains("/api/webhooks/")) {
            result.errors.add("Invalid webhook URL format - missing /api/webhooks/");
        }

        if (!DISCORD_WEBHOOK_PATTERN.matcher(url.trim()).matches()) {
            result.errors.add("Discord webhook URL format is invalid");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    // ===========================================
    // ITEM VALIDATION
    // ===========================================

    /**
     * Validate item name
     */
    public static ValidationResult validateItemName(String itemName) {
        ValidationResult result = new ValidationResult();

        if (itemName == null || itemName.trim().isEmpty()) {
            result.errors.add("Item name cannot be empty");
            return new ValidationResult(false, result.errors, result.warnings);
        }

        if (itemName.trim().length() < 3) {
            result.errors.add("Item name must be at least 3 characters long");
        }

        if (itemName.trim().length() > 50) {
            result.warnings.add("Item name is unusually long (>50 characters)");
        }

        // Check for potentially invalid characters
        if (itemName.matches(".*[^a-zA-Z0-9\\s'()-].*")) {
            result.warnings.add("Item name contains special characters that may not be valid");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    /**
     * Validate item ID
     */
    public static ValidationResult validateItemId(int itemId) {
        ValidationResult result = new ValidationResult();

        if (itemId < 0) {
            result.errors.add("Item ID cannot be negative");
        } else if (itemId == 0) {
            result.warnings.add("Item ID is 0 - this may not be a valid item");
        } else if (itemId > 30000) {
            result.warnings.add("Item ID is very high (>" + 30000 + ") - verify this is correct");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    // ===========================================
    // TRADING VALIDATION
    // ===========================================

    /**
     * Validate buy limit
     */
    public static ValidationResult validateBuyLimit(int buyLimit) {
        ValidationResult result = new ValidationResult();

        if (buyLimit < Constants.MIN_BUY_LIMIT) {
            result.errors.add("Buy limit must be at least " + Constants.MIN_BUY_LIMIT);
        }

        if (buyLimit > Constants.MAX_BUY_LIMIT) {
            result.errors.add("Buy limit cannot exceed " + Constants.MAX_BUY_LIMIT);
        }

        if (buyLimit > 1000) {
            result.warnings.add("Buy limit is very high - most items have 4-hour buy limits of 100-1000");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    /**
     * Validate price markup percentage
     */
    public static ValidationResult validatePriceMarkup(double markup) {
        ValidationResult result = new ValidationResult();

        if (markup < Constants.MIN_PRICE_MARKUP) {
            result.errors.add("Price markup cannot be negative");
        }

        if (markup > Constants.MAX_PRICE_MARKUP) {
            result.errors.add("Price markup cannot exceed " + Constants.MAX_PRICE_MARKUP + "%");
        }

        if (markup > 20.0) {
            result.warnings.add("High price markup (>" + 20 + "%) may result in overpaying significantly");
        }

        if (markup < 1.0) {
            result.warnings.add("Very low price markup (<1%) may result in slow or failed purchases");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    /**
     * Validate nature rune amount
     */
    public static ValidationResult validateNatureRuneAmount(int amount) {
        ValidationResult result = new ValidationResult();

        if (amount < Constants.MIN_NATURE_RUNE_AMOUNT) {
            result.errors.add("Nature rune amount must be at least " + Constants.MIN_NATURE_RUNE_AMOUNT);
        }

        if (amount > Constants.MAX_NATURE_RUNE_AMOUNT) {
            result.errors.add("Nature rune amount cannot exceed " + Constants.MAX_NATURE_RUNE_AMOUNT);
        }

        if (amount < 500) {
            result.warnings.add("Low nature rune amount - consider buying 1000+ for better efficiency");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    // ===========================================
    // ANTI-BAN VALIDATION
    // ===========================================

    /**
     * Validate anti-ban aggression level
     */
    public static ValidationResult validateAntibanAggression(int aggression) {
        ValidationResult result = new ValidationResult();

        if (aggression < Constants.MIN_ANTIBAND_AGGRESSION) {
            result.errors.add("Anti-ban aggression must be at least " + Constants.MIN_ANTIBAND_AGGRESSION);
        }

        if (aggression > Constants.MAX_ANTIBAND_AGGRESSION) {
            result.errors.add("Anti-ban aggression cannot exceed " + Constants.MAX_ANTIBAND_AGGRESSION);
        }

        if (aggression >= 8) {
            result.warnings.add("High aggression (≥8) reduces human-like behavior and increases ban risk");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    /**
     * Validate break time range
     */
    public static ValidationResult validateBreakTimes(int minMinutes, int maxMinutes) {
        ValidationResult result = new ValidationResult();

        if (minMinutes < Constants.MIN_BREAK_MINUTES) {
            result.errors.add("Minimum break time must be at least " + Constants.MIN_BREAK_MINUTES + " minute(s)");
        }

        if (maxMinutes > Constants.MAX_BREAK_MINUTES) {
            result.errors.add("Maximum break time cannot exceed " + Constants.MAX_BREAK_MINUTES + " minutes");
        }

        if (minMinutes > maxMinutes) {
            result.errors.add("Minimum break time cannot be greater than maximum break time");
        }

        if (minMinutes == maxMinutes) {
            result.warnings.add("Min and max break times are the same - breaks will not be randomized");
        }

        if (maxMinutes - minMinutes < 3) {
            result.warnings.add("Small break range - consider larger variance for more human-like behavior");
        }

        if (minMinutes < 3) {
            result.warnings.add("Very short minimum break time (<3 min) may not be realistic");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    /**
     * Validate profile seed
     */
    public static ValidationResult validateProfileSeed(String seed) {
        ValidationResult result = new ValidationResult();

        if (seed == null || seed.trim().isEmpty()) {
            result.warnings.add("Profile seed is empty - randomized anti-ban behavior will be used");
            return new ValidationResult(true, result.errors, result.warnings);
        }

        if (seed.trim().length() < 3) {
            result.warnings.add("Very short profile seed - consider using a longer, more unique identifier");
        }

        if (seed.trim().length() > 50) {
            result.warnings.add("Profile seed is very long - shorter seeds are easier to remember");
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    // ===========================================
    // COMPREHENSIVE CONFIGURATION VALIDATION
    // ===========================================

    /**
     * Validate entire GUIConfiguration object
     */
    public static ValidationResult validateConfiguration(GUIConfiguration config) {
        ValidationResult result = new ValidationResult();

        if (config == null) {
            result.errors.add("Configuration object is null");
            return new ValidationResult(false, result.errors, result.warnings);
        }

        // Validate item settings
        ValidationResult itemNameResult = validateItemName(config.selectedItemName);
        result.errors.addAll(itemNameResult.getErrors());
        result.warnings.addAll(itemNameResult.getWarnings());

        ValidationResult itemIdResult = validateItemId(config.selectedItemId);
        result.errors.addAll(itemIdResult.getErrors());
        result.warnings.addAll(itemIdResult.getWarnings());

        ValidationResult buyLimitResult = validateBuyLimit(config.buyLimit);
        result.errors.addAll(buyLimitResult.getErrors());
        result.warnings.addAll(buyLimitResult.getWarnings());

        ValidationResult markupResult = validatePriceMarkup(config.priceMarkup);
        result.errors.addAll(markupResult.getErrors());
        result.warnings.addAll(markupResult.getWarnings());

        ValidationResult runeAmountResult = validateNatureRuneAmount(config.natureRuneAmount);
        result.errors.addAll(runeAmountResult.getErrors());
        result.warnings.addAll(runeAmountResult.getWarnings());

        // Validate Discord settings if enabled
        if (config.discordNotificationsEnabled) {
            ValidationResult webhookResult = validateDiscordWebhook(config.discordWebhookUrl);
            result.errors.addAll(webhookResult.getErrors());
            result.warnings.addAll(webhookResult.getWarnings());
        }

        // Validate anti-ban settings
        ValidationResult aggressionResult = validateAntibanAggression(config.antibanAggression);
        result.errors.addAll(aggressionResult.getErrors());
        result.warnings.addAll(aggressionResult.getWarnings());

        if (config.enableBreakSystem) {
            ValidationResult breakResult = validateBreakTimes(config.minBreakMinutes, config.maxBreakMinutes);
            result.errors.addAll(breakResult.getErrors());
            result.warnings.addAll(breakResult.getWarnings());
        }

        if (config.useProfileSeeding) {
            ValidationResult seedResult = validateProfileSeed(config.userProfileSeed);
            result.warnings.addAll(seedResult.getWarnings());
        }

        return new ValidationResult(result.errors.isEmpty(), result.errors, result.warnings);
    }

    // ===========================================
    // QUICK VALIDATION METHODS
    // ===========================================

    /**
     * Quick validation - returns true/false only
     */
    public static boolean isValidConfiguration(GUIConfiguration config) {
        return validateConfiguration(config).isValid();
    }

    /**
     * Quick validation with exception on failure
     */
    public static void requireValidConfiguration(GUIConfiguration config) throws IllegalArgumentException {
        ValidationResult result = validateConfiguration(config);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid configuration:\n" + result.getErrorMessage());
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Check if a string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if a value is within range (inclusive)
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Check if a value is within range (inclusive)
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Prevent instantiation
     */
    private ConfigValidator() {
        throw new AssertionError("Cannot instantiate ConfigValidator class");
    }
}
