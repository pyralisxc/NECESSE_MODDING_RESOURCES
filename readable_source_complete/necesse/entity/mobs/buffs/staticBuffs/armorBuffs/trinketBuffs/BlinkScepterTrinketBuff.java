/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketBlinkScepter;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class BlinkScepterTrinketBuff
extends TrinketBuff
implements BuffAbility {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        Level level = player.getLevel();
        int range = 224;
        Point2D.Float dir = PacketBlinkScepter.getMobDir(player);
        PacketBlinkScepter.applyToMob(level, player, dir.x, dir.y, range);
        PacketForceOfWind.addCooldownStack(player, 5.0f, false);
        if (level.isClient()) {
            SoundManager.playSound(GameResources.swoosh2, (SoundEffect)SoundEffect.effect(player).volume(0.5f));
        }
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.isRiding() && !PacketForceOfWind.isOnCooldown(buff.owner);
    }
}

