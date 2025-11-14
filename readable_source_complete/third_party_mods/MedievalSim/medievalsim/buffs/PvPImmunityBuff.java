/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.gameTooltips.ListGameTooltips
 */
package medievalsim.buffs;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class PvPImmunityBuff
extends Buff {
    public PvPImmunityBuff() {
        this.isImportant = true;
        this.canCancel = false;
        this.isVisible = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible() && buff.owner.getLevel().tickManager().isGameTick() && buff.owner.getLevel().getWorldEntity().getTime() % 10L == 0L) {
            buff.owner.getLevel().entityManager.addParticle(buff.owner.x + (float)(Math.random() * 20.0 - 10.0), buff.owner.y + (float)(Math.random() * 20.0 - 10.0), Particle.GType.IMPORTANT_COSMETIC).color(new Color(100, 200, 255, 150)).givesLight(0.3f, 0.6f).height(16.0f).lifeTime(500);
        }
    }

    public ListGameTooltips getTooltip(ActiveBuff buff, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate((String)"buff", (String)"pvpimmunity"));
        tooltips.add(Localization.translate((String)"buff", (String)"pvpimmunitydesc"));
        return tooltips;
    }
}

