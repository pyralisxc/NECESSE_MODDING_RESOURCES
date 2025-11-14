/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.GameResources
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
package aphorea.projectiles.arrow;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
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

public class GelArrowProjectile
extends Projectile {
    ToolItem toolItem;
    InventoryItem item;
    Color color = AphColors.gel;
    GameDamage gameDamage;

    public GelArrowProjectile() {
    }

    public GelArrowProjectile(GameDamage damage, int knockback, ToolItem toolItem, InventoryItem item, Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
        this.setDamage(damage);
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
        this.gameDamage = damage.modDamage(0.5f);
    }

    public AphAreaList getAreaList(GameDamage gameDamage) {
        return new AphAreaList(new AphArea(50.0f, this.color).setDamageArea(gameDamage));
    }

    public void init() {
        super.init();
        this.piercing = 0;
        this.bouncing = 0;
        this.canHitMobs = true;
        this.givesLight = false;
        this.heightBasedOnDistance = true;
        this.setWidth(8.0f);
    }

    public void dropItem() {
        if (GameRandom.globalRandom.getChance(0.5f)) {
            this.getLevel().entityManager.pickups.add((Entity)new InventoryItem("stonearrow").getPickupEntity(this.getLevel(), this.x, this.y));
        }
    }

    protected void playHitSound(float x, float y) {
        SoundManager.playSound((GameSound)GameResources.slimeSplash1, (SoundEffect)SoundEffect.effect((float)x, (float)y));
    }

    public Color getParticleColor() {
        return this.color;
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), this.color, 26.0f, 500, this.getHeight());
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

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        this.getAreaList(this.gameDamage).execute(this.getOwner(), x, y, 1.0f, this.item, this.toolItem, false);
        if (this.isServer() && mob != null) {
            mob.addBuff(new ActiveBuff(AphBuffs.STICKY, mob, 1000, (Attacker)this), true);
        }
    }
}

