/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;

public class KatanaDashLevelEvent
extends MobDashLevelEvent {
    protected int maxStacks = 10;
    protected boolean addedStack = false;

    public KatanaDashLevelEvent() {
    }

    public KatanaDashLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage, int maxStacks) {
        super(owner, seed, dirX, dirY, distance, animTime, damage);
        this.maxStacks = maxStacks;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.maxStacks);
        writer.putNextBoolean(this.addedStack);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.maxStacks = reader.getNextShortUnsigned();
        this.addedStack = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        if (this.level != null && this.level.isClient() && this.owner != null) {
            float forceMod = Math.min((float)this.animTime / 700.0f, 1.0f);
            float forceX = this.dirX * this.distance * forceMod;
            float forceY = this.dirY * this.distance * forceMod;
            for (int i = 0; i < 30; ++i) {
                this.level.entityManager.addParticle(this.owner.x + (float)GameRandom.globalRandom.nextGaussian() * 15.0f + forceX / 5.0f, this.owner.y + (float)GameRandom.globalRandom.nextGaussian() * 20.0f + forceY / 5.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 5.0f, forceY * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 5.0f).color(new Color(200, 200, 220)).height(18.0f).lifeTime(700);
            }
        }
        if (this.owner != null) {
            this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, this.owner, this.animTime, null), false);
        }
    }

    @Override
    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        if (!this.addedStack) {
            if (this.owner.buffManager.getStacks(BuffRegistry.KATANA_DASH_STACKS) < this.maxStacks - 1) {
                this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.KATANA_DASH_STACKS, this.owner, 10.0f, null), false);
                this.owner.buffManager.removeBuff(BuffRegistry.Debuffs.KATANA_DASH_COOLDOWN, false);
            } else {
                this.owner.buffManager.removeBuff(BuffRegistry.KATANA_DASH_STACKS, false);
                this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.KATANA_DASH_COOLDOWN, this.owner, 10.0f, null), false);
            }
            this.addedStack = true;
        }
    }

    @Override
    public void dealServerDamage(Mob target, boolean isClientSubmitted) {
        super.dealServerDamage(target, isClientSubmitted);
        if (!this.addedStack) {
            if (this.owner.buffManager.getStacks(BuffRegistry.KATANA_DASH_STACKS) < this.maxStacks - 1) {
                this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.KATANA_DASH_STACKS, this.owner, 10.0f, null), true);
                this.owner.buffManager.removeBuff(BuffRegistry.Debuffs.KATANA_DASH_COOLDOWN, true);
            } else {
                this.owner.buffManager.removeBuff(BuffRegistry.KATANA_DASH_STACKS, true);
                this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.KATANA_DASH_COOLDOWN, this.owner, 10.0f, null), true);
            }
            this.addedStack = true;
        }
    }

    @Override
    public Shape getHitBox() {
        Point2D.Float dir = this.owner.getDir() == 3 ? GameMath.getPerpendicularDir(-this.dirX, -this.dirY) : GameMath.getPerpendicularDir(this.dirX, this.dirY);
        float width = 40.0f;
        float frontOffset = 20.0f;
        float range = 120.0f;
        float rangeOffset = -40.0f;
        return new LineHitbox(this.owner.x + dir.x * rangeOffset + this.dirX * frontOffset, this.owner.y + dir.y * rangeOffset + this.dirY * frontOffset, dir.x, dir.y, range, width);
    }
}

