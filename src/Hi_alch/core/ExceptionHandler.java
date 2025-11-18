package Hi_alch.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized Exception Handling System
 *
 * Provides structured exception handling, error tracking, and recovery strategies.
 * This class helps manage errors consistently across the application.
 */
public class ExceptionHandler {

    // ===========================================
    // ERROR TRACKING
    // ===========================================

    private static final Map<String, Integer> errorCounts = new ConcurrentHashMap<>();
    private static final Map<String, Long> lastErrorTimes = new ConcurrentHashMap<>();
    private static final Map<String, String> lastErrorMessages = new ConcurrentHashMap<>();

    private static int totalErrors = 0;
    private static int criticalErrors = 0;

    // ===========================================
    // ERROR SEVERITY LEVELS
    // ===========================================

    public enum ErrorSeverity {
        LOW("Low", "Minor issue, can continue"),
        MEDIUM("Medium", "Moderate issue, may affect functionality"),
        HIGH("High", "Serious issue, requires attention"),
        CRITICAL("Critical", "Fatal error, bot should stop");

        private final String level;
        private final String description;

        ErrorSeverity(String level, String description) {
            this.level = level;
            this.description = description;
        }

        public String getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }
    }

    // ===========================================
    // ERROR CATEGORIES
    // ===========================================

    public enum ErrorCategory {
        GUI("GUI Error", "Error in graphical user interface"),
        NETWORK("Network Error", "Network or API error"),
        TRADING("Trading Error", "Grand Exchange or trading error"),
        ALCHEMY("Alchemy Error", "High alchemy execution error"),
        CONFIGURATION("Configuration Error", "Configuration or settings error"),
        VALIDATION("Validation Error", "Data validation error"),
        DISCORD("Discord Error", "Discord webhook error"),
        ANTIBAND("Anti-ban Error", "Anti-ban system error"),
        FILE_IO("File I/O Error", "File read/write error"),
        GENERAL("General Error", "Uncategorized error");

        private final String name;
        private final String description;

        ErrorCategory(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    // ===========================================
    // EXCEPTION HANDLING METHODS
    // ===========================================

    /**
     * Handle an exception with full details
     */
    public static void handle(Exception e, ErrorCategory category, ErrorSeverity severity, String context) {
        if (e == null) {
            return;
        }

        // Track the error
        trackError(category.name(), e.getMessage());

        // Build error message
        String errorKey = category.name() + ":" + e.getClass().getSimpleName();
        String fullMessage = buildErrorMessage(e, category, severity, context);

        // Log based on severity
        switch (severity) {
            case LOW:
                Logger.warn(Logger.Category.GENERAL, fullMessage);
                break;
            case MEDIUM:
                Logger.error(Logger.Category.GENERAL, fullMessage);
                break;
            case HIGH:
                Logger.error(Logger.Category.GENERAL, fullMessage);
                logStackTrace(e);
                break;
            case CRITICAL:
                criticalErrors++;
                Logger.error(Logger.Category.GENERAL, "🚨 CRITICAL ERROR: " + fullMessage);
                logStackTrace(e);
                break;
        }

        // Store last error details
        lastErrorMessages.put(errorKey, fullMessage);
    }

    /**
     * Handle exception with medium severity by default
     */
    public static void handle(Exception e, ErrorCategory category, String context) {
        handle(e, category, ErrorSeverity.MEDIUM, context);
    }

    /**
     * Handle exception with general category and medium severity
     */
    public static void handle(Exception e, String context) {
        handle(e, ErrorCategory.GENERAL, ErrorSeverity.MEDIUM, context);
    }

    /**
     * Handle exception with minimal details
     */
    public static void handle(Exception e) {
        handle(e, ErrorCategory.GENERAL, ErrorSeverity.MEDIUM, "Unknown context");
    }

    // ===========================================
    // SAFE EXECUTION WRAPPERS
    // ===========================================

    /**
     * Execute code safely with exception handling
     */
    public static void safeExecute(Runnable code, String description) {
        try {
            code.run();
        } catch (Exception e) {
            handle(e, ErrorCategory.GENERAL, ErrorSeverity.LOW, description);
        }
    }

    /**
     * Execute code safely with custom error category
     */
    public static void safeExecute(Runnable code, ErrorCategory category, String description) {
        try {
            code.run();
        } catch (Exception e) {
            handle(e, category, ErrorSeverity.LOW, description);
        }
    }

    /**
     * Execute code safely and return success status
     */
    public static boolean safeExecuteWithResult(Runnable code, String description) {
        try {
            code.run();
            return true;
        } catch (Exception e) {
            handle(e, ErrorCategory.GENERAL, ErrorSeverity.LOW, description);
            return false;
        }
    }

    /**
     * Execute code with retry logic
     */
    public static boolean executeWithRetry(Runnable code, int maxAttempts, String description) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                code.run();
                if (attempt > 1) {
                    Logger.info(Logger.Category.GENERAL, "✅ " + description + " succeeded on attempt " + attempt);
                }
                return true;
            } catch (Exception e) {
                if (attempt < maxAttempts) {
                    Logger.warn(Logger.Category.GENERAL, "⚠️ " + description + " failed (attempt " + attempt + "/" + maxAttempts + "), retrying...");
                    try {
                        Thread.sleep(1000 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                } else {
                    handle(e, ErrorCategory.GENERAL, ErrorSeverity.MEDIUM, description + " (all " + maxAttempts + " attempts failed)");
                    return false;
                }
            }
        }
        return false;
    }

    // ===========================================
    // ERROR MESSAGE BUILDING
    // ===========================================

    /**
     * Build a detailed error message
     */
    private static String buildErrorMessage(Exception e, ErrorCategory category, ErrorSeverity severity, String context) {
        StringBuilder message = new StringBuilder();

        message.append("[").append(severity.getLevel()).append("] ");
        message.append(category.getName()).append(": ");
        message.append(context);

        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            message.append(" - ").append(e.getMessage());
        }

        message.append(" (").append(e.getClass().getSimpleName()).append(")");

        return message.toString();
    }

    /**
     * Log full stack trace
     */
    private static void logStackTrace(Exception e) {
        if (Logger.getMinimumLogLevel().getPriority() <= Logger.LogLevel.DEBUG.getPriority()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Logger.debug("Stack trace:\n" + sw.toString());
        }
    }

    // ===========================================
    // ERROR TRACKING & STATISTICS
    // ===========================================

    /**
     * Track an error occurrence
     */
    private static void trackError(String errorType, String message) {
        totalErrors++;

        errorCounts.put(errorType, errorCounts.getOrDefault(errorType, 0) + 1);
        lastErrorTimes.put(errorType, System.currentTimeMillis());
        if (message != null) {
            lastErrorMessages.put(errorType, message);
        }
    }

    /**
     * Get error count for a specific type
     */
    public static int getErrorCount(String errorType) {
        return errorCounts.getOrDefault(errorType, 0);
    }

    /**
     * Get error count for a category
     */
    public static int getErrorCount(ErrorCategory category) {
        return getErrorCount(category.name());
    }

    /**
     * Get total error count
     */
    public static int getTotalErrors() {
        return totalErrors;
    }

    /**
     * Get critical error count
     */
    public static int getCriticalErrors() {
        return criticalErrors;
    }

    /**
     * Check if error threshold exceeded
     */
    public static boolean hasExceededErrorThreshold() {
        return totalErrors > Constants.MAX_ERRORS_BEFORE_STOP;
    }

    /**
     * Check if too many errors of a specific type
     */
    public static boolean hasExceededErrorThreshold(ErrorCategory category, int threshold) {
        return getErrorCount(category) > threshold;
    }

    /**
     * Get last error time for a category
     */
    public static Long getLastErrorTime(ErrorCategory category) {
        return lastErrorTimes.get(category.name());
    }

    /**
     * Get last error message for a category
     */
    public static String getLastErrorMessage(ErrorCategory category) {
        return lastErrorMessages.get(category.name());
    }

    /**
     * Check if error occurred recently
     */
    public static boolean hasRecentError(ErrorCategory category, long milliseconds) {
        Long lastTime = getLastErrorTime(category);
        if (lastTime == null) {
            return false;
        }
        return (System.currentTimeMillis() - lastTime) < milliseconds;
    }

    // ===========================================
    // ERROR RECOVERY
    // ===========================================

    /**
     * Suggest recovery action based on error category
     */
    public static String suggestRecovery(ErrorCategory category) {
        switch (category) {
            case NETWORK:
                return "Check internet connection and retry";
            case TRADING:
                return "Verify Grand Exchange access and try again";
            case ALCHEMY:
                return "Check magic level and nature rune supply";
            case CONFIGURATION:
                return "Review and correct configuration settings";
            case VALIDATION:
                return "Check input values and try again";
            case DISCORD:
                return "Verify Discord webhook URL is correct";
            case FILE_IO:
                return "Check file permissions and disk space";
            default:
                return "Review logs and retry operation";
        }
    }

    /**
     * Determine if bot should stop based on errors
     */
    public static boolean shouldStopBot() {
        if (criticalErrors > 0) {
            Logger.error(Logger.Category.GENERAL, "🛑 Critical error detected - bot should stop");
            return true;
        }

        if (hasExceededErrorThreshold()) {
            Logger.error(Logger.Category.GENERAL, "🛑 Error threshold exceeded (" + totalErrors + " errors) - bot should stop");
            return true;
        }

        return false;
    }

    // ===========================================
    // RESET & CLEANUP
    // ===========================================

    /**
     * Reset error tracking
     */
    public static void reset() {
        errorCounts.clear();
        lastErrorTimes.clear();
        lastErrorMessages.clear();
        totalErrors = 0;
        criticalErrors = 0;
        Logger.info(Logger.Category.GENERAL, "🔄 Error tracking reset");
    }

    /**
     * Reset errors for a specific category
     */
    public static void reset(ErrorCategory category) {
        String key = category.name();
        errorCounts.remove(key);
        lastErrorTimes.remove(key);
        lastErrorMessages.remove(key);
        Logger.debug("Reset errors for category: " + category.getName());
    }

    /**
     * Get error statistics summary
     */
    public static String getErrorSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Error Statistics:\n");
        summary.append("  Total Errors: ").append(totalErrors).append("\n");
        summary.append("  Critical Errors: ").append(criticalErrors).append("\n");
        summary.append("  Error Types: ").append(errorCounts.size()).append("\n");

        if (!errorCounts.isEmpty()) {
            summary.append("\nError Breakdown:\n");
            errorCounts.forEach((type, count) -> {
                summary.append("  ").append(type).append(": ").append(count).append("\n");
            });
        }

        return summary.toString();
    }

    /**
     * Log error statistics
     */
    public static void logErrorStatistics() {
        Logger.info(Logger.Category.GENERAL, "📊 " + getErrorSummary());
    }

    // ===========================================
    // SPECIFIC ERROR HANDLERS
    // ===========================================

    /**
     * Handle network errors with automatic retry suggestion
     */
    public static void handleNetworkError(Exception e, String context) {
        handle(e, ErrorCategory.NETWORK, ErrorSeverity.MEDIUM, context);
        Logger.info(Logger.Category.GENERAL, "💡 " + suggestRecovery(ErrorCategory.NETWORK));
    }

    /**
     * Handle GUI errors (usually low severity)
     */
    public static void handleGUIError(Exception e, String context) {
        handle(e, ErrorCategory.GUI, ErrorSeverity.LOW, context);
    }

    /**
     * Handle configuration errors (medium severity)
     */
    public static void handleConfigError(Exception e, String context) {
        handle(e, ErrorCategory.CONFIGURATION, ErrorSeverity.MEDIUM, context);
    }

    /**
     * Handle validation errors (low severity - expected)
     */
    public static void handleValidationError(String message, String context) {
        Logger.warn(Logger.Category.GENERAL, "Validation Error: " + context + " - " + message);
        trackError(ErrorCategory.VALIDATION.name(), message);
    }

    /**
     * Handle critical errors that should stop the bot
     */
    public static void handleCriticalError(Exception e, String context) {
        handle(e, ErrorCategory.GENERAL, ErrorSeverity.CRITICAL, context);
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Extract root cause from exception chain
     */
    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * Get simplified exception message
     */
    public static String getSimpleMessage(Exception e) {
        if (e == null) {
            return "Unknown error";
        }
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            return e.getClass().getSimpleName();
        }
        return message;
    }

    /**
     * Prevent instantiation
     */
    private ExceptionHandler() {
        throw new AssertionError("Cannot instantiate ExceptionHandler class");
    }
}
