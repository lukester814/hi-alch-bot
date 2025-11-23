package Hi_alch.managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
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
 * REUSABLE: Perfect for any OSRS bot that needs market data!
 */
public class PriceManager {
    // ===========================================
    // API CONFIGURATION
    // Primary API endpoints (multiple for reliability)
    private static final String[] API_ENDPOINTS = {
            "https://secure.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=",
            "https://oldschool.runescape.wiki/api.php?action=query&format=json&prop=extracts&titles=",
            "https://api.osrsbox.com/items/"
    };
    // Price cache settings
    private static final long CACHE_TTL = 5 * 60 * 1000; // 5 minutes
    private static final int MAX_CACHE_SIZE = 1000;
    // Rate limiting
    private static final long MIN_REQUEST_INTERVAL = 1000; // 1 second between requests
    private long lastRequestTime = 0;
    // CACHING & STATE
    // Price cache with TTL
    private final Map<Integer, CachedPrice> priceCache = new ConcurrentHashMap<>();
    // Monitoring state
    private boolean isMonitoring = false;
    private String monitoredItemName = "";
    private int monitoredItemId = -1;
    private long lastMonitorUpdate = 0;
    private int lastKnownPrice = 0;
    // Statistics
    private int totalRequests = 0;
    private int successfulRequests = 0;
    private int cacheHits = 0;
    private String lastError = "";
    /**
     * Cached price data structure
     */
    private static class CachedPrice {
        final int buyPrice;
        final int sellPrice;
        final long timestamp;
        final boolean isValid;
        CachedPrice(int buyPrice, int sellPrice, boolean isValid) {
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.timestamp = System.currentTimeMillis();
            this.isValid = isValid;
        }
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL;
    }
    // CONSTRUCTOR
    public PriceManager() {
        BotUtils.log("💰 PriceManager initialized with " + API_ENDPOINTS.length + " API endpoints");
        BotUtils.log("🕐 Cache TTL: " + (CACHE_TTL / 1000) + " seconds");
    // PUBLIC API - PRICE RETRIEVAL
     * Get current buy price for an item
    public int getBuyPrice(int itemId) {
        CachedPrice cached = getCachedPrice(itemId);
        if (cached != null && cached.isValid) {
            return cached.buyPrice;
        return fetchLivePrice(itemId, true);
     * Get current sell price for an item
    public int getSellPrice(int itemId) {
            return cached.sellPrice;
        return fetchLivePrice(itemId, false);
     * Get both buy and sell prices
    public int[] getBothPrices(int itemId) {
            return new int[]{cached.buyPrice, cached.sellPrice};
        // Fetch fresh data
        fetchPriceData(itemId);
        cached = getCachedPrice(itemId);
        return new int[]{0, 0}; // Failed to get prices
     * Calculate potential profit for high alchemy
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
    // MONITORING SYSTEM
     * Start monitoring a specific item's price
    public void startMonitoring(String itemName) {
        // Convert item name to ID (would need ItemDatabase integration)
        int itemId = getItemIdByName(itemName);
        if (itemId > 0) {
            this.monitoredItemName = itemName;
            this.monitoredItemId = itemId;
            this.isMonitoring = true;
            this.lastMonitorUpdate = System.currentTimeMillis();
            BotUtils.log("📈 Started price monitoring for: " + itemName + " (ID: " + itemId + ")");
            // Get initial price
            this.lastKnownPrice = getBuyPrice(itemId);
            BotUtils.log("💰 Initial price: " + BotUtils.formatNumber(lastKnownPrice) + " GP");
        } else {
            BotUtils.log("❌ Could not find item ID for: " + itemName);
     * Stop price monitoring
    public void stopMonitoring() {
        if (isMonitoring) {
            BotUtils.log("📈 Stopped price monitoring for: " + monitoredItemName);
            this.isMonitoring = false;
            this.monitoredItemName = "";
            this.monitoredItemId = -1;
     * Check for price changes (call periodically)
    public void checkPriceUpdates() {
        if (!isMonitoring || monitoredItemId <= 0) {
            return;
        // Only check every 2 minutes
        if (System.currentTimeMillis() - lastMonitorUpdate < 120000) {
            int currentPrice = getBuyPrice(monitoredItemId);
            if (currentPrice > 0 && currentPrice != lastKnownPrice) {
                double changePercent = ((double)(currentPrice - lastKnownPrice)) / lastKnownPrice * 100;
                BotUtils.log("📈 Price change detected for " + monitoredItemName + ":");
                BotUtils.log("   Old: " + BotUtils.formatNumber(lastKnownPrice) + " GP");
                BotUtils.log("   New: " + BotUtils.formatNumber(currentPrice) + " GP");
                BotUtils.log("   Change: " + String.format("%.1f%%", changePercent));
                lastKnownPrice = currentPrice;
            lastMonitorUpdate = System.currentTimeMillis();
            BotUtils.logError("Error checking price updates", e);
    // PRELOADING & OPTIMIZATION
     * Preload prices for commonly traded items
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
                // Add more common items here
        };
        int loaded = 0;
        for (int itemId : commonItems) {
            try {
                if (fetchPriceData(itemId)) {
                    loaded++;
                    BotUtils.sleep(1100); // Rate limiting
                }
            } catch (Exception e) {
                BotUtils.log("⚠️ Failed to preload price for item " + itemId);
        BotUtils.log("✅ Preloaded " + loaded + "/" + commonItems.length + " item prices");
    // INTERNAL METHODS
     * Get cached price if available and not expired
    private CachedPrice getCachedPrice(int itemId) {
        CachedPrice cached = priceCache.get(itemId);
        if (cached != null && !cached.isExpired()) {
            cacheHits++;
            return cached;
        return null;
     * Fetch live price from API
    private int fetchLivePrice(int itemId, boolean buyPrice) {
        if (cached != null) {
            return buyPrice ? cached.buyPrice : cached.sellPrice;
        if (fetchPriceData(itemId)) {
            cached = getCachedPrice(itemId);
            if (cached != null) {
                return buyPrice ? cached.buyPrice : cached.sellPrice;
        return 0; // Failed to get price
     * Fetch price data from API and cache it
    private boolean fetchPriceData(int itemId) {
        // Rate limiting
        long now = System.currentTimeMillis();
        if (now - lastRequestTime < MIN_REQUEST_INTERVAL) {
            BotUtils.sleep((int)(MIN_REQUEST_INTERVAL - (now - lastRequestTime)));
        totalRequests++;
        // Try each API endpoint
        for (String endpoint : API_ENDPOINTS) {
                String response = makeApiRequest(endpoint + itemId);
                if (response != null && !response.isEmpty()) {
                    int[] prices = parseApiResponse(response, endpoint);
                    if (prices != null && prices.length == 2) {
                        // Cache the result
                        CachedPrice cached = new CachedPrice(prices[0], prices[1], true);
                        priceCache.put(itemId, cached);
                        // Clean cache if getting too large
                        if (priceCache.size() > MAX_CACHE_SIZE) {
                            cleanCache();
                        }
                        successfulRequests++;
                        lastRequestTime = System.currentTimeMillis();
                        BotUtils.log("💰 Price data fetched for item " + itemId +
                                ": Buy=" + BotUtils.formatNumber(prices[0]) +
                                " Sell=" + BotUtils.formatNumber(prices[1]));
                        return true;
                    }
                BotUtils.log("⚠️ API endpoint failed: " + endpoint + " - " + e.getMessage());
                lastError = e.getMessage();
        // All APIs failed - cache negative result to avoid spam
        CachedPrice cached = new CachedPrice(0, 0, false);
        priceCache.put(itemId, cached);
        BotUtils.log("❌ Failed to fetch price data for item " + itemId + " from all APIs");
        return false;
     * Make HTTP request to API
    private String makeApiRequest(String urlString) {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set timeouts and headers
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(15000);    // 15 seconds
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "OSRS-Bot-PriceChecker/1.0");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                reader.close();
                return response.toString();
            } else {
                BotUtils.log("⚠️ API returned status code: " + responseCode);
                return null;
        } catch (SocketTimeoutException e) {
            BotUtils.log("⏰ API request timed out: " + urlString);
            return null;
        } catch (ConnectException e) {
            BotUtils.log("🔌 Connection failed: " + urlString);
            BotUtils.logError("API request error", e);
     * Parse API response to extract prices
    private int[] parseApiResponse(String response, String endpoint) {
            // Simple JSON parsing (avoiding external dependencies)
            if (response.contains("\"current\"") && response.contains("\"price\"")) {
                // Extract buy/sell prices from official OSRS API format
                String[] parts = response.split("\"current\":");
                if (parts.length > 1) {
                    String currentSection = parts[1].split("}")[0];
                    int buyPrice = extractPrice(currentSection, "price");
                    // For sell price, use trending data or estimate
                    int sellPrice = (int)(buyPrice * 0.95); // Rough estimate
                    return new int[]{buyPrice, sellPrice};
            // Fallback: try to extract any price information
            if (response.contains("\"price\":")) {
                int price = extractPrice(response, "price");
                if (price > 0) {
                    return new int[]{price, (int)(price * 0.95)};
            BotUtils.logError("Error parsing API response", e);
     * Extract price value from JSON string
    private int extractPrice(String json, String key) {
            String searchFor = "\"" + key + "\":";
            int startIndex = json.indexOf(searchFor);
            if (startIndex == -1) return 0;
            startIndex += searchFor.length();
            int endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf("}", startIndex);
            if (endIndex == -1) return 0;
            String priceStr = json.substring(startIndex, endIndex).trim();
            // Remove quotes and commas
            priceStr = priceStr.replace("\"", "").replace(",", "");
            return Integer.parseInt(priceStr);
     * Clean expired entries from cache
    private void cleanCache() {
        priceCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        BotUtils.log("🧹 Cleaned price cache: " + priceCache.size() + " entries remaining");
     * Convert item name to ID (would integrate with ItemDatabase)
    private int getItemIdByName(String itemName) {
        // This is a placeholder - in a real implementation, this would use ItemDatabase
        // For now, return some common item IDs for testing
        switch (itemName.toLowerCase()) {
            case "dragon longsword": return 1305;
            case "dragon dagger": return 1215;
            case "rune platebody": return 1127;
            case "dragon scimitar": return 4587;
            default: return -1; // Unknown item
    // GETTERS & STATUS
    public boolean isMonitoring() { return isMonitoring; }
    public String getMonitoredItemName() { return monitoredItemName; }
    public int getMonitoredItemId() { return monitoredItemId; }
    public int getCacheSize() { return priceCache.size(); }
    public String getStatistics() {
        double successRate = totalRequests > 0 ? (double)successfulRequests / totalRequests * 100 : 0;
        return String.format("Requests: %d | Success: %.1f%% | Cache: %d hits | Errors: %s",
                totalRequests, successRate, cacheHits, lastError.isEmpty() ? "None" : lastError);
}
