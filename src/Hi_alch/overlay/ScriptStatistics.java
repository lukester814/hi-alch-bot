package Hi_alch.overlay;

import Hi_alch.BotUtils;

/**
 * Comprehensive statistics tracking for the bot
 *
 * This class maintains all runtime statistics including alchs completed,
 * profit, XP, performance metrics, and error tracking.
 */
public class ScriptStatistics {

    // ===========================================
    // SESSION INFORMATION
    // ===========================================

    public long sessionStartTime = 0;
    public long scriptRuntime = 0;
    public boolean isRunning = false;

    // ===========================================
    // CORE STATISTICS
    // ===========================================

    public int alchsCompleted = 0;
    public int totalProfit = 0;
    public int xpGained = 0;
    public int itemsBought = 0;
    public int itemsSold = 0;

    // ===========================================
    // PERFORMANCE METRICS
    // ===========================================

    public double alchsPerHour = 0.0;
    public double profitPerHour = 0.0;
    public double xpPerHour = 0.0;

    // ===========================================
    // CURRENT STATE
    // ===========================================

    public String currentState = "Initializing";
    public String currentAction = "Starting up";

    // ===========================================
    // ERROR TRACKING
    // ===========================================

    public int errorsEncountered = 0;
    public String lastError = "";

    // ===========================================
    // MARKET DATA
    // ===========================================

    public int currentBuyPrice = 0;
    public int currentSellPrice = 0;
    public int currentAlchValue = 0;

    // ===========================================
    // EFFICIENCY METRICS
    // ===========================================

    public long averageAlchTime = 0;
    public double successRate = 100.0;
    public int consecutiveSuccesses = 0;

    // ===========================================
    // RESOURCES
    // ===========================================

    public int natureRunesUsed = 0;
    public int natureRunesRemaining = 0;
    public int itemsRemaining = 0;

    // ===========================================
    // METHODS
    // ===========================================

    /**
     * Calculate derived statistics based on current data
     */
    public void calculateDerivedStats() {
        if (scriptRuntime > 0) {
            double hoursRunning = scriptRuntime / 3600000.0; // Convert ms to hours

            alchsPerHour = alchsCompleted / hoursRunning;
            profitPerHour = totalProfit / hoursRunning;
            xpPerHour = xpGained / hoursRunning;

            // Calculate success rate
            if (alchsCompleted + errorsEncountered > 0) {
                successRate = ((double) alchsCompleted / (alchsCompleted + errorsEncountered)) * 100.0;
            }

            // Calculate average alch time
            if (alchsCompleted > 0) {
                averageAlchTime = scriptRuntime / alchsCompleted;
            }
        }
    }

    /**
     * Update runtime
     */
    public void updateRuntime() {
        if (sessionStartTime > 0) {
            scriptRuntime = System.currentTimeMillis() - sessionStartTime;
        }
    }

    /**
     * Record a successful alch
     */
    public void recordAlch(int profit, int xp) {
        alchsCompleted++;
        totalProfit += profit;
        xpGained += xp;
        consecutiveSuccesses++;
        natureRunesUsed++;

        calculateDerivedStats();
    }

    /**
     * Record an error
     */
    public void recordError(String error) {
        errorsEncountered++;
        lastError = error;
        consecutiveSuccesses = 0;

        calculateDerivedStats();
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

    /**
     * Get detailed statistics report
     */
    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== STATISTICS REPORT ===\n");
        report.append(String.format("Runtime: %s\n", BotUtils.formatDuration(scriptRuntime)));
        report.append(String.format("Alchs: %d (%.0f/hr)\n", alchsCompleted, alchsPerHour));
        report.append(String.format("Profit: %s (%s/hr)\n",
                BotUtils.formatGP(totalProfit),
                BotUtils.formatGP((int)profitPerHour)));
        report.append(String.format("XP: %s (%.0f/hr)\n",
                BotUtils.formatNumber(xpGained),
                xpPerHour));
        report.append(String.format("Success Rate: %.1f%%\n", successRate));
        report.append(String.format("Errors: %d\n", errorsEncountered));
        report.append(String.format("Nature Runes Used: %d\n", natureRunesUsed));
        return report.toString();
    }
}
