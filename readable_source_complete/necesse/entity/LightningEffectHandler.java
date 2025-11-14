/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.function.Supplier;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class LightningEffectHandler {
    public float distanceBetweenZaps = 20.0f;
    public Color color = new Color(58, 220, 220);
    private final Level level;
    private float currentX;
    private float currentY;
    private float distanceMovedBuffer;
    private final LinkedList<TargetPoint> nextPoints = new LinkedList();
    private final TrailHandler[] trails;
    private boolean initialized;
    private Point startFirePosition;

    public LightningEffectHandler(Level level, int trailCount) {
        this.level = level;
        this.trails = new TrailHandler[trailCount];
    }

    public LightningEffectHandler(Level level, int trailCount, Point startFirePosition) {
        this.level = level;
        this.trails = new TrailHandler[trailCount];
        this.startFirePosition = startFirePosition;
    }

    public void addNextPoint(Supplier<Point2D.Float> coordinate, int timeToPoint, boolean forceTrailHit, Runnable onPointReached) {
        this.nextPoints.add(new TargetPoint(coordinate, timeToPoint, forceTrailHit, onPointReached));
    }

    public void addNextPoint(float x, float y, int timeToPoint, boolean forceTrailHit, Runnable onPointReached) {
        this.addNextPoint(() -> new Point2D.Float(x, y), timeToPoint, forceTrailHit, onPointReached);
    }

    public void setNextPoint(Supplier<Point2D.Float> coordinate, int timeToPoint, boolean forceTrailHit, Runnable onPointReached) {
        this.nextPoints.clear();
        this.addNextPoint(coordinate, timeToPoint, forceTrailHit, onPointReached);
    }

    public void tickMovement(float delta) {
        if (this.nextPoints.isEmpty()) {
            return;
        }
        if (!this.initialized) {
            TargetPoint firstPoint = this.nextPoints.removeFirst();
            Point2D.Float pos = firstPoint.coordinate.get();
            for (int i = 0; i < this.trails.length; ++i) {
                this.trails[i] = new TrailHandler(pos.x, pos.y);
            }
            this.currentX = pos.x;
            this.currentY = pos.y;
            this.onPointHit(firstPoint);
            this.initialized = true;
            if (this.nextPoints.isEmpty()) {
                return;
            }
        }
        TargetPoint nextPoint = this.nextPoints.getFirst();
        while (delta >= nextPoint.timeToPoint) {
            this.nextPoints.removeFirst();
            this.tickMovementProgress(nextPoint.timeToPoint, nextPoint);
            delta -= nextPoint.timeToPoint;
            if (this.nextPoints.isEmpty()) {
                nextPoint = null;
                break;
            }
            nextPoint = this.nextPoints.getFirst();
        }
        if (nextPoint != null) {
            this.tickMovementProgress(delta, nextPoint);
        }
    }

    private void tickMovementProgress(float delta, TargetPoint nextPoint) {
        float percentToMove = nextPoint.timeToPoint <= 0.0f ? 1.0f : delta / nextPoint.timeToPoint;
        Point2D.Float pos = nextPoint.coordinate.get();
        Point2D.Float dir = GameMath.normalize(pos.x - this.currentX, pos.y - this.currentY);
        float distanceToMove = GameMath.getExactDistance(this.currentX, this.currentY, pos.x, pos.y) * percentToMove;
        while (distanceToMove > 0.0f) {
            float currentDistanceToMove = Math.min(distanceToMove, this.distanceBetweenZaps - this.distanceMovedBuffer);
            this.distanceMovedBuffer += currentDistanceToMove;
            distanceToMove -= currentDistanceToMove;
            this.currentX += dir.x * currentDistanceToMove;
            this.currentY += dir.y * currentDistanceToMove;
            if (!(this.distanceMovedBuffer >= this.distanceBetweenZaps)) continue;
            this.randomizeTrailPoint(this.currentX, this.currentY);
            this.distanceMovedBuffer -= this.distanceBetweenZaps;
        }
        nextPoint.timeToPoint -= delta;
        if (percentToMove >= 1.0f) {
            this.onPointHit(nextPoint);
        }
    }

    private void onPointHit(TargetPoint point) {
        if (point.forceTrailHit) {
            this.randomizeTrailPoint(this.currentX, this.currentY);
        }
        if (point.onPointReached != null) {
            point.onPointReached.run();
        }
    }

    private void randomizeTrailPoint(float currentX, float currentY) {
        if (!this.level.isClient()) {
            return;
        }
        Point2D.Float nextPoint = this.nextPoints.isEmpty() ? new Point2D.Float(currentX, currentY) : this.nextPoints.getFirst().coordinate.get();
        for (TrailHandler trail : this.trails) {
            float randomX = currentX + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f);
            float randomY = currentY + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f);
            trail.trail.addPoint(new TrailVector(randomX, randomY, nextPoint.x - randomX, nextPoint.y - randomY, 10.0f, 0.0f));
        }
    }

    public void dispose() {
        if (!this.initialized) {
            return;
        }
        if (!this.level.isClient()) {
            return;
        }
        for (TrailHandler trail : this.trails) {
            trail.trail.removeOnFadeOut = true;
        }
    }

    private class TrailHandler {
        public float lastX;
        public float lastY;
        public Trail trail;

        public TrailHandler(float startX, float startY) {
            this.lastX = startX;
            this.lastY = startY;
            if (LightningEffectHandler.this.level.isClient()) {
                Point2D.Float nextPoint = LightningEffectHandler.this.nextPoints.isEmpty() ? new Point2D.Float(LightningEffectHandler.this.currentX, LightningEffectHandler.this.currentY) : ((TargetPoint)((LightningEffectHandler)LightningEffectHandler.this).nextPoints.getFirst()).coordinate.get();
                this.trail = new Trail(new TrailVector(startX, startY, nextPoint.x - startX, nextPoint.y - startY, 10.0f, 0.0f), LightningEffectHandler.this.level, LightningEffectHandler.this.color, 500);
                this.trail.removeOnFadeOut = false;
                this.trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
                this.trail.drawOnTop = true;
                this.trail.addLight(200.0f, 1.0f);
                ((LightningEffectHandler)LightningEffectHandler.this).level.entityManager.addTrail(this.trail);
            }
        }
    }

    private class TargetPoint {
        public Supplier<Point2D.Float> coordinate;
        public float timeToPoint;
        public boolean forceTrailHit;
        public Runnable onPointReached;

        public TargetPoint(Supplier<Point2D.Float> coordinate, int timeToPoint, boolean forceTrailHit, Runnable onPointReached) {
            this.coordinate = coordinate;
            this.timeToPoint = timeToPoint;
            this.forceTrailHit = forceTrailHit;
            this.onPointReached = onPointReached;
        }
    }
}

