/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class ForceOfWindTrinketBuff
extends TrinketBuff
implements BuffAbility {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        Level level = player.getLevel();
        int strength = 150;
        Point2D.Float dir = PacketForceOfWind.getMobDir(player);
        PacketForceOfWind.applyToMob(level, player, dir.x, dir.y, strength);
        PacketForceOfWind.addCooldownStack(player, 3.0f, false);
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)player, 0.15f, null), false);
        player.buffManager.forceUpdateBuffs();
        if (level.isClient()) {
            SoundManager.playSound(GameResources.swoosh, (SoundEffect)SoundEffect.effect(player).volume(0.5f).pitch(1.7f));
        }
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.isRiding() && !PacketForceOfWind.isOnCooldown(buff.owner);
    }
}

