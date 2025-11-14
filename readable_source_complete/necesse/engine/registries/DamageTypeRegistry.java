/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.stream.Collectors;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.entity.mobs.gameDamageType.MagicDamageType;
import necesse.entity.mobs.gameDamageType.MeleeDamageType;
import necesse.entity.mobs.gameDamageType.NormalDamageType;
import necesse.entity.mobs.gameDamageType.RangedDamageType;
import necesse.entity.mobs.gameDamageType.SummonDamageType;
import necesse.entity.mobs.gameDamageType.TrueDamageType;

public class DamageTypeRegistry
extends GameRegistry<DamageTypeRegistryElement> {
    public static final DamageTypeRegistry instance = new DamageTypeRegistry();
    public static DamageType NORMAL;
    public static DamageType TRUE;
    public static DamageType MELEE;
    public static DamageType RANGED;
    public static DamageType MAGIC;
    public static DamageType SUMMON;

    private DamageTypeRegistry() {
        super("GameDamageType", 122);
    }

    @Override
    public void registerCore() {
        NORMAL = new NormalDamageType();
        DamageTypeRegistry.registerDamageType("normal", NORMAL);
        TRUE = new TrueDamageType();
        DamageTypeRegistry.registerDamageType("true", TRUE);
        MELEE = new MeleeDamageType();
        DamageTypeRegistry.registerDamageType("melee", MELEE);
        RANGED = new RangedDamageType();
        DamageTypeRegistry.registerDamageType("ranged", RANGED);
        MAGIC = new MagicDamageType();
        DamageTypeRegistry.registerDamageType("magic", MAGIC);
        SUMMON = new SummonDamageType();
        DamageTypeRegistry.registerDamageType("summon", SUMMON);
    }

    @Override
    protected void onRegister(DamageTypeRegistryElement element, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        for (DamageTypeRegistryElement element : this.getElements()) {
            element.type.onDamageTypeRegistryClosed();
        }
    }

    public static int registerDamageType(String stringID, DamageType type) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register damage types");
        }
        return instance.register(stringID, new DamageTypeRegistryElement(type));
    }

    public static DamageType getDamageType(int id) {
        if (id == -1) {
            return null;
        }
        DamageTypeRegistryElement element = (DamageTypeRegistryElement)instance.getElement(id);
        return element == null ? null : element.type;
    }

    public static int getDamageTypeID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static DamageType getDamageType(String stringID) {
        return DamageTypeRegistry.getDamageType(DamageTypeRegistry.getDamageTypeID(stringID));
    }

    public static String getDamageTypeStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static Iterable<DamageType> getDamageTypes() {
        return instance.streamElements().map(e -> e.type).collect(Collectors.toList());
    }

    protected static class DamageTypeRegistryElement
    implements IDDataContainer {
        public final DamageType type;

        public DamageTypeRegistryElement(DamageType type) {
            this.type = type;
        }

        @Override
        public IDData getIDData() {
            return this.type.idData;
        }
    }
}

