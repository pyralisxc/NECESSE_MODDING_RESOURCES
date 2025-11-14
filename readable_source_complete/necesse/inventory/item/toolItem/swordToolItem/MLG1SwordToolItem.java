/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;

public class MLG1SwordToolItem
extends SwordToolItem {
    public MLG1SwordToolItem() {
        super(2000, null);
        this.rarity = Item.Rarity.LEGENDARY;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(1000.0f);
        this.attackRange.setBaseValue(200);
        this.knockback.setBaseValue(75);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("MLG 1");
    }
}

