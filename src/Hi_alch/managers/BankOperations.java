package Hi_alch.managers;

import Hi_alch.utils.BotUtils;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;

/**
 * Bank Operations Handler
 *
 * Handles all bank item operations:
 * - Withdraw items (by ID or name)
 * - Deposit items
 * - Deposit all / deposit all except
 * - Bank preset loading
 * - Cash management
 */
public class BankOperations {

    private static final int COINS_ID = 995;

    // ===========================================
    // ITEM OPERATIONS
    // ===========================================

    /**
     * Withdraw item from bank
     */
    public boolean withdrawItem(int itemId, int quantity) {
        try {
            if (!Bank.isOpen()) {
                BotUtils.log("❌ Bank is not open");
                return false;
            }

            if (!Bank.contains(itemId)) {
                BotUtils.log("❌ Item " + itemId + " not in bank");
                return false;
            }

            int beforeCount = Inventory.count(itemId);

            if (Bank.withdraw(itemId, quantity)) {
                Sleep.sleepUntil(() -> Inventory.count(itemId) > beforeCount, 3000);
                BotUtils.log("✅ Withdrew " + quantity + "x item " + itemId);
                return true;
            }

            BotUtils.log("❌ Failed to withdraw item " + itemId);
            return false;

        } catch (Exception e) {
            BotUtils.logError("Error withdrawing item", e);
            return false;
        }
    }

    /**
     * Withdraw item by name
     */
    public boolean withdrawItem(String itemName, int quantity) {
        try {
            if (!Bank.isOpen()) {
                BotUtils.log("❌ Bank is not open");
                return false;
            }

            if (!Bank.contains(itemName)) {
                BotUtils.log("❌ Item '" + itemName + "' not in bank");
                return false;
            }

            int beforeCount = Inventory.count(itemName);

            if (Bank.withdraw(itemName, quantity)) {
                Sleep.sleepUntil(() -> Inventory.count(itemName) > beforeCount, 3000);
                BotUtils.log("✅ Withdrew " + quantity + "x " + itemName);
                return true;
            }

            BotUtils.log("❌ Failed to withdraw " + itemName);
            return false;

        } catch (Exception e) {
            BotUtils.logError("Error withdrawing item", e);
            return false;
        }
    }

    /**
     * Deposit item to bank
     */
    public boolean depositItem(int itemId, int quantity) {
        try {
            if (!Bank.isOpen()) {
                BotUtils.log("❌ Bank is not open");
                return false;
            }

            if (!Inventory.contains(itemId)) {
                BotUtils.log("❌ Item " + itemId + " not in inventory");
                return false;
            }

            if (Bank.deposit(itemId, quantity)) {
                Sleep.sleep(500, 800);
                BotUtils.log("✅ Deposited " + quantity + "x item " + itemId);
                return true;
            }

            BotUtils.log("❌ Failed to deposit item " + itemId);
            return false;

        } catch (Exception e) {
            BotUtils.logError("Error depositing item", e);
            return false;
        }
    }

    /**
     * Deposit all items
     */
    public boolean depositAll() {
        try {
            if (!Bank.isOpen()) {
                BotUtils.log("❌ Bank is not open");
                return false;
            }

            if (Bank.depositAllItems()) {
                Sleep.sleepUntil(() -> Inventory.isEmpty(), 3000);
                BotUtils.log("✅ Deposited all items");
                return true;
            }

            BotUtils.log("❌ Failed to deposit all items");
            return false;

        } catch (Exception e) {
            BotUtils.logError("Error depositing all items", e);
            return false;
        }
    }

    /**
     * Deposit all except specified items
     */
    public boolean depositAllExcept(int... itemIds) {
        try {
            if (!Bank.isOpen()) {
                BotUtils.log("❌ Bank is not open");
                return false;
            }

            if (Bank.depositAllExcept(itemIds)) {
                Sleep.sleep(500, 800);
                BotUtils.log("✅ Deposited all except " + itemIds.length + " items");
                return true;
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error depositing items", e);
            return false;
        }
    }

    // ===========================================
    // PRESET OPERATIONS
    // ===========================================

    /**
     * Load a bank preset
     */
    public boolean loadPreset(BankManager.BankPreset preset) {
        try {
            BotUtils.log("📦 Loading preset: " + preset.name);

            // Deposit all items first
            depositAll();

            // Withdraw items
            for (int i = 0; i < preset.itemIds.length; i++) {
                int itemId = preset.itemIds[i];
                int quantity = preset.quantities[i];

                if (!withdrawItem(itemId, quantity)) {
                    BotUtils.log("⚠️ Failed to withdraw item " + itemId +
                               " from preset " + preset.name);
                }

                Sleep.sleep(400, 700);
            }

            BotUtils.log("✅ Preset loaded: " + preset.name);
            return true;

        } catch (Exception e) {
            BotUtils.logError("Error loading preset", e);
            return false;
        }
    }

    // ===========================================
    // QUERY OPERATIONS
    // ===========================================

    /**
     * Check if item is available in bank
     */
    public boolean hasItemInBank(int itemId) {
        try {
            if (!Bank.isOpen()) {
                return false;
            }

            return Bank.contains(itemId) && Bank.count(itemId) > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get item count in bank
     */
    public int getBankItemCount(int itemId) {
        try {
            if (!Bank.isOpen()) {
                return 0;
            }

            return Bank.count(itemId);

        } catch (Exception e) {
            return 0;
        }
    }

    // ===========================================
    // CASH MANAGEMENT
    // ===========================================

    /**
     * Check if player has cash in bank
     */
    public boolean hasCashInBank() {
        try {
            if (!Bank.isOpen()) {
                return false;
            }

            return Bank.contains(COINS_ID) && Bank.count(COINS_ID) > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get cash amount in bank
     */
    public int getCashInBank() {
        try {
            if (!Bank.isOpen()) {
                return 0;
            }

            return Bank.count(COINS_ID);

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Withdraw cash from bank
     */
    public boolean withdrawCash(int amount) {
        try {
            if (!Bank.isOpen()) {
                BotUtils.log("❌ Bank is not open");
                return false;
            }

            if (!Bank.contains(COINS_ID)) {
                BotUtils.log("❌ No coins in bank");
                return false;
            }

            int bankCash = Bank.count(COINS_ID);
            BotUtils.log("💰 Bank has " + BotUtils.formatNumber(bankCash) + " GP");

            if (bankCash < amount) {
                BotUtils.log("⚠️ Insufficient cash in bank. Need: " +
                        BotUtils.formatNumber(amount) + " GP, Have: " +
                        BotUtils.formatNumber(bankCash) + " GP");
                // Withdraw what we have
                amount = bankCash;
            }

            int beforeCount = Inventory.count(COINS_ID);

            if (Bank.withdraw(COINS_ID, amount)) {
                Sleep.sleepUntil(() -> Inventory.count(COINS_ID) > beforeCount, 3000);
                int withdrawn = Inventory.count(COINS_ID) - beforeCount;
                BotUtils.log("✅ Withdrew " + BotUtils.formatNumber(withdrawn) + " GP from bank");
                return true;
            }

            BotUtils.log("❌ Failed to withdraw cash");
            return false;

        } catch (Exception e) {
            BotUtils.logError("Error withdrawing cash", e);
            return false;
        }
    }

    /**
     * Withdraw all cash from bank
     */
    public boolean withdrawAllCash() {
        try {
            if (!Bank.isOpen()) {
                BotUtils.log("❌ Bank is not open");
                return false;
            }

            if (!Bank.contains(COINS_ID)) {
                BotUtils.log("❌ No coins in bank");
                return false;
            }

            int bankCash = Bank.count(COINS_ID);
            return withdrawCash(bankCash);

        } catch (Exception e) {
            BotUtils.logError("Error withdrawing all cash", e);
            return false;
        }
    }
}
