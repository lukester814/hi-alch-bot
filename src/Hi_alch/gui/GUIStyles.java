package Hi_alch.gui;

import java.awt.*;

/**
 * Centralized GUI styling constants for consistent theming
 */
public class GUIStyles {

    // Background colors
    public static final Color DARK_BG = new Color(45, 45, 48);
    public static final Color PANEL_BG = new Color(60, 60, 64);
    public static final Color COMPONENT_BG = new Color(75, 75, 80);

    // Text colors
    public static final Color TEXT_COLOR = new Color(220, 220, 220);
    public static final Color TEXT_DIM = new Color(160, 160, 160);

    // Accent colors
    public static final Color ACCENT_COLOR = new Color(64, 128, 255);
    public static final Color ACCENT_BLUE = new Color(64, 128, 255);
    public static final Color ACCENT_GREEN = new Color(76, 175, 80);
    public static final Color ACCENT_RED = new Color(244, 67, 54);
    public static final Color ACCENT_ORANGE = new Color(249, 115, 22);
    public static final Color ACCENT_YELLOW = new Color(251, 191, 36);

    // Border colors
    public static final Color BORDER_COLOR = new Color(100, 100, 105);

    // Fonts
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 11);

    // Border
    public static final int BORDER_RADIUS = 8;
    public static final int PADDING = 10;

    private GUIStyles() {
        // Prevent instantiation
    }
}
