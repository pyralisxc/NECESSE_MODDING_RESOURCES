/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class FinsBuff
extends TrinketBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SWIM_SPEED, Float.valueOf(1.0f));
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "finstip"));
        return tooltips;
    }

    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
        if (owner.inLiquid() && (owner.dx != 0.0f || owner.dy != 0.0f) && owner.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
            owner.getLevel().entityManager.addParticle(owner.x - Math.signum(owner.dx) * 10.0f + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), owner.y - Math.signum(owner.dy) * 10.0f + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(new Color(89, 139, 224));
        }
    }
}

