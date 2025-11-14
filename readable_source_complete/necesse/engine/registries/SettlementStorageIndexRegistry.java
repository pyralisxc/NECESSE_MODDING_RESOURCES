/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageEquipmentTypeIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageFoodQualityIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;

public class SettlementStorageIndexRegistry
extends ClassedGameRegistry<SettlementStorageIndex, IndexRegistryElement> {
    public static int ITEM_IDS;
    public static int GLOBAL_INGREDIENT_IDS;
    public static int FOOD_QUALITY;
    public static int ARMOR_TYPE;
    public static final SettlementStorageIndexRegistry instance;

    public SettlementStorageIndexRegistry() {
        super("SettlementStorageIndex", Short.MAX_VALUE);
    }

    @Override
    public void registerCore() {
        ITEM_IDS = SettlementStorageIndexRegistry.registerIndex("itemids", SettlementStorageItemIDIndex.class);
        GLOBAL_INGREDIENT_IDS = SettlementStorageIndexRegistry.registerIndex("globalingredientids", SettlementStorageGlobalIngredientIDIndex.class);
        FOOD_QUALITY = SettlementStorageIndexRegistry.registerIndex("foodquality", SettlementStorageFoodQualityIndex.class);
        ARMOR_TYPE = SettlementStorageIndexRegistry.registerIndex("armortype", SettlementStorageEquipmentTypeIndex.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerIndex(String stringID, Class<? extends SettlementStorageIndex> layerClass) {
        try {
            return instance.register(stringID, new IndexRegistryElement(layerClass));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(layerClass.getSimpleName() + " does not have a constructor with level parameter");
        }
    }

    public static int replaceIndex(String stringID, Class<? extends SettlementStorageIndex> layerClass) {
        try {
            return instance.replace(stringID, new IndexRegistryElement(layerClass));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(layerClass.getSimpleName() + " does not have a constructor with level parameter");
        }
    }

    public static int getIndexID(Class<? extends SettlementStorageIndex> clazz) {
        return instance.getElementID(clazz);
    }

    public static int getIndexID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static SettlementStorageIndex[] getNewIndexesArray(Level level) {
        SettlementStorageIndex[] out = new SettlementStorageIndex[instance.size()];
        for (int i = 0; i < out.length; ++i) {
            IndexRegistryElement e = (IndexRegistryElement)instance.getElement(i);
            try {
                SettlementStorageIndex index = (SettlementStorageIndex)e.newInstance(level);
                index.idData.setData(e.getIDData().getID(), e.getIDData().getStringID());
                out[i] = index;
                continue;
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                throw new RuntimeException("Could not create new " + SettlementStorageIndexRegistry.instance.objectCallName + " object for " + e.indexClass.getSimpleName(), ex);
            }
        }
        return out;
    }

    static {
        instance = new SettlementStorageIndexRegistry();
    }

    protected static class IndexRegistryElement
    extends ClassIDDataContainer<SettlementStorageIndex> {
        private Class<? extends SettlementStorageIndex> indexClass;

        public IndexRegistryElement(Class<? extends SettlementStorageIndex> indexClass) throws NoSuchMethodException {
            super(indexClass, Level.class);
            this.indexClass = indexClass;
        }
    }
}

