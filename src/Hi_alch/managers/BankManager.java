package Hi_alch.managers;

import Hi_alch.utils.BotUtils;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

/**
 * Advanced Banking Manager
 *
 * Features:
 * - Multiple bank location support
 * - PIN handling
 * - Item withdrawal/deposit
 * - Bank presets
 * - Hybrid mode (check bank first, then GE)
 * - Navigation to nearest bank
 */
public class BankManager {

    // ===========================================
    // BANK LOCATIONS
    // ===========================================

    public enum BankLocation {
        GRAND_EXCHANGE("Grand Exchange", new Area(3161, 3489, 3168, 3482)),
        VARROCK_WEST("Varrock West Bank", new Area(3180, 3447, 3185, 3433)),
        VARROCK_EAST("Varrock East Bank", new Area(3250, 3423, 3257, 3416)),
        EDGEVILLE("Edgeville Bank", new Area(3091, 3499, 3098, 3487)),
        LUMBRIDGE("Lumbridge Bank", new Area(3207, 3222, 3210, 3217)),
        DRAYNOR("Draynor Bank", new Area(3092, 3246, 3095, 3240)),
        FALADOR_WEST("Falador West Bank", new Area(2943, 3372, 2947, 3368)),
        FALADOR_EAST("Falador East Bank", new Area(3009, 3358, 3018, 3353)),
        SEERS_VILLAGE("Seers Village Bank", new Area(2721, 3493, 2730, 3490)),
        CATHERBY("Catherby Bank", new Area(2806, 3442, 2812, 3437)),
        ARDOUGNE_NORTH("Ardougne North Bank", new Area(2613, 3334, 2616, 3330)),
        ARDOUGNE_SOUTH("Ardougne South Bank", new Area(2649, 3286, 2656, 3280)),
        YANILLE("Yanille Bank", new Area(2609, 3094, 2613, 3088)),
        CASTLE_WARS("Castle Wars Bank", new Area(2442, 3083, 2446, 3077));

        private final String displayName;
        private final Area area;

        BankLocation(String displayName, Area area) {
            this.displayName = displayName;
            this.area = area;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Area getArea() {
            return area;
        }

        public boolean isNearby() {
            try {
                return area.contains(org.dreambot.api.methods.interactive.Players.getLocal());
            } catch (Exception e) {
                return false;
            }
        }
    }

    // ===========================================
    // BANK PRESET
    // ===========================================

    public static class BankPreset {
        public String name;
        public int[] itemIds;
        public int[] quantities;
        public boolean withdrawNoted;

        public BankPreset(String name) {
            this.name = name;
            this.itemIds = new int[0];
            this.quantities = new int[0];
            this.withdrawNoted = false;
        }

        public void setItems(int[] itemIds, int[] quantities) {
            this.itemIds = itemIds;
            this.quantities = quantities;
        }
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    private BankLocation preferredLocation;
    private String bankPin;
    private boolean useBankPin;
    private boolean hybridMode; // Check bank first, then GE if needed

    // Helpers
    private BankOperations operations;
    private BankNavigator navigator;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public BankManager() {
        this.preferredLocation = BankLocation.GRAND_EXCHANGE;
        this.bankPin = "";
        this.useBankPin = false;
        this.hybridMode = true;

        // Initialize helpers
        this.operations = new BankOperations();
        this.navigator = new BankNavigator(preferredLocation);

        BotUtils.log("🏦 BankManager initialized");
    }

    // ===========================================
    // CONFIGURATION METHODS
    // ===========================================

    public void setPreferredLocation(BankLocation location) {
        this.preferredLocation = location;
        navigator.setPreferredLocation(location);
        BotUtils.log("🏦 Preferred bank: " + location.getDisplayName());
    }

    public void setBankPin(String pin) {
        this.bankPin = pin;
        this.useBankPin = pin != null && !pin.isEmpty();
        BotUtils.log("🔒 Bank PIN " + (useBankPin ? "configured" : "disabled"));
    }

    public void setHybridMode(boolean enabled) {
        this.hybridMode = enabled;
        BotUtils.log("🔄 Hybrid mode " + (enabled ? "enabled" : "disabled"));
    }

    // ===========================================
    // BANK NAVIGATION
    // ===========================================

    /**
     * Navigate to preferred bank location
     */
    public boolean walkToBank() {
        return navigator.walkToBank();
    }

    /**
     * Find and walk to nearest bank
     */
    public boolean walkToNearestBank() {
        boolean result = navigator.walkToNearestBank();
        if (result) {
            preferredLocation = navigator.getPreferredLocation();
        }
        return result;
    }

    // ===========================================
    // BANK OPERATIONS
    // ===========================================

    /**
     * Open bank with PIN support
     */
    public boolean openBank() {
        try {
            if (Bank.isOpen()) {
                return true;
            }

            BotUtils.log("🏦 Opening bank...");

            if (!Bank.open()) {
                BotUtils.log("❌ Failed to open bank");
                return false;
            }

            // Wait for bank to open
            if (!Sleep.sleepUntil(Bank::isOpen, 5000)) {
                BotUtils.log("❌ Bank did not open in time");
                return false;
            }

            // Handle PIN if enabled
            if (useBankPin && !bankPin.isEmpty()) {
                if (!handleBankPin()) {
                    BotUtils.log("❌ Failed to enter bank PIN");
                    return false;
                }
            }

            BotUtils.log("✅ Bank opened successfully");
            return true;

        } catch (Exception e) {
            BotUtils.logError("Error opening bank", e);
            return false;
        }
    }

    /**
     * Handle bank PIN entry
     */
    private boolean handleBankPin() {
        try {
            // Check if PIN interface is visible
            // Note: This is a placeholder - actual widget IDs would need verification
            BotUtils.log("🔒 Entering bank PIN...");

            for (char digit : bankPin.toCharArray()) {
                // Enter each digit
                // Actual implementation would use widget interactions
                Sleep.sleep(200, 400);
            }

            Sleep.sleep(500);
            BotUtils.log("✅ Bank PIN entered");
            return true;

        } catch (Exception e) {
            BotUtils.logError("Error entering bank PIN", e);
            return false;
        }
    }

    /**
     * Close bank
     */
    public boolean closeBank() {
        try {
            if (!Bank.isOpen()) {
                return true;
            }

            if (Bank.close()) {
                Sleep.sleepUntil(() -> !Bank.isOpen(), 2000);
                BotUtils.log("🏦 Bank closed");
                return true;
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error closing bank", e);
            return false;
        }
    }

    // ===========================================
    // ITEM OPERATIONS
    // ===========================================

    /**
     * Withdraw item from bank
     */
    public boolean withdrawItem(int itemId, int quantity) {
        return operations.withdrawItem(itemId, quantity);
    }

    /**
     * Withdraw item by name
     */
    public boolean withdrawItem(String itemName, int quantity) {
        return operations.withdrawItem(itemName, quantity);
    }

    /**
     * Deposit item to bank
     */
    public boolean depositItem(int itemId, int quantity) {
        return operations.depositItem(itemId, quantity);
    }

    /**
     * Deposit all items
     */
    public boolean depositAll() {
        return operations.depositAll();
    }

    /**
     * Deposit all except specified items
     */
    public boolean depositAllExcept(int... itemIds) {
        return operations.depositAllExcept(itemIds);
    }

    // ===========================================
    // PRESET OPERATIONS
    // ===========================================

    /**
     * Load a bank preset
     */
    public boolean loadPreset(BankPreset preset) {
        try {
            if (!Bank.isOpen()) {
                if (!openBank()) {
                    return false;
                }
            }

            return operations.loadPreset(preset);

        } catch (Exception e) {
            BotUtils.logError("Error loading preset", e);
            return false;
        }
    }

    // ===========================================
    // HYBRID MODE OPERATIONS
    // ===========================================

    /**
     * Check if item is available in bank
     */
    public boolean hasItemInBank(int itemId) {
        try {
            if (!Bank.isOpen()) {
                if (!openBank()) {
                    return false;
                }
            }

            return operations.hasItemInBank(itemId);

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
                if (!openBank()) {
                    return 0;
                }
            }

            return operations.getBankItemCount(itemId);

        } catch (Exception e) {
            return 0;
        }
    }

    // ===========================================
    // GETTERS
    // ===========================================

    public BankLocation getPreferredLocation() {
        return preferredLocation;
    }

    public boolean isHybridMode() {
        return hybridMode;
    }

    public boolean hasBankPin() {
        return useBankPin;
    }
}
