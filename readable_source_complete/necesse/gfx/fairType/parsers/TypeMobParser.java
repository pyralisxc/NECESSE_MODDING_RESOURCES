/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveSyntaxException;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairMobGlyph;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.fairType.parsers.TypeParserResult;

public class TypeMobParser
extends TypeParser<MobParserResult> {
    public static final Pattern MOB_PATTERN = Pattern.compile("\\[mob=(\\w+)]");
    public static final Pattern MOBS_PATTERN = Pattern.compile("\\[mobs=(.+)]");
    public final int size;
    public final Function<FairMobGlyph, FairMobGlyph> modder;

    public TypeMobParser(int size, Function<FairMobGlyph, FairMobGlyph> modder) {
        this.size = size;
        this.modder = modder;
    }

    @Override
    public MobParserResult getMatchResult(FairGlyph[] glyphs, int startIndex) {
        int end;
        StringBuilder sBuilder = new StringBuilder();
        for (FairGlyph glyph : glyphs) {
            sBuilder.append(glyph.getCharacter());
        }
        Matcher m = MOBS_PATTERN.matcher(sBuilder.toString());
        if (m.find(startIndex)) {
            String[] split;
            String mobsData = m.group(1);
            end = m.end(1) + 1;
            try {
                int sectionStop = SaveComponent.getSectionStop(mobsData, '[', ']', 0);
                mobsData = mobsData.substring(0, sectionStop + 1);
                end = m.start(2) + sectionStop + 1;
            }
            catch (SaveSyntaxException saveSyntaxException) {
                // empty catch block
            }
            ArrayList<Integer> mobIDs = new ArrayList<Integer>();
            for (String mobStringID : split = mobsData.split(",")) {
                int mobID = MobRegistry.getMobID(mobStringID);
                if (mobID != -1) continue;
                mobIDs.add(mobID);
            }
            return new MobParserResult(m.start(), end, mobIDs);
        }
        m = MOB_PATTERN.matcher(sBuilder.toString());
        if (m.find(startIndex)) {
            String stringID = m.group(1);
            end = m.end(1) + 1;
            int mobID = MobRegistry.getMobID(stringID);
            if (mobID == -1) {
                return null;
            }
            return new MobParserResult(m.start(), end, mobID);
        }
        return null;
    }

    @Override
    public FairGlyph[] parse(MobParserResult result, FairGlyph[] oldGlyphs) {
        if (result.mobIDs.isEmpty()) {
            return oldGlyphs;
        }
        FairMobGlyph glyph = new FairMobGlyph(this.size, result.mobIDs);
        if (this.modder != null) {
            glyph = this.modder.apply(glyph);
        }
        return new FairGlyph[]{glyph};
    }

    public static class MobParserResult
    extends TypeParserResult {
        public final List<Integer> mobIDs;

        public MobParserResult(int start, int end, List<Integer> mobIDs) {
            super(start, end);
            this.mobIDs = mobIDs;
        }

        public MobParserResult(int start, int end, int mobID) {
            this(start, end, mobID == -1 ? Collections.emptyList() : Collections.singletonList(mobID));
        }
    }
}

