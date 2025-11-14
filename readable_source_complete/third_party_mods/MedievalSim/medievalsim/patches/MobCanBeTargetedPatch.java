/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.NetworkClient
 *  necesse.entity.mobs.Mob
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import medievalsim.zones.AdminZonesLevelData;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.NetworkClient;
import necesse.entity.mobs.Mob;
import net.bytebuddy.asm.Advice;

public class MobCanBeTargetedPatch {

    @ModMethodPatch(target=Mob.class, name="canBeTargeted", arguments={Mob.class, NetworkClient.class})
    public static class CanBeTargeted {
        @Advice.OnMethodExit
        static void onExit(@Advice.This Mob targetMob, @Advice.Argument(value=0) Mob attacker, @Advice.Argument(value=1) NetworkClient attackerClient, @Advice.Return(readOnly=false) boolean result) {
            if (result) {
                return;
            }
            if (!targetMob.isPlayer || !attacker.isPlayer) {
                return;
            }
            if (!targetMob.isServer()) {
                return;
            }
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(targetMob.getLevel(), false);
            if (zoneData != null && zoneData.areBothInPvPZone(attacker.x, attacker.y, targetMob.x, targetMob.y)) {
                result = true;
            }
        }
    }
}

