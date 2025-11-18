package Hi_alch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Live OSRS Item Search using Wiki API
 * Searches for items by name and returns ID and price data
 */
public class ItemSearchAPI {

    // Cache for mapping data
    private static String cachedMappingData = null;
    private static long lastMappingFetch = 0;
    private static final long MAPPING_CACHE_DURATION = 3600000; // 1 hour

    /**
     * Search for an item by name and return its data
     */
    public static ItemSearchResult searchItem(String itemName) {
        try {
            BotUtils.log("🔍 Searching for item: " + itemName);

            // First, get the item mapping (ID to name)
            String mappingData = getItemMapping();
            if (mappingData == null) {
                BotUtils.log("❌ Could not fetch item mapping");
                return null;
            }

            // Clean the search term
            String searchTerm = itemName.trim().toLowerCase();

            // Search through the mapping for the best match
            ItemSearchResult bestMatch = null;
            int bestScore = 0;

            // The API returns a JSON array: [{"id":2,"name":"Cannonball",...},...]
            // Remove the outer brackets
            mappingData = mappingData.trim();
            if (mappingData.startsWith("[")) {
                mappingData = mappingData.substring(1);
            }
            if (mappingData.endsWith("]")) {
                mappingData = mappingData.substring(0, mappingData.length() - 1);
            }

            // Split by "},{"  to get individual items
            String[] items = mappingData.split("\\},\\{");

            BotUtils.log("📊 Searching through " + items.length + " items...");

            for (String itemData : items) {
                try {
                    // Clean up the item data
                    if (!itemData.startsWith("{")) itemData = "{" + itemData;
                    if (!itemData.endsWith("}")) itemData = itemData + "}";

                    // Extract ID - look for "id":number
                    int idIdx = itemData.indexOf("\"id\":");
                    if (idIdx == -1) continue;
                    idIdx += 5;
                    int idEnd = itemData.indexOf(",", idIdx);
                    if (idEnd == -1) idEnd = itemData.indexOf("}", idIdx);

                    int itemId = Integer.parseInt(itemData.substring(idIdx, idEnd).trim());

                    // Extract name - look for "name":"..."
                    int nameIdx = itemData.indexOf("\"name\":\"");
                    if (nameIdx == -1) continue;
                    nameIdx += 8;
                    int nameEnd = itemData.indexOf("\"", nameIdx);
                    if (nameEnd == -1) continue;

                    String foundName = itemData.substring(nameIdx, nameEnd);
                    String foundNameLower = foundName.toLowerCase();

                    // Also check for highalch value if present
                    int alchValue = 0;
                    int alchIdx = itemData.indexOf("\"highalch\":");
                    if (alchIdx != -1) {
                        alchIdx += 11;
                        int alchEnd = itemData.indexOf(",", alchIdx);
                        if (alchEnd == -1) alchEnd = itemData.indexOf("}", alchIdx);
                        try {
                            alchValue = Integer.parseInt(itemData.substring(alchIdx, alchEnd).trim());
                        } catch (Exception e) {
                            // Ignore parse errors
                        }
                    }

                    // Also check for buy limit if present
                    int buyLimit = 0;
                    int limitIdx = itemData.indexOf("\"limit\":");
                    if (limitIdx != -1) {
                        limitIdx += 8;
                        int limitEnd = itemData.indexOf(",", limitIdx);
                        if (limitEnd == -1) limitEnd = itemData.indexOf("}", limitIdx);
                        try {
                            buyLimit = Integer.parseInt(itemData.substring(limitIdx, limitEnd).trim());
                        } catch (Exception e) {
                            // Ignore parse errors
                        }
                    }

                    // Calculate match score
                    int score = calculateMatchScore(searchTerm, foundNameLower);

                    // If exact match, return immediately
                    if (foundNameLower.equals(searchTerm)) {
                        BotUtils.log("✅ Exact match found: " + foundName + " (ID: " + itemId + ")");
                        ItemSearchResult result = new ItemSearchResult();
                        result.itemId = itemId;
                        result.itemName = foundName;
                        result.matchScore = 100;
                        result.alchValue = alchValue;
                        result.buyLimit = buyLimit;

                        // Get price data
                        fetchPriceData(result);
                        return result;
                    }

                    // Track best partial match
                    if (score > bestScore) {
                        bestScore = score;
                        bestMatch = new ItemSearchResult();
                        bestMatch.itemId = itemId;
                        bestMatch.itemName = foundName;
                        bestMatch.matchScore = score;
                        bestMatch.alchValue = alchValue;
                        bestMatch.buyLimit = buyLimit;
                    }

                } catch (Exception e) {
                    // Skip malformed entries
                    continue;
                }
            }

            // Return best match if score is good enough
            if (bestMatch != null && bestScore >= 70) {
                BotUtils.log("✅ Best match: " + bestMatch.itemName +
                        " (ID: " + bestMatch.itemId + ", Score: " + bestScore + "%)");

                // Get price data
                fetchPriceData(bestMatch);
                return bestMatch;
            }

            BotUtils.log("❌ No good match found for: " + itemName);
            return null;

        } catch (Exception e) {
            BotUtils.logError("Error searching for item", e);
            return null;
        }
    }

    /**
     * Get item mapping from API
     */
    private static String getItemMapping() {
        try {
            // Check cache
            if (cachedMappingData != null &&
                    System.currentTimeMillis() - lastMappingFetch < MAPPING_CACHE_DURATION) {
                BotUtils.log("📦 Using cached item mapping data");
                return cachedMappingData;
            }

            // Fetch from API
            String apiUrl = "https://prices.runescape.wiki/api/v1/osrs/mapping";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "High-Alch-Bot/1.0 (Contact: your-email@example.com)");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            BotUtils.log("📡 API Response Code: " + responseCode);

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Cache the data
                cachedMappingData = response.toString();
                lastMappingFetch = System.currentTimeMillis();

                BotUtils.log("✅ Item mapping fetched successfully (" + cachedMappingData.length() + " chars)");

                // Log a sample to verify format
                if (cachedMappingData.length() > 200) {
                    BotUtils.log("📄 Sample data: " + cachedMappingData.substring(0, 200) + "...");
                }

                return cachedMappingData;
            } else {
                BotUtils.log("❌ API returned error code: " + responseCode);
            }

        } catch (Exception e) {
            BotUtils.logError("Error fetching item mapping", e);
        }

        return null;
    }

    /**
     * Calculate match score between search term and item name
     */
    private static int calculateMatchScore(String searchTerm, String itemName) {
        // Exact match
        if (itemName.equals(searchTerm)) return 100;

        // Contains exact search term
        if (itemName.contains(searchTerm)) return 90;

        // Search term contains item name
        if (searchTerm.contains(itemName)) return 85;

        // Calculate word-based matching
        String[] searchWords = searchTerm.split("\\s+");
        String[] itemWords = itemName.split("\\s+");

        int matchedWords = 0;
        for (String searchWord : searchWords) {
            for (String itemWord : itemWords) {
                if (itemWord.contains(searchWord) || searchWord.contains(itemWord)) {
                    matchedWords++;
                    break;
                }
            }
        }

        if (matchedWords > 0) {
            return Math.min(80, (matchedWords * 100) / searchWords.length);
        }

        // Levenshtein distance for fuzzy matching
        int distance = levenshteinDistance(searchTerm, itemName);
        int maxLen = Math.max(searchTerm.length(), itemName.length());

        if (maxLen > 0) {
            int score = (int)(((maxLen - distance) / (double)maxLen) * 60);
            return Math.max(0, score);
        }

        return 0;
    }

    /**
     * Calculate Levenshtein distance for fuzzy matching
     */
    private static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                            Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Fetch price data for an item
     */
    private static void fetchPriceData(ItemSearchResult result) {
        try {
            String apiUrl = "https://prices.runescape.wiki/api/v1/osrs/latest";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "High-Alch-Bot/1.0 (Contact: your-email@example.com)");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                String response = reader.readLine();
                reader.close();

                // The response format is: {"data":{"2":{"high":180,"low":170},...}}
                // First find "data":{
                int dataIdx = response.indexOf("\"data\":{");
                if (dataIdx == -1) {
                    BotUtils.log("❌ No data section in price response");
                    return;
                }

                // Look for the item's price data
                String itemKey = "\"" + result.itemId + "\":{";
                int idx = response.indexOf(itemKey, dataIdx);

                if (idx != -1) {
                    // Extract high price
                    int highIdx = response.indexOf("\"high\":", idx) + 7;
                    int highEnd = response.indexOf(",", highIdx);
                    if (highEnd == -1) highEnd = response.indexOf("}", highIdx);

                    // Extract low price
                    int lowIdx = response.indexOf("\"low\":", idx) + 6;
                    int lowEnd = response.indexOf(",", lowIdx);
                    if (lowEnd == -1) lowEnd = response.indexOf("}", lowIdx);

                    try {
                        result.highPrice = Integer.parseInt(
                                response.substring(highIdx, highEnd).trim()
                        );
                        result.lowPrice = Integer.parseInt(
                                response.substring(lowIdx, lowEnd).trim()
                        );
                        result.averagePrice = (result.highPrice + result.lowPrice) / 2;

                        BotUtils.log("💰 Price data: High=" + result.highPrice +
                                ", Low=" + result.lowPrice +
                                ", Avg=" + result.averagePrice);
                    } catch (Exception e) {
                        BotUtils.log("❌ Failed to parse price data: " + e.getMessage());
                    }
                } else {
                    BotUtils.log("⚠️ No price data found for item ID: " + result.itemId);
                }
            }

        } catch (Exception e) {
            BotUtils.logError("Error fetching price data", e);
        }
    }

    /**
     * Result class for item search
     */
    public static class ItemSearchResult {
        public int itemId = -1;
        public String itemName = "";
        public int matchScore = 0;
        public int highPrice = 0;
        public int lowPrice = 0;
        public int averagePrice = 0;
        public int alchValue = 0;
        public int buyLimit = 0;

        public boolean isValid() {
            return itemId > 0 && !itemName.isEmpty();
        }

        @Override
        public String toString() {
            return String.format("%s (ID: %d, Price: %d GP, Alch: %d GP, Limit: %d, Score: %d%%)",
                    itemName, itemId, averagePrice, alchValue, buyLimit, matchScore);
        }
    }

    /**
     * Get price data for a specific item ID
     */
    public static ItemSearchResult getItemPrice(int itemId) {
        try {
            BotUtils.log("💰 Fetching price for item ID: " + itemId);

            String apiUrl = "https://prices.runescape.wiki/api/v1/osrs/latest";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "High-Alch-Bot/1.0 (Contact: your-email@example.com)");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                String response = reader.readLine();
                reader.close();

                // The response format is: {"data":{"2":{"high":180,"low":170},...}}
                int dataIdx = response.indexOf("\"data\":{");
                if (dataIdx == -1) return null;

                // Look for the item's price data
                String itemKey = "\"" + itemId + "\":{";
                int idx = response.indexOf(itemKey, dataIdx);

                if (idx != -1) {
                    ItemSearchResult result = new ItemSearchResult();
                    result.itemId = itemId;

                    // Extract high price
                    int highIdx = response.indexOf("\"high\":", idx) + 7;
                    int highEnd = response.indexOf(",", highIdx);
                    if (highEnd == -1) highEnd = response.indexOf("}", highIdx);

                    // Extract low price
                    int lowIdx = response.indexOf("\"low\":", idx) + 6;
                    int lowEnd = response.indexOf(",", lowIdx);
                    if (lowEnd == -1) lowEnd = response.indexOf("}", lowIdx);

                    try {
                        result.highPrice = Integer.parseInt(
                                response.substring(highIdx, highEnd).trim()
                        );
                        result.lowPrice = Integer.parseInt(
                                response.substring(lowIdx, lowEnd).trim()
                        );
                        result.averagePrice = (result.highPrice + result.lowPrice) / 2;

                        BotUtils.log("✅ Price found - High: " + result.highPrice +
                                ", Low: " + result.lowPrice +
                                ", Avg: " + result.averagePrice);

                        return result;
                    } catch (Exception e) {
                        BotUtils.log("❌ Failed to parse price data");
                    }
                }
            }
        } catch (Exception e) {
            BotUtils.logError("Error fetching price by ID", e);
        }

        return null;
    }

    /**
     * Test the API with some common items
     */
    public static void testAPI() {
        BotUtils.log("🧪 Testing ItemSearchAPI...");

        String[] testItems = {"Santa hat", "Rune 2h sword", "Nature rune", "Dragon scimitar"};

        for (String item : testItems) {
            ItemSearchResult result = searchItem(item);
            if (result != null) {
                BotUtils.log("✅ Found: " + result.toString());
            } else {
                BotUtils.log("❌ Not found: " + item);
            }
        }
    }
}