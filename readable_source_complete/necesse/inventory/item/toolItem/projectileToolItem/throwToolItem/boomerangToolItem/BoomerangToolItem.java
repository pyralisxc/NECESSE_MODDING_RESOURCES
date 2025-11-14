/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.ThrowToolItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class BoomerangToolItem
extends ThrowToolItem {
    protected String projectileID;

    public BoomerangToolItem(int enchantCost, OneOfLootItems lootTableCategory, String projectileID) {
        super(enchantCost, lootTableCategory);
        this.projectileID = projectileID;
        this.setItemCategory("equipment", "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "meleeweapons");
        this.keyWords.add("boomerang");
        this.damageType = DamageTypeRegistry.MELEE;
        this.knockback.setBaseValue(25);
        this.stackSize = 1;
        this.attackRange.setBaseValue(200);
    }

    @Override
    public LootItemInterface getDefaultToolLootItem() {
        return new LootItem(this.getStringID(), this.getStackSize());
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        int stackSize = this.getStackSize();
        if (stackSize > 1) {
            tooltips.add(Localization.translate("itemtooltip", "boomerangstack", "value", (Object)stackSize));
        }
        return tooltips;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob.getBoomerangsUsage() < Math.min(item.getAmount(), item.itemStackSize()) ? null : "";
    }

    @Override
    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        return null;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = ProjectileRegistry.getProjectile(this.projectileID, level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        attackerMob.boomerangs.add(projectile);
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile);
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

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        if (item.getAmount() < this.getStackSize()) {
            return Localization.translate("ui", "upgradefullstack");
        }
        return super.getCanBeUpgradedError(item);
    }

    @Override
    protected float getSavageRewardPerTier(InventoryItem item) {
        return super.getSavageRewardPerTier(item) / (float)this.getStackSize();
    }
}

