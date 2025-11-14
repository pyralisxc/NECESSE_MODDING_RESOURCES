/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.input.Control
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.ActiveBuffAbility
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.StaminaBuff
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.camera.GameCamera
 */
package aphorea.buffs.Trinkets.Shield;

import aphorea.levelevents.AphSpinelShieldEvent;
import aphorea.utils.AphColors;
import java.awt.Color;
import necesse.engine.input.Control;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;

public class SpinelShieldBuff
extends TrinketBuff
implements ActiveBuffAbility {
    public static int msToDeplete = 6000;
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
    public int clientTicks = 0;
    public int serverTicks = 0;

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public Packet getStartAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        return this.getRunningAbilityContent(player, buff);
    }

    public Packet getRunningAbilityContent(PlayerMob player, ActiveBuff buff) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        StaminaBuff.writeStaminaData((Mob)player, (PacketWriter)writer);
        return content;
    }

    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        if (buff.owner.isRiding() || buff.owner.buffManager.hasBuff("spinelshieldactive")) {
            return false;
        }
        return StaminaBuff.canStartStaminaUsage((Mob)buff.owner);
    }

    public void onActiveAbilityStarted(PlayerMob player, ActiveBuff buff, Packet content) {
        PacketReader reader = new PacketReader(content);
        if (!buff.owner.isServer()) {
            StaminaBuff.readStaminaData((Mob)buff.owner, (PacketReader)reader);
        }
        if (buff.owner.isServer()) {
            this.serverTicks = 0;
        }
        if (buff.owner.isClient()) {
            this.clientTicks = 0;
        }
    }

    public boolean tickActiveAbility(PlayerMob player, ActiveBuff buff, boolean isRunningClient) {
        float usage;
        ActiveBuff shieldBuff = buff.owner.buffManager.getBuff(BuffRegistry.getBuff((String)"spinelshieldactive"));
        if (shieldBuff != null) {
            if (shieldBuff.getDurationLeft() < 200) {
                shieldBuff.setDurationLeftSeconds(0.2f);
            }
        } else {
            buff.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff((String)"spinelshieldactive"), buff.owner, 1.0f, null), true);
        }
        if (buff.owner.isServer() && this.serverTicks < 10) {
            ++this.serverTicks;
            if (buff.owner.isServer() && this.serverTicks == 10) {
                buff.owner.getLevel().entityManager.events.add((LevelEvent)new AphSpinelShieldEvent(buff.owner, SpinelShieldBuff.getInitialAngle(buff.owner)));
            }
        }
        if (buff.owner.isClient() && this.clientTicks < 6) {
            ++this.clientTicks;
            for (int i = 0; i < 2; ++i) {
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                buff.owner.getLevel().entityManager.addParticle(buff.owner.moveX * 3.0f + buff.owner.x - dx, buff.owner.moveY * 3.0f + buff.owner.y - dy, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color((Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.spinel_light, AphColors.spinel})).heightMoves(10.0f, 20.0f, 5.0f, 0.0f, 10.0f, 0.0f).lifeTime(250);
            }
        }
        if (!StaminaBuff.useStaminaAndGetValid((Mob)buff.owner, (float)(usage = 50.0f / (float)msToDeplete))) {
            return false;
        }
        return !isRunningClient || Control.TRINKET_ABILITY.isDown() || buff.owner.buffManager.hasBuff("spinelshieldactive");
    }

    public void onActiveAbilityUpdate(PlayerMob player, ActiveBuff buff, Packet content) {
    }

    public void onActiveAbilityStopped(PlayerMob player, ActiveBuff buff) {
        buff.owner.buffManager.removeBuff(BuffRegistry.getBuff((String)"spinelshieldactive"), false);
    }

    public static float getInitialAngle(Mob mob) {
        if (mob.moveX == 0.0f && mob.moveY == 0.0f) {
            switch (mob.getDir()) {
                case 0: {
                    return -1.5707964f;
                }
                case 1: {
                    return 0.0f;
                }
                case 2: {
                    return 1.5707964f;
                }
                case 3: {
                    return (float)Math.PI;
                }
            }
            return 0.0f;
        }
        return (float)Math.atan2(mob.moveY, mob.moveX);
    }
}

