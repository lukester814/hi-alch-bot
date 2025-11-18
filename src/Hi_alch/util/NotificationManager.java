package Hi_alch.util;

import Hi_alch.core.Logger;
import Hi_alch.overlay.ScriptStatistics;
import Hi_alch.gui.GUIConfiguration;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * Notification Manager - Centralized Notification System
 *
 * Coordinates all notifications across the bot:
 * - Discord webhook notifications
 * - GUI popup notifications
 * - System tray notifications (future)
 * - Logging notifications
 *
 * Prevents notification spam and manages notification priorities.
 */
public class NotificationManager {

    // ===========================================
    // NOTIFICATION TYPES
    // ===========================================

    public enum NotificationType {
        INFO("Information", JOptionPane.INFORMATION_MESSAGE),
        SUCCESS("Success", JOptionPane.INFORMATION_MESSAGE),
        WARNING("Warning", JOptionPane.WARNING_MESSAGE),
        ERROR("Error", JOptionPane.ERROR_MESSAGE),
        QUESTION("Question", JOptionPane.QUESTION_MESSAGE);

        private final String name;
        private final int dialogType;

        NotificationType(String name, int dialogType) {
            this.name = name;
            this.dialogType = dialogType;
        }

        public String getName() {
            return name;
        }

        public int getDialogType() {
            return dialogType;
        }
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    private static String discordWebhookUrl = null;
    private static boolean discordEnabled = false;
    private static boolean guiNotificationsEnabled = true;
    private static long lastNotificationTime = 0;
    private static long minNotificationInterval = 5000; // 5 seconds between notifications

    private static final List<String> recentNotifications = new ArrayList<>();
    private static final int MAX_RECENT_NOTIFICATIONS = 10;

    // ===========================================
    // CONFIGURATION METHODS
    // ===========================================

    /**
     * Configure Discord webhook
     */
    public static void configureDiscord(String webhookUrl, boolean enabled) {
        discordWebhookUrl = webhookUrl;
        discordEnabled = enabled && DiscordWebhookUtil.isValidWebhookUrl(webhookUrl);

        if (discordEnabled) {
            Logger.info(Logger.Category.DISCORD, "✅ Discord notifications enabled");
        } else {
            Logger.info(Logger.Category.DISCORD, "❌ Discord notifications disabled");
        }
    }

    /**
     * Enable/disable GUI notifications
     */
    public static void setGUINotificationsEnabled(boolean enabled) {
        guiNotificationsEnabled = enabled;
    }

    /**
     * Set minimum interval between notifications
     */
    public static void setMinNotificationInterval(long milliseconds) {
        minNotificationInterval = milliseconds;
    }

    // ===========================================
    // NOTIFICATION METHODS
    // ===========================================

    /**
     * Send notification to all enabled channels
     */
    public static void notify(NotificationType type, String title, String message) {
        // Check spam prevention
        if (!canSendNotification()) {
            Logger.debug(Logger.Category.GENERAL, "Notification throttled: " + title);
            return;
        }

        // Log notification
        switch (type) {
            case ERROR:
                Logger.error(Logger.Category.GENERAL, title + ": " + message);
                break;
            case WARNING:
                Logger.warn(Logger.Category.GENERAL, title + ": " + message);
                break;
            default:
                Logger.info(Logger.Category.GENERAL, title + ": " + message);
                break;
        }

        // Track notification
        trackNotification(title + ": " + message);

        // Send GUI notification if enabled
        if (guiNotificationsEnabled) {
            sendGUINotification(type, title, message);
        }

        // Send Discord notification for important messages
        if (discordEnabled && shouldSendToDiscord(type)) {
            sendDiscordNotification(type, title, message);
        }
    }

    /**
     * Send simple notification
     */
    public static void notify(String message) {
        notify(NotificationType.INFO, "Notification", message);
    }

    /**
     * Send success notification
     */
    public static void notifySuccess(String message) {
        notify(NotificationType.SUCCESS, "Success", message);
    }

    /**
     * Send warning notification
     */
    public static void notifyWarning(String message) {
        notify(NotificationType.WARNING, "Warning", message);
    }

    /**
     * Send error notification
     */
    public static void notifyError(String message) {
        notify(NotificationType.ERROR, "Error", message);
    }

    // ===========================================
    // BOT EVENT NOTIFICATIONS
    // ===========================================

    /**
     * Notify bot started
     */
    public static void notifyBotStarted(GUIConfiguration config) {
        String message = "High Alchemy Bot started for item: " + config.selectedItemName;
        notify(NotificationType.INFO, "Bot Started", message);

        // Send detailed Discord notification
        if (discordEnabled) {
            DiscordWebhookUtil.sendStartupNotification(discordWebhookUrl, config);
        }
    }

    /**
     * Notify bot stopped
     */
    public static void notifyBotStopped(String reason) {
        String message = "Bot stopped: " + reason;
        notify(NotificationType.WARNING, "Bot Stopped", message);

        // Send Discord notification
        if (discordEnabled) {
            DiscordWebhookUtil.sendStoppedNotification(discordWebhookUrl, reason);
        }
    }

    /**
     * Notify session complete
     */
    public static void notifySessionComplete(ScriptStatistics stats, String itemName) {
        String message = String.format("Session complete! Alchs: %d, Profit: %,d GP, Runtime: %s",
                stats.alchsCompleted, stats.totalProfit, formatDuration(stats.scriptRuntime));

        notify(NotificationType.SUCCESS, "Session Complete", message);

        // Send detailed Discord summary
        if (discordEnabled) {
            DiscordWebhookUtil.sendSessionSummary(discordWebhookUrl, stats, itemName);
        }
    }

    /**
     * Notify progress update (periodic)
     */
    public static void notifyProgress(ScriptStatistics stats) {
        // Only send Discord updates (not GUI popups for progress)
        if (discordEnabled) {
            DiscordWebhookUtil.sendProgressUpdate(discordWebhookUrl, stats);
        }

        // Log progress
        Logger.logStats(String.format("Progress: %d alchs, %,d GP profit, %.0f alchs/hr",
                stats.alchsCompleted, stats.totalProfit, stats.alchsPerHour));
    }

    /**
     * Notify error occurred
     */
    public static void notifyError(String errorMessage, boolean critical) {
        NotificationType type = critical ? NotificationType.ERROR : NotificationType.WARNING;
        String title = critical ? "Critical Error" : "Error";

        notify(type, title, errorMessage);

        // Send Discord notification for errors
        if (discordEnabled) {
            DiscordWebhookUtil.sendErrorNotification(discordWebhookUrl, errorMessage);
        }
    }

    /**
     * Notify mule needed
     */
    public static void notifyMuleNeeded(int gpAmount) {
        String message = String.format("Mule needed! Current GP: %,d", gpAmount);
        notify(NotificationType.WARNING, "Mule Required", message);
    }

    /**
     * Notify mule complete
     */
    public static void notifyMuleComplete(int gpTransferred, boolean success) {
        if (success) {
            String message = String.format("Successfully muled %,d GP", gpTransferred);
            notify(NotificationType.SUCCESS, "Mule Complete", message);
        } else {
            notify(NotificationType.ERROR, "Mule Failed", "Failed to complete mule operation");
        }
    }

    // ===========================================
    // GUI NOTIFICATIONS
    // ===========================================

    /**
     * Send GUI popup notification
     */
    private static void sendGUINotification(NotificationType type, String title, String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                JOptionPane.showMessageDialog(
                    null,
                    message,
                    title,
                    type.getDialogType()
                );
            } catch (Exception e) {
                Logger.debug(Logger.Category.GUI, "Failed to show GUI notification: " + e.getMessage());
            }
        });
    }

    /**
     * Ask user a question via GUI
     */
    public static boolean askQuestion(String title, String question) {
        final boolean[] result = {false};

        try {
            SwingUtilities.invokeAndWait(() -> {
                int response = JOptionPane.showConfirmDialog(
                    null,
                    question,
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                result[0] = (response == JOptionPane.YES_OPTION);
            });
        } catch (Exception e) {
            Logger.error(Logger.Category.GUI, "Error showing question dialog", e);
            return false;
        }

        return result[0];
    }

    // ===========================================
    // DISCORD NOTIFICATIONS
    // ===========================================

    /**
     * Send Discord notification
     */
    private static void sendDiscordNotification(NotificationType type, String title, String message) {
        if (!discordEnabled || discordWebhookUrl == null) {
            return;
        }

        // Simple message for basic notifications
        String fullMessage = "**" + title + "**\n" + message;
        DiscordWebhookUtil.sendMessage(discordWebhookUrl, fullMessage);
    }

    /**
     * Determine if notification type should go to Discord
     */
    private static boolean shouldSendToDiscord(NotificationType type) {
        // Only send warnings and errors to Discord
        // Info messages are too spammy
        return type == NotificationType.WARNING || type == NotificationType.ERROR;
    }

    // ===========================================
    // SPAM PREVENTION
    // ===========================================

    /**
     * Check if notification can be sent (anti-spam)
     */
    private static boolean canSendNotification() {
        long now = System.currentTimeMillis();

        if (now - lastNotificationTime < minNotificationInterval) {
            return false;
        }

        lastNotificationTime = now;
        return true;
    }

    /**
     * Track recent notifications
     */
    private static void trackNotification(String notification) {
        recentNotifications.add(0, notification);

        // Keep only recent notifications
        while (recentNotifications.size() > MAX_RECENT_NOTIFICATIONS) {
            recentNotifications.remove(recentNotifications.size() - 1);
        }
    }

    /**
     * Get recent notifications
     */
    public static List<String> getRecentNotifications() {
        return new ArrayList<>(recentNotifications);
    }

    /**
     * Clear recent notifications
     */
    public static void clearRecentNotifications() {
        recentNotifications.clear();
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Format duration
     */
    private static String formatDuration(long milliseconds) {
        long hours = milliseconds / 3600000;
        long minutes = (milliseconds % 3600000) / 60000;
        long seconds = (milliseconds % 60000) / 1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Test all notification channels
     */
    public static void testNotifications() {
        Logger.info(Logger.Category.GENERAL, "🧪 Testing notification channels...");

        // Test Discord if enabled
        if (discordEnabled) {
            boolean success = DiscordWebhookUtil.testWebhook(discordWebhookUrl);
            if (success) {
                Logger.info(Logger.Category.DISCORD, "✅ Discord test successful");
            } else {
                Logger.error(Logger.Category.DISCORD, "❌ Discord test failed");
            }
        }

        // Test GUI notification
        if (guiNotificationsEnabled) {
            notify(NotificationType.INFO, "Test Notification", "All notification systems are working!");
        }
    }

    /**
     * Prevent instantiation
     */
    private NotificationManager() {
        throw new AssertionError("Cannot instantiate NotificationManager class");
    }
}
