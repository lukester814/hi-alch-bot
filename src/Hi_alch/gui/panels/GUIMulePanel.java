package Hi_alch.gui.panels;

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
        // Initialize components
        enableMuleCheck = new JCheckBox("Enable Mule Support");
        muleUsernameField = new JTextField(15);
        String[] locations = {
            "Grand Exchange",
            "Lumbridge",
            "Varrock West Bank",
            "Edgeville",
            "Falador Park"
        };
        locationCombo = new JComboBox<>(locations);
        worldSpinner = new JSpinner(new SpinnerNumberModel(301, 301, 580, 1));

        return MulePanelBuilder.createBasicConfigPanel(
            enableMuleCheck, muleUsernameField, locationCombo, worldSpinner
        );
    }

    /**
     * Create auto-transfer settings panel
     */
    private JPanel createAutoTransferPanel() {
        // Initialize components
        autoProfitCheck = new JCheckBox("Auto-transfer profit when reaching:");
        profitThresholdSpinner = new JSpinner(new SpinnerNumberModel(100000, 10000, 10000000, 10000));
        autoItemsCheck = new JCheckBox("Auto-transfer items when reaching:");
        itemThresholdSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 500, 10));

        return MulePanelBuilder.createAutoTransferPanel(
            autoProfitCheck, profitThresholdSpinner, autoItemsCheck, itemThresholdSpinner
        );
    }

    /**
     * Create actions panel
     */
    private JPanel createActionsPanel() {
        // Initialize components
        testConnectionButton = new JButton("Test Connection");
        manualTransferButton = new JButton("Manual Transfer");
        saveSettingsButton = new JButton("Save Settings");

        return MulePanelBuilder.createActionsPanel(
            testConnectionButton, manualTransferButton, saveSettingsButton
        );
    }

    /**
     * Create status panel
     */
    private JPanel createStatusPanel() {
        // Initialize components
        statusLabel = new JLabel("Idle - Not configured");
        lastTransferLabel = new JLabel("Never");
        transferProgressBar = new JProgressBar();

        return MulePanelBuilder.createStatusPanel(
            statusLabel, lastTransferLabel, transferProgressBar
        );
    }

    /**
     * Create statistics panel
     */
    private JPanel createStatisticsPanel() {
        // Initialize components
        totalGPLabel = new JLabel("0 GP");
        totalItemsLabel = new JLabel("0 items");
        transferCountLabel = new JLabel("0 transfers");

        return MulePanelBuilder.createStatisticsPanel(
            totalGPLabel, totalItemsLabel, transferCountLabel
        );
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
