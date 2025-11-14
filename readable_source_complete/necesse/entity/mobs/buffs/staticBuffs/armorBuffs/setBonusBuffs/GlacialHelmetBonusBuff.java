/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.IcicleStaffProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.ThrowToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

public class GlacialHelmetBonusBuff
extends SetBonusBuff {
    public IntUpgradeValue maxResilience = new IntUpgradeValue().setBaseValue(20).setUpgradedValue(1.0f, 30);
    public FloatUpgradeValue resilienceGain = new FloatUpgradeValue().setBaseValue(0.2f).setUpgradedValue(1.0f, 0.2f);
    public FloatUpgradeValue icicleDamage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(25.0f).setUpgradedValue(1.0f, 30.0f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(buff.getUpgradeTier()));
        buff.setModifier(BuffModifiers.RESILIENCE_GAIN, this.resilienceGain.getValue(buff.getUpgradeTier()));
    }

    @Override
    public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, GNDItemMap attackMap) {
        ToolItem toolItem;
        super.onItemAttacked(buff, targetX, targetY, attackerMob, attackHeight, item, slot, animAttack, attackMap);
        Level level = buff.owner.getLevel();
        if (level.isServer() && item.item instanceof ToolItem && !(item.item instanceof ThrowToolItem) && (toolItem = (ToolItem)item.item).getDamageType(item) == DamageTypeRegistry.MELEE) {
            float totalModifier;
            int cooldown;
            String shotTimeKey = "icicleshottime";
            long shotTime = buff.getGndData().getLong(shotTimeKey);
            if (shotTime + (long)(cooldown = Math.round(750.0f * (1.0f / (totalModifier = DamageTypeRegistry.MELEE.calculateTotalAttackSpeedModifier(attackerMob))))) < level.getWorldEntity().getTime()) {
                buff.getGndData().setLong(shotTimeKey, level.getWorldEntity().getTime());
                GameRandom random = GameRandom.globalRandom;
                Point2D.Float dir = GameMath.normalize(attackerMob.x - (float)targetX, attackerMob.y - (float)targetY);
                int offsetDistance = random.getIntBetween(30, 50);
                Point2D.Float offset = new Point2D.Float(dir.x * (float)offsetDistance, dir.y * (float)offsetDistance);
                offset = GameMath.getPerpendicularPoint(offset, (float)random.getIntBetween(-50, 50), dir);
                float velocity = 125.0f * attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue();
                GameDamage damage = new GameDamage(this.icicleDamage.getValue(buff.getUpgradeTier()).floatValue());
                IcicleStaffProjectile projectile = new IcicleStaffProjectile(level, attackerMob, attackerMob.x + offset.x, attackerMob.y + offset.y, targetX, targetY, velocity, 500, damage, 0);
                level.entityManager.projectiles.add(projectile);
            }
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "glacialhelmetset"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

