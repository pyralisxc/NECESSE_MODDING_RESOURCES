/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.ParticleOption;
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
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class FrostArrowProjectile
extends Projectile
implements RicochetableProjectile {
    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.heightBasedOnDistance = true;
        this.setWidth(8.0f);
    }

    @Override
    public Color getParticleColor() {
        return new Color(87, 189, 216);
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        particle.givesLight(75.0f, 0.5f).lifeTime(1000);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(41, 146, 177), 12.0f, 250, this.getHeight());
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        if (mob != null) {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.FROSTSLOW, mob, 10.0f, (Attacker)this.getOwner());
            mob.addBuff(ab, true);
        }
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
    public void dropItem() {
        if (GameRandom.globalRandom.getChance(0.5f)) {
            this.getLevel().entityManager.pickups.add(new InventoryItem("frostarrow").getPickupEntity(this.getLevel(), this.x, this.y));
        }
    }

    @Override
    protected void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.bowhit, (SoundEffect)SoundEffect.effect(x, y));
    }
}

