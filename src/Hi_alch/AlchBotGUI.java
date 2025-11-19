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

    // Main Tab Components
    private JComboBox<String> itemCategoryCombo;
    private JComboBox<String> itemDropdown;
    private JTextField customItemField;
    private JButton validateItemButton;
    private JSpinner buyLimitSpinner;
    private JSpinner priceMarkupSpinner;
    private JSpinner natureRuneSpinner;
    private JCheckBox smartProfitCheck;
    private JCheckBox worldHopCheck;
    private JCheckBox alchConfigCheck;
    private JCheckBox skipBuyingCheck;
    private JCheckBox restockWhenEmptyCheck;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel profitPreviewLabel;

    // Settings Tab Components
    private JTextField webhookField;
    private JButton testWebhookButton;
    private JCheckBox enableDiscordCheck;
    private JCheckBox enableAntibanCheck;
    private JSlider antibanAggressionSlider;
    private JLabel aggressionValueLabel;
    private JCheckBox breakSystemCheck;
    private JSpinner minBreakSpinner;
    private JSpinner maxBreakSpinner;
    private JCheckBox fatigueSystemCheck;
    private JCheckBox profileSeedingCheck;
    private JTextField userProfileField;
    private JButton generateSeedButton;
    private JProgressBar fatigueBar;
    private JLabel fatigueLabel;

    // Data Tab Components - Using extracted GUIDataPanel
    private GUIDataPanel guiDataPanel;

    // Mule Tab Components - Using extracted GUIMulePanel
    private GUIMulePanel guiMulePanel;

    // NEW PROFESSIONAL FEATURES PANELS
    private GUISessionGoalsPanel guiSessionGoalsPanel;
    private GUIBreaksPanel guiBreaksPanel;

    // Helper components
    private final List<JLabel> questionMarkLabels = new ArrayList<>();

    // Store the found custom item ID
    private int foundCustomItemId = -1;

    // ===========================================
    // ITEM CATEGORIES - F2P AND P2P
    // ===========================================

    // F2P High Alchemy Items (sorted by profit potential)
    private static final String[] F2P_ITEMS = {
            "Select F2P item...",
            "Rune longsword",
            "Rune battleaxe",
            "Rune 2h sword",
            "Rune platebody",
            "Rune platelegs",
            "Rune plateskirt",
            "Rune chainbody",
            "Rune med helm",
            "Rune full helm",
            "Rune sq shield",
            "Rune kiteshield",
            "Green d'hide body",
            "Green d'hide chaps",
            "Adamant platebody",
            "Adamant platelegs",
            "Custom F2P item..."
    };

    // P2P High Alchemy Items (sorted by profit potential)
    private static final String[] P2P_ITEMS = {
            "Select P2P item...",
            "Dragon longsword",
            "Dragon battleaxe",
            "Dragon dagger",
            "Dragon mace",
            "Dragon scimitar",
            "Dragon sword",
            "Black d'hide body",
            "Black d'hide chaps",
            "Red d'hide body",
            "Red d'hide chaps",
            "Blue d'hide body",
            "Blue d'hide chaps",
            "Rune crossbow",
            "Magic longbow",
            "Yew longbow",
            "Maple longbow",
            "Splitbark body",
            "Splitbark legs",
            "Custom P2P item..."
    };

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
        JScrollPane mainScrollPane = new JScrollPane(createMainPanel());
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JScrollPane settingsScrollPane = new JScrollPane(createSettingsPanel());
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
    // MAIN PANEL - CONFIGURATION TAB
    // ===========================================

    /**
     * Create main configuration panel with professional layout
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(createItemSelectionPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createTradingConfigPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createBotFeaturesPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createProfitPreviewPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createControlButtonsPanel());
        panel.add(Box.createVerticalStrut(15));
        panel.add(createStatusPanel());

        return panel;
    }

    /**
     * Create professional item selection panel
     */
    private JPanel createItemSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createProfessionalBorder("Item Selection"));

        // Category selection
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel categoryLabel = createStyledLabel("Category:");
        categoryPanel.add(categoryLabel);
        categoryPanel.add(createQuestionMark("Choose between F2P (free-to-play) or P2P (members) items"));

        String[] categories = {"F2P Items", "P2P Items"};
        itemCategoryCombo = new JComboBox<>(categories);
        itemCategoryCombo.setPreferredSize(new Dimension(120, 30));
        styleComboBox(itemCategoryCombo);
        categoryPanel.add(itemCategoryCombo);

        // Item selection
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel itemLabel = createStyledLabel("Item:");
        itemPanel.add(itemLabel);
        itemPanel.add(createQuestionMark("Select an item with good high alchemy profit potential"));

        itemDropdown = new JComboBox<>(F2P_ITEMS);
        itemDropdown.setPreferredSize(new Dimension(200, 30));
        styleComboBox(itemDropdown);
        itemPanel.add(itemDropdown);

        // Custom item field (initially hidden)
        customItemField = new JTextField(15);
        customItemField.setVisible(false);
        customItemField.setToolTipText("Enter custom item name or ID");
        styleTextField(customItemField);

        validateItemButton = new JButton("✓ Validate");
        validateItemButton.setVisible(false);
        validateItemButton.setPreferredSize(new Dimension(90, 30));
        styleButton(validateItemButton, new Color(76, 175, 80));

        JPanel customPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customPanel.add(customItemField);
        customPanel.add(validateItemButton);

        panel.add(categoryPanel, BorderLayout.NORTH);
        panel.add(itemPanel, BorderLayout.CENTER);
        panel.add(customPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create professional trading configuration panel
     */
    private JPanel createTradingConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createProfessionalBorder("Trading Configuration"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Buy Limit
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel buyLimitLabel = createStyledLabel("Buy Limit:");
        panel.add(buyLimitLabel, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Maximum number of items to buy from Grand Exchange per 4-hour period. Each item has a different buy limit (usually 100-1000). Exceeding this will prevent further purchases."), gbc);

        gbc.gridx = 2;
        buyLimitSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        buyLimitSpinner.setPreferredSize(new Dimension(100, 30));
        styleSpinner(buyLimitSpinner);
        panel.add(buyLimitSpinner, gbc);

        // Price Markup
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel markupLabel = createStyledLabel("Price Markup:");
        panel.add(markupLabel, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Percentage above current market price to offer. Higher markup = faster purchase but higher cost. 5-10% is usually optimal for quick buying without overpaying significantly."), gbc);

        gbc.gridx = 2;
        priceMarkupSpinner = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 50.0, 0.5));
        priceMarkupSpinner.setPreferredSize(new Dimension(100, 30));
        styleSpinner(priceMarkupSpinner);
        panel.add(priceMarkupSpinner, gbc);

        gbc.gridx = 3;
        JLabel percentLabel = createStyledLabel("%");
        panel.add(percentLabel, gbc);

        // Nature Runes
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel runesLabel = createStyledLabel("Nature Runes:");
        panel.add(runesLabel, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Number of nature runes to purchase for High Level Alchemy. Each alch requires 1 nature rune + fire staff/runes. 1000 runes = 1000 alchs. Buy in bulk for better prices."), gbc);

        gbc.gridx = 2;
        natureRuneSpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 10000, 100));
        natureRuneSpinner.setPreferredSize(new Dimension(100, 30));
        styleSpinner(natureRuneSpinner);
        panel.add(natureRuneSpinner, gbc);

        return panel;
    }

    /**
     * Create professional bot features panel
     */
    private JPanel createBotFeaturesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createProfessionalBorder("Bot Features & Configuration"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Smart Profit Analysis
        gbc.gridx = 0; gbc.gridy = 0;
        smartProfitCheck = new JCheckBox("Smart Profit Analysis");
        smartProfitCheck.setSelected(true);
        styleCheckBox(smartProfitCheck);
        panel.add(smartProfitCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Automatically calculates real-time profit margins using live market data before each alching session. Prevents losses from market fluctuations."), gbc);

        // World Hopping
        gbc.gridx = 0; gbc.gridy = 1;
        worldHopCheck = new JCheckBox("World Hopping");
        styleCheckBox(worldHopCheck);
        panel.add(worldHopCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Automatically changes worlds if Grand Exchange offers take too long to complete. Improves buying efficiency and reduces wait times."), gbc);

        // Alchemy Configuration
        gbc.gridx = 0; gbc.gridy = 2;
        alchConfigCheck = new JCheckBox("Auto-Configure High Alchemy");
        alchConfigCheck.setSelected(true);
        styleCheckBox(alchConfigCheck);
        panel.add(alchConfigCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Automatically right-clicks High Level Alchemy spell to configure the warning threshold above 10M GP. Prevents confirmation dialogs during alching."), gbc);

        // Skip Buying Toggle
        gbc.gridx = 0; gbc.gridy = 3;
        skipBuyingCheck = new JCheckBox("Items Already in Inventory");
        skipBuyingCheck.setToolTipText("Skip Grand Exchange buying if items are already in inventory");
        styleCheckBox(skipBuyingCheck);
        panel.add(skipBuyingCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Enable this if you already have items in your inventory and want to skip the Grand Exchange buying phase. Bot will start alching immediately."), gbc);

        // Restock When Empty Toggle
        gbc.gridx = 0; gbc.gridy = 4;
        restockWhenEmptyCheck = new JCheckBox("Restock When Empty");
        restockWhenEmptyCheck.setEnabled(false); // Disabled by default
        restockWhenEmptyCheck.setToolTipText("When enabled, bot will buy more items when inventory runs out");
        styleCheckBox(restockWhenEmptyCheck);
        panel.add(restockWhenEmptyCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("When enabled, the bot will buy more items from GE when inventory runs out. When disabled, bot stops when all items are alched."), gbc);

        return panel;
    }

    /**
     * Create profit preview panel
     */
    private JPanel createProfitPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createProfessionalBorder("Profit Preview"));

        profitPreviewLabel = new JLabel("Select an item to see profit analysis", JLabel.CENTER);
        profitPreviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        profitPreviewLabel.setForeground(new Color(255, 215, 0));
        profitPreviewLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(profitPreviewLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create professional control buttons panel
     */
    private JPanel createControlButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        startButton = new JButton("Start High Alchemy Bot");
        startButton.setPreferredSize(new Dimension(180, 45));
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        styleButton(startButton, new Color(76, 175, 80));

        stopButton = new JButton("Stop Bot");
        stopButton.setPreferredSize(new Dimension(120, 45));
        stopButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stopButton.setEnabled(false);
        styleButton(stopButton, new Color(244, 67, 54));

        panel.add(startButton);
        panel.add(stopButton);

        return panel;
    }

    /**
     * Create professional status panel
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createProfessionalBorder("Bot Status"));

        statusLabel = new JLabel("Ready to start - Configure your settings above", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(statusLabel, BorderLayout.CENTER);

        return panel;
    }

    // ===========================================
    // SETTINGS PANEL - ADVANCED TAB
    // ===========================================

    /**
     * Create professional settings panel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(createAntibanSettingsPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createDiscordIntegrationPanel());

        return panel;
    }

    /**
     * Create professional anti-ban settings panel
     */
    private JPanel createAntibanSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(createProfessionalBorder("Advanced Anti-ban Protection System"));

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Enable anti-ban
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        enableAntibanCheck = new JCheckBox("Enable Advanced Anti-ban System");
        enableAntibanCheck.setSelected(true);
        styleCheckBox(enableAntibanCheck);
        settingsPanel.add(enableAntibanCheck, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        settingsPanel.add(createQuestionMark("Enables comprehensive human-like behavior patterns including camera movements, tab checking, mouse variations, break scheduling, and fatigue-based adjustments."), gbc);

        // Profile-based seeding
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        profileSeedingCheck = new JCheckBox("User Profile-Based Anti-ban Seeding");
        profileSeedingCheck.setSelected(true);
        styleCheckBox(profileSeedingCheck);
        settingsPanel.add(profileSeedingCheck, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        settingsPanel.add(createQuestionMark("Personalizes anti-ban patterns based on your unique user profile. Creates realistic behavior patterns specific to your account characteristics and playstyle."), gbc);

        // User profile field
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel profileLabel = createStyledLabel("Profile Seed:");
        settingsPanel.add(profileLabel, gbc);

        gbc.gridx = 1;
        userProfileField = new JTextField(12);
        userProfileField.setToolTipText("Your unique profile identifier for personalized anti-ban");
        styleTextField(userProfileField);
        settingsPanel.add(userProfileField, gbc);

        gbc.gridx = 2;
        generateSeedButton = new JButton("Generate");
        generateSeedButton.setPreferredSize(new Dimension(90, 25));
        styleButton(generateSeedButton, new Color(156, 39, 176));
        settingsPanel.add(generateSeedButton, gbc);

        gbc.gridx = 3;
        settingsPanel.add(createQuestionMark("Generates a unique seed based on your username, combat level, and account age. This personalizes anti-ban behavior to match your specific account profile."), gbc);

        // Fatigue system
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        fatigueSystemCheck = new JCheckBox("Intelligent Fatigue System");
        fatigueSystemCheck.setSelected(true);
        styleCheckBox(fatigueSystemCheck);
        settingsPanel.add(fatigueSystemCheck, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        settingsPanel.add(createQuestionMark("Monitors session length and adjusts behavior patterns as fatigue increases. Longer sessions result in more human-like mistakes, slower reactions, and increased break frequency."), gbc);

        // Fatigue indicator
        gbc.gridx = 0; gbc.gridy = 4;
        fatigueLabel = createStyledLabel("Current Fatigue:");
        settingsPanel.add(fatigueLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        fatigueBar = new JProgressBar(0, 100);
        fatigueBar.setStringPainted(true);
        fatigueBar.setString("0% - Fresh & Alert");
        fatigueBar.setForeground(new Color(76, 175, 80));
        settingsPanel.add(fatigueBar, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        settingsPanel.add(createQuestionMark("Real-time fatigue level. As this increases, the bot becomes more human-like with slower reactions and more mistakes. Resets after breaks."), gbc);

        // Aggression slider
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel aggressionLabel = createStyledLabel("Behavior Aggression:");
        settingsPanel.add(aggressionLabel, gbc);

        gbc.gridx = 1;
        settingsPanel.add(createQuestionMark("1 = Very human-like (slower, safer, more realistic) | 10 = Fast but riskier (fewer human behaviors, faster execution)"), gbc);

        gbc.gridx = 2;
        aggressionValueLabel = createStyledLabel("Level: 5 (Balanced)");
        aggressionValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        aggressionValueLabel.setForeground(new Color(255, 215, 0));
        settingsPanel.add(aggressionValueLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        antibanAggressionSlider = new JSlider(1, 10, 5);
        antibanAggressionSlider.setMajorTickSpacing(3);
        antibanAggressionSlider.setMinorTickSpacing(1);
        antibanAggressionSlider.setPaintTicks(true);
        antibanAggressionSlider.setPaintLabels(true);
        settingsPanel.add(antibanAggressionSlider, gbc);

        // Break system
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        breakSystemCheck = new JCheckBox("Intelligent Break System");
        breakSystemCheck.setSelected(true);
        styleCheckBox(breakSystemCheck);
        settingsPanel.add(breakSystemCheck, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        settingsPanel.add(createQuestionMark("Takes random breaks based on session length, fatigue level, and personalized patterns. Break frequency increases with fatigue and user profile preferences."), gbc);

        // Break timing
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel minBreakLabel = createStyledLabel("Break Range:");
        settingsPanel.add(minBreakLabel, gbc);

        gbc.gridx = 1;
        JPanel breakRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        minBreakSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        minBreakSpinner.setPreferredSize(new Dimension(60, 25));
        styleSpinner(minBreakSpinner);
        breakRangePanel.add(minBreakSpinner);

        breakRangePanel.add(createStyledLabel(" to "));

        maxBreakSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 60, 1));
        maxBreakSpinner.setPreferredSize(new Dimension(60, 25));
        styleSpinner(maxBreakSpinner);
        breakRangePanel.add(maxBreakSpinner);

        breakRangePanel.add(createStyledLabel(" minutes"));

        settingsPanel.add(breakRangePanel, gbc);

        gbc.gridx = 2;
        settingsPanel.add(createQuestionMark("Random break duration range. Actual break time is influenced by current fatigue level and user profile patterns. Higher fatigue = longer breaks."), gbc);

        panel.add(settingsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create professional Discord integration panel
     */
    private JPanel createDiscordIntegrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(createProfessionalBorder("Discord Integration"));

        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        enableDiscordCheck = new JCheckBox("Enable Discord Notifications");
        styleCheckBox(enableDiscordCheck);
        enablePanel.add(enableDiscordCheck);
        enablePanel.add(createQuestionMark("Receive bot status updates and alerts in your Discord channel"));

        JPanel webhookPanel = new JPanel(new BorderLayout(5, 5));

        JPanel webhookLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel webhookLabel = createStyledLabel("Webhook URL:");
        webhookLabelPanel.add(webhookLabel);
        webhookLabelPanel.add(createQuestionMark("Discord webhook URL for sending notifications"));

        webhookField = new JTextField();
        webhookField.setToolTipText("https://discord.com/api/webhooks/YOUR_WEBHOOK_URL");
        styleTextField(webhookField);

        testWebhookButton = new JButton("Test");
        testWebhookButton.setPreferredSize(new Dimension(80, 30));
        styleButton(testWebhookButton, new Color(114, 137, 218));

        JPanel webhookFieldPanel = new JPanel(new BorderLayout());
        webhookFieldPanel.add(webhookField, BorderLayout.CENTER);
        webhookFieldPanel.add(testWebhookButton, BorderLayout.EAST);

        webhookPanel.add(webhookLabelPanel, BorderLayout.NORTH);
        webhookPanel.add(webhookFieldPanel, BorderLayout.CENTER);

        panel.add(enablePanel, BorderLayout.NORTH);
        panel.add(webhookPanel, BorderLayout.CENTER);

        return panel;
    }

    // ===========================================
    // DATA PANEL - ANALYTICS TAB
    // ===========================================

    // ===========================================
    // STYLING METHODS - SUBSTANCE COMPATIBLE
    // ===========================================

    private TitledBorder createProfessionalBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(new Color(64, 128, 255));
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        border.setBorder(BorderFactory.createLineBorder(new Color(64, 128, 255), 1));
        return border;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.PLAIN, 13));
        return label;
    }

    private JLabel createQuestionMark(String tooltipText) {
        final JLabel questionMark = new JLabel("[?]");
        questionMark.setFont(new Font("Inter", Font.BOLD, 12));
        questionMark.setForeground(new Color(96, 165, 250));
        questionMark.setToolTipText("<html><div style='width: 300px; font-family: Inter;'>" + tooltipText + "</div></html>");
        questionMark.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        questionMark.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        questionMark.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                source.setForeground(new Color(147, 197, 253));
                source.setText("【?】");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                source.setForeground(new Color(96, 165, 250));
                source.setText("[?]");
            }
        });

        questionMarkLabels.add(questionMark);
        return questionMark;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkBox.setFocusPainted(false);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Inter", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 16, 8, 16),
                BorderFactory.createLineBorder(color.darker(), 1, true)
        ));

        button.putClientProperty("originalColor", color);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                Color originalColor = (Color) sourceButton.getClientProperty("originalColor");
                if (originalColor != null) {
                    Color hoverColor = new Color(
                            Math.min(255, originalColor.getRed() + 20),
                            Math.min(255, originalColor.getGreen() + 20),
                            Math.min(255, originalColor.getBlue() + 20)
                    );
                    sourceButton.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                Color originalColor = (Color) sourceButton.getClientProperty("originalColor");
                if (originalColor != null) {
                    sourceButton.setBackground(originalColor);
                }
            }
        });
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
            if (buyLimitSpinner != null && buyLimitSpinner.getParent() != null) {
                Component parent = buyLimitSpinner.getParent();
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
        itemCategoryCombo.addActionListener(e -> {
            String selected = (String) itemCategoryCombo.getSelectedItem();
            if ("F2P Items".equals(selected)) {
                itemDropdown.setModel(new DefaultComboBoxModel<>(F2P_ITEMS));
            } else {
                itemDropdown.setModel(new DefaultComboBoxModel<>(P2P_ITEMS));
            }
            itemDropdown.setSelectedIndex(0);
            updateProfitPreview();
        });

        // Item dropdown change handler
        itemDropdown.addActionListener(e -> {
            String selected = (String) itemDropdown.getSelectedItem();
            boolean isCustom = selected != null && selected.contains("Custom");

            customItemField.setVisible(isCustom);
            validateItemButton.setVisible(isCustom);

            frame.revalidate();
            frame.repaint();

            updateProfitPreview();

            if (eventListener != null && selected != null && !selected.startsWith("Select")) {
                eventListener.onItemSelected(selected);
            }
        });

        // Custom item validation
        validateItemButton.addActionListener(e -> {
            String customItem = customItemField.getText().trim();
            if (customItem.isEmpty()) {
                updateStatus("❌ Please enter an item name to validate");
                return;
            }

            validateItemButton.setEnabled(false);
            validateItemButton.setText("⏳");

            new Thread(() -> {
                try {
                    boolean isValid = validateItemOnline(customItem);

                    SwingUtilities.invokeLater(() -> {
                        if (isValid) {
                            updateStatus("✅ Item '" + customItem + "' validated successfully!");
                            customItemField.setBackground(new Color(34, 197, 94, 50));
                            if (eventListener != null) {
                                eventListener.onItemSelected(customItem);
                            }
                            updateProfitPreview();
                        } else {
                            updateStatus("❌ Item '" + customItem + "' not found in OSRS database");
                            customItemField.setBackground(new Color(239, 68, 68, 50));
                        }

                        validateItemButton.setEnabled(true);
                        validateItemButton.setText("✓ Validate");
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        updateStatus("❌ Error validating item: " + ex.getMessage());
                        customItemField.setBackground(new Color(239, 68, 68, 50));
                        validateItemButton.setEnabled(true);
                        validateItemButton.setText("✓ Validate");
                    });
                }
            }).start();
        });

        // FIXED: Skip buying checkbox handler with panel toggling
        skipBuyingCheck.addActionListener(e -> {
            boolean skipBuyingEnabled = skipBuyingCheck.isSelected();

            // Enable/disable restock checkbox
            if (restockWhenEmptyCheck != null) {
                restockWhenEmptyCheck.setEnabled(skipBuyingEnabled);
                if (!skipBuyingEnabled) {
                    restockWhenEmptyCheck.setSelected(false);
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
        antibanAggressionSlider.addChangeListener(e -> {
            int value = antibanAggressionSlider.getValue();
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
            aggressionValueLabel.setText("Level: " + value + " (" + description + ")");
            aggressionValueLabel.setForeground(color);
        });

        // Right-click alchemy configuration
        alchConfigCheck.addActionListener(e -> {
            boolean enabled = alchConfigCheck.isSelected();
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
        generateSeedButton.addActionListener(e -> {
            generateSeedButton.setEnabled(false);
            generateSeedButton.setText("...");

            new Thread(() -> {
                try {
                    Thread.sleep(1000);

                    SwingUtilities.invokeLater(() -> {
                        String seed = generateUniqueProfileSeed();
                        userProfileField.setText(seed);
                        updateStatus("Generated personalized anti-ban seed: " + seed);

                        generateSeedButton.setEnabled(true);
                        generateSeedButton.setText("Generate");
                    });

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });

        // Start/Stop button handlers
        startButton.addActionListener(e -> {
            if (eventListener != null) {
                GUIConfiguration config = getCurrentConfiguration();
                eventListener.onStartBot(config);
                updateButtonStates(true);
            }
        });

        stopButton.addActionListener(e -> {
            if (eventListener != null) {
                eventListener.onStopBot();
                updateButtonStates(false);
            }
        });

        // Test webhook button
        testWebhookButton.addActionListener(e -> testWebhookWithFeedback());

        // Refresh data button (handled by GUIDataPanel now)
        // refreshDataButton is created and handled within GUIDataPanel

        // Configuration change handlers
        buyLimitSpinner.addChangeListener(e -> {
            updateProfitPreview();
            if (eventListener != null) eventListener.onConfigurationChanged();
        });

        priceMarkupSpinner.addChangeListener(e -> {
            updateProfitPreview();
            if (eventListener != null) eventListener.onConfigurationChanged();
        });
    }

    /**
     * Test webhook with professional feedback
     */
    private void testWebhookWithFeedback() {
        String webhookUrl = webhookField.getText().trim();

        if (webhookUrl.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter your Discord webhook URL first!\n\nTo create a webhook:\n1. Go to your Discord server\n2. Edit channel → Integrations → Webhooks\n3. Create webhook and copy URL",
                    "Discord Webhook Required",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        testWebhookButton.setEnabled(false);
        testWebhookButton.setText("Testing...");

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

                        testWebhookButton.setEnabled(true);
                        testWebhookButton.setText("Test");
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Error testing webhook:\n" + ex.getMessage(),
                            "Webhook Error",
                            JOptionPane.ERROR_MESSAGE);

                    testWebhookButton.setEnabled(true);
                    testWebhookButton.setText("Test");
                });
            }
        }).start();
    }

    /**
     * Update profit preview with LIVE API data
     */
    private void updateProfitPreview() {
        try {
            String selectedItem = (String) itemDropdown.getSelectedItem();

            if (selectedItem == null || selectedItem.startsWith("Select")) {
                profitPreviewLabel.setText("Select an item to see real profit analysis");
                profitPreviewLabel.setForeground(new Color(156, 163, 175));
                return;
            }

            if (selectedItem.contains("Custom")) {
                String customItem = customItemField.getText().trim();
                if (!customItem.isEmpty() && foundCustomItemId > 0) {
                    selectedItem = customItem;
                } else {
                    profitPreviewLabel.setText("Enter and validate custom item for real-time analysis");
                    profitPreviewLabel.setForeground(new Color(251, 191, 36));
                    return;
                }
            }

            int buyLimit = (Integer) buyLimitSpinner.getValue();
            double markup = (Double) priceMarkupSpinner.getValue();

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

                profitPreviewLabel.setText(profitText);
                profitPreviewLabel.setForeground(profitColor);
            } else {
                profitPreviewLabel.setText("Unable to fetch live data for " + selectedItem);
                profitPreviewLabel.setForeground(new Color(239, 68, 68));
            }

        } catch (Exception e) {
            profitPreviewLabel.setText("Error calculating profit preview");
            profitPreviewLabel.setForeground(new Color(239, 68, 68));
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
        if (statusLabel != null) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(status);

                if (status.contains("✅") || status.contains("successful")) {
                    statusLabel.setForeground(new Color(76, 175, 80));
                } else if (status.contains("❌") || status.contains("failed") || status.contains("error")) {
                    statusLabel.setForeground(new Color(244, 67, 54));
                } else if (status.contains("🚀") || status.contains("running")) {
                    statusLabel.setForeground(new Color(64, 128, 255));
                }
            });
        }
    }

    /**
     * Update button states based on bot running status
     */
    public void updateButtonStates(boolean botRunning) {
        SwingUtilities.invokeLater(() -> {
            startButton.setEnabled(!botRunning);
            stopButton.setEnabled(botRunning);

            if (botRunning) {
                updateStatus("High Alchemy Bot is running - Monitor progress in overlay");
                startButton.setText("Bot Running...");
                frame.setVisible(false);
            } else {
                updateStatus("Bot stopped - Ready to start new session");
                startButton.setText("Start High Alchemy Bot");
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
        String selectedItem = (String) itemDropdown.getSelectedItem();
        if (selectedItem != null && selectedItem.contains("Custom")) {
            config.selectedItemName = customItemField.getText().trim();
            config.selectedItemId = foundCustomItemId;
        } else {
            config.selectedItemName = selectedItem;
            config.selectedItemId = getItemIdFromName(config.selectedItemName);
        }

        // Get trading configuration
        config.buyLimit = (Integer) buyLimitSpinner.getValue();
        config.priceMarkup = (Double) priceMarkupSpinner.getValue();
        config.natureRuneAmount = (Integer) natureRuneSpinner.getValue();

        // Get bot features
        config.smartProfitEnabled = smartProfitCheck.isSelected();
        config.worldHopEnabled = worldHopCheck.isSelected();
        config.alchConfigEnabled = alchConfigCheck.isSelected();
        config.skipBuying = skipBuyingCheck.isSelected();
        config.restockWhenEmpty = restockWhenEmptyCheck != null ? restockWhenEmptyCheck.isSelected() : false;

        // Get Discord settings
        config.discordWebhookUrl = webhookField.getText().trim();
        config.discordNotificationsEnabled = enableDiscordCheck.isSelected();

        // Get anti-ban settings
        config.antibanEnabled = enableAntibanCheck.isSelected();
        config.antibanAggression = antibanAggressionSlider.getValue();
        config.breakSystemEnabled = breakSystemCheck.isSelected();
        config.minBreakMinutes = (Integer) minBreakSpinner.getValue();
        config.maxBreakMinutes = (Integer) maxBreakSpinner.getValue();
        config.fatigueSystemEnabled = fatigueSystemCheck.isSelected();
        config.profileSeedingEnabled = profileSeedingCheck.isSelected();
        config.userProfileSeed = userProfileField.getText().trim();

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
                for (int i = 0; i < itemDropdown.getItemCount(); i++) {
                    if (config.selectedItemName.equals(itemDropdown.getItemAt(i))) {
                        itemDropdown.setSelectedIndex(i);
                        foundInDropdown = true;
                        break;
                    }
                }

                // If not found, check other category
                if (!foundInDropdown) {
                    String currentCategory = (String) itemCategoryCombo.getSelectedItem();
                    String otherCategory = "F2P Items".equals(currentCategory) ? "P2P Items" : "F2P Items";
                    itemCategoryCombo.setSelectedItem(otherCategory);

                    for (int i = 0; i < itemDropdown.getItemCount(); i++) {
                        if (config.selectedItemName.equals(itemDropdown.getItemAt(i))) {
                            itemDropdown.setSelectedIndex(i);
                            foundInDropdown = true;
                            break;
                        }
                    }
                }

                // If still not found, use custom
                if (!foundInDropdown) {
                    itemDropdown.setSelectedItem(itemDropdown.getItemAt(itemDropdown.getItemCount() - 1));
                    customItemField.setText(config.selectedItemName);
                    customItemField.setVisible(true);
                    validateItemButton.setVisible(true);
                }
            }

            // Set trading configuration
            buyLimitSpinner.setValue(config.buyLimit);
            priceMarkupSpinner.setValue(config.priceMarkup);
            natureRuneSpinner.setValue(config.natureRuneAmount);

            // Set bot features
            smartProfitCheck.setSelected(config.smartProfitEnabled);
            worldHopCheck.setSelected(config.worldHopEnabled);
            alchConfigCheck.setSelected(config.alchConfigEnabled);
            skipBuyingCheck.setSelected(config.skipBuying);

            // Set Discord settings
            if (config.discordWebhookUrl != null) {
                webhookField.setText(config.discordWebhookUrl);
            }
            enableDiscordCheck.setSelected(config.discordNotificationsEnabled);

            // Set anti-ban settings
            enableAntibanCheck.setSelected(config.antibanEnabled);
            antibanAggressionSlider.setValue(config.antibanAggression);
            breakSystemCheck.setSelected(config.breakSystemEnabled);
            minBreakSpinner.setValue(config.minBreakMinutes);
            maxBreakSpinner.setValue(config.maxBreakMinutes);
            fatigueSystemCheck.setSelected(config.fatigueSystemEnabled);
            profileSeedingCheck.setSelected(config.profileSeedingEnabled);
            if (config.userProfileSeed != null) {
                userProfileField.setText(config.userProfileSeed);
            }

            updateProfitPreview();
        });
    }

    /**
     * Refresh the data table
     */
    public void refreshDataTable() {
        refreshDataTablePage(1);
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
        SwingUtilities.invokeLater(() -> {
            int adjustedFatigue = Math.max(0, Math.min(100, fatiguePercent));
            fatigueBar.setValue(adjustedFatigue);

            String status;
            Color color;

            if (adjustedFatigue <= 20) {
                status = "Fresh & Alert";
                color = new Color(34, 197, 94);
            } else if (adjustedFatigue <= 50) {
                status = "Slightly Tired";
                color = new Color(251, 191, 36);
            } else if (adjustedFatigue <= 80) {
                status = "Getting Tired";
                color = new Color(249, 115, 22);
            } else {
                status = "Very Fatigued";
                color = new Color(239, 68, 68);
            }

            fatigueBar.setString(adjustedFatigue + "% - " + status);
            fatigueBar.setForeground(color);

            fatigueLabel.setText("Current Fatigue: " + status);
            fatigueLabel.setForeground(color);
        });
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
        return alchConfigCheck != null && alchConfigCheck.isSelected();
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

                customItemField.setText(result.itemName);
                foundCustomItemId = result.itemId;

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
                    buyLimitSpinner.setValue(result.buyLimit);
                }

                updateStatus(status.toString());
                updateProfitPreview();

                return true;
            } else {
                BotUtils.log("❌ Item not found in OSRS database: " + cleanName);
                foundCustomItemId = -1;
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

            questionMarkLabels.clear();

            BotUtils.log("✅ GUI resources cleaned up successfully");

        } catch (Exception e) {
            BotUtils.logError("Error disposing GUI resources", e);
        }
    }
}