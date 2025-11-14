/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.controller;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;

public interface ControllerNavigationHandler {
    public boolean handleNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4);
}

