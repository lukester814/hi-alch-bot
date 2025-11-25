package Hi_alch.managers;

import Hi_alch.utils.BotUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Default Profile Provider
 *
 * Provides pre-configured bot profiles:
 * - F2P Safe Profile
 * - P2P Battlestaffs Profile
 * - Agility Training Profile
 */
public class DefaultProfiles {

    /**
     * Load all default profiles
     */
    public static Map<String, ProfileManager.BotProfile> loadDefaultProfiles() {
        Map<String, ProfileManager.BotProfile> profiles = new HashMap<>();

        profiles.put("F2P Safe", createF2PSafeProfile());
        profiles.put("P2P Battlestaffs", createP2PBattlestaffsProfile());
        profiles.put("Agility Training", createAgilityProfile());

        BotUtils.log("📚 Loaded " + profiles.size() + " default profiles");
        return profiles;
    }

    /**
     * F2P Safe Profile
     */
    private static ProfileManager.BotProfile createF2PSafeProfile() {
        ProfileManager.BotProfile profile = new ProfileManager.BotProfile("F2P Safe");
        profile.itemName = "Rune platebody";
        profile.itemId = 1127;
        profile.buyLimit = 125;
        profile.priceMarkup = 5.0;
        profile.natureRuneAmount = 1000;
        profile.smartProfit = true;
        profile.worldHopping = false;
        profile.alchLocation = "GRAND_EXCHANGE";
        profile.breaksEnabled = true;
        profile.minBreakIntervalMinutes = 60;
        profile.maxBreakIntervalMinutes = 90;
        profile.minBreakDurationMinutes = 10;
        profile.maxBreakDurationMinutes = 20;
        profile.aggressionLevel = 3; // Very human-like
        return profile;
    }

    /**
     * P2P Battlestaffs Profile
     */
    private static ProfileManager.BotProfile createP2PBattlestaffsProfile() {
        ProfileManager.BotProfile profile = new ProfileManager.BotProfile("P2P Battlestaffs");
        profile.itemName = "Air battlestaff";
        profile.itemId = 1397;
        profile.buyLimit = 13000;
        profile.priceMarkup = 3.0;
        profile.natureRuneAmount = 5000;
        profile.smartProfit = true;
        profile.worldHopping = true;
        profile.alchLocation = "GRAND_EXCHANGE";
        profile.breaksEnabled = true;
        profile.minBreakIntervalMinutes = 45;
        profile.maxBreakIntervalMinutes = 75;
        profile.minBreakDurationMinutes = 5;
        profile.maxBreakDurationMinutes = 15;
        profile.aggressionLevel = 5; // Moderate
        return profile;
    }

    /**
     * Agility Training Profile
     */
    private static ProfileManager.BotProfile createAgilityProfile() {
        ProfileManager.BotProfile profile = new ProfileManager.BotProfile("Agility Training");
        profile.itemName = "Rune 2h sword";
        profile.itemId = 1319;
        profile.buyLimit = 70;
        profile.priceMarkup = 5.0;
        profile.natureRuneAmount = 500;
        profile.skipBuying = true;
        profile.restockWhenEmpty = false;
        profile.alchLocation = "VARROCK_ROOFTOP";
        profile.autoNavigate = true;
        profile.breaksEnabled = true;
        profile.minBreakIntervalMinutes = 90;
        profile.maxBreakIntervalMinutes = 120;
        profile.aggressionLevel = 4;
        return profile;
    }
}
