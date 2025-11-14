/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.texture;

import java.util.Objects;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsMods;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsObj;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.gameTexture.AbstractGameTexture;

public class TextureDrawOptions
extends TextureDrawOptionsMods
implements DrawOptions {
    public final AbstractGameTexture texture;

    private TextureDrawOptions(AbstractGameTexture texture, TextureDrawOptionsObj options) {
        super(options);
        this.texture = texture;
    }

    private TextureDrawOptions(AbstractGameTexture texture) {
        super(new TextureDrawOptionsObj(texture, texture.getWidth(), texture.getHeight()));
        Objects.requireNonNull(texture);
        this.texture = texture;
    }

    protected TextureDrawOptions(TextureDrawOptions other) {
        super(other.opts);
        this.texture = other.texture;
    }

    public TextureDrawOptions copy() {
        return new TextureDrawOptions(this.texture, new TextureDrawOptionsObj(this.opts));
    }

    @Override
    public int getWidth() {
        return this.opts.width;
    }

    @Override
    public int getHeight() {
        return this.opts.height;
    }

    @Override
    public void draw() {
        this.opts.draw(true, true);
    }

    public static float pixel(int pixel, int texLength) {
        return (float)pixel / (float)texLength;
    }

    public static float pixelFloat(float pixel, float texLength) {
        return pixel / texLength;
    }

    public static float pixel(int spriteCoord, int spriteRes, int texLength) {
        return TextureDrawOptions.pixel(spriteCoord * spriteRes, texLength);
    }

    public static float pixel(int spriteCoord, int pixel, int spriteRes, int texLength) {
        return TextureDrawOptions.pixel(spriteCoord * spriteRes + pixel, texLength);
    }

    public static TextureDrawOptionsStart initDraw(AbstractGameTexture texture) {
        return new TextureDrawOptionsStart(new TextureDrawOptions(texture));
    }
}

