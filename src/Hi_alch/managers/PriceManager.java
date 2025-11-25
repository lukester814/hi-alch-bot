package Hi_alch.managers;

import Hi_alch.utils.BotUtils;
import Hi_alch.managers.PriceCache.CachedPrice;

/**
 * Professional Price Manager for OSRS Market Data
 *
 * Features:
 * - Live OSRS Grand Exchange API integration
 * - Intelligent caching with TTL
 * - Multiple API fallbacks for reliability
 * - Rate limiting and error handling
 * - Price monitoring and alerts
 * - Profit calculation utilities
 *
 * REUSABLE: Perfect for any OSRS bot that needs market data!
 *
 * REFACTORED: Now uses PriceFetcher, PriceCache, and PriceMonitor
 */
public class PriceManager implements PriceMonitor.PriceManagerInterface {

    // ===========================================
    // COMPONENTS
    // ===========================================

    private PriceFetcher fetcher;
    private PriceCache cache;
    private PriceMonitor monitor;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public PriceManager() {
        this.fetcher = new PriceFetcher();
        this.cache = new PriceCache();
        this.monitor = new PriceMonitor();
        this.monitor.setPriceManager(this);

        BotUtils.log("💰 PriceManager initialized with caching and monitoring");
    }

    // ===========================================
    // PUBLIC API - PRICE RETRIEVAL
    // ===========================================

    /**
     * Get current buy price for an item
     */
    @Override
    public int getBuyPrice(int itemId) {
        CachedPrice cached = cache.get(itemId);
        if (cached != null && cached.isValid) {
            return cached.buyPrice;
        }

        return fetchLivePrice(itemId, true);
    }

    /**
     * Get current sell price for an item
     */
    public int getSellPrice(int itemId) {
        CachedPrice cached = cache.get(itemId);
        if (cached != null && cached.isValid) {
            return cached.sellPrice;
        }

        return fetchLivePrice(itemId, false);
    }

    /**
     * Get both buy and sell prices
     */
    public int[] getBothPrices(int itemId) {
        CachedPrice cached = cache.get(itemId);
        if (cached != null && cached.isValid) {
            return new int[]{cached.buyPrice, cached.sellPrice};
        }

        // Fetch fresh data
        int[] prices = fetcher.fetchPrices(itemId);
        if (prices != null && prices[0] > 0) {
            cache.put(itemId, prices[0], prices[1], true);
            return prices;
        }

        return new int[]{0, 0}; // Failed to get prices
    }

    /**
     * Calculate potential profit for high alchemy
     */
    public int calculateAlchProfit(int itemId, int quantity) {
        try {
            int buyPrice = getBuyPrice(itemId);
            if (buyPrice <= 0) {
                BotUtils.log("⚠️ Could not get buy price for item " + itemId);
                return 0;
            }

            // High alchemy gives 60% of item's base shop value
            // This is an approximation - actual values would need item database lookup
            int alchValue = (int)(buyPrice * 0.6); // Rough estimate

            // Nature rune cost (approximately 200-300 GP each)
            int natureRuneCost = 250;

            int profitPerItem = alchValue - buyPrice - natureRuneCost;
            int totalProfit = profitPerItem * quantity;

            BotUtils.log("💰 Alch Profit Calculation:");
            BotUtils.log("   Buy Price: " + BotUtils.formatNumber(buyPrice) + " GP");
            BotUtils.log("   Alch Value: " + BotUtils.formatNumber(alchValue) + " GP (est.)");
            BotUtils.log("   Nature Rune: " + BotUtils.formatNumber(natureRuneCost) + " GP");
            BotUtils.log("   Profit/Item: " + BotUtils.formatNumber(profitPerItem) + " GP");
            BotUtils.log("   Total Profit: " + BotUtils.formatNumber(totalProfit) + " GP");

            return totalProfit;

        } catch (Exception e) {
            BotUtils.logError("Error calculating alch profit", e);
            return 0;
        }
    }

    // ===========================================
    // MONITORING SYSTEM
    // ===========================================

    /**
     * Start monitoring a specific item's price
     */
    public void startMonitoring(String itemName) {
        // Convert item name to ID (would need ItemDatabase integration)
        int itemId = getItemIdByName(itemName);
        monitor.startMonitoring(itemName, itemId);
    }

    /**
     * Stop price monitoring
     */
    public void stopMonitoring() {
        monitor.stopMonitoring();
    }

    /**
     * Check for price changes (call periodically)
     */
    public void checkPriceUpdates() {
        monitor.checkPriceUpdates();
    }

    // ===========================================
    // PRELOADING & OPTIMIZATION
    // ===========================================

    /**
     * Preload prices for commonly traded items
     */
    public void preloadCommonPrices() {
        BotUtils.log("🔄 Preloading common item prices...");

        // Common high alchemy items
        int[] commonItems = {
                1249, // Dragon longsword
                1215, // Dragon dagger
                1305, // Dragon long
                4587, // Dragon scimitar
                892,  // Rune platebody
                1127, // Rune platelegs
                1163, // Rune full helm
        };

        int loaded = 0;
        for (int itemId : commonItems) {
            try {
                int[] prices = fetcher.fetchPrices(itemId);
                if (prices != null && prices[0] > 0) {
                    cache.put(itemId, prices[0], prices[1], true);
                    loaded++;
                    BotUtils.sleep(1100); // Rate limiting
                }
            } catch (Exception e) {
                BotUtils.log("⚠️ Failed to preload price for item " + itemId);
            }
        }

        BotUtils.log("✅ Preloaded " + loaded + "/" + commonItems.length + " item prices");
    }

    // ===========================================
    // INTERNAL METHODS
    // ===========================================

    /**
     * Fetch live price from API
     */
    private int fetchLivePrice(int itemId, boolean buyPrice) {
        int[] prices = fetcher.fetchPrices(itemId);

        if (prices != null && prices[0] > 0) {
            cache.put(itemId, prices[0], prices[1], true);
            return buyPrice ? prices[0] : prices[1];
        } else {
            // Cache negative result to avoid spam
            cache.put(itemId, 0, 0, false);
            return 0;
        }
    }

    /**
     * Convert item name to ID (would integrate with ItemDatabase)
     */
    private int getItemIdByName(String itemName) {
        // This is a placeholder - in a real implementation, this would use ItemDatabase
        // For now, return some common item IDs for testing
        switch (itemName.toLowerCase()) {
            case "dragon longsword": return 1305;
            case "dragon dagger": return 1215;
            case "rune platebody": return 1127;
            case "dragon scimitar": return 4587;
            default: return -1; // Unknown item
        }
    }

    // ===========================================
    // GETTERS & STATUS
    // ===========================================

    public boolean isMonitoring() {
        return monitor.isMonitoring();
    }

    public String getMonitoredItemName() {
        return monitor.getMonitoredItemName();
    }

    public int getMonitoredItemId() {
        return monitor.getMonitoredItemId();
    }

    public int getCacheSize() {
        return cache.getSize();
    }

    public String getStatistics() {
        return String.format("Requests: %d | Success: %.1f%% | Cache: %d hits / %d size",
                fetcher.getTotalRequests(),
                fetcher.getSuccessRate(),
                cache.getCacheHits(),
                cache.getSize());
    }

    /**
     * Clear all cached price data
     */
    public void clearCache() {
        cache.clear();
    }
}
