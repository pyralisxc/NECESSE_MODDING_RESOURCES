/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.creative;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketCreativeTeleport;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.creative.CreativeTeleportToPlayerContainer;

public class CreativeTeleportToPlayerContainerForm<T extends CreativeTeleportToPlayerContainer>
extends ContainerForm<T> {
    public CreativeTeleportToPlayerContainerForm(Client client, T container) {
        super(client, 300, 300, container);
        this.addComponent(new FormLocalLabel("ui", "creativettptitle", new FontOptions(20), 0, this.getWidth() / 2, 5));
        FormContentBox content = this.addComponent(new FormContentBox(0, 30, this.getWidth(), this.getHeight() - 75));
        List targets = client.streamClients().filter(c -> c != null && c.slot != client.getSlot()).collect(Collectors.toList());
        FormFlow invitesFlow = new FormFlow(10);
        for (ClientClient target : targets) {
            FormTextButton targetButton = content.addComponent(invitesFlow.nextY(new FormTextButton(target.getName(), 14, 0, this.getWidth() - 28, FormInputSize.SIZE_24, ButtonColor.BASE), 5));
            targetButton.onClicked(e -> {
                client.network.sendPacket(new PacketCreativeTeleport(target));
                client.closeContainer(true);
            });
        }
        content.setContentBox(new Rectangle(0, 0, content.getWidth(), invitesFlow.next() + 4));
        this.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.getHeight() - 40, this.getWidth() - 8)).onClicked(e -> client.closeContainer(true));
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }
}

