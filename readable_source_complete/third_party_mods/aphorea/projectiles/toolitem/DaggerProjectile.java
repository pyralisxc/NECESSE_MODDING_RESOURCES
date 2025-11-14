/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.pickup.ItemPickupEntity
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.InventoryItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.toolitem;

import aphorea.projectiles.toolitem.OpenLostUmbrellaProjectile;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public abstract class DaggerProjectile
extends Projectile {
    boolean shouldDrop;
    String stringItemID;
    GNDItemMap gndData;

    abstract Color getColor();

    abstract GameTexture getTexture();

    public DaggerProjectile() {
    }

    public DaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
        this.shouldDrop = shouldDrop;
        this.stringItemID = stringItemID;
        this.gndData = gndData;
    }

    public void init() {
        super.init();
        this.height = 14.0f;
        this.heightBasedOnDistance = false;
        this.setWidth(8.0f);
        this.canBounce = false;
    }

    public Color getParticleColor() {
        return null;
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), this.getColor(), 12.0f, 100, this.getHeight());
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameTexture texture = this.getTexture();
        GameLight light = level.getLightLevel((Entity)this);
        int drawX = camera.getDrawX(this.x) - texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - texture.getHeight() / 2;
        TextureDrawOptionsEnd options = texture.initDraw().light(light).rotate(this.getAngle() + 45.0f, texture.getWidth() / 2, texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
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

    public void remove() {
        if (this.isServer() && this.shouldDrop && this.stringItemID != null) {
            this.shouldDrop = false;
            InventoryItem inventoryItem = new InventoryItem(ItemRegistry.getItem((String)this.stringItemID));
            inventoryItem.setGndData(this.gndData == null ? new GNDItemMap() : this.gndData);
            this.getLevel().entityManager.pickups.add((Entity)new ItemPickupEntity(this.getLevel(), inventoryItem, this.x, this.y, 0.0f, 0.0f));
        }
        super.remove();
    }

    public static class LostUmbrellaDaggerProjectile
    extends DaggerProjectile {
        public static GameTexture texture;

        @Override
        GameTexture getTexture() {
            return texture;
        }

        @Override
        Color getColor() {
            return AphColors.pink_witch;
        }

        public LostUmbrellaDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public LostUmbrellaDaggerProjectile() {
        }

        public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
            if (mob != null) {
                if (this.isServer()) {
                    Level level = this.getLevel();
                    float angle = (float)Math.toRadians(this.getAngle() - 90.0f);
                    float newTargetX = (float)((double)x + 100.0 * Math.cos(angle));
                    float newTargetY = (float)((double)y + 100.0 * Math.sin(angle));
                    OpenLostUmbrellaProjectile projectile = new OpenLostUmbrellaProjectile(level, this.getOwner(), x, y, newTargetX, newTargetY, this.speed / 2.0f, this.distance / 4, this.getDamage(), this.knockback);
                    projectile.resetUniqueID(GameRandom.globalRandom);
                    if (mob instanceof ItemAttackerMob) {
                        ((ItemAttackerMob)mob).addAndSendAttackerProjectile((Projectile)projectile, 0);
                    }
                    if (this.shouldDrop && this.stringItemID != null && this.gndData != null) {
                        if (this.amountHit() < this.piercing) {
                            return;
                        }
                        int bouncing = this.bouncing;
                        Mob owner = this.getOwner();
                        if (owner != null) {
                            bouncing += ((Integer)owner.buffManager.getModifier(BuffModifiers.PROJECTILE_BOUNCES)).intValue();
                        }
                        if (object != null && this.bounced < bouncing && this.canBounce) {
                            return;
                        }
                        this.shouldDrop = false;
                        InventoryItem inventoryItem = new InventoryItem(ItemRegistry.getItem((String)this.stringItemID));
                        inventoryItem.setGndData(this.gndData);
                        this.getLevel().entityManager.pickups.add((Entity)new ItemPickupEntity(this.getLevel(), inventoryItem, x, y, 0.0f, 0.0f));
                    }
                }
                this.remove();
            }
            super.doHitLogic(mob, object, x, y);
        }
    }

    public static class TungstenDaggerProjectile
    extends DaggerProjectile {
        public static GameTexture texture;

        @Override
        GameTexture getTexture() {
            return texture;
        }

        @Override
        Color getColor() {
            return AphColors.tungsten;
        }

        public TungstenDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public TungstenDaggerProjectile() {
        }
    }

    public static class DemonicDaggerProjectile
    extends DaggerProjectile {
        public static GameTexture texture;

        @Override
        GameTexture getTexture() {
            return texture;
        }

        @Override
        Color getColor() {
            return AphColors.demonic;
        }

        public DemonicDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public DemonicDaggerProjectile() {
        }
    }

    public static class GoldDaggerProjectile
    extends DaggerProjectile {
        public static GameTexture texture;

        @Override
        GameTexture getTexture() {
            return texture;
        }

        @Override
        Color getColor() {
            return AphColors.gold;
        }

        public GoldDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public GoldDaggerProjectile() {
        }
    }

    public static class IronDaggerProjectile
    extends DaggerProjectile {
        public static GameTexture texture;

        @Override
        GameTexture getTexture() {
            return texture;
        }

        @Override
        Color getColor() {
            return AphColors.iron;
        }

        public IronDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public IronDaggerProjectile() {
        }
    }

    public static class CopperDaggerProjectile
    extends DaggerProjectile {
        public static GameTexture texture;

        @Override
        GameTexture getTexture() {
            return texture;
        }

        @Override
        Color getColor() {
            return AphColors.copper;
        }

        public CopperDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean shouldDrop, String stringItemID, GNDItemMap gndData) {
            super(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, shouldDrop, stringItemID, gndData);
        }

        public CopperDaggerProjectile() {
        }
    }
}

