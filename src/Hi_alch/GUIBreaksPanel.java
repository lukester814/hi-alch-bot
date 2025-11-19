package Hi_alch;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import static Hi_alch.GUIStyles.*;

/**
 * GUI Panel for Break Handler Configuration
 */
public class GUIBreaksPanel extends JPanel {

    // Break scheduling
    private JCheckBox enableBreaksCheck;
    private JSpinner minBreakIntervalSpinner;
    private JSpinner maxBreakIntervalSpinner;
    private JSpinner minBreakDurationSpinner;
    private JSpinner maxBreakDurationSpinner;

    // Break activities
    private JComboBox<String> breakActivityCombo;
    private JCheckBox randomizeBreakCheck;

    // Antiban configuration
    private JSlider aggressionSlider;
    private JCheckBox cameraMovementCheck;
    private JCheckBox tabChecksCheck;
    private JCheckBox mouseLeavesCheck;
    private JCheckBox skillChecksCheck;

    // Status display
    private JLabel nextBreakLabel;
    private JLabel lastBreakLabel;
    private JLabel totalBreaksLabel;
    private JProgressBar breakProgressBar;

    // ===========================================
    // BREAK CONFIGURATION
    // ===========================================

    public static class BreakConfiguration {
        // Break scheduling
        public boolean breaksEnabled = true;
        public int minBreakIntervalMinutes = 45;
        public int maxBreakIntervalMinutes = 90;
        public int minBreakDurationMinutes = 5;
        public int maxBreakDurationMinutes = 15;

        // Break activity
        public String breakActivity = "Logout";
        public boolean randomizeBreak = true;

        // Antiban settings
        public int aggressionLevel = 5; // 1-10
        public boolean cameraMovement = true;
        public boolean tabChecks = true;
        public boolean mouseLeaves = true;
        public boolean skillChecks = true;
    }

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public GUIBreaksPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(DARK_BG);

        // Main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(DARK_BG);

        // Add sections
        mainPanel.add(createBreakSchedulingSection());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createBreakActivitySection());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createAntibanSection());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createStatusSection());

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DARK_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ===========================================
    // UI SECTIONS
    // ===========================================

    private JPanel createBreakSchedulingSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createStyledBorder("Break Scheduling"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Enable breaks
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        enableBreaksCheck = new JCheckBox("Enable automatic breaks");
        enableBreaksCheck.setSelected(true);
        styleCheckbox(enableBreaksCheck);
        panel.add(enableBreaksCheck, gbc);

        row++;

        // Break interval
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel intervalLabel = new JLabel("Break every (minutes):");
        intervalLabel.setForeground(TEXT_COLOR);
        intervalLabel.setFont(LABEL_FONT);
        panel.add(intervalLabel, gbc);

        gbc.gridx = 1;
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        intervalPanel.setBackground(PANEL_BG);

        minBreakIntervalSpinner = new JSpinner(new SpinnerNumberModel(45, 10, 300, 5));
        styleSpinner(minBreakIntervalSpinner);
        intervalPanel.add(minBreakIntervalSpinner);

        intervalPanel.add(new JLabel(" to "));

        maxBreakIntervalSpinner = new JSpinner(new SpinnerNumberModel(90, 10, 300, 5));
        styleSpinner(maxBreakIntervalSpinner);
        intervalPanel.add(maxBreakIntervalSpinner);

        panel.add(intervalPanel, gbc);

        row++;

        // Break duration
        gbc.gridx = 0; gbc.gridy = row;
        JLabel durationLabel = new JLabel("Break duration (minutes):");
        durationLabel.setForeground(TEXT_COLOR);
        durationLabel.setFont(LABEL_FONT);
        panel.add(durationLabel, gbc);

        gbc.gridx = 1;
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        durationPanel.setBackground(PANEL_BG);

        minBreakDurationSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
        styleSpinner(minBreakDurationSpinner);
        durationPanel.add(minBreakDurationSpinner);

        durationPanel.add(new JLabel(" to "));

        maxBreakDurationSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 60, 1));
        styleSpinner(maxBreakDurationSpinner);
        durationPanel.add(maxBreakDurationSpinner);

        panel.add(durationPanel, gbc);

        return panel;
    }

    private JPanel createBreakActivitySection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createStyledBorder("Break Activity"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Activity selection
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel activityLabel = new JLabel("During breaks:");
        activityLabel.setForeground(TEXT_COLOR);
        activityLabel.setFont(LABEL_FONT);
        panel.add(activityLabel, gbc);

        gbc.gridx = 1;
        String[] activities = {
            "Logout",
            "Bank standing",
            "Random skill check",
            "Examine objects",
            "Camera movement"
        };
        breakActivityCombo = new JComboBox<>(activities);
        breakActivityCombo.setFont(LABEL_FONT);
        breakActivityCombo.setPreferredSize(new Dimension(200, 30));
        panel.add(breakActivityCombo, gbc);

        // Randomize
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        randomizeBreakCheck = new JCheckBox("Randomize break activities");
        randomizeBreakCheck.setSelected(true);
        styleCheckbox(randomizeBreakCheck);
        panel.add(randomizeBreakCheck, gbc);

        return panel;
    }

    private JPanel createAntibanSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createStyledBorder("Anti-Ban Behaviors"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Aggression slider
        gbc.gridx = 0; gbc.gridy = row;
        JLabel aggressionLabel = new JLabel("Behavior Aggression:");
        aggressionLabel.setForeground(TEXT_COLOR);
        aggressionLabel.setFont(LABEL_FONT);
        panel.add(aggressionLabel, gbc);

        gbc.gridx = 1;
        JPanel sliderPanel = new JPanel(new BorderLayout(5, 0));
        sliderPanel.setBackground(PANEL_BG);

        aggressionSlider = new JSlider(1, 10, 5);
        aggressionSlider.setMajorTickSpacing(1);
        aggressionSlider.setPaintTicks(true);
        aggressionSlider.setPaintLabels(true);
        aggressionSlider.setBackground(PANEL_BG);
        aggressionSlider.setForeground(TEXT_COLOR);

        sliderPanel.add(aggressionSlider, BorderLayout.CENTER);

        JLabel sliderHint = new JLabel("(1 = Very Human-like, 10 = Aggressive)");
        sliderHint.setForeground(TEXT_COLOR.darker());
        sliderHint.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        sliderPanel.add(sliderHint, BorderLayout.SOUTH);

        panel.add(sliderPanel, gbc);

        row++;

        // Behavior checkboxes
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel behaviorsLabel = new JLabel("Enabled Behaviors:");
        behaviorsLabel.setForeground(TEXT_COLOR);
        behaviorsLabel.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        panel.add(behaviorsLabel, gbc);

        row++;

        gbc.gridy = row++;
        cameraMovementCheck = new JCheckBox("Random camera adjustments");
        cameraMovementCheck.setSelected(true);
        styleCheckbox(cameraMovementCheck);
        panel.add(cameraMovementCheck, gbc);

        gbc.gridy = row++;
        tabChecksCheck = new JCheckBox("Random tab checks");
        tabChecksCheck.setSelected(true);
        styleCheckbox(tabChecksCheck);
        panel.add(tabChecksCheck, gbc);

        gbc.gridy = row++;
        mouseLeavesCheck = new JCheckBox("Mouse off-screen movements");
        mouseLeavesCheck.setSelected(true);
        styleCheckbox(mouseLeavesCheck);
        panel.add(mouseLeavesCheck, gbc);

        gbc.gridy = row++;
        skillChecksCheck = new JCheckBox("Random skill checks");
        skillChecksCheck.setSelected(true);
        styleCheckbox(skillChecksCheck);
        panel.add(skillChecksCheck, gbc);

        return panel;
    }

    private JPanel createStatusSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createStyledBorder("Break Status"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Next break
        gbc.gridx = 0; gbc.gridy = row;
        JLabel nextBreakTitleLabel = new JLabel("Next break in:");
        nextBreakTitleLabel.setForeground(TEXT_COLOR);
        nextBreakTitleLabel.setFont(LABEL_FONT);
        panel.add(nextBreakTitleLabel, gbc);

        gbc.gridx = 1;
        nextBreakLabel = new JLabel("Not scheduled");
        nextBreakLabel.setForeground(ACCENT_COLOR);
        nextBreakLabel.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        panel.add(nextBreakLabel, gbc);

        row++;

        // Last break
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lastBreakTitleLabel = new JLabel("Last break:");
        lastBreakTitleLabel.setForeground(TEXT_COLOR);
        lastBreakTitleLabel.setFont(LABEL_FONT);
        panel.add(lastBreakTitleLabel, gbc);

        gbc.gridx = 1;
        lastBreakLabel = new JLabel("Never");
        lastBreakLabel.setForeground(TEXT_COLOR);
        lastBreakLabel.setFont(LABEL_FONT);
        panel.add(lastBreakLabel, gbc);

        row++;

        // Total breaks
        gbc.gridx = 0; gbc.gridy = row;
        JLabel totalBreaksTitleLabel = new JLabel("Total breaks:");
        totalBreaksTitleLabel.setForeground(TEXT_COLOR);
        totalBreaksTitleLabel.setFont(LABEL_FONT);
        panel.add(totalBreaksTitleLabel, gbc);

        gbc.gridx = 1;
        totalBreaksLabel = new JLabel("0");
        totalBreaksLabel.setForeground(TEXT_COLOR);
        totalBreaksLabel.setFont(LABEL_FONT);
        panel.add(totalBreaksLabel, gbc);

        row++;

        // Progress bar
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        breakProgressBar = new JProgressBar(0, 100);
        breakProgressBar.setStringPainted(true);
        breakProgressBar.setPreferredSize(new Dimension(400, 25));
        breakProgressBar.setFont(LABEL_FONT);
        breakProgressBar.setString("No breaks scheduled");
        panel.add(breakProgressBar, gbc);

        return panel;
    }

    // ===========================================
    // PUBLIC API
    // ===========================================

    /**
     * Get configured break settings
     */
    public BreakConfiguration getBreakConfiguration() {
        BreakConfiguration config = new BreakConfiguration();

        config.breaksEnabled = enableBreaksCheck.isSelected();
        config.minBreakIntervalMinutes = (Integer) minBreakIntervalSpinner.getValue();
        config.maxBreakIntervalMinutes = (Integer) maxBreakIntervalSpinner.getValue();
        config.minBreakDurationMinutes = (Integer) minBreakDurationSpinner.getValue();
        config.maxBreakDurationMinutes = (Integer) maxBreakDurationSpinner.getValue();

        config.breakActivity = (String) breakActivityCombo.getSelectedItem();
        config.randomizeBreak = randomizeBreakCheck.isSelected();

        config.aggressionLevel = aggressionSlider.getValue();
        config.cameraMovement = cameraMovementCheck.isSelected();
        config.tabChecks = tabChecksCheck.isSelected();
        config.mouseLeaves = mouseLeavesCheck.isSelected();
        config.skillChecks = skillChecksCheck.isSelected();

        return config;
    }

    /**
     * Set break configuration
     */
    public void setBreakConfiguration(BreakConfiguration config) {
        enableBreaksCheck.setSelected(config.breaksEnabled);
        minBreakIntervalSpinner.setValue(config.minBreakIntervalMinutes);
        maxBreakIntervalSpinner.setValue(config.maxBreakIntervalMinutes);
        minBreakDurationSpinner.setValue(config.minBreakDurationMinutes);
        maxBreakDurationSpinner.setValue(config.maxBreakDurationMinutes);

        breakActivityCombo.setSelectedItem(config.breakActivity);
        randomizeBreakCheck.setSelected(config.randomizeBreak);

        aggressionSlider.setValue(config.aggressionLevel);
        cameraMovementCheck.setSelected(config.cameraMovement);
        tabChecksCheck.setSelected(config.tabChecks);
        mouseLeavesCheck.setSelected(config.mouseLeaves);
        skillChecksCheck.setSelected(config.skillChecks);
    }

    /**
     * Update break status display
     */
    public void updateBreakStatus(long nextBreakTime, long lastBreakTime, int totalBreaks) {
        // Next break
        if (nextBreakTime > 0) {
            long timeUntilBreak = nextBreakTime - System.currentTimeMillis();
            if (timeUntilBreak > 0) {
                int minutes = (int) (timeUntilBreak / 60000);
                nextBreakLabel.setText(minutes + " minutes");

                // Update progress bar
                int totalInterval = (Integer) maxBreakIntervalSpinner.getValue() * 60000;
                int elapsed = (int) (totalInterval - timeUntilBreak);
                int progress = (int) ((elapsed * 100.0) / totalInterval);
                breakProgressBar.setValue(Math.max(0, Math.min(100, progress)));
                breakProgressBar.setString(progress + "% until next break");
            } else {
                nextBreakLabel.setText("BREAK TIME!");
                breakProgressBar.setValue(100);
                breakProgressBar.setString("Break in progress");
            }
        } else {
            nextBreakLabel.setText("Not scheduled");
            breakProgressBar.setValue(0);
            breakProgressBar.setString("Breaks disabled");
        }

        // Last break
        if (lastBreakTime > 0) {
            long timeSinceBreak = System.currentTimeMillis() - lastBreakTime;
            int minutes = (int) (timeSinceBreak / 60000);
            lastBreakLabel.setText(minutes + " minutes ago");
        } else {
            lastBreakLabel.setText("Never");
        }

        // Total breaks
        totalBreaksLabel.setText(String.valueOf(totalBreaks));
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

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(LABEL_FONT);
        spinner.setPreferredSize(new Dimension(80, 30));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(LABEL_FONT);
        }
    }
}
