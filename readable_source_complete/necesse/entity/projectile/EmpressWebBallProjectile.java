/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.chains.Chain;
import necesse.entity.chains.ChainLocation;
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

public class EmpressWebBallProjectile
extends BoomerangProjectile {
    private Chain chain;

    public EmpressWebBallProjectile() {
    }

    public EmpressWebBallProjectile(float x, float y, float angle, GameDamage damage, float projectileSpeed, Mob owner) {
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(500);
        this.speed = projectileSpeed;
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(10.0f, true);
        this.height = 18.0f;
        this.piercing = Integer.MAX_VALUE;
        this.isSolid = false;
        final Mob owner = this.getOwner();
        if (owner != null) {
            this.chain = new Chain(new ChainLocation(){

                @Override
                public int getX() {
                    return (int)owner.x;
                }

                @Override
                public int getY() {
                    return (int)owner.y - 60;
                }

                @Override
                public boolean removed() {
                    return false;
                }
            }, this){

                @Override
                public int getDrawY() {
                    return super.getDrawY() - 30;
                }
            };
            this.chain.sprite = new GameSprite(GameResources.chains, 5, 0, 32);
            this.chain.height = this.getHeight();
            this.getLevel().entityManager.addChain(this.chain);
        }
    }

    @Override
    protected void returnToOwner() {
        if (!this.returningToOwner) {
            this.speed *= 2.0f;
        }
        super.returnToOwner();
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
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.shadowTexture.getHeight() / 2);
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

