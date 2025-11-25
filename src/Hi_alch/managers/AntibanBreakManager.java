package Hi_alch.managers;

import Hi_alch.utils.BotUtils;
import java.util.Random;

/**
 * Anti-Ban Break Manager
 *
 * Handles intelligent break scheduling:
 * - Scheduled breaks at realistic intervals
 * - Fatigue-based break probability
 * - Dynamic break durations
 * - Session tracking
 *
 * REUSABLE: Perfect for any DreamBot script!
 */
public class AntibanBreakManager {

    // Random number generator
    private static final Random RANDOM = new Random();

    // Session tracking
    private long sessionStartTime = 0;
    private long nextBreakTime = 0;
    private boolean isOnBreak = false;
    private long breakStartTime = 0;
    private int breaksTriggered = 0;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public AntibanBreakManager() {
        this.sessionStartTime = System.currentTimeMillis();
        this.nextBreakTime = calculateNextBreakTime();
    }

    // ===========================================
    // BREAK LOGIC
    // ===========================================

    /**
     * Check if we should take a break
     */
    public boolean shouldTakeBreak() {
        long currentTime = System.currentTimeMillis();

        // Check if it's time for scheduled break
        if (currentTime >= nextBreakTime) {
            return true;
        }

        // Check for fatigue-based break (longer sessions = more likely)
        long sessionDuration = currentTime - sessionStartTime;
        if (sessionDuration > 2 * 60 * 60 * 1000) { // After 2 hours
            // Increasing chance of break the longer we run
            int breakChance = (int) (sessionDuration / (60 * 60 * 1000)); // 1% per hour
            if (RANDOM.nextInt(1000) < breakChance) {
                return true;
            }
        }

        return false;
    }

    /**
     * Initiate a break
     */
    public void initiateBreak() {
        if (isOnBreak) {
            return;
        }

        int breakDuration = calculateBreakDuration();

        BotUtils.log("😴 Anti-ban break initiated: " + BotUtils.formatDuration(breakDuration));
        BotUtils.log("⏰ Break will end at: " + BotUtils.getCurrentTimeString());

        isOnBreak = true;
        breakStartTime = System.currentTimeMillis();
        breaksTriggered++;

        // Schedule next break
        nextBreakTime = System.currentTimeMillis() + breakDuration + calculateNextBreakInterval();
    }

    /**
     * Handle break logic
     */
    public void handleBreak() {
        long currentTime = System.currentTimeMillis();
        int breakDuration = calculateBreakDuration();

        if (currentTime - breakStartTime >= breakDuration) {
            // Break is over
            isOnBreak = false;
            BotUtils.log("✅ Anti-ban break completed, resuming bot activity");
        }

        // During break, do nothing (sleep in main loop will handle this)
    }

    // ===========================================
    // CALCULATION METHODS
    // ===========================================

    /**
     * Calculate break duration based on session length and randomness
     */
    public int calculateBreakDuration() {
        // Base break: 2-8 minutes
        int baseBreak = BotUtils.randomDelay(2 * 60 * 1000, 8 * 60 * 1000);

        // Longer sessions get longer breaks
        long sessionDuration = System.currentTimeMillis() - sessionStartTime;
        if (sessionDuration > 4 * 60 * 60 * 1000) { // After 4 hours
            baseBreak += BotUtils.randomDelay(5 * 60 * 1000, 15 * 60 * 1000); // +5-15 minutes
        }

        return baseBreak;
    }

    /**
     * Calculate time until next break
     */
    private long calculateNextBreakTime() {
        // Next break in 45 minutes to 2 hours
        long interval = BotUtils.randomDelay(45 * 60 * 1000, 120 * 60 * 1000);
        return System.currentTimeMillis() + interval;
    }

    /**
     * Calculate interval between breaks
     */
    private long calculateNextBreakInterval() {
        // 30 minutes to 3 hours between breaks
        return BotUtils.randomDelay(30 * 60 * 1000, 180 * 60 * 1000);
    }

    // ===========================================
    // STATUS & STATISTICS
    // ===========================================

    /**
     * Check if currently on break
     */
    public boolean isOnBreak() {
        return isOnBreak;
    }

    /**
     * Get time remaining in current break
     */
    public long getBreakTimeRemaining() {
        if (!isOnBreak) {
            return 0;
        }

        long breakDuration = calculateBreakDuration();
        long elapsed = System.currentTimeMillis() - breakStartTime;
        return Math.max(0, breakDuration - elapsed);
    }

    /**
     * Get time until next break
     */
    public long getTimeUntilNextBreak() {
        return Math.max(0, nextBreakTime - System.currentTimeMillis());
    }

    /**
     * Get breaks triggered count
     */
    public int getBreaksTriggered() {
        return breaksTriggered;
    }

    /**
     * Reset statistics
     */
    public void resetStatistics() {
        sessionStartTime = System.currentTimeMillis();
        breaksTriggered = 0;
    }
}
