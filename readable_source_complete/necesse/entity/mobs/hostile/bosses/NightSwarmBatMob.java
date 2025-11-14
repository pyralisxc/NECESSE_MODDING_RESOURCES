/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.GameDifficulty;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmBatStage;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMap;

public class NightSwarmBatMob
extends FlyingBossMob {
    public int nightSwarmEventUniqueID;
    public int batIndex;
    public int shareHitCooldownUniqueID;
    public long shareHitCooldownDisabledTime;
    public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(8);
    public ParticleTypeSwitcher particleTypes = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    public final EmptyMobAbility disableShareCooldown;
    public float idleXPos;
    public float idleYPos;
    public int idleDistance = 100;
    public NightSwarmBatStage currentStage;
    public ArrayList<NightSwarmBatStage> stages = new ArrayList();
    public static GameDamage COLLISION_DAMAGE = new GameDamage(115.0f);

    public NightSwarmBatMob() {
        super((Integer)NightSwarmLevelEvent.BAT_MAX_HEALTH.get(GameDifficulty.CLASSIC));
        this.isSummoned = true;
        this.moveAccuracy = 10;
        this.setSpeed(100.0f);
        this.setFriction(2.0f);
        this.setArmor(40);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 40);
        this.disableShareCooldown = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                NightSwarmBatMob.this.shareHitCooldownDisabledTime = NightSwarmBatMob.this.getWorldEntity().getTime() + 5000L;
            }
        });
    }

    public NightSwarmLevelEvent getEvent() {
        if (this.getLevel() == null || this.nightSwarmEventUniqueID == 0) {
            return null;
        }
        LevelEvent levelEvent = this.getLevel().entityManager.events.get(this.nightSwarmEventUniqueID, false);
        if (levelEvent instanceof NightSwarmLevelEvent) {
            return (NightSwarmLevelEvent)levelEvent;
        }
        return null;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.nightSwarmEventUniqueID);
        writer.putNextInt(this.batIndex);
        writer.putNextInt(this.shareHitCooldownUniqueID);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.nightSwarmEventUniqueID = reader.getNextInt();
        this.batIndex = reader.getNextInt();
        this.shareHitCooldownUniqueID = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<NightSwarmBatMob>(this, new NightSwarmBatAI(), new FlyingAIMover());
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.currentStage != null) {
            this.currentStage.serverTick(this);
            if (this.currentStage.hasCompleted(this)) {
                this.currentStage.onCompletedOrRemoved(this, false);
                this.currentStage = null;
            }
        }
        if (this.currentStage == null && !this.stages.isEmpty()) {
            this.currentStage = this.stages.remove(0);
            this.currentStage.onStarted(this);
        }
        if (!(this.currentStage != null && !this.currentStage.idleAllowed || this.idleXPos == 0.0f && this.idleYPos == 0.0f || !this.hasArrivedAtTarget() && this.hasCurrentMovement())) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float distance = (0.7f + GameRandom.globalRandom.floatGaussian() * 0.3f) * (float)this.idleDistance;
            this.setMovement(new MobMovementLevelPos(this.idleXPos + dir.x * distance, this.idleYPos + dir.y * distance));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.particleTicks.gameTick();
        while (this.particleTicks.shouldTick()) {
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 5.0f, this.y + GameRandom.globalRandom.floatGaussian() * 5.0f, this.particleTypes.next()).movesConstant(this.dx / 2.0f + GameRandom.globalRandom.floatGaussian() * 4.0f, this.dy / 2.0f + GameRandom.globalRandom.floatGaussian() * 4.0f).color(new Color(7, 13, 24)).height(20.0f).lifeTime(500);
        }
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.nightswarmhurt).volume(0.2f).fallOffDistance(1500);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.nightswarmdeath).volume(0.6f).fallOffDistance(3000);
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return COLLISION_DAMAGE;
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        super.handleCollisionHit(target, damage, knockback);
        if (this.currentStage != null) {
            this.currentStage.onCollisionHit(this, target);
        }
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        if (this.currentStage != null) {
            this.currentStage.onWasHit(this, event);
        }
        this.shareHitCooldownDisabledTime = 0L;
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        if (this.currentStage != null) {
            this.currentStage.onCompletedOrRemoved(this, true);
        }
        for (NightSwarmBatStage stage : this.stages) {
            stage.onCompletedOrRemoved(this, true);
        }
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        NightSwarmLevelEvent event = this.getEvent();
        if (event != null) {
            if (attacker != null) {
                attacker.addAttackersToSet(event.attackers);
            }
            event.attackers.addAll(attackers);
            event.isDamagedByPlayers = event.isDamagedByPlayers || this.isDamagedByPlayers;
        }
    }

    public void clearStages() {
        if (this.currentStage != null) {
            this.currentStage.onCompletedOrRemoved(this, true);
        }
        for (NightSwarmBatStage stage : this.stages) {
            stage.onCompletedOrRemoved(this, true);
        }
        this.currentStage = null;
        this.stages.clear();
    }

    @Override
    public int getHitCooldownUniqueID() {
        if (this.shareHitCooldownUniqueID != 0 && this.shareHitCooldownDisabledTime < this.getWorldEntity().getTime()) {
            return this.shareHitCooldownUniqueID;
        }
        return super.getHitCooldownUniqueID();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.nightSwarmBat, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public int getFlyingHeight() {
        return 20;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(NightSwarmBatMob.getTileCoordinate(x), NightSwarmBatMob.getTileCoordinate(y)).minLevelCopy(100.0f);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 55;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        float bobbing = GameUtils.getBobbing(this.getWorldEntity().getTime(), 1000) * 5.0f;
        drawY = (int)((float)drawY + bobbing);
        final TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.nightSwarmBat.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY += level.getTile(NightSwarmBatMob.getTileCoordinate(x), NightSwarmBatMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public boolean isVisibleOnMap(Client client, ClientDiscoveredMap map) {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        int drawX = x - 16;
        int drawY = y - 26;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        float bobbing = GameUtils.getBobbing(this.getWorldEntity().getTime(), 1000) * 5.0f;
        drawY = (int)((float)drawY + bobbing);
        MobRegistry.Textures.nightSwarmBat.initDraw().sprite(sprite.x, sprite.y, 64).size(32).draw(drawX, drawY += this.getLevel().getTile(NightSwarmBatMob.getTileCoordinate(x), NightSwarmBatMob.getTileCoordinate(y)).getMobSinkingAmount(this));
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-10, -24, 20, 28);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 300), dir);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.POISON_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.FROST_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)));
    }

    public static class NightSwarmBatAI<T extends NightSwarmBatMob>
    extends SelectorAINode<T> {
        @Override
        public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            blackboard.onEvent("refreshBossDespawn", event -> {
                NightSwarmLevelEvent nightSwarmEvent = mob.getEvent();
                if (nightSwarmEvent != null) {
                    nightSwarmEvent.despawnTimer = 0;
                }
            });
            super.onRootSet(root, mob, blackboard);
        }
    }
}

