/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Language
 *  necesse.engine.localization.Localization
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
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;

public class LanguageDropdownWidget
extends ParameterWidget {
    private FormDropdownSelectionButton<Language> languageDropdown;
    private Language[] availableLanguages = Localization.getLanguages();

    public LanguageDropdownWidget(ParameterMetadata parameter, int x, int y) {
        super(parameter);
        this.languageDropdown = new FormDropdownSelectionButton(x, y, FormInputSize.SIZE_32, ButtonColor.BASE, 200, (GameMessage)new StaticMessage("Select language..."));
        for (Language lang : this.availableLanguages) {
            this.languageDropdown.options.add((Object)lang, (GameMessage)new StaticMessage(lang.localDisplayName));
        }
        this.languageDropdown.onSelected(event -> {
            this.currentValue = event.value != null ? ((Language)event.value).stringID : null;
        });
    }

    @Override
    public String getValue() {
        Language selected = (Language)this.languageDropdown.getSelected();
        return selected != null ? selected.stringID : null;
    }

    @Override
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            this.currentValue = null;
            return;
        }
        for (Language lang : this.availableLanguages) {
            if (!lang.stringID.equalsIgnoreCase(value.trim())) continue;
            this.languageDropdown.setSelected((Object)lang, (GameMessage)new StaticMessage(lang.localDisplayName));
            this.currentValue = value.trim();
            return;
        }
        this.currentValue = null;
    }

    @Override
    public boolean validateValue() {
        String value = this.getValue();
        if (this.parameter.isRequired() && (value == null || value.trim().isEmpty())) {
            this.validationError = "Please select a language";
            return false;
        }
        this.validationError = null;
        return true;
    }

    @Override
    public FormComponent getComponent() {
        return this.languageDropdown;
    }
}

