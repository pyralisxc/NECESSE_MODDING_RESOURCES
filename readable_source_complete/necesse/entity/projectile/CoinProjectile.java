/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.RicochetableProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CoinProjectile
extends Projectile
implements RicochetableProjectile {
    private int animTime = 400;

    public CoinProjectile() {
    }

    public CoinProjectile(float x, float y, float targetX, float targetY, int speed, int distance, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
        this.knockback = 5;
        this.dropItem = true;
    }

    @Override
    public void init() {
        super.init();
        this.animTime = GameRandom.globalRandom.getIntBetween(400, 800);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(228, 160, 67), 4.0f, 250, this.getHeight());
    }

    @Override
    public Color getParticleColor() {
        return new Color(250, 197, 62);
    }

    @Override
    public float getParticleChance() {
        return super.getParticleChance() * 0.05f;
    }

    @Override
    protected void spawnDeathParticles() {
        Color particleColor = this.getParticleColor();
        if (particleColor != null) {
            float height = this.getHeight();
            for (int i = 0; i < 3; ++i) {
                this.getLevel().entityManager.addParticle(this.x, this.y, this.spinningTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.getIntBetween(2, 5) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1), GameRandom.globalRandom.getIntBetween(2, 5) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1)).color(this.getParticleColor()).height(height);
            }
        }
    }

    @Override
    protected int getExtraSpinningParticles() {
        return 0;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 16;
        int drawY = camera.getDrawY(this.y);
        int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, this.animTime);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(anim, 0, 32, 64).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(anim, 0, 32, 64).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }

    @Override
    public float tickMovement(float delta) {
        float out = super.tickMovement(delta);
        float percDistance = GameMath.limit((float)this.distance / 100.0f, 0.0f, 2.0f) / 2.0f;
        float maxTileHeight = 0.6f;
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        float bounceHeight = GameMath.sin(travelPerc * 180.0f);
        float groundHeight = GameMath.lerp(travelPerc, 18.0f, 0.0f);
        this.height = groundHeight + bounceHeight * 32.0f * maxTileHeight * percDistance;
        return out;
    }

    @Override
    public void dropItem() {
    }

    @Override
    public void playDisappearSound(float x, float y) {
        super.playDisappearSound(x, y);
        SoundManager.playSound(GameResources.jinglehit, (SoundEffect)SoundEffect.effect(x, y));
    }

    @Override
    public void remove() {
        super.remove();
        if (this.isClient()) {
            return;
        }
        this.getLevel().entityManager.pickups.add(new InventoryItem("coin").getPickupEntity(this.getLevel(), this.x, this.y));
    }

    @Override
    protected void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.jinglehit, (SoundEffect)SoundEffect.effect(x, y));
    }
}

