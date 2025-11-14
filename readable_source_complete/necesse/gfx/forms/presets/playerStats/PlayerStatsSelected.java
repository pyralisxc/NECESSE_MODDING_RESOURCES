/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.playerStats;

public interface PlayerStatsSelected {
    public void onSelected();

    default public boolean backPressed() {
        return false;
    }
}

