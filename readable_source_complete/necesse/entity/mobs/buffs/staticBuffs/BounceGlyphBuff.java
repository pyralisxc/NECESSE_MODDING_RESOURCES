/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketBounceGlyphTrap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.BounceGlyphTrapEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class BounceGlyphBuff
extends Buff {
    public BounceGlyphBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public static void bounceMobAndSendPacket(Level level, float fromX, float fromY, Mob target) {
        if (level.isServer()) {
            float angle = GameMath.getAngle(GameMath.normalize(target.x - fromX, target.y - fromY));
            float strength = GameRandom.globalRandom.getFloatBetween(150.0f, 200.0f);
            PacketBounceGlyphTrap.applyToMob(target, target.x, target.y, angle, strength);
            level.getServer().network.sendToClientsWithEntity(new PacketBounceGlyphTrap(target, target.x, target.y, angle, strength), target);
        }
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.BOUNCY, true);
        buff.setMaxModifier(BuffModifiers.FRICTION, Float.valueOf(0.03f), Integer.MAX_VALUE);
        if (buff.owner.isClient()) {
            GNDItemMap gndData = buff.getGndData();
            gndData.setBoolean("lastXDir", buff.owner.dx >= 0.0f);
            gndData.setBoolean("lastYDir", buff.owner.dy >= 0.0f);
            Level level = buff.owner.getLevel();
            int count = 8;
            for (int i = 0; i < count; ++i) {
                float offset = (float)i * 360.0f / (float)count;
                ParticleOption particle = level.entityManager.addParticle(buff.owner, Particle.GType.IMPORTANT_COSMETIC).lifeTime(buff.getDurationLeft()).dontRotate().color(BounceGlyphTrapEvent.particleColor).givesLight(BounceGlyphTrapEvent.particleHue, 0.6f).sprite(GameResources.bubbleParticle.sprite(0, 0, 16)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float rotationSpeed = 0.25f * (float)(buff.owner.dx >= 0.0f ? 1 : -1);
                    pos.x = GameMath.cos((float)buff.owner.getTime() * rotationSpeed + offset) * 12.0f + 1.0f;
                    pos.y = GameMath.sin((float)buff.owner.getTime() * rotationSpeed + offset) * 10.0f + 3.0f;
                }).size((options, lifeTime, timeAlive, lifePercent) -> {
                    ActiveBuff currentBuff = buff.owner.buffManager.getBuff(BuffRegistry.BOUNCE_GLYPH);
                    if (currentBuff != null) {
                        int size = (int)(10.0f * ((float)currentBuff.getDurationLeft() / (float)currentBuff.getDuration())) + 5;
                        options.size(size, size);
                    }
                }).onDied(pos -> level.entityManager.addParticle(buff.owner.x + pos.x, buff.owner.y + pos.y, Particle.GType.IMPORTANT_COSMETIC).color(BounceGlyphTrapEvent.particleColor).givesLight(BounceGlyphTrapEvent.particleHue, 0.6f).sizeFades(8, 8).lifeTime(500).movesFriction(pos.x, -10.0f, 1.0f));
                particle.removeIf(() -> {
                    ActiveBuff currentBuff = buff.owner.buffManager.getBuff(BuffRegistry.BOUNCE_GLYPH);
                    return currentBuff != buff;
                });
            }
        }
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        boolean xDir = owner.dx >= 0.0f;
        boolean yDir = owner.dy >= 0.0f;
        GNDItemMap gndData = buff.getGndData();
        boolean lastXDir = gndData.getBoolean("lastXDir");
        boolean lastYDir = gndData.getBoolean("lastYDir");
        if (xDir != lastXDir || yDir != lastYDir) {
            gndData.setBoolean("lastXDir", xDir);
            gndData.setBoolean("lastYDir", yDir);
            Level level = owner.getLevel();
            float x = owner.x;
            float y = owner.y;
            float angle = GameMath.getAngle(GameMath.normalize(owner.dx, owner.dy));
            for (int i = 0; i < 10; ++i) {
                float particlePosX = x + GameRandom.globalRandom.getFloatBetween(-8.0f, 8.0f);
                float particlePosY = y + GameRandom.globalRandom.getFloatBetween(-8.0f, 8.0f);
                float particleAngle = angle + GameRandom.globalRandom.floatGaussian() * 10.0f;
                float speed = GameRandom.globalRandom.getFloatBetween(20.0f, 40.0f);
                ParticleOption particle = level.entityManager.addParticle(particlePosX, particlePosY, Particle.GType.COSMETIC).color(BounceGlyphTrapEvent.particleColor).sizeFades(5, GameRandom.globalRandom.getIntBetween(8, 12)).movesFrictionAngle(particleAngle, speed, 0.8f).lifeTime(GameRandom.globalRandom.getIntBetween(750, 1000)).givesLight(BounceGlyphTrapEvent.particleHue, 0.8f);
                particle.onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> particle.givesLight((int)(100.0f * (1.0f - lifePercent))));
            }
        }
    }
}

