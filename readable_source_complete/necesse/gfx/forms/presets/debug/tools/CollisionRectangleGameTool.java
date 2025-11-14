/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.ExpandingPolygon;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.hudManager.HudDrawElement;

public class CollisionRectangleGameTool
extends MouseDebugGameTool {
    private boolean m1Down;
    private boolean m2Down;
    public Point r1p1;
    public Point r1p2;
    public Point r2p1;
    public Point r2p2;
    public HudDrawElement hudElement;

    public CollisionRectangleGameTool(DebugForm parent, String name) {
        super(parent, name);
        this.onLeftEvent(e -> {
            this.m1Down = e.state;
            if (e.state) {
                this.r1p1 = new Point(this.getMouseX(), this.getMouseY());
            }
            this.r1p2 = new Point(this.getMouseX(), this.getMouseY());
            return true;
        }, "Select rectangle 1");
        this.onRightEvent(e -> {
            this.m2Down = e.state;
            if (e.state) {
                this.r2p1 = new Point(this.getMouseX(), this.getMouseY());
            }
            this.r2p2 = new Point(this.getMouseX(), this.getMouseY());
            return true;
        }, "Select rectangle 2");
        this.onMouseMove(e -> {
            if (this.m1Down) {
                this.r1p2 = new Point(this.getMouseX(), this.getMouseY());
            }
            if (this.m2Down) {
                this.r2p2 = new Point(this.getMouseX(), this.getMouseY());
            }
            e.useMove();
            return false;
        });
    }

    @Override
    public void init() {
        this.r1p1 = null;
        this.r1p2 = null;
        this.r2p1 = null;
        this.r2p2 = null;
        final CollisionFilter filter = new CollisionFilter().mobCollision();
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return -10000;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        Rectangle r1 = CollisionRectangleGameTool.toRectangle(CollisionRectangleGameTool.this.r1p1, CollisionRectangleGameTool.this.r1p2);
                        Rectangle r2 = CollisionRectangleGameTool.toRectangle(CollisionRectangleGameTool.this.r2p1, CollisionRectangleGameTool.this.r2p2);
                        Shape shape = null;
                        if (r1 != null) {
                            shape = r1;
                            Renderer.drawShape(r1, camera, true, 0.0f, 1.0f, 0.0f, 0.5f);
                        }
                        if (r2 != null) {
                            shape = r2;
                            Renderer.drawShape(r2, camera, true, 0.0f, 0.0f, 1.0f, 0.5f);
                        }
                        if (r1 != null && r2 != null) {
                            ExpandingPolygon rect = new ExpandingPolygon(r1, r2);
                            shape = rect;
                            Rectangle bounds = shape.getBounds();
                            int centerX = (int)bounds.getCenterX();
                            int centerY = (int)bounds.getCenterY();
                            FontManager.bit.drawString(camera.getDrawX(centerX), camera.getDrawY(centerY), "" + rect.npoints, new FontOptions(16).outline());
                        }
                        if (shape != null) {
                            Renderer.drawShape(shape, camera, false, 1.0f, 1.0f, 0.0f, 1.0f);
                            ArrayList<LevelObjectHit> collisions = this.getLevel().getCollisions(shape, filter);
                            for (LevelObjectHit collision : collisions) {
                                Renderer.drawRectangleLines(collision, camera, 1.0f, 0.0f, 0.0f, 1.0f);
                            }
                        }
                    }
                });
            }
        };
        this.getLevel().hudManager.addElement(this.hudElement);
    }

    private static Rectangle toRectangle(Point p1, Point p2) {
        return p1 != null && p2 != null ? new Rectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y)) : null;
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
}

