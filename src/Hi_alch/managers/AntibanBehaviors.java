package Hi_alch.managers;

import Hi_alch.BotUtils;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Sleep;

/**
 * Anti-Ban Behavior Executor
 *
 * Handles execution of anti-ban behaviors:
 * - Camera adjustments
 * - Tab checking
 * - Mouse movement
 * - Skill checking
 *
 * REUSABLE: Perfect for any DreamBot script!
 */
public class AntibanBehaviors {

    // Cooldown tracking
    private long lastCameraMove = 0;
    private long lastTabCheck = 0;
    private long lastMouseLeave = 0;

    // Statistics
    private int cameraAdjustments = 0;
    private int tabChecks = 0;
    private int mouseLeaves = 0;

    // ===========================================
    // BEHAVIOR EXECUTION
    // ===========================================

    /**
     * Perform camera adjustment
     */
    public void performCameraAdjustment() {
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
    public void performTabCheck() {
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
    public void performMouseLeave() {
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
    public void performSkillCheck() {
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
    // STATISTICS
    // ===========================================

    public int getCameraAdjustments() {
        return cameraAdjustments;
    }

    public int getTabChecks() {
        return tabChecks;
    }

    public int getMouseLeaves() {
        return mouseLeaves;
    }

    public void resetStatistics() {
        cameraAdjustments = 0;
        tabChecks = 0;
        mouseLeaves = 0;
    }
}
