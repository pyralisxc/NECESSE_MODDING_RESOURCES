/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.liquidManager.ClosestHeightResult;

public class FindClosestHeightGameTool
extends MouseDebugGameTool {
    public HudDrawElement hudElement;
    private Mode mode = Mode.NO_COLLISION;
    private int maxSameHeightTravel = 0;
    private int desiredHeight = 0;
    private Point fromTile;
    private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

    public FindClosestHeightGameTool(DebugForm parent, String name) {
        super(parent, name);
    }

    @Override
    public void init() {
        this.fromTile = null;
        this.onLeftClick(e -> {
            this.fromTile = new Point(this.getMouseTileX(), this.getMouseTileY());
            this.update();
            return true;
        }, "Start find");
        this.onRightClick(e -> {
            if (this.hudElement != null) {
                this.hudElement.remove();
            }
            return true;
        }, "Clear draw");
        this.onKeyClick(266, e -> {
            this.desiredHeight = Math.min(this.desiredHeight + 1, 10);
            this.update();
            return true;
        }, "Increase desired height");
        this.onKeyClick(267, e -> {
            this.desiredHeight = Math.max(this.desiredHeight - 1, -10);
            this.update();
            return true;
        }, "Decrease desired height");
        this.onKeyClick(77, e -> {
            Mode[] modes = Mode.values();
            this.mode = modes[Math.floorMod(this.mode.ordinal() + 1, modes.length)];
            this.update();
            return true;
        }, "");
        this.onScroll(e -> {
            this.wheelBuffer.add((InputEvent)e);
            int wheel = this.wheelBuffer.useAllScrollY();
            if (wheel != 0) {
                this.maxSameHeightTravel = Math.max(this.maxSameHeightTravel + wheel, 0);
                this.update();
            }
            return true;
        }, "");
        this.update();
    }

    public void update() {
        this.setKeyUsage(77, "Mode: " + (Object)((Object)this.mode));
        this.scrollUsage = "Max same height travel: " + this.maxSameHeightTravel;
        this.setLeftUsage("Find " + this.desiredHeight + " height");
        if (this.fromTile == null) {
            return;
        }
        Level level = this.getLevel();
        final ClosestHeightResult result = level.liquidManager.findClosestHeightTile(this.fromTile.x, this.fromTile.y, this.desiredHeight, this.maxSameHeightTravel, checkTile -> this.mode.validChecker.check(level, this.fromTile, (Point)checkTile));
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                int drawY;
                int drawX;
                final DrawOptionsList drawOptions = new DrawOptionsList();
                for (Point point : result.closedTiles) {
                    drawX = camera.getTileDrawX(point.x);
                    drawY = camera.getTileDrawY(point.y);
                    drawOptions.add(Renderer.initQuadDraw(32, 32).color(1.0f, 0.0f, 0.0f, 0.5f).pos(drawX, drawY));
                }
                for (Point point : result.openTiles) {
                    drawX = camera.getTileDrawX(point.x);
                    drawY = camera.getTileDrawY(point.y);
                    drawOptions.add(Renderer.initQuadDraw(32, 32).color(0.0f, 1.0f, 0.0f, 0.5f).pos(drawX, drawY));
                }
                int drawX2 = camera.getTileDrawX(result.startX);
                int n = camera.getTileDrawY(result.startY);
                drawOptions.add(Renderer.initQuadDraw(32, 32).color(0.0f, 0.0f, 1.0f).pos(drawX2, n));
                int drawX3 = camera.getTileDrawX(result.best.x);
                int n2 = camera.getTileDrawY(result.best.y);
                drawOptions.add(Renderer.initQuadDraw(32, 32).color(1.0f, 1.0f, 0.0f).pos(drawX3, n2));
                if (result.found != null) {
                    drawX3 = camera.getTileDrawX(result.found.x);
                    int n3 = camera.getTileDrawY(result.found.y);
                    drawOptions.add(Renderer.initQuadDraw(32, 32).color(0.0f, 1.0f, 0.0f).pos(drawX3, n3));
                }
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return Integer.MIN_VALUE;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        drawOptions.draw();
                    }
                });
            }
        };
        level.hudManager.addElement(this.hudElement);
    }

    @Override
    public void isCancelled() {
        super.isCancelled();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    @Override
    public void isCleared() {
        super.isCleared();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    private static enum Mode {
        NO_COLLISION((level, from, current) -> true),
        LINE_COLLISION((level, from, current) -> !level.collides(new Line2D.Float(from.x * 32 + 16, from.y * 32 + 16, current.x * 32 + 16, current.y * 32 + 16), new CollisionFilter().mobCollision())),
        LINE_WIDTH_16_COLLISION((level, from, current) -> !level.collides(new Line2D.Float(from.x * 32 + 16, from.y * 32 + 16, current.x * 32 + 16, current.y * 32 + 16), 16.0f, 2.0f, new CollisionFilter().mobCollision())),
        LINE_WIDTH_32_COLLISION((level, from, current) -> !level.collides(new Line2D.Float(from.x * 32 + 16, from.y * 32 + 16, current.x * 32 + 16, current.y * 32 + 16), 32.0f, 2.0f, new CollisionFilter().mobCollision()));

        public final ValidChecker validChecker;

        private Mode(ValidChecker validChecker) {
            this.validChecker = validChecker;
        }
    }

    private static interface ValidChecker {
        public boolean check(Level var1, Point var2, Point var3);
    }
}

