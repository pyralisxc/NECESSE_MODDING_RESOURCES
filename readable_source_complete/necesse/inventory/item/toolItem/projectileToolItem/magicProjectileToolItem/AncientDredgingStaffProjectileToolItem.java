/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AncientDredgingStaffEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class AncientDredgingStaffProjectileToolItem
extends MagicProjectileToolItem {
    public AncientDredgingStaffProjectileToolItem() {
        super(1750, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(85.0f).setUpgradedValue(1.0f, 120.40003f);
        this.velocity.setBaseValue(150);
        this.attackXOffset = 20;
        this.attackYOffset = 20;
        this.attackRange.setBaseValue(300);
        this.knockback.setBaseValue(50);
        this.manaCost.setBaseValue(7.5f).setUpgradedValue(1.0f, 7.5f);
        this.resilienceGain.setBaseValue(1.0f);
        this.itemAttackerProjectileCanHitWidth = 10.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ancientdredgingstafftip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int range = this.getAttackRange(item);
        Point2D.Float dir = new Point2D.Float(x - attackerMob.getX(), y - attackerMob.getY());
        AncientDredgingStaffEvent event = new AncientDredgingStaffEvent((Mob)attackerMob, attackerMob.getX(), attackerMob.getY(), new GameRandom(seed), GameMath.getAngle(dir), this.getAttackDamage(item), this.getResilienceGain(item), (float)this.getProjectileVelocity(item, attackerMob), (float)this.getKnockback(item, attackerMob), (float)range);
        attackerMob.addAndSendAttackerLevelEvent(event);
        this.consumeMana(attackerMob, item);
        return item;
    }
}

