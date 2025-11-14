/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.gfx.forms.components.FormComponent
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import necesse.gfx.forms.components.FormComponent;

public abstract class ParameterWidget {
    protected final ParameterMetadata parameter;
    protected String currentValue;
    protected boolean isValid;
    protected String validationError;
    protected Runnable onValueChangedCallback;

    public ParameterWidget(ParameterMetadata parameter) {
        this.parameter = parameter;
        this.currentValue = "";
        this.isValid = parameter.isOptional();
        this.validationError = null;
        this.onValueChangedCallback = null;
    }

    public void setOnValueChanged(Runnable callback) {
        this.onValueChangedCallback = callback;
    }

    protected void notifyValueChanged() {
        if (this.onValueChangedCallback != null) {
            this.onValueChangedCallback.run();
        }
    }

    public String getValue() {
        return this.currentValue != null ? this.currentValue : "";
    }

    public abstract void setValue(String var1);

    public boolean validate() {
        if (this.parameter.isOptional() && (this.currentValue == null || this.currentValue.trim().isEmpty())) {
            this.isValid = true;
            this.validationError = null;
            return true;
        }
        if (!this.parameter.isOptional() && (this.currentValue == null || this.currentValue.trim().isEmpty())) {
            this.isValid = false;
            this.validationError = "Required parameter";
            return false;
        }
        boolean result = this.validateValue();
        return result;
    }

    protected abstract boolean validateValue();

    public abstract FormComponent getComponent();

    public ParameterMetadata getParameter() {
        return this.parameter;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public String getValidationError() {
        return this.validationError;
    }

    public void reset() {
        this.setValue("");
        this.isValid = this.parameter.isOptional();
        this.validationError = null;
    }

    public void onFocus() {
    }

    public void onBlur() {
        this.validate();
    }
}

