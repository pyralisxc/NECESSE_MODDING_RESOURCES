/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.EdgeMaskOptions;
import necesse.gfx.shader.EdgeMaskTextureOptions;
import necesse.gfx.shader.ShaderState;

public class MaskShaderOptions
implements ShaderState {
    private final ArrayList<EdgeMaskOptions> masks = new ArrayList();
    public final int drawXOffset;
    public final int drawYOffset;
    private boolean useShader = true;
    private boolean locked;

    public MaskShaderOptions(int drawXOffset, int drawYOffset) {
        this.drawXOffset = drawXOffset;
        this.drawYOffset = drawYOffset;
    }

    public MaskShaderOptions(GameTexture mask, int drawXOffset, int drawYOffset, int maskXOffset, int maskYOffset) {
        this(drawXOffset, drawYOffset);
        this.addMask(mask, maskXOffset, maskYOffset);
    }

    private MaskShaderOptions(MaskShaderOptions copy) {
        this.drawXOffset = copy.drawXOffset;
        this.drawYOffset = copy.drawYOffset;
        this.masks.addAll(copy.masks);
        this.useShader = copy.useShader;
    }

    public MaskShaderOptions useShader(boolean useShader) {
        this.useShader = useShader;
        return this;
    }

    public MaskShaderOptions addMask(GameTexture mask, int maskXOffset, int maskYOffset) {
        return this.addMask(new EdgeMaskTextureOptions(mask, maskXOffset, maskYOffset));
    }

    public MaskShaderOptions copyAndAddMask(GameTexture mask, int maskXOffset, int maskYOffset) {
        return new MaskShaderOptions(this).addMask(new EdgeMaskTextureOptions(mask, maskXOffset, maskYOffset)).useShader(true);
    }

    public MaskShaderOptions addMask(EdgeMaskOptions options) {
        if (this.locked) {
            throw new IllegalStateException("Mask shader is already in use");
        }
        this.masks.add(options);
        return this;
    }

    public MaskShaderOptions copyAndAddMask(EdgeMaskOptions options) {
        return new MaskShaderOptions(this).addMask(options).useShader(true);
    }

    public TextureDrawOptionsEnd apply(TextureDrawOptionsEnd options) {
        options = options.addTranslatePos(this.drawXOffset, this.drawYOffset);
        for (int i = 0; i < this.masks.size(); ++i) {
            EdgeMaskOptions mask = this.masks.get(i);
            options = mask.apply(options, i + 1);
        }
        if (this.useShader) {
            options = options.addShaderState(new ShaderState(){

                @Override
                public void use() {
                    MaskShaderOptions.this.use();
                }

                @Override
                public void stop() {
                    MaskShaderOptions.this.stop();
                }
            });
        }
        return options;
    }

    @Override
    public void use() {
        if (!this.masks.isEmpty()) {
            GameResources.edgeMaskShader.use(this.masks);
        }
        this.locked = true;
    }

    @Override
    public void stop() {
        if (!this.masks.isEmpty()) {
            GameResources.edgeMaskShader.stop();
        }
        this.locked = false;
    }

    public ShaderState addMaskOffset(final int xOffset, final int yOffset) {
        final AtomicInteger usedMasks = new AtomicInteger();
        final int[][] lastOffsets = new int[4][2];
        return new ShaderState(){

            @Override
            public void use() {
                usedMasks.set(MaskShaderOptions.this.masks.size());
                int masks = usedMasks.get();
                for (int i = 0; i < masks; ++i) {
                    int textureIndex = i + 1;
                    int[] get = GameResources.edgeMaskShader.getOffset(textureIndex);
                    lastOffsets[i] = get;
                    GameResources.edgeMaskShader.passOffset(textureIndex, get[0] + xOffset, get[1] + yOffset);
                }
            }

            @Override
            public void stop() {
                int masks = usedMasks.get();
                for (int i = 0; i < masks; ++i) {
                    int textureIndex = i + 1;
                    int[] last = lastOffsets[i];
                    GameResources.edgeMaskShader.passOffset(textureIndex, last[0], last[1]);
                }
            }
        };
    }
}

