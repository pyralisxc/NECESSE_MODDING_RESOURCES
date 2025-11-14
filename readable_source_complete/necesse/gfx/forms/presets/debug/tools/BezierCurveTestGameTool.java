/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.util.List;
import necesse.engine.GameBezierCurve;
import necesse.engine.GameBezierPoint;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.maps.hudManager.HudDrawElement;

public class BezierCurveTestGameTool
extends MouseDebugGameTool {
    public Point clickDownPoint;
    public GameBezierCurve curve = new GameBezierCurve();
    public HudDrawElement hudElement;

    public BezierCurveTestGameTool(DebugForm parent, String name) {
        super(parent, name);
    }

    @Override
    public void init() {
        this.onLeftEvent(e -> {
            Point nextPoint = new Point(this.getMouseX(), this.getMouseY());
            if (e.state) {
                this.clickDownPoint = nextPoint;
            } else {
                if (this.clickDownPoint != null) {
                    this.curve.points.add(new GameBezierPoint(this.clickDownPoint.x, this.clickDownPoint.y, nextPoint.x, nextPoint.y));
                }
                this.clickDownPoint = null;
            }
            return true;
        }, "Add point");
        this.onRightClick(e -> {
            int bestIndex = -1;
            double bestDistance = 0.0;
            int mouseX = this.getMouseX();
            int mouseY = this.getMouseY();
            for (int i = 0; i < this.curve.points.size(); ++i) {
                GameBezierPoint current = this.curve.points.get(i);
                float distance = GameMath.getExactDistance(mouseX, mouseY, current.startX, current.startY);
                if (bestIndex >= 0 && !((double)distance < bestDistance)) continue;
                bestDistance = distance;
                bestIndex = i;
            }
            if (bestIndex >= 0) {
                this.curve.points.remove(bestIndex);
            }
            return true;
        }, "Remove point");
        this.onKeyClick(67, e -> {
            this.curve.points.clear();
            return true;
        }, "Clear points");
        this.setupHudElement();
    }

    public void setupHudElement() {
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        BezierCurveTestGameTool.this.curve.draw(-camera.getX(), -camera.getY(), 5.0f);
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

