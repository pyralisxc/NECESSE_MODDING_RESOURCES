/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.GameEventListener;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.logicGate.LogicGateContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.DelayLogicGateContainer;
import necesse.level.gameLogicGate.entities.LogicGateEntity;

public class DelayLogicGateContainerForm<T extends DelayLogicGateContainer>
extends LogicGateContainerForm<T> {
    private final GameEventListener<LogicGateEntity.ApplyPacketEvent> applyListener;

    public DelayLogicGateContainerForm(Client client, final T container) {
        super(client, 400, 160, container);
        this.addComponent(new FormLocalLabel(((DelayLogicGateContainer)container).entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 135, 45, 100, false));
        this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireinputs"), ((DelayLogicGateContainer)container).entity, e -> e.wireInputs, ((DelayLogicGateContainer)container).setInputs);
        this.addWireCheckboxes(150, 40, new LocalMessage("ui", "wireoutputs"), ((DelayLogicGateContainer)container).entity, e -> e.wireOutputs, ((DelayLogicGateContainer)container).setOutputs);
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 265, 45, 100, false));
        this.addComponent(new FormLocalLabel("ui", "bufferticks", new FontOptions(20), -1, 280, 40));
        final FormSlider delay = this.addComponent(new FormSlider("", 280, 65, ((DelayLogicGateContainer)container).entity.delayTicks, 1, 200, 100)).onGrab(e -> {
            if (!e.grabbed) {
                container.setDelay.runAndSend(((FormSlider)e.from).getValue());
            }
        });
        delay.onScroll(e -> container.setDelay.runAndSend(((FormSlider)e.from).getValue()));
        delay.setValue(((DelayLogicGateContainer)container).entity.delayTicks);
        delay.drawValueInPercent = false;
        this.addComponent(new FormLocalLabel(new LocalMessage("ui", "buffertip", "ticks", 20), new FontOptions(12), -1, 280, delay.getY() + delay.getTotalHeight() + 5, 100));
        this.applyListener = ((DelayLogicGateContainer)container).entity.applyPacketEvents.addListener(new GameEventListener<LogicGateEntity.ApplyPacketEvent>(){

            @Override
            public void onEvent(LogicGateEntity.ApplyPacketEvent event) {
                delay.setValue(container.entity.delayTicks);
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        this.applyListener.dispose();
    }
}

