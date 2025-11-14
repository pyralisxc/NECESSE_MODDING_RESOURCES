/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.HashMap;

public class GameBlackboard {
    private final HashMap<String, Object> map = new HashMap();

    public GameBlackboard copy() {
        GameBlackboard out = new GameBlackboard();
        out.map.putAll(this.map);
        return out;
    }

    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    public <C> C get(Class<? extends C> expectedClass, String key, C defaultObject) {
        try {
            return expectedClass.cast(this.map.getOrDefault(key, defaultObject));
        }
        catch (ClassCastException e) {
            return defaultObject;
        }
    }

    public <C> C get(Class<? extends C> expectedClass, String key) {
        return this.get(expectedClass, key, null);
    }

    public <C> C getNotNull(Class<? extends C> expectedClass, String key, C defaultObject) {
        C object = this.get(expectedClass, key, defaultObject);
        return object == null ? defaultObject : object;
    }

    public boolean getBoolean(String key) {
        return this.getNotNull(Boolean.class, key, false);
    }

    public byte getByte(String key) {
        return this.getNotNull(Byte.class, key, (byte)0);
    }

    public short getShort(String key) {
        return this.getNotNull(Short.class, key, (short)0);
    }

    public int getInt(String key) {
        return this.getNotNull(Integer.class, key, 0);
    }

    public long getLong(String key) {
        return this.getNotNull(Long.class, key, 0L);
    }

    public float getFloat(String key) {
        return this.getNotNull(Float.class, key, Float.valueOf(0.0f)).floatValue();
    }

    public double getDouble(String key) {
        return this.getNotNull(Double.class, key, 0.0);
    }

    public String getString(String key, String defaultObject) {
        return this.getNotNull(String.class, key, defaultObject);
    }

    public String getString(String key) {
        return this.getString(key, null);
    }

    public GameBlackboard set(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    public String getDebugString() {
        return this.map.toString();
    }
}

