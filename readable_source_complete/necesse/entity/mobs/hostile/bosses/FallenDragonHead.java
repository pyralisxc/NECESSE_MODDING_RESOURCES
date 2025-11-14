/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChargingCirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.BossWormMobHead;
import necesse.entity.mobs.hostile.bosses.FallenDragonBody;
import necesse.entity.mobs.hostile.bosses.FallenWizardMob;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenDragonHead
extends BossWormMobHead<FallenDragonBody, FallenDragonHead> {
    public static LootTable lootTable = new LootTable();
    public static float lengthPerBodyPart = 40.0f;
    public static float waveLength = 800.0f;
    public static final int totalBodyParts = 8;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(150, 300, 400, 500, 650);
    public Point2D.Float centerPosition;
    public FallenWizardMob master;
    public float circlingAngleOffset;
    protected SoundPlayer moveSoundPlayer;

    public FallenDragonHead() {
        super(100, waveLength, 100.0f, 8, 10.0f, -40.0f);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 100;
        this.movementUpdateCooldown = 2000;
        this.movePosTolerance = 700.0f;
        this.setSpeed(180.0f);
        this.setArmor(35);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-25, -20, 50, 40);
        this.selectBox = new Rectangle(-32, -80, 64, 84);
    }

    @Override
    protected float getDistToBodyPart(FallenDragonBody bodyPart, int index, float lastDistance) {
        if (index == 1) {
            return lengthPerBodyPart;
        }
        return lengthPerBodyPart;
    }

    @Override
    protected FallenDragonBody createNewBodyPart(int index) {
        FallenDragonBody bodyPart = new FallenDragonBody();
        int tailParts = 3;
        if (index == 1) {
            bodyPart.spriteY = 1;
        } else if (index == 8 - tailParts - 1) {
            bodyPart.spriteY = 1;
        } else if (index >= 8 - tailParts) {
            int tailPart = Math.abs(8 - index - tailParts);
            bodyPart.spriteY = 3 + tailPart;
        } else {
            bodyPart.spriteY = 2;
        }
        bodyPart.spawnsParticles = true;
        return bodyPart;
    }

    @Override
    protected void playMoveSound() {
        if (this.moveSoundPlayer == null || this.moveSoundPlayer.isDone()) {
            this.moveSoundPlayer = SoundManager.playSound(new SoundSettings(GameResources.wind2).volume(0.2f).pitchVariance(0.0f).fallOffDistance(1400), this);
        }
        if (this.moveSoundPlayer != null) {
            this.moveSoundPlayer.refreshLooping();
        }
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.roar).volume(0.7f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.roar);
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.roar).volume(0.6f).basePitch(2.0f);
    }

    @Override
    public void dispose() {
        if (this.moveSoundPlayer != null) {
            this.moveSoundPlayer.stop();
        }
        super.dispose();
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        if (this.moveSoundPlayer != null) {
            this.moveSoundPlayer.stop();
        }
        super.remove(knockbackX, knockbackY, attacker, isDeath);
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return FallenWizardMob.dragonHeadDamage;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<FallenDragonHead>(this, new FallenDragonAI(10000, 800, 2560, 500, 20), new FlyingAIMover());
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.master != null && (this.master.removed() || !this.master.isHostile)) {
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public int getFlyingHeight() {
        return 20;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.fallenWizardDragon, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        MobDrawable shoulderDrawable;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 32;
        int drawY = camera.getDrawY(this.y);
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        final MobDrawable headDrawable = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.fallenWizardDragon, 0, 0, 64), null, light, (int)this.height, headAngle, drawX, drawY, 96);
        ComputedObjectValue<Object, Double> shoulderLine = new ComputedObjectValue<Object, Double>(null, () -> 0.0);
        shoulderLine = WormMobHead.moveDistance(this.moveLines.getFirstElement(), 35.0);
        if (shoulderLine.object != null) {
            Point2D.Double shoulderPos = WormMobHead.linePos(shoulderLine);
            GameLight shoulderLight = level.getLightLevel(FallenDragonHead.getTileCoordinate(shoulderPos.x), FallenDragonHead.getTileCoordinate(shoulderPos.y));
            int shoulderDrawX = camera.getDrawX((float)shoulderPos.x) - 32;
            int shoulderDrawY = camera.getDrawY((float)shoulderPos.y);
            float shoulderHeight = this.getWaveHeight(((WormMoveLine)((GameLinkedList.Element)shoulderLine.object).object).movedDist + ((Double)shoulderLine.get()).floatValue());
            float shoulderAngle = GameMath.fixAngle((float)GameMath.getAngle(new Point2D.Double((double)this.x - shoulderPos.x, (double)(this.y - this.height) - (shoulderPos.y - (double)shoulderHeight))));
            shoulderDrawable = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.fallenWizardDragon, 0, 2, 64), null, shoulderLight, (int)shoulderHeight, shoulderAngle, shoulderDrawX, shoulderDrawY, 96);
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
        GameTexture shadowTexture = MobRegistry.Textures.swampGuardian_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(2, 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 16;
        int drawY = y - 16;
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        MobRegistry.Textures.fallenWizardDragon.initDraw().sprite(0, 6, 64).rotate(headAngle + 90.0f, 16, 16).size(32, 32).draw(drawX, drawY);
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-12, -12, 24, 24);
    }

    @Override
    public GameTooltips getMapTooltips() {
        if (!this.isVisible()) {
            return null;
        }
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.POISON_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.FROST_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    public static class FallenDragonAI<T extends FallenDragonHead>
    extends SequenceAINode<T> {
        public FallenDragonAI(int circlingTimeMilliseconds, int circlingRange, int chaserSearchDistance, int chaserCirclingRange, int chaserNextAngleOffset) {
            this.addChild(new ArenaCirclingAINode(circlingTimeMilliseconds, circlingRange));
            this.addChild(new TargetFinderAINode<T>(chaserSearchDistance){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
                }
            });
            this.addChild(new ChargingCirclingChaserAINode(chaserCirclingRange, chaserNextAngleOffset));
        }
    }

    public static class ArenaCirclingAINode<T extends FallenDragonHead>
    extends AINode<T> {
        public int circlingTime;
        public int circlingRange;
        public int timer;

        public ArenaCirclingAINode(int circlingTimeMilliseconds, int circlingRange) {
            this.circlingTime = circlingTimeMilliseconds;
            this.circlingRange = circlingRange;
        }

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (this.timer < this.circlingTime) {
                if (!blackboard.mover.isCurrentlyMovingFor(this)) {
                    Point2D.Float center = ((FallenDragonHead)mob).centerPosition != null ? ((FallenDragonHead)mob).centerPosition : new Point2D.Float(((FallenDragonHead)mob).x, ((FallenDragonHead)mob).y);
                    float speed = MobMovementCircle.convertToRotSpeed(this.circlingRange, ((Mob)mob).getSpeed());
                    blackboard.mover.setCustomMovement(this, new MobMovementCircleLevelPos((Mob)mob, center.x, center.y, this.circlingRange, speed, ((FallenDragonHead)mob).circlingAngleOffset, false));
                }
                this.timer += 50;
                return AINodeResult.FAILURE;
            }
            return AINodeResult.SUCCESS;
        }
    }
}

