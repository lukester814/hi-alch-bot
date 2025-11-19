package Hi_alch;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

// Import styling utilities
import static Hi_alch.GUIStyles.*;

/**
 * Professional Mule Configuration Panel
 *
 * Features:
 * - Mule username configuration
 * - Transfer location selection
 * - World specification
 * - Auto-transfer settings (profit/items)
 * - Threshold configuration
 * - Manual transfer button
 * - Real-time status display
 * - Statistics tracking
 * - Test connection button
 *
 * REUSABLE: Perfect for any OSRS bot that needs mule support!
 */
public class GUIMulePanel {

    // ===========================================
    // COMPONENTS
    // ===========================================

    private JPanel mainPanel;

    // Configuration components
    private JCheckBox enableMuleCheck;
    private JTextField muleUsernameField;
    private JComboBox<String> locationCombo;
    private JSpinner worldSpinner;

    // Auto-transfer settings
    private JCheckBox autoProfitCheck;
    private JSpinner profitThresholdSpinner;
    private JCheckBox autoItemsCheck;
    private JSpinner itemThresholdSpinner;

    // Action buttons
    private JButton testConnectionButton;
    private JButton manualTransferButton;
    private JButton saveSettingsButton;

    // Status display
    private JLabel statusLabel;
    private JLabel lastTransferLabel;
    private JProgressBar transferProgressBar;

    // Statistics display
    private JLabel totalGPLabel;
    private JLabel totalItemsLabel;
    private JLabel transferCountLabel;

    // Event listener
    private MuleEventListener eventListener;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public GUIMulePanel() {
        createPanel();
    }

    // ===========================================
    // PANEL CREATION
    // ===========================================

    /**
     * Create the main mule configuration panel
     */
    private void createPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("🤝 Mule Support Configuration");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        JLabel subtitleLabel = new JLabel("Configure automated trading with your mule account");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(INFO_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Basic configuration panel
        mainPanel.add(createBasicConfigPanel());
        mainPanel.add(Box.createVerticalStrut(15));

        // Auto-transfer settings panel
        mainPanel.add(createAutoTransferPanel());
        mainPanel.add(Box.createVerticalStrut(15));

        // Action buttons panel
        mainPanel.add(createActionsPanel());
        mainPanel.add(Box.createVerticalStrut(15));

        // Status and statistics panel
        mainPanel.add(createStatusPanel());
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(createStatisticsPanel());

        // Add glue to push everything to the top
        mainPanel.add(Box.createVerticalGlue());
    }

    /**
     * Create basic configuration panel
     */
    private JPanel createBasicConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createProfessionalBorder("Basic Configuration"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Enable checkbox
        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enableMuleCheck = new JCheckBox("Enable Mule Support");
        styleCheckBox(enableMuleCheck);
        enableMuleCheck.setFont(new Font("Inter", Font.BOLD, 13));
        enablePanel.add(enableMuleCheck);
        enablePanel.add(createQuestionMark(
            "Enable automated trading with your mule account. " +
            "Make sure your mule is logged in and at the specified location."
        ));
        panel.add(enablePanel);

        // Mule username
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.add(createStyledLabel("Mule Username:"));
        muleUsernameField = new JTextField(15);
        styleTextField(muleUsernameField);
        muleUsernameField.setToolTipText("Enter the exact username of your mule account");
        usernamePanel.add(muleUsernameField);
        usernamePanel.add(createQuestionMark(
            "The exact in-game name of your mule account. " +
            "Case-insensitive, but must match exactly."
        ));
        panel.add(usernamePanel);

        // Transfer location
        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationPanel.add(createStyledLabel("Transfer Location:"));
        String[] locations = {
            "Grand Exchange",
            "Lumbridge",
            "Varrock West Bank",
            "Edgeville",
            "Falador Park"
        };
        locationCombo = new JComboBox<>(locations);
        styleComboBox(locationCombo);
        locationCombo.setPreferredSize(new Dimension(200, 25));
        locationPanel.add(locationCombo);
        locationPanel.add(createQuestionMark(
            "Where to meet your mule for trading. " +
            "Make sure your mule is at this location!"
        ));
        panel.add(locationPanel);

        // Transfer world
        JPanel worldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        worldPanel.add(createStyledLabel("Transfer World:"));
        worldSpinner = new JSpinner(new SpinnerNumberModel(301, 301, 580, 1));
        styleSpinner(worldSpinner);
        worldSpinner.setPreferredSize(new Dimension(80, 25));
        worldSpinner.setToolTipText("World to hop to for mule transfers");
        worldPanel.add(worldSpinner);
        worldPanel.add(createQuestionMark(
            "The bot will hop to this world before trading. " +
            "Your mule should be on this world."
        ));
        panel.add(worldPanel);

        return panel;
    }

    /**
     * Create auto-transfer settings panel
     */
    private JPanel createAutoTransferPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createProfessionalBorder("Auto-Transfer Settings"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Auto-transfer profit
        JPanel profitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        autoProfitCheck = new JCheckBox("Auto-transfer profit when reaching:");
        styleCheckBox(autoProfitCheck);
        profitPanel.add(autoProfitCheck);
        profitThresholdSpinner = new JSpinner(new SpinnerNumberModel(100000, 10000, 10000000, 10000));
        styleSpinner(profitThresholdSpinner);
        profitThresholdSpinner.setPreferredSize(new Dimension(120, 25));
        profitPanel.add(profitThresholdSpinner);
        profitPanel.add(createStyledLabel("GP"));
        profitPanel.add(createQuestionMark(
            "Automatically transfer GP to mule when your profit reaches this amount. " +
            "Recommended: 100k-500k GP to minimize transfer frequency."
        ));
        panel.add(profitPanel);

        // Auto-transfer items
        JPanel itemsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        autoItemsCheck = new JCheckBox("Auto-transfer items when reaching:");
        styleCheckBox(autoItemsCheck);
        itemsPanel.add(autoItemsCheck);
        itemThresholdSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 500, 10));
        styleSpinner(itemThresholdSpinner);
        itemThresholdSpinner.setPreferredSize(new Dimension(80, 25));
        itemsPanel.add(itemThresholdSpinner);
        itemsPanel.add(createStyledLabel("items"));
        itemsPanel.add(createQuestionMark(
            "Automatically transfer items to mule when inventory reaches this count. " +
            "Useful for transferring alchable items to mule for safekeeping."
        ));
        panel.add(itemsPanel);

        // Warning label
        JLabel warningLabel = new JLabel("⚠️ Transfers have 5-minute cooldown for safety");
        warningLabel.setFont(SMALL_FONT);
        warningLabel.setForeground(WARNING_COLOR);
        JPanel warningPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        warningPanel.add(warningLabel);
        panel.add(warningPanel);

        return panel;
    }

    /**
     * Create actions panel
     */
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createProfessionalBorder("Actions"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // Test connection button
        testConnectionButton = new JButton("Test Connection");
        testConnectionButton.setPreferredSize(new Dimension(150, 35));
        styleButton(testConnectionButton, INFO_COLOR);
        testConnectionButton.setToolTipText("Verify mule is online and at location");
        buttonsPanel.add(testConnectionButton);

        // Manual transfer button
        manualTransferButton = new JButton("Manual Transfer");
        manualTransferButton.setPreferredSize(new Dimension(150, 35));
        styleButton(manualTransferButton, WARNING_COLOR);
        manualTransferButton.setToolTipText("Manually initiate transfer now");
        manualTransferButton.setEnabled(false);
        buttonsPanel.add(manualTransferButton);

        // Save settings button
        saveSettingsButton = new JButton("Save Settings");
        saveSettingsButton.setPreferredSize(new Dimension(150, 35));
        styleButton(saveSettingsButton, SUCCESS_COLOR);
        saveSettingsButton.setToolTipText("Save mule configuration");
        buttonsPanel.add(saveSettingsButton);

        panel.add(buttonsPanel);

        return panel;
    }

    /**
     * Create status panel
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createProfessionalBorder("Status"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Status label
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(createStyledLabel("Current Status:"));
        statusLabel = new JLabel("Idle - Not configured");
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(TEXT_SECONDARY);
        statusPanel.add(statusLabel);
        panel.add(statusPanel);

        // Last transfer label
        JPanel lastTransferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lastTransferPanel.add(createStyledLabel("Last Transfer:"));
        lastTransferLabel = new JLabel("Never");
        lastTransferLabel.setFont(LABEL_FONT);
        lastTransferPanel.add(lastTransferLabel);
        panel.add(lastTransferPanel);

        // Progress bar
        transferProgressBar = new JProgressBar();
        transferProgressBar.setStringPainted(true);
        transferProgressBar.setString("Ready");
        transferProgressBar.setForeground(SUCCESS_COLOR);
        transferProgressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        transferProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(Box.createVerticalStrut(5));
        panel.add(transferProgressBar);

        return panel;
    }

    /**
     * Create statistics panel
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createProfessionalBorder("Transfer Statistics"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Total GP transferred
        JPanel gpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gpPanel.add(createStyledLabel("Total GP Transferred:"));
        totalGPLabel = new JLabel("0 GP");
        totalGPLabel.setFont(VALUE_FONT);
        totalGPLabel.setForeground(PROFIT_COLOR);
        gpPanel.add(totalGPLabel);
        panel.add(gpPanel);

        // Total items transferred
        JPanel itemsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemsPanel.add(createStyledLabel("Total Items Transferred:"));
        totalItemsLabel = new JLabel("0 items");
        totalItemsLabel.setFont(VALUE_FONT);
        totalItemsLabel.setForeground(ACCENT_COLOR);
        itemsPanel.add(totalItemsLabel);
        panel.add(itemsPanel);

        // Transfer count
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countPanel.add(createStyledLabel("Transfer Count:"));
        transferCountLabel = new JLabel("0 transfers");
        transferCountLabel.setFont(VALUE_FONT);
        transferCountLabel.setForeground(INFO_COLOR);
        countPanel.add(transferCountLabel);
        panel.add(countPanel);

        return panel;
    }

    // ===========================================
    // EVENT HANDLING
    // ===========================================

    /**
     * Setup event handlers
     */
    public void setupEventHandlers() {
        enableMuleCheck.addActionListener(e -> {
            boolean enabled = enableMuleCheck.isSelected();
            setFieldsEnabled(enabled);

            if (eventListener != null) {
                eventListener.onMuleEnabledChanged(enabled);
            }
        });

        testConnectionButton.addActionListener(e -> {
            if (eventListener != null) {
                eventListener.onTestConnection();
            }
        });

        manualTransferButton.addActionListener(e -> {
            if (eventListener != null) {
                eventListener.onManualTransfer();
            }
        });

        saveSettingsButton.addActionListener(e -> {
            if (eventListener != null) {
                eventListener.onSaveSettings();
            }
        });

        // Enable manual transfer button when mule is enabled
        enableMuleCheck.addActionListener(e -> {
            manualTransferButton.setEnabled(enableMuleCheck.isSelected());
        });
    }

    /**
     * Enable/disable configuration fields
     */
    private void setFieldsEnabled(boolean enabled) {
        muleUsernameField.setEnabled(enabled);
        locationCombo.setEnabled(enabled);
        worldSpinner.setEnabled(enabled);
        autoProfitCheck.setEnabled(enabled);
        profitThresholdSpinner.setEnabled(enabled);
        autoItemsCheck.setEnabled(enabled);
        itemThresholdSpinner.setEnabled(enabled);
        testConnectionButton.setEnabled(enabled);
        manualTransferButton.setEnabled(enabled);
    }

    // ===========================================
    // PUBLIC INTERFACE
    // ===========================================

    /**
     * Get the main panel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * Set event listener
     */
    public void setEventListener(MuleEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Get mule configuration from GUI
     */
    public MuleConfiguration getConfiguration() {
        MuleConfiguration config = new MuleConfiguration();
        config.enabled = enableMuleCheck.isSelected();
        config.muleUsername = muleUsernameField.getText().trim();
        config.location = (String) locationCombo.getSelectedItem();
        config.world = (Integer) worldSpinner.getValue();
        config.autoTransferProfit = autoProfitCheck.isSelected();
        config.profitThreshold = (Integer) profitThresholdSpinner.getValue();
        config.autoTransferItems = autoItemsCheck.isSelected();
        config.itemThreshold = (Integer) itemThresholdSpinner.getValue();
        return config;
    }

    /**
     * Apply configuration to GUI
     */
    public void applyConfiguration(MuleConfiguration config) {
        enableMuleCheck.setSelected(config.enabled);
        muleUsernameField.setText(config.muleUsername);
        locationCombo.setSelectedItem(config.location);
        worldSpinner.setValue(config.world);
        autoProfitCheck.setSelected(config.autoTransferProfit);
        profitThresholdSpinner.setValue(config.profitThreshold);
        autoItemsCheck.setSelected(config.autoTransferItems);
        itemThresholdSpinner.setValue(config.itemThreshold);
        setFieldsEnabled(config.enabled);
    }

    /**
     * Update status display
     */
    public void updateStatus(String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            statusLabel.setForeground(color);
        });
    }

    /**
     * Update progress bar
     */
    public void updateProgress(int value, String message) {
        SwingUtilities.invokeLater(() -> {
            transferProgressBar.setValue(value);
            transferProgressBar.setString(message);
        });
    }

    /**
     * Update statistics display
     */
    public void updateStatistics(int totalGP, int totalItems, int transferCount, long lastTransferTime) {
        SwingUtilities.invokeLater(() -> {
            totalGPLabel.setText(BotUtils.formatNumber(totalGP) + " GP");
            totalItemsLabel.setText(BotUtils.formatNumber(totalItems) + " items");
            transferCountLabel.setText(transferCount + " transfers");

            if (lastTransferTime > 0) {
                String timeAgo = BotUtils.formatDuration(System.currentTimeMillis() - lastTransferTime) + " ago";
                lastTransferLabel.setText(timeAgo);
            } else {
                lastTransferLabel.setText("Never");
            }
        });
    }

    // ===========================================
    // EVENT LISTENER INTERFACE
    // ===========================================

    /**
     * Event listener for mule panel actions
     */
    public interface MuleEventListener {
        void onMuleEnabledChanged(boolean enabled);
        void onTestConnection();
        void onManualTransfer();
        void onSaveSettings();
    }

    // ===========================================
    // CONFIGURATION CLASS
    // ===========================================

    /**
     * Mule configuration data class
     */
    public static class MuleConfiguration {
        public boolean enabled = false;
        public String muleUsername = "";
        public String location = "Grand Exchange";
        public int world = 301;
        public boolean autoTransferProfit = false;
        public int profitThreshold = 100000;
        public boolean autoTransferItems = false;
        public int itemThreshold = 50;
    }
}
