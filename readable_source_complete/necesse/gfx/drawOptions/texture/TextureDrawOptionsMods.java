/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.texture;

import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Consumer;
import necesse.engine.world.GameClock;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsObj;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.level.maps.light.GameLight;

public abstract class TextureDrawOptionsMods {
    protected final TextureDrawOptionsObj opts;

    protected TextureDrawOptionsMods(TextureDrawOptionsObj opts) {
        this.opts = opts;
    }

    protected TextureDrawOptionsMods rotate(float angle, int centerX, int centerY) {
        this.opts.setRotation(angle, centerX, centerY);
        return this;
    }

    protected TextureDrawOptionsMods rotate(float angle) {
        return this.rotate(angle, this.opts.width / 2, this.opts.height / 2);
    }

    protected TextureDrawOptionsMods addRotation(float angle, int centerX, int centerY) {
        this.opts.addRotation(angle, centerX, centerY);
        return this;
    }

    protected TextureDrawOptionsMods addPositionMod(Consumer<TextureDrawOptionsPositionMod> change) {
        this.opts.addPositionMod(change);
        return this;
    }

    protected TextureDrawOptionsMods rotateTexture(int rightAngles, int midX, int midY) {
        if (rightAngles < -4 || rightAngles > 4) {
            rightAngles %= 4;
        }
        if (rightAngles < 0) {
            for (int i = 0; i > rightAngles; --i) {
                float tempX = this.opts.spriteX1;
                this.opts.spriteX1 = this.opts.spriteX2;
                this.opts.spriteX2 = this.opts.spriteX3;
                this.opts.spriteX3 = this.opts.spriteX4;
                this.opts.spriteX4 = tempX;
                float tempY = this.opts.spriteY1;
                this.opts.spriteY1 = this.opts.spriteY2;
                this.opts.spriteY2 = this.opts.spriteY3;
                this.opts.spriteY3 = this.opts.spriteY4;
                this.opts.spriteY4 = tempY;
                this.opts.translateX -= this.opts.width / 2 - midX;
                this.opts.translateY -= this.opts.height / 2 - midY;
                int tempSize = this.opts.width;
                this.opts.width = this.opts.height;
                this.opts.height = tempSize;
            }
        } else {
            for (int i = 0; i < rightAngles; ++i) {
                float tempX = this.opts.spriteX1;
                this.opts.spriteX1 = this.opts.spriteX4;
                this.opts.spriteX4 = this.opts.spriteX3;
                this.opts.spriteX3 = this.opts.spriteX2;
                this.opts.spriteX2 = tempX;
                float tempY = this.opts.spriteY1;
                this.opts.spriteY1 = this.opts.spriteY4;
                this.opts.spriteY4 = this.opts.spriteY3;
                this.opts.spriteY3 = this.opts.spriteY2;
                this.opts.spriteY2 = tempY;
                this.opts.translateX += this.opts.width / 2 - midX;
                this.opts.translateY += this.opts.height / 2 - midY;
                int tempSize = this.opts.width;
                this.opts.width = this.opts.height;
                this.opts.height = tempSize;
            }
        }
        return this;
    }

    protected TextureDrawOptionsMods rotateTexture(int rightAngles) {
        return this.rotateTexture(rightAngles, this.opts.width / 2, this.opts.height / 2);
    }

    protected TextureDrawOptionsMods size(int width, int height) {
        this.opts.width = width;
        this.opts.height = height;
        return this;
    }

    protected TextureDrawOptionsMods size(Dimension dimension) {
        this.opts.width = dimension.width;
        this.opts.height = dimension.height;
        return this;
    }

    protected TextureDrawOptionsMods shrinkWidth(int size, boolean translate) {
        float ratio = (float)this.opts.height / (float)this.opts.width;
        this.opts.height = (int)((float)size * ratio);
        this.opts.width = size;
        if (translate) {
            this.opts.translateY += (size - this.opts.height) / 2;
        }
        return this;
    }

    protected TextureDrawOptionsMods shrinkHeight(int size, boolean translate) {
        float ratio = (float)this.opts.width / (float)this.opts.height;
        this.opts.width = (int)((float)size * ratio);
        this.opts.height = size;
        if (translate) {
            this.opts.translateX += (size - this.opts.width) / 2;
        }
        return this;
    }

    protected TextureDrawOptionsMods size(int size, boolean translate) {
        if (this.opts.width < this.opts.height) {
            return this.shrinkHeight(size, translate);
        }
        if (this.opts.height < this.opts.width) {
            return this.shrinkWidth(size, translate);
        }
        if (translate && this.opts.width < size) {
            this.opts.translateX += (size - this.opts.width) / 2;
        } else {
            this.opts.width = size;
        }
        if (translate && this.opts.height < size) {
            this.opts.translateY += (size - this.opts.height) / 2;
        } else {
            this.opts.height = size;
        }
        return this;
    }

    protected TextureDrawOptionsMods size(int size) {
        return this.size(size, true);
    }

    protected TextureDrawOptionsMods color(float red, float green, float blue, float alpha) {
        this.opts.red = red;
        this.opts.green = green;
        this.opts.blue = blue;
        this.opts.alpha = alpha;
        return this;
    }

    protected TextureDrawOptionsMods color(float grayScale) {
        this.opts.red = grayScale;
        this.opts.green = grayScale;
        this.opts.blue = grayScale;
        return this;
    }

    protected TextureDrawOptionsMods brightness(float brightness) {
        this.opts.red *= brightness;
        this.opts.green *= brightness;
        this.opts.blue *= brightness;
        return this;
    }

    protected TextureDrawOptionsMods color(float red, float green, float blue) {
        this.opts.red = red;
        this.opts.green = green;
        this.opts.blue = blue;
        return this;
    }

    protected TextureDrawOptionsMods color(Color color, boolean overrideAlpha) {
        if (overrideAlpha) {
            return this.color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
        }
        return this.color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f);
    }

    protected TextureDrawOptionsMods color(Color color) {
        return this.color(color, true);
    }

    protected TextureDrawOptionsMods alpha(float alpha) {
        this.opts.alpha = alpha;
        return this;
    }

    protected TextureDrawOptionsMods light(GameLight light) {
        float brightness = light.getFloatLevel();
        this.opts.red *= light.getFloatRed();
        this.opts.green *= light.getFloatGreen();
        this.opts.blue *= light.getFloatBlue();
        return this.brightness(brightness);
    }

    protected TextureDrawOptionsMods colorLight(float red, float green, float blue, GameLight light) {
        this.color(red, green, blue);
        return this.light(light);
    }

    protected TextureDrawOptionsMods colorLight(float red, float green, float blue, float alpha, GameLight light) {
        this.color(red, green, blue, alpha);
        return this.light(light);
    }

    protected TextureDrawOptionsMods colorLight(Color color, GameLight light) {
        this.color(color);
        return this.light(light);
    }

    protected TextureDrawOptionsMods colorLight(Color color, boolean overrideAlpha, GameLight light) {
        this.color(color, overrideAlpha);
        return this.light(light);
    }

    protected TextureDrawOptionsMods colorMult(Color color) {
        this.opts.red *= (float)color.getRed() / 255.0f;
        this.opts.green *= (float)color.getGreen() / 255.0f;
        this.opts.blue *= (float)color.getBlue() / 255.0f;
        this.opts.alpha *= (float)color.getAlpha() / 255.0f;
        return this;
    }

    protected TextureDrawOptionsMods spelunkerColorLight(float red, float green, float blue, float alpha, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        if (hasSpelunker) {
            long currentTime = gameClock.getTime();
            float hue = (float)Math.floorMod(currentTime += fadeTime * colorHash, fadeTime) / (float)fadeTime;
            int spelunkerRGB = Color.HSBtoRGB(hue, saturation, 1.0f);
            float spelunkerRed = (float)(spelunkerRGB >> 16 & 0xFF) / 255.0f;
            float spelunkerGreen = (float)(spelunkerRGB >> 8 & 0xFF) / 255.0f;
            float spelunkerBlue = (float)(spelunkerRGB & 0xFF) / 255.0f;
            return this.colorLight(red *= spelunkerRed, green *= spelunkerGreen, blue *= spelunkerBlue, alpha, light.minLevelCopy(minLight));
        }
        return this.colorLight(red, green, blue, alpha, light);
    }

    protected TextureDrawOptionsMods spelunkerColorLight(float red, float green, float blue, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        return this.spelunkerColorLight(red, green, blue, this.opts.alpha, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
    }

    protected TextureDrawOptionsMods spelunkerColorLight(Color color, boolean overrideAlpha, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        return this.spelunkerColorLight((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, overrideAlpha ? (float)color.getAlpha() / 255.0f : this.opts.alpha, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
    }

    protected TextureDrawOptionsMods spelunkerColorLight(Color color, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        return this.spelunkerColorLight(color, true, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
    }

    protected TextureDrawOptionsMods spelunkerLight(GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
        if (hasSpelunker) {
            long currentTime = gameClock.getTime();
            float hue = (float)Math.floorMod(currentTime += fadeTime * colorHash, fadeTime) / (float)fadeTime;
            return this.colorLight(Color.getHSBColor(hue, saturation, 1.0f), light.minLevelCopy(minLight));
        }
        return this.light(light);
    }

    protected TextureDrawOptionsMods spelunkerLight(GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock) {
        return this.spelunkerLight(light, hasSpelunker, colorHash, gameClock, 2500L, 0.2f, 100);
    }

    protected TextureDrawOptionsMods advColor(float[] colors) {
        if (colors != null && colors.length != 16) {
            throw new IllegalArgumentException("Colors must have 16 indexes");
        }
        this.opts.advCol = colors;
        return this;
    }

    protected TextureDrawOptionsMods translatePos(int x, int y) {
        this.opts.translateX = x;
        this.opts.translateY = y;
        return this;
    }

    protected TextureDrawOptionsMods addTranslatePos(int x, int y) {
        this.opts.translateX += x;
        this.opts.translateY += y;
        return this;
    }

    protected TextureDrawOptionsMods mirrorX() {
        float temp1 = this.opts.spriteX1;
        this.opts.spriteX1 = this.opts.spriteX2;
        this.opts.spriteX2 = temp1;
        float temp2 = this.opts.spriteX3;
        this.opts.spriteX3 = this.opts.spriteX4;
        this.opts.spriteX4 = temp2;
        return this;
    }

    protected TextureDrawOptionsMods mirrorY() {
        float temp1 = this.opts.spriteY1;
        this.opts.spriteY1 = this.opts.spriteY4;
        this.opts.spriteY4 = temp1;
        float temp2 = this.opts.spriteY2;
        this.opts.spriteY2 = this.opts.spriteY3;
        this.opts.spriteY3 = temp2;
        return this;
    }

    protected TextureDrawOptionsMods mirror(boolean horizontal, boolean vertical) {
        if (horizontal) {
            this.mirrorX();
        }
        if (vertical) {
            this.mirrorY();
        }
        return this;
    }

    protected TextureDrawOptionsMods depth(float depth) {
        this.opts.drawDepth = depth;
        return this;
    }

    protected TextureDrawOptionsMods blendFunc(int blendSourceRGB, int blendDestinationRGB, int blendSourceAlpha, int blendDestinationAlpha) {
        this.opts.setBlend = true;
        this.opts.blendSourceRGB = blendSourceRGB;
        this.opts.blendDestinationRGB = blendDestinationRGB;
        this.opts.blendSourceAlpha = blendSourceAlpha;
        this.opts.blendDestinationAlpha = blendDestinationAlpha;
        return this;
    }

    protected TextureDrawOptionsMods pos(int drawX, int drawY) {
        this.opts.pos(drawX, drawY);
        return this;
    }

    protected TextureDrawOptionsMods pos(int drawX, int drawY, boolean useTranslate) {
        return this.pos(drawX - (useTranslate ? this.opts.translateX : 0), drawY - (useTranslate ? this.opts.translateY : 0));
    }

    protected TextureDrawOptionsMods posMiddle(int drawX, int drawY, boolean useTranslate) {
        return this.pos(drawX - this.opts.width / 2, drawY - this.opts.height / 2, useTranslate);
    }

    protected TextureDrawOptionsMods posMiddle(int drawX, int drawY) {
        return this.posMiddle(drawX, drawY, false);
    }

    protected int getWidth() {
        return this.opts.width;
    }

    protected int getHeight() {
        return this.opts.height;
    }
}

