package Hi_alch.core;

import Hi_alch.*;

/**
 * ComponentManager - Manages All Bot Components
 *
 * Responsibilities:
 * - Holds references to all bot components
 * - Initializes all components in correct order
 * - Configures components with user settings
 * - Provides component cleanup on shutdown
 * - Provides getters for component access
 *
 * This class centralizes component lifecycle management,
 * making it easier to add/remove components and ensuring
 * proper initialization order and cleanup.
 *
 * Extracted from HighAlchBot Phase 2 refactoring.
 */
public class ComponentManager {

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

    // ===========================================
    // PROFESSIONAL FEATURES
    // ===========================================

    private SessionGoalsManager goalsManager;
    private BankManager bankManager;
    private LocationManager locationManager;
    private CSVExporter csvExporter;
    private GESlotManager geSlotManager;
    private ProfileManager profileManager;

    // ===========================================
    // COMPONENT INITIALIZATION
    // ===========================================

    /**
     * Initialize all bot components in correct order
     */
    public void initializeAllComponents() {
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

            // Initialize professional features
            initializeProfessionalFeatures();

            BotUtils.log("✅ All components (including professional features) initialized successfully");

        } catch (Exception e) {
            BotUtils.logError("Error initializing components", e);
            throw e;
        }
    }

    /**
     * Initialize professional features
     */
    private void initializeProfessionalFeatures() {
        try {
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

        } catch (Exception e) {
            BotUtils.logError("Error initializing professional features", e);
            throw e;
        }
    }

    // ===========================================
    // COMPONENT CONFIGURATION
    // ===========================================

    /**
     * Configure all components with the provided configuration
     * @param config The GUI configuration to apply
     */
    public void configureAllComponents(AlchBotGUI.GUIConfiguration config) {
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

            // Configure professional features
            configureProfessionalFeatures(config);

            BotUtils.log("✅ All components (including professional features) configured successfully");

        } catch (Exception e) {
            BotUtils.logError("Error configuring components", e);
            // Don't throw here, continue with default configuration
        }
    }

    /**
     * Configure professional features
     */
    private void configureProfessionalFeatures(AlchBotGUI.GUIConfiguration config) {
        try {
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

        } catch (Exception e) {
            BotUtils.logError("Error configuring professional features", e);
        }
    }

    // ===========================================
    // COMPONENT SHUTDOWN
    // ===========================================

    /**
     * Shutdown all components and cleanup resources
     */
    public void shutdownAllComponents() {
        try {
            BotUtils.log("🛑 Shutting down components...");

            // Stop price monitoring
            if (priceManager != null) {
                priceManager.stopMonitoring();
            }

            // Cleanup GUI
            if (gui != null) {
                gui.dispose();
            }

            BotUtils.log("✅ Components shutdown complete");

        } catch (Exception e) {
            BotUtils.logError("Error shutting down components", e);
        }
    }

    // ===========================================
    // COMPONENT GETTERS - CORE COMPONENTS
    // ===========================================

    public AlchBotGUI getGui() {
        return gui;
    }

    public void setGui(AlchBotGUI gui) {
        this.gui = gui;
    }

    public AlchingEngine getAlchingEngine() {
        return alchingEngine;
    }

    public PriceManager getPriceManager() {
        return priceManager;
    }

    public AntibanSystem getAntibanSystem() {
        return antibanSystem;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public OverlayRenderer getOverlayRenderer() {
        return overlayRenderer;
    }

    public ModernPaintRenderer getModernPaint() {
        return modernPaint;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    // ===========================================
    // COMPONENT GETTERS - PROFESSIONAL FEATURES
    // ===========================================

    public SessionGoalsManager getGoalsManager() {
        return goalsManager;
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public CSVExporter getCsvExporter() {
        return csvExporter;
    }

    public GESlotManager getGeSlotManager() {
        return geSlotManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
