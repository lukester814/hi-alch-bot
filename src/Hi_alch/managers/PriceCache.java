package Hi_alch.managers;

import Hi_alch.utils.BotUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Price Cache Manager
 *
 * Features:
 * - TTL-based caching
 * - Automatic expiration
 * - Cache statistics
 * - Size management
 *
 * REUSABLE: Perfect for any application that needs caching!
 */
public class PriceCache {

    // Cache settings
    private static final long CACHE_TTL = 5 * 60 * 1000; // 5 minutes
    private static final int MAX_CACHE_SIZE = 1000;

    // Price cache with TTL
    private final Map<Integer, CachedPrice> priceCache = new ConcurrentHashMap<>();

    // Statistics
    private int cacheHits = 0;

    /**
     * Cached price data structure
     */
    public static class CachedPrice {
        public final int buyPrice;
        public final int sellPrice;
        public final long timestamp;
        public final boolean isValid;

        public CachedPrice(int buyPrice, int sellPrice, boolean isValid) {
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.timestamp = System.currentTimeMillis();
            this.isValid = isValid;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL;
        }
    }

    // ===========================================
    // PUBLIC API
    // ===========================================

    /**
     * Get cached price if available and not expired
     */
    public CachedPrice get(int itemId) {
        CachedPrice cached = priceCache.get(itemId);
        if (cached != null && !cached.isExpired()) {
            cacheHits++;
            return cached;
        }
        return null;
    }

    /**
     * Put price into cache
     */
    public void put(int itemId, int buyPrice, int sellPrice, boolean isValid) {
        CachedPrice cached = new CachedPrice(buyPrice, sellPrice, isValid);
        priceCache.put(itemId, cached);

        // Clean cache if getting too large
        if (priceCache.size() > MAX_CACHE_SIZE) {
            clean();
        }
    }

    /**
     * Clean expired entries from cache
     */
    public void clean() {
        priceCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        BotUtils.log("🧹 Cleaned price cache: " + priceCache.size() + " entries remaining");
    }

    /**
     * Clear all cached data
     */
    public void clear() {
        priceCache.clear();
        BotUtils.log("🗑️ Price cache cleared");
    }

    // ===========================================
    // STATISTICS
    // ===========================================

    public int getSize() {
        return priceCache.size();
    }

    public int getCacheHits() {
        return cacheHits;
    }

    public void resetStatistics() {
        cacheHits = 0;
    }
}
