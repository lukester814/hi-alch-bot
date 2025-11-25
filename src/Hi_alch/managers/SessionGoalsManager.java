package Hi_alch.managers;

import Hi_alch.utils.BotUtils;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

/**
 * Session Goals & Auto-Stop Manager
 *
 * Features:
 * - Multiple goal types (alchs, profit, XP, time, level)
 * - AND/OR logic for multiple conditions
 * - Progress tracking and notifications
 * - Auto-stop when goals reached
 * - Discord notifications on goal completion
 */
public class SessionGoalsManager {

    // ===========================================
    // GOAL CONFIGURATION
    // ===========================================

    public static class SessionGoals {
        // Goal enablement
        public boolean alchGoalEnabled = false;
        public boolean profitGoalEnabled = false;
        public boolean xpGoalEnabled = false;
        public boolean timeGoalEnabled = false;
        public boolean levelGoalEnabled = false;

        // Goal targets
        public int targetAlchs = 1000;
        public int targetProfit = 100000;
        public int targetXP = 50000;
        public int targetMinutes = 120;
        public int targetLevel = 70;

        // Logic mode
        public boolean useAndLogic = false; // true = AND, false = OR

        // Notification settings
        public boolean notifyOnGoal = true;
        public boolean discordNotify = true;
        public boolean soundAlert = true;

        public SessionGoals() {}

        public boolean hasAnyGoalEnabled() {
            return alchGoalEnabled || profitGoalEnabled || xpGoalEnabled ||
                   timeGoalEnabled || levelGoalEnabled;
        }

        public int getEnabledGoalCount() {
            int count = 0;
            if (alchGoalEnabled) count++;
            if (profitGoalEnabled) count++;
            if (xpGoalEnabled) count++;
            if (timeGoalEnabled) count++;
            if (levelGoalEnabled) count++;
            return count;
        }
    }

    // ===========================================
    // STATE
    // ===========================================

    private SessionGoals goals;
    private DiscordManager discordManager;

    // Starting values
    private long sessionStartTime;
    private int startingMagicXP;
    private int startingMagicLevel;

    // Current progress
    private int currentAlchs = 0;
    private int currentProfit = 0;
    private int currentXP = 0;
    private long currentRuntime = 0;
    private int currentLevel = 0;

    // Goal tracking
    private boolean goalReached = false;
    private String goalReachedMessage = "";
    private boolean notificationSent = false;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public SessionGoalsManager(DiscordManager discordManager) {
        this.goals = new SessionGoals();
        this.discordManager = discordManager;
        this.sessionStartTime = System.currentTimeMillis();

        try {
            this.startingMagicXP = Skills.getExperience(Skill.MAGIC);
            this.startingMagicLevel = Skills.getRealLevel(Skill.MAGIC);
        } catch (Exception e) {
            this.startingMagicXP = 0;
            this.startingMagicLevel = 1;
        }

        BotUtils.log("🎯 SessionGoalsManager initialized");
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    public void setGoals(SessionGoals goals) {
        this.goals = goals;
        this.goalReached = false;
        this.goalReachedMessage = "";
        this.notificationSent = false;

        if (goals.hasAnyGoalEnabled()) {
            String logic = goals.useAndLogic ? "ALL" : "ANY";
            BotUtils.log("🎯 Session goals configured (" + logic + " of " +
                         goals.getEnabledGoalCount() + " goals):");

            if (goals.alchGoalEnabled) {
                BotUtils.log("   📦 Alchs: " + BotUtils.formatNumber(goals.targetAlchs));
            }
            if (goals.profitGoalEnabled) {
                BotUtils.log("   💰 Profit: " + BotUtils.formatNumber(goals.targetProfit) + " GP");
            }
            if (goals.xpGoalEnabled) {
                BotUtils.log("   ⭐ XP: " + BotUtils.formatNumber(goals.targetXP));
            }
            if (goals.timeGoalEnabled) {
                BotUtils.log("   ⏰ Time: " + goals.targetMinutes + " minutes");
            }
            if (goals.levelGoalEnabled) {
                BotUtils.log("   📊 Level: " + goals.targetLevel);
            }
        } else {
            BotUtils.log("🎯 No session goals configured - will run until manual stop");
        }
    }

    public SessionGoals getGoals() {
        return goals;
    }

    // ===========================================
    // PROGRESS TRACKING
    // ===========================================

    public void updateProgress(int alchs, int profit) {
        this.currentAlchs = alchs;
        this.currentProfit = profit;
        this.currentRuntime = System.currentTimeMillis() - sessionStartTime;

        try {
            int currentXP = Skills.getExperience(Skill.MAGIC);
            this.currentXP = currentXP - startingMagicXP;
            this.currentLevel = Skills.getRealLevel(Skill.MAGIC);
        } catch (Exception e) {
            // Ignore if skills not available
        }
    }

    // ===========================================
    // GOAL CHECKING
    // ===========================================

    /**
     * Check if any/all goals are reached based on logic mode
     * @return true if session should stop
     */
    public boolean shouldStop() {
        if (!goals.hasAnyGoalEnabled()) {
            return false; // No goals = run forever
        }

        if (goalReached) {
            return true; // Already reached
        }

        // Check each enabled goal
        boolean alchGoalMet = !goals.alchGoalEnabled || currentAlchs >= goals.targetAlchs;
        boolean profitGoalMet = !goals.profitGoalEnabled || currentProfit >= goals.targetProfit;
        boolean xpGoalMet = !goals.xpGoalEnabled || currentXP >= goals.targetXP;
        boolean timeGoalMet = !goals.timeGoalEnabled ||
                              (currentRuntime / 60000) >= goals.targetMinutes;
        boolean levelGoalMet = !goals.levelGoalEnabled || currentLevel >= goals.targetLevel;

        boolean shouldStop;

        if (goals.useAndLogic) {
            // AND logic - all enabled goals must be met
            shouldStop = alchGoalMet && profitGoalMet && xpGoalMet &&
                        timeGoalMet && levelGoalMet;
        } else {
            // OR logic - any enabled goal can trigger stop
            shouldStop = (goals.alchGoalEnabled && alchGoalMet) ||
                        (goals.profitGoalEnabled && profitGoalMet) ||
                        (goals.xpGoalEnabled && xpGoalMet) ||
                        (goals.timeGoalEnabled && timeGoalMet) ||
                        (goals.levelGoalEnabled && levelGoalMet);
        }

        if (shouldStop && !goalReached) {
            onGoalReached();
        }

        return goalReached;
    }

    /**
     * Called when goal(s) are reached
     */
    private void onGoalReached() {
        goalReached = true;

        // Build completion message
        StringBuilder message = new StringBuilder("🎯 Session Goal" +
                (goals.getEnabledGoalCount() > 1 ? "s" : "") + " Reached!\n");

        if (goals.alchGoalEnabled && currentAlchs >= goals.targetAlchs) {
            message.append("   ✅ Alchs: ").append(BotUtils.formatNumber(currentAlchs))
                   .append("/").append(BotUtils.formatNumber(goals.targetAlchs)).append("\n");
        }
        if (goals.profitGoalEnabled && currentProfit >= goals.targetProfit) {
            message.append("   ✅ Profit: ").append(BotUtils.formatNumber(currentProfit))
                   .append("/").append(BotUtils.formatNumber(goals.targetProfit)).append(" GP\n");
        }
        if (goals.xpGoalEnabled && currentXP >= goals.targetXP) {
            message.append("   ✅ XP: ").append(BotUtils.formatNumber(currentXP))
                   .append("/").append(BotUtils.formatNumber(goals.targetXP)).append("\n");
        }
        if (goals.timeGoalEnabled && (currentRuntime / 60000) >= goals.targetMinutes) {
            message.append("   ✅ Time: ").append(currentRuntime / 60000)
                   .append("/").append(goals.targetMinutes).append(" minutes\n");
        }
        if (goals.levelGoalEnabled && currentLevel >= goals.targetLevel) {
            message.append("   ✅ Level: ").append(currentLevel)
                   .append("/").append(goals.targetLevel).append("\n");
        }

        goalReachedMessage = message.toString();
        BotUtils.log(goalReachedMessage);

        // Send notifications
        if (goals.notifyOnGoal && !notificationSent) {
            sendNotifications();
            notificationSent = true;
        }
    }

    /**
     * Send goal completion notifications
     */
    private void sendNotifications() {
        try {
            // Discord notification
            if (goals.discordNotify && discordManager != null) {
                discordManager.sendSessionGoalReached(
                    currentAlchs,
                    currentProfit,
                    currentXP,
                    currentRuntime / 60000,
                    currentLevel,
                    goals
                );
                BotUtils.log("📨 Discord notification sent");
            }

            // Sound alert
            if (goals.soundAlert) {
                playGoalSound();
            }

        } catch (Exception e) {
            BotUtils.logError("Error sending goal notifications", e);
        }
    }

    /**
     * Play sound alert (basic beep)
     */
    private void playGoalSound() {
        try {
            java.awt.Toolkit.getDefaultToolkit().beep();
            BotUtils.log("🔔 Goal sound played");
        } catch (Exception e) {
            // Ignore if sound not available
        }
    }

    // ===========================================
    // PROGRESS REPORTING
    // ===========================================

    /**
     * Get progress percentage for a specific goal
     */
    public int getGoalProgress(String goalType) {
        switch (goalType.toLowerCase()) {
            case "alchs":
                return goals.alchGoalEnabled ?
                       Math.min(100, (currentAlchs * 100) / goals.targetAlchs) : 0;
            case "profit":
                return goals.profitGoalEnabled ?
                       Math.min(100, (currentProfit * 100) / goals.targetProfit) : 0;
            case "xp":
                return goals.xpGoalEnabled ?
                       Math.min(100, (currentXP * 100) / goals.targetXP) : 0;
            case "time":
                return goals.timeGoalEnabled ?
                       Math.min(100, (int)((currentRuntime / 60000) * 100) / goals.targetMinutes) : 0;
            case "level":
                return goals.levelGoalEnabled ?
                       Math.min(100, (currentLevel * 100) / goals.targetLevel) : 0;
            default:
                return 0;
        }
    }

    /**
     * Get overall progress percentage
     */
    public int getOverallProgress() {
        if (!goals.hasAnyGoalEnabled()) {
            return 0;
        }

        int totalProgress = 0;
        int enabledCount = 0;

        if (goals.alchGoalEnabled) {
            totalProgress += getGoalProgress("alchs");
            enabledCount++;
        }
        if (goals.profitGoalEnabled) {
            totalProgress += getGoalProgress("profit");
            enabledCount++;
        }
        if (goals.xpGoalEnabled) {
            totalProgress += getGoalProgress("xp");
            enabledCount++;
        }
        if (goals.timeGoalEnabled) {
            totalProgress += getGoalProgress("time");
            enabledCount++;
        }
        if (goals.levelGoalEnabled) {
            totalProgress += getGoalProgress("level");
            enabledCount++;
        }

        return enabledCount > 0 ? totalProgress / enabledCount : 0;
    }

    /**
     * Get formatted progress summary
     */
    public String getProgressSummary() {
        if (!goals.hasAnyGoalEnabled()) {
            return "No goals set";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Progress: ").append(getOverallProgress()).append("%\n");

        if (goals.alchGoalEnabled) {
            summary.append("  Alchs: ").append(currentAlchs).append("/")
                   .append(goals.targetAlchs).append(" (").append(getGoalProgress("alchs")).append("%)\n");
        }
        if (goals.profitGoalEnabled) {
            summary.append("  Profit: ").append(BotUtils.formatNumber(currentProfit)).append("/")
                   .append(BotUtils.formatNumber(goals.targetProfit)).append(" GP (")
                   .append(getGoalProgress("profit")).append("%)\n");
        }
        if (goals.xpGoalEnabled) {
            summary.append("  XP: ").append(BotUtils.formatNumber(currentXP)).append("/")
                   .append(BotUtils.formatNumber(goals.targetXP)).append(" (")
                   .append(getGoalProgress("xp")).append("%)\n");
        }
        if (goals.timeGoalEnabled) {
            summary.append("  Time: ").append(currentRuntime / 60000).append("/")
                   .append(goals.targetMinutes).append(" min (")
                   .append(getGoalProgress("time")).append("%)\n");
        }
        if (goals.levelGoalEnabled) {
            summary.append("  Level: ").append(currentLevel).append("/")
                   .append(goals.targetLevel).append(" (")
                   .append(getGoalProgress("level")).append("%)\n");
        }

        return summary.toString();
    }

    // ===========================================
    // GETTERS
    // ===========================================

    public boolean isGoalReached() {
        return goalReached;
    }

    public String getGoalReachedMessage() {
        return goalReachedMessage;
    }

    public void reset() {
        goalReached = false;
        goalReachedMessage = "";
        notificationSent = false;
        currentAlchs = 0;
        currentProfit = 0;
        currentXP = 0;
        currentRuntime = 0;
        sessionStartTime = System.currentTimeMillis();

        try {
            startingMagicXP = Skills.getExperience(Skill.MAGIC);
            startingMagicLevel = Skills.getRealLevel(Skill.MAGIC);
        } catch (Exception e) {
            startingMagicXP = 0;
            startingMagicLevel = 1;
        }

        BotUtils.log("🔄 Session goals reset");
    }
}
