/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameUtils;
import necesse.engine.util.voronoi.DelaunayTriangulator;
import necesse.engine.util.voronoi.TriangleData;
import necesse.engine.util.voronoi.TriangleLine;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.level.maps.hudManager.HudDrawElement;

public class DelaunayTriangulatorGameTool
extends MouseDebugGameTool {
    public ControlForm controlForm;
    public HudDrawElement hudElement;
    public ArrayList<Point2D.Float> points = new ArrayList();
    public ArrayList<TriangleData> triangles = new ArrayList();
    public ArrayList<TriangleLine> voronoiLines = new ArrayList();

    public DelaunayTriangulatorGameTool(DebugForm parent) {
        super(parent, "DelaunayTriangulator");
    }

    @Override
    public void init() {
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                final DrawOptionsList drawOptions = new DrawOptionsList();
                if (DelaunayTriangulatorGameTool.this.triangles != null && DelaunayTriangulatorGameTool.this.controlForm.showTriangles.checked) {
                    for (TriangleData triangle : DelaunayTriangulatorGameTool.this.triangles) {
                        drawOptions.add(triangle.getDrawOptions(camera));
                    }
                }
                if (DelaunayTriangulatorGameTool.this.voronoiLines != null && DelaunayTriangulatorGameTool.this.controlForm.showVoronoi.checked) {
                    for (TriangleLine line : DelaunayTriangulatorGameTool.this.voronoiLines) {
                        drawOptions.add(line.getDrawOptions(camera));
                    }
                }
                if (DelaunayTriangulatorGameTool.this.controlForm.showPoints.checked) {
                    for (Point2D.Float point : DelaunayTriangulatorGameTool.this.points) {
                        int x = camera.getDrawX(point.x);
                        int y = camera.getDrawY(point.y);
                        drawOptions.add(Renderer.initQuadDraw(4, 4).color(1.0f, 0.0f, 0.0f).posMiddle(x, y));
                    }
                }
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return -10000;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        drawOptions.draw();
                    }
                });
            }
        };
        this.getLevel().hudManager.addElement(this.hudElement);
        this.updateInput();
        if (this.controlForm != null) {
            this.controlForm.invalidate();
        }
        if (GlobalData.getCurrentState() instanceof MainGame) {
            this.controlForm = new ControlForm(200);
            ((MainGame)GlobalData.getCurrentState()).formManager.addSidebar(this.controlForm);
        }
    }

    public void updateInput() {
        this.onLeftClick(e -> {
            this.points.add(new Point2D.Float(this.getMouseX(), this.getMouseY()));
            this.voronoiLines = new ArrayList<TriangleLine>();
            this.triangles = DelaunayTriangulator.compute(this.points, false, this.voronoiLines);
            return true;
        }, "Add point");
        this.onRightClick(e -> {
            int bestIndex = -1;
            double bestDistance = 0.0;
            for (int i = 0; i < this.points.size(); ++i) {
                Point2D.Float point = this.points.get(i);
                double distance = point.distance(this.getMouseX(), this.getMouseY());
                if (bestIndex != -1 && !(distance < bestDistance)) continue;
                bestIndex = i;
                bestDistance = distance;
            }
            if (bestIndex != -1) {
                this.points.remove(bestIndex);
                this.voronoiLines = new ArrayList<TriangleLine>();
                this.triangles = DelaunayTriangulator.compute(this.points, false, this.voronoiLines);
            }
            return true;
        }, "Remove point");
        this.onKeyClick(84, e -> {
            long time = System.currentTimeMillis();
            this.voronoiLines = new ArrayList<TriangleLine>();
            this.triangles = DelaunayTriangulator.compute(this.points, false, this.voronoiLines);
            System.out.println("Triangles: " + this.triangles.size() + ", Voronoi: " + this.voronoiLines.size() + " took " + GameUtils.getTimeStringMillis(System.currentTimeMillis() - time));
            return true;
        }, "Triangulate");
    }

    @Override
    public void isCancelled() {
        super.isCancelled();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
        if (this.controlForm != null) {
            this.controlForm.invalidate();
        }
    }

    @Override
    public void isCleared() {
        super.isCleared();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
        if (this.controlForm != null) {
            this.controlForm.invalidate();
        }
    }

    protected static class ControlForm
    extends SidebarForm {
        private boolean isValid = true;
        public FormCheckBox showPoints;
        public FormCheckBox showTriangles;
        public FormCheckBox showVoronoi;

        public ControlForm(int width) {
            super("DTControl", width, 100);
            FormFlow flow = new FormFlow(10);
            this.showPoints = this.addComponent(flow.nextY(new FormCheckBox("Show points", 5, 0, this.getWidth() - 10, true), 5));
            this.showTriangles = this.addComponent(flow.nextY(new FormCheckBox("Show triangles", 5, 0, this.getWidth() - 10, true), 5));
            this.showVoronoi = this.addComponent(flow.nextY(new FormCheckBox("Show voronoi", 5, 0, this.getWidth() - 10, true), 5));
            this.showPoints.handleClicksIfNoEventHandlers = true;
            this.showTriangles.handleClicksIfNoEventHandlers = true;
            this.showVoronoi.handleClicksIfNoEventHandlers = true;
            this.setHeight(flow.next() + 5);
        }

        @Override
        public boolean isValid(Client client) {
            return this.isValid;
        }

        public void invalidate() {
            this.isValid = false;
        }
    }
}

