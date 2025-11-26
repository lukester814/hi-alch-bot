package Hi_alch.engine;

import Hi_alch.utils.BotUtils;
import Hi_alch.api.ItemSearchAPI;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.utilities.Sleep;

/**
 * Manages supply purchasing and inventory checks
 */
public class SupplyManager {

    private static final int NATURE_RUNE_ID = 563;
    private static final int COINS_ID = 995;

    /**
     * Buy items from Grand Exchange
     */
    public static boolean buyItems(int itemId, String itemName, int buyQuantity, int buyPrice) {
        try {
            // Validate item ID first
            if (itemId <= 0) {
                BotUtils.log("❌ Invalid item ID: " + itemId + " for item: " + itemName);
                BotUtils.log("💡 Item ID must be a positive number");
                return false;
            }

            if (!GrandExchange.isOpen()) {
                if (!GrandExchange.open()) {
                    BotUtils.log("❌ Failed to open Grand Exchange");
                    return false;
                }
                Sleep.sleepUntil(GrandExchange::isOpen, 5000);
            }

            if (GrandExchange.getFirstOpenSlot() == -1) {
                BotUtils.log("❌ No free GE slots available");
                return false;
            }

            BotUtils.log("💰 Buying " + buyQuantity + "x " + itemName + " at " +
                    BotUtils.formatNumber(buyPrice) + " GP each");

            if (GrandExchange.buyItem(itemId, buyQuantity, buyPrice)) {
                BotUtils.log("✅ Buy offer placed: " + buyQuantity + "x " + itemName);

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 30000 && GrandExchange.isOpen()) {
                    if (GrandExchange.isReadyToCollect()) {
                        if (GrandExchange.collect()) {
                            BotUtils.log("✅ Items collected successfully");
                            return true;
                        }
                    }
                    Sleep.sleep(1000);
                }

                BotUtils.log("⏰ Buy offer taking too long, continuing anyway");
                return true;

            } else {
                BotUtils.log("❌ Failed to place buy offer");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying items", e);
            return false;
        }
    }

    /**
     * Buy nature runes from Grand Exchange
     */
    public static boolean buyNatureRunes(int quantity) {
        try {
            if (!GrandExchange.isOpen()) {
                if (!GrandExchange.open()) {
                    BotUtils.log("❌ Failed to open Grand Exchange");
                    return false;
                }
                Sleep.sleepUntil(GrandExchange::isOpen, 5000);
            }

            if (GrandExchange.getFirstOpenSlot() == -1) {
                BotUtils.log("❌ No free GE slots available");
                return false;
            }

            int buyQuantity = Math.min(quantity, Inventory.getEmptySlots());

            if (buyQuantity <= 0) {
                BotUtils.log("❌ No inventory space for nature runes");
                return false;
            }

            if (GrandExchange.buyItem(NATURE_RUNE_ID, buyQuantity, 300)) {
                BotUtils.log("✅ Nature rune buy offer placed: " + buyQuantity + "x");

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 20000 && GrandExchange.isOpen()) {
                    if (GrandExchange.isReadyToCollect()) {
                        if (GrandExchange.collect()) {
                            BotUtils.log("✅ Nature runes collected successfully");
                            return true;
                        }
                    }
                    Sleep.sleep(1000);
                }

                BotUtils.log("⏰ Nature rune offer taking too long, continuing anyway");
                return true;

            } else {
                BotUtils.log("❌ Failed to place nature rune buy offer");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying nature runes", e);
            return false;
        }
    }

    /**
     * Get market price for an item
     */
    public static int getMarketPrice(int itemId, String itemName) {
        try {
            // Try to get price by item ID
            if (itemId > 0) {
                ItemSearchAPI.ItemSearchResult priceData = ItemSearchAPI.getItemPrice(itemId);
                if (priceData != null && priceData.averagePrice > 0) {
                    BotUtils.log("💰 Got live price for " + itemName + " (ID: " + itemId + "): " +
                            BotUtils.formatNumber(priceData.averagePrice) + " GP");
                    return priceData.averagePrice;
                }
            }

            // Search by name
            BotUtils.log("🔍 Searching for price by name: " + itemName);
            ItemSearchAPI.ItemSearchResult searchResult = ItemSearchAPI.searchItem(itemName);

            if (searchResult != null && searchResult.isValid() && searchResult.averagePrice > 0) {
                BotUtils.log("✅ Found " + searchResult.itemName + " (ID: " + searchResult.itemId +
                        ") - Price: " + BotUtils.formatNumber(searchResult.averagePrice) + " GP");
                return searchResult.averagePrice;
            }

            // Fallback prices
            BotUtils.log("⚠️ Could not get live price for " + itemName + ", using fallback");
            String nameLower = itemName.toLowerCase();

            if (nameLower.contains("santa hat")) {
                return 150000000;
            } else if (nameLower.contains("party hat")) {
                return 2147000000;
            } else if (nameLower.contains("rune 2h")) {
                return 37500;
            } else if (nameLower.contains("rune platebody")) {
                return 38000;
            }

            return 10000;

        } catch (Exception e) {
            BotUtils.logError("Error getting market price", e);
            return 10000;
        }
    }

    /**
     * Check if item is in inventory (supports both noted and unnoted)
     */
    public static boolean hasItem(int itemId) {
        int notedItemId = itemId + 1;
        boolean hasUnnoted = Inventory.contains(itemId) && Inventory.count(itemId) > 0;
        boolean hasNoted = Inventory.contains(notedItemId) && Inventory.count(notedItemId) > 0;
        return hasUnnoted || hasNoted;
    }

    /**
     * Check if nature runes are in inventory
     */
    public static boolean hasNatureRunes() {
        return Inventory.contains(NATURE_RUNE_ID) && Inventory.count(NATURE_RUNE_ID) > 0;
    }

    /**
     * Get nature rune count
     */
    public static int getNatureRuneCount() {
        return Inventory.count(NATURE_RUNE_ID);
    }

    /**
     * Check if player has sufficient cash in inventory
     */
    public static boolean hasCash(int minAmount) {
        return Inventory.contains(COINS_ID) && Inventory.count(COINS_ID) >= minAmount;
    }

    /**
     * Get current cash amount in inventory
     */
    public static int getCashAmount() {
        return Inventory.count(COINS_ID);
    }

    /**
     * Check if player has cash in inventory (any amount)
     */
    public static boolean hasCash() {
        return Inventory.contains(COINS_ID) && Inventory.count(COINS_ID) > 0;
    }
}
