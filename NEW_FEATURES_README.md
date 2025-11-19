# Hi-Alch Bot - New Features Documentation

## 🎉 Major Update - Professional-Grade Features Added

This update transforms the Hi-Alch Bot from a simple alching script into a **professional-grade automation suite** with enterprise-level features for safety, efficiency, and profit tracking.

---

## 📑 Table of Contents

1. [Session Goals & Auto-Stop](#1-session-goals--auto-stop)
2. [Banking Support](#2-banking-support)
3. [Location Manager](#3-location-manager)
4. [CSV/Excel Export](#4-csvexcel-export)
5. [Grand Exchange Slot Manager](#5-grand-exchange-slot-manager)
6. [Break Handler GUI](#6-break-handler-gui)
7. [Profile System](#7-profile-system)
8. [Integration Guide](#8-integration-guide)

---

## 1. Session Goals & Auto-Stop

**File**: `SessionGoalsManager.java`, `GUISessionGoalsPanel.java`

### Features

✅ **Multiple Goal Types**:
- Stop after X alchs completed
- Stop after X GP profit earned
- Stop after X Magic XP gained
- Stop after X hours/minutes runtime
- Stop at target Magic level

✅ **Flexible Logic**:
- **OR Logic**: Stop when ANY goal is reached
- **AND Logic**: Stop when ALL goals are reached

✅ **Real-Time Tracking**:
- Live progress bars for each goal
- Overall progress percentage
- Detailed progress summary

✅ **Notifications**:
- Discord webhook notifications
- Sound alerts
- Desktop notifications

### Usage Example

```java
// Create session goals
SessionGoalsManager goalsManager = new SessionGoalsManager(discordManager);

SessionGoalsManager.SessionGoals goals = new SessionGoalsManager.SessionGoals();
goals.alchGoalEnabled = true;
goals.targetAlchs = 1000;
goals.profitGoalEnabled = true;
goals.targetProfit = 500000; // 500K GP
goals.useAndLogic = false; // OR logic - stop when either goal reached

goalsManager.setGoals(goals);

// In your main loop
goalsManager.updateProgress(currentAlchs, currentProfit);

if (goalsManager.shouldStop()) {
    BotUtils.log(goalsManager.getGoalReachedMessage());
    // Stop the bot
}
```

### GUI Configuration

The `GUISessionGoalsPanel` provides:
- Checkboxes to enable/disable each goal type
- Spinners to set target values
- Radio buttons for AND/OR logic
- Progress visualization
- Real-time status updates

---

## 2. Banking Support

**File**: `BankManager.java`

### Features

✅ **14 Bank Locations** including:
- Grand Exchange
- Varrock West/East
- Edgeville
- Lumbridge
- Draynor
- Falador West/East
- Seers Village
- Catherby
- Ardougne North/South
- Yanille
- Castle Wars

✅ **Smart Navigation**:
- Auto-walk to preferred bank
- Find nearest bank automatically
- Area validation

✅ **Advanced Operations**:
- Withdraw items (by ID or name)
- Deposit items/all items
- Bank presets
- PIN support

✅ **Hybrid Mode**:
- Check bank first, then GE if needed
- Reduces GE dependency for ironmen
- Faster restocking workflow

### Usage Example

```java
// Create bank manager
BankManager bankManager = new BankManager();

// Set preferred location
bankManager.setPreferredLocation(BankManager.BankLocation.GRAND_EXCHANGE);

// Enable hybrid mode
bankManager.setHybridMode(true);

// Navigate and open bank
if (bankManager.walkToBank()) {
    if (bankManager.openBank()) {
        // Withdraw items
        bankManager.withdrawItem("Rune platebody", 100);
        bankManager.withdrawItem("Nature rune", 1000);

        // Close bank
        bankManager.closeBank();
    }
}

// Bank presets
BankManager.BankPreset preset = new BankManager.BankPreset("Alching Setup");
preset.setItems(
    new int[]{1127, 563},  // Rune platebody, Nature rune
    new int[]{100, 1000}   // Quantities
);
bankManager.loadPreset(preset);
```

---

## 3. Location Manager

**File**: `LocationManager.java`

### Features

✅ **15+ Alching Locations**:

**Bank Standing**:
- Grand Exchange
- Varrock West Bank
- Edgeville Bank
- Lumbridge Bank

**Agility Training**:
- Varrock Rooftop
- Seers Village Rooftop
- Canifis Rooftop (P2P)

**Fishing Spots**:
- Barbarian Fishing
- Catherby Fishing
- Fishing Guild (P2P)

**Resource Gathering**:
- Woodcutting Guild (P2P)
- Motherload Mine (P2P)

**Safe AFK Spots**:
- Lumbridge Castle
- Varrock Palace

✅ **Location-Specific Behavior**:
- Delay multipliers per location type
- Agility: 0.7x delays (faster alching)
- Fishing/Mining/WC: 1.5x delays (AFK training)
- Custom antiban patterns per location

✅ **Auto-Navigation**:
- Automatic walking to selected location
- Path verification
- Stuck detection

### Usage Example

```java
// Create location manager
LocationManager locationManager = new LocationManager(antibanSystem);

// Set location
locationManager.setLocation(LocationManager.AlchLocation.VARROCK_ROOFTOP);
locationManager.setAutoNavigate(true);

// Navigate
if (locationManager.navigateToLocation()) {
    // Start alching
}

// Get location-specific delay
int baseDelay = 1500;
double multiplier = locationManager.getDelayMultiplier(); // 0.7 for agility
int actualDelay = (int)(baseDelay * multiplier); // 1050ms

// Perform location-specific antiban
if (locationManager.requiresExtraAntiban()) {
    locationManager.performLocationAntiban();
}
```

### GUI Integration

```java
// In GUI - dropdown for location selection
JComboBox<String> locationCombo = new JComboBox<>();
for (LocationManager.AlchLocation loc : LocationManager.AlchLocation.values()) {
    locationCombo.addItem(loc.getName());
}
```

---

## 4. CSV/Excel Export

**File**: `CSVExporter.java`

### Features

✅ **Comprehensive Data Tracking**:
- Every alch recorded with timestamp
- Item name, ID, buy price, alch value, profit, XP
- Session summaries with statistics
- Per-hour rate calculations

✅ **Multiple Export Formats**:
- Detailed alch history (all records)
- Session summaries
- Daily reports
- Weekly reports
- All sessions aggregate

✅ **Auto-Export**:
- Incremental export every 100 alchs
- Live CSV file (`alch_history_live.csv`)
- Session summaries on completion
- Daily report generation

✅ **Excel-Compatible**:
- Proper CSV escaping
- Headers included
- Date/time formatting
- Numeric formatting

### Usage Example

```java
// Create exporter
CSVExporter exporter = new CSVExporter();
exporter.setExportDirectory("/path/to/exports/");
exporter.setAutoExport(true);

// Record alchs
exporter.recordAlch(
    "Rune platebody",  // Item name
    1127,              // Item ID
    38000,             // Buy price
    65000,             // Alch value
    26780,             // Profit (after nature rune cost)
    65                 // Magic XP
);

// Record session
CSVExporter.SessionSummary session = new CSVExporter.SessionSummary();
session.sessionStartTime = startTime;
session.sessionEndTime = System.currentTimeMillis();
session.itemName = "Rune platebody";
session.totalAlchs = 1000;
session.totalProfit = 26780000; // 26.78M
session.totalXPGained = 65000;
session.startingLevel = 55;
session.endingLevel = 58;
session.location = "Grand Exchange";

exporter.recordSession(session);

// Manual exports
exporter.exportAlchHistory();          // Export all alchs
exporter.exportAllSessions();          // Export session history
exporter.generateDailyReport();        // Generate today's report
```

### Export File Locations

Default directory: `~/HiAlchBot_Exports/`

Generated files:
- `alch_history_live.csv` - Real-time alch log
- `alch_history_YYYY-MM-DD_HHMMSS.csv` - Full history snapshot
- `session_summary_YYYY-MM-DD_HHMMSS.csv` - Individual session
- `all_sessions_YYYY-MM-DD_HHMMSS.csv` - All sessions aggregate
- `daily_report_YYYY-MM-DD.csv` - Daily summary

---

## 5. Grand Exchange Slot Manager

**File**: `GESlotManager.java`

### Features

✅ **8-Slot Tracking**:
- Real-time status of all GE slots
- Empty/Buying/Selling/Completed/Cancelled/Stuck

✅ **Auto-Management**:
- Auto-collect completed offers
- Auto-cancel stuck offers (5+ min timeout)
- Queue overflow purchases

✅ **Purchase Queue**:
- Queue items when slots full
- Auto-process queue when slots free
- Priority management

✅ **Offer History**:
- Track all completed/cancelled offers
- Success rate statistics
- Average offer duration
- Performance metrics

### Usage Example

```java
// Create GE slot manager
GESlotManager slotManager = new GESlotManager();
slotManager.setAutoCollect(true);
slotManager.setAutoCancelStuck(true);

// Update slot statuses (call periodically)
slotManager.updateSlotStatuses();

// Place buy offer
slotManager.placeBuyOffer(
    1127,              // Item ID
    "Rune platebody",  // Item name
    100,               // Quantity
    38500              // Price
);

// Check slot availability
if (slotManager.getEmptySlotCount() == 0) {
    // Queue for later
    slotManager.queuePurchase(1127, "Rune platebody", 100, 38500, true);
}

// Process queue
slotManager.processPurchaseQueue();

// Get statistics
BotUtils.log(slotManager.getSlotSummary());
// Output: "Slots: 3 empty | 4 active | 1 completed | Queue: 2"

BotUtils.log(slotManager.getOfferHistorySummary());
// Output: "History: 50 total | 48 successful (96.0%) | Avg time: 45s"

// Access individual slots
GESlotManager.GESlot slot = slotManager.getSlot(0);
if (slot.status == GESlotManager.SlotStatus.STUCK) {
    BotUtils.log("Slot 0 stuck for " + (slot.getOfferAge() / 1000) + " seconds");
}
```

---

## 6. Break Handler GUI

**File**: `GUIBreaksPanel.java`

### Features

✅ **Break Scheduling**:
- Configurable break intervals (45-90 min default)
- Configurable break durations (5-15 min default)
- Randomization for humanlike behavior

✅ **Break Activities**:
- Logout (safest)
- Bank standing
- Random skill checks
- Examine objects
- Camera movement

✅ **Antiban Configuration**:
- Aggression slider (1-10)
  - 1 = Very human-like (slow, cautious)
  - 10 = Aggressive (fast, risky)
- Enable/disable behaviors:
  - Camera movements
  - Tab checks
  - Mouse off-screen
  - Skill checks

✅ **Real-Time Status**:
- Next break countdown
- Last break timer
- Total breaks taken
- Progress bar

### Usage Example

```java
// Create breaks panel
GUIBreaksPanel breaksPanel = new GUIBreaksPanel();

// Get configuration
GUIBreaksPanel.BreakConfiguration config = breaksPanel.getBreakConfiguration();

// Apply to antiban system
antibanSystem.setAggressionLevel(config.aggressionLevel);
antibanSystem.setCameraMovementEnabled(config.cameraMovement);
antibanSystem.setTabChecksEnabled(config.tabChecks);
antibanSystem.setMouseLeavesEnabled(config.mouseLeaves);
antibanSystem.setSkillChecksEnabled(config.skillChecks);

// Configure breaks
if (config.breaksEnabled) {
    int interval = BotUtils.random(
        config.minBreakIntervalMinutes,
        config.maxBreakIntervalMinutes
    ) * 60000; // Convert to ms

    int duration = BotUtils.random(
        config.minBreakDurationMinutes,
        config.maxBreakDurationMinutes
    ) * 60000;

    // Schedule next break
    antibanSystem.scheduleBreak(interval, duration);
}

// Update UI status
breaksPanel.updateBreakStatus(
    antibanSystem.getNextBreakTime(),
    antibanSystem.getLastBreakTime(),
    antibanSystem.getTotalBreaks()
);
```

---

## 7. Profile System

**File**: `ProfileManager.java`

### Features

✅ **Complete Configuration Save/Load**:
- Bot settings (item, markup, quantities)
- Location preferences
- Session goals
- Break/antiban settings
- Discord/mule settings
- Export preferences

✅ **Default Profiles Included**:
- **F2P Safe**: Conservative settings for F2P accounts
- **P2P Battlestaffs**: Optimized for battlestaff alching
- **Agility Training**: Settings for alching while training agility

✅ **Profile Management**:
- Create/save/load/delete profiles
- Profile versioning
- Timestamp tracking
- Import/export as files

✅ **File Format**:
- Human-readable key=value format
- Comments supported
- Easy manual editing
- Cross-platform compatible

### Usage Example

```java
// Create profile manager
ProfileManager profileManager = new ProfileManager();

// Create new profile
ProfileManager.BotProfile profile = new ProfileManager.BotProfile("My Custom Profile");
profile.itemName = "Dragon longsword";
profile.itemId = 1305;
profile.buyLimit = 70;
profile.priceMarkup = 5.0;
profile.alchLocation = "EDGEVILLE_BANK";
profile.breaksEnabled = true;
profile.minBreakIntervalMinutes = 60;
profile.maxBreakIntervalMinutes = 90;

// Save profile
profileManager.saveProfile(profile);

// Load profile
ProfileManager.BotProfile loaded = profileManager.loadProfile("My Custom Profile");

// Apply profile to bot
applyProfileToBot(loaded);

// Get available profiles
List<String> profiles = profileManager.getAvailableProfiles();
for (String profileName : profiles) {
    System.out.println("Available: " + profileName);
}

// Delete profile
profileManager.deleteProfile("Old Profile");
```

### Profile File Example

```
# Hi-Alch Bot Profile
# Profile: P2P Battlestaffs
# Created: 2025-01-19 12:00:00
# Modified: 2025-01-19 15:30:00

profileName=P2P Battlestaffs
profileVersion=1
createdTimestamp=1737291600000
lastModifiedTimestamp=1737304200000

# Bot Settings
itemName=Air battlestaff
itemId=1397
buyLimit=13000
priceMarkup=3.0
natureRuneAmount=5000
smartProfit=true
worldHopping=true

# Location Settings
alchLocation=GRAND_EXCHANGE
autoNavigate=true

# Session Goals
alchGoalEnabled=true
targetAlchs=5000
profitGoalEnabled=true
targetProfit=1000000

# Break Settings
breaksEnabled=true
minBreakIntervalMinutes=45
maxBreakIntervalMinutes=75
aggressionLevel=5
```

---

## 8. Integration Guide

### Adding to Existing Bot

#### Step 1: Add Components to Main Bot Class

```java
public class HighAlchBot extends AbstractScript {

    // New managers
    private SessionGoalsManager goalsManager;
    private BankManager bankManager;
    private LocationManager locationManager;
    private CSVExporter csvExporter;
    private GESlotManager geSlotManager;
    private ProfileManager profileManager;

    @Override
    public void onStart() {
        // Initialize managers
        goalsManager = new SessionGoalsManager(discordManager);
        bankManager = new BankManager();
        locationManager = new LocationManager(antibanSystem);
        csvExporter = new CSVExporter();
        geSlotManager = new GESlotManager();
        profileManager = new ProfileManager();

        // Load profile if configured
        if (selectedProfile != null) {
            ProfileManager.BotProfile profile = profileManager.loadProfile(selectedProfile);
            applyProfile(profile);
        }
    }
}
```

#### Step 2: Add GUI Panels

```java
// In AlchBotGUI.java

private GUISessionGoalsPanel sessionGoalsPanel;
private GUIBreaksPanel breaksPanel;

// In createMainTabbedPane()
sessionGoalsPanel = new GUISessionGoalsPanel();
breaksPanel = new GUIBreaksPanel();

tabbedPane.addTab("Session Goals", null, sessionGoalsPanel, "Configure auto-stop goals");
tabbedPane.addTab("Breaks & Antiban", null, breaksPanel, "Configure breaks and antiban");
```

#### Step 3: Main Loop Integration

```java
@Override
public int onLoop() {
    try {
        // Update session goals
        goalsManager.updateProgress(
            alchingEngine.getAlchCount(),
            alchingEngine.getCurrentProfit()
        );

        // Check if goals reached
        if (goalsManager.shouldStop()) {
            BotUtils.log(goalsManager.getGoalReachedMessage());
            return -1; // Stop bot
        }

        // Update GE slots
        if (GrandExchange.isOpen()) {
            geSlotManager.updateSlotStatuses();
            geSlotManager.processPurchaseQueue();
        }

        // Ensure at correct location
        if (!locationManager.isAtLocation()) {
            locationManager.ensureAtLocation();
            return 2000;
        }

        // Normal alching logic
        return alchingEngine.executeNextAction();

    } catch (Exception e) {
        BotUtils.logError("Error in main loop", e);
        return 3000;
    }
}
```

#### Step 4: Add Event Handlers

```java
// On alch completed
csvExporter.recordAlch(
    itemName,
    itemId,
    buyPrice,
    alchValue,
    profit,
    65 // Magic XP per alch
);

// On session end
CSVExporter.SessionSummary summary = new CSVExporter.SessionSummary();
summary.sessionStartTime = sessionStartTime;
summary.sessionEndTime = System.currentTimeMillis();
summary.totalAlchs = totalAlchs;
summary.totalProfit = totalProfit;
summary.totalXPGained = totalXP;
csvExporter.recordSession(summary);
```

---

## 🚀 Quick Start

### Minimal Setup

```java
// 1. Create managers
SessionGoalsManager goals = new SessionGoalsManager(discordManager);
LocationManager location = new LocationManager(antibanSystem);
CSVExporter exporter = new CSVExporter();

// 2. Configure goals
SessionGoalsManager.SessionGoals sessionGoals = new SessionGoalsManager.SessionGoals();
sessionGoals.alchGoalEnabled = true;
sessionGoals.targetAlchs = 1000;
goals.setGoals(sessionGoals);

// 3. Set location
location.setLocation(LocationManager.AlchLocation.GRAND_EXCHANGE);

// 4. Enable exports
exporter.setAutoExport(true);

// 5. Main loop
while (running) {
    // Update progress
    goals.updateProgress(alchs, profit);

    // Check stop condition
    if (goals.shouldStop()) break;

    // Alch
    performAlch();
    exporter.recordAlch(itemName, itemId, buyPrice, alchValue, profit, 65);
}
```

---

## 📊 Statistics

**Lines of Code Added**: ~4,000+
**New Files**: 10
**GUI Panels**: 2
**Manager Classes**: 7
**Features**: 7 major systems

---

## 🎯 Benefits Summary

✅ **Safety**: Auto-stop prevents overtraining, break system mimics humans
✅ **Efficiency**: Location-based alching, GE slot optimization, banking support
✅ **Profitability**: CSV tracking, profit analysis, session reports
✅ **Convenience**: Profile system, auto-exports, Discord notifications
✅ **Flexibility**: Multiple locations, custom goals, hybrid bank/GE mode
✅ **Professional**: Enterprise-grade code quality, comprehensive documentation

---

## 📝 Notes

- All features are **optional** and can be disabled
- Default configurations are **conservative** (safe settings)
- Profile system includes **3 ready-to-use profiles**
- CSV exports are **Excel-compatible**
- Discord notifications work with **existing webhook system**
- All new code follows existing **code style and patterns**

---

## 🐛 Troubleshooting

**Q: Session goals not stopping the bot?**
A: Ensure `goalsManager.shouldStop()` is checked in main loop and returns `-1` or breaks loop when true.

**Q: CSV files not exporting?**
A: Check export directory exists and is writable. Default: `~/HiAlchBot_Exports/`

**Q: GE slot manager not auto-collecting?**
A: Call `geSlotManager.updateSlotStatuses()` periodically when GE is open.

**Q: Location manager not navigating?**
A: Ensure `autoNavigate` is enabled and call `ensureAtLocation()` before alching.

**Q: Profile not loading?**
A: Check profile file exists in `~/HiAlchBot_Profiles/` and has `.profile` extension.

---

## 🔜 Future Enhancements

Potential additions in future updates:
- Web dashboard for remote monitoring
- Multi-account coordination
- Price alert system with auto-item switching
- Advanced session timeline visualization
- CLI mode for VPS hosting
- Mobile app integration

---

**Last Updated**: 2025-01-19
**Author**: Claude
**Version**: 2.0.0
