/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormTextInput
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;

public class TeamDropdownWidget
extends ParameterWidget {
    private FormTextInput textInput;

    public TeamDropdownWidget(ParameterMetadata parameter, int x, int y) {
        super(parameter);
        this.textInput = new FormTextInput(x, y, FormInputSize.SIZE_32, 200, 200, 50);
        this.textInput.placeHolder = new StaticMessage("Team ID (number)");
    }

    @Override
    public String getValue() {
        String value = this.textInput.getText().trim();
        return value.isEmpty() ? null : value;
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            this.textInput.setText("");
        } else {
            this.textInput.setText(value);
        }
    }

    @Override
    public boolean validateValue() {
        String value = this.textInput.getText().trim();
        if (value.isEmpty()) {
            return !this.parameter.isRequired();
        }
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e) {
            this.validationError = "Team ID must be a number";
            return false;
        }
    }

    @Override
    public void reset() {
        this.textInput.setText("");
        this.isValid = true;
        this.validationError = null;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public FormComponent getComponent() {
        return this.textInput;
    }
}

