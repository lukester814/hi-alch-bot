package Hi_alch.core;

import Hi_alch.*;
import java.awt.Graphics2D;

/**
 * PaintCoordinator - Handles Paint Rendering
 *
 * Responsibilities:
 * - Gathering statistics from all components
 * - Creating unified statistics object
 * - Rendering overlay paint
 * - Rendering modern tabbed paint
 * - Error handling for paint failures
 *
 * This class centralizes all paint rendering logic,
 * gathering statistics from various components and
 * coordinating the rendering of both old and new paint styles.
 *
 * Extracted from HighAlchBot Phase 5 refactoring.
 */
public class PaintCoordinator {

    private final ComponentManager componentManager;
    private final BotStateManager stateManager;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    /**
     * Create new paint coordinator
     * @param componentManager Component manager for accessing components
     * @param stateManager State manager for bot state
     */
    public PaintCoordinator(ComponentManager componentManager, BotStateManager stateManager) {
        this.componentManager = componentManager;
        this.stateManager = stateManager;
    }

    // ===========================================
    // PAINT RENDERING
    // ===========================================

    /**
     * Main paint rendering method - called by DreamBot
     * @param g Graphics2D object for rendering
     */
    public void onPaint(Graphics2D g) {
        try {
            // Gather statistics from all sources
            OverlayRenderer.ScriptStatistics stats = gatherStatistics();

            // Calculate derived statistics
            stats.calculateDerivedStats();

            // Determine bot status
            String status = stateManager.isBotRunning() ? "Running" : "Stopped";

            // Render both old and new paint (user can choose which they prefer)
            renderOverlayPaint(g, stats, status);
            renderModernPaint(g, stats, status);

        } catch (Exception e) {
            // Don't log paint errors too frequently to avoid spam
            if (Math.random() < 0.01) { // Log only 1% of paint errors
                BotUtils.logError("Error in paint method", e);
            }
        }
    }

    // ===========================================
    // STATISTICS GATHERING
    // ===========================================

    /**
     * Gather statistics from all components into unified object
     */
    private OverlayRenderer.ScriptStatistics gatherStatistics() {
        // Create statistics for rendering using correct field names
        OverlayRenderer.ScriptStatistics stats = new OverlayRenderer.ScriptStatistics();

        // Gather basic statistics from state manager
        stats.sessionStartTime = stateManager.getSessionStartTime();
        stats.scriptRuntime = stateManager.getSessionRuntime();
        stats.alchsCompleted = stateManager.getTotalAlchs();
        stats.totalProfit = stateManager.getTotalProfit();
        stats.xpGained = stateManager.getTotalXpGained();
        stats.isRunning = stateManager.isBotRunning();

        // Get current state from alchemy engine
        AlchingEngine alchingEngine = componentManager.getAlchingEngine();
        if (alchingEngine != null) {
            stats.currentState = alchingEngine.getCurrentStateDescription();
            stats.currentAction = alchingEngine.getCurrentStateDescription();

            // Get additional stats from alching engine if available
            OverlayRenderer.ScriptStatistics engineStats = alchingEngine.getStatistics();
            if (engineStats != null) {
                stats.currentBuyPrice = engineStats.currentBuyPrice;
                stats.currentAlchValue = engineStats.currentAlchValue;
                stats.itemsBought = engineStats.itemsBought;
                stats.errorsEncountered = engineStats.errorsEncountered;
                stats.lastError = engineStats.lastError;
            }
        } else {
            stats.currentState = "Stopped";
            stats.currentAction = "Idle";
        }

        return stats;
    }

    // ===========================================
    // RENDER METHODS
    // ===========================================

    /**
     * Render overlay paint (classic style)
     */
    private void renderOverlayPaint(Graphics2D g, OverlayRenderer.ScriptStatistics stats, String status) {
        try {
            OverlayRenderer overlayRenderer = componentManager.getOverlayRenderer();
            if (overlayRenderer != null) {
                overlayRenderer.render(g, stats, status);
            }
        } catch (Exception e) {
            // Silently fail - already logged in main onPaint
        }
    }

    /**
     * Render modern tabbed paint (new style)
     */
    private void renderModernPaint(Graphics2D g, OverlayRenderer.ScriptStatistics stats, String status) {
        try {
            ModernPaintRenderer modernPaint = componentManager.getModernPaint();
            if (modernPaint != null) {
                modernPaint.render(g, stats, status);
            }
        } catch (Exception e) {
            // Silently fail - already logged in main onPaint
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Check if paint is enabled
     */
    public boolean isPaintEnabled() {
        return componentManager.getOverlayRenderer() != null ||
               componentManager.getModernPaint() != null;
    }

    /**
     * Get current statistics snapshot (for external use)
     */
    public OverlayRenderer.ScriptStatistics getCurrentStatistics() {
        return gatherStatistics();
    }
}
