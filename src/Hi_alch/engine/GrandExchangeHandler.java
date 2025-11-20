package Hi_alch.engine;

import Hi_alch.BotUtils;
import Hi_alch.ItemSearchAPI;
import Hi_alch.OverlayRenderer;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.utilities.Sleep;

/**
 * Grand Exchange Handler - GE Trading Operations
 *
 * Manages all Grand Exchange operations including buying items,
 * buying nature runes, and retrieving market prices.
 *
 * @author Hi-Alch Bot Team
 * @version 1.0
 */
public class GrandExchangeHandler {

    // Constants
    private static final int NATURE_RUNE_ID = 563;
    private static final int NATURE_RUNE_PRICE = 300;

    private OverlayRenderer.ScriptStatistics statistics;

    /**
     * Result of a buy operation
     */
    public static class BuyResult {
        public final boolean success;
        public final String message;
        public final NextAction nextAction;
        public final int itemsPurchased;
        public final int totalCost;

        public BuyResult(boolean success, String message, NextAction nextAction,
                        int itemsPurchased, int totalCost) {
            this.success = success;
            this.message = message;
            this.nextAction = nextAction;
            this.itemsPurchased = itemsPurchased;
            this.totalCost = totalCost;
        }

        public static BuyResult success(String message, NextAction nextAction,
                                       int itemsPurchased, int totalCost) {
            return new BuyResult(true, message, nextAction, itemsPurchased, totalCost);
        }

        public static BuyResult failure(String message) {
            return new BuyResult(false, message, NextAction.ERROR_RECOVERY, 0, 0);
        }
    }

    /**
     * Next action after buying
     */
    public enum NextAction {
        CHECK_SUPPLIES,
        ERROR_RECOVERY
    }

    /**
     * Constructor
     *
     * @param statistics Statistics tracker for updates
     */
    public GrandExchangeHandler(OverlayRenderer.ScriptStatistics statistics) {
        this.statistics = statistics;
    }

    /**
     * Buy items from the Grand Exchange
     *
     * @param itemId The item ID to purchase
     * @param itemName The item name
     * @param buyLimit The buy limit/quantity
     * @param priceMarkup The price markup percentage
     * @return BuyResult containing operation status
     */
    public BuyResult buyItems(int itemId, String itemName, int buyLimit, double priceMarkup) {
        statistics.currentState = "Buying Items";
        statistics.currentAction = "Purchasing " + itemName;

        BotUtils.log("💰 Buying items: " + itemName);

        try {
            if (!GrandExchange.isOpen()) {
                if (!GrandExchange.open()) {
                    BotUtils.log("❌ Failed to open Grand Exchange");
                    return BuyResult.failure("Failed to open Grand Exchange");
                }
                Sleep.sleepUntil(GrandExchange::isOpen, 5000);
            }

            if (GrandExchange.getFirstOpenSlot() == -1) {
                BotUtils.log("❌ No free GE slots available");
                return BuyResult.failure("No free GE slots available");
            }

            // Get actual market price
            int marketPrice = getMarketPrice(itemId, itemName);

            // Apply the markup percentage
            int buyPrice = (int)(marketPrice * (1 + priceMarkup/100));

            // Use the configured buy limit
            int buyQuantity = buyLimit;

            // Check if we have enough inventory space
            int emptySlots = Inventory.getEmptySlots();
            if (emptySlots < 5) {
                BotUtils.log("⚠️ Low inventory space: " + emptySlots + " slots");
            }

            BotUtils.log("📊 Market price: " + BotUtils.formatNumber(marketPrice) + " GP");
            BotUtils.log("💰 Buying " + buyQuantity + "x at " + BotUtils.formatNumber(buyPrice) +
                    " GP each (+" + String.format("%.1f%%", priceMarkup) + " markup)");
            BotUtils.log("💵 Total cost: " + BotUtils.formatNumber(buyQuantity * buyPrice) + " GP");

            if (GrandExchange.buyItem(itemId, buyQuantity, buyPrice)) {
                BotUtils.log("✅ Buy offer placed: " + buyQuantity + "x " + itemName);

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 30000 && GrandExchange.isOpen()) {
                    if (GrandExchange.isReadyToCollect()) {
                        if (GrandExchange.collect()) {
                            BotUtils.log("✅ Items collected successfully");
                            return BuyResult.success("Items purchased successfully",
                                                    NextAction.CHECK_SUPPLIES,
                                                    buyQuantity, buyQuantity * buyPrice);
                        }
                    }
                    Sleep.sleep(1000);
                }

                BotUtils.log("⏰ Buy offer taking too long, continuing anyway");
                return BuyResult.success("Buy offer placed (waiting for completion)",
                                        NextAction.CHECK_SUPPLIES, buyQuantity, buyQuantity * buyPrice);

            } else {
                BotUtils.log("❌ Failed to place buy offer");
                return BuyResult.failure("Failed to place buy offer");
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying items", e);
            if (statistics != null) {
                statistics.errorsEncountered++;
                statistics.lastError = e.getMessage();
            }
            return BuyResult.failure("Error buying items: " + e.getMessage());
        }
    }

    /**
     * Buy nature runes from the Grand Exchange
     *
     * @param runeAmount The amount of nature runes to purchase
     * @return BuyResult containing operation status
     */
    public BuyResult buyNatureRunes(int runeAmount) {
        statistics.currentState = "Buying Runes";
        statistics.currentAction = "Purchasing nature runes";

        BotUtils.log("🌿 Buying nature runes: " + runeAmount);

        try {
            if (!GrandExchange.isOpen()) {
                if (!GrandExchange.open()) {
                    BotUtils.log("❌ Failed to open Grand Exchange");
                    return BuyResult.failure("Failed to open Grand Exchange");
                }
                Sleep.sleepUntil(GrandExchange::isOpen, 5000);
            }

            if (GrandExchange.getFirstOpenSlot() == -1) {
                BotUtils.log("❌ No free GE slots available");
                return BuyResult.failure("No free GE slots available");
            }

            int buyQuantity = Math.min(runeAmount, Inventory.getEmptySlots());

            if (buyQuantity <= 0) {
                BotUtils.log("❌ No inventory space for nature runes");
                return BuyResult.failure("No inventory space for nature runes");
            }

            if (GrandExchange.buyItem(NATURE_RUNE_ID, buyQuantity, NATURE_RUNE_PRICE)) {
                BotUtils.log("✅ Nature rune buy offer placed: " + buyQuantity + "x");

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 20000 && GrandExchange.isOpen()) {
                    if (GrandExchange.isReadyToCollect()) {
                        if (GrandExchange.collect()) {
                            BotUtils.log("✅ Nature runes collected successfully");
                            return BuyResult.success("Nature runes purchased successfully",
                                                    NextAction.CHECK_SUPPLIES,
                                                    buyQuantity, buyQuantity * NATURE_RUNE_PRICE);
                        }
                    }
                    Sleep.sleep(1000);
                }

                BotUtils.log("⏰ Nature rune offer taking too long, continuing anyway");
                return BuyResult.success("Nature rune offer placed (waiting for completion)",
                                        NextAction.CHECK_SUPPLIES,
                                        buyQuantity, buyQuantity * NATURE_RUNE_PRICE);

            } else {
                BotUtils.log("❌ Failed to place nature rune buy offer");
                return BuyResult.failure("Failed to place nature rune buy offer");
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying nature runes", e);
            if (statistics != null) {
                statistics.errorsEncountered++;
                statistics.lastError = e.getMessage();
            }
            return BuyResult.failure("Error buying nature runes: " + e.getMessage());
        }
    }

    /**
     * Get market price for an item using live GE data
     *
     * @param itemId The item ID
     * @param itemName The item name
     * @return The market price in GP
     */
    public int getMarketPrice(int itemId, String itemName) {
        try {
            // First try to get price by item ID if valid
            if (itemId > 0) {
                ItemSearchAPI.ItemSearchResult priceData = ItemSearchAPI.getItemPrice(itemId);
                if (priceData != null && priceData.averagePrice > 0) {
                    BotUtils.log("💰 Got live price for " + itemName + " (ID: " + itemId + "): " +
                            BotUtils.formatNumber(priceData.averagePrice) + " GP");
                    return priceData.averagePrice;
                }
            }

            // If no ID or price lookup failed, search by name
            BotUtils.log("🔍 Searching for price by name: " + itemName);
            ItemSearchAPI.ItemSearchResult searchResult = ItemSearchAPI.searchItem(itemName);

            if (searchResult != null && searchResult.isValid() && searchResult.averagePrice > 0) {
                BotUtils.log("✅ Found " + searchResult.itemName + " (ID: " + searchResult.itemId +
                        ") - Price: " + BotUtils.formatNumber(searchResult.averagePrice) + " GP");
                return searchResult.averagePrice;
            }

            // Fallback prices for common items if API fails
            BotUtils.log("⚠️ Could not get live price for " + itemName + ", using fallback");
            return getFallbackPrice(itemName);

        } catch (Exception e) {
            BotUtils.logError("Error getting market price", e);
            return 10000; // Default fallback
        }
    }

    /**
     * Get fallback price for common items when API fails
     *
     * @param itemName The item name
     * @return Fallback price in GP
     */
    private int getFallbackPrice(String itemName) {
        String nameLower = itemName.toLowerCase();

        if (nameLower.contains("santa hat")) {
            return 150000000; // 150M for Santa hat
        } else if (nameLower.contains("party hat")) {
            return 2147000000; // Max cash for party hats
        } else if (nameLower.contains("rune 2h")) {
            return 37500;
        } else if (nameLower.contains("rune platebody")) {
            return 38000;
        }

        // Default price
        return 10000;
    }
}
