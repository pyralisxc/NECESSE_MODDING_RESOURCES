/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.common;

import java.util.Arrays;
import java.util.Random;

public class Utils {
    private static Random rnd = new Random();

    public static String[] copyOfRange(String[] args, int start) {
        return Utils.copyOfRange(args, start, args.length);
    }

    public static String[] copyOfRange(String[] args, int start, int end) {
        String[] subarray = new String[args.length - 1];
        int counter = 0;
        for (int i = start; i < end; ++i) {
            subarray[counter++] = args[i];
        }
        return subarray;
    }

    public static String[] concat(String[] a1, String[] a2) {
        String[] result = new String[a1.length + a2.length];
        System.arraycopy(a1, 0, result, 0, a1.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
    }

    public static void dumpMemory(String message) {
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();
        System.out.println("Memory: [" + message + "]" + (total - free) / 0x100000L + " MB");
    }

    public static final long[] copy(long[] source) {
        long[] result = new long[source.length];
        for (int i = 0; i < source.length; ++i) {
            result[i] = source[i];
        }
        return result;
    }

    public static final long[] copy(long[] source, long[] result) {
        for (int i = 0; i < source.length; ++i) {
            result[i] = source[i];
        }
        return result;
    }

    public static final int[] copy(int[] source) {
        int[] result = new int[source.length];
        for (int i = 0; i < source.length; ++i) {
            result[i] = source[i];
        }
        return result;
    }

    public static final boolean[] copy(boolean[] source) {
        boolean[] result = new boolean[source.length];
        for (int i = 0; i < source.length; ++i) {
            result[i] = source[i];
        }
        return result;
    }

    public static final long[][] copy(long[][] source) {
        long[][] result = new long[source.length][];
        for (int i = 0; i < source.length; ++i) {
            long[] el = source[i];
            if (el == null) continue;
            result[i] = Utils.copy(source[i]);
        }
        return result;
    }

    public static final boolean equals(long[][] arr1, long[][] arr2) {
        boolean result = false;
        if (arr1.length == arr2.length) {
            for (int i = 0; i < arr1.length; ++i) {
                long[] el1 = arr1[i];
                long[] el2 = arr2[i];
                if (!Arrays.equals(el1, el2)) {
                    result = false;
                    break;
                }
                if (i != arr1.length - 1) continue;
                result = true;
                break;
            }
        }
        return result;
    }

    public static final int countBits_less1s(long number) {
        int result = 0;
        while (number != 0L) {
            number = number - 1L & number;
            ++result;
        }
        if (result < 0 || result >= 64) {
            throw new IllegalStateException();
        }
        return result;
    }

    public static final int countBits(long val) {
        val -= (val & 0xAAAAAAAAAAAAAAAAL) >>> 1;
        val = (val & 0x3333333333333333L) + (val >>> 2 & 0x3333333333333333L);
        val = val + (val >>> 4) & 0xF0F0F0F0F0F0F0FL;
        val += val >>> 8;
        val += val >>> 16;
        return (int)val + (int)(val >>> 32) & 0xFF;
    }

    public static final boolean has1BitSet(long number) {
        long number1 = number - 1L & number;
        return number1 == 0L;
    }

    public static final boolean has2BitsSet(long number) {
        long number1 = number - 1L & number;
        long number2 = number1 - 1L & number1;
        return number2 == 0L;
    }

    public static final boolean has3BitsSet(long number) {
        long number1 = number - 1L & number;
        long number2 = number1 - 1L & number1;
        long number3 = number2 - 1L & number2;
        return number3 == 0L;
    }

    public static double[] reverseSpecial_100_256(double[] arr) {
        Utils.reverseSpecial(arr);
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = (int)(arr[i] * 100.0 / 256.0);
        }
        return arr;
    }

    public static double[] reverseSpecial(double[] arr) {
        if (arr.length != 64) {
            throw new IllegalStateException();
        }
        Utils.reverse(arr, 0, arr.length);
        Utils.reverse(arr, 0, 8);
        Utils.reverse(arr, 8, 16);
        Utils.reverse(arr, 16, 24);
        Utils.reverse(arr, 24, 32);
        Utils.reverse(arr, 32, 40);
        Utils.reverse(arr, 40, 48);
        Utils.reverse(arr, 48, 56);
        Utils.reverse(arr, 56, 64);
        return arr;
    }

    public static byte[] reverseSpecial(byte[] arr) {
        if (arr.length != 64) {
            throw new IllegalStateException();
        }
        Utils.reverse(arr, 0, arr.length);
        Utils.reverse(arr, 0, 8);
        Utils.reverse(arr, 8, 16);
        Utils.reverse(arr, 16, 24);
        Utils.reverse(arr, 24, 32);
        Utils.reverse(arr, 32, 40);
        Utils.reverse(arr, 40, 48);
        Utils.reverse(arr, 48, 56);
        Utils.reverse(arr, 56, 64);
        return arr;
    }

    public static int[] reverseSpecial(int[] arr) {
        if (arr.length != 64) {
            throw new IllegalStateException();
        }
        Utils.reverse(arr, 0, arr.length);
        Utils.reverse(arr, 0, 8);
        Utils.reverse(arr, 8, 16);
        Utils.reverse(arr, 16, 24);
        Utils.reverse(arr, 24, 32);
        Utils.reverse(arr, 32, 40);
        Utils.reverse(arr, 40, 48);
        Utils.reverse(arr, 48, 56);
        Utils.reverse(arr, 56, 64);
        return arr;
    }

    public static double[] reverse(double[] arr) {
        int size = arr.length;
        for (int i = 0; i < size / 2; ++i) {
            double tmp = arr[i];
            arr[i] = arr[size - i - 1];
            arr[size - i - 1] = tmp;
        }
        return arr;
    }

    public static int[] reverse(int[] arr) {
        int size = arr.length;
        for (int i = 0; i < size / 2; ++i) {
            int tmp = arr[i];
            arr[i] = arr[size - i - 1];
            arr[size - i - 1] = tmp;
        }
        return arr;
    }

    public static byte[] reverse(byte[] arr) {
        int size = arr.length;
        for (int i = 0; i < size / 2; ++i) {
            byte tmp = arr[i];
            arr[i] = arr[size - i - 1];
            arr[size - i - 1] = tmp;
        }
        return arr;
    }

    public static double[] reverse(double[] arr, int from, int to) {
        --to;
        while (from < to) {
            double t;
            double f = arr[from];
            arr[from] = t = arr[to];
            arr[to] = f;
            ++from;
            --to;
        }
        return arr;
    }

    public static int[] reverse(int[] arr, int from, int to) {
        --to;
        while (from < to) {
            int t;
            int f = arr[from];
            arr[from] = t = arr[to];
            arr[to] = f;
            ++from;
            --to;
        }
        return arr;
    }

    public static byte[] reverse(byte[] arr, int from, int to) {
        --to;
        while (from < to) {
            byte t;
            byte f = arr[from];
            arr[from] = t = arr[to];
            arr[to] = f;
            ++from;
            --to;
        }
        return arr;
    }

    public static void bubbleSort(int from, int to, long[] moves) {
        for (int i = from; i < to; ++i) {
            boolean change = false;
            for (int j = i + 1; j < to; ++j) {
                long j_move = moves[j];
                long i_move = moves[i];
                if (j_move <= i_move) continue;
                moves[i] = j_move;
                moves[j] = i_move;
                change = true;
            }
            if (change) continue;
            return;
        }
    }

    public static void bubbleSort(int[] arr1_sortby, int[] arr2, int size) {
        for (int i = 0; i < size; ++i) {
            boolean change = false;
            for (int j = i + 1; j < size; ++j) {
                int j_el = arr1_sortby[j];
                int i_el = arr1_sortby[i];
                if (j_el <= i_el) continue;
                arr1_sortby[i] = i_el;
                arr1_sortby[j] = j_el;
                int i_el1 = arr2[i];
                int j_el1 = arr2[j];
                arr2[i] = i_el1;
                arr2[j] = j_el1;
                change = true;
            }
            if (change) continue;
            return;
        }
    }

    public static void randomize(long[] arr, int start, int end) {
        for (int i = end; i > 1 + start; --i) {
            int rnd_index = start + rnd.nextInt(i - start);
            long tmp = arr[i - 1];
            arr[i - 1] = arr[rnd_index];
            arr[rnd_index] = tmp;
        }
    }

    public static void randomize(int[] arr, int start, int end) {
        for (int i = end; i > 1 + start; --i) {
            int rnd_index = start + rnd.nextInt(i - start);
            int tmp = arr[i - 1];
            arr[i - 1] = arr[rnd_index];
            arr[rnd_index] = tmp;
        }
    }

    public static void main(String[] args) {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long usedMemory = max - (free + (max - total));
        System.out.println("max memory " + max / 0x100000L);
        System.out.println("total memory " + total / 0x100000L);
        System.out.println("free memory " + free / 0x100000L);
        System.out.println("Used memory " + usedMemory / 0x100000L + "MB");
    }
}

