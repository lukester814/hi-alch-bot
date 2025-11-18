package Hi_alch.mule;

import Hi_alch.core.Logger;
import Hi_alch.core.ExceptionHandler;
import Hi_alch.core.Constants;
import Hi_alch.util.DiscordWebhookUtil;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;

/**
 * Mule Manager - Professional Mule Support System
 *
 * Handles automatic GP/item muling to a designated account:
 * - Detects when muling is needed based on GP threshold
 * - Manages trading with mule account
 * - Handles world hopping to mule world
 * - Discord notifications for mule requests
 * - Safety checks and error recovery
 *
 * IMPORTANT: Both accounts must be manually controlled or on separate
 * DreamBot instances. This manager handles the main bot's side of muling.
 */
public class MuleManager {

    // ===========================================
    // CONFIGURATION
    // ===========================================

    private MuleConfiguration config;
    private MuleState currentState;
    private long lastMuleTime;
    private int totalMuledGP;
    private int muleAttempts;

    // ===========================================
    // MULE STATE
    // ===========================================

    public enum MuleState {
        IDLE("Idle", "Not muling"),
        CHECKING("Checking", "Checking if mule needed"),
        PREPARING("Preparing", "Preparing for mule"),
        HOPPING("Hopping", "Hopping to mule world"),
        FINDING_MULE("Finding Mule", "Looking for mule account"),
        TRADING("Trading", "Trading with mule"),
        COMPLETED("Completed", "Mule completed"),
        FAILED("Failed", "Mule failed");

        private final String name;
        private final String description;

        MuleState(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public MuleManager() {
        this.currentState = MuleState.IDLE;
        this.lastMuleTime = 0;
        this.totalMuledGP = 0;
        this.muleAttempts = 0;

        Logger.info(Logger.Category.GENERAL, "💰 MuleManager initialized");
    }

    public MuleManager(MuleConfiguration config) {
        this();
        this.config = config;
        Logger.logConfig("Mule Account", config.muleUsername);
        Logger.logConfig("Mule Threshold", formatGP(config.muleThresholdGP));
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    /**
     * Configure mule settings
     */
    public void configure(MuleConfiguration config) {
        this.config = config;
        Logger.info(Logger.Category.GENERAL, "⚙️ MuleManager configured");
        Logger.logConfig("Mule Username", config.muleUsername);
        Logger.logConfig("GP Threshold", formatGP(config.muleThresholdGP));
        Logger.logConfig("Mule World", String.valueOf(config.muleWorld));
        Logger.logConfig("Mule Location", config.muleLocation);
    }

    /**
     * Check if muling is configured
     */
    public boolean isConfigured() {
        return config != null &&
               config.enabled &&
               config.muleUsername != null &&
               !config.muleUsername.trim().isEmpty() &&
               config.muleThresholdGP > 0;
    }

    // ===========================================
    // MULE CHECKING
    // ===========================================

    /**
     * Check if muling is needed
     */
    public boolean isMuleNeeded() {
        if (!isConfigured()) {
            return false;
        }

        currentState = MuleState.CHECKING;

        try {
            // Check GP in inventory
            int inventoryGP = Inventory.count(Constants.COINS_ID);

            // Check GP in bank if accessible
            int bankGP = 0;
            if (Bank.isOpen()) {
                bankGP = Bank.count(Constants.COINS_ID);
            }

            int totalGP = inventoryGP + bankGP;

            Logger.debug(Logger.Category.GENERAL, "💰 Total GP: " + formatGP(totalGP) +
                " (Threshold: " + formatGP(config.muleThresholdGP) + ")");

            if (totalGP >= config.muleThresholdGP) {
                Logger.warn(Logger.Category.GENERAL, "⚠️ Mule threshold reached! GP: " + formatGP(totalGP));
                currentState = MuleState.PREPARING;
                return true;
            }

            currentState = MuleState.IDLE;
            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.LOW, "Checking mule status");
            currentState = MuleState.IDLE;
            return false;
        }
    }

    /**
     * Get current GP count
     */
    public int getCurrentGP() {
        try {
            int inventoryGP = Inventory.count(Constants.COINS_ID);
            int bankGP = 0;

            if (Bank.isOpen()) {
                bankGP = Bank.count(Constants.COINS_ID);
            }

            return inventoryGP + bankGP;

        } catch (Exception e) {
            return 0;
        }
    }

    // ===========================================
    // MULE EXECUTION
    // ===========================================

    /**
     * Execute mule operation
     */
    public boolean executeMule() {
        if (!isConfigured()) {
            Logger.error(Logger.Category.GENERAL, "❌ Cannot mule - not configured");
            return false;
        }

        Logger.info(Logger.Category.GENERAL, "💰 Starting mule operation...");
        muleAttempts++;

        try {
            // Step 1: Prepare for mule
            if (!prepareMule()) {
                currentState = MuleState.FAILED;
                return false;
            }

            // Step 2: Hop to mule world
            if (!hopToMuleWorld()) {
                currentState = MuleState.FAILED;
                return false;
            }

            // Step 3: Find mule
            if (!findMule()) {
                currentState = MuleState.FAILED;
                return false;
            }

            // Step 4: Trade mule
            if (!tradeMule()) {
                currentState = MuleState.FAILED;
                return false;
            }

            // Success
            currentState = MuleState.COMPLETED;
            lastMuleTime = System.currentTimeMillis();

            Logger.info(Logger.Category.GENERAL, "✅ Mule operation completed successfully!");

            // Send Discord notification
            if (config.discordWebhookUrl != null && !config.discordWebhookUrl.isEmpty()) {
                int gpTransferred = getCurrentGP(); // This should now be lower
                DiscordWebhookUtil.sendMuleCompleteNotification(config.discordWebhookUrl, gpTransferred, true);
            }

            return true;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.HIGH, "Mule operation");
            currentState = MuleState.FAILED;

            // Send failure notification
            if (config.discordWebhookUrl != null && !config.discordWebhookUrl.isEmpty()) {
                DiscordWebhookUtil.sendMuleCompleteNotification(config.discordWebhookUrl, 0, false);
            }

            return false;
        }
    }

    /**
     * Prepare for muling (withdraw GP, go to location, etc.)
     */
    private boolean prepareMule() {
        currentState = MuleState.PREPARING;
        Logger.info(Logger.Category.GENERAL, "📦 Preparing for mule...");

        try {
            // Close any open interfaces
            if (Trade.isOpen()) {
                Trade.decline();
                Sleep.sleep(1000);
            }

            // Withdraw all GP from bank if needed
            if (Bank.isOpen()) {
                if (Bank.contains(Constants.COINS_ID)) {
                    Logger.info(Logger.Category.GENERAL, "💰 Withdrawing GP from bank...");
                    Bank.withdrawAll(Constants.COINS_ID);
                    Sleep.sleepUntil(() -> Inventory.contains(Constants.COINS_ID), 5000);
                }
                Bank.close();
                Sleep.sleepUntil(() -> !Bank.isOpen(), 3000);
            }

            // TODO: Walk to mule location if specified
            // This would require additional navigation utilities

            Logger.info(Logger.Category.GENERAL, "✅ Mule preparation complete");
            return true;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.MEDIUM, "Preparing for mule");
            return false;
        }
    }

    /**
     * Hop to mule world
     */
    private boolean hopToMuleWorld() {
        currentState = MuleState.HOPPING;
        Logger.info(Logger.Category.GENERAL, "🌍 Hopping to mule world " + config.muleWorld + "...");

        try {
            int currentWorld = Worlds.getCurrentWorld();

            if (currentWorld == config.muleWorld) {
                Logger.debug(Logger.Category.GENERAL, "Already on mule world");
                return true;
            }

            // Hop to mule world
            if (Worlds.hopToWorld(config.muleWorld)) {
                Logger.info(Logger.Category.GENERAL, "✅ Successfully hopped to world " + config.muleWorld);

                // Wait for world hop to complete
                Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == config.muleWorld, 10000);

                // Additional delay for world loading
                Sleep.sleep(3000, 5000);

                return true;
            } else {
                Logger.error(Logger.Category.GENERAL, "❌ Failed to hop to world " + config.muleWorld);
                return false;
            }

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.MEDIUM, "Hopping to mule world");
            return false;
        }
    }

    /**
     * Find mule player
     */
    private boolean findMule() {
        currentState = MuleState.FINDING_MULE;
        Logger.info(Logger.Category.GENERAL, "🔍 Looking for mule: " + config.muleUsername);

        try {
            long startTime = System.currentTimeMillis();
            long timeout = 60000; // 1 minute timeout

            // Send Discord notification for mule request
            if (config.discordWebhookUrl != null && !config.discordWebhookUrl.isEmpty()) {
                int gpAmount = getCurrentGP();
                DiscordWebhookUtil.sendMuleRequestNotification(config.discordWebhookUrl,
                    gpAmount, config.muleLocation);
            }

            while (System.currentTimeMillis() - startTime < timeout) {
                // Check if mule is nearby
                Player mule = Players.closest(player ->
                    player != null &&
                    player.getName() != null &&
                    player.getName().equalsIgnoreCase(config.muleUsername)
                );

                if (mule != null) {
                    Logger.info(Logger.Category.GENERAL, "✅ Found mule: " + mule.getName());
                    return true;
                }

                // Wait and check again
                Sleep.sleep(2000);

                if ((System.currentTimeMillis() - startTime) % 15000 == 0) {
                    Logger.info(Logger.Category.GENERAL, "⏳ Still waiting for mule...");
                }
            }

            Logger.error(Logger.Category.GENERAL, "❌ Mule not found after timeout");
            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.MEDIUM, "Finding mule player");
            return false;
        }
    }

    /**
     * Trade with mule
     */
    private boolean tradeMule() {
        currentState = MuleState.TRADING;
        Logger.info(Logger.Category.GENERAL, "🤝 Initiating trade with mule...");

        try {
            // Find mule player
            Player mule = Players.closest(player ->
                player != null &&
                player.getName() != null &&
                player.getName().equalsIgnoreCase(config.muleUsername)
            );

            if (mule == null) {
                Logger.error(Logger.Category.GENERAL, "❌ Mule not found for trading");
                return false;
            }

            // Initiate trade
            if (!Trade.isOpen()) {
                Logger.info(Logger.Category.GENERAL, "📤 Requesting trade with " + mule.getName());

                if (mule.interact("Trade with")) {
                    Sleep.sleepUntil(() -> Trade.isOpen(), 10000);
                } else {
                    Logger.error(Logger.Category.GENERAL, "❌ Failed to initiate trade");
                    return false;
                }
            }

            if (!Trade.isOpen()) {
                Logger.error(Logger.Category.GENERAL, "❌ Trade did not open");
                return false;
            }

            // Offer all GP
            int gpAmount = Inventory.count(Constants.COINS_ID);
            Logger.info(Logger.Category.GENERAL, "💰 Offering " + formatGP(gpAmount) + " GP");

            if (Trade.contains(Constants.COINS_ID)) {
                // Already offered
            } else {
                if (!Trade.offer(Constants.COINS_ID, gpAmount)) {
                    Logger.error(Logger.Category.GENERAL, "❌ Failed to offer GP");
                    return false;
                }

                Sleep.sleep(1000, 2000);
            }

            // Wait for mule to accept first screen
            Logger.info(Logger.Category.GENERAL, "⏳ Waiting for mule to accept...");

            if (!Sleep.sleepUntil(() -> Trade.hasOtherAccepted(), 30000)) {
                Logger.error(Logger.Category.GENERAL, "❌ Mule did not accept trade");
                Trade.decline();
                return false;
            }

            // Accept first screen
            if (!Trade.accept()) {
                Logger.error(Logger.Category.GENERAL, "❌ Failed to accept first screen");
                return false;
            }

            Sleep.sleep(1500, 2500);

            // Accept second screen
            if (Trade.isInSecondInterface()) {
                Logger.info(Logger.Category.GENERAL, "✅ In second trade screen, finalizing...");

                Sleep.sleep(1000, 2000);

                if (Trade.accept()) {
                    Logger.info(Logger.Category.GENERAL, "✅ Trade accepted, waiting for completion...");

                    Sleep.sleepUntil(() -> !Trade.isOpen(), 10000);

                    if (!Trade.isOpen()) {
                        totalMuledGP += gpAmount;
                        Logger.info(Logger.Category.GENERAL, "✅ Successfully muled " + formatGP(gpAmount) + " GP!");
                        return true;
                    }
                }
            }

            Logger.error(Logger.Category.GENERAL, "❌ Trade failed to complete");
            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.HIGH, "Trading with mule");

            // Try to decline trade if open
            if (Trade.isOpen()) {
                Trade.decline();
            }

            return false;
        }
    }

    // ===========================================
    // STATUS & STATISTICS
    // ===========================================

    /**
     * Get current mule state
     */
    public MuleState getCurrentState() {
        return currentState;
    }

    /**
     * Get total GP muled this session
     */
    public int getTotalMuledGP() {
        return totalMuledGP;
    }

    /**
     * Get number of mule attempts
     */
    public int getMuleAttempts() {
        return muleAttempts;
    }

    /**
     * Get time since last mule
     */
    public long getTimeSinceLastMule() {
        if (lastMuleTime == 0) {
            return 0;
        }
        return System.currentTimeMillis() - lastMuleTime;
    }

    /**
     * Reset mule statistics
     */
    public void reset() {
        currentState = MuleState.IDLE;
        lastMuleTime = 0;
        totalMuledGP = 0;
        muleAttempts = 0;
        Logger.info(Logger.Category.GENERAL, "🔄 MuleManager reset");
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Format GP amount
     */
    private String formatGP(int amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fB GP", amount / 1_000_000_000.0);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fM GP", amount / 1_000_000.0);
        } else if (amount >= 1_000) {
            return String.format("%.1fK GP", amount / 1_000.0);
        } else {
            return String.format("%,d GP", amount);
        }
    }
}
