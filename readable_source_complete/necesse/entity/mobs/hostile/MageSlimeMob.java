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
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileSlimeMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.MageSlimeBoltProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MageSlimeMob
extends HostileSlimeMob {
    public static LootTable lootTable = new LootTable();
    public static GameDamage damage = new GameDamage(100.0f);

    public MageSlimeMob() {
        super(450);
        this.setSpeed(50.0f);
        this.setFriction(2.0f);
        this.setArmor(30);
        this.jumpStats.setJumpStrength(150.0f);
        this.jumpStats.setJumpCooldown(50);
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
        this.ai = new BehaviourTreeAI<MageSlimeMob>(this, new ConfusedPlayerChaserWandererAI<MageSlimeMob>(null, 512, 384, 40000, false, false){

            @Override
            public boolean attackTarget(MageSlimeMob mob, Mob target) {
                if (mob.canAttack() && MageSlimeMob.this.dx == 0.0f && MageSlimeMob.this.dy == 0.0f && !MageSlimeMob.this.isAccelerating() && !MageSlimeMob.this.hasCurrentMovement()) {
                    mob.attack(target.getX(), target.getY(), false);
                    MageSlimeBoltProjectile projectile = new MageSlimeBoltProjectile(mob.getLevel(), mob, mob.x, mob.y, target.x, target.y, 60.0f, 768, damage, 50);
                    projectile.moveDist(15.0);
                    projectile.target = target;
                    mob.getLevel().entityManager.projectiles.add(projectile);
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.mageSlime.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(MageSlimeMob.getTileCoordinate(x), MageSlimeMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 50;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.mageSlime.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(MageSlimeMob.getTileCoordinate(x), MageSlimeMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        float attackProgress = this.getAttackAnimProgress();
        final DrawOptions arms = this.isAttacking ? ItemAttackDrawOptions.start(dir).itemSprite(MobRegistry.Textures.mageSlime.body, 0, 9, 32).itemRotatePoint(6, 6).itemEnd().armSprite(MobRegistry.Textures.mageSlime.body, 0, 8, 32).setOffsets((dir == 3 ? 36 : 28) + swimMask.drawXOffset, 22 + swimMask.drawYOffset, 8, 15, 12, 4, 12).swingRotation(attackProgress).light(light).pos(drawX, drawY) : null;
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
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.mageSlime.shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
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

