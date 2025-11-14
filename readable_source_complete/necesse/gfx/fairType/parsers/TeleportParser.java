/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType.parsers;

import java.util.function.Function;
import necesse.engine.GlobalData;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.fairType.parsers.TypeParserResult;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;

public class TeleportParser
extends TypeParser<TargetTypeParserResult> {
    public final FontOptions fontOptions;

    public TeleportParser(FontOptions fontOptions) {
        this.fontOptions = fontOptions;
    }

    @Override
    public TargetTypeParserResult getMatchResult(FairGlyph[] glyphs, int startIndex) {
        TargetTypeParserResult teleportResult = this.checkTeleportResult(glyphs, startIndex);
        if (teleportResult != null) {
            return teleportResult;
        }
        TargetTypeParserResult setLevelResult = this.checkSetLevelResult(glyphs, startIndex);
        if (setLevelResult != null) {
            return setLevelResult;
        }
        return this.checkSetPositionResult(glyphs, startIndex);
    }

    protected TargetTypeParserResult checkTeleportResult(FairGlyph[] glyphs, int startIndex) {
        String prefix = "[teleport=";
        int start = TeleportParser.getIndexOf(glyphs, prefix, startIndex);
        if (start == -1) {
            return null;
        }
        int end = TeleportParser.getIndexOf(glyphs, "]", startIndex + prefix.length());
        if (end == -1) {
            return null;
        }
        String target = TeleportParser.subString(glyphs, start + prefix.length(), end - 1);
        Function<ClientClient, String> chatCommandGetter = client -> "/tp \"" + client.getName() + "\" " + target;
        return new TargetTypeParserResult(start, end + 1, target, chatCommandGetter);
    }

    protected TargetTypeParserResult checkSetLevelResult(FairGlyph[] glyphs, int startIndex) {
        String prefix = "[setlevel=";
        int start = TeleportParser.getIndexOf(glyphs, prefix, startIndex);
        if (start == -1) {
            return null;
        }
        int end = TeleportParser.getIndexOf(glyphs, "]", startIndex + prefix.length());
        if (end == -1) {
            return null;
        }
        String target = TeleportParser.subString(glyphs, start + prefix.length(), end - 1);
        Function<ClientClient, String> chatCommandGetter = client -> "/setlevel \"" + client.getName() + "\" " + target;
        return new TargetTypeParserResult(start, end + 1, target, chatCommandGetter);
    }

    protected TargetTypeParserResult checkSetPositionResult(FairGlyph[] glyphs, int startIndex) {
        String prefix = "[setposition=";
        int start = TeleportParser.getIndexOf(glyphs, prefix, startIndex);
        if (start == -1) {
            return null;
        }
        int positionSplit = TeleportParser.getIndexOf(glyphs, ",", startIndex + prefix.length());
        if (positionSplit == -1) {
            return null;
        }
        int tileSplit = TeleportParser.getIndexOf(glyphs, "x", positionSplit);
        if (tileSplit == -1) {
            return null;
        }
        int end = TeleportParser.getIndexOf(glyphs, "]", positionSplit);
        if (end == -1) {
            return null;
        }
        String levelIdentifierString = TeleportParser.subString(glyphs, start + prefix.length(), positionSplit - 1);
        String tileXString = TeleportParser.subString(glyphs, positionSplit + 1, tileSplit - 1);
        String tileYString = TeleportParser.subString(glyphs, tileSplit + 1, end - 1);
        Function<ClientClient, String> chatCommandGetter = client -> "/setposition \"" + client.getName() + "\" " + levelIdentifierString + " " + tileXString + " " + tileYString;
        return new TargetTypeParserResult(start, end + 1, levelIdentifierString + " tile " + tileXString + "x" + tileYString, chatCommandGetter);
    }

    @Override
    public FairGlyph[] parse(TargetTypeParserResult result, FairGlyph[] oldGlyphs) {
        return FairCharacterGlyph.fromString(this.fontOptions, result.displayString, e -> {
            if (e.getID() == -100) {
                Client client;
                State currentState;
                if (!e.state && (currentState = GlobalData.getCurrentState()) instanceof MainGame && (client = ((MainGame)currentState).getClient()) != null) {
                    client.network.sendPacket(new PacketChatMessage(client.getSlot(), result.chatCommandGetter.apply(client.getClient())));
                }
                return true;
            }
            return false;
        }, () -> new StringTooltips("Teleport to " + result.displayString));
    }

    public static class TargetTypeParserResult
    extends TypeParserResult {
        public final String displayString;
        public final Function<ClientClient, String> chatCommandGetter;

        public TargetTypeParserResult(int start, int end, String displayString, Function<ClientClient, String> chatCommandGetter) {
            super(start, end);
            this.displayString = displayString;
            this.chatCommandGetter = chatCommandGetter;
        }
    }
}

