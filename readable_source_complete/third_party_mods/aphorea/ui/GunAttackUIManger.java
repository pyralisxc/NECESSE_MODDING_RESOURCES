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

public class GunAttackUIManger
extends AphCustomUI {
    public float chargePercent;
    public int chargeTime;

    public static int getBorderAdded() {
        return (int)(10.0f * GunAttackUIManger.getZoom());
    }

    public static int getLoweredY() {
        return (int)(34.0f * GunAttackUIManger.getZoom());
    }

    public GunAttackUIManger(String formId) {
        super(formId);
    }

    @Override
    public void startForm() {
        this.form = (Form)this.mainGameFormManager.addComponent((FormComponent)new AttackTrackForm(this.formId, this.getWidth(), this.getHeight()));
    }

    @Override
    public void updatePosition() {
        this.form.setPosition(WindowManager.getWindow().getHudWidth() / 2 - this.form.getWidth() / 2, WindowManager.getWindow().getHudHeight() / 2 - this.form.getHeight() / 2 + GunAttackUIManger.getLoweredY());
    }

    @Override
    public int getWidth() {
        return (int)((float)AphResources.gunAttackTrackTexture.getWidth() * GunAttackUIManger.getZoom()) + GunAttackUIManger.getBorderAdded() * 2;
    }

    @Override
    public int getHeight() {
        return (int)((float)AphResources.gunAttackTrackTexture.getHeight() * GunAttackUIManger.getZoom()) + GunAttackUIManger.getBorderAdded() * 2;
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
        float cycleLength = 2.0f;
        float radians = chargePercent / cycleLength * (float)Math.PI * 2.0f;
        return (float)Math.cos(radians);
    }

    public static float barY(float chargePercent) {
        float cycleLength = 2.0f;
        float radians = chargePercent / cycleLength * (float)Math.PI * 2.0f;
        return Math.abs((float)Math.sin(radians));
    }

    public class AttackTrackForm
    extends Form {
        public AttackTrackForm(String name, int width, int height) {
            super(name, width, height);
        }

        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            int trackWidth = (int)((float)AphResources.gunAttackTrackTexture.getWidth() * AphCustomUI.getZoom());
            int trackHeight = (int)((float)AphResources.gunAttackTrackTexture.getHeight() * AphCustomUI.getZoom());
            AphCustomUI.getResizedTexture("gunattacktrack", AphResources.gunAttackTrackTexture, trackWidth, trackHeight).initDraw().pos(this.getX() + GunAttackUIManger.getBorderAdded(), this.getY() + GunAttackUIManger.getBorderAdded()).draw();
            int thumbWidth = (int)((float)AphResources.gunAttackThumbTexture.getWidth() * AphCustomUI.getZoom());
            int thumbHeight = (int)((float)AphResources.gunAttackThumbTexture.getHeight() * AphCustomUI.getZoom());
            int midX = this.getWidth() / 2 + this.getX() - thumbWidth / 2;
            int midY = this.getHeight() / 2 + this.getY() - thumbHeight / 2;
            float progressX = GunAttackUIManger.barPercent(AphCustomUI.showProgress(GunAttackUIManger.this.chargePercent, GunAttackUIManger.this.chargeTime));
            float progressY = GunAttackUIManger.barY(AphCustomUI.showProgress(GunAttackUIManger.this.chargePercent, GunAttackUIManger.this.chargeTime));
            AphCustomUI.getResizedTexture("gunattackthumb", AphResources.gunAttackThumbTexture, thumbWidth, thumbHeight).initDraw().pos(midX + (int)((float)trackWidth * progressX * 0.5f), midY + (int)((float)trackHeight * (progressY - 1.0f) * 0.5f)).draw();
        }
    }
}

