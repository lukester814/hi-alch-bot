# Project Structure

This document describes the complete project structure of the OSRS High Alchemy Bot.

## 📁 Directory Tree

```
hi-alch-bot/
├── .github/                          # GitHub configuration
│   ├── ISSUE_TEMPLATE/              # Issue templates
│   │   ├── bug_report.md            # Bug report template
│   │   └── feature_request.md       # Feature request template
│   ├── PULL_REQUEST_TEMPLATE.md     # PR template
│   └── FUNDING.yml                  # Funding configuration
│
├── .idea/                            # IntelliJ IDEA configuration
│   ├── artifacts/                   # Build artifacts config
│   ├── libraries/                   # Library dependencies
│   └── *.xml                        # IDE settings
│
├── src/                              # Source code
│   └── Hi_alch/                     # Main package
│       ├── core/                    # Core functionality (planned)
│       ├── engine/                  # Bot engine components
│       │   └── BotState.java       # State machine states
│       ├── gui/                     # GUI components
│       │   ├── GUIConfiguration.java      # Configuration data class
│       │   ├── AlchBotGUI.java           # Main GUI (to be refactored)
│       │   ├── panels/                    # GUI panels (planned)
│       │   └── components/
│       │       └── GUIComponentFactory.java  # Component factory
│       ├── overlay/                 # Overlay rendering
│       │   ├── ScriptStatistics.java      # Statistics tracking
│       │   └── OverlayRenderer.java       # Overlay renderer
│       ├── AlchingEngine.java       # Core alchemy logic
│       ├── AntibanSystem.java       # Anti-ban system
│       ├── BotUtils.java            # Utility methods
│       ├── DiscordManager.java      # Discord integration
│       ├── HighAlchBot.java         # Main bot coordinator
│       ├── ItemDatabase.java        # Item data
│       ├── ItemSearchAPI.java       # OSRS Wiki API
│       ├── PriceManager.java        # Price management
│       └── SettingsManager.java     # Settings persistence
│
├── .editorconfig                     # Editor configuration
├── .gitattributes                    # Git attributes
├── .gitignore                        # Git ignore rules
├── build.gradle                      # Gradle build script
├── settings.gradle                   # Gradle settings
├── gradle.properties                 # Gradle properties
├── Hi alch bot.iml                  # IntelliJ module file
│
├── CHANGELOG.md                      # Version history
├── CODE_OF_CONDUCT.md               # Code of conduct
├── CONTRIBUTING.md                   # Contribution guidelines
├── LICENSE                           # MIT License
├── README.md                         # Project documentation
├── REFACTORING_GUIDE.md             # Refactoring plan
├── REFACTORING_STATUS.md            # Refactoring status
├── SECURITY.md                       # Security policy
└── PROJECT_STRUCTURE.md             # This file

```

## 📦 Package Organization

### Hi_alch (Main Package)

#### Core Components

| File | Lines | Purpose |
|------|-------|---------|
| `HighAlchBot.java` | ~764 | Main DreamBot script coordinator |
| `BotUtils.java` | ~565 | Utility methods and logging |

#### Engine Package (`Hi_alch.engine`)

| File | Lines | Purpose |
|------|-------|---------|
| `AlchingEngine.java` | ~827 | Core alchemy state machine |
| `BotState.java` | ~130 | State definitions and metadata |

#### GUI Package (`Hi_alch.gui`)

| File | Lines | Purpose |
|------|-------|---------|
| `AlchBotGUI.java` | ~1,921 | Main GUI interface |
| `GUIConfiguration.java` | ~150 | Configuration data structure |
| `components/GUIComponentFactory.java` | ~380 | GUI component factory |

**Planned Panels** (to be extracted):
- `panels/ConfigurationPanel.java` (~400 lines)
- `panels/AdvancedSettingsPanel.java` (~400 lines)
- `panels/AnalyticsPanel.java` (~400 lines)

#### Overlay Package (`Hi_alch.overlay`)

| File | Lines | Purpose |
|------|-------|---------|
| `OverlayRenderer.java` | ~523 | In-game overlay rendering |
| `ScriptStatistics.java` | ~220 | Statistics tracking |

#### Utility Classes

| File | Lines | Purpose |
|------|-------|---------|
| `AntibanSystem.java` | ~538 | Human-like behavior |
| `DiscordManager.java` | ~492 | Discord webhooks |
| `ItemDatabase.java` | ~448 | 40+ item database |
| `ItemSearchAPI.java` | ~476 | OSRS Wiki API |
| `PriceManager.java` | ~524 | Price management |
| `SettingsManager.java` | ~419 | Configuration persistence |

## 📋 File Type Breakdown

### Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | Main project documentation |
| `CHANGELOG.md` | Version history and release notes |
| `CONTRIBUTING.md` | Contributor guidelines |
| `LICENSE` | MIT License with disclaimers |
| `CODE_OF_CONDUCT.md` | Community standards |
| `SECURITY.md` | Security policy and reporting |
| `REFACTORING_GUIDE.md` | Code refactoring plan |
| `REFACTORING_STATUS.md` | Refactoring progress |
| `PROJECT_STRUCTURE.md` | This document |

### Configuration Files

| File | Purpose |
|------|---------|
| `.editorconfig` | Code style configuration |
| `.gitattributes` | Git file handling |
| `.gitignore` | Git ignore rules |
| `build.gradle` | Gradle build configuration |
| `settings.gradle` | Gradle project settings |
| `gradle.properties` | Gradle properties |

### GitHub Files

| File | Purpose |
|------|---------|
| `.github/ISSUE_TEMPLATE/bug_report.md` | Bug report template |
| `.github/ISSUE_TEMPLATE/feature_request.md` | Feature request template |
| `.github/PULL_REQUEST_TEMPLATE.md` | Pull request template |
| `.github/FUNDING.yml` | Sponsorship configuration |

## 🎯 Module Dependencies

```
HighAlchBot (Main Script)
├── AlchBotGUI (User Interface)
│   ├── GUIConfiguration (Data)
│   └── GUIComponentFactory (Components)
│
├── AlchingEngine (Core Logic)
│   ├── BotState (States)
│   └── ScriptStatistics (Stats)
│
├── OverlayRenderer (Display)
│   └── ScriptStatistics (Stats)
│
├── AntibanSystem (Behavior)
├── DiscordManager (Notifications)
│   ├── GUIConfiguration (Data)
│   └── ScriptStatistics (Stats)
│
├── ItemDatabase (Data)
├── ItemSearchAPI (API)
├── PriceManager (Pricing)
├── SettingsManager (Persistence)
│   └── GUIConfiguration (Data)
│
└── BotUtils (Utilities)
```

## 📊 Code Metrics

### Total Lines of Code

| Category | Lines | Files |
|----------|-------|-------|
| Java Source | ~7,657 | 15 |
| Extracted Classes | ~880 | 4 |
| Documentation | ~15,000+ | 9 |
| Configuration | ~200 | 6 |
| **Total** | **~23,737+** | **34** |

### File Size Distribution

| Size Range | Count | Files |
|------------|-------|-------|
| 0-200 lines | 4 | BotState, GUIConfiguration |
| 201-500 lines | 8 | Most utilities |
| 501-1000 lines | 3 | AlchingEngine, OverlayRenderer, HighAlchBot |
| 1000+ lines | 1 | AlchBotGUI (being refactored) |

## 🔄 Refactoring Progress

### Phase 1: Core Class Extraction ✅
- Extracted 4 classes (880 lines)
- Created new package structure
- Reduced code duplication

### Phase 2a: Import Updates ✅
- Updated all files to use new classes
- Removed inner classes
- Reduced ~147 lines

### Phase 2b: Panel Extraction (In Progress)
- Extract GUI panels from AlchBotGUI
- Target: Reduce AlchBotGUI to ~500 lines
- Create modular panel architecture

## 🏗️ Architecture Patterns

### Design Patterns Used

1. **Factory Pattern**
   - `GUIComponentFactory` creates styled components

2. **State Pattern**
   - `BotState` enum defines bot states
   - `AlchingEngine` implements state machine

3. **Data Class Pattern**
   - `GUIConfiguration` holds configuration data
   - `ScriptStatistics` tracks statistics

4. **Manager Pattern**
   - `DiscordManager` handles Discord integration
   - `PriceManager` manages pricing data
   - `SettingsManager` persists settings

5. **Coordinator Pattern**
   - `HighAlchBot` coordinates all components

6. **Utility Class Pattern**
   - `BotUtils` provides shared utilities

## 📚 Development Workflow

### Adding New Features

1. Identify appropriate package/module
2. Follow existing patterns
3. Update documentation
4. Add to CHANGELOG.md
5. Create pull request

### Refactoring Guidelines

1. Keep files under 500 lines
2. Single responsibility principle
3. Extract reusable components
4. Update imports and references
5. Test thoroughly

## 🧪 Testing Structure (Planned)

```
test/
├── Hi_alch/
│   ├── core/
│   ├── engine/
│   ├── gui/
│   ├── overlay/
│   └── utils/
└── resources/
    └── test-data/
```

## 📝 Notes

- All Java files use UTF-8 encoding
- Indentation: 4 spaces (no tabs)
- Line endings: LF (Unix-style)
- Max line length: 120 characters
- JavaDoc required for public classes and methods

## 🔗 Related Documents

- [README.md](README.md) - Getting started
- [CONTRIBUTING.md](CONTRIBUTING.md) - How to contribute
- [REFACTORING_GUIDE.md](REFACTORING_GUIDE.md) - Refactoring details
- [CHANGELOG.md](CHANGELOG.md) - Version history

---

**Last Updated**: 2025-01-XX
**Version**: 2.0.0
**Status**: Active Development
