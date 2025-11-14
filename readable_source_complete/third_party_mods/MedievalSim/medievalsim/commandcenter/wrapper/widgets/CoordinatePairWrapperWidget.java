/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.gfx.forms.components.FormComponent
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.CoordinatePairWidget;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.gfx.forms.components.FormComponent;

public class CoordinatePairWrapperWidget
extends ParameterWidget {
    private final CoordinatePairWidget coordWidget;
    private final boolean isXCoordinate;

    public CoordinatePairWrapperWidget(ParameterMetadata parameter, CoordinatePairWidget coordWidget, boolean isXCoordinate) {
        super(parameter);
        this.coordWidget = coordWidget;
        this.isXCoordinate = isXCoordinate;
    }

    @Override
    public String getValue() {
        return this.isXCoordinate ? this.coordWidget.getXValue() : this.coordWidget.getYValue();
    }

    @Override
    public void setValue(String value) {
        if (this.isXCoordinate) {
            this.coordWidget.setXValue(value);
        } else {
            this.coordWidget.setYValue(value);
        }
    }

    @Override
    protected boolean validateValue() {
        String error = this.coordWidget.validate();
        if (error != null) {
            this.validationError = error;
            return false;
        }
        return true;
    }

    @Override
    public FormComponent getComponent() {
        return this.isXCoordinate ? this.coordWidget.getXInput() : this.coordWidget.getYInput();
    }

    @Override
    public void reset() {
        this.coordWidget.reset();
    }

    @Override
    public void onFocus() {
    }
}

