/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.Comparator;
import necesse.engine.util.GameLinkedList;

public class ExpandingPolygon
extends Polygon {
    private GameLinkedList<Point> points = new GameLinkedList();

    public ExpandingPolygon() {
        this.updatePolygon();
    }

    public ExpandingPolygon(Shape ... shapes) {
        for (Shape shape : shapes) {
            this.addShape(shape);
        }
    }

    public ExpandingPolygon(Point ... points) {
        for (Point point : points) {
            this.addPoint(point);
        }
    }

    public void addShape(Shape shape) {
        int SG;
        float[] coords = new float[6];
        PathIterator it = shape.getPathIterator(null);
        while (!it.isDone() && (SG = it.currentSegment(coords)) != 4) {
            if (SG == 0 || SG == 1) {
                this.addPoint(new Point((int)coords[0], (int)coords[1]));
            }
            it.next();
        }
    }

    public void addPoint(Point p) {
        if (this.points.size() < 2) {
            this.points.addLast(p);
        } else if (this.points.size() == 2) {
            Point first = this.points.getFirst();
            Point last = this.points.getLast();
            double d = (last.x - first.x) * (p.y - first.y) - (last.y - first.y) * (p.x - first.x);
            if (d < 0.0) {
                this.points.getFirstElement().insertAfter(p);
            } else {
                this.points.addLast(p);
            }
        } else {
            GameLinkedList.Element last = this.points.getLastElement();
            double lastDistance = ((Point)last.object).distance(p);
            GameLinkedList<TempLine> lines = new GameLinkedList<TempLine>();
            for (GameLinkedList.Element element : this.points.elements()) {
                Point current = (Point)element.object;
                double currentDistance = current.distance(p);
                double d = (current.x - ((Point)last.object).x) * (p.y - ((Point)last.object).y) - (current.y - ((Point)last.object).y) * (p.x - ((Point)last.object).x);
                lines.addLast(new TempLine(last, element, d, lastDistance + currentDistance));
                last = element;
                lastDistance = currentDistance;
            }
            GameLinkedList.Element best = lines.streamElements().filter(l -> ((TempLine)l.object).d < 0.0).min(Comparator.comparingDouble(l -> ((TempLine)l.object).distance)).orElse(null);
            if (best != null) {
                ((TempLine)best.object).p1.insertAfter(p);
                GameLinkedList.Element prev = best.prevWrap();
                while (((TempLine)prev.object).d < 0.0 && !((TempLine)prev.object).p2.isRemoved()) {
                    ((TempLine)prev.object).p2.remove();
                    if ((prev = prev.prevWrap()) != best) continue;
                }
                GameLinkedList.Element next = best.nextWrap();
                while (((TempLine)next.object).d < 0.0 && !((TempLine)next.object).p1.isRemoved()) {
                    ((TempLine)next.object).p1.remove();
                    if ((next = next.nextWrap()) != best) continue;
                    break;
                }
            } else {
                return;
            }
        }
        this.updatePolygon();
    }

    private void updatePolygon() {
        this.xpoints = new int[this.points.size()];
        this.ypoints = new int[this.points.size()];
        this.npoints = this.points.size();
        int i = 0;
        for (Point point : this.points) {
            this.xpoints[i] = point.x;
            this.ypoints[i] = point.y;
            ++i;
        }
        this.invalidate();
    }

    private static class TempLine {
        public GameLinkedList.Element p1;
        public GameLinkedList.Element p2;
        public double d;
        public double distance;

        public TempLine(GameLinkedList.Element p1, GameLinkedList.Element p2, double d, double distance) {
            this.p1 = p1;
            this.p2 = p2;
            this.d = d;
            this.distance = distance;
        }
    }
}

