/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL13
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.ShaderSpriteAbstract;
import org.lwjgl.opengl.GL13;

public class StaticShaderSprite4f
extends ShaderSpriteAbstract {
    protected float x;
    protected float y;
    protected float z;
    protected float w;

    public StaticShaderSprite4f(int pos, float x, float y, float z, float w) {
        super(pos);
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public void startTopLeft() {
        GL13.glMultiTexCoord4f((int)this.glPos, (float)this.x, (float)this.y, (float)this.z, (float)this.w);
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

