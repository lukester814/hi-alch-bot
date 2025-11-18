# Refactoring Guide

This document describes the refactoring work done to break down large files into smaller, more maintainable modules.

## 📊 File Size Analysis

### Before Refactoring
| File | Lines | Status |
|------|-------|--------|
| AlchBotGUI.java | 1,951 | ⚠️ TOO LARGE |
| AlchingEngine.java | 851 | ⚠️ LARGE |
| HighAlchBot.java | 761 | ⚠️ LARGE |
| OverlayRenderer.java | 640 | ⚠️ LARGE |
| AntibanSystem.java | 539 | ✅ OK |
| BotUtils.java | 565 | ✅ OK |
| DiscordManager.java | 490 | ✅ OK |
| ItemDatabase.java | 448 | ✅ OK |

## 🎯 Refactoring Goals

1. **Modularity**: Each class should have a single responsibility
2. **Maintainability**: Files under 500 lines each
3. **Reusability**: Extract common components
4. **Testability**: Smaller classes are easier to test

## 📁 New Package Structure

```
Hi_alch/
├── core/                          # Core bot functionality
│   ├── BotCoordinator.java       # (To be extracted from HighAlchBot)
│   └── ComponentManager.java     # (To be extracted from HighAlchBot)
│
├── engine/                        # Alchemy engine
│   ├── BotState.java             # ✅ CREATED - State enum
│   ├── AlchingEngine.java        # Keep existing, refactor to use BotState
│   └── StateHandler.java         # (To be extracted from AlchingEngine)
│
├── gui/                           # GUI components
│   ├── AlchBotGUI.java           # Keep existing, refactor to use panels
│   ├── GUIConfiguration.java     # ✅ CREATED - Configuration data class
│   │
│   ├── panels/                    # GUI panels
│   │   ├── ConfigurationPanel.java    # (To be extracted from AlchBotGUI)
│   │   ├── AdvancedSettingsPanel.java # (To be extracted from AlchBotGUI)
│   │   └── AnalyticsPanel.java        # (To be extracted from AlchBotGUI)
│   │
│   └── components/                # Reusable GUI components
│       └── GUIComponentFactory.java   # ✅ CREATED - Component styling
│
├── overlay/                       # Overlay rendering
│   ├── OverlayRenderer.java      # Keep existing, refactor to use components
│   ├── ScriptStatistics.java     # ✅ CREATED - Statistics tracking
│   └── OverlayPainter.java       # (To be extracted from OverlayRenderer)
│
└── [Original files...]            # Existing utilities remain

```

## ✅ Completed Extractions

### 1. GUIConfiguration.java (150 lines)
**Extracted from**: AlchBotGUI.java (inner class)

**Purpose**: Data class for GUI configuration

**Location**: `Hi_alch/gui/GUIConfiguration.java`

**Contains**:
- All configuration fields
- Validation methods
- Copy/reset methods
- Default values

**Usage**:
```java
import Hi_alch.gui.GUIConfiguration;

GUIConfiguration config = new GUIConfiguration();
config.selectedItemName = "Dragon longsword";
config.buyLimit = 100;

if (config.isValid()) {
    // Use configuration
}
```

### 2. ScriptStatistics.java (220 lines)
**Extracted from**: OverlayRenderer.java (inner class)

**Purpose**: Centralized statistics tracking

**Location**: `Hi_alch/overlay/ScriptStatistics.java`

**Contains**:
- All statistic fields
- Performance metrics
- Calculation methods
- Summary generation

**Usage**:
```java
import Hi_alch.overlay.ScriptStatistics;

ScriptStatistics stats = new ScriptStatistics();
stats.sessionStartTime = System.currentTimeMillis();
stats.recordAlch(profit, xp);
stats.calculateDerivedStats();
```

### 3. BotState.java (130 lines)
**Extracted from**: AlchingEngine.java (enum)

**Purpose**: State machine states with metadata

**Location**: `Hi_alch/engine/BotState.java`

**Contains**:
- All bot states
- State descriptions
- State classification methods
- Utility methods

**Usage**:
```java
import Hi_alch.engine.BotState;

BotState currentState = BotState.ALCHING;
if (currentState.isActiveState()) {
    // Bot is working
}
```

### 4. GUIComponentFactory.java (380 lines)
**Extracted from**: AlchBotGUI.java (styling methods)

**Purpose**: Centralized GUI component creation and styling

**Location**: `Hi_alch/gui/components/GUIComponentFactory.java`

**Contains**:
- Color scheme constants
- Font definitions
- Component factory methods
- Consistent styling

**Usage**:
```java
import Hi_alch.gui.components.GUIComponentFactory;

JButton button = GUIComponentFactory.createSuccessButton("Start Bot");
JLabel label = GUIComponentFactory.createHeaderLabel("Configuration");
JPanel panel = GUIComponentFactory.createTitledPanel("Settings");
```

## 📋 Remaining Work

### Priority 1: AlchBotGUI.java (1,951 lines → ~500 lines)

**Target**: Break into 4 files

#### ConfigurationPanel.java (~400 lines)
- Extract main configuration tab
- Item selection
- Basic settings
- Buy limits

#### AdvancedSettingsPanel.java (~400 lines)
- Extract advanced settings tab
- Anti-ban configuration
- Discord settings
- Break system

#### AnalyticsPanel.java (~400 lines)
- Extract analytics tab
- Market data table
- Statistics display
- Charts/graphs

#### AlchBotGUI.java (reduced to ~300 lines)
- Main frame setup
- Tab management
- Menu bar
- Event coordination

### Priority 2: AlchingEngine.java (851 lines → ~400 lines)

**Target**: Break into 2 files

#### StateHandler.java (~500 lines)
- Extract state handling logic
- All state-specific methods
- Transitions

#### AlchingEngine.java (reduced to ~350 lines)
- Main coordinator
- State machine framework
- Public API

### Priority 3: OverlayRenderer.java (640 lines → ~350 lines)

**Target**: Break into 2 files

#### OverlayPainter.java (~350 lines)
- Extract all drawing methods
- Visual rendering logic
- Layout calculations

#### OverlayRenderer.java (reduced to ~290 lines)
- Main coordinator
- Position management
- Statistics integration

### Priority 4: HighAlchBot.java (761 lines → ~350 lines)

**Target**: Break into 3 files

#### BotCoordinator.java (~300 lines)
- Extract coordination logic
- Component interaction
- State management

#### ComponentManager.java (~200 lines)
- Component initialization
- Dependency injection
- Lifecycle management

#### HighAlchBot.java (reduced to ~260 lines)
- DreamBot script interface
- Event handlers
- Main loop

## 🔧 Migration Steps

### Step 1: Update Imports
When using the new extracted classes, update imports:

```java
// Old (inner class)
AlchBotGUI.GUIConfiguration config = new AlchBotGUI.GUIConfiguration();

// New (separate class)
import Hi_alch.gui.GUIConfiguration;
GUIConfiguration config = new GUIConfiguration();
```

### Step 2: Update References
Replace references to inner classes:

```java
// Old
OverlayRenderer.ScriptStatistics stats = new OverlayRenderer.ScriptStatistics();

// New
import Hi_alch.overlay.ScriptStatistics;
ScriptStatistics stats = new ScriptStatistics();
```

### Step 3: Use Component Factory
Replace manual component creation with factory:

```java
// Old
JButton button = new JButton("Start");
button.setBackground(new Color(0, 122, 204));
button.setForeground(Color.WHITE);
// ... more styling

// New
import Hi_alch.gui.components.GUIComponentFactory;
JButton button = GUIComponentFactory.createSuccessButton("Start");
```

## 📝 Best Practices

### File Size Guidelines
- **Target**: 200-400 lines per file
- **Maximum**: 500 lines per file
- **Absolute limit**: 600 lines per file

### Class Responsibilities
- **Single Responsibility**: Each class should do ONE thing
- **High Cohesion**: Related code stays together
- **Low Coupling**: Minimal dependencies between classes

### Naming Conventions
- **Manager**: Manages lifecycle (e.g., ComponentManager)
- **Handler**: Handles events/states (e.g., StateHandler)
- **Factory**: Creates objects (e.g., GUIComponentFactory)
- **Coordinator**: Coordinates between components (e.g., BotCoordinator)
- **Renderer**: Draws/renders (e.g., OverlayRenderer)
- **Panel**: GUI panel (e.g., ConfigurationPanel)

## 🧪 Testing Recommendations

### Unit Testing
Now that classes are smaller, they're easier to test:

```java
@Test
public void testConfigurationValidation() {
    GUIConfiguration config = new GUIConfiguration();
    config.buyLimit = -1;
    assertFalse(config.isValid());
}

@Test
public void testStatisticsCalculation() {
    ScriptStatistics stats = new ScriptStatistics();
    stats.recordAlch(100, 65);
    assertEquals(1, stats.alchsCompleted);
    assertEquals(100, stats.totalProfit);
}
```

### Integration Testing
Test how components work together:

```java
@Test
public void testComponentFactory() {
    JButton button = GUIComponentFactory.createButton("Test");
    assertNotNull(button);
    assertEquals("Test", button.getText());
}
```

## 🎯 Benefits of Refactoring

### Maintainability
- ✅ Easier to find specific functionality
- ✅ Smaller files are less intimidating
- ✅ Changes affect fewer lines

### Testability
- ✅ Smaller classes are easier to test
- ✅ Fewer dependencies to mock
- ✅ More focused unit tests

### Reusability
- ✅ Components can be reused across projects
- ✅ Clear interfaces
- ✅ Modular design

### Collaboration
- ✅ Fewer merge conflicts
- ✅ Clearer code ownership
- ✅ Easier code reviews

## 📚 References

### Related Documents
- [README.md](README.md) - Project overview
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines
- [CHANGELOG.md](CHANGELOG.md) - Version history

### Design Patterns Used
- **Factory Pattern**: GUIComponentFactory
- **State Pattern**: BotState enum
- **Data Class Pattern**: GUIConfiguration, ScriptStatistics
- **Coordinator Pattern**: (planned for BotCoordinator)

## 🚀 Next Steps

1. **Complete remaining extractions** (panels, handlers, coordinators)
2. **Update existing files** to use new extracted classes
3. **Add unit tests** for new classes
4. **Update documentation** with new structure
5. **Verify compilation** after changes
6. **Run integration tests** to ensure everything works

---

**Status**: Phase 1 Complete (4 classes extracted)
**Next**: Extract GUI panels from AlchBotGUI.java
**Timeline**: Ongoing refactoring as needed
