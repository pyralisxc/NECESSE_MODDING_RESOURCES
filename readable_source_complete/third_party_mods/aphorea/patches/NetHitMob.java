/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.miscToolItem.NetToolItem
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import aphorea.mobs.friendly.WildPhosphorSlime;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.miscToolItem.NetToolItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=NetToolItem.class, name="hitMob", arguments={InventoryItem.class, ToolItemMobAbilityEvent.class, Level.class, Mob.class, Mob.class})
public class NetHitMob {
    @Advice.OnMethodExit
    static void onExit(@Advice.This NetToolItem netToolItem, @Advice.Argument(value=0) InventoryItem item, @Advice.Argument(value=1) ToolItemMobAbilityEvent event, @Advice.Argument(value=2) Level level, @Advice.Argument(value=3) Mob target, @Advice.Argument(value=4) Mob attacker) {
        PlayerMob player;
        if (target instanceof WildPhosphorSlime && attacker.isPlayer && (player = (PlayerMob)attacker).isServerClient()) {
            player.getServerClient().newStats.mob_kills.addKill(target);
        }
    }
}

