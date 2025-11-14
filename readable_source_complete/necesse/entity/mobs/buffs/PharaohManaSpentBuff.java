/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import java.io.FileNotFoundException;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class PharaohManaSpentBuff
extends Buff {
    protected int manaNeeded = 75;

    public PharaohManaSpentBuff() {
        this.isImportant = true;
        this.isVisible = true;
    }

    public void setManaNeeded(int value) {
        this.manaNeeded = value;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 75;
    }

    @Override
    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("buffs/manaspent");
        }
        catch (FileNotFoundException e) {
            this.iconTexture = GameTexture.fromFile("buffs/unknown");
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltip = super.getTooltip(ab, blackboard);
        tooltip.add(Localization.translate("bufftooltip", "pharaohmanaspent", "value", (Object)this.manaNeeded), 400);
        return tooltip;
    }
}

