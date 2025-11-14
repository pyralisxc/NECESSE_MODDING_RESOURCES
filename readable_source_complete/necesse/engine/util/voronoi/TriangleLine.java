/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.voronoi;

import java.awt.geom.Point2D;
import java.util.Objects;
import necesse.engine.util.voronoi.TriangleData;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;

public class TriangleLine {
    public final Point2D.Float p1;
    public final Point2D.Float p2;

    public TriangleLine(Point2D.Float p1, Point2D.Float p2) {
        if (TriangleData.compare(p1, p2) == -1) {
            this.p1 = p1;
            this.p2 = p2;
        } else {
            this.p2 = p1;
            this.p1 = p2;
        }
    }

    public DrawOptions getDrawOptions(GameCamera camera) {
        DrawOptionsList drawOptions = new DrawOptionsList(3);
        int x1 = camera.getDrawX(this.p1.x);
        int y1 = camera.getDrawY(this.p1.y);
        int x2 = camera.getDrawX(this.p2.x);
        int y2 = camera.getDrawY(this.p2.y);
        drawOptions.add(() -> Renderer.drawLineRGBA(x1, y1, x2, y2, 0.0f, 1.0f, 1.0f, 1.0f));
        return drawOptions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TriangleLine) {
            TriangleLine that = (TriangleLine)o;
            return this.p1.equals(that.p1) && this.p2.equals(that.p2);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.p1, this.p2);
    }

    public String toString() {
        return "L[" + this.p1.x + "x" + this.p1.y + ", " + this.p2.x + "x" + this.p2.y + "]";
    }
}

