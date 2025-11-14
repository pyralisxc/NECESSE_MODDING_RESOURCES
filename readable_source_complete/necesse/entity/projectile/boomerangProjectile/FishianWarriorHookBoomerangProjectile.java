/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.boomerangProjectile;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
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
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FishianWarriorHookBoomerangProjectile
extends BoomerangProjectile {
    private Chain chain;

    public FishianWarriorHookBoomerangProjectile() {
    }

    public FishianWarriorHookBoomerangProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
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
        this.height = 14.0f;
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
            this.chain.sprite = new GameSprite(GameResources.chains, 10, 0, 32);
            this.getLevel().entityManager.addChain(this.chain);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.getOwner() == null) {
            this.remove();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.getOwner() == null) {
            this.remove();
        }
    }

    @Override
    public void applyDamage(Mob mob, float x, float y, float knockbackDirX, float knockbackDirY) {
        mob.isServerHit(this.getDamage(), knockbackDirX, knockbackDirY, this.getDistanceBasedKnockback(mob), this);
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
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle() + 45.0f + (this.returningToOwner ? 180.0f : 0.0f), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
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

    private int getDistanceBasedKnockback(Mob mob) {
        Mob attackOwner = this.getAttackOwner();
        return (int)(2.0f * ((float)(this.returningToOwner ? 1 : -1) * GameMath.diamondDistance(attackOwner.x, attackOwner.y, mob.x, mob.y)));
    }

    @Override
    public void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.punch, (SoundEffect)SoundEffect.effect(x, y).volume(1.5f));
    }

    @Override
    protected SoundSettings getMoveSound() {
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

