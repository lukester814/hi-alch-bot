package Hi_alch.core;

import Hi_alch.utils.BotUtils;
import Hi_alch.engine.AlchingEngine;
import Hi_alch.managers.AntibanSystem;
import Hi_alch.managers.DiscordManager;
import Hi_alch.managers.PriceManager;
import Hi_alch.overlay.OverlayRenderer;
import Hi_alch.gui.GUIConfiguration;

/**
 * Handles bot configuration and validation
 * Separates configuration logic from main bot class
 */
public class BotConfigurationManager {

    /**
     * Validate bot configuration before starting
     */
    public static boolean validateConfiguration(GUIConfiguration config) {
        try {
            // Validate item selection
            if (config.selectedItemName == null || config.selectedItemName.trim().isEmpty()) {
                BotUtils.log("❌ No item selected");
                return false;
            }

            // Check for placeholder text
            String itemNameLower = config.selectedItemName.toLowerCase().trim();
            if (itemNameLower.contains("select") || itemNameLower.contains("choose") ||
                itemNameLower.equals("---") || itemNameLower.isEmpty()) {
                BotUtils.log("❌ Please select a valid item to alch");
                return false;
            }

            // Validate buy limit
            if (config.buyLimit <= 0) {
                BotUtils.log("❌ Invalid buy limit: " + config.buyLimit);
                return false;
            }

            // Validate nature rune amount
            if (config.natureRuneAmount <= 0) {
                BotUtils.log("❌ Invalid nature rune amount: " + config.natureRuneAmount);
                return false;
            }

            // Validate Discord webhook if enabled
            if (config.discordNotificationsEnabled) {
                if (config.discordWebhookUrl == null || config.discordWebhookUrl.trim().isEmpty()) {
                    BotUtils.log("❌ Discord notifications enabled but no webhook URL");
                    return false;
                }

                if (!BotUtils.isValidDiscordWebhook(config.discordWebhookUrl)) {
                    BotUtils.log("❌ Invalid Discord webhook URL format");
                    return false;
                }
            }

            BotUtils.log("✅ Configuration validation successful");
            return true;

        } catch (Exception e) {
            BotUtils.logError("Error validating configuration", e);
            return false;
        }
    }

    /**
     * Configure all components with the provided configuration
     */
    public static void configureComponents(GUIConfiguration config,
                                          AlchingEngine alchingEngine,
                                          AntibanSystem antibanSystem,
                                          DiscordManager discordManager,
                                          PriceManager priceManager,
                                          OverlayRenderer overlayRenderer) {
        try {
            // Configure alchemy engine
            if (alchingEngine != null) {
                alchingEngine.configure(
                        config.selectedItemName,
                        config.selectedItemId,
                        config.buyLimit,
                        config.priceMarkup,
                        config.natureRuneAmount,
                        config.smartProfitEnabled,
                        config.worldHopEnabled,
                        config.skipBuying,
                        config.restockWhenEmpty
                );
            }

            // Configure anti-ban system
            if (antibanSystem != null) {
                antibanSystem.configure(
                        config.antibanEnabled,
                        config.userProfileSeed,
                        config.antibanAggression
                );
            }

            // Configure Discord manager
            if (discordManager != null && config.discordNotificationsEnabled) {
                discordManager.configure(config.discordWebhookUrl);
            }

            // Configure price manager
            if (priceManager != null) {
                priceManager.startMonitoring(config.selectedItemName);
            }

            // Configure overlay renderer
            if (overlayRenderer != null) {
                overlayRenderer.configure(config.selectedItemName, config.selectedItemId, true);
            }

            BotUtils.log("✅ All components configured successfully");

        } catch (Exception e) {
            BotUtils.logError("Error configuring components", e);
        }
    }
}
