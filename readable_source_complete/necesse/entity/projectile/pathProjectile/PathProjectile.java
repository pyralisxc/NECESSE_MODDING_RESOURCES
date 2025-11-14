/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.pathProjectile;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.util.GameMath;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.TrailVector;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public abstract class PathProjectile
extends Projectile {
    protected boolean canMoveLessThanDist;
    protected boolean autoSetDirection;

    public PathProjectile() {
        this.isSolid = false;
        this.canMoveLessThanDist = false;
        this.autoSetDirection = true;
        this.dx = 1.0f;
        this.dy = 0.0f;
    }

    @Override
    protected double getDistanceMovedBeforeCollision(double dist) {
        double actualDist;
        Point2D.Float startPoint = new Point2D.Float(this.x, this.y);
        Point2D.Float endPoint = this.getPosition(dist);
        this.x = endPoint.x;
        this.y = endPoint.y;
        if (this.autoSetDirection) {
            Point2D.Float norm = GameMath.normalize(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
            this.dx = norm.x;
            this.dy = norm.y;
            if (this.dx == 0.0f && this.dy == 0.0f) {
                this.dx = 1.0f;
                this.dy = 0.0f;
            }
            this.angle = (float)Math.toDegrees(Math.atan2(this.dy, this.dx));
            this.angle += 90.0f;
            this.fixAngle();
        }
        Line2D.Float hitLine = new Line2D.Float(startPoint, endPoint);
        if (!this.isSolid) {
            this.checkHitCollision(hitLine);
        } else {
            IntersectionPoint<LevelObjectHit> p;
            Point2D.Float hitStartPoint = startPoint;
            Line2D.Float objectHitLine = hitLine;
            CollisionFilter collisionFilter = this.getLevelCollisionFilter();
            ArrayList<LevelObjectHit> hitCollisions = this.getLevel().getCollisions(hitLine, collisionFilter);
            if (hitCollisions.isEmpty() && this.useWidthForCollision) {
                float width = this.getWidth();
                int i = 8;
                while ((float)i < width / 2.0f) {
                    hitStartPoint = GameMath.getPerpendicularPoint(startPoint, (float)i, this.dx, this.dy);
                    Point2D.Float perpEndPoint = new Point2D.Float(hitStartPoint.x + (float)((double)this.dx * dist), hitStartPoint.y + (float)((double)this.dy * dist));
                    Line2D.Float perpLine = new Line2D.Float(hitStartPoint, perpEndPoint);
                    hitCollisions = this.getLevel().getCollisions(perpLine, collisionFilter);
                    if (!hitCollisions.isEmpty()) {
                        objectHitLine = perpLine;
                        break;
                    }
                    hitStartPoint = GameMath.getPerpendicularPoint(startPoint, (float)(-i), this.dx, this.dy);
                    perpEndPoint = new Point2D.Float(hitStartPoint.x + (float)((double)this.dx * dist), hitStartPoint.y + (float)((double)this.dy * dist));
                    perpLine = new Line2D.Float(hitStartPoint, perpEndPoint);
                    hitCollisions = this.getLevel().getCollisions(perpLine, collisionFilter);
                    if (!hitCollisions.isEmpty()) {
                        objectHitLine = perpLine;
                        break;
                    }
                    i += 8;
                }
            }
            if ((p = this.getLevel().getCollisionPoint(hitCollisions, objectHitLine, false)) != null) {
                Point2D.Float objectHitOffset = new Point2D.Float(this.x - hitStartPoint.x, this.y - hitStartPoint.y);
                this.x = (float)p.getX() + objectHitOffset.x;
                this.y = (float)p.getY() + objectHitOffset.y;
                if (this.bounced >= this.getTotalBouncing() || !this.canBounce) {
                    if (p.dir == IntersectionPoint.Dir.UP) {
                        this.y += 8.0f;
                    } else if (p.dir == IntersectionPoint.Dir.RIGHT) {
                        this.x -= 8.0f;
                    } else if (p.dir == IntersectionPoint.Dir.DOWN) {
                        this.y -= 8.0f;
                    } else if (p.dir == IntersectionPoint.Dir.LEFT) {
                        this.x += 8.0f;
                    }
                } else {
                    this.onBounce(p);
                    this.updateAngle();
                    this.sendPositionUpdate = true;
                }
                this.onHit(null, (LevelObjectHit)p.target, (float)p.getX(), (float)p.getY(), false, null);
                ++this.bounced;
                this.checkHitCollision(new Line2D.Float(this.x, this.y, (float)p.x, (float)p.y));
            } else {
                this.checkHitCollision(hitLine);
            }
        }
        if (this.canMoveLessThanDist && (actualDist = startPoint.distance(endPoint)) < dist) {
            double delta = dist - actualDist;
            this.traveledDistance = (float)((double)this.traveledDistance - delta);
            this.tickDistMoved = (float)((double)this.tickDistMoved - delta);
            this.lightDistMoved = (float)((double)this.lightDistMoved - delta);
        }
        return dist;
    }

    public void onBounce(IntersectionPoint p) {
        if (this.trail != null) {
            this.trail.addBreakPoint(new TrailVector((float)p.getX(), (float)p.getY(), this.dx, this.dy, this.trail.thickness, this.getHeight()));
        }
        if (p.dir == IntersectionPoint.Dir.RIGHT || p.dir == IntersectionPoint.Dir.LEFT) {
            this.dx = -this.dx;
            this.x += Math.signum(this.dx) * 4.0f;
        } else if (p.dir == IntersectionPoint.Dir.UP || p.dir == IntersectionPoint.Dir.DOWN) {
            this.dy = -this.dy;
            this.y += Math.signum(this.dy) * 4.0f;
        }
    }

    public abstract Point2D.Float getPosition(double var1);
}

