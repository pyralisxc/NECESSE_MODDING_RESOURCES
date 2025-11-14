/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.IDData;

public class Tech {
    public final IDData data = new IDData();
    public final String itemStringID;
    public final GameMessage displayName;
    public final GameMessage craftingMatTip;

    public String getStringID() {
        return this.data.getStringID();
    }

    public int getID() {
        return this.data.getID();
    }

    public Tech(String itemStringID, GameMessage displayName, GameMessage craftingMatTip) {
        this.itemStringID = itemStringID;
        this.displayName = displayName;
        this.craftingMatTip = craftingMatTip;
    }
}

