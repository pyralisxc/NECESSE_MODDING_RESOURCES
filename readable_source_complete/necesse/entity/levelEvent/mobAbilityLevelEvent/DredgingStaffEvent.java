/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class DredgingStaffEvent
extends GroundEffectEvent {
    private static final float pillarSpread = 12.0f;
    protected GameDamage damage;
    protected float resilienceGain;
    public float speed;
    public int distance;
    public int width;
    public int targetX;
    public int targetY;
    protected HashSet<Integer> hits;
    protected final GroundPillarList<DredgePillar> pillars = new GroundPillarList();
    protected float currentDistance;
    protected float lastPillarDist;
    protected int pillarRow;
    protected Point2D.Float dir;

    public DredgingStaffEvent() {
    }

    public DredgingStaffEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, int targetX, int targetY, GameDamage damage, float resilienceGain, float speed, int distance, int width) {
        super(owner, x, y, uniqueIDRandom);
        this.targetX = targetX;
        this.targetY = targetY;
        this.damage = damage;
        this.resilienceGain = resilienceGain;
        this.speed = speed;
        this.distance = distance;
        this.width = width;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.speed);
        writer.putNextInt(this.distance);
        writer.putNextInt(this.width);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.speed = reader.getNextFloat();
        this.distance = reader.getNextInt();
        this.width = reader.getNextInt();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.hits = new HashSet();
        this.dir = GameMath.normalize(this.targetX - this.x, this.targetY - this.y);
        if (this.dir.x == 0.0f && this.dir.y == 0.0f) {
            this.dir.x = 1.0f;
        }
        if (this.isClient()) {
            this.level.entityManager.addPillarHandler(new GroundPillarHandler<DredgePillar>(this.pillars){

                @Override
                protected boolean canRemove() {
                    return DredgingStaffEvent.this.isOver();
                }

                @Override
                public double getCurrentDistanceMoved() {
                    return 0.0;
                }
            });
            SoundManager.playSound(GameResources.shake, (SoundEffect)SoundEffect.effect(this.owner));
            SoundManager.playSound(GameResources.stomp, (SoundEffect)SoundEffect.effect(this.owner).volume(0.5f));
        }
        this.lastPillarDist = (float)this.distance % 12.0f;
    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.hits.add(target.getUniqueID());
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !this.hits.contains(target.getUniqueID())) {
            this.hits.add(target.getUniqueID());
            target.isServerHit(this.damage, 0.0f, 0.0f, 0.0f, this.owner);
            if (target.canGiveResilience(this.owner) && this.resilienceGain != 0.0f) {
                this.owner.addResilience(this.resilienceGain);
                this.resilienceGain = 0.0f;
            }
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !this.hits.contains(mob.getUniqueID());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        float lastDistance = this.currentDistance;
        this.currentDistance += this.speed * delta / 250.0f;
        if (this.currentDistance > (float)this.distance) {
            this.currentDistance = this.distance;
            this.over();
        }
        if (this.currentDistance > lastDistance) {
            if (this.isClient()) {
                GroundPillarList<DredgePillar> groundPillarList = this.pillars;
                synchronized (groundPillarList) {
                    while (this.lastPillarDist <= this.currentDistance) {
                        float offset;
                        float distPerc = this.lastPillarDist / (float)this.distance;
                        float pillarsWidth = (float)this.width * distPerc / 2.0f;
                        Point2D.Float midPos = new Point2D.Float((float)this.x + this.dir.x * this.lastPillarDist, (float)this.y + this.dir.y * this.lastPillarDist);
                        if (this.pillarRow % 2 == 0) {
                            offset = 12.0f;
                            this.pillars.add(new DredgePillar((int)midPos.x, (int)midPos.y, this.lastPillarDist, this.level.getWorldEntity().getLocalTime()));
                        } else {
                            offset = 6.0f;
                        }
                        for (float i = offset; i < pillarsWidth; i += 12.0f) {
                            Point2D.Float leftPos = GameMath.getPerpendicularPoint(midPos, -i, this.dir);
                            this.pillars.add(new DredgePillar((int)leftPos.x, (int)leftPos.y, this.lastPillarDist, this.level.getWorldEntity().getLocalTime()));
                            Point2D.Float rightPos = GameMath.getPerpendicularPoint(midPos, i, this.dir);
                            this.pillars.add(new DredgePillar((int)rightPos.x, (int)rightPos.y, this.lastPillarDist, this.level.getWorldEntity().getLocalTime()));
                        }
                        ++this.pillarRow;
                        this.lastPillarDist += 12.0f;
                    }
                }
            }
            float lastDistancePerc = lastDistance / (float)this.distance;
            float distancePerc = this.currentDistance / (float)this.distance;
            float lastWidth = (float)this.width * lastDistancePerc / 2.0f;
            float currentWidth = (float)this.width * distancePerc / 2.0f;
            Point2D.Float lastPos = new Point2D.Float((float)this.x + this.dir.x * lastDistance, (float)this.y + this.dir.y * lastDistance);
            Point2D.Float currentPos = new Point2D.Float((float)this.x + this.dir.x * this.currentDistance, (float)this.y + this.dir.y * this.currentDistance);
            Point2D.Float lastPosLeft = GameMath.getPerpendicularPoint(lastPos, -lastWidth, this.dir);
            Point2D.Float lastPosRight = GameMath.getPerpendicularPoint(lastPos, lastWidth, this.dir);
            Point2D.Float currentPosLeft = GameMath.getPerpendicularPoint(currentPos, -currentWidth, this.dir);
            Point2D.Float currentPosRight = GameMath.getPerpendicularPoint(currentPos, currentWidth, this.dir);
            int[] xPoints = new int[]{(int)lastPosLeft.x, (int)lastPosRight.x, (int)currentPosRight.x, (int)currentPosLeft.x};
            int[] yPoints = new int[]{(int)lastPosLeft.y, (int)lastPosRight.y, (int)currentPosRight.y, (int)currentPosLeft.y};
            Polygon hitBox = new Polygon(xPoints, yPoints, 4);
            this.handleHits(hitBox, this::canHit, null);
        }
    }

    protected Point2D.Float getLeftPos(float distance) {
        float distPerc = distance / (float)this.distance;
        Point2D.Float pos = new Point2D.Float((float)this.x + this.dir.x * distPerc, (float)this.y + this.dir.y * distPerc);
        float width = (float)this.width * distPerc;
        return GameMath.getPerpendicularPoint(pos, -width, this.dir);
    }

    protected Point2D.Float getRightPos(float distance) {
        float distPerc = distance / (float)this.distance;
        Point2D.Float pos = new Point2D.Float((float)this.x + this.dir.x * distPerc, (float)this.y + this.dir.y * distPerc);
        float width = (float)this.width * distPerc;
        return GameMath.getPerpendicularPoint(pos, width, this.dir);
    }

    @Override
    public void clientTick() {
    }

    @Override
    public void serverTick() {
    }

    public static class DredgePillar
    extends GroundPillar {
        public GameTextureSection texture = null;
        public boolean mirror = GameRandom.globalRandom.nextBoolean();

        public DredgePillar(int x, int y, double spawnDistance, long spawnTime) {
            super(x, y, spawnDistance, spawnTime);
            GameTexture pillarSprites = GameResources.dredgingStaffPillars;
            if (pillarSprites != null) {
                int res = pillarSprites.getHeight();
                int sprite = GameRandom.globalRandom.nextInt(pillarSprites.getWidth() / res);
                this.texture = new GameTextureSection(GameResources.dredgingStaffPillars).sprite(sprite, 0, res);
            }
            this.behaviour = new GroundPillar.TimedBehaviour(200, 100, 200);
        }

        @Override
        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameLight light = level.getLightLevel(LevelEvent.getTileCoordinate(this.x), LevelEvent.getTileCoordinate(this.y));
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.section(0, this.texture.getWidth(), 0, endY).initDraw().mirror(this.mirror, false).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY);
        }
    }
}

