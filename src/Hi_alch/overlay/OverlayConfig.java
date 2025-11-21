package Hi_alch.overlay;

import java.awt.*;

/**
 * Configuration constants for overlay rendering
 * Defines colors, fonts, and layout settings
 */
public class OverlayConfig {

    // ===========================================
    // COLORS - Modern dark theme
    // ===========================================

    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 200);
    public static final Color BORDER_COLOR = new Color(64, 128, 255, 220);
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color ACCENT_COLOR = new Color(64, 128, 255);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);
    public static final Color ERROR_COLOR = new Color(244, 67, 54);
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);

    // ===========================================
    // FONTS - Larger for better visibility
    // ===========================================

    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 13);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 11);

    // ===========================================
    // LAYOUT - Modern chat-covering overlay
    // ===========================================

    public static final int OVERLAY_WIDTH = 280;
    public static final int OVERLAY_PADDING = 15;
    public static final int LINE_HEIGHT = 20;

    // Position defaults
    public static final int DEFAULT_X = 15;
    public static final int DEFAULT_Y = 350;

    // Private constructor to prevent instantiation
    private OverlayConfig() {
    }
}
