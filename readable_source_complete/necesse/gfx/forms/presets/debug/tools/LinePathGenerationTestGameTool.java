/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.hudManager.HudDrawElement;

public class LinePathGenerationTestGameTool
extends MouseDebugGameTool {
    public Point p1;
    public Point p2;
    public Line2D.Float line;
    public GameLinkedList<Point> path;
    public HudDrawElement hudElement;

    public LinePathGenerationTestGameTool(DebugForm parent, String name) {
        super(parent, name);
        this.onLeftEvent(e -> {
            if (e.state) {
                this.p1 = new Point(this.getMouseTileX(), this.getMouseTileY());
                this.generatePath();
            }
            return true;
        }, "Select point 1");
        this.onRightEvent(e -> {
            if (e.state) {
                this.p2 = new Point(this.getMouseTileX(), this.getMouseTileY());
                this.generatePath();
            }
            return true;
        }, "Select point 2");
    }

    @Override
    public void init() {
        this.p1 = null;
        this.p2 = null;
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
                        if (LinePathGenerationTestGameTool.this.line != null) {
                            Renderer.drawLineRGBA(camera.getDrawX(LinePathGenerationTestGameTool.this.line.x1), camera.getDrawY(LinePathGenerationTestGameTool.this.line.y1), camera.getDrawX(LinePathGenerationTestGameTool.this.line.x2), camera.getDrawY(LinePathGenerationTestGameTool.this.line.y2), 1.0f, 0.0f, 0.0f, 1.0f);
                        }
                        if (LinePathGenerationTestGameTool.this.path != null) {
                            for (GameLinkedList.Element e : LinePathGenerationTestGameTool.this.path.elements()) {
                                Point current = (Point)e.object;
                                Renderer.drawRectangleLines(new Rectangle(current.x * 32, current.y * 32, 32, 32), camera, 0.0f, 1.0f, 1.0f, 1.0f);
                                GameLinkedList.Element prevE = e.prev();
                                Point prev = prevE == null ? null : (Point)prevE.object;
                                GameLinkedList.Element nextE = e.next();
                                Point next = nextE == null ? null : (Point)nextE.object;
                                Point start = new Point(current.x * 32 + 16, current.y * 32 + 16);
                                Point end = new Point(current.x * 32 + 16, current.y * 32 + 16);
                                if (prev != null) {
                                    if (prev.x < current.x) {
                                        start.x -= 16;
                                    } else if (prev.x > current.x) {
                                        start.x += 16;
                                    }
                                    if (prev.y < current.y) {
                                        start.y -= 16;
                                    } else if (prev.y > current.y) {
                                        start.y += 16;
                                    }
                                }
                                if (next != null) {
                                    if (next.x < current.x) {
                                        end.x -= 16;
                                    } else if (next.x > current.x) {
                                        end.x += 16;
                                    }
                                    if (next.y < current.y) {
                                        end.y -= 16;
                                    } else if (next.y > current.y) {
                                        end.y += 16;
                                    }
                                }
                                Renderer.drawLineRGBA(camera.getDrawX(start.x), camera.getDrawY(start.y), camera.getDrawX(end.x), camera.getDrawY(end.y), 1.0f, 1.0f, 0.0f, 1.0f);
                            }
                        }
                        if (LinePathGenerationTestGameTool.this.p1 != null) {
                            Renderer.drawRectangleLines(new Rectangle(LinePathGenerationTestGameTool.this.p1.x * 32, LinePathGenerationTestGameTool.this.p1.y * 32, 32, 32), camera, 0.0f, 1.0f, 0.0f, 1.0f);
                        }
                        if (LinePathGenerationTestGameTool.this.p2 != null) {
                            Renderer.drawRectangleLines(new Rectangle(LinePathGenerationTestGameTool.this.p2.x * 32, LinePathGenerationTestGameTool.this.p2.y * 32, 32, 32), camera, 0.0f, 0.0f, 1.0f, 1.0f);
                        }
                    }
                });
            }
        };
        this.getLevel().hudManager.addElement(this.hudElement);
    }

    private void generatePath() {
        if (this.p1 != null && this.p2 != null) {
            this.line = new Line2D.Float(this.p1.x * 32 + 16, this.p1.y * 32 + 16, this.p2.x * 32 + 16, this.p2.y * 32 + 16);
            this.path = new GameLinkedList();
            LinesGeneration.pathTiles(new Line2D.Float(this.p1, this.p2), true, (from, current) -> this.path.add((Point)current));
        }
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

