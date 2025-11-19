package Hi_alch;

import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.utilities.Sleep;

import java.util.*;

/**
 * Grand Exchange Slot Manager
 *
 * Features:
 * - Track all 8 GE slots status
 * - Auto-collect completed offers
 * - Cancel stuck offers
 * - Queue multiple purchases
 * - Offer history logging
 * - Smart slot allocation
 */
public class GESlotManager {

    // ===========================================
    // SLOT DATA STRUCTURES
    // ===========================================

    public enum SlotStatus {
        EMPTY("Empty", "No active offer"),
        BUYING("Buying", "Buy offer in progress"),
        SELLING("Selling", "Sell offer in progress"),
        COMPLETED("Completed", "Offer completed, ready to collect"),
        CANCELLED("Cancelled", "Offer cancelled"),
        STUCK("Stuck", "Offer stuck (no progress)");

        private final String displayName;
        private final String description;

        SlotStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class GESlot {
        public int slotIndex;
        public SlotStatus status;
        public int itemId;
        public String itemName;
        public int quantity;
        public int price;
        public long offerStartTime;
        public long lastUpdateTime;
        public boolean isBuyOffer;

        public GESlot(int slotIndex) {
            this.slotIndex = slotIndex;
            this.status = SlotStatus.EMPTY;
            this.itemId = -1;
            this.itemName = "";
            this.quantity = 0;
            this.price = 0;
            this.offerStartTime = 0;
            this.lastUpdateTime = 0;
            this.isBuyOffer = true;
        }

        public long getOfferAge() {
            return offerStartTime > 0 ? System.currentTimeMillis() - offerStartTime : 0;
        }

        public boolean isStuck(long timeoutMillis) {
            return getOfferAge() > timeoutMillis &&
                   (status == SlotStatus.BUYING || status == SlotStatus.SELLING);
        }
    }

    public static class OfferHistoryEntry {
        public long timestamp;
        public String itemName;
        public int quantity;
        public int price;
        public boolean isBuyOffer;
        public boolean wasSuccessful;
        public long offerDuration;

        public OfferHistoryEntry(String itemName, int quantity, int price, boolean isBuyOffer,
                                boolean wasSuccessful, long offerDuration) {
            this.timestamp = System.currentTimeMillis();
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
            this.isBuyOffer = isBuyOffer;
            this.wasSuccessful = wasSuccessful;
            this.offerDuration = offerDuration;
        }
    }

    // ===========================================
    // STATE
    // ===========================================

    private static final int TOTAL_SLOTS = 8;
    private static final long STUCK_OFFER_TIMEOUT = 5 * 60 * 1000; // 5 minutes

    private GESlot[] slots;
    private Queue<PurchaseRequest> purchaseQueue;
    private List<OfferHistoryEntry> offerHistory;

    private boolean autoCollect;
    private boolean autoCancelStuck;
    private long lastUpdateTime;

    // ===========================================
    // PURCHASE REQUEST
    // ===========================================

    public static class PurchaseRequest {
        public int itemId;
        public String itemName;
        public int quantity;
        public int price;
        public boolean isBuyOffer;

        public PurchaseRequest(int itemId, String itemName, int quantity, int price, boolean isBuyOffer) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
            this.isBuyOffer = isBuyOffer;
        }
    }

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public GESlotManager() {
        this.slots = new GESlot[TOTAL_SLOTS];
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            slots[i] = new GESlot(i);
        }

        this.purchaseQueue = new LinkedList<>();
        this.offerHistory = new ArrayList<>();
        this.autoCollect = true;
        this.autoCancelStuck = true;
        this.lastUpdateTime = 0;

        BotUtils.log("🏪 GESlotManager initialized (8 slots)");
    }

    // ===========================================
    // SLOT TRACKING
    // ===========================================

    /**
     * Update all slot statuses from GE
     */
    public void updateSlotStatuses() {
        try {
            if (!GrandExchange.isOpen()) {
                return;
            }

            for (int i = 0; i < TOTAL_SLOTS; i++) {
                updateSlot(i);
            }

            lastUpdateTime = System.currentTimeMillis();

            // Auto-collect if enabled
            if (autoCollect) {
                collectCompletedOffers();
            }

            // Auto-cancel stuck offers if enabled
            if (autoCancelStuck) {
                cancelStuckOffers();
            }

        } catch (Exception e) {
            BotUtils.logError("Error updating GE slot statuses", e);
        }
    }

    /**
     * Update single slot status
     */
    private void updateSlot(int slotIndex) {
        try {
            GESlot slot = slots[slotIndex];

            // Check if slot has active offer
            if (GrandExchange.slotContainsItem(slotIndex)) {
                // Slot has an item/offer
                if (GrandExchange.isReadyToCollect(slotIndex)) {
                    slot.status = SlotStatus.COMPLETED;
                } else {
                    // Active offer - check if it's stuck
                    if (slot.isStuck(STUCK_OFFER_TIMEOUT)) {
                        slot.status = SlotStatus.STUCK;
                    } else {
                        slot.status = slot.isBuyOffer ? SlotStatus.BUYING : SlotStatus.SELLING;
                    }
                }

                slot.lastUpdateTime = System.currentTimeMillis();

            } else {
                // Slot is empty
                if (slot.status != SlotStatus.EMPTY) {
                    // Offer was just completed or cancelled
                    recordOfferCompletion(slot);
                    slot.status = SlotStatus.EMPTY;
                    slot.itemId = -1;
                    slot.itemName = "";
                }
            }

        } catch (Exception e) {
            BotUtils.logError("Error updating slot " + slotIndex, e);
        }
    }

    /**
     * Record offer completion in history
     */
    private void recordOfferCompletion(GESlot slot) {
        if (slot.offerStartTime > 0) {
            long duration = System.currentTimeMillis() - slot.offerStartTime;
            boolean successful = slot.status == SlotStatus.COMPLETED;

            OfferHistoryEntry entry = new OfferHistoryEntry(
                slot.itemName,
                slot.quantity,
                slot.price,
                slot.isBuyOffer,
                successful,
                duration
            );

            offerHistory.add(entry);

            BotUtils.log("📊 Offer completed: " + slot.itemName +
                       " (" + (successful ? "Success" : "Cancelled") +
                       ", " + (duration / 1000) + "s)");
        }
    }

    // ===========================================
    // SLOT OPERATIONS
    // ===========================================

    /**
     * Find first empty slot
     */
    public int getFirstEmptySlot() {
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (slots[i].status == SlotStatus.EMPTY) {
                return i;
            }
        }
        return -1; // No empty slots
    }

    /**
     * Count empty slots
     */
    public int getEmptySlotCount() {
        int count = 0;
        for (GESlot slot : slots) {
            if (slot.status == SlotStatus.EMPTY) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count active offers
     */
    public int getActiveOfferCount() {
        int count = 0;
        for (GESlot slot : slots) {
            if (slot.status == SlotStatus.BUYING || slot.status == SlotStatus.SELLING) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count completed offers
     */
    public int getCompletedOfferCount() {
        int count = 0;
        for (GESlot slot : slots) {
            if (slot.status == SlotStatus.COMPLETED) {
                count++;
            }
        }
        return count;
    }

    // ===========================================
    // OFFER MANAGEMENT
    // ===========================================

    /**
     * Place a buy offer
     */
    public boolean placeBuyOffer(int itemId, String itemName, int quantity, int price) {
        try {
            int slot = getFirstEmptySlot();
            if (slot == -1) {
                BotUtils.log("❌ No empty GE slots available");

                // Add to queue for later
                queuePurchase(itemId, itemName, quantity, price, true);
                return false;
            }

            if (!GrandExchange.isOpen()) {
                BotUtils.log("❌ GE is not open");
                return false;
            }

            if (GrandExchange.buyItem(itemId, quantity, price)) {
                // Update slot tracking
                GESlot geSlot = slots[slot];
                geSlot.status = SlotStatus.BUYING;
                geSlot.itemId = itemId;
                geSlot.itemName = itemName;
                geSlot.quantity = quantity;
                geSlot.price = price;
                geSlot.isBuyOffer = true;
                geSlot.offerStartTime = System.currentTimeMillis();
                geSlot.lastUpdateTime = System.currentTimeMillis();

                BotUtils.log("✅ Buy offer placed in slot " + slot + ": " +
                           quantity + "x " + itemName + " @ " + BotUtils.formatNumber(price) + " GP");
                return true;
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error placing buy offer", e);
            return false;
        }
    }

    /**
     * Collect all completed offers
     */
    public boolean collectCompletedOffers() {
        try {
            if (!GrandExchange.isOpen()) {
                return false;
            }

            int collected = 0;

            for (int i = 0; i < TOTAL_SLOTS; i++) {
                if (slots[i].status == SlotStatus.COMPLETED) {
                    if (GrandExchange.collect()) {
                        collected++;
                        Sleep.sleep(300, 600);
                    }
                }
            }

            if (collected > 0) {
                BotUtils.log("✅ Collected " + collected + " completed offers");
                return true;
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error collecting offers", e);
            return false;
        }
    }

    /**
     * Cancel stuck offers
     */
    public boolean cancelStuckOffers() {
        try {
            if (!GrandExchange.isOpen()) {
                return false;
            }

            int cancelled = 0;

            for (int i = 0; i < TOTAL_SLOTS; i++) {
                if (slots[i].isStuck(STUCK_OFFER_TIMEOUT)) {
                    BotUtils.log("⚠️ Slot " + i + " stuck (" +
                               (slots[i].getOfferAge() / 1000) + "s) - cancelling...");

                    if (GrandExchange.cancelOffer(i)) {
                        slots[i].status = SlotStatus.CANCELLED;
                        cancelled++;
                        Sleep.sleep(500, 800);
                    }
                }
            }

            if (cancelled > 0) {
                BotUtils.log("🚫 Cancelled " + cancelled + " stuck offers");
                return true;
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error cancelling stuck offers", e);
            return false;
        }
    }

    // ===========================================
    // PURCHASE QUEUE
    // ===========================================

    /**
     * Add purchase to queue
     */
    public void queuePurchase(int itemId, String itemName, int quantity, int price, boolean isBuyOffer) {
        PurchaseRequest request = new PurchaseRequest(itemId, itemName, quantity, price, isBuyOffer);
        purchaseQueue.offer(request);

        BotUtils.log("📋 Added to purchase queue: " + itemName + " (Queue size: " + purchaseQueue.size() + ")");
    }

    /**
     * Process purchase queue
     */
    public void processPurchaseQueue() {
        if (purchaseQueue.isEmpty()) {
            return;
        }

        if (getEmptySlotCount() == 0) {
            return; // No slots available
        }

        // Process as many queued purchases as possible
        while (!purchaseQueue.isEmpty() && getEmptySlotCount() > 0) {
            PurchaseRequest request = purchaseQueue.poll();

            if (request.isBuyOffer) {
                placeBuyOffer(request.itemId, request.itemName, request.quantity, request.price);
            }

            Sleep.sleep(500, 800);
        }
    }

    // ===========================================
    // STATISTICS
    // ===========================================

    /**
     * Get slot status summary
     */
    public String getSlotSummary() {
        return String.format("Slots: %d empty | %d active | %d completed | Queue: %d",
            getEmptySlotCount(),
            getActiveOfferCount(),
            getCompletedOfferCount(),
            purchaseQueue.size()
        );
    }

    /**
     * Get offer history summary
     */
    public String getOfferHistorySummary() {
        if (offerHistory.isEmpty()) {
            return "No offer history";
        }

        int successful = 0;
        long totalDuration = 0;

        for (OfferHistoryEntry entry : offerHistory) {
            if (entry.wasSuccessful) {
                successful++;
            }
            totalDuration += entry.offerDuration;
        }

        long avgDuration = totalDuration / offerHistory.size();

        return String.format("History: %d total | %d successful (%.1f%%) | Avg time: %ds",
            offerHistory.size(),
            successful,
            (successful * 100.0 / offerHistory.size()),
            avgDuration / 1000
        );
    }

    // ===========================================
    // GETTERS/SETTERS
    // ===========================================

    public GESlot[] getSlots() {
        return slots;
    }

    public GESlot getSlot(int index) {
        return index >= 0 && index < TOTAL_SLOTS ? slots[index] : null;
    }

    public void setAutoCollect(boolean enabled) {
        this.autoCollect = enabled;
        BotUtils.log("🏪 Auto-collect " + (enabled ? "enabled" : "disabled"));
    }

    public void setAutoCancelStuck(boolean enabled) {
        this.autoCancelStuck = enabled;
        BotUtils.log("🏪 Auto-cancel stuck offers " + (enabled ? "enabled" : "disabled"));
    }

    public List<OfferHistoryEntry> getOfferHistory() {
        return new ArrayList<>(offerHistory);
    }

    public int getQueueSize() {
        return purchaseQueue.size();
    }
}
