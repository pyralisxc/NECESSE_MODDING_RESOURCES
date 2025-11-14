/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.tween.Easings;
import necesse.engine.util.tween.FloatTween;
import necesse.engine.util.tween.IntTween;
import necesse.engine.util.tween.Playable;
import necesse.engine.util.tween.PlayableSequence;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CustomMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.StationaryPlayerShooterAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.FlamelingShooterProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FlamelingShooterMob
extends HostileMob {
    public static GameDamage damage = new GameDamage(70.0f);
    protected long sequenceTime = -1L;
    protected PlayableSequence sequence = new PlayableSequence();
    protected FloatTween enteredTween = new FloatTween(0.0f);
    protected IntTween frameCountTween = new IntTween(0);
    protected Item orbItem;
    protected FloatTween orbEntranceTween = new FloatTween(0.0f);
    protected FloatTween orbHoveringTween;
    protected FloatTween orbPullBackTween = new FloatTween(0.0f);
    protected CustomMobAbility targetAcquiredAbility;
    protected Mob target;
    protected boolean canAttack = true;

    public FlamelingShooterMob() {
        super(200);
        this.isStatic = true;
        this.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(150, Integer.MAX_VALUE);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.targetAcquiredAbility = this.registerAbility(new CustomMobAbility(){

            @Override
            protected void run(Packet content) {
                PacketReader reader = new PacketReader(content);
                int uniqueID = reader.getNextInt();
                FlamelingShooterMob.this.target = GameUtils.getLevelMob(uniqueID, this.getMob().getLevel());
                FlamelingShooterMob.this.sequenceTime = reader.getNextLong();
                FlamelingShooterMob.this.sequence.play(FlamelingShooterMob.this.sequenceTime, FlamelingShooterMob.this.getTime());
            }
        });
    }

    @Override
    public void init() {
        super.init();
        if (!this.isServer()) {
            this.orbItem = ItemRegistry.getItem("flamelingorb");
        }
        this.setupAnimations();
        if (this.isClient() && this.target != null) {
            this.sequence.play(this.sequenceTime, this.getTime());
        }
        this.ai = new BehaviourTreeAI<FlamelingShooterMob>(this, new StationaryPlayerShooterAI<FlamelingShooterMob>(320){

            @Override
            public void shootTarget(FlamelingShooterMob mob, Mob target) {
                Packet content = new Packet();
                PacketWriter writer = new PacketWriter(content);
                writer.putNextInt(target.getUniqueID());
                writer.putNextLong(FlamelingShooterMob.this.getTime());
                FlamelingShooterMob.this.targetAcquiredAbility.runAndSend(content);
                FlamelingShooterMob.this.canAttack = false;
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.sequenceTime);
        writer.putNextInt(this.target != null ? this.target.getUniqueID() : -1);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.sequenceTime = reader.getNextLong();
        int targetID = reader.getNextInt();
        if (targetID != -1) {
            this.target = GameUtils.getLevelMob(targetID, this.getLevel());
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.sequence.update(this.getTime());
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
        this.sequence.update(this.getTime());
        if (this.getLevel().getTileID(this.getTileX(), this.getTileY()) != TileRegistry.lavaID) {
            this.remove();
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 48;
        this.sequence.update(this.getTime());
        this.orbHoveringTween.update(this.getTime());
        GameTexture texture = MobRegistry.Textures.flamelingShooter;
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(-GameMath.lerp(((Float)this.enteredTween.getValue()).floatValue(), -3.0f, -1.0f));
        Point2D.Float direction = this.target != null ? GameMath.normalize(this.target.x - (float)x, this.target.y - (float)y) : new Point2D.Float(this.getDirVector().x, this.getDirVector().y);
        this.setFacingDir(direction.x, direction.y);
        int spriteY = this.getDir();
        final TextureDrawOptionsEnd throwOptions = texture.initDraw().sprite((Integer)this.frameCountTween.getValue(), spriteY, 64).light(light).addMaskShader(swimMask).pos(drawX, drawY);
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.caveling_shadow.initDraw().sprite(0, 0, 64).light(light).alpha(((Float)this.enteredTween.getValue()).floatValue() * 0.5f).size(60, 50).posMiddle(drawX + 32, drawY + 45);
        tileList.add(tm -> shadow.draw());
        Point2D.Float orbDirectionOffset = new Point2D.Float(0.0f, 0.0f);
        switch (this.getDir()) {
            case 1: {
                orbDirectionOffset.x = 5.0f;
                break;
            }
            case 2: {
                orbDirectionOffset.y = 25.0f;
                break;
            }
            case 3: {
                orbDirectionOffset.x = -5.0f;
            }
        }
        float orbPullBack = -((Float)this.orbPullBackTween.getValue()).floatValue() * 20.0f;
        final TextureDrawOptionsEnd itemOptions = this.orbItem != null ? this.orbItem.getItemSprite(null, null).initDraw().light(light.minLevelCopy(150.0f)).size((int)(32.0f * ((Float)this.orbEntranceTween.getValue()).floatValue())).alpha(((Float)this.orbEntranceTween.getValue()).floatValue()).posMiddle((int)((double)(drawX + 32 - 2) + (double)orbPullBack * direction.getX() + (double)orbDirectionOffset.x), (int)((double)((float)(drawY + 19) + ((Float)this.orbHoveringTween.getValue()).floatValue()) + (double)orbPullBack * direction.getY() + (double)orbDirectionOffset.y)) : null;
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                throwOptions.draw();
                if (itemOptions != null) {
                    itemOptions.draw();
                }
            }
        });
    }

    @Override
    public boolean canAttack() {
        return this.canAttack;
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.flick, (SoundEffect)SoundEffect.effect(this).pitch(1.2f));
        }
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
        return location.checkNotLevelCollides().checkTile((tileX, tileY) -> {
            int tileID = this.getLevel().getTileID((int)tileX, (int)tileY);
            return tileID == TileRegistry.lavaID;
        }).checkMaxMobsAround(1, 15, mob -> mob instanceof FlamelingShooterMob, null);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return DeathMessageTable.fromRange("flamelingshooter", 3);
    }

    @Override
    public GameMessage getLocalization() {
        return MobRegistry.getLocalization("flameling");
    }

    protected void setupAnimations() {
        this.orbHoveringTween = (FloatTween)new FloatTween(1000.0, 0.0f, 10.0f).setLoops(Playable.LoopType.Yoyo, -1, 0.0);
        this.orbHoveringTween.play(this.getTime(), this.getTime());
        this.sequence.addAfterPrevious((Playable<?>)((FloatTween)this.enteredTween.newTween(250.0, Float.valueOf(1.0f)).setEase(Easings.BackOut)).onStart(() -> {
            if (this.isClient()) {
                this.spawnEntranceParticles();
                SoundManager.playSound(GameResources.flamelingShooterPopIn, (SoundEffect)SoundEffect.effect(this).pitch(GameRandom.globalRandom.getFloatBetween(1.1f, 1.3f)));
            }
            this.frameCountTween.setValue(1);
        }));
        this.sequence.addAfterPrevious((Playable<?>)((FloatTween)this.orbEntranceTween.newTween(1000.0, Float.valueOf(1.0f)).setEase(Easings.CircIn)).onPercent(0.55f, forwards -> this.frameCountTween.setValue(2)));
        this.sequence.addAfterPrevious((Playable<?>)this.frameCountTween.newTween(500.0, 4).onStart(() -> this.frameCountTween.setInitialValue(3)), 500.0);
        this.sequence.addAtTheSameTime((Playable<?>)((FloatTween)((FloatTween)this.orbPullBackTween.newTween(500.0, Float.valueOf(1.0f)).setEase(Easings.BackOut)).onPercent(0.55f, forwards -> {
            if (this.isClient()) {
                SoundManager.playSound(GameResources.fireShot, (SoundEffect)SoundEffect.effect(this).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f)));
            }
        })).onComplete(() -> this.shootParticle(this.target)), 100.0);
        this.sequence.addAfterPrevious((Playable<?>)this.orbPullBackTween.newTween(200.0, Float.valueOf(0.0f)).setEase(Easings.CubicIn), -75.0);
        this.sequence.addAtTheSameTime(this.frameCountTween.newTween(150.0, 5));
        this.sequence.addAtTheSameTime((Playable<?>)this.orbEntranceTween.newTween(50.0, Float.valueOf(0.0f)).setEase(Easings.CubicOut), 150.0);
        this.sequence.addAfterPrevious((Playable<?>)((FloatTween)this.enteredTween.newTween(1000.0, Float.valueOf(0.0f)).onStart(() -> this.frameCountTween.setValue(1))).setEase(Easings.BackIn), 250.0);
        this.sequence.onComplete(this::remove);
    }

    protected void shootParticle(Mob target) {
        if (this.isServer()) {
            FlamelingShooterProjectile projectile = new FlamelingShooterProjectile(this.x, this.y, 0.0f, 70.0f, 800, damage, this);
            projectile.setTargetPrediction(target, -20.0f);
            projectile.setLevel(this.getLevel());
            projectile.moveDist(20.0);
            this.getLevel().entityManager.projectiles.add(projectile);
        }
    }

    protected void spawnEntranceParticles() {
        int particleCount = GameRandom.globalRandom.getIntBetween(5, 10);
        for (int i = 0; i < particleCount; ++i) {
            float xOffset = (float)(GameRandom.globalRandom.nextGaussian() * 6.0);
            this.getLevel().entityManager.addParticle(this.x + xOffset, this.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0) + 15.0f, Particle.GType.IMPORTANT_COSMETIC).heightMoves(0.0f, 20.0f, 100.0f, 0.1f, 0.0f, 0.0f).movesFriction(xOffset, 0.0f, 0.1f).flameColor().givesLight(0.0f, 0.5f).lifeTime(1000);
        }
    }
}

