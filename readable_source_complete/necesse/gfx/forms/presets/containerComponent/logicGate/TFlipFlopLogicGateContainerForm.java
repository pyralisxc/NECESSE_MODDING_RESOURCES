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
import necesse.inventory.container.logicGate.TFlipFlopLogicGateContainer;

public class TFlipFlopLogicGateContainerForm<T extends TFlipFlopLogicGateContainer>
extends LogicGateContainerForm<T> {
    public TFlipFlopLogicGateContainerForm(Client client, T container) {
        super(client, 400, 160, container);
        this.addComponent(new FormLocalLabel(((TFlipFlopLogicGateContainer)container).entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
        this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireinputs"), ((TFlipFlopLogicGateContainer)container).entity, e -> e.wireInputs, ((TFlipFlopLogicGateContainer)container).setInputs);
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 135, 45, 100, false));
        this.addWireCheckboxes(150, 40, new LocalMessage("ui", "wireoutputs"), ((TFlipFlopLogicGateContainer)container).entity, e -> e.wireOutputs1, ((TFlipFlopLogicGateContainer)container).setOutputs1);
        this.addWireCheckboxes(270, 40, null, ((TFlipFlopLogicGateContainer)container).entity, e -> e.wireOutputs2, ((TFlipFlopLogicGateContainer)container).setOutputs2);
    }
}

