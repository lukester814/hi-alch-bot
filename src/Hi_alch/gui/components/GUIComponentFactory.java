package Hi_alch.gui.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Factory for creating styled GUI components
 *
 * Provides consistent styling across all GUI components including
 * labels, text fields, buttons, panels, and more.
 */
public class GUIComponentFactory {

    // ===========================================
    // COLOR SCHEME
    // ===========================================

    // Modern color palette
    public static final Color BACKGROUND_COLOR = new Color(45, 45, 48);
    public static final Color PANEL_BACKGROUND = new Color(37, 37, 38);
    public static final Color INPUT_BACKGROUND = new Color(60, 63, 65);
    public static final Color TEXT_COLOR = new Color(187, 187, 187);
    public static final Color LABEL_COLOR = new Color(204, 204, 204);
    public static final Color BORDER_COLOR = new Color(64, 64, 64);
    public static final Color ACCENT_COLOR = new Color(0, 122, 204);
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    public static final Color WARNING_COLOR = new Color(255, 152, 0);
    public static final Color ERROR_COLOR = new Color(244, 67, 54);

    // ===========================================
    // FONTS
    // ===========================================

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 12);

    // ===========================================
    // BORDERS
    // ===========================================

    public static final Border EMPTY_BORDER_10 = new EmptyBorder(10, 10, 10, 10);
    public static final Border EMPTY_BORDER_15 = new EmptyBorder(15, 15, 15, 15);
    public static final Border EMPTY_BORDER_5 = new EmptyBorder(5, 5, 5, 5);

    // ===========================================
    // LABEL CREATION
    // ===========================================

    /**
     * Create a standard label
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL_COLOR);
        label.setFont(NORMAL_FONT);
        return label;
    }

    /**
     * Create a header label
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL_COLOR);
        label.setFont(HEADER_FONT);
        return label;
    }

    /**
     * Create a title label
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ACCENT_COLOR);
        label.setFont(TITLE_FONT);
        return label;
    }

    /**
     * Create a small label
     */
    public static JLabel createSmallLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(SMALL_FONT);
        return label;
    }

    /**
     * Create a colored label
     */
    public static JLabel createColoredLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(NORMAL_FONT);
        return label;
    }

    // ===========================================
    // TEXT FIELD CREATION
    // ===========================================

    /**
     * Create a standard text field
     */
    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        styleTextField(field);
        return field;
    }

    /**
     * Create a text field with initial text
     */
    public static JTextField createTextField(String text, int columns) {
        JTextField field = new JTextField(text, columns);
        styleTextField(field);
        return field;
    }

    /**
     * Style a text field
     */
    public static void styleTextField(JTextField field) {
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    // ===========================================
    // BUTTON CREATION
    // ===========================================

    /**
     * Create a standard button
     */
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, ACCENT_COLOR);
        return button;
    }

    /**
     * Create a success button
     */
    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, SUCCESS_COLOR);
        return button;
    }

    /**
     * Create a warning button
     */
    public static JButton createWarningButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, WARNING_COLOR);
        return button;
    }

    /**
     * Create an error/danger button
     */
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, ERROR_COLOR);
        return button;
    }

    /**
     * Style a button with specific color
     */
    public static void styleButton(JButton button, Color baseColor) {
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFont(NORMAL_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColor.darker()),
                new EmptyBorder(8, 15, 8, 15)
        ));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
    }

    // ===========================================
    // PANEL CREATION
    // ===========================================

    /**
     * Create a standard panel
     */
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    /**
     * Create a panel with specific layout
     */
    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    /**
     * Create a titled panel
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = createPanel();
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                title
        );
        border.setTitleColor(LABEL_COLOR);
        border.setTitleFont(HEADER_FONT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                border,
                EMPTY_BORDER_10
        ));
        return panel;
    }

    // ===========================================
    // COMBOBOX CREATION
    // ===========================================

    /**
     * Create a combo box
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        styleComboBox(comboBox);
        return comboBox;
    }

    /**
     * Style a combo box
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(NORMAL_FONT);
    }

    // ===========================================
    // CHECKBOX CREATION
    // ===========================================

    /**
     * Create a checkbox
     */
    public static JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        styleCheckBox(checkBox);
        return checkBox;
    }

    /**
     * Create a checkbox with initial state
     */
    public static JCheckBox createCheckBox(String text, boolean selected) {
        JCheckBox checkBox = new JCheckBox(text, selected);
        styleCheckBox(checkBox);
        return checkBox;
    }

    /**
     * Style a checkbox
     */
    public static void styleCheckBox(JCheckBox checkBox) {
        checkBox.setBackground(PANEL_BACKGROUND);
        checkBox.setForeground(LABEL_COLOR);
        checkBox.setFont(NORMAL_FONT);
        checkBox.setFocusPainted(false);
    }

    // ===========================================
    // SPINNER CREATION
    // ===========================================

    /**
     * Create a number spinner
     */
    public static JSpinner createSpinner(int value, int min, int max, int step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        styleSpinner(spinner);
        return spinner;
    }

    /**
     * Style a spinner
     */
    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(NORMAL_FONT);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setBackground(INPUT_BACKGROUND);
            spinnerEditor.getTextField().setForeground(TEXT_COLOR);
            spinnerEditor.getTextField().setFont(NORMAL_FONT);
        }
    }

    // ===========================================
    // SLIDER CREATION
    // ===========================================

    /**
     * Create a slider
     */
    public static JSlider createSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        styleSlider(slider);
        return slider;
    }

    /**
     * Style a slider
     */
    public static void styleSlider(JSlider slider) {
        slider.setBackground(PANEL_BACKGROUND);
        slider.setForeground(ACCENT_COLOR);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing((slider.getMaximum() - slider.getMinimum()) / 10);
        slider.setMinorTickSpacing((slider.getMaximum() - slider.getMinimum()) / 20);
    }

    // ===========================================
    // TEXT AREA CREATION
    // ===========================================

    /**
     * Create a text area
     */
    public static JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        styleTextArea(textArea);
        return textArea;
    }

    /**
     * Style a text area
     */
    public static void styleTextArea(JTextArea textArea) {
        textArea.setBackground(INPUT_BACKGROUND);
        textArea.setForeground(TEXT_COLOR);
        textArea.setCaretColor(TEXT_COLOR);
        textArea.setFont(MONO_FONT);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(8, 8, 8, 8)
        ));
    }

    // ===========================================
    // SCROLL PANE CREATION
    // ===========================================

    /**
     * Create a scroll pane
     */
    public static JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        styleScrollPane(scrollPane);
        return scrollPane;
    }

    /**
     * Style a scroll pane
     */
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Create a horizontal spacer
     */
    public static Component createHorizontalSpace(int width) {
        return Box.createRigidArea(new Dimension(width, 0));
    }

    /**
     * Create a vertical spacer
     */
    public static Component createVerticalSpace(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    /**
     * Create a horizontal glue
     */
    public static Component createHorizontalGlue() {
        return Box.createHorizontalGlue();
    }

    /**
     * Create a vertical glue
     */
    public static Component createVerticalGlue() {
        return Box.createVerticalGlue();
    }
}
