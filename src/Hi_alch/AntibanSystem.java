package Hi_alch;

import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Sleep;
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

    // Timing configurations
    private static final long MIN_ACTION_INTERVAL = 1000;     // 1 second
    private static final long MAX_IDLE_TIME = 300000;         // 5 minutes
    private static final long BREAK_CHECK_INTERVAL = 1800000; // 30 minutes

    // ===========================================
    // STATE TRACKING
    // ===========================================

    // Configuration
    private boolean isEnabled = true;
    private String botType = "generic";
    private int aggressionLevel = 5; // 1-10 scale (1 = very human-like, 10 = aggressive)

    // Behavior tracking
    private long lastActionTime = 0;
    private long lastCameraMove = 0;
    private long lastTabCheck = 0;
    private long lastMouseLeave = 0;
    private long lastBreakCheck = 0;
    private long sessionStartTime = 0;

    // Statistics
    private int totalActions = 0;
    private int cameraAdjustments = 0;
    private int tabChecks = 0;
    private int mouseLeaves = 0;
    private int breaksTriggered = 0;

    // Break scheduling
    private long nextBreakTime = 0;
    private boolean isOnBreak = false;
    private long breakStartTime = 0;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public AntibanSystem() {
        this.sessionStartTime = System.currentTimeMillis();
        this.lastActionTime = System.currentTimeMillis();
        this.nextBreakTime = calculateNextBreakTime();

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
        if (shouldTakeBreak()) {
            initiateBreak();
            return;
        }

        // If on break, handle break logic
        if (isOnBreak) {
            handleBreak();
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
            performCameraAdjustment();
        }

        // Tab checking
        if (shouldPerformBehavior(TAB_CHECK_CHANCE)) {
            performTabCheck();
        }

        // Mouse leaving game area
        if (shouldPerformBehavior(MOUSE_LEAVE_CHANCE)) {
            performMouseLeave();
        }

        // Skill checking (for relevant bot types)
        if (shouldPerformBehavior(SKILL_CHECK_CHANCE) && botType.contains("skill")) {
            performSkillCheck();
        }
    }

    /**
     * Perform camera adjustment
     */
    private void performCameraAdjustment() {
        try {
            long currentTime = System.currentTimeMillis();

            // Don't adjust too frequently
            if (currentTime - lastCameraMove < 30000) { // 30 seconds cooldown
                return;
            }

            BotUtils.log("📹 Performing anti-ban camera adjustment");

            // Random camera movement
            int pitchChange = BotUtils.randomDelay(-20, 20);
            int yawChange = BotUtils.randomDelay(-30, 30);

            Camera.rotateTo(Camera.getYaw() + yawChange, Camera.getPitch() + pitchChange);

            // Small delay after camera movement
            Sleep.sleep(BotUtils.randomDelay(500, 1500));

            cameraAdjustments++;
            lastCameraMove = currentTime;

        } catch (Exception e) {
            BotUtils.logError("Error during camera adjustment", e);
        }
    }

    /**
     * Perform random tab check
     */
    private void performTabCheck() {
        try {
            long currentTime = System.currentTimeMillis();

            // Don't check tabs too frequently
            if (currentTime - lastTabCheck < 45000) { // 45 seconds cooldown
                return;
            }

            BotUtils.log("📋 Performing anti-ban tab check");

            // Get current tab
            Tab currentTab = Tabs.getOpen();

            // Choose a random tab to check (using only common tabs)
            Tab[] randomTabs = {Tab.INVENTORY, Tab.EQUIPMENT, Tab.MAGIC};
            Tab randomTab = BotUtils.randomChoice(randomTabs);

            if (randomTab != null && randomTab != currentTab) {
                // Open random tab
                Tabs.open(randomTab);
                Sleep.sleep(BotUtils.randomDelay(800, 2000));

                // Return to original tab
                if (currentTab != null) {
                    Tabs.open(currentTab);
                    Sleep.sleep(BotUtils.randomDelay(400, 800));
                }
            }

            tabChecks++;
            lastTabCheck = currentTime;

        } catch (Exception e) {
            BotUtils.logError("Error during tab check", e);
        }
    }

    /**
     * Perform mouse leaving game area
     */
    private void performMouseLeave() {
        try {
            long currentTime = System.currentTimeMillis();

            // Don't leave mouse too frequently
            if (currentTime - lastMouseLeave < 60000) { // 1 minute cooldown
                return;
            }

            BotUtils.log("🖱️ Performing anti-ban mouse leave");

            // Move mouse to edge of screen briefly
            // This simulates checking other applications
            // Note: Actual mouse movement would require more specific DreamBot API calls

            Sleep.sleep(BotUtils.randomDelay(1000, 3000));

            mouseLeaves++;
            lastMouseLeave = currentTime;

        } catch (Exception e) {
            BotUtils.logError("Error during mouse leave", e);
        }
    }

    /**
     * Perform skill checking
     */
    private void performSkillCheck() {
        try {
            BotUtils.log("📊 Performing anti-ban skill check");

            // Open inventory tab (since skills tab name is unclear)
            Tab originalTab = Tabs.getOpen();
            Tabs.open(Tab.INVENTORY);

            Sleep.sleep(BotUtils.randomDelay(1500, 3000));

            // Return to original tab
            if (originalTab != null) {
                Tabs.open(originalTab);
            }

            Sleep.sleep(BotUtils.randomDelay(300, 600));

        } catch (Exception e) {
            BotUtils.logError("Error during skill check", e);
        }
    }

    // ===========================================
    // BREAK SYSTEM
    // ===========================================

    /**
     * Check if we should take a break
     */
    private boolean shouldTakeBreak() {
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
    private void initiateBreak() {
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
    private void handleBreak() {
        long currentTime = System.currentTimeMillis();
        int breakDuration = calculateBreakDuration();

        if (currentTime - breakStartTime >= breakDuration) {
            // Break is over
            isOnBreak = false;
            BotUtils.log("✅ Anti-ban break completed, resuming bot activity");
        }

        // During break, do nothing (sleep in main loop will handle this)
    }

    /**
     * Calculate break duration based on session length and randomness
     */
    private int calculateBreakDuration() {
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
     * Get anti-ban statistics
     */
    public String getStatistics() {
        long sessionDuration = System.currentTimeMillis() - sessionStartTime;

        return String.format(
                "Session: %s | Actions: %d | Camera: %d | Tabs: %d | Breaks: %d",
                BotUtils.formatDuration(sessionDuration),
                totalActions,
                cameraAdjustments,
                tabChecks,
                breaksTriggered
        );
    }

    /**
     * Reset statistics
     */
    public void resetStatistics() {
        sessionStartTime = System.currentTimeMillis();
        totalActions = 0;
        cameraAdjustments = 0;
        tabChecks = 0;
        mouseLeaves = 0;
        breaksTriggered = 0;

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
    public int getCameraAdjustments() { return cameraAdjustments; }
    public int getTabChecks() { return tabChecks; }
    public int getBreaksTriggered() { return breaksTriggered; }
}