/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.lists.FormScoreboardList;

public class ScoreboardForm
extends Form {
    private FormScoreboardList list;

    public ScoreboardForm(String name, Client client) {
        super(name, 10, 10);
        this.list = new FormScoreboardList(0, 0, this.getWidth(), this.getHeight(), client);
        this.addComponent(this.list);
        this.drawBase = false;
        this.onWindowResized(WindowManager.getWindow());
    }

    public void fixSize() {
        GameWindow window = WindowManager.getWindow();
        int width = Math.min(480, Math.max(40, window.getHudWidth() - 600));
        int height = Math.min(480, Math.max(40, window.getHudHeight() - 400));
        this.setWidth(width);
        this.setHeight(height);
        this.list.setWidth(width);
        this.list.setHeight(height);
    }

    public void slotChanged(int slot, ClientClient player) {
        this.list.slotChanged(slot, player);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.fixSize();
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        InputEvent offsetEvent = this.getComponentList().offsetEvent(event, false);
        return this.list.isMouseOver(offsetEvent);
    }
}

