/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveSerialize;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.WorldFile;

public class SaveData {
    private final SaveComponent save;

    public SaveData(String name) {
        this(new SaveComponent(name));
    }

    public SaveData(String name, String comment) {
        this(new SaveComponent(name, comment));
    }

    public SaveData(SaveComponent saveComponent) {
        Objects.requireNonNull(saveComponent);
        this.save = saveComponent;
    }

    public int getSize() {
        return this.save.getSize();
    }

    public boolean isEmpty() {
        return this.save.isEmpty();
    }

    public void addSaveData(SaveData data) {
        this.save.addComponent(data.save);
    }

    public void removeSaveDataByName(String name) {
        this.save.removeComponentsByName(name);
    }

    public boolean removeFirstSaveDataByName(String name) {
        return this.save.removeFirstComponentByName(name);
    }

    public void clearComponents() {
        this.save.clearComponents();
    }

    public String getScript() {
        return this.save.getScript();
    }

    public String getScript(boolean compressed) {
        return this.save.getScript(compressed);
    }

    public LoadData toLoadData() {
        return new LoadData(this.save);
    }

    public void saveScriptRaw(File file, boolean compressed) throws IOException {
        SaveComponent.saveScriptRaw(file, this.save, compressed);
    }

    public void saveScript(File file, boolean compressed) {
        SaveComponent.saveScript(file, this.save, compressed);
    }

    public void saveScript(File file) {
        SaveComponent.saveScript(file, this.save);
    }

    public void saveScriptRaw(WorldFile file, boolean compressed) throws IOException {
        SaveComponent.saveScriptRaw(file, this.save, compressed);
    }

    public void saveScript(WorldFile file, boolean compressed) {
        SaveComponent.saveScript(file, this.save, compressed);
    }

    public void saveScript(WorldFile file) {
        SaveComponent.saveScript(file, this.save);
    }

    public void addBoolean(String name, boolean data) {
        this.save.addData(name, data);
    }

    public void addBoolean(String name, boolean data, String comment) {
        this.save.addData(name, (Object)data, comment);
    }

    public void addByte(String name, byte data) {
        this.save.addData(name, data);
    }

    public void addByte(String name, byte data, String comment) {
        this.save.addData(name, (Object)data, comment);
    }

    public void addShort(String name, short data) {
        this.save.addData(name, data);
    }

    public void addShort(String name, short data, String comment) {
        this.save.addData(name, (Object)data, comment);
    }

    public void addInt(String name, int data) {
        this.save.addData(name, data);
    }

    public void addInt(String name, int data, String comment) {
        this.save.addData(name, (Object)data, comment);
    }

    public void addLong(String name, long data) {
        this.save.addData(name, data);
    }

    public void addLong(String name, long data, String comment) {
        this.save.addData(name, (Object)data, comment);
    }

    public void addFloat(String name, float data) {
        this.save.addData(name, Float.valueOf(data));
    }

    public void addFloat(String name, float data, String comment) {
        this.save.addData(name, (Object)Float.valueOf(data), comment);
    }

    public void addDouble(String name, double data) {
        this.save.addData(name, data);
    }

    public void addDouble(String name, double data, String comment) {
        this.save.addData(name, (Object)data, comment);
    }

    public void addEnum(String name, Enum e) {
        this.save.addData(name, SaveSerialize.serializeEnum(e));
    }

    public void addEnum(String name, Enum e, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeEnum(e), comment);
    }

    public void addUnsafeString(String name, String data) {
        this.save.addData(name, data);
    }

    public void addUnsafeString(String name, String data, String comment) {
        this.save.addData(name, (Object)data, comment);
    }

    public void addSafeString(String name, String data) {
        this.save.addData(name, SaveComponent.toSafeData(data));
    }

    public void addSafeString(String name, String data, String comment) {
        this.save.addData(name, (Object)SaveComponent.toSafeData(data), comment);
    }

    public void addPoint(String name, Point data) {
        this.save.addData(name, SaveSerialize.serializePoint(data));
    }

    public void addPoint(String name, Point data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializePoint(data), comment);
    }

    public void addDimension(String name, Dimension data) {
        this.save.addData(name, SaveSerialize.serializeDimension(data));
    }

    public void addDimension(String name, Dimension data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeDimension(data), comment);
    }

    public void addColor(String name, Color data) {
        this.save.addData(name, SaveSerialize.serializeColor(data));
    }

    public void addColor(String name, Color data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeColor(data), comment);
    }

    public void addSafeStringCollection(String name, Collection<String> data) {
        this.save.addData(name, SaveSerialize.serializeSafeStringCollection(data));
    }

    public void addSafeStringCollection(String name, Collection<String> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeSafeStringCollection(data), comment);
    }

    public void addStringList(String name, List<String> data) {
        this.save.addData(name, SaveSerialize.serializeStringList(data));
    }

    public void addStringList(String name, List<String> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeStringList(data), comment);
    }

    public void addStringHashSet(String name, HashSet<String> data) {
        this.save.addData(name, SaveSerialize.serializeStringHashSet(data));
    }

    public void addStringHashSet(String name, HashSet<String> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeStringHashSet(data), comment);
    }

    public void addStringArray(String name, String[] data) {
        this.save.addData(name, SaveSerialize.serializeStringArray(data));
    }

    public void addStringArray(String name, String[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeStringArray(data), comment);
    }

    public void addShortArray(String name, short[] data) {
        this.save.addData(name, SaveSerialize.serializeShortArray(data));
    }

    public void addShortArray(String name, short[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeShortArray(data), comment);
    }

    public void addCompressedShortArray(String name, short[] data) throws IOException {
        byte[] bytes = GameMath.toByteArray(data);
        byte[] compressed = GameUtils.compressData(bytes);
        String base64 = GameUtils.toBase64(compressed);
        this.save.addData(name, base64);
    }

    public void addShortObjectArray(String name, Short[] data) {
        this.save.addData(name, SaveSerialize.serializeShortObjectArray(data));
    }

    public void addShortObjectArray(String name, Short[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeShortObjectArray(data), comment);
    }

    public void addShortCollection(String name, Collection<Short> data) {
        this.save.addData(name, SaveSerialize.serializeShortCollection(data));
    }

    public void addShortCollection(String name, Collection<Short> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeShortCollection(data), comment);
    }

    public void addIntArray(String name, int[] data) {
        this.save.addData(name, SaveSerialize.serializeIntArray(data));
    }

    public void addIntArray(String name, int[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeIntArray(data), comment);
    }

    public void addCompressedIntArray(String name, int[] data) throws IOException {
        byte[] bytes = GameMath.toByteArray(data);
        byte[] compressed = GameUtils.compressData(bytes);
        String base64 = GameUtils.toBase64(compressed);
        this.save.addData(name, base64);
    }

    public void addIntObjectArray(String name, Integer[] data) {
        this.save.addData(name, SaveSerialize.serializeIntObjectArray(data));
    }

    public void addIntObjectArray(String name, Integer[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeIntObjectArray(data), comment);
    }

    public void addIntCollection(String name, Collection<Integer> data) {
        this.save.addData(name, SaveSerialize.serializeIntCollection(data));
    }

    public void addIntCollection(String name, Collection<Integer> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeIntCollection(data), comment);
    }

    public void addByteArray(String name, byte[] data) {
        this.save.addData(name, SaveSerialize.serializeByteArray(data));
    }

    public void addByteArray(String name, byte[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeByteArray(data), comment);
    }

    public void addCompressedByteArray(String name, byte[] data) throws IOException {
        byte[] compressed = GameUtils.compressData(data);
        String base64 = GameUtils.toBase64(compressed);
        this.save.addData(name, base64);
    }

    public void addByteObjectArray(String name, Byte[] data) {
        this.save.addData(name, SaveSerialize.serializeByteObjectArray(data));
    }

    public void addByteObjectArray(String name, Byte[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeByteObjectArray(data), comment);
    }

    public void addByteCollection(String name, Collection<Byte> data) {
        this.save.addData(name, SaveSerialize.serializeByteCollection(data));
    }

    public void addByteCollection(String name, Collection<Byte> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeByteCollection(data), comment);
    }

    public void addLongArray(String name, long[] data) {
        this.save.addData(name, SaveSerialize.serializeLongArray(data));
    }

    public void addLongArray(String name, long[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeLongArray(data), comment);
    }

    public void addCompressedLongArray(String name, long[] data) throws IOException {
        byte[] bytes = GameMath.toByteArray(data);
        byte[] compressed = GameUtils.compressData(bytes);
        String base64 = GameUtils.toBase64(compressed);
        this.save.addData(name, base64);
    }

    public void addLongObjectArray(String name, Long[] data) {
        this.save.addData(name, SaveSerialize.serializeLongObjectArray(data));
    }

    public void addLongObjectArray(String name, Long[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeLongObjectArray(data), comment);
    }

    public void addLongCollection(String name, Collection<Long> data) {
        this.save.addData(name, SaveSerialize.serializeLongCollection(data));
    }

    public void addLongCollection(String name, Collection<Long> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeLongCollection(data), comment);
    }

    public void addBooleanArray(String name, boolean[] data) {
        this.save.addData(name, SaveSerialize.serializeBooleanArray(data));
    }

    public void addBooleanArray(String name, boolean[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeBooleanArray(data), comment);
    }

    public void addSmallBooleanArray(String name, boolean[] data) {
        this.save.addData(name, SaveSerialize.serializeSmallBooleanArray(data));
    }

    public void addSmallBooleanArray(String name, boolean[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeSmallBooleanArray(data), comment);
    }

    public void addCompressedBooleanArray(String name, boolean[] data) throws IOException {
        byte[] bytes = GameMath.toByteArray(data);
        byte[] compressed = GameUtils.compressData(bytes);
        String base64 = GameUtils.toBase64(compressed);
        this.save.addData(name, base64);
    }

    public void addBooleanObjectArray(String name, Boolean[] data) {
        this.save.addData(name, SaveSerialize.serializeBooleanObjectArray(data));
    }

    public void addBooleanObjectArray(String name, Boolean[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeBooleanObjectArray(data), comment);
    }

    public void addSmallBooleanObjectArray(String name, Boolean[] data) {
        this.save.addData(name, SaveSerialize.serializeSmallBooleanObjectArray(data));
    }

    public void addSmallBooleanObjectArray(String name, Boolean[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeSmallBooleanObjectArray(data), comment);
    }

    public void addBooleanCollection(String name, Collection<Boolean> data) {
        this.save.addData(name, SaveSerialize.serializeBooleanCollection(data));
    }

    public void addBooleanCollection(String name, Collection<Boolean> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeBooleanCollection(data), comment);
    }

    public void addSmallBooleanCollection(String name, Collection<Boolean> data) {
        this.save.addData(name, SaveSerialize.serializeSmallBooleanCollection(data));
    }

    public void addSmallBooleanCollection(String name, Collection<Boolean> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializeSmallBooleanCollection(data), comment);
    }

    public void addPointArray(String name, Point[] data) {
        this.save.addData(name, SaveSerialize.serializePointArray(data));
    }

    public void addPointArray(String name, Point[] data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializePointArray(data), comment);
    }

    public void addPointCollection(String name, Collection<Point> data) {
        this.save.addData(name, SaveSerialize.serializePointCollection(data));
    }

    public void addPointCollection(String name, Collection<Point> data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializePointCollection(data), comment);
    }

    public void addPointHashSet(String name, PointHashSet data) {
        this.save.addData(name, SaveSerialize.serializePointHashSet(data));
    }

    public void addPointHashSet(String name, PointHashSet data, String comment) {
        this.save.addData(name, (Object)SaveSerialize.serializePointHashSet(data), comment);
    }
}

