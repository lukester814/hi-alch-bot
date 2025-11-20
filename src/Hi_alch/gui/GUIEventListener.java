package Hi_alch.gui;

/**
 * Event listener interface for GUI interactions
 */
public interface GUIEventListener {
    void onStartBot(GUIConfiguration config);
    void onStopBot();
    boolean onTestWebhook(String webhookUrl);
    void onSaveSettings();
    void onLoadSettings();
    void onItemSelected(String itemName);
    void onConfigurationChanged();
    void onAlchemyConfigurationChanged(boolean enabled);
}
