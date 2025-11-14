/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ItemRegistry;

public class GlobalIngredient {
    public final IDData idData = new IDData();
    public final GameMessage displayName;
    public final GameMessage craftingMatTip;
    private ArrayList<Integer> registeredItemIDs;
    private ArrayList<Integer> obtainableRegisteredItemIDs;

    public final int getID() {
        return this.idData.getID();
    }

    public String getStringID() {
        return this.idData.getStringID();
    }

    public GlobalIngredient(GameMessage displayName, GameMessage craftingMatTip) {
        this.displayName = displayName;
        this.craftingMatTip = craftingMatTip;
        this.registeredItemIDs = new ArrayList();
        this.obtainableRegisteredItemIDs = new ArrayList();
    }

    public void onGlobalIngredientRegistryClosed() {
    }

    public void registerItemID(int itemID) {
        if (this.registeredItemIDs.contains(itemID)) {
            return;
        }
        this.registeredItemIDs.add(itemID);
        if (ItemRegistry.isObtainable(itemID)) {
            this.obtainableRegisteredItemIDs.add(itemID);
        }
    }

    public ArrayList<Integer> getRegisteredItemIDs() {
        return this.registeredItemIDs;
    }

    public ArrayList<Integer> getObtainableRegisteredItemIDs() {
        return this.obtainableRegisteredItemIDs;
    }
}

