/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameLog;
import necesse.gfx.drawOptions.texture.ShaderTexture;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.EdgeMaskOptions;
import necesse.gfx.shader.EdgeMaskTextureOptions;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.ShaderState;
import necesse.gfx.shader.shaderVariable.ShaderIntVariable;

public class EdgeMaskShader
extends GameShader {
    public static final int MAX_MASKS = 4;

    public EdgeMaskShader() {
        super("vertMask", "fragEdgeMask");
        this.addVariable(new ShaderIntVariable("maskDebug", 0, 5));
    }

    @Override
    public void use() {
        this.use(null, 0, 0);
    }

    public void passOffset(int pos, int xOffset, int yOffset) {
        this.pass2i("mask" + pos + "Offset", xOffset, yOffset);
    }

    public void passSprite(int pos, float x1, float y1, float x2, float y2) {
        this.pass4f("mask" + pos + "Sprite", x1, y1, x2, y2);
    }

    public void passSize(int pos, int width, int height) {
        this.pass2i("mask" + pos + "Size", width, height);
    }

    public int[] getOffset(int pos) {
        return this.get2i("mask" + pos + "Offset");
    }

    public void use(List<EdgeMaskOptions> options) {
        super.use();
        if (options.size() > 4) {
            GameLog.warn.println("Mask shader cannot use more than 4 masks");
        }
        this.pass1i("maskCount", options.size());
        for (int i = 0; i < options.size(); ++i) {
            EdgeMaskOptions mask = options.get(i);
            this.applyUse(mask, i + 1);
        }
    }

    public void use(EdgeMaskOptions ... options) {
        super.use();
        if (options.length > 4) {
            GameLog.warn.println("Mask shader cannot use more than 4 masks");
        }
        this.pass1i("maskCount", options.length);
        for (int i = 0; i < options.length; ++i) {
            EdgeMaskOptions mask = options[i];
            this.applyUse(mask, i + 1);
        }
    }

    public void use(GameTexture mask, int maskXOffset, int maskYOffset) {
        this.use(new EdgeMaskTextureOptions(mask, maskXOffset, maskYOffset));
    }

    private void applyUse(EdgeMaskOptions mask, int pos) {
        this.pass1i("mask" + pos + "Texture", pos);
        mask.use(this, pos);
    }

    public ShaderState addMaskDebug(final int debug) {
        final AtomicInteger lastDebug = new AtomicInteger();
        return new ShaderState(){

            @Override
            public void use() {
                lastDebug.set(EdgeMaskShader.this.get1i("maskDebug"));
                EdgeMaskShader.this.pass1i("maskDebug", debug);
            }

            @Override
            public void stop() {
                EdgeMaskShader.this.pass1i("maskDebug", lastDebug.get());
            }
        };
    }

    public ShaderState setup(TextureDrawOptionsEnd textureDrawOptions, GameSprite maskTexture, int xOffset, int yOffset) {
        int pixelXIncrease = textureDrawOptions.getWidth() - maskTexture.width;
        float spriteX1 = TextureDrawOptions.pixel(maskTexture.spriteX * maskTexture.spriteWidth - xOffset, maskTexture.texture.getWidth());
        float spriteX2 = TextureDrawOptions.pixel((maskTexture.spriteX + 1) * maskTexture.spriteWidth - xOffset + pixelXIncrease, maskTexture.texture.getWidth());
        int pixelYIncrease = textureDrawOptions.getHeight() - maskTexture.height;
        float spriteY1 = TextureDrawOptions.pixel(maskTexture.spriteY * maskTexture.spriteHeight - yOffset, maskTexture.texture.getHeight());
        float spriteY2 = TextureDrawOptions.pixel((maskTexture.spriteY + 1) * maskTexture.spriteHeight - yOffset + pixelYIncrease, maskTexture.texture.getHeight());
        textureDrawOptions.addShaderTexture(new ShaderTexture(1, maskTexture.texture, spriteX1, spriteX2, spriteY1, spriteY2));
        return new ShaderState(){

            @Override
            public void use() {
                EdgeMaskShader.this.use();
            }

            @Override
            public void stop() {
                EdgeMaskShader.this.stop();
            }
        };
    }

    public ShaderState setupCenterX(TextureDrawOptionsEnd textureDrawOptions, GameSprite maskTexture, int xOffset, int yOffset) {
        return this.setup(textureDrawOptions, maskTexture, xOffset += (textureDrawOptions.getWidth() - maskTexture.width) / 2, yOffset);
    }

    public ShaderState setup(TextureDrawOptionsEnd textureDrawOptions, GameTexture maskTexture, int xOffset, int yOffset) {
        return this.setup(textureDrawOptions, new GameSprite(maskTexture), xOffset, yOffset);
    }

    public ShaderState setupCenterX(TextureDrawOptionsEnd textureDrawOptions, GameTexture maskTexture, int xOffset, int yOffset) {
        return this.setupCenterX(textureDrawOptions, new GameSprite(maskTexture), xOffset, yOffset);
    }
}

