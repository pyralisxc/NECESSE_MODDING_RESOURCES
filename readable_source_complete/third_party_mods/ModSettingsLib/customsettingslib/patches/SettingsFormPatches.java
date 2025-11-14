/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModConstructorPatch
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.client.Client
 *  necesse.engine.window.GameWindow
 *  necesse.engine.window.WindowManager
 *  necesse.gfx.forms.ContinueComponentManager
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.forms.components.localComponents.FormLocalTextButton
 *  necesse.gfx.forms.position.FormFixedPosition
 *  necesse.gfx.forms.position.FormPosition
 *  necesse.gfx.forms.presets.SettingsForm
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package customsettingslib.patches;

import customsettingslib.forms.CustomModSettingsForm;
import customsettingslib.forms.ModSettingsForm;
import java.lang.reflect.Field;
import java.util.Iterator;
import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.presets.SettingsForm;
import net.bytebuddy.asm.Advice;

public class SettingsFormPatches {
    public static SettingsForm settingsForm;
    public static ModSettingsForm modSettingsForm;

    public static void construct() {
        modSettingsForm = (ModSettingsForm)settingsForm.addComponent((FormComponent)new ModSettingsForm(400, 400));
        modSettingsForm.setPosMiddle(WindowManager.getWindow().getWidth() / 2, WindowManager.getWindow().getHeight() / 2);
    }

    public static void setup() {
        try {
            Field mainMenuField = SettingsForm.class.getDeclaredField("mainMenu");
            mainMenuField.setAccessible(true);
            Form mainMenu = (Form)mainMenuField.get(settingsForm);
            mainMenu.setHeight(mainMenu.getHeight() + 40);
            FormComponent component = null;
            Iterator iterator = mainMenu.getComponentList().iterator();
            while (iterator.hasNext()) {
                FormComponent formComponent;
                component = formComponent = (FormComponent)iterator.next();
            }
            assert (component != null);
            FormTextButton backButton = (FormTextButton)component;
            FormPosition position = backButton.getPosition();
            backButton.setPosition((FormPosition)new FormFixedPosition(position.getX(), position.getY() + 40));
            FormLocalTextButton button = (FormLocalTextButton)mainMenu.addComponent((FormComponent)new FormLocalTextButton("ui", "modsettings", backButton.getX(), position.getY(), mainMenu.getWidth() - 8));
            button.onClicked(event -> {
                modSettingsForm.start();
                settingsForm.makeCurrent((FormComponent)modSettingsForm);
            });
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void windowResize() {
        if (modSettingsForm == null) {
            return;
        }
        modSettingsForm.setPosMiddle(WindowManager.getWindow().getWidth() / 2, WindowManager.getWindow().getHeight() / 2);
        for (CustomModSettingsForm customModSettingsForm : ModSettingsForm.customModSettingsForms) {
            customModSettingsForm.setPosMiddle(WindowManager.getWindow().getWidth() / 2, WindowManager.getWindow().getHeight() / 2);
        }
    }

    @ModMethodPatch(target=SettingsForm.class, name="onWindowResized", arguments={GameWindow.class})
    public static class onWindowResized {
        @Advice.OnMethodExit
        public static void onExit(@Advice.This SettingsForm This2) {
            SettingsFormPatches.windowResize();
        }
    }

    @ModMethodPatch(target=SettingsForm.class, name="setupMenuForm", arguments={})
    public static class setupMenuForm {
        @Advice.OnMethodExit
        public static void onExit(@Advice.This SettingsForm This2) {
            settingsForm = This2;
            SettingsFormPatches.setup();
        }
    }

    @ModConstructorPatch(target=SettingsForm.class, arguments={Client.class, ContinueComponentManager.class})
    public static class constructor {
        @Advice.OnMethodExit
        public static void onExit(@Advice.This SettingsForm This2) {
            settingsForm = This2;
            SettingsFormPatches.construct();
        }
    }
}

