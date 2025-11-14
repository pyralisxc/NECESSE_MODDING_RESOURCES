/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.Settings;
import necesse.engine.input.Control;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.camera.GameCamera;

public class ZephyrBootsTrinketBuff
extends TrinketBuff
implements ActiveBuffAbility {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public Packet getStartAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        return this.getRunningAbilityContent(player, buff);
    }

    @Override
    public Packet getRunningAbilityContent(PlayerMob player, ActiveBuff buff) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        StaminaBuff.writeStaminaData(player, writer);
        return content;
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        if (buff.owner.isRiding()) {
            return false;
        }
        if (player.isServer() && !Settings.strictServerAuthority) {
            return true;
        }
        return StaminaBuff.canStartStaminaUsage(buff.owner);
    }

    @Override
    public void onActiveAbilityStarted(PlayerMob player, ActiveBuff buff, Packet content) {
        PacketReader reader = new PacketReader(content);
        if (!player.isServer() || !Settings.strictServerAuthority) {
            StaminaBuff.readStaminaData(player, reader);
        }
        if (!buff.owner.inLiquid()) {
            player.buffManager.addBuff(new ActiveBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE, (Mob)player, 1.0f, null), false);
        }
    }

    @Override
    public boolean tickActiveAbility(PlayerMob player, ActiveBuff buff, boolean isRunningClient) {
        if (player.inLiquid()) {
            player.buffManager.removeBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE, false);
        } else {
            long msToDeplete;
            float usage;
            ActiveBuff speedBuff = player.buffManager.getBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE);
            if (speedBuff != null) {
                speedBuff.setDurationLeftSeconds(1.0f);
            } else {
                player.buffManager.addBuff(new ActiveBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE, (Mob)player, 1.0f, null), false);
            }
            if (!(player.moveX == 0.0f && player.moveY == 0.0f || player.dx == 0.0f && player.dy == 0.0f || StaminaBuff.useStaminaAndGetValid(player, usage = 50.0f / (float)(msToDeplete = 4000L)))) {
                return false;
            }
        }
        return !isRunningClient || Control.TRINKET_ABILITY.isDown();
    }

    @Override
    public void onActiveAbilityUpdate(PlayerMob player, ActiveBuff buff, Packet content) {
    }

    @Override
    public void onActiveAbilityStopped(PlayerMob player, ActiveBuff buff) {
        player.buffManager.removeBuff(BuffRegistry.ZEPHYR_BOOTS_ACTIVE, false);
    }
}

