/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.RuneSpiritPoolEvent;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.TheCursedCroneMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CursedCroneSpiritGhoulMob
extends BossMob {
    public static LootTable lootTable = new LootTable();
    protected double distanceRanSinceLastPoolSpawn;

    public CursedCroneSpiritGhoulMob() {
        super(150);
        this.isSummoned = true;
        this.setSpeed(15.0f);
        this.setFriction(4.0f);
        this.setKnockbackModifier(0.5f);
        this.setArmor(20);
        this.moveAccuracy = 8;
        this.collision = new Rectangle(-12, -5, 24, 20);
        this.hitBox = new Rectangle(-16, -8, 32, 26);
        this.selectBox = new Rectangle(-20, -20, 40, 40);
        this.swimMaskMove = 16;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = -4;
    }

    @Override
    public void init() {
        super.init();
        CollisionPlayerChaserWandererAI<CursedCroneSpiritGhoulMob> chaserAINode = new CollisionPlayerChaserWandererAI<CursedCroneSpiritGhoulMob>(null, 1024, TheCursedCroneMob.ghoulsMeleeDamage, 50, 40000){

            @Override
            public boolean attackTarget(CursedCroneSpiritGhoulMob mob, Mob target) {
                return true;
            }
        };
        this.ai = new BehaviourTreeAI<CursedCroneSpiritGhoulMob>(this, chaserAINode);
        if (this.isClient()) {
            this.playAmbientSound();
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.spiritGhoul.body, 12, i, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(CursedCroneSpiritGhoulMob.getTileCoordinate(x), CursedCroneSpiritGhoulMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 36;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd body = MobRegistry.Textures.spiritGhoul.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(CursedCroneSpiritGhoulMob.getTileCoordinate(x), CursedCroneSpiritGhoulMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                body.draw();
                swimMask.stop();
            }
        });
    }

    @Override
    public int getSwimMaskMove() {
        if (this.getDir() != 2) {
            return super.getSwimMaskMove() + 4;
        }
        return super.getSwimMaskMove();
    }

    @Override
    public int getRockSpeed() {
        return 15;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.FRICTION, Float.valueOf(0.0f)).min(Float.valueOf(0.75f)));
    }

    @Override
    public void serverTick() {
        double poolSpawnRunDistance;
        double distanceRan;
        super.serverTick();
        if (!this.inLiquid() && (distanceRan = this.getDistanceRan()) - this.distanceRanSinceLastPoolSpawn > (poolSpawnRunDistance = 16.0)) {
            RuneSpiritPoolEvent event = new RuneSpiritPoolEvent(this, (int)this.x, (int)this.y, GameRandom.globalRandom, TheCursedCroneMob.ghoulsGooDamage, 4.0f);
            this.getLevel().entityManager.events.add(event);
            this.distanceRanSinceLastPoolSpawn = distanceRan;
        }
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.petEvilMinion).volume(0.6f);
    }

    @Override
    protected SoundSettings getHurtSound() {
        return null;
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.petEvilMinion).volume(0.6f);
    }
}

