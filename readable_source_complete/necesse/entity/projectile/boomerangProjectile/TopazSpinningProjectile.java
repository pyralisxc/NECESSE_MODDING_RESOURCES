/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.boomerangProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.boomerangProjectile.SpinningProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class TopazSpinningProjectile
extends SpinningProjectile {
    protected static final float percentChanceOfLifeEssenceOnHit = 0.75f;

    @Override
    public void init() {
        super.init();
        this.setWidth(5.0f, true);
        this.height = 18.0f;
        this.bouncing = 100;
        this.distance = 2000;
        this.speed = 140.0f;
    }

    @Override
    public Color getParticleColor() {
        return ThemeColorRegistry.TOPAZ.getRandomColor();
    }

    @Override
    protected Color getWallHitColor() {
        return ThemeColorRegistry.TOPAZ.getRandomColor();
    }

    @Override
    protected int getExtraSpinningParticles() {
        return 0;
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        Mob owner;
        super.doHitLogic(mob, object, x, y);
        GameRandom random = new GameRandom();
        if (random.nextFloat() <= 0.75f && (owner = this.getOwner()) != null && mob != null) {
            Float gainMod = owner.buffManager.getModifier(BuffModifiers.LIFE_ESSENCE_GAIN);
            Float durationMod = owner.buffManager.getModifier(BuffModifiers.LIFE_ESSENCE_DURATION);
            int i = 0;
            while (true) {
                double d = i;
                double d2 = Math.floor(gainMod.floatValue());
                boolean bl = GameRandom.globalRandom.getChance(gainMod.floatValue() % 1.0f);
                if (!(d < d2 + (double)bl)) break;
                owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.LIFE_ESSENCE, owner, 60.0f * durationMod.floatValue(), null), false);
                ++i;
            }
        }
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
    public float getAngle() {
        return super.getAngle() * 1.5f;
    }
}

