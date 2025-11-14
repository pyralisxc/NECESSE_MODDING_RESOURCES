/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.creative;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;

public abstract class CreativeTab {
    protected Client playerClient;
    protected Form form;

    public CreativeTab(Form form, Client playerClient) {
        this.form = form;
        this.playerClient = playerClient;
    }

    public void updateBeforeDraw(TickManager tickManager) {
    }

    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    public void tabFocused() {
    }

    public void onTabUnfocused() {
    }

    public void dispose() {
    }
}

