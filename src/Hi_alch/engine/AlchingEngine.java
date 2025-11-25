package Hi_alch.engine;

import Hi_alch.utils.BotUtils;
import Hi_alch.managers.PriceManager;
import Hi_alch.managers.AntibanSystem;
import Hi_alch.managers.DiscordManager;
import Hi_alch.overlay.OverlayRenderer;
import Hi_alch.overlay.ScriptStatistics;
import org.dreambot.api.methods.container.impl.Inventory;

/**
 * High Alchemy Bot Engine - Core Logic System (Refactored)
 * Delegates to specialized component classes for cleaner code
 */
public class AlchingEngine {

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
    private boolean skipBuyingEnabled;

    // Runtime tracking
    private int alchingCount;
    private int totalProfit;
    private boolean alchemyConfigured = false;

    // Constants
    private static final int NATURE_RUNE_ID = 563;

    // ===========================================
    // CONSTRUCTORS
    // ===========================================

    public AlchingEngine() {
        this.statistics = new ScriptStatistics();
        this.currentState = BotState.INITIALIZING;
        this.alchingCount = 0;
        this.totalProfit = 0;
        this.isInitialized = false;
        this.alchemyConfigured = false;

        BotUtils.log("🧠 AlchingEngine created (Refactored)");
    }

    public AlchingEngine(PriceManager priceManager, AntibanSystem antibanSystem, DiscordManager discordManager) {
        this();
        BotUtils.log("🧠 AlchingEngine created with dependencies (Refactored)");
    }

    // ===========================================
    // PUBLIC INTERFACE
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
            updateStatistics();

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
    // STATE HANDLERS
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

        // Configure alchemy once per session
        if (!alchemyConfigured) {
            BotUtils.log("🔧 Performing one-time alchemy configuration...");
            AlchemyConfigurator.configure();
            alchemyConfigured = true;
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
            boolean hasItems = SupplyManager.hasItem(selectedItemId);
            boolean hasNatureRunes = SupplyManager.hasNatureRunes();

            if (hasItems) {
                BotUtils.log("🔍 Items found in inventory");
            }
            BotUtils.log("🌿 Nature Runes: " + (hasNatureRunes ? "✅ " + SupplyManager.getNatureRuneCount() : "❌"));

            if (skipBuyingEnabled) {
                BotUtils.log("💡 Skip buying enabled - checking existing inventory");

                if (!hasItems) {
                    BotUtils.log("❌ Skip buying enabled but no items in inventory!");
                    statistics.currentAction = "❌ No items to alch in inventory";
                    currentState = BotState.ERROR_RECOVERY;
                    return 3000;
                }

                if (!hasNatureRunes) {
                    BotUtils.log("⚠️ Skip buying enabled but no nature runes - will buy those");
                    currentState = BotState.BUYING_NATURE_RUNES;
                    return 1000;
                }

                BotUtils.log("✅ Items and runes found - proceeding to alchemy");
                currentState = BotState.ALCHING;
                return 1000;
            }

            // Normal buying flow
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
            int marketPrice = SupplyManager.getMarketPrice(selectedItemId, selectedItemName);
            int buyPrice = (int)(marketPrice * (1 + priceMarkup/100));
            int buyQuantity = selectedItemBuyLimit;

            BotUtils.log("📊 Market price: " + BotUtils.formatNumber(marketPrice) + " GP");
            BotUtils.log("💵 Total cost: " + BotUtils.formatNumber(buyQuantity * buyPrice) + " GP");

            boolean success = SupplyManager.buyItems(selectedItemId, selectedItemName, buyQuantity, buyPrice);

            if (success) {
                currentState = BotState.CHECKING_SUPPLIES;
                return 2000;
            } else {
                currentState = BotState.ERROR_RECOVERY;
                return 3000;
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying items", e);
            currentState = BotState.ERROR_RECOVERY;
            return 3000;
        }
    }

    private int handleBuyingNatureRunes() {
        statistics.currentState = "Buying Runes";
        statistics.currentAction = "Purchasing nature runes";

        BotUtils.log("🌿 Buying nature runes: " + natureRuneAmount);

        try {
            boolean success = SupplyManager.buyNatureRunes(natureRuneAmount);

            if (success) {
                currentState = BotState.CHECKING_SUPPLIES;
                return 2000;
            } else {
                currentState = BotState.ERROR_RECOVERY;
                return 2500;
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying nature runes", e);
            currentState = BotState.ERROR_RECOVERY;
            return 2500;
        }
    }

    private int handleAlching() {
        statistics.currentState = "Alching";
        statistics.currentAction = "Performing high alchemy";

        try {
            if (!Inventory.contains(selectedItemId)) {
                BotUtils.log("❌ No more items to alch");

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

            if (!SupplyManager.hasNatureRunes()) {
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

            // Perform alchemy using the executor
            boolean success = AlchemyExecutor.performAlchemy(selectedItemId, selectedItemName);

            if (success) {
                alchingCount++;
                statistics.alchsCompleted = alchingCount;

                int estimatedProfit = 50;
                totalProfit += estimatedProfit;
                statistics.totalProfit = totalProfit;
                statistics.xpGained += 65;

                BotUtils.log("🔥 Alch #" + alchingCount + " completed (+65 XP, +" +
                        BotUtils.formatNumber(estimatedProfit) + " GP est.)");

                // Check if we need to restock
                if (!skipBuyingEnabled || restockWhenEmpty) {
                    if (Inventory.count(selectedItemId) <= 5) {
                        currentState = BotState.RESTOCKING;
                        return 2000;
                    }
                }

                return AlchemyExecutor.getAlchDelay();
            } else {
                BotUtils.log("❌ Alchemy failed");
                return 3000;
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

    public ScriptStatistics getStatistics() {
        statistics.calculateDerivedStats();
        return statistics;
    }

    public void reset() {
        currentState = BotState.INITIALIZING;
        statistics.reset();
        alchingCount = 0;
        totalProfit = 0;
        isInitialized = false;
        alchemyConfigured = false;

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
