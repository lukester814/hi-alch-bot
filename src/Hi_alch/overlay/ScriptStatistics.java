package Hi_alch.overlay;

import Hi_alch.BotUtils;

/**
 * Comprehensive statistics tracking class for bot scripts
 * Tracks all performance metrics, rates, and runtime data
 */
public class ScriptStatistics {
    // Session information
    public long sessionStartTime = 0;
    public long scriptRuntime = 0;
    public boolean isRunning = false;

    // Core statistics
    public int alchsCompleted = 0;
    public int totalProfit = 0;
    public int xpGained = 0;
    public int itemsBought = 0;
    public int itemsSold = 0;

    // Performance metrics
    public double alchsPerHour = 0.0;
    public double profitPerHour = 0.0;
    public double xpPerHour = 0.0;

    // Current state
    public String currentState = "Initializing";
    public String currentAction = "Starting up";

    // Error tracking
    public int errorsEncountered = 0;
    public String lastError = "";

    // Market data
    public int currentBuyPrice = 0;
    public int currentSellPrice = 0;
    public int currentAlchValue = 0;

    // Advanced metrics
    public long averageAlchTime = 0;
    public double successRate = 100.0;
    public int consecutiveSuccesses = 0;

    // Resource tracking
    public int natureRunesUsed = 0;
    public int natureRunesRemaining = 0;
    public int itemsRemaining = 0;

    /**
     * Calculate derived statistics (rates per hour)
     */
    public void calculateDerivedStats() {
        if (scriptRuntime > 0) {
            double hoursRunning = scriptRuntime / 3600000.0;

            if (hoursRunning > 0) {
                alchsPerHour = alchsCompleted / hoursRunning;
                profitPerHour = totalProfit / hoursRunning;
                xpPerHour = xpGained / hoursRunning;
            }
        }

        // Calculate success rate
        if (alchsCompleted > 0) {
            consecutiveSuccesses = alchsCompleted - errorsEncountered;
            successRate = (consecutiveSuccesses * 100.0) / alchsCompleted;
        }
    }

    /**
     * Reset all statistics
     */
    public void reset() {
        sessionStartTime = System.currentTimeMillis();
        scriptRuntime = 0;
        isRunning = false;

        alchsCompleted = 0;
        totalProfit = 0;
        xpGained = 0;
        itemsBought = 0;
        itemsSold = 0;

        alchsPerHour = 0.0;
        profitPerHour = 0.0;
        xpPerHour = 0.0;

        currentState = "Initializing";
        currentAction = "Starting up";

        errorsEncountered = 0;
        lastError = "";

        currentBuyPrice = 0;
        currentSellPrice = 0;
        currentAlchValue = 0;

        averageAlchTime = 0;
        successRate = 100.0;
        consecutiveSuccesses = 0;

        natureRunesUsed = 0;
        natureRunesRemaining = 0;
        itemsRemaining = 0;
    }

    /**
     * Get formatted statistics summary
     */
    public String getSummary() {
        return String.format("Alchs: %d | Profit: %s | XP: %s | Runtime: %s",
                alchsCompleted,
                BotUtils.formatGP(totalProfit),
                BotUtils.formatNumber(xpGained),
                BotUtils.formatDuration(scriptRuntime));
    }
}
