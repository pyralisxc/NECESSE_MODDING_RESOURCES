/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.placeableItem.PlaceableItem
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import medievalsim.buildmode.BlockCountCalculator;
import medievalsim.buildmode.BuildModeManager;
import medievalsim.util.ModLogger;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.PlaceableItem;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=Item.class, name="getTooltips", arguments={InventoryItem.class, PlayerMob.class, GameBlackboard.class})
public class ItemGetTooltipsPatch {
    @Advice.OnMethodExit
    static void onExit(@Advice.This Item item, @Advice.Argument(value=0) InventoryItem inventoryItem, @Advice.Argument(value=1) PlayerMob perspective, @Advice.Argument(value=2) GameBlackboard blackboard, @Advice.Return(readOnly=false) ListGameTooltips tooltips) {
        if (!(item instanceof PlaceableItem)) {
            return;
        }
        if (!BuildModeManager.isActive()) {
            return;
        }
        try {
            BuildModeManager manager = BuildModeManager.getInstance();
            if (manager.selectedShape == 0) {
                return;
            }
            int blockCount = BlockCountCalculator.calculateBlockCount(manager.selectedShape, manager.isHollow, manager.lineLength, manager.squareSize, manager.circleRadius);
            String shapeName = manager.getShapeName(manager.selectedShape, manager.isHollow);
            String tooltip = Localization.translate((String)"ui", (String)"buildmodeblockcost", (Object[])new Object[]{"shape", shapeName, "count", blockCount});
            tooltips.add(tooltip, 900);
        }
        catch (Exception e) {
            ModLogger.error("Failed to add build mode tooltip", e);
        }
    }
}

