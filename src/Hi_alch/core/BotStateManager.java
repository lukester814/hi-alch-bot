package Hi_alch.core;

/**
 * BotStateManager - Centralized Bot State Management
 *
 * Manages all bot state including:
 * - Running/stopping state
 * - Session timing
 * - Statistics tracking (alchs, profit, XP)
 * - Thread-safe state transitions
 *
 * Extracted from HighAlchBot to improve separation of concerns.
 */
public class BotStateManager {

    // ===========================================
    // STATE FIELDS
    // ===========================================

    private volatile boolean botRunning = false;
    private volatile boolean botStopping = false;
    private long sessionStartTime = 0;
    private final Object stateLock = new Object();

    // Statistics
    private int totalAlchs = 0;
    private int totalProfit = 0;
    private int totalXpGained = 0;

    // ===========================================
    // STATE MANAGEMENT
    // ===========================================

    /**
     * Start the bot - thread-safe
     */
    public synchronized void startBot() {
        synchronized (stateLock) {
            this.botRunning = true;
            this.botStopping = false;
            this.sessionStartTime = System.currentTimeMillis();
        }
    }

    /**
     * Stop the bot - thread-safe
     */
    public synchronized void stopBot() {
        synchronized (stateLock) {
            this.botRunning = false;
            this.botStopping = true;
        }
    }

    /**
     * Emergency stop - immediate halt
     */
    public synchronized void emergencyStop() {
        synchronized (stateLock) {
            this.botRunning = false;
            this.botStopping = true;
        }
    }

    /**
     * Reset bot state (for restart)
     */
    public synchronized void resetState() {
        synchronized (stateLock) {
            this.botRunning = false;
            this.botStopping = false;
            this.sessionStartTime = 0;
        }
    }

    // ===========================================
    // STATE QUERIES
    // ===========================================

    /**
     * Check if bot is currently running
     */
    public boolean isBotRunning() {
        synchronized (stateLock) {
            return botRunning;
        }
    }

    /**
     * Check if bot is in stopping state
     */
    public boolean isBotStopping() {
        synchronized (stateLock) {
            return botStopping;
        }
    }

    /**
     * Check if bot is idle (not running and not stopping)
     */
    public boolean isBotIdle() {
        synchronized (stateLock) {
            return !botRunning && !botStopping;
        }
    }

    // ===========================================
    // SESSION TIMING
    // ===========================================

    /**
     * Get session start time
     */
    public long getSessionStartTime() {
        return sessionStartTime;
    }

    /**
     * Get session runtime in milliseconds
     */
    public long getSessionRuntime() {
        if (sessionStartTime == 0) {
            return 0;
        }
        return System.currentTimeMillis() - sessionStartTime;
    }

    /**
     * Get session runtime formatted as HH:MM:SS
     */
    public String getFormattedRuntime() {
        long runtime = getSessionRuntime();
        long seconds = (runtime / 1000) % 60;
        long minutes = (runtime / (1000 * 60)) % 60;
        long hours = (runtime / (1000 * 60 * 60));
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // ===========================================
    // STATISTICS MANAGEMENT
    // ===========================================

    /**
     * Update bot statistics
     */
    public synchronized void updateStatistics(int alchs, int profit, int xp) {
        this.totalAlchs = alchs;
        this.totalProfit = profit;
        this.totalXpGained = xp;
    }

    /**
     * Increment alchemy count
     */
    public synchronized void incrementAlchs(int count) {
        this.totalAlchs += count;
    }

    /**
     * Add to profit
     */
    public synchronized void addProfit(int profit) {
        this.totalProfit += profit;
    }

    /**
     * Add to XP gained
     */
    public synchronized void addXpGained(int xp) {
        this.totalXpGained += xp;
    }

    /**
     * Get total alchs completed
     */
    public int getTotalAlchs() {
        return totalAlchs;
    }

    /**
     * Get total profit earned
     */
    public int getTotalProfit() {
        return totalProfit;
    }

    /**
     * Get total XP gained
     */
    public int getTotalXpGained() {
        return totalXpGained;
    }

    /**
     * Reset all statistics
     */
    public synchronized void resetStatistics() {
        this.totalAlchs = 0;
        this.totalProfit = 0;
        this.totalXpGained = 0;
    }

    /**
     * Get statistics summary as formatted string
     */
    public String getStatisticsSummary() {
        return String.format(
            "Runtime: %s | Alchs: %d | Profit: %d GP | XP: %d",
            getFormattedRuntime(),
            totalAlchs,
            totalProfit,
            totalXpGained
        );
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Calculate alchs per hour
     */
    public int getAlchsPerHour() {
        long runtime = getSessionRuntime();
        if (runtime == 0) {
            return 0;
        }
        return (int) ((totalAlchs * 3600000.0) / runtime);
    }

    /**
     * Calculate profit per hour
     */
    public int getProfitPerHour() {
        long runtime = getSessionRuntime();
        if (runtime == 0) {
            return 0;
        }
        return (int) ((totalProfit * 3600000.0) / runtime);
    }

    /**
     * Calculate XP per hour
     */
    public int getXpPerHour() {
        long runtime = getSessionRuntime();
        if (runtime == 0) {
            return 0;
        }
        return (int) ((totalXpGained * 3600000.0) / runtime);
    }
}
