/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.localComponents;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;

public class FormLocalLabel
extends FormLabel {
    private GameMessage localMessage;
    private int maxWidth;

    public FormLocalLabel(GameMessage localMessage, FontOptions fontOptions, int align, int x, int y) {
        super("", fontOptions, align, x, y);
        this.setLocalization(localMessage);
    }

    public FormLocalLabel(GameMessage localMessage, FontOptions fontOptions, int align, int x, int y, int maxWidth) {
        super("", fontOptions, align, x, y);
        this.setLocalization(localMessage, maxWidth);
    }

    public FormLocalLabel(String category, String key, FontOptions fontOptions, int align, int x, int y) {
        this(new LocalMessage(category, key), fontOptions, align, x, y);
    }

    public FormLocalLabel(String category, String key, FontOptions fontOptions, int align, int x, int y, int maxWidth) {
        this(new LocalMessage(category, key), fontOptions, align, x, y, maxWidth);
    }

    public void setLocalization(GameMessage localMessage) {
        this.localMessage = localMessage;
        this.updateText();
    }

    public void setLocalization(GameMessage localMessage, int maxWidth) {
        this.maxWidth = maxWidth;
        this.setLocalization(localMessage);
    }

    public void setLocalization(String category, String key) {
        this.setLocalization(new LocalMessage(category, key));
    }

    public void setLocalization(String category, String key, int maxWidth) {
        this.maxWidth = maxWidth;
        this.setLocalization(category, key);
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                FormLocalLabel.this.updateText();
            }

            @Override
            public boolean isDisposed() {
                return FormLocalLabel.this.isDisposed();
            }
        });
    }

    private void updateText() {
        if (this.localMessage != null) {
            if (this.maxWidth <= 0) {
                this.setText(this.localMessage);
            } else {
                this.setText(this.localMessage, this.maxWidth);
            }
        } else {
            this.setText("");
        }
    }
}

