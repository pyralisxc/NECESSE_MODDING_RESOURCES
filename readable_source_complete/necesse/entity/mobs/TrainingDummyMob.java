/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketShowDPS;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.DPSTracker;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.TrainingDummyObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class TrainingDummyMob
extends Mob {
    private boolean isSnowman;
    public DPSTracker trainingDummyDPSTracker = new DPSTracker();
    private int aliveTimer;

    public TrainingDummyMob() {
        super(Integer.MAX_VALUE);
        this.setArmor(0);
        this.setSpeed(0.0f);
        this.setFriction(1000.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-18, -15, 36, 30);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.shouldSave = false;
        this.aliveTimer = 20;
        this.isStatic = true;
    }

    public TrainingDummyMob(boolean isSnowman) {
        this();
        this.isSnowman = isSnowman;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isSnowman);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isSnowman = reader.getNextBoolean();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickAlive();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        long currentTime = this.getWorldEntity().getTime();
        this.trainingDummyDPSTracker.tick(currentTime);
        if (this.getLevel().tickManager().isFirstGameTickInSecond() && this.trainingDummyDPSTracker.isLastHitBeforeReset(currentTime)) {
            float dps = this.trainingDummyDPSTracker.getDPS(currentTime);
            if (this.isServer()) {
                this.getLevel().getServer().network.sendToClientsWithEntity(new PacketShowDPS(this.getUniqueID(), dps), this);
            }
        }
        this.tickAlive();
    }

    @Override
    public boolean canBeTargetedFromAdjacentTiles() {
        return true;
    }

    private void tickAlive() {
        this.setHealthHidden(this.getMaxHealth());
        --this.aliveTimer;
        if (this.aliveTimer <= 0) {
            this.remove();
        }
    }

    public void keepAlive(TrainingDummyObjectEntity entity) {
        this.aliveTimer = 20;
        this.setPos(entity.tileX * 32 + 16, entity.tileY * 32 + 16, true);
    }

    @Override
    public void playHitSound() {
        if (this.isSnowman) {
            SoundManager.playSound(GameResources.snowBallHit, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 1.1f)));
            SoundManager.playSound(GameResources.blunthit, (SoundEffect)SoundEffect.effect(this).volume(0.5f).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
        } else {
            SoundManager.playSound(GameResources.blunthit, (SoundEffect)SoundEffect.effect(this).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
        }
    }

    @Override
    public void playHitDeathSound() {
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    protected int getDrawSortY(Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective, boolean fromMount) {
        return this.getTileY() * 32 + 20;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public boolean canTakeDamage() {
        return true;
    }

    @Override
    public boolean countDamageDealt() {
        return false;
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public MobWasHitEvent isHit(MobWasHitEvent event, Attacker attacker) {
        if (this.getLevel() != null) {
            this.getLevel().forceGrassWeave(this.getTileX(), this.getTileY(), 200);
        }
        return super.isHit(event, attacker);
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        this.setHealthHidden(this.getMaxHealth());
        if (this.isClient() && this.isSnowman) {
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 3.0f, this.y + GameRandom.globalRandom.nextFloat() * -4.0f, Particle.GType.CRITICAL).movesConstant(GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f), -16.0f).sizeFades(14, 34).lifeTime(600).heightMoves(10.0f, 20.0f).color(new Color(219, 252, 255));
        }
    }

    @Override
    public boolean canGiveResilience(Attacker attacker) {
        PlayerMob attackOwner;
        if (attacker != null && (attackOwner = attacker.getFirstPlayerOwner()) != null) {
            return !attackOwner.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY);
        }
        return super.canGiveResilience(attacker);
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        int afterHealth;
        int beforeHealth = this.getHealth();
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
        if (this.getLevel() != null && (afterHealth = this.getHealth()) < beforeHealth) {
            Mob attackOwner;
            int delta = beforeHealth - afterHealth;
            this.trainingDummyDPSTracker.addHit(this.getWorldEntity().getTime(), delta);
            if (this.isServer() && attacker != null && (attackOwner = attacker.getAttackOwner()) != null && attackOwner.isPlayer) {
                ServerClient serverClient = ((PlayerMob)attackOwner).getServerClient();
                serverClient.trainingDummyDPSTracker.addHit(this.getWorldEntity().getTime(), delta);
            }
        }
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug) {
            return false;
        }
        return super.onMouseHover(camera, perspective, debug);
    }

    @Override
    public float getArmorAfterPen(float armorPen) {
        return this.getArmor() - armorPen;
    }
}

