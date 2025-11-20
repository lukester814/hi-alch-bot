package Hi_alch.core;

import Hi_alch.*;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.skills.Skill;

/**
 * ExecutionCoordinator - Coordinates Bot Execution Loop
 *
 * Responsibilities:
 * - Main bot loop coordination
 * - Anti-ban system updates
 * - GE slot management
 * - CSV recording of alchs
 * - Session summary export
 * - Alchemy engine execution
 * - Overlay statistics updates
 * - Discord notifications
 * - Bot stop/cleanup
 *
 * This class orchestrates the main bot execution flow,
 * coordinating all the different subsystems to work together.
 *
 * Extracted from HighAlchBot Phase 3 refactoring.
 */
public class ExecutionCoordinator {

    private final ComponentManager componentManager;
    private final BotStateManager stateManager;
    private AlchBotGUI.GUIConfiguration currentConfig;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    /**
     * Create new execution coordinator
     * @param componentManager Component manager for accessing components
     * @param stateManager State manager for bot state
     */
    public ExecutionCoordinator(ComponentManager componentManager, BotStateManager stateManager) {
        this.componentManager = componentManager;
        this.stateManager = stateManager;
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    /**
     * Set current configuration
     */
    public void setConfiguration(AlchBotGUI.GUIConfiguration config) {
        this.currentConfig = config;
    }

    // ===========================================
    // MAIN LOOP EXECUTION
    // ===========================================

    /**
     * Execute main bot loop - returns delay for next iteration
     */
    public int executeMainLoop() {
        try {
            // Check if bot should be running
            if (!stateManager.isBotRunning()) {
                return 1000; // Wait for user to start bot via GUI
            }

            if (stateManager.isBotStopping()) {
                return -1; // Signal script termination
            }

            // 1. Check Session Goals - HIGHEST PRIORITY
            if (shouldStopForGoals()) {
                return -1; // Stop bot
            }

            // 2. Update Session Goals Progress
            updateSessionGoalsProgress();

            // 3. Update GE Slot Manager (if at GE)
            updateGESlotManager();

            // 4. Ensure we're at the correct location
            int locationDelay = handleLocationManagement();
            if (locationDelay > 0) {
                return locationDelay;
            }

            // 5. Location-specific antiban
            performLocationAntiban();

            // 6. Update anti-ban system
            updateAntibanSystem();

            // 7. Update overlay statistics
            updateOverlayStatistics();

            // 8. Main bot logic using AlchingEngine
            int delay = executeAlchingEngine();

            // 9. Apply location-specific delay multiplier
            delay = applyLocationDelayMultiplier(delay);

            return delay;

        } catch (Exception e) {
            BotUtils.logError("Error in main bot loop", e);
            return 1000; // Continue running but wait before next iteration
        }
    }

    // ===========================================
    // SESSION GOALS MANAGEMENT
    // ===========================================

    /**
     * Check if bot should stop for session goals
     */
    private boolean shouldStopForGoals() {
        SessionGoalsManager goalsManager = componentManager.getGoalsManager();
        if (goalsManager != null && goalsManager.shouldStop()) {
            BotUtils.log("🎯 Session goal reached!");
            BotUtils.log(goalsManager.getGoalReachedMessage());

            // Export final session summary
            exportSessionSummary();

            // Stop bot
            stopBotExecution();
            return true;
        }
        return false;
    }

    /**
     * Update session goals progress
     */
    private void updateSessionGoalsProgress() {
        SessionGoalsManager goalsManager = componentManager.getGoalsManager();
        if (goalsManager != null) {
            goalsManager.updateProgress(
                stateManager.getTotalAlchs(),
                stateManager.getTotalProfit()
            );
        }
    }

    // ===========================================
    // LOCATION MANAGEMENT
    // ===========================================

    /**
     * Handle location management
     * @return delay if location navigation needed, 0 otherwise
     */
    private int handleLocationManagement() {
        LocationManager locationManager = componentManager.getLocationManager();
        if (locationManager != null && !locationManager.isAtLocation()) {
            BotUtils.log("📍 Not at correct location - navigating...");
            if (locationManager.ensureAtLocation()) {
                BotUtils.log("✅ Arrived at location");
                return 2000;
            } else {
                BotUtils.log("❌ Failed to reach location");
                return 3000;
            }
        }
        return 0;
    }

    /**
     * Perform location-specific antiban
     */
    private void performLocationAntiban() {
        LocationManager locationManager = componentManager.getLocationManager();
        if (locationManager != null && locationManager.requiresExtraAntiban()) {
            if (BotUtils.random(0, 100) < 10) { // 10% chance per loop
                locationManager.performLocationAntiban();
            }
        }
    }

    /**
     * Apply location-specific delay multiplier
     */
    private int applyLocationDelayMultiplier(int delay) {
        LocationManager locationManager = componentManager.getLocationManager();
        if (locationManager != null) {
            double multiplier = locationManager.getDelayMultiplier();
            delay = (int)(delay * multiplier);
        }
        return delay;
    }

    // ===========================================
    // SUBSYSTEM UPDATES
    // ===========================================

    /**
     * Update anti-ban system
     */
    private void updateAntibanSystem() {
        try {
            AntibanSystem antibanSystem = componentManager.getAntibanSystem();
            if (antibanSystem != null && currentConfig != null && currentConfig.antibanEnabled) {
                antibanSystem.update();
            }
        } catch (Exception e) {
            BotUtils.logError("Error updating anti-ban system", e);
        }
    }

    /**
     * Update GE Slot Manager
     */
    private void updateGESlotManager() {
        try {
            GESlotManager geSlotManager = componentManager.getGeSlotManager();
            if (geSlotManager != null) {
                // Check if we're at GE/bank
                if (org.dreambot.api.methods.grandexchange.GrandExchange.isOpen()) {
                    geSlotManager.updateSlotStatuses();
                    geSlotManager.processPurchaseQueue();

                    // Log slot summary occasionally
                    if (BotUtils.random(0, 100) < 5) { // 5% chance
                        BotUtils.log("🏪 " + geSlotManager.getSlotSummary());
                    }
                }
            }
        } catch (Exception e) {
            BotUtils.logError("Error updating GE slot manager", e);
        }
    }

    /**
     * Update overlay statistics
     */
    private void updateOverlayStatistics() {
        try {
            OverlayRenderer overlayRenderer = componentManager.getOverlayRenderer();
            AlchingEngine alchingEngine = componentManager.getAlchingEngine();

            if (overlayRenderer != null) {
                // Create statistics object with correct field names
                OverlayRenderer.ScriptStatistics stats = new OverlayRenderer.ScriptStatistics();

                // Set statistics using correct field names from your ScriptStatistics class
                stats.sessionStartTime = stateManager.getSessionStartTime();
                stats.scriptRuntime = stateManager.getSessionRuntime();
                stats.isRunning = stateManager.isBotRunning();
                stats.alchsCompleted = stateManager.getTotalAlchs();
                stats.totalProfit = stateManager.getTotalProfit();
                stats.xpGained = stateManager.getTotalXpGained();
                stats.currentState = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Waiting";
                stats.currentAction = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Ready";

                // Calculate derived stats
                stats.calculateDerivedStats();

                // Update the overlay
                overlayRenderer.updateStatistics(stats);
            }
        } catch (Exception e) {
            BotUtils.logError("Error updating overlay", e);
        }
    }

    // ===========================================
    // ALCHEMY ENGINE EXECUTION
    // ===========================================

    /**
     * Execute alchemy engine logic
     */
    private int executeAlchingEngine() {
        try {
            AlchingEngine alchingEngine = componentManager.getAlchingEngine();
            if (alchingEngine != null) {
                // Store previous alch count
                int previousAlchs = stateManager.getTotalAlchs();

                // Execute the alchemy engine's next action
                int delay = alchingEngine.executeNextAction();

                // Update our statistics from the engine
                OverlayRenderer.ScriptStatistics stats = alchingEngine.getStatistics();
                if (stats != null) {
                    stateManager.updateStatistics(
                        stats.alchsCompleted,
                        stats.totalProfit,
                        stats.xpGained
                    );

                    // If we completed a new alch, record it to CSV
                    if (stateManager.getTotalAlchs() > previousAlchs && currentConfig != null) {
                        recordAlchToCSV(
                            currentConfig.selectedItemName,
                            currentConfig.selectedItemId,
                            stats.currentBuyPrice,
                            stats.currentAlchValue,
                            stats.currentAlchValue - stats.currentBuyPrice - 220 // Approx profit
                        );
                    }
                }

                return delay;
            }

            return 1000; // Default wait time

        } catch (Exception e) {
            BotUtils.logError("Error in alchemy engine execution", e);
            return 2000; // Wait longer on error
        }
    }

    // ===========================================
    // CSV RECORDING & EXPORTS
    // ===========================================

    /**
     * Record an alch to CSV
     */
    private void recordAlchToCSV(String itemName, int itemId, int buyPrice, int alchValue, int profit) {
        try {
            CSVExporter csvExporter = componentManager.getCsvExporter();
            if (csvExporter != null) {
                csvExporter.recordAlch(itemName, itemId, buyPrice, alchValue, profit, 65);
            }
        } catch (Exception e) {
            BotUtils.logError("Error recording alch to CSV", e);
        }
    }

    /**
     * Export session summary
     */
    public void exportSessionSummary() {
        try {
            CSVExporter csvExporter = componentManager.getCsvExporter();
            LocationManager locationManager = componentManager.getLocationManager();

            if (csvExporter != null) {
                CSVExporter.SessionSummary summary = new CSVExporter.SessionSummary();
                summary.sessionStartTime = stateManager.getSessionStartTime();
                summary.sessionEndTime = System.currentTimeMillis();
                summary.itemName = currentConfig != null ? currentConfig.selectedItemName : "Unknown";
                summary.totalAlchs = stateManager.getTotalAlchs();
                summary.totalProfit = stateManager.getTotalProfit();
                summary.totalXPGained = stateManager.getTotalXpGained();

                try {
                    summary.startingLevel = Skills.getRealLevel(Skill.MAGIC);
                    summary.endingLevel = Skills.getRealLevel(Skill.MAGIC);
                } catch (Exception e) {
                    summary.startingLevel = 55;
                    summary.endingLevel = 55;
                }

                summary.location = locationManager != null ? locationManager.getCurrentLocation().getName() : "Grand Exchange";
                summary.errorsEncountered = 0;

                csvExporter.recordSession(summary);
                BotUtils.log("📊 Session summary exported");
            }
        } catch (Exception e) {
            BotUtils.logError("Error exporting session summary", e);
        }
    }

    // ===========================================
    // NOTIFICATIONS
    // ===========================================

    /**
     * Send completion notification
     */
    public void sendCompletionNotification() {
        try {
            DiscordManager discordManager = componentManager.getDiscordManager();
            if (discordManager != null && currentConfig != null && currentConfig.discordNotificationsEnabled) {
                OverlayRenderer.ScriptStatistics finalStats = new OverlayRenderer.ScriptStatistics();
                finalStats.alchsCompleted = stateManager.getTotalAlchs();
                finalStats.totalProfit = stateManager.getTotalProfit();
                finalStats.xpGained = stateManager.getTotalXpGained();
                finalStats.scriptRuntime = stateManager.getSessionRuntime();
                finalStats.calculateDerivedStats();

                discordManager.sendCompletionNotification(finalStats);
            }
        } catch (Exception e) {
            BotUtils.logError("Error sending completion notification", e);
        }
    }

    // ===========================================
    // BOT CONTROL
    // ===========================================

    /**
     * Stop bot execution
     */
    public void stopBotExecution() {
        try {
            stateManager.stopBot();

            // Send completion notification when stopped
            sendCompletionNotification();

            // Update GUI
            AlchBotGUI gui = componentManager.getGui();
            if (gui != null) {
                gui.updateButtonStates(false);
                gui.updateStatus("⏹️ Bot stopped - Ready for next session");
            } else {
                BotUtils.log("⚠️ GUI not available - bot stopped without GUI update");
            }

            BotUtils.log("✅ Bot stopped successfully");

        } catch (Exception e) {
            BotUtils.logError("Error in stop execution", e);
        }
    }
}
