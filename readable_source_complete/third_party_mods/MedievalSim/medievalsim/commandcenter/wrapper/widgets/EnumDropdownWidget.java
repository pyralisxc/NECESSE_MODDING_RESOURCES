/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.parameterHandlers.EnumParameterHandler
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormDropdownSelectionButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.commandcenter.wrapper.widgets;

import java.lang.reflect.Field;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.engine.commands.parameterHandlers.EnumParameterHandler;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;

public class EnumDropdownWidget
extends ParameterWidget {
    private FormDropdownSelectionButton<Enum<?>> enumDropdown;
    private Enum<?>[] enumValues;

    public EnumDropdownWidget(ParameterMetadata parameter, int x, int y) {
        super(parameter);
        this.enumValues = this.extractEnumValues(parameter);
        this.enumDropdown = new FormDropdownSelectionButton(x, y, FormInputSize.SIZE_32, ButtonColor.BASE, 200, (GameMessage)new StaticMessage("Select value..."));
        if (this.enumValues != null && this.enumValues.length > 0) {
            for (Enum<?> value : this.enumValues) {
                String displayName = this.formatEnumName(value.name());
                this.enumDropdown.options.add(value, (GameMessage)new StaticMessage(displayName));
            }
            Enum<?> defaultValue = this.enumValues[0];
            String displayName = this.formatEnumName(defaultValue.name());
            this.enumDropdown.setSelected(defaultValue, (GameMessage)new StaticMessage(displayName));
            this.currentValue = defaultValue.name();
        }
        this.enumDropdown.onSelected(event -> {
            this.currentValue = event.value != null ? ((Enum)event.value).name() : null;
        });
    }

    private String formatEnumName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        String[] parts = name.split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; ++i) {
            String part;
            if (i > 0) {
                result.append(" ");
            }
            if ((part = parts[i]).isEmpty()) continue;
            result.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() <= 1) continue;
            result.append(part.substring(1).toLowerCase());
        }
        return result.toString();
    }

    private Enum<?>[] extractEnumValues(ParameterMetadata parameter) {
        try {
            if (!(parameter.getHandler() instanceof EnumParameterHandler)) {
                System.err.println("[EnumDropdownWidget] Handler is not EnumParameterHandler: " + parameter.getHandler().getClass().getName());
                return null;
            }
            EnumParameterHandler enumHandler = (EnumParameterHandler)parameter.getHandler();
            Field valuesField = EnumParameterHandler.class.getDeclaredField("values");
            valuesField.setAccessible(true);
            Enum[] values = (Enum[])valuesField.get(enumHandler);
            return values;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getValue() {
        String result;
        Enum selected = (Enum)this.enumDropdown.getSelected();
        this.currentValue = result = selected != null ? selected.name() : null;
        return result;
    }

    @Override
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            this.currentValue = null;
            return;
        }
        if (this.enumValues != null) {
            for (Enum<?> enumValue : this.enumValues) {
                if (!enumValue.name().equalsIgnoreCase(value.trim())) continue;
                String displayName = this.formatEnumName(enumValue.name());
                this.enumDropdown.setSelected(enumValue, (GameMessage)new StaticMessage(displayName));
                this.currentValue = value.trim();
                return;
            }
        }
        this.currentValue = null;
    }

    @Override
    public boolean validateValue() {
        String value = this.getValue();
        if (this.parameter.isRequired() && (value == null || value.trim().isEmpty())) {
            this.validationError = "Please select a value";
            return false;
        }
        this.validationError = null;
        return true;
    }

    @Override
    public FormComponent getComponent() {
        return this.enumDropdown;
    }
}

