/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.registries.MobRegistry
 *  necesse.entity.Entity
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.mob;

import aphorea.mobs.bosses.minions.MiniUnstableGelSlime;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class MiniUnstableGelSlimeProjectile
extends Projectile {
    public MiniUnstableGelSlimeProjectile() {
    }

    public MiniUnstableGelSlimeProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    public void init() {
        super.init();
        this.givesLight = false;
        this.height = 18.0f;
        this.trailOffset = -14.0f;
        this.setWidth(28.0f, true);
        this.piercing = 0;
        this.bouncing = 0;
    }

    public Color getParticleColor() {
        return AphColors.unstableGel;
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), AphColors.unstableGel, 26.0f, 500, this.getHeight());
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel((Entity)this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 2).pos(drawX, drawY - (int)this.getHeight());
        list.add((LevelSortedDrawable)new EntityDrawable((Entity)this, (TextureDrawOptions)options){
            final /* synthetic */ TextureDrawOptions val$options;
            {
                this.val$options = textureDrawOptions;
                super(arg0);
            }

            public void draw(TickManager tickManager) {
                this.val$options.draw();
            }
        });
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        MiniUnstableGelSlime spawnMob = (MiniUnstableGelSlime)MobRegistry.getMob((String)"miniunstablegelslime", (Level)this.getLevel());
        spawnMob.addBuff(new ActiveBuff(AphBuffs.STUN, (Mob)spawnMob, 500, (Attacker)spawnMob), true);
        this.getLevel().entityManager.addMob((Mob)spawnMob, x, y);
        if (mob != null) {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.BROKEN_ARMOR, mob, 5000, (Attacker)this.getOwner());
            mob.addBuff(ab, true);
            spawnMob.addBuff(new ActiveBuff(AphBuffs.STICKY, (Mob)spawnMob, 2.0f, (Attacker)spawnMob), true);
        }
    }
}

