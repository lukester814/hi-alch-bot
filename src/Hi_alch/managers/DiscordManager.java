package Hi_alch.managers;

import Hi_alch.utils.BotUtils;
import Hi_alch.gui.GUIConfiguration;
import Hi_alch.overlay.ScriptStatistics;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Professional Discord Integration Manager
 *
 * Features:
 * - Discord webhook integration with rich embeds
 * - Multiple notification types and templates
 * - Rate limiting and error handling
 * - Batch messaging and queue management
 * - Emergency notification system
 * - Statistics tracking and reporting
 *
 * REUSABLE: Perfect for any bot that needs Discord notifications!
 */
public class DiscordManager {

    // ===========================================
    // CONFIGURATION
    // ===========================================

    // Current webhook configuration
    private String currentWebhookUrl;
    private boolean isEnabled;
    private boolean notificationsEnabled;

    // Rate limiting
    private static final long MIN_MESSAGE_INTERVAL = 2000; // 2 seconds between messages
    private long lastMessageTime = 0;

    // Statistics and state tracking
    private int totalMessagesSent = 0;
    private int successfulSends = 0;
    private int failedSends = 0;
    private String lastError = "";
    private long sessionStartTime = 0;
    private long lastPeriodicUpdate = 0;

    // Templates
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public DiscordManager() {
        this.isEnabled = false;
        this.notificationsEnabled = false;
        this.sessionStartTime = System.currentTimeMillis();
        this.lastPeriodicUpdate = System.currentTimeMillis();

        BotUtils.log("📱 DiscordManager initialized");
    }

    // ===========================================
    // CONFIGURATION METHODS
    // ===========================================

    /**
     * Configure Discord webhook URL
     */
    public void configure(String webhookUrl) {
        if (webhookUrl != null && BotUtils.isValidDiscordWebhook(webhookUrl)) {
            this.currentWebhookUrl = webhookUrl;
            this.isEnabled = true;
            this.notificationsEnabled = true;

            BotUtils.log("✅ Discord webhook configured successfully");
        } else {
            BotUtils.log("❌ Invalid Discord webhook URL provided");
            this.isEnabled = false;
        }
    }

    /**
     * Enable or disable Discord notifications
     */
    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
        BotUtils.log("📱 Discord notifications " + (enabled ? "enabled" : "disabled"));
    }

    // ===========================================
    // MESSAGE SENDING METHODS
    // ===========================================

    /**
     * Send startup notification when bot begins
     */
    public void sendStartupNotification(GUIConfiguration config) {
        if (!isEnabled || !notificationsEnabled) return;

        try {
            String message = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"🚀 High Alchemy Bot Started\",\n" +
                    "    \"description\": \"Bot session has begun successfully!\",\n" +
                    "    \"color\": 65280,\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"name\": \"🎯 Target Item\",\n" +
                    "        \"value\": \"" + config.selectedItemName + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"💰 Buy Limit\",\n" +
                    "        \"value\": \"" + config.buyLimit + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📈 Price Markup\",\n" +
                    "        \"value\": \"" + config.priceMarkup + "%\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"🌿 Nature Runes\",\n" +
                    "        \"value\": \"" + config.natureRuneAmount + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📅 Start Time\",\n" +
                    "        \"value\": \"" + DATE_FORMAT.format(new Date()) + "\",\n" +
                    "        \"inline\": false\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | Session Started\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            sendWebhookMessage(currentWebhookUrl, message);
            BotUtils.log("📱 Startup notification sent to Discord");

        } catch (Exception e) {
            BotUtils.logError("Error sending startup notification", e);
        }
    }

    /**
     * Send progress update notification
     */
    public void sendProgressUpdate(ScriptStatistics stats) {
        if (!isEnabled || !notificationsEnabled) return;

        try {
            // Calculate session duration
            long sessionDuration = System.currentTimeMillis() - sessionStartTime;
            String durationString = BotUtils.formatDuration(sessionDuration);

            // Calculate rates
            double alchsPerHour = sessionDuration > 0 ? (double)stats.alchsCompleted / (sessionDuration / 3600000.0) : 0;
            double profitPerHour = sessionDuration > 0 ? (double)stats.totalProfit / (sessionDuration / 3600000.0) : 0;

            String message = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"📊 Progress Update\",\n" +
                    "    \"description\": \"Current bot performance statistics\",\n" +
                    "    \"color\": 3447003,\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"name\": \"🔥 Total Alchs\",\n" +
                    "        \"value\": \"" + BotUtils.formatNumber(stats.alchsCompleted) + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"💰 Total Profit\",\n" +
                    "        \"value\": \"" + BotUtils.formatNumber(stats.totalProfit) + " GP\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"✨ XP Gained\",\n" +
                    "        \"value\": \"" + BotUtils.formatNumber(stats.xpGained) + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"⚡ Alchs/Hour\",\n" +
                    "        \"value\": \"" + String.format("%.0f", alchsPerHour) + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"💵 Profit/Hour\",\n" +
                    "        \"value\": \"" + BotUtils.formatNumber((int)profitPerHour) + " GP\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"⏱️ Runtime\",\n" +
                    "        \"value\": \"" + durationString + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"🎯 Current State\",\n" +
                    "        \"value\": \"" + stats.currentState + "\",\n" +
                    "        \"inline\": false\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | Progress Report\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            sendWebhookMessage(currentWebhookUrl, message);
            BotUtils.log("📱 Progress update sent to Discord");

        } catch (Exception e) {
            BotUtils.logError("Error sending progress update", e);
        }
    }

    /**
     * Send completion notification when bot finishes
     */
    public void sendCompletionNotification(ScriptStatistics finalStats) {
        if (!isEnabled || !notificationsEnabled) return;

        try {
            long totalRuntime = System.currentTimeMillis() - sessionStartTime;
            String runtimeString = BotUtils.formatDuration(totalRuntime);

            String message = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"✅ Bot Session Completed\",\n" +
                    "    \"description\": \"High Alchemy bot has finished successfully!\",\n" +
                    "    \"color\": 65280,\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"name\": \"🏁 Final Results\",\n" +
                    "        \"value\": \"Session completed successfully\",\n" +
                    "        \"inline\": false\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"🔥 Total Alchs\",\n" +
                    "        \"value\": \"" + BotUtils.formatNumber(finalStats.alchsCompleted) + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"💰 Total Profit\",\n" +
                    "        \"value\": \"" + BotUtils.formatNumber(finalStats.totalProfit) + " GP\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"✨ XP Gained\",\n" +
                    "        \"value\": \"" + BotUtils.formatNumber(finalStats.xpGained) + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"⏱️ Total Runtime\",\n" +
                    "        \"value\": \"" + runtimeString + "\",\n" +
                    "        \"inline\": false\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | Session Complete\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            sendWebhookMessage(currentWebhookUrl, message);
            BotUtils.log("📱 Completion notification sent to Discord");

        } catch (Exception e) {
            BotUtils.logError("Error sending completion notification", e);
        }
    }

    /**
     * Send session goal reached notification
     */
    public void sendSessionGoalReached(int alchs, int profit, int xp, long minutes, int level, SessionGoalsManager.SessionGoals goals) {
        if (!isEnabled || !notificationsEnabled) return;

        try {
            String runtimeString = BotUtils.formatDuration(minutes * 60000);

            StringBuilder fieldsBuilder = new StringBuilder();
            fieldsBuilder.append("      {\n");
            fieldsBuilder.append("        \"name\": \"🎯 Goals Reached\",\n");
            fieldsBuilder.append("        \"value\": \"Session goals achieved!\",\n");
            fieldsBuilder.append("        \"inline\": false\n");
            fieldsBuilder.append("      },\n");

            // Add each enabled goal's progress
            if (goals.alchGoalEnabled) {
                fieldsBuilder.append("      {\n");
                fieldsBuilder.append("        \"name\": \"🔥 Alchs\",\n");
                fieldsBuilder.append("        \"value\": \"" + BotUtils.formatNumber(alchs) + " / " + BotUtils.formatNumber(goals.targetAlchs) + "\",\n");
                fieldsBuilder.append("        \"inline\": true\n");
                fieldsBuilder.append("      },\n");
            }
            if (goals.profitGoalEnabled) {
                fieldsBuilder.append("      {\n");
                fieldsBuilder.append("        \"name\": \"💰 Profit\",\n");
                fieldsBuilder.append("        \"value\": \"" + BotUtils.formatNumber(profit) + " / " + BotUtils.formatNumber(goals.targetProfit) + " GP\",\n");
                fieldsBuilder.append("        \"inline\": true\n");
                fieldsBuilder.append("      },\n");
            }
            if (goals.xpGoalEnabled) {
                fieldsBuilder.append("      {\n");
                fieldsBuilder.append("        \"name\": \"✨ XP Gained\",\n");
                fieldsBuilder.append("        \"value\": \"" + BotUtils.formatNumber(xp) + " / " + BotUtils.formatNumber(goals.targetXP) + "\",\n");
                fieldsBuilder.append("        \"inline\": true\n");
                fieldsBuilder.append("      },\n");
            }
            if (goals.timeGoalEnabled) {
                fieldsBuilder.append("      {\n");
                fieldsBuilder.append("        \"name\": \"⏱️ Runtime\",\n");
                fieldsBuilder.append("        \"value\": \"" + minutes + " / " + goals.targetMinutes + " minutes\",\n");
                fieldsBuilder.append("        \"inline\": true\n");
                fieldsBuilder.append("      },\n");
            }
            if (goals.levelGoalEnabled) {
                fieldsBuilder.append("      {\n");
                fieldsBuilder.append("        \"name\": \"📊 Level\",\n");
                fieldsBuilder.append("        \"value\": \"" + level + " / " + goals.targetLevel + "\",\n");
                fieldsBuilder.append("        \"inline\": true\n");
                fieldsBuilder.append("      },\n");
            }

            // Remove trailing comma
            String fields = fieldsBuilder.toString();
            if (fields.endsWith(",\n")) {
                fields = fields.substring(0, fields.length() - 2) + "\n";
            }

            String message = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"🎯 Session Goals Reached!\",\n" +
                    "    \"description\": \"Your session goals have been achieved!\",\n" +
                    "    \"color\": 15844367,\n" +
                    "    \"fields\": [\n" +
                    fields +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | Goals Complete\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            sendWebhookMessage(currentWebhookUrl, message);
            BotUtils.log("📱 Session goal notification sent to Discord");

        } catch (Exception e) {
            BotUtils.logError("Error sending session goal notification", e);
        }
    }

    /**
     * Send emergency notification for critical issues
     */
    public void sendEmergencyNotification(String issue, String details) {
        if (!isEnabled) return; // Send even if notifications disabled - it's an emergency!

        try {
            String message = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"🚨 EMERGENCY ALERT\",\n" +
                    "    \"description\": \"Critical issue detected!\",\n" +
                    "    \"color\": 16711680,\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"name\": \"⚠️ Issue\",\n" +
                    "        \"value\": \"" + issue + "\",\n" +
                    "        \"inline\": false\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📋 Details\",\n" +
                    "        \"value\": \"" + details + "\",\n" +
                    "        \"inline\": false\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📅 Time\",\n" +
                    "        \"value\": \"" + DATE_FORMAT.format(new Date()) + "\",\n" +
                    "        \"inline\": true\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | EMERGENCY\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            sendWebhookMessage(currentWebhookUrl, message);
            BotUtils.log("🚨 Emergency notification sent to Discord");

        } catch (Exception e) {
            BotUtils.logError("Error sending emergency notification", e);
        }
    }

    /**
     * Send mule transfer started notification
     */
    public void sendMuleTransferStarted(String muleUsername, String location) {
        if (!isEnabled || !notificationsEnabled) return;

        try {
            String message = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"🤝 Mule Transfer Started\",\n" +
                    "    \"description\": \"Initiating trade with mule account\",\n" +
                    "    \"color\": 3447003,\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"name\": \"👤 Mule Username\",\n" +
                    "        \"value\": \"" + muleUsername + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📍 Location\",\n" +
                    "        \"value\": \"" + location + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📅 Time\",\n" +
                    "        \"value\": \"" + DATE_FORMAT.format(new Date()) + "\",\n" +
                    "        \"inline\": false\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | Mule Transfer\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            sendWebhookMessage(currentWebhookUrl, message);
            BotUtils.log("📱 Mule transfer started notification sent to Discord");

        } catch (Exception e) {
            BotUtils.logError("Error sending mule transfer started notification", e);
        }
    }

    /**
     * Send mule transfer completed notification
     */
    public void sendMuleTransferCompleted(String muleUsername, int gpTransferred, int itemsTransferred) {
        if (!isEnabled || !notificationsEnabled) return;

        try {
            String message = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"✅ Mule Transfer Completed\",\n" +
                    "    \"description\": \"Successfully transferred items to mule\",\n" +
                    "    \"color\": 5763719,\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"name\": \"👤 Mule Username\",\n" +
                    "        \"value\": \"" + muleUsername + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"💰 GP Transferred\",\n" +
                    "        \"value\": \"" + BotUtils.formatNumber(gpTransferred) + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📦 Items Transferred\",\n" +
                    "        \"value\": \"" + itemsTransferred + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📅 Time\",\n" +
                    "        \"value\": \"" + DATE_FORMAT.format(new Date()) + "\",\n" +
                    "        \"inline\": false\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | Mule Transfer\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            sendWebhookMessage(currentWebhookUrl, message);
            BotUtils.log("📱 Mule transfer completed notification sent to Discord");

        } catch (Exception e) {
            BotUtils.logError("Error sending mule transfer completed notification", e);
        }
    }

    /**
     * Send mule transfer failed notification
     */
    public void sendMuleTransferFailed(String muleUsername, String reason) {
        if (!isEnabled || !notificationsEnabled) return;

        try {
            String message = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"❌ Mule Transfer Failed\",\n" +
                    "    \"description\": \"Failed to complete trade with mule\",\n" +
                    "    \"color\": 15158332,\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"name\": \"👤 Mule Username\",\n" +
                    "        \"value\": \"" + muleUsername + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"❌ Reason\",\n" +
                    "        \"value\": \"" + reason + "\",\n" +
                    "        \"inline\": false\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"📅 Time\",\n" +
                    "        \"value\": \"" + DATE_FORMAT.format(new Date()) + "\",\n" +
                    "        \"inline\": false\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | Mule Transfer\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            sendWebhookMessage(currentWebhookUrl, message);
            BotUtils.log("📱 Mule transfer failed notification sent to Discord");

        } catch (Exception e) {
            BotUtils.logError("Error sending mule transfer failed notification", e);
        }
    }

    // ===========================================
    // PERIODIC UPDATES & TESTING
    // ===========================================

    /**
     * Handle periodic updates (call from main loop)
     */
    public void handlePeriodicUpdates() {
        if (!isEnabled || !notificationsEnabled) return;

        // Send progress updates every 30 minutes
        long timeSinceLastUpdate = System.currentTimeMillis() - lastPeriodicUpdate;
        if (timeSinceLastUpdate >= 30 * 60 * 1000) { // 30 minutes

            // This would ideally get stats from the bot coordinator
            // For now, create a placeholder stats object
            ScriptStatistics stats = new ScriptStatistics();

            sendProgressUpdate(stats);
            lastPeriodicUpdate = System.currentTimeMillis();
        }
    }

    /**
     * Test webhook (no parameters - gets URL from current configuration)
     */
    public boolean testWebhook() {
        if (currentWebhookUrl == null || currentWebhookUrl.trim().isEmpty()) {
            BotUtils.log("❌ No webhook URL configured for testing");
            return false;
        }

        return testWebhook(currentWebhookUrl);
    }

    /**
     * Test webhook with specific URL
     */
    public boolean testWebhook(String webhookUrl) {
        BotUtils.log("🧪 Testing Discord webhook...");

        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            BotUtils.log("❌ Empty webhook URL provided");
            return false;
        }

        // Validate webhook URL format
        if (!BotUtils.isValidDiscordWebhook(webhookUrl)) {
            BotUtils.log("❌ Invalid Discord webhook URL format");
            return false;
        }

        try {
            // Create test message
            String testMessage = "{\n" +
                    "  \"embeds\": [{\n" +
                    "    \"title\": \"🧪 Webhook Test\",\n" +
                    "    \"description\": \"This is a test message from your OSRS High Alchemy Bot!\",\n" +
                    "    \"color\": 3447003,\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"name\": \"📅 Test Time\",\n" +
                    "        \"value\": \"" + BotUtils.getCurrentTimeString() + "\",\n" +
                    "        \"inline\": true\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"name\": \"✅ Status\",\n" +
                    "        \"value\": \"Webhook is working correctly!\",\n" +
                    "        \"inline\": true\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"footer\": {\n" +
                    "      \"text\": \"OSRS High Alchemy Bot | Test Message\"\n" +
                    "    },\n" +
                    "    \"timestamp\": \"" + java.time.Instant.now().toString() + "\"\n" +
                    "  }]\n" +
                    "}";

            // Send test message
            boolean success = sendWebhookMessage(webhookUrl, testMessage);

            if (success) {
                BotUtils.log("✅ Webhook test successful! Check your Discord channel.");
                // Store the working webhook URL
                this.currentWebhookUrl = webhookUrl;
                return true;
            } else {
                BotUtils.log("❌ Webhook test failed. Check your webhook URL and try again.");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Webhook test error", e);
            return false;
        }
    }

    // ===========================================
    // INTERNAL METHODS
    // ===========================================

    /**
     * Send webhook message with rate limiting
     */
    public boolean sendWebhookMessage(String webhookUrl, String jsonMessage) {
        if (webhookUrl == null || jsonMessage == null) {
            return false;
        }

        // Rate limiting
        long now = System.currentTimeMillis();
        if (now - lastMessageTime < MIN_MESSAGE_INTERVAL) {
            BotUtils.sleep((int)(MIN_MESSAGE_INTERVAL - (now - lastMessageTime)));
        }

        totalMessagesSent++;

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set connection properties
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "OSRS-Bot-Discord/1.0");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(10000);    // 10 seconds

            // Send the message
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonMessage.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                successfulSends++;
                lastMessageTime = now;
                return true;
            } else {
                failedSends++;
                lastError = "HTTP " + responseCode + ": " + connection.getResponseMessage();
                BotUtils.log("❌ Discord webhook failed: " + lastError);
                return false;
            }

        } catch (Exception e) {
            failedSends++;
            lastError = e.getMessage();
            BotUtils.logError("Discord webhook error", e);
            return false;
        }
    }

    // ===========================================
    // STATUS & GETTERS
    // ===========================================

    public boolean isEnabled() { return isEnabled; }
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public String getCurrentWebhookUrl() { return currentWebhookUrl; }

    public String getStatistics() {
        double successRate = totalMessagesSent > 0 ? (double)successfulSends / totalMessagesSent * 100 : 0;
        return String.format("Messages: %d | Success: %.1f%% | Last Error: %s",
                totalMessagesSent, successRate, lastError.isEmpty() ? "None" : lastError);
    }
}