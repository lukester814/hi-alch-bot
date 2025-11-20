package Hi_alch.gui;

/**
 * Configuration data class for GUI settings
 */
public class GUIConfiguration {
    public String selectedItemName;
    public int selectedItemId;
    public int buyLimit;
    public double priceMarkup;
    public int natureRuneAmount;
    public boolean smartProfitEnabled;
    public boolean worldHopEnabled;
    public boolean alchConfigEnabled;
    public boolean skipBuying;
    public boolean restockWhenEmpty;
    public String discordWebhookUrl;
    public boolean discordNotificationsEnabled;
    public boolean antibanEnabled;
    public int antibanAggression;
    public boolean breakSystemEnabled;
    public int minBreakMinutes;
    public int maxBreakMinutes;
    public boolean fatigueSystemEnabled;
    public boolean profileSeedingEnabled;
    public String userProfileSeed;
    public int currentFatigueLevel;

    @Override
    public String toString() {
        return String.format("Item: %s | Limit: %d | Markup: %.1f%% | Runes: %d | Skip: %s | Restock: %s",
                selectedItemName, buyLimit, priceMarkup, natureRuneAmount, skipBuying, restockWhenEmpty);
    }
}
