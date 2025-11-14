/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.voronoi;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;

public class TriangleData {
    public final Point2D.Float p1;
    public final Point2D.Float p2;
    public final Point2D.Float p3;
    public final Point2D.Float average;
    boolean complete;
    public static Comparator<Point2D.Float> comparator = (p1, p2) -> Float.compare(p1.x, p2.x);

    public TriangleData(Point2D.Float p1, Point2D.Float p2, Point2D.Float p3) {
        Point2D.Float[] floats = new Point2D.Float[]{p1, p2, p3};
        Arrays.sort(floats, comparator);
        this.p1 = floats[0];
        this.p2 = floats[1];
        this.p3 = floats[2];
        this.average = new Point2D.Float((p1.x + p2.x + p3.x) / 3.0f, (p1.y + p2.y + p3.y) / 3.0f);
    }

    public DrawOptions getDrawOptions(GameCamera camera) {
        DrawOptionsList drawOptions = new DrawOptionsList(3);
        int x1 = camera.getDrawX(this.p1.x);
        int y1 = camera.getDrawY(this.p1.y);
        int x2 = camera.getDrawX(this.p2.x);
        int y2 = camera.getDrawY(this.p2.y);
        int x3 = camera.getDrawX(this.p3.x);
        int y3 = camera.getDrawY(this.p3.y);
        drawOptions.add(() -> Renderer.drawLineRGBA(x1, y1, x2, y2, 0.0f, 0.0f, 1.0f, 1.0f));
        drawOptions.add(() -> Renderer.drawLineRGBA(x2, y2, x3, y3, 0.0f, 0.0f, 1.0f, 1.0f));
        drawOptions.add(() -> Renderer.drawLineRGBA(x3, y3, x1, y1, 0.0f, 0.0f, 1.0f, 1.0f));
        return drawOptions;
    }

    public String toString() {
        return "L[" + this.p1.x + "x" + this.p1.y + ", " + this.p2.x + "x" + this.p2.y + ", " + this.p3.x + "x" + this.p3.y + "]";
    }

    public static int compare(Point2D.Float p1, Point2D.Float p2) {
        return Objects.compare(p1, p2, comparator);
    }

    static {
        comparator = comparator.thenComparing((p1, p2) -> Float.compare(p1.y, p2.y));
    }
}

