/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType.parsers;

import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.parsers.TypeParserResult;

public abstract class TypeParser<T extends TypeParserResult> {
    public abstract T getMatchResult(FairGlyph[] var1, int var2);

    public abstract FairGlyph[] parse(T var1, FairGlyph[] var2);

    public static int getIndexOf(FairGlyph[] glyphs, String str, int startIndex) {
        for (int i = startIndex; i < glyphs.length; ++i) {
            boolean valid = true;
            for (int j = 0; j < str.length(); ++j) {
                if (i + j < glyphs.length && glyphs[i + j].getCharacter() == str.charAt(j)) continue;
                valid = false;
                break;
            }
            if (!valid) continue;
            return i;
        }
        return -1;
    }

    public static String subString(FairGlyph[] glyphs, int startIndex, int endIndex) {
        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i <= endIndex; ++i) {
            builder.append(glyphs[i].getCharacter());
        }
        return builder.toString();
    }
}

