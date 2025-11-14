/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.Level;

public class SupporterSetBonusBuff
extends SetBonusBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltip = super.getTooltip(ab, blackboard);
        StringTooltips stringTooltips = new StringTooltips(Localization.translate("itemtooltip", "supportersetbonus"), GameColor.ITEM_UNIQUE);
        tooltip.add(stringTooltips);
        return tooltip;
    }

    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
        Level level = owner.getLevel();
        float height = GameRandom.globalRandom.getFloatBetween(4.0f, 24.0f);
        level.entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(new Color(255, 213, 0)).sizeFades(8, 14).height(height);
        level.lightManager.refreshParticleLightFloat(owner.x, owner.y, 0.0f, 0.8f, 50);
    }
}

