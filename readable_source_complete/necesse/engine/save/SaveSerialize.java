/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveComponent;
import necesse.engine.util.PointHashSet;

public class SaveSerialize {
    public static String serializeEnum(Enum e) {
        return e.toString();
    }

    public static <T extends Enum<T>> T unserializeEnum(Class<T> type, String str) {
        Enum[] constants;
        for (Enum constant : constants = (Enum[])type.getEnumConstants()) {
            if (!constant.toString().equals(str)) continue;
            return (T)constant;
        }
        throw new IllegalArgumentException("No enum constant " + type.getName() + "." + str);
    }

    public static String serializePoint(Point point) {
        return "[" + point.x + ", " + point.y + "]";
    }

    public static Point unserializePoint(String str) {
        try {
            str = str.substring(1, str.length() - 1);
            if (str.isEmpty()) {
                return new Point();
            }
            String[] values = str.split(",");
            return new Point(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()));
        }
        catch (Exception e) {
            System.err.println("Could not unserialize point: " + str);
            throw new NullPointerException();
        }
    }

    public static String serializeDimension(Dimension dim) {
        return "[" + dim.width + ", " + dim.height + "]";
    }

    public static Dimension unserializeDimension(String str) {
        try {
            str = str.substring(1, str.length() - 1);
            if (str.isEmpty()) {
                return new Dimension();
            }
            String[] values = str.split(",");
            return new Dimension(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()));
        }
        catch (Exception e) {
            System.err.println("Could not unserialize point: " + str);
            throw new NullPointerException();
        }
    }

    public static String serializeColor(Color col) {
        return "[" + col.getRed() + ", " + col.getGreen() + ", " + col.getBlue() + ", " + col.getAlpha() + "]";
    }

    public static Color unserializeColor(String str) {
        str = str.substring(1, str.length() - 1);
        String[] values = str.split(",");
        try {
            return new Color(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()), Integer.parseInt(values[2].trim()), Integer.parseInt(values[3].trim()));
        }
        catch (Exception e) {
            System.err.println("Could not unserialize color: " + str);
            throw new NullPointerException();
        }
    }

    public static <T> String serializeCollect(Collection<T> list, Function<T, String> toString) {
        StringBuilder builder = new StringBuilder("[");
        Iterator<T> iterator = list.iterator();
        boolean addSeparator = false;
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (addSeparator) {
                builder.append(",");
            }
            builder.append(toString.apply(next));
            addSeparator = true;
        }
        return builder.append("]").toString();
    }

    public static <T, V, L> L unserializeCollect(String str, Function<String, T> toObject, Collector<? super T, V, L> collector) {
        int index;
        str = str.substring(1, str.length() - 1);
        V out = collector.supplier().get();
        int currentIndex = 0;
        while ((index = SaveComponent.indexOf(str, ',', currentIndex)) != -1) {
            collector.accumulator().accept(out, toObject.apply(str.substring(currentIndex, index)));
            currentIndex = index + 1;
        }
        collector.accumulator().accept(out, toObject.apply(str.substring(currentIndex)));
        return collector.finisher().apply(out);
    }

    public static String serializeSafeStringCollection(Collection<String> list) {
        return SaveSerialize.serializeCollect(list, SaveComponent::toSafeData);
    }

    public static List<String> unserializeSafeStringCollection(String str) {
        return SaveSerialize.unserializeCollect(str, SaveComponent::fromSafeData, Collectors.toList());
    }

    public static String serializeStringList(List<String> list) {
        return Arrays.toString(list.toArray());
    }

    public static List<String> unserializeStringList(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new ArrayList<String>();
        }
        String[] values = str.split(", ");
        return new ArrayList<String>(Arrays.asList(values));
    }

    public static String serializeStringHashSet(HashSet<String> hashSet) {
        return Arrays.toString(hashSet.toArray());
    }

    public static HashSet<String> unserializeStringHashSet(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new HashSet<String>();
        }
        String[] values = str.split(", ");
        HashSet<String> output = new HashSet<String>();
        Collections.addAll(output, values);
        return output;
    }

    public static String serializeStringArray(String[] list) {
        return Arrays.toString(list);
    }

    public static String[] unserializeStringArray(String str) {
        str = str.substring(1, str.length() - 1);
        return str.split(", ");
    }

    protected static <T> String serializeCollection(Collection<? extends T> collection, String delimiter, Function<T, String> toString) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (T element : collection) {
            if (!first) {
                builder.append(delimiter);
            }
            builder.append(toString.apply(element));
            first = false;
        }
        return "[" + builder + "]";
    }

    protected static <T> String serializeCollection(Collection<? extends T> collection, Function<T, String> toString) {
        return SaveSerialize.serializeCollection(collection, ",", toString);
    }

    protected static <T> String serializeCollection(Collection<? extends T> collection) {
        return SaveSerialize.serializeCollection(collection, Objects::toString);
    }

    protected static <T> ArrayList<T> unserializeCollection(String data, String delimiterRegex, Function<String, T> parser) {
        if (data.charAt(0) != '[' || data.charAt(data.length() - 1) != ']') {
            throw new LoadDataException("Could not unserialize collection because of missing [ and ].");
        }
        if ((data = data.substring(1, data.length() - 1)).isEmpty()) {
            return new ArrayList();
        }
        String[] split = data.split(delimiterRegex);
        ArrayList<T> list = new ArrayList<T>(split.length);
        for (String str : split) {
            T parsed = parser.apply(str);
            list.add(parsed);
        }
        return list;
    }

    protected static <T> ArrayList<T> unserializeCollection(String data, Function<String, T> parser) {
        return SaveSerialize.unserializeCollection(data, ",", parser);
    }

    protected static <T> String serializeIterable(Iterable<? extends T> iterable, String delimiter, Function<T, String> toString) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (T element : iterable) {
            if (!first) {
                builder.append(delimiter);
            }
            builder.append(toString.apply(element));
            first = false;
        }
        return "[" + builder + "]";
    }

    protected static <T> String serializeIterable(Iterable<? extends T> iterable, Function<T, String> toString) {
        return SaveSerialize.serializeIterable(iterable, ",", toString);
    }

    protected static <T> String serializeIterable(Iterable<? extends T> iterable) {
        return SaveSerialize.serializeIterable(iterable, Objects::toString);
    }

    protected static <T, R, A> R unserializeIterable(String data, String delimiterRegex, Function<String, T> parser, Collector<? super T, A, R> collector) {
        if (data.charAt(0) != '[' || data.charAt(data.length() - 1) != ']') {
            throw new LoadDataException("Could not unserialize collection because of missing [ and ].");
        }
        if ((data = data.substring(1, data.length() - 1)).isEmpty()) {
            return collector.finisher().apply(collector.supplier().get());
        }
        String[] split = data.split(delimiterRegex);
        A accumulator = collector.supplier().get();
        for (String str : split) {
            T parsed = parser.apply(str);
            collector.accumulator().accept(accumulator, parsed);
        }
        return collector.finisher().apply(accumulator);
    }

    protected static <T, R, A> R unserializeIterable(String data, Function<String, T> parser, Collector<? super T, A, R> collector) {
        return SaveSerialize.unserializeIterable(data, ",", parser, collector);
    }

    protected static <T> String serializeArray(T[] array, String delimiter, Function<T, String> toString) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (T element : array) {
            if (!first) {
                builder.append(delimiter);
            }
            builder.append(toString.apply(element));
            first = false;
        }
        return "[" + builder + "]";
    }

    protected static <T> String serializeArray(T[] array, Function<T, String> toString) {
        return SaveSerialize.serializeArray(array, ",", toString);
    }

    protected static <T> String serializeArray(T[] array) {
        return SaveSerialize.serializeArray(array, Objects::toString);
    }

    protected static <T> T[] unserializeArray(T[] a, String data, String delimiterRegex, Function<String, T> parser) {
        if (data.charAt(0) != '[' || data.charAt(data.length() - 1) != ']') {
            throw new LoadDataException("Could not unserialize collection because of missing [ and ].");
        }
        if ((data = data.substring(1, data.length() - 1)).isEmpty()) {
            return Arrays.copyOf(a, 0);
        }
        String[] split = data.split(delimiterRegex);
        T[] array = Arrays.copyOf(a, split.length);
        for (int i = 0; i < split.length; ++i) {
            T parsed = parser.apply(split[i]);
            array[i] = parsed;
        }
        return array;
    }

    protected static <T> T[] unserializeArray(T[] a, String data, Function<String, T> parser) {
        return SaveSerialize.unserializeArray(a, data, ",", parser);
    }

    public static String serializeShortArray(short[] list) {
        return Arrays.toString(list);
    }

    public static short[] unserializeShortArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new short[0];
        }
        String[] values = str.split(", ");
        short[] output = new short[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = Short.parseShort(values[i]);
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse short: " + values[i] + "  in string array.");
            }
        }
        return output;
    }

    public static String serializeShortObjectArray(Short[] list) {
        return Arrays.toString((Object[])list);
    }

    public static Short[] unserializeShortObjectArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new Short[0];
        }
        String[] values = str.split(", ");
        Short[] output = new Short[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = values[i].equals("null") ? null : Short.valueOf(Short.parseShort(values[i]));
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse short object: " + values[i] + "  in string array.");
            }
        }
        return output;
    }

    public static String serializeShortCollection(Collection<Short> collection) {
        return SaveSerialize.serializeCollection(collection);
    }

    public static ArrayList<Short> unserializeShortCollection(String str) {
        return SaveSerialize.unserializeCollection(str, Short::parseShort);
    }

    public static String serializeIntArray(int[] list) {
        return Arrays.toString(list);
    }

    public static int[] unserializeIntArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new int[0];
        }
        String[] values = str.split(", ");
        int[] output = new int[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = Integer.parseInt(values[i]);
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse integer: " + values[i] + "  in string array.");
            }
        }
        return output;
    }

    public static String serializeIntObjectArray(Integer[] list) {
        return Arrays.toString((Object[])list);
    }

    public static Integer[] unserializeIntObjectArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new Integer[0];
        }
        String[] values = str.split(", ");
        Integer[] output = new Integer[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = values[i].equals("null") ? null : Integer.valueOf(Integer.parseInt(values[i]));
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse integer object: " + values[i] + "  in string array.");
            }
        }
        return output;
    }

    public static String serializeIntCollection(Collection<Integer> collection) {
        return SaveSerialize.serializeCollection(collection);
    }

    public static ArrayList<Integer> unserializeIntCollection(String str) {
        return SaveSerialize.unserializeCollection(str, Integer::parseInt);
    }

    public static String serializeByteArray(byte[] list) {
        return Arrays.toString(list);
    }

    public static byte[] unserializeByteArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new byte[0];
        }
        String[] values = str.split(", ");
        byte[] output = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = Byte.parseByte(values[i]);
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse byte: " + values[i] + " in string array.");
            }
        }
        return output;
    }

    public static String serializeByteObjectArray(Byte[] list) {
        return Arrays.toString((Object[])list);
    }

    public static Byte[] unserializeByteObjectArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new Byte[0];
        }
        String[] values = str.split(", ");
        Byte[] output = new Byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = values[i].equals("null") ? null : Byte.valueOf(Byte.parseByte(values[i]));
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse byte object: " + values[i] + " in string array.");
            }
        }
        return output;
    }

    public static String serializeByteCollection(Collection<Byte> collection) {
        return SaveSerialize.serializeCollection(collection);
    }

    public static ArrayList<Byte> unserializeByteCollection(String str) {
        return SaveSerialize.unserializeCollection(str, Byte::parseByte);
    }

    public static String serializeLongArray(long[] list) {
        return Arrays.toString(list);
    }

    public static long[] unserializeLongArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new long[0];
        }
        String[] values = str.split(", ");
        long[] output = new long[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = Long.parseLong(values[i]);
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse long: " + values[i] + "  in string array.");
            }
        }
        return output;
    }

    public static String serializeLongObjectArray(Long[] list) {
        return Arrays.toString((Object[])list);
    }

    public static Long[] unserializeLongObjectArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new Long[0];
        }
        String[] values = str.split(", ");
        Long[] output = new Long[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = values[i].equals("null") ? null : Long.valueOf(Long.parseLong(values[i]));
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse long object: " + values[i] + "  in string array.");
            }
        }
        return output;
    }

    public static String serializeLongCollection(Collection<Long> collection) {
        return SaveSerialize.serializeCollection(collection);
    }

    public static ArrayList<Long> unserializeLongCollection(String str) {
        return SaveSerialize.unserializeCollection(str, Long::parseLong);
    }

    public static String serializeBooleanArray(boolean[] list) {
        return Arrays.toString(list);
    }

    public static boolean[] unserializeBooleanArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new boolean[0];
        }
        String[] values = str.split(", ");
        boolean[] output = new boolean[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = Boolean.parseBoolean(values[i]);
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse boolean: " + values[i] + "  in string array.");
            }
        }
        return output;
    }

    public static String serializeSmallBooleanArray(boolean[] list) {
        StringBuilder out = new StringBuilder();
        for (boolean v : list) {
            out.append(v ? "1" : "0");
        }
        return out.toString();
    }

    public static boolean isSmallBooleanArray(String str) {
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '0' || c == '1') continue;
            return false;
        }
        return true;
    }

    public static boolean[] unserializeSmallBooleanArray(String str) {
        boolean[] output = new boolean[str.length()];
        for (int i = 0; i < str.length(); ++i) {
            output[i] = str.charAt(i) != '0';
        }
        return output;
    }

    public static String serializeBooleanObjectArray(Boolean[] list) {
        return Arrays.toString((Object[])list);
    }

    public static Boolean[] unserializeBooleanObjectArray(String str) {
        if ((str = str.substring(1, str.length() - 1)).isEmpty()) {
            return new Boolean[0];
        }
        String[] values = str.split(", ");
        Boolean[] output = new Boolean[values.length];
        for (int i = 0; i < values.length; ++i) {
            try {
                output[i] = values[i].equals("null") ? null : Boolean.valueOf(Boolean.parseBoolean(values[i]));
                continue;
            }
            catch (Exception e) {
                System.err.println("Could not parse boolean object: " + values[i] + "  in string array.");
            }
        }
        return output;
    }

    public static String serializeSmallBooleanObjectArray(Boolean[] list) {
        StringBuilder out = new StringBuilder();
        for (Boolean v : list) {
            out.append(v == null ? "2" : (v != false ? "1" : "0"));
        }
        return out.toString();
    }

    public static Boolean[] unserializeSmallBooleanObjectArray(String str) {
        Boolean[] output = new Boolean[str.length()];
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            output[i] = c == '\u0002' ? null : Boolean.valueOf(c != '0');
        }
        return output;
    }

    public static String serializeBooleanCollection(Collection<Boolean> collection) {
        return SaveSerialize.serializeCollection(collection);
    }

    public static ArrayList<Boolean> unserializeBooleanCollection(String str) {
        return SaveSerialize.unserializeCollection(str, Boolean::parseBoolean);
    }

    public static String serializeSmallBooleanCollection(Collection<Boolean> collection) {
        StringBuilder out = new StringBuilder();
        for (boolean v : collection) {
            out.append(v ? "1" : "0");
        }
        return out.toString();
    }

    public static ArrayList<Boolean> unserializeSmallBooleanCollection(String str) {
        ArrayList<Boolean> output = new ArrayList<Boolean>(str.length());
        for (int i = 0; i < str.length(); ++i) {
            output.add(str.charAt(i) != '0');
        }
        return output;
    }

    public static String serializePointArray(Point[] list) {
        return SaveSerialize.serializeArray(list, p -> p.x + "x" + p.y);
    }

    public static Point[] unserializePointArray(String str) {
        return SaveSerialize.unserializeArray(new Point[0], str, s -> {
            int index = s.indexOf("x");
            if (index == -1) {
                throw new LoadDataException("Could not parse point: " + s);
            }
            int x = Integer.parseInt(s.substring(0, index));
            int y = Integer.parseInt(s.substring(index + 1));
            return new Point(x, y);
        });
    }

    public static String serializePointCollection(Collection<Point> collection) {
        return SaveSerialize.serializeCollection(collection, p -> p.x + "x" + p.y);
    }

    public static ArrayList<Point> unserializePointCollection(String str) {
        return SaveSerialize.unserializeCollection(str, s -> {
            int index = s.indexOf("x");
            if (index == -1) {
                throw new LoadDataException("Could not parse point: " + s);
            }
            int x = Integer.parseInt(s.substring(0, index));
            int y = Integer.parseInt(s.substring(index + 1));
            return new Point(x, y);
        });
    }

    public static String serializePointHashSet(PointHashSet set) {
        return SaveSerialize.serializeIterable(set, p -> p.x + "x" + p.y);
    }

    public static PointHashSet unserializePointHashSet(String str) {
        return SaveSerialize.unserializeIterable(str, s -> {
            int index = s.indexOf("x");
            if (index == -1) {
                throw new LoadDataException("Could not parse point: " + s);
            }
            int x = Integer.parseInt(s.substring(0, index));
            int y = Integer.parseInt(s.substring(index + 1));
            return new Point(x, y);
        }, PointHashSet.collector());
    }
}

