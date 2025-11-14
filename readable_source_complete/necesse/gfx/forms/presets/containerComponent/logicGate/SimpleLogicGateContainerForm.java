/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.logicGate.LogicGateContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.SimpleLogicGateContainer;

public class SimpleLogicGateContainerForm<T extends SimpleLogicGateContainer>
extends LogicGateContainerForm<T> {
    public SimpleLogicGateContainerForm(Client client, T container) {
        super(client, 400, 160, container);
        this.addComponent(new FormLocalLabel(((SimpleLogicGateContainer)container).entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 150, 45, 100, false));
        this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireinputs"), ((SimpleLogicGateContainer)container).entity, e -> e.wireInputs, ((SimpleLogicGateContainer)container).setInputs);
        this.addWireCheckboxes(165, 40, new LocalMessage("ui", "wireoutputs"), ((SimpleLogicGateContainer)container).entity, e -> e.wireOutputs, ((SimpleLogicGateContainer)container).setOutputs);
    }
}

