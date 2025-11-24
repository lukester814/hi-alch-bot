package Hi_alch.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * CSV/Excel Export Manager for Profit Tracking
 *
 * Features:
 * - Export session logs to CSV
 * - Daily/weekly profit reports
 * - Detailed alch history tracking
 * - Excel-compatible formatting
 * - Auto-save functionality
 */
public class CSVExporter {

    // ===========================================
    // SESSION DATA STRUCTURES
    // ===========================================

    public static class AlchRecord {
        public long timestamp;
        public String itemName;
        public int itemId;
        public int buyPrice;
        public int alchValue;
        public int profitPerAlch;
        public int magicXP;

        public AlchRecord(String itemName, int itemId, int buyPrice, int alchValue, int profitPerAlch, int magicXP) {
            this.timestamp = System.currentTimeMillis();
            this.itemName = itemName;
            this.itemId = itemId;
            this.buyPrice = buyPrice;
            this.alchValue = alchValue;
            this.profitPerAlch = profitPerAlch;
            this.magicXP = magicXP;
        }
    }

    public static class SessionSummary {
        public long sessionStartTime;
        public long sessionEndTime;
        public String itemName;
        public int totalAlchs;
        public int totalProfit;
        public int totalXPGained;
        public int startingLevel;
        public int endingLevel;
        public String location;
        public int errorsEncountered;

        public long getSessionDuration() {
            return sessionEndTime - sessionStartTime;
        }

        public int getAlchsPerHour() {
            long hours = getSessionDuration() / 3600000;
            return hours > 0 ? (int)(totalAlchs / hours) : totalAlchs;
        }

        public int getProfitPerHour() {
            long hours = getSessionDuration() / 3600000;
            return hours > 0 ? (int)(totalProfit / hours) : totalProfit;
        }

        public int getXPPerHour() {
            long hours = getSessionDuration() / 3600000;
            return hours > 0 ? (int)(totalXPGained / hours) : totalXPGained;
        }
    }

    // ===========================================
    // STATE
    // ===========================================

    private List<AlchRecord> alchHistory;
    private List<SessionSummary> sessionHistory;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat filenameFormat;
    private String exportDirectory;
    private boolean autoExport;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public CSVExporter() {
        this.alchHistory = new ArrayList<>();
        this.sessionHistory = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.filenameFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        this.exportDirectory = System.getProperty("user.home") + "/HiAlchBot_Exports/";
        this.autoExport = true;

        // Create export directory if it doesn't exist
        createExportDirectory();

        BotUtils.log("📊 CSVExporter initialized");
        BotUtils.log("📁 Export directory: " + exportDirectory);
    }

    // ===========================================
    // DIRECTORY MANAGEMENT
    // ===========================================

    private void createExportDirectory() {
        File dir = new File(exportDirectory);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                BotUtils.log("✅ Created export directory: " + exportDirectory);
            } else {
                BotUtils.log("⚠️ Failed to create export directory");
            }
        }
    }

    public void setExportDirectory(String directory) {
        this.exportDirectory = directory;
        if (!directory.endsWith("/")) {
            this.exportDirectory += "/";
        }
        createExportDirectory();
        BotUtils.log("📁 Export directory changed to: " + exportDirectory);
    }

    // ===========================================
    // RECORD TRACKING
    // ===========================================

    /**
     * Add an alch record
     */
    public void recordAlch(String itemName, int itemId, int buyPrice, int alchValue, int profitPerAlch, int magicXP) {
        AlchRecord record = new AlchRecord(itemName, itemId, buyPrice, alchValue, profitPerAlch, magicXP);
        alchHistory.add(record);

        // Auto-export every 100 alchs if enabled
        if (autoExport && alchHistory.size() % 100 == 0) {
            exportAlchHistoryIncremental();
        }
    }

    /**
     * Add a session summary
     */
    public void recordSession(SessionSummary summary) {
        sessionHistory.add(summary);

        if (autoExport) {
            exportSessionSummary(summary);
        }

        BotUtils.log("📊 Session recorded: " + summary.totalAlchs + " alchs, " +
                   BotUtils.formatNumber(summary.totalProfit) + " GP profit");
    }

    // ===========================================
    // CSV EXPORT METHODS
    // ===========================================

    /**
     * Export full alch history to CSV
     */
    public boolean exportAlchHistory() {
        try {
            String filename = exportDirectory + "alch_history_" + filenameFormat.format(new Date()) + ".csv";
            return exportAlchHistoryToFile(filename);

        } catch (Exception e) {
            BotUtils.logError("Error exporting alch history", e);
            return false;
        }
    }

    /**
     * Export alch history to specific file
     */
    private boolean exportAlchHistoryToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            // Write CSV header
            writer.write("Timestamp,Item Name,Item ID,Buy Price,Alch Value,Profit Per Alch,Magic XP Gained\n");

            // Write data rows
            for (AlchRecord record : alchHistory) {
                writer.write(String.format("%s,%s,%d,%d,%d,%d,%d\n",
                    dateFormat.format(new Date(record.timestamp)),
                    escapeCsvField(record.itemName),
                    record.itemId,
                    record.buyPrice,
                    record.alchValue,
                    record.profitPerAlch,
                    record.magicXP
                ));
            }

            BotUtils.log("✅ Exported " + alchHistory.size() + " alch records to: " + filename);
            return true;

        } catch (Exception e) {
            BotUtils.logError("Error writing alch history CSV", e);
            return false;
        }
    }

    /**
     * Export alch history incrementally (append mode)
     */
    private boolean exportAlchHistoryIncremental() {
        try {
            String filename = exportDirectory + "alch_history_live.csv";
            File file = new File(filename);
            boolean isNewFile = !file.exists();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {

                // Write header if new file
                if (isNewFile) {
                    writer.write("Timestamp,Item Name,Item ID,Buy Price,Alch Value,Profit Per Alch,Magic XP Gained\n");
                }

                // Write only the last record
                if (!alchHistory.isEmpty()) {
                    AlchRecord record = alchHistory.get(alchHistory.size() - 1);
                    writer.write(String.format("%s,%s,%d,%d,%d,%d,%d\n",
                        dateFormat.format(new Date(record.timestamp)),
                        escapeCsvField(record.itemName),
                        record.itemId,
                        record.buyPrice,
                        record.alchValue,
                        record.profitPerAlch,
                        record.magicXP
                    ));
                }

                return true;
            }

        } catch (Exception e) {
            BotUtils.logError("Error appending to alch history", e);
            return false;
        }
    }

    /**
     * Export session summary
     */
    public boolean exportSessionSummary(SessionSummary summary) {
        try {
            String filename = exportDirectory + "session_summary_" +
                            filenameFormat.format(new Date(summary.sessionStartTime)) + ".csv";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

                // Session metadata
                writer.write("Hi-Alch Bot Session Summary\n");
                writer.write("Session Start," + dateFormat.format(new Date(summary.sessionStartTime)) + "\n");
                writer.write("Session End," + dateFormat.format(new Date(summary.sessionEndTime)) + "\n");
                writer.write("Duration (minutes)," + (summary.getSessionDuration() / 60000) + "\n");
                writer.write("\n");

                // Session statistics
                writer.write("Item," + escapeCsvField(summary.itemName) + "\n");
                writer.write("Total Alchs," + summary.totalAlchs + "\n");
                writer.write("Total Profit (GP)," + summary.totalProfit + "\n");
                writer.write("Total XP Gained," + summary.totalXPGained + "\n");
                writer.write("Starting Level," + summary.startingLevel + "\n");
                writer.write("Ending Level," + summary.endingLevel + "\n");
                writer.write("Location," + escapeCsvField(summary.location) + "\n");
                writer.write("Errors," + summary.errorsEncountered + "\n");
                writer.write("\n");

                // Per-hour rates
                writer.write("Alchs Per Hour," + summary.getAlchsPerHour() + "\n");
                writer.write("Profit Per Hour (GP)," + summary.getProfitPerHour() + "\n");
                writer.write("XP Per Hour," + summary.getXPPerHour() + "\n");

                BotUtils.log("✅ Exported session summary to: " + filename);
                return true;
            }

        } catch (Exception e) {
            BotUtils.logError("Error exporting session summary", e);
            return false;
        }
    }

    /**
     * Export all session history
     */
    public boolean exportAllSessions() {
        try {
            String filename = exportDirectory + "all_sessions_" + filenameFormat.format(new Date()) + ".csv";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

                // Write CSV header
                writer.write("Session Start,Session End,Duration (min),Item,Total Alchs,Total Profit,Total XP," +
                           "Start Level,End Level,Location,Errors,Alchs/Hr,Profit/Hr,XP/Hr\n");

                // Write session data
                for (SessionSummary session : sessionHistory) {
                    writer.write(String.format("%s,%s,%d,%s,%d,%d,%d,%d,%d,%s,%d,%d,%d,%d\n",
                        dateFormat.format(new Date(session.sessionStartTime)),
                        dateFormat.format(new Date(session.sessionEndTime)),
                        session.getSessionDuration() / 60000,
                        escapeCsvField(session.itemName),
                        session.totalAlchs,
                        session.totalProfit,
                        session.totalXPGained,
                        session.startingLevel,
                        session.endingLevel,
                        escapeCsvField(session.location),
                        session.errorsEncountered,
                        session.getAlchsPerHour(),
                        session.getProfitPerHour(),
                        session.getXPPerHour()
                    ));
                }

                BotUtils.log("✅ Exported " + sessionHistory.size() + " sessions to: " + filename);
                return true;
            }

        } catch (Exception e) {
            BotUtils.logError("Error exporting all sessions", e);
            return false;
        }
    }

    // ===========================================
    // REPORTS
    // ===========================================

    /**
     * Generate daily profit report
     */
    public boolean generateDailyReport() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            long dayStart = cal.getTimeInMillis();

            List<AlchRecord> todayRecords = new ArrayList<>();
            for (AlchRecord record : alchHistory) {
                if (record.timestamp >= dayStart) {
                    todayRecords.add(record);
                }
            }

            if (todayRecords.isEmpty()) {
                BotUtils.log("📊 No alchs today - skipping daily report");
                return false;
            }

            // Calculate daily totals
            int totalAlchs = todayRecords.size();
            int totalProfit = 0;
            int totalXP = 0;

            for (AlchRecord record : todayRecords) {
                totalProfit += record.profitPerAlch;
                totalXP += record.magicXP;
            }

            // Export daily report
            String filename = exportDirectory + "daily_report_" +
                            new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".csv";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write("Hi-Alch Bot Daily Report\n");
                writer.write("Date," + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "\n");
                writer.write("\n");
                writer.write("Total Alchs," + totalAlchs + "\n");
                writer.write("Total Profit (GP)," + totalProfit + "\n");
                writer.write("Total XP Gained," + totalXP + "\n");
                writer.write("Average Profit Per Alch," + (totalAlchs > 0 ? totalProfit / totalAlchs : 0) + "\n");

                BotUtils.log("✅ Generated daily report: " + filename);
                return true;
            }

        } catch (Exception e) {
            BotUtils.logError("Error generating daily report", e);
            return false;
        }
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Escape CSV field (handle commas and quotes)
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }

        // If field contains comma, quote, or newline, wrap in quotes and escape internal quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }

    /**
     * Clear all history
     */
    public void clearHistory() {
        alchHistory.clear();
        sessionHistory.clear();
        BotUtils.log("🗑️ Export history cleared");
    }

    /**
     * Get total records count
     */
    public int getTotalRecords() {
        return alchHistory.size();
    }

    /**
     * Get total sessions count
     */
    public int getTotalSessions() {
        return sessionHistory.size();
    }

    // ===========================================
    // GETTERS/SETTERS
    // ===========================================

    public void setAutoExport(boolean enabled) {
        this.autoExport = enabled;
        BotUtils.log("📊 Auto-export " + (enabled ? "enabled" : "disabled"));
    }

    public boolean isAutoExport() {
        return autoExport;
    }

    public String getExportDirectory() {
        return exportDirectory;
    }

    public List<AlchRecord> getAlchHistory() {
        return new ArrayList<>(alchHistory);
    }

    public List<SessionSummary> getSessionHistory() {
        return new ArrayList<>(sessionHistory);
    }
}
