/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.trails;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.util.GameMath;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;

public class TrailPointList {
    private final Trail trail;
    private final ArrayList<TrailPoint> list = new ArrayList();

    public TrailPointList(Trail trail) {
        this.trail = trail;
    }

    public TrailPointList copy() {
        TrailPointList copy = new TrailPointList(this.trail);
        copy.list.addAll(this.list);
        return copy;
    }

    public TrailPoint getLastPoint() {
        return this.list.get(this.list.size() - 1);
    }

    public TrailPoint getNextPoint(TrailVector vector, long spawnTime) {
        TrailPoint point = new TrailPoint(vector, spawnTime);
        point.updatePrev(this.list.get(this.list.size() - 1));
        this.getLastPoint().updateNext(point);
        return point;
    }

    public TrailPointList getNextPointSection(TrailPoint nextPoint) {
        TrailPointList out = new TrailPointList(this.trail);
        out.list.add(this.getLastPoint());
        out.list.add(nextPoint);
        return out;
    }

    public TrailPoint add(TrailVector vector, long spawnTime) {
        return this.add(new TrailPoint(vector, spawnTime));
    }

    private TrailPoint add(TrailPoint point) {
        if (!this.isEmpty()) {
            TrailPoint lastPoint = this.getLastPoint();
            lastPoint.updateNext(point);
            point.updatePrev(lastPoint);
        } else {
            point.updatePrev(null);
        }
        this.list.add(point);
        return point;
    }

    public TrailPoint get(int index) {
        return this.list.get(index);
    }

    public TrailPoint removeFirst() {
        TrailPoint remove = this.list.remove(0);
        if (!this.isEmpty()) {
            this.list.get(0).prevPoint = null;
        }
        return remove;
    }

    public int size() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public void ensureCapacity(int minCapacity) {
        this.list.ensureCapacity(minCapacity);
    }

    public class TrailPoint {
        public final TrailVector vector;
        public final long spawnTime;
        private TrailPoint prevPoint;
        private TrailPoint nextPoint;
        private Point2D.Float drawPos1;
        private Point2D.Float drawPos2;

        private TrailPoint(TrailVector vector, long spawnTime) {
            this.vector = vector;
            this.spawnTime = spawnTime;
        }

        private void updatePrev(TrailPoint prevPoint) {
            this.prevPoint = prevPoint;
            this.drawPos1 = this.getDrawPoint1(this.vector.thickness);
            this.drawPos2 = this.getDrawPoint2(this.vector.thickness);
            if (((TrailPointList)TrailPointList.this).trail.smoothCorners && prevPoint != null && new Line2D.Float(this.drawPos1, this.drawPos2).intersectsLine(new Line2D.Float(prevPoint.drawPos1, prevPoint.drawPos2))) {
                float angleDiff = GameMath.getAngleDifference(prevPoint.vector.getAngle(), this.vector.getAngle());
                if (angleDiff == 0.0f) {
                    this.drawPos1 = prevPoint.drawPos1;
                    this.drawPos2 = prevPoint.drawPos2;
                } else if (angleDiff > 0.0f) {
                    this.drawPos1 = prevPoint.drawPos1;
                } else {
                    this.drawPos2 = prevPoint.drawPos2;
                }
            }
        }

        private void updateNext(TrailPoint nextPoint) {
            this.nextPoint = nextPoint;
        }

        private Point2D.Float getDrawPoint1(float thickness) {
            return GameMath.getPerpendicularPoint(this.vector.pos, thickness / 2.0f, this.vector.dx, this.vector.dy);
        }

        private Point2D.Float getDrawPoint2(float thickness) {
            return GameMath.getPerpendicularPoint(this.vector.pos, -thickness / 2.0f, this.vector.dx, this.vector.dy);
        }

        public TrailPoint getPrevPoint() {
            return this.prevPoint;
        }

        public TrailPoint getNextPoint() {
            return this.nextPoint;
        }

        public Point2D.Float getDrawPos1() {
            return this.drawPos1;
        }

        public Point2D.Float getDrawPos2() {
            return this.drawPos2;
        }
    }
}

