/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.inventory.container.Container;

public class ContainerForm<T extends Container>
extends Form
implements ContainerComponent<T> {
    protected T container;
    protected Client client;

    public ContainerForm(Client client, int width, int height, T container) {
        super("focus", width, height);
        this.client = client;
        this.container = container;
        ((Container)this.container).form = this;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this);
    }

    @Override
    public T getContainer() {
        return this.container;
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }
}

