/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.client.Client
 *  necesse.engine.world.WorldSettings
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.engine.network.client.Client;
import necesse.engine.world.WorldSettings;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.ui.ButtonColor;

public class ToggleButtonWidget
extends ParameterWidget {
    private FormTextButton button;
    private boolean currentState;
    private final Client client;
    private final int x;
    private final int y;

    public ToggleButtonWidget(ParameterMetadata parameter, int x, int y, Client client) {
        this(parameter, x, y, client, null);
    }

    public ToggleButtonWidget(ParameterMetadata parameter, int x, int y, Client client, String defaultValue) {
        super(parameter);
        this.client = client;
        this.x = x;
        this.y = y;
        this.currentState = defaultValue != null ? Boolean.parseBoolean(defaultValue) : this.getCurrentStateFromWorld(parameter.getName());
        this.createButton();
    }

    private void createButton() {
        this.button = new FormTextButton(this.getButtonText(), this.x, this.y, 200, FormInputSize.SIZE_32, this.getButtonColor());
        this.button.onClicked(e -> {
            this.currentState = !this.currentState;
            this.createButton();
        });
    }

    private boolean getCurrentStateFromWorld(String paramName) {
        if (this.client == null || this.client.worldSettings == null) {
            return false;
        }
        WorldSettings settings = this.client.worldSettings;
        try {
            switch (paramName.toLowerCase()) {
                case "creative": 
                case "creativemode": {
                    return settings.creativeMode;
                }
                case "hunger": 
                case "playerhunger": {
                    return settings.playerHunger;
                }
                case "cheats": 
                case "allowcheats": {
                    return settings.allowCheats;
                }
                case "pvp": 
                case "forcedpvp": {
                    return settings.forcedPvP;
                }
                case "mobspawns": 
                case "disablemobspawns": {
                    return settings.disableMobSpawns;
                }
                case "mobai": 
                case "disablemobai": {
                    return settings.disableMobAI;
                }
                case "survival": 
                case "survivalmode": {
                    return settings.survivalMode;
                }
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    private String getButtonText() {
        return this.currentState ? "Enabled" : "Disabled";
    }

    private ButtonColor getButtonColor() {
        return this.currentState ? ButtonColor.GREEN : ButtonColor.RED;
    }

    private void updateButtonAppearance() {
        this.createButton();
    }

    @Override
    public String getValue() {
        return this.currentState ? "true" : "false";
    }

    @Override
    public void setValue(String value) {
        this.currentState = value == null ? false : value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes");
        this.updateButtonAppearance();
    }

    @Override
    public boolean validateValue() {
        return true;
    }

    @Override
    public void reset() {
        this.currentState = this.getCurrentStateFromWorld(this.parameter.getName());
        this.updateButtonAppearance();
        this.isValid = true;
        this.validationError = null;
    }

    @Override
    public void onFocus() {
        boolean worldState = this.getCurrentStateFromWorld(this.parameter.getName());
        if (worldState != this.currentState) {
            this.currentState = worldState;
            this.updateButtonAppearance();
        }
    }

    @Override
    public FormComponent getComponent() {
        return this.button;
    }

    public void setState(boolean state) {
        this.currentState = state;
        this.updateButtonAppearance();
    }

    public boolean getState() {
        return this.currentState;
    }
}

