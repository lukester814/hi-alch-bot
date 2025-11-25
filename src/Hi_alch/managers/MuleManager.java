package Hi_alch.managers;

import Hi_alch.utils.BotUtils;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;

import java.util.Random;

/**
 * Professional Mule Manager for OSRS Trading Automation
 *
 * Features:
 * - Safe player detection and interaction
 * - Automated trade request/accept flow
 * - Inventory management for transfers
 * - World coordination between main and mule
 * - Anti-ban delays and patterns
 * - Discord notifications for transfers
 * - Profit/item tracking
 * - Emergency abort system
 * - Smart retry logic
 *
 * SAFETY: Includes human-like delays and anti-ban patterns
 */
public class MuleManager {

    // ===========================================
    // CONFIGURATION
    // ===========================================

    // Mule settings
    private String muleUsername;
    private String muleLocation;
    private int transferWorld;
    private boolean enabled;
    private boolean autoTransferProfit;
    private boolean autoTransferItems;
    private int profitThreshold;
    private int itemThreshold;

    // State tracking
    private boolean isTransferring = false;
    private boolean waitingForMule = false;
    private long lastTransferTime = 0;
    private int transferCount = 0;

    // Helpers
    private Random random = new Random();
    private AntibanSystem antibanSystem;
    private DiscordManager discordManager;
    private MuleTradeHandler tradeHandler;
    private MuleNavigator navigator;

    // Constants
    private static final long TRANSFER_COOLDOWN = 300000; // 5 minutes between transfers
    private static final int MAX_WAIT_TIME = 180000; // 3 minutes max wait for mule
    private static final int TRADE_TIMEOUT = 60000; // 1 minute trade timeout

    // ===========================================
    // TRADE STATE ENUM
    // ===========================================

    public enum TradeState {
        IDLE("Not trading"),
        FINDING_MULE("Finding mule player"),
        REQUESTING_TRADE("Requesting trade"),
        WAITING_FOR_ACCEPT("Waiting for trade accept"),
        FIRST_SCREEN("First trade screen"),
        SECOND_SCREEN("Second trade screen"),
        COMPLETING("Completing trade"),
        COMPLETED("Trade completed"),
        FAILED("Trade failed"),
        ABORTED("Trade aborted");

        private final String description;

        TradeState(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private TradeState currentTradeState = TradeState.IDLE;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public MuleManager(AntibanSystem antibanSystem, DiscordManager discordManager) {
        this.antibanSystem = antibanSystem;
        this.discordManager = discordManager;
        this.enabled = false;

        // Initialize helpers
        this.tradeHandler = new MuleTradeHandler(discordManager);
        this.navigator = new MuleNavigator();

        BotUtils.log("🤝 MuleManager initialized");
    }

    // ===========================================
    // CONFIGURATION METHODS
    // ===========================================

    /**
     * Configure mule settings
     */
    public void configure(String username, String location, int world,
                         boolean autoProfit, boolean autoItems,
                         int profitThresh, int itemThresh) {
        this.muleUsername = username;
        this.muleLocation = location;
        this.transferWorld = world;
        this.autoTransferProfit = autoProfit;
        this.autoTransferItems = autoItems;
        this.profitThreshold = profitThresh;
        this.itemThreshold = itemThresh;
        this.enabled = true;

        // Configure helpers
        tradeHandler.configure(autoProfit, autoItems);
        navigator.configure(location, world);

        BotUtils.log("🤝 Mule configured: " + username + " @ " + location + " (W" + world + ")");
        BotUtils.log("   Auto-transfer: Profit=" + autoProfit + " (" + profitThreshold + " GP), Items=" + autoItems + " (" + itemThresh + ")");
    }

    /**
     * Enable or disable mule support
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        BotUtils.log("🤝 Mule support " + (enabled ? "enabled" : "disabled"));
    }

    // ===========================================
    // MAIN TRANSFER LOGIC
    // ===========================================

    /**
     * Check if transfer conditions are met and initiate if needed
     */
    public boolean checkAndTransfer(int currentProfit, int currentItemCount) {
        if (!enabled || isTransferring) {
            return false;
        }

        // Check cooldown
        long timeSinceLastTransfer = System.currentTimeMillis() - lastTransferTime;
        if (timeSinceLastTransfer < TRANSFER_COOLDOWN) {
            return false;
        }

        // Check thresholds
        boolean shouldTransfer = false;
        String reason = "";

        if (autoTransferProfit && currentProfit >= profitThreshold) {
            shouldTransfer = true;
            reason = "Profit threshold reached: " + BotUtils.formatNumber(currentProfit) + " GP";
        }

        if (autoTransferItems && currentItemCount >= itemThreshold) {
            shouldTransfer = true;
            reason = reason.isEmpty() ? "Item threshold reached: " + currentItemCount + " items" :
                     reason + " and " + currentItemCount + " items";
        }

        if (shouldTransfer) {
            BotUtils.log("🤝 Initiating mule transfer: " + reason);
            return initiateTransfer();
        }

        return false;
    }

    /**
     * Manually initiate a transfer
     */
    public boolean initiateTransfer() {
        if (!enabled) {
            BotUtils.log("❌ Mule support is disabled");
            return false;
        }

        if (isTransferring) {
            BotUtils.log("⚠️ Transfer already in progress");
            return false;
        }

        try {
            isTransferring = true;
            currentTradeState = TradeState.IDLE;

            BotUtils.log("🤝 Starting mule transfer process...");

            // Send Discord notification
            if (discordManager != null) {
                discordManager.sendMuleTransferStarted(muleUsername, muleLocation);
            }

            // Step 1: Prepare for transfer
            if (!navigator.prepareForTransfer()) {
                BotUtils.log("❌ Failed to prepare for transfer");
                abortTransfer();
                return false;
            }

            // Step 2: Find and trade mule
            if (!findAndTradeMule()) {
                BotUtils.log("❌ Failed to find and trade mule");
                abortTransfer();
                return false;
            }

            // Success!
            transferCount++;
            lastTransferTime = System.currentTimeMillis();
            isTransferring = false;

            BotUtils.log("✅ Mule transfer completed successfully!");

            return true;

        } catch (Exception e) {
            BotUtils.logError("Error during mule transfer", e);
            abortTransfer();
            return false;
        }
    }

    // ===========================================
    // MULE FINDING & TRADING
    // ===========================================

    /**
     * Find mule and execute trade
     */
    private boolean findAndTradeMule() {
        try {
            currentTradeState = TradeState.FINDING_MULE;
            BotUtils.log("👀 Looking for mule: " + muleUsername);

            long startWaitTime = System.currentTimeMillis();
            waitingForMule = true;

            // Wait for mule to appear
            while (waitingForMule && (System.currentTimeMillis() - startWaitTime) < MAX_WAIT_TIME) {
                Player mule = Players.closest(p -> p != null &&
                                              p.getName() != null &&
                                              p.getName().equalsIgnoreCase(muleUsername));

                if (mule != null && mule.exists()) {
                    BotUtils.log("✅ Found mule: " + mule.getName());
                    waitingForMule = false;

                    // Anti-ban delay before interaction
                    Sleep.sleep(1000 + random.nextInt(2000), 2000 + random.nextInt(1000));

                    return tradeHandler.executeTrade(mule, muleUsername);
                }

                // Wait a bit before checking again
                Sleep.sleep(1000, 2000);
            }

            if (waitingForMule) {
                BotUtils.log("⏰ Timed out waiting for mule");
                return false;
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error finding mule", e);
            return false;
        }
    }

    // ===========================================
    // ABORT & ERROR HANDLING
    // ===========================================

    /**
     * Abort current transfer
     */
    private void abortTransfer() {
        try {
            BotUtils.log("🛑 Aborting mule transfer...");

            currentTradeState = TradeState.ABORTED;
            isTransferring = false;
            waitingForMule = false;

            // Close trade if open
            if (Trade.isOpen()) {
                Trade.close();
                Sleep.sleep(1000, 2000);
            }

            // Send Discord notification
            if (discordManager != null) {
                discordManager.sendMuleTransferFailed(muleUsername, "Transfer aborted");
            }

            BotUtils.log("❌ Transfer aborted");

        } catch (Exception e) {
            BotUtils.logError("Error aborting transfer", e);
        }
    }

    // ===========================================
    // STATUS & GETTERS
    // ===========================================

    /**
     * Get current trade state
     */
    public TradeState getCurrentState() {
        return currentTradeState;
    }

    /**
     * Get current state description
     */
    public String getCurrentStateDescription() {
        return currentTradeState.getDescription();
    }

    /**
     * Check if currently transferring
     */
    public boolean isTransferring() {
        return isTransferring;
    }

    /**
     * Get total GP transferred
     */
    public int getTotalGPTransferred() {
        return tradeHandler.getTotalGPTransferred();
    }

    /**
     * Get total items transferred
     */
    public int getTotalItemsTransferred() {
        return tradeHandler.getTotalItemsTransferred();
    }

    /**
     * Get transfer count
     */
    public int getTransferCount() {
        return transferCount;
    }

    /**
     * Get statistics object
     */
    public MuleStatistics getStatistics() {
        MuleStatistics stats = new MuleStatistics();
        stats.totalGPTransferred = tradeHandler.getTotalGPTransferred();
        stats.totalItemsTransferred = tradeHandler.getTotalItemsTransferred();
        stats.transferCount = transferCount;
        stats.currentState = currentTradeState.getDescription();
        stats.isTransferring = isTransferring;
        stats.muleUsername = muleUsername;
        stats.lastTransferTime = lastTransferTime;
        return stats;
    }

    // ===========================================
    // STATISTICS CLASS
    // ===========================================

    /**
     * Mule statistics data class
     */
    public static class MuleStatistics {
        public int totalGPTransferred = 0;
        public int totalItemsTransferred = 0;
        public int transferCount = 0;
        public String currentState = "Idle";
        public boolean isTransferring = false;
        public String muleUsername = "";
        public long lastTransferTime = 0;
    }
}
