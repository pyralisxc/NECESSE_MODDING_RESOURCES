/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.matItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.inventory.item.Item;
import necesse.inventory.item.ObtainTip;
import necesse.inventory.item.matItem.MatItem;

public class BookMatItem
extends MatItem
implements ObtainTip {
    public BookMatItem() {
        super(500, Item.Rarity.COMMON, new String[0]);
    }

    @Override
    public GameMessage getObtainTip() {
        return new LocalMessage("itemtooltip", "bookobtain");
    }
}

