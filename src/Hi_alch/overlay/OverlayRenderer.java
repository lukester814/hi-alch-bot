package Hi_alch.overlay;

import Hi_alch.BotUtils;
import java.awt.*;
import static Hi_alch.overlay.OverlayStyleManager.*;
import static Hi_alch.overlay.OverlayDrawingUtils.*;

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
 *
 * REFACTORED: Now uses OverlayStyleManager and OverlayDrawingUtils
 */
public class OverlayRenderer {

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
        int overlayHeight = calculateOverlayHeight(statistics);

        // Draw main overlay background
        drawBackground(g, overlayX, overlayY, OVERLAY_WIDTH, overlayHeight);

        // Draw content
        int currentY = overlayY + OVERLAY_PADDING;

        currentY = drawTitle(g, overlayX, currentY, OVERLAY_WIDTH);
        currentY = drawState(g, overlayX, currentY, currentItemName, currentState, statistics);
        currentY = drawSeparator(g, overlayX, currentY, OVERLAY_WIDTH);
        currentY = drawCoreStats(g, overlayX, currentY, statistics);
        currentY = drawSeparator(g, overlayX, currentY, OVERLAY_WIDTH);
        currentY = drawPerformanceStats(g, overlayX, currentY, statistics);
        currentY = drawSeparator(g, overlayX, currentY, OVERLAY_WIDTH);
        currentY = drawMarketInfo(g, overlayX, currentY, statistics);
        currentY = drawProgressBar(g, overlayX, currentY, OVERLAY_WIDTH, statistics);

        // Draw status indicators
        drawStatusIndicators(g, overlayX, overlayY, OVERLAY_WIDTH, statistics);
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
