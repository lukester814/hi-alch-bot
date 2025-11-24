package Hi_alch.overlay;

import Hi_alch.BotUtils;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import static Hi_alch.overlay.OverlayStyleManager.*;

/**
 * Overlay Drawing Utilities - Helper methods for drawing overlay components
 *
 * Features:
 * - Background and border drawing
 * - Text rendering (centered, aligned)
 * - Section rendering (title, stats, performance)
 * - Progress bars
 * - Status indicators
 * - Separators
 *
 * REUSABLE: Perfect for any DreamBot script overlay!
 */
public class OverlayDrawingUtils {

    // ===========================================
    // BACKGROUND & BORDERS
    // ===========================================

    /**
     * Draw overlay background with modern styling
     */
    public static void drawBackground(Graphics2D g, int x, int y, int width, int height) {
        // Drop shadow effect
        g.setColor(SHADOW_COLOR);
        g.fillRoundRect(x + SHADOW_OFFSET, y + SHADOW_OFFSET, width, height,
                       BORDER_RADIUS, BORDER_RADIUS);

        // Main background with gradient effect
        GradientPaint gradient = createBackgroundGradient(x, y, height);
        g.setPaint(gradient);
        g.fillRoundRect(x, y, width, height, BORDER_RADIUS, BORDER_RADIUS);

        // Modern border with glow effect
        g.setStroke(new BasicStroke(BORDER_STROKE_WIDTH));
        g.setColor(BORDER_COLOR);
        g.drawRoundRect(x, y, width, height, BORDER_RADIUS, BORDER_RADIUS);

        // Inner border for depth
        g.setStroke(new BasicStroke(INNER_BORDER_STROKE_WIDTH));
        g.setColor(INNER_BORDER_COLOR);
        g.drawRoundRect(x + 1, y + 1, width - 2, height - 2, BORDER_RADIUS - 2, BORDER_RADIUS - 2);
    }

    // ===========================================
    // TEXT RENDERING
    // ===========================================

    /**
     * Draw centered text
     */
    public static void drawCenteredText(Graphics2D g, String text, int centerX, int width, int y) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        int textX = centerX + (width - (int)rect.getWidth()) / 2;
        g.drawString(text, textX, y);
    }

    /**
     * Draw separator line
     */
    public static int drawSeparator(Graphics2D g, int x, int y, int width) {
        g.setColor(BORDER_COLOR);
        g.drawLine(x + OVERLAY_PADDING, y, x + width - OVERLAY_PADDING, y);
        return y + 8;
    }

    // ===========================================
    // SECTION RENDERING
    // ===========================================

    /**
     * Draw title section with modern styling
     */
    public static int drawTitle(Graphics2D g, int x, int y, int width) {
        g.setFont(TITLE_FONT);

        // Modern gradient title
        GradientPaint titleGradient = createTitleGradient(x, width);
        g.setPaint(titleGradient);

        String title = "⚡ High Alchemy Bot v2.0";
        drawCenteredText(g, title, x, width, y);

        // Underline effect
        y += 5;
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(1));
        g.drawLine(x + OVERLAY_PADDING, y, x + width - OVERLAY_PADDING, y);

        return y + LINE_HEIGHT;
    }

    /**
     * Draw current state section
     */
    public static int drawState(Graphics2D g, int x, int y, String itemName,
                                String currentState, ScriptStatistics stats) {
        g.setFont(NORMAL_FONT);

        // Current item
        g.setColor(TEXT_COLOR);
        g.drawString("Item: " + itemName, x + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // Current state with color coding
        Color stateColor = getStateColor(currentState);
        g.setColor(stateColor);
        g.drawString("State: " + currentState, x + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // Current action
        g.setColor(LIGHT_GRAY);
        g.setFont(SMALL_FONT);
        String action = BotUtils.truncate(stats.currentAction, 25);
        g.drawString(action, x + OVERLAY_PADDING, y);
        y += 15;

        return y;
    }

    /**
     * Draw core statistics with modern styling
     */
    public static int drawCoreStats(Graphics2D g, int x, int y, ScriptStatistics stats) {
        g.setFont(NORMAL_FONT);

        // Alchs completed with modern icon
        g.setColor(TEXT_COLOR);
        g.drawString("🔥 Alchs: " + BotUtils.formatNumber(stats.alchsCompleted),
                x + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // Total profit with enhanced color coding
        Color profitColor = getProfitColor(stats.totalProfit);
        g.setColor(profitColor);
        g.drawString("💰 Profit: " + BotUtils.formatGP(stats.totalProfit),
                x + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // XP gained with magic icon
        g.setColor(MAGIC_PURPLE);
        g.drawString("✨ XP: " + BotUtils.formatNumber(stats.xpGained),
                x + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // Runtime with clock icon
        g.setColor(LIGHT_GRAY);
        g.drawString("⏱️ Runtime: " + BotUtils.formatDuration(stats.scriptRuntime),
                x + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        return y + 8;
    }

    /**
     * Draw performance statistics
     */
    public static int drawPerformanceStats(Graphics2D g, int x, int y, ScriptStatistics stats) {
        g.setFont(SMALL_FONT);
        g.setColor(LIGHT_GRAY);

        // Rates per hour
        g.drawString("Per Hour:", x + OVERLAY_PADDING, y);
        y += 15;

        g.drawString("  Alchs: " + String.format("%.0f", stats.alchsPerHour),
                x + OVERLAY_PADDING, y);
        y += 12;

        g.drawString("  Profit: " + BotUtils.formatGP((int)stats.profitPerHour),
                x + OVERLAY_PADDING, y);
        y += 12;

        g.drawString("  XP: " + String.format("%.0f", stats.xpPerHour),
                x + OVERLAY_PADDING, y);
        y += 15;

        // Success rate
        Color successColor = getSuccessRateColor(stats.successRate);
        g.setColor(successColor);
        g.drawString("Success: " + String.format("%.1f%%", stats.successRate),
                x + OVERLAY_PADDING, y);
        y += 12;

        return y + 5;
    }

    /**
     * Draw market information
     */
    public static int drawMarketInfo(Graphics2D g, int x, int y, ScriptStatistics stats) {
        if (stats.currentBuyPrice > 0) {
            g.setFont(SMALL_FONT);
            g.setColor(LIGHT_GRAY);

            g.drawString("Market Data:", x + OVERLAY_PADDING, y);
            y += 15;

            g.drawString("  Buy: " + BotUtils.formatGP(stats.currentBuyPrice),
                    x + OVERLAY_PADDING, y);
            y += 12;

            if (stats.currentAlchValue > 0) {
                g.drawString("  Alch: " + BotUtils.formatGP(stats.currentAlchValue),
                        x + OVERLAY_PADDING, y);
                y += 12;
            }

            // Profit per item
            int profitPerItem = stats.currentAlchValue - stats.currentBuyPrice - 250; // Nature rune cost
            Color profitColor = getProfitColor(profitPerItem);
            g.setColor(profitColor);
            g.drawString("  Profit/Item: " + BotUtils.formatGP(profitPerItem),
                    x + OVERLAY_PADDING, y);
            y += 15;
        }

        return y;
    }

    // ===========================================
    // VISUAL ELEMENTS
    // ===========================================

    /**
     * Draw progress bar
     */
    public static int drawProgressBar(Graphics2D g, int x, int y, int width,
                                     ScriptStatistics stats) {
        if (stats.alchsCompleted > 0) {
            int barWidth = width - (OVERLAY_PADDING * 2);
            int barHeight = 6;

            // Background
            g.setColor(DARK_PROGRESS_BG);
            g.fillRoundRect(x + OVERLAY_PADDING, y, barWidth, barHeight, 3, 3);

            // Progress (based on time running, showing activity)
            double progress = (System.currentTimeMillis() % 10000) / 10000.0; // Animated progress
            int progressWidth = (int)(barWidth * progress);

            g.setColor(ACCENT_COLOR);
            g.fillRoundRect(x + OVERLAY_PADDING, y, progressWidth, barHeight, 3, 3);

            y += barHeight + 10;
        }

        return y;
    }

    /**
     * Draw status indicators
     */
    public static void drawStatusIndicators(Graphics2D g, int x, int y, int width,
                                           ScriptStatistics stats) {
        int indicatorX = x + width - 20;
        int indicatorY = y + 10;

        // Running indicator
        Color statusColor = getStatusIndicatorColor(stats.isRunning);
        g.setColor(statusColor);
        g.fillOval(indicatorX, indicatorY, INDICATOR_SIZE, INDICATOR_SIZE);

        // Error indicator
        if (stats.errorsEncountered > 0) {
            indicatorY += INDICATOR_SIZE + 5;
            g.setColor(ERROR_COLOR);
            g.fillOval(indicatorX, indicatorY, INDICATOR_SIZE, INDICATOR_SIZE);
        }
    }

    // ===========================================
    // HEIGHT CALCULATION
    // ===========================================

    /**
     * Calculate total overlay height with modern styling
     */
    public static int calculateOverlayHeight(ScriptStatistics stats) {
        int baseHeight = 220; // Increased base height for modern styling

        if (stats.currentBuyPrice > 0) {
            baseHeight += 80; // Add space for market data
        }

        if (stats.alchsCompleted > 0) {
            baseHeight += 25; // Add space for progress bar
        }

        if (stats.errorsEncountered > 0) {
            baseHeight += 30; // Add space for error information
        }

        return baseHeight;
    }
}
