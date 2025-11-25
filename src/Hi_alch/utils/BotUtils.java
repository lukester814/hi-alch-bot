package Hi_alch.utils;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Logger;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Professional Bot Utilities Class
 *
 * Features:
 * - Comprehensive logging system with different levels
 * - Number and time formatting utilities
 * - Random delay generation with human-like patterns
 * - Validation methods for URLs, items, etc.
 * - Common bot utility methods
 * - Error handling and debugging tools
 *
 * REUSABLE: Perfect foundation for any DreamBot script!
 */
public class BotUtils {

    // ===========================================
    // CONSTANTS & CONFIGURATION
    // ===========================================

    // Random number generator
    private static final Random RANDOM = new Random();

    // Formatters
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat FULL_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Discord webhook URL pattern
    private static final Pattern DISCORD_WEBHOOK_PATTERN = Pattern.compile(
            "https://discord(?:app)?\\.com/api/webhooks/\\d+/[A-Za-z0-9_-]+"
    );

    // ===========================================
    // LOGGING METHODS
    // ===========================================

    /**
     * Log an info message
     */
    public static void log(String message) {
        Logger.log("[HIGH ALCH BOT] " + message);
        System.out.println("[" + TIME_FORMAT.format(new Date()) + "] " + message);
    }

    /**
     * Log a debug message
     */
    public static void logDebug(String message) {
        Logger.log("[DEBUG] " + message);
        // Only show debug in console during development
        // System.out.println("[DEBUG] " + message);
    }

    /**
     * Log a warning message
     */
    public static void logWarning(String message) {
        Logger.warn("[WARNING] " + message);
        System.out.println("[WARNING] " + message);
    }

    /**
     * Log an error message with exception
     */
    public static void logError(String message, Exception e) {
        String errorMsg = message + ": " + e.getMessage();
        Logger.error(errorMsg);
        System.err.println("[ERROR] " + errorMsg);

        // Print stack trace for debugging
        if (e != null) {
            e.printStackTrace();
        }
    }

    /**
     * Log an error message without exception
     */
    public static void logError(String message) {
        Logger.error(message);
        System.err.println("[ERROR] " + message);
    }

    // ===========================================
    // FORMATTING METHODS
    // ===========================================

    /**
     * Format a number with commas (e.g., 1,234,567)
     */
    public static String formatNumber(int number) {
        return NUMBER_FORMAT.format(number);
    }

    /**
     * Format a number with commas (long version)
     */
    public static String formatNumber(long number) {
        return NUMBER_FORMAT.format(number);
    }

    /**
     * Format a duration in milliseconds to readable format
     */
    public static String formatDuration(long milliseconds) {
        if (milliseconds < 0) {
            return "00:00:00";
        }

        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Get current time as formatted string
     */
    public static String getCurrentTimeString() {
        return TIME_FORMAT.format(new Date());
    }

    /**
     * Get current date and time as formatted string
     */
    public static String getCurrentFullTimeString() {
        return FULL_TIME_FORMAT.format(new Date());
    }

    /**
     * Format percentage with decimal places
     */
    public static String formatPercentage(double percentage) {
        return String.format("%.1f%%", percentage);
    }

    /**
     * Format GP amount with appropriate suffix (K, M, B)
     */
    public static String formatGP(int amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fB GP", amount / 1_000_000_000.0);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fM GP", amount / 1_000_000.0);
        } else if (amount >= 1_000) {
            return String.format("%.1fK GP", amount / 1_000.0);
        } else {
            return amount + " GP";
        }
    }

    // ===========================================
    // RANDOM & TIMING METHODS
    // ===========================================

    /**
     * Generate random delay between min and max milliseconds
     */
    public static int randomDelay(int minMs, int maxMs) {
        if (minMs >= maxMs) {
            return minMs;
        }
        return RANDOM.nextInt(maxMs - minMs + 1) + minMs;
    }

    /**
     * Sleep for a random amount of time with human-like variance
     */
    public static void randomSleep(int baseMs, int varianceMs) {
        int sleepTime = baseMs + RANDOM.nextInt(varianceMs * 2) - varianceMs;
        sleep(Math.max(sleepTime, 50)); // Minimum 50ms
    }

    /**
     * Sleep for specified milliseconds (wrapper for DreamBot's Sleep)
     */
    public static void sleep(int milliseconds) {
        if (milliseconds > 0) {
            Sleep.sleep(milliseconds);
        }
    }

    /**
     * Generate human-like random delay for actions
     */
    public static int humanDelay() {
        // Most human actions take 200-800ms with occasional longer pauses
        if (RANDOM.nextInt(20) == 0) { // 5% chance of longer pause
            return randomDelay(1000, 2000);
        } else {
            return randomDelay(200, 800);
        }
    }

    /**
     * Generate typing delay between keystrokes
     */
    public static int typingDelay() {
        return randomDelay(50, 150);
    }

    // ===========================================
    // VALIDATION METHODS
    // ===========================================

    /**
     * Check if a string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validate Discord webhook URL format
     */
    public static boolean isValidDiscordWebhook(String url) {
        if (isNullOrEmpty(url)) {
            return false;
        }
        return DISCORD_WEBHOOK_PATTERN.matcher(url.trim()).matches();
    }

    /**
     * Check if a number is within a valid range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Check if a percentage is valid (0-100)
     */
    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0.0 && percentage <= 100.0;
    }

    /**
     * Validate item ID (positive integer)
     */
    public static boolean isValidItemId(int itemId) {
        return itemId > 0;
    }

    // ===========================================
    // GAME STATE METHODS
    // ===========================================

    /**
     * Check if player is logged in
     */
    public static boolean isLoggedIn() {
        try {
            return Players.getLocal() != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get current world number
     */
    public static int getCurrentWorld() {
        try {
            return Worlds.getCurrentWorld();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Check if inventory is full
     */
    public static boolean isInventoryFull() {
        try {
            return Inventory.isFull();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get inventory free slots count
     */
    public static int getInventoryFreeSlots() {
        try {
            return Inventory.getEmptySlots();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Hop to a specific world
     */
    public static boolean hopToWorld(int worldNumber) {
        try {
            if (worldNumber < 301 || worldNumber > 580) {
                logError("Invalid world number: " + worldNumber);
                return false;
            }

            int currentWorld = Worlds.getCurrentWorld();
            if (currentWorld == worldNumber) {
                log("Already on world " + worldNumber);
                return true;
            }

            log("Hopping to world " + worldNumber + "...");

            World targetWorld = Worlds.getWorld(worldNumber);
            if (targetWorld == null) {
                logError("World " + worldNumber + " not found");
                return false;
            }

            if (WorldHopper.hopWorld(worldNumber)) {
                // Wait for world hop to complete
                Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == worldNumber, 15000);

                if (Worlds.getCurrentWorld() == worldNumber) {
                    log("Successfully hopped to world " + worldNumber);
                    return true;
                } else {
                    logError("Failed to hop to world " + worldNumber);
                    return false;
                }
            } else {
                logError("World hop attempt failed for world " + worldNumber);
                return false;
            }

        } catch (Exception e) {
            logError("Error hopping to world " + worldNumber, e);
            return false;
        }
    }

    // ===========================================
    // CALCULATION METHODS
    // ===========================================

    /**
     * Calculate percentage
     */
    public static double calculatePercentage(double value, double total) {
        if (total == 0) {
            return 0.0;
        }
        return (value / total) * 100.0;
    }

    /**
     * Calculate rate per hour
     */
    public static double calculateRatePerHour(long count, long timeMs) {
        if (timeMs <= 0) {
            return 0.0;
        }
        return (double) count / (timeMs / 3600000.0); // Convert ms to hours
    }

    /**
     * Calculate profit margin
     */
    public static double calculateProfitMargin(int costPrice, int sellPrice) {
        if (costPrice <= 0) {
            return 0.0;
        }
        return ((double)(sellPrice - costPrice) / costPrice) * 100.0;
    }

    /**
     * Calculate estimated time remaining
     */
    public static long calculateTimeRemaining(int completed, int target, long elapsedTime) {
        if (completed <= 0 || target <= completed) {
            return 0;
        }

        double rate = (double) completed / elapsedTime;
        int remaining = target - completed;
        return (long) (remaining / rate);
    }

    // ===========================================
    // STRING UTILITY METHODS
    // ===========================================

    /**
     * Truncate string to specified length with ellipsis
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Capitalize first letter of each word
     */
    public static String toTitleCase(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        String[] words = str.toLowerCase().split("\\s+");

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            if (words[i].length() > 0) {
                result.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    result.append(words[i].substring(1));
                }
            }
        }

        return result.toString();
    }

    /**
     * Remove non-alphanumeric characters except spaces
     */
    public static String sanitizeString(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        return str.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }

    // ===========================================
    // PROBABILITY & RANDOMIZATION METHODS
    // ===========================================

    /**
     * Generate random boolean with specified probability
     */
    public static boolean randomChance(double probability) {
        return RANDOM.nextDouble() < probability;
    }

    /**
     * Pick random element from array
     */
    public static <T> T randomChoice(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[RANDOM.nextInt(array.length)];
    }

    /**
     * Generate random integer in Gaussian distribution (bell curve)
     */
    public static int randomGaussian(int mean, int standardDeviation) {
        double gaussian = RANDOM.nextGaussian();
        return (int) Math.round(mean + (gaussian * standardDeviation));
    }

    // ===========================================
    // ERROR HANDLING METHODS
    // ===========================================

    /**
     * Safe integer parsing with default value
     */
    public static int safeParseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Safe double parsing with default value
     */
    public static double safeParseDouble(String str, double defaultValue) {
        try {
            return Double.parseDouble(str.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Execute code with exception handling and logging
     */
    public static void safeExecute(Runnable code, String actionDescription) {
        try {
            code.run();
        } catch (Exception e) {
            logError("Error during " + actionDescription, e);
        }
    }

    // ===========================================
    // DEBUGGING METHODS
    // ===========================================

    /**
     * Print object details for debugging
     */
    public static void debugPrint(Object obj, String label) {
        if (obj == null) {
            logDebug(label + ": null");
        } else {
            logDebug(label + ": " + obj.toString());
        }
    }

    /**
     * Print execution time for a block of code
     */
    public static void timeExecution(Runnable code, String description) {
        long startTime = System.currentTimeMillis();
        code.run();
        long endTime = System.currentTimeMillis();
        logDebug(description + " took " + (endTime - startTime) + "ms");
    }

    /**
     * Get memory usage information
     */
    public static String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return String.format("Memory: %s used / %s total",
                formatBytes(usedMemory), formatBytes(totalMemory));
    }

    /**
     * Format bytes to human readable format
     */
    public static String formatBytes(long bytes) {
        if (bytes >= 1024 * 1024 * 1024) {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        } else if (bytes >= 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else if (bytes >= 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return bytes + " B";
        }
    }

    // ===========================================
    // CONFIGURATION HELPERS
    // ===========================================

    /**
     * Clamp value between min and max
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamp double value between min and max
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Linear interpolation between two values
     */
    public static double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }

    /**
     * Check if current time is within specified hours (24-hour format)
     */
    public static boolean isTimeInRange(int startHour, int endHour) {
        int currentHour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));

        if (startHour <= endHour) {
            return currentHour >= startHour && currentHour <= endHour;
        } else { // Overnight range (e.g., 22:00 to 06:00)
            return currentHour >= startHour || currentHour <= endHour;
        }
    }
}