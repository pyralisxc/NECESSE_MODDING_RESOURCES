/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType.parsers;

import java.util.regex.Pattern;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.fairType.parsers.TypeParserMatcherResult;

public abstract class TypeRegexParser
extends TypeParser<TypeParserMatcherResult> {
    public final Pattern regex;

    public TypeRegexParser(Pattern regex) {
        this.regex = regex;
    }

    public TypeRegexParser(String regex) {
        this(Pattern.compile(regex));
    }

    @Override
    public TypeParserMatcherResult getMatchResult(FairGlyph[] glyphs, int startIndex) {
        return TypeParserMatcherResult.regexResult(glyphs, startIndex, this.regex);
    }
}

