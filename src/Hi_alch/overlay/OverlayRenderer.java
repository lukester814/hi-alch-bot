package Hi_alch.overlay;

import Hi_alch.overlay.*;
import Hi_alch.overlay.ScriptStatistics;
import java.awt.*;
import static Hi_alch.overlay.OverlayConfig.*;
/**
 * Professional Overlay Renderer for Bot Statistics (REFACTORED)
 * Delegates configuration and statistics to specialized classes
 */
public class OverlayRenderer {
    // State
    private String currentItemName = "";
    private int currentItemId = -1;
    private boolean showOverlay = true;
    private ScriptStatistics statistics;
    // Position
    private int overlayX = DEFAULT_X;
    private int overlayY = DEFAULT_Y;
    // ===========================================
    // CONSTRUCTOR
    public OverlayRenderer() {
        this.statistics = new ScriptStatistics();
        positionOverChatArea();
        BotUtils.log("🎨 OverlayRenderer initialized");
    }
    // CONFIGURATION
    public void configure(String itemName, int itemId, boolean showOverlay) {
        this.currentItemName = itemName;
        this.currentItemId = itemId;
        this.showOverlay = showOverlay;
        BotUtils.log("🎨 Overlay configured for: " + itemName);
    public void positionOverChatArea() {
        this.overlayX = DEFAULT_X;
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (screenSize.height >= 1080) {
                this.overlayY = (int)(screenSize.height * 0.65);
            } else if (screenSize.height >= 900) {
                this.overlayY = (int)(screenSize.height * 0.60);
            } else {
                this.overlayY = DEFAULT_Y;
            }
        } catch (Exception e) {
            this.overlayY = DEFAULT_Y;
        }
    public void updateStatistics(ScriptStatistics stats) {
        if (stats != null) {
            this.statistics = stats;
    // RENDERING
    public void render(Graphics2D g, ScriptStatistics stats, String currentState) {
        if (!showOverlay || g == null) {
            return;
        // High quality rendering
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int overlayHeight = calculateOverlayHeight();
        // Draw components
        drawBackground(g, overlayX, overlayY, OVERLAY_WIDTH, overlayHeight);
        int currentY = overlayY + OVERLAY_PADDING;
        currentY = drawTitle(g, currentY);
        currentY = drawState(g, currentY, currentState);
        currentY = drawSeparator(g, currentY);
        currentY = drawCoreStats(g, currentY);
        currentY = drawPerformanceStats(g, currentY);
        currentY = drawMarketInfo(g, currentY);
        currentY = drawProgressBar(g, currentY);
        drawStatusIndicators(g);
    private int calculateOverlayHeight() {
        return OVERLAY_PADDING * 2 + LINE_HEIGHT * 18;
    // DRAWING METHODS
    private void drawBackground(Graphics2D g, int x, int y, int width, int height) {
        // Shadow
        g.setColor(new Color(0, 0, 0, 50));
        g.fillRoundRect(x + 3, y + 3, width, height, 12, 12);
        // Gradient background
        GradientPaint gradient = new GradientPaint(
                x, y, new Color(20, 20, 30, 220),
                x, y + height, new Color(40, 40, 60, 200)
        );
        g.setPaint(gradient);
        g.fillRoundRect(x, y, width, height, 12, 12);
        // Border
        g.setStroke(new BasicStroke(2));
        g.setColor(BORDER_COLOR);
        g.drawRoundRect(x, y, width, height, 12, 12);
        // Inner border
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(255, 255, 255, 30));
        g.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 10, 10);
    private int drawTitle(Graphics2D g, int y) {
        g.setFont(TITLE_FONT);
        GradientPaint titleGradient = new GradientPaint(
                overlayX, y, new Color(100, 150, 255),
                overlayX + OVERLAY_WIDTH, y, new Color(150, 100, 255)
        g.setPaint(titleGradient);
        String title = "⚡ High Alchemy Bot v2.0";
        drawCenteredText(g, title, y);
        return y + LINE_HEIGHT + 5;
    private int drawState(Graphics2D g, int y, String state) {
        g.setFont(NORMAL_FONT);
        g.setColor(statistics.isRunning ? SUCCESS_COLOR : WARNING_COLOR);
        String status = statistics.isRunning ? "● Running" : "○ Stopped";
        drawText(g, status, y);
        g.setColor(TEXT_COLOR);
        drawRightAlignedText(g, state != null ? state : "Ready", y);
        return y + LINE_HEIGHT;
    private int drawSeparator(Graphics2D g, int y) {
        g.setColor(new Color(100, 100, 150, 80));
        g.drawLine(overlayX + OVERLAY_PADDING, y, overlayX + OVERLAY_WIDTH - OVERLAY_PADDING, y);
        return y + 8;
    private int drawCoreStats(Graphics2D g, int y) {
        y = drawStatLine(g, "Alchs:", String.format("%,d", statistics.alchsCompleted), y);
        y = drawStatLine(g, "Profit:", BotUtils.formatGP(statistics.totalProfit), y);
        y = drawStatLine(g, "XP Gained:", String.format("%,d", statistics.xpGained), y);
        y = drawStatLine(g, "Runtime:", BotUtils.formatDuration(statistics.scriptRuntime), y);
        return y;
    private int drawPerformanceStats(Graphics2D g, int y) {
        g.setColor(ACCENT_COLOR);
        y = drawStatLine(g, "Alchs/hr:", String.format("%.1f", statistics.alchsPerHour), y);
        y = drawStatLine(g, "Profit/hr:", BotUtils.formatGP((int)statistics.profitPerHour), y);
        y = drawStatLine(g, "XP/hr:", String.format("%,.0f", statistics.xpPerHour), y);
    private int drawMarketInfo(Graphics2D g, int y) {
        if (!currentItemName.isEmpty()) {
            g.setFont(SMALL_FONT);
            g.setColor(new Color(200, 200, 200));
            y = drawStatLine(g, "Item:", currentItemName, y);
            if (statistics.currentAlchValue > 0) {
                y = drawStatLine(g, "Alch Value:", BotUtils.formatGP(statistics.currentAlchValue), y);
    private int drawProgressBar(Graphics2D g, int y) {
        if (statistics.itemsRemaining > 0) {
            int barWidth = OVERLAY_WIDTH - (OVERLAY_PADDING * 2);
            int barHeight = 8;
            int barX = overlayX + OVERLAY_PADDING;
            int totalItems = statistics.alchsCompleted + statistics.itemsRemaining;
            float progress = totalItems > 0 ? (float)statistics.alchsCompleted / totalItems : 0;
            // Background
            g.setColor(new Color(50, 50, 50));
            g.fillRoundRect(barX, y, barWidth, barHeight, 4, 4);
            // Progress fill
            int fillWidth = (int)(barWidth * progress);
            GradientPaint progressGradient = new GradientPaint(
                    barX, y, SUCCESS_COLOR,
                    barX + fillWidth, y, ACCENT_COLOR
            );
            g.setPaint(progressGradient);
            g.fillRoundRect(barX, y, fillWidth, barHeight, 4, 4);
            // Border
            g.setColor(BORDER_COLOR);
            g.setStroke(new BasicStroke(1));
            g.drawRoundRect(barX, y, barWidth, barHeight, 4, 4);
            y += barHeight + 5;
            // Progress text
            g.setColor(TEXT_COLOR);
            String progressText = String.format("%d / %d items (%.1f%%)",
                    statistics.alchsCompleted, totalItems, progress * 100);
            drawCenteredText(g, progressText, y);
            y += LINE_HEIGHT;
    private void drawStatusIndicators(Graphics2D g) {
        int indicatorSize = 8;
        int indicatorX = overlayX + OVERLAY_WIDTH - OVERLAY_PADDING - indicatorSize;
        int indicatorY = overlayY + OVERLAY_PADDING;
        // Running indicator
        g.setColor(statistics.isRunning ? SUCCESS_COLOR : new Color(100, 100, 100));
        g.fillOval(indicatorX, indicatorY, indicatorSize, indicatorSize);
        // Error indicator
        if (statistics.errorsEncountered > 0) {
            indicatorX -= indicatorSize + 5;
            g.setColor(ERROR_COLOR);
            g.fillOval(indicatorX, indicatorY, indicatorSize, indicatorSize);
    // HELPER METHODS
    private int drawStatLine(Graphics2D g, String label, String value, int y) {
        drawText(g, label, y);
        drawRightAlignedText(g, value, y);
    private void drawText(Graphics2D g, String text, int y) {
        g.drawString(text, overlayX + OVERLAY_PADDING, y);
    private void drawRightAlignedText(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, overlayX + OVERLAY_WIDTH - OVERLAY_PADDING - textWidth, y);
    private void drawCenteredText(Graphics2D g, String text, int y) {
        int x = overlayX + (OVERLAY_WIDTH - textWidth) / 2;
        g.drawString(text, x, y);
    // GETTERS / SETTERS
    public void setPosition(int x, int y) {
        this.overlayX = x;
        this.overlayY = y;
    public void toggleOverlay() {
        this.showOverlay = !this.showOverlay;
    public void setShowOverlay(boolean show) {
        this.showOverlay = show;
    public boolean isOverlayVisible() {
        return showOverlay;
    public ScriptStatistics getStatistics() {
        return statistics;
}
