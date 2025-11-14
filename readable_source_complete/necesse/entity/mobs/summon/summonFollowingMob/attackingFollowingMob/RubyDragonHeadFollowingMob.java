/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerCirclingFollowerCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingWormMobHead;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RubyDragonBodyFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RubyDragonHeadFollowingMob
extends AttackingFollowingWormMobHead<RubyDragonBodyFollowingMob, RubyDragonHeadFollowingMob> {
    public static float lengthPerBodyPart = 32.0f;
    public static float waveLength = 350.0f;
    public static final int totalBodyParts = 4;

    public RubyDragonHeadFollowingMob() {
        super(1200, waveLength, 70.0f, 4, 20.0f, -24.0f);
        this.moveAccuracy = 10;
        this.setSpeed(100.0f);
        this.setArmor(15);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-16, -14, 32, 28);
        this.hitBox = new Rectangle(-20, -16, 40, 32);
        this.selectBox = new Rectangle(-20, -35, 40, 40);
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "rubydragon");
    }

    @Override
    protected float getDistToBodyPart(RubyDragonBodyFollowingMob bodyPart, int index, float lastDistance) {
        return lengthPerBodyPart;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<RubyDragonHeadFollowingMob>(this, new PlayerCirclingFollowerCollisionChaserAI(576, null, 15, -1, 1), new FlyingAIMover());
    }

    @Override
    protected RubyDragonBodyFollowingMob createNewBodyPart(int index) {
        RubyDragonBodyFollowingMob bodyPart = new RubyDragonBodyFollowingMob();
        bodyPart.spriteY = index == 3 ? 2 : 1;
        bodyPart.collisionDamage = this.summonDamage.modDamage(0.8f);
        bodyPart.modifiers = this.summonModifiers;
        return bodyPart;
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        return false;
    }

    @Override
    protected void playMoveSound() {
    }

    @Override
    public float getTurnSpeed(float delta) {
        return super.getTurnSpeed(delta) * 1.2f;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.summonDamage;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.sandWorm, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        MobDrawable shoulderDrawable;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y);
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        final MobDrawable headDrawable = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.rubyDragon, 0, 0, 64), null, light, (int)this.height, headAngle, drawX, drawY, 96);
        ComputedObjectValue<Object, Double> shoulderLine = new ComputedObjectValue<Object, Double>(null, () -> 0.0);
        shoulderLine = WormMobHead.moveDistance(this.moveLines.getFirstElement(), 32.0);
        if (shoulderLine.object != null) {
            Point2D.Double shoulderPos = WormMobHead.linePos(shoulderLine);
            GameLight shoulderLight = level.getLightLevel(GameMath.getTileCoordinate(shoulderPos.x), GameMath.getTileCoordinate(shoulderPos.y));
            int shoulderDrawX = camera.getDrawX((float)shoulderPos.x) - 32;
            int shoulderDrawY = camera.getDrawY((float)shoulderPos.y);
            float shoulderHeight = this.getWaveHeight(((WormMoveLine)((GameLinkedList.Element)shoulderLine.object).object).movedDist + ((Double)shoulderLine.get()).floatValue());
            float shoulderAngle = GameMath.fixAngle((float)GameMath.getAngle(new Point2D.Double((double)this.x - shoulderPos.x, (double)(this.y - this.height) - (shoulderPos.y - (double)shoulderHeight))));
            shoulderDrawable = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.rubyDragon, 0, 1, 64), null, shoulderLight, (int)shoulderHeight, shoulderAngle, shoulderDrawX, shoulderDrawY, 96);
        } else {
            shoulderDrawable = null;
        }
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                if (shoulderDrawable != null) {
                    shoulderDrawable.draw(tickManager);
                }
                headDrawable.draw(tickManager);
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.sandWorm_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }
}

