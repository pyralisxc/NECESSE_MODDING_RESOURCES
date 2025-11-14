/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.entity.Entity
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.inventory.InventoryItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.mob;

import aphorea.projectiles.mob.RockyGelSlimeProjectile;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RockyGelSlimeLootProjectile
extends RockyGelSlimeProjectile {
    public RockyGelSlimeLootProjectile(Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        super(owner, x, y, targetX, targetY, speed, distance, damage, knockback);
        this.dropItem = true;
    }

    public RockyGelSlimeLootProjectile() {
    }

    public void dropItem() {
        this.getLevel().entityManager.pickups.add((Entity)new InventoryItem("rockygel").getPickupEntity(this.getLevel(), this.x, this.y));
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel((Entity)this);
            int textureRes = 32;
            int halfTextureRes = textureRes / 2;
            int drawX = camera.getDrawX(this.x) - halfTextureRes;
            int drawY = camera.getDrawY(this.y) - halfTextureRes;
            TextureDrawOptionsEnd options = this.texture.initDraw().sprite(0, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY - (int)this.getHeight());
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
            TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(0, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY);
            tileList.add(arg_0 -> RockyGelSlimeLootProjectile.lambda$addDrawables$0((TextureDrawOptions)shadowOptions, arg_0));
        }
    }

    private static /* synthetic */ void lambda$addDrawables$0(TextureDrawOptions shadowOptions, TickManager tm) {
        shadowOptions.draw();
    }
}

