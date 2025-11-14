/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveSyntaxException;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairHelpIconGlyph;
import necesse.gfx.fairType.FairInvisibleTextParseGlyph;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.fairType.parsers.TypeParserResult;
import necesse.gfx.forms.presets.HelpForms;
import necesse.gfx.gameFont.FontOptions;

public class HelpIconParser
extends TypeParser<HelpParserResult> {
    public static final Pattern HELP_PATTERN = Pattern.compile("\\[help=(\\w+)](\\{.+})?");
    public final FontOptions fontOptions;

    public HelpIconParser(FontOptions fontOptions) {
        this.fontOptions = fontOptions;
    }

    @Override
    public HelpParserResult getMatchResult(FairGlyph[] glyphs, int startIndex) {
        StringBuilder sBuilder = new StringBuilder();
        for (FairGlyph glyph : glyphs) {
            sBuilder.append(glyph.getCharacter());
        }
        Matcher m = HELP_PATTERN.matcher(sBuilder.toString());
        if (m.find(startIndex)) {
            String stringID = m.group(1);
            int end = m.end(1) + 1;
            String displayName = null;
            if (m.groupCount() > 1 && m.group(2) != null) {
                String displayNameGroup = m.group(2);
                try {
                    int sectionStop = SaveComponent.getSectionStop(displayNameGroup, '{', '}', 0);
                    displayName = displayNameGroup.substring(1, sectionStop);
                    end = m.start(2) + sectionStop + 1;
                }
                catch (SaveSyntaxException saveSyntaxException) {
                    // empty catch block
                }
            }
            return new HelpParserResult(m.start(), end, stringID, displayName);
        }
        return null;
    }

    @Override
    public FairGlyph[] parse(HelpParserResult result, FairGlyph[] oldGlyphs) {
        if (result.displayName != null && !result.displayName.isEmpty()) {
            ArrayList<FairGlyph> glyphs = new ArrayList<FairGlyph>(3 + result.displayName.length());
            glyphs.add(new FairHelpIconGlyph(this.fontOptions.getSize(), result.key));
            glyphs.add(new FairInvisibleTextParseGlyph("{"));
            glyphs.addAll(Arrays.asList(FairCharacterGlyph.fromString(this.fontOptions, result.displayName, e -> {
                if (e.getID() == -100) {
                    if (!e.state) {
                        HelpForms.openHelpForm(result.key, new Object[0]);
                    }
                    return true;
                }
                return false;
            }, null)));
            glyphs.add(new FairInvisibleTextParseGlyph("}"));
            return glyphs.toArray(new FairGlyph[0]);
        }
        return new FairGlyph[]{new FairHelpIconGlyph(this.fontOptions.getSize(), result.key)};
    }

    public static class HelpParserResult
    extends TypeParserResult {
        public final String key;
        public final String displayName;

        public HelpParserResult(int start, int end, String key, String displayName) {
            super(start, end);
            this.key = key;
            this.displayName = displayName;
        }
    }
}

