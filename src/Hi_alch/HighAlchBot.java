package Hi_alch;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.Category;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import javax.swing.SwingUtilities;

/**
 * High Alchemy Bot v2.0 - Main Coordinator Class
 *
 * This is the main script class that coordinates all bot components:
 * - AlchBotGUI: User interface for configuration
 * - AlchingEngine: Core alchemy logic and Grand Exchange trading
 * - PriceManager: Live market data and profit analysis
 * - AntibanSystem: Human-like behavior patterns
 * - DiscordManager: Webhook notifications
 * - OverlayRenderer: In-game statistics display
 * - SettingsManager: Configuration persistence
 *
 * The bot implements a modular architecture where each component handles
 * specific functionality and communicates through well-defined interfaces.
 */
@ScriptManifest(
        author = "Plebs Scripts",
        description = "Advanced High Alchemy Bot with GUI, Anti-ban, and Live Market Data",
        category = Category.MAGIC,
        version = 2.0,
        name = "High Alchemy Bot v0.02"
)
public class HighAlchBot extends AbstractScript implements AlchBotGUI.GUIEventListener {

    // ===========================================
    // CORE COMPONENTS
    // ===========================================

    private AlchBotGUI gui;
    private AlchingEngine alchingEngine;
    private PriceManager priceManager;
    private AntibanSystem antibanSystem;
    private DiscordManager discordManager;
    private OverlayRenderer overlayRenderer;
    private ModernPaintRenderer modernPaint;
    private SettingsManager settingsManager;

    // NEW PROFESSIONAL FEATURES
    private SessionGoalsManager goalsManager;
    private BankManager bankManager;
    private LocationManager locationManager;
    private CSVExporter csvExporter;
    private GESlotManager geSlotManager;
    private ProfileManager profileManager;

    // ===========================================
    // BOT STATE MANAGEMENT
    // ===========================================

    private volatile boolean botRunning = false;
    private volatile boolean botStopping = false;
    private AlchBotGUI.GUIConfiguration currentConfig;
    private long sessionStartTime;
    private final Object stateLock = new Object();

    // Bot statistics
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

            // Initialize all components
            initializeComponents();

            // Create and show GUI
            initializeGUI();

            // Test the API
            BotUtils.log("🧪 Testing Item Search API...");
            ItemSearchAPI.testAPI();

            // Load default settings
            loadDefaultSettings();

            BotUtils.log("✅ Bot initialization complete!");
            BotUtils.log("📱 Please configure settings in the GUI and click Start Bot");

        } catch (Exception e) {
            BotUtils.logError("Critical error during initialization", e);
            stop();
        }
    }

    @Override
    public int onLoop() {
        try {
            synchronized (stateLock) {
                if (!botRunning) {
                    return 1000; // Wait for user to start bot via GUI
                }

                if (botStopping) {
                    return -1; // Signal script termination
                }
            }

            // 1. Check Session Goals - HIGHEST PRIORITY
            if (goalsManager != null && goalsManager.shouldStop()) {
                BotUtils.log("🎯 Session goal reached!");
                BotUtils.log(goalsManager.getGoalReachedMessage());

                // Export final session summary
                exportSessionSummary();

                // Stop bot
                stopBotExecution();
                return -1;
            }

            // 2. Update Session Goals Progress
            if (goalsManager != null) {
                goalsManager.updateProgress(totalAlchs, totalProfit);
            }

            // 3. Update GE Slot Manager (if at GE)
            updateGESlotManager();

            // 4. Ensure we're at the correct location
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

            // 5. Location-specific antiban
            if (locationManager != null && locationManager.requiresExtraAntiban()) {
                if (BotUtils.random(0, 100) < 10) { // 10% chance per loop
                    locationManager.performLocationAntiban();
                }
            }

            // 6. Update anti-ban system
            updateAntibanSystem();

            // 7. Update overlay statistics
            updateOverlayStatistics();

            // 8. Main bot logic using AlchingEngine
            int delay = executeAlchingEngine();

            // 9. Apply location-specific delay multiplier
            if (locationManager != null) {
                double multiplier = locationManager.getDelayMultiplier();
                delay = (int)(delay * multiplier);
            }

            return delay;

        } catch (Exception e) {
            BotUtils.logError("Error in main bot loop", e);
            return 1000; // Continue running but wait before next iteration
        }
    }

    @Override
    public void onExit() {
        try {
            BotUtils.log("🛑 High Alchemy Bot shutting down...");

            // Stop bot if running
            if (botRunning) {
                stopBotExecution();
            }

            // Export final session summary
            if (totalAlchs > 0) {
                exportSessionSummary();
                BotUtils.log("📊 Final session data exported");
            }

            // Generate daily report if CSV exporter exists
            if (csvExporter != null) {
                csvExporter.generateDailyReport();
            }

            // Send final Discord notification
            if (discordManager != null && currentConfig != null && currentConfig.discordNotificationsEnabled) {
                OverlayRenderer.ScriptStatistics finalStats = new OverlayRenderer.ScriptStatistics();
                finalStats.alchsCompleted = totalAlchs;
                finalStats.totalProfit = totalProfit;
                finalStats.xpGained = totalXpGained;
                finalStats.scriptRuntime = System.currentTimeMillis() - sessionStartTime;

                discordManager.sendCompletionNotification(finalStats);
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
    // COMPONENT INITIALIZATION
    // ===========================================

    /**
     * Initialize all bot components
     */
    private void initializeComponents() {
        try {
            BotUtils.log("🔧 Initializing bot components...");

            // Initialize price manager first (needed by other components)
            priceManager = new PriceManager();
            BotUtils.log("💰 PriceManager initialized");

            // Initialize anti-ban system
            antibanSystem = new AntibanSystem();
            BotUtils.log("🛡️ AntibanSystem initialized");

            // Initialize Discord manager
            discordManager = new DiscordManager();
            BotUtils.log("📱 DiscordManager initialized");

            // Initialize alchemy engine with required parameters
            alchingEngine = new AlchingEngine(priceManager, antibanSystem, discordManager);
            BotUtils.log("⚗️ AlchingEngine initialized");

            // Initialize overlay renderer
            overlayRenderer = new OverlayRenderer();
            BotUtils.log("🖼️ OverlayRenderer initialized");

            // Initialize modern paint renderer
            modernPaint = new ModernPaintRenderer();
            BotUtils.log("🎨 ModernPaintRenderer initialized");

            // Initialize settings manager
            settingsManager = new SettingsManager();
            BotUtils.log("💾 SettingsManager initialized");

            // Initialize NEW professional features
            BotUtils.log("🚀 Initializing professional features...");

            // Session Goals Manager
            goalsManager = new SessionGoalsManager(discordManager);
            BotUtils.log("🎯 SessionGoalsManager initialized");

            // Bank Manager
            bankManager = new BankManager();
            BotUtils.log("🏦 BankManager initialized");

            // Location Manager
            locationManager = new LocationManager(antibanSystem);
            BotUtils.log("📍 LocationManager initialized");

            // CSV Exporter
            csvExporter = new CSVExporter();
            csvExporter.setAutoExport(true);
            BotUtils.log("📊 CSVExporter initialized (auto-export enabled)");

            // GE Slot Manager
            geSlotManager = new GESlotManager();
            geSlotManager.setAutoCollect(true);
            geSlotManager.setAutoCancelStuck(true);
            BotUtils.log("🏪 GESlotManager initialized (auto-management enabled)");

            // Profile Manager
            profileManager = new ProfileManager();
            BotUtils.log("💾 ProfileManager initialized");

            BotUtils.log("✅ All components (including professional features) initialized successfully");

        } catch (Exception e) {
            BotUtils.logError("Error initializing components", e);
            throw e;
        }
    }

    /**
     * Initialize GUI with proper threading
     */
    private void initializeGUI() {
        try {
            BotUtils.log("🖼️ Initializing GUI...");

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        BotUtils.log("🎨 Creating AlchBotGUI instance...");
                        gui = new AlchBotGUI();

                        BotUtils.log("🔗 Setting event listener...");
                        gui.setEventListener(HighAlchBot.this);

                        BotUtils.log("📱 Showing GUI...");
                        gui.showGUI();

                        BotUtils.log("✅ GUI initialized and displayed");

                    } catch (Exception e) {
                        BotUtils.logError("Error creating GUI", e);
                        e.printStackTrace();

                        // Try to continue without GUI
                        BotUtils.log("⚠️ Continuing without GUI due to initialization error");
                        gui = null;
                    }
                }
            });

        } catch (Exception e) {
            BotUtils.logError("Error initializing GUI", e);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Load default settings
     */
    private void loadDefaultSettings() {
        try {
            if (settingsManager != null) {
                // Try to load existing default settings
                AlchBotGUI.GUIConfiguration loadedConfig = settingsManager.loadDefaultSettings();

                if (loadedConfig != null && gui != null) {
                    gui.applyConfiguration(loadedConfig);
                    BotUtils.log("📁 Default settings loaded successfully");
                } else {
                    BotUtils.log("📝 Using built-in default configuration");
                }
            }
        } catch (Exception e) {
            BotUtils.logError("Error loading default settings", e);
            BotUtils.log("📝 Continuing with default configuration");
        }
    }

    // ===========================================
    // MAIN BOT EXECUTION LOGIC
    // ===========================================

    /**
     * Update anti-ban system
     */
    private void updateAntibanSystem() {
        try {
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
     * Record an alch to CSV
     */
    private void recordAlchToCSV(String itemName, int itemId, int buyPrice, int alchValue, int profit) {
        try {
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
    private void exportSessionSummary() {
        try {
            if (csvExporter != null) {
                CSVExporter.SessionSummary summary = new CSVExporter.SessionSummary();
                summary.sessionStartTime = sessionStartTime;
                summary.sessionEndTime = System.currentTimeMillis();
                summary.itemName = currentConfig != null ? currentConfig.selectedItemName : "Unknown";
                summary.totalAlchs = totalAlchs;
                summary.totalProfit = totalProfit;
                summary.totalXPGained = totalXpGained;

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

    /**
     * Execute alchemy engine logic
     */
    private int executeAlchingEngine() {
        try {
            if (alchingEngine != null) {
                // Store previous alch count
                int previousAlchs = totalAlchs;

                // Execute the alchemy engine's next action
                int delay = alchingEngine.executeNextAction();

                // Update our statistics from the engine
                OverlayRenderer.ScriptStatistics stats = alchingEngine.getStatistics();
                if (stats != null) {
                    totalAlchs = stats.alchsCompleted;
                    totalProfit = stats.totalProfit;
                    totalXpGained = stats.xpGained;

                    // If we completed a new alch, record it to CSV
                    if (totalAlchs > previousAlchs && currentConfig != null) {
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

    /**
     * Update overlay statistics
     */
    private void updateOverlayStatistics() {
        try {
            if (overlayRenderer != null) {
                // Create statistics object with correct field names
                OverlayRenderer.ScriptStatistics stats = new OverlayRenderer.ScriptStatistics();

                // Set statistics using correct field names from your ScriptStatistics class
                stats.sessionStartTime = sessionStartTime;
                stats.scriptRuntime = System.currentTimeMillis() - sessionStartTime;
                stats.isRunning = botRunning;
                stats.alchsCompleted = totalAlchs;
                stats.totalProfit = totalProfit;
                stats.xpGained = totalXpGained;
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

    /**
     * Send completion notification
     */
    private void sendCompletionNotification() {
        try {
            if (discordManager != null && currentConfig != null && currentConfig.discordNotificationsEnabled) {
                OverlayRenderer.ScriptStatistics finalStats = new OverlayRenderer.ScriptStatistics();
                finalStats.alchsCompleted = totalAlchs;
                finalStats.totalProfit = totalProfit;
                finalStats.xpGained = totalXpGained;
                finalStats.scriptRuntime = System.currentTimeMillis() - sessionStartTime;
                finalStats.calculateDerivedStats();

                discordManager.sendCompletionNotification(finalStats);
            }
        } catch (Exception e) {
            BotUtils.logError("Error sending completion notification", e);
        }
    }

    // ===========================================
    // GUI EVENT HANDLERS
    // ===========================================

    @Override
    public void onStartBot(AlchBotGUI.GUIConfiguration config) {
        try {
            synchronized (stateLock) {
                if (botRunning) {
                    BotUtils.log("⚠️ Bot is already running");
                    return;
                }

                BotUtils.log("🚀 Starting bot with configuration: " + config.toString());

                // Validate configuration
                if (!validateConfiguration(config)) {
                    BotUtils.log("❌ Configuration validation failed");
                    return;
                }

                // Store configuration
                currentConfig = config;
                sessionStartTime = System.currentTimeMillis();

                // Initialize components with configuration
                configureComponents(config);

                // Send start notification using the correct method
                if (discordManager != null && config.discordNotificationsEnabled) {
                    discordManager.sendStartupNotification(config);
                }

                // Update GUI
                if (gui != null) {
                    gui.updateButtonStates(true);
                    gui.updateStatus("🚀 High Alchemy Bot started successfully!");
                } else {
                    BotUtils.log("⚠️ GUI not available - bot started without GUI interface");
                }

                // Start bot
                botRunning = true;
                botStopping = false;

                BotUtils.log("✅ Bot started successfully!");
            }

        } catch (Exception e) {
            BotUtils.logError("Error starting bot", e);
            e.printStackTrace();
            if (gui != null) {
                gui.updateStatus("❌ Error starting bot: " + e.getMessage());
            } else {
                BotUtils.log("❌ Error starting bot (GUI not available): " + e.getMessage());
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

    /**
     * Stop bot execution
     */
    private void stopBotExecution() {
        try {
            synchronized (stateLock) {
                botStopping = true;
                botRunning = false;
            }

            // Send completion notification when stopped
            sendCompletionNotification();

            // Update GUI
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

    @Override
    public boolean onTestWebhook(String webhookUrl) {
        try {
            if (discordManager != null) {
                BotUtils.log("🧪 Testing Discord webhook...");

                // Test webhook with the URL using the correct method
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
                // Update current config in settings manager
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
                AlchBotGUI.GUIConfiguration loadedConfig = settingsManager.loadSettings("user_settings");

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

            // Start price monitoring for selected item
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
        // Handle any configuration changes
        if (gui != null) {
            currentConfig = gui.getCurrentConfiguration();
        } else {
            BotUtils.log("⚠️ GUI not available - cannot update configuration");
        }
    }



    @Override
    public void onAlchemyConfigurationChanged(boolean enabled) {
        BotUtils.log("🖱️ Right-click alchemy configuration changed: " + (enabled ? "ENABLED" : "DISABLED"));

        // Store the alchemy configuration setting
        if (enabled) {
            BotUtils.log("✅ Right-click alchemy auto-configuration will be applied when bot starts");
            // This will be used by AlchingEngine to configure the warning threshold
        } else {
            BotUtils.log("❌ Right-click alchemy auto-configuration disabled");
        }

        // The alchemy configuration will be passed to AlchingEngine through the config
    }

    // ===========================================
    // CONFIGURATION & VALIDATION
    // ===========================================

    /**
     * Validate bot configuration
     */
    private boolean validateConfiguration(AlchBotGUI.GUIConfiguration config) {
        try {
            // Validate item selection
            if (config.selectedItemName == null || config.selectedItemName.trim().isEmpty()) {
                BotUtils.log("❌ No item selected");
                return false;
            }

            // Validate buy limit
            if (config.buyLimit <= 0) {
                BotUtils.log("❌ Invalid buy limit: " + config.buyLimit);
                return false;
            }

            // Validate nature rune amount
            if (config.natureRuneAmount <= 0) {
                BotUtils.log("❌ Invalid nature rune amount: " + config.natureRuneAmount);
                return false;
            }

            // Validate Discord webhook if enabled
            if (config.discordNotificationsEnabled) {
                if (config.discordWebhookUrl == null || config.discordWebhookUrl.trim().isEmpty()) {
                    BotUtils.log("❌ Discord notifications enabled but no webhook URL provided");
                    return false;
                }

                if (!BotUtils.isValidDiscordWebhook(config.discordWebhookUrl)) {
                    BotUtils.log("❌ Invalid Discord webhook URL format");
                    return false;
                }
            }

            BotUtils.log("✅ Configuration validation successful");
            return true;

        } catch (Exception e) {
            BotUtils.logError("Error validating configuration", e);
            return false;
        }
    }

    /**
     * Configure all components with the provided configuration
     */
    private void configureComponents(AlchBotGUI.GUIConfiguration config) {
        try {
            // Configure alchemy engine with individual parameters using correct method signature
            if (alchingEngine != null) {
                alchingEngine.configure(
                        config.selectedItemName,
                        config.selectedItemId,
                        config.buyLimit,
                        config.priceMarkup,
                        config.natureRuneAmount,
                        config.smartProfitEnabled,
                        config.worldHopEnabled,
                        config.skipBuying,
                        config.restockWhenEmpty
                );
            }

            // Configure anti-ban system using the correct constructor parameters
            if (antibanSystem != null) {
                antibanSystem.configure(
                        config.antibanEnabled,
                        config.userProfileSeed,
                        config.antibanAggression
                );
            }

            // Configure Discord manager using the correct method
            if (discordManager != null && config.discordNotificationsEnabled) {
                discordManager.configure(config.discordWebhookUrl);
            }

            // Configure price manager
            if (priceManager != null) {
                priceManager.startMonitoring(config.selectedItemName);
            }

            // Configure overlay renderer
            if (overlayRenderer != null) {
                overlayRenderer.configure(config.selectedItemName, config.selectedItemId, true);
            }

            // Configure NEW professional features
            BotUtils.log("🚀 Configuring professional features...");

            // Configure Session Goals (default: no goals set - runs indefinitely)
            if (goalsManager != null) {
                SessionGoalsManager.SessionGoals goals = new SessionGoalsManager.SessionGoals();
                // Goals can be configured via GUI - for now use defaults (all disabled)
                goalsManager.setGoals(goals);
                BotUtils.log("🎯 Session goals configured (running indefinitely by default)");
            }

            // Configure Location Manager (default: Grand Exchange)
            if (locationManager != null) {
                locationManager.setLocation(LocationManager.AlchLocation.GRAND_EXCHANGE);
                locationManager.setAutoNavigate(true);
                BotUtils.log("📍 Location set to: Grand Exchange (auto-navigate enabled)");
            }

            // Configure Bank Manager (default: Grand Exchange)
            if (bankManager != null) {
                bankManager.setPreferredLocation(BankManager.BankLocation.GRAND_EXCHANGE);
                bankManager.setHybridMode(true);
                BotUtils.log("🏦 Bank location set to: Grand Exchange (hybrid mode enabled)");
            }

            // CSV Exporter already configured in initialization
            // GE Slot Manager already configured in initialization
            // Profile Manager already configured in initialization

            BotUtils.log("✅ All components (including professional features) configured successfully");

        } catch (Exception e) {
            BotUtils.logError("Error configuring components", e);
            // Don't throw here, continue with default configuration
        }
    }

    // ===========================================
    // DREAMBOT PAINT METHOD
    // ===========================================

    @Override
    public void onPaint(java.awt.Graphics2D g) {
        try {
            // Create statistics for rendering using correct field names
            OverlayRenderer.ScriptStatistics stats = new OverlayRenderer.ScriptStatistics();
            stats.sessionStartTime = sessionStartTime;
            stats.scriptRuntime = System.currentTimeMillis() - sessionStartTime;
            stats.alchsCompleted = totalAlchs;
            stats.totalProfit = totalProfit;
            stats.xpGained = totalXpGained;
            stats.isRunning = botRunning;
            stats.currentState = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Stopped";
            stats.currentAction = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Idle";

            // Get additional stats from alching engine if available
            if (alchingEngine != null) {
                OverlayRenderer.ScriptStatistics engineStats = alchingEngine.getStatistics();
                if (engineStats != null) {
                    stats.currentBuyPrice = engineStats.currentBuyPrice;
                    stats.currentAlchValue = engineStats.currentAlchValue;
                    stats.itemsBought = engineStats.itemsBought;
                    stats.errorsEncountered = engineStats.errorsEncountered;
                    stats.lastError = engineStats.lastError;
                }
            }

            stats.calculateDerivedStats();

            String status = botRunning ? "Running" : "Stopped";

            // Render both old and new paint (user can choose which they prefer)
            if (overlayRenderer != null) {
                overlayRenderer.render(g, stats, status);
            }

            // Render modern tabbed paint
            if (modernPaint != null) {
                modernPaint.render(g, stats, status);
            }
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
     * Emergency stop method
     */
    public void emergencyStop() {
        try {
            BotUtils.log("🚨 Emergency stop activated!");

            synchronized (stateLock) {
                botRunning = false;
                botStopping = true;
            }

            // Send emergency notification
            if (discordManager != null) {
                discordManager.sendEmergencyNotification("Emergency Stop", "Bot was stopped via emergency stop");
            }

            // Stop the script
            stop();

        } catch (Exception e) {
            BotUtils.logError("Error in emergency stop", e);
        }
    }

    /**
     * Get current bot statistics
     */
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