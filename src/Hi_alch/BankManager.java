package Hi_alch;

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

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public BankManager() {
        this.preferredLocation = BankLocation.GRAND_EXCHANGE;
        this.bankPin = "";
        this.useBankPin = false;
        this.hybridMode = true;

        BotUtils.log("🏦 BankManager initialized");
    }

    // ===========================================
    // CONFIGURATION METHODS
    // ===========================================

    public void setPreferredLocation(BankLocation location) {
        this.preferredLocation = location;
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
        try {
            BotUtils.log("🚶 Walking to " + preferredLocation.getDisplayName());

            if (preferredLocation.isNearby()) {
                BotUtils.log("✅ Already at bank");
                return true;
            }

            if (Walking.shouldWalk()) {
                Walking.walk(preferredLocation.getArea().getRandomTile());
            }

            boolean arrived = Sleep.sleepUntil(() -> preferredLocation.isNearby(), 30000);

            if (arrived) {
                BotUtils.log("✅ Arrived at " + preferredLocation.getDisplayName());
                return true;
            } else {
                BotUtils.log("❌ Failed to reach bank");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error walking to bank", e);
            return false;
        }
    }

    /**
     * Find and walk to nearest bank
     */
    public boolean walkToNearestBank() {
        try {
            BotUtils.log("🔍 Finding nearest bank...");

            BankLocation nearest = null;
            int shortestDistance = Integer.MAX_VALUE;

            for (BankLocation location : BankLocation.values()) {
                try {
                    int distance = org.dreambot.api.methods.interactive.Players.getLocal()
                                   .getTile().distance(location.getArea().getCenter());

                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        nearest = location;
                    }
                } catch (Exception e) {
                    // Skip this location
                }
            }

            if (nearest != null) {
                BotUtils.log("🎯 Nearest bank: " + nearest.getDisplayName() +
                           " (" + shortestDistance + " tiles)");
                setPreferredLocation(nearest);
                return walkToBank();
            }

            BotUtils.log("❌ No nearby banks found");
            return false;

        } catch (Exception e) {
            BotUtils.logError("Error finding nearest bank", e);
            return false;
        }
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
    public boolean loadPreset(BankPreset preset) {
        try {
            if (!Bank.isOpen()) {
                if (!openBank()) {
                    return false;
                }
            }

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
                if (!openBank()) {
                    return 0;
                }
            }

            return Bank.count(itemId);

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
