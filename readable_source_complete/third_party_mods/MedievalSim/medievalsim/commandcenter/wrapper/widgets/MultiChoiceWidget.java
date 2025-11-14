/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.parameterHandlers.MultiParameterHandler
 *  necesse.engine.commands.parameterHandlers.ParameterHandler
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.client.Client
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormDropdownSelectionButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.commandcenter.wrapper.widgets;

import java.lang.reflect.Field;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidgetFactory;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;

public class MultiChoiceWidget
extends ParameterWidget {
    private FormDropdownSelectionButton<Integer> choiceDropdown;
    private ParameterHandler<?>[] handlers;
    private ParameterWidget[] subWidgets;
    private int selectedIndex = 0;
    private int previousSelectedIndex = 0;
    private FormContentBox parentForm = null;
    private FormComponent currentSubComponent = null;
    private static final int DROPDOWN_WIDTH = 180;
    private static final int SUB_WIDGET_Y_OFFSET = 30;

    public MultiChoiceWidget(ParameterMetadata parameter, int x, int y, Client client) {
        super(parameter);
        int i;
        this.handlers = this.extractHandlers(parameter);
        this.choiceDropdown = new FormDropdownSelectionButton(x, y, FormInputSize.SIZE_16, ButtonColor.BASE, 180, (GameMessage)new StaticMessage("Select Type"));
        for (i = 0; i < this.handlers.length; ++i) {
            String handlerName = this.getHandlerDisplayName(this.handlers[i]);
            this.choiceDropdown.options.add((Object)i, (GameMessage)new StaticMessage(handlerName));
        }
        if (this.handlers.length > 0) {
            this.choiceDropdown.setSelected((Object)0, (GameMessage)new StaticMessage(this.getHandlerDisplayName(this.handlers[0])));
        }
        this.subWidgets = new ParameterWidget[this.handlers.length];
        for (i = 0; i < this.handlers.length; ++i) {
            ParameterMetadata subParam = this.createSubParameterMetadata(parameter, this.handlers[i]);
            this.subWidgets[i] = ParameterWidgetFactory.createWidget(subParam, x, y + 30, client);
        }
        if (this.subWidgets.length > 0) {
            this.currentSubComponent = this.subWidgets[0].getComponent();
        }
    }

    private ParameterHandler<?>[] extractHandlers(ParameterMetadata parameter) {
        try {
            ParameterHandler<?> handler = parameter.getHandler();
            if (handler instanceof MultiParameterHandler) {
                Field handlersField = MultiParameterHandler.class.getDeclaredField("handlers");
                handlersField.setAccessible(true);
                return (ParameterHandler[])handlersField.get(handler);
            }
            return new ParameterHandler[]{parameter.getHandler()};
        }
        catch (Exception e) {
            System.err.println("Failed to extract handlers from MultiParameterHandler: " + e.getMessage());
            e.printStackTrace();
            return new ParameterHandler[]{parameter.getHandler()};
        }
    }

    private String getHandlerDisplayName(ParameterHandler<?> handler) {
        String className = handler.getClass().getSimpleName();
        if (className.endsWith("ParameterHandler")) {
            className = className.substring(0, className.length() - "ParameterHandler".length());
        }
        String spaced = className.replaceAll("([A-Z])", " $1").trim();
        return spaced;
    }

    private ParameterMetadata createSubParameterMetadata(ParameterMetadata parent, ParameterHandler<?> handler) {
        return new ParameterMetadata(parent.getName(), parent.isOptional(), parent.isRequired(), handler, ParameterMetadata.determineHandlerType(handler), new ParameterMetadata[0]);
    }

    public void setParentForm(FormContentBox form) {
        this.parentForm = form;
    }

    public boolean updateSelectionIfChanged() {
        Integer currentSelection = (Integer)this.choiceDropdown.getSelected();
        if (currentSelection == null) {
            return false;
        }
        int newIndex = currentSelection;
        if (newIndex != this.previousSelectedIndex) {
            this.swapSubWidget(this.previousSelectedIndex, newIndex);
            this.previousSelectedIndex = newIndex;
            this.selectedIndex = newIndex;
            return true;
        }
        return false;
    }

    private void swapSubWidget(int oldIndex, int newIndex) {
        if (this.parentForm == null) {
            System.err.println("[MultiChoiceWidget] Cannot swap sub-widget - parentForm is null! Call setParentForm() first.");
            return;
        }
        if (this.currentSubComponent != null && oldIndex >= 0 && oldIndex < this.subWidgets.length) {
            try {
                this.parentForm.removeComponent(this.currentSubComponent);
            }
            catch (Exception e) {
                System.err.println("[MultiChoiceWidget] Failed to remove old sub-component: " + e.getMessage());
            }
        }
        if (oldIndex >= 0 && oldIndex < this.subWidgets.length) {
            this.subWidgets[oldIndex].reset();
        }
        if (newIndex >= 0 && newIndex < this.subWidgets.length) {
            ParameterWidget newWidget = this.subWidgets[newIndex];
            this.currentSubComponent = newWidget.getComponent();
            try {
                this.parentForm.addComponent(this.currentSubComponent);
            }
            catch (Exception e) {
                System.err.println("[MultiChoiceWidget] Failed to add new sub-component: " + e.getMessage());
            }
        }
    }

    @Override
    public FormComponent getComponent() {
        return this.choiceDropdown;
    }

    public FormComponent getSelectedSubComponent() {
        Integer selected = (Integer)this.choiceDropdown.getSelected();
        if (selected != null && selected >= 0 && selected < this.subWidgets.length) {
            this.selectedIndex = selected;
            return this.subWidgets[this.selectedIndex].getComponent();
        }
        if (this.subWidgets.length > 0) {
            return this.subWidgets[0].getComponent();
        }
        return null;
    }

    public int getSelectedIndex() {
        Integer selected = (Integer)this.choiceDropdown.getSelected();
        if (selected != null && selected >= 0 && selected < this.subWidgets.length) {
            return selected;
        }
        return 0;
    }

    public ParameterWidget getSelectedWidget() {
        Integer selected = (Integer)this.choiceDropdown.getSelected();
        if (selected != null && selected >= 0 && selected < this.subWidgets.length) {
            this.selectedIndex = selected;
            return this.subWidgets[this.selectedIndex];
        }
        return this.subWidgets[0];
    }

    public ParameterWidget[] getSubWidgets() {
        return this.subWidgets;
    }

    @Override
    public String getValue() {
        ParameterWidget selectedWidget = this.getSelectedWidget();
        return selectedWidget != null ? selectedWidget.getValue() : "";
    }

    @Override
    public void setValue(String value) {
        for (ParameterWidget widget : this.subWidgets) {
            try {
                widget.setValue(value);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected boolean validateValue() {
        ParameterWidget selectedWidget = this.getSelectedWidget();
        if (selectedWidget != null) {
            boolean valid = selectedWidget.validate();
            if (!valid) {
                this.validationError = selectedWidget.validationError;
            }
            return valid;
        }
        return !this.parameter.isRequired();
    }

    @Override
    public void reset() {
        for (ParameterWidget widget : this.subWidgets) {
            widget.reset();
        }
        if (this.handlers.length > 0) {
            this.choiceDropdown.setSelected((Object)0, (GameMessage)new StaticMessage(this.getHandlerDisplayName(this.handlers[0])));
            this.selectedIndex = 0;
        }
        this.isValid = this.parameter.isOptional();
        this.validationError = null;
    }

    @Override
    public void onFocus() {
        ParameterWidget selectedWidget = this.getSelectedWidget();
        if (selectedWidget != null) {
            selectedWidget.onFocus();
        }
    }

    @Override
    public void onBlur() {
        this.validate();
    }
}

