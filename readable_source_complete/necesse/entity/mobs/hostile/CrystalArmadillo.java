/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CrystalArmadillo
extends HostileMob {
    public static GameDamage collisionDamage = new GameDamage(90.0f);
    public boolean isBall;
    public BooleanMobAbility setIsBallAbility;

    public CrystalArmadillo() {
        super(400);
        this.setDefaultStats();
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.setIsBallAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                if (value) {
                    CrystalArmadillo.this.setBallStats();
                } else {
                    CrystalArmadillo.this.setDefaultStats();
                }
                CrystalArmadillo.this.isBall = value;
            }
        });
    }

    private void setDefaultStats() {
        this.setArmor(60);
        this.setSpeed(20.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.0f);
    }

    private void setBallStats() {
        this.setArmor(40);
        this.setSpeed(200.0f);
        this.setFriction(1.0f);
        this.setKnockbackModifier(1.5f);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isBall);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        boolean shouldBeBall = reader.getNextBoolean();
        if (shouldBeBall) {
            this.setBallStats();
        }
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        if (this.isBall) {
            return collisionDamage;
        }
        return super.getCollisionDamage(target, fromPacket, packetSubmitter);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<CrystalArmadillo>(this, new CrystalArmadilloAI(null, 256, collisionDamage, 100, 40000));
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isBall) {
            int dx = GameRandom.globalRandom.getIntBetween(-25, 25);
            int dy = GameRandom.globalRandom.getIntBetween(-1, -10);
            int colorRandomizer = GameRandom.globalRandom.getIntBetween(0, 30);
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).color(new Color(130 + colorRandomizer, 120 + colorRandomizer, 110 + colorRandomizer)).sizeFades(50, 75).movesConstant(dx, dy).lifeTime(1000);
        }
    }

    @Override
    public int stoppingDistance(float friction, float currentSpeed) {
        return 0;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.crystalArmadillo, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(CrystalArmadillo.getTileCoordinate(x), CrystalArmadillo.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(CrystalArmadillo.getTileCoordinate(x), CrystalArmadillo.getTileCoordinate(y)).getMobSinkingAmount(this);
        if (this.isBall) {
            sprite.x = 6 + (int)(this.getLevel().getWorldEntity().getLocalTime() / 100L % 2L);
        }
        final TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.crystalArmadillo.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        final TextureDrawOptionsEnd drawOptionsLight = MobRegistry.Textures.crystalArmadillo_light.initDraw().sprite(sprite.x, sprite.y, 64).light(light.minLevelCopy(100.0f)).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
                drawOptionsLight.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public static class CrystalArmadilloAI<T extends CrystalArmadillo>
    extends SelectorAINode<T> {
        public CrystalArmadilloAI(Supplier<Boolean> shouldEscape, int searchDistance, GameDamage damage, int knockback, int wanderFrequency) {
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                    blackboard.onGlobalTick(event -> {
                        boolean shouldBeBall;
                        Mob target = blackboard.getObject(Mob.class, "chaserTarget");
                        boolean bl = shouldBeBall = target != null;
                        if (mob.isBall != shouldBeBall) {
                            mob.setIsBallAbility.runAndSend(shouldBeBall);
                        }
                    });
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    return AINodeResult.FAILURE;
                }
            });
            this.addChild(new CollisionPlayerChaserWandererAI(shouldEscape, searchDistance, damage, knockback, wanderFrequency));
        }
    }
}

