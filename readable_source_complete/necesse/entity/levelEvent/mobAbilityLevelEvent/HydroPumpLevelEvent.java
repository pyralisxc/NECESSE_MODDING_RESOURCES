/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.HydroPumpParticle;
import necesse.entity.particle.Particle;
import necesse.level.gameTile.LiquidTile;
import necesse.level.maps.LevelObjectHit;

public class HydroPumpLevelEvent
extends GroundEffectEvent {
    public GameDamage damage;
    protected int tickCounter;
    protected MobHitCooldowns hitCooldowns;

    public HydroPumpLevelEvent() {
    }

    public HydroPumpLevelEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        this.damage.writePacket(writer);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.damage = GameDamage.fromReader(reader);
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        this.hitCooldowns = new MobHitCooldowns();
        if (this.isClient()) {
            this.level.entityManager.addParticle(new HydroPumpParticle(this.level, this.x, this.y, this.getWaterColor(), 500, 4000, 500), Particle.GType.CRITICAL);
        }
    }

    @Override
    public Shape getHitBox() {
        if (this.tickCounter < 10 || this.tickCounter > 90) {
            return null;
        }
        int width = 40;
        int height = 30;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.isServerHit(this.damage, 0.0f, 0.0f, 0.0f, this.owner);
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    public Color getWaterColor() {
        return ((LiquidTile)TileRegistry.getTile(TileRegistry.waterID)).getLiquidColor(this.level, 0, 0);
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > 100) {
            this.over();
        } else {
            if (this.tickCounter >= 10 && this.tickCounter <= 90) {
                Color color = this.getWaterColor();
                for (int i = 0; i < 5; ++i) {
                    AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
                    AtomicReference<Float> currentDistance = new AtomicReference<Float>(Float.valueOf(16.0f));
                    this.level.entityManager.addParticle((float)this.x + GameMath.sin(currentAngle.get().floatValue()) * currentDistance.get().floatValue(), (float)this.y + GameMath.cos(currentAngle.get().floatValue()) * currentDistance.get().floatValue() * 0.75f, Particle.GType.CRITICAL).color(color).heightMoves(0.0f, 80.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                        float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                        float distX = currentDistance.accumulateAndGet(Float.valueOf(delta * 6.0f / 250.0f), Float::sum).floatValue();
                        float distY = distX * 0.75f;
                        pos.x = (float)this.x + GameMath.sin(angle) * distX;
                        pos.y = (float)this.y + GameMath.cos(angle) * distY * 0.75f;
                    }).lifeTime(800).sizeFades(12, 20);
                }
            }
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > 100) {
            this.over();
        } else {
            super.serverTick();
        }
    }
}

