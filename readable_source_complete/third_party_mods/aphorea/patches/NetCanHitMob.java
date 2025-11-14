/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.Mob
 *  necesse.inventory.item.toolItem.miscToolItem.NetToolItem
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import aphorea.mobs.friendly.WildPhosphorSlime;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.inventory.item.toolItem.miscToolItem.NetToolItem;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=NetToolItem.class, name="canHitMob", arguments={Mob.class, ToolItemMobAbilityEvent.class})
public class NetCanHitMob {
    @Advice.OnMethodExit
    static void onExit(@Advice.This NetToolItem netToolItem, @Advice.Argument(value=0) Mob mob, @Advice.Argument(value=1) ToolItemMobAbilityEvent toolItemEvent, @Advice.Return(readOnly=false) boolean result) {
        if (mob instanceof WildPhosphorSlime) {
            result = true;
        }
    }
}

