package Hi_alch.mule;

/**
 * Mule Configuration Data Class
 *
 * Holds all configuration for mule support:
 * - Mule account username
 * - GP threshold for muling
 * - Mule world and location
 * - Discord webhook for notifications
 */
public class MuleConfiguration {

    // ===========================================
    // MULE SETTINGS
    // ===========================================

    public boolean enabled = false;
    public String muleUsername = "";
    public int muleThresholdGP = 5_000_000; // 5M GP default
    public int muleWorld = 301; // Default F2P world
    public String muleLocation = "Grand Exchange";

    // Discord notifications
    public String discordWebhookUrl = "";
    public boolean discordNotificationsEnabled = false;

    // Safety settings
    public int maxMuleAttempts = 3;
    public long minTimeBetweenMules = 600000; // 10 minutes

    // ===========================================
    // VALIDATION
    // ===========================================

    /**
     * Validate mule configuration
     */
    public boolean isValid() {
        if (!enabled) {
            return true; // Valid but disabled
        }

        // Username required
        if (muleUsername == null || muleUsername.trim().isEmpty()) {
            return false;
        }

        // Threshold must be positive
        if (muleThresholdGP <= 0) {
            return false;
        }

        // World must be valid
        if (muleWorld < 301 || muleWorld > 578) {
            return false;
        }

        return true;
    }

    /**
     * Create a copy of this configuration
     */
    public MuleConfiguration copy() {
        MuleConfiguration copy = new MuleConfiguration();

        copy.enabled = this.enabled;
        copy.muleUsername = this.muleUsername;
        copy.muleThresholdGP = this.muleThresholdGP;
        copy.muleWorld = this.muleWorld;
        copy.muleLocation = this.muleLocation;
        copy.discordWebhookUrl = this.discordWebhookUrl;
        copy.discordNotificationsEnabled = this.discordNotificationsEnabled;
        copy.maxMuleAttempts = this.maxMuleAttempts;
        copy.minTimeBetweenMules = this.minTimeBetweenMules;

        return copy;
    }

    /**
     * Reset to default values
     */
    public void resetToDefaults() {
        enabled = false;
        muleUsername = "";
        muleThresholdGP = 5_000_000;
        muleWorld = 301;
        muleLocation = "Grand Exchange";
        discordWebhookUrl = "";
        discordNotificationsEnabled = false;
        maxMuleAttempts = 3;
        minTimeBetweenMules = 600000;
    }

    @Override
    public String toString() {
        return String.format("MuleConfig[enabled=%b, mule=%s, threshold=%d GP, world=%d]",
                enabled, muleUsername, muleThresholdGP, muleWorld);
    }
}
