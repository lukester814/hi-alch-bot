package Hi_alch;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

// Import required Hi_alch classes
import Hi_alch.OverlayRenderer.ScriptStatistics;
import Hi_alch.BotUtils;

/**
 * Modern Paint Renderer with Tabbed Interface
 *
 * Features:
 * - Beautiful tabbed interface for organizing statistics
 * - Modern glassmorphism design with transparency
 * - Smooth gradients and rounded corners
 * - Interactive tabs (Overview, Stats, Profit, Performance)
 * - Compact chatbox overlay design
 * - Click detection for tab switching
 * - Real-time animations and transitions
 *
 * REUSABLE: Perfect modern overlay for any DreamBot script!
 */
public class ModernPaintRenderer {

    // ===========================================
    // TAB DEFINITIONS
    // ===========================================

    public enum PaintTab {
        OVERVIEW("Overview"),
        STATISTICS("Statistics"),
        PROFIT("Profit"),
        PERFORMANCE("Performance");

        private final String displayName;

        PaintTab(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // ===========================================
    // STYLING CONSTANTS
    // ===========================================

    // Colors - Modern dark glassmorphism theme
    private static final Color BACKGROUND_PRIMARY = new Color(17, 24, 39, 245);
    private static final Color BACKGROUND_SECONDARY = new Color(31, 41, 55, 235);
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color ACCENT_HOVER = new Color(96, 165, 250);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(251, 191, 36);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175);
    private static final Color BORDER_COLOR = new Color(75, 85, 99, 200);
    private static final Color TAB_ACTIVE = new Color(59, 130, 246, 180);
    private static final Color TAB_INACTIVE = new Color(55, 65, 81, 120);

    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font TAB_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 10);

    // Layout dimensions
    private static final int PAINT_WIDTH = 320;
    private static final int PAINT_HEIGHT = 240;
    private static final int PAINT_X = 10;
    private static final int PAINT_Y = 350; // Chatbox area
    private static final int CORNER_RADIUS = 12;
    private static final int TAB_HEIGHT = 35;
    private static final int PADDING = 15;

    // ===========================================
    // STATE
    // ===========================================

    private PaintTab activeTab = PaintTab.OVERVIEW;
    private ScriptStatistics currentStats;
    private long lastTabSwitch = 0;
    private boolean enabled = true;

    // Mouse tracking for tab clicks
    private int lastMouseX = -1;
    private int lastMouseY = -1;

    // ===========================================
    // MAIN RENDER METHOD
    // ===========================================

    /**
     * Main render method called by onPaint
     */
    public void render(Graphics2D g, ScriptStatistics stats, String status) {
        if (!enabled || g == null) return;

        this.currentStats = stats;

        // Enable anti-aliasing for smooth graphics
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw main container
        drawMainContainer(g);

        // Draw tab bar
        drawTabBar(g);

        // Draw active tab content
        drawTabContent(g, status);

        // Draw footer
        drawFooter(g);
    }

    // ===========================================
    // CONTAINER DRAWING
    // ===========================================

    /**
     * Draw the main container with modern styling
     */
    private void drawMainContainer(Graphics2D g) {
        // Main background with rounded corners
        RoundRectangle2D mainRect = new RoundRectangle2D.Double(
            PAINT_X, PAINT_Y, PAINT_WIDTH, PAINT_HEIGHT, CORNER_RADIUS, CORNER_RADIUS
        );

        // Gradient background
        GradientPaint gradient = new GradientPaint(
            PAINT_X, PAINT_Y, BACKGROUND_PRIMARY,
            PAINT_X, PAINT_Y + PAINT_HEIGHT, BACKGROUND_SECONDARY
        );
        g.setPaint(gradient);
        g.fill(mainRect);

        // Border
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(2f));
        g.draw(mainRect);

        // Subtle inner glow
        g.setColor(new Color(255, 255, 255, 10));
        g.setStroke(new BasicStroke(1f));
        RoundRectangle2D innerRect = new RoundRectangle2D.Double(
            PAINT_X + 2, PAINT_Y + 2, PAINT_WIDTH - 4, PAINT_HEIGHT - 4, CORNER_RADIUS - 2, CORNER_RADIUS - 2
        );
        g.draw(innerRect);
    }

    // ===========================================
    // TAB BAR DRAWING
    // ===========================================

    /**
     * Draw the tab bar with all tabs
     */
    private void drawTabBar(Graphics2D g) {
        int tabWidth = (PAINT_WIDTH - (PADDING * 2)) / PaintTab.values().length;
        int tabY = PAINT_Y + PADDING;

        for (int i = 0; i < PaintTab.values().length; i++) {
            PaintTab tab = PaintTab.values()[i];
            int tabX = PAINT_X + PADDING + (i * tabWidth);

            drawTab(g, tab, tabX, tabY, tabWidth, tab == activeTab);
        }
    }

    /**
     * Draw individual tab
     */
    private void drawTab(Graphics2D g, PaintTab tab, int x, int y, int width, boolean isActive) {
        // Tab background
        RoundRectangle2D tabRect = new RoundRectangle2D.Double(
            x, y, width - 2, TAB_HEIGHT, 8, 8
        );

        if (isActive) {
            // Active tab styling with gradient
            GradientPaint tabGradient = new GradientPaint(
                x, y, TAB_ACTIVE,
                x, y + TAB_HEIGHT, new Color(37, 99, 235, 180)
            );
            g.setPaint(tabGradient);
            g.fill(tabRect);

            // Active tab border
            g.setColor(ACCENT_COLOR);
            g.setStroke(new BasicStroke(2f));
            g.draw(tabRect);
        } else {
            // Inactive tab styling
            g.setColor(TAB_INACTIVE);
            g.fill(tabRect);

            // Subtle border
            g.setColor(new Color(75, 85, 99, 80));
            g.setStroke(new BasicStroke(1f));
            g.draw(tabRect);
        }

        // Tab text
        g.setFont(TAB_FONT);
        g.setColor(isActive ? TEXT_PRIMARY : TEXT_SECONDARY);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(tab.getDisplayName());
        int textX = x + (width - textWidth) / 2;
        int textY = y + ((TAB_HEIGHT + fm.getAscent()) / 2) - 2;

        g.drawString(tab.getDisplayName(), textX, textY);
    }

    // ===========================================
    // TAB CONTENT DRAWING
    // ===========================================

    /**
     * Draw content for the active tab
     */
    private void drawTabContent(Graphics2D g, String status) {
        int contentY = PAINT_Y + PADDING + TAB_HEIGHT + 10;

        switch (activeTab) {
            case OVERVIEW:
                drawOverviewTab(g, contentY, status);
                break;
            case STATISTICS:
                drawStatisticsTab(g, contentY);
                break;
            case PROFIT:
                drawProfitTab(g, contentY);
                break;
            case PERFORMANCE:
                drawPerformanceTab(g, contentY);
                break;
        }
    }

    /**
     * Draw Overview tab content
     */
    private void drawOverviewTab(Graphics2D g, int startY, String status) {
        int y = startY;
        int lineHeight = 22;

        // Status
        drawLabelValue(g, "Status:", status, y, getStatusColor(status));
        y += lineHeight;

        // Runtime
        String runtime = currentStats != null ? BotUtils.formatDuration(currentStats.scriptRuntime) : "00:00:00";
        drawLabelValue(g, "Runtime:", runtime, y, TEXT_PRIMARY);
        y += lineHeight;

        // Current action
        String action = currentStats != null ? currentStats.currentAction : "Idle";
        drawLabelValue(g, "Action:", action, y, ACCENT_COLOR);
        y += lineHeight;

        // Alchs completed
        int alchs = currentStats != null ? currentStats.alchsCompleted : 0;
        drawLabelValue(g, "Alchs:", String.valueOf(alchs), y, SUCCESS_COLOR);
        y += lineHeight;

        // Total profit
        int profit = currentStats != null ? currentStats.totalProfit : 0;
        String profitStr = (profit >= 0 ? "+" : "") + BotUtils.formatNumber(profit) + " GP";
        drawLabelValue(g, "Profit:", profitStr, y, profit >= 0 ? SUCCESS_COLOR : ERROR_COLOR);
        y += lineHeight;

        // XP gained
        int xp = currentStats != null ? currentStats.xpGained : 0;
        drawLabelValue(g, "XP Gained:", BotUtils.formatNumber(xp) + " XP", y, new Color(147, 51, 234));
    }

    /**
     * Draw Statistics tab content
     */
    private void drawStatisticsTab(Graphics2D g, int startY) {
        int y = startY;
        int lineHeight = 22;

        if (currentStats == null) {
            drawCenteredText(g, "No statistics available", startY + 50, TEXT_SECONDARY);
            return;
        }

        // Alchs per hour
        drawLabelValue(g, "Alchs/Hour:", String.format("%.0f", currentStats.alchsPerHour), y, SUCCESS_COLOR);
        y += lineHeight;

        // XP per hour
        drawLabelValue(g, "XP/Hour:", BotUtils.formatNumber((int)currentStats.xpPerHour), y, new Color(147, 51, 234));
        y += lineHeight;

        // Profit per hour
        int profitHr = (int)currentStats.profitPerHour;
        String profitStr = (profitHr >= 0 ? "+" : "") + BotUtils.formatNumber(profitHr) + " GP/hr";
        drawLabelValue(g, "Profit/Hour:", profitStr, y, profitHr >= 0 ? SUCCESS_COLOR : ERROR_COLOR);
        y += lineHeight;

        // Average alch time
        if (currentStats.averageAlchTime > 0) {
            String avgTime = String.format("%.1fs", currentStats.averageAlchTime / 1000.0);
            drawLabelValue(g, "Avg Alch Time:", avgTime, y, ACCENT_COLOR);
            y += lineHeight;
        }

        // Success rate
        drawLabelValue(g, "Success Rate:", String.format("%.1f%%", currentStats.successRate), y, SUCCESS_COLOR);
        y += lineHeight;

        // Items bought/sold
        drawLabelValue(g, "Items Bought:", String.valueOf(currentStats.itemsBought), y, TEXT_PRIMARY);
    }

    /**
     * Draw Profit tab content
     */
    private void drawProfitTab(Graphics2D g, int startY) {
        int y = startY;
        int lineHeight = 22;

        if (currentStats == null) {
            drawCenteredText(g, "No profit data available", startY + 50, TEXT_SECONDARY);
            return;
        }

        // Current buy price
        if (currentStats.currentBuyPrice > 0) {
            drawLabelValue(g, "Buy Price:", BotUtils.formatNumber(currentStats.currentBuyPrice) + " GP", y, WARNING_COLOR);
            y += lineHeight;
        }

        // Current alch value
        if (currentStats.currentAlchValue > 0) {
            drawLabelValue(g, "Alch Value:", BotUtils.formatNumber(currentStats.currentAlchValue) + " GP", y, ACCENT_COLOR);
            y += lineHeight;
        }

        // Profit per item
        if (currentStats.currentBuyPrice > 0 && currentStats.currentAlchValue > 0) {
            int profitPerItem = currentStats.currentAlchValue - currentStats.currentBuyPrice - 220;
            String profitStr = (profitPerItem >= 0 ? "+" : "") + BotUtils.formatNumber(profitPerItem) + " GP";
            drawLabelValue(g, "Profit/Item:", profitStr, y, profitPerItem >= 0 ? SUCCESS_COLOR : ERROR_COLOR);
            y += lineHeight;
        }

        // Total profit
        int totalProfit = currentStats.totalProfit;
        String totalStr = (totalProfit >= 0 ? "+" : "") + BotUtils.formatNumber(totalProfit) + " GP";
        drawLabelValue(g, "Total Profit:", totalStr, y, totalProfit >= 0 ? SUCCESS_COLOR : ERROR_COLOR);
        y += lineHeight;

        // Profit per hour
        int profitHr = (int)currentStats.profitPerHour;
        String profitHrStr = (profitHr >= 0 ? "+" : "") + BotUtils.formatNumber(profitHr) + " GP/hr";
        drawLabelValue(g, "Profit/Hour:", profitHrStr, y, profitHr >= 0 ? SUCCESS_COLOR : ERROR_COLOR);
        y += lineHeight;

        // Draw profit progress bar
        if (totalProfit > 0) {
            y += 10;
            drawProfitProgressBar(g, y, totalProfit);
        }
    }

    /**
     * Draw Performance tab content
     */
    private void drawPerformanceTab(Graphics2D g, int startY) {
        int y = startY;
        int lineHeight = 22;

        if (currentStats == null) {
            drawCenteredText(g, "No performance data available", startY + 50, TEXT_SECONDARY);
            return;
        }

        // Runtime
        drawLabelValue(g, "Runtime:", BotUtils.formatDuration(currentStats.scriptRuntime), y, TEXT_PRIMARY);
        y += lineHeight;

        // Success rate
        drawLabelValue(g, "Success Rate:", String.format("%.1f%%", currentStats.successRate), y, SUCCESS_COLOR);
        y += lineHeight;

        // Consecutive successes
        drawLabelValue(g, "Streak:", String.valueOf(currentStats.consecutiveSuccesses), y, new Color(147, 51, 234));
        y += lineHeight;

        // Errors encountered
        Color errorColor = currentStats.errorsEncountered > 0 ? ERROR_COLOR : SUCCESS_COLOR;
        drawLabelValue(g, "Errors:", String.valueOf(currentStats.errorsEncountered), y, errorColor);
        y += lineHeight;

        // Last error
        if (currentStats.lastError != null && !currentStats.lastError.isEmpty()) {
            y += 5;
            g.setFont(SMALL_FONT);
            g.setColor(ERROR_COLOR);
            g.drawString("Last Error:", PAINT_X + PADDING, y);
            y += 15;

            String errorText = currentStats.lastError;
            if (errorText.length() > 35) {
                errorText = errorText.substring(0, 35) + "...";
            }
            g.setColor(TEXT_SECONDARY);
            g.drawString(errorText, PAINT_X + PADDING, y);
        }

        // Performance chart (visual indicator)
        y += 20;
        drawPerformanceIndicator(g, y);
    }

    // ===========================================
    // HELPER DRAWING METHODS
    // ===========================================

    /**
     * Draw label-value pair
     */
    private void drawLabelValue(Graphics2D g, String label, String value, int y, Color valueColor) {
        g.setFont(LABEL_FONT);
        g.setColor(TEXT_SECONDARY);
        g.drawString(label, PAINT_X + PADDING, y);

        g.setFont(VALUE_FONT);
        g.setColor(valueColor);

        FontMetrics fm = g.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        g.drawString(value, PAINT_X + PADDING + labelWidth + 10, y);
    }

    /**
     * Draw centered text
     */
    private void drawCenteredText(Graphics2D g, String text, int y, Color color) {
        g.setFont(LABEL_FONT);
        g.setColor(color);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = PAINT_X + (PAINT_WIDTH - textWidth) / 2;

        g.drawString(text, x, y);
    }

    /**
     * Draw profit progress bar
     */
    private void drawProfitProgressBar(Graphics2D g, int y, int totalProfit) {
        int barWidth = PAINT_WIDTH - (PADDING * 2);
        int barHeight = 20;
        int barX = PAINT_X + PADDING;

        // Background
        g.setColor(new Color(55, 65, 81));
        g.fillRoundRect(barX, y, barWidth, barHeight, 10, 10);

        // Progress (calculate based on a goal, e.g., 100k)
        int goal = 100000;
        double progress = Math.min(1.0, (double)totalProfit / goal);
        int progressWidth = (int)(barWidth * progress);

        // Gradient fill
        GradientPaint progressGradient = new GradientPaint(
            barX, y, new Color(34, 197, 94),
            barX + progressWidth, y, new Color(22, 163, 74)
        );
        g.setPaint(progressGradient);
        g.fillRoundRect(barX, y, progressWidth, barHeight, 10, 10);

        // Text overlay
        g.setFont(SMALL_FONT);
        g.setColor(TEXT_PRIMARY);
        String progressText = BotUtils.formatNumber(totalProfit) + " / " + BotUtils.formatNumber(goal) + " GP";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(progressText);
        int textX = barX + (barWidth - textWidth) / 2;
        int textY = y + ((barHeight + fm.getAscent()) / 2) - 2;
        g.drawString(progressText, textX, textY);
    }

    /**
     * Draw performance indicator
     */
    private void drawPerformanceIndicator(Graphics2D g, int y) {
        int indicatorSize = 12;
        int spacing = 8;
        int startX = PAINT_X + PADDING;

        g.setFont(SMALL_FONT);
        g.setColor(TEXT_SECONDARY);
        g.drawString("Performance:", startX, y);

        y += 18;

        // Draw 5 circles representing performance levels
        for (int i = 0; i < 5; i++) {
            int x = startX + (i * (indicatorSize + spacing));

            Color circleColor;
            if (currentStats != null && currentStats.successRate >= (i + 1) * 20) {
                circleColor = SUCCESS_COLOR;
            } else {
                circleColor = new Color(75, 85, 99);
            }

            g.setColor(circleColor);
            g.fillOval(x, y, indicatorSize, indicatorSize);

            // Border
            g.setColor(new Color(55, 65, 81));
            g.drawOval(x, y, indicatorSize, indicatorSize);
        }
    }

    // ===========================================
    // FOOTER DRAWING
    // ===========================================

    /**
     * Draw footer with additional info
     */
    private void drawFooter(Graphics2D g) {
        int footerY = PAINT_Y + PAINT_HEIGHT - 20;

        g.setFont(SMALL_FONT);
        g.setColor(TEXT_SECONDARY);

        // Current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(new Date());

        String footerText = "High Alchemy Bot v2.0 | " + currentTime;
        g.drawString(footerText, PAINT_X + PADDING, footerY);

        // Version info on the right
        String versionText = "Click tabs to switch views";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(versionText);
        g.drawString(versionText, PAINT_X + PAINT_WIDTH - PADDING - textWidth, footerY);
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Get color based on status text
     */
    private Color getStatusColor(String status) {
        if (status == null) return TEXT_PRIMARY;

        String lower = status.toLowerCase();
        if (lower.contains("running") || lower.contains("active")) {
            return SUCCESS_COLOR;
        } else if (lower.contains("error") || lower.contains("failed")) {
            return ERROR_COLOR;
        } else if (lower.contains("waiting") || lower.contains("idle")) {
            return WARNING_COLOR;
        }
        return TEXT_PRIMARY;
    }

    // ===========================================
    // TAB SWITCHING
    // ===========================================

    /**
     * Handle mouse click for tab switching
     */
    public void handleMouseClick(int mouseX, int mouseY) {
        // Check if click is within tab bar
        int tabY = PAINT_Y + PADDING;
        if (mouseY < tabY || mouseY > tabY + TAB_HEIGHT) {
            return;
        }

        int tabWidth = (PAINT_WIDTH - (PADDING * 2)) / PaintTab.values().length;

        for (int i = 0; i < PaintTab.values().length; i++) {
            int tabX = PAINT_X + PADDING + (i * tabWidth);

            if (mouseX >= tabX && mouseX <= tabX + tabWidth) {
                activeTab = PaintTab.values()[i];
                lastTabSwitch = System.currentTimeMillis();
                BotUtils.log("Switched to tab: " + activeTab.getDisplayName());
                break;
            }
        }
    }

    /**
     * Switch to next tab
     */
    public void nextTab() {
        int currentIndex = activeTab.ordinal();
        int nextIndex = (currentIndex + 1) % PaintTab.values().length;
        activeTab = PaintTab.values()[nextIndex];
    }

    /**
     * Switch to previous tab
     */
    public void previousTab() {
        int currentIndex = activeTab.ordinal();
        int prevIndex = (currentIndex - 1 + PaintTab.values().length) % PaintTab.values().length;
        activeTab = PaintTab.values()[prevIndex];
    }

    // ===========================================
    // PUBLIC CONFIGURATION
    // ===========================================

    /**
     * Set whether paint is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get current active tab
     */
    public PaintTab getActiveTab() {
        return activeTab;
    }

    /**
     * Set active tab
     */
    public void setActiveTab(PaintTab tab) {
        this.activeTab = tab;
    }
}
