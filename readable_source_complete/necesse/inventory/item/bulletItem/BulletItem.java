/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.bulletItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class BulletItem
extends Item {
    public int damage;
    public int armorPen;
    public float critChance;

    public BulletItem(int stackAmount) {
        super(stackAmount);
        this.setItemCategory("equipment", "ammo");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "ammo");
        this.keyWords.add("bullet");
    }

    public BulletItem() {
        this(5000);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
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
        return velocity;
    }

    public int modRange(int range) {
        return range;
    }

    public int modKnockback(int knockback) {
        return knockback;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return null;
    }

    public boolean overrideProjectile() {
        return false;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "bullet");
    }
}

