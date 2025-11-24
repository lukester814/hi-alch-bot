package Hi_alch.overlay;

import java.awt.*;

/**
 * Overlay Style Manager - Centralized styling for overlays
 *
 * Features:
 * - Modern dark theme colors
 * - Professional fonts
 * - Layout constants
 * - Gradient generators
 * - Color utilities
 *
 * REUSABLE: Perfect for any DreamBot script overlay!
 */
public class OverlayStyleManager {

    // ===========================================
    // COLORS - MODERN DARK THEME
    // ===========================================

    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 200);
    public static final Color BORDER_COLOR = new Color(64, 128, 255, 220);
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color ACCENT_COLOR = new Color(64, 128, 255);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);
    public static final Color ERROR_COLOR = new Color(244, 67, 54);
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);

    // Additional colors
    public static final Color MAGIC_PURPLE = new Color(150, 100, 255);
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;
    public static final Color DARK_BACKGROUND_1 = new Color(20, 20, 30, 220);
    public static final Color DARK_BACKGROUND_2 = new Color(40, 40, 60, 200);
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);
    public static final Color INNER_BORDER_COLOR = new Color(255, 255, 255, 30);
    public static final Color DARK_PROGRESS_BG = new Color(50, 50, 50);

    // ===========================================
    // FONTS - LARGER FOR BETTER VISIBILITY
    // ===========================================

    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 13);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 11);

    // ===========================================
    // LAYOUT - MODERN CHAT-COVERING OVERLAY
    // ===========================================

    public static final int OVERLAY_WIDTH = 280;
    public static final int OVERLAY_PADDING = 15;
    public static final int LINE_HEIGHT = 20;
    public static final int BORDER_RADIUS = 12;
    public static final int INDICATOR_SIZE = 8;

    // Shadow offsets
    public static final int SHADOW_OFFSET = 3;

    // Stroke widths
    public static final int BORDER_STROKE_WIDTH = 2;
    public static final int INNER_BORDER_STROKE_WIDTH = 1;

    // ===========================================
    // GRADIENT GENERATORS
    // ===========================================

    /**
     * Create background gradient
     */
    public static GradientPaint createBackgroundGradient(int x, int y, int height) {
        return new GradientPaint(
            x, y, DARK_BACKGROUND_1,
            x, y + height, DARK_BACKGROUND_2
        );
    }

    /**
     * Create title gradient
     */
    public static GradientPaint createTitleGradient(int x, int width) {
        return new GradientPaint(
            x, 0, new Color(100, 150, 255),
            x + width, 0, new Color(150, 100, 255)
        );
    }

    // ===========================================
    // COLOR UTILITIES
    // ===========================================

    /**
     * Get color for current state
     */
    public static Color getStateColor(String state) {
        if (state == null) return TEXT_COLOR;

        String stateLower = state.toLowerCase();
        if (stateLower.contains("error") || stateLower.contains("failed")) {
            return ERROR_COLOR;
        } else if (stateLower.contains("warning") || stateLower.contains("waiting")) {
            return WARNING_COLOR;
        } else if (stateLower.contains("running") || stateLower.contains("alching")) {
            return ACCENT_COLOR;
        } else {
            return TEXT_COLOR;
        }
    }

    /**
     * Get profit color (green for positive, red for negative)
     */
    public static Color getProfitColor(int profit) {
        return profit >= 0 ? SUCCESS_COLOR : ERROR_COLOR;
    }

    /**
     * Get success rate color
     */
    public static Color getSuccessRateColor(double successRate) {
        if (successRate >= 95) return ACCENT_COLOR;
        if (successRate >= 90) return WARNING_COLOR;
        return ERROR_COLOR;
    }

    /**
     * Get status indicator color
     */
    public static Color getStatusIndicatorColor(boolean isRunning) {
        return isRunning ? ACCENT_COLOR : ERROR_COLOR;
    }
}
