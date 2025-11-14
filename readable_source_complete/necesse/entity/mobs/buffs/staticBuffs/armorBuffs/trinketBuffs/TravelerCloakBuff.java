/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import java.awt.geom.Point2D;
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
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.LiquidTile;

public class TravelerCloakBuff
extends OutOfCombatBuff {
    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
        if (this.isActive(buff) && (owner.dx != 0.0f || owner.dy != 0.0f)) {
            Color color;
            if (owner.inLiquid()) {
                GameTile tile = owner.getLevel().getTile(owner.getTileX(), owner.getTileY());
                color = tile.isLiquid ? ((LiquidTile)tile).getLiquidColor(owner.getLevel(), owner.getTileX(), owner.getTileY()).brighter() : new Color(89, 139, 224);
            } else {
                color = new Color(65, 30, 109);
            }
            boolean next = buff.getGndData().getBoolean("pAlt");
            buff.getGndData().setBoolean("pAlt", !next);
            int dir = owner.getDir();
            Point2D.Float pos = dir == 0 || dir == 2 ? new Point2D.Float(owner.x + (float)(next ? -4 : 4), owner.y) : new Point2D.Float(owner.x, owner.y + (float)(next ? -4 : 4));
            owner.getLevel().entityManager.addParticle(pos.x + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), pos.y + (float)(GameRandom.globalRandom.nextGaussian() * 2.0), Particle.GType.IMPORTANT_COSMETIC).color(color).sizeFades(10, 12).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).lifeTime(300).height(0.0f);
        }
    }

    @Override
    protected void updateActive(ActiveBuff buff, boolean active) {
        if (active) {
            buff.setModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(5.0f));
            buff.setModifier(BuffModifiers.SPEED, Float.valueOf(0.15f));
            buff.setModifier(BuffModifiers.SWIM_SPEED, Float.valueOf(1.0f));
        }
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "travelercloak"));
        return tooltips;
    }
}

