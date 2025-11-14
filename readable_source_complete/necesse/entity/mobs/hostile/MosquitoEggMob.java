/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.tween.Playable;
import necesse.engine.util.tween.PlayableSequence;
import necesse.engine.util.tween.ShakeTween;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderShooterAINode;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.MosquitoMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MosquitoEggMob
extends HostileMob {
    public static LootTable lootTable = new LootTable();
    public final double hatchTime = 3000.0;
    public final double removeTime = 5000.0;
    public final int searchDistance = 320;
    private final PlayableSequence hatchAnimation = new PlayableSequence();
    protected EmptyMobAbility hatchAbility;
    protected long triggeredTime = 0L;
    protected boolean hatched = false;
    protected Point2D.Double spriteOffset = new Point2D.Double(0.0, 0.0);
    protected int amountToSpawn;

    public MosquitoEggMob() {
        super(400);
        this.collision = new Rectangle(-13, -12, 26, 20);
        this.hitBox = new Rectangle(-25, -24, 50, 38);
        this.selectBox = new Rectangle(-30, -45, 60, 60);
        this.setKnockbackModifier(0.0f);
        this.setArmor(30);
        this.setSpeed(0.0f);
        this.setupHatchAnimation();
        this.hatchAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                MosquitoEggMob.this.triggeredTime = MosquitoEggMob.this.getTime();
                MosquitoEggMob.this.hatchAnimation.play(MosquitoEggMob.this.triggeredTime, MosquitoEggMob.this.getTime());
            }
        });
    }

    private void setupHatchAnimation() {
        double amplitude = 4.0;
        this.hatchAnimation.addAt(0.0, (Playable<?>)new ShakeTween(500.0, 0.06, amplitude, 0.4, this.spriteOffset).onPlay(this::playShakeSound)).addAt(1500.0, (Playable<?>)new ShakeTween(500.0, 0.06, amplitude, 0.4, this.spriteOffset).onPlay(this::playShakeSound)).addAt(2750.0, (Playable<?>)((ShakeTween)new ShakeTween(250.0, 0.06, amplitude, 0.4, this.spriteOffset).setFadeOut(false).onPlay(this::playShakeSound)).onComplete(() -> {
            this.hatched = true;
            this.spawnDeathParticles(0.0f, 0.0f);
            this.playDeathSound();
        })).addAfterPrevious(new ShakeTween(500.0, 0.06, amplitude, 0.4, this.spriteOffset));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.triggeredTime);
        writer.putNextBoolean(this.hatched);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.triggeredTime = reader.getNextLong();
        this.hatched = reader.getNextBoolean();
        if (this.triggeredTime > 0L) {
            this.hatchAnimation.play(this.triggeredTime, this.getTime());
        }
    }

    @Override
    public void init() {
        super.init();
        this.amountToSpawn = new GameRandom(GameObject.getTileSeed(this.getTileX(), this.getTileY())).getIntBetween(3, 6);
        this.ai = new BehaviourTreeAI<MosquitoEggMob>(this, new TargetFinderShooterAINode<MosquitoEggMob>(320){

            @Override
            public boolean canAttack(MosquitoEggMob mob) {
                return MosquitoEggMob.this.triggeredTime == 0L;
            }

            @Override
            public Stream<Mob> streamTargets(MosquitoEggMob mob, int shootDistance) {
                return TargetFinderShooterAINode.streamPlayersAndHumans(mob, shootDistance);
            }

            @Override
            public void shootTarget(MosquitoEggMob mob, Mob target) {
                MosquitoEggMob.this.hatchAbility.runAndSend();
            }
        });
    }

    @Override
    protected void addDrawables(List<MobDrawable> mobList, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(mobList, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(MosquitoEggMob.getTileCoordinate(x), MosquitoEggMob.getTileCoordinate(y));
        this.hatchAnimation.update(this.getTime());
        int sprite = this.hatched ? 1 : 0;
        int drawX = camera.getDrawX(x) - 32 + (int)this.spriteOffset.x;
        int drawY = camera.getDrawY(y) - 43 + (int)this.spriteOffset.y;
        final TextureDrawOptionsEnd options = MobRegistry.Textures.mosquitoEgg.initDraw().spriteSection(sprite, 0, 64, 0, 64, 0, 64, false).rotate(0.0f, 32, 40).light(light).pos(drawX, drawY);
        mobList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.mosquitoEgg, i, 2, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    private void playShakeSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(this).volume(0.6f).pitch(pitch));
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (!this.hatched && this.triggeredTime > 0L && (double)this.getTime() > (double)this.triggeredTime + 3000.0) {
            for (int i = 0; i < this.amountToSpawn; ++i) {
                MosquitoMob mosquito = new MosquitoMob();
                this.getLevel().entityManager.addMob(mosquito, this.getX(), this.getY());
            }
            this.hatched = true;
        }
        if (this.triggeredTime > 0L && (double)this.getTime() > (double)this.triggeredTime + 5000.0) {
            this.setHealth(0);
        }
    }
}

