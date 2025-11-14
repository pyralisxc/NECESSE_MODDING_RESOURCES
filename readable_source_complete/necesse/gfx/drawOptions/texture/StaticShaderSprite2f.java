/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL13
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.ShaderSpriteAbstract;
import org.lwjgl.opengl.GL13;

public class StaticShaderSprite2f
extends ShaderSpriteAbstract {
    protected float x;
    protected float y;

    public StaticShaderSprite2f(int pos, float x, float y) {
        super(pos);
        this.x = x;
        this.y = y;
    }

    @Override
    public void startTopLeft() {
        GL13.glMultiTexCoord2f((int)this.glPos, (float)this.x, (float)this.y);
    }

    @Override
    public void startTopRight() {
    }

    @Override
    public void startBotRight() {
    }

    @Override
    public void startBotLeft() {
    }
}

