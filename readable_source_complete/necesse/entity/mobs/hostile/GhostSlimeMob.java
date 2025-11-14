/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileSlimeMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GhostSlimeMob
extends HostileSlimeMob {
    public static LootTable lootTable = new LootTable();
    public static GameDamage damage = new GameDamage(115.0f);
    public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(5);
    public ParticleTypeSwitcher particleTypes = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

    public GhostSlimeMob() {
        super(500);
        this.setSpeed(50.0f);
        this.setFriction(2.0f);
        this.setArmor(30);
        this.jumpStats.setJumpStrength(150.0f);
        this.jumpStats.setJumpCooldown(75);
        this.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(150, Integer.MAX_VALUE);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-15, -32, 30, 40);
        this.swimMaskMove = 10;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -8;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<GhostSlimeMob>(this, new ConfusedCollisionPlayerChaserWandererAI(null, 512, damage, 100, 40000), new FlyingAIMover());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.particleTicks.gameTick();
        while (this.particleTicks.shouldTick()) {
            this.getLevel().entityManager.addTopParticle(this.x + GameRandom.globalRandom.floatGaussian() * 6.0f, this.y + GameRandom.globalRandom.floatGaussian() * 8.0f, this.particleTypes.next()).movesConstant(this.dx / 5.0f, this.dy / 5.0f).color(new Color(41, 52, 55, 150)).height(10.0f).lifeTime(1000);
        }
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.ghostSlime.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath3);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(GhostSlimeMob.getTileCoordinate(x), GhostSlimeMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 50;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.ghostSlime.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(GhostSlimeMob.getTileCoordinate(x), GhostSlimeMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        float attackProgress = this.getAttackAnimProgress();
        final DrawOptions arms = this.isAttacking ? ItemAttackDrawOptions.start(dir).armSprite(MobRegistry.Textures.ghostSlime.body, 0, 8, 32).setOffsets((dir == 3 ? 36 : 28) + swimMask.drawXOffset, 22 + swimMask.drawYOffset, 8, 15, 12, 4, 12).swingRotation(attackProgress).light(light).pos(drawX, drawY) : null;
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
                if (arms != null) {
                    arms.draw();
                }
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.ghostSlime.shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        if (this.inLiquid(x, y)) {
            return new Point(6, dir);
        }
        return new Point(this.getJumpAnimationFrame(6), dir);
    }

    @Override
    public boolean isSlimeImmune() {
        return true;
    }
}

