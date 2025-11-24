package Hi_alch.managers;

import Hi_alch.BotUtils;
import java.util.Random;

/**
 * Advanced Anti-Ban System for DreamBot Scripts
 *
 * Features:
 * - Human-like mouse movements and timing
 * - Random camera adjustments and tab interactions
 * - Intelligent break scheduling
 * - Activity pattern variation
 * - Realistic delay patterns
 * - Statistical behavior modeling
 *
 * REUSABLE: Perfect anti-ban foundation for any DreamBot script!
 *
 * REFACTORED: Now uses AntibanBehaviors and AntibanBreakManager
 */
public class AntibanSystem {

    // ===========================================
    // CONFIGURATION & CONSTANTS
    // ===========================================

    // Random number generator
    private static final Random RANDOM = new Random();

    // Anti-ban behavior frequencies (lower = more frequent)
    private static final int CAMERA_ADJUSTMENT_CHANCE = 100;  // 1% chance per action
    private static final int TAB_CHECK_CHANCE = 200;          // 0.5% chance per action
    private static final int MOUSE_LEAVE_CHANCE = 150;        // 0.67% chance per action
    private static final int SKILL_CHECK_CHANCE = 300;        // 0.33% chance per action

    // ===========================================
    // COMPONENTS
    // ===========================================

    private AntibanBehaviors behaviors;
    private AntibanBreakManager breakManager;

    // ===========================================
    // STATE TRACKING
    // ===========================================

    // Configuration
    private boolean isEnabled = true;
    private String botType = "generic";
    private int aggressionLevel = 5; // 1-10 scale (1 = very human-like, 10 = aggressive)

    // Behavior tracking
    private long lastActionTime = 0;
    private int totalActions = 0;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public AntibanSystem() {
        this.behaviors = new AntibanBehaviors();
        this.breakManager = new AntibanBreakManager();
        this.lastActionTime = System.currentTimeMillis();

        // Configure mouse settings for more human-like behavior
        configureMouseSettings();

        BotUtils.log("🛡️ AntibanSystem initialized with human-like behavior patterns");
    }

    /**
     * Constructor with configuration
     */
    public AntibanSystem(boolean enabled, String botType, int aggressionLevel) {
        this();
        this.isEnabled = enabled;
        this.botType = botType;
        this.aggressionLevel = BotUtils.clamp(aggressionLevel, 1, 10);

        BotUtils.log("🛡️ AntibanSystem configured: " + botType + " (aggression: " + aggressionLevel + ")");
    }

    // ===========================================
    // MAIN INTERFACE METHODS
    // ===========================================

    /**
     * Configure the anti-ban system
     */
    public void configure(boolean enabled, String botType, int aggressionLevel) {
        this.isEnabled = enabled;
        this.botType = botType;
        this.aggressionLevel = BotUtils.clamp(aggressionLevel, 1, 10);

        BotUtils.log("⚙️ AntibanSystem configured: " + (enabled ? "ENABLED" : "DISABLED"));
        BotUtils.log("🎯 Bot type: " + botType + " | Aggression: " + aggressionLevel + "/10");

        // Adjust mouse settings based on aggression level
        configureMouseSettings();
    }

    /**
     * Main update method - call this from your bot's main loop
     */
    public void update() {
        if (!isEnabled) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Check if we should take a break
        if (breakManager.shouldTakeBreak()) {
            breakManager.initiateBreak();
            return;
        }

        // If on break, handle break logic
        if (breakManager.isOnBreak()) {
            breakManager.handleBreak();
            return;
        }

        // Update action tracking
        totalActions++;
        lastActionTime = currentTime;

        // Perform random anti-ban behaviors
        performRandomBehaviors();
    }

    /**
     * Get human-like delay for actions
     */
    public int getActionDelay() {
        // Base delay varies by aggression level
        int baseDelay = 1800 - (aggressionLevel * 100); // 1700ms to 900ms range
        int variance = baseDelay / 3; // 33% variance

        int delay = baseDelay + RANDOM.nextInt(variance * 2) - variance;

        // Add occasional longer pauses (human hesitation)
        if (RANDOM.nextInt(20) == 0) { // 5% chance
            delay += BotUtils.randomDelay(1000, 3000);
        }

        return Math.max(delay, 500); // Minimum 500ms
    }

    /**
     * Get delay between similar actions (prevents repetitive patterns)
     */
    public int getSimilarActionDelay() {
        return getActionDelay() + BotUtils.randomDelay(200, 800);
    }

    // ===========================================
    // BEHAVIOR METHODS
    // ===========================================

    /**
     * Perform random anti-ban behaviors
     */
    private void performRandomBehaviors() {
        // Camera adjustment
        if (shouldPerformBehavior(CAMERA_ADJUSTMENT_CHANCE)) {
            behaviors.performCameraAdjustment();
        }

        // Tab checking
        if (shouldPerformBehavior(TAB_CHECK_CHANCE)) {
            behaviors.performTabCheck();
        }

        // Mouse leaving game area
        if (shouldPerformBehavior(MOUSE_LEAVE_CHANCE)) {
            behaviors.performMouseLeave();
        }

        // Skill checking (for relevant bot types)
        if (shouldPerformBehavior(SKILL_CHECK_CHANCE) && botType.contains("skill")) {
            behaviors.performSkillCheck();
        }
    }

    /**
     * Check if we should perform a behavior based on chance
     */
    private boolean shouldPerformBehavior(int chance) {
        // Adjust chance based on aggression level
        // Higher aggression = less anti-ban behaviors
        int adjustedChance = chance + (aggressionLevel * 50);

        return RANDOM.nextInt(adjustedChance) == 0;
    }

    // ===========================================
    // CONFIGURATION METHODS
    // ===========================================

    /**
     * Configure mouse settings for human-like behavior
     */
    private void configureMouseSettings() {
        try {
            // Adjust mouse speed based on aggression level
            // Lower aggression = slower, more human-like mouse
            int mouseSpeed = 80 + (aggressionLevel * 10); // 90-180 range

            // Note: Actual MouseSettings configuration would depend on DreamBot API
            // This is a placeholder for the concept
            BotUtils.log("🖱️ Mouse configured for aggression level " + aggressionLevel);

        } catch (Exception e) {
            BotUtils.logError("Error configuring mouse settings", e);
        }
    }

    // ===========================================
    // STATUS & STATISTICS
    // ===========================================

    /**
     * Check if currently on break
     */
    public boolean isOnBreak() {
        return breakManager.isOnBreak();
    }

    /**
     * Get time remaining in current break
     */
    public long getBreakTimeRemaining() {
        return breakManager.getBreakTimeRemaining();
    }

    /**
     * Get time until next break
     */
    public long getTimeUntilNextBreak() {
        return breakManager.getTimeUntilNextBreak();
    }

    /**
     * Get anti-ban statistics
     */
    public String getStatistics() {
        return String.format(
                "Actions: %d | Camera: %d | Tabs: %d | Breaks: %d",
                totalActions,
                behaviors.getCameraAdjustments(),
                behaviors.getTabChecks(),
                breakManager.getBreaksTriggered()
        );
    }

    /**
     * Reset statistics
     */
    public void resetStatistics() {
        totalActions = 0;
        behaviors.resetStatistics();
        breakManager.resetStatistics();

        BotUtils.log("📊 Anti-ban statistics reset");
    }

    // ===========================================
    // GETTERS & SETTERS
    // ===========================================

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { this.isEnabled = enabled; }

    public String getBotType() { return botType; }
    public void setBotType(String botType) { this.botType = botType; }

    public int getAggressionLevel() { return aggressionLevel; }
    public void setAggressionLevel(int level) {
        this.aggressionLevel = BotUtils.clamp(level, 1, 10);
        configureMouseSettings();
    }

    public int getTotalActions() { return totalActions; }
    public int getCameraAdjustments() { return behaviors.getCameraAdjustments(); }
    public int getTabChecks() { return behaviors.getTabChecks(); }
    public int getBreaksTriggered() { return breakManager.getBreaksTriggered(); }
}
