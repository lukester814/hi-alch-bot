package Hi_alch.gui.panels;

import Hi_alch.BotUtils;
import Hi_alch.ItemSearchAPI;
import Hi_alch.gui.GUIStyler;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Main configuration panel builder for the GUI
 * Handles item selection, trading config, and bot features
 */
public class MainPanelBuilder {

    private final GUIStyler styler;

    // Main Tab Components
    private JComboBox<String> itemCategoryCombo;
    private JComboBox<String> itemDropdown;
    private JTextField customItemField;
    private JButton validateItemButton;
    private JSpinner buyLimitSpinner;
    private JSpinner priceMarkupSpinner;
    private JSpinner natureRuneSpinner;
    private JCheckBox smartProfitCheck;
    private JCheckBox worldHopCheck;
    private JCheckBox alchConfigCheck;
    private JCheckBox skipBuyingCheck;
    private JCheckBox restockWhenEmptyCheck;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel profitPreviewLabel;

    private int foundCustomItemId = -1;

    // Item lists
    private static final String[] F2P_ITEMS = {
            "Select F2P item...",
            "Rune longsword",
            "Rune battleaxe",
            "Rune 2h sword",
            "Rune platebody",
            "Rune platelegs",
            "Rune plateskirt",
            "Rune chainbody",
            "Rune med helm",
            "Rune full helm",
            "Rune sq shield",
            "Rune kiteshield",
            "Green d'hide body",
            "Green d'hide chaps",
            "Adamant platebody",
            "Adamant platelegs",
            "Custom F2P item..."
    };

    private static final String[] P2P_ITEMS = {
            "Select P2P item...",
            "Dragon longsword",
            "Dragon battleaxe",
            "Dragon dagger",
            "Dragon mace",
            "Dragon scimitar",
            "Dragon sword",
            "Black d'hide body",
            "Black d'hide chaps",
            "Red d'hide body",
            "Red d'hide chaps",
            "Blue d'hide body",
            "Blue d'hide chaps",
            "Rune crossbow",
            "Magic longbow",
            "Yew longbow",
            "Maple longbow",
            "Splitbark body",
            "Splitbark legs",
            "Custom P2P item..."
    };

    public MainPanelBuilder(GUIStyler styler) {
        this.styler = styler;
    }

    /**
     * Create main configuration panel with professional layout
     */
    public JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(createItemSelectionPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createTradingConfigPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createBotFeaturesPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createProfitPreviewPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createControlButtonsPanel());
        panel.add(Box.createVerticalStrut(15));
        panel.add(createStatusPanel());

        return panel;
    }

    private JPanel createItemSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(styler.createProfessionalBorder("Item Selection"));

        // Category selection
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel categoryLabel = styler.createStyledLabel("Category:");
        categoryPanel.add(categoryLabel);
        categoryPanel.add(styler.createQuestionMark("Choose between F2P (free-to-play) or P2P (members) items"));

        String[] categories = {"F2P Items", "P2P Items"};
        itemCategoryCombo = new JComboBox<>(categories);
        itemCategoryCombo.setPreferredSize(new Dimension(120, 30));
        styler.styleComboBox(itemCategoryCombo);
        categoryPanel.add(itemCategoryCombo);

        // Item selection
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel itemLabel = styler.createStyledLabel("Item:");
        itemPanel.add(itemLabel);
        itemPanel.add(styler.createQuestionMark("Select an item with good high alchemy profit potential"));

        itemDropdown = new JComboBox<>(F2P_ITEMS);
        itemDropdown.setPreferredSize(new Dimension(200, 30));
        styler.styleComboBox(itemDropdown);
        itemPanel.add(itemDropdown);

        // Custom item field
        customItemField = new JTextField(15);
        customItemField.setVisible(false);
        customItemField.setToolTipText("Enter custom item name or ID");
        styler.styleTextField(customItemField);

        validateItemButton = new JButton("✓ Validate");
        validateItemButton.setVisible(false);
        validateItemButton.setPreferredSize(new Dimension(90, 30));
        styler.styleButton(validateItemButton, new Color(76, 175, 80));

        JPanel customPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customPanel.add(customItemField);
        customPanel.add(validateItemButton);

        panel.add(categoryPanel, BorderLayout.NORTH);
        panel.add(itemPanel, BorderLayout.CENTER);
        panel.add(customPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTradingConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(styler.createProfessionalBorder("Trading Configuration"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Buy Limit
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel buyLimitLabel = styler.createStyledLabel("Buy Limit:");
        panel.add(buyLimitLabel, gbc);

        gbc.gridx = 1;
        panel.add(styler.createQuestionMark("Maximum number of items to buy from Grand Exchange per 4-hour period."), gbc);

        gbc.gridx = 2;
        buyLimitSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        buyLimitSpinner.setPreferredSize(new Dimension(100, 30));
        styler.styleSpinner(buyLimitSpinner);
        panel.add(buyLimitSpinner, gbc);

        // Price Markup
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel markupLabel = styler.createStyledLabel("Price Markup:");
        panel.add(markupLabel, gbc);

        gbc.gridx = 1;
        panel.add(styler.createQuestionMark("Percentage above market price. 5-10% is optimal."), gbc);

        gbc.gridx = 2;
        priceMarkupSpinner = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 50.0, 0.5));
        priceMarkupSpinner.setPreferredSize(new Dimension(100, 30));
        styler.styleSpinner(priceMarkupSpinner);
        panel.add(priceMarkupSpinner, gbc);

        gbc.gridx = 3;
        JLabel percentLabel = styler.createStyledLabel("%");
        panel.add(percentLabel, gbc);

        // Nature Runes
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel runesLabel = styler.createStyledLabel("Nature Runes:");
        panel.add(runesLabel, gbc);

        gbc.gridx = 1;
        panel.add(styler.createQuestionMark("Number of nature runes to purchase. Each alch requires 1 nature rune."), gbc);

        gbc.gridx = 2;
        natureRuneSpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 10000, 100));
        natureRuneSpinner.setPreferredSize(new Dimension(100, 30));
        styler.styleSpinner(natureRuneSpinner);
        panel.add(natureRuneSpinner, gbc);

        return panel;
    }

    private JPanel createBotFeaturesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(styler.createProfessionalBorder("Bot Features & Configuration"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Smart Profit Analysis
        gbc.gridx = 0; gbc.gridy = 0;
        smartProfitCheck = new JCheckBox("Smart Profit Analysis");
        smartProfitCheck.setSelected(true);
        styler.styleCheckBox(smartProfitCheck);
        panel.add(smartProfitCheck, gbc);

        gbc.gridx = 1;
        panel.add(styler.createQuestionMark("Automatically calculates real-time profit margins."), gbc);

        // World Hopping
        gbc.gridx = 0; gbc.gridy = 1;
        worldHopCheck = new JCheckBox("World Hopping");
        styler.styleCheckBox(worldHopCheck);
        panel.add(worldHopCheck, gbc);

        gbc.gridx = 1;
        panel.add(styler.createQuestionMark("Changes worlds if GE offers take too long."), gbc);

        // Alchemy Configuration
        gbc.gridx = 0; gbc.gridy = 2;
        alchConfigCheck = new JCheckBox("Auto-Configure High Alchemy");
        alchConfigCheck.setSelected(true);
        styler.styleCheckBox(alchConfigCheck);
        panel.add(alchConfigCheck, gbc);

        gbc.gridx = 1;
        panel.add(styler.createQuestionMark("Automatically configures warning threshold."), gbc);

        // Skip Buying Toggle
        gbc.gridx = 0; gbc.gridy = 3;
        skipBuyingCheck = new JCheckBox("Items Already in Inventory");
        skipBuyingCheck.setToolTipText("Skip Grand Exchange buying");
        styler.styleCheckBox(skipBuyingCheck);
        panel.add(skipBuyingCheck, gbc);

        gbc.gridx = 1;
        panel.add(styler.createQuestionMark("Enable if you already have items in inventory."), gbc);

        // Restock When Empty Toggle
        gbc.gridx = 0; gbc.gridy = 4;
        restockWhenEmptyCheck = new JCheckBox("Restock When Empty");
        restockWhenEmptyCheck.setEnabled(false);
        styler.styleCheckBox(restockWhenEmptyCheck);
        panel.add(restockWhenEmptyCheck, gbc);

        gbc.gridx = 1;
        panel.add(styler.createQuestionMark("Bot will buy more items when inventory runs out."), gbc);

        return panel;
    }

    private JPanel createProfitPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(styler.createProfessionalBorder("Profit Preview"));

        profitPreviewLabel = new JLabel("Select an item to see profit analysis", JLabel.CENTER);
        profitPreviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        profitPreviewLabel.setForeground(new Color(255, 215, 0));
        profitPreviewLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(profitPreviewLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        startButton = new JButton("Start High Alchemy Bot");
        startButton.setPreferredSize(new Dimension(180, 45));
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        styler.styleButton(startButton, new Color(76, 175, 80));

        stopButton = new JButton("Stop Bot");
        stopButton.setPreferredSize(new Dimension(120, 45));
        stopButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stopButton.setEnabled(false);
        styler.styleButton(stopButton, new Color(244, 67, 54));

        panel.add(startButton);
        panel.add(stopButton);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(styler.createProfessionalBorder("Bot Status"));

        statusLabel = new JLabel("Ready to start - Configure your settings above", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(statusLabel, BorderLayout.CENTER);

        return panel;
    }

    // Getters for components
    public JComboBox<String> getItemCategoryCombo() { return itemCategoryCombo; }
    public JComboBox<String> getItemDropdown() { return itemDropdown; }
    public JTextField getCustomItemField() { return customItemField; }
    public JButton getValidateItemButton() { return validateItemButton; }
    public JSpinner getBuyLimitSpinner() { return buyLimitSpinner; }
    public JSpinner getPriceMarkupSpinner() { return priceMarkupSpinner; }
    public JSpinner getNatureRuneSpinner() { return natureRuneSpinner; }
    public JCheckBox getSmartProfitCheck() { return smartProfitCheck; }
    public JCheckBox getWorldHopCheck() { return worldHopCheck; }
    public JCheckBox getAlchConfigCheck() { return alchConfigCheck; }
    public JCheckBox getSkipBuyingCheck() { return skipBuyingCheck; }
    public JCheckBox getRestockWhenEmptyCheck() { return restockWhenEmptyCheck; }
    public JButton getStartButton() { return startButton; }
    public JButton getStopButton() { return stopButton; }
    public JLabel getStatusLabel() { return statusLabel; }
    public JLabel getProfitPreviewLabel() { return profitPreviewLabel; }

    public int getFoundCustomItemId() { return foundCustomItemId; }
    public void setFoundCustomItemId(int id) { this.foundCustomItemId = id; }

    public String[] getF2PItems() { return F2P_ITEMS; }
    public String[] getP2PItems() { return P2P_ITEMS; }
}
