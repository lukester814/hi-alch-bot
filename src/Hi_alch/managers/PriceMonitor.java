package Hi_alch.managers;

import Hi_alch.BotUtils;

/**
 * Price Monitoring System
 *
 * Features:
 * - Monitor specific item prices
 * - Detect price changes
 * - Periodic update checking
 * - Change notifications
 *
 * REUSABLE: Perfect for any trading bot!
 */
public class PriceMonitor {

    // Monitoring state
    private boolean isMonitoring = false;
    private String monitoredItemName = "";
    private int monitoredItemId = -1;
    private long lastMonitorUpdate = 0;
    private int lastKnownPrice = 0;

    // Reference to price manager for fetching prices
    private PriceManagerInterface priceManager;

    /**
     * Interface for price fetching
     */
    public interface PriceManagerInterface {
        int getBuyPrice(int itemId);
    }

    // ===========================================
    // PUBLIC API
    // ===========================================

    /**
     * Set the price manager
     */
    public void setPriceManager(PriceManagerInterface manager) {
        this.priceManager = manager;
    }

    /**
     * Start monitoring a specific item's price
     */
    public void startMonitoring(String itemName, int itemId) {
        if (itemId > 0) {
            this.monitoredItemName = itemName;
            this.monitoredItemId = itemId;
            this.isMonitoring = true;
            this.lastMonitorUpdate = System.currentTimeMillis();

            BotUtils.log("📈 Started price monitoring for: " + itemName + " (ID: " + itemId + ")");

            // Get initial price
            if (priceManager != null) {
                this.lastKnownPrice = priceManager.getBuyPrice(itemId);
                BotUtils.log("💰 Initial price: " + BotUtils.formatNumber(lastKnownPrice) + " GP");
            }

        } else {
            BotUtils.log("❌ Invalid item ID for: " + itemName);
        }
    }

    /**
     * Stop price monitoring
     */
    public void stopMonitoring() {
        if (isMonitoring) {
            BotUtils.log("📈 Stopped price monitoring for: " + monitoredItemName);
            this.isMonitoring = false;
            this.monitoredItemName = "";
            this.monitoredItemId = -1;
        }
    }

    /**
     * Check for price changes (call periodically)
     */
    public void checkPriceUpdates() {
        if (!isMonitoring || monitoredItemId <= 0 || priceManager == null) {
            return;
        }

        // Only check every 2 minutes
        if (System.currentTimeMillis() - lastMonitorUpdate < 120000) {
            return;
        }

        try {
            int currentPrice = priceManager.getBuyPrice(monitoredItemId);

            if (currentPrice > 0 && currentPrice != lastKnownPrice) {
                double changePercent = ((double)(currentPrice - lastKnownPrice)) / lastKnownPrice * 100;

                BotUtils.log("📈 Price change detected for " + monitoredItemName + ":");
                BotUtils.log("   Old: " + BotUtils.formatNumber(lastKnownPrice) + " GP");
                BotUtils.log("   New: " + BotUtils.formatNumber(currentPrice) + " GP");
                BotUtils.log("   Change: " + String.format("%.1f%%", changePercent));

                lastKnownPrice = currentPrice;
            }

            lastMonitorUpdate = System.currentTimeMillis();

        } catch (Exception e) {
            BotUtils.logError("Error checking price updates", e);
        }
    }

    // ===========================================
    // GETTERS
    // ===========================================

    public boolean isMonitoring() {
        return isMonitoring;
    }

    public String getMonitoredItemName() {
        return monitoredItemName;
    }

    public int getMonitoredItemId() {
        return monitoredItemId;
    }

    public int getLastKnownPrice() {
        return lastKnownPrice;
    }
}
