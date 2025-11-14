/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.window.WindowManager
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormContentBox
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.forms.components.localComponents.FormLocalTextButton
 */
package customsettingslib.forms;

import customsettingslib.forms.CustomModSettingsForm;
import customsettingslib.patches.SettingsFormPatches;
import customsettingslib.settings.CustomModSettings;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;

public class ModSettingsForm
extends Form {
    public boolean started = false;
    public static List<CustomModSettingsForm> customModSettingsForms = new ArrayList<CustomModSettingsForm>();

    public ModSettingsForm(int width, int height) {
        super("modSettings", width, height);
    }

    public void start() {
        if (!this.started) {
            FormContentBox modSettingsButtons = (FormContentBox)this.addComponent((FormComponent)new FormContentBox(0, 0, this.getWidth(), this.getHeight() - 40));
            int contentHeight = 8 + CustomModSettings.customModSettingsList.size() * 40;
            int buttonWidth = modSettingsButtons.getWidth() - 8 - (contentHeight > modSettingsButtons.getHeight() ? 12 : 0);
            for (int i = 0; i < CustomModSettings.customModSettingsList.size(); ++i) {
                CustomModSettings customModSetting = CustomModSettings.customModSettingsList.get(i);
                CustomModSettingsForm customModSettingsForm = (CustomModSettingsForm)SettingsFormPatches.settingsForm.addComponent((FormComponent)new CustomModSettingsForm(customModSetting, 400, 600));
                customModSettingsForm.setPosMiddle(WindowManager.getWindow().getWidth() / 2, WindowManager.getWindow().getHeight() / 2);
                customModSettingsForms.add(customModSettingsForm);
                modSettingsButtons.addComponent((FormComponent)new FormTextButton(customModSetting.mod.name, 4, 4 + i * 40, buttonWidth).onClicked(event -> customModSettingsForm.makeCurrent(customModSetting, SettingsFormPatches.settingsForm)));
            }
            modSettingsButtons.setContentBox(new Rectangle(0, 0, this.getWidth(), contentHeight));
            FormLocalTextButton backButton = (FormLocalTextButton)this.addComponent((FormComponent)new FormLocalTextButton("ui", "backbutton", 4, this.getHeight() - 40, this.getWidth() - 8));
            backButton.onClicked(e -> SettingsFormPatches.settingsForm.subMenuBackPressed());
        }
    }
}

