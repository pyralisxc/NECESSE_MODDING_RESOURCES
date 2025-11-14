/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.gfx.forms.components.FormInputSize
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import medievalsim.ui.fixes.InputFocusManager;
import necesse.gfx.forms.components.FormInputSize;

public class NumberInputWidget
extends ParameterWidget {
    private InputFocusManager.EnhancedTextInput textInput;
    private boolean allowDecimals;
    private Double minValue;
    private Double maxValue;

    public NumberInputWidget(ParameterMetadata parameter, int x, int y, int width, boolean allowDecimals) {
        this(parameter, x, y, width, allowDecimals, null);
    }

    public NumberInputWidget(ParameterMetadata parameter, int x, int y, int width, boolean allowDecimals, String defaultValue) {
        super(parameter);
        this.allowDecimals = allowDecimals;
        this.minValue = null;
        this.maxValue = null;
        this.textInput = new InputFocusManager.EnhancedTextInput(x, y, FormInputSize.SIZE_16, width, 200, 20);
        if (defaultValue != null && !defaultValue.isEmpty()) {
            this.textInput.setText(defaultValue);
        }
    }

    @Override
    public String getValue() {
        String value = this.textInput.getText().trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            if (this.allowDecimals) {
                double num = Double.parseDouble(value);
                return String.valueOf(num);
            }
            int num = Integer.parseInt(value);
            return String.valueOf(num);
        }
        catch (NumberFormatException e) {
            return null;
        }
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
            double numValue;
            if (this.allowDecimals) {
                numValue = Double.parseDouble(value);
            } else {
                if (value.contains(".")) {
                    this.validationError = "Must be a whole number (no decimals)";
                    return false;
                }
                numValue = Integer.parseInt(value);
            }
            if (this.minValue != null && numValue < this.minValue) {
                this.validationError = "Value must be at least " + this.minValue;
                return false;
            }
            if (this.maxValue != null && numValue > this.maxValue) {
                this.validationError = "Value must be at most " + this.maxValue;
                return false;
            }
            return true;
        }
        catch (NumberFormatException e) {
            this.validationError = this.allowDecimals ? "Must be a valid number" : "Must be a valid integer";
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
    public void onBlur() {
        this.validate();
    }

    public InputFocusManager.EnhancedTextInput getComponent() {
        return this.textInput;
    }

    public void setMinValue(double min) {
        this.minValue = min;
    }

    public void setMaxValue(double max) {
        this.maxValue = max;
    }

    public void setRange(double min, double max) {
        this.minValue = min;
        this.maxValue = max;
    }
}

