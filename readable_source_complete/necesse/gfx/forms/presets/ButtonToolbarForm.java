/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class ButtonToolbarForm
extends Form {
    private ArrayList<ToolbarButton> buttons = new ArrayList();

    public ButtonToolbarForm(String name) {
        super(name, 16, 40);
    }

    public ButtonToolbarForm() {
        this(null);
    }

    public void addButton(FormComponent component, BiConsumer<Integer, Integer> positionSetter, BooleanSupplier shouldAlwaysShow) {
        FormComponentList list = this.addComponent(new FormComponentList());
        list.addComponent(component);
        this.buttons.add(new ToolbarButton(list, component, positionSetter, shouldAlwaysShow));
    }

    public void addButton(FormPositionContainer component, BooleanSupplier shouldAlwaysShow) {
        this.addButton((FormComponent)((Object)component), component::setPosition, shouldAlwaysShow);
    }

    public void addButton(FormContentIconButton button, BooleanSupplier shouldAlwaysShow) {
        this.addButton(button, button::setPosition, shouldAlwaysShow);
    }

    public FormContentIconButton addButton(String stringID, ButtonIcon icon, FormEventListener<FormInputEvent<FormButton>> listener, GameMessage tooltip, BooleanSupplier shouldAlwaysShow) {
        FormContentIconButton button = new FormContentIconButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, icon, tooltip);
        button.controllerFocusHashcode = stringID;
        button.onClicked(listener);
        this.addButton(button, shouldAlwaysShow);
        return button;
    }

    public void addButton(FormPositionContainer component) {
        this.addButton(component, () -> false);
    }

    public void addButton(FormContentIconButton button) {
        this.addButton(button, () -> false);
    }

    public FormContentIconButton addButton(String stringID, ButtonIcon icon, FormEventListener<FormInputEvent<FormButton>> listener, GameMessage tooltip) {
        return this.addButton(stringID, icon, listener, tooltip, () -> false);
    }

    public void updateButtons(boolean alwaysShowActive) {
        boolean anyActive = false;
        FormFlow flow = new FormFlow(4);
        for (ToolbarButton button : this.buttons) {
            boolean show;
            boolean bl = show = alwaysShowActive || button.shouldAlwaysShow.getAsBoolean();
            if (show) {
                button.componentHider.setHidden(false);
                button.positionSetter.accept(flow.next(button.component.getBoundingBox().width), 4);
                anyActive = true;
                continue;
            }
            button.componentHider.setHidden(true);
        }
        this.setWidth(flow.next() + 4);
        this.setHidden(!anyActive);
    }

    private static class ToolbarButton {
        public final FormComponentList componentHider;
        public final FormComponent component;
        public final BiConsumer<Integer, Integer> positionSetter;
        public BooleanSupplier shouldAlwaysShow;

        public ToolbarButton(FormComponentList componentHider, FormComponent component, BiConsumer<Integer, Integer> positionSetter, BooleanSupplier shouldAlwaysShow) {
            this.componentHider = componentHider;
            this.component = component;
            this.positionSetter = positionSetter;
            this.shouldAlwaysShow = shouldAlwaysShow;
        }
    }
}

