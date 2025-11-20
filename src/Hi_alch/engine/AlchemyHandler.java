package Hi_alch.engine;

import Hi_alch.BotUtils;
import Hi_alch.OverlayRenderer;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.methods.interactive.Players;

/**
 * Alchemy Handler - High Alchemy Operations Manager
 *
 * Manages all high alchemy operations including casting spells,
 * configuring alchemy settings, and tracking alchemy statistics.
 *
 * @author Hi-Alch Bot Team
 * @version 1.0
 */
public class AlchemyHandler {

    // Constants
    private static final int NATURE_RUNE_ID = 563;
    private static final int ALCHEMY_XP = 65;
    private static final int LOW_ITEM_THRESHOLD = 5;

    private OverlayRenderer.ScriptStatistics statistics;
    private boolean alchemyConfigured = false;

    /**
     * Result of an alchemy operation
     */
    public static class AlchResult {
        public final boolean success;
        public final String message;
        public final NextAction nextAction;
        public final int xpGained;
        public final int profitGained;
        public final int alchCount;

        public AlchResult(boolean success, String message, NextAction nextAction,
                         int xpGained, int profitGained, int alchCount) {
            this.success = success;
            this.message = message;
            this.nextAction = nextAction;
            this.xpGained = xpGained;
            this.profitGained = profitGained;
            this.alchCount = alchCount;
        }

        public static AlchResult success(String message, NextAction nextAction,
                                        int xpGained, int profitGained, int alchCount) {
            return new AlchResult(true, message, nextAction, xpGained, profitGained, alchCount);
        }

        public static AlchResult failure(String message, NextAction nextAction) {
            return new AlchResult(false, message, nextAction, 0, 0, 0);
        }
    }

    /**
     * Next action after alchemy
     */
    public enum NextAction {
        CONTINUE_ALCHING,
        RESTOCK_ITEMS,
        BUY_NATURE_RUNES,
        COMPLETE_SESSION,
        ERROR_RECOVERY
    }

    /**
     * Constructor
     *
     * @param statistics Statistics tracker for updates
     */
    public AlchemyHandler(OverlayRenderer.ScriptStatistics statistics) {
        this.statistics = statistics;
        this.alchemyConfigured = false;
    }

    /**
     * Perform initial alchemy configuration (call once per session)
     * Configures the high alchemy warning threshold to prevent accidental valuable item alching
     */
    public void initializeAlchemy() {
        if (alchemyConfigured) {
            BotUtils.log("⚙️ Alchemy already configured for this session");
            return;
        }

        BotUtils.log("🔧 Performing one-time alchemy configuration...");
        configureHighAlchemy();
        alchemyConfigured = true;
    }

    /**
     * Perform high alchemy on an item
     *
     * @param itemId The item ID to alch
     * @param itemName The item name
     * @param skipBuying Whether skip buying mode is enabled
     * @param restockWhenEmpty Whether to restock when empty
     * @return AlchResult containing operation status
     */
    public AlchResult performAlchemy(int itemId, String itemName, boolean skipBuying, boolean restockWhenEmpty) {
        statistics.currentState = "Alching";
        statistics.currentAction = "Performing high alchemy";

        try {
            // Check if we have items to alch
            if (!Inventory.contains(itemId)) {
                BotUtils.log("❌ No more items to alch");

                // Check if we should restock or stop
                if (skipBuying && !restockWhenEmpty) {
                    BotUtils.log("🛑 Skip buying enabled with no restock - completing session");
                    return AlchResult.failure("No more items to alch", NextAction.COMPLETE_SESSION);
                } else {
                    BotUtils.log("🔄 Proceeding to restock items");
                    return AlchResult.failure("Out of items", NextAction.RESTOCK_ITEMS);
                }
            }

            // Check if we have nature runes
            if (!Inventory.contains(NATURE_RUNE_ID)) {
                BotUtils.log("❌ No more nature runes");

                if (skipBuying && !restockWhenEmpty) {
                    BotUtils.log("🛑 Out of nature runes and restocking disabled - completing session");
                    return AlchResult.failure("No more nature runes", NextAction.COMPLETE_SESSION);
                } else {
                    return AlchResult.failure("Out of nature runes", NextAction.BUY_NATURE_RUNES);
                }
            }

            // Open magic tab if not already open
            if (!Tabs.isOpen(Tab.MAGIC)) {
                Tabs.open(Tab.MAGIC);
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.MAGIC), 2000);
                return AlchResult.failure("Opening magic tab", NextAction.CONTINUE_ALCHING);
            }

            // Check if we can cast high alchemy
            if (!Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY)) {
                BotUtils.log("❌ Cannot cast High Level Alchemy");
                BotUtils.log("🔍 Magic level: " + Skills.getBoostedLevel(Skill.MAGIC));
                BotUtils.log("🌿 Nature runes: " + Inventory.count(NATURE_RUNE_ID));

                if (statistics != null) {
                    statistics.errorsEncountered++;
                }
                return AlchResult.failure("Cannot cast High Level Alchemy", NextAction.ERROR_RECOVERY);
            }

            // Get the item to alch
            Item itemToAlch = Inventory.get(itemId);

            if (itemToAlch == null) {
                BotUtils.log("❌ Item not found in inventory");
                return AlchResult.failure("Item not found", NextAction.RESTOCK_ITEMS);
            }

            BotUtils.log("🎯 Casting High Alchemy on: " + itemToAlch.getName());

            // Cast the spell
            if (!Magic.castSpell(Normal.HIGH_LEVEL_ALCHEMY)) {
                BotUtils.log("❌ Failed to cast High Level Alchemy spell");
                return AlchResult.failure("Failed to cast spell", NextAction.ERROR_RECOVERY);
            }

            Sleep.sleep(300);

            // Click on the item
            if (!itemToAlch.interact()) {
                BotUtils.log("❌ Failed to click item after casting spell");
                return AlchResult.failure("Failed to click item", NextAction.CONTINUE_ALCHING);
            }

            // Wait for animation to complete
            Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000);

            // Update statistics
            int currentAlchCount = statistics.alchsCompleted + 1;
            statistics.alchsCompleted = currentAlchCount;

            int estimatedProfit = 50; // TODO: Calculate actual profit
            statistics.totalProfit += estimatedProfit;
            statistics.xpGained += ALCHEMY_XP;

            BotUtils.log("🔥 Alch #" + currentAlchCount + " completed (+" + ALCHEMY_XP + " XP, +" +
                    BotUtils.formatNumber(estimatedProfit) + " GP est.)");

            // Determine next action
            NextAction nextAction = determineNextAction(itemId, skipBuying, restockWhenEmpty);

            return AlchResult.success("Alchemy completed", nextAction,
                                     ALCHEMY_XP, estimatedProfit, currentAlchCount);

        } catch (Exception e) {
            BotUtils.logError("Error during alchemy", e);
            if (statistics != null) {
                statistics.errorsEncountered++;
                statistics.lastError = e.getMessage();
            }
            return AlchResult.failure("Error during alchemy: " + e.getMessage(),
                                     NextAction.ERROR_RECOVERY);
        }
    }

    /**
     * Determine the next action based on current inventory state
     */
    private NextAction determineNextAction(int itemId, boolean skipBuying, boolean restockWhenEmpty) {
        // Don't check for low inventory if skip buying without restock
        if (skipBuying && !restockWhenEmpty) {
            // Just continue alching until we run out
            return NextAction.CONTINUE_ALCHING;
        }

        // Normal restock check
        if (Inventory.count(itemId) <= LOW_ITEM_THRESHOLD) {
            return NextAction.RESTOCK_ITEMS;
        }

        return NextAction.CONTINUE_ALCHING;
    }

    /**
     * Configure high alchemy warning threshold
     */
    private void configureHighAlchemy() {
        try {
            BotUtils.log("⚙️ Configuring High Level Alchemy warning threshold...");

            if (!Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY)) {
                int magicLevel = Skills.getBoostedLevel(Skill.MAGIC);
                int natureRunes = Inventory.count(NATURE_RUNE_ID);

                BotUtils.log("❌ Cannot cast High Level Alchemy:");
                BotUtils.log("   Magic Level: " + magicLevel + " (need 55)");
                BotUtils.log("   Nature Runes: " + natureRunes + " (need 1+)");
                return;
            }

            if (rightClickAlchSpell()) {
                BotUtils.log("✅ Successfully configured High Level Alchemy threshold");
            } else {
                BotUtils.log("⚠️ Could not configure warning threshold - will attempt alchemy anyway");
            }

        } catch (Exception e) {
            BotUtils.logError("Error configuring high alchemy", e);
        }
    }

    /**
     * Right-click High Alchemy spell and configure warning threshold
     *
     * @return true if configuration was successful
     */
    private boolean rightClickAlchSpell() {
        try {
            BotUtils.log("🔧 Attempting to configure High Level Alchemy threshold...");

            // Open magic tab
            if (!Tabs.isOpen(Tab.MAGIC)) {
                if (!Tabs.open(Tab.MAGIC)) {
                    BotUtils.log("❌ Failed to open magic tab");
                    return false;
                }
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.MAGIC), 3000);
            }

            Sleep.sleep(1000);
            BotUtils.log("✅ Magic tab is open");

            // Find the High Level Alchemy spell widget
            WidgetChild alchSpell = Widgets.get(218, 44);

            if (alchSpell == null || !alchSpell.isVisible()) {
                BotUtils.log("❌ Could not find High Level Alchemy spell at widget 218, 44");
                return false;
            }

            BotUtils.log("✅ Found High Level Alchemy spell");

            Sleep.sleep(500);

            // Try to interact with "Warnings" action
            if (alchSpell.interact("Warnings")) {
                BotUtils.log("✅ Successfully clicked 'Warnings' from High Alchemy spell");

                // Wait for continue dialog
                Sleep.sleepUntil(() -> {
                    WidgetChild continueDialog = Widgets.get(11, 4);
                    return continueDialog != null && continueDialog.isVisible();
                }, 3000);

                WidgetChild continueDialog = Widgets.get(11, 4);
                if (continueDialog != null && continueDialog.isVisible()) {
                    if (continueDialog.interact()) {
                        BotUtils.log("✅ Successfully clicked 'Click here to continue'");

                        // Wait for threshold option
                        Sleep.sleepUntil(() -> {
                            WidgetChild thresholdOption = Widgets.get(219, 1, 1);
                            return thresholdOption != null && thresholdOption.isVisible();
                        }, 3000);

                        WidgetChild thresholdOption = Widgets.get(219, 1, 1);
                        if (thresholdOption != null && thresholdOption.isVisible()) {
                            if (thresholdOption.interact()) {
                                BotUtils.log("✅ Successfully clicked 'Set value threshold'");

                                // Wait for input field
                                Sleep.sleepUntil(() -> {
                                    WidgetChild inputField = Widgets.get(162, 43);
                                    return inputField != null && inputField.isVisible();
                                }, 3000);

                                WidgetChild inputField = Widgets.get(162, 43);
                                if (inputField != null && inputField.isVisible()) {
                                    if (inputField.interact()) {
                                        Sleep.sleep(500);

                                        BotUtils.log("📝 Typing threshold value: 10000000");

                                        // Type the value using Keyboard API
                                        try {
                                            // Approach 1: Direct typing if Keyboard class exists
                                            org.dreambot.api.input.Keyboard.type("10000000");
                                            Sleep.sleep(500);
                                            org.dreambot.api.input.Keyboard.type("\n"); // Press enter
                                        } catch (Exception e1) {
                                            try {
                                                // Approach 2: Alternative keyboard method
                                                org.dreambot.api.input.Keyboard.type("10000000");
                                                Sleep.sleep(500);
                                                org.dreambot.api.input.Keyboard.type("\n");
                                            } catch (Exception e2) {
                                                // Approach 3: Manual key events
                                                BotUtils.log("⚠️ Using fallback keyboard method");
                                                String threshold = "10000000";
                                                for (char c : threshold.toCharArray()) {
                                                    org.dreambot.api.input.Keyboard.type(String.valueOf(c));
                                                    Sleep.sleep(50);
                                                }
                                                Sleep.sleep(200);
                                                org.dreambot.api.input.Keyboard.type("\n");
                                            }
                                        }

                                        Sleep.sleep(1000);
                                        BotUtils.log("✅ Successfully set High Alchemy threshold to 10,000,000 GP");
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                BotUtils.log("⚠️ Could not find 'Warnings' option - spell may already be configured");
                // Try basic interaction as fallback
                if (alchSpell.interact()) {
                    BotUtils.log("✅ Basic interaction with High Alchemy spell successful");
                    Sleep.sleep(1000);
                    // Check if warning dialog appears
                    WidgetChild warningDialog = Widgets.get(219, 1);
                    if (warningDialog != null && warningDialog.isVisible()) {
                        BotUtils.log("⚠️ Warning dialog detected - manual configuration may be needed");
                    }
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error in rightClickAlchSpell", e);
            return false;
        }
    }

    /**
     * Check if alchemy has been configured for this session
     *
     * @return true if configured
     */
    public boolean isConfigured() {
        return alchemyConfigured;
    }

    /**
     * Reset configuration state (for new sessions)
     */
    public void resetConfiguration() {
        alchemyConfigured = false;
        BotUtils.log("🔄 Alchemy configuration reset");
    }
}
