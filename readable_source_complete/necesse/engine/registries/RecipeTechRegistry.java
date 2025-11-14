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
import necesse.inventory.recipe.Tech;

public class RecipeTechRegistry
extends GameRegistry<TechRegistryElement> {
    @Deprecated
    public static Tech DEMONIC;
    @Deprecated
    public static Tech ADVANCED_WORKSTATION;
    public static Tech ALL;
    public static Tech NONE;
    public static Tech WORKSTATION;
    public static Tech COOKING_POT;
    public static Tech ROASTING_STATION;
    public static Tech FORGE;
    public static Tech CARPENTER;
    public static Tech LANDSCAPING;
    public static Tech IRON_ANVIL;
    public static Tech ALCHEMY;
    public static Tech DEMONIC_WORKSTATION;
    public static Tech COOKING_STATION;
    public static Tech DEMONIC_ANVIL;
    public static Tech VOID_ALCHEMY;
    public static Tech TUNGSTEN_WORKSTATION;
    public static Tech TUNGSTEN_ANVIL;
    public static Tech CAVEGLOW_ALCHEMY;
    public static Tech TUNGSTEN_CARPENTER;
    public static Tech TUNGSTEN_LANDSCAPING;
    public static Tech FALLEN_WORKSTATION;
    public static Tech FALLEN_ANVIL;
    public static Tech FALLEN_ALCHEMY;
    public static Tech FALLEN_CARPENTER;
    public static Tech FALLEN_LANDSCAPING;
    public static Tech COMPOST_BIN;
    public static Tech GRAIN_MILL;
    public static Tech CHEESE_PRESS;
    public static final RecipeTechRegistry instance;

    private RecipeTechRegistry() {
        super("Recipe Tech", 65535);
    }

    @Override
    public void registerCore() {
        ALL = RecipeTechRegistry.registerTech("all", "all");
        NONE = RecipeTechRegistry.registerTech("none", "inventory", new LocalMessage("tech", "inventory"));
        WORKSTATION = RecipeTechRegistry.registerTech("workstation", "workstation");
        COOKING_POT = RecipeTechRegistry.registerTech("cookingpot", "cookingpot");
        ROASTING_STATION = RecipeTechRegistry.registerTech("roastingstation", "roastingstation");
        FORGE = RecipeTechRegistry.registerTech("forge", "forge");
        CARPENTER = RecipeTechRegistry.registerTech("carpenter", "carpentersbench");
        LANDSCAPING = RecipeTechRegistry.registerTech("landscaping", "landscapingstation");
        IRON_ANVIL = RecipeTechRegistry.registerTech("ironanvil", "ironanvil");
        ALCHEMY = RecipeTechRegistry.registerTech("alchemy", "alchemytable");
        DEMONIC_WORKSTATION = DEMONIC = RecipeTechRegistry.registerTech("demonic", "demonicworkstation");
        COOKING_STATION = RecipeTechRegistry.registerTech("cookingstation", "cookingstation");
        DEMONIC_ANVIL = RecipeTechRegistry.registerTech("demonicanvil", "demonicanvil");
        VOID_ALCHEMY = RecipeTechRegistry.registerTech("voidalchemy", "voidalchemytable");
        TUNGSTEN_WORKSTATION = ADVANCED_WORKSTATION = RecipeTechRegistry.registerTech("tungstenworkstation", "tungstenworkstation");
        TUNGSTEN_ANVIL = RecipeTechRegistry.registerTech("tungstenanvil", "tungstenanvil");
        TUNGSTEN_CARPENTER = RecipeTechRegistry.registerTech("tungstencarpenter", "tungstencarpentersbench");
        TUNGSTEN_LANDSCAPING = RecipeTechRegistry.registerTech("tungstenlandscaping", "tungstenlandscapingstation");
        CAVEGLOW_ALCHEMY = RecipeTechRegistry.registerTech("caveglowalchemy", "caveglowalchemytable");
        FALLEN_WORKSTATION = RecipeTechRegistry.registerTech("fallen", "fallenworkstation");
        FALLEN_ANVIL = RecipeTechRegistry.registerTech("fallenanvil", "fallenanvil");
        FALLEN_ALCHEMY = RecipeTechRegistry.registerTech("fallenalchemy", "fallenalchemytable");
        FALLEN_CARPENTER = RecipeTechRegistry.registerTech("fallencarpenter", "fallencarpentersbench");
        FALLEN_LANDSCAPING = RecipeTechRegistry.registerTech("fallenlandscaping", "fallenlandscapingstation");
        COMPOST_BIN = RecipeTechRegistry.registerTech("compostbin", "compostbin", new LocalMessage("object", "compostbin"), null);
        GRAIN_MILL = RecipeTechRegistry.registerTech("grainmill", "grainmill", new LocalMessage("object", "grainmill"), new LocalMessage("itemtooltip", "milltip"));
        CHEESE_PRESS = RecipeTechRegistry.registerTech("cheesepress", "cheesepress", new LocalMessage("object", "cheesepress"), null);
    }

    @Override
    protected void onRegister(TechRegistryElement object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static Tech registerTech(String stringID, String itemStringID, GameMessage displayName, GameMessage craftingMatTip) {
        Tech tech = new Tech(itemStringID, displayName, craftingMatTip);
        instance.register(stringID, new TechRegistryElement(tech));
        return tech;
    }

    public static Tech registerTech(String stringID, String itemStringID, GameMessage displayName) {
        return RecipeTechRegistry.registerTech(stringID, itemStringID, displayName, new LocalMessage("itemtooltip", "craftingmat"));
    }

    public static Tech registerTech(String stringID, String itemStringID) {
        return RecipeTechRegistry.registerTech(stringID, itemStringID, new LocalMessage("tech", stringID));
    }

    public static Tech getTech(int ID) {
        return ((TechRegistryElement)RecipeTechRegistry.instance.getElement((int)ID)).tech;
    }

    public static Tech getTech(String stringID) throws NoSuchElementException {
        int id = instance.getElementIDRaw(stringID);
        return RecipeTechRegistry.getTech(id);
    }

    static {
        instance = new RecipeTechRegistry();
    }

    protected static class TechRegistryElement
    implements IDDataContainer {
        public final Tech tech;

        public TechRegistryElement(Tech tech) {
            this.tech = tech;
        }

        @Override
        public IDData getIDData() {
            return this.tech.data;
        }
    }
}

