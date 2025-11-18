package Hi_alch.util;

import Hi_alch.core.Logger;
import Hi_alch.core.ExceptionHandler;
import Hi_alch.core.Constants;
import Hi_alch.overlay.ScriptStatistics;
import Hi_alch.gui.GUIConfiguration;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Discord Webhook Utility - Professional Integration
 *
 * Provides enhanced Discord webhook functionality with:
 * - Rich embeds with colors and fields
 * - Bot status notifications
 * - Error notifications
 * - Session summaries
 * - Test webhook functionality
 */
public class DiscordWebhookUtil {

    // ===========================================
    // EMBED COLORS
    // ===========================================

    private static final int COLOR_SUCCESS = 0x4CAF50;  // Green
    private static final int COLOR_ERROR = 0xF44336;    // Red
    private static final int COLOR_WARNING = 0xFFC107;  // Yellow
    private static final int COLOR_INFO = 0x2196F3;     // Blue
    private static final int COLOR_PROFIT = 0x4CAF50;   // Green
    private static final int COLOR_LOSS = 0xF44336;     // Red

    // ===========================================
    // WEBHOOK SENDING
    // ===========================================

    /**
     * Send a simple text message to Discord
     */
    public static boolean sendMessage(String webhookUrl, String message) {
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            Logger.warn(Logger.Category.DISCORD, "Cannot send Discord message - webhook URL is empty");
            return false;
        }

        try {
            String json = buildSimpleMessageJson(message);
            return sendWebhookRequest(webhookUrl, json);

        } catch (Exception e) {
            ExceptionHandler.handleNetworkError(e, "Sending Discord message");
            return false;
        }
    }

    /**
     * Send a rich embed to Discord
     */
    public static boolean sendEmbed(String webhookUrl, String title, String description,
                                    int color, EmbedField... fields) {
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            return false;
        }

        try {
            String json = buildEmbedJson(title, description, color, fields);
            return sendWebhookRequest(webhookUrl, json);

        } catch (Exception e) {
            ExceptionHandler.handleNetworkError(e, "Sending Discord embed");
            return false;
        }
    }

    /**
     * Send raw webhook request
     */
    private static boolean sendWebhookRequest(String webhookUrl, String jsonPayload) {
        HttpsURLConnection connection = null;

        try {
            URL url = new URL(webhookUrl);
            connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "OSRS-HighAlch-Bot/2.0");
            connection.setDoOutput(true);

            // Write JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get response code
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                Logger.debug(Logger.Category.DISCORD, "Discord webhook sent successfully (HTTP " + responseCode + ")");
                return true;
            } else {
                Logger.error(Logger.Category.DISCORD, "Discord webhook failed with HTTP " + responseCode);
                return false;
            }

        } catch (Exception e) {
            ExceptionHandler.handleNetworkError(e, "Discord webhook HTTP request");
            return false;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // ===========================================
    // BOT STATUS NOTIFICATIONS
    // ===========================================

    /**
     * Send bot startup notification
     */
    public static boolean sendStartupNotification(String webhookUrl, GUIConfiguration config) {
        String title = "🚀 High Alchemy Bot Started";
        String description = "Bot has been started and is initializing...";

        EmbedField[] fields = {
            new EmbedField("Item", config.selectedItemName, true),
            new EmbedField("Buy Limit", String.valueOf(config.buyLimit), true),
            new EmbedField("Price Markup", String.format("%.1f%%", config.priceMarkup), true),
            new EmbedField("Nature Runes", String.valueOf(config.natureRuneAmount), true),
            new EmbedField("Anti-ban", config.antibanEnabled ? "Enabled" : "Disabled", true),
            new EmbedField("Smart Profit", config.smartProfitEnabled ? "Enabled" : "Disabled", true),
            new EmbedField("Started At", getCurrentTimestamp(), false)
        };

        return sendEmbed(webhookUrl, title, description, COLOR_INFO, fields);
    }

    /**
     * Send bot stopped notification
     */
    public static boolean sendStoppedNotification(String webhookUrl, String reason) {
        String title = "🛑 High Alchemy Bot Stopped";
        String description = "Bot has been stopped.";

        EmbedField[] fields = {
            new EmbedField("Reason", reason, false),
            new EmbedField("Stopped At", getCurrentTimestamp(), false)
        };

        return sendEmbed(webhookUrl, title, description, COLOR_WARNING, fields);
    }

    /**
     * Send error notification
     */
    public static boolean sendErrorNotification(String webhookUrl, String errorMessage) {
        String title = "❌ Error Occurred";
        String description = "The bot encountered an error.";

        EmbedField[] fields = {
            new EmbedField("Error", errorMessage, false),
            new EmbedField("Time", getCurrentTimestamp(), false)
        };

        return sendEmbed(webhookUrl, title, description, COLOR_ERROR, fields);
    }

    /**
     * Send progress update
     */
    public static boolean sendProgressUpdate(String webhookUrl, ScriptStatistics stats) {
        String title = "📊 Progress Update";
        String description = "Bot is running...";

        int color = stats.totalProfit >= 0 ? COLOR_PROFIT : COLOR_LOSS;

        EmbedField[] fields = {
            new EmbedField("Alchs Completed", formatNumber(stats.alchsCompleted), true),
            new EmbedField("Total Profit", formatGP(stats.totalProfit), true),
            new EmbedField("XP Gained", formatNumber(stats.xpGained) + " XP", true),
            new EmbedField("Alchs/Hour", String.format("%.0f", stats.alchsPerHour), true),
            new EmbedField("Profit/Hour", formatGP((int) stats.profitPerHour), true),
            new EmbedField("Runtime", formatDuration(stats.scriptRuntime), true),
            new EmbedField("State", stats.currentState, false)
        };

        return sendEmbed(webhookUrl, title, description, color, fields);
    }

    /**
     * Send session summary
     */
    public static boolean sendSessionSummary(String webhookUrl, ScriptStatistics stats, String itemName) {
        String title = "✅ Session Complete";
        String description = "High Alchemy session has finished!";

        int color = stats.totalProfit >= 0 ? COLOR_SUCCESS : COLOR_ERROR;

        EmbedField[] fields = {
            new EmbedField("Item", itemName, false),
            new EmbedField("Total Alchs", formatNumber(stats.alchsCompleted), true),
            new EmbedField("Total Profit", formatGP(stats.totalProfit), true),
            new EmbedField("Total XP", formatNumber(stats.xpGained) + " XP", true),
            new EmbedField("Runtime", formatDuration(stats.scriptRuntime), true),
            new EmbedField("Alchs/Hour", String.format("%.0f", stats.alchsPerHour), true),
            new EmbedField("Profit/Hour", formatGP((int) stats.profitPerHour), true),
            new EmbedField("Errors", String.valueOf(stats.errorsEncountered), true),
            new EmbedField("Finished At", getCurrentTimestamp(), false)
        };

        return sendEmbed(webhookUrl, title, description, color, fields);
    }

    /**
     * Send test notification
     */
    public static boolean sendTestNotification(String webhookUrl) {
        String title = "✅ Discord Webhook Test";
        String description = "Your Discord webhook is configured correctly!";

        EmbedField[] fields = {
            new EmbedField("Status", "Connected", true),
            new EmbedField("Bot", "OSRS High Alchemy Bot v2.0", true),
            new EmbedField("Test Time", getCurrentTimestamp(), false)
        };

        return sendEmbed(webhookUrl, title, description, COLOR_SUCCESS, fields);
    }

    /**
     * Send mule request notification
     */
    public static boolean sendMuleRequestNotification(String webhookUrl, int gpAmount, String location) {
        String title = "💰 Mule Request";
        String description = "Bot is requesting mule support.";

        EmbedField[] fields = {
            new EmbedField("GP Amount", formatGP(gpAmount), true),
            new EmbedField("Location", location, true),
            new EmbedField("Time", getCurrentTimestamp(), false)
        };

        return sendEmbed(webhookUrl, title, description, COLOR_WARNING, fields);
    }

    /**
     * Send mule complete notification
     */
    public static boolean sendMuleCompleteNotification(String webhookUrl, int gpTransferred, boolean success) {
        String title = success ? "✅ Mule Complete" : "❌ Mule Failed";
        String description = success ? "GP successfully transferred to mule." : "Failed to transfer GP to mule.";

        int color = success ? COLOR_SUCCESS : COLOR_ERROR;

        EmbedField[] fields = {
            new EmbedField("GP Transferred", formatGP(gpTransferred), true),
            new EmbedField("Status", success ? "Success" : "Failed", true),
            new EmbedField("Time", getCurrentTimestamp(), false)
        };

        return sendEmbed(webhookUrl, title, description, color, fields);
    }

    // ===========================================
    // JSON BUILDING
    // ===========================================

    /**
     * Build simple message JSON
     */
    private static String buildSimpleMessageJson(String message) {
        return String.format("{\"content\": %s}", escapeJson(message));
    }

    /**
     * Build rich embed JSON
     */
    private static String buildEmbedJson(String title, String description, int color, EmbedField[] fields) {
        StringBuilder json = new StringBuilder();
        json.append("{\"embeds\": [{");

        // Title
        if (title != null && !title.isEmpty()) {
            json.append("\"title\": ").append(escapeJson(title)).append(",");
        }

        // Description
        if (description != null && !description.isEmpty()) {
            json.append("\"description\": ").append(escapeJson(description)).append(",");
        }

        // Color
        json.append("\"color\": ").append(color).append(",");

        // Fields
        if (fields != null && fields.length > 0) {
            json.append("\"fields\": [");
            for (int i = 0; i < fields.length; i++) {
                EmbedField field = fields[i];
                json.append("{");
                json.append("\"name\": ").append(escapeJson(field.name)).append(",");
                json.append("\"value\": ").append(escapeJson(field.value)).append(",");
                json.append("\"inline\": ").append(field.inline);
                json.append("}");

                if (i < fields.length - 1) {
                    json.append(",");
                }
            }
            json.append("],");
        }

        // Timestamp
        json.append("\"timestamp\": \"").append(getISOTimestamp()).append("\"");

        json.append("}]}");
        return json.toString();
    }

    /**
     * Escape JSON string
     */
    private static String escapeJson(String str) {
        if (str == null) {
            return "null";
        }

        String escaped = str
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");

        return "\"" + escaped + "\"";
    }

    // ===========================================
    // FORMATTING UTILITIES
    // ===========================================

    /**
     * Format number with commas
     */
    private static String formatNumber(int number) {
        return String.format("%,d", number);
    }

    /**
     * Format GP amount
     */
    private static String formatGP(int amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fB GP", amount / 1_000_000_000.0);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fM GP", amount / 1_000_000.0);
        } else if (amount >= 1_000) {
            return String.format("%.1fK GP", amount / 1_000.0);
        } else {
            return formatNumber(amount) + " GP";
        }
    }

    /**
     * Format duration in milliseconds
     */
    private static String formatDuration(long milliseconds) {
        long hours = milliseconds / 3600000;
        long minutes = (milliseconds % 3600000) / 60000;
        long seconds = (milliseconds % 60000) / 1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Get current timestamp as string
     */
    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * Get ISO 8601 timestamp for Discord
     */
    private static String getISOTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return sdf.format(new Date());
    }

    // ===========================================
    // EMBED FIELD CLASS
    // ===========================================

    /**
     * Represents a field in a Discord embed
     */
    public static class EmbedField {
        public final String name;
        public final String value;
        public final boolean inline;

        public EmbedField(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public EmbedField(String name, String value) {
            this(name, value, false);
        }
    }

    // ===========================================
    // VALIDATION
    // ===========================================

    /**
     * Validate webhook URL format
     */
    public static boolean isValidWebhookUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        return url.matches("https://discord(?:app)?\\.com/api/webhooks/\\d+/[A-Za-z0-9_-]+");
    }

    /**
     * Test webhook connectivity
     */
    public static boolean testWebhook(String webhookUrl) {
        Logger.info(Logger.Category.DISCORD, "Testing Discord webhook...");

        boolean success = sendTestNotification(webhookUrl);

        if (success) {
            Logger.info(Logger.Category.DISCORD, "✅ Discord webhook test successful!");
        } else {
            Logger.error(Logger.Category.DISCORD, "❌ Discord webhook test failed!");
        }

        return success;
    }

    /**
     * Prevent instantiation
     */
    private DiscordWebhookUtil() {
        throw new AssertionError("Cannot instantiate DiscordWebhookUtil class");
    }
}
