/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.parsers.TypeParserResult;

public class TypeParserMatcherResult
extends TypeParserResult {
    public final Matcher matcher;

    private TypeParserMatcherResult(Matcher matcher, int start, int end) {
        super(start, end);
        this.matcher = matcher;
    }

    public static TypeParserMatcherResult regexResult(FairGlyph[] glyphs, int startIndex, Pattern pattern) {
        StringBuilder sBuilder = new StringBuilder();
        for (FairGlyph glyph : glyphs) {
            if (glyph.canBeParsed()) {
                sBuilder.append(glyph.getCharacter());
                continue;
            }
            sBuilder.append('\ufffe');
        }
        Matcher m = pattern.matcher(sBuilder.toString());
        if (m.find(startIndex)) {
            return new TypeParserMatcherResult(m, m.start(), m.end());
        }
        return null;
    }
}

