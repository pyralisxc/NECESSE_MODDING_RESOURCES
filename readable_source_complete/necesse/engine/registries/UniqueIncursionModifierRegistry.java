/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.registries.GameRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.incursionModifiers.AlchemicalInterferenceIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.CrawlmageddonIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.ExplosiveIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.FlamelingsIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.FrenzyIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.StormingIncursionModifier;
import necesse.entity.levelEvent.incursionModifiers.TremorsIncursionModifier;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class UniqueIncursionModifierRegistry
extends GameRegistry<UniqueIncursionModifier> {
    public static final UniqueIncursionModifierRegistry instance = new UniqueIncursionModifierRegistry();

    private UniqueIncursionModifierRegistry() {
        super("UniqueIncursionModifier", 32766);
    }

    @Override
    public void registerCore() {
        UniqueIncursionModifierRegistry.registerUniqueModifier("alchemicalinterference", new AlchemicalInterferenceIncursionModifier());
        UniqueIncursionModifierRegistry.registerUniqueModifier("crawlmageddon", new CrawlmageddonIncursionModifier());
        UniqueIncursionModifierRegistry.registerUniqueModifier("frenzy", new FrenzyIncursionModifier());
        UniqueIncursionModifierRegistry.registerUniqueModifier("tremors", new TremorsIncursionModifier());
        UniqueIncursionModifierRegistry.registerUniqueModifier("storming", new StormingIncursionModifier());
        UniqueIncursionModifierRegistry.registerUniqueModifier("explosive", new ExplosiveIncursionModifier());
        UniqueIncursionModifierRegistry.registerUniqueModifier("flamelings", new FlamelingsIncursionModifier());
    }

    @Override
    protected void onRegister(UniqueIncursionModifier object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerUniqueModifier(String stringID, UniqueIncursionModifier modifier) {
        return instance.register(stringID, modifier);
    }

    public static Iterable<UniqueIncursionModifier> getIncursionModifiers() {
        return instance.getElements();
    }

    public static UniqueIncursionModifier getRandomIncursionModifier(GameRandom seededRandom, ArrayList<UniqueIncursionModifier> usedModifiers) {
        Iterable<UniqueIncursionModifier> incursionModifiers = UniqueIncursionModifierRegistry.getIncursionModifiers();
        ArrayList<UniqueIncursionModifier> unusedModifiers = new ArrayList<UniqueIncursionModifier>();
        for (UniqueIncursionModifier incursionModifier : incursionModifiers) {
            if (usedModifiers.contains(incursionModifier)) continue;
            unusedModifiers.add(incursionModifier);
        }
        if (unusedModifiers.isEmpty()) {
            GameLog.warn.println("No unused modifier found. Adding a random modifier instead.");
            return (UniqueIncursionModifier)((Object)seededRandom.getOneOf(incursionModifiers));
        }
        return (UniqueIncursionModifier)seededRandom.getOneOf(unusedModifiers);
    }
}

