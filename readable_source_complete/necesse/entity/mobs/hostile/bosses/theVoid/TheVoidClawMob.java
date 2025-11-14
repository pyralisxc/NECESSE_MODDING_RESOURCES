/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.theVoid;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.tween.Easings;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.TheVoidClawGroundShatterGroundEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheVoidClawBeamLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TheVoidClawMob
extends FlyingBossMob {
    public final LevelMob<TheVoidMob> master = new LevelMob();
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    public int clawIndex;
    public boolean leftHanded;
    protected Runnable onArrived;
    protected int maxArrivedTime;
    protected long nextIdleMovementChangeTime;
    protected long slamAnimationStartTime;
    protected int slamAnimationDuration = 500;
    protected int slamAnimationWaitTime;
    protected int slamRiseTime = 2000;
    protected boolean isSlamClenched;
    protected boolean slamEventTriggered;
    public final StartSlamAbility startSlamAbility;
    protected long beamStartTime;
    protected int beamDuration;
    public final StartBeamAbility startBeamAbility;

    public TheVoidClawMob() {
        super(10);
        this.isSummoned = true;
        this.dropsLoot = false;
        this.moveAccuracy = 40;
        this.collision = new Rectangle(-45, -60, 90, 90);
        this.hitBox = new Rectangle(-50, -65, 100, 100);
        this.selectBox = new Rectangle(-60, -85, 120, 120);
        this.setKnockbackModifier(0.0f);
        this.setRegen(0.0f);
        this.setSpeed(250.0f);
        this.setFriction(2.0f);
        this.startSlamAbility = this.registerAbility(new StartSlamAbility());
        this.startBeamAbility = this.registerAbility(new StartBeamAbility());
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.master.uniqueID);
        writer.putNextBoolean(this.leftHanded);
        writer.putNextInt(this.clawIndex);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.master.uniqueID = reader.getNextInt();
        this.leftHanded = reader.getNextBoolean();
        this.clawIndex = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.countStats = false;
    }

    public Point getClawBasePositionOffset() {
        int xOffset = 220 + this.clawIndex * 75;
        return new Point(this.leftHanded ? xOffset : -xOffset, 250 - this.clawIndex * 75);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickMaster();
        TheVoidMob master = this.master.get(this.getLevel());
        float alpha = master == null ? 1.0f : master.getFadeAlpha();
        alpha = Math.min(master == null ? 1.0f : 1.0f - master.getFadeWhiteness(), alpha);
        for (int i = 0; i < 5; ++i) {
            int xOffset = GameRandom.globalRandom.getIntBetween(-50, 50);
            this.getLevel().entityManager.addParticle(this.x + (float)xOffset, this.y + (float)GameRandom.globalRandom.getIntBetween(-30, 30), this.particleTypeSwitcher.next()).height(this.getCurrentHeight() + 75.0f).sprite(GameResources.voidPuffParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 12)).sizeFades(24, 48).color(1.0f, 1.0f, 1.0f, alpha).movesFriction(GameRandom.globalRandom.getIntBetween(-5, 5), GameRandom.globalRandom.getIntBetween(-30, -60) + Math.abs(xOffset), 0.8f).lifeTime(1000);
        }
    }

    @Override
    public void serverTick() {
        TheVoidMob master;
        super.serverTick();
        this.movementUpdateTime = this.getTime();
        this.healthUpdateTime = this.getTime();
        this.tickMaster();
        boolean checkMaxArrivedTime = false;
        if (this.maxArrivedTime > 0) {
            checkMaxArrivedTime = true;
            this.maxArrivedTime -= 50;
        }
        if (this.onArrived != null && this.slamAnimationStartTime == 0L && this.beamStartTime == 0L && (this.hasArrivedAtTarget() || checkMaxArrivedTime && this.maxArrivedTime <= 0)) {
            this.onArrived.run();
            this.onArrived = null;
        }
        if (this.isIdle() && this.nextIdleMovementChangeTime <= this.getTime() && (master = this.master.get(this.getLevel())) != null && !master.isInDeathAnimation()) {
            Point baseOffset = this.getClawBasePositionOffset();
            if (GameRandom.globalRandom.nextBoolean()) {
                Point randomOffset = new Point(GameRandom.globalRandom.getIntBetween(-35, 35), GameRandom.globalRandom.getIntBetween(-35, 35));
                this.setMovement(new MobMovementRelative(master, baseOffset.x + randomOffset.x, baseOffset.y + randomOffset.y));
                this.nextIdleMovementChangeTime = this.getTime() + 1000L;
            } else {
                int radius = GameRandom.globalRandom.getIntBetween(30, 50);
                float speed = MobMovementCircle.convertToRotSpeed(radius, this.getSpeed());
                this.setMovement(new MobMovementCircleRelative((Mob)this, (Mob)master, baseOffset.x, baseOffset.y, radius, speed / 4.0f, GameRandom.globalRandom.nextBoolean()));
                this.nextIdleMovementChangeTime = this.getTime() + 2000L;
            }
        }
    }

    public void flyToPositionAndSlam(float levelX, float levelY, int duration, int waitTime, int riseTime, boolean isSlamClenched) {
        this.setMovement(new MobMovementLevelPos(levelX, levelY));
        this.maxArrivedTime = 0;
        this.onArrived = () -> {
            this.stopMoving();
            this.startSlamAbility.runAndSend(duration, waitTime, riseTime, isSlamClenched);
        };
    }

    public void flyToTargetAndSlam(Mob target, float relativeX, float relativeY, int maxFlyTime, int duration, int waitTime, int riseTime, boolean isSlamClenched) {
        this.setMovement(new MobMovementRelative(target, relativeX, relativeY));
        this.maxArrivedTime = maxFlyTime;
        this.onArrived = () -> {
            this.stopMoving();
            this.startSlamAbility.runAndSend(duration, waitTime, riseTime, isSlamClenched);
        };
    }

    public void flyToTargetAndSlam(int posX, int posY, int maxFlyTime, int duration, int waitTime, int riseTime, boolean isSlamClenched) {
        this.setMovement(new MobMovementLevelPos(posX, posY));
        this.maxArrivedTime = maxFlyTime;
        this.onArrived = () -> {
            this.stopMoving();
            this.startSlamAbility.runAndSend(duration, waitTime, riseTime, isSlamClenched);
        };
    }

    public void flyToTargetAndSlam(Mob target, int maxFlyTime, int duration, int waitTime, int riseTime, boolean isSlamClenched) {
        this.flyToTargetAndSlam(target, 0.0f, 0.0f, maxFlyTime, duration, waitTime, riseTime, isSlamClenched);
    }

    @Override
    public void tickMovement(float delta) {
        long timeSinceStart;
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null && master.isInDeathAnimation()) {
            this.dx = 0.0f;
            this.dy = 0.0f;
            this.stopMoving();
        }
        if (this.slamAnimationStartTime != 0L) {
            timeSinceStart = this.getTime() - this.slamAnimationStartTime;
            if (master != null && master.isInDeathAnimation()) {
                long timeSinceDeathStart = this.getTime() - master.deathAnimationStartTime;
                timeSinceStart -= timeSinceDeathStart;
            }
            if (!this.slamEventTriggered && timeSinceStart > (long)this.slamAnimationDuration) {
                if (!this.isClient()) {
                    FlyingBossMob owner = this;
                    if (master != null) {
                        owner = master;
                    }
                    if (this.isSlamClenched) {
                        TheVoidClawGroundShatterGroundEvent e = new TheVoidClawGroundShatterGroundEvent(owner, this.getX(), this.getY(), TheVoidMob.clawShatterDamage, 200, 500, GameRandom.globalRandom);
                        this.getLevel().entityManager.events.add(e);
                    }
                } else {
                    if (this.isClient()) {
                        CameraShake cameraShake = this.getClient().startCameraShake(this.x, this.y, 200, 40, 2.0f, 2.0f, true);
                        cameraShake.minDistance = 200;
                        cameraShake.listenDistance = 2000;
                    }
                    SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.8f).pitch(0.8f).falloffDistance(1400));
                    SoundManager.playSound(GameResources.heavyHammer, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(1.2f).pitch(0.4f).falloffDistance(1400));
                    for (int i = 0; i < 20; ++i) {
                        int lifeTime = GameRandom.globalRandom.getIntBetween(500, 3000);
                        float lifePerc = (float)lifeTime / 2000.0f;
                        float startHeight = 0.0f;
                        float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(30, 60) * lifePerc;
                        int angle = GameRandom.globalRandom.nextInt(360);
                        Point2D.Float dir = GameMath.getAngleDir(angle);
                        this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), this.y + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), Particle.GType.IMPORTANT_COSMETIC).sizeFades(20, 30).movesFriction(dir.x * GameRandom.globalRandom.getFloatBetween(30.0f, 50.0f), dir.y * GameRandom.globalRandom.getFloatBetween(30.0f, 50.0f), 0.5f).heightMoves(startHeight, height).colorRandom(310.0f, 0.95f, 0.95f, 10.0f, 0.05f, 0.05f).lifeTime(lifeTime);
                    }
                }
                this.slamEventTriggered = true;
            }
            if (timeSinceStart <= (long)(this.slamAnimationDuration + this.slamAnimationWaitTime)) {
                this.dx = 0.0f;
                this.dy = 0.0f;
                this.colDx = 0.0f;
                this.colDy = 0.0f;
            }
            if (timeSinceStart > (long)(this.slamAnimationDuration + this.slamAnimationWaitTime + this.slamRiseTime)) {
                this.slamAnimationStartTime = 0L;
            }
        }
        if (this.beamStartTime != 0L && (timeSinceStart = this.getTime() - this.beamStartTime) > (long)this.beamDuration) {
            this.beamStartTime = 0L;
        }
        super.tickMovement(delta);
    }

    public boolean isIdle() {
        long timeSinceStart;
        if (this.slamAnimationStartTime != 0L && (timeSinceStart = this.getTime() - this.slamAnimationStartTime) <= (long)(this.slamAnimationDuration + this.slamAnimationWaitTime)) {
            return false;
        }
        return this.beamStartTime == 0L && this.onArrived == null;
    }

    public float getCurrentHeight() {
        long startTime = this.getTime();
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null && master.isInDeathAnimation()) {
            startTime = master.deathAnimationStartTime;
        }
        float bobbing = GameUtils.getAnimFloatContinuous(startTime, 2000) * 5.0f;
        float height = 100.0f + bobbing;
        float addedHeight = 0.0f;
        if (master != null && master.isInDeathAnimation()) {
            int timeSinceDeathAnimationStart = (int)(this.getTime() - master.deathAnimationStartTime);
            float totalHeightIncreaseOnDeath = 150.0f;
            float deathProgress = (float)timeSinceDeathAnimationStart / (float)master.totalDeathAnimationDuration;
            addedHeight = GameMath.lerp(deathProgress, 0.0f, totalHeightIncreaseOnDeath);
        }
        if (this.slamAnimationStartTime != 0L) {
            float progress;
            long timeSinceSlamStart = startTime - this.slamAnimationStartTime;
            if (timeSinceSlamStart < (long)this.slamAnimationDuration) {
                progress = (float)timeSinceSlamStart / (float)this.slamAnimationDuration;
                float ease = Easings.BackIn.ease(progress);
                if (ease < 0.0f) {
                    ease *= 2.0f;
                }
                return GameMath.lerp(ease, height, 0.0f) + addedHeight;
            }
            if ((timeSinceSlamStart -= (long)this.slamAnimationDuration) < (long)this.slamAnimationWaitTime) {
                return addedHeight;
            }
            if ((timeSinceSlamStart -= (long)this.slamAnimationWaitTime) < (long)this.slamRiseTime) {
                progress = (float)timeSinceSlamStart / (float)this.slamRiseTime;
                return GameMath.lerp(Easings.CubicIn.ease(progress), 0.0f, height) + addedHeight;
            }
        }
        return height + addedHeight;
    }

    @Override
    public int getHealth() {
        TheVoidMob head;
        if (this.master != null && (head = this.master.get(this.getLevel())) != null) {
            return head.getHealth();
        }
        return super.getHealth();
    }

    @Override
    public int getMaxHealth() {
        TheVoidMob head;
        if (this.master != null && (head = this.master.get(this.getLevel())) != null) {
            return head.getMaxHealth();
        }
        return super.getMaxHealth();
    }

    public void tickMaster() {
        if (this.removed()) {
            return;
        }
        TheVoidMob master = this.master.get(this.getLevel());
        if (master == null) {
            this.remove();
        } else {
            this.setMaxHealth(master.getMaxHealth());
            this.setHealthHidden(master.getHealth(), 0.0f, 0.0f, null);
            this.setArmor(master.getArmorFlat() * 2);
        }
        this.setSpeed(GameMath.lerp(this.getHealthPercent(), 325, 250));
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        return this.getFlyingHeight() < 50 && super.canBeTargeted(attacker, attackerClient);
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        return this.getFlyingHeight() < 50 && super.canBeHit(attacker);
    }

    public void teleportToMasterIfIdle() {
        if (!this.isIdle()) {
            return;
        }
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null) {
            Point offset = this.getClawBasePositionOffset();
            this.setPos(master.x + (float)offset.x, master.y + (float)offset.y, true);
            this.sendMovementPacket(true);
            this.dx = 0.0f;
            this.dy = 0.0f;
        }
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        if (this.master != null) {
            this.master.computeIfPresent(this.getLevel(), m -> m.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate));
        }
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public float getIncomingDamageModifier() {
        TheVoidMob master = this.master.get(this.getLevel());
        return master == null ? super.getIncomingDamageModifier() : master.getIncomingDamageModifier();
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        long timeSinceStart;
        if (fromPacket) {
            return TheVoidMob.clawCollisionDamage;
        }
        if (this.slamAnimationStartTime != 0L && (timeSinceStart = this.getTime() - this.slamAnimationStartTime) >= (long)this.slamAnimationDuration && timeSinceStart <= (long)(this.slamAnimationDuration + this.slamAnimationWaitTime)) {
            return TheVoidMob.clawCollisionDamage;
        }
        return super.getCollisionDamage(target, fromPacket, packetSubmitter);
    }

    @Override
    public int getFlyingHeight() {
        return (int)this.getCurrentHeight();
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    @Override
    public Rectangle getSelectBox(int x, int y) {
        Rectangle selectBox = super.getSelectBox(x, y);
        selectBox.y -= this.getFlyingHeight();
        return selectBox;
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(TheVoidClawMob.getTileCoordinate(x), TheVoidClawMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 144 + (this.leftHanded ? -10 : 10);
        int drawY = camera.getDrawY(y) - 144 - 35;
        drawY -= this.getFlyingHeight();
        float rotate = GameMath.limit(this.dx / 10.0f, -10.0f, 10.0f);
        int spriteY = 0;
        if (this.slamAnimationStartTime != 0L) {
            spriteY = 2;
        } else if (this.beamStartTime != 0L) {
            spriteY = 3;
        }
        TheVoidMob master = this.master.get(this.getLevel());
        float alpha = master != null ? master.getFadeAlpha() : 1.0f;
        final float whiteness = master != null ? master.getFadeWhiteness() : 0.0f;
        alpha = Math.max(alpha, whiteness);
        final TextureDrawOptionsEnd clawOptions = MobRegistry.Textures.theVoidClaw.initDraw().sprite(0, spriteY, 288, 288).mirror(this.leftHanded, false).rotate(rotate).alpha(alpha).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                try {
                    GameResources.whiteShader.use();
                    GameResources.whiteShader.pass1f("white", whiteness);
                    clawOptions.draw();
                }
                finally {
                    GameResources.whiteShader.stop();
                }
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_big_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public Mob getAttackOwner() {
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null) {
            return master;
        }
        return super.getAttackOwner();
    }

    @Override
    public GameMessage getAttackerName() {
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getAttackerName();
        }
        return super.getAttackerName();
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        TheVoidMob master = this.master.get(this.getLevel());
        if (master != null) {
            return ((Mob)master).getDeathMessages();
        }
        return super.getDeathMessages();
    }

    public class StartSlamAbility
    extends MobAbility {
        public void runAndSend(int duration, int waitTime, int riseTime, boolean isSlamClenched) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(duration);
            writer.putNextInt(waitTime);
            writer.putNextInt(riseTime);
            writer.putNextBoolean(isSlamClenched);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            TheVoidClawMob.this.collisionHitCooldowns.resetCooldowns();
            TheVoidClawMob.this.slamAnimationStartTime = TheVoidClawMob.this.getTime();
            TheVoidClawMob.this.slamAnimationDuration = reader.getNextInt();
            TheVoidClawMob.this.slamAnimationWaitTime = reader.getNextInt();
            TheVoidClawMob.this.slamRiseTime = reader.getNextInt();
            TheVoidClawMob.this.isSlamClenched = reader.getNextBoolean();
            TheVoidClawMob.this.slamEventTriggered = false;
        }
    }

    public class StartBeamAbility
    extends MobAbility {
        public void runAndSend(int attackSeed, float startAngleOffset, float endAngleOffset, int duration, boolean reversed) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(attackSeed);
            writer.putNextFloat(startAngleOffset);
            writer.putNextFloat(endAngleOffset);
            writer.putNextInt(duration);
            writer.putNextBoolean(reversed);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            TheVoidMob master;
            TheVoidClawMob.this.beamStartTime = TheVoidClawMob.this.getTime();
            int attackSeed = reader.getNextInt();
            float startAngleOffset = reader.getNextFloat();
            float endAngleOffset = reader.getNextFloat();
            TheVoidClawMob.this.beamDuration = reader.getNextInt();
            boolean reversed = reader.getNextBoolean();
            float startAngle = 90.0f - (reversed ? -startAngleOffset : startAngleOffset);
            float endAngle = 90.0f + (reversed ? -endAngleOffset : endAngleOffset);
            TheVoidClawBeamLevelEvent event = new TheVoidClawBeamLevelEvent(TheVoidClawMob.this, startAngle, endAngle, TheVoidClawMob.this.beamStartTime, TheVoidClawMob.this.beamDuration, attackSeed, 1500.0f, TheVoidMob.clawBeamDamage, 100, 1000, 0);
            TheVoidClawMob.this.getLevel().entityManager.events.addHidden(event);
            if (!TheVoidClawMob.this.isClient() && (master = TheVoidClawMob.this.master.get(TheVoidClawMob.this.getLevel())) != null) {
                master.spawnedEvents.add(event);
            }
            TheVoidClawMob.this.stopMoving();
        }
    }
}

