/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
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
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.toolitem;

import aphorea.items.tools.weapons.throwable.UnstableGelveline;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
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
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class UnstableGelvelineProjectile
extends Projectile {
    UnstableGelveline toolItem;
    InventoryItem item;
    Color color = AphColors.unstableGel;
    AphAreaList areaList = new AphAreaList(new AphArea(100.0f, this.color));

    public UnstableGelvelineProjectile() {
    }

    public UnstableGelvelineProjectile(GameDamage attackDamage, int knockback, UnstableGelveline toolItem, InventoryItem item, Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
        this.knockback = knockback;
        this.setDamage(attackDamage);
        this.knockback = knockback;
        this.toolItem = toolItem;
        this.item = item;
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.areaList = new AphAreaList(new AphArea(100.0f, this.color).setDamageArea(attackDamage.modDamage(0.5f)));
    }

    public void init() {
        super.init();
        this.piercing = 0;
        this.bouncing = 0;
        this.canHitMobs = true;
        this.givesLight = false;
        this.setWidth(0.0f, 20.0f);
    }

    public Color getParticleColor() {
        return this.color;
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), this.color, 26.0f, 500, this.getHeight());
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            mob.addBuff(new ActiveBuff(AphBuffs.STICKY, mob, 2000, (Attacker)this), true);
        }
    }

    public void remove() {
        this.areaList.execute(this.getOwner(), this.x, this.y, 1.0f, this.item, (ToolItem)this.toolItem, false);
        super.remove();
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
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getWidth() / 2, 2);
    }
}

