/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.voronoi;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import necesse.engine.util.voronoi.TriangleData;
import necesse.engine.util.voronoi.TriangleLine;

public class DelaunayTriangulator {
    private static final float EPSILON = 1.0E-6f;
    private static final int INSIDE = 0;
    private static final int COMPLETE = 1;
    private static final int INCOMPLETE = 2;

    public static ArrayList<TriangleData> compute(ArrayList<Point2D.Float> points, boolean sorted, ArrayList<TriangleLine> voronoiLines) {
        if (points.isEmpty()) {
            return new ArrayList<TriangleData>();
        }
        if (!sorted) {
            points.sort((p1, p2) -> Float.compare(p1.x, p2.x));
        }
        Point2D.Float first = points.get(0);
        Point2D.Float min = new Point2D.Float(first.x, first.y);
        Point2D.Float max = new Point2D.Float(first.x, first.y);
        for (Point2D.Float point : points) {
            if (point.x < min.x) {
                min.x = point.x;
            }
            if (point.x > max.x) {
                max.x = point.x;
            }
            if (point.y < min.y) {
                min.y = point.y;
            }
            if (!(point.y > max.y)) continue;
            max.y = point.y;
        }
        float dx = max.x - min.x;
        float dy = max.y - min.y;
        float madD = Math.max(dx, dy) * 20.0f;
        float midX = (max.x + min.x) / 2.0f;
        float midY = (max.y + min.y) / 2.0f;
        TriangleData superTriangle = new TriangleData(new Point2D.Float(midX - madD, midY - madD), new Point2D.Float(midX, midY + madD), new Point2D.Float(midX + madD, midY - madD));
        ArrayList<TriangleLine> edges = new ArrayList<TriangleLine>(points.size() / 2);
        ArrayList<TriangleData> triangles = new ArrayList<TriangleData>(points.size());
        triangles.add(null);
        for (Point2D.Float point : points) {
            block6: for (int triangleIndex = triangles.size() - 1; triangleIndex >= 0; --triangleIndex) {
                Point2D.Float p3;
                Point2D.Float p22;
                Point2D.Float p12;
                TriangleData triangle = (TriangleData)triangles.get(triangleIndex);
                if (triangle != null && triangle.complete) continue;
                if (triangle == null) {
                    p12 = superTriangle.p1;
                    p22 = superTriangle.p2;
                    p3 = superTriangle.p3;
                } else {
                    p12 = triangle.p1;
                    p22 = triangle.p2;
                    p3 = triangle.p3;
                }
                switch (DelaunayTriangulator.circumCircle(point, p12, p22, p3)) {
                    case 1: {
                        if (triangle == null) continue block6;
                        triangle.complete = true;
                        continue block6;
                    }
                    case 0: {
                        edges.add(new TriangleLine(p12, p22));
                        edges.add(new TriangleLine(p22, p3));
                        edges.add(new TriangleLine(p3, p12));
                        triangles.remove(triangleIndex);
                    }
                }
            }
            int n = edges.size();
            for (int i = 0; i < n; ++i) {
                TriangleLine edge = (TriangleLine)edges.get(i);
                if (edge == null) continue;
                boolean skip = false;
                for (int ii = i + 1; ii < n; ++ii) {
                    TriangleLine edge2 = (TriangleLine)edges.get(ii);
                    if (!edge.equals(edge2)) continue;
                    skip = true;
                    edges.set(ii, null);
                }
                if (skip) continue;
                triangles.add(new TriangleData(edge.p1, edge.p2, point));
            }
            edges.clear();
        }
        if (voronoiLines != null) {
            voronoiLines.ensureCapacity(triangles.size() / 2);
        }
        HashMap<TriangleLine, TriangleData> lineTriangles = new HashMap<TriangleLine, TriangleData>();
        for (int i = triangles.size() - 1; i >= 0; --i) {
            TriangleData t = triangles.get(i);
            if (t == null || DelaunayTriangulator.isSamePoint(t.p1, superTriangle.p1) || DelaunayTriangulator.isSamePoint(t.p1, superTriangle.p2) || DelaunayTriangulator.isSamePoint(t.p1, superTriangle.p3) || DelaunayTriangulator.isSamePoint(t.p2, superTriangle.p1) || DelaunayTriangulator.isSamePoint(t.p2, superTriangle.p2) || DelaunayTriangulator.isSamePoint(t.p2, superTriangle.p3) || DelaunayTriangulator.isSamePoint(t.p3, superTriangle.p1) || DelaunayTriangulator.isSamePoint(t.p3, superTriangle.p2) || DelaunayTriangulator.isSamePoint(t.p3, superTriangle.p3)) {
                if (t != null) {
                    t.complete = false;
                }
                triangles.remove(i);
                continue;
            }
            if (voronoiLines == null) continue;
            lineTriangles.compute(new TriangleLine(t.p1, t.p2), (line, prev) -> {
                if (prev == null) {
                    return t;
                }
                voronoiLines.add(new TriangleLine(prev.average, t.average));
                return null;
            });
            lineTriangles.compute(new TriangleLine(t.p2, t.p3), (line, prev) -> {
                if (prev == null) {
                    return t;
                }
                voronoiLines.add(new TriangleLine(prev.average, t.average));
                return null;
            });
            lineTriangles.compute(new TriangleLine(t.p3, t.p1), (line, prev) -> {
                if (prev == null) {
                    return t;
                }
                voronoiLines.add(new TriangleLine(prev.average, t.average));
                return null;
            });
        }
        return triangles;
    }

    private static boolean isSamePoint(Point2D.Float p1, Point2D.Float p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    private static int circumCircle(Point2D.Float p, Point2D.Float p1, Point2D.Float p2, Point2D.Float p3) {
        float yc;
        float xc;
        float y1y2 = Math.abs(p1.y - p2.y);
        float y2y3 = Math.abs(p2.y - p3.y);
        if (y1y2 < 1.0E-6f) {
            if (y2y3 < 1.0E-6f) {
                return 2;
            }
            float m2 = -(p3.x - p2.x) / (p3.y - p2.y);
            float mx2 = (p2.x + p3.x) / 2.0f;
            float my2 = (p2.y + p3.y) / 2.0f;
            xc = (p2.x + p1.x) / 2.0f;
            yc = m2 * (xc - mx2) + my2;
        } else {
            float m1 = -(p2.x - p1.x) / (p2.y - p1.y);
            float mx1 = (p1.x + p2.x) / 2.0f;
            float my1 = (p1.y + p2.y) / 2.0f;
            if (y2y3 < 1.0E-6f) {
                xc = (p3.x + p2.x) / 2.0f;
                yc = m1 * (xc - mx1) + my1;
            } else {
                float m2 = -(p3.x - p2.x) / (p3.y - p2.y);
                float mx2 = (p2.x + p3.x) / 2.0f;
                float my2 = (p2.y + p3.y) / 2.0f;
                xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
                yc = m1 * (xc - mx1) + my1;
            }
        }
        float dx = p2.x - xc;
        float dy = p2.y - yc;
        float rsqr = dx * dx + dy * dy;
        dx = p.x - xc;
        dx *= dx;
        dy = p.y - yc;
        if (dx + dy * dy - rsqr <= 1.0E-6f) {
            return 0;
        }
        return p.x > xc && dx > rsqr ? 1 : 2;
    }
}

