/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.window.WindowManager
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormComponent
 */
package aphorea.ui;

import aphorea.AphResources;
import aphorea.ui.AphCustomUI;
import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;

public class SaberAttackUIManger
extends AphCustomUI {
    public float chargePercent;
    public int chargeTime;
    public static int baseWidth = 66;
    public static int baseHeight = 22;

    public static int getLoweredY() {
        return (int)(34.0f * SaberAttackUIManger.getZoom());
    }

    public SaberAttackUIManger(String formId) {
        super(formId);
    }

    @Override
    public void startForm() {
        this.form = (Form)this.mainGameFormManager.addComponent((FormComponent)new AttackTrackForm(this.formId, this.getWidth(), this.getHeight()));
    }

    @Override
    public void updatePosition() {
        this.form.setPosition(WindowManager.getWindow().getHudWidth() / 2 - this.form.getWidth() / 2, WindowManager.getWindow().getHudHeight() / 2 - this.form.getHeight() / 2 + SaberAttackUIManger.getLoweredY());
    }

    @Override
    public int getWidth() {
        return (int)((float)AphResources.glacialSaberAttackTrackTexture.getWidth() * SaberAttackUIManger.getZoom());
    }

    @Override
    public int getHeight() {
        return (int)((float)AphResources.glacialSaberAttackTrackTexture.getHeight() * SaberAttackUIManger.getZoom());
    }

    @Override
    public void setupForm() {
        this.form.setHidden(true);
        super.setupForm();
    }

    @Override
    public void onWindowResized() {
        this.updatePosition();
        this.updateSize();
    }

    public static float barPercent(float chargePercent) {
        if (chargePercent < 0.0f || chargePercent > 2.0f) {
            return 0.0f;
        }
        return 1.0f - (float)Math.sin((double)chargePercent * 1.5707963267948966 + 1.5707963267948966 * (double)(chargePercent <= 1.0f ? 1 : -1));
    }

    public class AttackTrackForm
    extends Form {
        public AttackTrackForm(String name, int width, int height) {
            super(name, width, height);
        }

        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            float progress = SaberAttackUIManger.barPercent(AphCustomUI.showProgress(SaberAttackUIManger.this.chargePercent, SaberAttackUIManger.this.chargeTime));
            int spriteY = (int)(27.0f - 27.0f * progress);
            AphCustomUI.getResizedTextures("saberattack", AphResources.saberAttackTexture, (int)((float)baseWidth * AphCustomUI.getZoom()), (int)((float)baseHeight * AphCustomUI.getZoom()), spriteY).initDraw().pos(this.getX(), this.getY()).draw();
        }
    }
}

