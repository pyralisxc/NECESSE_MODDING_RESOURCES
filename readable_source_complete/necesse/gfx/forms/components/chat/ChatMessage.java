/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.chat;

import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptionsContainer;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.gameFont.FontOptions;

public class ChatMessage {
    public static final int textMaxLength = 500;
    public static final int textFadeTime = 10000;
    public static final FontOptions fontOptions = new FontOptions(16).outline();
    public final String identifier;
    public final FairType type;
    public final FairTypeDrawOptionsContainer drawOptions;
    public final long fadeTime;

    public static TypeParser<?>[] getParsers(FontOptions fontOptions) {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(fontOptions.getSize()), TypeParsers.MobIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions), TypeParsers.Teleport(fontOptions)};
    }

    public ChatMessage(FairType type) {
        this(null, type);
    }

    public ChatMessage(String identifier, FairType type) {
        this.identifier = identifier;
        this.type = type;
        this.drawOptions = new FairTypeDrawOptionsContainer(() -> {
            type.updateGlyphDimensions();
            return type.getDrawOptions(FairType.TextAlign.LEFT, 500, true, true);
        });
        this.fadeTime = System.currentTimeMillis() + 10000L;
    }

    public boolean shouldDraw() {
        return System.currentTimeMillis() < this.fadeTime;
    }
}

