/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.inventory.container.Container;

public abstract class ContainerFormList<T extends Container>
extends FormComponentList
implements ContainerComponent<T> {
    protected T container;
    protected Client client;

    public ContainerFormList(Client client, T container) {
        this.client = client;
        this.container = container;
        ((Container)this.container).form = this;
    }

    public Client getClient() {
        return this.client;
    }

    @Override
    public T getContainer() {
        return this.container;
    }
}

