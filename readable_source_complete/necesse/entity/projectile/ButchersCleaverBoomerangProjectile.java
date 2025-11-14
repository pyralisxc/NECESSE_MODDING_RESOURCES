/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.FoodBuff;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class ButchersCleaverBoomerangProjectile
extends BoomerangProjectile {
    private final HashSet<Integer> ignoredTargets = new HashSet();
    public int chainsLeft;
    protected GameDamage damage;
    protected float eventResilienceGain;
    protected boolean extendsFoodBuff;

    public ButchersCleaverBoomerangProjectile() {
    }

    public ButchersCleaverBoomerangProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, float eventResilienceGain, int knockback, int chainsLeft, boolean extendsFoodBuff) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.damage = damage;
        this.eventResilienceGain = eventResilienceGain;
        this.knockback = knockback;
        this.chainsLeft = chainsLeft;
        this.extendsFoodBuff = extendsFoodBuff;
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.setWidth(14.0f);
        this.bouncing = 0;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.ignoredTargets.size());
        for (Integer ignoredTarget : this.ignoredTargets) {
            writer.putNextInt(ignoredTarget);
        }
        writer.putNextShortUnsigned(this.chainsLeft);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int ignoredTargetsTotal = reader.getNextShortUnsigned();
        for (int i = 0; i < ignoredTargetsTotal; ++i) {
            this.ignoredTargets.add(reader.getNextInt());
        }
        this.chainsLeft = reader.getNextShortUnsigned();
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 2;
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        particle.lifeTime(1000);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        Mob owner = this.getOwner();
        if (this.extendsFoodBuff && owner != null && mob != null) {
            for (ActiveBuff buff : owner.buffManager.getArrayBuffs()) {
                FoodConsumableItem foodItem;
                if (!(buff.buff instanceof FoodBuff) || (foodItem = FoodBuff.getFoodItem(buff)) == null || foodItem.duration * 1000 <= buff.getDurationLeft()) continue;
                buff.setDurationLeft(buff.getDurationLeft() + 1000);
            }
        }
        if (mob != null && this.chainsLeft > 0) {
            --this.chainsLeft;
            if (this.isServer()) {
                int checkForMobsRange = 256;
                Mob firstMobFound = GameUtils.streamTargetsRange(owner, mob.getX(), mob.getY(), checkForMobsRange).filter(mobFound -> mobFound != mob && (mobFound.isHostile || mobFound instanceof TrainingDummyMob) && !this.ignoredTargets.contains(mobFound.getUniqueID())).filter(mobFound -> mobFound.getDistance(mob.x, mob.y) <= (float)checkForMobsRange).min(Comparator.comparingInt(m -> (int)m.getDistance(mob.x, mob.y))).orElse(null);
                if (firstMobFound != null) {
                    this.remove();
                    Level level = this.getLevel();
                    ButchersCleaverBoomerangProjectile projectile = new ButchersCleaverBoomerangProjectile(level, owner, x, y, firstMobFound.x, firstMobFound.y, this.speed, this.distance, this.damage, this.eventResilienceGain, this.knockback, this.chainsLeft, this.extendsFoodBuff);
                    if (this.modifier != null) {
                        this.modifier.initChildProjectile(projectile, 1.0f, 1);
                    }
                    projectile.setTargetPrediction(firstMobFound);
                    if (owner != null && owner.isPlayer) {
                        ((PlayerMob)owner).boomerangs.add(projectile);
                    }
                    projectile.ignoredTargets.addAll(this.ignoredTargets);
                    projectile.ignoredTargets.add(mob.getUniqueID());
                    projectile.resetUniqueID(new GameRandom(this.getUniqueID()));
                    level.entityManager.projectiles.add(projectile);
                } else {
                    this.getServer().network.sendToClientsWithEntity(new PacketSpawnProjectile(this), this);
                }
            } else {
                this.remove();
            }
        }
    }

    @Override
    public boolean canHit(Mob mob) {
        if (this.ignoredTargets.contains(mob.getUniqueID())) {
            return false;
        }
        return super.canHit(mob);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(197, 197, 197), 12.0f, 180, 18.0f);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        float angle = this.getAngle();
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(angle, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, angle, this.shadowTexture.getHeight() / 2);
    }

    @Override
    public float getAngle() {
        return super.getAngle() * 1.5f;
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.butchersCleaver).volume(0.1f);
    }
}

