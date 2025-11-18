# Contributing to OSRS High Alchemy Bot

First off, thank you for considering contributing to this project! 🎉

This document provides guidelines for contributing to the OSRS High Alchemy Bot project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Code Style Guidelines](#code-style-guidelines)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Pull Request Process](#pull-request-process)

## Code of Conduct

### Our Standards

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Accept constructive criticism gracefully
- Focus on what's best for the community
- Show empathy towards other contributors

### Unacceptable Behavior

- Harassment, trolling, or insulting comments
- Publishing others' private information
- Promoting cheating or unfair advantages in live games
- Other conduct which could reasonably be considered inappropriate

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates.

When creating a bug report, include:

- **Clear title**: Describe the issue briefly
- **Steps to reproduce**: Detailed steps to recreate the bug
- **Expected behavior**: What should happen
- **Actual behavior**: What actually happens
- **Environment**: Java version, DreamBot version, OS
- **Screenshots**: If applicable
- **Error logs**: Any error messages or stack traces

**Example:**

```markdown
**Title**: GUI crashes when selecting custom item

**Steps to Reproduce**:
1. Open the bot GUI
2. Select "Custom P2P item..."
3. Enter "Dragon scimitar"
4. Click "Validate"

**Expected**: Item should be validated and added
**Actual**: GUI freezes and throws NullPointerException

**Environment**:
- Java 11
- DreamBot 3.0.5
- Windows 10

**Error Log**:
```
[Stack trace here]
```
```

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion:

- **Use a clear title**: Describe the enhancement
- **Provide detailed description**: Explain the feature
- **Explain why it's useful**: Describe the benefits
- **Provide examples**: Show how it would work
- **Consider alternatives**: Mention other approaches

### Contributing Code

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/AmazingFeature`
3. **Make your changes**: Follow the code style guidelines
4. **Test thoroughly**: Ensure everything works
5. **Commit your changes**: Follow commit message guidelines
6. **Push to your fork**: `git push origin feature/AmazingFeature`
7. **Open a Pull Request**: Describe your changes

## Development Setup

### Prerequisites

- Java 8 or higher (Java 11 recommended)
- IntelliJ IDEA or Eclipse (recommended)
- Git
- DreamBot client and API

### Setup Steps

1. **Clone your fork**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/hi-alch-bot.git
   cd hi-alch-bot
   ```

2. **Download DreamBot API**:
   - Visit [DreamBot Downloads](https://dreambot.org/)
   - Download the DreamBot JAR
   - Create `libs/` folder in project root
   - Place DreamBot.jar in `libs/`

3. **Build the project**:
   ```bash
   gradle build
   ```

4. **Import into IDE**:
   - **IntelliJ IDEA**: File → Open → Select project directory
   - **Eclipse**: File → Import → Existing Gradle Project

5. **Configure DreamBot**:
   - Add DreamBot.jar to classpath
   - Set up run configuration

### Project Structure

```
hi-alch-bot/
├── src/
│   └── Hi_alch/          # Main source package
│       ├── HighAlchBot.java      # Main coordinator
│       ├── AlchingEngine.java    # Core logic
│       ├── AlchBotGUI.java       # GUI
│       ├── AntibanSystem.java    # Anti-ban
│       ├── BotUtils.java         # Utilities
│       ├── DiscordManager.java   # Discord integration
│       ├── ItemDatabase.java     # Item data
│       ├── ItemSearchAPI.java    # OSRS Wiki API
│       ├── OverlayRenderer.java  # In-game overlay
│       ├── PriceManager.java     # Price management
│       └── SettingsManager.java  # Configuration
├── build.gradle          # Build configuration
├── .editorconfig         # Code style config
├── README.md             # Project documentation
├── CONTRIBUTING.md       # This file
├── CHANGELOG.md          # Version history
└── LICENSE               # License information
```

## Code Style Guidelines

### Java Style

We follow standard Java conventions with some specific preferences:

#### Naming Conventions

```java
// Classes: PascalCase
public class AlchingEngine { }

// Methods: camelCase
public void performHighAlchemy() { }

// Variables: camelCase
private int alchCount;

// Constants: UPPER_SNAKE_CASE
private static final int MAX_RETRIES = 3;

// Packages: lowercase
package hi_alch;
```

#### Formatting

```java
// Braces: K&R style (opening brace on same line)
public void method() {
    if (condition) {
        // code
    } else {
        // code
    }
}

// Indentation: 4 spaces (no tabs)
public void example() {
    int x = 5;
    if (x > 0) {
        doSomething();
    }
}

// Line length: 120 characters maximum
// Wrap long lines at logical points
```

#### Documentation

```java
/**
 * JavaDoc for all public classes and methods
 *
 * @param itemId The OSRS item ID
 * @return The alch profit in GP
 * @throws IllegalArgumentException if itemId is invalid
 */
public int calculateProfit(int itemId) {
    // Implementation
}

// Inline comments for complex logic
private void complexMethod() {
    // First, check if the item exists
    if (itemExists()) {
        // Then calculate the profit margin
        int profit = calculateMargin();

        // Finally, apply any bonuses
        profit = applyBonuses(profit);
    }
}
```

#### Best Practices

```java
// Use meaningful variable names
int alchCount;  // Good
int ac;         // Bad

// Extract magic numbers to constants
private static final int NATURE_RUNE_COST = 250;
int profit = alchValue - buyPrice - NATURE_RUNE_COST;  // Good
int profit = alchValue - buyPrice - 250;                // Bad

// Keep methods focused and small
// Good: One responsibility
public int calculateProfit(int itemId) {
    return getAlchValue(itemId) - getBuyPrice(itemId) - NATURE_RUNE_COST;
}

// Bad: Multiple responsibilities
public int doEverything(int itemId) {
    int value = getAlchValue(itemId);
    int price = getBuyPrice(itemId);
    updateDatabase(itemId);
    sendNotification(itemId);
    return value - price - NATURE_RUNE_COST;
}

// Handle errors appropriately
try {
    riskyOperation();
} catch (SpecificException e) {
    BotUtils.logError("Operation failed", e);
    // Handle or rethrow
}

// Use early returns to reduce nesting
public void method() {
    if (!condition) {
        return;
    }

    // Main logic here without deep nesting
}
```

### EditorConfig

The project includes a `.editorconfig` file. Make sure your editor supports it:

- IntelliJ IDEA: Built-in support
- Eclipse: Install EditorConfig plugin
- VS Code: Install EditorConfig extension

## Commit Message Guidelines

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks

### Examples

```
feat(gui): add custom item search functionality

Implemented item search using OSRS Wiki API with fuzzy matching.
Users can now search for any item by name and get accurate pricing.

Closes #42
```

```
fix(antiban): correct fatigue calculation

Fixed bug where fatigue level wasn't resetting after breaks,
causing increasingly slower behavior over time.

Fixes #38
```

```
docs(readme): update installation instructions

Added detailed steps for IntelliJ IDEA setup and clarified
DreamBot API configuration.
```

### Commit Message Rules

- Use present tense: "add feature" not "added feature"
- Use imperative mood: "move cursor to..." not "moves cursor to..."
- Keep subject line under 72 characters
- Reference issues and pull requests
- Explain *what* and *why*, not *how*

## Pull Request Process

### Before Submitting

1. **Update documentation**: Update README if needed
2. **Update changelog**: Add entry to CHANGELOG.md
3. **Test thoroughly**: Ensure everything works
4. **Follow code style**: Use .editorconfig
5. **Write good commits**: Follow commit guidelines
6. **Update dependencies**: If you added any

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## How Has This Been Tested?
Describe your testing process

## Checklist
- [ ] My code follows the code style of this project
- [ ] I have performed a self-review of my own code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes

## Screenshots (if applicable)
Add screenshots to help explain your changes

## Related Issues
Closes #issue_number
```

### Review Process

1. Automated checks must pass
2. At least one maintainer review required
3. All comments must be resolved
4. Up-to-date with main branch
5. No merge conflicts

### After Approval

1. Squash commits if requested
2. Update PR if changes needed
3. Maintainer will merge when ready

## Questions?

If you have questions:

- Check existing issues and discussions
- Create a new discussion on GitHub
- Ask in the community channels

## Recognition

Contributors will be:

- Listed in the project README
- Credited in the CHANGELOG
- Acknowledged in release notes

---

Thank you for contributing to the OSRS High Alchemy Bot! 🎉

Remember: This is an educational project. Always bot responsibly and at your own risk.
