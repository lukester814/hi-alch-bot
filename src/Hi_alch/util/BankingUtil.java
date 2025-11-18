package Hi_alch.util;

import Hi_alch.core.Logger;
import Hi_alch.core.ExceptionHandler;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

/**
 * Banking Utility - Common Banking Operations
 *
 * Provides convenient methods for:
 * - Opening and closing bank
 * - Depositing items
 * - Withdrawing items
 * - Checking bank contents
 * - Smart banking operations
 */
public class BankingUtil {

    // ===========================================
    // BANK OPENING/CLOSING
    // ===========================================

    /**
     * Open bank with retries
     */
    public static boolean openBank() {
        return openBank(3);
    }

    /**
     * Open bank with specified retry attempts
     */
    public static boolean openBank(int maxAttempts) {
        if (Bank.isOpen()) {
            Logger.debug(Logger.Category.GENERAL, "Bank already open");
            return true;
        }

        Logger.info(Logger.Category.GENERAL, "🏦 Opening bank...");

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                if (Bank.open()) {
                    boolean opened = Sleep.sleepUntil(Bank::isOpen, 5000);

                    if (opened) {
                        Logger.debug(Logger.Category.GENERAL, "✅ Bank opened successfully");
                        return true;
                    }
                }

                if (attempt < maxAttempts) {
                    Logger.warn(Logger.Category.GENERAL, "⚠️ Failed to open bank (attempt " + attempt + "/" + maxAttempts + "), retrying...");
                    Sleep.sleep(1000);
                }

            } catch (Exception e) {
                ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                    ExceptionHandler.ErrorSeverity.LOW, "Opening bank (attempt " + attempt + ")");
            }
        }

        Logger.error(Logger.Category.GENERAL, "❌ Failed to open bank after " + maxAttempts + " attempts");
        return false;
    }

    /**
     * Close bank
     */
    public static boolean closeBank() {
        if (!Bank.isOpen()) {
            return true;
        }

        Logger.debug(Logger.Category.GENERAL, "Closing bank...");

        try {
            if (Bank.close()) {
                boolean closed = Sleep.sleepUntil(() -> !Bank.isOpen(), 3000);

                if (closed) {
                    Logger.debug(Logger.Category.GENERAL, "✅ Bank closed");
                    return true;
                }
            }

            Logger.warn(Logger.Category.GENERAL, "⚠️ Failed to close bank");
            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.LOW, "Closing bank");
            return false;
        }
    }

    // ===========================================
    // DEPOSIT OPERATIONS
    // ===========================================

    /**
     * Deposit all items
     */
    public static boolean depositAll() {
        if (!Bank.isOpen()) {
            if (!openBank()) {
                return false;
            }
        }

        try {
            Logger.debug(Logger.Category.GENERAL, "Depositing all items...");

            if (Bank.depositAllItems()) {
                Sleep.sleepUntil(() -> Inventory.isEmpty(), 3000);
                Logger.debug(Logger.Category.GENERAL, "✅ Deposited all items");
                return true;
            }

            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.LOW, "Depositing all items");
            return false;
        }
    }

    /**
     * Deposit all except specified items
     */
    public static boolean depositAllExcept(int... itemIds) {
        if (!Bank.isOpen()) {
            if (!openBank()) {
                return false;
            }
        }

        try {
            Logger.debug(Logger.Category.GENERAL, "Depositing all except specified items...");

            if (Bank.depositAllExcept(itemIds)) {
                Sleep.sleep(500);
                Logger.debug(Logger.Category.GENERAL, "✅ Deposited items");
                return true;
            }

            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.LOW, "Depositing all except");
            return false;
        }
    }

    /**
     * Deposit specific item
     */
    public static boolean deposit(int itemId, int amount) {
        if (!Bank.isOpen()) {
            if (!openBank()) {
                return false;
            }
        }

        try {
            if (!Inventory.contains(itemId)) {
                Logger.debug(Logger.Category.GENERAL, "Item " + itemId + " not in inventory");
                return true; // Not an error
            }

            Logger.debug(Logger.Category.GENERAL, "Depositing " + amount + "x item " + itemId);

            if (Bank.deposit(itemId, amount)) {
                Sleep.sleep(500);
                Logger.debug(Logger.Category.GENERAL, "✅ Deposited item");
                return true;
            }

            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.LOW, "Depositing item " + itemId);
            return false;
        }
    }

    /**
     * Deposit all of specific item
     */
    public static boolean depositAll(int itemId) {
        if (!Bank.isOpen()) {
            if (!openBank()) {
                return false;
            }
        }

        try {
            int count = Inventory.count(itemId);
            if (count == 0) {
                return true; // Nothing to deposit
            }

            return deposit(itemId, count);

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.LOW, "Depositing all of item " + itemId);
            return false;
        }
    }

    // ===========================================
    // WITHDRAW OPERATIONS
    // ===========================================

    /**
     * Withdraw item
     */
    public static boolean withdraw(int itemId, int amount) {
        if (!Bank.isOpen()) {
            if (!openBank()) {
                return false;
            }
        }

        try {
            if (!Bank.contains(itemId)) {
                Logger.warn(Logger.Category.GENERAL, "⚠️ Item " + itemId + " not in bank");
                return false;
            }

            Logger.debug(Logger.Category.GENERAL, "Withdrawing " + amount + "x item " + itemId);

            int inventoryCountBefore = Inventory.count(itemId);

            if (Bank.withdraw(itemId, amount)) {
                boolean success = Sleep.sleepUntil(() ->
                    Inventory.count(itemId) > inventoryCountBefore, 3000);

                if (success) {
                    Logger.debug(Logger.Category.GENERAL, "✅ Withdrawn item");
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.LOW, "Withdrawing item " + itemId);
            return false;
        }
    }

    /**
     * Withdraw all of item
     */
    public static boolean withdrawAll(int itemId) {
        if (!Bank.isOpen()) {
            if (!openBank()) {
                return false;
            }
        }

        try {
            if (!Bank.contains(itemId)) {
                Logger.warn(Logger.Category.GENERAL, "⚠️ Item " + itemId + " not in bank");
                return false;
            }

            Logger.debug(Logger.Category.GENERAL, "Withdrawing all of item " + itemId);

            if (Bank.withdrawAll(itemId)) {
                Sleep.sleep(500);
                Logger.debug(Logger.Category.GENERAL, "✅ Withdrawn all items");
                return true;
            }

            return false;

        } catch (Exception e) {
            ExceptionHandler.handle(e, ExceptionHandler.ErrorCategory.GENERAL,
                ExceptionHandler.ErrorSeverity.LOW, "Withdrawing all of item " + itemId);
            return false;
        }
    }

    // ===========================================
    // CHECKING OPERATIONS
    // ===========================================

    /**
     * Check if bank contains item
     */
    public static boolean contains(int itemId) {
        if (!Bank.isOpen()) {
            return false;
        }

        try {
            return Bank.contains(itemId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get count of item in bank
     */
    public static int count(int itemId) {
        if (!Bank.isOpen()) {
            return 0;
        }

        try {
            return Bank.count(itemId);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if bank has enough of item
     */
    public static boolean hasEnough(int itemId, int requiredAmount) {
        return count(itemId) >= requiredAmount;
    }

    // ===========================================
    // SMART OPERATIONS
    // ===========================================

    /**
     * Smart withdraw - opens bank if needed
     */
    public static boolean smartWithdraw(int itemId, int amount) {
        if (!Bank.isOpen()) {
            if (!openBank()) {
                Logger.error(Logger.Category.GENERAL, "❌ Cannot withdraw - failed to open bank");
                return false;
            }
        }

        return withdraw(itemId, amount);
    }

    /**
     * Smart deposit - opens bank if needed
     */
    public static boolean smartDeposit(int itemId, int amount) {
        if (!Bank.isOpen()) {
            if (!openBank()) {
                Logger.error(Logger.Category.GENERAL, "❌ Cannot deposit - failed to open bank");
                return false;
            }
        }

        return deposit(itemId, amount);
    }

    /**
     * Ensure inventory has item (withdraw if needed)
     */
    public static boolean ensureInventoryHas(int itemId, int requiredAmount) {
        int currentCount = Inventory.count(itemId);

        if (currentCount >= requiredAmount) {
            Logger.debug(Logger.Category.GENERAL, "✅ Already have enough of item " + itemId);
            return true;
        }

        int needed = requiredAmount - currentCount;
        Logger.info(Logger.Category.GENERAL, "📦 Need " + needed + " more of item " + itemId);

        return smartWithdraw(itemId, needed);
    }

    /**
     * Clear inventory (deposit all)
     */
    public static boolean clearInventory() {
        if (!Inventory.isEmpty()) {
            return depositAll();
        }
        return true;
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Check if near a bank
     */
    public static boolean isNearBank() {
        try {
            GameObject bank = GameObjects.closest(obj ->
                obj != null &&
                obj.getName() != null &&
                (obj.hasAction("Bank") || obj.getName().contains("Bank"))
            );

            return bank != null && bank.distance() < 10;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get GP count in bank
     */
    public static int getBankGP() {
        if (!Bank.isOpen()) {
            return 0;
        }

        return count(995); // Coins ID
    }

    /**
     * Get total GP (inventory + bank)
     */
    public static int getTotalGP() {
        int inventoryGP = Inventory.count(995);
        int bankGP = 0;

        if (Bank.isOpen()) {
            bankGP = getBankGP();
        }

        return inventoryGP + bankGP;
    }

    /**
     * Prevent instantiation
     */
    private BankingUtil() {
        throw new AssertionError("Cannot instantiate BankingUtil class");
    }
}
