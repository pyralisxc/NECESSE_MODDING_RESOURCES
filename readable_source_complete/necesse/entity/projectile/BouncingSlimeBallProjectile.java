/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
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
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BouncingSlimeBallProjectile
extends Projectile {
    protected float deltaHeight;
    protected float bouncingHeight;

    public BouncingSlimeBallProjectile() {
    }

    public BouncingSlimeBallProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setDistance(distance);
        this.setOwner(owner);
        this.bouncingHeight = 10.0f;
        this.deltaHeight = 50.0f;
    }

    @Override
    public void setupPositionPacket(PacketWriter writer) {
        super.setupPositionPacket(writer);
        writer.putNextFloat(this.deltaHeight);
        writer.putNextFloat(this.bouncingHeight);
    }

    @Override
    public void applyPositionPacket(PacketReader reader) {
        super.applyPositionPacket(reader);
        this.deltaHeight = reader.getNextFloat();
        this.bouncingHeight = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getWorldEntity().getTime();
        this.isSolid = true;
        this.width = 8.0f;
        this.trailOffset = 0.0f;
        this.piercing = 0;
        this.bouncing = 10;
    }

    @Override
    public float tickMovement(float delta) {
        float out = super.tickMovement(delta);
        float heightChange = 50.0f * delta / 250.0f;
        this.deltaHeight -= heightChange;
        this.bouncingHeight += this.deltaHeight * delta / 250.0f;
        if (this.bouncingHeight < 0.0f) {
            if (this.isClient()) {
                SoundManager.playSound(GameResources.slimeSplash2, (SoundEffect)SoundEffect.effect(this).volume(0.4f).pitch(0.9f));
                ExplosionEvent.spawnExplosionParticles(this.getLevel(), this.x, this.y, 10, 0.0f, 12.0f, (level, x, y, dirX, dirY, lifeTime, range) -> level.entityManager.addParticle(x, y, Particle.GType.CRITICAL).movesConstant(dirX, dirY).color(new Color(154, 32, 176)).lifeTime(lifeTime));
            }
            this.deltaHeight = -this.deltaHeight * 0.95f;
            this.bouncingHeight = -this.bouncingHeight;
            if (Math.abs(this.deltaHeight) < heightChange * 2.0f) {
                this.bouncingHeight = -1.0f;
                this.deltaHeight = 0.0f;
            }
        }
        this.height = Math.max(this.bouncingHeight, 0.0f);
        return out;
    }

    @Override
    public Color getParticleColor() {
        return new Color(154, 32, 176);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(124, 19, 136), 16.0f, 500, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    @Override
    public float getTrailThickness() {
        return 30.0f;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        float angle = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 1.5f;
        if (this.dx < 0.0f) {
            angle = -angle;
        }
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(angle, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        float shadowAlpha = Math.abs(GameMath.limit(this.height / 100.0f, 0.0f, 1.0f) - 1.0f);
        float sizeMod = Math.abs(GameMath.limit(this.height / 100.0f, 0.0f, 1.0f) - 1.0f);
        int shadowWidth = (int)((float)this.shadowTexture.getWidth() * sizeMod);
        int shadowHeight = (int)((float)this.shadowTexture.getHeight() * sizeMod);
        int shadowX = camera.getDrawX(this.x) - shadowWidth / 2;
        int shadowY = camera.getDrawY(this.y) - shadowHeight / 2;
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().size(shadowWidth, shadowHeight).light(light).alpha(shadowAlpha).pos(shadowX, shadowY);
        tileList.add(tm -> shadowOptions.draw());
    }
}

