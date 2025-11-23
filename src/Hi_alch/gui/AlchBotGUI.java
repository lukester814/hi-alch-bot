package Hi_alch.gui;

import Hi_alch.gui.*;
import Hi_alch.gui.panels.*;
import javax.swing.*;
import java.awt.*;
/**
 * Professional High Alchemy Bot GUI - REFACTORED
 * Coordinates panel builders and event handlers
 * All heavy lifting delegated to specialized classes
 */
public class AlchBotGUI {
    // Core components
    private JFrame frame;
    private JTabbedPane tabbedPane;
    // Builders and managers
    private final GUIStyler styler = new GUIStyler();
    private MainPanelBuilder mainPanelBuilder;
    private SettingsPanelBuilder settingsPanelBuilder;
    private DataPanelBuilder dataPanelBuilder;
    private EventHandlerManager eventManager;
    // ===========================================
    // CONSTRUCTOR
    public AlchBotGUI() {
        BotUtils.log("🎨 Creating professional GUI (Refactored)...");
        try {
            initializeGUI();
            BotUtils.log("✅ GUI created successfully");
        } catch (Exception e) {
            BotUtils.logError("Error creating GUI", e);
            throw new RuntimeException("Failed to initialize GUI", e);
        }
    }
    private void initializeGUI() {
        setLookAndFeel();
        createMainFrame();
        createTabbedPane();
        setupEventHandlers();
        finalizeFrame();
    private void setLookAndFeel() {
        BotUtils.log("🎨 Using DreamBot's Substance theme");
    private void createMainFrame() {
        frame = new JFrame("OSRS High Alchemy Bot v2.0");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(450, 700);
        frame.setMinimumSize(new Dimension(420, 650));
        frame.setResizable(true);
        frame.setAlwaysOnTop(true);
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        // Initialize panel builders
        mainPanelBuilder = new MainPanelBuilder(styler);
        settingsPanelBuilder = new SettingsPanelBuilder(styler);
        dataPanelBuilder = new DataPanelBuilder(styler);
        // Create scroll panes for panels
        JScrollPane mainScrollPane = createScrollPane(mainPanelBuilder.createMainPanel());
        JScrollPane settingsScrollPane = createScrollPane(settingsPanelBuilder.createSettingsPanel());
        JScrollPane dataScrollPane = createScrollPane(dataPanelBuilder.createDataPanel());
        // Add tabs
        tabbedPane.addTab("Configuration", null, mainScrollPane, "Main configuration");
        tabbedPane.addTab("Advanced", null, settingsScrollPane, "Advanced settings");
        tabbedPane.addTab("Analytics", null, dataScrollPane, "Market data");
        tabbedPane.setSelectedIndex(0);
        // Tab change listener
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2 && dataPanelBuilder.getTableModel().getRowCount() == 0) {
                SwingUtilities.invokeLater(this::refreshDataTable);
            }
        });
    private JScrollPane createScrollPane(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    private void setupEventHandlers() {
        eventManager = new EventHandlerManager(
                mainPanelBuilder,
                settingsPanelBuilder,
                dataPanelBuilder,
                frame,
                tabbedPane
        );
        eventManager.setupAllHandlers();
    private void finalizeFrame() {
        frame.add(tabbedPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
    // PUBLIC INTERFACE
    public void setEventListener(GUIEventListener listener) {
        eventManager.setEventListener(listener);
    public void show() {
        showGUI();
    public void showGUI() {
        if (frame != null) {
            frame.setVisible(true);
            frame.toFront();
            frame.requestFocus();
            positionOverChatBox();
            BotUtils.log("✅ GUI displayed");
    private void positionOverChatBox() {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int chatBoxY = screenSize.height < 900 ? 50 : (screenSize.height >= 1200 ? 400 : 300);
            frame.setLocation(25, chatBoxY);
            frame.setLocation(25, 25);
    public void hideGUI() {
            frame.setVisible(false);
    public void updateStatus(String status) {
        if (mainPanelBuilder.getStatusLabel() != null) {
            SwingUtilities.invokeLater(() -> {
                mainPanelBuilder.getStatusLabel().setText(status);
                Color color = new Color(64, 128, 255); // Default blue
                if (status.contains("✅") || status.contains("successful")) {
                    color = new Color(76, 175, 80); // Green
                } else if (status.contains("❌") || status.contains("failed")) {
                    color = new Color(244, 67, 54); // Red
                }
                mainPanelBuilder.getStatusLabel().setForeground(color);
            });
    public void updateButtonStates(boolean botRunning) {
        eventManager.updateButtonStates(botRunning);
    public GUIConfiguration getCurrentConfiguration() {
        GUIConfiguration config = new GUIConfiguration();
        // Get selected item
        String selectedItem = (String) mainPanelBuilder.getItemDropdown().getSelectedItem();
        if (selectedItem != null && selectedItem.contains("Custom")) {
            config.selectedItemName = mainPanelBuilder.getCustomItemField().getText().trim();
            config.selectedItemId = mainPanelBuilder.getFoundCustomItemId();
        } else {
            config.selectedItemName = selectedItem;
            config.selectedItemId = getItemIdFromName(selectedItem);
        // Trading config
        config.buyLimit = (Integer) mainPanelBuilder.getBuyLimitSpinner().getValue();
        config.priceMarkup = (Double) mainPanelBuilder.getPriceMarkupSpinner().getValue();
        config.natureRuneAmount = (Integer) mainPanelBuilder.getNatureRuneSpinner().getValue();
        // Bot features
        config.smartProfitEnabled = mainPanelBuilder.getSmartProfitCheck().isSelected();
        config.worldHopEnabled = mainPanelBuilder.getWorldHopCheck().isSelected();
        config.alchConfigEnabled = mainPanelBuilder.getAlchConfigCheck().isSelected();
        config.skipBuying = mainPanelBuilder.getSkipBuyingCheck().isSelected();
        config.restockWhenEmpty = mainPanelBuilder.getRestockWhenEmptyCheck() != null ?
                                   mainPanelBuilder.getRestockWhenEmptyCheck().isSelected() : false;
        // Discord settings
        config.discordWebhookUrl = settingsPanelBuilder.getWebhookField().getText().trim();
        config.discordNotificationsEnabled = settingsPanelBuilder.getEnableDiscordCheck().isSelected();
        // Anti-ban settings
        config.antibanEnabled = settingsPanelBuilder.getEnableAntibanCheck().isSelected();
        config.antibanAggression = settingsPanelBuilder.getAntibanAggressionSlider().getValue();
        config.breakSystemEnabled = settingsPanelBuilder.getBreakSystemCheck().isSelected();
        config.minBreakMinutes = (Integer) settingsPanelBuilder.getMinBreakSpinner().getValue();
        config.maxBreakMinutes = (Integer) settingsPanelBuilder.getMaxBreakSpinner().getValue();
        config.fatigueSystemEnabled = settingsPanelBuilder.getFatigueSystemCheck().isSelected();
        config.profileSeedingEnabled = settingsPanelBuilder.getProfileSeedingCheck().isSelected();
        config.userProfileSeed = settingsPanelBuilder.getUserProfileField().getText().trim();
        return config;
    public void applyConfiguration(GUIConfiguration config) {
        // Apply configuration to all components
        SwingUtilities.invokeLater(() -> {
            if (config.selectedItemName != null) {
                // Find and select item in dropdown
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) mainPanelBuilder.getItemDropdown().getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (config.selectedItemName.equals(model.getElementAt(i))) {
                        mainPanelBuilder.getItemDropdown().setSelectedIndex(i);
                        break;
                    }
            mainPanelBuilder.getBuyLimitSpinner().setValue(config.buyLimit);
            mainPanelBuilder.getPriceMarkupSpinner().setValue(config.priceMarkup);
            mainPanelBuilder.getNatureRuneSpinner().setValue(config.natureRuneAmount);
            mainPanelBuilder.getSmartProfitCheck().setSelected(config.smartProfitEnabled);
            mainPanelBuilder.getWorldHopCheck().setSelected(config.worldHopEnabled);
            mainPanelBuilder.getAlchConfigCheck().setSelected(config.alchConfigEnabled);
            mainPanelBuilder.getSkipBuyingCheck().setSelected(config.skipBuying);
            if (config.discordWebhookUrl != null) {
                settingsPanelBuilder.getWebhookField().setText(config.discordWebhookUrl);
            settingsPanelBuilder.getEnableDiscordCheck().setSelected(config.discordNotificationsEnabled);
            settingsPanelBuilder.getEnableAntibanCheck().setSelected(config.antibanEnabled);
            settingsPanelBuilder.getAntibanAggressionSlider().setValue(config.antibanAggression);
            settingsPanelBuilder.getBreakSystemCheck().setSelected(config.breakSystemEnabled);
            settingsPanelBuilder.getMinBreakSpinner().setValue(config.minBreakMinutes);
            settingsPanelBuilder.getMaxBreakSpinner().setValue(config.maxBreakMinutes);
            settingsPanelBuilder.getFatigueSystemCheck().setSelected(config.fatigueSystemEnabled);
            settingsPanelBuilder.getProfileSeedingCheck().setSelected(config.profileSeedingEnabled);
            if (config.userProfileSeed != null) {
                settingsPanelBuilder.getUserProfileField().setText(config.userProfileSeed);
            eventManager.updateProfitPreview();
    public void refreshDataTable() {
        JPanel dataPanel = (JPanel) ((JScrollPane) tabbedPane.getComponentAt(2)).getViewport().getView();
        dataPanelBuilder.refreshDataTablePage(dataPanel, mainPanelBuilder.getItemCategoryCombo(), 1);
    public void updateFatigueLevel(int fatiguePercent) {
            int adjusted = Math.max(0, Math.min(100, fatiguePercent));
            settingsPanelBuilder.getFatigueBar().setValue(adjusted);
            String status;
            Color color;
            if (adjusted <= 20) {
                status = "Fresh & Alert";
                color = new Color(34, 197, 94);
            } else if (adjusted <= 50) {
                status = "Slightly Tired";
                color = new Color(251, 191, 36);
            } else if (adjusted <= 80) {
                status = "Getting Tired";
                color = new Color(249, 115, 22);
            } else {
                status = "Very Fatigued";
                color = new Color(239, 68, 68);
            settingsPanelBuilder.getFatigueBar().setString(adjusted + "% - " + status);
            settingsPanelBuilder.getFatigueBar().setForeground(color);
    public void resetFatigueLevel() {
        updateFatigueLevel(0);
        updateStatus("😌 Fatigue reset");
    public boolean isAlchemyConfigurationEnabled() {
        return mainPanelBuilder.getAlchConfigCheck() != null && mainPanelBuilder.getAlchConfigCheck().isSelected();
    public void bringToFront() {
        if (frame != null && frame.isVisible()) {
                frame.toFront();
                frame.requestFocus();
    public boolean isVisible() {
        return frame != null && frame.isVisible();
    public void dispose() {
            if (frame != null) {
                frame.setVisible(false);
                frame.dispose();
            styler.cleanup();
            BotUtils.logError("Error disposing GUI", e);
    private int getItemIdFromName(String itemName) {
        if (itemName == null) return -1;
        String nameLower = itemName.toLowerCase().trim();
        switch (nameLower) {
            case "rune longsword": return 1303;
            case "rune 2h sword": return 1319;
            case "rune platebody": return 1127;
            case "rune platelegs": return 1079;
            case "dragon longsword": return 1305;
            case "dragon battleaxe": return 1377;
            case "dragon dagger": return 1215;
            default: return -1;
}
