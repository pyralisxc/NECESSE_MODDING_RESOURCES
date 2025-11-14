/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
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
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChargingCirclingChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileWormMobHead;
import necesse.entity.mobs.hostile.SlimeWormBody;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SlimeWormHead
extends HostileWormMobHead<SlimeWormBody, SlimeWormHead> {
    public static LootTable lootTable = new LootTable();
    public static float lengthPerBodyPart = 20.0f;
    public static float waveLength = 350.0f;
    public static final int totalBodyParts = 30;
    public static GameDamage headCollisionDamage = new GameDamage(130.0f);
    public static GameDamage bodyCollisionDamage = new GameDamage(100.0f);

    public SlimeWormHead() {
        super(1800, waveLength, 70.0f, 30, 0.0f, -24.0f);
        this.moveAccuracy = 120;
        this.setSpeed(50.0f);
        this.setArmor(25);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-16, -14, 32, 28);
        this.hitBox = new Rectangle(-20, -16, 40, 32);
        this.selectBox = new Rectangle(-20, -35, 40, 40);
    }

    @Override
    protected float getDistToBodyPart(SlimeWormBody bodyPart, int index, float lastDistance) {
        float length = lengthPerBodyPart;
        if (index >= 24) {
            int sprite = index - 24 + 1;
            length = Math.max(length - (float)(sprite * 2), 5.0f);
        }
        float movedDist = 0.0f;
        WormMoveLine first = (WormMoveLine)this.moveLines.getFirst();
        if (first != null) {
            movedDist = first.movedDist;
        }
        return length - (float)((Math.sin(((float)index - (movedDist /= 0.77f) / (lengthPerBodyPart * 0.8f)) / 2.0f) + 1.0) / 2.0) * (length * 0.6f);
    }

    @Override
    protected void onUpdatedBodyPartPos(SlimeWormBody bodyPart, int index, float distToBodyPart) {
        super.onUpdatedBodyPartPos(bodyPart, index, distToBodyPart);
        int minSprite = 0;
        if (index >= 24) {
            minSprite = index - 24 + 1;
        }
        float movedDist = 0.0f;
        WormMoveLine first = (WormMoveLine)this.moveLines.getFirst();
        if (first != null) {
            movedDist = first.movedDist;
        }
        float percent = 1.0f - (float)((Math.sin(((float)index - (movedDist /= 0.77f) / (lengthPerBodyPart * 0.8f)) / 2.0f) + 1.0) / 2.0);
        int lerp = GameMath.limit(GameMath.lerp(percent, 0, 4), minSprite, Math.max(minSprite, 4));
        bodyPart.sprite = new Point(0, lerp);
    }

    @Override
    protected SlimeWormBody createNewBodyPart(int index) {
        SlimeWormBody bodyPart = new SlimeWormBody();
        bodyPart.sharesHitCooldownWithNext = index % 3 < 2;
        bodyPart.relaysBuffsToNext = index % 3 < 2;
        bodyPart.bodyIndex = index;
        if (index >= 24) {
            int sprite = index - 24 + 1;
            bodyPart.sprite = new Point(0, sprite);
        } else {
            bodyPart.sprite = new Point(0, 0);
        }
        return bodyPart;
    }

    @Override
    protected void playMoveSound() {
        SoundManager.playSound(GameResources.slimeSplash2, (SoundEffect)SoundEffect.effect(this).volume(0.2f).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.2f)));
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SlimeWormHead>(this, new PlayerChargingCirclingChaserAI(null, 2560, 500, 20), new FlyingAIMover());
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
        return null;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.slimeWorm.shadow;
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 32;
        return shadowTexture.initDraw().sprite(0, 1, 64).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    @Override
    public boolean isSlimeImmune() {
        return true;
    }
}

