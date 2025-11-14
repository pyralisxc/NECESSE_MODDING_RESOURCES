/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButtonToggle;
import necesse.gfx.ui.ButtonColor;

public class FormLocalTextButtonToggle
extends FormTextButtonToggle {
    private GameMessage localMessage;
    private GameMessage localTooltip;

    public FormLocalTextButtonToggle(GameMessage localMessage, int x, int y, int width, FormInputSize size, ButtonColor color) {
        super("", x, y, width, size, color);
        this.setLocalization(localMessage);
    }

    public FormLocalTextButtonToggle(GameMessage localMessage, int x, int y, int width) {
        super("", x, y, width);
        this.setLocalization(localMessage);
    }

    public FormLocalTextButtonToggle(String category, String key, int x, int y, int width, FormInputSize size, ButtonColor color) {
        this(new LocalMessage(category, key), x, y, width, size, color);
    }

    public FormLocalTextButtonToggle(String category, String key, int x, int y, int width) {
        this(new LocalMessage(category, key), x, y, width);
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                FormLocalTextButtonToggle.this.setText(FormLocalTextButtonToggle.this.localMessage == null ? "" : FormLocalTextButtonToggle.this.localMessage.translate());
                FormLocalTextButtonToggle.this.setTooltip(FormLocalTextButtonToggle.this.localTooltip == null ? "" : FormLocalTextButtonToggle.this.localTooltip.translate());
            }

            @Override
            public boolean isDisposed() {
                return FormLocalTextButtonToggle.this.isDisposed();
            }
        });
    }

    public void setLocalization(GameMessage localMessage) {
        this.localMessage = localMessage;
        if (this.localMessage != null) {
            this.setText(this.localMessage.translate());
        } else {
            this.setText("");
        }
    }

    public void setLocalization(String category, String key) {
        this.setLocalization(new LocalMessage(category, key));
    }

    public void setLocalTooltip(GameMessage localTooltip) {
        this.localTooltip = localTooltip;
        if (this.localTooltip != null) {
            this.setTooltip(this.localTooltip.translate());
        } else {
            this.setTooltip("");
        }
    }

    public void setLocalTooltip(String category, String key) {
        this.setLocalTooltip(new LocalMessage(category, key));
    }
}

