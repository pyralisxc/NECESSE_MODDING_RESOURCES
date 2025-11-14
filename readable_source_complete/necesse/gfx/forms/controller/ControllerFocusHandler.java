/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.controller;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.ui.HUD;

public interface ControllerFocusHandler {
    public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3);

    public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4);

    default public void onControllerFocused(ControllerFocus current) {
    }

    default public void onControllerUnfocused(ControllerFocus current) {
    }

    default public void frameTickControllerFocus(TickManager tickManager, ControllerFocus current) {
    }

    default public void drawControllerFocus(ControllerFocus current) {
        Rectangle box = current.boundingBox;
        int padding = 5;
        box = new Rectangle(box.x - padding, box.y - padding, box.width + padding * 2, box.height + padding * 2);
        HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsColor, true, box).draw();
    }

    default public Point getControllerTooltipAndFloatMenuPoint(ControllerFocus current) {
        return null;
    }

    default public int getControllerFocusHashcode() {
        return this.hashCode();
    }
}

