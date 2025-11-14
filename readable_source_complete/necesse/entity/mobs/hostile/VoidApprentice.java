/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.decorators.FailerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TeleportOnProjectileHitAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.VoidApprenticeProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidApprentice
extends HostileMob {
    public static LootTable lootTable = new LootTable(LootItem.between("voidshard", 0, 2));
    public final CoordinateMobAbility teleportAbility;

    public VoidApprentice() {
        super(145);
        this.attackCooldown = 800;
        this.attackAnimTime = 200;
        this.setSpeed(40.0f);
        this.setFriction(3.0f);
        this.setArmor(10);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.teleportAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                if (VoidApprentice.this.isClient()) {
                    VoidApprentice.this.getLevel().entityManager.addParticle(new SmokePuffParticle(VoidApprentice.this.getLevel(), VoidApprentice.this.x, VoidApprentice.this.y, new Color(58, 22, 100)), Particle.GType.CRITICAL);
                    VoidApprentice.this.getLevel().entityManager.addParticle(new SmokePuffParticle(VoidApprentice.this.getLevel(), x, y, new Color(58, 22, 100)), Particle.GType.CRITICAL);
                }
                VoidApprentice.this.setPos(x, y, true);
            }
        });
    }

    @Override
    public void init() {
        super.init();
        ConfusedPlayerChaserWandererAI<VoidApprentice> chaserAI = new ConfusedPlayerChaserWandererAI<VoidApprentice>(null, 640, 320, 40000, true, false){

            @Override
            public boolean attackTarget(VoidApprentice mob, Mob target) {
                if (mob.canAttack() && !mob.isAccelerating() && !mob.hasCurrentMovement()) {
                    mob.attack(target.getX(), target.getY(), false);
                    mob.getLevel().entityManager.projectiles.add(new VoidApprenticeProjectile(mob.x, mob.y, target.x, target.y, new GameDamage(20.0f), mob));
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                    return true;
                }
                return false;
            }
        };
        chaserAI.addChildFirst(new FailerAINode<VoidApprentice>(new TeleportOnProjectileHitAINode<VoidApprentice>(5000, 7){

            @Override
            public boolean teleport(VoidApprentice mob, int x, int y) {
                if (mob.isServer()) {
                    mob.teleportAbility.runAndSend(x, y);
                    this.getBlackboard().mover.stopMoving(mob);
                }
                return true;
            }
        }));
        this.ai = new BehaviourTreeAI<VoidApprentice>(this, chaserAI);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.voidApprentice.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(VoidApprentice.getTileCoordinate(x), VoidApprentice.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 8;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(VoidApprentice.getTileCoordinate(x), VoidApprentice.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.voidApprentice).sprite(sprite).dir(dir).mask(swimMask).light(light);
        float animProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir).itemSprite(MobRegistry.Textures.voidApprentice.body, 0, 9, 32).itemRotatePoint(3, 3).itemEnd().armSprite(MobRegistry.Textures.voidApprentice.body, 0, 8, 32).swingRotation(animProgress).light(light);
            humanDrawOptions.attackAnim(attackOptions, animProgress);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.voidApprentice_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("voidapp", 3);
    }
}

