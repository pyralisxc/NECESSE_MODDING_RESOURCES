/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.IntersectionPoint;
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

public class CollisionPointGameTool
extends MouseDebugGameTool {
    private boolean m1Down;
    private boolean m2Down;
    public Point p1;
    public Point p2;
    public HudDrawElement hudElement;

    public CollisionPointGameTool(DebugForm parent, String name) {
        super(parent, name);
        this.onLeftEvent(e -> {
            this.m1Down = e.state;
            this.p1 = new Point(this.getMouseX(), this.getMouseY());
            return true;
        }, "Select point 1");
        this.onRightEvent(e -> {
            this.m2Down = e.state;
            this.p2 = new Point(this.getMouseX(), this.getMouseY());
            return true;
        }, "Select point 2");
        this.onMouseMove(e -> {
            if (this.m1Down) {
                this.p1 = new Point(this.getMouseX(), this.getMouseY());
            }
            if (this.m2Down) {
                this.p2 = new Point(this.getMouseX(), this.getMouseY());
            }
            e.useMove();
            return false;
        });
    }

    @Override
    public void init() {
        this.p1 = null;
        this.p2 = null;
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
                        if (CollisionPointGameTool.this.p1 != null && CollisionPointGameTool.this.p2 != null) {
                            FontManager.bit.drawString(camera.getDrawX(CollisionPointGameTool.this.p1.x), camera.getDrawY(CollisionPointGameTool.this.p1.y), "Distance: " + GameMath.toDecimals(CollisionPointGameTool.this.p1.distance(CollisionPointGameTool.this.p2), 2), new FontOptions(12).outline());
                            Renderer.drawLineRGBA(camera.getDrawX(CollisionPointGameTool.this.p1.x), camera.getDrawY(CollisionPointGameTool.this.p1.y), camera.getDrawX(CollisionPointGameTool.this.p2.x), camera.getDrawY(CollisionPointGameTool.this.p2.y), 1.0f, 1.0f, 0.0f, 1.0f);
                            Line2D.Float line = new Line2D.Float(CollisionPointGameTool.this.p1.x, CollisionPointGameTool.this.p1.y, CollisionPointGameTool.this.p2.x, CollisionPointGameTool.this.p2.y);
                            ArrayList<LevelObjectHit> collisions = this.getLevel().getCollisions(line, filter);
                            for (LevelObjectHit collision : collisions) {
                                Renderer.drawRectangleLines(collision, camera, 1.0f, 0.0f, 0.0f, 1.0f);
                            }
                            IntersectionPoint<LevelObjectHit> ip = this.getLevel().getCollisionPoint(collisions, line, true);
                            if (ip != null) {
                                Renderer.drawCircle(camera.getDrawX((int)ip.getX()), camera.getDrawY((int)ip.getY()), 5, 10, 1.0f, 0.0f, 0.0f, 1.0f, true);
                                FontManager.bit.drawString(camera.getDrawX((int)ip.getX()), camera.getDrawY((int)ip.getY()) + 5, "Dir: " + (Object)((Object)ip.dir), new FontOptions(16).colorf(1.0f, 0.0f, 0.0f).outline());
                            }
                        }
                    }
                });
            }
        };
        this.getLevel().hudManager.addElement(this.hudElement);
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

