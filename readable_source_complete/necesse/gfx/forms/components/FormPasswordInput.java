/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairPasswordCharacterGlyph;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;

public class FormPasswordInput
extends FormTextInput {
    public FormPasswordInput(int x, int y, FormInputSize size, int width, int maxWidth, int maxLength) {
        super(x, y, size, width, maxWidth, maxLength);
    }

    public FormPasswordInput(int x, int y, FormInputSize size, int width, int maxLength) {
        super(x, y, size, width, maxLength);
    }

    @Override
    protected FairGlyph getCharacterGlyph(char character) {
        return new FairPasswordCharacterGlyph(this.fontOptions, character);
    }
}

