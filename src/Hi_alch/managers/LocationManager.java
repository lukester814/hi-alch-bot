package Hi_alch.managers;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.methods.interactive.Players;

/**
 * Location Manager for High Alchemy
 *
 * Features:
 * - Multiple alching locations (GE, Agility, Fishing, etc.)
 * - Auto-navigation to selected location
 * - Location-specific antiban behaviors
 * - AFK training integration (alch while doing other skills)
 */
public class LocationManager {

    // ===========================================
    // ALCHING LOCATIONS
    // ===========================================

    public enum AlchLocation {
        GRAND_EXCHANGE(
            "Grand Exchange",
            "Alch while at GE (easy restocking)",
            new Area(3161, 3489, 3168, 3482),
            LocationType.BANK_STANDING
        ),
        VARROCK_WEST_BANK(
            "Varrock West Bank",
            "Alch at Varrock West bank",
            new Area(3180, 3447, 3185, 3433),
            LocationType.BANK_STANDING
        ),
        EDGEVILLE_BANK(
            "Edgeville Bank",
            "Alch at Edgeville bank (safe, popular)",
            new Area(3091, 3499, 3098, 3487),
            LocationType.BANK_STANDING
        ),
        LUMBRIDGE_BANK(
            "Lumbridge Bank",
            "Alch at Lumbridge bank (starter-friendly)",
            new Area(3207, 3222, 3210, 3217),
            LocationType.BANK_STANDING
        ),

        // Agility locations
        VARROCK_ROOFTOP(
            "Varrock Rooftop Agility",
            "Alch while training agility at Varrock rooftops",
            new Area(3214, 3414, 3222, 3410),
            LocationType.AGILITY
        ),
        SEERS_ROOFTOP(
            "Seers Village Rooftop",
            "Alch while training agility at Seers Village",
            new Area(2721, 3493, 2730, 3490),
            LocationType.AGILITY
        ),
        CANIFIS_ROOFTOP(
            "Canifis Rooftop",
            "Alch while training agility at Canifis (P2P)",
            new Area(3505, 3488, 3510, 3483),
            LocationType.AGILITY
        ),

        // Fishing locations
        BARBARIAN_FISHING(
            "Barbarian Fishing",
            "Alch while AFK fishing at Barbarian Village",
            new Area(3103, 3433, 3108, 3427),
            LocationType.FISHING
        ),
        CATHERBY_FISHING(
            "Catherby Fishing",
            "Alch while fishing at Catherby",
            new Area(2835, 3433, 2847, 3425),
            LocationType.FISHING
        ),
        FISHING_GUILD(
            "Fishing Guild",
            "Alch while fishing at Fishing Guild (P2P)",
            new Area(2595, 3419, 2614, 3407),
            LocationType.FISHING
        ),

        // Safe AFK spots
        LUMBRIDGE_CASTLE(
            "Lumbridge Castle",
            "Alch in Lumbridge Castle (very safe)",
            new Area(3206, 3229, 3222, 3218),
            LocationType.SAFE_AFK
        ),
        VARROCK_PALACE(
            "Varrock Palace",
            "Alch at Varrock Palace courtyard",
            new Area(3205, 3465, 3216, 3456),
            LocationType.SAFE_AFK
        ),

        // Resource gathering
        WOODCUTTING_GUILD(
            "Woodcutting Guild",
            "Alch while woodcutting (P2P)",
            new Area(1662, 3505, 1677, 3490),
            LocationType.WOODCUTTING
        ),
        MOTHERLOAD_MINE(
            "Motherload Mine",
            "Alch while AFK mining (P2P)",
            new Area(3723, 5692, 3772, 5665),
            LocationType.MINING
        );

        private final String name;
        private final String description;
        private final Area area;
        private final LocationType type;

        AlchLocation(String name, String description, Area area, LocationType type) {
            this.name = name;
            this.description = description;
            this.area = area;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Area getArea() {
            return area;
        }

        public LocationType getType() {
            return type;
        }

        public boolean isNearby() {
            try {
                return area.contains(Players.getLocal());
            } catch (Exception e) {
                return false;
            }
        }
    }

    // ===========================================
    // LOCATION TYPES
    // ===========================================

    public enum LocationType {
        BANK_STANDING("Bank Standing", "Stand near a bank"),
        AGILITY("Agility Training", "Alch while doing agility courses"),
        FISHING("Fishing", "Alch while AFK fishing"),
        SAFE_AFK("Safe AFK Spot", "Safe location for extended alching"),
        WOODCUTTING("Woodcutting", "Alch while chopping trees"),
        MINING("Mining", "Alch while mining");

        private final String displayName;
        private final String description;

        LocationType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    // ===========================================
    // STATE
    // ===========================================

    private AlchLocation currentLocation;
    private boolean autoNavigate;
    private AntibanSystem antibanSystem;

    // ===========================================
    // CONSTRUCTOR
    // ===========================================

    public LocationManager(AntibanSystem antibanSystem) {
        this.currentLocation = AlchLocation.GRAND_EXCHANGE;
        this.autoNavigate = true;
        this.antibanSystem = antibanSystem;

        BotUtils.log("📍 LocationManager initialized");
    }

    // ===========================================
    // CONFIGURATION
    // ===========================================

    public void setLocation(AlchLocation location) {
        this.currentLocation = location;
        BotUtils.log("📍 Location set to: " + location.getName());
        BotUtils.log("   Type: " + location.getType().getDisplayName());
        BotUtils.log("   Description: " + location.getDescription());
    }

    public void setAutoNavigate(boolean enabled) {
        this.autoNavigate = enabled;
        BotUtils.log("🧭 Auto-navigation " + (enabled ? "enabled" : "disabled"));
    }

    // ===========================================
    // NAVIGATION
    // ===========================================

    /**
     * Navigate to current location
     */
    public boolean navigateToLocation() {
        try {
            if (currentLocation.isNearby()) {
                BotUtils.log("✅ Already at " + currentLocation.getName());
                return true;
            }

            BotUtils.log("🚶 Walking to " + currentLocation.getName());

            if (Walking.shouldWalk()) {
                Walking.walk(currentLocation.getArea().getRandomTile());
            }

            boolean arrived = Sleep.sleepUntil(() -> currentLocation.isNearby(), 45000);

            if (arrived) {
                BotUtils.log("✅ Arrived at " + currentLocation.getName());
                return true;
            } else {
                BotUtils.log("❌ Failed to reach " + currentLocation.getName());
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error navigating to location", e);
            return false;
        }
    }

    /**
     * Check if at correct location, navigate if needed
     */
    public boolean ensureAtLocation() {
        if (currentLocation.isNearby()) {
            return true;
        }

        if (autoNavigate) {
            BotUtils.log("⚠️ Not at correct location, navigating...");
            return navigateToLocation();
        }

        BotUtils.log("⚠️ Not at " + currentLocation.getName() +
                   " and auto-navigation is disabled");
        return false;
    }

    // ===========================================
    // LOCATION-SPECIFIC BEHAVIORS
    // ===========================================

    /**
     * Get location-specific antiban delay multiplier
     */
    public double getDelayMultiplier() {
        switch (currentLocation.getType()) {
            case AGILITY:
                // Shorter delays while doing agility
                return 0.7;

            case FISHING:
            case WOODCUTTING:
            case MINING:
                // Longer delays while AFK training
                return 1.5;

            case BANK_STANDING:
                // Normal delays at banks
                return 1.0;

            case SAFE_AFK:
                // Slightly longer delays for safety
                return 1.2;

            default:
                return 1.0;
        }
    }

    /**
     * Check if location requires specific antiban behaviors
     */
    public boolean requiresExtraAntiban() {
        // Agility and fishing locations need extra randomization
        return currentLocation.getType() == LocationType.AGILITY ||
               currentLocation.getType() == LocationType.FISHING;
    }

    /**
     * Execute location-specific antiban action
     */
    public void performLocationAntiban() {
        try {
            // Trigger antiban system update which will perform random behaviors
            // based on the configured profile and aggression level
            antibanSystem.update();

        } catch (Exception e) {
            BotUtils.logError("Error performing location antiban", e);
        }
    }

    // ===========================================
    // LOCATION RECOMMENDATIONS
    // ===========================================

    /**
     * Get recommended locations based on account type
     */
    public static AlchLocation[] getRecommendedLocations(boolean isF2P, boolean isIronman) {
        if (isF2P) {
            // F2P recommendations
            return new AlchLocation[]{
                AlchLocation.GRAND_EXCHANGE,
                AlchLocation.VARROCK_WEST_BANK,
                AlchLocation.EDGEVILLE_BANK,
                AlchLocation.LUMBRIDGE_BANK,
                AlchLocation.VARROCK_ROOFTOP,
                AlchLocation.LUMBRIDGE_CASTLE
            };
        } else if (isIronman) {
            // Ironman recommendations (no GE)
            return new AlchLocation[]{
                AlchLocation.EDGEVILLE_BANK,
                AlchLocation.SEERS_ROOFTOP,
                AlchLocation.BARBARIAN_FISHING,
                AlchLocation.MOTHERLOAD_MINE
            };
        } else {
            // P2P regular account - all locations available
            return AlchLocation.values();
        }
    }

    /**
     * Get optimal location for efficiency
     */
    public static AlchLocation getOptimalLocation(boolean hasGEAccess) {
        if (hasGEAccess) {
            return AlchLocation.GRAND_EXCHANGE; // Best for restocking
        } else {
            return AlchLocation.EDGEVILLE_BANK; // Good compromise
        }
    }

    // ===========================================
    // GETTERS
    // ===========================================

    public AlchLocation getCurrentLocation() {
        return currentLocation;
    }

    public boolean isAutoNavigate() {
        return autoNavigate;
    }

    public boolean isAtLocation() {
        return currentLocation.isNearby();
    }

    public String getCurrentLocationInfo() {
        return String.format("%s (%s) - %s",
            currentLocation.getName(),
            currentLocation.getType().getDisplayName(),
            currentLocation.getDescription()
        );
    }
}
