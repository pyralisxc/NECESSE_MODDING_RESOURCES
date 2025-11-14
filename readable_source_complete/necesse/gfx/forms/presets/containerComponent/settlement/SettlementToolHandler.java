/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.window.GameWindow;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;

public interface SettlementToolHandler {
    default public boolean onLeftClick(Point pos) {
        return false;
    }

    default public DrawOptions getLeftClickDraw(Point startPos, Point endPos) {
        return null;
    }

    default public boolean onLeftClickSelection(Point startPos, Point endPos, Rectangle selection) {
        return false;
    }

    default public DrawOptions getLeftClickSelectionDraw(Point startPos, Point endPos, Rectangle selection) {
        return null;
    }

    default public boolean onRightClick(Point pos) {
        return false;
    }

    default public DrawOptions getRightClickDraw(Point startPos, Point endPos) {
        return null;
    }

    default public boolean onRightClickSelection(Point startPos, Point endPos, Rectangle selection) {
        return false;
    }

    default public DrawOptions getRightClickSelectionDraw(Point startPos, Point endPos, Rectangle selection) {
        return null;
    }

    default public boolean onHover(Point pos, Consumer<ListGameTooltips> setTooltips, Consumer<GameWindow.CURSOR> setCursor) {
        return false;
    }
}

