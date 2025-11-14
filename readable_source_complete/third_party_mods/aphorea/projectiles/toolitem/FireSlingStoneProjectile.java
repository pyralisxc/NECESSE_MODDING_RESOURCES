/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.BuffRegistry$Debuffs
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
package aphorea.projectiles.toolitem;

import aphorea.projectiles.toolitem.SlingStoneProjectile;
import aphorea.utils.AphColors;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
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

public class FireSlingStoneProjectile
extends SlingStoneProjectile {
    int count = 0;

    public FireSlingStoneProjectile() {
    }

    @Override
    public void init() {
        super.init();
        this.givesLight = true;
        this.height = 28.0f;
        this.setWidth(20.0f, true);
    }

    public FireSlingStoneProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
    }

    @Override
    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), AphColors.fire, 26.0f, 100, this.getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel((Entity)this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 4;
        int drawY = camera.getDrawY(this.y);
        ++this.count;
        if (this.count >= 20) {
            this.count = 0;
        }
        TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.count >= 10 ? 0 : 1, 0, 32, 64).light(light).rotate(this.getAngle(), this.texture.getWidth() / 4, 2).pos(drawX, drawY - (int)this.getHeight());
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

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer() && mob != null) {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.ON_FIRE, mob, 10000, (Attacker)this), true);
        }
    }
}

