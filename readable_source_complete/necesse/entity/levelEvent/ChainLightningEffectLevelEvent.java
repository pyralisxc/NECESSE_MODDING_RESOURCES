/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;

public class ChainLightningEffectLevelEvent
extends LevelEvent {
    public Point2D.Float startPos;
    public Point2D.Float endPos;
    public Trail trail;
    private long startTime;
    private int eventLifeTime;
    private int zapsPerLifeTime;
    private int zapCounter = 1;
    private Point2D.Float previousTargetPoints;
    private GameRandom random;

    public ChainLightningEffectLevelEvent() {
    }

    public ChainLightningEffectLevelEvent(Point2D.Float startPos, Point2D.Float endPos, int eventLifeTime, int zapsPerLifeTime) {
        super(true);
        this.startPos = startPos;
        this.endPos = endPos;
        this.eventLifeTime = eventLifeTime;
        this.zapsPerLifeTime = zapsPerLifeTime;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.startPos.x);
        writer.putNextFloat(this.startPos.y);
        writer.putNextFloat(this.endPos.x);
        writer.putNextFloat(this.endPos.y);
        writer.putNextInt(this.eventLifeTime);
        writer.putNextInt(this.zapsPerLifeTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        if (this.startPos == null) {
            this.startPos = new Point2D.Float();
        }
        this.startPos.x = reader.getNextFloat();
        this.startPos.y = reader.getNextFloat();
        if (this.endPos == null) {
            this.endPos = new Point2D.Float();
        }
        this.endPos.x = reader.getNextFloat();
        this.endPos.y = reader.getNextFloat();
        this.eventLifeTime = reader.getNextInt();
        this.zapsPerLifeTime = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        if (this.startPos == null || this.endPos == null || this.isServer()) {
            this.over();
        }
        this.random = new GameRandom();
        this.startTime = this.getLocalTime();
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.eventLifeTime != -1) {
            long timeSinceStart = this.getLocalTime() - this.startTime;
            if (timeSinceStart > (long)this.eventLifeTime) {
                this.over();
                return;
            }
            float lifePercent = (float)timeSinceStart / (float)this.eventLifeTime;
            float expectedZapsSpawned = lifePercent * (float)this.zapsPerLifeTime + 2.0f;
            while ((float)this.zapCounter < expectedZapsSpawned) {
                this.addPointToTrail(null);
            }
        }
    }

    public void addPointToTrail(Point2D.Float customTargetPoints) {
        Point2D.Float targetPoints = customTargetPoints == null ? this.getTargetPoints(this.random) : customTargetPoints;
        ++this.zapCounter;
        if (this.trail == null) {
            this.trail = new Trail(new TrailVector(this.startPos.x, this.startPos.y, targetPoints.x, targetPoints.y, 10.0f, 0.0f), this.level, new Color(58, 220, 220), 500);
            this.trail.removeOnFadeOut = false;
            this.trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
            this.trail.drawOnTop = true;
            this.level.entityManager.addTrail(this.trail);
        } else {
            this.trail.addPoint(new TrailVector(this.previousTargetPoints.x, this.previousTargetPoints.y, targetPoints.x, targetPoints.y, 10.0f, 0.0f));
        }
        this.previousTargetPoints = targetPoints;
    }

    private Point2D.Float getTargetPoints(GameRandom random) {
        float distanceBetweenStartEndX = Math.abs(this.startPos.x - this.endPos.x);
        float distanceBetweenStartEndY = Math.abs(this.startPos.y - this.endPos.y);
        float targetX = this.startPos.x > this.endPos.x ? this.startPos.x - distanceBetweenStartEndX / (float)this.zapsPerLifeTime * (float)this.zapCounter : this.startPos.x + distanceBetweenStartEndX / (float)this.zapsPerLifeTime * (float)this.zapCounter;
        float targetY = this.startPos.y > this.endPos.y ? this.startPos.y - distanceBetweenStartEndY / (float)this.zapsPerLifeTime * (float)this.zapCounter : this.startPos.y + distanceBetweenStartEndY / (float)this.zapsPerLifeTime * (float)this.zapCounter;
        return new Point2D.Float(targetX += (float)random.getIntBetween(-15, 15), targetY += (float)random.getIntBetween(-15, 15));
    }

    @Override
    public void over() {
        super.over();
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
        }
    }
}

