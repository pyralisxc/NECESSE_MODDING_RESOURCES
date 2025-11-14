/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.client.Client
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.commandcenter.wrapper.widgets;

import java.util.ArrayList;
import java.util.List;
import medievalsim.commandcenter.worldclick.WorldClickHandler;
import medievalsim.commandcenter.worldclick.WorldClickIntegration;
import medievalsim.ui.fixes.InputFocusManager;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.ui.ButtonColor;

public class CoordinatePairWidget {
    private InputFocusManager.EnhancedTextInput xInput;
    private InputFocusManager.EnhancedTextInput yInput;
    private FormTextButton clickWorldButton;
    private FormTextButton useCurrentButton;
    private Client client;
    private String xParamName;
    private String yParamName;
    private boolean xRequired;
    private boolean yRequired;
    private static final int INPUT_WIDTH = 100;
    private static final int INPUT_SPACING = 10;
    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_SPACING = 5;

    public CoordinatePairWidget(int x, int y, String xParamName, String yParamName, boolean xRequired, boolean yRequired) {
        this.xParamName = xParamName;
        this.yParamName = yParamName;
        this.xRequired = xRequired;
        this.yRequired = yRequired;
        this.xInput = new InputFocusManager.EnhancedTextInput(x, y, FormInputSize.SIZE_16, 100, 200, 10);
        this.xInput.placeHolder = new StaticMessage("X (e.g. 1000)");
        int yInputX = x + 100 + 10;
        this.yInput = new InputFocusManager.EnhancedTextInput(yInputX, y, FormInputSize.SIZE_16, 100, 200, 10);
        this.yInput.placeHolder = new StaticMessage("Y (e.g. 1000)");
        int buttonX = yInputX + 100 + 5;
        this.clickWorldButton = new FormTextButton("Click World", buttonX, y, 90, FormInputSize.SIZE_16, ButtonColor.BASE);
        this.clickWorldButton.onClicked(btn -> this.onClickWorldPressed());
        this.useCurrentButton = new FormTextButton("Current Pos", buttonX += 95, y, 90, FormInputSize.SIZE_16, ButtonColor.BASE);
        this.useCurrentButton.onClicked(btn -> this.onUseCurrentPressed());
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void onClickWorldPressed() {
        if (this.client == null) {
            System.err.println("[CoordinatePairWidget] Cannot start world-click: client is null");
            return;
        }
        WorldClickHandler.getInstance().startSelection(this.client, (tileX, tileY) -> {
            this.xInput.setText(String.valueOf(tileX));
            this.yInput.setText(String.valueOf(tileY));
        });
        WorldClickIntegration.startIntegration(this.client);
    }

    private void onUseCurrentPressed() {
        if (this.client == null) {
            System.err.println("[CoordinatePairWidget] Cannot use current position: client is null");
            return;
        }
        PlayerMob player = this.client.getPlayer();
        if (player == null) {
            System.err.println("[CoordinatePairWidget] Cannot use current position: player is null");
            return;
        }
        int tileX = (int)(player.x / 32.0f);
        int tileY = (int)(player.y / 32.0f);
        this.xInput.setText(String.valueOf(tileX));
        this.yInput.setText(String.valueOf(tileY));
    }

    public String getXValue() {
        String value = this.xInput.getText().trim();
        return value.isEmpty() ? "" : value;
    }

    public String getYValue() {
        String value = this.yInput.getText().trim();
        return value.isEmpty() ? "" : value;
    }

    public void setXValue(String value) {
        this.xInput.setText(value == null ? "" : value.trim());
    }

    public void setYValue(String value) {
        this.yInput.setText(value == null ? "" : value.trim());
    }

    public List<FormComponent> getComponents() {
        ArrayList<FormComponent> components = new ArrayList<FormComponent>();
        components.add((FormComponent)this.xInput);
        components.add((FormComponent)this.yInput);
        components.add((FormComponent)this.clickWorldButton);
        components.add((FormComponent)this.useCurrentButton);
        return components;
    }

    public InputFocusManager.EnhancedTextInput getXInput() {
        return this.xInput;
    }

    public InputFocusManager.EnhancedTextInput getYInput() {
        return this.yInput;
    }

    public FormTextButton getClickWorldButton() {
        return this.clickWorldButton;
    }

    public FormTextButton getUseCurrentButton() {
        return this.useCurrentButton;
    }

    public void reset() {
        this.xInput.setText("");
        this.yInput.setText("");
    }

    public String validate() {
        String yError;
        String xError;
        String xValue = this.getXValue();
        String yValue = this.getYValue();
        if (this.xRequired && xValue.isEmpty()) {
            return (this.xParamName != null ? this.xParamName : "X coordinate") + " is required";
        }
        if (this.yRequired && yValue.isEmpty()) {
            return (this.yParamName != null ? this.yParamName : "Y coordinate") + " is required";
        }
        if (!xValue.isEmpty() && (xError = this.validateCoordinate(xValue, this.xParamName != null ? this.xParamName : "X")) != null) {
            return xError;
        }
        if (!yValue.isEmpty() && (yError = this.validateCoordinate(yValue, this.yParamName != null ? this.yParamName : "Y")) != null) {
            return yError;
        }
        return null;
    }

    private String validateCoordinate(String value, String coordName) {
        if ((value = value.trim()).startsWith("%")) {
            String numPart = value.substring(1);
            if (numPart.isEmpty()) {
                return coordName + ": Relative syntax requires a number (e.g. %+100)";
            }
            if (!numPart.startsWith("+") && !numPart.startsWith("-")) {
                return coordName + ": Relative syntax requires %+ or %- (e.g. %+100)";
            }
            String number = numPart.substring(1);
            if (number.isEmpty()) {
                return coordName + ": Relative syntax requires a number after +/- (e.g. %+100)";
            }
            try {
                Integer.parseInt(number);
                return null;
            }
            catch (NumberFormatException e) {
                return coordName + ": Invalid number in relative syntax: " + number;
            }
        }
        try {
            Integer.parseInt(value);
            return null;
        }
        catch (NumberFormatException e) {
            return coordName + ": Must be an integer or %+N syntax (e.g. 1000 or %+100)";
        }
    }
}

