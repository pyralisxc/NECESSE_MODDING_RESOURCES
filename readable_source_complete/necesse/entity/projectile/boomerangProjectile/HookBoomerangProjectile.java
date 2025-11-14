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

public class HookBoomerangProjectile
extends BoomerangProjectile {
    @Override
    public void init() {
        super.init();
        this.setWidth(8.0f);
        this.height = 18.0f;
        this.bouncing = 0;
        this.piercing = 0;
        if (this.getOwner() != null) {
            Chain chain = new Chain(this.getOwner(), this);
            chain.height = this.getHeight();
            this.getLevel().entityManager.addChain(chain);
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
        float angle = this.getAngle() + (this.returningToOwner ? 180.0f : 0.0f);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(angle, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, angle, this.texture.getHeight() / 2);
    }

    @Override
    public float getAngle() {
        return this.angle % 360.0f;
    }

    @Override
    protected SoundSettings getMoveSound() {
        return null;
    }

    @Override
    public void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(x, y));
    }
}

