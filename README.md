# 🔥 OSRS High Alchemy Bot v2.0

A professional, feature-rich high alchemy bot for Old School RuneScape (OSRS) built with DreamBot API.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![DreamBot](https://img.shields.io/badge/DreamBot-API-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-Educational-green?style=for-the-badge)

## ✨ Features

### 🎯 Core Functionality
- **Smart High Alchemy**: Automated high alchemy with profit optimization
- **Live Market Data**: Real-time OSRS Wiki API integration for accurate pricing
- **Grand Exchange Integration**: Automated buying and selling
- **Profit Analysis**: Real-time profit calculations and projections
- **Multiple Item Support**: F2P and P2P item database with 40+ profitable items

### 🖼️ Professional GUI
- **Modern Interface**: Clean, professional Swing GUI with Substance theme support
- **Live Profit Preview**: Real-time profit analysis for selected items
- **Item Search**: Search and validate custom items using OSRS Wiki API
- **Configuration Tabs**: Organized settings across Configuration, Advanced, and Analytics tabs
- **Visual Feedback**: Progress indicators, status updates, and color-coded information

### 🛡️ Advanced Anti-Ban System
- **Human-Like Behavior**: Random delays, camera movements, tab checking
- **Intelligent Break System**: Randomized breaks based on session length and fatigue
- **Fatigue Simulation**: Behavior changes as session progresses (slower reactions, more mistakes)
- **Profile-Based Seeding**: Personalized anti-ban patterns unique to your account
- **Configurable Aggression**: 1-10 scale from very safe to fast execution

### 📊 Analytics & Monitoring
- **Live Statistics Overlay**: Real-time display of alchs, profit, XP, and rates
- **Performance Metrics**: Alchs/hour, profit/hour, XP/hour tracking
- **Success Rate Monitoring**: Track consecutive successes and error rates
- **Market Data Display**: Current buy prices, alch values, and profit margins
- **Runtime Statistics**: Session duration, total operations, efficiency metrics

### 🔔 Discord Integration
- **Webhook Notifications**: Bot start, progress updates, completion, and emergency alerts
- **Rich Embeds**: Professional Discord embeds with detailed statistics
- **Configurable Updates**: Periodic progress reports (every 30 minutes)
- **Test Function**: Verify webhook configuration before running

### 💾 Configuration Management
- **Save/Load Profiles**: Save and load bot configurations
- **Auto-Save**: Automatic configuration backup
- **Default Settings**: Quick-start with optimized default values
- **File Dialogs**: User-friendly save/load interface

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- DreamBot client
- OSRS account (F2P or P2P)
- (Optional) Discord webhook for notifications

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/lukester814/hi-alch-bot.git
   cd hi-alch-bot
   ```

2. **Build the project**
   ```bash
   # Using Gradle
   gradle build

   # Or manually compile
   javac -cp "dreambot-api.jar" src/Hi_alch/*.java
   ```

3. **Add to DreamBot**
   - Open DreamBot client
   - Navigate to Scripts → Local Scripts
   - Add the compiled script directory
   - Select "High Alchemy Bot v2.0"

### Quick Start Guide

1. **Launch the Bot**
   - Start DreamBot
   - Select "High Alchemy Bot v2.0" from the script list
   - Click Start

2. **Configure Settings**
   - **Select Item**: Choose from F2P or P2P items (or use custom item search)
   - **Buy Limit**: Set your GE buy limit (typically 100-1000)
   - **Price Markup**: Percentage above market price (5-10% recommended)
   - **Nature Runes**: Amount to purchase (1000+ recommended)

3. **Enable Optional Features**
   - **Smart Profit Analysis**: Real-time market data and profit calculation
   - **World Hopping**: Automatic world changes if GE is slow
   - **Auto-Configure Alchemy**: Sets high alch warning threshold to 10M GP
   - **Items Already in Inventory**: Skip buying if you already have items

4. **Configure Anti-Ban** (Advanced Tab)
   - **Enable Anti-Ban**: Turn on human-like behavior patterns
   - **Aggression Level**: 1 (safest) to 10 (fastest)
   - **Break System**: Enable intelligent break scheduling
   - **Profile Seed**: Generate unique behavioral profile

5. **Discord Notifications** (Advanced Tab)
   - Enter your Discord webhook URL
   - Click "Test" to verify connection
   - Enable notifications

6. **Start Botting**
   - Review profit preview
   - Click "Start High Alchemy Bot"
   - Monitor the in-game overlay for statistics

## 📋 Item Database

### F2P Items (22 items)
**Rune Equipment** (Best Profit)
- Rune 2h sword, Rune platebody, Rune platelegs
- Rune chainbody, Rune longsword, Rune scimitar
- And more...

**Adamant Equipment** (Mid-tier)
- Adamant platebody, platelegs, chainbody
- Various shields and weapons

**Dragonhide Armor**
- Green d'hide body, Blue d'hide body

### P2P Items (18+ items)
**Battlestaffs** (BEST P2P Profit)
- Air, Water, Earth, Fire battlestaffs
- ~9,300 GP alch value each

**Dragon Equipment**
- Dragon longsword, scimitar, battleaxe
- Dragon dagger, mace

**Advanced Dragonhide**
- Black, Red d'hide bodies

**Mystic Robes**
- Full mystic set support

## 🔧 Architecture

### Module Overview

```
Hi_alch/
├── HighAlchBot.java      # Main coordinator & DreamBot script
├── AlchingEngine.java    # Core alchemy state machine
├── AlchBotGUI.java       # Professional Swing interface
├── AntibanSystem.java    # Human-like behavior simulation
├── BotUtils.java         # Utility methods & logging
├── DiscordManager.java   # Discord webhook integration
├── ItemDatabase.java     # Static item data (40+ items)
├── ItemSearchAPI.java    # OSRS Wiki API integration
├── OverlayRenderer.java  # In-game statistics overlay
├── PriceManager.java     # Live GE price management
└── SettingsManager.java  # Configuration persistence
```

### State Machine (AlchingEngine)
1. **INITIALIZING**: Setup and validation
2. **CHECKING_SUPPLIES**: Verify inventory and runes
3. **BUYING_ITEMS**: Purchase from Grand Exchange
4. **BUYING_NATURE_RUNES**: Restock nature runes
5. **ALCHING**: Perform high alchemy operations
6. **RESTOCKING**: Refill supplies when low
7. **BANKING**: Deposit/withdraw items
8. **ERROR_RECOVERY**: Handle failures gracefully
9. **COMPLETED**: Session finished

## ⚙️ Configuration Options

### Main Settings
- **Item Selection**: Choose from database or search custom items
- **Buy Limit**: GE buy limit (1-10,000)
- **Price Markup**: % above market price (0-50%)
- **Nature Runes**: Amount to purchase (100-10,000)
- **Smart Profit**: Enable/disable live market analysis
- **World Hopping**: Automatic world switching
- **Skip Buying**: Use items already in inventory
- **Restock When Empty**: Buy more items or stop

### Anti-Ban Settings
- **Enable Anti-Ban**: Master toggle
- **Aggression Level**: 1 (safe) to 10 (fast)
- **Break System**: Intelligent break scheduling
- **Break Range**: Min-max break duration (minutes)
- **Fatigue System**: Simulate player tiredness
- **Profile Seeding**: Unique behavioral patterns
- **User Profile Seed**: Custom seed for randomization

### Discord Settings
- **Webhook URL**: Discord channel webhook
- **Enable Notifications**: Toggle Discord messages
- **Test Webhook**: Verify connection

## 📊 Statistics & Monitoring

### Real-Time Overlay
- Alchs completed
- Total profit (GP)
- XP gained
- Runtime duration
- Alchs/hour rate
- Profit/hour rate
- XP/hour rate
- Success rate percentage
- Current state/action

### Discord Notifications
- **Startup**: Bot configuration summary
- **Progress**: Every 30 minutes (configurable)
- **Completion**: Final session statistics
- **Emergency**: Critical errors or issues

## 🛠️ Development

### Building from Source

```bash
# Clone the repository
git clone https://github.com/lukester814/hi-alch-bot.git
cd hi-alch-bot

# Build with Gradle (if build.gradle is configured)
gradle build

# Or compile manually
javac -cp "path/to/dreambot-api.jar" src/Hi_alch/*.java
```

### Project Structure
```
hi-alch-bot/
├── src/
│   ├── Hi_alch/          # Main package
│   └── META-INF/         # Manifest
├── .idea/                # IntelliJ IDEA config
├── .gitignore            # Git ignore rules
├── README.md             # This file
├── CHANGELOG.md          # Version history
└── LICENSE               # License information
```

### Dependencies
- **DreamBot API**: Required for OSRS client integration
- **Java Swing**: Built-in GUI framework
- **Java Network**: HTTP requests for API calls
- **Java IO**: File operations for settings

## ⚠️ Important Disclaimers

### Educational Purpose
This project is for **educational purposes only**. It demonstrates:
- Java programming concepts
- API integration
- GUI development
- State machine patterns
- Anti-detection techniques

### Terms of Service
- Using bots violates Old School RuneScape's Terms of Service
- Botting can result in account bans
- Use at your own risk
- The authors are not responsible for any consequences

### Responsible Use
If you choose to use this bot:
- Use on alternate accounts only
- Understand the risks involved
- Follow reasonable usage patterns
- Don't use for commercial gain
- Respect the game and other players

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Standards
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Include error handling
- Test thoroughly before submitting

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Note**: This is an educational project. The authors do not endorse or encourage violating game terms of service.

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/lukester814/hi-alch-bot/issues)
- **Discussions**: [GitHub Discussions](https://github.com/lukester814/hi-alch-bot/discussions)

## 🙏 Acknowledgments

- DreamBot team for their excellent API
- OSRS Wiki for price data API
- RuneScape community for item data
- Contributors and testers

## 📈 Roadmap

### Planned Features
- [ ] MySQL database integration for historical data
- [ ] Web dashboard for remote monitoring
- [ ] Multi-account support
- [ ] Advanced profit optimization algorithms
- [ ] Machine learning for anti-ban improvements
- [ ] Custom item lists and favorites
- [ ] Export statistics to CSV
- [ ] Mobile notifications (Telegram/SMS)

---

**Made with ❤️ for the OSRS botting community**

*Remember: Always bot responsibly and at your own risk.*
