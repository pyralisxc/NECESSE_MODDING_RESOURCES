/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.exotic;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.inventory.item.Item;

public class WalkingStickHoldItem
extends Item {
    public WalkingStickHoldItem() {
        super(1);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage(this.getStringID());
    }
}

