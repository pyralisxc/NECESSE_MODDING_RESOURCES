/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardStage1ArmsDownAnimation;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedWizardPeripheralMob
extends FlyingBossMob {
    public int removeBuffer;
    public boolean isHiding;
    public long hidingStopTime;
    public long nextAppearTime;
    public long nextDisappearTime;
    public AscendedWizardAnimation animation = new AscendedWizardStage1ArmsDownAnimation();
    public final CoordinateMobAbility shadeTeleportAbility;
    public final EmptyMobAbility disappearAbility;

    public AscendedWizardPeripheralMob() {
        super(100);
        this.setSpeed(100.0f);
        this.setFriction(1.0f);
        this.moveAccuracy = 10;
        this.setArmor(10);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-18, -15, 36, 30);
        this.selectBox = new Rectangle(-18, -41, 36, 48);
        this.shouldSave = false;
        this.isStatic = true;
        this.shadeTeleportAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                if (!AscendedWizardPeripheralMob.this.isHiding) {
                    AscendedWizardPeripheralMob.this.spawnFadingParticle(500);
                }
                AscendedWizardPeripheralMob.this.setPos(x, y, true);
                AscendedWizardPeripheralMob.this.isHiding = false;
                AscendedWizardPeripheralMob.this.hidingStopTime = AscendedWizardPeripheralMob.this.getLocalTime();
            }
        });
        this.disappearAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                AscendedWizardPeripheralMob.this.isHiding = true;
                AscendedWizardPeripheralMob.this.nextDisappearTime = 0L;
                AscendedWizardPeripheralMob.this.spawnFadingParticle(500);
            }
        });
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isHiding = reader.getNextBoolean();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isHiding);
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        this.nextAppearTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(5000, 30000);
        this.animation.onAnimationStarted(this);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.animation.onMovementTick(this, delta);
    }

    @Override
    public boolean isVisible() {
        return !this.isHiding;
    }

    public PlayerMob getRandomPlayer() {
        ArrayList list = GameUtils.streamNetworkClients(this.getLevel()).filter(c -> c.isSamePlace(this.getLevel())).map(c -> c.playerMob).collect(Collectors.toCollection(ArrayList::new));
        if (list.isEmpty()) {
            return null;
        }
        return (PlayerMob)GameRandom.globalRandom.getOneOf(list);
    }

    public PlayerMob getClosestPlayer(int fromX, int fromY, int searchRange, boolean getOutsideRange) {
        PlayerMob player;
        TreeSet<PlayerMob> closestSet = this.getLevel().entityManager.players.streamArea(fromX, fromY, searchRange).findExtraDistanceSorted(2, Comparator.comparingDouble(p -> GameMath.diagonalMoveDistance(p.getX(), p.getY(), fromX, fromY)));
        PlayerMob playerMob = player = closestSet.isEmpty() ? null : closestSet.first();
        if (player == null && getOutsideRange) {
            player = GameUtils.streamNetworkClients(this.getLevel()).filter(c -> c.isSamePlace(this.getLevel())).map(c -> c.playerMob).min(Comparator.comparingDouble(p -> GameMath.diagonalMoveDistance(p.getX(), p.getY(), fromX, fromY))).orElse(null);
        }
        return player;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        ++this.removeBuffer;
        if (this.removeBuffer >= 20) {
            this.remove();
            return;
        }
        if (!this.isHiding) {
            if (this.hasArrivedAtTarget() || this.nextDisappearTime != 0L && this.nextDisappearTime <= this.getTime()) {
                this.disappearAbility.runAndSend();
                this.nextAppearTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(15000, 30000);
            }
        } else if (this.nextAppearTime <= this.getTime()) {
            for (int i = 0; i < 10; ++i) {
                PlayerMob player = this.getRandomPlayer();
                if (player != null) {
                    boolean useCircle;
                    float appearAngle = GameRandom.globalRandom.getIntBetween(0, 300);
                    float angleOffset = GameRandom.globalRandom.getIntBetween(30, 90);
                    if (GameRandom.globalRandom.nextBoolean()) {
                        angleOffset = -angleOffset;
                    }
                    float goToAngle = GameMath.fixAngle(appearAngle + angleOffset);
                    int appearDistance = GameRandom.globalRandom.getIntBetween(480, 640);
                    boolean bl = useCircle = Math.abs(angleOffset) >= 60.0f;
                    if (this.moveFromAndTo(player.getX(), player.getY(), appearAngle, goToAngle, appearDistance, appearDistance, useCircle)) break;
                } else {
                    this.nextAppearTime = this.getTime() + 2000L;
                    break;
                }
                this.nextAppearTime = this.getTime() + 2000L;
            }
        }
    }

    protected boolean moveFromAndTo(int centerX, int centerY, float fromAngle, float toAngle, float distance, int minPlayerDistance, boolean useCircle) {
        Point2D.Float appearDir = GameMath.getAngleDir(fromAngle);
        Point2D.Float goToDir = GameMath.getAngleDir(toAngle);
        Point appearPos = new Point((int)((float)centerX + appearDir.x * distance), (int)((float)centerY + appearDir.y * distance * 0.8f));
        PlayerMob closestPlayer = this.getClosestPlayer(appearPos.x, appearPos.y, minPlayerDistance, false);
        if (closestPlayer != null && closestPlayer.getDistance(appearPos.x, appearPos.y) < (float)minPlayerDistance) {
            return false;
        }
        Point goToPos = new Point((int)((float)centerX + goToDir.x * distance), (int)((float)centerY + goToDir.y * distance * 0.8f));
        if (useCircle) {
            float angleDifference = GameMath.getAngleDifference(fromAngle, toAngle);
            float travelDistance = (float)appearPos.distance(goToPos);
            int circleRadius = (int)(travelDistance / 2.0f);
            boolean reversed = angleDifference > 0.0f;
            float speed = MobMovementCircle.convertToRotSpeed(circleRadius, this.getSpeed());
            Point2D.Float centerPos = new Point2D.Float((float)(appearPos.x + goToPos.x) / 2.0f, (float)(appearPos.y + goToPos.y) / 2.0f);
            this.shadeTeleportAbility.runAndSend(appearPos.x, appearPos.y);
            this.setMovement(new MobMovementCircleLevelPos(this, centerPos.x, centerPos.y, circleRadius, speed, reversed));
            int timeToAngle = (int)MobMovementCircle.getTimeToAngle(speed, 180.0f);
            this.nextDisappearTime = this.getTime() + (long)timeToAngle;
        } else {
            this.shadeTeleportAbility.runAndSend(appearPos.x, appearPos.y);
            this.setMovement(new MobMovementLevelPos(goToPos.x, goToPos.y));
        }
        return true;
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.isHiding) {
            return;
        }
        float alpha = 0.5f;
        long timeSinceStoppedHiding = this.getLocalTime() - this.hidingStopTime;
        if (timeSinceStoppedHiding < 500L) {
            float percentSinceStoppedHiding = Math.max((float)timeSinceStoppedHiding / 500.0f, 0.0f);
            alpha = GameMath.lerp(percentSinceStoppedHiding, 0.0f, 0.5f);
        }
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        final DrawOptions bodyDrawOptions = this.getBodyDrawOptions(level, x, y, this.dx, camera, alpha);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                bodyDrawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public void spawnFadingParticle(int fadeTime) {
        float dx = this.dx;
        float dy = this.dy;
        this.getLevel().entityManager.addParticle(new Particle(this.getLevel(), this.getX(), this.getY(), dx, dy, fadeTime){

            @Override
            public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                float alpha = GameMath.lerp(this.getLifeCyclePercent(), 0.5f, 0.0f);
                DrawOptions drawOptions = AscendedWizardPeripheralMob.this.getBodyDrawOptions(level, this.getX(), this.getY(), this.dx, camera, alpha);
                topList.add(tm -> drawOptions.draw());
            }
        }, Particle.GType.CRITICAL);
    }

    public DrawOptions getBodyDrawOptions(Level level, int x, int y, float dx, GameCamera camera, float alpha) {
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 75;
        int anim = GameUtils.getAnim(level.getTime(), 4, 400);
        float rotate = Math.min(20.0f, dx / 8.0f);
        light = light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f));
        TextureDrawOptionsEnd options1 = MobRegistry.Textures.ascendedWizard_stage1.initDraw().sprite(anim, 2, 128).light(light).rotate(rotate, 64, 64).alpha(alpha).pos(drawX, drawY);
        Point sprite = this.animation.getSprite(this);
        TextureDrawOptionsEnd options2 = MobRegistry.Textures.ascendedWizard_stage1.initDraw().sprite(sprite.x, sprite.y, 128).light(light).rotate(rotate, 64, 64).alpha(alpha).pos(drawX, drawY);
        TextureDrawOptionsEnd options3 = MobRegistry.Textures.ascendedWizard_stage1.initDraw().sprite(anim, 3, 128).light(light).rotate(rotate, 64, 64).alpha(alpha).pos(drawX, drawY);
        return () -> {
            options1.draw();
            options2.draw();
            options3.draw();
        };
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.voidWizard_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 5;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }
}

