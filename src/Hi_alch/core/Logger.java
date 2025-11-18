package Hi_alch.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enhanced Logging System for High Alchemy Bot
 *
 * Provides structured logging with levels, formatting, and optional file output.
 * This class improves upon BotUtils logging by adding:
 * - Log levels (DEBUG, INFO, WARN, ERROR)
 * - Structured formatting
 * - Category-based logging
 * - Optional log filtering
 */
public class Logger {

    // ===========================================
    // LOG LEVELS
    // ===========================================

    public enum LogLevel {
        DEBUG(0, "DEBUG", "🔍"),
        INFO(1, "INFO", "ℹ️"),
        WARN(2, "WARN", "⚠️"),
        ERROR(3, "ERROR", "❌");

        private final int priority;
        private final String name;
        private final String emoji;

        LogLevel(int priority, String name, String emoji) {
            this.priority = priority;
            this.name = name;
            this.emoji = emoji;
        }

        public int getPriority() {
            return priority;
        }

        public String getName() {
            return name;
        }

        public String getEmoji() {
            return emoji;
        }
    }

    // ===========================================
    // LOG CATEGORIES
    // ===========================================

    public enum Category {
        GENERAL("GENERAL", "📋"),
        GUI("GUI", "🖼️"),
        ENGINE("ENGINE", "🧠"),
        ALCHEMY("ALCHEMY", "🔥"),
        TRADING("TRADING", "💰"),
        ANTIBAND("ANTIBAND", "🛡️"),
        DISCORD("DISCORD", "💬"),
        API("API", "🌐"),
        CONFIG("CONFIG", "⚙️"),
        STATS("STATS", "📊");

        private final String name;
        private final String emoji;

        Category(String name, String emoji) {
            this.name = name;
            this.emoji = emoji;
        }

        public String getName() {
            return name;
        }

        public String getEmoji() {
            return emoji;
        }
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    private static LogLevel minimumLogLevel = LogLevel.INFO;
    private static boolean includeTimestamps = true;
    private static boolean includeEmojis = true;
    private static boolean includeCategory = true;
    private static boolean useColors = false; // For terminal color support

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat FULL_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // ===========================================
    // LOGGING METHODS - BY LEVEL
    // ===========================================

    /**
     * Log a debug message (lowest priority)
     */
    public static void debug(String message) {
        log(LogLevel.DEBUG, Category.GENERAL, message);
    }

    public static void debug(Category category, String message) {
        log(LogLevel.DEBUG, category, message);
    }

    /**
     * Log an info message (normal priority)
     */
    public static void info(String message) {
        log(LogLevel.INFO, Category.GENERAL, message);
    }

    public static void info(Category category, String message) {
        log(LogLevel.INFO, category, message);
    }

    /**
     * Log a warning message (high priority)
     */
    public static void warn(String message) {
        log(LogLevel.WARN, Category.GENERAL, message);
    }

    public static void warn(Category category, String message) {
        log(LogLevel.WARN, category, message);
    }

    /**
     * Log an error message (highest priority)
     */
    public static void error(String message) {
        log(LogLevel.ERROR, Category.GENERAL, message);
    }

    public static void error(Category category, String message) {
        log(LogLevel.ERROR, category, message);
    }

    /**
     * Log an error with exception details
     */
    public static void error(String message, Exception e) {
        error(Category.GENERAL, message, e);
    }

    public static void error(Category category, String message, Exception e) {
        String errorMsg = message + ": " + (e != null ? e.getMessage() : "Unknown error");
        log(LogLevel.ERROR, category, errorMsg);

        if (e != null && minimumLogLevel.getPriority() <= LogLevel.DEBUG.getPriority()) {
            e.printStackTrace();
        }
    }

    // ===========================================
    // CORE LOGGING METHOD
    // ===========================================

    /**
     * Core logging method with full control
     */
    public static void log(LogLevel level, Category category, String message) {
        // Check if this log level should be displayed
        if (level.getPriority() < minimumLogLevel.getPriority()) {
            return;
        }

        StringBuilder logMessage = new StringBuilder();

        // Add timestamp if enabled
        if (includeTimestamps) {
            logMessage.append("[").append(TIME_FORMAT.format(new Date())).append("] ");
        }

        // Add level with emoji if enabled
        if (includeEmojis) {
            logMessage.append(level.getEmoji()).append(" ");
        }
        logMessage.append("[").append(level.getName()).append("] ");

        // Add category if enabled
        if (includeCategory) {
            if (includeEmojis) {
                logMessage.append(category.getEmoji()).append(" ");
            }
            logMessage.append("[").append(category.getName()).append("] ");
        }

        // Add the actual message
        logMessage.append(message);

        // Output to appropriate stream
        if (level == LogLevel.ERROR) {
            System.err.println(logMessage.toString());
            org.dreambot.api.utilities.Logger.error(logMessage.toString());
        } else {
            System.out.println(logMessage.toString());
            org.dreambot.api.utilities.Logger.log(logMessage.toString());
        }
    }

    // ===========================================
    // CONVENIENCE METHODS
    // ===========================================

    /**
     * Log initialization messages
     */
    public static void logInit(String componentName) {
        info(Category.GENERAL, "🚀 Initializing " + componentName + "...");
    }

    /**
     * Log completion messages
     */
    public static void logComplete(String componentName) {
        info(Category.GENERAL, "✅ " + componentName + " completed successfully");
    }

    /**
     * Log failure messages
     */
    public static void logFailure(String componentName, String reason) {
        error(Category.GENERAL, "❌ " + componentName + " failed: " + reason);
    }

    /**
     * Log statistics/metrics
     */
    public static void logStats(String stats) {
        info(Category.STATS, stats);
    }

    /**
     * Log action being performed
     */
    public static void logAction(String action) {
        info(Category.ENGINE, "→ " + action);
    }

    /**
     * Log state change
     */
    public static void logStateChange(String from, String to) {
        info(Category.ENGINE, "🔄 State change: " + from + " → " + to);
    }

    /**
     * Log configuration change
     */
    public static void logConfig(String configItem, String value) {
        info(Category.CONFIG, "⚙️ " + configItem + ": " + value);
    }

    /**
     * Log trade/purchase
     */
    public static void logTrade(String item, int quantity, int price) {
        info(Category.TRADING, "💰 " + item + " x" + quantity + " @ " +
                formatGP(price) + " GP each");
    }

    /**
     * Log profit calculation
     */
    public static void logProfit(int profit, String description) {
        String emoji = profit >= 0 ? "📈" : "📉";
        info(Category.STATS, emoji + " " + description + ": " +
                (profit >= 0 ? "+" : "") + formatGP(profit) + " GP");
    }

    // ===========================================
    // FORMATTED LOGGING
    // ===========================================

    /**
     * Log with formatting (similar to printf)
     */
    public static void logf(LogLevel level, Category category, String format, Object... args) {
        String message = String.format(format, args);
        log(level, category, message);
    }

    public static void infof(String format, Object... args) {
        logf(LogLevel.INFO, Category.GENERAL, format, args);
    }

    public static void debugf(String format, Object... args) {
        logf(LogLevel.DEBUG, Category.GENERAL, format, args);
    }

    public static void warnf(String format, Object... args) {
        logf(LogLevel.WARN, Category.GENERAL, format, args);
    }

    public static void errorf(String format, Object... args) {
        logf(LogLevel.ERROR, Category.GENERAL, format, args);
    }

    // ===========================================
    // SECTION HEADERS
    // ===========================================

    /**
     * Log a section header for better organization
     */
    public static void logSection(String sectionName) {
        String separator = "=".repeat(50);
        info(Category.GENERAL, "");
        info(Category.GENERAL, separator);
        info(Category.GENERAL, "  " + sectionName);
        info(Category.GENERAL, separator);
    }

    /**
     * Log a subsection
     */
    public static void logSubsection(String subsectionName) {
        info(Category.GENERAL, "");
        info(Category.GENERAL, "--- " + subsectionName + " ---");
    }

    // ===========================================
    // PROGRESS TRACKING
    // ===========================================

    /**
     * Log progress (e.g., "50/100 items processed")
     */
    public static void logProgress(int current, int total, String description) {
        double percentage = (current * 100.0) / total;
        infof(Category.GENERAL, "⏳ %s: %d/%d (%.1f%%)", description, current, total, percentage);
    }

    /**
     * Log progress bar
     */
    public static void logProgressBar(int current, int total, int barLength) {
        double percentage = (current * 100.0) / total;
        int filled = (int) ((current * barLength) / total);
        int empty = barLength - filled;

        StringBuilder bar = new StringBuilder("[");
        bar.append("█".repeat(Math.max(0, filled)));
        bar.append("░".repeat(Math.max(0, empty)));
        bar.append("] ");
        bar.append(String.format("%.1f%%", percentage));

        info(Category.GENERAL, bar.toString());
    }

    // ===========================================
    // CONFIGURATION METHODS
    // ===========================================

    /**
     * Set minimum log level to display
     */
    public static void setMinimumLogLevel(LogLevel level) {
        minimumLogLevel = level;
        info(Category.CONFIG, "Log level set to: " + level.getName());
    }

    /**
     * Enable or disable timestamps
     */
    public static void setIncludeTimestamps(boolean include) {
        includeTimestamps = include;
    }

    /**
     * Enable or disable emojis
     */
    public static void setIncludeEmojis(boolean include) {
        includeEmojis = include;
    }

    /**
     * Enable or disable category labels
     */
    public static void setIncludeCategory(boolean include) {
        includeCategory = include;
    }

    /**
     * Get current minimum log level
     */
    public static LogLevel getMinimumLogLevel() {
        return minimumLogLevel;
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Format GP amount
     */
    private static String formatGP(int amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fB", amount / 1_000_000_000.0);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fM", amount / 1_000_000.0);
        } else if (amount >= 1_000) {
            return String.format("%.1fK", amount / 1_000.0);
        } else {
            return String.valueOf(amount);
        }
    }

    /**
     * Measure and log execution time
     */
    public static void timeExecution(Runnable code, String description) {
        long startTime = System.currentTimeMillis();
        code.run();
        long endTime = System.currentTimeMillis();
        debug(Category.GENERAL, "⏱️ " + description + " took " + (endTime - startTime) + "ms");
    }

    /**
     * Prevent instantiation
     */
    private Logger() {
        throw new AssertionError("Cannot instantiate Logger class");
    }
}
