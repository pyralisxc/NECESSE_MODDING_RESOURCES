/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.BouncingSlimeBallProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LeggedSlimeThrowerMob
extends HostileMob {
    public static LootTable lootTable = new LootTable();
    public static GameDamage damage = new GameDamage(100.0f);

    public LeggedSlimeThrowerMob() {
        super(450);
        this.setSpeed(60.0f);
        this.setFriction(3.0f);
        this.setArmor(30);
        this.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(150, Integer.MAX_VALUE);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-15, -22, 30, 30);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 4;
        this.swimSinkOffset = -12;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<LeggedSlimeThrowerMob>(this, new ConfusedPlayerChaserWandererAI<LeggedSlimeThrowerMob>(null, 512, 384, 40000, true, false){

            @Override
            public boolean attackTarget(LeggedSlimeThrowerMob mob, Mob target) {
                if (mob.canAttack() && !LeggedSlimeThrowerMob.this.isAccelerating() && !LeggedSlimeThrowerMob.this.hasCurrentMovement()) {
                    mob.attack(target.getX(), target.getY(), false);
                    BouncingSlimeBallProjectile projectile = new BouncingSlimeBallProjectile(mob.getLevel(), mob, mob.x, mob.y, target.x, target.y, 60.0f, 576, damage, 50);
                    projectile.setTargetPrediction(target);
                    mob.getLevel().entityManager.projectiles.add(projectile);
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                    if (LeggedSlimeThrowerMob.this.isClient()) {
                        SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(mob).pitch(1.2f).volume(0.6f));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.leggedSlime.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(LeggedSlimeThrowerMob.getTileCoordinate(x), LeggedSlimeThrowerMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 46;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd body = MobRegistry.Textures.leggedSlime.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(LeggedSlimeThrowerMob.getTileCoordinate(x), LeggedSlimeThrowerMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        float attackProgress = this.getAttackAnimProgress();
        final DrawOptions arms = this.isAttacking ? ItemAttackDrawOptions.start(dir).armSprite(MobRegistry.Textures.leggedSlime.body, 0, 8, 32).setOffsets((dir == 3 ? 36 : 28) + swimMask.drawXOffset, 18 + swimMask.drawYOffset, 8, 15, 12, 4, 12).swingRotation(attackProgress).light(light).pos(drawX, drawY) : null;
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                body.draw();
                swimMask.stop();
                if (arms != null) {
                    arms.draw();
                }
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.leggedSlime.shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 15;
    }

    @Override
    public boolean isSlimeImmune() {
        return true;
    }
}

