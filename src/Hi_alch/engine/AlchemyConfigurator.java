package Hi_alch.engine;

import Hi_alch.utils.BotUtils;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.methods.container.impl.Inventory;

/**
 * Handles High Level Alchemy spell configuration
 * Configures the warning threshold to prevent confirmation dialogs
 */
public class AlchemyConfigurator {

    private static final int NATURE_RUNE_ID = 563;

    /**
     * Configure High Level Alchemy warning threshold
     */
    public static boolean configure() {
        try {
            BotUtils.log("⚙️ Configuring High Level Alchemy warning threshold...");

            if (!Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY)) {
                int magicLevel = Skills.getBoostedLevel(Skill.MAGIC);
                int natureRunes = Inventory.count(NATURE_RUNE_ID);

                BotUtils.log("❌ Cannot cast High Level Alchemy:");
                BotUtils.log("   Magic Level: " + magicLevel + " (need 55)");
                BotUtils.log("   Nature Runes: " + natureRunes + " (need 1+)");
                return false;
            }

            if (rightClickAlchSpell()) {
                BotUtils.log("✅ Successfully configured High Level Alchemy threshold");
                return true;
            } else {
                BotUtils.log("⚠️ Could not configure warning threshold - will attempt alchemy anyway");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error configuring high alchemy", e);
            return false;
        }
    }

    /**
     * Right-click High Alchemy spell and configure warning
     */
    private static boolean rightClickAlchSpell() {
        try {
            BotUtils.log("🔧 Attempting to configure High Level Alchemy threshold...");

            if (!Tabs.isOpen(Tab.MAGIC)) {
                if (!Tabs.open(Tab.MAGIC)) {
                    BotUtils.log("❌ Failed to open magic tab");
                    return false;
                }
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.MAGIC), 3000);
            }

            Sleep.sleep(1000);
            BotUtils.log("✅ Magic tab is open");

            WidgetChild alchSpell = Widgets.get(218, 44);

            if (alchSpell == null || !alchSpell.isVisible()) {
                BotUtils.log("❌ Could not find High Level Alchemy spell at widget 218, 44");
                return false;
            }

            BotUtils.log("✅ Found High Level Alchemy spell");
            Sleep.sleep(500);

            // Try to interact with "Warnings" action
            if (alchSpell.interact("Warnings")) {
                BotUtils.log("✅ Successfully clicked 'Warnings' from High Alchemy spell");

                // Wait for continue dialog
                Sleep.sleepUntil(() -> {
                    WidgetChild continueDialog = Widgets.get(11, 4);
                    return continueDialog != null && continueDialog.isVisible();
                }, 3000);

                WidgetChild continueDialog = Widgets.get(11, 4);
                if (continueDialog != null && continueDialog.isVisible()) {
                    if (continueDialog.interact()) {
                        BotUtils.log("✅ Successfully clicked 'Click here to continue'");

                        // Wait for threshold option
                        Sleep.sleepUntil(() -> {
                            WidgetChild thresholdOption = Widgets.get(219, 1, 1);
                            return thresholdOption != null && thresholdOption.isVisible();
                        }, 3000);

                        WidgetChild thresholdOption = Widgets.get(219, 1, 1);
                        if (thresholdOption != null && thresholdOption.isVisible()) {
                            if (thresholdOption.interact()) {
                                BotUtils.log("✅ Successfully clicked 'Set value threshold'");

                                // Wait for input field
                                Sleep.sleepUntil(() -> {
                                    WidgetChild inputField = Widgets.get(162, 43);
                                    return inputField != null && inputField.isVisible();
                                }, 3000);

                                WidgetChild inputField = Widgets.get(162, 43);
                                if (inputField != null && inputField.isVisible()) {
                                    if (inputField.interact()) {
                                        Sleep.sleep(500);

                                        BotUtils.log("📝 Typing threshold value: 10000000");

                                        // Type the value
                                        try {
                                            org.dreambot.api.input.Keyboard.type("10000000");
                                            Sleep.sleep(500);
                                            org.dreambot.api.input.Keyboard.type("\n");
                                        } catch (Exception e1) {
                                            BotUtils.log("⚠️ Using fallback keyboard method");
                                            String threshold = "10000000";
                                            for (char c : threshold.toCharArray()) {
                                                org.dreambot.api.input.Keyboard.type(String.valueOf(c));
                                                Sleep.sleep(50);
                                            }
                                            Sleep.sleep(200);
                                            org.dreambot.api.input.Keyboard.type("\n");
                                        }

                                        Sleep.sleep(1000);
                                        BotUtils.log("✅ Successfully set High Alchemy threshold to 10,000,000 GP");
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                BotUtils.log("⚠️ Could not find 'Warnings' option - spell may already be configured");
                // Try basic interaction as fallback
                if (alchSpell.interact()) {
                    BotUtils.log("✅ Basic interaction with High Alchemy spell successful");
                    Sleep.sleep(1000);
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            BotUtils.logError("Error in rightClickAlchSpell", e);
            return false;
        }
    }
}
