/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.DeathMessageTable
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.HumanTexture
 *  necesse.entity.mobs.MaskShaderOptions
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ability.CoordinateMobAbility
 *  necesse.entity.mobs.ability.MobAbility
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.decorators.FailerAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.TeleportOnProjectileHitAINode
 *  necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.hostile.HostileMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.particle.ParticleOption
 *  necesse.entity.particle.SmokePuffParticle
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.human.HumanDrawOptions
 *  necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.ChanceLootItem
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.decorators.FailerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TeleportOnProjectileHitAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidAdept
extends HostileMob {
    public final CoordinateMobAbility teleportAbility;
    public final CoordinateMobAbility teleportParticle;
    public static AphAreaList showAttackRange = new AphAreaList(new AphArea(250.0f, AphColors.lighter_gray));
    public static AphAreaList attackArea = new AphAreaList(new AphArea(250.0f, AphColors.dark_magic).setDamageArea(new GameDamage(DamageTypeRegistry.MAGIC, 50.0f)).setDebuffArea(10000, "brokenarmor"));
    public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between((String)"voidshard", (int)0, (int)2), new ChanceLootItem(0.05f, "adeptsbook")});
    public static HumanTexture texture;
    public int attackCount = 0;

    public VoidAdept() {
        super(160);
        this.attackCooldown = 2000;
        this.attackAnimTime = 1500;
        this.setSpeed(40.0f);
        this.setFriction(3.0f);
        this.setArmor(10);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.teleportAbility = (CoordinateMobAbility)this.registerAbility((MobAbility)new CoordinateMobAbility(){

            protected void run(int x, int y) {
                if (VoidAdept.this.isClient()) {
                    VoidAdept.this.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(VoidAdept.this.getLevel(), VoidAdept.this.x, VoidAdept.this.y, AphColors.dark_magic), Particle.GType.CRITICAL);
                    VoidAdept.this.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(VoidAdept.this.getLevel(), (float)x, (float)y, AphColors.dark_magic), Particle.GType.CRITICAL);
                }
                VoidAdept.this.setPos(x, y, true);
            }
        });
        this.teleportParticle = (CoordinateMobAbility)this.registerAbility((MobAbility)new CoordinateMobAbility(){

            protected void run(int x, int y) {
                if (VoidAdept.this.isClient()) {
                    VoidAdept.this.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(VoidAdept.this.getLevel(), (float)x, (float)y, AphColors.dark_magic), Particle.GType.CRITICAL);
                }
            }
        });
    }

    public void init() {
        super.init();
        PlayerChaserWandererAI<VoidAdept> playerChaserAI = new PlayerChaserWandererAI<VoidAdept>(null, 640, 200, 40000, false, false){

            public boolean attackTarget(VoidAdept mob, Mob target) {
                if (mob.canAttack() && !mob.isAccelerating() && !mob.hasCurrentMovement()) {
                    mob.attack(target.getX(), target.getY(), false);
                    mob.addBuff(new ActiveBuff(AphBuffs.STUN, (Mob)mob, 1500, (Attacker)mob), true);
                    return true;
                }
                return false;
            }
        };
        playerChaserAI.addChildFirst((AINode)new FailerAINode((AINode)new TeleportOnProjectileHitAINode<VoidAdept>(2000, 3){

            public boolean teleport(VoidAdept mob, int x, int y) {
                if (mob.isServer()) {
                    if (mob.isAttacking) {
                        mob.teleportParticle.runAndSend(x, y);
                    } else {
                        mob.teleportAbility.runAndSend((int)mob.x, (int)mob.y);
                    }
                    this.getBlackboard().mover.stopMoving((Mob)mob);
                }
                return true;
            }
        }));
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)playerChaserAI);
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), VoidAdept.texture.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    public float getAttackAnimProgress() {
        float progress = (float)(this.getWorldEntity().getTime() - this.attackTime) / (float)this.attackAnimTime;
        if (progress >= 1.0f) {
            this.isAttacking = false;
            if (this.attackCount == 2) {
                this.attackCount = 0;
                attackArea.execute((Mob)this, false);
                if (this.isServer()) {
                    int index;
                    int tileX = this.getX() / 32;
                    int tileY = this.getY() / 32;
                    Point moveOffset = this.getPathMoveOffset();
                    ArrayList<Point> possiblePoints = new ArrayList<Point>();
                    for (index = tileX - 7; index <= tileX + 7; ++index) {
                        for (int y = tileY - 7; y <= tileY + 7; ++y) {
                            int mobX = index * 32 + moveOffset.x;
                            int mobY = y * 32 + moveOffset.y;
                            if (this.collidesWith(this.getLevel(), mobX, mobY)) continue;
                            possiblePoints.add(new Point(mobX, mobY));
                        }
                    }
                    if (!possiblePoints.isEmpty()) {
                        index = GameRandom.globalRandom.nextInt(possiblePoints.size());
                        Point point = (Point)possiblePoints.get(index);
                        this.teleportAbility.runAndSend(point.x, point.y);
                    }
                }
            }
        } else if (progress >= 0.5f && this.attackCount == 1) {
            this.attackCount = 2;
            if (this.isClient()) {
                showAttackRange.executeClient(this.getLevel(), this.x, this.y);
            }
        } else if (this.attackCount == 0) {
            this.attackCount = 1;
            if (this.isClient()) {
                showAttackRange.executeClient(this.getLevel(), this.x, this.y);
            }
        }
        return Math.min(1.0f, progress);
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount((Mob)this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, texture).sprite(sprite).dir(dir).mask(swimMask).light(light);
        float animProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            ItemAttackDrawOptions attackOptions = VoidAdept.getItemAttackDrawOptions(dir, light);
            humanDrawOptions.attackAnim((HumanAttackDrawOptions)attackOptions, animProgress);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable(){

            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    private static ItemAttackDrawOptions getItemAttackDrawOptions(int dir, GameLight light) {
        return ItemAttackDrawOptions.start((int)dir).itemSprite(VoidAdept.texture.body, 0, 9, 32).itemRotatePoint(3, 3).itemEnd().armSprite(VoidAdept.texture.body, 0, 8, 32).light(light);
    }

    public int getRockSpeed() {
        return 20;
    }

    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("voidapp", 3);
    }
}

