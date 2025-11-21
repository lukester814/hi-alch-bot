package Hi_alch;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Professional Overlay Renderer for Bot Statistics
 *
 * Features:
 * - Modern overlay design with transparent backgrounds
 * - Real-time statistics display
 * - Progress bars and visual indicators
 * - Performance metrics and rates
 * - Customizable positioning and colors
 * - Memory and system information
 *
 * REUSABLE: Perfect overlay system for any DreamBot script!
 */
public class OverlayRenderer {

    // ===========================================
    // CONFIGURATION & STYLING
    // ===========================================

    // Colors - Modern dark theme
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 200);
    private static final Color BORDER_COLOR = new Color(64, 128, 255, 220);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(64, 128, 255);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color ERROR_COLOR = new Color(244, 67, 54);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);

    // Fonts - Larger for better visibility
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 13);
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 11);

    // Layout - Modern chat-covering overlay
    private static final int OVERLAY_WIDTH = 280;
    private static final int OVERLAY_PADDING = 15;
    private static final int LINE_HEIGHT = 20;

    // ===========================================
    // STATE & CONFIGURATION
    // ===========================================

    private String currentItemName = "";
    private int currentItemId = -1;
    private boolean showOverlay = true;
    private ScriptStatistics statistics;

    // Position - Chat area positioning
    private int overlayX = 15;
    private int overlayY = 350; // Chat area Y position

    // ===========================================
    // SCRIPT STATISTICS CLASS
    // ===========================================

    /**
     * Comprehensive statistics tracking class
     */
    public static class ScriptStatistics {
        // Session information
        public long sessionStartTime = 0;
        public long scriptRuntime = 0;
        public boolean isRunning = false;

        // Core statistics
        public int alchsCompleted = 0;
        public int totalProfit = 0;
        public int xpGained = 0;
        public int itemsBought = 0;
        public int itemsSold = 0;

        // Performance metrics
        public double alchsPerHour = 0.0;
        public double profitPerHour = 0.0;
        public double xpPerHour = 0.0;

        // Current state
        public String currentState = "Initializing";
        public String currentAction = "Starting up";

        // Error tracking
        public int errorsEncountered = 0;
        public String lastError = "";

        // Market data
        public int currentBuyPrice = 0;
        public int currentSellPrice = 0;
        public int currentAlchValue = 0;

        // Efficiency metrics
        public long averageAlchTime = 0;
        public double successRate = 100.0;
        public int consecutiveSuccesses = 0;

        // Resources
        public int natureRunesUsed = 0;
        public int natureRunesRemaining = 0;
        public int itemsRemaining = 0;

        /**
         * Calculate derived statistics based on current data
         */
        public void calculateDerivedStats() {
            if (scriptRuntime > 0) {
                double hoursRunning = scriptRuntime / 3600000.0; // Convert ms to hours

                alchsPerHour = alchsCompleted / hoursRunning;
                profitPerHour = totalProfit / hoursRunning;
                xpPerHour = xpGained / hoursRunning;

                // Calculate success rate
                if (alchsCompleted + errorsEncountered > 0) {
                    successRate = ((double) alchsCompleted / (alchsCompleted + errorsEncountered)) * 100.0;
                }

                // Calculate average alch time
                if (alchsCompleted > 0) {
                    averageAlchTime = scriptRuntime / alchsCompleted;
                }
            }
        }

        /**
         * Reset all statistics
         */
        public void reset() {
            sessionStartTime = System.currentTimeMillis();
            scriptRuntime = 0;
            isRunning = false;

            alchsCompleted = 0;
            totalProfit = 0;
            xpGained = 0;
            itemsBought = 0;
            itemsSold = 0;

            alchsPerHour = 0.0;
            profitPerHour = 0.0;
            xpPerHour = 0.0;

            currentState = "Initializing";
            currentAction = "Starting up";

            errorsEncountered = 0;
            lastError = "";

            currentBuyPrice = 0;
            currentSellPrice = 0;
            currentAlchValue = 0;

            averageAlchTime = 0;
            successRate = 100.0;
            consecutiveSuccesses = 0;

            natureRunesUsed = 0;
            natureRunesRemaining = 0;
            itemsRemaining = 0;
        }

        /**
         * Get formatted statistics summary
         */
        public String getSummary() {
            return String.format("Alchs: %d | Profit: %s | XP: %s | Runtime: %s",
                    alchsCompleted,
                    BotUtils.formatGP(totalProfit),
                    BotUtils.formatNumber(xpGained),
                    BotUtils.formatDuration(scriptRuntime));
        }
    }

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public OverlayRenderer() {
        this.statistics = new ScriptStatistics();

        // Auto-position over chat area on initialization
        positionOverChatArea();

        BotUtils.log("🎨 OverlayRenderer initialized with modern styling");
        BotUtils.log("📍 Auto-positioned over chat area: (" + overlayX + ", " + overlayY + ")");
    }

    // ===========================================
    // CONFIGURATION METHODS
    // ===========================================

    /**
     * Configure the overlay renderer with automatic positioning
     */
    public void configure(String itemName, int itemId, boolean showOverlay) {
        this.currentItemName = itemName;
        this.currentItemId = itemId;
        this.showOverlay = showOverlay;

        // Auto-position over chat area
        positionOverChatArea();

        BotUtils.log("🎨 Overlay configured for: " + itemName);
        BotUtils.log("📍 Positioned at: (" + overlayX + ", " + overlayY + ")");
    }

    /**
     * Position overlay over chat area automatically
     */
    public void positionOverChatArea() {
        // Standard OSRS client chat area positioning
        this.overlayX = 15;

        // Try to detect screen size and position accordingly
        try {
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

            if (screenSize.height >= 1080) {
                // High resolution - position in lower area
                this.overlayY = (int)(screenSize.height * 0.65); // 65% down the screen
            } else if (screenSize.height >= 900) {
                // Medium resolution
                this.overlayY = (int)(screenSize.height * 0.60); // 60% down
            } else {
                // Lower resolution - position higher up
                this.overlayY = (int)(screenSize.height * 0.40); // 40% down
            }

            BotUtils.log("🖥️ Screen: " + screenSize.width + "x" + screenSize.height +
                    " | Overlay: (" + overlayX + ", " + overlayY + ")");

        } catch (Exception e) {
            // Fallback positioning
            this.overlayY = 350;
            BotUtils.log("⚠️ Using fallback overlay position");
        }
    }

    /**
     * Initialize with statistics object
     */
    public void initialize(ScriptStatistics stats) {
        if (stats != null) {
            this.statistics = stats;
        }
        BotUtils.log("🎨 Overlay initialized with statistics");
    }

    /**
     * Update statistics
     */
    public void updateStatistics(ScriptStatistics stats) {
        if (stats != null) {
            this.statistics = stats;
        }
    }

    // ===========================================
    // RENDERING METHODS
    // ===========================================

    /**
     * Main render method - called from onPaint
     */
    public void render(Graphics2D g, ScriptStatistics stats, String currentState) {
        if (!showOverlay || g == null) {
            return;
        }

        // Update statistics
        if (stats != null) {
            this.statistics = stats;
        }

        // Set high quality rendering
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Calculate overlay height dynamically
        int overlayHeight = calculateOverlayHeight();

        // Draw main overlay background
        drawBackground(g, overlayX, overlayY, OVERLAY_WIDTH, overlayHeight);

        // Draw content
        int currentY = overlayY + OVERLAY_PADDING;

        currentY = drawTitle(g, currentY);
        currentY = drawState(g, currentY, currentState);
        currentY = drawSeparator(g, currentY);
        currentY = drawCoreStats(g, currentY);
        currentY = drawSeparator(g, currentY);
        currentY = drawPerformanceStats(g, currentY);
        currentY = drawSeparator(g, currentY);
        currentY = drawMarketInfo(g, currentY);
        currentY = drawProgressBar(g, currentY);

        // Draw status indicators
        drawStatusIndicators(g);
    }

    /**
     * Draw overlay background with modern styling
     */
    private void drawBackground(Graphics2D g, int x, int y, int width, int height) {
        // Drop shadow effect
        g.setColor(new Color(0, 0, 0, 50));
        g.fillRoundRect(x + 3, y + 3, width, height, 12, 12);

        // Main background with gradient effect
        GradientPaint gradient = new GradientPaint(
                x, y, new Color(20, 20, 30, 220),
                x, y + height, new Color(40, 40, 60, 200)
        );
        g.setPaint(gradient);
        g.fillRoundRect(x, y, width, height, 12, 12);

        // Modern border with glow effect
        g.setStroke(new BasicStroke(2));
        g.setColor(BORDER_COLOR);
        g.drawRoundRect(x, y, width, height, 12, 12);

        // Inner border for depth
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(255, 255, 255, 30));
        g.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 10, 10);
    }

    /**
     * Draw title section with modern styling
     */
    private int drawTitle(Graphics2D g, int y) {
        g.setFont(TITLE_FONT);

        // Modern gradient title
        GradientPaint titleGradient = new GradientPaint(
                overlayX, y, new Color(100, 150, 255),
                overlayX + OVERLAY_WIDTH, y, new Color(150, 100, 255)
        );
        g.setPaint(titleGradient);

        String title = "⚡ High Alchemy Bot v2.0";
        drawCenteredText(g, title, y);

        // Underline effect
        y += 5;
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(1));
        g.drawLine(overlayX + OVERLAY_PADDING, y,
                overlayX + OVERLAY_WIDTH - OVERLAY_PADDING, y);

        return y + LINE_HEIGHT;
    }

    /**
     * Draw current state section
     */
    private int drawState(Graphics2D g, int y, String currentState) {
        g.setFont(NORMAL_FONT);

        // Current item
        g.setColor(TEXT_COLOR);
        g.drawString("Item: " + currentItemName, overlayX + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // Current state with color coding
        Color stateColor = getStateColor(currentState);
        g.setColor(stateColor);
        g.drawString("State: " + currentState, overlayX + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // Current action
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(SMALL_FONT);
        String action = BotUtils.truncate(statistics.currentAction, 25);
        g.drawString(action, overlayX + OVERLAY_PADDING, y);
        y += 15;

        return y;
    }

    /**
     * Draw core statistics with modern styling
     */
    private int drawCoreStats(Graphics2D g, int y) {
        g.setFont(NORMAL_FONT);

        // Alchs completed with modern icon
        g.setColor(TEXT_COLOR);
        g.drawString("🔥 Alchs: " + BotUtils.formatNumber(statistics.alchsCompleted),
                overlayX + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // Total profit with enhanced color coding
        Color profitColor = statistics.totalProfit >= 0 ? SUCCESS_COLOR : ERROR_COLOR;
        g.setColor(profitColor);
        g.drawString("💰 Profit: " + BotUtils.formatGP(statistics.totalProfit),
                overlayX + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // XP gained with magic icon
        g.setColor(new Color(150, 100, 255)); // Magic purple
        g.drawString("✨ XP: " + BotUtils.formatNumber(statistics.xpGained),
                overlayX + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        // Runtime with clock icon
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("⏱️ Runtime: " + BotUtils.formatDuration(statistics.scriptRuntime),
                overlayX + OVERLAY_PADDING, y);
        y += LINE_HEIGHT;

        return y + 8;
    }

    /**
     * Draw performance statistics
     */
    private int drawPerformanceStats(Graphics2D g, int y) {
        g.setFont(SMALL_FONT);
        g.setColor(Color.LIGHT_GRAY);

        // Rates per hour
        g.drawString("Per Hour:", overlayX + OVERLAY_PADDING, y);
        y += 15;

        g.drawString("  Alchs: " + String.format("%.0f", statistics.alchsPerHour),
                overlayX + OVERLAY_PADDING, y);
        y += 12;

        g.drawString("  Profit: " + BotUtils.formatGP((int)statistics.profitPerHour),
                overlayX + OVERLAY_PADDING, y);
        y += 12;

        g.drawString("  XP: " + String.format("%.0f", statistics.xpPerHour),
                overlayX + OVERLAY_PADDING, y);
        y += 15;

        // Success rate
        Color successColor = statistics.successRate >= 95 ? ACCENT_COLOR :
                statistics.successRate >= 90 ? WARNING_COLOR : ERROR_COLOR;
        g.setColor(successColor);
        g.drawString("Success: " + String.format("%.1f%%", statistics.successRate),
                overlayX + OVERLAY_PADDING, y);
        y += 12;

        return y + 5;
    }

    /**
     * Draw market information
     */
    private int drawMarketInfo(Graphics2D g, int y) {
        if (statistics.currentBuyPrice > 0) {
            g.setFont(SMALL_FONT);
            g.setColor(Color.LIGHT_GRAY);

            g.drawString("Market Data:", overlayX + OVERLAY_PADDING, y);
            y += 15;

            g.drawString("  Buy: " + BotUtils.formatGP(statistics.currentBuyPrice),
                    overlayX + OVERLAY_PADDING, y);
            y += 12;

            if (statistics.currentAlchValue > 0) {
                g.drawString("  Alch: " + BotUtils.formatGP(statistics.currentAlchValue),
                        overlayX + OVERLAY_PADDING, y);
                y += 12;
            }

            // Profit per item
            int profitPerItem = statistics.currentAlchValue - statistics.currentBuyPrice - 250; // Nature rune cost
            Color profitColor = profitPerItem >= 0 ? ACCENT_COLOR : ERROR_COLOR;
            g.setColor(profitColor);
            g.drawString("  Profit/Item: " + BotUtils.formatGP(profitPerItem),
                    overlayX + OVERLAY_PADDING, y);
            y += 15;
        }

        return y;
    }

    /**
     * Draw progress bar
     */
    private int drawProgressBar(Graphics2D g, int y) {
        if (statistics.alchsCompleted > 0) {
            // Simple progress indication based on session progress
            int barWidth = OVERLAY_WIDTH - (OVERLAY_PADDING * 2);
            int barHeight = 6;

            // Background
            g.setColor(new Color(50, 50, 50));
            g.fillRoundRect(overlayX + OVERLAY_PADDING, y, barWidth, barHeight, 3, 3);

            // Progress (based on time running, showing activity)
            double progress = (System.currentTimeMillis() % 10000) / 10000.0; // Animated progress
            int progressWidth = (int)(barWidth * progress);

            g.setColor(ACCENT_COLOR);
            g.fillRoundRect(overlayX + OVERLAY_PADDING, y, progressWidth, barHeight, 3, 3);

            y += barHeight + 10;
        }

        return y;
    }

    /**
     * Draw status indicators
     */
    private void drawStatusIndicators(Graphics2D g) {
        int indicatorSize = 8;
        int indicatorX = overlayX + OVERLAY_WIDTH - 20;
        int indicatorY = overlayY + 10;

        // Running indicator
        Color statusColor = statistics.isRunning ? ACCENT_COLOR : ERROR_COLOR;
        g.setColor(statusColor);
        g.fillOval(indicatorX, indicatorY, indicatorSize, indicatorSize);

        // Error indicator
        if (statistics.errorsEncountered > 0) {
            indicatorY += indicatorSize + 5;
            g.setColor(ERROR_COLOR);
            g.fillOval(indicatorX, indicatorY, indicatorSize, indicatorSize);
        }
    }

    /**
     * Draw separator line
     */
    private int drawSeparator(Graphics2D g, int y) {
        g.setColor(BORDER_COLOR);
        g.drawLine(overlayX + OVERLAY_PADDING, y,
                overlayX + OVERLAY_WIDTH - OVERLAY_PADDING, y);
        return y + 8;
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Draw centered text
     */
    private void drawCenteredText(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        int textX = overlayX + (OVERLAY_WIDTH - (int)rect.getWidth()) / 2;
        g.drawString(text, textX, y);
    }

    /**
     * Calculate total overlay height with modern styling
     */
    private int calculateOverlayHeight() {
        int baseHeight = 220; // Increased base height for modern styling

        if (statistics.currentBuyPrice > 0) {
            baseHeight += 80; // Add space for market data
        }

        if (statistics.alchsCompleted > 0) {
            baseHeight += 25; // Add space for progress bar
        }

        if (statistics.errorsEncountered > 0) {
            baseHeight += 30; // Add space for error information
        }

        return baseHeight;
    }

    /**
     * Get color for current state
     */
    private Color getStateColor(String state) {
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

    // ===========================================
    // PUBLIC INTERFACE
    // ===========================================

    /**
     * Toggle overlay visibility
     */
    public void setOverlayVisible(boolean visible) {
        this.showOverlay = visible;
    }

    /**
     * Set overlay position manually
     */
    public void setPosition(int x, int y) {
        this.overlayX = x;
        this.overlayY = y;
        BotUtils.log("📍 Overlay position manually set to: (" + x + ", " + y + ")");
    }

    /**
     * Reset position to auto-chat area
     */
    public void resetTochatPosition() {
        positionOverChatArea();
        BotUtils.log("🔄 Overlay position reset to chat area");
    }

    /**
     * Get current statistics
     */
    public ScriptStatistics getStatistics() {
        return statistics;
    }

    /**
     * Update current item being processed
     */
    public void updateCurrentItem(String itemName, int itemId) {
        this.currentItemName = itemName;
        this.currentItemId = itemId;
    }
}