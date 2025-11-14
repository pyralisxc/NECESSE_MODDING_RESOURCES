/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.trees.FollowerWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.friendly.FriendlyRopableMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WildOstrichMob
extends FriendlyRopableMob {
    public static LootTable lootTable = new LootTable(new LootItem("inefficientfeather"));
    private boolean hide;
    public final CoordinateMobAbility hideAbility;

    public WildOstrichMob() {
        super(200);
        this.setSpeed(65.0f);
        this.setFriction(3.0f);
        this.setSwimSpeed(0.4f);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-16, -12, 32, 24);
        this.selectBox = new Rectangle(-18, -83, 36, 90);
        this.swimMaskMove = 32;
        this.swimMaskOffset = -32;
        this.swimSinkOffset = -2;
        this.hideAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                WildOstrichMob.this.stopMoving();
                WildOstrichMob.this.setPos(x, y, false);
                WildOstrichMob.this.hide = true;
                if (x < 0) {
                    WildOstrichMob.this.setDir(1);
                } else {
                    WildOstrichMob.this.setDir(3);
                }
                WildOstrichMob.this.dx = 0.0f;
                WildOstrichMob.this.dy = 0.0f;
            }
        });
    }

    @Override
    public void init() {
        super.init();
        FollowerWandererAI<WildOstrichMob> followerWandererAI = new FollowerWandererAI<WildOstrichMob>(320, 64, 30000){

            @Override
            protected Mob getFollowingMob(WildOstrichMob mob) {
                return mob.getRopeMob();
            }
        };
        followerWandererAI.addChild(new AINode<WildOstrichMob>(){

            @Override
            protected void onRootSet(AINode<WildOstrichMob> root, WildOstrichMob mob, Blackboard<WildOstrichMob> blackboard) {
                blackboard.onEvent("ranAway", e -> mob.hideAbility.runAndSend(mob.getX(), mob.getY()));
            }

            @Override
            public void init(WildOstrichMob mob, Blackboard<WildOstrichMob> blackboard) {
            }

            @Override
            public AINodeResult tick(WildOstrichMob mob, Blackboard<WildOstrichMob> blackboard) {
                return AINodeResult.SUCCESS;
            }
        });
        this.ai = new BehaviourTreeAI<WildOstrichMob>(this, followerWandererAI, new AIMover());
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.dx != 0.0f || this.dy != 0.0f) {
            this.hide = false;
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.ostrichAmbient).volume(1.1f);
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.ostrichHurt).volume(1.2f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.ostrichDeath).volume(1.2f);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.ostrich, GameRandom.globalRandom.nextInt(5), 12, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(WildOstrichMob.getTileCoordinate(x), WildOstrichMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 32 - 11;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(WildOstrichMob.getTileCoordinate(x), WildOstrichMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        if (this.hide && !this.inLiquid(x, y)) {
            sprite = dir == 1 ? new Point(5, 4) : new Point(4, 4);
        }
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = !this.isMounted() ? MobRegistry.Textures.ostrich.initDraw().sprite(sprite.x, sprite.y, 64, 96).addMaskShader(swimMask).light(light).pos(drawX, drawY) : MobRegistry.Textures.ostrichMount.initDraw().sprite(sprite.x, sprite.y, 64, 96).addMaskShader(swimMask).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.ostrich_shadow.initDraw().sprite(sprite.x, sprite.y, 64, 96).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.ostrich_shadow;
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 32 - 11;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        if (this.hide && !this.inLiquid(x, y)) {
            sprite = dir == 1 ? new Point(5, 4) : new Point(4, 4);
        }
        return shadowTexture.initDraw().sprite(sprite.x, sprite.y, 64, 96).light(light).pos(drawX, drawY);
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }
}

