package Hi_alch.gui.panels;

import Hi_alch.gui.GUIStyler;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Settings panel builder for advanced configuration
 * Handles anti-ban settings and Discord integration
 */
public class SettingsPanelBuilder {

    private final GUIStyler styler;

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

    public SettingsPanelBuilder(GUIStyler styler) {
        this.styler = styler;
    }

    /**
     * Create professional settings panel
     */
    public JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(createAntibanSettingsPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createDiscordIntegrationPanel());

        return panel;
    }

    private JPanel createAntibanSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(styler.createProfessionalBorder("Advanced Anti-ban Protection System"));

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Enable anti-ban
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        enableAntibanCheck = new JCheckBox("Enable Advanced Anti-ban System");
        enableAntibanCheck.setSelected(true);
        styler.styleCheckBox(enableAntibanCheck);
        settingsPanel.add(enableAntibanCheck, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        settingsPanel.add(styler.createQuestionMark("Enables comprehensive human-like behavior patterns."), gbc);

        // Profile-based seeding
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        profileSeedingCheck = new JCheckBox("User Profile-Based Anti-ban Seeding");
        profileSeedingCheck.setSelected(true);
        styler.styleCheckBox(profileSeedingCheck);
        settingsPanel.add(profileSeedingCheck, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        settingsPanel.add(styler.createQuestionMark("Personalizes anti-ban patterns based on your profile."), gbc);

        // User profile field
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel profileLabel = styler.createStyledLabel("Profile Seed:");
        settingsPanel.add(profileLabel, gbc);

        gbc.gridx = 1;
        userProfileField = new JTextField(12);
        userProfileField.setToolTipText("Your unique profile identifier");
        styler.styleTextField(userProfileField);
        settingsPanel.add(userProfileField, gbc);

        gbc.gridx = 2;
        generateSeedButton = new JButton("Generate");
        generateSeedButton.setPreferredSize(new Dimension(90, 25));
        styler.styleButton(generateSeedButton, new Color(156, 39, 176));
        settingsPanel.add(generateSeedButton, gbc);

        gbc.gridx = 3;
        settingsPanel.add(styler.createQuestionMark("Generates a unique seed for personalized anti-ban."), gbc);

        // Fatigue system
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        fatigueSystemCheck = new JCheckBox("Intelligent Fatigue System");
        fatigueSystemCheck.setSelected(true);
        styler.styleCheckBox(fatigueSystemCheck);
        settingsPanel.add(fatigueSystemCheck, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        settingsPanel.add(styler.createQuestionMark("Monitors session length and adjusts behavior."), gbc);

        // Fatigue indicator
        gbc.gridx = 0; gbc.gridy = 4;
        fatigueLabel = styler.createStyledLabel("Current Fatigue:");
        settingsPanel.add(fatigueLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        fatigueBar = new JProgressBar(0, 100);
        fatigueBar.setStringPainted(true);
        fatigueBar.setString("0% - Fresh & Alert");
        fatigueBar.setForeground(new Color(76, 175, 80));
        settingsPanel.add(fatigueBar, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        settingsPanel.add(styler.createQuestionMark("Real-time fatigue level indicator."), gbc);

        // Aggression slider
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel aggressionLabel = styler.createStyledLabel("Behavior Aggression:");
        settingsPanel.add(aggressionLabel, gbc);

        gbc.gridx = 1;
        settingsPanel.add(styler.createQuestionMark("1 = Very human-like | 10 = Fast but riskier"), gbc);

        gbc.gridx = 2;
        aggressionValueLabel = styler.createStyledLabel("Level: 5 (Balanced)");
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
        styler.styleCheckBox(breakSystemCheck);
        settingsPanel.add(breakSystemCheck, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        settingsPanel.add(styler.createQuestionMark("Takes random breaks based on session length."), gbc);

        // Break timing
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel minBreakLabel = styler.createStyledLabel("Break Range:");
        settingsPanel.add(minBreakLabel, gbc);

        gbc.gridx = 1;
        JPanel breakRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        minBreakSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        minBreakSpinner.setPreferredSize(new Dimension(60, 25));
        styler.styleSpinner(minBreakSpinner);
        breakRangePanel.add(minBreakSpinner);

        breakRangePanel.add(styler.createStyledLabel(" to "));

        maxBreakSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 60, 1));
        maxBreakSpinner.setPreferredSize(new Dimension(60, 25));
        styler.styleSpinner(maxBreakSpinner);
        breakRangePanel.add(maxBreakSpinner);

        breakRangePanel.add(styler.createStyledLabel(" minutes"));

        settingsPanel.add(breakRangePanel, gbc);

        gbc.gridx = 2;
        settingsPanel.add(styler.createQuestionMark("Random break duration range."), gbc);

        panel.add(settingsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDiscordIntegrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(styler.createProfessionalBorder("Discord Integration"));

        JPanel enablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        enableDiscordCheck = new JCheckBox("Enable Discord Notifications");
        styler.styleCheckBox(enableDiscordCheck);
        enablePanel.add(enableDiscordCheck);
        enablePanel.add(styler.createQuestionMark("Receive bot status updates in your Discord channel"));

        JPanel webhookPanel = new JPanel(new BorderLayout(5, 5));

        JPanel webhookLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel webhookLabel = styler.createStyledLabel("Webhook URL:");
        webhookLabelPanel.add(webhookLabel);
        webhookLabelPanel.add(styler.createQuestionMark("Discord webhook URL for sending notifications"));

        webhookField = new JTextField();
        webhookField.setToolTipText("https://discord.com/api/webhooks/YOUR_WEBHOOK_URL");
        styler.styleTextField(webhookField);

        testWebhookButton = new JButton("Test");
        testWebhookButton.setPreferredSize(new Dimension(80, 30));
        styler.styleButton(testWebhookButton, new Color(114, 137, 218));

        JPanel webhookFieldPanel = new JPanel(new BorderLayout());
        webhookFieldPanel.add(webhookField, BorderLayout.CENTER);
        webhookFieldPanel.add(testWebhookButton, BorderLayout.EAST);

        webhookPanel.add(webhookLabelPanel, BorderLayout.NORTH);
        webhookPanel.add(webhookFieldPanel, BorderLayout.CENTER);

        panel.add(enablePanel, BorderLayout.NORTH);
        panel.add(webhookPanel, BorderLayout.CENTER);

        return panel;
    }

    // Getters
    public JTextField getWebhookField() { return webhookField; }
    public JButton getTestWebhookButton() { return testWebhookButton; }
    public JCheckBox getEnableDiscordCheck() { return enableDiscordCheck; }
    public JCheckBox getEnableAntibanCheck() { return enableAntibanCheck; }
    public JSlider getAntibanAggressionSlider() { return antibanAggressionSlider; }
    public JLabel getAggressionValueLabel() { return aggressionValueLabel; }
    public JCheckBox getBreakSystemCheck() { return breakSystemCheck; }
    public JSpinner getMinBreakSpinner() { return minBreakSpinner; }
    public JSpinner getMaxBreakSpinner() { return maxBreakSpinner; }
    public JCheckBox getFatigueSystemCheck() { return fatigueSystemCheck; }
    public JCheckBox getProfileSeedingCheck() { return profileSeedingCheck; }
    public JTextField getUserProfileField() { return userProfileField; }
    public JButton getGenerateSeedButton() { return generateSeedButton; }
    public JProgressBar getFatigueBar() { return fatigueBar; }
    public JLabel getFatigueLabel() { return fatigueLabel; }
}
