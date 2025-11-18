package Hi_alch.engine;

/**
 * Bot State Machine States
 *
 * Represents all possible states the alchemy bot can be in.
 * The bot transitions between these states based on conditions
 * and actions.
 */
public enum BotState {

    /**
     * Initial state - bot is setting up
     */
    INITIALIZING("Initializing", "Setting up bot components..."),

    /**
     * Checking if we have required items and runes
     */
    CHECKING_SUPPLIES("Checking Supplies", "Verifying inventory..."),

    /**
     * Buying items from Grand Exchange
     */
    BUYING_ITEMS("Buying Items", "Purchasing items from GE..."),

    /**
     * Buying nature runes from Grand Exchange
     */
    BUYING_NATURE_RUNES("Buying Nature Runes", "Purchasing nature runes..."),

    /**
     * Performing high alchemy
     */
    ALCHING("Alching", "Performing high alchemy..."),

    /**
     * Restocking supplies
     */
    RESTOCKING("Restocking", "Restocking supplies..."),

    /**
     * Banking operations
     */
    BANKING("Banking", "Managing bank..."),

    /**
     * Recovering from an error
     */
    ERROR_RECOVERY("Error Recovery", "Recovering from error..."),

    /**
     * Bot has completed its task
     */
    COMPLETED("Completed", "Session finished"),

    /**
     * Bot is idle/waiting
     */
    IDLE("Idle", "Waiting..."),

    /**
     * Bot is stopped
     */
    STOPPED("Stopped", "Bot stopped");

    // ===========================================
    // FIELDS
    // ===========================================

    private final String displayName;
    private final String description;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    BotState(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    // ===========================================
    // GETTERS
    // ===========================================

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Check if this state represents an error condition
     */
    public boolean isErrorState() {
        return this == ERROR_RECOVERY;
    }

    /**
     * Check if this state represents a terminal state
     */
    public boolean isTerminalState() {
        return this == COMPLETED || this == STOPPED;
    }

    /**
     * Check if this state involves Grand Exchange
     */
    public boolean isGEState() {
        return this == BUYING_ITEMS || this == BUYING_NATURE_RUNES;
    }

    /**
     * Check if this state is active (doing work)
     */
    public boolean isActiveState() {
        return this != IDLE && this != STOPPED && this != COMPLETED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
