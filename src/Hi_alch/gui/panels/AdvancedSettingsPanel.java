package Hi_alch.gui.panels;

import Hi_alch.BotUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static Hi_alch.GUIStyles.*;

/**
 * Advanced Settings Panel for High Alchemy Bot
 *
 * Contains:
 * - Anti-ban Settings (enable, profile seeding, fatigue system, aggression, break system)
 * - Discord Integration (webhook URL, enable/disable, test button)
 *
 * Extracted from AlchBotGUI to reduce class size and improve maintainability.
 */
public class AdvancedSettingsPanel extends JPanel {

    // ===========================================
    // GUI COMPONENTS
    // ===========================================

    // Anti-ban Components
    private JCheckBox enableAntibanCheck;
    private JCheckBox profileSeedingCheck;
    private JTextField userProfileField;
    private JButton generateSeedButton;
    private JCheckBox fatigueSystemCheck;
    private JProgressBar fatigueBar;
    private JLabel fatigueLabel;
    private JLabel aggressionValueLabel;
    private JSlider antibanAggressionSlider;
    private JCheckBox breakSystemCheck;
    private JSpinner minBreakSpinner;
    private JSpinner maxBreakSpinner;

    // Discord Components
    private JTextField webhookField;
    private JButton testWebhookButton;
    private JCheckBox enableDiscordCheck;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public AdvancedSettingsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createAntibanSettingsPanel());
        add(Box.createVerticalStrut(20));
        add(createDiscordIntegrationPanel());

        BotUtils.log("✅ AdvancedSettingsPanel initialized");
    }

    // ===========================================
    // PANEL CREATION METHODS
    // ===========================================

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
    // PUBLIC GETTERS FOR VALUES
    // ===========================================

    public boolean isAntibanEnabled() {
        return enableAntibanCheck.isSelected();
    }

    public boolean isProfileSeedingEnabled() {
        return profileSeedingCheck.isSelected();
    }

    public String getUserProfile() {
        return userProfileField.getText();
    }

    public boolean isFatigueSystemEnabled() {
        return fatigueSystemCheck.isSelected();
    }

    public int getAggressionLevel() {
        return antibanAggressionSlider.getValue();
    }

    public boolean isBreakSystemEnabled() {
        return breakSystemCheck.isSelected();
    }

    public int getMinBreakMinutes() {
        return (Integer) minBreakSpinner.getValue();
    }

    public int getMaxBreakMinutes() {
        return (Integer) maxBreakSpinner.getValue();
    }

    public boolean isDiscordEnabled() {
        return enableDiscordCheck.isSelected();
    }

    public String getWebhookUrl() {
        return webhookField.getText();
    }

    // ===========================================
    // PUBLIC SETTERS FOR UI UPDATES
    // ===========================================

    public void setAntibanEnabled(boolean enabled) {
        enableAntibanCheck.setSelected(enabled);
    }

    public void setProfileSeedingEnabled(boolean enabled) {
        profileSeedingCheck.setSelected(enabled);
    }

    public void setUserProfile(String profile) {
        userProfileField.setText(profile);
    }

    public void setFatigueLevel(int level) {
        fatigueBar.setValue(level);
        if (level < 30) {
            fatigueBar.setString(level + "% - Fresh & Alert");
            fatigueBar.setForeground(new Color(76, 175, 80));
        } else if (level < 60) {
            fatigueBar.setString(level + "% - Moderately Tired");
            fatigueBar.setForeground(new Color(255, 193, 7));
        } else {
            fatigueBar.setString(level + "% - Very Fatigued");
            fatigueBar.setForeground(new Color(244, 67, 54));
        }
    }

    public void setAggressionLevel(int level) {
        antibanAggressionSlider.setValue(level);
        updateAggressionLabel(level);
    }

    public void setWebhookUrl(String url) {
        webhookField.setText(url);
    }

    public void setDiscordEnabled(boolean enabled) {
        enableDiscordCheck.setSelected(enabled);
    }

    // ===========================================
    // PUBLIC GETTERS FOR COMPONENTS (for event handling)
    // ===========================================

    public JCheckBox getEnableAntibanCheck() {
        return enableAntibanCheck;
    }

    public JCheckBox getProfileSeedingCheck() {
        return profileSeedingCheck;
    }

    public JTextField getUserProfileField() {
        return userProfileField;
    }

    public JButton getGenerateSeedButton() {
        return generateSeedButton;
    }

    public JCheckBox getFatigueSystemCheck() {
        return fatigueSystemCheck;
    }

    public JSlider getAntibanAggressionSlider() {
        return antibanAggressionSlider;
    }

    public JCheckBox getBreakSystemCheck() {
        return breakSystemCheck;
    }

    public JSpinner getMinBreakSpinner() {
        return minBreakSpinner;
    }

    public JSpinner getMaxBreakSpinner() {
        return maxBreakSpinner;
    }

    public JCheckBox getEnableDiscordCheck() {
        return enableDiscordCheck;
    }

    public JTextField getWebhookField() {
        return webhookField;
    }

    public JButton getTestWebhookButton() {
        return testWebhookButton;
    }

    public JProgressBar getFatigueBar() {
        return fatigueBar;
    }

    public JLabel getAggressionValueLabel() {
        return aggressionValueLabel;
    }

    // ===========================================
    // HELPER METHODS
    // ===========================================

    private void updateAggressionLabel(int level) {
        String text;
        if (level <= 3) {
            text = "Level: " + level + " (Very Human-like)";
        } else if (level <= 6) {
            text = "Level: " + level + " (Balanced)";
        } else {
            text = "Level: " + level + " (Fast & Aggressive)";
        }
        aggressionValueLabel.setText(text);
    }
}
