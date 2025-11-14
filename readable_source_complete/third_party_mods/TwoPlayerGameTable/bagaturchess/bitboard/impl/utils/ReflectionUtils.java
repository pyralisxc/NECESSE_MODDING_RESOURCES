/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {
    public static Object createObjectByClassName_NoArgsConstructor(String className) {
        Object result = null;
        try {
            Class<?> clazz = ReflectionUtils.class.getClassLoader().loadClass(className);
            result = clazz.newInstance();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Object createObjectByClassName_StringsConstructor(String className, String[] args) {
        Object result = null;
        try {
            Class<?> clazz = ReflectionUtils.class.getClassLoader().loadClass(className);
            Constructor<?> constructor = clazz.getConstructor(String[].class);
            result = constructor.newInstance(new Object[]{args});
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Object createObjectByClassName_ObjectsConstructor(String className, Object[] args) {
        Object result = null;
        try {
            Class<?> clazz = ReflectionUtils.class.getClassLoader().loadClass(className);
            Constructor<?> constructor = clazz.getConstructor(Object[].class);
            result = constructor.newInstance(new Object[]{args});
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void main(String[] args) {
        args = new String[]{"test"};
    }
}

