package Hi_alch.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for styling GUI components
 * Handles all visual styling and theming
 */
public class GUIStyler {

    private final List<JLabel> questionMarkLabels = new ArrayList<>();

    public TitledBorder createProfessionalBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(new Color(64, 128, 255));
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        border.setBorder(BorderFactory.createLineBorder(new Color(64, 128, 255), 1));
        return border;
    }

    public JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.PLAIN, 13));
        return label;
    }

    public JLabel createQuestionMark(String tooltipText) {
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

    public void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    public void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    public void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    public void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkBox.setFocusPainted(false);
    }

    public void styleButton(JButton button, Color color) {
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

    public void cleanup() {
        questionMarkLabels.clear();
    }
}
