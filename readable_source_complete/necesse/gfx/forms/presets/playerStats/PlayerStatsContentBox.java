/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.playerStats;

import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.presets.playerStats.PlayerStatsForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSubForm;

public class PlayerStatsContentBox
extends FormContentBox
implements PlayerStatsSubForm {
    public final PlayerStatsForm statsForm;

    public PlayerStatsContentBox(PlayerStatsForm statsForm) {
        super(0, 0, statsForm.getWidth(), statsForm.getHeight());
        this.statsForm = statsForm;
    }

    @Override
    public void updateDisabled(int headerHeight) {
        this.setPosition(0, headerHeight);
        this.setHeight(this.statsForm.getHeight() - headerHeight);
    }
}

