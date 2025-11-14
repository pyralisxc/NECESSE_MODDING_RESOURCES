/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormDropdownSelectionButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.commandcenter.wrapper.widgets;

import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;

public class PermissionLevelDropdownWidget
extends ParameterWidget {
    private FormDropdownSelectionButton<PermissionLevel> permissionDropdown;

    public PermissionLevelDropdownWidget(ParameterMetadata parameter, int x, int y) {
        super(parameter);
        this.permissionDropdown = new FormDropdownSelectionButton(x, y, FormInputSize.SIZE_32, ButtonColor.BASE, 150, (GameMessage)new StaticMessage("Select permission..."));
        for (PermissionLevel level : PermissionLevel.values()) {
            this.permissionDropdown.options.add((Object)level, (GameMessage)new StaticMessage(level.name()));
        }
        this.permissionDropdown.onSelected(event -> {
            this.currentValue = event.value != null ? ((PermissionLevel)event.value).name().toLowerCase() : null;
        });
    }

    @Override
    public String getValue() {
        PermissionLevel selected = (PermissionLevel)this.permissionDropdown.getSelected();
        return selected != null ? selected.name().toLowerCase() : null;
    }

    @Override
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            this.currentValue = null;
            return;
        }
        for (PermissionLevel level : PermissionLevel.values()) {
            if (!level.name().equalsIgnoreCase(value.trim())) continue;
            this.permissionDropdown.setSelected((Object)level, (GameMessage)new StaticMessage(level.name()));
            this.currentValue = value.trim().toLowerCase();
            return;
        }
        this.currentValue = null;
    }

    @Override
    public boolean validateValue() {
        String value = this.getValue();
        if (this.parameter.isRequired() && (value == null || value.trim().isEmpty())) {
            this.validationError = "Please select a permission level";
            return false;
        }
        this.validationError = null;
        return true;
    }

    @Override
    public FormComponent getComponent() {
        return this.permissionDropdown;
    }
}

