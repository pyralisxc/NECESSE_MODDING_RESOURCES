/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.NecroticPoisonBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class NecroticGreatswordWaveProjectile
extends Projectile {
    public NecroticGreatswordWaveProjectile() {
    }

    public NecroticGreatswordWaveProjectile(Level level, Mob owner, int x, int y, int targetX, int targetY, GameDamage damage, float speed, int range) {
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setLevel(level);
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.speed = speed;
        this.setDistance(range);
    }

    @Override
    public void init() {
        super.init();
        this.piercing = 4;
        this.setWidth(180.0f, true, 30.0f);
        this.particleRandomPerpOffset = 60.0f;
        this.isSolid = true;
        this.givesLight = true;
        this.particleRandomOffset = 8.0f;
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 4;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        if (mob != null) {
            mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.NECROTIC_POISON, mob, 2.0f, (Attacker)this), true);
        }
    }

    @Override
    public Color getParticleColor() {
        return NecroticPoisonBuff.getNecroticParticleColor();
    }

    @Override
    public void refreshParticleLight() {
        Color color = new Color(155, 55, 155);
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, color, this.lightSaturation);
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
        float alpha = this.getFadeAlphaDistance(100, 100);
        final TextureDrawOptionsEnd options = this.texture.initDraw().rotate(this.getAngle() - 135.0f, this.texture.getWidth() / 2, this.texture.getHeight() / 2).light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).alpha(alpha).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

