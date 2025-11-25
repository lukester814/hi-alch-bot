package Hi_alch.core;

import Hi_alch.utils.BotUtils;
import Hi_alch.engine.AlchingEngine;
import Hi_alch.managers.PriceManager;
import Hi_alch.managers.AntibanSystem;
import Hi_alch.managers.DiscordManager;
import Hi_alch.managers.SettingsManager;
import Hi_alch.overlay.OverlayRenderer;
import Hi_alch.gui.AlchBotGUI;
import Hi_alch.gui.GUIConfiguration;
import Hi_alch.gui.GUIEventListener;
import javax.swing.SwingUtilities;

/**
 * Handles initialization of all bot components
 * Separates initialization logic from main bot class
 */
public class BotComponentInitializer {

    private AlchBotGUI gui;
    private AlchingEngine alchingEngine;
    private PriceManager priceManager;
    private AntibanSystem antibanSystem;
    private DiscordManager discordManager;
    private OverlayRenderer overlayRenderer;
    private SettingsManager settingsManager;

    public BotComponentInitializer() {
    }

    /**
     * Initialize all bot components
     */
    public void initializeComponents() {
        try {
            BotUtils.log("🔧 Initializing bot components...");

            // Initialize in dependency order
            priceManager = new PriceManager();
            BotUtils.log("💰 PriceManager initialized");

            antibanSystem = new AntibanSystem();
            BotUtils.log("🛡️ AntibanSystem initialized");

            discordManager = new DiscordManager();
            BotUtils.log("📱 DiscordManager initialized");

            alchingEngine = new AlchingEngine(priceManager, antibanSystem, discordManager);
            BotUtils.log("⚗️ AlchingEngine initialized");

            overlayRenderer = new OverlayRenderer();
            BotUtils.log("🖼️ OverlayRenderer initialized");

            settingsManager = new SettingsManager();
            BotUtils.log("💾 SettingsManager initialized");

            BotUtils.log("✅ All components initialized successfully");

        } catch (Exception e) {
            BotUtils.logError("Error initializing components", e);
            throw e;
        }
    }

    /**
     * Initialize GUI with proper threading
     */
    public void initializeGUI(Object eventListener) {
        try {
            BotUtils.log("🖼️ Initializing GUI...");

            SwingUtilities.invokeLater(() -> {
                try {
                    BotUtils.log("🎨 Creating AlchBotGUI instance...");
                    gui = new AlchBotGUI();

                    BotUtils.log("🔗 Setting event listener...");
                    if (eventListener instanceof Hi_alch.gui.GUIEventListener) {
                        gui.setEventListener((Hi_alch.gui.GUIEventListener) eventListener);
                    }

                    BotUtils.log("📱 Showing GUI...");
                    gui.showGUI();

                    BotUtils.log("✅ GUI initialized and displayed");

                } catch (Exception e) {
                    BotUtils.logError("Error creating GUI", e);
                    gui = null;
                }
            });

        } catch (Exception e) {
            BotUtils.logError("Error initializing GUI", e);
            throw e;
        }
    }

    /**
     * Load default settings from SettingsManager
     */
    public void loadDefaultSettings() {
        try {
            if (settingsManager != null) {
                Hi_alch.gui.GUIConfiguration loadedConfig = settingsManager.loadDefaultSettings();

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

    // Getters
    public AlchBotGUI getGui() { return gui; }
    public AlchingEngine getAlchingEngine() { return alchingEngine; }
    public PriceManager getPriceManager() { return priceManager; }
    public AntibanSystem getAntibanSystem() { return antibanSystem; }
    public DiscordManager getDiscordManager() { return discordManager; }
    public OverlayRenderer getOverlayRenderer() { return overlayRenderer; }
    public SettingsManager getSettingsManager() { return settingsManager; }
}
