/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.inventory.container.mob.MobContainer;

public abstract class MobContainerFormSwitcher<T extends MobContainer>
extends ContainerFormSwitcher<T> {
    protected Mob mob;

    public MobContainerFormSwitcher(Client client, T container) {
        super(client, container);
        this.mob = ((MobContainer)container).getMob();
    }
}

