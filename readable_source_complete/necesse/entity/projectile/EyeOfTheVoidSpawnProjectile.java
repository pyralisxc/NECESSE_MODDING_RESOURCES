/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.WanderbotFollowingMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EyeOfTheVoidSpawnProjectile
extends Projectile {
    private SummonToolItem summonToolItem;
    private InventoryItem inventoryItem;
    protected long spawnTime;
    protected long deathTime;
    protected boolean hasSpawnedBot = false;

    public EyeOfTheVoidSpawnProjectile() {
    }

    public EyeOfTheVoidSpawnProjectile(Level level, Mob owner, float x, float y, int targetX, int targetY, int distance, GameDamage damage, int knockback, SummonToolItem summonToolItem, InventoryItem inventoryItem) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setDistance(distance);
        this.setOwner(owner);
        this.distance = distance;
        this.summonToolItem = summonToolItem;
        this.inventoryItem = inventoryItem;
    }

    public EyeOfTheVoidSpawnProjectile(Level level, Mob owner, int targetX, int targetY, int distance, GameDamage damage, int knockback, SummonToolItem summonToolItem, InventoryItem inventoryItem) {
        this(level, owner, owner.x, owner.y, targetX, targetY, distance, damage, knockback, summonToolItem, inventoryItem);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.targetX);
        writer.putNextFloat(this.targetY);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.targetX = reader.getNextFloat();
        this.targetY = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getTime();
        this.isSolid = false;
        this.canHitMobs = false;
        this.trailOffset = 0.0f;
        if (!this.isClient()) {
            return;
        }
        PostProcessingEffects.addShockwaveEffect(new PostProcessingEffects.AbstractShockwaveEffect(){

            @Override
            public int getDrawX(GameCamera camera) {
                return camera.getDrawX(EyeOfTheVoidSpawnProjectile.this.x);
            }

            @Override
            public int getDrawY(GameCamera camera) {
                return camera.getDrawY(EyeOfTheVoidSpawnProjectile.this.y) - (int)EyeOfTheVoidSpawnProjectile.this.getHeight();
            }

            @Override
            public float getCurrentDistance() {
                return 0.0f;
            }

            @Override
            public float getSize() {
                return EyeOfTheVoidSpawnProjectile.this.getBlackHoleSize();
            }

            @Override
            public float getEasingScale() {
                return 3.4f;
            }

            @Override
            public float getEasingPower() {
                return 0.65f;
            }

            @Override
            public boolean shouldRemove() {
                return EyeOfTheVoidSpawnProjectile.this.removed();
            }
        });
    }

    protected float getBlackHoleSize() {
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        float lastFifteenPerc = GameMath.limit(travelPerc - 0.85f, 0.0f, 1.0f) * 10.0f;
        float lastFivePerc = GameMath.limit(GameMath.lerp((travelPerc - 0.98f) * 50.0f, 1.0f, 0.0f), 0.0f, 1.0f);
        if (travelPerc > 0.95f) {
            return lastFivePerc * 32.0f * 3.0f;
        }
        return lastFifteenPerc * 32.0f * 3.0f;
    }

    @Override
    public Color getParticleColor() {
        return new Color(159, 31, 177);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    protected void spawnDeathParticles() {
        ExplosionEvent.spawnExplosionParticles(this.getLevel(), this.x, this.y, 12, 15.0f, 25.0f, (level, x, y, dirX, dirY, lifeTime, currentRange) -> level.entityManager.addParticle(x, y - 8.0f, Particle.GType.CRITICAL).movesConstant(dirX * 0.1f, dirY * 0.05f).color(new Color(255, 56, 155, 237)).heightMoves(5.0f, 55.0f).sizeFades(25, 45).onProgress(0.1f, p -> {
            Point2D.Float norm = GameMath.normalize(dirX, dirY);
            level.entityManager.addParticle(p.x + norm.x * 20.0f, p.y + norm.y * 10.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(dirX * 0.2f, dirY * 0.1f).color(new Color(255, 98, 200, 255)).heightMoves(10.0f, 60.0f).lifeTime(lifeTime);
        }).lifeTime(lifeTime * 2));
    }

    @Override
    public float tickMovement(float delta) {
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        float travelPercInv = Math.abs(travelPerc - 1.0f);
        this.speed = GameMath.limit(150.0f * travelPercInv, 5.0f, 150.0f);
        this.height = 20.0f * travelPercInv;
        if (travelPerc >= 0.94f && !this.hasSpawnedBot && !this.isClient()) {
            this.spawnWanderbot();
            this.hasSpawnedBot = true;
        }
        return super.tickMovement(delta);
    }

    protected void spawnWanderbot() {
        if (this.isClient()) {
            return;
        }
        Mob owner = this.getOwner();
        if (owner != null && !owner.removed() && owner.isItemAttacker) {
            ItemAttackerMob itemAttackerMob = (ItemAttackerMob)owner;
            WanderbotFollowingMob wanderbotMob = new WanderbotFollowingMob();
            itemAttackerMob.serverFollowersManager.addFollower(this.summonToolItem.summonType, (Mob)wanderbotMob, FollowPosition.CIRCLE_FAR, "summonedmob", 1.0f, this.summonToolItem.getMaxSummons(this.inventoryItem, (ItemAttackerMob)owner), null, false);
            wanderbotMob.updateDamage(this.summonToolItem.getAttackDamage(this.inventoryItem));
            wanderbotMob.setEnchantment(this.summonToolItem.getEnchantment(this.inventoryItem));
            if (!owner.isPlayer) {
                wanderbotMob.setRemoveWhenNotInInventory(ItemRegistry.getItem("eyeofthevoid"), CheckSlotType.WEAPON);
            }
            Point2D.Float spawnPoint = new Point2D.Float(this.targetX, this.targetY + 32.0f);
            owner.getLevel().entityManager.addMob(wanderbotMob, spawnPoint.x, spawnPoint.y);
        }
    }

    @Override
    public float getAngle() {
        return (float)(this.getWorldEntity().getTime() - this.spawnTime) * 0.1f;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.x) - 24;
        int drawY = camera.getDrawY(this.y) - 24;
        int spriteX = GameUtils.getAnim(level.getLocalTime(), 6, 600);
        TextureDrawOptionsEnd options = this.texture.initDraw().sprite(spriteX, 0, 48).light(new GameLight(GameMath.max(light.getLevel(), 100.0f))).rotate(this.getAngle(), 24, 24).pos(drawX, drawY - (int)this.getHeight());
        int shadowX = camera.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
        int shadowY = camera.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().light(light).rotate(this.angle).alpha(0.2f).pos(shadowX, shadowY);
        topList.add(tm -> {
            options.draw();
            shadowOptions.draw();
        });
    }
}

