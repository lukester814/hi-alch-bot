package Hi_alch.engine;

import Hi_alch.utils.BotUtils;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

/**
 * Handles high alchemy spell execution
 */
public class AlchemyExecutor {

    private static final int NATURE_RUNE_ID = 563;

    /**
     * Perform high alchemy on an item
     * Returns true if successful, false otherwise
     */
    public static boolean performAlchemy(int itemId, String itemName) {
        try {
            // Check supplies
            if (!Inventory.contains(itemId)) {
                BotUtils.log("❌ No items to alch");
                return false;
            }

            if (!Inventory.contains(NATURE_RUNE_ID)) {
                BotUtils.log("❌ No nature runes");
                return false;
            }

            // Open magic tab if not already open
            if (!Tabs.isOpen(Tab.MAGIC)) {
                Tabs.open(Tab.MAGIC);
                Sleep.sleepUntil(() -> Tabs.isOpen(Tab.MAGIC), 2000);
                return false; // Try again next loop
            }

            // Check if we can cast the spell
            if (!Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY)) {
                BotUtils.log("❌ Cannot cast High Level Alchemy");
                return false;
            }

            // Get the item to alch
            Item itemToAlch = Inventory.get(itemId);

            if (itemToAlch == null) {
                BotUtils.log("❌ Item not found in inventory");
                return false;
            }

            BotUtils.log("🎯 Casting High Alchemy on: " + itemToAlch.getName());

            // Cast the spell
            if (Magic.castSpell(Normal.HIGH_LEVEL_ALCHEMY)) {
                Sleep.sleep(300);

                // Click the item
                if (itemToAlch.interact()) {
                    Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000);

                    BotUtils.log("🔥 Alchemy completed successfully");
                    return true;

                } else {
                    BotUtils.log("❌ Failed to click item after casting spell");
                    return false;
                }
            } else {
                BotUtils.log("❌ Failed to cast High Level Alchemy spell");
                return false;
            }

        } catch (Exception e) {
            BotUtils.logError("Error during alchemy", e);
            return false;
        }
    }

    /**
     * Calculate delay between alchs with randomization
     */
    public static int getAlchDelay() {
        return BotUtils.randomDelay(1400, 2200);
    }
}
