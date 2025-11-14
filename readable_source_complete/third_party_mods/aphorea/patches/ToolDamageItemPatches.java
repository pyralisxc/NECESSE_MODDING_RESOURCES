/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.AttackHandler
 *  necesse.entity.mobs.attackHandler.ToolDamageItemAttackHandler
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.ToolDamageItem
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import java.awt.geom.Line2D;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.attackHandler.ToolDamageItemAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

public class ToolDamageItemPatches {

    @ModMethodPatch(target=ToolDamageItem.class, name="onAttack", arguments={Level.class, int.class, int.class, ItemAttackerMob.class, int.class, InventoryItem.class, ItemAttackSlot.class, int.class, int.class, GNDItemMap.class})
    public static class onAttack {
        @Advice.OnMethodEnter
        static boolean onEnter(@Advice.This ToolDamageItem This2) {
            return true;
        }

        @Advice.OnMethodExit
        static void onExit(@Advice.This ToolDamageItem This2, @Advice.Argument(value=0) Level level, @Advice.Argument(value=1) int x, @Advice.Argument(value=2) int y, @Advice.Argument(value=3) ItemAttackerMob attackerMob, @Advice.Argument(value=4) int attackHeight, @Advice.Argument(value=5) InventoryItem item, @Advice.Argument(value=6) ItemAttackSlot slot, @Advice.Argument(value=7) int animAttack, @Advice.Argument(value=8) int seed, @Advice.Argument(value=9) GNDItemMap mapContent, @Advice.Return(readOnly=false) InventoryItem returnItem) {
            if (attackerMob.isPlayer) {
                attackerMob.startAttackHandler((AttackHandler)new ToolDamageItemAttackHandler((PlayerMob)attackerMob, slot, x, y, seed, This2, mapContent));
            } else {
                item = This2.runTileAttack(level, x, y, attackerMob, (Line2D)null, item, animAttack, mapContent);
            }
            returnItem = item = This2.startToolItemEventAbilityEvent(level, x, y, attackerMob, attackHeight, item, seed);
        }
    }
}

