/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.engine.util;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.opengl.GL11;

public class LineHitbox
extends Polygon {
    private final float width;
    private boolean circular = false;
    private float circleX;
    private float circleY;

    public LineHitbox(Line2D line, float width) {
        this.width = width;
        this.calculatePolygon(line);
    }

    public LineHitbox(Line2D line, float circleX, float circleY, float width) {
        this.width = width;
        this.calculatePolygon(line);
        this.circular = true;
        this.circleX = circleX;
        this.circleY = circleY;
    }

    public LineHitbox(float x, float y, float dx, float dy, float length, float width) {
        this.width = width;
        Point2D.Float p = GameMath.normalize(dx, dy);
        this.calculatePolygon(x, y, p.x, p.y, length);
    }

    public LineHitbox(float fromX, float fromY, float toX, float toY, float width) {
        this.width = width;
        Point2D.Float tempPoint = new Point2D.Float(toX - fromX, toY - fromY);
        float dist = (float)tempPoint.distance(0.0, 0.0);
        float normX = dist == 0.0f ? 0.0f : tempPoint.x / dist;
        float normY = dist == 0.0f ? 0.0f : tempPoint.y / dist;
        this.calculatePolygon(fromX, fromY, normX, normY, dist);
    }

    public static LineHitbox fromAngled(float fromX, float fromY, float angle, float length, float width) {
        angle = GameMath.fixAngle(angle);
        float dx = (float)Math.cos(Math.toRadians(angle - 90.0f));
        float dy = (float)Math.sin(Math.toRadians(angle - 90.0f));
        return new LineHitbox(fromX, fromY, dx, dy, length, width);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        boolean intersects = super.intersects(x, y, w, h);
        if (intersects && this.circular) {
            double midX = x + w / 2.0;
            double midY = y + h / 2.0;
            Point2D.Float normalize = GameMath.normalize((float)midX - this.circleX, (float)midY - this.circleY);
            Line2D.Float line = new Line2D.Float(this.circleX, this.circleY, this.circleX + normalize.x * this.width / 2.0f, this.circleY + normalize.y * this.width / 2.0f);
            return line.intersects(x, y, w, h);
        }
        return intersects;
    }

    protected void calculatePolygon(float x, float y, float dx, float dy, float length) {
        this.calculatePolygon(new Line2D.Float(x, y, x + dx * length, y + dy * length));
    }

    protected void calculatePolygon(Line2D line) {
        Point2D.Float start = new Point2D.Float((float)line.getX1(), (float)line.getY1());
        Point2D.Float end = new Point2D.Float((float)line.getX2(), (float)line.getY2());
        Point2D.Float dir = GameMath.normalize(start.x - end.x, start.y - end.y);
        Point2D.Float start1 = GameMath.getPerpendicularPoint(start, -this.width / 2.0f, dir);
        Point2D.Float start2 = GameMath.getPerpendicularPoint(start, this.width / 2.0f, dir);
        Point2D.Float end1 = GameMath.getPerpendicularPoint(end, -this.width / 2.0f, dir);
        Point2D.Float end2 = GameMath.getPerpendicularPoint(end, this.width / 2.0f, dir);
        this.xpoints = new int[]{(int)start1.x, (int)start2.x, (int)end2.x, (int)end1.x};
        this.ypoints = new int[]{(int)start1.y, (int)start2.y, (int)end2.y, (int)end1.y};
        this.npoints = 4;
    }

    public void draw(float red, float green, float blue, float alpha) {
        GameTexture.unbindTexture();
        GL11.glLoadIdentity();
        GL11.glBegin((int)5);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glVertex2f((float)this.xpoints[1], (float)this.ypoints[1]);
        GL11.glVertex2f((float)this.xpoints[0], (float)this.ypoints[0]);
        GL11.glVertex2f((float)this.xpoints[2], (float)this.ypoints[2]);
        GL11.glVertex2f((float)this.xpoints[3], (float)this.ypoints[3]);
        GL11.glEnd();
    }
}

