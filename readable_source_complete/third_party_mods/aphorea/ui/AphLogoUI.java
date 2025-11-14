/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.MainMenuFormManager
 *  necesse.gfx.forms.components.FormComponent
 */
package aphorea.ui;

import aphorea.AphResources;
import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.MainMenuFormManager;
import necesse.gfx.forms.components.FormComponent;

public class AphLogoUI {
    public static AphLogoForm form;

    public static void setup(MainMenuFormManager mainMenuFormManager) {
        form = (AphLogoForm)mainMenuFormManager.mainForm.main.addComponent((FormComponent)new AphLogoForm(AphResources.modLogo.getWidth(), AphResources.modLogo.getHeight()));
        AphLogoUI.updatePosition(mainMenuFormManager);
    }

    public static void onWindowResized(MainMenuFormManager mainMenuFormManager) {
        AphLogoUI.updatePosition(mainMenuFormManager);
    }

    public static void updatePosition(MainMenuFormManager mainMenuFormManager) {
        Form mainForm = mainMenuFormManager.mainForm.mainForm;
        form.setX(mainForm.getX() + (mainForm.getWidth() - form.getWidth()) / 2);
        form.setY(mainForm.getY() - form.getHeight() - 20);
    }

    public static class AphLogoForm
    extends Form {
        public AphLogoForm(int width, int height) {
            super("aphlogoui", width, height);
        }

        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            AphResources.modLogo.initDraw().draw(this.getX(), this.getY());
        }
    }
}

