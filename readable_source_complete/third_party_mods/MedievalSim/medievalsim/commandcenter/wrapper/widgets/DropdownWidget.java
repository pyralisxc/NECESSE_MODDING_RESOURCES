/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.gfx.forms.components.FormDropdownSelectionButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;

public class DropdownWidget
extends ParameterWidget {
    private FormDropdownSelectionButton<String> dropdown;
    private String[] presetOptions;

    public DropdownWidget(ParameterMetadata parameter, int x, int y, String[] options) {
        super(parameter);
        this.presetOptions = options;
        this.dropdown = new FormDropdownSelectionButton(x, y, FormInputSize.SIZE_16, ButtonColor.BASE, 200, (GameMessage)new StaticMessage("Select " + parameter.getDisplayName()));
        for (String option : options) {
            this.dropdown.options.add((Object)option, (GameMessage)new StaticMessage(option));
        }
        if (parameter.isRequired() && options.length > 0) {
            this.dropdown.setSelected((Object)options[0], (GameMessage)new StaticMessage(options[0]));
        }
        this.dropdown.onSelected(event -> {
            this.currentValue = (String)event.value;
        });
    }

    @Override
    public String getValue() {
        String selected = (String)this.dropdown.getSelected();
        if (selected == null && !this.parameter.isRequired()) {
            return null;
        }
        return selected;
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            if (!this.parameter.isRequired()) {
                this.dropdown.setSelected(null, (GameMessage)new StaticMessage(""));
            }
        } else {
            this.dropdown.setSelected((Object)value, (GameMessage)new StaticMessage(value));
        }
        this.currentValue = value;
    }

    @Override
    public boolean validateValue() {
        String selected = (String)this.dropdown.getSelected();
        if (this.parameter.isRequired() && selected == null) {
            this.validationError = "Please select a value";
            return false;
        }
        if (selected != null) {
            boolean found = false;
            for (String option : this.presetOptions) {
                if (!option.equals(selected)) continue;
                found = true;
                break;
            }
            if (!found) {
                this.validationError = "Invalid selection";
                return false;
            }
        }
        return true;
    }

    @Override
    public void reset() {
        if (this.parameter.isRequired() && this.presetOptions.length > 0) {
            this.dropdown.setSelected((Object)this.presetOptions[0], (GameMessage)new StaticMessage(this.presetOptions[0]));
            this.currentValue = this.presetOptions[0];
        } else {
            this.dropdown.setSelected(null, (GameMessage)new StaticMessage(""));
            this.currentValue = null;
        }
        this.isValid = true;
        this.validationError = null;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onBlur() {
        this.validate();
    }

    public FormDropdownSelectionButton<String> getComponent() {
        return this.dropdown;
    }

    public void addOption(String option) {
        this.dropdown.options.add((Object)option, (GameMessage)new StaticMessage(option));
    }

    public String[] getOptions() {
        return this.presetOptions;
    }
}

