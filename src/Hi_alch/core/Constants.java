package Hi_alch.core;

/**
 * Centralized Constants for High Alchemy Bot
 *
 * This class contains all magic numbers, string constants, and configuration values
 * used throughout the application. Centralizing constants improves maintainability
 * and makes it easier to update values.
 */
public class Constants {

    // ===========================================
    // OSRS ITEM IDS
    // ===========================================

    public static final int NATURE_RUNE_ID = 563;
    public static final int FIRE_RUNE_ID = 554;
    public static final int COINS_ID = 995;

    // Common alchemy items
    public static final int RUNE_LONGSWORD_ID = 1303;
    public static final int RUNE_2H_SWORD_ID = 1319;
    public static final int RUNE_PLATEBODY_ID = 1127;
    public static final int RUNE_PLATELEGS_ID = 1079;
    public static final int RUNE_PLATESKIRT_ID = 1093;
    public static final int DRAGON_LONGSWORD_ID = 1305;
    public static final int DRAGON_BATTLEAXE_ID = 1377;

    // ===========================================
    // MAGIC & ALCHEMY
    // ===========================================

    public static final int REQUIRED_MAGIC_LEVEL = 55;
    public static final int ALCHEMY_XP_GAIN = 65;
    public static final int ALCHEMY_WARNING_THRESHOLD = 10_000_000; // 10M GP
    public static final int NATURE_RUNE_COST = 220; // Average GE price

    // ===========================================
    // TIMING CONSTANTS (milliseconds)
    // ===========================================

    public static final int MIN_ALCH_DELAY = 1400;
    public static final int MAX_ALCH_DELAY = 2200;
    public static final int HUMAN_ACTION_MIN_DELAY = 200;
    public static final int HUMAN_ACTION_MAX_DELAY = 800;
    public static final int TYPING_MIN_DELAY = 50;
    public static final int TYPING_MAX_DELAY = 150;

    // Timeout values
    public static final int GE_OFFER_TIMEOUT = 30000; // 30 seconds
    public static final int RUNE_OFFER_TIMEOUT = 20000; // 20 seconds
    public static final int GENERAL_TIMEOUT = 5000; // 5 seconds
    public static final int ANIMATION_TIMEOUT = 4000; // 4 seconds

    // ===========================================
    // INVENTORY & TRADING
    // ===========================================

    public static final int MIN_NATURE_RUNES = 50;
    public static final int LOW_INVENTORY_THRESHOLD = 5;
    public static final int INVENTORY_SIZE = 28;
    public static final int MIN_INVENTORY_SPACE_FOR_BUYING = 5;

    // ===========================================
    // GRAND EXCHANGE
    // ===========================================

    public static final int DEFAULT_BUY_LIMIT = 100;
    public static final double DEFAULT_PRICE_MARKUP = 5.0;
    public static final double MIN_PRICE_MARKUP = 0.0;
    public static final double MAX_PRICE_MARKUP = 50.0;
    public static final int DEFAULT_NATURE_RUNE_AMOUNT = 1000;
    public static final int NATURE_RUNE_BUY_PRICE = 300;

    // ===========================================
    // ANTI-BAN SETTINGS
    // ===========================================

    public static final int DEFAULT_ANTIBAND_AGGRESSION = 5;
    public static final int MIN_ANTIBAND_AGGRESSION = 1;
    public static final int MAX_ANTIBAND_AGGRESSION = 10;

    public static final int DEFAULT_MIN_BREAK_MINUTES = 5;
    public static final int DEFAULT_MAX_BREAK_MINUTES = 15;
    public static final int MIN_BREAK_MINUTES = 1;
    public static final int MAX_BREAK_MINUTES = 60;

    // Fatigue levels
    public static final int FATIGUE_FRESH = 20;
    public static final int FATIGUE_TIRED = 50;
    public static final int FATIGUE_VERY_TIRED = 80;

    // ===========================================
    // ERROR HANDLING
    // ===========================================

    public static final int MAX_ERRORS_BEFORE_STOP = 5;
    public static final int ERROR_RECOVERY_DELAY = 5000;
    public static final int CRITICAL_ERROR_DELAY = 10000;

    // ===========================================
    // GUI DIMENSIONS
    // ===========================================

    public static final int GUI_WIDTH = 450;
    public static final int GUI_HEIGHT = 700;
    public static final int GUI_MIN_WIDTH = 420;
    public static final int GUI_MIN_HEIGHT = 650;

    // Component sizes
    public static final int BUTTON_WIDTH = 180;
    public static final int BUTTON_HEIGHT = 45;
    public static final int SMALL_BUTTON_WIDTH = 120;
    public static final int SMALL_BUTTON_HEIGHT = 30;
    public static final int SPINNER_WIDTH = 100;
    public static final int SPINNER_HEIGHT = 30;
    public static final int COMBOBOX_WIDTH = 120;
    public static final int COMBOBOX_HEIGHT = 30;

    // Spacing
    public static final int PANEL_PADDING = 20;
    public static final int COMPONENT_SPACING = 10;
    public static final int SECTION_SPACING = 20;

    // ===========================================
    // GUI POSITIONING
    // ===========================================

    public static final int DEFAULT_GUI_X = 25;
    public static final int DEFAULT_GUI_Y = 300;
    public static final int SMALL_SCREEN_GUI_Y = 50;
    public static final int LARGE_SCREEN_GUI_Y = 400;
    public static final int SMALL_SCREEN_HEIGHT_THRESHOLD = 900;
    public static final int LARGE_SCREEN_HEIGHT_THRESHOLD = 1200;

    // ===========================================
    // COLORS (RGB)
    // ===========================================

    // Modern theme colors
    public static final class Colors {
        public static final int[] BACKGROUND = {45, 45, 48};
        public static final int[] PANEL_BACKGROUND = {37, 37, 38};
        public static final int[] ACCENT = {0, 122, 204};
        public static final int[] ACCENT_BLUE = {64, 128, 255};
        public static final int[] SUCCESS = {76, 175, 80};
        public static final int[] SUCCESS_GREEN = {34, 197, 94};
        public static final int[] WARNING = {251, 191, 36};
        public static final int[] WARNING_ORANGE = {249, 115, 22};
        public static final int[] ERROR = {244, 67, 54};
        public static final int[] ERROR_RED = {239, 68, 68};
        public static final int[] GOLD = {255, 215, 0};
        public static final int[] PURPLE = {156, 39, 176};
        public static final int[] DISCORD_BLUE = {114, 137, 218};
        public static final int[] GRAY = {108, 117, 125};
        public static final int[] LIGHT_BLUE = {96, 165, 250};
        public static final int[] BORDER = {100, 100, 100};
        public static final int[] TEXT_GRAY = {156, 163, 175};
    }

    // ===========================================
    // FONTS
    // ===========================================

    public static final String FONT_FAMILY_MAIN = "Segoe UI";
    public static final String FONT_FAMILY_MODERN = "Inter";
    public static final int FONT_SIZE_TITLE = 16;
    public static final int FONT_SIZE_LARGE = 14;
    public static final int FONT_SIZE_NORMAL = 13;
    public static final int FONT_SIZE_SMALL = 12;
    public static final int FONT_SIZE_TINY = 11;

    // ===========================================
    // DATA TABLE
    // ===========================================

    public static final int DATA_TABLE_ROW_HEIGHT = 30;
    public static final int DATA_TABLE_ITEMS_PER_PAGE = 10;
    public static final int DATA_TABLE_PREFERRED_WIDTH = 400;
    public static final int DATA_TABLE_PREFERRED_HEIGHT = 350;

    // ===========================================
    // PROFIT THRESHOLDS
    // ===========================================

    public static final int EXCELLENT_PROFIT_THRESHOLD = 500;
    public static final int GOOD_PROFIT_THRESHOLD = 100;
    public static final int LOW_PROFIT_THRESHOLD = 0;

    // ===========================================
    // VALIDATION LIMITS
    // ===========================================

    public static final int MIN_BUY_LIMIT = 1;
    public static final int MAX_BUY_LIMIT = 10000;
    public static final int MIN_NATURE_RUNE_AMOUNT = 100;
    public static final int MAX_NATURE_RUNE_AMOUNT = 10000;

    // ===========================================
    // WIDGET IDS (for High Alchemy configuration)
    // ===========================================

    public static final int MAGIC_TAB_WIDGET = 218;
    public static final int HIGH_ALCH_SPELL_CHILD = 44;
    public static final int CONTINUE_DIALOG_WIDGET = 11;
    public static final int CONTINUE_DIALOG_CHILD = 4;
    public static final int THRESHOLD_OPTION_WIDGET = 219;
    public static final int THRESHOLD_OPTION_CHILD_1 = 1;
    public static final int THRESHOLD_OPTION_CHILD_2 = 1;
    public static final int INPUT_FIELD_WIDGET = 162;
    public static final int INPUT_FIELD_CHILD = 43;
    public static final int WARNING_DIALOG_WIDGET = 219;
    public static final int WARNING_DIALOG_CHILD = 1;

    // ===========================================
    // MESSAGES & TEXT
    // ===========================================

    public static final class Messages {
        public static final String BOT_NAME = "OSRS High Alchemy Bot";
        public static final String BOT_VERSION = "v2.0";
        public static final String BOT_TITLE = BOT_NAME + " " + BOT_VERSION + " - Professional Edition";

        public static final String READY_STATUS = "Ready to start - Configure your settings above";
        public static final String RUNNING_STATUS = "High Alchemy Bot is running - Monitor progress in overlay";
        public static final String STOPPED_STATUS = "Bot stopped - Ready to start new session";
        public static final String ERROR_STATUS = "Error occurred - Check logs for details";

        public static final String SELECT_ITEM_PROMPT = "Select an item to see profit analysis";
        public static final String VALIDATE_CUSTOM_ITEM = "Enter and validate custom item for real-time analysis";

        public static final String DISCORD_WEBHOOK_INFO = "Please enter your Discord webhook URL first!\n\nTo create a webhook:\n1. Go to your Discord server\n2. Edit channel → Integrations → Webhooks\n3. Create webhook and copy URL";
    }

    // ===========================================
    // ITEM CATEGORIES
    // ===========================================

    public static final String CATEGORY_F2P = "F2P Items";
    public static final String CATEGORY_P2P = "P2P Items";
    public static final String SELECT_F2P_PROMPT = "Select F2P item...";
    public static final String SELECT_P2P_PROMPT = "Select P2P item...";
    public static final String CUSTOM_F2P_OPTION = "Custom F2P item...";
    public static final String CUSTOM_P2P_OPTION = "Custom P2P item...";

    // ===========================================
    // STATISTICS
    // ===========================================

    public static final long MILLISECONDS_PER_HOUR = 3600000L;
    public static final long MILLISECONDS_PER_MINUTE = 60000L;
    public static final long MILLISECONDS_PER_SECOND = 1000L;

    // ===========================================
    // UTILITY METHODS
    // ===========================================

    /**
     * Prevent instantiation of constants class
     */
    private Constants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    /**
     * Get a color as java.awt.Color object
     */
    public static java.awt.Color getColor(int[] rgb) {
        if (rgb == null || rgb.length != 3) {
            return java.awt.Color.BLACK;
        }
        return new java.awt.Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Create color with alpha
     */
    public static java.awt.Color getColorWithAlpha(int[] rgb, int alpha) {
        if (rgb == null || rgb.length != 3) {
            return new java.awt.Color(0, 0, 0, alpha);
        }
        return new java.awt.Color(rgb[0], rgb[1], rgb[2], alpha);
    }
}
