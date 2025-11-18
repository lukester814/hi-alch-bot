package Hi_alch;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Professional Data Analytics Panel for OSRS Market Analysis
 *
 * Features:
 * - Live market data table with pagination
 * - Real-time profit calculations
 * - Item filtering (F2P vs P2P)
 * - Status indicators for profit levels
 * - Auto-refresh capabilities
 * - Loading progress indicators
 *
 * REUSABLE: Perfect for any OSRS bot that needs market data display!
 */
public class GUIDataPanel {

    // ===========================================
    // COMPONENTS
    // ===========================================

    private JPanel mainPanel;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JButton refreshDataButton;
    private JLabel lastUpdateLabel;
    private JProgressBar dataLoadingBar;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private JLabel pageLabel;

    // ===========================================
    // STATE
    // ===========================================

    private int currentPage = 1;
    private String currentCategory = "F2P Items";

    // Item lists
    private final List<String> allF2PItems = Arrays.asList(
        "Rune longsword", "Rune 2h sword", "Rune platebody", "Rune platelegs",
        "Rune plateskirt", "Rune chainbody", "Rune med helm", "Rune full helm",
        "Rune sq shield", "Rune kiteshield", "Rune scimitar", "Rune battleaxe",
        "Rune dagger", "Rune mace", "Rune sword", "Rune warhammer",
        "Adamant platebody", "Adamant platelegs", "Adamant chainbody",
        "Green d'hide body", "Green d'hide chaps", "Green d'hide vambraces"
    );

    private final List<String> allP2PItems = Arrays.asList(
        "Dragon longsword", "Dragon battleaxe", "Dragon dagger", "Dragon mace",
        "Dragon scimitar", "Dragon sword", "Dragon spear", "Dragon halberd",
        "Black d'hide body", "Black d'hide chaps", "Black d'hide vambraces",
        "Red d'hide body", "Red d'hide chaps", "Blue d'hide body", "Blue d'hide chaps",
        "Rune crossbow", "Magic longbow", "Yew longbow", "Maple longbow",
        "Battlestaff", "Air battlestaff", "Water battlestaff", "Earth battlestaff",
        "Fire battlestaff", "Mystic robe top", "Mystic robe bottom"
    );

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public GUIDataPanel() {
        createDataPanel();
    }

    // ===========================================
    // PANEL CREATION
    // ===========================================

    /**
     * Create the complete data analytics panel
     */
    private void createDataPanel() {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header with title and controls
        JPanel headerPanel = createHeaderPanel();

        // Data table
        JScrollPane tableScrollPane = createDataTable();

        // Footer with status
        JPanel footerPanel = createFooterPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Create header panel with title and controls
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Title panel
        JLabel titleLabel = new JLabel("Live OSRS Market Data & Profit Analysis");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));

        JLabel subtitleLabel = new JLabel("Real-time data from OSRS Wiki API");
        subtitleLabel.setFont(GUIStyles.SUBTITLE_FONT);
        subtitleLabel.setForeground(GUIStyles.PROFIT_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Control panel with pagination
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        prevPageButton = new JButton("◀ Previous");
        prevPageButton.setPreferredSize(new Dimension(100, 30));
        GUIStyles.styleButton(prevPageButton, new Color(108, 117, 125));
        prevPageButton.setEnabled(false);

        pageLabel = new JLabel("Page 1");
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pageLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

        nextPageButton = new JButton("Next ▶");
        nextPageButton.setPreferredSize(new Dimension(100, 30));
        GUIStyles.styleButton(nextPageButton, new Color(108, 117, 125));

        refreshDataButton = new JButton("Refresh Data");
        refreshDataButton.setPreferredSize(new Dimension(120, 30));
        GUIStyles.styleButton(refreshDataButton, new Color(59, 130, 246));

        controlPanel.add(prevPageButton);
        controlPanel.add(pageLabel);
        controlPanel.add(nextPageButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(refreshDataButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Create data table with styling
     */
    private JScrollPane createDataTable() {
        String[] columnNames = {"Item", "Buy Price", "Alch Value", "Profit/Item", "Profit %", "Buy Limit", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dataTable = new JTable(tableModel);
        dataTable.setFont(GUIStyles.TABLE_FONT);
        dataTable.setRowHeight(30);
        dataTable.setGridColor(new Color(70, 70, 70));
        dataTable.setSelectionBackground(new Color(64, 128, 255, 100));
        dataTable.getTableHeader().setFont(GUIStyles.TABLE_HEADER_FONT);

        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 350));

        return tableScrollPane;
    }

    /**
     * Create footer panel with loading bar and status
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());

        // Loading bar
        dataLoadingBar = new JProgressBar();
        dataLoadingBar.setStringPainted(true);
        dataLoadingBar.setString("Ready to fetch live OSRS market data");
        dataLoadingBar.setForeground(GUIStyles.PROFIT_COLOR);

        // Status label
        lastUpdateLabel = new JLabel("Click 'Refresh Data' to load live prices");
        lastUpdateLabel.setFont(GUIStyles.SUBTITLE_FONT);

        footerPanel.add(dataLoadingBar, BorderLayout.NORTH);
        footerPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        footerPanel.add(lastUpdateLabel, BorderLayout.SOUTH);

        return footerPanel;
    }

    // ===========================================
    // EVENT HANDLERS SETUP
    // ===========================================

    /**
     * Setup event handlers for pagination and refresh
     */
    public void setupEventHandlers() {
        prevPageButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                refreshDataTablePage(currentPage);
            }
        });

        nextPageButton.addActionListener(e -> {
            currentPage++;
            refreshDataTablePage(currentPage);
        });

        refreshDataButton.addActionListener(e -> refreshDataTable());
    }

    // ===========================================
    // DATA REFRESH LOGIC
    // ===========================================

    /**
     * Refresh data table starting from page 1
     */
    public void refreshDataTable() {
        currentPage = 1;
        refreshDataTablePage(1);
    }

    /**
     * Refresh data table for specific page
     */
    public void refreshDataTablePage(int page) {
        SwingUtilities.invokeLater(() -> {
            dataLoadingBar.setString("Fetching page " + page + " data...");
            dataLoadingBar.setIndeterminate(true);

            new Thread(() -> {
                try {
                    Thread.sleep(500);

                    SwingUtilities.invokeLater(() -> {
                        tableModel.setRowCount(0);

                        List<String> items = "F2P Items".equals(currentCategory) ? allF2PItems : allP2PItems;

                        int itemsPerPage = 10;
                        int startIndex = (page - 1) * itemsPerPage;
                        int endIndex = Math.min(startIndex + itemsPerPage, items.size());

                        for (int i = startIndex; i < endIndex; i++) {
                            String itemName = items.get(i);

                            ItemSearchAPI.ItemSearchResult result = ItemSearchAPI.searchItem(itemName);

                            if (result != null && result.isValid()) {
                                int buyPrice = result.averagePrice > 0 ? result.averagePrice : result.highPrice;
                                int alchValue = result.alchValue;
                                int profit = alchValue - buyPrice - 220; // Nature rune cost
                                double profitPercent = buyPrice > 0 ? (profit * 100.0) / buyPrice : 0;

                                String status;
                                if (profit > 500) {
                                    status = "⭐ Excellent";
                                } else if (profit > 100) {
                                    status = "✅ Good";
                                } else if (profit > 0) {
                                    status = "⚠️ Low";
                                } else {
                                    status = "❌ Loss";
                                }

                                String[] rowData = {
                                    itemName,
                                    BotUtils.formatNumber(buyPrice) + " GP",
                                    BotUtils.formatNumber(alchValue) + " GP",
                                    (profit >= 0 ? "+" : "") + BotUtils.formatNumber(profit) + " GP",
                                    String.format("%.1f%%", profitPercent),
                                    String.valueOf(result.buyLimit > 0 ? result.buyLimit : "?"),
                                    status
                                };

                                tableModel.addRow(rowData);
                            }
                        }

                        // Update pagination controls
                        prevPageButton.setEnabled(page > 1);
                        nextPageButton.setEnabled(endIndex < items.size());
                        pageLabel.setText("Page " + page + " of " + ((items.size() + 9) / 10));

                        dataLoadingBar.setIndeterminate(false);
                        dataLoadingBar.setValue(100);
                        dataLoadingBar.setString("Live data loaded - Page " + page);

                        lastUpdateLabel.setText("Last updated: " + BotUtils.getCurrentTimeString() +
                            " | Showing " + (endIndex - startIndex) + " items");
                    });

                } catch (Exception e) {
                    BotUtils.logError("Error loading page data", e);
                    SwingUtilities.invokeLater(() -> {
                        dataLoadingBar.setIndeterminate(false);
                        dataLoadingBar.setString("Error loading data");
                        lastUpdateLabel.setText("Failed to load data: " + e.getMessage());
                    });
                }
            }).start();
        });
    }

    // ===========================================
    // PUBLIC ACCESSORS
    // ===========================================

    /**
     * Get the main panel component
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * Set the category for filtering items
     */
    public void setCategory(String category) {
        this.currentCategory = category;
    }

    /**
     * Get refresh button for external event handling
     */
    public JButton getRefreshButton() {
        return refreshDataButton;
    }
}
