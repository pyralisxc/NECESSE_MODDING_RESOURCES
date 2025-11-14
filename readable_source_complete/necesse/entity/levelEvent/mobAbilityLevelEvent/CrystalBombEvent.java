/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Shape;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.CrystalBombParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CrystalDragonShardProjectile;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class CrystalBombEvent
extends GroundEffectEvent {
    private GameDamage damage;
    protected int tickCounter;
    private CrystalBombParticle particle;
    private long lifetime;

    public CrystalBombEvent() {
    }

    public CrystalBombEvent(Mob owner, int x, int y, GameDamage damage, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        this.lifetime = 3000L;
        if (this.isClient()) {
            this.particle = new CrystalBombParticle(this.level, this.x, this.y, this.lifetime);
            this.level.entityManager.addParticle(this.particle, true, Particle.GType.CRITICAL);
        }
    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public void clientHit(Mob target) {
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
    }

    @Override
    public boolean canHit(Mob mob) {
        return false;
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if ((long)this.tickCounter > 20L * (this.lifetime / 2000L)) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if ((long)this.tickCounter > 20L * (this.lifetime / 2000L)) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    @Override
    public void over() {
        if (this.isServer()) {
            int projectileCount = 8;
            GameRandom random = new GameRandom((long)this.getUniqueID() * 7L);
            float angleOffset = random.getFloatBetween(0.0f, 360.0f);
            for (int i = 0; i < projectileCount; ++i) {
                float angle = 360.0f / (float)projectileCount * (float)i + angleOffset;
                CrystalDragonShardProjectile projectile = new CrystalDragonShardProjectile(this.level, this.x, this.y, angle, 150.0f, this.damage, this.owner);
                projectile.resetUniqueID(random);
                this.getLevel().entityManager.projectiles.add(projectile);
            }
        }
        super.over();
        if (this.particle != null) {
            this.particle.despawnNow();
        }
        if (this.isClient()) {
            SoundManager.playSound(GameResources.shatter1, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(GameRandom.globalRandom.getFloatBetween(1.3f, 1.7f)));
        }
    }
}

