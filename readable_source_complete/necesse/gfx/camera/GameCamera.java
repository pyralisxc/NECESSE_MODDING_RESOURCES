/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.camera;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.Entity;
import necesse.level.maps.Level;

public class GameCamera
implements SoundEmitter {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public GameCamera(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GameCamera(Rectangle rectangle) {
        this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public GameCamera(int x, int y) {
        this(x, y, WindowManager.getWindow().getSceneWidth(), WindowManager.getWindow().getSceneHeight());
    }

    public GameCamera() {
        this(0, 0);
    }

    public void updateToSceneDimensions() {
        GameWindow window = WindowManager.getWindow();
        this.setDimensions(window.getSceneWidth(), window.getSceneHeight());
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void limitToLevel(Level level) {
        if (level.tileWidth > 0 && this.width <= GameMath.getLevelCoordinate(level.tileWidth)) {
            this.x = level.limitLevelXToBounds(this.x, this.width, 0);
        }
        if (level.tileHeight > 0 && this.height <= GameMath.getLevelCoordinate(level.tileHeight)) {
            this.y = level.limitLevelYToBounds(this.y, this.height, 0);
        }
    }

    public int getDrawX(int levelX) {
        return levelX - this.getX();
    }

    public int getDrawY(int levelY) {
        return levelY - this.getY();
    }

    public int getDrawX(float levelX) {
        return this.getDrawX((int)levelX);
    }

    public int getDrawY(float levelY) {
        return this.getDrawY((int)levelY);
    }

    public int getTileDrawX(int tileX) {
        return this.getDrawX(tileX * 32);
    }

    public int getTileDrawY(int tileY) {
        return this.getDrawY(tileY * 32);
    }

    public int getMouseLevelPosX(InputPosition pos) {
        return pos.sceneX + this.getX();
    }

    public int getMouseLevelPosX(InputEvent event) {
        return this.getMouseLevelPosX(event.pos);
    }

    public int getMouseLevelPosX() {
        return this.getMouseLevelPosX(WindowManager.getWindow().mousePos());
    }

    public int getMouseLevelPosY(InputPosition pos) {
        return pos.sceneY + this.getY();
    }

    public int getMouseLevelPosY(InputEvent event) {
        return this.getMouseLevelPosY(event.pos);
    }

    public int getMouseLevelPosY() {
        return this.getMouseLevelPosY(WindowManager.getWindow().mousePos());
    }

    public int getMouseLevelTilePosX(InputPosition pos) {
        return GameMath.getTileCoordinate(this.getMouseLevelPosX(pos));
    }

    public int getMouseLevelTilePosX(InputEvent event) {
        return GameMath.getTileCoordinate(this.getMouseLevelPosX(event));
    }

    public int getMouseLevelTilePosX() {
        return GameMath.getTileCoordinate(this.getMouseLevelPosX());
    }

    public int getMouseLevelTilePosY(InputPosition pos) {
        return GameMath.getTileCoordinate(this.getMouseLevelPosY(pos));
    }

    public int getMouseLevelTilePosY(InputEvent event) {
        return GameMath.getTileCoordinate(this.getMouseLevelPosY(event));
    }

    public int getMouseLevelTilePosY() {
        return GameMath.getTileCoordinate(this.getMouseLevelPosY());
    }

    public Point getMouseLevelPos(InputPosition pos) {
        return new Point(this.getMouseLevelPosX(pos), this.getMouseLevelPosY(pos));
    }

    public Point getMouseLevelPos(InputEvent event) {
        return new Point(this.getMouseLevelPosX(event), this.getMouseLevelPosY(event));
    }

    public Point getMouseLevelPos() {
        return new Point(this.getMouseLevelPosX(), this.getMouseLevelPosY());
    }

    public Point getMouseLevelTilePos(InputPosition pos) {
        return new Point(this.getMouseLevelTilePosX(pos), this.getMouseLevelTilePosY(pos));
    }

    public Point getMouseLevelTilePos(InputEvent event) {
        return new Point(this.getMouseLevelTilePosX(event), this.getMouseLevelTilePosY(event));
    }

    public Point getMouseLevelTilePos() {
        return new Point(this.getMouseLevelTilePosX(), this.getMouseLevelTilePosY());
    }

    public void centerCamera(int x, int y) {
        this.setPosition(x - this.width / 2, y - this.height / 2);
    }

    public void centerCamera(Point pos) {
        this.centerCamera(pos.x, pos.y);
    }

    public void centerCamera(Entity entity) {
        this.centerCamera(entity.getX(), entity.getY());
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getStartTileX() {
        return GameMath.getTileCoordinate(this.getX());
    }

    public int getStartTileY() {
        return GameMath.getTileCoordinate(this.getY());
    }

    public int getEndTileX() {
        return GameMath.getTileCoordinate(this.getX() + this.getWidth());
    }

    public int getEndTileY() {
        return GameMath.getTileCoordinate(this.getY() + this.getHeight());
    }

    public Rectangle getBounds() {
        return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public float getSoundPositionX() {
        return (float)this.getX() + (float)this.getWidth() / 2.0f;
    }

    @Override
    public float getSoundPositionY() {
        return (float)this.getY() + (float)this.getHeight() / 2.0f;
    }
}

