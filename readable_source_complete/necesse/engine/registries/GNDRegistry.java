/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import necesse.engine.GameLog;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDAltarDataItem;
import necesse.engine.network.gameNetworkData.GNDIncursionDataItem;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemArray;
import necesse.engine.network.gameNetworkData.GNDItemArrayList;
import necesse.engine.network.gameNetworkData.GNDItemBoolean;
import necesse.engine.network.gameNetworkData.GNDItemByte;
import necesse.engine.network.gameNetworkData.GNDItemDouble;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.network.gameNetworkData.GNDItemFloat;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemGameItem;
import necesse.engine.network.gameNetworkData.GNDItemGameMessage;
import necesse.engine.network.gameNetworkData.GNDItemInt;
import necesse.engine.network.gameNetworkData.GNDItemIntArrayList;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.network.gameNetworkData.GNDItemLong;
import necesse.engine.network.gameNetworkData.GNDItemLongArrayList;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.gameNetworkData.GNDItemNull;
import necesse.engine.network.gameNetworkData.GNDItemShort;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.engine.network.gameNetworkData.GNDMusicPlayerInventory;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDRegistry
extends GameRegistry<RegistryItem> {
    public static final GNDRegistry instance = new GNDRegistry();

    public GNDRegistry() {
        super("GNDItem", Short.MAX_VALUE);
    }

    @Override
    public void registerCore() {
        GNDRegistry.registerGNDItem("null", GNDItemNull.class);
        GNDRegistry.registerGNDItem("bool", GNDItemBoolean.class);
        GNDRegistry.registerGNDItem("byte", GNDItemByte.class);
        GNDRegistry.registerGNDItem("short", GNDItemShort.class);
        GNDRegistry.registerGNDItem("int", GNDItemInt.class);
        GNDRegistry.registerGNDItem("long", GNDItemLong.class);
        GNDRegistry.registerGNDItem("float", GNDItemFloat.class);
        GNDRegistry.registerGNDItem("double", GNDItemDouble.class);
        GNDRegistry.registerGNDItem("string", GNDItemString.class);
        GNDRegistry.registerGNDItem("map", GNDItemMap.class);
        GNDRegistry.registerGNDItem("array", GNDItemArray.class);
        GNDRegistry.registerGNDItem("itemenchant", GNDItemEnchantment.class);
        GNDRegistry.registerGNDItem("inventory", GNDItemInventory.class);
        GNDRegistry.registerGNDItem("gameitem", GNDItemGameItem.class);
        GNDRegistry.registerGNDItem("gamedamage", GNDItemGameDamage.class);
        GNDRegistry.registerGNDItem("inventoryitem", GNDItemInventoryItem.class);
        GNDRegistry.registerGNDItem("incursiondata", GNDIncursionDataItem.class);
        GNDRegistry.registerGNDItem("altardata", GNDAltarDataItem.class);
        GNDRegistry.registerGNDItem("musicplayer", GNDMusicPlayerInventory.class);
        GNDRegistry.registerGNDItem("arraylist", GNDItemArrayList.class);
        GNDRegistry.registerGNDItem("intarraylist", GNDItemIntArrayList.class);
        GNDRegistry.registerGNDItem("longarraylist", GNDItemLongArrayList.class);
        GNDRegistry.registerGNDItem("gamemessage", GNDItemGameMessage.class);
    }

    @Override
    protected void onRegister(RegistryItem object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerGNDItem(String stringID, Class<? extends GNDItem> gndClass) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register GND items");
        }
        try {
            return instance.register(stringID, new RegistryItem(gndClass));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register " + GNDRegistry.instance.objectCallName + " " + gndClass.getSimpleName() + ":");
            System.err.println("\tMust have a constructor with PacketReader parameter");
            System.err.println("\tMust have a constructor with LoadData parameter");
            return -1;
        }
    }

    public static GNDItem loadGNDItem(LoadData save) {
        int id = -1;
        String stringID = null;
        if (!save.isArray()) {
            return null;
        }
        if (save.hasLoadDataByName("gndType")) {
            stringID = save.getUnsafeString("gndType");
        } else if (save.hasLoadDataByName("index")) {
            id = save.getInt("index");
        }
        try {
            if (stringID != null && !stringID.isEmpty()) {
                return ((RegistryItem)instance.getElementRaw(instance.getElementID(stringID))).newInstance(save);
            }
            RegistryItem elementRaw = (RegistryItem)instance.getElementRaw(id);
            LoadData itemData = save.getFirstLoadDataByName("item");
            if (itemData != null) {
                return elementRaw.newInstance(itemData);
            }
            GameLog.warn.println("Failed GND item load: Could not find item data, {" + save.getScript() + "}");
            return null;
        }
        catch (NoSuchElementException no) {
            if (stringID != null && !stringID.isEmpty()) {
                GameLog.warn.println("Failed GND item load: Invalid gndType " + stringID + ", {" + save.getScript() + "}");
            } else {
                GameLog.warn.println("Failed GND item load: Invalid index " + id + ", {" + save.getScript() + "}");
            }
            return null;
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            GameLog.warn.println("Failed GND item load: Error on instance creation");
            e.printStackTrace(GameLog.warn);
            return null;
        }
    }

    public static void writeGNDItem(SaveData data, GNDItem item) {
        data.addUnsafeString("gndType", item.getStringID());
        item.addSaveData(data);
    }

    public static GNDItem readGNDItem(PacketReader reader) {
        int id = reader.getNextShortUnsigned();
        try {
            return ((RegistryItem)instance.getElementRaw(id)).newInstance(reader);
        }
        catch (NoSuchElementException no) {
            GameLog.warn.println("Could not find GND item with id " + id);
            return null;
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            GameLog.warn.println("Failed GND item packet: Error on instance creation");
            e.printStackTrace(GameLog.warn);
            return null;
        }
    }

    public static void writeGNDItem(PacketWriter writer, GNDItem item) {
        writer.putNextShortUnsigned(item.getID());
        item.writePacket(writer);
    }

    public static <C extends GNDItem> void applyIDData(C item, Class<? extends GNDItem> gndClass) {
        try {
            RegistryItem found = instance.streamElements().filter(d -> d.itemClass == gndClass).findFirst().orElseThrow(NoSuchElementException::new);
            item.idData.setData(found.data.getID(), found.data.getStringID());
        }
        catch (NoSuchElementException e) {
            throw new IllegalStateException("Cannot construct unregistered " + GNDRegistry.instance.objectCallName + " class " + item.getClass().getSimpleName());
        }
    }

    public static <C extends GNDItem> void applyIDData(C item) {
        GNDRegistry.applyIDData(item, item.getClass());
    }

    protected static class RegistryItem
    implements IDDataContainer {
        private final IDData data = new IDData();
        public final Class<? extends GNDItem> itemClass;
        private final Constructor<? extends GNDItem> packetConstructor;
        private final Constructor<? extends GNDItem> loadConstructor;

        public RegistryItem(Class<? extends GNDItem> itemClass) throws NoSuchMethodException {
            this.itemClass = itemClass;
            this.packetConstructor = itemClass.getConstructor(PacketReader.class);
            this.loadConstructor = itemClass.getConstructor(LoadData.class);
        }

        @Override
        public IDData getIDData() {
            return this.data;
        }

        public GNDItem newInstance(PacketReader reader) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return this.packetConstructor.newInstance(reader);
        }

        public GNDItem newInstance(LoadData save) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return this.loadConstructor.newInstance(save);
        }
    }
}

