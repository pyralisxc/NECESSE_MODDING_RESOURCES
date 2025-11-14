/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionShooterPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CryoMissileProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoFlakeMob
extends FlyingHostileMob {
    public static LootTable lootTable = new LootTable(new MobConditionLootItemList(mob -> mob.getLevel() == null || !mob.getLevel().isIncursionLevel, LootItem.between("glacialshard", 1, 2)));
    public static GameDamage baseDamage = new GameDamage(65.0f);
    public static GameDamage incursionDamage = new GameDamage(100.0f);
    public final EmptyMobAbility attackSoundAbility;

    public CryoFlakeMob() {
        super(350);
        this.setSpeed(35.0f);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.2f);
        this.setArmor(20);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-16, -16, 32, 32);
        this.hitBox = new Rectangle(-20, -20, 40, 40);
        this.selectBox = new Rectangle(-25, -25, 50, 50);
        this.attackSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (CryoFlakeMob.this.isClient()) {
                    float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
                    SoundManager.playSound(GameResources.jingle, (SoundEffect)SoundEffect.effect(CryoFlakeMob.this).volume(0.8f).pitch(pitch));
                }
            }
        });
    }

    @Override
    public void init() {
        GameDamage damage;
        super.init();
        if (this.getLevel() instanceof IncursionLevel) {
            this.setMaxHealth(450);
            this.setHealthHidden(this.getMaxHealth());
            this.setArmor(30);
            damage = incursionDamage;
        } else {
            damage = baseDamage;
        }
        this.ai = new BehaviourTreeAI<CryoFlakeMob>(this, new CollisionShooterPlayerChaserWandererAI<CryoFlakeMob>(null, 448, damage, 100, CooldownAttackTargetAINode.CooldownTimer.CAN_ATTACK, 2000, 384, 40000){

            @Override
            public boolean shootAtTarget(CryoFlakeMob mob, Mob target) {
                if (CryoFlakeMob.this.canAttack()) {
                    CryoFlakeMob.this.attackSoundAbility.runAndSend();
                    CryoFlakeMob.this.startAttackCooldown();
                    mob.getLevel().entityManager.projectiles.add(new CryoMissileProjectile(mob.getLevel(), mob, mob.x, mob.y, target.x, target.y, 100.0f, 448, damage, 100));
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
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 30; ++i) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.IMPORTANT_COSMETIC).movesConstant(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1), GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1)).color(new Color(88, 105, 218));
        }
    }

    @Override
    public void playDeathSound() {
        this.playHitSound();
    }

    @Override
    public void playHitSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.jinglehit, (SoundEffect)SoundEffect.effect(this).pitch(pitch));
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(CryoFlakeMob.getTileCoordinate(x), CryoFlakeMob.getTileCoordinate(y));
        int res = MobRegistry.Textures.cryoFlake.getWidth();
        int resHalf = res / 2;
        int drawX = camera.getDrawX(x) - resHalf;
        int drawY = camera.getDrawY(y) - resHalf;
        long time = level.getWorldEntity().getTime();
        float rotation = GameUtils.getTimeRotation(time, 4);
        float glowLight = GameUtils.getAnimFloatContinuous(time, 1000) / 1.5f;
        TextureDrawOptionsEnd body = MobRegistry.Textures.cryoFlake.initDraw().sprite(0, 0, res).rotate(rotation * (float)(this.dx < 0.0f ? -1 : 1), resHalf, resHalf).light(light).pos(drawX, drawY);
        GameLight glowLightLevel = light.copy();
        glowLightLevel.setLevel((int)(glowLight * 150.0f));
        TextureDrawOptionsEnd glow = MobRegistry.Textures.cryoFlake.initDraw().sprite(0, 1, res).rotate(rotation * (float)(this.dx < 0.0f ? -1 : 1), resHalf, resHalf).light(glowLightLevel).pos(drawX, drawY);
        topList.add(tm -> {
            body.draw();
            glow.draw();
        });
    }
}

