package Hi_alch.gui;

import Hi_alch.utils.BotUtils;
import Hi_alch.api.ItemSearchAPI;
import Hi_alch.gui.panels.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Manages all GUI event handlers
 * Separates event handling logic from GUI construction
 */
public class EventHandlerManager {

    private final MainPanelBuilder mainPanel;
    private final SettingsPanelBuilder settingsPanel;
    private final DataPanelBuilder dataPanel;
    private final JFrame frame;
    private final JTabbedPane tabbedPane;
    private GUIEventListener eventListener;

    public EventHandlerManager(MainPanelBuilder mainPanel, SettingsPanelBuilder settingsPanel,
                               DataPanelBuilder dataPanel, JFrame frame, JTabbedPane tabbedPane) {
        this.mainPanel = mainPanel;
        this.settingsPanel = settingsPanel;
        this.dataPanel = dataPanel;
        this.frame = frame;
        this.tabbedPane = tabbedPane;
    }

    public void setEventListener(GUIEventListener listener) {
        this.eventListener = listener;
    }

    public void setupAllHandlers() {
        setupCategoryHandler();
        setupItemDropdownHandler();
        setupCustomItemHandler();
        setupSkipBuyingHandler();
        setupAggressionSliderHandler();
        setupAlchemyConfigHandler();
        setupGenerateSeedHandler();
        setupStartStopHandlers();
        setupWebhookHandler();
        setupDataRefreshHandler();
        setupConfigurationChangeHandlers();
    }

    private void setupCategoryHandler() {
        mainPanel.getItemCategoryCombo().addActionListener(e -> {
            String selected = (String) mainPanel.getItemCategoryCombo().getSelectedItem();
            if ("F2P Items".equals(selected)) {
                mainPanel.getItemDropdown().setModel(new DefaultComboBoxModel<>(mainPanel.getF2PItems()));
            } else {
                mainPanel.getItemDropdown().setModel(new DefaultComboBoxModel<>(mainPanel.getP2PItems()));
            }
            mainPanel.getItemDropdown().setSelectedIndex(0);
            updateProfitPreview();
        });
    }

    private void setupItemDropdownHandler() {
        mainPanel.getItemDropdown().addActionListener(e -> {
            String selected = (String) mainPanel.getItemDropdown().getSelectedItem();
            boolean isCustom = selected != null && selected.contains("Custom");

            mainPanel.getCustomItemField().setVisible(isCustom);
            mainPanel.getValidateItemButton().setVisible(isCustom);

            frame.revalidate();
            frame.repaint();

            updateProfitPreview();

            if (eventListener != null && selected != null && !selected.startsWith("Select")) {
                eventListener.onItemSelected(selected);
            }
        });
    }

    private void setupCustomItemHandler() {
        mainPanel.getValidateItemButton().addActionListener(e -> validateCustomItem());
    }

    private void setupSkipBuyingHandler() {
        mainPanel.getSkipBuyingCheck().addActionListener(e -> {
            boolean skipBuyingEnabled = mainPanel.getSkipBuyingCheck().isSelected();

            if (mainPanel.getRestockWhenEmptyCheck() != null) {
                mainPanel.getRestockWhenEmptyCheck().setEnabled(skipBuyingEnabled);
                if (!skipBuyingEnabled) {
                    mainPanel.getRestockWhenEmptyCheck().setSelected(false);
                }
            }

            toggleBuyingPanels(!skipBuyingEnabled);
            updateProfitPreview();

            if (skipBuyingEnabled) {
                updateStatus("💡 Skip buying enabled");
            } else {
                updateStatus("🛒 Skip buying disabled");
            }
        });
    }

    private void setupAggressionSliderHandler() {
        settingsPanel.getAntibanAggressionSlider().addChangeListener(e -> {
            int value = settingsPanel.getAntibanAggressionSlider().getValue();
            String description;
            Color color;
            if (value <= 3) {
                description = "Very Human-like";
                color = new Color(34, 197, 94);
            } else if (value <= 6) {
                description = "Balanced";
                color = new Color(251, 191, 36);
            } else {
                description = "Fast & Risky";
                color = new Color(239, 68, 68);
            }
            settingsPanel.getAggressionValueLabel().setText("Level: " + value + " (" + description + ")");
            settingsPanel.getAggressionValueLabel().setForeground(color);
        });
    }

    private void setupAlchemyConfigHandler() {
        mainPanel.getAlchConfigCheck().addActionListener(e -> {
            boolean enabled = mainPanel.getAlchConfigCheck().isSelected();
            if (enabled) {
                updateStatus("Right-click alchemy auto-configuration enabled");
            } else {
                updateStatus("Right-click alchemy auto-configuration disabled");
            }

            if (eventListener != null) {
                eventListener.onAlchemyConfigurationChanged(enabled);
            }
        });
    }

    private void setupGenerateSeedHandler() {
        settingsPanel.getGenerateSeedButton().addActionListener(e -> {
            settingsPanel.getGenerateSeedButton().setEnabled(false);
            settingsPanel.getGenerateSeedButton().setText("...");

            new Thread(() -> {
                try {
                    Thread.sleep(1000);

                    SwingUtilities.invokeLater(() -> {
                        String seed = generateUniqueProfileSeed();
                        settingsPanel.getUserProfileField().setText(seed);
                        updateStatus("Generated personalized anti-ban seed: " + seed);

                        settingsPanel.getGenerateSeedButton().setEnabled(true);
                        settingsPanel.getGenerateSeedButton().setText("Generate");
                    });

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    private void setupStartStopHandlers() {
        mainPanel.getStartButton().addActionListener(e -> {
            if (eventListener != null) {
                GUIConfiguration config = getCurrentConfiguration();
                eventListener.onStartBot(config);
                updateButtonStates(true);
            }
        });

        mainPanel.getStopButton().addActionListener(e -> {
            if (eventListener != null) {
                eventListener.onStopBot();
                updateButtonStates(false);
            }
        });
    }

    private void setupWebhookHandler() {
        settingsPanel.getTestWebhookButton().addActionListener(e -> testWebhookWithFeedback());
    }

    private void setupDataRefreshHandler() {
        dataPanel.getRefreshDataButton().addActionListener(e -> {
            JPanel dataPanelComponent = (JPanel) ((JScrollPane) tabbedPane.getComponentAt(2)).getViewport().getView();
            dataPanel.refreshDataTablePage(dataPanelComponent, mainPanel.getItemCategoryCombo(), 1);
        });
    }

    private void setupConfigurationChangeHandlers() {
        mainPanel.getBuyLimitSpinner().addChangeListener(e -> {
            updateProfitPreview();
            if (eventListener != null) eventListener.onConfigurationChanged();
        });

        mainPanel.getPriceMarkupSpinner().addChangeListener(e -> {
            updateProfitPreview();
            if (eventListener != null) eventListener.onConfigurationChanged();
        });
    }

    // Helper methods
    private void validateCustomItem() {
        String customItem = mainPanel.getCustomItemField().getText().trim();
        if (customItem.isEmpty()) {
            updateStatus("❌ Please enter an item name to validate");
            return;
        }

        mainPanel.getValidateItemButton().setEnabled(false);
        mainPanel.getValidateItemButton().setText("⏳");

        new Thread(() -> {
            try {
                ItemSearchAPI.ItemSearchResult result = ItemSearchAPI.searchItem(customItem);
                boolean isValid = result != null && result.isValid();

                SwingUtilities.invokeLater(() -> {
                    if (isValid) {
                        updateStatus("✅ Item '" + customItem + "' validated!");
                        mainPanel.getCustomItemField().setBackground(new Color(34, 197, 94, 50));
                        mainPanel.setFoundCustomItemId(result.itemId);
                        if (eventListener != null) {
                            eventListener.onItemSelected(customItem);
                        }
                        updateProfitPreview();
                    } else {
                        updateStatus("❌ Item not found");
                        mainPanel.getCustomItemField().setBackground(new Color(239, 68, 68, 50));
                    }

                    mainPanel.getValidateItemButton().setEnabled(true);
                    mainPanel.getValidateItemButton().setText("✓ Validate");
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    mainPanel.getValidateItemButton().setEnabled(true);
                    mainPanel.getValidateItemButton().setText("✓ Validate");
                });
            }
        }).start();
    }

    public void updateProfitPreview() {
        String selectedItem = (String) mainPanel.getItemDropdown().getSelectedItem();

        if (selectedItem != null && selectedItem.contains("Custom")) {
            String customItem = mainPanel.getCustomItemField().getText().trim();
            if (!customItem.isEmpty() && mainPanel.getFoundCustomItemId() > 0) {
                selectedItem = customItem;
            } else {
                mainPanel.getProfitPreviewLabel().setText("Enter and validate custom item");
                mainPanel.getProfitPreviewLabel().setForeground(new Color(251, 191, 36));
                return;
            }
        }

        int buyLimit = (Integer) mainPanel.getBuyLimitSpinner().getValue();
        double markup = (Double) mainPanel.getPriceMarkupSpinner().getValue();

        ProfitCalculator.ProfitResult result = ProfitCalculator.calculateProfit(selectedItem, buyLimit, markup);
        mainPanel.getProfitPreviewLabel().setText(result.text);
        mainPanel.getProfitPreviewLabel().setForeground(result.color);
    }

    private void toggleBuyingPanels(boolean showBuying) {
        SwingUtilities.invokeLater(() -> {
            Component mainTab = tabbedPane.getComponentAt(0);

            if (mainTab instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) mainTab;
                Component viewport = scrollPane.getViewport().getView();

                if (viewport instanceof JPanel) {
                    toggleBuyingComponentsInPanel((JPanel) viewport, showBuying);
                }
            }

            tabbedPane.revalidate();
            tabbedPane.repaint();
        });
    }

    private void toggleBuyingComponentsInPanel(JPanel panel, boolean showBuying) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel subPanel = (JPanel) comp;

                if (subPanel.getBorder() instanceof TitledBorder) {
                    TitledBorder border = (TitledBorder) subPanel.getBorder();
                    if (border.getTitle().contains("Trading Configuration")) {
                        subPanel.setVisible(showBuying);
                    }
                }

                toggleBuyingComponentsInPanel(subPanel, showBuying);
            }
        }
    }

    private void testWebhookWithFeedback() {
        String webhookUrl = settingsPanel.getWebhookField().getText().trim();

        if (webhookUrl.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter webhook URL!", "Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        settingsPanel.getTestWebhookButton().setEnabled(false);
        settingsPanel.getTestWebhookButton().setText("Testing...");

        new Thread(() -> {
            try {
                if (eventListener != null) {
                    boolean success = eventListener.onTestWebhook(webhookUrl);

                    SwingUtilities.invokeLater(() -> {
                        if (success) {
                            JOptionPane.showMessageDialog(frame, "✅ Webhook test successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateStatus("✅ Discord webhook connected!");
                        } else {
                            JOptionPane.showMessageDialog(frame, "❌ Webhook test failed!", "Failed", JOptionPane.ERROR_MESSAGE);
                            updateStatus("❌ Discord webhook test failed");
                        }

                        settingsPanel.getTestWebhookButton().setEnabled(true);
                        settingsPanel.getTestWebhookButton().setText("Test");
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    settingsPanel.getTestWebhookButton().setEnabled(true);
                    settingsPanel.getTestWebhookButton().setText("Test");
                });
            }
        }).start();
    }

    public void updateButtonStates(boolean botRunning) {
        SwingUtilities.invokeLater(() -> {
            mainPanel.getStartButton().setEnabled(!botRunning);
            mainPanel.getStopButton().setEnabled(botRunning);

            if (botRunning) {
                updateStatus("Bot running");
                mainPanel.getStartButton().setText("Bot Running...");
                frame.setVisible(false);
            } else {
                updateStatus("Bot stopped");
                mainPanel.getStartButton().setText("Start High Alchemy Bot");
                frame.setVisible(true);
                frame.toFront();
            }
        });
    }

    private void updateStatus(String status) {
        if (mainPanel.getStatusLabel() != null) {
            SwingUtilities.invokeLater(() -> {
                mainPanel.getStatusLabel().setText(status);

                if (status.contains("✅")) {
                    mainPanel.getStatusLabel().setForeground(new Color(76, 175, 80));
                } else if (status.contains("❌")) {
                    mainPanel.getStatusLabel().setForeground(new Color(244, 67, 54));
                } else if (status.contains("🚀")) {
                    mainPanel.getStatusLabel().setForeground(new Color(64, 128, 255));
                }
            });
        }
    }

    private GUIConfiguration getCurrentConfiguration() {
        GUIConfiguration config = new GUIConfiguration();

        String selectedItem = (String) mainPanel.getItemDropdown().getSelectedItem();
        if (selectedItem != null && selectedItem.contains("Custom")) {
            config.selectedItemName = mainPanel.getCustomItemField().getText().trim();
            config.selectedItemId = mainPanel.getFoundCustomItemId();
        } else {
            config.selectedItemName = selectedItem;
            config.selectedItemId = -1;
        }

        config.buyLimit = (Integer) mainPanel.getBuyLimitSpinner().getValue();
        config.priceMarkup = (Double) mainPanel.getPriceMarkupSpinner().getValue();
        config.natureRuneAmount = (Integer) mainPanel.getNatureRuneSpinner().getValue();

        config.smartProfitEnabled = mainPanel.getSmartProfitCheck().isSelected();
        config.worldHopEnabled = mainPanel.getWorldHopCheck().isSelected();
        config.alchConfigEnabled = mainPanel.getAlchConfigCheck().isSelected();
        config.skipBuying = mainPanel.getSkipBuyingCheck().isSelected();
        config.restockWhenEmpty = mainPanel.getRestockWhenEmptyCheck() != null ?
                                   mainPanel.getRestockWhenEmptyCheck().isSelected() : false;

        config.discordWebhookUrl = settingsPanel.getWebhookField().getText().trim();
        config.discordNotificationsEnabled = settingsPanel.getEnableDiscordCheck().isSelected();

        config.antibanEnabled = settingsPanel.getEnableAntibanCheck().isSelected();
        config.antibanAggression = settingsPanel.getAntibanAggressionSlider().getValue();
        config.breakSystemEnabled = settingsPanel.getBreakSystemCheck().isSelected();
        config.minBreakMinutes = (Integer) settingsPanel.getMinBreakSpinner().getValue();
        config.maxBreakMinutes = (Integer) settingsPanel.getMaxBreakSpinner().getValue();
        config.fatigueSystemEnabled = settingsPanel.getFatigueSystemCheck().isSelected();
        config.profileSeedingEnabled = settingsPanel.getProfileSeedingCheck().isSelected();
        config.userProfileSeed = settingsPanel.getUserProfileField().getText().trim();

        return config;
    }

    private String generateUniqueProfileSeed() {
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 10000);
        return "USR" + (timestamp % 100000) + "R" + random;
    }
}
