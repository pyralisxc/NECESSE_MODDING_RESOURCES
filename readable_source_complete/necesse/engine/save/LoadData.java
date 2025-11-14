/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import necesse.engine.GameLog;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSerialize;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.WorldFile;

public class LoadData {
    private final SaveComponent save;

    public static LoadData newRaw(File file, boolean isCompressed) throws IOException, DataFormatException {
        return new LoadData(SaveComponent.loadScriptRaw(file, isCompressed));
    }

    private LoadData(File file, boolean isCompressed) {
        this(SaveComponent.loadScript(file, isCompressed));
    }

    public LoadData(File file) {
        this(SaveComponent.loadScript(file));
    }

    public LoadData(WorldFile file) {
        this(SaveComponent.loadScript(file));
    }

    public LoadData(String script) {
        this(SaveComponent.loadScript(script));
    }

    public LoadData(SaveComponent saveComponent) {
        Objects.requireNonNull(saveComponent);
        this.save = saveComponent;
    }

    public String getName() {
        return this.save.getName();
    }

    public String getData() {
        return this.save.getData();
    }

    public boolean isArray() {
        return this.save.getType() == 2;
    }

    public boolean isData() {
        return this.save.getType() == 1;
    }

    public List<LoadData> getLoadData() {
        return this.save.getComponents().stream().map(LoadData::new).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return this.save.isEmpty();
    }

    public List<LoadData> getLoadDataByName(String name) {
        return this.save.getComponentsByName(name).stream().map(LoadData::new).collect(Collectors.toList());
    }

    public LoadData getFirstLoadDataByName(String name) {
        SaveComponent comp = this.save.getFirstComponentByName(name);
        return comp != null ? new LoadData(comp) : null;
    }

    public String getFirstDataByName(String name) {
        return this.save.getFirstDataByName(name);
    }

    public boolean hasLoadDataByName(String name) {
        return this.save.hasComponentByName(name);
    }

    public String getScript() {
        return this.save.getScript();
    }

    public String getScript(boolean compressed) {
        return this.save.getScript(compressed);
    }

    public SaveData toSaveData() {
        return new SaveData(this.save);
    }

    public static boolean getBoolean(LoadData data) {
        return Boolean.parseBoolean(data.getData());
    }

    public boolean getBoolean(String dataName) {
        return LoadData.getBoolean(this.getFirstLoadDataByName(dataName));
    }

    public boolean getBoolean(String dataName, boolean defaultValue, boolean printWarning) {
        try {
            return this.getBoolean(dataName);
        }
        catch (NullPointerException e) {
            if (printWarning) {
                GameLog.warn.println("Could not load boolean " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public boolean getBoolean(String dataName, boolean defaultValue) {
        return this.getBoolean(dataName, defaultValue, true);
    }

    public static byte getByte(LoadData data) {
        return Byte.parseByte(data.getData());
    }

    public byte getByte(String dataName) {
        return LoadData.getByte(this.getFirstLoadDataByName(dataName));
    }

    public byte getByte(String dataName, byte defaultValue, boolean printWarning) {
        try {
            return this.getByte(dataName);
        }
        catch (NullPointerException | NumberFormatException e) {
            if (printWarning) {
                GameLog.warn.println("Could not load byte " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public byte getByte(String dataName, byte defaultValue) {
        return this.getByte(dataName, defaultValue, true);
    }

    public byte getByte(String dataName, byte defaultValue, byte minValue, byte maxValue, boolean printWarning) {
        return (byte)GameMath.limit(this.getByte(dataName, defaultValue, printWarning), minValue, maxValue);
    }

    public byte getByte(String dataName, byte defaultValue, byte minValue, byte maxValue) {
        return this.getByte(dataName, defaultValue, minValue, maxValue, true);
    }

    public static short getShort(LoadData data) {
        return Short.parseShort(data.getData());
    }

    public short getShort(String dataName) {
        return LoadData.getShort(this.getFirstLoadDataByName(dataName));
    }

    public short getShort(String dataName, short defaultValue, boolean printWarning) {
        try {
            return this.getShort(dataName);
        }
        catch (NullPointerException | NumberFormatException e) {
            if (printWarning) {
                GameLog.warn.println("Could not load short " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public short getShort(String dataName, short defaultValue) {
        return this.getShort(dataName, defaultValue, true);
    }

    public short getShort(String dataName, short defaultValue, short minValue, short maxValue, boolean printWarning) {
        return (short)GameMath.limit(this.getShort(dataName, defaultValue, printWarning), minValue, maxValue);
    }

    public short getShort(String dataName, short defaultValue, short minValue, short maxValue) {
        return this.getShort(dataName, defaultValue, minValue, maxValue, true);
    }

    public static int getInt(LoadData data) {
        return Integer.parseInt(data.getData());
    }

    public int getInt(String dataName) {
        return LoadData.getInt(this.getFirstLoadDataByName(dataName));
    }

    public int getInt(String dataName, int defaultValue, boolean printWarning) {
        try {
            return this.getInt(dataName);
        }
        catch (NullPointerException | NumberFormatException e) {
            if (printWarning) {
                GameLog.warn.println("Could not load int " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public int getInt(String dataName, int defaultValue) {
        return this.getInt(dataName, defaultValue, true);
    }

    public int getInt(String dataName, int defaultValue, int minValue, int maxValue, boolean printWarning) {
        return GameMath.limit(this.getInt(dataName, defaultValue, printWarning), minValue, maxValue);
    }

    public int getInt(String dataName, int defaultValue, int minValue, int maxValue) {
        return this.getInt(dataName, defaultValue, minValue, maxValue, true);
    }

    public static long getLong(LoadData data) {
        return Long.parseLong(data.getData());
    }

    public long getLong(String dataName) {
        return LoadData.getLong(this.getFirstLoadDataByName(dataName));
    }

    public long getLong(String dataName, long defaultValue, boolean printWarning) {
        try {
            return this.getLong(dataName);
        }
        catch (NullPointerException | NumberFormatException e) {
            if (printWarning) {
                GameLog.warn.println("Could not load long " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public long getLong(String dataName, long defaultValue) {
        return this.getLong(dataName, defaultValue, true);
    }

    public long getLong(String dataName, long defaultValue, long minValue, long maxValue, boolean printWarning) {
        return GameMath.limit(this.getLong(dataName, defaultValue, printWarning), minValue, maxValue);
    }

    public long getLong(String dataName, long defaultValue, long minValue, long maxValue) {
        return this.getLong(dataName, defaultValue, minValue, maxValue, true);
    }

    public static float getFloat(LoadData data) {
        return Float.parseFloat(data.getData());
    }

    public float getFloat(String dataName) {
        return LoadData.getFloat(this.getFirstLoadDataByName(dataName));
    }

    public float getFloat(String dataName, float defaultValue, boolean printWarning) {
        try {
            return this.getFloat(dataName);
        }
        catch (NullPointerException | NumberFormatException e) {
            if (printWarning) {
                GameLog.warn.println("Could not load float " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public float getFloat(String dataName, float defaultValue) {
        return this.getFloat(dataName, defaultValue, true);
    }

    public float getFloat(String dataName, float defaultValue, float minValue, float maxValue, boolean printWarning) {
        return GameMath.limit(this.getFloat(dataName, defaultValue, printWarning), minValue, maxValue);
    }

    public float getFloat(String dataName, float defaultValue, float minValue, float maxValue) {
        return this.getFloat(dataName, defaultValue, minValue, maxValue, true);
    }

    public static double getDouble(LoadData data) {
        return Double.parseDouble(data.getData());
    }

    public double getDouble(String dataName) {
        return LoadData.getDouble(this.getFirstLoadDataByName(dataName));
    }

    public double getDouble(String dataName, double defaultValue, boolean printWarning) {
        try {
            return this.getDouble(dataName);
        }
        catch (NullPointerException | NumberFormatException e) {
            if (printWarning) {
                GameLog.warn.println("Could not load double " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public double getDouble(String dataName, double defaultValue) {
        return this.getDouble(dataName, defaultValue, true);
    }

    public double getDouble(String dataName, double defaultValue, double minValue, double maxValue, boolean printWarning) {
        return GameMath.limit(this.getDouble(dataName, defaultValue, printWarning), minValue, maxValue);
    }

    public double getDouble(String dataName, double defaultValue, double minValue, double maxValue) {
        return this.getDouble(dataName, defaultValue, minValue, maxValue, true);
    }

    public static <T extends Enum<T>> T getEnum(Class<T> type, LoadData data) {
        return SaveSerialize.unserializeEnum(type, data.getData());
    }

    public <T extends Enum<T>> T getEnum(Class<T> type, String dataName) {
        return LoadData.getEnum(type, this.getFirstLoadDataByName(dataName));
    }

    public <T extends Enum<T>> T getEnum(Class<T> type, String dataName, T defaultValue, boolean printWarning) {
        try {
            return this.getEnum(type, dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load " + type.getSimpleName() + " enum " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public <T extends Enum<T>> T getEnum(Class<T> type, String dataName, T defaultValue) {
        return this.getEnum(type, dataName, defaultValue, true);
    }

    public static String getUnsafeString(LoadData data) {
        return data.getData();
    }

    public String getUnsafeString(String dataName) {
        return LoadData.getUnsafeString(this.getFirstLoadDataByName(dataName));
    }

    public String getUnsafeString(String dataName, String defaultValue, boolean printWarning) {
        try {
            return this.getUnsafeString(dataName);
        }
        catch (NullPointerException e) {
            if (printWarning) {
                GameLog.warn.println("Could not load string " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public String getUnsafeString(String dataName, String defaultValue) {
        return this.getUnsafeString(dataName, defaultValue, true);
    }

    public static String getSafeString(LoadData data) {
        return SaveComponent.fromSafeData(LoadData.getUnsafeString(data));
    }

    public String getSafeString(String dataName) {
        return LoadData.getSafeString(this.getFirstLoadDataByName(dataName));
    }

    public String getSafeString(String dataName, String defaultValue, boolean printWarning) {
        String string = this.getUnsafeString(dataName, defaultValue, printWarning);
        if (string == null) {
            return null;
        }
        return SaveComponent.fromSafeData(string);
    }

    public String getSafeString(String dataName, String defaultValue) {
        return this.getSafeString(dataName, defaultValue, true);
    }

    public static Point getPoint(LoadData data) {
        return SaveSerialize.unserializePoint(data.getData());
    }

    public Point getPoint(String dataName) {
        return LoadData.getPoint(this.getFirstLoadDataByName(dataName));
    }

    public Point getPoint(String dataName, Point defaultValue, boolean printWarning) {
        try {
            return this.getPoint(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load point " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Point getPoint(String dataName, Point defaultValue) {
        return this.getPoint(dataName, defaultValue, true);
    }

    public static Dimension getDimension(LoadData data) {
        return SaveSerialize.unserializeDimension(data.getData());
    }

    public Dimension getDimension(String dataName) {
        return LoadData.getDimension(this.getFirstLoadDataByName(dataName));
    }

    public Dimension getDimension(String dataName, Dimension defaultValue, boolean printWarning) {
        try {
            return this.getDimension(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load dimension " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Dimension getDimension(String dataName, Dimension defaultValue) {
        return this.getDimension(dataName, defaultValue, true);
    }

    public static Color getColor(LoadData data) {
        return SaveSerialize.unserializeColor(data.getData());
    }

    public Color getColor(String dataName) {
        return LoadData.getColor(this.getFirstLoadDataByName(dataName));
    }

    public Color getColor(String dataName, Color defaultValue, boolean printWarning) {
        try {
            return this.getColor(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load color " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Color getColor(String dataName, Color defaultValue) {
        return this.getColor(dataName, defaultValue, true);
    }

    public static Collection<String> getSafeStringCollection(LoadData data) {
        return SaveSerialize.unserializeSafeStringCollection(data.getData());
    }

    public Collection<String> getSafeStringCollection(String dataName) {
        return LoadData.getSafeStringCollection(this.getFirstLoadDataByName(dataName));
    }

    public Collection<String> getSafeStringCollection(String dataName, Collection<String> defaultValue, boolean printWarning) {
        try {
            return this.getSafeStringCollection(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load string collection " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Collection<String> getSafeStringCollection(String dataName, Collection<String> defaultValue) {
        return this.getSafeStringCollection(dataName, defaultValue, true);
    }

    public static List<String> getStringList(LoadData data) {
        return SaveSerialize.unserializeStringList(data.getData());
    }

    public List<String> getStringList(String dataName) {
        return LoadData.getStringList(this.getFirstLoadDataByName(dataName));
    }

    public List<String> getStringList(String dataName, List<String> defaultValue, boolean printWarning) {
        try {
            return this.getStringList(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load string array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public List<String> getStringList(String dataName, List<String> defaultValue) {
        return this.getStringList(dataName, defaultValue, true);
    }

    public static HashSet<String> getStringHashSet(LoadData data) {
        return SaveSerialize.unserializeStringHashSet(data.getData());
    }

    public HashSet<String> getStringHashSet(String dataName) {
        return LoadData.getStringHashSet(this.getFirstLoadDataByName(dataName));
    }

    public HashSet<String> getStringHashSet(String dataName, HashSet<String> defaultValue, boolean printWarning) {
        try {
            return this.getStringHashSet(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load string hash set " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public HashSet<String> getStringHashSet(String dataName, HashSet<String> defaultValue) {
        return this.getStringHashSet(dataName, defaultValue, true);
    }

    public static String[] getStringArray(LoadData data) {
        return SaveSerialize.unserializeStringArray(data.getData());
    }

    public String[] getStringArray(String dataName) {
        return LoadData.getStringArray(this.getFirstLoadDataByName(dataName));
    }

    public String[] getStringArray(String dataName, String[] defaultValue, boolean printWarning) {
        try {
            return this.getStringArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load string array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public String[] getStringArray(String dataName, String[] defaultValue) {
        return this.getStringArray(dataName, defaultValue, true);
    }

    public static short[] getShortArray(LoadData data) {
        return SaveSerialize.unserializeShortArray(data.getData());
    }

    public short[] getShortArray(String dataName) {
        return LoadData.getShortArray(this.getFirstLoadDataByName(dataName));
    }

    public short[] getShortArray(String dataName, short[] defaultValue, boolean printWarning) {
        try {
            return this.getShortArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load short array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public short[] getShortArray(String dataName, short[] defaultValue) {
        return this.getShortArray(dataName, defaultValue, true);
    }

    public static short[] getCompressedShortArray(LoadData data) throws DataFormatException, IOException {
        byte[] compressedBytes = GameUtils.fromBase64(data.getData());
        byte[] bytes = GameUtils.decompressData(compressedBytes);
        return GameMath.toShortArray(bytes);
    }

    public short[] getCompressedShortArray(String dataName) throws DataFormatException, IOException {
        return LoadData.getCompressedShortArray(this.getFirstLoadDataByName(dataName));
    }

    public short[] getCompressedShortArray(String dataName, short[] defaultValue, boolean printWarning) {
        try {
            return this.getCompressedShortArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load compressed short array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public short[] getCompressedShortArray(String dataName, short[] defaultValue) {
        return this.getCompressedShortArray(dataName, defaultValue, true);
    }

    public static Short[] getShortObjectArray(LoadData data) {
        return SaveSerialize.unserializeShortObjectArray(data.getData());
    }

    public Short[] getShortObjectArray(String dataName) {
        return LoadData.getShortObjectArray(this.getFirstLoadDataByName(dataName));
    }

    public Short[] getShortObjectArray(String dataName, Short[] defaultValue, boolean printWarning) {
        try {
            return this.getShortObjectArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load short object array " + dataName + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Short[] getShortObjectArray(String dataName, Short[] defaultValue) {
        return this.getShortObjectArray(dataName, defaultValue, true);
    }

    public static ArrayList<Short> getShortCollection(LoadData data) {
        return SaveSerialize.unserializeShortCollection(data.getData());
    }

    public ArrayList<Short> getShortCollection(String dataName) {
        return LoadData.getShortCollection(this.getFirstLoadDataByName(dataName));
    }

    public ArrayList<Short> getShortCollection(String dataName, ArrayList<Short> defaultValue, boolean printWarning) {
        try {
            return this.getShortCollection(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load short collection " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public ArrayList<Short> getShortCollection(String dataName, ArrayList<Short> defaultValue) {
        return this.getShortCollection(dataName, defaultValue, true);
    }

    public static int[] getIntArray(LoadData data) {
        return SaveSerialize.unserializeIntArray(data.getData());
    }

    public int[] getIntArray(String dataName) {
        return LoadData.getIntArray(this.getFirstLoadDataByName(dataName));
    }

    public int[] getIntArray(String dataName, int[] defaultValue, boolean printWarning) {
        try {
            return this.getIntArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load int array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public int[] getIntArray(String dataName, int[] defaultValue) {
        return this.getIntArray(dataName, defaultValue, true);
    }

    public static int[] getCompressedIntArray(LoadData data) throws DataFormatException, IOException {
        byte[] compressedBytes = GameUtils.fromBase64(data.getData());
        byte[] bytes = GameUtils.decompressData(compressedBytes);
        return GameMath.toIntArray(bytes);
    }

    public int[] getCompressedIntArray(String dataName) throws DataFormatException, IOException {
        return LoadData.getCompressedIntArray(this.getFirstLoadDataByName(dataName));
    }

    public int[] getCompressedIntArray(String dataName, int[] defaultValue, boolean printWarning) {
        try {
            return this.getCompressedIntArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load compressed int array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public int[] getCompressedIntArray(String dataName, int[] defaultValue) {
        return this.getCompressedIntArray(dataName, defaultValue, true);
    }

    public static Integer[] getIntObjectArray(LoadData data) {
        return SaveSerialize.unserializeIntObjectArray(data.getData());
    }

    public Integer[] getIntObjectArray(String dataName) {
        return LoadData.getIntObjectArray(this.getFirstLoadDataByName(dataName));
    }

    public Integer[] getIntObjectArray(String dataName, Integer[] defaultValue, boolean printWarning) {
        try {
            return this.getIntObjectArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load int object array " + dataName + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Integer[] getIntObjectArray(String dataName, Integer[] defaultValue) {
        return this.getIntObjectArray(dataName, defaultValue, true);
    }

    public static ArrayList<Integer> getIntCollection(LoadData data) {
        return SaveSerialize.unserializeIntCollection(data.getData());
    }

    public ArrayList<Integer> getIntCollection(String dataName) {
        return LoadData.getIntCollection(this.getFirstLoadDataByName(dataName));
    }

    public ArrayList<Integer> getIntCollection(String dataName, ArrayList<Integer> defaultValue, boolean printWarning) {
        try {
            return this.getIntCollection(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load int collection " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public ArrayList<Integer> getIntCollection(String dataName, ArrayList<Integer> defaultValue) {
        return this.getIntCollection(dataName, defaultValue, true);
    }

    public static byte[] getByteArray(LoadData data) {
        return SaveSerialize.unserializeByteArray(data.getData());
    }

    public byte[] getByteArray(String dataName) {
        return LoadData.getByteArray(this.getFirstLoadDataByName(dataName));
    }

    public byte[] getByteArray(String dataName, byte[] defaultValue, boolean printWarning) {
        try {
            return this.getByteArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load byte array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public byte[] getByteArray(String dataName, byte[] defaultValue) {
        return this.getByteArray(dataName, defaultValue, true);
    }

    public static byte[] getCompressedByteArray(LoadData data) throws DataFormatException, IOException {
        byte[] compressedBytes = GameUtils.fromBase64(data.getData());
        return GameUtils.decompressData(compressedBytes);
    }

    public byte[] getCompressedByteArray(String dataName) throws DataFormatException, IOException {
        return LoadData.getCompressedByteArray(this.getFirstLoadDataByName(dataName));
    }

    public byte[] getCompressedByteArray(String dataName, byte[] defaultValue, boolean printWarning) {
        try {
            return this.getCompressedByteArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load compressed byte array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public byte[] getCompressedByteArray(String dataName, byte[] defaultValue) {
        return this.getCompressedByteArray(dataName, defaultValue, true);
    }

    public static Byte[] getByteObjectArray(LoadData data) {
        return SaveSerialize.unserializeByteObjectArray(data.getData());
    }

    public Byte[] getByteObjectArray(String dataName) {
        return LoadData.getByteObjectArray(this.getFirstLoadDataByName(dataName));
    }

    public Byte[] getByteObjectArray(String dataName, Byte[] defaultValue, boolean printWarning) {
        try {
            return this.getByteObjectArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load byte object array " + dataName + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Byte[] getByteObjectArray(String dataName, Byte[] defaultValue) {
        return this.getByteObjectArray(dataName, defaultValue, true);
    }

    public static ArrayList<Byte> getByteCollection(LoadData data) {
        return SaveSerialize.unserializeByteCollection(data.getData());
    }

    public ArrayList<Byte> getByteCollection(String dataName) {
        return LoadData.getByteCollection(this.getFirstLoadDataByName(dataName));
    }

    public ArrayList<Byte> getByteCollection(String dataName, ArrayList<Byte> defaultValue, boolean printWarning) {
        try {
            return this.getByteCollection(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load byte collection " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public ArrayList<Byte> getByteCollection(String dataName, ArrayList<Byte> defaultValue) {
        return this.getByteCollection(dataName, defaultValue, true);
    }

    public static long[] getLongArray(LoadData data) {
        return SaveSerialize.unserializeLongArray(data.getData());
    }

    public long[] getLongArray(String dataName) {
        return LoadData.getLongArray(this.getFirstLoadDataByName(dataName));
    }

    public long[] getLongArray(String dataName, long[] defaultValue, boolean printWarning) {
        try {
            return this.getLongArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load long array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public long[] getLongArray(String dataName, long[] defaultValue) {
        return this.getLongArray(dataName, defaultValue, true);
    }

    public static long[] getCompressedLongArray(LoadData data) throws DataFormatException, IOException {
        byte[] compressedBytes = GameUtils.fromBase64(data.getData());
        byte[] bytes = GameUtils.decompressData(compressedBytes);
        return GameMath.toLongArray(bytes);
    }

    public long[] getCompressedLongArray(String dataName) throws DataFormatException, IOException {
        return LoadData.getCompressedLongArray(this.getFirstLoadDataByName(dataName));
    }

    public long[] getCompressedLongArray(String dataName, long[] defaultValue, boolean printWarning) {
        try {
            return this.getCompressedLongArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load compressed long array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public long[] getCompressedLongArray(String dataName, long[] defaultValue) {
        return this.getCompressedLongArray(dataName, defaultValue, true);
    }

    public static Long[] getLongObjectArray(LoadData data) {
        return SaveSerialize.unserializeLongObjectArray(data.getData());
    }

    public Long[] getLongObjectArray(String dataName) {
        return LoadData.getLongObjectArray(this.getFirstLoadDataByName(dataName));
    }

    public Long[] getLongObjectArray(String dataName, Long[] defaultValue, boolean printWarning) {
        try {
            return this.getLongObjectArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load long object array " + dataName + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Long[] getLongObjectArray(String dataName, Long[] defaultValue) {
        return this.getLongObjectArray(dataName, defaultValue, true);
    }

    public static ArrayList<Long> getLongCollection(LoadData data) {
        return SaveSerialize.unserializeLongCollection(data.getData());
    }

    public ArrayList<Long> getLongCollection(String dataName) {
        return LoadData.getLongCollection(this.getFirstLoadDataByName(dataName));
    }

    public ArrayList<Long> getLongCollection(String dataName, ArrayList<Long> defaultValue, boolean printWarning) {
        try {
            return this.getLongCollection(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load long collection " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public ArrayList<Long> getLongCollection(String dataName, ArrayList<Long> defaultValue) {
        return this.getLongCollection(dataName, defaultValue, true);
    }

    public static boolean[] getBooleanArray(LoadData data) {
        return SaveSerialize.unserializeBooleanArray(data.getData());
    }

    public boolean[] getBooleanArray(String dataName) {
        return LoadData.getBooleanArray(this.getFirstLoadDataByName(dataName));
    }

    public boolean[] getBooleanArray(String dataName, boolean[] defaultValue, boolean printWarning) {
        try {
            return this.getBooleanArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load boolean array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public boolean[] getBooleanArray(String dataName, boolean[] defaultValue) {
        return this.getBooleanArray(dataName, defaultValue, true);
    }

    public static boolean[] getSmallBooleanArray(LoadData data) {
        return SaveSerialize.unserializeSmallBooleanArray(data.getData());
    }

    public boolean[] getSmallBooleanArray(String dataName) {
        return LoadData.getSmallBooleanArray(this.getFirstLoadDataByName(dataName));
    }

    public boolean[] getSmallBooleanArray(String dataName, boolean[] defaultValue, boolean printWarning) {
        try {
            return this.getSmallBooleanArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load small boolean array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public boolean[] getSmallBooleanArray(String dataName, boolean[] defaultValue) {
        return this.getSmallBooleanArray(dataName, defaultValue, true);
    }

    public boolean isSmallBooleanArray(LoadData data) {
        return SaveSerialize.isSmallBooleanArray(data.getData());
    }

    public boolean isSmallBooleanArray(String dataName) {
        return this.isSmallBooleanArray(this.getFirstLoadDataByName(dataName));
    }

    public static boolean[] getCompressedBooleanArray(LoadData data) throws DataFormatException, IOException {
        byte[] compressedBytes = GameUtils.fromBase64(data.getData());
        byte[] bytes = GameUtils.decompressData(compressedBytes);
        return GameMath.toBooleanArray(bytes);
    }

    public boolean[] getCompressedBooleanArray(String dataName) throws DataFormatException, IOException {
        return LoadData.getCompressedBooleanArray(this.getFirstLoadDataByName(dataName));
    }

    public boolean[] getCompressedBooleanArray(String dataName, boolean[] defaultValue, boolean printWarning) {
        try {
            return this.getCompressedBooleanArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load compressed boolean array " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public boolean[] getCompressedBooleanArray(String dataName, boolean[] defaultValue) {
        return this.getCompressedBooleanArray(dataName, defaultValue, true);
    }

    public static Boolean[] getBooleanObjectArray(LoadData data) {
        return SaveSerialize.unserializeBooleanObjectArray(data.getData());
    }

    public Boolean[] getBooleanObjectArray(String dataName) {
        return LoadData.getBooleanObjectArray(this.getFirstLoadDataByName(dataName));
    }

    public Boolean[] getBooleanObjectArray(String dataName, Boolean[] defaultValue, boolean printWarning) {
        try {
            return this.getBooleanObjectArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load boolean object array " + dataName + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Boolean[] getBooleanObjectArray(String dataName, Boolean[] defaultValue) {
        return this.getBooleanObjectArray(dataName, defaultValue, true);
    }

    public static Boolean[] getSmallBooleanObjectArray(LoadData data) {
        return SaveSerialize.unserializeSmallBooleanObjectArray(data.getData());
    }

    public Boolean[] getSmallBooleanObjectArray(String dataName) {
        return LoadData.getSmallBooleanObjectArray(this.getFirstLoadDataByName(dataName));
    }

    public Boolean[] getSmallBooleanObjectArray(String dataName, Boolean[] defaultValue, boolean printWarning) {
        try {
            return this.getSmallBooleanObjectArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load small boolean object array " + dataName + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Boolean[] getSmallBooleanObjectArray(String dataName, Boolean[] defaultValue) {
        return this.getSmallBooleanObjectArray(dataName, defaultValue, true);
    }

    public static ArrayList<Boolean> getBooleanCollection(LoadData data) {
        return SaveSerialize.unserializeBooleanCollection(data.getData());
    }

    public ArrayList<Boolean> getBooleanCollection(String dataName) {
        return LoadData.getBooleanCollection(this.getFirstLoadDataByName(dataName));
    }

    public ArrayList<Boolean> getBooleanCollection(String dataName, ArrayList<Boolean> defaultValue, boolean printWarning) {
        try {
            return this.getBooleanCollection(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load boolean collection " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public ArrayList<Boolean> getBooleanCollection(String dataName, ArrayList<Boolean> defaultValue) {
        return this.getBooleanCollection(dataName, defaultValue, true);
    }

    public static ArrayList<Boolean> getSmallBooleanCollection(LoadData data) {
        return SaveSerialize.unserializeSmallBooleanCollection(data.getData());
    }

    public ArrayList<Boolean> getSmallBooleanCollection(String dataName) {
        return LoadData.getSmallBooleanCollection(this.getFirstLoadDataByName(dataName));
    }

    public ArrayList<Boolean> getSmallBooleanCollection(String dataName, ArrayList<Boolean> defaultValue, boolean printWarning) {
        try {
            return this.getSmallBooleanCollection(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load small boolean collection " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public ArrayList<Boolean> getSmallBooleanCollection(String dataName, ArrayList<Boolean> defaultValue) {
        return this.getSmallBooleanCollection(dataName, defaultValue, true);
    }

    public static Point[] getPointArray(LoadData data) {
        return SaveSerialize.unserializePointArray(data.getData());
    }

    public Point[] getPointArray(String dataName) {
        return LoadData.getPointArray(this.getFirstLoadDataByName(dataName));
    }

    public Point[] getPointArray(String dataName, Point[] defaultValue, boolean printWarning) {
        try {
            return this.getPointArray(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load point array " + dataName + " from " + (this.getName().equals("") ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public Point[] getPointArray(String dataName, Point[] defaultValue) {
        return this.getPointArray(dataName, defaultValue, true);
    }

    public static ArrayList<Point> getPointCollection(LoadData data) {
        return SaveSerialize.unserializePointCollection(data.getData());
    }

    public ArrayList<Point> getPointCollection(String dataName) {
        return LoadData.getPointCollection(this.getFirstLoadDataByName(dataName));
    }

    public ArrayList<Point> getPointCollection(String dataName, ArrayList<Point> defaultValue, boolean printWarning) {
        try {
            return this.getPointCollection(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load point collection " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public ArrayList<Point> getPointCollection(String dataName, ArrayList<Point> defaultValue) {
        return this.getPointCollection(dataName, defaultValue, true);
    }

    public static PointHashSet getPointHashSet(LoadData data) {
        return SaveSerialize.unserializePointHashSet(data.getData());
    }

    public PointHashSet getPointHashSet(String dataName) {
        return LoadData.getPointHashSet(this.getFirstLoadDataByName(dataName));
    }

    public PointHashSet getPointHashSet(String dataName, PointHashSet defaultValue, boolean printWarning) {
        try {
            return this.getPointHashSet(dataName);
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load point hashset " + dataName + " from " + (this.getName().isEmpty() ? "null" : this.getName()) + ".");
            }
            return defaultValue;
        }
    }

    public PointHashSet getPointHashSet(String dataName, PointHashSet defaultValue) {
        return this.getPointHashSet(dataName, defaultValue, true);
    }
}

