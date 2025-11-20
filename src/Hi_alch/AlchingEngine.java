package Hi_alch;

import Hi_alch.engine.AlchemyHandler;
import Hi_alch.engine.BankingHandler;
import Hi_alch.engine.GrandExchangeHandler;
import Hi_alch.engine.SupplyChecker;

/**
 * High Alchemy Bot Engine - Core Logic System
 * Utility class for managing alchemy operations
 */
public class AlchingEngine {

    // ===========================================
    // BOT STATES ENUM
    // ===========================================

    public enum BotState {
        INITIALIZING("Initializing bot systems"),
        CHECKING_SUPPLIES("Checking inventory supplies"),
        BUYING_ITEMS("Purchasing items from GE"),
        BUYING_NATURE_RUNES("Buying nature runes"),
        ALCHING("Performing high alchemy"),
        RESTOCKING("Restocking items"),
        BANKING("Banking items"),
        ERROR_RECOVERY("Recovering from error"),
        COMPLETED("Session completed");

        private final String description;

        BotState(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ===========================================
    // CONFIGURATION & STATE
    // ===========================================

    private BotState currentState;
    private OverlayRenderer.ScriptStatistics statistics;
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
    private long lastActionTime;
    private boolean alchemyConfigured = false;

    // Constants
    private static final int NATURE_RUNE_ID = 563;
    private static final int MIN_NATURE_RUNES = 50;

    // Handler instances
    private SupplyChecker supplyChecker;
    private BankingHandler bankingHandler;
    private GrandExchangeHandler geHandler;
    private AlchemyHandler alchemyHandler;

    // ===========================================
    // CONSTRUCTORS
    // ===========================================

    public AlchingEngine() {
        this.statistics = new OverlayRenderer.ScriptStatistics();
        this.currentState = BotState.INITIALIZING;
        this.alchingCount = 0;
        this.totalProfit = 0;
        this.lastActionTime = 0;
        this.isInitialized = false;
        this.alchemyConfigured = false;

        // Initialize handlers
        this.supplyChecker = new SupplyChecker();
        this.bankingHandler = new BankingHandler(statistics);
        this.geHandler = new GrandExchangeHandler(statistics);
        this.alchemyHandler = new AlchemyHandler(statistics);

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
            alchemyHandler.initializeAlchemy();
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

        try {
            // Use SupplyChecker to analyze inventory
            SupplyChecker.SupplyStatus status = supplyChecker.checkSupplies(
                selectedItemId,
                skipBuyingEnabled,
                restockWhenEmpty
            );

            // Map SupplyChecker.NextAction to BotState
            switch (status.recommendedAction) {
                case START_ALCHING:
                    BotUtils.log("✅ " + status.message);
                    currentState = BotState.ALCHING;
                    break;

                case BUY_ITEMS:
                    BotUtils.log("💰 " + status.message);
                    currentState = BotState.BUYING_ITEMS;
                    break;

                case BUY_NATURE_RUNES:
                    BotUtils.log("🌿 " + status.message);
                    currentState = BotState.BUYING_NATURE_RUNES;
                    break;

                case UNNOTE_ITEMS:
                    BotUtils.log("📝 " + status.message);
                    currentState = BotState.BANKING;
                    break;

                case ERROR_NO_SUPPLIES:
                    BotUtils.log("❌ " + status.message);
                    updateStatus("❌ " + status.message);
                    currentState = BotState.ERROR_RECOVERY;
                    break;

                default:
                    BotUtils.log("⚠️ Unknown supply check result");
                    currentState = BotState.ERROR_RECOVERY;
                    break;
            }

        } catch (Exception e) {
            BotUtils.logError("Error checking supplies", e);
            currentState = BotState.ERROR_RECOVERY;
        }

        return 1500;
    }

    private int handleBuyingItems() {
        try {
            // Use GrandExchangeHandler to buy items
            GrandExchangeHandler.BuyResult result = geHandler.buyItems(
                selectedItemId,
                selectedItemName,
                selectedItemBuyLimit,
                priceMarkup
            );

            if (result.success) {
                BotUtils.log("✅ " + result.message);
                // Map GE handler next action to bot state
                currentState = mapGENextActionToState(result.nextAction);
                return 2000;
            } else {
                BotUtils.log("❌ " + result.message);
                currentState = mapGENextActionToState(result.nextAction);
                return 3000;
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying items", e);
            currentState = BotState.ERROR_RECOVERY;
            return 3000;
        }
    }

    private int handleBuyingNatureRunes() {
        try {
            // Use GrandExchangeHandler to buy nature runes
            GrandExchangeHandler.BuyResult result = geHandler.buyNatureRunes(natureRuneAmount);

            if (result.success) {
                BotUtils.log("✅ " + result.message);
                currentState = mapGENextActionToState(result.nextAction);
                return 2000;
            } else {
                BotUtils.log("❌ " + result.message);
                currentState = mapGENextActionToState(result.nextAction);
                return 2500;
            }

        } catch (Exception e) {
            BotUtils.logError("Error buying nature runes", e);
            currentState = BotState.ERROR_RECOVERY;
            return 2500;
        }
    }

    private int handleAlching() {
        try {
            // Use AlchemyHandler to perform alchemy
            AlchemyHandler.AlchResult result = alchemyHandler.performAlchemy(
                selectedItemId,
                selectedItemName,
                skipBuyingEnabled,
                restockWhenEmpty
            );

            if (result.success) {
                // Update local tracking (statistics already updated by handler)
                alchingCount = result.alchCount;
                totalProfit = statistics.totalProfit;
                lastActionTime = System.currentTimeMillis();

                // Map alchemy next action to bot state
                currentState = mapAlchemyNextActionToState(result.nextAction);

                // Return appropriate delay
                if (result.nextAction == AlchemyHandler.NextAction.CONTINUE_ALCHING) {
                    return BotUtils.randomDelay(1400, 2200);
                } else {
                    return 2000;
                }

            } else {
                // Handle alchemy failure
                currentState = mapAlchemyNextActionToState(result.nextAction);
                return 2000;
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
        try {
            // Use BankingHandler to perform banking
            BankingHandler.BankResult result = bankingHandler.performBanking();

            if (result.success) {
                BotUtils.log("✅ " + result.message);
                currentState = mapBankingNextActionToState(result.nextAction);
            } else {
                BotUtils.log("❌ " + result.message);
                currentState = mapBankingNextActionToState(result.nextAction);
            }

        } catch (Exception e) {
            BotUtils.logError("Error during banking", e);
            currentState = BotState.ERROR_RECOVERY;
        }

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
    // MAPPING METHODS
    // ===========================================

    /**
     * Map GrandExchangeHandler.NextAction to BotState
     */
    private BotState mapGENextActionToState(GrandExchangeHandler.NextAction nextAction) {
        switch (nextAction) {
            case CHECK_SUPPLIES:
                return BotState.CHECKING_SUPPLIES;
            case ERROR_RECOVERY:
                return BotState.ERROR_RECOVERY;
            default:
                return BotState.ERROR_RECOVERY;
        }
    }

    /**
     * Map AlchemyHandler.NextAction to BotState
     */
    private BotState mapAlchemyNextActionToState(AlchemyHandler.NextAction nextAction) {
        switch (nextAction) {
            case CONTINUE_ALCHING:
                return BotState.ALCHING;
            case RESTOCK_ITEMS:
                return BotState.RESTOCKING;
            case BUY_NATURE_RUNES:
                return BotState.BUYING_NATURE_RUNES;
            case COMPLETE_SESSION:
                return BotState.COMPLETED;
            case ERROR_RECOVERY:
                return BotState.ERROR_RECOVERY;
            default:
                return BotState.ERROR_RECOVERY;
        }
    }

    /**
     * Map BankingHandler.NextAction to BotState
     */
    private BotState mapBankingNextActionToState(BankingHandler.NextAction nextAction) {
        switch (nextAction) {
            case CHECK_SUPPLIES:
                return BotState.CHECKING_SUPPLIES;
            case ERROR_RECOVERY:
                return BotState.ERROR_RECOVERY;
            default:
                return BotState.ERROR_RECOVERY;
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

    public OverlayRenderer.ScriptStatistics getStatistics() {
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
        alchemyConfigured = false;

        // Reset handler configurations
        if (alchemyHandler != null) {
            alchemyHandler.resetConfiguration();
        }

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
