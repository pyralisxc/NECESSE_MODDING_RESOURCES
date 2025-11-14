/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormTextInput
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;

public class TextInputWidget
extends ParameterWidget {
    private FormTextInput textInput;
    private int maxLength = 100;

    public TextInputWidget(ParameterMetadata parameter, int x, int y, int width) {
        this(parameter, x, y, width, null);
    }

    public TextInputWidget(ParameterMetadata parameter, int x, int y, int width, String defaultValue) {
        super(parameter);
        this.textInput = new FormTextInput(x, y, FormInputSize.SIZE_16, width, 200, this.maxLength);
        if (defaultValue != null && !defaultValue.isEmpty()) {
            this.textInput.setText(defaultValue);
        }
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
        if (value.length() > this.maxLength) {
            this.validationError = "Maximum length is " + this.maxLength + " characters";
            return false;
        }
        return true;
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
    public void onBlur() {
        this.validate();
    }

    public FormTextInput getComponent() {
        return this.textInput;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}

