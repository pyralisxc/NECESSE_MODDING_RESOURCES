/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.logicGate;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import necesse.engine.GameEventListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.events.FormCheckboxesEventHandler;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.Container;
import necesse.inventory.container.logicGate.WireSelectCustomAction;
import necesse.level.gameLogicGate.entities.LogicGateEntity;

public class LogicGateContainerForm<T extends Container>
extends ContainerForm<T> {
    private ArrayList<GameEventListener<?>> listeners = new ArrayList();

    public LogicGateContainerForm(Client client, int width, int height, T container) {
        super(client, width, height, container);
    }

    public GameMessage getWireColorName(int wireID) {
        return new LocalMessage("ui", "wire" + wireID);
    }

    public <V extends LogicGateEntity> FormCheckBox[] addCheckboxList(int x, int y, GameMessage label, int totalCheckboxes, Function<Integer, GameMessage> checkboxNameGetter, final V entity, final Function<V, boolean[]> currentGetter) {
        if (label != null) {
            this.addComponent(new FormLocalLabel(label, new FontOptions(16), -1, x, y));
        }
        boolean[] checked = currentGetter.apply(entity);
        final FormCheckBox[] output = new FormCheckBox[totalCheckboxes];
        for (int i = 0; i < totalCheckboxes; ++i) {
            output[i] = this.addComponent(new FormLocalCheckBox(checkboxNameGetter.apply(i), x, y + 20 * i + 25));
            output[i].checked = checked[i];
        }
        this.listeners.add(entity.applyPacketEvents.addListener(new GameEventListener<LogicGateEntity.ApplyPacketEvent>(){

            @Override
            public void onEvent(LogicGateEntity.ApplyPacketEvent event) {
                boolean[] newChecked = (boolean[])currentGetter.apply(entity);
                for (int i = 0; i < output.length; ++i) {
                    output[i].checked = newChecked[i];
                }
            }
        }));
        return output;
    }

    public <V extends LogicGateEntity> FormCheckBox[] addWireCheckboxes(int x, int y, GameMessage label, V entity, Function<V, boolean[]> wiresExtractor) {
        return this.addCheckboxList(x, y, label, 4, this::getWireColorName, entity, wiresExtractor);
    }

    public <V extends LogicGateEntity> FormCheckboxesEventHandler addWireCheckboxesHandler(int x, int y, GameMessage label, V entity, Function<V, boolean[]> wiresExtractor) {
        return new FormCheckboxesEventHandler(this.addWireCheckboxes(x, y, label, entity, wiresExtractor));
    }

    public <V extends LogicGateEntity> void addWireCheckboxes(int x, int y, GameMessage label, V entity, Function<V, boolean[]> wiresExtractor, WireSelectCustomAction action) {
        FormCheckboxesEventHandler handler = this.addWireCheckboxesHandler(x, y, label, entity, wiresExtractor);
        handler.onClicked(e -> action.runAndSend(handler.getStates()));
    }

    @Override
    public void dispose() {
        super.dispose();
        this.listeners.forEach((Consumer<GameEventListener<?>>)((Consumer<GameEventListener>)GameEventListener::dispose));
    }
}

