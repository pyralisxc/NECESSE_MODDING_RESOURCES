/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.FoodBuff;
import necesse.entity.particle.Particle;
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

public class ChefsSpecialRollingPinProjectile
extends BoomerangProjectile {
    protected ParticleTypeSwitcher explosionTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.setWidth(14.0f);
        this.bouncing = 8;
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
        if (mob != null) {
            if (owner != null && !this.isClient()) {
                ArrayList<ActiveBuff> validBuffBuffs = new ArrayList<ActiveBuff>();
                for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
                    FoodConsumableItem foodItem;
                    ActiveBuff activeBuff = owner.buffManager.getBuff(foodBuff);
                    if (activeBuff == null || (foodItem = FoodBuff.getFoodItem(activeBuff)) == null || foodItem.duration * 1000 <= activeBuff.getDurationLeft()) continue;
                    validBuffBuffs.add(activeBuff);
                }
                if (!validBuffBuffs.isEmpty()) {
                    ActiveBuff activeBuff = (ActiveBuff)GameRandom.globalRandom.getOneOf(validBuffBuffs);
                    activeBuff.setDurationLeft(activeBuff.getDurationLeft() + 1000);
                    if (this.isServer()) {
                        this.getServer().network.sendToClientsWithEntity(new PacketMobBuff(owner.getUniqueID(), activeBuff, true), mob);
                    }
                }
            }
            if (!this.isServer()) {
                int particleCount = 25;
                for (int i = 0; i < particleCount; ++i) {
                    float dx = GameRandom.globalRandom.getIntBetween(-25, 25);
                    float dy = GameRandom.globalRandom.getIntBetween(-20, 20);
                    this.getLevel().entityManager.addParticle(x + dx, y + dy, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(25, 35).fadesAlphaTime(100, 500).movesConstant(dx * 0.4f, dy * 0.4f).color(new Color(246, 235, 209, 187)).heightMoves(0.0f, 40.0f).lifeTime(750);
                }
            }
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FLOUR_COVERED_SLOW_DEBUFF, mob, 4000, null), true);
        }
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
        return new SoundSettings(GameResources.rollingPin).volume(0.8f);
    }
}

