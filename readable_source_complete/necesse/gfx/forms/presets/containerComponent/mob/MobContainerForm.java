/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.inventory.container.mob.MobContainer;

public class MobContainerForm<T extends MobContainer>
extends ContainerForm<T> {
    protected Mob mob;

    public MobContainerForm(Client client, int width, int height, T container) {
        super(client, width, height, container);
        this.mob = ((MobContainer)container).getMob();
    }
}

