/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.RecipeTechRegistry
 *  necesse.inventory.recipe.Tech
 */
package aphorea.registry;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Tech;

public class AphTech {
    public static Tech RUNES;

    public static void registerCore() {
        RUNES = RecipeTechRegistry.registerTech((String)"runes", (String)"runestable");
    }
}

