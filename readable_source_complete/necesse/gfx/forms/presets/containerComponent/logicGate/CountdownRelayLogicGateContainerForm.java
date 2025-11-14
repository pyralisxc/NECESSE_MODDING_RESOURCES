/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.events.FormCheckboxesEventHandler;
import necesse.gfx.forms.presets.containerComponent.logicGate.LogicGateContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.CountdownRelayLogicGateContainer;
import necesse.level.gameLogicGate.entities.CountdownLogicGateEntity;

public class CountdownRelayLogicGateContainerForm<T extends CountdownRelayLogicGateContainer>
extends LogicGateContainerForm<T> {
    public CountdownRelayLogicGateContainerForm(Client client, T container) {
        super(client, 400, 160, container);
        this.addComponent(new FormLocalLabel(((CountdownRelayLogicGateContainer)container).entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
        this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireoutputs"), ((CountdownRelayLogicGateContainer)container).entity, e -> e.wireOutputs, ((CountdownRelayLogicGateContainer)container).setOutputs);
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 135, 45, 100, false));
        FormCheckboxesEventHandler handler = new FormCheckboxesEventHandler(this.addCheckboxList(150, 40, new LocalMessage("ui", "countdownrelaydirs"), 4, CountdownLogicGateEntity::getRelayDirName, ((CountdownRelayLogicGateContainer)container).entity, e -> e.relayDirections));
        handler.onClicked(e -> container.setRelayDirections.runAndSend(handler.getStates()));
    }
}

