/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.entity.chains;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.chains.ChainLocation;
import necesse.entity.chains.StaticChainLocation;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import org.lwjgl.opengl.GL11;

public class Chain {
    public ChainLocation pos1;
    public ChainLocation pos2;
    public float height;
    private boolean removed;
    private int drawStart;
    private float m;
    private boolean calculatedM;
    public boolean drawOnTop;
    public int drawOnTopOrder = -10000;
    public GameSprite sprite = new GameSprite(GameResources.chains, 1, 0, 32);

    public Chain(ChainLocation pos1, ChainLocation pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Chain(int x1, int y1, int x2, int y2) {
        this(new StaticChainLocation(x1, y1), new StaticChainLocation(x2, y2));
    }

    public int getMaxY() {
        return Math.max(this.pos1.getY(), this.pos2.getY());
    }

    public int getMinY() {
        return Math.min(this.pos1.getY(), this.pos2.getY());
    }

    public float getXPos(float y) {
        if (y < (float)this.getMinY() || y > (float)this.getMaxY()) {
            return -1.0f;
        }
        if (!this.calculatedM) {
            float xDif = this.pos2.getX() - this.pos1.getX();
            float yDif = this.pos2.getY() - this.pos1.getY();
            this.m = xDif == 0.0f ? 0.0f : yDif / xDif;
            this.calculatedM = true;
        }
        if (this.m == 0.0f) {
            return this.pos1.getX();
        }
        return (y - (float)this.pos1.getY() + this.m * (float)this.pos1.getX()) / this.m;
    }

    public void addDrawables(OrderableDrawables list, int startTileY, int endTileY, Level level, TickManager tickManager, GameCamera camera) {
        final ArrayList<LevelSortedDrawable> sortedDrawables = new ArrayList<LevelSortedDrawable>();
        this.addDrawables(sortedDrawables, startTileY, endTileY, level, tickManager, camera);
        sortedDrawables.sort(null);
        list.add(this.drawOnTopOrder, new Drawable(){

            @Override
            public void draw(TickManager tickManager) {
                sortedDrawables.forEach(d -> d.draw(tickManager));
            }
        });
    }

    public void addDrawables(List<LevelSortedDrawable> list, int startTileY, int endTileY, Level level, TickManager tickManager, GameCamera camera) {
        this.resetDraw();
        for (int i = startTileY; i < endTileY; ++i) {
            int startY = i * 32;
            int endY = startY + 32;
            if (startY < this.getMinY() && endY < this.getMinY() || startY > this.getMaxY() && endY > this.getMaxY()) continue;
            final int sortY = startY + this.getDrawY();
            final DrawOptions options = this.getDrawSection(startY, endY, level, camera);
            list.add(new LevelSortedDrawable(this){

                @Override
                public int getSortY() {
                    return sortY;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "chainDraw", options::draw);
                }
            });
        }
    }

    private DrawOptions getDrawSection(int startY, int endY, Level level, GameCamera camera) {
        int startX = (int)this.getXPos(startY);
        int endX = (int)this.getXPos(endY);
        if (endX == -1) {
            endY = this.getMaxY();
            endX = (int)this.getXPos(endY);
        }
        if (startX == -1) {
            startY = this.getMinY();
            startX = (int)this.getXPos(startY);
        }
        if (this.pos1.getY() == this.pos2.getY()) {
            startX = this.pos1.getX();
            endX = this.pos2.getX();
        }
        DrawOptionsList options = new DrawOptionsList();
        this.drawStart = this.addSpriteChainLightStartTextLevelDrawOptions(options, this.sprite, startX, startY, endX, endY, this.drawStart, this.height, level, camera);
        return options;
    }

    protected int addSpriteChainLightStartTextLevelDrawOptions(List<DrawOptions> options, GameSprite sprite, int x1, int y1, int x2, int y2, int start, float chainHeight, Level level, GameCamera camera) {
        Point2D.Float tempPoint = new Point2D.Float(x2 - x1, y2 - y1);
        float dist = (float)tempPoint.distance(0.0, 0.0);
        float normX = tempPoint.x / dist;
        float normY = tempPoint.y / dist;
        float halfWidth = (float)sprite.width / 2.0f;
        int height = sprite.height;
        float i = -start;
        float posX = x1;
        float posY = y1;
        while (i < dist) {
            if (i > dist - (float)sprite.height) {
                height = Math.abs((int)(i - dist));
            }
            i += (float)sprite.height;
            int size = Math.abs(start - height);
            float nextPosX = posX + normX * (float)size;
            float nextPosY = posY + normY * (float)size;
            float middlePosX = posX + normX * (float)size / 2.0f;
            float halfNextPosY = posY + normY * (float)size / 2.0f;
            GameLight light = new GameLight(150.0f);
            if (level != null) {
                light = level.getLightLevel(GameMath.getTileCoordinate(middlePosX), GameMath.getTileCoordinate(halfNextPosY));
            }
            Runnable glColorSetter = light.getGLColorSetter(1.0f, 1.0f, 1.0f, 1.0f);
            Point2D.Float drawPos1 = new Point2D.Float(camera.getDrawX(posX), (float)camera.getDrawY(posY) - chainHeight);
            Point2D.Float drawPos2 = new Point2D.Float(camera.getDrawX(nextPosX), (float)camera.getDrawY(nextPosY) - chainHeight);
            Point2D.Float quad1 = GameMath.getPerpendicularPoint(drawPos1, halfWidth, normX, normY);
            Point2D.Float quad2 = GameMath.getPerpendicularPoint(drawPos1, -halfWidth, normX, normY);
            Point2D.Float quad3 = GameMath.getPerpendicularPoint(drawPos2, -halfWidth, normX, normY);
            Point2D.Float quad4 = GameMath.getPerpendicularPoint(drawPos2, halfWidth, normX, normY);
            float spriteX1 = TextureDrawOptions.pixel(sprite.spriteX, sprite.spriteWidth, sprite.texture.getWidth());
            float spriteX2 = TextureDrawOptions.pixel(sprite.spriteX + 1, sprite.spriteWidth, sprite.texture.getWidth());
            float spriteY1 = TextureDrawOptions.pixel(sprite.spriteY, start, sprite.spriteHeight, sprite.texture.getHeight());
            float spriteY2 = TextureDrawOptions.pixel(sprite.spriteY, height, sprite.spriteHeight, sprite.texture.getHeight());
            options.add(() -> {
                sprite.texture.bindTexture();
                glColorSetter.run();
                GL11.glBegin((int)7);
                GL11.glTexCoord2f((float)spriteX1, (float)spriteY1);
                GL11.glVertex2f((float)quad1.x, (float)(quad1.y - 1.0f));
                GL11.glTexCoord2f((float)spriteX2, (float)spriteY1);
                GL11.glVertex2f((float)quad2.x, (float)(quad2.y - 1.0f));
                GL11.glTexCoord2f((float)spriteX2, (float)spriteY2);
                GL11.glVertex2f((float)quad3.x, (float)quad3.y);
                GL11.glTexCoord2f((float)spriteX1, (float)spriteY2);
                GL11.glVertex2f((float)quad4.x, (float)quad4.y);
                GL11.glEnd();
            });
            posX = nextPosX;
            posY = nextPosY;
            start = 0;
        }
        return height;
    }

    public void remove() {
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.pos1.removed() || this.pos2.removed() || this.removed;
    }

    public int getDrawY() {
        return (int)this.height;
    }

    public void resetDraw() {
        this.drawStart = 0;
        this.calculatedM = false;
    }
}

