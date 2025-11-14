/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.localComponents;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class FormLocalCheckBox
extends FormCheckBox {
    private GameMessage localMessage;
    private int maxWidth;

    public FormLocalCheckBox(GameMessage gameMessage, int x, int y, int maxWidth) {
        super("", x, y);
        this.setLocalization(gameMessage, maxWidth);
    }

    public FormLocalCheckBox(GameMessage gameMessage, int x, int y) {
        this(gameMessage, x, y, -1);
    }

    public FormLocalCheckBox(String category, String key, int x, int y, int maxWidth) {
        this((GameMessage)new LocalMessage(category, key), x, y, maxWidth);
    }

    public FormLocalCheckBox(String category, String key, int x, int y) {
        this(new LocalMessage(category, key), x, y);
    }

    public FormLocalCheckBox(GameMessage gameMessage, int x, int y, boolean checked, int maxWidth) {
        super("", x, y, checked);
        this.setLocalization(gameMessage, maxWidth);
    }

    public FormLocalCheckBox(GameMessage gameMessage, int x, int y, boolean checked) {
        this(gameMessage, x, y, checked, -1);
    }

    public FormLocalCheckBox(String category, String key, int x, int y, boolean checked) {
        this((GameMessage)new LocalMessage(category, key), x, y, checked);
    }

    public FormLocalCheckBox(String category, String key, int x, int y, boolean checked, int maxWidth) {
        this(new LocalMessage(category, key), x, y, checked, maxWidth);
    }

    @Override
    public FormLocalCheckBox useButtonTexture(ButtonColor color, ButtonIcon buttonCheckedIcon, ButtonIcon buttonUncheckedIcon) {
        super.useButtonTexture(color, buttonCheckedIcon, buttonUncheckedIcon);
        return this;
    }

    @Override
    public FormLocalCheckBox useButtonTexture(ButtonColor color) {
        super.useButtonTexture(color);
        return this;
    }

    @Override
    public FormLocalCheckBox useButtonTexture() {
        super.useButtonTexture();
        return this;
    }

    public void setLocalization(GameMessage localMessage, int maxWidth) {
        this.localMessage = localMessage;
        this.maxWidth = maxWidth;
        if (this.localMessage != null) {
            this.setText(this.localMessage.translate(), maxWidth);
        } else {
            this.setText("", maxWidth);
        }
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                FormLocalCheckBox.this.setText(FormLocalCheckBox.this.localMessage == null ? "" : FormLocalCheckBox.this.localMessage.translate(), FormLocalCheckBox.this.maxWidth);
            }

            @Override
            public boolean isDisposed() {
                return FormLocalCheckBox.this.isDisposed();
            }
        });
    }

    public void setLocalization(GameMessage localMessage) {
        this.setLocalization(localMessage, -1);
    }

    public void setLocalization(String category, String key) {
        this.setLocalization(new LocalMessage(category, key));
    }
}

