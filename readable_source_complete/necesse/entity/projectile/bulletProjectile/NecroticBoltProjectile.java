/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.bulletProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.NecroticPoisonBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.WitchRobesSetBonusBuff;
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

public class NecroticBoltProjectile
extends Projectile {
    public NecroticBoltProjectile() {
    }

    public NecroticBoltProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int range, GameDamage damage) {
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.knockback = 0;
        this.setLevel(level);
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.speed = speed;
        this.setDistance(range);
    }

    @Override
    public void init() {
        super.init();
        this.givesLight = false;
        this.height = 18.0f;
        this.piercing = 0;
        this.bouncing = 0;
        this.particleDirOffset = -28.0f;
    }

    @Override
    public Color getParticleColor() {
        return NecroticPoisonBuff.getNecroticParticleColor();
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 6.0f, 500, 18.0f);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob == null) {
            return;
        }
        if (this.isServer()) {
            this.spawnIfServer(mob);
        }
    }

    protected void spawnIfServer(Mob mob) {
        WitchRobesSetBonusBuff.spawnCrawlingZombie(this.getOwner(), mob, this.getDamage().modDamage(0.3f));
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
    }

    @Override
    protected SoundSettings getHitSound() {
        return new SoundSettings(GameResources.blunthit).volume(0.6f);
    }
}

