/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.inventory.item.StringItemStatTip;

public class LocalMessageStringItemStatTip
extends StringItemStatTip {
    protected String localeCategory;
    protected String localeKey;
    protected String replaceKey;

    public LocalMessageStringItemStatTip(String localeCategory, String localeKey, String replaceKey, String value) {
        super(value);
        this.localeCategory = localeCategory;
        this.localeKey = localeKey;
        this.replaceKey = replaceKey;
    }

    @Override
    public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
        return new LocalMessage(this.localeCategory, this.localeKey, this.replaceKey, this.getReplaceValue(betterColor, worseColor, neutralColor, showDifference));
    }
}

