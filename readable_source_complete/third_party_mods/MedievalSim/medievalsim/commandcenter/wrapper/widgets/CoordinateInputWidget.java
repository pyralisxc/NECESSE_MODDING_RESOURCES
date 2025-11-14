/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormInputSize
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import medievalsim.ui.fixes.InputFocusManager;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;

public class CoordinateInputWidget
extends ParameterWidget {
    private InputFocusManager.EnhancedTextInput xInput;
    private InputFocusManager.EnhancedTextInput yInput;
    private static final int INPUT_WIDTH = 85;

    public CoordinateInputWidget(ParameterMetadata parameter, int x, int y) {
        super(parameter);
        this.xInput = new InputFocusManager.EnhancedTextInput(x, y, FormInputSize.SIZE_16, 85, 200, 10);
        this.xInput.placeHolder = new StaticMessage("X (e.g. 1000)");
        this.yInput = new InputFocusManager.EnhancedTextInput(x + 85 + 10, y, FormInputSize.SIZE_16, 85, 200, 10);
        this.yInput.placeHolder = new StaticMessage("Y (e.g. 2000)");
    }

    @Override
    public FormComponent getComponent() {
        return this.xInput;
    }

    public InputFocusManager.EnhancedTextInput getYInput() {
        return this.yInput;
    }

    @Override
    public String getValue() {
        String xValue = this.xInput.getText().trim();
        String yValue = this.yInput.getText().trim();
        if (xValue.isEmpty()) {
            xValue = "0";
        }
        if (yValue.isEmpty()) {
            yValue = "0";
        }
        return xValue + " " + yValue;
    }

    @Override
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            this.xInput.setText("");
            this.yInput.setText("");
            return;
        }
        String[] parts = value.trim().split("\\s+");
        if (parts.length >= 1) {
            this.xInput.setText(parts[0]);
        }
        if (parts.length >= 2) {
            this.yInput.setText(parts[1]);
        }
    }

    @Override
    protected boolean validateValue() {
        String xValue = this.xInput.getText().trim();
        String yValue = this.yInput.getText().trim();
        if (xValue.isEmpty() && yValue.isEmpty()) {
            return !this.parameter.isRequired();
        }
        if (xValue.isEmpty() || yValue.isEmpty()) {
            this.validationError = "Both X and Y coordinates required";
            return false;
        }
        if (!this.isValidCoordinate(xValue)) {
            this.validationError = "Invalid X coordinate (use number or %+100 syntax)";
            return false;
        }
        if (!this.isValidCoordinate(yValue)) {
            this.validationError = "Invalid Y coordinate (use number or %+100 syntax)";
            return false;
        }
        return true;
    }

    private boolean isValidCoordinate(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        if (value.startsWith("%")) {
            String numPart = value.substring(1);
            if (numPart.isEmpty()) {
                return false;
            }
            if (!numPart.startsWith("+") && !numPart.startsWith("-")) {
                return false;
            }
            String number = numPart.substring(1);
            try {
                Integer.parseInt(number);
                return true;
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void reset() {
        this.xInput.setText("");
        this.yInput.setText("");
        this.isValid = this.parameter.isOptional();
        this.validationError = null;
    }

    @Override
    public void onFocus() {
        this.xInput.setTyping(true);
    }

    @Override
    public void onBlur() {
        this.validate();
    }
}

