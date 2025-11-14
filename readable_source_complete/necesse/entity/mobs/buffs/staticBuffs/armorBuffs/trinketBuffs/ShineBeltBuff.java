/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

public class ShineBeltBuff
extends TrinketBuff {
    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "shinebelttip"));
        return tooltips;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Level level = buff.owner.getLevel();
        if (level.tickManager().getTotalTicks() % 5L == 0L) {
            level.entityManager.addParticle(buff.owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), buff.owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(buff.owner.dx / 10.0f, buff.owner.dy / 10.0f).color(new Color(249, 226, 117)).sizeFades(6, 10).height(16.0f);
        }
        level.lightManager.refreshParticleLightFloat(buff.owner.x, buff.owner.y, 50.0f, 0.4f, 135);
    }
}

