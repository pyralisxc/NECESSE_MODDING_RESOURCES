/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.entity.trails;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.util.GameMath;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailPointList;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import org.lwjgl.opengl.GL11;

public class TrailDrawSection {
    public final Trail trail;
    public int pointsStartIndex;
    public int pointsEndIndex;
    public int yTile;

    public TrailDrawSection(Trail trail, int yTile, int pointsStartIndex, int pointsEndIndex) {
        this.trail = trail;
        this.yTile = yTile;
        this.pointsStartIndex = pointsStartIndex;
        this.pointsEndIndex = pointsEndIndex;
    }

    public static TrailPointConsumer fadeLightColorSetter(int fadeInTime, int fadeOutTime, Level level, Color col) {
        return TrailDrawSection.fadeLightColorSetter(level == null ? 0L : level.getWorldEntity().getLocalTime(), fadeInTime, fadeOutTime, level, col);
    }

    public static TrailPointConsumer fadeLightColorSetter(long globalTime, int fadeInTime, int fadeOutTime, Level level, Color col) {
        return (p1, p2, time) -> {
            GameLight light = level == null ? new GameLight(150.0f) : level.getLightLevel(GameMath.getTileCoordinate(p1.getX()), GameMath.getTileCoordinate(p1.getY()));
            float lifeTime = globalTime - time;
            float fadeInAlpha = fadeInTime > 0 ? Math.min(lifeTime / (float)fadeInTime, 1.0f) : 1.0f;
            float fadeOutAlpha = fadeOutTime > 0 ? 1.0f - Math.min(lifeTime / (float)fadeOutTime, 1.0f) : 1.0f;
            light.getGLColorSetter((float)col.getRed() / 255.0f, (float)col.getGreen() / 255.0f, (float)col.getBlue() / 255.0f, (float)col.getAlpha() / 255.0f * fadeInAlpha * fadeOutAlpha).run();
        };
    }

    public static TrailPointConsumer lightColorSetter(Level level, Color col) {
        return (p1, p2, time) -> {
            GameLight light = level == null ? new GameLight(150.0f) : level.getLightLevel(GameMath.getTileCoordinate(p1.getX()), GameMath.getTileCoordinate(p1.getY()));
            light.getGLColorSetter((float)col.getRed() / 255.0f, (float)col.getGreen() / 255.0f, (float)col.getBlue() / 255.0f, (float)col.getAlpha() / 255.0f).run();
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DrawOptions getSpriteTrailsDraw(GameSprite sprite, GameCamera camera, TrailPointConsumer colorSetter) {
        Trail trail = this.trail;
        synchronized (trail) {
            TrailPointList points = this.trail.points;
            int pointsStartIndex = this.pointsStartIndex;
            int pointsEndIndex = this.pointsEndIndex;
            return TrailDrawSection.getSpriteTrailsDraw(sprite, points, pointsStartIndex, pointsEndIndex, camera, colorSetter);
        }
    }

    public static DrawOptions getSpriteTrailsDraw(GameSprite sprite, TrailPointList points, int pointsStartIndex, int pointsEndIndex, GameCamera camera, TrailPointConsumer colorSetter) {
        int width = sprite.texture.getWidth();
        int height = sprite.texture.getHeight();
        float leftXCoord = TextureDrawOptions.pixel(sprite.spriteX, 1, sprite.spriteWidth, width);
        float rightXCoord = TextureDrawOptions.pixel(sprite.spriteX + 1, -1, sprite.spriteWidth, width);
        float topYCoord = TextureDrawOptions.pixel(sprite.spriteY, 1, sprite.spriteHeight, height);
        float botYCoord = TextureDrawOptions.pixel(sprite.spriteY + 1, -1, sprite.spriteHeight, height);
        LinkedList<TrailDrawOption> drawPoints = new LinkedList<TrailDrawOption>();
        pointsEndIndex = Math.min(pointsEndIndex, points.size() - 1);
        for (int i = pointsStartIndex; i <= pointsEndIndex; ++i) {
            drawPoints.add(new TrailDrawOption(points.get(i), i % 2));
        }
        return () -> {
            sprite.texture.bindTexture();
            GL11.glLoadIdentity();
            GL11.glBegin((int)5);
            for (TrailDrawOption drawPoint : drawPoints) {
                Point2D p1 = drawPoint.draw1;
                int x1 = camera.getDrawX((int)p1.getX());
                int y1 = camera.getDrawY((int)(p1.getY() - (double)drawPoint.height));
                Point2D p2 = drawPoint.draw2;
                int x2 = camera.getDrawX((int)p2.getX());
                int y2 = camera.getDrawY((int)(p2.getY() - (double)drawPoint.height));
                if (p1.getX() == 0.0 && p1.getY() == 0.0 || p2.getX() == 0.0 && p2.getY() == 0.0) continue;
                colorSetter.apply(p1, p2, drawPoint.spawnTime);
                if (drawPoint.tex == 0) {
                    GL11.glTexCoord2f((float)leftXCoord, (float)topYCoord);
                } else {
                    GL11.glTexCoord2f((float)leftXCoord, (float)botYCoord);
                }
                GL11.glVertex2f((float)x1, (float)y1);
                if (drawPoint.tex == 0) {
                    GL11.glTexCoord2f((float)rightXCoord, (float)topYCoord);
                } else {
                    GL11.glTexCoord2f((float)rightXCoord, (float)botYCoord);
                }
                GL11.glVertex2f((float)x2, (float)y2);
            }
            GL11.glEnd();
        };
    }

    @FunctionalInterface
    public static interface TrailPointConsumer {
        public void apply(Point2D var1, Point2D var2, long var3);
    }

    private static class TrailDrawOption {
        public final Point2D draw1;
        public final Point2D draw2;
        public final long spawnTime;
        public final float height;
        public final int tex;

        public TrailDrawOption(TrailPointList.TrailPoint point, int tex) {
            this.draw1 = point.getDrawPos1();
            this.draw2 = point.getDrawPos2();
            this.spawnTime = point.spawnTime;
            this.height = point.vector.height;
            this.tex = tex;
        }
    }
}

