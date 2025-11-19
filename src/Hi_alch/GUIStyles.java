package Hi_alch;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Professional GUI Styling Utilities
 *
 * Provides consistent styling across all GUI components:
 * - Modern color schemes compatible with DreamBot Substance theme
 * - Consistent fonts and sizing
 * - Interactive hover effects
 * - Professional borders and spacing
 *
 * REUSABLE: Can be used in any Swing-based GUI project!
 */
public class GUIStyles {

    // ===========================================
    // COLOR CONSTANTS
    // ===========================================

    // Primary colors
    public static final Color BORDER_COLOR = new Color(64, 128, 255);
    public static final Color ACCENT_COLOR = new Color(64, 128, 255);
    public static final Color QUESTION_MARK_COLOR = new Color(96, 165, 250);
    public static final Color QUESTION_MARK_HOVER_COLOR = new Color(147, 197, 253);
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    public static final Color WARNING_COLOR = new Color(251, 191, 36);
    public static final Color ERROR_COLOR = new Color(244, 67, 54);
    public static final Color INFO_COLOR = new Color(64, 128, 255);
    public static final Color ACCENT_GOLD = new Color(255, 215, 0);
    public static final Color PROFIT_COLOR = new Color(34, 197, 94);
    public static final Color LOSS_COLOR = new Color(239, 68, 68);

    // Background colors
    public static final Color DARK_BG = new Color(45, 45, 48);
    public static final Color PANEL_BG = new Color(60, 63, 65);

    // Text colors
    public static final Color TEXT_COLOR = new Color(220, 220, 220);
    public static final Color TEXT_SECONDARY = new Color(160, 160, 160);

    // ===========================================
    // FONT CONSTANTS
    // ===========================================

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Inter", Font.PLAIN, 13);
    public static final Font LABEL_FONT = new Font("Inter", Font.PLAIN, 13);
    public static final Font BUTTON_FONT = new Font("Inter", Font.BOLD, 13);
    public static final Font QUESTION_FONT = new Font("Inter", Font.BOLD, 12);
    public static final Font SUBTITLE_FONT = new Font("Inter", Font.ITALIC, 11);
    public static final Font SMALL_FONT = new Font("Inter", Font.PLAIN, 11);
    public static final Font VALUE_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font STATUS_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font PROFIT_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // ===========================================
    // BORDER CREATION
    // ===========================================

    /**
     * Create a professional titled border with accent color
     */
    public static TitledBorder createProfessionalBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(BORDER_COLOR);
        border.setTitleFont(TITLE_FONT);
        border.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return border;
    }

    // ===========================================
    // LABEL STYLING
    // ===========================================

    /**
     * Create a styled label with consistent font
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    /**
     * Create an interactive question mark tooltip label
     */
    public static JLabel createQuestionMark(String tooltipText) {
        final JLabel questionMark = new JLabel("[?]");
        questionMark.setFont(QUESTION_FONT);
        questionMark.setForeground(QUESTION_MARK_COLOR);
        questionMark.setToolTipText("<html><div style='width: 300px; font-family: Inter;'>" + tooltipText + "</div></html>");
        questionMark.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        questionMark.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        questionMark.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                questionMark.setForeground(QUESTION_MARK_HOVER_COLOR);
                questionMark.setText("【?】");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                questionMark.setForeground(QUESTION_MARK_COLOR);
                questionMark.setText("[?]");
            }
        });

        return questionMark;
    }

    // ===========================================
    // TEXT FIELD STYLING
    // ===========================================

    /**
     * Apply professional styling to a text field
     */
    public static void styleTextField(JTextField field) {
        field.setFont(TABLE_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    // ===========================================
    // COMBO BOX STYLING
    // ===========================================

    /**
     * Apply professional styling to a combo box
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(TABLE_FONT);
    }

    // ===========================================
    // SPINNER STYLING
    // ===========================================

    /**
     * Apply professional styling to a spinner
     */
    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(TABLE_FONT);
    }

    // ===========================================
    // CHECKBOX STYLING
    // ===========================================

    /**
     * Apply professional styling to a checkbox
     */
    public static void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(TABLE_FONT);
        checkBox.setFocusPainted(false);
    }

    // ===========================================
    // BUTTON STYLING
    // ===========================================

    /**
     * Apply professional styling to a button with color
     */
    public static void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
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
    // STATUS COLOR DETERMINATION
    // ===========================================

    /**
     * Get appropriate color for status message
     */
    public static Color getStatusColor(String status) {
        if (status.contains("✅") || status.contains("successful")) {
            return SUCCESS_COLOR;
        } else if (status.contains("❌") || status.contains("failed") || status.contains("error")) {
            return ERROR_COLOR;
        } else if (status.contains("🚀") || status.contains("running")) {
            return INFO_COLOR;
        } else if (status.contains("⚠️") || status.contains("warning")) {
            return WARNING_COLOR;
        }
        return Color.WHITE;
    }
}
