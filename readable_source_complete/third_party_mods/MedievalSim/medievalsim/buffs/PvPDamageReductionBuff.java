/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.ListGameTooltips
 */
package medievalsim.buffs;

import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class PvPDamageReductionBuff
extends Buff {
    public PvPDamageReductionBuff() {
        this.isImportant = false;
        this.canCancel = false;
        this.isVisible = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void loadTextures() {
        this.iconTexture = GameTexture.fromFile((String)"buffs/pvpdamagereduction.png");
    }

    public ListGameTooltips getTooltip(ActiveBuff buff, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add("PVP damage reduced");
        return tooltips;
    }
}

