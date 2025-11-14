/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.GhostSkullExplosionEvent;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class GhostSkullProjectile
extends Projectile {
    private long spawnTime;

    public GhostSkullProjectile() {
    }

    public GhostSkullProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(15.0f);
        this.height = 18.0f;
        this.heightBasedOnDistance = true;
        this.spawnTime = this.getWorldEntity().getTime();
        this.doesImpactDamage = false;
        this.trailOffset = 0.0f;
        if (this.isClient()) {
            this.addSmokeParticles();
            SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(this).volume(0.35f).pitch(1.5f));
        }
    }

    private void addSmokeParticles() {
        GameRandom random = GameRandom.globalRandom;
        for (int i = 0; i < 15; ++i) {
            int colorMod = random.getIntBetween(0, 30);
            this.getLevel().entityManager.addParticle(this.x, this.y - this.getHeight(), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).color(new Color(230 - colorMod, 230 - colorMod, 230 - colorMod)).sizeFades(22, 44).movesFriction(this.dx * (float)random.getIntBetween(5, 50), this.dy * (float)random.getIntBetween(5, 50), 0.8f).givesLight(247.0f, 0.3f).alpha(0.75f).heightMoves(0.0f, random.getIntBetween(5, 15)).lifeTime(1000);
        }
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(176, 234, 190), 14.0f, 250, 18.0f);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 12;
        int drawY = camera.getDrawY(this.y) - 12;
        int timePerFrame = 50;
        int spriteIndex = (int)(this.getWorldEntity().getTime() / (long)timePerFrame) % 10;
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(spriteIndex, 1, 24).light(light).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 12);
    }

    @Override
    public float getAngle() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        GhostSkullExplosionEvent event = new GhostSkullExplosionEvent(x, y, this.getDamage(), this.getOwner());
        this.getLevel().entityManager.addLevelEvent(event);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("explosion", 3);
    }
}

