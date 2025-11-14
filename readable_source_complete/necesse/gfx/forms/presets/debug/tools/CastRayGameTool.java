/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.CollisionPoint;
import necesse.level.maps.hudManager.HudDrawElement;

public class CastRayGameTool
extends MouseDebugGameTool {
    private boolean m1Down;
    private boolean m2Down;
    public Point p1;
    public Point p2;
    public int maxBounces = 0;
    public int distanceMultiplier = 1;
    public boolean hitMobs = false;
    public HudDrawElement hudElement;
    private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

    public CastRayGameTool(DebugForm parent, String name) {
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
        this.onKeyClick(77, event -> {
            this.hitMobs = !this.hitMobs;
            this.setKeyUsage(77, "Hit mobs: " + this.hitMobs);
            return true;
        }, "Hit mobs: " + this.hitMobs);
        this.onScroll(e -> {
            this.wheelBuffer.add((InputEvent)e);
            this.wheelBuffer.useScrollY(isPositive -> {
                if (isPositive) {
                    if (WindowManager.getWindow().isKeyDown(340)) {
                        ++this.distanceMultiplier;
                    } else {
                        ++this.maxBounces;
                    }
                } else if (WindowManager.getWindow().isKeyDown(340)) {
                    this.distanceMultiplier = Math.max(--this.distanceMultiplier, 1);
                } else {
                    this.maxBounces = Math.max(--this.maxBounces, 0);
                }
            });
            this.scrollUsage = "Max bounces: " + this.maxBounces + ", Distance mod: " + this.distanceMultiplier;
            return true;
        }, "");
        this.scrollUsage = "Max bounces: " + this.maxBounces + ", Distance mod: " + this.distanceMultiplier;
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
                        if (CastRayGameTool.this.p1 != null && CastRayGameTool.this.p2 != null) {
                            Renderer.drawLineRGBA(camera.getDrawX(CastRayGameTool.this.p1.x), camera.getDrawY(CastRayGameTool.this.p1.y), camera.getDrawX(CastRayGameTool.this.p2.x), camera.getDrawY(CastRayGameTool.this.p2.y), 1.0f, 1.0f, 0.0f, 1.0f);
                            double distance = CastRayGameTool.this.p1.distance(CastRayGameTool.this.p2) * (double)CastRayGameTool.this.distanceMultiplier;
                            RayLinkedList rays = GameUtils.castRay(CastRayGameTool.this.p1.x, (double)CastRayGameTool.this.p1.y, (double)(CastRayGameTool.this.p2.x - CastRayGameTool.this.p1.x), (double)(CastRayGameTool.this.p2.y - CastRayGameTool.this.p1.y), distance, 100.0, CastRayGameTool.this.maxBounces, line -> {
                                Stream<Rectangle> stream = this.getLevel().getCollisions((Shape)line, filter).stream().map(hit -> hit);
                                if (CastRayGameTool.this.hitMobs) {
                                    Stream<Mob> mobStream = (this).getLevel().entityManager.mobs.streamInRegionsShape((Shape)line, 1);
                                    Stream playersStream = (this).getLevel().entityManager.players.streamInRegionsShape((Shape)line, 1);
                                    Stream<Rectangle> concat = Stream.concat(mobStream, playersStream).map(Mob::getCollision).filter(line::intersects);
                                    stream = Stream.concat(stream, concat);
                                }
                                return CollisionPoint.getClosestCollision(stream, line, false);
                            });
                            FontManager.bit.drawString(camera.getDrawX(CastRayGameTool.this.p1.x), camera.getDrawY(CastRayGameTool.this.p1.y), "Distance: " + GameMath.toDecimals(distance, 2), new FontOptions(12).outline());
                            FontManager.bit.drawString(camera.getDrawX(CastRayGameTool.this.p1.x), camera.getDrawY(CastRayGameTool.this.p1.y) + 16, "Bounces: " + rays.size(), new FontOptions(12).outline());
                            for (Ray ray : rays) {
                                int x1 = camera.getDrawX((float)ray.getX1());
                                int y1 = camera.getDrawY((float)ray.getY1());
                                int x2 = camera.getDrawX((float)ray.getX2());
                                int y2 = camera.getDrawY((float)ray.getY2());
                                Renderer.drawLineRGBA(x1, y1, x2, y2, 1.0f, 0.0f, 0.0f, 1.0f);
                                if (ray.targetHit == null) continue;
                                Renderer.drawRectangleLines((Rectangle)ray.targetHit, camera, 1.0f, 0.0f, 1.0f, 1.0f);
                            }
                            FontManager.bit.drawString(camera.getDrawX(CastRayGameTool.this.p1.x), camera.getDrawY(CastRayGameTool.this.p1.y) + 32, "Ray distance: " + GameMath.toDecimals(rays.totalDist, 2), new FontOptions(12).outline());
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

