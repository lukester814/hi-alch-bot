package Hi_alch.engine;

/**
 * Bot states enum representing different phases of bot execution
 */
public enum BotState {
    INITIALIZING("Initializing bot systems"),
    CHECKING_SUPPLIES("Checking inventory supplies"),
    BUYING_ITEMS("Purchasing items from GE"),
    BUYING_NATURE_RUNES("Buying nature runes"),
    ALCHING("Performing high alchemy"),
    RESTOCKING("Restocking items"),
    BANKING("Banking items"),
    ERROR_RECOVERY("Recovering from error"),
    COMPLETED("Session completed");

    private final String description;

    BotState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
