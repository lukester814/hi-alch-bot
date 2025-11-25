package Hi_alch.core;

import Hi_alch.engine.AlchingEngine;
import Hi_alch.managers.PriceManager;
import Hi_alch.managers.AntibanSystem;
import Hi_alch.managers.DiscordManager;
import Hi_alch.managers.SettingsManager;
import Hi_alch.overlay.OverlayRenderer;
import Hi_alch.overlay.ScriptStatistics;
import Hi_alch.gui.AlchBotGUI;
import Hi_alch.gui.GUIConfiguration;
import Hi_alch.gui.GUIEventListener;
import Hi_alch.utils.BotUtils;
import Hi_alch.api.ItemSearchAPI;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.Category;
import javax.swing.SwingUtilities;

/**
 * High Alchemy Bot v2.0 - Main Coordinator Class (REFACTORED)
 * Delegates initialization and configuration to helper classes
 */
@ScriptManifest(
        author = "YourName",
        description = "Advanced High Alchemy Bot with GUI, Anti-ban, and Live Market Data",
        category = Category.MAGIC,
        version = 2.0,
        name = "High Alchemy Bot v2.0"
)
public class HighAlchBot extends AbstractScript implements GUIEventListener {

    // Component initializer and managers
    private BotComponentInitializer componentInitializer;

    // Core components (accessed via initializer)
    private AlchBotGUI gui;
    private AlchingEngine alchingEngine;
    private PriceManager priceManager;
    private AntibanSystem antibanSystem;
    private DiscordManager discordManager;
    private OverlayRenderer overlayRenderer;
    private SettingsManager settingsManager;

    // Bot state
    private volatile boolean botRunning = false;
    private volatile boolean botStopping = false;
    private GUIConfiguration currentConfig;
    private long sessionStartTime;
    private final Object stateLock = new Object();

    // Statistics
    private int totalAlchs = 0;
    private int totalProfit = 0;
    private int totalXpGained = 0;

    // ===========================================
    // DREAMBOT SCRIPT LIFECYCLE
    // ===========================================

    @Override
    public void onStart() {
        try {
            BotUtils.log("🚀 High Alchemy Bot v2.0 starting...");

            // Initialize all components using helper
            componentInitializer = new BotComponentInitializer();
            componentInitializer.initializeComponents();

            // Get component references
            extractComponentReferences();

            // Initialize GUI
            componentInitializer.initializeGUI(this);
            gui = componentInitializer.getGui();

            // Test API
            BotUtils.log("🧪 Testing Item Search API...");
            ItemSearchAPI.testAPI();

            // Load default settings
            componentInitializer.loadDefaultSettings();

            BotUtils.log("✅ Bot initialization complete!");
            BotUtils.log("📱 Please configure settings in the GUI and click Start Bot");

        } catch (Exception e) {
            BotUtils.logError("Critical error during initialization", e);
            stop();
        }
    }

    private void extractComponentReferences() {
        alchingEngine = componentInitializer.getAlchingEngine();
        priceManager = componentInitializer.getPriceManager();
        antibanSystem = componentInitializer.getAntibanSystem();
        discordManager = componentInitializer.getDiscordManager();
        overlayRenderer = componentInitializer.getOverlayRenderer();
        settingsManager = componentInitializer.getSettingsManager();
    }

    @Override
    public int onLoop() {
        try {
            synchronized (stateLock) {
                if (!botRunning) {
                    return 1000;
                }

                if (botStopping) {
                    return -1;
                }
            }

            // Update systems
            updateAntibanSystem();
            updateOverlayStatistics();

            // Execute alchemy engine
            return executeAlchingEngine();

        } catch (Exception e) {
            BotUtils.logError("Error in main bot loop", e);
            return 1000;
        }
    }

    @Override
    public void onExit() {
        try {
            BotUtils.log("🛑 High Alchemy Bot shutting down...");

            if (botRunning) {
                stopBotExecution();
            }

            // Send final Discord notification
            if (discordManager != null && currentConfig != null && currentConfig.discordNotificationsEnabled) {
                sendCompletionNotification();
            }

            // Auto-save settings
            if (settingsManager != null && currentConfig != null) {
                settingsManager.updateCurrentConfig(currentConfig);
                settingsManager.quickSave();
            }

            // Cleanup GUI
            if (gui != null) {
                gui.dispose();
            }

            BotUtils.log("✅ Bot shutdown complete!");

        } catch (Exception e) {
            BotUtils.logError("Error during bot shutdown", e);
        }
    }

    // ===========================================
    // BOT EXECUTION LOGIC
    // ===========================================

    private void updateAntibanSystem() {
        try {
            if (antibanSystem != null && currentConfig != null && currentConfig.antibanEnabled) {
                antibanSystem.update();
            }
        } catch (Exception e) {
            BotUtils.logError("Error updating anti-ban system", e);
        }
    }

    private int executeAlchingEngine() {
        try {
            if (alchingEngine != null) {
                int delay = alchingEngine.executeNextAction();

                // Update statistics from engine
                ScriptStatistics stats = alchingEngine.getStatistics();
                if (stats != null) {
                    totalAlchs = stats.alchsCompleted;
                    totalProfit = stats.totalProfit;
                    totalXpGained = stats.xpGained;
                }

                return delay;
            }
        } catch (Exception e) {
            BotUtils.logError("Error in alchemy engine execution", e);
        }
        return 2000;
    }

    private void updateOverlayStatistics() {
        try {
            if (overlayRenderer != null) {
                ScriptStatistics stats = new ScriptStatistics();
                stats.sessionStartTime = sessionStartTime;
                stats.scriptRuntime = System.currentTimeMillis() - sessionStartTime;
                stats.isRunning = botRunning;
                stats.alchsCompleted = totalAlchs;
                stats.totalProfit = totalProfit;
                stats.xpGained = totalXpGained;
                stats.currentState = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Waiting";
                stats.currentAction = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Ready";
                stats.calculateDerivedStats();

                overlayRenderer.updateStatistics(stats);
            }
        } catch (Exception e) {
            BotUtils.logError("Error updating overlay", e);
        }
    }

    private void sendCompletionNotification() {
        try {
            ScriptStatistics finalStats = new ScriptStatistics();
            finalStats.alchsCompleted = totalAlchs;
            finalStats.totalProfit = totalProfit;
            finalStats.xpGained = totalXpGained;
            finalStats.scriptRuntime = System.currentTimeMillis() - sessionStartTime;
            finalStats.calculateDerivedStats();

            discordManager.sendCompletionNotification(finalStats);
        } catch (Exception e) {
            BotUtils.logError("Error sending completion notification", e);
        }
    }

    // ===========================================
    // GUI EVENT HANDLERS
    // ===========================================

    @Override
    public void onStartBot(GUIConfiguration config) {
        try {
            synchronized (stateLock) {
                if (botRunning) {
                    BotUtils.log("⚠️ Bot is already running");
                    return;
                }

                BotUtils.log("🚀 Starting bot with configuration: " + config.toString());

                // Validate configuration using helper
                if (!BotConfigurationManager.validateConfiguration(config)) {
                    BotUtils.log("❌ Configuration validation failed");
                    return;
                }

                // Store configuration
                currentConfig = config;
                sessionStartTime = System.currentTimeMillis();

                // Configure components using helper
                BotConfigurationManager.configureComponents(
                    config, alchingEngine, antibanSystem, discordManager, priceManager, overlayRenderer
                );

                // Send start notification
                if (discordManager != null && config.discordNotificationsEnabled) {
                    discordManager.sendStartupNotification(config);
                }

                // Update GUI
                if (gui != null) {
                    gui.updateButtonStates(true);
                    gui.updateStatus("🚀 High Alchemy Bot started successfully!");
                } else {
                    BotUtils.log("⚠️ GUI not available");
                }

                // Start bot
                botRunning = true;
                botStopping = false;

                BotUtils.log("✅ Bot started successfully!");
            }
        } catch (Exception e) {
            BotUtils.logError("Error starting bot", e);
            if (gui != null) {
                gui.updateStatus("❌ Error starting bot: " + e.getMessage());
            }
        }
    }

    @Override
    public void onStopBot() {
        try {
            synchronized (stateLock) {
                if (!botRunning) {
                    BotUtils.log("⚠️ Bot is not running");
                    return;
                }
                BotUtils.log("🛑 Stopping bot...");
                stopBotExecution();
            }
        } catch (Exception e) {
            BotUtils.logError("Error stopping bot", e);
        }
    }

    private void stopBotExecution() {
        try {
            synchronized (stateLock) {
                botStopping = true;
                botRunning = false;
            }

            // Send completion notification
            sendCompletionNotification();

            // Update GUI
            if (gui != null) {
                gui.updateButtonStates(false);
                gui.updateStatus("⏹️ Bot stopped - Ready for next session");
            }

            BotUtils.log("✅ Bot stopped successfully");

        } catch (Exception e) {
            BotUtils.logError("Error in stop execution", e);
        }
    }

    @Override
    public boolean onTestWebhook(String webhookUrl) {
        try {
            if (discordManager != null) {
                BotUtils.log("🧪 Testing Discord webhook...");
                boolean success = discordManager.testWebhook(webhookUrl);
                if (success) {
                    BotUtils.log("✅ Discord webhook test successful");
                } else {
                    BotUtils.log("❌ Discord webhook test failed");
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            BotUtils.logError("Error testing webhook", e);
            return false;
        }
    }

    @Override
    public void onSaveSettings() {
        try {
            if (settingsManager != null && gui != null) {
                if (currentConfig != null) {
                    settingsManager.updateCurrentConfig(currentConfig);
                }
                boolean success = settingsManager.saveSettings("user_settings");
                if (success) {
                    BotUtils.log("💾 Settings saved successfully");
                } else {
                    BotUtils.log("❌ Failed to save settings");
                }
            }
        } catch (Exception e) {
            BotUtils.logError("Error saving settings", e);
        }
    }

    @Override
    public void onLoadSettings() {
        try {
            if (settingsManager != null && gui != null) {
                GUIConfiguration loadedConfig = settingsManager.loadSettings("user_settings");
                if (loadedConfig != null) {
                    gui.applyConfiguration(loadedConfig);
                    BotUtils.log("📁 Settings loaded successfully");
                } else {
                    BotUtils.log("❌ No saved settings found");
                }
            }
        } catch (Exception e) {
            BotUtils.logError("Error loading settings", e);
        }
    }

    @Override
    public void onItemSelected(String itemName) {
        try {
            BotUtils.log("🎯 Item selected: " + itemName);
            if (priceManager != null) {
                priceManager.startMonitoring(itemName);
            }
        } catch (Exception e) {
            BotUtils.logError("Error handling item selection", e);
        }
    }

    @Override
    public void onConfigurationChanged() {
        BotUtils.log("📝 GUI configuration changed");
        if (gui != null) {
            currentConfig = gui.getCurrentConfiguration();
        }
    }

    @Override
    public void onAlchemyConfigurationChanged(boolean enabled) {
        BotUtils.log("🖱️ Right-click alchemy configuration changed: " + (enabled ? "ENABLED" : "DISABLED"));
        if (enabled) {
            BotUtils.log("✅ Right-click alchemy auto-configuration will be applied when bot starts");
        } else {
            BotUtils.log("❌ Right-click alchemy auto-configuration disabled");
        }
    }

    // ===========================================
    // DREAMBOT PAINT METHOD
    // ===========================================

    @Override
    public void onPaint(java.awt.Graphics2D g) {
        try {
            if (overlayRenderer != null) {
                ScriptStatistics stats = new ScriptStatistics();
                stats.sessionStartTime = sessionStartTime;

                // Only calculate runtime if session has started
                if (sessionStartTime > 0) {
                    stats.scriptRuntime = System.currentTimeMillis() - sessionStartTime;
                } else {
                    stats.scriptRuntime = 0;
                }

                stats.isRunning = botRunning;
                stats.alchsCompleted = totalAlchs;
                stats.totalProfit = totalProfit;
                stats.xpGained = totalXpGained;
                stats.currentState = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Stopped";
                stats.currentAction = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Ready";

                // Calculate derived stats (rates per hour)
                stats.calculateDerivedStats();

                String status = botRunning ? "Running" : "Stopped";
                overlayRenderer.render(g, stats, status);
            }
        } catch (Exception e) {
            // Always log paint errors so we can debug issues
            BotUtils.logError("Error in paint method", e);
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    public void emergencyStop() {
        try {
            BotUtils.log("🚨 Emergency stop activated!");
            stopBotExecution();
            if (discordManager != null) {
                discordManager.sendEmergencyNotification("Emergency Stop", "Bot was stopped via emergency stop");
            }
        } catch (Exception e) {
            BotUtils.logError("Error in emergency stop", e);
        }
    }

    public String getBotStatistics() {
        try {
            if (!botRunning || sessionStartTime == 0) {
                return "Bot is not running";
            }

            long runtime = System.currentTimeMillis() - sessionStartTime;
            return String.format(
                    "Runtime: %s | Alchs: %,d | Profit: %,d GP | XP: %,d",
                    BotUtils.formatDuration(runtime),
                    totalAlchs,
                    totalProfit,
                    totalXpGained
            );
        } catch (Exception e) {
            BotUtils.logError("Error getting bot statistics", e);
            return "Error retrieving statistics";
        }
    }
}
