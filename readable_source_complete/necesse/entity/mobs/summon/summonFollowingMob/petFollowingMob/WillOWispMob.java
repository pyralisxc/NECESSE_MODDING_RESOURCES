/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TileFinderFlyingAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetFollowingMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WillOWispMob
extends PetFollowingMob {
    public WillOWispMob() {
        super(10);
        this.setSpeed(80.0f);
        this.setFriction(2.0f);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -40, 32, 50);
        this.swimMaskMove = 22;
        this.swimMaskOffset = 16;
        this.swimSinkOffset = -12;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<WillOWispMob>(this, new WillOWispAI(480, 64), new FlyingAIMover());
    }

    public float getDesiredHeight() {
        float perc = GameUtils.getAnimFloat(this.getLevel().getWorldEntity().getTime(), 3000);
        return GameMath.sin(perc * 360.0f) * 10.0f - 20.0f;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f), this.y + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f) + this.getDesiredHeight(), Particle.GType.IMPORTANT_COSMETIC).movesConstant(0.0f, -10.0f).ignoreLight(true).givesLight(193.0f, 0.5f).color((options, lifeTime, timeAlive, lifePercent) -> options.color(new Color(Math.max(36 - (int)(36.0f * lifePercent), 0), Math.max(174 - (int)(97.0f * lifePercent), 0), Math.max(214 - (int)(120.0f * lifePercent), 0)))).size((options, lifeTime, timeAlive, lifePercent) -> options.size((int)(20.0f * (1.0f - lifePercent)), (int)(20.0f * (1.0f - lifePercent)))).lifeTime(500);
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameTexture texture = MobRegistry.Textures.willOWisp;
        int drawX = camera.getDrawX(x) - texture.getWidth() / 2;
        int drawY = camera.getDrawY(y) + (int)this.getDesiredHeight() - texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = texture.initDraw().light(new GameLight(150.0f)).pos(drawX, drawY).size(90);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }

    static class WillOWispAI<T extends WillOWispMob>
    extends SelectorAINode<T> {
        public WillOWispAI(int teleportDistance, int stoppingDistance) {
            this.addChild(new TileFinderFlyingAINode<T>(){

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                    super.init(mob, blackboard);
                    this.maxDistance = 20;
                }

                @Override
                public boolean isValidTile(T mob, Blackboard<T> blackboard, int tileX, int tileY) {
                    return ((Entity)mob).getLevel().getObject((int)tileX, (int)tileY).isPressurePlate;
                }

                @Override
                public Point getStartTile(T mob, Blackboard<T> blackboard) {
                    Mob followingMob = ((Mob)mob).getFollowingMob();
                    if (followingMob != null) {
                        return new Point(followingMob.getTileX(), followingMob.getTileY());
                    }
                    return null;
                }
            });
            this.addChild(new PlayerFollowerAINode(teleportDistance, stoppingDistance));
        }
    }
}

