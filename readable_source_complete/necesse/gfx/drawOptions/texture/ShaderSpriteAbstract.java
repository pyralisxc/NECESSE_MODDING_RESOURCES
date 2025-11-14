/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.ShaderBind;

public abstract class ShaderSpriteAbstract {
    public final int glPos;

    public ShaderSpriteAbstract(int pos) {
        this.glPos = ShaderBind.toGlPos(pos);
    }

    public abstract void startTopLeft();

    public abstract void startTopRight();

    public abstract void startBotRight();

    public abstract void startBotLeft();
}

