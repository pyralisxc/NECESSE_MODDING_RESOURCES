/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.gfx.forms.components.FormCheckBox
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.gfx.forms.components.FormCheckBox;

public class CheckboxWidget
extends ParameterWidget {
    private FormCheckBox checkbox;

    public CheckboxWidget(ParameterMetadata parameter, int x, int y) {
        super(parameter);
        String label = parameter.getDisplayName();
        this.checkbox = new FormCheckBox(label, x, y);
        this.checkbox.checked = false;
    }

    @Override
    public String getValue() {
        return this.checkbox.checked ? "true" : "false";
    }

    @Override
    public void setValue(String value) {
        this.checkbox.checked = value == null ? false : value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes");
    }

    @Override
    public boolean validateValue() {
        return true;
    }

    @Override
    public void reset() {
        this.checkbox.checked = false;
        this.isValid = true;
        this.validationError = null;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onBlur() {
    }

    public FormCheckBox getComponent() {
        return this.checkbox;
    }

    public void setChecked(boolean checked) {
        this.checkbox.checked = checked;
    }

    public boolean isChecked() {
        return this.checkbox.checked;
    }
}

