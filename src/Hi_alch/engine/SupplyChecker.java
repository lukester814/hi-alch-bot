package Hi_alch.engine;

import Hi_alch.BotUtils;
import org.dreambot.api.methods.container.impl.Inventory;

/**
 * Supply Checker - Inventory Analysis System
 *
 * Handles checking and validating inventory supplies for alchemy operations.
 * Determines what items and runes are available and recommends next actions.
 *
 * @author Hi-Alch Bot Team
 * @version 1.0
 */
public class SupplyChecker {

    // Constants
    private static final int NATURE_RUNE_ID = 563;
    private static final int MIN_NATURE_RUNES = 50;

    /**
     * Result of supply check containing status and recommendations
     */
    public static class SupplyStatus {
        public final boolean hasUnnotedItems;
        public final boolean hasNotedItems;
        public final boolean hasNatureRunes;
        public final int unnotedItemCount;
        public final int notedItemCount;
        public final int natureRuneCount;
        public final NextAction recommendedAction;
        public final String message;

        public SupplyStatus(boolean hasUnnotedItems, boolean hasNotedItems, boolean hasNatureRunes,
                          int unnotedItemCount, int notedItemCount, int natureRuneCount,
                          NextAction recommendedAction, String message) {
            this.hasUnnotedItems = hasUnnotedItems;
            this.hasNotedItems = hasNotedItems;
            this.hasNatureRunes = hasNatureRunes;
            this.unnotedItemCount = unnotedItemCount;
            this.notedItemCount = notedItemCount;
            this.natureRuneCount = natureRuneCount;
            this.recommendedAction = recommendedAction;
            this.message = message;
        }
    }

    /**
     * Recommended next action based on supply analysis
     */
    public enum NextAction {
        START_ALCHING("Ready to begin alchemy"),
        BUY_ITEMS("Need to purchase items"),
        BUY_NATURE_RUNES("Need to purchase nature runes"),
        UNNOTE_ITEMS("Need to unnote items at bank"),
        ERROR_NO_SUPPLIES("No supplies available");

        private final String description;

        NextAction(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Check inventory supplies and determine next action
     *
     * @param itemId The ID of the item to alch
     * @param skipBuying Whether skip buying mode is enabled
     * @param restockWhenEmpty Whether to restock when empty
     * @return SupplyStatus containing analysis and recommendations
     */
    public SupplyStatus checkSupplies(int itemId, boolean skipBuying, boolean restockWhenEmpty) {
        BotUtils.log("📦 Checking inventory supplies...");

        try {
            // Check for both noted and unnoted items
            int notedItemId = itemId + 1; // Noted items are usually ID + 1
            boolean hasUnnotedItems = Inventory.contains(itemId) && Inventory.count(itemId) > 0;
            boolean hasNotedItems = Inventory.contains(notedItemId) && Inventory.count(notedItemId) > 0;
            boolean hasItems = hasUnnotedItems || hasNotedItems;
            boolean hasNatureRunes = Inventory.contains(NATURE_RUNE_ID) && Inventory.count(NATURE_RUNE_ID) > 0;

            int unnotedCount = hasUnnotedItems ? Inventory.count(itemId) : 0;
            int notedCount = hasNotedItems ? Inventory.count(notedItemId) : 0;
            int natureRuneCount = hasNatureRunes ? Inventory.count(NATURE_RUNE_ID) : 0;

            if (hasItems) {
                BotUtils.log("🔍 Items found - Unnoted: " + unnotedCount + " | Noted: " + notedCount);
            }
            BotUtils.log("🌿 Nature Runes: " + (hasNatureRunes ? "✅ " + natureRuneCount : "❌"));

            // Determine recommended action based on skip buying mode
            if (skipBuying) {
                return handleSkipBuyingMode(hasItems, hasNatureRunes, hasNotedItems, hasUnnotedItems,
                                          unnotedCount, notedCount, natureRuneCount, restockWhenEmpty);
            } else {
                return handleNormalMode(hasItems, hasNatureRunes, unnotedCount, notedCount, natureRuneCount);
            }

        } catch (Exception e) {
            BotUtils.logError("Error checking supplies", e);
            return new SupplyStatus(false, false, false, 0, 0, 0,
                                  NextAction.ERROR_NO_SUPPLIES, "Error checking supplies: " + e.getMessage());
        }
    }

    /**
     * Handle supply checking in skip buying mode
     */
    private SupplyStatus handleSkipBuyingMode(boolean hasItems, boolean hasNatureRunes,
                                             boolean hasNotedItems, boolean hasUnnotedItems,
                                             int unnotedCount, int notedCount, int natureRuneCount,
                                             boolean restockWhenEmpty) {
        BotUtils.log("💡 Skip buying enabled - checking existing inventory");

        if (!hasItems) {
            BotUtils.log("❌ Skip buying enabled but no items in inventory!");
            return new SupplyStatus(false, false, hasNatureRunes, 0, 0, natureRuneCount,
                                  NextAction.ERROR_NO_SUPPLIES,
                                  "No items to alch in inventory - please add items or disable skip buying");
        }

        if (!hasNatureRunes) {
            BotUtils.log("⚠️ Skip buying enabled but no nature runes - will buy those");
            return new SupplyStatus(hasUnnotedItems, hasNotedItems, false,
                                  unnotedCount, notedCount, 0,
                                  NextAction.BUY_NATURE_RUNES,
                                  "Need to purchase nature runes");
        }

        // Has items and nature runes - check if we need to unnote
        if (hasNotedItems && !hasUnnotedItems) {
            BotUtils.log("📝 Only noted items found - may need to unnote at bank");
            return new SupplyStatus(false, true, true, 0, notedCount, natureRuneCount,
                                  NextAction.UNNOTE_ITEMS,
                                  "Need to unnote items at bank");
        }

        BotUtils.log("✅ Items and runes found - proceeding to alchemy");
        return new SupplyStatus(hasUnnotedItems, hasNotedItems, true,
                              unnotedCount, notedCount, natureRuneCount,
                              NextAction.START_ALCHING,
                              "Ready to begin alchemy");
    }

    /**
     * Handle supply checking in normal buying mode
     */
    private SupplyStatus handleNormalMode(boolean hasItems, boolean hasNatureRunes,
                                         int unnotedCount, int notedCount, int natureRuneCount) {
        if (!hasItems) {
            BotUtils.log("💰 Need to buy items");
            return new SupplyStatus(false, false, hasNatureRunes, 0, 0, natureRuneCount,
                                  NextAction.BUY_ITEMS,
                                  "Need to purchase items");
        } else if (!hasNatureRunes) {
            BotUtils.log("🌿 Need to buy nature runes");
            return new SupplyStatus(true, false, false, unnotedCount, notedCount, 0,
                                  NextAction.BUY_NATURE_RUNES,
                                  "Need to purchase nature runes");
        } else {
            BotUtils.log("✅ Supplies ready, proceeding to alchemy");
            return new SupplyStatus(true, false, true, unnotedCount, notedCount, natureRuneCount,
                                  NextAction.START_ALCHING,
                                  "Supplies ready for alchemy");
        }
    }
}
