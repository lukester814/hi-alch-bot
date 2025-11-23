package Hi_alch.gui.panels;

import Hi_alch.utils.BotUtils;
import Hi_alch.api.ItemSearchAPI;
import Hi_alch.gui.GUIStyler;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Data panel builder for analytics and market data
 * Handles live market data table with pagination
 */
public class DataPanelBuilder {

    private final GUIStyler styler;

    // Data Tab Components
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JButton refreshDataButton;
    private JLabel lastUpdateLabel;
    private JProgressBar dataLoadingBar;

    private int currentDataPage = 1;
    private List<String> allF2PItems;
    private List<String> allP2PItems;

    public DataPanelBuilder(GUIStyler styler) {
        this.styler = styler;

        allF2PItems = Arrays.asList(
                "Rune longsword", "Rune 2h sword", "Rune platebody", "Rune platelegs",
                "Rune plateskirt", "Rune chainbody", "Rune med helm", "Rune full helm",
                "Rune sq shield", "Rune kiteshield", "Rune scimitar", "Rune battleaxe",
                "Rune dagger", "Rune mace", "Rune sword", "Rune warhammer",
                "Adamant platebody", "Adamant platelegs", "Adamant chainbody",
                "Green d'hide body", "Green d'hide chaps", "Green d'hide vambraces"
        );

        allP2PItems = Arrays.asList(
                "Dragon longsword", "Dragon battleaxe", "Dragon dagger", "Dragon mace",
                "Dragon scimitar", "Dragon sword", "Dragon spear", "Dragon halberd",
                "Black d'hide body", "Black d'hide chaps", "Black d'hide vambraces",
                "Red d'hide body", "Red d'hide chaps", "Blue d'hide body", "Blue d'hide chaps",
                "Rune crossbow", "Magic longbow", "Yew longbow", "Maple longbow",
                "Battlestaff", "Air battlestaff", "Water battlestaff", "Earth battlestaff",
                "Fire battlestaff", "Mystic robe top", "Mystic robe bottom"
        );
    }

    /**
     * Create professional data analytics panel with pagination
     */
    public JPanel createDataPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header with title and controls
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Live OSRS Market Data & Profit Analysis");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));

        JLabel subtitleLabel = new JLabel("Real-time data from OSRS Wiki API");
        subtitleLabel.setFont(new Font("Inter", Font.ITALIC, 11));
        subtitleLabel.setForeground(new Color(34, 197, 94));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Control panel with pagination
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Page navigation
        JButton prevPageButton = new JButton("◀ Previous");
        prevPageButton.setPreferredSize(new Dimension(100, 30));
        styler.styleButton(prevPageButton, new Color(108, 117, 125));
        prevPageButton.setEnabled(false);

        JLabel pageLabel = new JLabel("Page 1");
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pageLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

        JButton nextPageButton = new JButton("Next ▶");
        nextPageButton.setPreferredSize(new Dimension(100, 30));
        styler.styleButton(nextPageButton, new Color(108, 117, 125));

        refreshDataButton = new JButton("Refresh Data");
        refreshDataButton.setPreferredSize(new Dimension(120, 30));
        styler.styleButton(refreshDataButton, new Color(59, 130, 246));

        controlPanel.add(prevPageButton);
        controlPanel.add(pageLabel);
        controlPanel.add(nextPageButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(refreshDataButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        // Data table
        String[] columnNames = {"Item", "Buy Price", "Alch Value", "Profit/Item", "Profit %", "Buy Limit", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dataTable = new JTable(tableModel);
        dataTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dataTable.setRowHeight(30);
        dataTable.setGridColor(new Color(70, 70, 70));
        dataTable.setSelectionBackground(new Color(64, 128, 255, 100));

        dataTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 350));

        // Loading bar
        dataLoadingBar = new JProgressBar();
        dataLoadingBar.setStringPainted(true);
        dataLoadingBar.setString("Ready to fetch live OSRS market data");
        dataLoadingBar.setForeground(new Color(34, 197, 94));

        // Footer with status
        JPanel footerPanel = new JPanel(new BorderLayout());

        lastUpdateLabel = new JLabel("Click 'Refresh Data' to load live prices");
        lastUpdateLabel.setFont(new Font("Inter", Font.ITALIC, 11));

        footerPanel.add(dataLoadingBar, BorderLayout.NORTH);
        footerPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        footerPanel.add(lastUpdateLabel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);

        // Store components for pagination on the PANEL
        panel.putClientProperty("prevButton", prevPageButton);
        panel.putClientProperty("nextButton", nextPageButton);
        panel.putClientProperty("pageLabel", pageLabel);
        panel.putClientProperty("currentPage", 1);

        return panel;
    }

    /**
     * Refresh data table page with pagination
     */
    public void refreshDataTablePage(JPanel dataPanel, JComboBox<String> itemCategoryCombo, int page) {
        SwingUtilities.invokeLater(() -> {
            dataLoadingBar.setString("Fetching page " + page + " data...");
            dataLoadingBar.setIndeterminate(true);

            new Thread(() -> {
                try {
                    Thread.sleep(500);

                    SwingUtilities.invokeLater(() -> {
                        tableModel.setRowCount(0);

                        String category = (String) itemCategoryCombo.getSelectedItem();
                        List<String> items = "F2P Items".equals(category) ? allF2PItems : allP2PItems;

                        int itemsPerPage = 10;
                        int startIndex = (page - 1) * itemsPerPage;
                        int endIndex = Math.min(startIndex + itemsPerPage, items.size());

                        for (int i = startIndex; i < endIndex; i++) {
                            String itemName = items.get(i);

                            ItemSearchAPI.ItemSearchResult result = ItemSearchAPI.searchItem(itemName);

                            if (result != null && result.isValid()) {
                                int buyPrice = result.averagePrice > 0 ? result.averagePrice : result.highPrice;
                                int alchValue = result.alchValue;
                                int profit = alchValue - buyPrice - 220;
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
                        JButton prevButton = (JButton) dataPanel.getClientProperty("prevButton");
                        JButton nextButton = (JButton) dataPanel.getClientProperty("nextButton");
                        JLabel pageLabel = (JLabel) dataPanel.getClientProperty("pageLabel");

                        if (prevButton != null) prevButton.setEnabled(page > 1);
                        if (nextButton != null) nextButton.setEnabled(endIndex < items.size());
                        if (pageLabel != null) pageLabel.setText("Page " + page + " of " + ((items.size() + 9) / 10));

                        dataLoadingBar.setIndeterminate(false);
                        dataLoadingBar.setValue(100);
                        dataLoadingBar.setString("Live data loaded - Page " + page);

                        lastUpdateLabel.setText("Last updated: " + BotUtils.getCurrentTimeString() +
                                " | Showing " + (endIndex - startIndex) + " items");
                    });

                } catch (Exception e) {
                    BotUtils.logError("Error loading page data", e);
                }
            }).start();
        });
    }

    // Getters
    public JTable getDataTable() { return dataTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getRefreshDataButton() { return refreshDataButton; }
    public JLabel getLastUpdateLabel() { return lastUpdateLabel; }
    public JProgressBar getDataLoadingBar() { return dataLoadingBar; }
    public List<String> getAllF2PItems() { return allF2PItems; }
    public List<String> getAllP2PItems() { return allP2PItems; }
}
