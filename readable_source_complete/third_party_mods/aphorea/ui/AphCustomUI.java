/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.MainGameFormManager
 *  necesse.gfx.gameTexture.GameTexture
 */
package aphorea.ui;

import aphorea.ui.AphCustomUIList;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.Settings;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.gameTexture.GameTexture;

public abstract class AphCustomUI {
    public static final int TICK_MS = 25;
    public final String formId;
    public Form form;
    public MainGameFormManager mainGameFormManager;
    public static Map<String, GameTexture> textureMap = new HashMap<String, GameTexture>();

    protected AphCustomUI(String formId) {
        this.formId = formId;
        AphCustomUIList.list.put(formId, this);
    }

    public abstract void startForm();

    public abstract void updatePosition();

    public abstract int getWidth();

    public abstract int getHeight();

    public void updateSize() {
        this.form.setWidth(this.getWidth());
        this.form.setHeight(this.getHeight());
    }

    public void setupForm() {
        this.updatePosition();
    }

    public void onWindowResized() {
        this.updatePosition();
    }

    public void onUpdateSceneSize() {
    }

    public static float getZoom() {
        return Settings.sceneSize;
    }

    public static float showProgress(float chargePercent, float chargeTime) {
        float timeSinceStart = chargePercent * chargeTime;
        return (timeSinceStart + 25.0f) / chargeTime;
    }

    public static GameTexture getResizedTexture(String string, GameTexture originalTexture, int width, int height) {
        String id = string + "-" + String.format("%.1f", Float.valueOf(AphCustomUI.getZoom() / 2.0f));
        GameTexture texture = textureMap.get(id);
        if (texture == null) {
            texture = originalTexture.resize(width, height);
            textureMap.put(id, texture);
        }
        return texture;
    }

    public static GameTexture getResizedTextures(String string, GameTexture[] originalTexture, int width, int height, int index) {
        String id = string + index + "-" + String.format("%.1f", Float.valueOf(AphCustomUI.getZoom() / 2.0f));
        GameTexture texture = textureMap.get(id);
        if (texture == null) {
            for (int i = 0; i < originalTexture.length; ++i) {
                String textureId = string + i + "-" + String.format("%.1f", Float.valueOf(AphCustomUI.getZoom() / 2.0f));
                textureMap.put(textureId, originalTexture[i].resize(width, height));
            }
        }
        return textureMap.get(id);
    }
}

