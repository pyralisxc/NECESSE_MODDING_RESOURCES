/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ForestSpectorDrainSoulLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.MobAfterimageParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ForestSpectorMob
extends FlyingHostileMob {
    private final int spriteRes = 64;
    protected long lastAfterImageTime;
    public static LootTable lootTable = new LootTable(ChanceLootItem.between(0.25f, "coin", 8, 22), ChanceLootItem.between(0.15f, "amber", 1, 2), new ChanceLootItem(0.025f, "enchantingscroll"));

    public ForestSpectorMob() {
        super(250);
        this.setSpeed(40.0f);
        this.setFriction(3.0f);
        this.setArmor(20);
        this.attackCooldown = 500;
        this.collision = new Rectangle(-10, -13, 20, 26);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -32, 32, 38);
    }

    @Override
    public void init() {
        super.init();
        this.lastAfterImageTime = 0L;
        this.ai = new BehaviourTreeAI<ForestSpectorMob>(this, new ConfusedPlayerChaserWandererAI<ForestSpectorMob>(null, 448, 200, 40000, false, false){

            @Override
            public boolean canHitTarget(ForestSpectorMob mob, float fromX, float fromY, Mob target) {
                return ChaserAINode.hasLineOfSightToTarget(mob, fromX, fromY, -10.0f, target, 10.0f);
            }

            @Override
            public boolean attackTarget(ForestSpectorMob mob, Mob target) {
                if (mob.canAttack()) {
                    mob.attack(target.getX(), target.getY(), false);
                    ForestSpectorDrainSoulLevelEvent event = new ForestSpectorDrainSoulLevelEvent(target, mob);
                    target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIRIT_HAUNTED, target, 6.0f, (Attacker)mob), true);
                    ForestSpectorMob.this.getLevel().entityManager.events.add(event);
                    return true;
                }
                return false;
            }
        }, new FlyingAIMover());
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("forestspector", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.forestSpector, i, 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Level level = this.getLevel();
        if ((this.dx != 0.0f || this.dy != 0.0f) && this.getTime() > this.lastAfterImageTime) {
            this.lastAfterImageTime = this.getTime() + 250L;
            level.entityManager.addParticle(new MobAfterimageParticle(level, this, MobRegistry.Textures.forestSpector, 64, 1000), Particle.GType.COSMETIC);
        }
    }

    @Override
    public int getFlyingHeight() {
        return 64;
    }

    @Override
    public boolean isWaterWalking() {
        return true;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(ForestSpectorMob.getTileCoordinate(x), ForestSpectorMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 56;
        int anim = GameUtils.getAnim(this.getTime(), 5, 500);
        final TextureDrawOptionsEnd options = MobRegistry.Textures.forestSpector.initDraw().sprite(anim, this.getDir(), 64).light(light).alpha(0.8f).pos(drawX, drawY += this.getBobbing(x, y));
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.forestSpector_shadow.initDraw().sprite(anim, this.getDir(), 64, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }
}

