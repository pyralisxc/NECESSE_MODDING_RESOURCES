/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.boomerangProjectile;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.chains.Chain;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BoxingGloveBoomerangProjectile
extends BoomerangProjectile {
    private Chain chain;

    public BoxingGloveBoomerangProjectile() {
    }

    public BoxingGloveBoomerangProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(8.0f);
        this.height = 18.0f;
        this.bouncing = 0;
        this.piercing = 0;
        Mob owner = this.getOwner();
        if (owner != null) {
            this.chain = new Chain(owner, this){

                @Override
                public int getDrawY() {
                    return super.getDrawY() - 30;
                }
            };
            this.chain.height = this.getHeight();
            this.getLevel().entityManager.addChain(this.chain);
        }
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle() + (this.returningToOwner ? 180.0f : 0.0f), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public float getAngle() {
        return this.angle % 360.0f;
    }

    @Override
    public void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.punch, (SoundEffect)SoundEffect.effect(x, y));
    }

    @Override
    protected SoundSettings getMoveSound() {
        return null;
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return null;
    }

    @Override
    public void remove() {
        if (this.chain != null) {
            this.chain.remove();
        }
        super.remove();
    }
}

