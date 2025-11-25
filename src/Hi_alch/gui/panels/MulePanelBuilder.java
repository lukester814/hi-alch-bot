package Hi_alch.gui.panels;

import Hi_alch.gui.GUIStyler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Import styling utilities
import static Hi_alch.gui.GUIStyles.*;

/**
 * Mule Panel Builder - Creates UI components for Mule Panel
 *
 * Handles creation of:
 * - Basic configuration panel
 * - Auto-transfer settings panel
 * - Actions panel
 * - Status panel
 * - Statistics panel
 */
public class MulePanelBuilder {

    private static final GUIStyler styler = new GUIStyler();

    /**
     * Create basic configuration panel
     */
    public static JPanel createBasicConfigPanel(
            JCheckBox enableMuleCheck,
            JTextField muleUsernameField,
            JComboBox<String> locationCombo,
            JSpinner worldSpinner) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(styler.createProfessionalBorder("Basic Configuration"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Enable checkbox
        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        styler.styleCheckBox(enableMuleCheck);
        enableMuleCheck.setFont(new Font("Inter", Font.BOLD, 13));
        enablePanel.add(enableMuleCheck);
        enablePanel.add(styler.createQuestionMark(
            "Enable automated trading with your mule account. " +
            "Make sure your mule is logged in and at the specified location."
        ));
        panel.add(enablePanel);

        // Mule username
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.add(styler.createStyledLabel("Mule Username:"));
        styler.styleTextField(muleUsernameField);
        muleUsernameField.setToolTipText("Enter the exact username of your mule account");
        usernamePanel.add(muleUsernameField);
        usernamePanel.add(styler.createQuestionMark(
            "The exact in-game name of your mule account. " +
            "Case-insensitive, but must match exactly."
        ));
        panel.add(usernamePanel);

        // Transfer location
        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationPanel.add(styler.createStyledLabel("Transfer Location:"));
        styler.styleComboBox(locationCombo);
        locationCombo.setPreferredSize(new Dimension(200, 25));
        locationPanel.add(locationCombo);
        locationPanel.add(styler.createQuestionMark(
            "Where to meet your mule for trading. " +
            "Make sure your mule is at this location!"
        ));
        panel.add(locationPanel);

        // Transfer world
        JPanel worldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        worldPanel.add(styler.createStyledLabel("Transfer World:"));
        styler.styleSpinner(worldSpinner);
        worldSpinner.setPreferredSize(new Dimension(80, 25));
        worldSpinner.setToolTipText("World to hop to for mule transfers");
        worldPanel.add(worldSpinner);
        worldPanel.add(styler.createQuestionMark(
            "The bot will hop to this world before trading. " +
            "Your mule should be on this world."
        ));
        panel.add(worldPanel);

        return panel;
    }

    /**
     * Create auto-transfer settings panel
     */
    public static JPanel createAutoTransferPanel(
            JCheckBox autoProfitCheck,
            JSpinner profitThresholdSpinner,
            JCheckBox autoItemsCheck,
            JSpinner itemThresholdSpinner) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(styler.createProfessionalBorder("Auto-Transfer Settings"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Auto-transfer profit
        JPanel profitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        styler.styleCheckBox(autoProfitCheck);
        profitPanel.add(autoProfitCheck);
        styler.styleSpinner(profitThresholdSpinner);
        profitThresholdSpinner.setPreferredSize(new Dimension(120, 25));
        profitPanel.add(profitThresholdSpinner);
        profitPanel.add(styler.createStyledLabel("GP"));
        profitPanel.add(styler.createQuestionMark(
            "Automatically transfer GP to mule when your profit reaches this amount. " +
            "Recommended: 100k-500k GP to minimize transfer frequency."
        ));
        panel.add(profitPanel);

        // Auto-transfer items
        JPanel itemsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        styler.styleCheckBox(autoItemsCheck);
        itemsPanel.add(autoItemsCheck);
        styler.styleSpinner(itemThresholdSpinner);
        itemThresholdSpinner.setPreferredSize(new Dimension(80, 25));
        itemsPanel.add(itemThresholdSpinner);
        itemsPanel.add(styler.createStyledLabel("items"));
        itemsPanel.add(styler.createQuestionMark(
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
    public static JPanel createActionsPanel(
            JButton testConnectionButton,
            JButton manualTransferButton,
            JButton saveSettingsButton) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(styler.createProfessionalBorder("Actions"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // Test connection button
        testConnectionButton.setPreferredSize(new Dimension(150, 35));
        styler.styleButton(testConnectionButton, INFO_COLOR);
        testConnectionButton.setToolTipText("Verify mule is online and at location");
        buttonsPanel.add(testConnectionButton);

        // Manual transfer button
        manualTransferButton.setPreferredSize(new Dimension(150, 35));
        styler.styleButton(manualTransferButton, WARNING_COLOR);
        manualTransferButton.setToolTipText("Manually initiate transfer now");
        manualTransferButton.setEnabled(false);
        buttonsPanel.add(manualTransferButton);

        // Save settings button
        saveSettingsButton.setPreferredSize(new Dimension(150, 35));
        styler.styleButton(saveSettingsButton, SUCCESS_COLOR);
        saveSettingsButton.setToolTipText("Save mule configuration");
        buttonsPanel.add(saveSettingsButton);

        panel.add(buttonsPanel);

        return panel;
    }

    /**
     * Create status panel
     */
    public static JPanel createStatusPanel(
            JLabel statusLabel,
            JLabel lastTransferLabel,
            JProgressBar transferProgressBar) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(styler.createProfessionalBorder("Status"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Status label
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(styler.createStyledLabel("Current Status:"));
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(TEXT_SECONDARY);
        statusPanel.add(statusLabel);
        panel.add(statusPanel);

        // Last transfer label
        JPanel lastTransferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lastTransferPanel.add(styler.createStyledLabel("Last Transfer:"));
        lastTransferLabel.setFont(LABEL_FONT);
        lastTransferPanel.add(lastTransferLabel);
        panel.add(lastTransferPanel);

        // Progress bar
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
    public static JPanel createStatisticsPanel(
            JLabel totalGPLabel,
            JLabel totalItemsLabel,
            JLabel transferCountLabel) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(styler.createProfessionalBorder("Transfer Statistics"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Total GP transferred
        JPanel gpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gpPanel.add(styler.createStyledLabel("Total GP Transferred:"));
        totalGPLabel.setFont(VALUE_FONT);
        totalGPLabel.setForeground(PROFIT_COLOR);
        gpPanel.add(totalGPLabel);
        panel.add(gpPanel);

        // Total items transferred
        JPanel itemsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemsPanel.add(styler.createStyledLabel("Total Items Transferred:"));
        totalItemsLabel.setFont(VALUE_FONT);
        totalItemsLabel.setForeground(ACCENT_COLOR);
        itemsPanel.add(totalItemsLabel);
        panel.add(itemsPanel);

        // Transfer count
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countPanel.add(styler.createStyledLabel("Transfer Count:"));
        transferCountLabel.setFont(VALUE_FONT);
        transferCountLabel.setForeground(INFO_COLOR);
        countPanel.add(transferCountLabel);
        panel.add(countPanel);

        return panel;
    }
}
