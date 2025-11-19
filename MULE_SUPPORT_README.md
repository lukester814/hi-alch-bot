# 🤝 Mule Support Documentation

## Overview
Comprehensive automated trading system for safely transferring GP and items to your mule account.

## Features

### Core Functionality
- **Automated Player Detection**: Finds your mule by username
- **Smart Trading**: Complete trade automation (both screens)
- **World Coordination**: Automatically hops to transfer world
- **Location Support**: 5 preset meeting locations
- **Inventory Management**: Handles GP and items separately
- **Anti-Ban Protection**: Human-like delays (800-2500ms)
- **Emergency Abort**: Safe cancellation at any point

### GUI Configuration
Access via the new **"Mule Support"** tab in the main GUI.

#### Basic Settings
- **Mule Username**: Your mule's in-game name (case-insensitive)
- **Transfer Location**: Where to meet
  - Grand Exchange
  - Lumbridge
  - Varrock West Bank
  - Edgeville
  - Falador Park
- **Transfer World**: World to hop to (301-580)

#### Auto-Transfer Settings
- **Auto-transfer Profit**
  - Enable/disable automatic GP transfers
  - Configurable threshold (default: 100,000 GP)
  - Transfers all GP when threshold reached

- **Auto-transfer Items**
  - Enable/disable automatic item transfers
  - Configurable threshold (default: 50 items)
  - Transfers all items when threshold reached

### Safety Features
1. **Cooldown System**: 5-minute minimum between transfers
2. **Timeout Protection**: 
   - 3 minutes max wait for mule
   - 1 minute trade timeout
3. **Anti-Ban Delays**: Random human-like pauses
4. **Emergency Abort**: Cancel transfer anytime
5. **Trade State Tracking**: Monitor every step

### Discord Integration
Automatic notifications sent for:
- 🤝 **Transfer Started** (Yellow) - When transfer begins
- ✅ **Transfer Completed** (Green) - GP/items transferred
- ❌ **Transfer Failed** (Red) - If something goes wrong

Each notification includes:
- Mule username
- Transfer details (GP/items)
- Timestamp
- Status information

## Usage Instructions

### Setup
1. Open the bot GUI
2. Navigate to **"Mule Support"** tab
3. Check **"Enable Mule Support"**
4. Enter your mule's username
5. Select transfer location
6. Set transfer world
7. Configure thresholds if using auto-transfer
8. Click **"Save Settings"**

### Testing Connection
Before running the bot:
1. Have your mule logged in at the transfer location
2. Have your mule on the correct world
3. Click **"Test Connection"** button
4. Bot will verify mule is present

### Manual Transfer
To force a transfer immediately:
1. Click **"Manual Transfer"** button
2. Bot will initiate transfer process
3. Watch status updates in real-time

### Auto-Transfer
When enabled:
- Bot automatically checks thresholds
- Transfers when profit/item count reached
- Respects 5-minute cooldown
- Sends Discord notifications

## Trade Flow

### Step-by-Step Process
1. **Check Thresholds**
   - Verify profit >= threshold (if enabled)
   - Verify items >= threshold (if enabled)
   - Check cooldown timer

2. **Prepare**
   - Hop to transfer world
   - Navigate to location
   - Wait for area loading

3. **Find Mule**
   - Scan for player with matching username
   - Wait up to 3 minutes
   - Abort if not found

4. **Request Trade**
   - Right-click mule
   - Select "Trade with"
   - Wait for trade screen

5. **First Screen**
   - Offer GP (if auto-transfer profit enabled)
   - Offer items (if auto-transfer items enabled)
   - Wait for mule to accept
   - Accept first screen

6. **Second Screen**
   - Verify trade details
   - Wait (human-like delay)
   - Accept second screen

7. **Complete**
   - Wait for trade to close
   - Update statistics
   - Send Discord notification
   - Start cooldown timer

## Statistics Tracking

The GUI displays real-time statistics:
- **Total GP Transferred**: Cumulative GP sent to mule
- **Total Items Transferred**: Cumulative item count
- **Transfer Count**: Number of successful transfers
- **Last Transfer**: Time since last transfer

## Error Handling

### Common Issues

**Mule Not Found**
- Verify mule is logged in
- Check mule is at correct location
- Confirm mule is on transfer world
- Verify username spelling

**Trade Timeout**
- Mule may be AFK
- Trade screen may be blocked
- Try manual transfer with fresh mule session

**Transfer Failed**
- Check Discord for error details
- Verify both accounts can trade
- Ensure no items blocking trade
- Check network connection

### Emergency Procedures
If transfer fails or hangs:
1. Bot automatically aborts after timeout
2. Discord notification sent with details
3. Cooldown timer reset
4. Bot returns to normal operation

## Best Practices

### Safety
1. **Use Different Worlds**: Don't mule on your main world
2. **Vary Transfer Times**: Don't transfer at exact intervals
3. **Reasonable Thresholds**: 100k+ GP, 50+ items recommended
4. **Monitor Discord**: Watch for failure notifications
5. **Test First**: Always test connection before running

### Efficiency
1. **Strategic Locations**: Grand Exchange is fastest
2. **Optimal Thresholds**: Balance safety vs frequency
3. **Cooldown Respect**: 5 minutes minimum for safety
4. **Mule Readiness**: Keep mule logged in during sessions

### Detection Avoidance
1. **Random Delays**: Built-in (800-2500ms)
2. **Human-like Behavior**: Delays between actions
3. **Cooldown System**: Prevents suspicious patterns
4. **Varied Timing**: Don't transfer on exact schedules

## API Reference

### MuleManager Methods

```java
// Configuration
void configure(String username, String location, int world,
               boolean autoProfit, boolean autoItems,
               int profitThresh, int itemThresh)

// Control
boolean initiateTransfer()
boolean checkAndTransfer(int currentProfit, int currentItemCount)
void setEnabled(boolean enabled)

// Status
TradeState getCurrentState()
String getCurrentStateDescription()
boolean isTransferring()
MuleStatistics getStatistics()

// Statistics
int getTotalGPTransferred()
int getTotalItemsTransferred()
int getTransferCount()
```

### GUIMulePanel Methods

```java
// Configuration
MuleConfiguration getConfiguration()
void applyConfiguration(MuleConfiguration config)

// Display Updates
void updateStatus(String status, Color color)
void updateProgress(int value, String message)
void updateStatistics(int totalGP, int totalItems, 
                     int transferCount, long lastTransferTime)

// Event Handling
void setEventListener(MuleEventListener listener)
void setupEventHandlers()
```

## Troubleshooting

### Debug Mode
Enable detailed logging:
```java
BotUtils.setDebugMode(true);  // If available
```

### Check Logs
Monitor console for:
- "🤝 Mule transfer..." messages
- "✅ Transfer completed" confirmations
- "❌" error indicators

### Discord Monitoring
Watch Discord for:
- Transfer started notifications
- Completion confirmations
- Failure alerts with details

## Future Enhancements

Potential additions:
- [ ] Multiple mule accounts
- [ ] Smart location selection
- [ ] Transfer scheduling
- [ ] GP splitting across mules
- [ ] Trade history logging
- [ ] Mule online detection
- [ ] Automatic world finding
- [ ] Custom transfer amounts

## Support

For issues or questions:
1. Check Discord notifications for error details
2. Review logs for "🤝" and "❌" messages
3. Verify mule configuration settings
4. Test connection before full run
5. Report issues with specific error messages

---

**Created**: 2025-11-19  
**Version**: 1.0  
**Status**: Production Ready ✅
