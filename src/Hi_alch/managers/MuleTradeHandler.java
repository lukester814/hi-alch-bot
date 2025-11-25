package Hi_alch.managers;

import Hi_alch.utils.BotUtils;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

import java.util.Random;

/**
 * Mule Trade Execution Handler
 *
 * Handles the actual trade flow:
 * - First trade screen (offer items/GP)
 * - Second trade screen (confirm)
 * - Trade state management
 */
public class MuleTradeHandler {

    private Random random = new Random();
    private DiscordManager discordManager;

    // Trade settings
    private boolean autoTransferProfit;
    private boolean autoTransferItems;

    // Tracking
    private int totalGPTransferred = 0;
    private int totalItemsTransferred = 0;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public MuleTradeHandler(DiscordManager discordManager) {
        this.discordManager = discordManager;
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    public void configure(boolean autoProfit, boolean autoItems) {
        this.autoTransferProfit = autoProfit;
        this.autoTransferItems = autoItems;
    }

    // ===========================================
    // TRADE EXECUTION
    // ===========================================

    /**
     * Execute trade with mule
     */
    public boolean executeTrade(Player mule, String muleUsername) {
        try {
            BotUtils.log("🤝 Initiating trade with: " + mule.getName());

            // Request trade
            if (!Trade.isOpen()) {
                if (!mule.interact("Trade with")) {
                    BotUtils.log("❌ Failed to right-click mule");
                    return false;
                }

                // Wait for trade screen
                if (!Sleep.sleepUntil(() -> Trade.isOpen(), 10000)) {
                    BotUtils.log("❌ Trade screen did not open");
                    return false;
                }
            }

            BotUtils.log("📋 Trade screen opened");

            // First trade screen - offer items/GP
            if (!handleFirstTradeScreen()) {
                return false;
            }

            // Second trade screen - confirm
            if (!handleSecondTradeScreen()) {
                return false;
            }

            // Wait for trade completion
            if (!Sleep.sleepUntil(() -> !Trade.isOpen(), 10000)) {
                BotUtils.log("⚠️ Trade did not close properly");
            }

            BotUtils.log("✅ Trade completed successfully!");

            // Send Discord notification
            if (discordManager != null) {
                discordManager.sendMuleTransferCompleted(muleUsername, totalGPTransferred, totalItemsTransferred);
            }

            return true;

        } catch (Exception e) {
            BotUtils.logError("Error executing trade", e);
            return false;
        }
    }

    /**
     * Handle first trade screen (offer items/GP)
     */
    private boolean handleFirstTradeScreen() {
        try {
            BotUtils.log("💰 First trade screen - offering items...");

            // Anti-ban delay
            Sleep.sleep(800 + random.nextInt(1200), 1500 + random.nextInt(500));

            // Offer all GP if auto-transfer is enabled
            if (autoTransferProfit) {
                int availableGP = Inventory.count(995); // Coins
                if (availableGP > 0) {
                    Item coinsItem = Inventory.get(995);
                    if (coinsItem != null && coinsItem.interact("Offer-All")) {
                        totalGPTransferred += availableGP;
                        BotUtils.log("💵 Offered " + BotUtils.formatNumber(availableGP) + " GP");
                        Sleep.sleep(500 + random.nextInt(800), 1000 + random.nextInt(500));
                    }
                }
            }

            // Offer items if auto-transfer is enabled
            if (autoTransferItems) {
                for (Item item : Inventory.all(i -> i != null && i.getId() != 995)) {
                    if (item.interact("Offer-All")) {
                        totalItemsTransferred += item.getAmount();
                        BotUtils.log("📦 Offered " + item.getAmount() + "x " + item.getName());
                        Sleep.sleep(400 + random.nextInt(600), 800 + random.nextInt(400));
                    }
                }
            }

            // Wait for mule to accept
            BotUtils.log("⏳ Waiting for mule to accept...");
            Sleep.sleep(2000 + random.nextInt(3000), 4000 + random.nextInt(2000));

            // Accept first screen by clicking the accept button
            if (org.dreambot.api.methods.widget.Widgets.get(335, 16) != null &&
                org.dreambot.api.methods.widget.Widgets.get(335, 16).interact()) {
                BotUtils.log("✅ Accepted first trade screen");

                // Wait for second screen
                if (!Sleep.sleepUntil(Trade::isOpen, 15000)) {
                    BotUtils.log("❌ Second trade screen did not open");
                    return false;
                }

                return true;
            } else {
                BotUtils.log("❌ Failed to accept first trade screen");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error in first trade screen", e);
            return false;
        }
    }

    /**
     * Handle second trade screen (confirm)
     */
    private boolean handleSecondTradeScreen() {
        try {
            BotUtils.log("✔️ Second trade screen - confirming...");

            // Anti-ban delay - read the second screen
            Sleep.sleep(1500 + random.nextInt(2500), 3000 + random.nextInt(1500));

            // Accept second screen by clicking the accept button
            if (org.dreambot.api.methods.widget.Widgets.get(334, 19) != null &&
                org.dreambot.api.methods.widget.Widgets.get(334, 19).interact()) {
                BotUtils.log("✅ Confirmed trade");
                return true;
            } else {
                BotUtils.log("❌ Failed to confirm trade");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error in second trade screen", e);
            return false;
        }
    }

    // ===========================================
    // STATISTICS
    // ===========================================

    public int getTotalGPTransferred() {
        return totalGPTransferred;
    }

    public int getTotalItemsTransferred() {
        return totalItemsTransferred;
    }

    public void resetStatistics() {
        totalGPTransferred = 0;
        totalItemsTransferred = 0;
    }
}
