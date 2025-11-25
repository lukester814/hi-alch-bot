package Hi_alch.gui.panels;

import Hi_alch.managers.SessionGoalsManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import static Hi_alch.gui.GUIStyles.*;

/**
 * GUI Panel for Session Goals Configuration
 */
public class GUISessionGoalsPanel extends JPanel {

    // Goal checkboxes
    private JCheckBox alchGoalCheck;
    private JCheckBox profitGoalCheck;
    private JCheckBox xpGoalCheck;
    private JCheckBox timeGoalCheck;
    private JCheckBox levelGoalCheck;

    // Goal input fields
    private JSpinner alchSpinner;
    private JSpinner profitSpinner;
    private JSpinner xpSpinner;
    private JSpinner timeSpinner;
    private JSpinner levelSpinner;

    // Logic mode
    private JRadioButton orLogicRadio;
    private JRadioButton andLogicRadio;

    // Notification settings
    private JCheckBox notifyCheck;
    private JCheckBox discordNotifyCheck;
    private JCheckBox soundAlertCheck;

    // Progress display
    private JProgressBar overallProgressBar;
    private JTextArea progressTextArea;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public GUISessionGoalsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(DARK_BG);

        // Main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(DARK_BG);

        // Add sections
        mainPanel.add(createGoalsSection());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLogicSection());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createNotificationSection());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createProgressSection());

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DARK_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ===========================================
    // UI SECTIONS
    // ===========================================

    private JPanel createGoalsSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createStyledBorder("Session Goals"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Alch Goal
        gbc.gridx = 0; gbc.gridy = row;
        alchGoalCheck = new JCheckBox("Stop after alchs:");
        styleCheckbox(alchGoalCheck);
        panel.add(alchGoalCheck, gbc);

        gbc.gridx = 1;
        alchSpinner = new JSpinner(new SpinnerNumberModel(1000, 1, 1000000, 100));
        styleSpinner(alchSpinner);
        panel.add(alchSpinner, gbc);

        row++;

        // Profit Goal
        gbc.gridx = 0; gbc.gridy = row;
        profitGoalCheck = new JCheckBox("Stop after profit (GP):");
        styleCheckbox(profitGoalCheck);
        panel.add(profitGoalCheck, gbc);

        gbc.gridx = 1;
        profitSpinner = new JSpinner(new SpinnerNumberModel(100000, 1000, 100000000, 10000));
        styleSpinner(profitSpinner);
        panel.add(profitSpinner, gbc);

        row++;

        // XP Goal
        gbc.gridx = 0; gbc.gridy = row;
        xpGoalCheck = new JCheckBox("Stop after XP gained:");
        styleCheckbox(xpGoalCheck);
        panel.add(xpGoalCheck, gbc);

        gbc.gridx = 1;
        xpSpinner = new JSpinner(new SpinnerNumberModel(50000, 100, 10000000, 1000));
        styleSpinner(xpSpinner);
        panel.add(xpSpinner, gbc);

        row++;

        // Time Goal
        gbc.gridx = 0; gbc.gridy = row;
        timeGoalCheck = new JCheckBox("Stop after time (minutes):");
        styleCheckbox(timeGoalCheck);
        panel.add(timeGoalCheck, gbc);

        gbc.gridx = 1;
        timeSpinner = new JSpinner(new SpinnerNumberModel(120, 1, 1440, 10));
        styleSpinner(timeSpinner);
        panel.add(timeSpinner, gbc);

        row++;

        // Level Goal
        gbc.gridx = 0; gbc.gridy = row;
        levelGoalCheck = new JCheckBox("Stop at Magic level:");
        styleCheckbox(levelGoalCheck);
        panel.add(levelGoalCheck, gbc);

        gbc.gridx = 1;
        levelSpinner = new JSpinner(new SpinnerNumberModel(70, 1, 99, 1));
        styleSpinner(levelSpinner);
        panel.add(levelSpinner, gbc);

        return panel;
    }

    private JPanel createLogicSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(PANEL_BG);
        panel.setBorder(createStyledBorder("Goal Logic"));

        JLabel label = new JLabel("Stop when:");
        label.setForeground(TEXT_COLOR);
        label.setFont(LABEL_FONT);
        panel.add(label);

        orLogicRadio = new JRadioButton("ANY goal is reached (OR)");
        styleRadioButton(orLogicRadio);
        orLogicRadio.setSelected(true);

        andLogicRadio = new JRadioButton("ALL goals are reached (AND)");
        styleRadioButton(andLogicRadio);

        ButtonGroup group = new ButtonGroup();
        group.add(orLogicRadio);
        group.add(andLogicRadio);

        panel.add(orLogicRadio);
        panel.add(andLogicRadio);

        return panel;
    }

    private JPanel createNotificationSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(PANEL_BG);
        panel.setBorder(createStyledBorder("Notifications"));

        notifyCheck = new JCheckBox("Enable notifications");
        notifyCheck.setSelected(true);
        styleCheckbox(notifyCheck);
        panel.add(notifyCheck);

        discordNotifyCheck = new JCheckBox("Discord alert");
        discordNotifyCheck.setSelected(true);
        styleCheckbox(discordNotifyCheck);
        panel.add(discordNotifyCheck);

        soundAlertCheck = new JCheckBox("Sound alert");
        soundAlertCheck.setSelected(true);
        styleCheckbox(soundAlertCheck);
        panel.add(soundAlertCheck);

        return panel;
    }

    private JPanel createProgressSection() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL_BG);
        panel.setBorder(createStyledBorder("Goal Progress"));

        // Overall progress bar
        JPanel barPanel = new JPanel(new BorderLayout(5, 5));
        barPanel.setBackground(PANEL_BG);

        JLabel barLabel = new JLabel("Overall Progress:");
        barLabel.setForeground(TEXT_COLOR);
        barLabel.setFont(LABEL_FONT);
        barPanel.add(barLabel, BorderLayout.WEST);

        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setStringPainted(true);
        overallProgressBar.setPreferredSize(new Dimension(300, 25));
        overallProgressBar.setFont(LABEL_FONT);
        barPanel.add(overallProgressBar, BorderLayout.CENTER);

        panel.add(barPanel, BorderLayout.NORTH);

        // Detailed progress text
        progressTextArea = new JTextArea(6, 40);
        progressTextArea.setEditable(false);
        progressTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        progressTextArea.setBackground(DARK_BG);
        progressTextArea.setForeground(TEXT_COLOR);
        progressTextArea.setText("No goals set");

        JScrollPane scrollPane = new JScrollPane(progressTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ===========================================
    // PUBLIC API
    // ===========================================

    /**
     * Get configured session goals
     */
    public SessionGoalsManager.SessionGoals getSessionGoals() {
        SessionGoalsManager.SessionGoals goals = new SessionGoalsManager.SessionGoals();

        goals.alchGoalEnabled = alchGoalCheck.isSelected();
        goals.profitGoalEnabled = profitGoalCheck.isSelected();
        goals.xpGoalEnabled = xpGoalCheck.isSelected();
        goals.timeGoalEnabled = timeGoalCheck.isSelected();
        goals.levelGoalEnabled = levelGoalCheck.isSelected();

        goals.targetAlchs = (Integer) alchSpinner.getValue();
        goals.targetProfit = (Integer) profitSpinner.getValue();
        goals.targetXP = (Integer) xpSpinner.getValue();
        goals.targetMinutes = (Integer) timeSpinner.getValue();
        goals.targetLevel = (Integer) levelSpinner.getValue();

        goals.useAndLogic = andLogicRadio.isSelected();

        goals.notifyOnGoal = notifyCheck.isSelected();
        goals.discordNotify = discordNotifyCheck.isSelected();
        goals.soundAlert = soundAlertCheck.isSelected();

        return goals;
    }

    /**
     * Set session goals from configuration
     */
    public void setSessionGoals(SessionGoalsManager.SessionGoals goals) {
        alchGoalCheck.setSelected(goals.alchGoalEnabled);
        profitGoalCheck.setSelected(goals.profitGoalEnabled);
        xpGoalCheck.setSelected(goals.xpGoalEnabled);
        timeGoalCheck.setSelected(goals.timeGoalEnabled);
        levelGoalCheck.setSelected(goals.levelGoalEnabled);

        alchSpinner.setValue(goals.targetAlchs);
        profitSpinner.setValue(goals.targetProfit);
        xpSpinner.setValue(goals.targetXP);
        timeSpinner.setValue(goals.targetMinutes);
        levelSpinner.setValue(goals.targetLevel);

        if (goals.useAndLogic) {
            andLogicRadio.setSelected(true);
        } else {
            orLogicRadio.setSelected(true);
        }

        notifyCheck.setSelected(goals.notifyOnGoal);
        discordNotifyCheck.setSelected(goals.discordNotify);
        soundAlertCheck.setSelected(goals.soundAlert);
    }

    /**
     * Update progress display
     */
    public void updateProgress(SessionGoalsManager manager) {
        if (manager == null) {
            overallProgressBar.setValue(0);
            progressTextArea.setText("No goals set");
            return;
        }

        int overallProgress = manager.getOverallProgress();
        overallProgressBar.setValue(overallProgress);

        if (manager.isGoalReached()) {
            overallProgressBar.setString("COMPLETE! " + overallProgress + "%");
            progressTextArea.setText(manager.getGoalReachedMessage());
        } else {
            overallProgressBar.setString(overallProgress + "%");
            progressTextArea.setText(manager.getProgressSummary());
        }
    }

    /**
     * Reset progress display
     */
    public void resetProgress() {
        overallProgressBar.setValue(0);
        overallProgressBar.setString("0%");
        progressTextArea.setText("No goals set");
    }

    // ===========================================
    // STYLING HELPERS
    // ===========================================

    private TitledBorder createStyledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            title
        );
        border.setTitleColor(ACCENT_COLOR);
        border.setTitleFont(HEADER_FONT);
        return border;
    }

    private void styleCheckbox(JCheckBox checkbox) {
        checkbox.setBackground(PANEL_BG);
        checkbox.setForeground(TEXT_COLOR);
        checkbox.setFont(LABEL_FONT);
        checkbox.setFocusPainted(false);
    }

    private void styleRadioButton(JRadioButton radio) {
        radio.setBackground(PANEL_BG);
        radio.setForeground(TEXT_COLOR);
        radio.setFont(LABEL_FONT);
        radio.setFocusPainted(false);
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(LABEL_FONT);
        spinner.setPreferredSize(new Dimension(150, 30));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(LABEL_FONT);
        }
    }
}
