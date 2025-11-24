package Hi_alch.managers;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;

/**
 * Bank Navigation Handler
 *
 * Handles all bank navigation:
 * - Walk to preferred bank
 * - Find and walk to nearest bank
 * - Bank location management
 */
public class BankNavigator {

    private BankManager.BankLocation preferredLocation;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public BankNavigator(BankManager.BankLocation preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    public void setPreferredLocation(BankManager.BankLocation location) {
        this.preferredLocation = location;
    }

    public BankManager.BankLocation getPreferredLocation() {
        return preferredLocation;
    }

    // ===========================================
    // NAVIGATION
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

            BankManager.BankLocation nearest = null;
            int shortestDistance = Integer.MAX_VALUE;

            for (BankManager.BankLocation location : BankManager.BankLocation.values()) {
                try {
                    double rawDistance = org.dreambot.api.methods.interactive.Players.getLocal()
                                   .getTile().distance(location.getArea().getCenter());
                    int distance = (int) Math.round(rawDistance);

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
                preferredLocation = nearest;
                return walkToBank();
            }

            BotUtils.log("❌ No nearby banks found");
            return false;

        } catch (Exception e) {
            BotUtils.logError("Error finding nearest bank", e);
            return false;
        }
    }
}
