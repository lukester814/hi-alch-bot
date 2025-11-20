package Hi_alch.core;

import Hi_alch.*;
import javax.swing.SwingUtilities;

/**
 * GUICoordinator - Handles All GUI Interactions
 *
 * Responsibilities:
 * - GUI initialization
 * - Loading default settings
 * - Start bot event handling
 * - Stop bot event handling
 * - Webhook testing
 * - Settings save/load
 * - Item selection handling
 * - Configuration change handling
 * - Alchemy configuration changes
 *
 * This class implements the GUIEventListener interface and acts
 * as the bridge between the GUI and the bot's core logic.
 *
 * Extracted from HighAlchBot Phase 4 refactoring.
 */
public class GUICoordinator implements AlchBotGUI.GUIEventListener {

    private final ComponentManager componentManager;
    private final BotStateManager stateManager;
    private final ConfigurationValidator validator;
    private AlchBotGUI.GUIConfiguration currentConfig;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    /**
     * Create new GUI coordinator
     * @param componentManager Component manager for accessing components
     * @param stateManager State manager for bot state
     * @param validator Configuration validator
     */
    public GUICoordinator(ComponentManager componentManager, BotStateManager stateManager, ConfigurationValidator validator) {
        this.componentManager = componentManager;
        this.stateManager = stateManager;
        this.validator = validator;
    }

    // ===========================================
    // GUI INITIALIZATION
    // ===========================================

    /**
     * Initialize GUI with proper threading
     */
    public void initializeGUI() {
        try {
            BotUtils.log("🖼️ Initializing GUI...");

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        BotUtils.log("🎨 Creating AlchBotGUI instance...");
                        AlchBotGUI gui = new AlchBotGUI();

                        BotUtils.log("🔗 Setting event listener...");
                        gui.setEventListener(GUICoordinator.this);

                        BotUtils.log("📱 Showing GUI...");
                        gui.showGUI();

                        // Store GUI reference in component manager
                        componentManager.setGui(gui);

                        BotUtils.log("✅ GUI initialized and displayed");

                    } catch (Exception e) {
                        BotUtils.logError("Error creating GUI", e);
                        e.printStackTrace();

                        // Try to continue without GUI
                        BotUtils.log("⚠️ Continuing without GUI due to initialization error");
                        componentManager.setGui(null);
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
    public void loadDefaultSettings() {
        try {
            SettingsManager settingsManager = componentManager.getSettingsManager();
            AlchBotGUI gui = componentManager.getGui();

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
    // GUI EVENT HANDLERS - BOT CONTROL
    // ===========================================

    @Override
    public void onStartBot(AlchBotGUI.GUIConfiguration config) {
        try {
            if (stateManager.isBotRunning()) {
                BotUtils.log("⚠️ Bot is already running");
                return;
            }

            BotUtils.log("🚀 Starting bot with configuration: " + config.toString());

            // Validate configuration
            if (!validator.validateConfiguration(config)) {
                BotUtils.log("❌ Configuration validation failed");
                AlchBotGUI gui = componentManager.getGui();
                if (gui != null) {
                    gui.updateStatus("❌ Configuration validation failed: " + validator.getLastValidationError());
                }
                return;
            }

            // Store configuration
            currentConfig = config;

            // Initialize components with configuration
            componentManager.configureAllComponents(config);

            // Send start notification using the correct method
            DiscordManager discordManager = componentManager.getDiscordManager();
            if (discordManager != null && config.discordNotificationsEnabled) {
                discordManager.sendStartupNotification(config);
            }

            // Update GUI
            AlchBotGUI gui = componentManager.getGui();
            if (gui != null) {
                gui.updateButtonStates(true);
                gui.updateStatus("🚀 High Alchemy Bot started successfully!");
            } else {
                BotUtils.log("⚠️ GUI not available - bot started without GUI interface");
            }

            // Start bot
            stateManager.startBot();

            BotUtils.log("✅ Bot started successfully!");

        } catch (Exception e) {
            BotUtils.logError("Error starting bot", e);
            e.printStackTrace();
            AlchBotGUI gui = componentManager.getGui();
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
            if (!stateManager.isBotRunning()) {
                BotUtils.log("⚠️ Bot is not running");
                return;
            }

            BotUtils.log("🛑 Stopping bot...");
            stateManager.stopBot();

        } catch (Exception e) {
            BotUtils.logError("Error stopping bot", e);
        }
    }

    // ===========================================
    // GUI EVENT HANDLERS - DISCORD
    // ===========================================

    @Override
    public boolean onTestWebhook(String webhookUrl) {
        try {
            DiscordManager discordManager = componentManager.getDiscordManager();
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

    // ===========================================
    // GUI EVENT HANDLERS - SETTINGS
    // ===========================================

    @Override
    public void onSaveSettings() {
        try {
            SettingsManager settingsManager = componentManager.getSettingsManager();
            AlchBotGUI gui = componentManager.getGui();

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
            SettingsManager settingsManager = componentManager.getSettingsManager();
            AlchBotGUI gui = componentManager.getGui();

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

    // ===========================================
    // GUI EVENT HANDLERS - ITEM & CONFIG
    // ===========================================

    @Override
    public void onItemSelected(String itemName) {
        try {
            BotUtils.log("🎯 Item selected: " + itemName);

            // Start price monitoring for selected item
            PriceManager priceManager = componentManager.getPriceManager();
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
        AlchBotGUI gui = componentManager.getGui();
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
    // CONFIGURATION ACCESS
    // ===========================================

    /**
     * Get current configuration
     */
    public AlchBotGUI.GUIConfiguration getCurrentConfiguration() {
        return currentConfig;
    }
}
