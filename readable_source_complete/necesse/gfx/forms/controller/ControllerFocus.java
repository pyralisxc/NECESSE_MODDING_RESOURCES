/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.controller;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.List;
import necesse.engine.util.GameMath;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;

public class ControllerFocus {
    public final ControllerFocusHandler handler;
    public final Rectangle boundingBox;
    public final int initialFocusPriority;
    public final ControllerNavigationHandler customNavigationHandler;
    private Point boundingBoxCenter;

    public static void add(List<ControllerFocus> list, Rectangle area, ControllerFocusHandler handler, Rectangle boundingBox, int xOffset, int yOffset, int initialFocusPriority, ControllerNavigationHandler customNavigationHandler) {
        if (area.intersects(new Rectangle(boundingBox.x + xOffset, boundingBox.y + yOffset, boundingBox.width, boundingBox.height))) {
            list.add(new ControllerFocus(handler, boundingBox, xOffset, yOffset, initialFocusPriority, customNavigationHandler));
        }
    }

    public ControllerFocus(ControllerFocusHandler handler, Rectangle boundingBox, int xOffset, int yOffset, int initialFocusPriority, ControllerNavigationHandler customNavigationHandler) {
        this.handler = handler;
        this.boundingBox = new Rectangle(xOffset + boundingBox.x, yOffset + boundingBox.y, boundingBox.width, boundingBox.height);
        this.initialFocusPriority = initialFocusPriority;
        this.customNavigationHandler = customNavigationHandler;
    }

    public ControllerFocus(ControllerFocus copy, int xOffset, int yOffset) {
        this(copy.handler, copy.boundingBox, xOffset, yOffset, copy.initialFocusPriority, copy.customNavigationHandler);
    }

    public Point getTooltipAndFloatMenuPoint() {
        Point point = this.handler.getControllerTooltipAndFloatMenuPoint(this);
        if (point == null) {
            point = new Point(this.boundingBox.x, this.boundingBox.y);
        }
        return point;
    }

    public Point getBoundingBoxCenter() {
        if (this.boundingBoxCenter != null) {
            return this.boundingBoxCenter;
        }
        this.boundingBoxCenter = new Point(this.boundingBox.x + this.boundingBox.width / 2, this.boundingBox.y + this.boundingBox.height / 2);
        return this.boundingBoxCenter;
    }

    public static ControllerFocus getNext(int dir, FormManager manager, List<ControllerFocus> list) {
        ControllerFocus current = manager.getCurrentFocus();
        Comparator<ControllerFocus> comparator = current == null ? Comparator.comparingInt(c -> -c.initialFocusPriority) : Comparator.comparingInt(c -> {
            int perpendicularDistanceFrom;
            int straightDistanceFrom;
            if (current.handler == c.handler) {
                return Integer.MAX_VALUE;
            }
            int parallelOverlap = 0;
            switch (dir) {
                case 0: {
                    if (c.boundingBox.y >= current.boundingBox.y || c.boundingBox.y + c.boundingBox.height >= current.boundingBox.y + current.boundingBox.height) {
                        return Integer.MAX_VALUE;
                    }
                    straightDistanceFrom = current.boundingBox.y - (c.boundingBox.y + c.boundingBox.height);
                    if (current.boundingBox.x + current.boundingBox.width < c.boundingBox.x) {
                        perpendicularDistanceFrom = c.boundingBox.x - (current.boundingBox.x + current.boundingBox.width);
                        break;
                    }
                    if (current.boundingBox.x > c.boundingBox.x + c.boundingBox.width) {
                        perpendicularDistanceFrom = current.boundingBox.x - (c.boundingBox.x + c.boundingBox.width);
                        break;
                    }
                    perpendicularDistanceFrom = 0;
                    parallelOverlap = current.boundingBox.x < c.boundingBox.x ? current.boundingBox.x + current.boundingBox.width - c.boundingBox.x : c.boundingBox.x + c.boundingBox.width - current.boundingBox.x;
                    parallelOverlap = GameMath.min(c.boundingBox.width, current.boundingBox.width, parallelOverlap);
                    break;
                }
                case 1: {
                    if (c.boundingBox.x <= current.boundingBox.x || c.boundingBox.x + c.boundingBox.width <= current.boundingBox.x + current.boundingBox.width) {
                        return Integer.MAX_VALUE;
                    }
                    straightDistanceFrom = c.boundingBox.x - (current.boundingBox.x + current.boundingBox.width);
                    if (current.boundingBox.y + current.boundingBox.height < c.boundingBox.y) {
                        perpendicularDistanceFrom = c.boundingBox.y - (current.boundingBox.y + current.boundingBox.height);
                        break;
                    }
                    if (current.boundingBox.y > c.boundingBox.y + c.boundingBox.height) {
                        perpendicularDistanceFrom = current.boundingBox.y - (c.boundingBox.y + c.boundingBox.height);
                        break;
                    }
                    perpendicularDistanceFrom = 0;
                    parallelOverlap = current.boundingBox.y < c.boundingBox.y ? current.boundingBox.y + current.boundingBox.height - c.boundingBox.y : c.boundingBox.y + c.boundingBox.height - current.boundingBox.y;
                    parallelOverlap = GameMath.min(c.boundingBox.height, current.boundingBox.height, parallelOverlap);
                    break;
                }
                case 2: {
                    if (c.boundingBox.y <= current.boundingBox.y || c.boundingBox.y + c.boundingBox.height <= current.boundingBox.y + current.boundingBox.height) {
                        return Integer.MAX_VALUE;
                    }
                    straightDistanceFrom = c.boundingBox.y - (current.boundingBox.y + current.boundingBox.height);
                    if (current.boundingBox.x + current.boundingBox.width < c.boundingBox.x) {
                        perpendicularDistanceFrom = c.boundingBox.x - (current.boundingBox.x + current.boundingBox.width);
                        break;
                    }
                    if (current.boundingBox.x > c.boundingBox.x + c.boundingBox.width) {
                        perpendicularDistanceFrom = current.boundingBox.x - (c.boundingBox.x + c.boundingBox.width);
                        break;
                    }
                    perpendicularDistanceFrom = 0;
                    parallelOverlap = current.boundingBox.x < c.boundingBox.x ? current.boundingBox.x + current.boundingBox.width - c.boundingBox.x : c.boundingBox.x + c.boundingBox.width - current.boundingBox.x;
                    parallelOverlap = GameMath.min(c.boundingBox.width, current.boundingBox.width, parallelOverlap);
                    break;
                }
                case 3: {
                    if (c.boundingBox.x >= current.boundingBox.x || c.boundingBox.x + c.boundingBox.width >= current.boundingBox.x + current.boundingBox.width) {
                        return Integer.MAX_VALUE;
                    }
                    straightDistanceFrom = current.boundingBox.x - (c.boundingBox.x + c.boundingBox.width);
                    if (current.boundingBox.y + current.boundingBox.height < c.boundingBox.y) {
                        perpendicularDistanceFrom = c.boundingBox.y - (current.boundingBox.y + current.boundingBox.height);
                        break;
                    }
                    if (current.boundingBox.y > c.boundingBox.y + c.boundingBox.height) {
                        perpendicularDistanceFrom = current.boundingBox.y - (c.boundingBox.y + c.boundingBox.height);
                        break;
                    }
                    perpendicularDistanceFrom = 0;
                    parallelOverlap = current.boundingBox.y < c.boundingBox.y ? current.boundingBox.y + current.boundingBox.height - c.boundingBox.y : c.boundingBox.y + c.boundingBox.height - current.boundingBox.y;
                    parallelOverlap = GameMath.min(c.boundingBox.height, current.boundingBox.height, parallelOverlap);
                    break;
                }
                default: {
                    return Integer.MAX_VALUE;
                }
            }
            return Math.max(straightDistanceFrom, 0) + Math.max(perpendicularDistanceFrom, 0) - parallelOverlap / 4;
        });
        ControllerFocus next = list.stream().min(comparator).orElse(null);
        if (next != null && current != null) {
            switch (dir) {
                case 0: {
                    if (next.boundingBox.y < current.boundingBox.y && next.boundingBox.y + next.boundingBox.height < current.boundingBox.y + current.boundingBox.height) break;
                    next = null;
                    break;
                }
                case 1: {
                    if (next.boundingBox.x > current.boundingBox.x && next.boundingBox.x + next.boundingBox.width > current.boundingBox.x + current.boundingBox.width) break;
                    next = null;
                    break;
                }
                case 2: {
                    if (next.boundingBox.y > current.boundingBox.y && next.boundingBox.y + next.boundingBox.height > current.boundingBox.y + current.boundingBox.height) break;
                    next = null;
                    break;
                }
                case 3: {
                    if (next.boundingBox.x < current.boundingBox.x && next.boundingBox.x + next.boundingBox.width < current.boundingBox.x + current.boundingBox.width) break;
                    next = null;
                }
            }
        }
        return next;
    }
}

