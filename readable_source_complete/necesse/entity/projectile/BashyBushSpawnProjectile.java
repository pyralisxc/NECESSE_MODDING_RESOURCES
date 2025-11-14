/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BashyBushFollowingMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class BashyBushSpawnProjectile
extends Projectile {
    protected long spawnTime;
    private SummonToolItem summonToolItem;
    private InventoryItem inventoryItem;

    public BashyBushSpawnProjectile() {
    }

    public BashyBushSpawnProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, SummonToolItem summonToolItem, InventoryItem inventoryItem) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setDistance(distance);
        this.setOwner(owner);
        this.summonToolItem = summonToolItem;
        this.inventoryItem = inventoryItem;
        this.distance = distance;
    }

    public BashyBushSpawnProjectile(Level level, Mob owner, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, SummonToolItem summonToolItem, InventoryItem inventoryItem) {
        this(level, owner, owner.x, owner.y, targetX, targetY, speed, distance, damage, knockback, summonToolItem, inventoryItem);
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getWorldEntity().getTime();
        this.isSolid = false;
        this.canHitMobs = false;
        this.trailOffset = 0.0f;
    }

    @Override
    public float tickMovement(float delta) {
        float out = super.tickMovement(delta);
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        float travelPercInv = Math.abs(travelPerc - 1.0f);
        float heightF = GameMath.sin(travelPerc * 180.0f);
        int heightBasedOnDistance = this.distance / 5;
        this.height = heightF * (float)heightBasedOnDistance + 50.0f * travelPercInv;
        return out;
    }

    @Override
    public Color getParticleColor() {
        return new Color(216, 213, 221);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        Mob owner = this.getOwner();
        if (owner != null && !owner.removed() && owner.isItemAttacker) {
            BashyBushFollowingMob bashyBushMob = new BashyBushFollowingMob();
            ((ItemAttackerMob)owner).serverFollowersManager.addFollower(this.summonToolItem.summonType, (Mob)bashyBushMob, FollowPosition.WALK_CLOSE, "summonedmob", 1.0f, this.summonToolItem.getMaxSummons(this.inventoryItem, (ItemAttackerMob)owner), null, false);
            Point2D.Float spawnPoint = new Point2D.Float(x, y);
            bashyBushMob.updateDamage(this.summonToolItem.getAttackDamage(this.inventoryItem));
            bashyBushMob.setEnchantment(this.summonToolItem.getEnchantment(this.inventoryItem));
            if (!owner.isPlayer) {
                bashyBushMob.setRemoveWhenNotInInventory(ItemRegistry.getItem("bashybush"), CheckSlotType.WEAPON);
            }
            owner.getLevel().entityManager.addMob(bashyBushMob, spawnPoint.x, spawnPoint.y);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.x) - 64;
        int drawY = camera.getDrawY(this.y) - 64 - 32;
        float angle = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 2.0f;
        if (this.dx < 0.0f) {
            angle = -angle;
        }
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.bashyBush).sprite(0, 2, 128).size(128, 128).light(light).rotate(angle, 64, 80);
        float shadowAlpha = Math.abs(GameMath.limit(this.height / 300.0f, 0.0f, 1.0f) - 1.0f);
        int shadowX = camera.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
        int shadowY = camera.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().light(light).rotate(angle).alpha(shadowAlpha).pos(shadowX, shadowY);
        DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY - (int)this.getHeight());
        topList.add(tm -> {
            shadowOptions.draw();
            drawOptions.draw();
        });
    }
}

