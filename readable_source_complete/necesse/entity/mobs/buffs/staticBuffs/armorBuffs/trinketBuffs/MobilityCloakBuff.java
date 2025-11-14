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
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.OutOfCombatBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class MobilityCloakBuff
extends OutOfCombatBuff {
    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
        if (this.isActive(buff) && (owner.dx != 0.0f || owner.dy != 0.0f) && owner.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), Particle.GType.IMPORTANT_COSMETIC).color(new Color(65, 30, 109)).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).lifeTime(300).height(2.0f);
        }
    }

    @Override
    protected void updateActive(ActiveBuff buff, boolean active) {
        if (active) {
            buff.setModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(5.0f));
            buff.setModifier(BuffModifiers.SPEED, Float.valueOf(0.15f));
        }
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "mobilitycloak"));
        return tooltips;
    }
}

