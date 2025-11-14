/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.FormSwitcher;
import necesse.inventory.container.Container;

public abstract class ContainerFormSwitcher<T extends Container>
extends FormSwitcher
implements ContainerComponent<T> {
    protected T container;
    protected Client client;
    private boolean hidden;

    public ContainerFormSwitcher(Client client, T container) {
        this.client = client;
        this.container = container;
        ((Container)this.container).form = this;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw() && !this.hidden;
    }

    @Override
    public T getContainer() {
        return this.container;
    }

    public Client getClient() {
        return this.client;
    }
}

