/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.inventory.InventoryItem;

public abstract class ProcessingHelp {
    public abstract boolean isProcessing();

    public abstract float getProcessingProgress();

    public abstract boolean needsFuel();

    public abstract InventoryItem getGhostItem(int var1);

    public abstract GameTooltips getTooltip(int var1, PlayerMob var2);

    public abstract GameTooltips getCurrentRecipeTooltip(PlayerMob var1);
}

