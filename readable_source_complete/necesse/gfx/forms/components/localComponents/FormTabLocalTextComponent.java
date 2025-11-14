/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.localComponents;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTabTextComponent;

public class FormTabLocalTextComponent
extends FormTabTextComponent {
    protected GameMessage localText;
    protected GameMessage localToolTip;

    public FormTabLocalTextComponent(GameMessage text, GameMessage toolTip, Form form, int x, FormInputSize size) {
        super("", null, form, x, size);
        this.setLocalization(text, toolTip);
    }

    public FormTabLocalTextComponent(GameMessage text, Form form, int x, FormInputSize size) {
        this(text, null, form, x, size);
    }

    public FormTabLocalTextComponent(String category, String key, Form form, int x, FormInputSize size) {
        this(new LocalMessage(category, key), form, x, size);
    }

    public FormTabLocalTextComponent(String category, String key, String tooltipKey, Form form, int x, FormInputSize size) {
        this(new LocalMessage(category, key), new LocalMessage(category, tooltipKey), form, x, size);
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                FormTabLocalTextComponent.this.updateText();
            }

            @Override
            public boolean isDisposed() {
                return FormTabLocalTextComponent.this.isDisposed();
            }
        });
    }

    @Override
    public void setText(String text) {
        super.setText(text);
    }

    public void setLocalization(GameMessage text, GameMessage tooltip) {
        this.localText = text;
        this.localToolTip = tooltip;
        this.updateText();
    }

    public void setLocalization(String category, String key) {
        this.setLocalization(new LocalMessage(category, key), null);
    }

    public void setLocalization(String category, String key, String tooltipKey) {
        this.setLocalization(new LocalMessage(category, key), new LocalMessage(category, tooltipKey));
    }

    private void updateText() {
        if (this.localToolTip != null) {
            this.setTooltip(this.localToolTip.translate());
        } else {
            this.setTooltip(null);
        }
        if (this.localText != null) {
            String translatedText = this.localText.translate();
            this.setText(translatedText);
        } else {
            this.setText("");
        }
    }
}

