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
 *  necesse.gfx.forms.components.FormTextInput
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.worldclick.WorldClickHandler;
import medievalsim.commandcenter.worldclick.WorldClickIntegration;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.ui.ButtonColor;

public class RelativeIntInputWidget
extends ParameterWidget {
    private FormTextInput input;
    private FormTextButton clickWorldButton;
    private FormTextButton useCurrentButton;
    private Client client;
    private static final int INPUT_WIDTH = 100;
    private static final int BUTTON_WIDTH = 70;
    private static final int BUTTON_SPACING = 5;

    public RelativeIntInputWidget(ParameterMetadata parameter, int x, int y) {
        super(parameter);
        this.input = new FormTextInput(x, y, FormInputSize.SIZE_16, 100, 200, 10);
        this.input.placeHolder = new StaticMessage("e.g. 1000 or %+100");
        int buttonX = x + 100 + 5;
        this.clickWorldButton = new FormTextButton("Click World", buttonX, y, 70, FormInputSize.SIZE_16, ButtonColor.BASE);
        this.clickWorldButton.onClicked(btn -> this.onClickWorldPressed());
        this.useCurrentButton = new FormTextButton("Current Pos", buttonX += 75, y, 70, FormInputSize.SIZE_16, ButtonColor.BASE);
        this.useCurrentButton.onClicked(btn -> this.onUseCurrentPressed());
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void onClickWorldPressed() {
        if (this.client == null) {
            System.err.println("[RelativeIntInputWidget] Cannot start world-click: client is null");
            return;
        }
        WorldClickHandler.getInstance().startSelection(this.client, (tileX, tileY) -> {
            String paramName = this.parameter.getName().toLowerCase();
            if (paramName.contains("x") || paramName.equals("tilexoffset")) {
                this.input.setText(String.valueOf(tileX));
            } else if (paramName.contains("y") || paramName.equals("tileyoffset")) {
                this.input.setText(String.valueOf(tileY));
            } else {
                this.input.setText(String.valueOf(tileX));
            }
            this.validate();
        });
        WorldClickIntegration.startIntegration(this.client);
    }

    private void onUseCurrentPressed() {
        if (this.client == null) {
            System.err.println("[RelativeIntInputWidget] Cannot use current position: client is null");
            return;
        }
        PlayerMob player = this.client.getPlayer();
        if (player == null) {
            System.err.println("[RelativeIntInputWidget] Cannot use current position: player is null");
            return;
        }
        int tileX = (int)(player.x / 32.0f);
        int tileY = (int)(player.y / 32.0f);
        String paramName = this.parameter.getName().toLowerCase();
        if (paramName.contains("x") || paramName.equals("tilexoffset")) {
            this.input.setText(String.valueOf(tileX));
        } else if (paramName.contains("y") || paramName.equals("tileyoffset")) {
            this.input.setText(String.valueOf(tileY));
        } else {
            this.input.setText(String.valueOf(tileX));
        }
        this.validate();
    }

    @Override
    public FormComponent getComponent() {
        return this.input;
    }

    public FormTextButton getClickWorldButton() {
        return this.clickWorldButton;
    }

    public FormTextButton getUseCurrentButton() {
        return this.useCurrentButton;
    }

    @Override
    public String getValue() {
        String value = this.input.getText().trim();
        return value;
    }

    @Override
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            this.input.setText("");
        } else {
            this.input.setText(value.trim());
        }
    }

    @Override
    protected boolean validateValue() {
        String value = this.input.getText().trim();
        if (value.isEmpty()) {
            return !this.parameter.isRequired();
        }
        if (value.startsWith("%")) {
            String numPart = value.substring(1);
            if (numPart.isEmpty()) {
                this.validationError = "Relative syntax requires a number (e.g. %+100)";
                return false;
            }
            if (!numPart.startsWith("+") && !numPart.startsWith("-")) {
                this.validationError = "Relative syntax requires %+ or %- (e.g. %+100)";
                return false;
            }
            String number = numPart.substring(1);
            if (number.isEmpty()) {
                this.validationError = "Relative syntax requires a number after +/- (e.g. %+100)";
                return false;
            }
            try {
                Integer.parseInt(number);
                return true;
            }
            catch (NumberFormatException e) {
                this.validationError = "Invalid number in relative syntax: " + number;
                return false;
            }
        }
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e) {
            this.validationError = "Must be an integer or %+N syntax (e.g. 1000 or %+100)";
            return false;
        }
    }

    @Override
    public void reset() {
        this.input.setText("");
        this.isValid = this.parameter.isOptional();
        this.validationError = null;
    }

    @Override
    public void onFocus() {
        this.input.setTyping(true);
    }

    @Override
    public void onBlur() {
        this.validate();
    }
}

