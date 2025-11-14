/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.utils;

import java.text.NumberFormat;

public class StringUtils {
    public static String align(double number) {
        int dotIndex;
        Object result = NumberFormat.getInstance().format(number);
        if (number < 10.0) {
            result = " " + (String)result;
        }
        if ((dotIndex = ((String)result).indexOf(46)) < 0) {
            result = (String)result + ".";
        }
        result = StringUtils.fill((String)result, 6, '0');
        return result;
    }

    public static String fill(String str, int max) {
        return StringUtils.fill(str, max, ' ');
    }

    private static String fill(String str, int max, char c) {
        int len = ((String)str).length();
        for (int i = max; i > len; --i) {
            str = (String)str + c;
        }
        return str;
    }
}

