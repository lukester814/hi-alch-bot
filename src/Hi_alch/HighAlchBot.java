package Hi_alch;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.Category;

import Hi_alch.core.*;

import java.awt.Graphics2D;

/**
 * High Alchemy Bot v2.0 - Main Script Class
 *
 * This is the main DreamBot script class that coordinates all bot functionality
 * through specialized coordinator classes. Each coordinator handles a specific
 * aspect of the bot's operation:
 *
 * - BotStateManager: Bot state and statistics tracking
 * - ComponentManager: Component lifecycle management
 * - ConfigurationValidator: Configuration validation
 * - GUICoordinator: GUI event handling and user interaction
 * - ExecutionCoordinator: Main bot execution loop
 * - PaintCoordinator: Statistics rendering and paint
 *
 * This refactored architecture separates concerns and makes the codebase
 * more maintainable, testable, and extensible.
 *
 * Refactored from 994 lines to ~290 lines through coordinator delegation.
 */
@ScriptManifest(
        author = "Plebs Scripts",
        description = "Advanced High Alchemy Bot with GUI, Anti-ban, and Live Market Data",
        category = Category.MAGIC,
        version = 2.0,
        name = "High Alchemy Bot v0.02"
)
public class HighAlchBot extends AbstractScript {

    // ===========================================
    // COORDINATOR REFERENCES
    // ===========================================

    private BotStateManager stateManager;
    private ComponentManager componentManager;
    private ConfigurationValidator configValidator;
    private GUICoordinator guiCoordinator;
    private ExecutionCoordinator executionCoordinator;
    private PaintCoordinator paintCoordinator;

    // ===========================================
    // DREAMBOT SCRIPT LIFECYCLE - INITIALIZATION
    // ===========================================

    /**
     * Called when the script starts.
     * Initializes all coordinators and delegates initialization tasks.
     */
    @Override
    public void onStart() {
        try {
            BotUtils.log("🚀 High Alchemy Bot v2.0 starting...");

            // Initialize coordinators in correct order
            initializeCoordinators();

            // Initialize all bot components through ComponentManager
            componentManager.initializeAllComponents();

            // Initialize and show GUI through GUICoordinator
            guiCoordinator.initializeGUI();

            // Test the Item Search API
            BotUtils.log("🧪 Testing Item Search API...");
            ItemSearchAPI.testAPI();

            // Load default settings through GUICoordinator
            guiCoordinator.loadDefaultSettings();

            BotUtils.log("✅ Bot initialization complete!");
            BotUtils.log("📱 Please configure settings in the GUI and click Start Bot");

        } catch (Exception e) {
            BotUtils.logError("Critical error during initialization", e);
            stop();
        }
    }

    /**
     * Initialize all coordinator instances
     */
    private void initializeCoordinators() {
        try {
            BotUtils.log("🔧 Initializing coordinators...");

            // Create state manager (no dependencies)
            stateManager = new BotStateManager();
            BotUtils.log("✅ BotStateManager initialized");

            // Create component manager (no dependencies)
            componentManager = new ComponentManager();
            BotUtils.log("✅ ComponentManager initialized");

            // Create configuration validator (no dependencies)
            configValidator = new ConfigurationValidator();
            BotUtils.log("✅ ConfigurationValidator initialized");

            // Create GUI coordinator (depends on componentManager, stateManager, configValidator)
            guiCoordinator = new GUICoordinator(componentManager, stateManager, configValidator);
            BotUtils.log("✅ GUICoordinator initialized");

            // Create execution coordinator (depends on componentManager, stateManager)
            executionCoordinator = new ExecutionCoordinator(componentManager, stateManager);
            BotUtils.log("✅ ExecutionCoordinator initialized");

            // Create paint coordinator (depends on componentManager, stateManager)
            paintCoordinator = new PaintCoordinator(componentManager, stateManager);
            BotUtils.log("✅ PaintCoordinator initialized");

            // Link configuration from GUI to Execution coordinator
            linkCoordinators();

            BotUtils.log("✅ All coordinators initialized successfully");

        } catch (Exception e) {
            BotUtils.logError("Error initializing coordinators", e);
            throw e;
        }
    }

    /**
     * Link coordinators together (configuration flow)
     */
    private void linkCoordinators() {
        // Execution coordinator needs access to current configuration
        // This will be set when the bot starts via GUI
        // The GUICoordinator will handle configuration and pass it to components
    }

    // ===========================================
    // DREAMBOT SCRIPT LIFECYCLE - MAIN LOOP
    // ===========================================

    /**
     * Main bot loop - called repeatedly by DreamBot.
     * Delegates all execution logic to ExecutionCoordinator.
     *
     * @return Delay in milliseconds before next loop iteration, or -1 to stop
     */
    @Override
    public int onLoop() {
        try {
            // Update execution coordinator with current configuration from GUI
            updateExecutionConfiguration();

            // Delegate main loop execution to ExecutionCoordinator
            return executionCoordinator.executeMainLoop();

        } catch (Exception e) {
            BotUtils.logError("Error in main bot loop", e);
            return 1000; // Continue running but wait before next iteration
        }
    }

    /**
     * Update execution coordinator with current configuration from GUI
     */
    private void updateExecutionConfiguration() {
        try {
            AlchBotGUI.GUIConfiguration currentConfig = guiCoordinator.getCurrentConfiguration();
            if (currentConfig != null) {
                executionCoordinator.setConfiguration(currentConfig);
            }
        } catch (Exception e) {
            // Silently fail - not critical
        }
    }

    // ===========================================
    // DREAMBOT SCRIPT LIFECYCLE - SHUTDOWN
    // ===========================================

    /**
     * Called when the script exits.
     * Delegates cleanup to coordinators.
     */
    @Override
    public void onExit() {
        try {
            BotUtils.log("🛑 High Alchemy Bot shutting down...");

            // Stop bot if running
            if (stateManager.isBotRunning()) {
                executionCoordinator.stopBotExecution();
            }

            // Export final session summary if we did any alchs
            if (stateManager.getTotalAlchs() > 0) {
                executionCoordinator.exportSessionSummary();
                BotUtils.log("📊 Final session data exported");
            }

            // Generate daily report if CSV exporter exists
            CSVExporter csvExporter = componentManager.getCsvExporter();
            if (csvExporter != null) {
                csvExporter.generateDailyReport();
            }

            // Send final Discord notification
            sendFinalDiscordNotification();

            // Auto-save settings
            autoSaveSettings();

            // Shutdown all components
            componentManager.shutdownAllComponents();

            BotUtils.log("✅ Bot shutdown complete!");

        } catch (Exception e) {
            BotUtils.logError("Error during bot shutdown", e);
        }
    }

    /**
     * Send final Discord notification on exit
     */
    private void sendFinalDiscordNotification() {
        try {
            AlchBotGUI.GUIConfiguration currentConfig = guiCoordinator.getCurrentConfiguration();
            DiscordManager discordManager = componentManager.getDiscordManager();

            if (discordManager != null && currentConfig != null && currentConfig.discordNotificationsEnabled) {
                OverlayRenderer.ScriptStatistics finalStats = new OverlayRenderer.ScriptStatistics();
                finalStats.alchsCompleted = stateManager.getTotalAlchs();
                finalStats.totalProfit = stateManager.getTotalProfit();
                finalStats.xpGained = stateManager.getTotalXpGained();
                finalStats.scriptRuntime = stateManager.getSessionRuntime();

                discordManager.sendCompletionNotification(finalStats);
            }
        } catch (Exception e) {
            BotUtils.logError("Error sending final Discord notification", e);
        }
    }

    /**
     * Auto-save current settings on exit
     */
    private void autoSaveSettings() {
        try {
            SettingsManager settingsManager = componentManager.getSettingsManager();
            AlchBotGUI.GUIConfiguration currentConfig = guiCoordinator.getCurrentConfiguration();

            if (settingsManager != null && currentConfig != null) {
                settingsManager.updateCurrentConfig(currentConfig);
                settingsManager.quickSave();
            }
        } catch (Exception e) {
            BotUtils.logError("Error auto-saving settings", e);
        }
    }

    // ===========================================
    // DREAMBOT SCRIPT LIFECYCLE - PAINT
    // ===========================================

    /**
     * Paint method - renders statistics overlay.
     * Delegates all rendering to PaintCoordinator.
     *
     * @param g Graphics2D object for rendering
     */
    @Override
    public void onPaint(Graphics2D g) {
        try {
            // Delegate all paint logic to PaintCoordinator
            paintCoordinator.onPaint(g);

        } catch (Exception e) {
            // Don't log paint errors too frequently to avoid spam
            if (Math.random() < 0.01) { // Log only 1% of paint errors
                BotUtils.logError("Error in paint method", e);
            }
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Emergency stop method - immediately halts the bot.
     * Can be called from external systems or error handlers.
     */
    public void emergencyStop() {
        try {
            BotUtils.log("🚨 Emergency stop activated!");

            // Emergency stop via state manager
            stateManager.emergencyStop();

            // Send emergency notification
            sendEmergencyNotification();

            // Stop the script
            stop();

        } catch (Exception e) {
            BotUtils.logError("Error in emergency stop", e);
        }
    }

    /**
     * Send emergency notification to Discord
     */
    private void sendEmergencyNotification() {
        try {
            DiscordManager discordManager = componentManager.getDiscordManager();
            if (discordManager != null) {
                discordManager.sendEmergencyNotification("Emergency Stop", "Bot was stopped via emergency stop");
            }
        } catch (Exception e) {
            BotUtils.logError("Error sending emergency notification", e);
        }
    }

    /**
     * Get current bot statistics as formatted string.
     * Useful for debugging and external monitoring.
     *
     * @return Formatted statistics string
     */
    public String getBotStatistics() {
        try {
            if (!stateManager.isBotRunning() || stateManager.getSessionStartTime() == 0) {
                return "Bot is not running";
            }

            return stateManager.getStatisticsSummary();

        } catch (Exception e) {
            BotUtils.logError("Error getting bot statistics", e);
            return "Error retrieving statistics";
        }
    }

    /**
     * Get the state manager (for external access if needed)
     */
    public BotStateManager getStateManager() {
        return stateManager;
    }

    /**
     * Get the component manager (for external access if needed)
     */
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    /**
     * Get the execution coordinator (for external access if needed)
     */
    public ExecutionCoordinator getExecutionCoordinator() {
        return executionCoordinator;
    }
}
