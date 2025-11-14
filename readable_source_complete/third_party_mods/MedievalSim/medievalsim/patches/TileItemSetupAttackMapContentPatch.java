/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.placeableItem.tileItem.TileItem
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 */
package medievalsim.patches;

import medievalsim.buildmode.BuildModeManager;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=TileItem.class, name="setupAttackMapContent", arguments={GNDItemMap.class, Level.class, int.class, int.class, ItemAttackerMob.class, int.class, InventoryItem.class})
public class TileItemSetupAttackMapContentPatch {
    @Advice.OnMethodExit
    static void onExit(@Advice.Argument(value=0) GNDItemMap map, @Advice.Argument(value=1) Level level, @Advice.Argument(value=4) ItemAttackerMob attackerMob) {
        if (level.isServer()) {
            return;
        }
        if (!attackerMob.isPlayer) {
            return;
        }
        BuildModeManager manager = null;
        try {
            manager = BuildModeManager.getInstance();
        }
        catch (IllegalStateException e) {
            return;
        }
        if (manager != null && manager.buildModeEnabled) {
            map.setBoolean("medievalsim_buildmode", true);
            map.setInt("medievalsim_shape", manager.selectedShape);
            map.setBoolean("medievalsim_isHollow", manager.isHollow);
            map.setInt("medievalsim_lineLength", manager.lineLength);
            map.setInt("medievalsim_squareSize", manager.squareSize);
            map.setInt("medievalsim_circleRadius", manager.circleRadius);
            map.setInt("medievalsim_spacing", manager.spacing);
            map.setInt("medievalsim_direction", manager.direction);
        }
    }
}

