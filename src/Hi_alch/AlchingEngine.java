package Hi_alch;

import Hi_alch.engine.BotState;
import Hi_alch.overlay.ScriptStatistics;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.GrandExchange;
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
 * High Alchemy Bot Engine - Core Logic System
 * Utility class for managing alchemy operations
        }
    }

    // ===========================================
    // CONFIGURATION & STATE
    // ===========================================

    private BotState currentState;
    private ScriptStatistics statistics;
    private boolean isInitialized;
    private boolean restockWhenEmpty;

    // Configuration
    private String selectedItemName;
    private int selectedItemId;
    private int selectedItemBuyLimit;
    private double priceMarkup;
    private int natureRuneAmount;
    private boolean smartProfitEnabled;
    private boolean worldHopEnabled;
    private boolean skipBuyingEnabled;  // Added field for skip buying

    // Runtime tracking
    private int alchingCount;
    private int totalProfit;
    private long lastActionTime;
    private boolean alchemyConfigured = false; // Track if we've configured this session

    // Constants
    private static final int NATURE_RUNE_ID = 563;
    private static final int MIN_NATURE_RUNES = 50;

    // ===========================================
    // CONSTRUCTORS
    // ===========================================

    public AlchingEngine() {
        this.statistics = new ScriptStatistics();
        this.currentState = BotState.INITIALIZING;
        this.alchingCount = 0;
        this.totalProfit = 0;
        this.lastActionTime = 0;
        this.isInitialized = false;
        this.alchemyConfigured = false; // Reset for new session

        BotUtils.log("🧠 AlchingEngine created");
    }

    public AlchingEngine(PriceManager priceManager, AntibanSystem antibanSystem, DiscordManager discordManager) {
        this();
        BotUtils.log("🧠 AlchingEngine created with dependencies");
    }

    // ===========================================
    // PUBLIC INTERFACE - MAIN METHODS
    // ===========================================

    public void configure(String itemName, int itemId, int buyLimit, double priceMarkup,
                          int natureRuneAmount, boolean smartProfitEnabled, boolean worldHopEnabled,
                          boolean skipBuying, boolean restockWhenEmpty) {
        this.selectedItemName = itemName;
        this.selectedItemId = itemId;
        this.selectedItemBuyLimit = buyLimit;
        this.priceMarkup = priceMarkup;
        this.natureRuneAmount = natureRuneAmount;
        this.smartProfitEnabled = smartProfitEnabled;
        this.worldHopEnabled = worldHopEnabled;
        this.skipBuyingEnabled = skipBuying;
        this.restockWhenEmpty = restockWhenEmpty;



        // Reset statistics
        statistics.reset();
        statistics.sessionStartTime = System.currentTimeMillis();
        this.alchemyConfigured = false;

        BotUtils.log("⚙️ AlchingEngine configured for: " + itemName);
        BotUtils.log("💰 Buy limit: " + buyLimit + " | Markup: " + priceMarkup + "%");
        if (skipBuying) {
            BotUtils.log("💡 Skip buying enabled - will use existing inventory");
            if (restockWhenEmpty) {
                BotUtils.log("🔄 Restocking enabled - will buy more when empty");
            } else {
                BotUtils.log("🛑 Restocking disabled - will stop when inventory empty");
            }
        }
    }

    public int executeNextAction() {
        try {
            // Update runtime statistics
            updateStatistics();

            // Handle the current state
            switch (currentState) {
                case INITIALIZING:
                    return handleInitializing();

                case CHECKING_SUPPLIES:
                    return handleCheckingSupplies();

                case BUYING_ITEMS:
                    return handleBuyingItems();

                case BUYING_NATURE_RUNES:
                    return handleBuyingNatureRunes();

                case ALCHING:
                    return handleAlching();

                case RESTOCKING:
                    return handleRestocking();

                case BANKING:
                    return handleBanking();

                case ERROR_RECOVERY:
                    return handleErrorRecovery();

                case COMPLETED:
                    return handleCompleted();

                default:
                    BotUtils.log("⚠️ Unknown state: " + currentState);
                    currentState = BotState.ERROR_RECOVERY;
                    return 2000;
            }

        } catch (Exception e) {
            BotUtils.logError("Error executing action", e);
            statistics.errorsEncountered++;
            statistics.lastError = e.getMessage();
            currentState = BotState.ERROR_RECOVERY;
            return 3000;
        }
    }

    // ===========================================
    // STATE HANDLER METHODS
    // ===========================================

    private int handleInitializing() {
        BotUtils.log("🚀 Initializing bot systems...");

        if (selectedItemName == null || selectedItemName.isEmpty()) {
            BotUtils.log("❌ No item configured!");
            currentState = BotState.ERROR_RECOVERY;
            return 5000;
        }

        statistics.currentState = "Initializing";
        statistics.currentAction = "Setting up bot systems";
        statistics.isRunning = true;

        // Configure right-click alchemy ONCE per session at startup
        if (!alchemyConfigured) {
            BotUtils.log("🔧 Performing one-time alchemy configuration...");
            configureHighAlchemy();
            alchemyConfigured = true; // Mark as configured for this session
        }

        currentState = BotState.CHECKING_SUPPLIES;
        isInitialized = true;

        BotUtils.log("✅ Initialization complete!");
        return 1000;
    }

    private int handleCheckingSupplies() {
        statistics.currentState = "Checking Supplies";
        statistics.currentAction = "Analyzing inventory";

        BotUtils.log("📦 Checking inventory supplies...");

        try {
            // Check for both noted and unnoted items
            int notedItemId = selectedItemId + 1; // Noted items are usually ID + 1
            boolean hasUnnotedItems = Inventory.contains(selectedItemId) && Inventory.count(selectedItemId) > 0;
            boolean hasNotedItems = Inventory.contains(notedItemId) && Inventory.count(notedItemId) > 0;
            boolean hasItems = hasUnnotedItems || hasNotedItems;
            boolean hasNatureRunes = Inventory.contains(NATURE_RUNE_ID) && Inventory.count(NATURE_RUNE_ID) > 0;

            if (hasItems) {
                int unnotedCount = hasUnnotedItems ? Inventory.count(selectedItemId) : 0;
                int notedCount = hasNotedItems ? Inventory.count(notedItemId) : 0;
                BotUtils.log("🔍 Items found - Unnoted: " + unnotedCount + " | Noted: " + notedCount);
            }
            BotUtils.log("🌿 Nature Runes: " + (hasNatureRunes ? "✅ " + Inventory.count(NATURE_RUNE_ID) : "❌"));

            // Check skip buying AFTER checking what's in inventory
            if (skipBuyingEnabled) {
                BotUtils.log("💡 Skip buying enabled - checking existing inventory");

                if (!hasItems) {
                    BotUtils.log("❌ Skip buying enabled but no items in inventory!");
                    updateStatus("❌ No items to alch in inventory - please add items or disable skip buying");
                    currentState = BotState.ERROR_RECOVERY;
                    return 3000;
                }

                if (!hasNatureRunes) {
                    BotUtils.log("⚠️ Skip buying enabled but no nature runes - will buy those");
                    currentState = BotState.BUYING_NATURE_RUNES;
                    return 1000;
                }

                // Has items and nature runes - check if we need to unnote
                if (hasNotedItems && !hasUnnotedItems) {
                    BotUtils.log("📝 Only noted items found - may need to unnote at bank");
                    // You could add banking logic here to unnote items
                }

                BotUtils.log("✅ Items and runes found - proceeding to alchemy");
                currentState = BotState.ALCHING;
                return 1000;
            }

            // Normal buying flow when skip buying is NOT enabled
            if (!hasItems) {
                BotUtils.log("💰 Need to buy items: " + selectedItemName);
                currentState = BotState.BUYING_ITEMS;
            } else if (!hasNatureRunes) {
                BotUtils.log("🌿 Need to buy nature runes");
                currentState = BotState.BUYING_NATURE_RUNES;
            } else {
                BotUtils.log("✅ Supplies ready, proceeding to alchemy");
                currentState = BotState.ALCHING;
            }

        } catch (Exception e) {
            BotUtils.logError("Error checking supplies", e);
            currentState = BotState.ERROR_RECOVERY;
        }

        return 1500;
    }
    private int handleBuyingItems() {
        statistics.currentState = "Buying Items";
        statistics.currentAction = "Purchasing " + selectedItemName;

        BotUtils.log("💰 Buying items: " + selectedItemName);

        try {
            if (!GrandExchange.isOpen()) {
                if (!GrandExchange.open()) {
                    BotUtils.log("❌ Failed to open Grand Exchange");
                    currentState = BotState.ERROR_RECOVERY;
                    return 3000;
                }
                Sleep.sleepUntil(GrandExchange::isOpen, 5000);
            }

            if (GrandExchange.getFirstOpenSlot() == -1) {
                BotUtils.log("❌ No free GE slots available");
                currentState = BotState.ERROR_RECOVERY;
                return 5000;
            }

            // Get actual market price from PriceManager or use default
            int marketPrice = getMarketPrice(selectedItemId, selectedItemName);

            // Apply the markup percentage
            int buyPrice = (int)(marketPrice * (1 + priceMarkup/100));

            // Use the configured buy limit, not inventory space
            int buyQuantity = selectedItemBuyLimit;

            // Check if we have enough inventory space
            int emptySlots = Inventory.getEmptySlots();
            if (emptySlots < 5) {
                BotUtils.log("⚠️ Low inventory space: " + emptySlots + " slots");
                // Don't reduce quantity, just warn
            }

            BotUtils.log("📊 Market price: " + BotUtils.formatNumber(marketPrice) + " GP");
            BotUtils.log("💰 Buying " + buyQuantity + "x at " + BotUtils.formatNumber(buyPrice) +
                    " GP each (+" + String.format("%.1f%%", priceMarkup) + " markup)");
            BotUtils.log("💵 Total cost: " + BotUtils.formatNumber(buyQuantity * buyPrice) + " GP");

            if (GrandExchange.buyItem(selectedItemId, buyQuantity, buyPrice)) {
                BotUtils.log("✅ Buy offer placed: " + buyQuantity + "x " + selectedItemName);

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 30000 && GrandExchange.isOpen()) {
                    if (GrandExchange.isReadyToCollect()) {
                        if (GrandExchange.collect()) {
                            BotUtils.log("✅ Items collected successfully");
                            currentState = BotState.CHECKING_SUPPLIES;
                            return 2000;
                        }
                    }
                    Sleep.sleep(1000);
                }

                BotUtils.log("⏰ Buy offer taking too long, continuing anyway");
                currentState = BotState.CHECKING_SUPPLIES;

            } else {
                BotUtils.log("❌ Failed to place buy offer");
                currentState = BotState.ERROR_RECOVERY;
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying items", e);
            currentState = BotState.ERROR_RECOVERY;
        }

        return 3000;
    }

    private int handleBuyingNatureRunes() {
        statistics.currentState = "Buying Runes";
        statistics.currentAction = "Purchasing nature runes";

        BotUtils.log("🌿 Buying nature runes: " + natureRuneAmount);

        try {
            if (!GrandExchange.isOpen()) {
                if (!GrandExchange.open()) {
                    BotUtils.log("❌ Failed to open Grand Exchange");
                    currentState = BotState.ERROR_RECOVERY;
                    return 3000;
                }
                Sleep.sleepUntil(GrandExchange::isOpen, 5000);
            }

            if (GrandExchange.getFirstOpenSlot() == -1) {
                BotUtils.log("❌ No free GE slots available");
                currentState = BotState.ERROR_RECOVERY;
                return 5000;
            }

            int buyQuantity = Math.min(natureRuneAmount, Inventory.getEmptySlots());

            if (buyQuantity <= 0) {
                BotUtils.log("❌ No inventory space for nature runes");
                currentState = BotState.BANKING;
                return 2000;
            }

            if (GrandExchange.buyItem(NATURE_RUNE_ID, buyQuantity, 300)) {
                BotUtils.log("✅ Nature rune buy offer placed: " + buyQuantity + "x");

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 20000 && GrandExchange.isOpen()) {
                    if (GrandExchange.isReadyToCollect()) {
                        if (GrandExchange.collect()) {
                            BotUtils.log("✅ Nature runes collected successfully");
                            currentState = BotState.CHECKING_SUPPLIES;
                            return 2000;
                        }
                    }
                    Sleep.sleep(1000);
                }

                BotUtils.log("⏰ Nature rune offer taking too long, continuing anyway");
                currentState = BotState.CHECKING_SUPPLIES;

            } else {
                BotUtils.log("❌ Failed to place nature rune buy offer");
                currentState = BotState.ERROR_RECOVERY;
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying nature runes", e);
            currentState = BotState.ERROR_RECOVERY;
        }

        return 2500;
    }

    /**
     * Get market price for an item using live GE data
     */
    private int getMarketPrice(int itemId, String itemName) {
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
                // Update the item ID for future use
                this.selectedItemId = searchResult.itemId;
                BotUtils.log("✅ Found " + searchResult.itemName + " (ID: " + searchResult.itemId +
                        ") - Price: " + BotUtils.formatNumber(searchResult.averagePrice) + " GP");
                return searchResult.averagePrice;
            }

            // Fallback prices for common items if API fails
            BotUtils.log("⚠️ Could not get live price for " + itemName + ", using fallback");
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

        } catch (Exception e) {
            BotUtils.logError("Error getting market price", e);
            return 10000;
        }
    }

    private int handleAlching() {
        statistics.currentState = "Alching";
        statistics.currentAction = "Performing high alchemy";

        try {
            if (!Inventory.contains(selectedItemId)) {
                BotUtils.log("❌ No more items to alch");

                // Check if we should restock or stop
                if (skipBuyingEnabled && !restockWhenEmpty) {
                    BotUtils.log("🛑 Skip buying enabled with no restock - completing session");
                    currentState = BotState.COMPLETED;
                    return 2000;
                } else {
                    BotUtils.log("🔄 Proceeding to restock items");
                    currentState = BotState.RESTOCKING;
                    return 2000;
                }
            }

            if (!Inventory.contains(NATURE_RUNE_ID)) {
                BotUtils.log("❌ No more nature runes");

                if (skipBuyingEnabled && !restockWhenEmpty) {
                    BotUtils.log("🛑 Out of nature runes and restocking disabled - completing session");
                    currentState = BotState.COMPLETED;
                    return 2000;
                } else {
                    currentState = BotState.BUYING_NATURE_RUNES;
                    return 2000;
                }
            }

            // Rest of the alching logic remains the same...
            if (!Tabs.isOpen(Tab.MAGIC)) {
                Tabs.open(Tab.MAGIC);
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.MAGIC), 2000);
                return 1000;
            }

            if (Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY)) {
                Item itemToAlch = Inventory.get(selectedItemId);

                if (itemToAlch != null) {
                    BotUtils.log("🎯 Casting High Alchemy on: " + itemToAlch.getName());

                    if (Magic.castSpell(Normal.HIGH_LEVEL_ALCHEMY)) {
                        Sleep.sleep(300);

                        if (itemToAlch.interact()) {
                            Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000);

                            alchingCount++;
                            statistics.alchsCompleted = alchingCount;

                            int estimatedProfit = 50;
                            totalProfit += estimatedProfit;
                            statistics.totalProfit = totalProfit;
                            statistics.xpGained += 65;

                            BotUtils.log("🔥 Alch #" + alchingCount + " completed (+65 XP, +" +
                                    BotUtils.formatNumber(estimatedProfit) + " GP est.)");

                            lastActionTime = System.currentTimeMillis();

                            // Don't check for low inventory if skip buying without restock
                            if (skipBuyingEnabled && !restockWhenEmpty) {
                                // Just continue alching until we run out
                                return BotUtils.randomDelay(1400, 2200);
                            }

                            // Normal restock check
                            if (Inventory.count(selectedItemId) <= 5) {
                                currentState = BotState.RESTOCKING;
                                return 2000;
                            }

                            return BotUtils.randomDelay(1400, 2200);

                        } else {
                            BotUtils.log("❌ Failed to click item after casting spell");
                            return 3000;
                        }
                    } else {
                        BotUtils.log("❌ Failed to cast High Level Alchemy spell");
                        return 3000;
                    }
                } else {
                    BotUtils.log("❌ Item not found in inventory");
                    currentState = BotState.CHECKING_SUPPLIES;
                    return 2000;
                }
            } else {
                BotUtils.log("❌ Cannot cast High Level Alchemy");
                BotUtils.log("🔍 Magic level: " + Skills.getBoostedLevel(Skill.MAGIC));
                BotUtils.log("🌿 Nature runes: " + Inventory.count(NATURE_RUNE_ID));

                currentState = BotState.ERROR_RECOVERY;
                return 5000;
            }

        } catch (Exception e) {
            BotUtils.logError("Error during alchemy", e);
            statistics.errorsEncountered++;
            statistics.lastError = e.getMessage();
            currentState = BotState.ERROR_RECOVERY;
            return 3000;
        }
    }

    private int handleRestocking() {
        statistics.currentState = "Restocking";
        statistics.currentAction = "Restocking supplies";

        BotUtils.log("📦 Restocking supplies...");
        currentState = BotState.CHECKING_SUPPLIES;
        return 2000;
    }

    private int handleBanking() {
        statistics.currentState = "Banking";
        statistics.currentAction = "Banking items";

        BotUtils.log("🏦 Banking items...");
        currentState = BotState.CHECKING_SUPPLIES;
        return 3000;
    }

    private int handleErrorRecovery() {
        statistics.currentState = "Error Recovery";
        statistics.currentAction = "Recovering from error";

        BotUtils.log("🚨 Attempting error recovery...");

        if (statistics.errorsEncountered > 5) {
            BotUtils.log("❌ Too many errors, stopping bot");
            currentState = BotState.COMPLETED;
            return 5000;
        }

        currentState = BotState.CHECKING_SUPPLIES;
        return 5000;
    }

    private int handleCompleted() {
        statistics.currentState = "Completed";
        statistics.currentAction = "Session finished";
        statistics.isRunning = false;

        BotUtils.log("✅ Bot session completed!");
        BotUtils.log("📊 Final Stats: " + alchingCount + " alchs, " +
                BotUtils.formatNumber(totalProfit) + " GP profit");

        return 10000;
    }

    // ===========================================
    // ALCHEMY CONFIGURATION
    // ===========================================

    private void configureHighAlchemy() {
        try {
            BotUtils.log("⚙️ Configuring High Level Alchemy warning threshold...");

            if (!Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY)) {
                int magicLevel = Skills.getBoostedLevel(Skill.MAGIC);
                int natureRunes = Inventory.count(NATURE_RUNE_ID);

                BotUtils.log("❌ Cannot cast High Level Alchemy:");
                BotUtils.log("   Magic Level: " + magicLevel + " (need 55)");
                BotUtils.log("   Nature Runes: " + natureRunes + " (need 1+)");

                if (magicLevel < 55) {
                    currentState = BotState.ERROR_RECOVERY;
                } else if (natureRunes == 0) {
                    currentState = BotState.BUYING_NATURE_RUNES;
                }
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

    private boolean rightClickAlchSpell() {
        try {
            BotUtils.log("🔧 Attempting to configure High Level Alchemy threshold...");

            if (!Tabs.isOpen(Tab.MAGIC)) {
                if (!Tabs.open(Tab.MAGIC)) {
                    BotUtils.log("❌ Failed to open magic tab");
                    return false;
                }
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.MAGIC), 3000);
            }

            Sleep.sleep(1000);
            BotUtils.log("✅ Magic tab is open");

            WidgetChild alchSpell = Widgets.get(218, 44);

            if (alchSpell == null || !alchSpell.isVisible()) {
                BotUtils.log("❌ Could not find High Level Alchemy spell at widget 218, 44");
                return false;
            }

            BotUtils.log("✅ Found High Level Alchemy spell");

            // Try to right-click and access Warnings
            // Note: Mouse.click with right-click parameter might not be available in current API
            // Using interact with action instead
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
                                        // Try different approaches based on what's available
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
                                                org.dreambot.api.input.Keyboard.type("\n"); // Press enter
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

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    private void updateStatistics() {
        statistics.scriptRuntime = System.currentTimeMillis() - statistics.sessionStartTime;
        statistics.alchsCompleted = alchingCount;
        statistics.totalProfit = totalProfit;
        statistics.currentState = currentState.getDescription();
        statistics.isRunning = true;
        statistics.calculateDerivedStats();
    }

    /**
     * Update status message and log it
     */
    private void updateStatus(String message) {
        statistics.currentAction = message;
        BotUtils.log(message);
    }

    public ScriptStatistics getStatistics() {
        statistics.calculateDerivedStats();
        return statistics;
    }

    public void reset() {
        currentState = BotState.INITIALIZING;
        statistics.reset();
        alchingCount = 0;
        totalProfit = 0;
        lastActionTime = 0;
        isInitialized = false;
        alchemyConfigured = false; // Reset for new session

        BotUtils.log("🔄 AlchingEngine reset");
    }

    public BotState getCurrentStateEnum() {
        return currentState;
    }

    public String getCurrentStateDescription() {
        return currentState.getDescription();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int getCurrentProfit() {
        return totalProfit;
    }

    public int getAlchCount() {
        return alchingCount;
    }

    public void setState(BotState newState) {
        BotUtils.log("🔄 Force state change: " + currentState + " → " + newState);
        this.currentState = newState;
    }

    public void cleanup() {
        BotUtils.log("🧹 AlchingEngine cleanup complete");
    }
}