/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.input.InputEvent;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;

public class FairPasswordCharacterGlyph
extends FairCharacterGlyph {
    private final char actualCharacter;

    public FairPasswordCharacterGlyph(FontOptions fontOptions, char character, Function<InputEvent, Boolean> onClick, Supplier<GameTooltips> tooltipsSupplier) {
        super(fontOptions, '*', onClick, tooltipsSupplier);
        this.actualCharacter = character;
    }

    public FairPasswordCharacterGlyph(FontOptions fontOptions, char character, Function<InputEvent, Boolean> onClick) {
        super(fontOptions, '*', onClick);
        this.actualCharacter = character;
    }

    public FairPasswordCharacterGlyph(FontOptions fontOptions, char character, Supplier<GameTooltips> tooltipsSupplier) {
        super(fontOptions, '*', tooltipsSupplier);
        this.actualCharacter = character;
    }

    public FairPasswordCharacterGlyph(FontOptions fontOptions, char character) {
        super(fontOptions, '*');
        this.actualCharacter = character;
    }

    @Override
    public char getCharacter() {
        return this.actualCharacter;
    }

    @Override
    public String toString() {
        return String.valueOf(this.actualCharacter);
    }
}

