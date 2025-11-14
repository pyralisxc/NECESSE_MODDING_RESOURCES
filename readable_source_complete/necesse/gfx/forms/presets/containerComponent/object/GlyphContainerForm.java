/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.object.GlyphTrapContainer;

public class GlyphContainerForm<T extends GlyphTrapContainer>
extends ContainerForm<T> {
    public GlyphContainerForm(Client client, T container) {
        super(client, 400, 135, container);
        this.addComponent(new FormLocalLabel(((GlyphTrapContainer)container).glyph.getObject().getLocalization(), new FontOptions(20), -1, 4, 4));
        FormFlow yFlow = new FormFlow(35);
        this.addComponent(yFlow.nextY(new FormLocalLabel(new LocalMessage("ui", "triggerslabel"), new FontOptions(16), -1, 10, 0), 10));
        this.addComponent(yFlow.nextY(new FormLocalCheckBox("ui", "sensorplayers", 10, 0, ((GlyphTrapContainer)container).glyph.players).useButtonTexture(), 5)).onClicked(e -> container.setPlayers.runAndSend(((FormCheckBox)e.from).checked));
        this.addComponent(yFlow.nextY(new FormLocalCheckBox("ui", "sensorhostile", 10, 0, ((GlyphTrapContainer)container).glyph.hostileMobs).useButtonTexture(), 5)).onClicked(e -> container.setHostileMobs.runAndSend(((FormCheckBox)e.from).checked));
        this.addComponent(yFlow.nextY(new FormLocalCheckBox("ui", "sensorpassive", 10, 0, ((GlyphTrapContainer)container).glyph.passiveMobs).useButtonTexture(), 5)).onClicked(e -> container.setPassiveMobs.runAndSend(((FormCheckBox)e.from).checked));
    }
}

