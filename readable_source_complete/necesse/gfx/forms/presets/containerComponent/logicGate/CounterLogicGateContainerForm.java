/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.logicGate.LogicGateContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.CounterLogicGateContainer;

public class CounterLogicGateContainerForm<T extends CounterLogicGateContainer>
extends LogicGateContainerForm<T> {
    public CounterLogicGateContainerForm(Client client, T container) {
        super(client, 660, 160, container);
        this.addComponent(new FormLocalLabel(((CounterLogicGateContainer)container).entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
        this.addWireCheckboxes(10, 40, new LocalMessage("ui", "counterinc"), ((CounterLogicGateContainer)container).entity, e -> e.incInputs, ((CounterLogicGateContainer)container).setIncInputs);
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 130, 45, 100, false));
        this.addWireCheckboxes(140, 40, new LocalMessage("ui", "counterdec"), ((CounterLogicGateContainer)container).entity, e -> e.decInputs, ((CounterLogicGateContainer)container).setDecInputs);
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 260, 45, 100, false));
        this.addWireCheckboxes(270, 40, new LocalMessage("ui", "rsreset"), ((CounterLogicGateContainer)container).entity, e -> e.resetInputs, ((CounterLogicGateContainer)container).setResetInputs);
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 390, 45, 100, false));
        this.addWireCheckboxes(400, 40, new LocalMessage("ui", "wireoutputs"), ((CounterLogicGateContainer)container).entity, e -> e.wireOutputs, ((CounterLogicGateContainer)container).setOutputs);
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 520, 45, 100, false));
        this.addComponent(new FormLocalLabel("ui", "countermax", new FontOptions(20), -1, 530, 40));
        FormSlider delay = this.addComponent(new FormSlider("", 530, 65, ((CounterLogicGateContainer)container).entity.getMaxValue(), 1, 256, 100)).onGrab(e -> {
            if (!e.grabbed) {
                container.setMaxValue.runAndSend(((FormSlider)e.from).getValue());
            }
        });
        delay.onScroll(e -> container.setMaxValue.runAndSend(((FormSlider)e.from).getValue()));
        delay.setValue(((CounterLogicGateContainer)container).entity.getMaxValue());
        delay.drawValueInPercent = false;
    }
}

