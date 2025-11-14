/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.arrowItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class ArrowItem
extends Item {
    public int damage;
    public int armorPen;
    public float critChance;
    public float speedMod;

    public ArrowItem(int stackSize) {
        super(stackSize);
        this.setItemCategory("equipment", "ammo");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "ammo");
        this.keyWords.add("arrow");
        this.speedMod = 1.0f;
    }

    public ArrowItem() {
        this(5000);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "arrowtip"));
        tooltips.add(Localization.translate("itemtooltip", "damagetip", "value", (Object)this.damage));
        return tooltips;
    }

    public float getAmmoConsumeChance() {
        return 1.0f;
    }

    public GameDamage modDamage(GameDamage damage) {
        return damage.add(this.damage, this.armorPen, this.critChance);
    }

    public float modVelocity(float velocity) {
        return velocity * this.speedMod;
    }

    public int modRange(int range) {
        return range;
    }

    public int modKnockback(int knockback) {
        return knockback;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, ItemAttackerMob owner) {
        return null;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "arrow");
    }
}

