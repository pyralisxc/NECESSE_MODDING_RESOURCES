/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrapArrowProjectile
extends Projectile {
    public TrapArrowProjectile() {
    }

    public TrapArrowProjectile(float x, float y, float targetX, float targetY, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = 200.0f;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(400);
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.heightBasedOnDistance = true;
        this.setWidth(16.0f);
        this.clientHandlesHit = false;
        this.canBreakObjects = true;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(150, 150, 150), 10.0f, 250, 18.0f);
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
    public GameMessage getAttackerName() {
        Mob owner = this.getOwner();
        if (owner != null) {
            return owner.getAttackerName();
        }
        return new LocalMessage("deaths", "arrowtrapname");
    }

    @Override
    public void applyDamage(Mob mob, float x, float y, float knockbackDirX, float knockbackDirY) {
        if (this.getLevel().isTrialRoom) {
            GameDamage trialDamage = new GameDamage(DamageTypeRegistry.TRUE, (float)mob.getMaxHealth() / 4.0f);
            mob.isServerHit(trialDamage, knockbackDirX, knockbackDirY, this.knockback, this);
        } else {
            super.applyDamage(mob, x, y, knockbackDirX, knockbackDirY);
        }
    }

    @Override
    protected void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.bowhit, (SoundEffect)SoundEffect.effect(x, y));
    }

    @Override
    public boolean isTrapAttacker() {
        return true;
    }
}

