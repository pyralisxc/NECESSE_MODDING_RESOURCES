/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle.fireworks;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.fireworks.FireworksRocketParticle;

public class FireworksPath {
    private static final float phi = (float)(Math.PI * (3.0 - Math.sqrt(5.0)));
    public static Polygon star = new Polygon();
    public static Polygon heart;
    public ParticleOption.FloatGetter delta = (lifeTime, timeAlive, lifePercent) -> (float)Math.pow(lifePercent, 0.3f);
    public float dx;
    public float dy;
    public float dh;

    public static FireworksRocketParticle.ParticleGetter<FireworksPath> sphere(float radius) {
        return (particle, progress, random) -> {
            float z = 1.0f - progress * 2.0f;
            float fRadius = (float)Math.sqrt(1.0f - z * z);
            float theta = phi * (float)particle;
            float x = (float)(Math.cos(theta) * (double)fRadius);
            float y = (float)(Math.sin(theta) * (double)fRadius);
            float heightPart = 0.25f;
            return new FireworksPath(x * radius, y * radius * (1.0f - heightPart), -y * radius * heightPart);
        };
    }

    public static FireworksRocketParticle.ParticleGetter<FireworksPath> disc(float radius) {
        return (particle, progress, random) -> {
            float x = GameMath.cos(progress * 360.0f);
            float y = GameMath.sin(progress * 360.0f) * 0.3f;
            float dist = particle % 2 == 0 ? 1.15f : 0.85f;
            float heightPart = 0.25f;
            return new FireworksPath(x * dist * radius, y * dist * radius * (1.0f - heightPart), -y * dist * radius * heightPart);
        };
    }

    public static FireworksRocketParticle.ParticleGetter<FireworksPath> splash(float angle, float size) {
        Point2D.Float dir = GameMath.getAngleDir(angle);
        return (particle, progress, random) -> {
            float x = dir.x / 2.0f + GameMath.cos(progress * 360.0f);
            float y = (dir.y / 2.0f + GameMath.sin(progress * 360.0f)) * 0.8f;
            float dist = random.getFloatBetween(0.0f, size);
            float heightPart = 0.25f;
            return new FireworksPath(x * dist, y * dist * (1.0f - heightPart), -y * dist * heightPart);
        };
    }

    public static FireworksRocketParticle.ParticleGetter<FireworksPath> shape(Shape shape, float size) {
        return FireworksPath.shape(shape, size, random -> Float.valueOf(1.0f));
    }

    public static FireworksRocketParticle.ParticleGetter<FireworksPath> shape(Shape shape, float size, Function<GameRandom, Float> distMod) {
        int points = 0;
        double midX = 0.0;
        double midY = 0.0;
        RayLinkedList<Ray<Boolean>> list = new RayLinkedList<Ray<Boolean>>();
        double[] lastPoint = null;
        double[] coords = new double[6];
        PathIterator it = shape.getPathIterator(null);
        while (!it.isDone()) {
            int SG = it.currentSegment(coords);
            if (SG == 4) {
                lastPoint = null;
            } else if (SG == 0) {
                lastPoint = new double[]{coords[0], coords[1]};
            } else if (SG == 1) {
                if (lastPoint != null) {
                    midX += lastPoint[0] + coords[0];
                    midY += lastPoint[1] + coords[1];
                    points += 2;
                    Ray<Boolean> ray = new Ray<Boolean>(lastPoint[0], lastPoint[1], coords[0], coords[1], false, null);
                    list.addLast(ray);
                    list.totalDist += ray.dist;
                }
                lastPoint = new double[]{coords[0], coords[1]};
            }
            it.next();
        }
        Rectangle2D bounds = shape.getBounds2D();
        double finalSize = (double)size / new Point2D.Double(bounds.getWidth(), bounds.getHeight()).distance(0.0, 0.0) * 2.5;
        double finalMidX = midX / (double)points;
        double finalMidY = midY / (double)points;
        return (particle, progress, random) -> {
            double currentDist = list.totalDist * (double)progress;
            double px = 0.0;
            double py = 0.0;
            for (Ray ray : list) {
                if (currentDist <= ray.dist) {
                    Point2D.Double rayDir = GameMath.normalize(ray.x2 - ray.x1, ray.y2 - ray.y1);
                    Point2D.Double rayPoint = new Point2D.Double(ray.x1 + rayDir.x * currentDist, ray.y1 + rayDir.y * currentDist);
                    Point2D.Double dir = new Point2D.Double(rayPoint.x - finalMidX, rayPoint.y - finalMidY);
                    Float mod = (Float)distMod.apply(random);
                    px = dir.x * (double)mod.floatValue() * finalSize;
                    py = dir.y * (double)mod.floatValue() * finalSize;
                    break;
                }
                currentDist -= ray.dist;
            }
            return new FireworksPath((float)px, (float)py, 0.0f);
        };
    }

    public FireworksPath(float dx, float dy, float dh) {
        this.dx = dx;
        this.dy = dy;
        this.dh = dh;
    }

    static {
        star.addPoint(0, 0);
        star.addPoint(1, 2);
        star.addPoint(3, 2);
        star.addPoint(1, 3);
        star.addPoint(2, 5);
        star.addPoint(0, 4);
        star.addPoint(-2, 5);
        star.addPoint(-1, 3);
        star.addPoint(-3, 2);
        star.addPoint(-1, 2);
        star.addPoint(0, 0);
        heart = new Polygon();
        heart.addPoint(0, 0);
        heart.addPoint(1, -1);
        heart.addPoint(2, -1);
        heart.addPoint(3, 0);
        heart.addPoint(3, 1);
        heart.addPoint(0, 4);
        heart.addPoint(-3, 1);
        heart.addPoint(-3, 0);
        heart.addPoint(-2, -1);
        heart.addPoint(-1, -1);
        heart.addPoint(0, 0);
    }
}

