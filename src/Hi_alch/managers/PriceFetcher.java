package Hi_alch.managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.net.ConnectException;

/**
 * Price Fetcher - Handles API requests for price data
 *
 * Features:
 * - Multiple API endpoint fallback
 * - HTTP connection management
 * - Error handling and retries
 * - Rate limiting enforcement
 */
public class PriceFetcher {

    // API endpoints
    private static final String[] API_ENDPOINTS = {
            "https://secure.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=",
            "https://oldschool.runescape.wiki/api.php?action=query&format=json&prop=extracts&titles=",
            "https://api.osrsbox.com/items/"
    };

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final long MIN_REQUEST_INTERVAL = 1000;

    private long lastRequestTime = 0;
    private int totalRequests = 0;
    private int successfulRequests = 0;

    /**
     * Fetch price data from API
     */
    public int[] fetchPrices(int itemId) {
        enforceRateLimit();
        totalRequests++;

        // Try each API endpoint until one succeeds
        for (int i = 0; i < API_ENDPOINTS.length; i++) {
            try {
                String endpoint = API_ENDPOINTS[i] + itemId;
                BotUtils.log("🔍 Fetching price from API endpoint " + (i + 1) + "...");

                String response = makeHttpRequest(endpoint);
                if (response != null && !response.isEmpty()) {
                    int[] prices = parsePriceResponse(response, i);
                    if (prices != null && prices[0] > 0) {
                        successfulRequests++;
                        BotUtils.log("✅ Successfully fetched prices from endpoint " + (i + 1));
                        return prices;
                    }
                }

            } catch (SocketTimeoutException e) {
                BotUtils.log("⏰ Timeout on endpoint " + (i + 1) + ", trying next...");
            } catch (ConnectException e) {
                BotUtils.log("🔌 Connection failed to endpoint " + (i + 1) + ", trying next...");
            } catch (Exception e) {
                BotUtils.log("⚠️ Error with endpoint " + (i + 1) + ": " + e.getMessage());
            }
        }

        BotUtils.log("❌ All API endpoints failed for item " + itemId);
        return new int[]{0, 0};
    }

    /**
     * Make HTTP request to endpoint
     */
    private String makeHttpRequest(String endpoint) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("User-Agent", "OSRS-Bot/1.0");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BotUtils.log("⚠️ HTTP " + responseCode + " from API");
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();

        } finally {
            conn.disconnect();
        }
    }

    /**
     * Parse API response to extract prices
     */
    private int[] parsePriceResponse(String response, int endpointIndex) {
        try {
            // Simple JSON parsing for price data
            // This is a simplified parser - production code would use a JSON library

            if (response.contains("\"price\"")) {
                // Extract price value from JSON
                int priceIndex = response.indexOf("\"price\"");
                int colonIndex = response.indexOf(":", priceIndex);
                int commaIndex = response.indexOf(",", colonIndex);
                if (commaIndex == -1) commaIndex = response.indexOf("}", colonIndex);

                if (colonIndex > 0 && commaIndex > colonIndex) {
                    String priceStr = response.substring(colonIndex + 1, commaIndex).trim();
                    priceStr = priceStr.replaceAll("[^0-9]", "");

                    if (!priceStr.isEmpty()) {
                        int price = Integer.parseInt(priceStr);
                        // Return [buyPrice, sellPrice]
                        // Many APIs don't distinguish, so use same for both
                        return new int[]{price, price};
                    }
                }
            }

        } catch (Exception e) {
            BotUtils.log("⚠️ Error parsing price response: " + e.getMessage());
        }

        return null;
    }

    /**
     * Enforce rate limiting between requests
     */
    private void enforceRateLimit() {
        long timeSinceLastRequest = System.currentTimeMillis() - lastRequestTime;
        if (timeSinceLastRequest < MIN_REQUEST_INTERVAL) {
            long sleepTime = MIN_REQUEST_INTERVAL - timeSinceLastRequest;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastRequestTime = System.currentTimeMillis();
    }

    // Statistics
    public int getTotalRequests() {
        return totalRequests;
    }

    public int getSuccessfulRequests() {
        return successfulRequests;
    }

    public double getSuccessRate() {
        if (totalRequests == 0) return 0.0;
        return (double) successfulRequests / totalRequests * 100.0;
    }
}
