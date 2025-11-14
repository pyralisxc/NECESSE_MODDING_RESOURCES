/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.BirdCritterAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BirdMob
extends CritterMob {
    public static LootTable lootTable = new LootTable();
    public static final int[] peckingAnimationTimes = new int[]{1000, 50, 50, 50, 50, 800, 50, 50, 50, 50, 200, 800};
    public static final int[] peckingFrames = new int[]{0, 1, 2, 3, 4, -1, 1, 2, 3, 4, -1, 0};
    protected float height;
    protected long peckStartTime;
    public final EmptyMobAbility peckAbility;

    public BirdMob() {
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -24, 32, 32);
        this.peckAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                BirdMob.this.peckStartTime = BirdMob.this.getWorldEntity().getLocalTime();
            }
        });
        this.ambientSoundCooldownMin = 8000;
        this.ambientSoundCooldownMax = 15000;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<BirdMob>(this, new BirdCritterAI());
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextFloat(this.height);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.height = reader.getNextFloat();
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.isRunning()) {
            this.height += Math.abs(this.dx) / 3.0f * delta / 250.0f;
            this.height += Math.abs(this.dy) / 3.0f * delta / 250.0f;
            this.height = Math.min(300.0f, this.height);
        } else {
            this.height = 0.0f;
        }
    }

    @Override
    public boolean canPushMob(Mob other) {
        if (this.height > 80.0f) {
            return false;
        }
        return super.canPushMob(other);
    }

    protected GameTexture getTexture() {
        return MobRegistry.Textures.bird;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), this.getTexture(), i, 4, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd options;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(BirdMob.getTileCoordinate(x), BirdMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 24;
        int dir = this.getDir();
        drawY += level.getTile(BirdMob.getTileCoordinate(x), BirdMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        if (this.isRunning()) {
            float rotate = Math.min(45.0f, this.dx / 3.0f);
            int sprite = this.moveX < 0.0f ? 0 : 1;
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400) + 1;
            options = this.getTexture().initDraw().sprite(anim, sprite, 32).rotate(rotate, 16, 20).light(light).pos(drawX, drawY - (int)this.height);
        } else {
            int peckingFrame;
            long peckTimePassed = this.getWorldEntity().getLocalTime() - this.peckStartTime;
            int spriteY = dir % 2;
            int spriteX = 0;
            int anim = GameUtils.getAnim(peckTimePassed, peckingAnimationTimes);
            if (anim != -1 && (peckingFrame = peckingFrames[anim]) != -1) {
                spriteX = peckingFrame;
                spriteY += 2;
            }
            options = this.getTexture().initDraw().sprite(spriteX, spriteY, 32).light(light).pos(drawX, drawY);
        }
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        int shadowSprite = this.isAccelerating() ? (this.moveX < 0.0f ? 0 : 1) : dir % 2;
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.bird_shadow.initDraw().sprite(0, shadowSprite, 32).light(light).pos(drawX, drawY);
        if (this.height > 0.0f) {
            topList.add(tm -> shadow.draw());
        } else {
            tileList.add(tm -> shadow.draw());
        }
    }

    @Override
    public boolean canTakeDamage() {
        if (this.height > 80.0f) {
            return false;
        }
        return super.canTakeDamage();
    }

    @Override
    public Rectangle getSelectBox(int x, int y) {
        Rectangle selectBox = super.getSelectBox(x, y);
        selectBox.y = (int)((float)selectBox.y - this.height);
        return selectBox;
    }

    @Override
    public int getFlyingHeight() {
        if (this.isAccelerating()) {
            return (int)this.height;
        }
        return super.getFlyingHeight();
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        if (this.isFlying()) {
            return null;
        }
        return super.getLevelCollisionFilter();
    }

    @Override
    protected Stream<ModifierValue<?>> getRunningModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SPEED));
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameRandom.globalRandom.getOneOf(GameResources.birdAmbients)).volume(0.5f);
    }
}

