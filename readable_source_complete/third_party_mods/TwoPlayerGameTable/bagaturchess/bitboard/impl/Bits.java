/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl;

public class Bits {
    public static final long NUMBER_0 = 0L;
    public static final long NUMBER_1 = 1L;
    public static final long NUMBER_MINUS_1 = -1L;
    public static final int NUMBER_64 = 64;
    public static final int NUMBER_69 = 69;
    public static final int PRIME_67 = 64;
    private static final int MAX_INT = Integer.MAX_VALUE;
    public static final long BIT_0 = Long.MIN_VALUE;
    public static final long BIT_1 = 0x4000000000000000L;
    public static final long BIT_2 = 0x2000000000000000L;
    public static final long BIT_3 = 0x1000000000000000L;
    public static final long BIT_4 = 0x800000000000000L;
    public static final long BIT_5 = 0x400000000000000L;
    public static final long BIT_6 = 0x200000000000000L;
    public static final long BIT_7 = 0x100000000000000L;
    public static final long BIT_8 = 0x80000000000000L;
    public static final long BIT_9 = 0x40000000000000L;
    public static final long BIT_10 = 0x20000000000000L;
    public static final long BIT_11 = 0x10000000000000L;
    public static final long BIT_12 = 0x8000000000000L;
    public static final long BIT_13 = 0x4000000000000L;
    public static final long BIT_14 = 0x2000000000000L;
    public static final long BIT_15 = 0x1000000000000L;
    public static final long BIT_16 = 0x800000000000L;
    public static final long BIT_17 = 0x400000000000L;
    public static final long BIT_18 = 0x200000000000L;
    public static final long BIT_19 = 0x100000000000L;
    public static final long BIT_20 = 0x80000000000L;
    public static final long BIT_21 = 0x40000000000L;
    public static final long BIT_22 = 0x20000000000L;
    public static final long BIT_23 = 0x10000000000L;
    public static final long BIT_24 = 0x8000000000L;
    public static final long BIT_25 = 0x4000000000L;
    public static final long BIT_26 = 0x2000000000L;
    public static final long BIT_27 = 0x1000000000L;
    public static final long BIT_28 = 0x800000000L;
    public static final long BIT_29 = 0x400000000L;
    public static final long BIT_30 = 0x200000000L;
    public static final long BIT_31 = 0x100000000L;
    public static final long BIT_32 = 0x80000000L;
    public static final long BIT_33 = 0x40000000L;
    public static final long BIT_34 = 0x20000000L;
    public static final long BIT_35 = 0x10000000L;
    public static final long BIT_36 = 0x8000000L;
    public static final long BIT_37 = 0x4000000L;
    public static final long BIT_38 = 0x2000000L;
    public static final long BIT_39 = 0x1000000L;
    public static final long BIT_40 = 0x800000L;
    public static final long BIT_41 = 0x400000L;
    public static final long BIT_42 = 0x200000L;
    public static final long BIT_43 = 0x100000L;
    public static final long BIT_44 = 524288L;
    public static final long BIT_45 = 262144L;
    public static final long BIT_46 = 131072L;
    public static final long BIT_47 = 65536L;
    public static final long BIT_48 = 32768L;
    public static final long BIT_49 = 16384L;
    public static final long BIT_50 = 8192L;
    public static final long BIT_51 = 4096L;
    public static final long BIT_52 = 2048L;
    public static final long BIT_53 = 1024L;
    public static final long BIT_54 = 512L;
    public static final long BIT_55 = 256L;
    public static final long BIT_56 = 128L;
    public static final long BIT_57 = 64L;
    public static final long BIT_58 = 32L;
    public static final long BIT_59 = 16L;
    public static final long BIT_60 = 8L;
    public static final long BIT_61 = 4L;
    public static final long BIT_62 = 2L;
    public static final long BIT_63 = 1L;
    public static final long[] ALL_BITS = new long[]{Long.MIN_VALUE, 0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L, 0x80000000000000L, 0x40000000000000L, 0x20000000000000L, 0x10000000000000L, 0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L, 0x800000000000L, 0x400000000000L, 0x200000000000L, 0x100000000000L, 0x80000000000L, 0x40000000000L, 0x20000000000L, 0x10000000000L, 0x8000000000L, 0x4000000000L, 0x2000000000L, 0x1000000000L, 0x800000000L, 0x400000000L, 0x200000000L, 0x100000000L, 0x80000000L, 0x40000000L, 0x20000000L, 0x10000000L, 0x8000000L, 0x4000000L, 0x2000000L, 0x1000000L, 0x800000L, 0x400000L, 0x200000L, 0x100000L, 524288L, 262144L, 131072L, 65536L, 32768L, 16384L, 8192L, 4096L, 2048L, 1024L, 512L, 256L, 128L, 64L, 32L, 16L, 8L, 4L, 2L, 1L};

    public static final int bitCount(long val) {
        val -= (val & 0xAAAAAAAAAAAAAAAAL) >>> 1;
        val = (val & 0x3333333333333333L) + (val >>> 2 & 0x3333333333333333L);
        val = val + (val >>> 4) & 0xF0F0F0F0F0F0F0FL;
        val += val >>> 8;
        val += val >>> 16;
        return (int)val + (int)(val >>> 32) & 0xFF;
    }

    public static long reverse(long i) {
        i = (i & 0x5555555555555555L) << 1 | i >>> 1 & 0x5555555555555555L;
        i = (i & 0x3333333333333333L) << 2 | i >>> 2 & 0x3333333333333333L;
        i = (i & 0xF0F0F0F0F0F0F0FL) << 4 | i >>> 4 & 0xF0F0F0F0F0F0F0FL;
        i = (i & 0xFF00FF00FF00FFL) << 8 | i >>> 8 & 0xFF00FF00FF00FFL;
        i = i << 48 | (i & 0xFFFF0000L) << 16 | i >>> 16 & 0xFFFF0000L | i >>> 48;
        return i;
    }

    public static final String toBinaryString(long number) {
        int len;
        Object result = Long.toBinaryString(number);
        for (int i = len = ((String)result).length(); i < 64; ++i) {
            result = "0" + (String)result;
        }
        return result;
    }

    public static final String toBinaryStringMatrix(long number) {
        Object result = "\r\n";
        String line = Bits.toBinaryString(number);
        result = (String)result + line.substring(56, 64) + "\r\n";
        result = (String)result + line.substring(48, 56) + "\r\n";
        result = (String)result + line.substring(40, 48) + "\r\n";
        result = (String)result + line.substring(32, 40) + "\r\n";
        result = (String)result + line.substring(24, 32) + "\r\n";
        result = (String)result + line.substring(16, 24) + "\r\n";
        result = (String)result + line.substring(8, 16) + "\r\n";
        result = (String)result + line.substring(0, 8) + "\r\n";
        return result;
    }

    public static long findNormalizerSimpleNumber(long[] numbers) {
        int numbersCount;
        long result = -1L;
        for (int i = numbersCount = numbers.length; i <= Integer.MAX_VALUE; ++i) {
            long[] remainders = new long[i];
            Bits.fillWithNegativeOnes(remainders);
            for (int j = 0; j < numbersCount; ++j) {
                long number = numbers[j];
                long remainder = number % (long)i;
                if (remainder <= 0L) {
                    remainder = -remainder;
                }
                if (Integer.MAX_VALUE <= remainder) {
                    throw new IllegalStateException("remainder=" + remainder + ", Integer.MAX_VALUE=2147483647");
                }
                if (remainders[(int)remainder] != -1L) break;
                if (j == numbersCount - 1) {
                    result = i;
                    break;
                }
                remainders[(int)remainder] = 1L;
            }
            if (result != -1L) break;
        }
        return result;
    }

    private static final void fillWithNegativeOnes(long[] arr) {
        int length = arr.length;
        for (int i = 0; i < length; ++i) {
            arr[i] = -1L;
        }
    }

    public static final boolean isSimple(long number) {
        if (number <= 0L) {
            throw new IllegalStateException();
        }
        boolean result = true;
        long square = (long)Math.ceil(Math.sqrt(number));
        for (long i = 2L; i <= square; ++i) {
            if (number % i != 0L) continue;
            result = false;
            break;
        }
        return result;
    }

    public static final int nextSetBit_R2L(int from, long bits) {
        long mask = 1L << from;
        do {
            if ((bits & mask) != 0L) {
                return from;
            }
            ++from;
        } while ((mask <<= 1) != 0L);
        return -1;
    }

    public static final int nextSetBit_R2L(int from, int to, long bits) {
        long mask = 1L << from;
        do {
            if ((bits & mask) != 0L) {
                return from;
            }
            mask <<= 1;
        } while (++from <= to);
        return -1;
    }

    public static final int nextSetBit_L2R(int from, long bits) {
        long mask = 1L << 63 - from;
        do {
            if ((bits & mask) != 0L) {
                return from;
            }
            ++from;
        } while ((mask >>= 1) != 0L);
        return -1;
    }

    public static void main(String[] args) {
        for (int i = 4769291; i < 9999999; ++i) {
            if (!Bits.isSimple(i) || i <= 999999) continue;
            System.out.println(i);
        }
    }
}

