package Hi_alch.util;

import Hi_alch.core.Constants;
import Hi_alch.overlay.ScriptStatistics;

/**
 * Profit Calculator - Advanced Profit Analysis
 *
 * Provides detailed profit calculations and analysis:
 * - Per-item profit calculations
 * - Hourly profit projections
 * - Break-even analysis
 * - ROI (Return on Investment) calculations
 * - Session profit tracking
 */
public class ProfitCalculator {

    // ===========================================
    // PROFIT CALCULATION
    // ===========================================

    /**
     * Calculate profit per alchemy
     */
    public static int calculateAlchProfit(int itemBuyPrice, int alchValue) {
        int natureRuneCost = Constants.NATURE_RUNE_COST;
        return alchValue - itemBuyPrice - natureRuneCost;
    }

    /**
     * Calculate profit with markup
     */
    public static int calculateAlchProfitWithMarkup(int basePrice, double markupPercent, int alchValue) {
        int actualBuyPrice = (int) (basePrice * (1.0 + markupPercent / 100.0));
        return calculateAlchProfit(actualBuyPrice, alchValue);
    }

    /**
     * Calculate total session profit
     */
    public static int calculateSessionProfit(int alchCount, int profitPerAlch) {
        return alchCount * profitPerAlch;
    }

    /**
     * Calculate profit percentage (margin)
     */
    public static double calculateProfitMargin(int buyPrice, int sellPrice) {
        if (buyPrice <= 0) {
            return 0.0;
        }

        int profit = sellPrice - buyPrice;
        return (profit * 100.0) / buyPrice;
    }

    // ===========================================
    // HOURLY PROJECTIONS
    // ===========================================

    /**
     * Calculate alchs per hour based on current rate
     */
    public static double calculateAlchsPerHour(int alchCount, long runtimeMs) {
        if (runtimeMs <= 0) {
            return 0.0;
        }

        double hoursRunning = runtimeMs / (double) Constants.MILLISECONDS_PER_HOUR;
        return alchCount / hoursRunning;
    }

    /**
     * Calculate profit per hour
     */
    public static double calculateProfitPerHour(int totalProfit, long runtimeMs) {
        if (runtimeMs <= 0) {
            return 0.0;
        }

        double hoursRunning = runtimeMs / (double) Constants.MILLISECONDS_PER_HOUR;
        return totalProfit / hoursRunning;
    }

    /**
     * Calculate XP per hour
     */
    public static double calculateXPPerHour(int totalXP, long runtimeMs) {
        if (runtimeMs <= 0) {
            return 0.0;
        }

        double hoursRunning = runtimeMs / (double) Constants.MILLISECONDS_PER_HOUR;
        return totalXP / hoursRunning;
    }

    /**
     * Project profit for target time
     */
    public static int projectProfit(double profitPerHour, long targetHours) {
        return (int) (profitPerHour * targetHours);
    }

    /**
     * Project alchs for target time
     */
    public static int projectAlchs(double alchsPerHour, long targetHours) {
        return (int) (alchsPerHour * targetHours);
    }

    // ===========================================
    // BREAK-EVEN ANALYSIS
    // ===========================================

    /**
     * Calculate break-even point (alchs needed to recover initial investment)
     */
    public static int calculateBreakEvenAlchs(int initialInvestment, int profitPerAlch) {
        if (profitPerAlch <= 0) {
            return -1; // Cannot break even with zero or negative profit
        }

        return (int) Math.ceil(initialInvestment / (double) profitPerAlch);
    }

    /**
     * Calculate time to break even
     */
    public static long calculateTimeToBreakEven(int initialInvestment, double profitPerHour) {
        if (profitPerHour <= 0) {
            return -1; // Cannot break even
        }

        double hoursNeeded = initialInvestment / profitPerHour;
        return (long) (hoursNeeded * Constants.MILLISECONDS_PER_HOUR);
    }

    /**
     * Check if session is profitable
     */
    public static boolean isProfitable(int totalProfit) {
        return totalProfit > 0;
    }

    // ===========================================
    // ROI CALCULATIONS
    // ===========================================

    /**
     * Calculate Return on Investment (ROI) percentage
     */
    public static double calculateROI(int initialInvestment, int totalProfit) {
        if (initialInvestment <= 0) {
            return 0.0;
        }

        return (totalProfit * 100.0) / initialInvestment;
    }

    /**
     * Calculate ROI per hour
     */
    public static double calculateROIPerHour(int initialInvestment, int totalProfit, long runtimeMs) {
        double roi = calculateROI(initialInvestment, totalProfit);

        if (runtimeMs <= 0) {
            return roi;
        }

        double hoursRunning = runtimeMs / (double) Constants.MILLISECONDS_PER_HOUR;
        return roi / hoursRunning;
    }

    // ===========================================
    // COST ANALYSIS
    // ===========================================

    /**
     * Calculate total item cost
     */
    public static int calculateTotalItemCost(int itemPrice, int quantity) {
        return itemPrice * quantity;
    }

    /**
     * Calculate total nature rune cost
     */
    public static int calculateNatureRuneCost(int quantity) {
        return Constants.NATURE_RUNE_COST * quantity;
    }

    /**
     * Calculate total investment needed
     */
    public static int calculateTotalInvestment(int itemPrice, int itemQuantity, int runeQuantity) {
        int itemCost = calculateTotalItemCost(itemPrice, itemQuantity);
        int runeCost = calculateNatureRuneCost(runeQuantity);
        return itemCost + runeCost;
    }

    // ===========================================
    // EFFICIENCY METRICS
    // ===========================================

    /**
     * Calculate efficiency rating (0-100)
     * Based on actual vs theoretical max alchs per hour
     */
    public static double calculateEfficiency(double actualAlchsPerHour) {
        // Theoretical maximum: one alch every 3 seconds = 1200/hour
        double theoreticalMax = 1200.0;
        double efficiency = (actualAlchsPerHour / theoreticalMax) * 100.0;

        return Math.min(efficiency, 100.0); // Cap at 100%
    }

    /**
     * Calculate downtime percentage
     */
    public static double calculateDowntime(int alchCount, long runtimeMs) {
        if (runtimeMs <= 0 || alchCount <= 0) {
            return 100.0;
        }

        // Each alch takes minimum 3 seconds
        long theoreticalTime = alchCount * 3000L;
        long downtime = runtimeMs - theoreticalTime;

        if (downtime <= 0) {
            return 0.0;
        }

        return (downtime * 100.0) / runtimeMs;
    }

    // ===========================================
    // STATISTICS ANALYSIS
    // ===========================================

    /**
     * Analyze script statistics and return detailed report
     */
    public static ProfitAnalysisReport analyzeStatistics(ScriptStatistics stats, int itemBuyPrice, int alchValue) {
        ProfitAnalysisReport report = new ProfitAnalysisReport();

        // Basic stats
        report.alchsCompleted = stats.alchsCompleted;
        report.totalProfit = stats.totalProfit;
        report.xpGained = stats.xpGained;
        report.runtime = stats.scriptRuntime;

        // Calculate rates
        report.alchsPerHour = calculateAlchsPerHour(stats.alchsCompleted, stats.scriptRuntime);
        report.profitPerHour = calculateProfitPerHour(stats.totalProfit, stats.scriptRuntime);
        report.xpPerHour = calculateXPPerHour(stats.xpGained, stats.scriptRuntime);

        // Calculate per-alch metrics
        if (stats.alchsCompleted > 0) {
            report.profitPerAlch = stats.totalProfit / stats.alchsCompleted;
            report.averageAlchTime = stats.scriptRuntime / stats.alchsCompleted;
        }

        // Efficiency
        report.efficiency = calculateEfficiency(report.alchsPerHour);
        report.downtime = calculateDowntime(stats.alchsCompleted, stats.scriptRuntime);

        // Profitability
        report.isProfitable = isProfitable(stats.totalProfit);
        report.profitMargin = calculateProfitMargin(itemBuyPrice, alchValue);

        return report;
    }

    // ===========================================
    // PROFIT ANALYSIS REPORT
    // ===========================================

    /**
     * Detailed profit analysis report
     */
    public static class ProfitAnalysisReport {
        // Basic stats
        public int alchsCompleted;
        public int totalProfit;
        public int xpGained;
        public long runtime;

        // Rates
        public double alchsPerHour;
        public double profitPerHour;
        public double xpPerHour;

        // Per-alch metrics
        public int profitPerAlch;
        public long averageAlchTime;

        // Efficiency
        public double efficiency;
        public double downtime;

        // Profitability
        public boolean isProfitable;
        public double profitMargin;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Profit Analysis Report ===\n");
            sb.append(String.format("Alchs Completed: %,d\n", alchsCompleted));
            sb.append(String.format("Total Profit: %,d GP\n", totalProfit));
            sb.append(String.format("XP Gained: %,d\n", xpGained));
            sb.append(String.format("Runtime: %s\n", formatDuration(runtime)));
            sb.append("\n=== Rates ===\n");
            sb.append(String.format("Alchs/Hour: %.0f\n", alchsPerHour));
            sb.append(String.format("Profit/Hour: %,d GP\n", (int) profitPerHour));
            sb.append(String.format("XP/Hour: %,d\n", (int) xpPerHour));
            sb.append("\n=== Efficiency ===\n");
            sb.append(String.format("Efficiency: %.1f%%\n", efficiency));
            sb.append(String.format("Downtime: %.1f%%\n", downtime));
            sb.append(String.format("Avg Alch Time: %.1fs\n", averageAlchTime / 1000.0));
            sb.append("\n=== Profitability ===\n");
            sb.append(String.format("Profitable: %s\n", isProfitable ? "YES" : "NO"));
            sb.append(String.format("Profit Margin: %.2f%%\n", profitMargin));
            sb.append(String.format("Profit Per Alch: %,d GP\n", profitPerAlch));

            return sb.toString();
        }

        private String formatDuration(long ms) {
            long hours = ms / 3600000;
            long minutes = (ms % 3600000) / 60000;
            long seconds = (ms % 60000) / 1000;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    // ===========================================
    // FORMATTING UTILITIES
    // ===========================================

    /**
     * Format GP amount with suffix
     */
    public static String formatGP(int amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.2fB GP", amount / 1_000_000_000.0);
        } else if (amount >= 1_000_000) {
            return String.format("%.2fM GP", amount / 1_000_000.0);
        } else if (amount >= 1_000) {
            return String.format("%.1fK GP", amount / 1_000.0);
        } else {
            return String.format("%,d GP", amount);
        }
    }

    /**
     * Format percentage
     */
    public static String formatPercent(double percentage) {
        return String.format("%.2f%%", percentage);
    }

    /**
     * Prevent instantiation
     */
    private ProfitCalculator() {
        throw new AssertionError("Cannot instantiate ProfitCalculator class");
    }
}
