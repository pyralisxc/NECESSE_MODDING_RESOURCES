/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.level.maps.Level
 */
package medievalsim.zones;

import java.lang.reflect.Field;
import java.util.ArrayList;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.level.maps.Level;

public class PvPZoneDotHandler {
    private static Field dotBufferField;

    public static void processLevelTick(Level level) {
        if (level == null || !level.isServer()) {
            return;
        }
        if (dotBufferField == null) {
            return;
        }
        AdminZonesLevelData zones = AdminZonesLevelData.getZoneData(level, false);
        if (zones == null) {
            return;
        }
        try {
            for (Mob mob : level.entityManager.mobs) {
                ArrayList abList;
                PvPZone zone;
                if (mob == null || mob.removed() || (zone = zones.getPvPZoneAt(mob.getX(), mob.getY())) == null) continue;
                float dmgMult = zone.dotDamageMultiplier;
                float intervalMult = zone.dotIntervalMultiplier;
                if (dmgMult == 1.0f && intervalMult == 1.0f || (abList = mob.buffManager.getArrayBuffs()) == null || abList.isEmpty()) continue;
                float combined = Math.max(0.0f, dmgMult) * Math.max(0.0f, intervalMult);
                for (ActiveBuff ab : abList) {
                    if (ab == null) continue;
                    try {
                        float buf = dotBufferField.getFloat(ab);
                        if (buf <= 0.0f) continue;
                        float newBuf = buf * combined;
                        dotBufferField.setFloat(ab, newBuf);
                    }
                    catch (Exception exception) {}
                }
            }
        }
        catch (Exception e) {
            ModLogger.error("PvPZoneDotHandler error", e);
        }
    }

    static {
        try {
            dotBufferField = ActiveBuff.class.getDeclaredField("dotBuffer");
            dotBufferField.setAccessible(true);
        }
        catch (Exception e) {
            dotBufferField = null;
            ModLogger.error("PvPZoneDotHandler - failed to access ActiveBuff.dotBuffer", e);
        }
    }
}

