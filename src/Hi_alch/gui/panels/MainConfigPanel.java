package Hi_alch.gui.panels;

import Hi_alch.BotUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

import static Hi_alch.GUIStyles.*;

/**
 * Main Configuration Panel for High Alchemy Bot
 *
 * Contains:
 * - Item Selection (F2P/P2P categories, item dropdown, custom item validation)
 * - Trading Configuration (buy limit, price markup, nature runes)
 * - Bot Features (smart profit, world hopping, alch config, skip buying, restock)
 * - Profit Preview
 * - Control Buttons (start/stop)
 * - Status Display
 *
 * Extracted from AlchBotGUI to reduce class size and improve maintainability.
 */
public class MainConfigPanel extends JPanel {

    // ===========================================
    // GUI COMPONENTS
    // ===========================================

    // Item Selection Components
    private JComboBox<String> itemCategoryCombo;
    private JComboBox<String> itemDropdown;
    private JTextField customItemField;
    private JButton validateItemButton;

    // Trading Configuration Components
    private JSpinner buyLimitSpinner;
    private JSpinner priceMarkupSpinner;
    private JSpinner natureRuneSpinner;

    // Bot Features Components
    private JCheckBox smartProfitCheck;
    private JCheckBox worldHopCheck;
    private JCheckBox alchConfigCheck;
    private JCheckBox skipBuyingCheck;
    private JCheckBox restockWhenEmptyCheck;

    // Display Components
    private JLabel profitPreviewLabel;
    private JLabel statusLabel;

    // Control Components
    private JButton startButton;
    private JButton stopButton;

    // Store the found custom item ID
    private int foundCustomItemId = -1;

    // ===========================================
    // ITEM CATEGORIES - F2P AND P2P
    // ===========================================

    private static final String[] F2P_ITEMS = {
        "Custom Item (Enter Below)",
        "Rune 2h Sword",
        "Rune Battleaxe",
        "Rune Longsword",
        "Rune Scimitar",
        "Rune Platebody",
        "Rune Platelegs",
        "Rune Plateskirt",
        "Rune Kiteshield",
        "Rune Full Helm",
        "Green D'hide Body",
        "Splitbark Body",
        "Adamant Platebody"
    };

    private static final String[] P2P_ITEMS = {
        "Custom Item (Enter Below)",
        "Dragon Longsword",
        "Dragon Dagger",
        "Dragon Battleaxe",
        "Black D'hide Body",
        "Granite Maul",
        "Amulet of Power",
        "Mithril Platebody (t)",
        "Adamant Platebody (t)",
        "Rune Platebody (t)",
        "Air Battlestaff",
        "Water Battlestaff",
        "Earth Battlestaff",
        "Fire Battlestaff"
    };

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public MainConfigPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createItemSelectionPanel());
        add(Box.createVerticalStrut(20));
        add(createTradingConfigPanel());
        add(Box.createVerticalStrut(20));
        add(createBotFeaturesPanel());
        add(Box.createVerticalStrut(20));
        add(createProfitPreviewPanel());
        add(Box.createVerticalStrut(20));
        add(createControlButtonsPanel());
        add(Box.createVerticalStrut(15));
        add(createStatusPanel());

        BotUtils.log("✅ MainConfigPanel initialized");
    }

    // ===========================================
    // PANEL CREATION METHODS
    // ===========================================

    /**
     * Create professional item selection panel
     */
    private JPanel createItemSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createProfessionalBorder("Item Selection"));

        // Category selection
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel categoryLabel = createStyledLabel("Category:");
        categoryPanel.add(categoryLabel);
        categoryPanel.add(createQuestionMark("Choose between F2P (free-to-play) or P2P (members) items"));

        String[] categories = {"F2P Items", "P2P Items"};
        itemCategoryCombo = new JComboBox<>(categories);
        itemCategoryCombo.setPreferredSize(new Dimension(120, 30));
        styleComboBox(itemCategoryCombo);
        categoryPanel.add(itemCategoryCombo);

        // Item selection
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel itemLabel = createStyledLabel("Item:");
        itemPanel.add(itemLabel);
        itemPanel.add(createQuestionMark("Select an item with good high alchemy profit potential"));

        itemDropdown = new JComboBox<>(F2P_ITEMS);
        itemDropdown.setPreferredSize(new Dimension(200, 30));
        styleComboBox(itemDropdown);
        itemPanel.add(itemDropdown);

        // Custom item field (initially hidden)
        customItemField = new JTextField(15);
        customItemField.setVisible(false);
        customItemField.setToolTipText("Enter custom item name or ID");
        styleTextField(customItemField);

        validateItemButton = new JButton("✓ Validate");
        validateItemButton.setVisible(false);
        validateItemButton.setPreferredSize(new Dimension(90, 30));
        styleButton(validateItemButton, new Color(76, 175, 80));

        JPanel customPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customPanel.add(customItemField);
        customPanel.add(validateItemButton);

        panel.add(categoryPanel, BorderLayout.NORTH);
        panel.add(itemPanel, BorderLayout.CENTER);
        panel.add(customPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create professional trading configuration panel
     */
    private JPanel createTradingConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createProfessionalBorder("Trading Configuration"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Buy Limit
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel buyLimitLabel = createStyledLabel("Buy Limit:");
        panel.add(buyLimitLabel, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Maximum number of items to buy from Grand Exchange per 4-hour period. Each item has a different buy limit (usually 100-1000). Exceeding this will prevent further purchases."), gbc);

        gbc.gridx = 2;
        buyLimitSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        buyLimitSpinner.setPreferredSize(new Dimension(100, 30));
        styleSpinner(buyLimitSpinner);
        panel.add(buyLimitSpinner, gbc);

        // Price Markup
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel markupLabel = createStyledLabel("Price Markup:");
        panel.add(markupLabel, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Percentage above current market price to offer. Higher markup = faster purchase but higher cost. 5-10% is usually optimal for quick buying without overpaying significantly."), gbc);

        gbc.gridx = 2;
        priceMarkupSpinner = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 50.0, 0.5));
        priceMarkupSpinner.setPreferredSize(new Dimension(100, 30));
        styleSpinner(priceMarkupSpinner);
        panel.add(priceMarkupSpinner, gbc);

        gbc.gridx = 3;
        JLabel percentLabel = createStyledLabel("%");
        panel.add(percentLabel, gbc);

        // Nature Runes
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel runesLabel = createStyledLabel("Nature Runes:");
        panel.add(runesLabel, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Number of nature runes to purchase for High Level Alchemy. Each alch requires 1 nature rune + fire staff/runes. 1000 runes = 1000 alchs. Buy in bulk for better prices."), gbc);

        gbc.gridx = 2;
        natureRuneSpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 10000, 100));
        natureRuneSpinner.setPreferredSize(new Dimension(100, 30));
        styleSpinner(natureRuneSpinner);
        panel.add(natureRuneSpinner, gbc);

        return panel;
    }

    /**
     * Create professional bot features panel
     */
    private JPanel createBotFeaturesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createProfessionalBorder("Bot Features & Configuration"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Smart Profit Analysis
        gbc.gridx = 0; gbc.gridy = 0;
        smartProfitCheck = new JCheckBox("Smart Profit Analysis");
        smartProfitCheck.setSelected(true);
        styleCheckBox(smartProfitCheck);
        panel.add(smartProfitCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Automatically calculates real-time profit margins using live market data before each alching session. Prevents losses from market fluctuations."), gbc);

        // World Hopping
        gbc.gridx = 0; gbc.gridy = 1;
        worldHopCheck = new JCheckBox("World Hopping");
        styleCheckBox(worldHopCheck);
        panel.add(worldHopCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Automatically changes worlds if Grand Exchange offers take too long to complete. Improves buying efficiency and reduces wait times."), gbc);

        // Alchemy Configuration
        gbc.gridx = 0; gbc.gridy = 2;
        alchConfigCheck = new JCheckBox("Auto-Configure High Alchemy");
        alchConfigCheck.setSelected(true);
        styleCheckBox(alchConfigCheck);
        panel.add(alchConfigCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Automatically right-clicks High Level Alchemy spell to configure the warning threshold above 10M GP. Prevents confirmation dialogs during alching."), gbc);

        // Skip Buying Toggle
        gbc.gridx = 0; gbc.gridy = 3;
        skipBuyingCheck = new JCheckBox("Items Already in Inventory");
        skipBuyingCheck.setToolTipText("Skip Grand Exchange buying if items are already in inventory");
        styleCheckBox(skipBuyingCheck);
        panel.add(skipBuyingCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("Enable this if you already have items in your inventory and want to skip the Grand Exchange buying phase. Bot will start alching immediately."), gbc);

        // Restock When Empty Toggle
        gbc.gridx = 0; gbc.gridy = 4;
        restockWhenEmptyCheck = new JCheckBox("Restock When Empty");
        restockWhenEmptyCheck.setEnabled(false); // Disabled by default
        restockWhenEmptyCheck.setToolTipText("When enabled, bot will buy more items when inventory runs out");
        styleCheckBox(restockWhenEmptyCheck);
        panel.add(restockWhenEmptyCheck, gbc);

        gbc.gridx = 1;
        panel.add(createQuestionMark("When enabled, the bot will buy more items from GE when inventory runs out. When disabled, bot stops when all items are alched."), gbc);

        return panel;
    }

    /**
     * Create profit preview panel
     */
    private JPanel createProfitPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createProfessionalBorder("Profit Preview"));

        profitPreviewLabel = new JLabel("Select an item to see profit analysis", JLabel.CENTER);
        profitPreviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        profitPreviewLabel.setForeground(new Color(255, 215, 0));
        profitPreviewLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(profitPreviewLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create professional control buttons panel
     */
    private JPanel createControlButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        startButton = new JButton("Start High Alchemy Bot");
        startButton.setPreferredSize(new Dimension(180, 45));
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        styleButton(startButton, new Color(76, 175, 80));

        stopButton = new JButton("Stop Bot");
        stopButton.setPreferredSize(new Dimension(120, 45));
        stopButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stopButton.setEnabled(false);
        styleButton(stopButton, new Color(244, 67, 54));

        panel.add(startButton);
        panel.add(stopButton);

        return panel;
    }

    /**
     * Create professional status panel
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createProfessionalBorder("Bot Status"));

        statusLabel = new JLabel("Ready to start - Configure your settings above", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(statusLabel, BorderLayout.CENTER);

        return panel;
    }

    // ===========================================
    // PUBLIC GETTERS FOR VALUES
    // ===========================================

    public String getSelectedCategory() {
        return (String) itemCategoryCombo.getSelectedItem();
    }

    public String getSelectedItem() {
        return (String) itemDropdown.getSelectedItem();
    }

    public String getCustomItem() {
        return customItemField.getText();
    }

    public int getBuyLimit() {
        return (Integer) buyLimitSpinner.getValue();
    }

    public double getPriceMarkup() {
        return (Double) priceMarkupSpinner.getValue();
    }

    public int getNatureRunes() {
        return (Integer) natureRuneSpinner.getValue();
    }

    public boolean isSmartProfitEnabled() {
        return smartProfitCheck.isSelected();
    }

    public boolean isWorldHopEnabled() {
        return worldHopCheck.isSelected();
    }

    public boolean isAlchConfigEnabled() {
        return alchConfigCheck.isSelected();
    }

    public boolean isSkipBuyingEnabled() {
        return skipBuyingCheck.isSelected();
    }

    public boolean isRestockEnabled() {
        return restockWhenEmptyCheck.isSelected();
    }

    public int getFoundCustomItemId() {
        return foundCustomItemId;
    }

    // ===========================================
    // PUBLIC SETTERS FOR UI UPDATES
    // ===========================================

    public void setFoundCustomItemId(int itemId) {
        this.foundCustomItemId = itemId;
    }

    public void setItemCategory(String category) {
        itemCategoryCombo.setSelectedItem(category);
    }

    public void setItemDropdown(String[] items) {
        itemDropdown.setModel(new DefaultComboBoxModel<>(items));
    }

    public void setCustomItemFieldVisible(boolean visible) {
        customItemField.setVisible(visible);
        validateItemButton.setVisible(visible);
    }

    public void setRestockEnabled(boolean enabled) {
        restockWhenEmptyCheck.setEnabled(enabled);
    }

    public void setProfitPreview(String text) {
        profitPreviewLabel.setText(text);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
        statusLabel.setForeground(getStatusColor(status));
    }

    public void setStartButtonEnabled(boolean enabled) {
        startButton.setEnabled(enabled);
    }

    public void setStopButtonEnabled(boolean enabled) {
        stopButton.setEnabled(enabled);
    }

    // ===========================================
    // PUBLIC GETTERS FOR COMPONENTS (for event handling)
    // ===========================================

    public JComboBox<String> getItemCategoryCombo() {
        return itemCategoryCombo;
    }

    public JComboBox<String> getItemDropdown() {
        return itemDropdown;
    }

    public JTextField getCustomItemField() {
        return customItemField;
    }

    public JButton getValidateItemButton() {
        return validateItemButton;
    }

    public JCheckBox getSkipBuyingCheck() {
        return skipBuyingCheck;
    }

    public JCheckBox getRestockWhenEmptyCheck() {
        return restockWhenEmptyCheck;
    }

    public JCheckBox getAlchConfigCheck() {
        return alchConfigCheck;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JSpinner getBuyLimitSpinner() {
        return buyLimitSpinner;
    }

    public JSpinner getPriceMarkupSpinner() {
        return priceMarkupSpinner;
    }

    public JSpinner getNatureRuneSpinner() {
        return natureRuneSpinner;
    }

    public JCheckBox getSmartProfitCheck() {
        return smartProfitCheck;
    }

    public JCheckBox getWorldHopCheck() {
        return worldHopCheck;
    }

    public JLabel getProfitPreviewLabel() {
        return profitPreviewLabel;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    // ===========================================
    // HELPER METHODS
    // ===========================================

    public String[] getF2PItems() {
        return F2P_ITEMS;
    }

    public String[] getP2PItems() {
        return P2P_ITEMS;
    }
}
