/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class VoidWizardMissileProjectile
extends Projectile {
    private int tickCounter;

    public VoidWizardMissileProjectile() {
    }

    public VoidWizardMissileProjectile(Level level, Mob owner, Mob target, GameDamage damage) {
        this.setLevel(level);
        this.x = owner.x;
        this.y = owner.y;
        this.setTarget(target.x, target.y);
        this.setDamage(damage);
        this.knockback = 0;
        this.setDistance(10000);
        this.setOwner(owner);
        this.moveDist(60.0);
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.piercing = 100;
        this.tickCounter = 0;
        this.speed = 0.0f;
    }

    @Override
    protected CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock);
    }

    @Override
    public Color getParticleColor() {
        return VoidWizard.getWizardProjectileColor(this.getOwner());
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), null, 12.0f, 500, this.getHeight()){

            @Override
            public Color getColor() {
                return VoidWizard.getWizardProjectileColor(VoidWizardMissileProjectile.this.getOwner());
            }
        };
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        if (mob != null) {
            ActiveBuff ab = new ActiveBuff("brokenarmor", mob, 20.0f, (Attacker)this.getOwner());
            mob.addBuff(ab, true);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        ++this.tickCounter;
        if (this.tickCounter == 10) {
            this.speed = 400.0f;
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        ++this.tickCounter;
        if (this.tickCounter == 10) {
            this.speed = 400.0f;
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
        final TextureDrawOptionsEnd options = this.texture.initDraw().rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("voidwiz", 4);
    }
}

