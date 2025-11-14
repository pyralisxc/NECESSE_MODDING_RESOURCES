/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.texture;

import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Consumer;
import necesse.engine.world.GameClock;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.gfx.drawOptions.texture.ShaderBind;
import necesse.gfx.drawOptions.texture.ShaderSprite;
import necesse.gfx.drawOptions.texture.ShaderSpriteAbstract;
import necesse.gfx.drawOptions.texture.ShaderTexture;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.ShaderState;
import necesse.level.maps.light.GameLight;

public class TextureDrawOptionsEnd
extends TextureDrawOptions {
    TextureDrawOptionsEnd(TextureDrawOptions other) {
        super(other);
    }

    @Override
    public TextureDrawOptionsEnd copy() {
        return new TextureDrawOptionsEnd(super.copy());
    }

    public TextureDrawOptionsEnd glRotate(float angle, int centerX, int centerY) {
        this.opts.rotation = angle;
        this.opts.rotTranslateX = centerX;
        this.opts.rotTranslateY = centerY;
        this.opts.useRotation = true;
        return this;
    }

    public TextureDrawOptionsEnd addShaderState(ShaderState state) {
        this.opts.shaderStates.add(state);
        return this;
    }

    public TextureDrawOptionsEnd addShaderBind(ShaderBind bind) {
        this.opts.shaderBinds.add(bind);
        return this;
    }

    public TextureDrawOptionsEnd addShaderBind(int pos, GameTexture texture) {
        return this.addShaderBind(new ShaderBind(pos, texture));
    }

    public TextureDrawOptionsEnd addShaderSprite(ShaderSpriteAbstract sprite) {
        this.opts.shaderSprites.add(sprite);
        return this;
    }

    public TextureDrawOptionsEnd addShaderSprite(int pos, float spriteX1, float spriteX2, float spriteY1, float spriteY2) {
        return this.addShaderSprite(new ShaderSprite(pos, spriteX1, spriteX2, spriteY1, spriteY2));
    }

    public TextureDrawOptionsEnd addShaderTexture(ShaderTexture texture) {
        this.opts.shaderBinds.add(texture.toBind());
        this.opts.shaderSprites.add(texture.toSprite());
        return this;
    }

    public TextureDrawOptionsEnd addShaderTextureFit(GameTexture texture, int pos) {
        if (texture == null) {
            return this;
        }
        float thisTextureWidth = Math.abs(this.opts.spriteX1 - this.opts.spriteX2) * (float)this.texture.getWidth();
        float thisTextureHeight = Math.abs(this.opts.spriteY2 - this.opts.spriteY3) * (float)this.texture.getHeight();
        float widthMod = thisTextureWidth / (float)texture.getWidth();
        float heightMod = thisTextureHeight / (float)texture.getHeight();
        return this.addShaderTexture(new ShaderTexture(pos, texture, 0.0f, widthMod, 0.0f, heightMod));
    }

    public TextureDrawOptionsEnd addShaderTextureFitCenterX(GameTexture texture, int pos) {
        if (texture == null) {
            return this;
        }
        float thisTextureWidth = Math.abs(this.opts.spriteX1 - this.opts.spriteX2) * (float)this.texture.getWidth();
        float thisTextureHeight = Math.abs(this.opts.spriteY2 - this.opts.spriteY3) * (float)this.texture.getHeight();
        float widthMod = thisTextureWidth / (float)texture.getWidth();
        float heightMod = thisTextureHeight / (float)texture.getHeight();
        float xOffset = (1.0f - widthMod) / 2.0f;
        return this.addShaderTexture(new ShaderTexture(pos, texture, xOffset, widthMod + xOffset, 0.0f, heightMod));
    }

    public TextureDrawOptionsEnd addShaderTexture(GameTexture texture, int pos) {
        if (texture == null) {
            return this;
        }
        return this.addShaderTexture(new ShaderTexture(pos, texture));
    }

    public TextureDrawOptionsEnd addShaderTextureFit(GameSprite sprite, int pos) {
        if (sprite == null) {
            return this;
        }
        float thisTextureWidth = Math.abs(this.opts.spriteX1 - this.opts.spriteX2) * (float)this.texture.getWidth();
        float thisTextureHeight = Math.abs(this.opts.spriteY2 - this.opts.spriteY3) * (float)this.texture.getHeight();
        float widthMod = thisTextureWidth / (float)sprite.spriteWidth;
        float heightMod = thisTextureHeight / (float)sprite.spriteHeight;
        ShaderTexture shaderTexture = new ShaderTexture(pos, sprite);
        shaderTexture.spriteX2 = shaderTexture.spriteX1 + (shaderTexture.spriteX2 - shaderTexture.spriteX1) * widthMod;
        shaderTexture.spriteY2 = shaderTexture.spriteY1 + (shaderTexture.spriteY2 - shaderTexture.spriteY1) * heightMod;
        return this.addShaderTexture(shaderTexture);
    }

    public TextureDrawOptionsEnd addShaderTexture(GameSprite sprite, int pos) {
        if (sprite == null) {
            return this;
        }
        return this.addShaderTexture(new ShaderTexture(pos, sprite));
    }

    public TextureDrawOptionsEnd addMaskShader(MaskShaderOptions maskShader) {
        if (maskShader == null) {
            return this;
        }
        return maskShader.apply(this);
    }

    @Override
    public TextureDrawOptionsEnd rotate(float angle, int centerX, int centerY) {
        super.rotate(angle, centerX, centerY);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd rotate(float angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd addRotation(float angle, int centerX, int centerY) {
        super.addRotation(angle, centerX, centerY);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd addPositionMod(Consumer<TextureDrawOptionsPositionMod> change) {
        super.addPositionMod(change);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd rotateTexture(int rightAngles, int midX, int midY) {
        super.rotateTexture(rightAngles, midX, midY);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd rotateTexture(int rightAngles) {
        super.rotateTexture(rightAngles);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd size(int width, int height) {
        super.size(width, height);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd size(Dimension dimension) {
        super.size(dimension);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd shrinkWidth(int size, boolean translate) {
        super.shrinkWidth(size, translate);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd shrinkHeight(int size, boolean translate) {
        super.shrinkHeight(size, translate);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd size(int size, boolean translate) {
        super.size(size, translate);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd size(int size) {
        super.size(size);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd color(float red, float green, float blue, float alpha) {
        super.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd color(float grayScale) {
        super.color(grayScale);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd brightness(float brightness) {
        super.brightness(brightness);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd color(float red, float green, float blue) {
        super.color(red, green, blue);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd color(Color color, boolean overrideAlpha) {
        super.color(color, overrideAlpha);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd color(Color color) {
        super.color(color);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd alpha(float alpha) {
        super.alpha(alpha);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd light(GameLight light) {
        super.light(light);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd colorLight(float red, float green, float blue, GameLight light) {
        super.colorLight(red, green, blue, light);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd colorLight(float red, float green, float blue, float alpha, GameLight light) {
        super.colorLight(red, green, blue, alpha, light);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd colorLight(Color color, GameLight light) {
        super.colorLight(color, light);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd colorLight(Color color, boolean overrideAlpha, GameLight light) {
        super.colorLight(color, overrideAlpha, light);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd colorMult(Color color) {
        super.colorMult(color);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd spelunkerColorLight(float red, float green, float blue, float alpha, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        super.spelunkerColorLight(red, green, blue, alpha, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd spelunkerColorLight(float red, float green, float blue, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        super.spelunkerColorLight(red, green, blue, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd spelunkerColorLight(Color color, boolean overrideAlpha, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        super.spelunkerColorLight(color, overrideAlpha, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd spelunkerColorLight(Color color, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        super.spelunkerColorLight(color, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd spelunkerLight(GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        super.spelunkerLight(light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd spelunkerLight(GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock) {
        super.spelunkerLight(light, hasSpelunker, colorHash, gameClock);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd advColor(float[] colors) {
        super.advColor(colors);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd translatePos(int x, int y) {
        super.translatePos(x, y);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd addTranslatePos(int x, int y) {
        super.addTranslatePos(x, y);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd mirrorX() {
        super.mirrorX();
        return this;
    }

    @Override
    public TextureDrawOptionsEnd mirrorY() {
        super.mirrorY();
        return this;
    }

    @Override
    public TextureDrawOptionsEnd mirror(boolean horizontal, boolean vertical) {
        super.mirror(horizontal, vertical);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd depth(float depth) {
        super.depth(depth);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd blendFunc(int blendSourceRGB, int blendDestinationRGB, int blendSourceAlpha, int blendDestinationAlpha) {
        super.blendFunc(blendSourceRGB, blendDestinationRGB, blendSourceAlpha, blendDestinationAlpha);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd pos(int drawX, int drawY) {
        super.pos(drawX, drawY);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd pos(int drawX, int drawY, boolean useTranslate) {
        super.pos(drawX, drawY, useTranslate);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd posMiddle(int drawX, int drawY, boolean useTranslate) {
        super.posMiddle(drawX, drawY, useTranslate);
        return this;
    }

    @Override
    public TextureDrawOptionsEnd posMiddle(int drawX, int drawY) {
        super.posMiddle(drawX, drawY);
        return this;
    }

    public void draw(int drawX, int drawY) {
        this.pos(drawX, drawY);
        this.draw();
    }
}

