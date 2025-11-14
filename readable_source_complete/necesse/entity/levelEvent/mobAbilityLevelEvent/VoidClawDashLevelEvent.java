/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.KatanaToolItem;
import necesse.inventory.item.toolItem.swordToolItem.VoidClawSwordToolItem;

public class VoidClawDashLevelEvent
extends MobDashLevelEvent {
    protected float timeAlive = 0.0f;
    public InventoryItem item;

    public VoidClawDashLevelEvent() {
    }

    public VoidClawDashLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, KatanaToolItem katanaItem, InventoryItem item) {
        super(owner, seed, dirX, dirY, distance, animTime, katanaItem.getAttackDamage(item).modDamage(2.0f));
        this.item = item;
    }

    @Override
    public void init() {
        super.init();
        if (this.owner != null) {
            this.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, this.owner, this.animTime, null), false);
            if (!this.owner.isPlayer) {
                this.spawnStepParticles();
            }
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.spawnStepParticles();
        this.timeAlive += delta;
    }

    @Override
    public void dealServerDamage(Mob target, boolean isClientSubmitted) {
        super.dealServerDamage(target, isClientSubmitted);
        VoidClawSwordToolItem.restoreHealthOnHit(this.item, this.getLevel(), target, this.owner);
    }

    protected void spawnStepParticles() {
        if (this.level != null && this.level.isClient() && this.owner != null) {
            float forceMod = Math.min((float)this.animTime / 700.0f, 1.0f);
            float forceX = this.dirX * this.distance * forceMod;
            float forceY = this.dirY * this.distance * forceMod;
            float colorFactor = GameMath.limit(this.timeAlive / (float)this.animTime, 0.0f, 1.0f);
            int colorValue = GameMath.limit(120 + (int)(135.0f * colorFactor), 0, 255);
            Color particleColor = new Color(colorValue, 255, colorValue);
            for (int i = 0; i < 4; ++i) {
                this.level.entityManager.addParticle(this.owner.x + (float)GameRandom.globalRandom.nextGaussian() * 15.0f + forceX / 5.0f, this.owner.y + (float)GameRandom.globalRandom.nextGaussian() * 20.0f + forceY / 5.0f, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).movesConstant(forceX * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 5.0f, forceY * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f) / 5.0f).color(particleColor).minDrawLight((int)(255.0f * colorFactor)).height(18.0f).lifeTime(700);
            }
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

