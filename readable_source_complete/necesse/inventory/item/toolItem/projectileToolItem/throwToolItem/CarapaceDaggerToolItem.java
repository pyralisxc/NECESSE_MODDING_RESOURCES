/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.CarapaceDaggerProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.ThrowToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;
import necesse.level.maps.Level;

public class CarapaceDaggerToolItem
extends ThrowToolItem {
    public CarapaceDaggerToolItem() {
        super(1750, ThrowWeaponsLootTable.throwWeapons);
        this.attackAnimTime.setBaseValue(250);
        this.damageType = DamageTypeRegistry.MELEE;
        this.attackDamage.setBaseValue(57.0f).setUpgradedValue(1.0f, 67.66669f);
        this.velocity.setBaseValue(200);
        this.rarity = Item.Rarity.UNCOMMON;
        this.stackSize = 1;
        this.attackRange.setBaseValue(800);
        this.resilienceGain.setBaseValue(0.3f);
        this.itemAttackerProjectileCanHitWidth = 8.0f;
        this.itemAttackerPredictionDistanceOffset = -30.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
        this.setItemCategory("equipment", "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "meleeweapons");
    }

    @Override
    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        return null;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "carapacedaggertip1"));
        tooltips.add(Localization.translate("itemtooltip", "carapacedaggertip2"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int velocity = this.getThrowingVelocity(item, attackerMob);
        CarapaceDaggerProjectile projectile = new CarapaceDaggerProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, velocity, this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob));
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        GameRandom random = new GameRandom(seed);
        projectile.resetUniqueID(random);
        attackerMob.addAndSendAttackerProjectile(projectile, 30, random.getIntBetween(-15, 15));
        return item;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return item.getAmount() >= this.getStackSize();
    }

    @Override
    public String getIsEnchantableError(InventoryItem item) {
        if (item.getAmount() < this.getStackSize()) {
            return Localization.translate("itemtooltip", "enchantfullstack");
        }
        return super.getIsEnchantableError(item);
    }
}

