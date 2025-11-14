/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.ItemRegistry
 */
package aphorea.registry;

import necesse.engine.registries.ItemRegistry;

public class AphGlobalIngredients {
    public static void registerCore() {
        ItemRegistry.getItem((String)"magicfoci").addGlobalIngredient(new String[]{"anybasicfoci"});
        ItemRegistry.getItem((String)"meleefoci").addGlobalIngredient(new String[]{"anybasicfoci"});
        ItemRegistry.getItem((String)"rangefoci").addGlobalIngredient(new String[]{"anybasicfoci"});
        ItemRegistry.getItem((String)"summonfoci").addGlobalIngredient(new String[]{"anybasicfoci"});
    }
}

