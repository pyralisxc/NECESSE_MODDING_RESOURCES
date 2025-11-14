/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.Packet
 *  necesse.engine.network.packet.PacketHitObject
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.GrassObject
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$OnNonDefaultValue
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketHitObject;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=GrassObject.class, name="attackThrough", arguments={Level.class, int.class, int.class, GameDamage.class, Attacker.class})
public class GrassNoBreakWhenNoDamage {
    @Advice.OnMethodEnter(skipOn=Advice.OnNonDefaultValue.class)
    static boolean onEnter(@Advice.This GrassObject grassObject, @Advice.Argument(value=0) Level level, @Advice.Argument(value=1) int x, @Advice.Argument(value=2) int y, @Advice.Argument(value=3) GameDamage damage, @Advice.Argument(value=4) Attacker attacker) {
        if (damage.damage == 0.0f) {
            level.getServer().network.sendToClientsWithTile((Packet)new PacketHitObject(level, x, y, (GameObject)grassObject, damage), level, x, y);
            return true;
        }
        return false;
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.This GrassObject grassObject, @Advice.Argument(value=0) Level level, @Advice.Argument(value=1) int x, @Advice.Argument(value=2) int y, @Advice.Argument(value=3) GameDamage damage, @Advice.Argument(value=4) Attacker attackerlevel) {
    }
}

