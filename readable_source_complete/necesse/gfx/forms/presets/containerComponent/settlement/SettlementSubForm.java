/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;

public interface SettlementSubForm {
    public void onSetCurrent(boolean var1);

    public GameMessage getMenuButtonName();

    public String getTypeString();

    default public void onMenuButtonClicked(FormSwitcher switcher) {
        switcher.makeCurrent((FormComponent)((Object)this));
    }

    default public SettlementToolHandler getToolHandler() {
        return null;
    }
}

