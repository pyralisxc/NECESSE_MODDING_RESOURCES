/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.texture;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import necesse.engine.world.GameClock;
import necesse.gfx.drawOptions.texture.ShaderBind;
import necesse.gfx.drawOptions.texture.ShaderSprite;
import necesse.gfx.drawOptions.texture.ShaderSpriteAbstract;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsMods;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsObj;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.light.GameLight;

public class SharedTextureDrawOptions {
    public static int MAX_VERTEX_CALLS_PER_DRAW_CALL = Integer.MAX_VALUE;
    public final GameTexture texture;
    private final ArrayList<TextureDrawOptionsObj> options = new ArrayList();
    private final ArrayList<ShaderBind> shaderBinds = new ArrayList();

    public SharedTextureDrawOptions(GameTexture texture) {
        Objects.requireNonNull(texture);
        this.texture = texture;
    }

    public SharedTextureDrawOptions addShaderBind(int pos, GameTexture texture) {
        this.shaderBinds.add(new ShaderBind(pos, texture));
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Wrapper add(GameTextureSection section) {
        if (section.getTexture() != this.texture) {
            throw new IllegalStateException("Invalid texture");
        }
        TextureDrawOptionsObj opts = section.initDraw().opts;
        SharedTextureDrawOptions sharedTextureDrawOptions = this;
        synchronized (sharedTextureDrawOptions) {
            this.options.add(opts);
        }
        return new Wrapper(opts);
    }

    public Wrapper addFull() {
        return this.add(new GameTextureSection(this.texture));
    }

    public Wrapper addSprite(int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
        return this.add(new GameTextureSection(this.texture).sprite(spriteX, spriteY, spriteWidth, spriteHeight));
    }

    public Wrapper addSprite(int spriteX, int spriteY, int spriteRes) {
        return this.add(new GameTextureSection(this.texture).sprite(spriteX, spriteY, spriteRes));
    }

    public Wrapper addSection(int startX, int endX, int startY, int endY) {
        return this.add(new GameTextureSection(this.texture, startX, endX, startY, endY));
    }

    public Wrapper addSpriteSection(int spriteX, int spriteY, int spriteWidth, int spriteHeight, int startX, int endX, int startY, int endY) {
        return this.add(new GameTextureSection(this.texture).sprite(spriteX, spriteY, spriteWidth, spriteHeight).section(startX, endX, startY, endY));
    }

    public Wrapper addSpriteSection(int spriteX, int spriteY, int spriteRes, int startX, int endX, int startY, int endY) {
        return this.add(new GameTextureSection(this.texture).sprite(spriteX, spriteY, spriteRes).section(startX, endX, startY, endY));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SharedTextureDrawOptions forEachDraw(Consumer<Wrapper> consumer) {
        SharedTextureDrawOptions sharedTextureDrawOptions = this;
        synchronized (sharedTextureDrawOptions) {
            for (TextureDrawOptionsObj option : this.options) {
                consumer.accept(new Wrapper(option));
            }
        }
        return this;
    }

    public void draw() {
        this.draw(MAX_VERTEX_CALLS_PER_DRAW_CALL);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void draw(int maxDrawsPerCall) {
        SharedTextureDrawOptions sharedTextureDrawOptions = this;
        synchronized (sharedTextureDrawOptions) {
            Iterator<TextureDrawOptionsObj> iterator = this.options.iterator();
            TextureDrawOptionsObj last = null;
            int drawCounter = 0;
            boolean first = true;
            while (iterator.hasNext()) {
                TextureDrawOptionsObj next = iterator.next();
                if (drawCounter >= maxDrawsPerCall && last != null) {
                    last.glEnd();
                    first = true;
                    drawCounter = 0;
                }
                if (next != null) {
                    if (first) {
                        this.shaderBinds.forEach(ShaderBind::bind);
                    }
                    next.draw(first, !iterator.hasNext());
                    last = next;
                    first = false;
                } else {
                    if (last != null) {
                        last.glEnd();
                    }
                    first = true;
                }
                ++drawCounter;
            }
        }
    }

    public static class Wrapper
    extends TextureDrawOptionsMods {
        public Wrapper(TextureDrawOptionsObj opts) {
            super(opts);
        }

        public Wrapper addShaderSprite(ShaderSpriteAbstract sprite) {
            this.opts.shaderSprites.add(sprite);
            return this;
        }

        public Wrapper addShaderSprite(int pos, float spriteX1, float spriteX2, float spriteY1, float spriteY2) {
            return this.addShaderSprite(new ShaderSprite(pos, spriteX1, spriteX2, spriteY1, spriteY2));
        }

        @Override
        public Wrapper rotate(float angle, int centerX, int centerY) {
            super.rotate(angle, centerX, centerY);
            return this;
        }

        @Override
        public Wrapper rotate(float angle) {
            super.rotate(angle);
            return this;
        }

        @Override
        public Wrapper addRotation(float angle, int centerX, int centerY) {
            super.addRotation(angle, centerX, centerY);
            return this;
        }

        @Override
        public Wrapper addPositionMod(Consumer<TextureDrawOptionsPositionMod> change) {
            super.addPositionMod(change);
            return this;
        }

        @Override
        public Wrapper rotateTexture(int rightAngles, int midX, int midY) {
            super.rotateTexture(rightAngles, midX, midY);
            return this;
        }

        @Override
        public Wrapper rotateTexture(int rightAngles) {
            super.rotateTexture(rightAngles);
            return this;
        }

        @Override
        public Wrapper size(int width, int height) {
            super.size(width, height);
            return this;
        }

        @Override
        public Wrapper size(Dimension dimension) {
            super.size(dimension);
            return this;
        }

        @Override
        public Wrapper shrinkWidth(int size, boolean translate) {
            super.shrinkWidth(size, translate);
            return this;
        }

        @Override
        public Wrapper shrinkHeight(int size, boolean translate) {
            super.shrinkHeight(size, translate);
            return this;
        }

        @Override
        public Wrapper size(int size, boolean translate) {
            super.size(size, translate);
            return this;
        }

        @Override
        public Wrapper size(int size) {
            super.size(size);
            return this;
        }

        @Override
        public Wrapper color(float red, float green, float blue, float alpha) {
            super.color(red, green, blue, alpha);
            return this;
        }

        @Override
        public Wrapper color(float grayScale) {
            super.color(grayScale);
            return this;
        }

        @Override
        public Wrapper brightness(float brightness) {
            super.brightness(brightness);
            return this;
        }

        @Override
        public Wrapper color(float red, float green, float blue) {
            super.color(red, green, blue);
            return this;
        }

        @Override
        public Wrapper color(Color color, boolean overrideAlpha) {
            super.color(color, overrideAlpha);
            return this;
        }

        @Override
        public Wrapper color(Color color) {
            super.color(color);
            return this;
        }

        @Override
        public Wrapper alpha(float alpha) {
            super.alpha(alpha);
            return this;
        }

        @Override
        public Wrapper light(GameLight light) {
            super.light(light);
            return this;
        }

        @Override
        public Wrapper colorLight(float red, float green, float blue, GameLight light) {
            super.colorLight(red, green, blue, light);
            return this;
        }

        @Override
        public Wrapper colorLight(float red, float green, float blue, float alpha, GameLight light) {
            super.colorLight(red, green, blue, alpha, light);
            return this;
        }

        @Override
        public Wrapper colorLight(Color color, GameLight light) {
            super.colorLight(color, light);
            return this;
        }

        @Override
        public Wrapper colorLight(Color color, boolean overrideAlpha, GameLight light) {
            super.colorLight(color, overrideAlpha, light);
            return this;
        }

        @Override
        public Wrapper colorMult(Color color) {
            super.colorMult(color);
            return this;
        }

        @Override
        public Wrapper spelunkerColorLight(float red, float green, float blue, float alpha, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
            super.spelunkerColorLight(red, green, blue, alpha, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
            return this;
        }

        @Override
        public Wrapper spelunkerColorLight(float red, float green, float blue, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
            super.spelunkerColorLight(red, green, blue, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
            return this;
        }

        @Override
        public Wrapper spelunkerColorLight(Color color, boolean overrideAlpha, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
            super.spelunkerColorLight(color, overrideAlpha, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
            return this;
        }

        @Override
        public Wrapper spelunkerColorLight(Color color, GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
            super.spelunkerColorLight(color, light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
            return this;
        }

        @Override
        public Wrapper spelunkerLight(GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock, long fadeTime, float saturation, int minLight) {
            super.spelunkerLight(light, hasSpelunker, colorHash, gameClock, fadeTime, saturation, minLight);
            return this;
        }

        @Override
        public Wrapper spelunkerLight(GameLight light, boolean hasSpelunker, long colorHash, GameClock gameClock) {
            super.spelunkerLight(light, hasSpelunker, colorHash, gameClock);
            return this;
        }

        @Override
        public Wrapper advColor(float[] colors) {
            super.advColor(colors);
            return this;
        }

        @Override
        public Wrapper translatePos(int x, int y) {
            super.translatePos(x, y);
            return this;
        }

        @Override
        public Wrapper addTranslatePos(int x, int y) {
            super.addTranslatePos(x, y);
            return this;
        }

        @Override
        public Wrapper mirrorX() {
            super.mirrorX();
            return this;
        }

        @Override
        public Wrapper mirrorY() {
            super.mirrorY();
            return this;
        }

        @Override
        public Wrapper mirror(boolean horizontal, boolean vertical) {
            super.mirror(horizontal, vertical);
            return this;
        }

        @Override
        public Wrapper depth(float depth) {
            super.depth(depth);
            return this;
        }

        @Override
        public Wrapper blendFunc(int blendSourceRGB, int blendDestinationRGB, int blendSourceAlpha, int blendDestinationAlpha) {
            super.blendFunc(blendSourceRGB, blendDestinationRGB, blendSourceAlpha, blendDestinationAlpha);
            return this;
        }

        @Override
        public Wrapper pos(int drawX, int drawY) {
            super.pos(drawX, drawY);
            return this;
        }

        @Override
        public Wrapper pos(int drawX, int drawY, boolean useTranslate) {
            super.pos(drawX, drawY, useTranslate);
            return this;
        }

        @Override
        public Wrapper posMiddle(int drawX, int drawY, boolean useTranslate) {
            super.posMiddle(drawX, drawY, useTranslate);
            return this;
        }

        @Override
        public Wrapper posMiddle(int drawX, int drawY) {
            super.posMiddle(drawX, drawY);
            return this;
        }

        @Override
        public int getWidth() {
            return super.getWidth();
        }

        @Override
        public int getHeight() {
            return super.getHeight();
        }
    }
}

