package Hi_alch;

import Hi_alch.core.*;
import Hi_alch.overlay.ScriptStatistics;
import Hi_alch.gui.GUIConfiguration;
import Hi_alch.gui.GUIEventListener;
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
    public int onLoop() {
            synchronized (stateLock) {
                if (!botRunning) {
                    return 1000;
                }
                if (botStopping) {
                    return -1;
            }
            // Update systems
            updateAntibanSystem();
            updateOverlayStatistics();
            // Execute alchemy engine
            return executeAlchingEngine();
            BotUtils.logError("Error in main bot loop", e);
            return 1000;
    public void onExit() {
            BotUtils.log("🛑 High Alchemy Bot shutting down...");
            if (botRunning) {
                stopBotExecution();
            // Send final Discord notification
            if (discordManager != null && currentConfig != null && currentConfig.discordNotificationsEnabled) {
                sendCompletionNotification();
            // Auto-save settings
            if (settingsManager != null && currentConfig != null) {
                settingsManager.updateCurrentConfig(currentConfig);
                settingsManager.quickSave();
            // Cleanup GUI
            if (gui != null) {
                gui.dispose();
            BotUtils.log("✅ Bot shutdown complete!");
            BotUtils.logError("Error during bot shutdown", e);
    // BOT EXECUTION LOGIC
    private void updateAntibanSystem() {
            if (antibanSystem != null && currentConfig != null && currentConfig.antibanEnabled) {
                antibanSystem.update();
            BotUtils.logError("Error updating anti-ban system", e);
    private int executeAlchingEngine() {
            if (alchingEngine != null) {
                int delay = alchingEngine.executeNextAction();
                // Update statistics from engine
                ScriptStatistics stats = alchingEngine.getStatistics();
                if (stats != null) {
                    totalAlchs = stats.alchsCompleted;
                    totalProfit = stats.totalProfit;
                    totalXpGained = stats.xpGained;
                return delay;
            BotUtils.logError("Error in alchemy engine execution", e);
            return 2000;
    private void updateOverlayStatistics() {
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
            BotUtils.logError("Error updating overlay", e);
    private void sendCompletionNotification() {
                ScriptStatistics finalStats = new ScriptStatistics();
                finalStats.alchsCompleted = totalAlchs;
                finalStats.totalProfit = totalProfit;
                finalStats.xpGained = totalXpGained;
                finalStats.scriptRuntime = System.currentTimeMillis() - sessionStartTime;
                finalStats.calculateDerivedStats();
                discordManager.sendCompletionNotification(finalStats);
            BotUtils.logError("Error sending completion notification", e);
    // GUI EVENT HANDLERS
    public void onStartBot(GUIConfiguration config) {
                if (botRunning) {
                    BotUtils.log("⚠️ Bot is already running");
                    return;
                BotUtils.log("🚀 Starting bot with configuration: " + config.toString());
                // Validate configuration using helper
                if (!BotConfigurationManager.validateConfiguration(config)) {
                    BotUtils.log("❌ Configuration validation failed");
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
                // Update GUI
                if (gui != null) {
                    gui.updateButtonStates(true);
                    gui.updateStatus("🚀 High Alchemy Bot started successfully!");
                } else {
                    BotUtils.log("⚠️ GUI not available");
                // Start bot
                botRunning = true;
                botStopping = false;
                BotUtils.log("✅ Bot started successfully!");
            BotUtils.logError("Error starting bot", e);
                gui.updateStatus("❌ Error starting bot: " + e.getMessage());
    public void onStopBot() {
                    BotUtils.log("⚠️ Bot is not running");
                BotUtils.log("🛑 Stopping bot...");
            BotUtils.logError("Error stopping bot", e);
    private void stopBotExecution() {
                botStopping = true;
                botRunning = false;
            // Send completion notification
            sendCompletionNotification();
            // Update GUI
                gui.updateButtonStates(false);
                gui.updateStatus("⏹️ Bot stopped - Ready for next session");
            BotUtils.log("✅ Bot stopped successfully");
            BotUtils.logError("Error in stop execution", e);
    public boolean onTestWebhook(String webhookUrl) {
            if (discordManager != null) {
                BotUtils.log("🧪 Testing Discord webhook...");
                boolean success = discordManager.testWebhook(webhookUrl);
                if (success) {
                    BotUtils.log("✅ Discord webhook test successful");
                    BotUtils.log("❌ Discord webhook test failed");
                return success;
            return false;
            BotUtils.logError("Error testing webhook", e);
    public void onSaveSettings() {
            if (settingsManager != null && gui != null) {
                if (currentConfig != null) {
                    settingsManager.updateCurrentConfig(currentConfig);
                boolean success = settingsManager.saveSettings("user_settings");
                    BotUtils.log("💾 Settings saved successfully");
                    BotUtils.log("❌ Failed to save settings");
            BotUtils.logError("Error saving settings", e);
    public void onLoadSettings() {
                GUIConfiguration loadedConfig = settingsManager.loadSettings("user_settings");
                if (loadedConfig != null) {
                    gui.applyConfiguration(loadedConfig);
                    BotUtils.log("📁 Settings loaded successfully");
                    BotUtils.log("❌ No saved settings found");
            BotUtils.logError("Error loading settings", e);
    public void onItemSelected(String itemName) {
            BotUtils.log("🎯 Item selected: " + itemName);
            if (priceManager != null) {
                priceManager.startMonitoring(itemName);
            BotUtils.logError("Error handling item selection", e);
    public void onConfigurationChanged() {
        BotUtils.log("📝 GUI configuration changed");
        if (gui != null) {
            currentConfig = gui.getCurrentConfiguration();
    public void onAlchemyConfigurationChanged(boolean enabled) {
        BotUtils.log("🖱️ Right-click alchemy configuration changed: " + (enabled ? "ENABLED" : "DISABLED"));
        if (enabled) {
            BotUtils.log("✅ Right-click alchemy auto-configuration will be applied when bot starts");
        } else {
            BotUtils.log("❌ Right-click alchemy auto-configuration disabled");
    // DREAMBOT PAINT METHOD
    public void onPaint(java.awt.Graphics2D g) {
                stats.currentState = alchingEngine != null ? alchingEngine.getCurrentStateDescription() : "Stopped";
                String status = botRunning ? "Running" : "Stopped";
                overlayRenderer.render(g, stats, status);
            // Don't log paint errors too frequently
            if (Math.random() < 0.01) {
                BotUtils.logError("Error in paint method", e);
    // UTILITY METHODS
    public void emergencyStop() {
            BotUtils.log("🚨 Emergency stop activated!");
                discordManager.sendEmergencyNotification("Emergency Stop", "Bot was stopped via emergency stop");
            BotUtils.logError("Error in emergency stop", e);
    public String getBotStatistics() {
            if (!botRunning || sessionStartTime == 0) {
                return "Bot is not running";
            long runtime = System.currentTimeMillis() - sessionStartTime;
            return String.format(
                    "Runtime: %s | Alchs: %,d | Profit: %,d GP | XP: %,d",
                    BotUtils.formatDuration(runtime),
                    totalAlchs,
                    totalProfit,
                    totalXpGained
            );
            BotUtils.logError("Error getting bot statistics", e);
            return "Error retrieving statistics";
}
