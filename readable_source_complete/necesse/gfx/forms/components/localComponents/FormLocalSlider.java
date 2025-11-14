/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.localComponents;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.gameFont.FontOptions;

public class FormLocalSlider
extends FormSlider {
    private GameMessage localMessage;

    public FormLocalSlider(GameMessage localMessage, int x, int y, int startValue, int minValue, int maxValue, int width, FontOptions fontOptions) {
        super("", x, y, startValue, minValue, maxValue, width, fontOptions);
        this.setLocalization(localMessage);
    }

    public FormLocalSlider(String category, String key, int x, int y, int startValue, int minValue, int maxValue, int width, FontOptions fontOptions) {
        this(new LocalMessage(category, key), x, y, startValue, minValue, maxValue, width, fontOptions);
    }

    public FormLocalSlider(GameMessage localMessage, int x, int y, int startValue, int minValue, int maxValue, int width) {
        super("", x, y, startValue, minValue, maxValue, width);
        this.setLocalization(localMessage);
    }

    public FormLocalSlider(String category, String key, int x, int y, int startValue, int minValue, int maxValue, int width) {
        this(new LocalMessage(category, key), x, y, startValue, minValue, maxValue, width);
    }

    public void setLocalization(GameMessage localMessage) {
        this.localMessage = localMessage;
        this.updateText();
    }

    public void setLocalization(String category, String key) {
        this.setLocalization(new LocalMessage(category, key));
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                FormLocalSlider.this.updateText();
            }

            @Override
            public boolean isDisposed() {
                return FormLocalSlider.this.isDisposed();
            }
        });
    }

    private void updateText() {
        this.text = this.localMessage != null ? this.localMessage.translate() : "";
    }
}

