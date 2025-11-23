package Hi_alch.gui;

import Hi_alch.utils.BotUtils;
import Hi_alch.api.ItemSearchAPI;
import java.awt.Color;

/**
 * Utility class for calculating and formatting profit information
 */
public class ProfitCalculator {

    /**
     * Calculate profit preview text and color
     */
    public static class ProfitResult {
        public String text;
        public Color color;

        public ProfitResult(String text, Color color) {
            this.text = text;
            this.color = color;
        }
    }

    public static ProfitResult calculateProfit(String itemName, int buyLimit, double markup) {
        try {
            if (itemName == null || itemName.startsWith("Select")) {
                return new ProfitResult("Select an item to see real profit analysis", new Color(156, 163, 175));
            }

            ItemSearchAPI.ItemSearchResult apiData = ItemSearchAPI.searchItem(itemName);

            if (apiData != null && apiData.isValid()) {
                int buyPrice = apiData.averagePrice > 0 ? apiData.averagePrice : apiData.highPrice;
                int alchValue = apiData.alchValue;

                int adjustedBuyPrice = (int) (buyPrice * (1.0 + markup / 100.0));
                int natureRuneCost = 220;
                int profitPerItem = alchValue - adjustedBuyPrice - natureRuneCost;
                int totalProfit = profitPerItem * buyLimit;

                String profitText;
                Color profitColor;

                if (profitPerItem > 0) {
                    profitText = String.format("💰 %s: +%s GP profit per item | Total: +%s GP (LIVE DATA)",
                            itemName,
                            BotUtils.formatNumber(profitPerItem),
                            BotUtils.formatNumber(totalProfit));
                    profitColor = new Color(34, 197, 94);
                } else {
                    profitText = String.format("⚠️ %s: %s GP loss per item | Total: %s GP (LIVE DATA)",
                            itemName,
                            BotUtils.formatNumber(Math.abs(profitPerItem)),
                            BotUtils.formatNumber(totalProfit));
                    profitColor = new Color(239, 68, 68);
                }

                return new ProfitResult(profitText, profitColor);
            } else {
                return new ProfitResult("Unable to fetch live data for " + itemName, new Color(239, 68, 68));
            }

        } catch (Exception e) {
            return new ProfitResult("Error calculating profit preview", new Color(239, 68, 68));
        }
    }
}
