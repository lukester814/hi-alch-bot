package Hi_alch;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// Import extracted GUI components
import static Hi_alch.GUIStyles.*;
import Hi_alch.gui.panels.MainConfigPanel;
import Hi_alch.gui.panels.AdvancedSettingsPanel;
import Hi_alch.gui.panels.GUIDataPanel;
import Hi_alch.gui.panels.GUIMulePanel;
import Hi_alch.gui.panels.GUISessionGoalsPanel;
import Hi_alch.gui.panels.GUIBreaksPanel;

/**
 * Professional High Alchemy Bot GUI - COMPLETE VERSION WITH ALL FIXES
 *
 * Features:
 * - Question mark tooltips with hover explanations
 * - F2P and P2P item categories with filtering
 * - Professional theme compatible with DreamBot's Substance
 * - Right-click alchemy configuration integration
 * - Advanced settings with anti-ban controls
 * - Discord webhook integration with testing
 * - Live market data monitoring
 * - Skip buying functionality with restock control
 * - Hide/show G.E. panels based on skip buying setting
 *
 * This is the COMPLETE version with all fixes integrated!
 */
public class AlchBotGUI {

    // ===========================================
    // GUI COMPONENTS
    // ===========================================

    // Main frame and layout
    private JFrame frame;
    private JTabbedPane tabbedPane;

    // Panel Components
    private MainConfigPanel mainConfigPanel;
    private AdvancedSettingsPanel advancedSettingsPanel;
    private GUIDataPanel guiDataPanel;
    private GUIMulePanel guiMulePanel;
    private GUISessionGoalsPanel guiSessionGoalsPanel;
    private GUIBreaksPanel guiBreaksPanel;

    // ===========================================
    // EVENT HANDLING
    // ===========================================

    private GUIEventListener eventListener;

    /**
     * Event listener interface for GUI interactions
     */
    public interface GUIEventListener {
        void onStartBot(GUIConfiguration config);
        void onStopBot();
        boolean onTestWebhook(String webhookUrl);
        void onSaveSettings();
        void onLoadSettings();
        void onItemSelected(String itemName);
        void onConfigurationChanged();
        void onAlchemyConfigurationChanged(boolean enabled);
    }

    /**
     * Configuration data class - WITH ALL FIELDS
     */
    public static class GUIConfiguration {
        public String selectedItemName;
        public int selectedItemId;
        public int buyLimit;
        public double priceMarkup;
        public int natureRuneAmount;
        public boolean smartProfitEnabled;
        public boolean worldHopEnabled;
        public boolean alchConfigEnabled;
        public boolean skipBuying;
        public boolean restockWhenEmpty;
        public String discordWebhookUrl;
        public boolean discordNotificationsEnabled;
        public boolean antibanEnabled;
        public int antibanAggression;
        public boolean breakSystemEnabled;
        public int minBreakMinutes;
        public int maxBreakMinutes;
        public boolean fatigueSystemEnabled;
        public boolean profileSeedingEnabled;
        public String userProfileSeed;
        public int currentFatigueLevel;

        @Override
        public String toString() {
            return String.format("Item: %s | Limit: %d | Markup: %.1f%% | Runes: %d | Skip: %s | Restock: %s",
                    selectedItemName, buyLimit, priceMarkup, natureRuneAmount, skipBuying, restockWhenEmpty);
        }
    }

    // ===========================================
    // CONSTRUCTOR & INITIALIZATION
    // ===========================================

    public AlchBotGUI() {
        BotUtils.log("🎨 Creating professional GUI with ALL fixes...");

        try {
            initializeGUI();
            BotUtils.log("✅ GUI constructor completed with all features");
        } catch (Exception e) {
            BotUtils.logError("Error in GUI constructor", e);
            throw new RuntimeException("Failed to initialize GUI", e);
        }
    }

    /**
     * Initialize the complete GUI system
     */
    private void initializeGUI() {
        BotUtils.log("🎨 Starting professional GUI initialization...");

        try {
            setLookAndFeel();
            createMainFrame();

            if (frame == null) {
                throw new RuntimeException("Failed to create main frame");
            }

            createTabbedPane();

            if (tabbedPane == null) {
                throw new RuntimeException("Failed to create tabbed pane");
            }

            setupEventHandlers();
            finalizeFrame();

            BotUtils.log("✅ Professional GUI initialization completed with all features");

        } catch (Exception e) {
            BotUtils.logError("Error during GUI initialization", e);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Set professional look and feel with modern styling
     */
    private void setLookAndFeel() {
        try {
            BotUtils.log("🎨 Using DreamBot's default Substance theme (no changes needed)");
        } catch (Exception e) {
            BotUtils.log("⚠️ Look and feel setup skipped due to error: " + e.getMessage());
        }
    }

    /**
     * Create main frame with professional styling
     */
    private void createMainFrame() {
        frame = new JFrame("OSRS High Alchemy Bot v2.0 - Professional Edition");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(450, 700);
        frame.setMinimumSize(new Dimension(420, 650));
        frame.setResizable(true);
        frame.setAlwaysOnTop(true);

        BotUtils.log("🖼️ Professional main frame created (compatible with Substance theme)");
    }

    /**
     * Create tabbed pane with all professional tabs
     */
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Create all professional panels
        mainConfigPanel = new MainConfigPanel();
        JScrollPane mainScrollPane = new JScrollPane(mainConfigPanel);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        advancedSettingsPanel = new AdvancedSettingsPanel();
        JScrollPane settingsScrollPane = new JScrollPane(advancedSettingsPanel);
        settingsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        settingsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        settingsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Use extracted GUIDataPanel component
        guiDataPanel = new GUIDataPanel();
        guiDataPanel.setupEventHandlers();
        JScrollPane dataScrollPane = new JScrollPane(guiDataPanel.getPanel());
        dataScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dataScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dataScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Use extracted GUIMulePanel component
        guiMulePanel = new GUIMulePanel();
        guiMulePanel.setupEventHandlers();
        JScrollPane muleScrollPane = new JScrollPane(guiMulePanel.getPanel());
        muleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        muleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        muleScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // NEW PROFESSIONAL FEATURES PANELS
        // Session Goals Panel
        guiSessionGoalsPanel = new GUISessionGoalsPanel();
        JScrollPane goalsScrollPane = new JScrollPane(guiSessionGoalsPanel);
        goalsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        goalsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        goalsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Breaks & Antiban Panel
        guiBreaksPanel = new GUIBreaksPanel();
        JScrollPane breaksScrollPane = new JScrollPane(guiBreaksPanel);
        breaksScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        breaksScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        breaksScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add tabs without icons (as requested)
        tabbedPane.addTab("Configuration", null, mainScrollPane, "Main bot configuration and item selection");
        tabbedPane.addTab("Advanced", null, settingsScrollPane, "Anti-ban settings and Discord integration");
        tabbedPane.addTab("Session Goals", null, goalsScrollPane, "Configure auto-stop conditions and goals");
        tabbedPane.addTab("Breaks & Antiban", null, breaksScrollPane, "Configure breaks and antiban behaviors");
        tabbedPane.addTab("Analytics", null, dataScrollPane, "Live market data and profit analysis");
        tabbedPane.addTab("Mule Support", null, muleScrollPane, "Configure automated mule trading");

        tabbedPane.setSelectedIndex(0);

        // Tab change listener for data refresh
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 2 && guiDataPanel != null) { // Analytics tab
                SwingUtilities.invokeLater(() -> guiDataPanel.refreshDataTable());
            }
        });

        BotUtils.log("📑 Professional tabbed interface created (Substance theme compatible)");
    }

    // ===========================================
    // STYLING METHODS (delegated to GUIStyles)
    // ===========================================

    private TitledBorder createProfessionalBorder(String title) {
        return GUIStyles.createProfessionalBorder(title);
    }

    private JLabel createStyledLabel(String text) {
        return GUIStyles.createStyledLabel(text);
    }

    private JLabel createQuestionMark(String tooltipText) {
        return GUIStyles.createQuestionMark(tooltipText);
    }

    private void styleTextField(JTextField field) {
        GUIStyles.styleTextField(field);
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        GUIStyles.styleComboBox(comboBox);
    }

    private void styleSpinner(JSpinner spinner) {
        GUIStyles.styleSpinner(spinner);
    }

    private void styleCheckBox(JCheckBox checkBox) {
        GUIStyles.styleCheckBox(checkBox);
    }

    private void styleButton(JButton button, Color color) {
        GUIStyles.styleButton(button, color);
    }

    // ===========================================
    // PANEL VISIBILITY CONTROL - NEW METHODS
    // ===========================================

    /**
     * Show or hide buying-related panels based on skip buying setting
     */
    private void toggleBuyingPanels(boolean showBuying) {
        SwingUtilities.invokeLater(() -> {
            Component mainTab = tabbedPane.getComponentAt(0); // Configuration tab

            if (mainTab instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) mainTab;
                Component viewport = scrollPane.getViewport().getView();

                if (viewport instanceof JPanel) {
                    toggleBuyingComponentsInPanel((JPanel) viewport, showBuying);
                }
            }

            // Update specific spinners visibility
            if (mainConfigPanel.getBuyLimitSpinner() != null && mainConfigPanel.getBuyLimitSpinner().getParent() != null) {
                Component parent = mainConfigPanel.getBuyLimitSpinner().getParent();
                while (parent != null && !(parent instanceof JPanel)) {
                    parent = parent.getParent();
                }
                if (parent instanceof JPanel) {
                    JPanel parentPanel = (JPanel) parent;
                    // Check if this is the trading config panel
                    if (parentPanel.getBorder() instanceof TitledBorder) {
                        TitledBorder border = (TitledBorder) parentPanel.getBorder();
                        if (border.getTitle().contains("Trading")) {
                            parentPanel.setVisible(showBuying);
                        }
                    }
                }
            }

            tabbedPane.revalidate();
            tabbedPane.repaint();
        });
    }

    /**
     * Recursively toggle buying components in panels
     */
    private void toggleBuyingComponentsInPanel(JPanel panel, boolean showBuying) {
        Component[] components = panel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel subPanel = (JPanel) comp;

                String panelName = "";
                if (subPanel.getBorder() instanceof TitledBorder) {
                    TitledBorder border = (TitledBorder) subPanel.getBorder();
                    panelName = border.getTitle();
                }

                // Hide G.E. and trading panels when skip buying is enabled
                if (panelName.contains("Trading Configuration") ||
                        panelName.contains("Grand Exchange") ||
                        panelName.contains("Buy Limit") ||
                        panelName.contains("Purchase")) {
                    subPanel.setVisible(showBuying);
                }

                // Recursively check sub-panels
                toggleBuyingComponentsInPanel(subPanel, showBuying);
            }
        }
    }

    // ===========================================
    // EVENT HANDLING & SETUP
    // ===========================================

    /**
     * Setup all professional event handlers WITH ALL FIXES
     */
    private void setupEventHandlers() {
        // Category selection handler
        mainConfigPanel.getItemCategoryCombo().addActionListener(e -> {
            String selected = (String) mainConfigPanel.getItemCategoryCombo().getSelectedItem();
            if ("F2P Items".equals(selected)) {
                mainConfigPanel.getItemDropdown().setModel(new DefaultComboBoxModel<>(mainConfigPanel.getF2PItems()));
            } else {
                mainConfigPanel.getItemDropdown().setModel(new DefaultComboBoxModel<>(mainConfigPanel.getP2PItems()));
            }
            mainConfigPanel.getItemDropdown().setSelectedIndex(0);
            updateProfitPreview();
        });

        // Item dropdown change handler
        mainConfigPanel.getItemDropdown().addActionListener(e -> {
            String selected = (String) mainConfigPanel.getItemDropdown().getSelectedItem();
            boolean isCustom = selected != null && selected.contains("Custom");

            mainConfigPanel.getCustomItemField().setVisible(isCustom);
            mainConfigPanel.getValidateItemButton().setVisible(isCustom);

            frame.revalidate();
            frame.repaint();

            updateProfitPreview();

            if (eventListener != null && selected != null && !selected.startsWith("Select")) {
                eventListener.onItemSelected(selected);
            }
        });

        // Custom item validation
        mainConfigPanel.getValidateItemButton().addActionListener(e -> {
            String customItem = mainConfigPanel.getCustomItemField().getText().trim();
            if (customItem.isEmpty()) {
                updateStatus("❌ Please enter an item name to validate");
                return;
            }

            mainConfigPanel.getValidateItemButton().setEnabled(false);
            mainConfigPanel.getValidateItemButton().setText("⏳");

            new Thread(() -> {
                try {
                    boolean isValid = validateItemOnline(customItem);

                    SwingUtilities.invokeLater(() -> {
                        if (isValid) {
                            updateStatus("✅ Item '" + customItem + "' validated successfully!");
                            mainConfigPanel.getCustomItemField().setBackground(new Color(34, 197, 94, 50));
                            if (eventListener != null) {
                                eventListener.onItemSelected(customItem);
                            }
                            updateProfitPreview();
                        } else {
                            updateStatus("❌ Item '" + customItem + "' not found in OSRS database");
                            mainConfigPanel.getCustomItemField().setBackground(new Color(239, 68, 68, 50));
                        }

                        mainConfigPanel.getValidateItemButton().setEnabled(true);
                        mainConfigPanel.getValidateItemButton().setText("✓ Validate");
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        updateStatus("❌ Error validating item: " + ex.getMessage());
                        mainConfigPanel.getCustomItemField().setBackground(new Color(239, 68, 68, 50));
                        mainConfigPanel.getValidateItemButton().setEnabled(true);
                        mainConfigPanel.getValidateItemButton().setText("✓ Validate");
                    });
                }
            }).start();
        });

        // FIXED: Skip buying checkbox handler with panel toggling
        mainConfigPanel.getSkipBuyingCheck().addActionListener(e -> {
            boolean skipBuyingEnabled = mainConfigPanel.getSkipBuyingCheck().isSelected();

            // Enable/disable restock checkbox
            if (mainConfigPanel.getRestockWhenEmptyCheck() != null) {
                mainConfigPanel.getRestockWhenEmptyCheck().setEnabled(skipBuyingEnabled);
                if (!skipBuyingEnabled) {
                    mainConfigPanel.getRestockWhenEmptyCheck().setSelected(false);
                }
            }

            // Show/hide G.E. buying panels
            toggleBuyingPanels(!skipBuyingEnabled);

            // Update profit preview
            updateProfitPreview();

            // Update status
            if (skipBuyingEnabled) {
                updateStatus("💡 Skip buying enabled - Trading sections hidden");
            } else {
                updateStatus("🛒 Skip buying disabled - Trading sections shown");
            }
        });

        // Anti-ban aggression slider
        advancedSettingsPanel.getAntibanAggressionSlider().addChangeListener(e -> {
            int value = advancedSettingsPanel.getAntibanAggressionSlider().getValue();
            String description;
            Color color;
            if (value <= 3) {
                description = "Very Human-like";
                color = new Color(34, 197, 94);
            } else if (value <= 6) {
                description = "Balanced";
                color = new Color(251, 191, 36);
            } else {
                description = "Fast & Risky";
                color = new Color(239, 68, 68);
            }
            advancedSettingsPanel.getAggressionValueLabel().setText("Level: " + value + " (" + description + ")");
            advancedSettingsPanel.getAggressionValueLabel().setForeground(color);
        });

        // Right-click alchemy configuration
        mainConfigPanel.getAlchConfigCheck().addActionListener(e -> {
            boolean enabled = mainConfigPanel.getAlchConfigCheck().isSelected();
            if (enabled) {
                updateStatus("Right-click alchemy auto-configuration enabled");
                BotUtils.log("✅ Auto-Configure High Alchemy enabled - will configure on bot start");
            } else {
                updateStatus("Right-click alchemy auto-configuration disabled");
                BotUtils.log("❌ Auto-Configure High Alchemy disabled");
            }

            if (eventListener != null) {
                eventListener.onAlchemyConfigurationChanged(enabled);
            }
        });

        // Generate Seed Button
        advancedSettingsPanel.getGenerateSeedButton().addActionListener(e -> {
            advancedSettingsPanel.getGenerateSeedButton().setEnabled(false);
            advancedSettingsPanel.getGenerateSeedButton().setText("...");

            new Thread(() -> {
                try {
                    Thread.sleep(1000);

                    SwingUtilities.invokeLater(() -> {
                        String seed = generateUniqueProfileSeed();
                        advancedSettingsPanel.getUserProfileField().setText(seed);
                        updateStatus("Generated personalized anti-ban seed: " + seed);

                        advancedSettingsPanel.getGenerateSeedButton().setEnabled(true);
                        advancedSettingsPanel.getGenerateSeedButton().setText("Generate");
                    });

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });

        // Start/Stop button handlers
        mainConfigPanel.getStartButton().addActionListener(e -> {
            if (eventListener != null) {
                GUIConfiguration config = getCurrentConfiguration();
                eventListener.onStartBot(config);
                updateButtonStates(true);
            }
        });

        mainConfigPanel.getStopButton().addActionListener(e -> {
            if (eventListener != null) {
                eventListener.onStopBot();
                updateButtonStates(false);
            }
        });

        // Test webhook button
        advancedSettingsPanel.getTestWebhookButton().addActionListener(e -> testWebhookWithFeedback());

        // Refresh data button (handled by GUIDataPanel now)
        // refreshDataButton is created and handled within GUIDataPanel

        // Configuration change handlers
        mainConfigPanel.getBuyLimitSpinner().addChangeListener(e -> {
            updateProfitPreview();
            if (eventListener != null) eventListener.onConfigurationChanged();
        });

        mainConfigPanel.getPriceMarkupSpinner().addChangeListener(e -> {
            updateProfitPreview();
            if (eventListener != null) eventListener.onConfigurationChanged();
        });
    }

    /**
     * Test webhook with professional feedback
     */
    private void testWebhookWithFeedback() {
        String webhookUrl = advancedSettingsPanel.getWebhookField().getText().trim();

        if (webhookUrl.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter your Discord webhook URL first!\n\nTo create a webhook:\n1. Go to your Discord server\n2. Edit channel → Integrations → Webhooks\n3. Create webhook and copy URL",
                    "Discord Webhook Required",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        advancedSettingsPanel.getTestWebhookButton().setEnabled(false);
        advancedSettingsPanel.getTestWebhookButton().setText("Testing...");

        new Thread(() -> {
            try {
                if (eventListener != null) {
                    boolean success = eventListener.onTestWebhook(webhookUrl);

                    SwingUtilities.invokeLater(() -> {
                        if (success) {
                            JOptionPane.showMessageDialog(frame,
                                    "✅ Discord webhook test successful!\n\nCheck your Discord channel for the test message.\nYour bot is ready to send notifications!",
                                    "Webhook Test Successful",
                                    JOptionPane.INFORMATION_MESSAGE);
                            updateStatus("✅ Discord webhook connected successfully!");
                        } else {
                            JOptionPane.showMessageDialog(frame,
                                    "❌ Discord webhook test failed!\n\nPlease check:\n• Webhook URL is correct\n• Discord server is accessible\n• Webhook permissions are valid",
                                    "Webhook Test Failed",
                                    JOptionPane.ERROR_MESSAGE);
                            updateStatus("❌ Discord webhook test failed - check URL");
                        }

                        advancedSettingsPanel.getTestWebhookButton().setEnabled(true);
                        advancedSettingsPanel.getTestWebhookButton().setText("Test");
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Error testing webhook:\n" + ex.getMessage(),
                            "Webhook Error",
                            JOptionPane.ERROR_MESSAGE);

                    advancedSettingsPanel.getTestWebhookButton().setEnabled(true);
                    advancedSettingsPanel.getTestWebhookButton().setText("Test");
                });
            }
        }).start();
    }

    /**
     * Update profit preview with LIVE API data
     */
    private void updateProfitPreview() {
        try {
            String selectedItem = (String) mainConfigPanel.getItemDropdown().getSelectedItem();

            if (selectedItem == null || selectedItem.startsWith("Select")) {
                mainConfigPanel.getProfitPreviewLabel().setText("Select an item to see real profit analysis");
                mainConfigPanel.getProfitPreviewLabel().setForeground(new Color(156, 163, 175));
                return;
            }

            if (selectedItem.contains("Custom")) {
                String customItem = mainConfigPanel.getCustomItemField().getText().trim();
                if (!customItem.isEmpty() && mainConfigPanel.getFoundCustomItemId() > 0) {
                    selectedItem = customItem;
                } else {
                    mainConfigPanel.getProfitPreviewLabel().setText("Enter and validate custom item for real-time analysis");
                    mainConfigPanel.getProfitPreviewLabel().setForeground(new Color(251, 191, 36));
                    return;
                }
            }

            int buyLimit = (Integer) mainConfigPanel.getBuyLimitSpinner().getValue();
            double markup = (Double) mainConfigPanel.getPriceMarkupSpinner().getValue();

            ItemSearchAPI.ItemSearchResult apiData = ItemSearchAPI.searchItem(selectedItem);

            if (apiData != null && apiData.isValid()) {
                int buyPrice = apiData.averagePrice > 0 ? apiData.averagePrice : apiData.highPrice;
                int alchValue = apiData.alchValue;

                int adjustedBuyPrice = (int) (buyPrice * (1.0 + markup / 100.0));
                int natureRuneCost = 220;
                int profitPerItem = alchValue - adjustedBuyPrice - natureRuneCost;
                int totalProfit = profitPerItem * buyLimit;

                String profitText;
                Color profitColor;

                if (profitPerItem > 0) {
                    profitText = String.format("💰 %s: +%s GP profit per item | Total: +%s GP (LIVE DATA)",
                            selectedItem,
                            BotUtils.formatNumber(profitPerItem),
                            BotUtils.formatNumber(totalProfit));
                    profitColor = new Color(34, 197, 94);
                } else {
                    profitText = String.format("⚠️ %s: %s GP loss per item | Total: %s GP (LIVE DATA)",
                            selectedItem,
                            BotUtils.formatNumber(Math.abs(profitPerItem)),
                            BotUtils.formatNumber(totalProfit));
                    profitColor = new Color(239, 68, 68);
                }

                mainConfigPanel.getProfitPreviewLabel().setText(profitText);
                mainConfigPanel.getProfitPreviewLabel().setForeground(profitColor);
            } else {
                mainConfigPanel.getProfitPreviewLabel().setText("Unable to fetch live data for " + selectedItem);
                mainConfigPanel.getProfitPreviewLabel().setForeground(new Color(239, 68, 68));
            }

        } catch (Exception e) {
            mainConfigPanel.getProfitPreviewLabel().setText("Error calculating profit preview");
            mainConfigPanel.getProfitPreviewLabel().setForeground(new Color(239, 68, 68));
        }
    }

    /**
     * Finalize frame setup
     */
    private void finalizeFrame() {
        frame.add(tabbedPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    // ===========================================
    // PUBLIC INTERFACE METHODS
    // ===========================================

    /**
     * Set event listener for GUI interactions
     */
    public void setEventListener(GUIEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Show the professional GUI
     */
    public void show() {
        showGUI();
    }

    /**
     * Show the professional GUI
     */
    public void showGUI() {
        BotUtils.log("🖼️ Displaying professional GUI...");

        if (frame != null) {
            try {
                frame.setVisible(true);
                frame.toFront();
                frame.requestFocus();
                positionOverChatBox();

                BotUtils.log("✅ Professional GUI displayed at position: (" + frame.getX() + ", " + frame.getY() + ")");

            } catch (Exception e) {
                BotUtils.logError("Error showing professional GUI", e);
                e.printStackTrace();
            }
        } else {
            BotUtils.logError("❌ Cannot show GUI - frame is null!");
        }
    }

    /**
     * Position GUI over the game client chat box area
     */
    private void positionOverChatBox() {
        try {
            int chatBoxX = 25;
            int chatBoxY = 300;

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (screenSize.height < 900) {
                chatBoxY = 50;
            } else if (screenSize.height >= 1200) {
                chatBoxY = 400;
            }

            frame.setLocation(chatBoxX, chatBoxY);
            BotUtils.log("🖼️ Professional GUI positioned at (" + chatBoxX + ", " + chatBoxY + ")");

        } catch (Exception e) {
            BotUtils.logError("Error positioning GUI", e);
            frame.setLocation(25, 25);
        }
    }

    /**
     * Hide the GUI
     */
    public void hideGUI() {
        if (frame != null) {
            frame.setVisible(false);
        }
    }

    /**
     * Update status message
     */
    public void updateStatus(String status) {
        mainConfigPanel.setStatus(status);
    }

    /**
     * Update button states based on bot running status
     */
    public void updateButtonStates(boolean botRunning) {
        SwingUtilities.invokeLater(() -> {
            mainConfigPanel.getStartButton().setEnabled(!botRunning);
            mainConfigPanel.getStopButton().setEnabled(botRunning);

            if (botRunning) {
                updateStatus("High Alchemy Bot is running - Monitor progress in overlay");
                mainConfigPanel.getStartButton().setText("Bot Running...");
                frame.setVisible(false);
            } else {
                updateStatus("Bot stopped - Ready to start new session");
                mainConfigPanel.getStartButton().setText("Start High Alchemy Bot");
                frame.setVisible(true);
                frame.toFront();
            }
        });
    }

    /**
     * Get current configuration from GUI
     */
    public GUIConfiguration getCurrentConfiguration() {
        GUIConfiguration config = new GUIConfiguration();

        // Get selected item
        String selectedItem = (String) mainConfigPanel.getItemDropdown().getSelectedItem();
        if (selectedItem != null && selectedItem.contains("Custom")) {
            config.selectedItemName = mainConfigPanel.getCustomItemField().getText().trim();
            config.selectedItemId = mainConfigPanel.getFoundCustomItemId();
        } else {
            config.selectedItemName = selectedItem;
            config.selectedItemId = getItemIdFromName(config.selectedItemName);
        }

        // Get trading configuration
        config.buyLimit = (Integer) mainConfigPanel.getBuyLimitSpinner().getValue();
        config.priceMarkup = (Double) mainConfigPanel.getPriceMarkupSpinner().getValue();
        config.natureRuneAmount = (Integer) mainConfigPanel.getNatureRuneSpinner().getValue();

        // Get bot features
        config.smartProfitEnabled = mainConfigPanel.getSmartProfitCheck().isSelected();
        config.worldHopEnabled = mainConfigPanel.getWorldHopCheck().isSelected();
        config.alchConfigEnabled = mainConfigPanel.getAlchConfigCheck().isSelected();
        config.skipBuying = mainConfigPanel.getSkipBuyingCheck().isSelected();
        config.restockWhenEmpty = mainConfigPanel.getRestockWhenEmptyCheck() != null ? mainConfigPanel.getRestockWhenEmptyCheck().isSelected() : false;

        // Get Discord settings
        config.discordWebhookUrl = advancedSettingsPanel.getWebhookField().getText().trim();
        config.discordNotificationsEnabled = advancedSettingsPanel.getEnableDiscordCheck().isSelected();

        // Get anti-ban settings
        config.antibanEnabled = advancedSettingsPanel.getEnableAntibanCheck().isSelected();
        config.antibanAggression = advancedSettingsPanel.getAntibanAggressionSlider().getValue();
        config.breakSystemEnabled = advancedSettingsPanel.getBreakSystemCheck().isSelected();
        config.minBreakMinutes = (Integer) advancedSettingsPanel.getMinBreakSpinner().getValue();
        config.maxBreakMinutes = (Integer) advancedSettingsPanel.getMaxBreakSpinner().getValue();
        config.fatigueSystemEnabled = advancedSettingsPanel.getFatigueSystemCheck().isSelected();
        config.profileSeedingEnabled = advancedSettingsPanel.getProfileSeedingCheck().isSelected();
        config.userProfileSeed = advancedSettingsPanel.getUserProfileField().getText().trim();

        return config;
    }

    /**
     * Apply configuration to GUI
     */
    public void applyConfiguration(GUIConfiguration config) {
        SwingUtilities.invokeLater(() -> {
            if (config.selectedItemName != null) {
                boolean foundInDropdown = false;

                // Check current category items
                for (int i = 0; i < mainConfigPanel.getItemDropdown().getItemCount(); i++) {
                    if (config.selectedItemName.equals(mainConfigPanel.getItemDropdown().getItemAt(i))) {
                        mainConfigPanel.getItemDropdown().setSelectedIndex(i);
                        foundInDropdown = true;
                        break;
                    }
                }

                // If not found, check other category
                if (!foundInDropdown) {
                    String currentCategory = (String) mainConfigPanel.getItemCategoryCombo().getSelectedItem();
                    String otherCategory = "F2P Items".equals(currentCategory) ? "P2P Items" : "F2P Items";
                    mainConfigPanel.getItemCategoryCombo().setSelectedItem(otherCategory);

                    for (int i = 0; i < mainConfigPanel.getItemDropdown().getItemCount(); i++) {
                        if (config.selectedItemName.equals(mainConfigPanel.getItemDropdown().getItemAt(i))) {
                            mainConfigPanel.getItemDropdown().setSelectedIndex(i);
                            foundInDropdown = true;
                            break;
                        }
                    }
                }

                // If still not found, use custom
                if (!foundInDropdown) {
                    mainConfigPanel.getItemDropdown().setSelectedItem(mainConfigPanel.getItemDropdown().getItemAt(mainConfigPanel.getItemDropdown().getItemCount() - 1));
                    mainConfigPanel.getCustomItemField().setText(config.selectedItemName);
                    mainConfigPanel.getCustomItemField().setVisible(true);
                    mainConfigPanel.getValidateItemButton().setVisible(true);
                }
            }

            // Set trading configuration
            mainConfigPanel.getBuyLimitSpinner().setValue(config.buyLimit);
            mainConfigPanel.getPriceMarkupSpinner().setValue(config.priceMarkup);
            mainConfigPanel.getNatureRuneSpinner().setValue(config.natureRuneAmount);

            // Set bot features
            mainConfigPanel.getSmartProfitCheck().setSelected(config.smartProfitEnabled);
            mainConfigPanel.getWorldHopCheck().setSelected(config.worldHopEnabled);
            mainConfigPanel.getAlchConfigCheck().setSelected(config.alchConfigEnabled);
            mainConfigPanel.getSkipBuyingCheck().setSelected(config.skipBuying);

            // Set Discord settings
            if (config.discordWebhookUrl != null) {
                advancedSettingsPanel.getWebhookField().setText(config.discordWebhookUrl);
            }
            advancedSettingsPanel.getEnableDiscordCheck().setSelected(config.discordNotificationsEnabled);

            // Set anti-ban settings
            advancedSettingsPanel.getEnableAntibanCheck().setSelected(config.antibanEnabled);
            advancedSettingsPanel.getAntibanAggressionSlider().setValue(config.antibanAggression);
            advancedSettingsPanel.getBreakSystemCheck().setSelected(config.breakSystemEnabled);
            advancedSettingsPanel.getMinBreakSpinner().setValue(config.minBreakMinutes);
            advancedSettingsPanel.getMaxBreakSpinner().setValue(config.maxBreakMinutes);
            advancedSettingsPanel.getFatigueSystemCheck().setSelected(config.fatigueSystemEnabled);
            advancedSettingsPanel.getProfileSeedingCheck().setSelected(config.profileSeedingEnabled);
            if (config.userProfileSeed != null) {
                advancedSettingsPanel.getUserProfileField().setText(config.userProfileSeed);
            }

            updateProfitPreview();
        });
    }

    /**
     * Refresh the data table
     */
    public void refreshDataTable() {
        // Data table is now handled by GUIDataPanel
        if (guiDataPanel != null) {
            guiDataPanel.refreshDataTable();
        }
    }

    /**
     * Check if GUI is currently visible
     */
    public boolean isVisible() {
        return frame != null && frame.isVisible();
    }

    /**
     * Update fatigue level
     */
    public void updateFatigueLevel(int fatiguePercent) {
        advancedSettingsPanel.setFatigueLevel(fatiguePercent);
    }

    /**
     * Reset fatigue level after break
     */
    public void resetFatigueLevel() {
        updateFatigueLevel(0);
        updateStatus("😌 Fatigue reset - Bot is fresh and alert after break");
    }

    /**
     * Check if right-click alchemy auto-configuration is enabled
     */
    public boolean isAlchemyConfigurationEnabled() {
        return mainConfigPanel.getAlchConfigCheck() != null && mainConfigPanel.getAlchConfigCheck().isSelected();
    }

    /**
     * Bring GUI to front if it exists
     */
    public void bringToFront() {
        if (frame != null && frame.isVisible()) {
            SwingUtilities.invokeLater(() -> {
                frame.toFront();
                frame.requestFocus();
            });
        }
    }

    /**
     * Validate custom item online using OSRS API
     */
    private boolean validateItemOnline(String itemName) {
        try {
            BotUtils.log("🔍 Validating item: " + itemName);

            String cleanName = itemName.trim();
            if (cleanName.isEmpty()) {
                return false;
            }

            ItemSearchAPI.ItemSearchResult result = ItemSearchAPI.searchItem(cleanName);

            if (result != null && result.isValid()) {
                BotUtils.log("✅ Item found: " + result.toString());

                mainConfigPanel.getCustomItemField().setText(result.itemName);
                mainConfigPanel.setFoundCustomItemId(result.itemId);

                StringBuilder status = new StringBuilder();
                status.append("✅ Found: ").append(result.itemName);
                status.append(" (ID: ").append(result.itemId).append(")");

                if (result.averagePrice > 0) {
                    status.append(" - Price: ").append(BotUtils.formatNumber(result.averagePrice)).append(" GP");
                }

                if (result.alchValue > 0) {
                    status.append(", Alch: ").append(BotUtils.formatNumber(result.alchValue)).append(" GP");
                }

                if (result.buyLimit > 0) {
                    status.append(", Limit: ").append(result.buyLimit);
                    mainConfigPanel.getBuyLimitSpinner().setValue(result.buyLimit);
                }

                updateStatus(status.toString());
                updateProfitPreview();

                return true;
            } else {
                BotUtils.log("❌ Item not found in OSRS database: " + cleanName);
                mainConfigPanel.setFoundCustomItemId(-1);
                updateStatus("❌ Item '" + cleanName + "' not found in OSRS database");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error validating item online", e);
            updateStatus("❌ Error validating item: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate unique profile seed for personalized anti-ban
     */
    private String generateUniqueProfileSeed() {
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 10000);
        String seed = "USR" + (timestamp % 100000) + "R" + random;
        BotUtils.log("🎲 Generated unique profile seed: " + seed);
        return seed;
    }

    /**
     * Convert item name to ID (for backup when API fails)
     */
    private int getItemIdFromName(String itemName) {
        if (itemName == null) return -1;

        String nameLower = itemName.toLowerCase().trim();

        switch (nameLower) {
            case "santa hat": return 1050;
            case "rune longsword": return 1303;
            case "rune 2h sword": return 1319;
            case "rune platebody": return 1127;
            case "rune platelegs": return 1079;
            case "rune plateskirt": return 1093;
            case "rune chainbody": return 1113;
            case "rune med helm": return 1147;
            case "rune full helm": return 1163;
            case "rune sq shield": return 1185;
            case "rune kiteshield": return 1201;
            case "rune scimitar": return 1333;
            case "rune battleaxe": return 1373;
            case "rune dagger": return 1213;
            case "dragon longsword": return 1305;
            case "dragon battleaxe": return 1377;
            case "dragon dagger": return 1215;
            case "dragon mace": return 1434;
            case "dragon scimitar": return 4587;
            case "green d'hide body": return 1135;
            case "blue d'hide body": return 2499;
            case "red d'hide body": return 2501;
            case "black d'hide body": return 2503;
            default:
                BotUtils.log("⚠️ Unknown item ID for: " + itemName);
                return -1;
        }
    }

    /**
     * Cleanup resources when closing
     */
    public void dispose() {
        try {
            BotUtils.log("🧹 Cleaning up GUI resources...");

            if (frame != null) {
                frame.setVisible(false);
                frame.dispose();
            }

            BotUtils.log("✅ GUI resources cleaned up successfully");

        } catch (Exception e) {
            BotUtils.logError("Error disposing GUI resources", e);
        }
    }
}