/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.engine;

import java.util.ArrayList;
import necesse.engine.GameBezierPoint;
import necesse.engine.util.GameMath;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.opengl.GL11;

public class GameBezierCurve {
    public ArrayList<GameBezierPoint> points = new ArrayList();

    public void draw(float offsetX, float offsetY, float curvePrecision) {
        GameTexture.unbindTexture();
        GL11.glLoadIdentity();
        GL11.glBegin((int)1);
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)1.0f, (float)1.0f);
        for (GameBezierPoint point : this.points) {
            GL11.glVertex2f((float)(point.startX + offsetX), (float)(point.startY + offsetY));
            GL11.glVertex2f((float)(point.targetX + offsetX), (float)(point.targetY + offsetY));
        }
        GL11.glEnd();
        for (GameBezierPoint point : this.points) {
            GL11.glBegin((int)2);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)0.0f, (float)1.0f);
            int segments = 20;
            int radius = 5;
            for (int i = 0; i < segments; ++i) {
                double theta = Math.PI * 2 * (double)i / (double)segments;
                double x = (double)radius * Math.cos(theta);
                double y = (double)radius * Math.sin(theta);
                GL11.glVertex2d((double)(x + (double)point.startX + (double)offsetX), (double)(y + (double)point.startY + (double)offsetY));
            }
            GL11.glEnd();
        }
        GL11.glBegin((int)3);
        GL11.glColor4f((float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        for (int i = 0; i < this.points.size() - 1; ++i) {
            GameBezierPoint first = this.points.get(i);
            GameBezierPoint second = this.points.get(i + 1);
            float deltaX = second.targetX - second.startX;
            float deltaY = second.targetY - second.startY;
            second = new GameBezierPoint(second.startX, second.startY, second.startX - deltaX, second.startY - deltaY);
            float distance = GameMath.getExactDistance(first.startX, first.startY, first.targetX, first.targetY) + GameMath.getExactDistance(first.targetX, first.targetY, second.startX, second.startY) + GameMath.getExactDistance(first.startX, first.startY, first.targetX, first.targetY);
            int drawIterations = (int)(distance / curvePrecision) + 1;
            for (int drawIteration = 0; drawIteration <= drawIterations; ++drawIteration) {
                float progress = (float)drawIteration / (float)drawIterations;
                float drawX = first.getPointXOnCurve(second, progress) + offsetX;
                float drawY = first.getPointYOnCurve(second, progress) + offsetY;
                GL11.glVertex2f((float)drawX, (float)drawY);
            }
        }
        GL11.glEnd();
    }
}

