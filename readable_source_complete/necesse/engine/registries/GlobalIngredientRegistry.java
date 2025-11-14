/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.NoSuchElementException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.util.GameUtils;
import necesse.inventory.recipe.GlobalIngredient;

public class GlobalIngredientRegistry
extends GameRegistry<GlobalIngredientRegistryElement> {
    public static final GlobalIngredientRegistry instance = new GlobalIngredientRegistry();

    private GlobalIngredientRegistry() {
        super("GlobalIngredient", Short.MAX_VALUE);
    }

    @Override
    public void registerCore() {
        GlobalIngredientRegistry.registerGlobalIngredient("anycoolingfuel", new LocalMessage("item", "anycoolingfuel"), new LocalMessage("itemtooltip", "coolingfueltip"));
        for (int i = 0; i < 10; ++i) {
            GlobalIngredientRegistry.registerGlobalIngredient("anytier" + i + "essence", new LocalMessage("item", "anytieressence", "tier", i), null);
        }
    }

    @Override
    protected void onRegister(GlobalIngredientRegistryElement object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        for (GlobalIngredientRegistryElement element : this.getElements()) {
            element.ingredient.onGlobalIngredientRegistryClosed();
        }
    }

    public static int registerGlobalIngredient(String stringID, GameMessage displayName, GameMessage itemTooltip) {
        GlobalIngredient newIngredient = new GlobalIngredient(displayName, itemTooltip);
        return instance.register(stringID, new GlobalIngredientRegistryElement(newIngredient));
    }

    public static GlobalIngredient getGlobalIngredient(String stringID) {
        try {
            int id = instance.getElementIDRaw(stringID);
            return ((GlobalIngredientRegistryElement)GlobalIngredientRegistry.instance.getElement((int)id)).ingredient;
        }
        catch (NoSuchElementException e) {
            int id = GlobalIngredientRegistry.registerGlobalIngredient(stringID, new LocalMessage("item", stringID), null);
            return ((GlobalIngredientRegistryElement)GlobalIngredientRegistry.instance.getElement((int)id)).ingredient;
        }
    }

    public static int getGlobalIngredientID(String stringID) {
        return GlobalIngredientRegistry.getGlobalIngredient(stringID).getID();
    }

    public static GlobalIngredient getGlobalIngredient(int id) {
        return ((GlobalIngredientRegistryElement)GlobalIngredientRegistry.instance.getElement((int)id)).ingredient;
    }

    public static Iterable<GlobalIngredient> getGlobalIngredients() {
        return GameUtils.mapIterable(instance.getElements().iterator(), e -> e.ingredient);
    }

    protected static class GlobalIngredientRegistryElement
    implements IDDataContainer {
        public final GlobalIngredient ingredient;

        public GlobalIngredientRegistryElement(GlobalIngredient ingredient) {
            this.ingredient = ingredient;
        }

        @Override
        public IDData getIDData() {
            return this.ingredient.idData;
        }
    }
}

