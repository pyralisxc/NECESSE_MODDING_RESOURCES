/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.window.WindowManager
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormFlow
 *  necesse.gfx.forms.components.FormLabel
 *  necesse.gfx.forms.components.localComponents.FormLocalTextButton
 *  necesse.gfx.forms.presets.SettingsForm
 *  necesse.gfx.gameFont.FontOptions
 */
package customsettingslib.forms;

import customsettingslib.components.CustomModSetting;
import customsettingslib.components.SettingsComponents;
import customsettingslib.patches.SettingsFormPatches;
import customsettingslib.settings.CustomModSettings;
import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.SettingsForm;
import necesse.gfx.gameFont.FontOptions;

public class CustomModSettingsForm
extends Form {
    public CustomModSettingsForm(CustomModSettings customModSettings, int width, int height) {
        super(customModSettings.mod.id + "customsettingsform", width, height);
    }

    protected void resetComponents(CustomModSettings customModSettings) {
        FormContentBox settingsForm;
        this.clearComponents();
        this.addComponent((FormComponent)new FormLabel(customModSettings.mod.getModNameString(), new FontOptions(16), -1, 4, 6, this.getWidth() - 8));
        SettingsComponents.settingsForm = settingsForm = (FormContentBox)this.addComponent((FormComponent)new FormContentBox(0, 26, this.getWidth(), this.getHeight() - 80 - 28));
        SettingsComponents.customModSettings = customModSettings;
        FormFlow settingsFlow = new FormFlow(12);
        for (int i = 0; i < customModSettings.settingsDisplay.size(); ++i) {
            settingsFlow.next(customModSettings.settingsDisplay.get(i).addComponents(settingsFlow.next(4), i));
            settingsFlow.next(4);
        }
        settingsForm.setContentBox(new Rectangle(0, 0, this.getWidth(), settingsFlow.next()));
        int trueHeight = Math.max(200, settingsFlow.next() + 80 + 28);
        if (trueHeight < this.getHeight() || this.getHeight() < 600 && trueHeight > this.getHeight()) {
            this.setHeight(Math.min(trueHeight, 600));
            settingsForm.setHeight(settingsForm.getHeight() - (600 - this.getHeight()));
            this.setPosMiddle(WindowManager.getWindow().getWidth() / 2, WindowManager.getWindow().getHeight() / 2);
        }
        FormLocalTextButton restoreButton = (FormLocalTextButton)this.addComponent((FormComponent)new FormLocalTextButton("settingsui", "restoredefaultbindall", 4, this.getHeight() - 80, this.getWidth() - 8));
        restoreButton.onClicked(e -> {
            for (Runnable runnable : customModSettings.onSavedListeners) {
                runnable.run();
            }
            for (CustomModSetting customModSetting : customModSettings.settingsList) {
                if (!customModSetting.isEnabled()) continue;
                customModSetting.restoreToDefault();
            }
            Settings.saveClientSettings();
            this.resetComponents(customModSettings);
        });
        FormLocalTextButton saveButton = (FormLocalTextButton)this.addComponent((FormComponent)new FormLocalTextButton("ui", "savebutton", 4, this.getHeight() - 40, this.getWidth() / 2 - 6));
        saveButton.onClicked(e -> {
            for (Runnable runnable : customModSettings.onSavedListeners) {
                runnable.run();
            }
            for (CustomModSetting customModSetting : customModSettings.settingsList) {
                if (!customModSetting.isEnabled()) continue;
                customModSetting.onSave();
            }
            Settings.saveClientSettings();
        });
        FormLocalTextButton backButton = (FormLocalTextButton)this.addComponent((FormComponent)new FormLocalTextButton("ui", "backbutton", this.getWidth() / 2 + 2, this.getHeight() - 40, this.getWidth() / 2 - 6));
        backButton.onClicked(e -> SettingsFormPatches.settingsForm.makeCurrent((FormComponent)SettingsFormPatches.modSettingsForm));
    }

    public void makeCurrent(CustomModSettings customModSettings, SettingsForm settingsForm) {
        this.resetComponents(customModSettings);
        settingsForm.makeCurrent((FormComponent)this);
    }
}

