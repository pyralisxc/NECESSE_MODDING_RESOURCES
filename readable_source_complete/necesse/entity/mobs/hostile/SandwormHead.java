/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChargingCirclingChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileWormMobHead;
import necesse.entity.mobs.hostile.SandwormBody;
import necesse.entity.mobs.hostile.SandwormTail;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SandwormHead
extends HostileWormMobHead<SandwormBody, SandwormHead> {
    public static LootTable lootTable = new LootTable(new MobConditionLootItemList(mob -> mob.getLevel() == null || !mob.getLevel().isIncursionLevel, LootItem.between("wormcarapace", 1, 2)));
    public static GameDamage headCollisionDamage = new GameDamage(105.0f);
    public static GameDamage bodyCollisionDamage = new GameDamage(75.0f);
    public static GameDamage incursionHeadCollisionDamage = new GameDamage(120.0f);
    public static GameDamage incursionBodyCollisionDamage = new GameDamage(90.0f);
    public static float lengthPerBodyPart = 20.0f;
    public static float waveLength = 350.0f;
    public static final int totalBodyParts = 20;
    public GameDamage collisionDamage;

    public SandwormHead() {
        super(1200, waveLength, 70.0f, 20, 20.0f, -24.0f);
        this.moveAccuracy = 120;
        this.setSpeed(100.0f);
        this.setArmor(15);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-16, -14, 32, 28);
        this.hitBox = new Rectangle(-20, -16, 40, 32);
        this.selectBox = new Rectangle(-20, -35, 40, 40);
    }

    @Override
    protected float getDistToBodyPart(SandwormBody bodyPart, int index, float lastDistance) {
        return lengthPerBodyPart;
    }

    @Override
    protected SandwormBody createNewBodyPart(int index) {
        SandwormBody bodyPart = index == 19 ? new SandwormTail() : new SandwormBody();
        bodyPart.sharesHitCooldownWithNext = index % 3 < 2;
        bodyPart.relaysBuffsToNext = index % 3 < 2;
        bodyPart.sprite = new Point(0, 1 + index % 4);
        return bodyPart;
    }

    @Override
    protected void playMoveSound() {
        SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(this).falloffDistance(2000).volume(0.6f));
    }

    @Override
    public void init() {
        super.init();
        if (this.getLevel() instanceof IncursionLevel) {
            this.setMaxHealth(1800);
            this.setHealthHidden(this.getMaxHealth());
            this.setArmor(20);
            this.collisionDamage = headCollisionDamage;
        } else {
            this.collisionDamage = incursionHeadCollisionDamage;
        }
        this.ai = new BehaviourTreeAI<SandwormHead>(this, new PlayerChargingCirclingChaserAI(null, 2560, 500, 20), new FlyingAIMover());
    }

    @Override
    public float getTurnSpeed(float delta) {
        return super.getTurnSpeed(delta) * 1.2f;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.collisionDamage;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.sandWorm, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y);
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        SandwormHead.addAngledDrawable(list, new GameSprite(MobRegistry.Textures.sandWorm, 0, 0, 64), MobRegistry.Textures.sandWorm_mask, light, (int)this.height, headAngle, drawX, drawY, 64);
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.sandWorm_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }
}

